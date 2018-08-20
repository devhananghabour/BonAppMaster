package app.bonapp.activities;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.app.bonapp.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import app.bonapp.constants.ApiKeys;
import app.bonapp.constants.Constants;
import app.bonapp.models.createdeal.CreateDealModel;
import app.bonapp.models.merchantdeals.DATum;
import app.bonapp.network.ApiCall;
import app.bonapp.network.ApiInterface;
import app.bonapp.network.NetworkListener;
import app.bonapp.network.RestApi;
import app.bonapp.utils.AppSharedPrefs;
import app.bonapp.utils.AppUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * In this class merchants can create their deals and activate their deals.
 */
public class CreateDealActivity extends AppCompatActivity {
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.ll_title)
    LinearLayout llTitle;
    @BindView(R.id.et_total_items)
    EditText etTotalItems;
    @BindView(R.id.ll_total_items)
    LinearLayout llTotalItems;
    @BindView(R.id.et_original_price)
    EditText etOriginalPrice;
    @BindView(R.id.ll_original_price)
    LinearLayout llOriginalPrice;
    @BindView(R.id.ll_new_price)
    LinearLayout llNewPrice;
    @BindView(R.id.et_begin_time)
    EditText etBeginTime;
    @BindView(R.id.et_end_time)
    EditText etEndTime;
    @BindView(R.id.tv_new_price)
    TextView tvNewPrice;
    @BindView(R.id.tv_discount_perc)
    TextView tvDiscountPerc;
    @BindView(R.id.seekbar_newprice)
    AppCompatSeekBar seekbarNewprice;
    @BindView(R.id.ll_slider)
    LinearLayout llSlider;
    @BindView(R.id.et_description)
    EditText etDescription;
    @BindView(R.id.ll_description)
    LinearLayout llDescription;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_end)
    TextView tvEnd;
    @BindView(R.id.btn_done)
    Button btnDone;
    @BindView(R.id.activity_create_deal)
    LinearLayout activityCreateDeal;

    private AppUtils appUtils;
    private Activity mActivity;
    private boolean isCreateDeal;
    private DATum dealData;
    private int position;
    private String pickUpTime;
    private boolean isNewDealCreated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_deal);
        ButterKnife.bind(this);
        mActivity = CreateDealActivity.this;
        appUtils = AppUtils.getInstance();
        changeFocus();
        setDiscountedPrice();
        if (getIntent().getExtras() != null) {
            isCreateDeal = getIntent().getBooleanExtra(Constants.IS_CREATE_DEAL, false);

            dealData = getIntent().getParcelableExtra("deal_model");
            position = getIntent().getIntExtra(Constants.DEAL_POSITION_KEY, 0);
            pickUpTime = getIntent().getStringExtra(Constants.PICKUP_TIME_MERCHANT);
        }
        setupViews();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    /**
     * method to setup views
     */
    private void setupViews() {
        ivBack.setVisibility(View.VISIBLE);
        if (isCreateDeal) {
            tvTitle.setText(getString(R.string.create_deal));
            btnDone.setVisibility(View.VISIBLE);
            btnDone.setText(R.string.done);
            etEndTime.setText(appUtils.parseToTimeAndDate(pickUpTime));

        } else {
            tvTitle.setText(getString(R.string.deactivated_deal));
            btnDone.setVisibility(View.VISIBLE);
            btnDone.setText(R.string.activate);

            if (dealData != null) {
                etTitle.setText(dealData.getDealTitle());
                etTitle.setSelection(etTitle.getText().length());
                etDescription.setText(dealData.getDealDescription());
                etTotalItems.setText(dealData.getTotalItems());
                etOriginalPrice.setText(dealData.getOriginalPrice());
                tvNewPrice.setText(dealData.getNewPrice());
                double discount = (Double.parseDouble(dealData.getOriginalPrice()) -
                        Double.parseDouble(dealData.getNewPrice())) / Double.parseDouble(dealData.getOriginalPrice()) * 100;
                String perc = discount + getString(R.string.percent_symbol);
                tvDiscountPerc.setText(perc);
                seekbarNewprice.setProgress((int) discount / 5);
                etBeginTime.setText(appUtils.parseToTimeAndDate(dealData.getStartTime()));
                etEndTime.setText(appUtils.parseToTimeAndDate(dealData.getEndTime()));
            }
        }
    }


    /**
     * method to change focus
     */
    private void changeFocus() {
        focusChange(etTitle, llTitle, mActivity);
        focusChange(etOriginalPrice, llOriginalPrice, mActivity);
        focusChange(etTotalItems, llTotalItems, mActivity);
        focusChange(etDescription, llDescription, mActivity);
    }


    @OnClick({R.id.et_begin_time, R.id.et_end_time, R.id.activity_create_deal, R.id.ll_new_price, R.id.tv_new_price, R.id.iv_back, R.id.btn_done})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_begin_time:
                showTimePicker(etBeginTime, true);
                selectDeselectNewPrice(false);
                break;
            case R.id.et_end_time:
                if (TextUtils.isEmpty(etBeginTime.getText().toString())) {
                    appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.empty_begin_time));

                } else {
                    showTimePicker(etEndTime, false);
                }

                selectDeselectNewPrice(false);
                break;
            case R.id.activity_create_deal:
                appUtils.hideNativeKeyboard(mActivity);
                selectDeselectNewPrice(false);
                break;
            case R.id.tv_new_price:
            case R.id.ll_new_price:
                if (TextUtils.isEmpty(etOriginalPrice.getText().toString().trim())) {
                    appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.pls_enter_original));
                } else {
                    appUtils.hideNativeKeyboard(mActivity);
                    selectDeselectNewPrice(true);
                }
                break;
            case R.id.iv_back:
                appUtils.hideNativeKeyboard(mActivity);
                onBackPressed();
                break;
            case R.id.btn_done:
                appUtils.hideNativeKeyboard(mActivity);
                if (!mActivity.isFinishing()) {
                    if (isCreateDeal) {
                        if (checkValidations()) {
                            if (appUtils.isInternetOn(mActivity)) {
                                hitApiForCreateDeal();
                            } else {
                                appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.internet_offline));
                            }
                        }
                    } else {
                        if (checkValidations()) {
                            if (appUtils.isInternetOn(mActivity)) {
                                hitApiForActivateDeal();
                            } else {
                                appUtils.showSnackBar(activityCreateDeal, getString(R.string.internet_offline));
                            }
                        }
                    }
                }
                break;

        }
    }

    /**
     * method to select and deselect
     *
     * @param isSelected
     */
    private void selectDeselectNewPrice(boolean isSelected) {
        if (isSelected) {
            llTitle.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
            llOriginalPrice.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
            llTotalItems.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorWhite));

            llNewPrice.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorCreateDealSelected));
            llSlider.setVisibility(View.VISIBLE);

            btnDone.setVisibility(View.GONE);
        } else {
            llNewPrice.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
            llSlider.setVisibility(View.GONE);

            btnDone.setVisibility(View.VISIBLE);
        }
    }

    /**
     * method to pick time
     */
    private void showTimePicker(final EditText editText, final boolean isStartType) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(mActivity, R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if (isStartType) {
                    Calendar datetime = Calendar.getInstance();
                    Calendar c = Calendar.getInstance();
                    datetime.set(Calendar.HOUR_OF_DAY, selectedHour);
                    datetime.set(Calendar.MINUTE, selectedMinute);
                    if (datetime.getTimeInMillis() > c.getTimeInMillis()) {
//            it's after current
                        editText.setText(parseTimeToTimeDate(String.format(Locale.getDefault(), "%02d", selectedHour) + ":" +
                                String.format(Locale.getDefault(), "%02d", selectedMinute)));
                    } else {
//            it's before current'
                        appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.start_time_cant_be_less));

                    }
                } else {
                    if (!checktimings(etBeginTime.getText().toString(), parseTimeToTimeDate(String.format(Locale.getDefault(), "%02d", selectedHour) + ":" +
                            String.format(Locale.getDefault(), "%02d", selectedMinute)))) {
                        appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.end_time_cant_less));
                    } else if (pickUpTime != null && !pickUpTime.equals("") && !appUtils.matchSameTime(appUtils.parseDateToTime(pickUpTime), selectedHour + ":" + selectedMinute)) {
                        appUtils.showAlertDialog(mActivity, "", getString(R.string.same_pickup_time), getString(R.string.ok), "", null);
                    } else {
                        editText.setText(parseTimeToTimeDate(String.format(Locale.getDefault(), "%02d", selectedHour) + ":" +
                                String.format(Locale.getDefault(), "%02d", selectedMinute)));
                    }
                }
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("");
        mTimePicker.show();
    }

    /**
     * method to set discounted price
     */
    private void setDiscountedPrice() {
        seekbarNewprice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvDiscountPerc.setText(progress * 5 + "%");
                try {
                    double originalPrice = Double.parseDouble(etOriginalPrice.getText().toString());
                    double sp = (progress * 5 * originalPrice) / 100;

                    tvNewPrice.setText(String.format(Locale.US, "%.2f", originalPrice - sp));
                } catch (NumberFormatException ne) {
                    ne.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * method to change focus on edit text
     *
     * @param editText
     * @param linearLayout
     * @param activity
     */
    private void focusChange(EditText editText, final LinearLayout linearLayout, final Activity activity) {

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    linearLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorCreateDealSelected));
                    selectDeselectNewPrice(false);
                } else
                    linearLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorWhite));
                if (!hasFocus) {
                    if (activity.getCurrentFocus() != null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) activity.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
            }
        });
    }

    /**
     * method to check validations
     *
     * @return true
     */
    private boolean checkValidations() {
        double originalPrice = 0.0, newPrice = 0.0;
        try {
            originalPrice = Double.parseDouble(etOriginalPrice.getText().toString());
            newPrice = Double.parseDouble(tvNewPrice.getText().toString());
        } catch (NumberFormatException ne) {
            ne.printStackTrace();
        }
        if (TextUtils.isEmpty(etTitle.getText().toString().trim())) {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.empty_title_war));
            return false;
        } else if (TextUtils.isEmpty(etDescription.getText().toString().trim())) {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.empty_description_war));
            return false;
        } else if (TextUtils.isEmpty(etTotalItems.getText().toString())) {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.empty_total_items));
            return false;
        } else if (Integer.parseInt(etTotalItems.getText().toString()) < 1) {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.empty_total_items));
            return false;
        } else if (TextUtils.isEmpty(etOriginalPrice.getText().toString())) {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.empty_original_price));
            return false;
        } else if (Double.parseDouble(etOriginalPrice.getText().toString().trim()) < 1) {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.zero_original_price));
            return false;
        } else if (TextUtils.isEmpty(tvNewPrice.getText().toString())) {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.empty_new_price));
            return false;
        } else if (originalPrice < newPrice) {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.new_price_cant_greater));
            return false;
        } else if (TextUtils.isEmpty(etBeginTime.getText().toString())) {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.empty_begin_time));
            return false;
        } else if (TextUtils.isEmpty(etEndTime.getText().toString())) {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.empty_end_time));
            return false;
        } else if (appUtils.isBeforeCurrentTime(etBeginTime.getText().toString())) {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.start_time_cant_be_less));
            return false;
        } else if (!checktimings(etBeginTime.getText().toString(), etEndTime.getText().toString())) {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.end_time_cant_less));
            return false;
        } else
            return true;
    }


    /**
     * method to hit api for creating deal
     */
    private void hitApiForCreateDeal() {
        appUtils.showProgressDialog(mActivity,false);

        HashMap<String, String> params = new HashMap<>();
        params.put(ApiKeys.DEAL_TITLE, etTitle.getText().toString().trim());
        params.put(ApiKeys.DEAL_DESCRIPTION, etDescription.getText().toString().trim());
        params.put(ApiKeys.TOTAL_ITEMS, etTotalItems.getText().toString().trim());
        params.put(ApiKeys.ORIGINAL_PRICE, etOriginalPrice.getText().toString().trim());
        params.put(ApiKeys.NEW_PRICE, tvNewPrice.getText().toString());
        params.put(ApiKeys.START_DATE_TIME, parseTime(etBeginTime.getText().toString().trim()));
        params.put(ApiKeys.END_DATE_TIME, parseTime(etEndTime.getText().toString().trim()));

        ApiInterface service = RestApi.createService(ApiInterface.class);
        Call<ResponseBody> call = service.createDeal(AppSharedPrefs.getInstance(mActivity).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, ""), appUtils.encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                CreateDealModel createDealModel = new Gson().fromJson(response, CreateDealModel.class);
                if (createDealModel.getCODE() == 200) {
                    /*Intent intent = new Intent(mActivity, MerchantActivity.class);
                    startActivity(intent);*/
                    setResult(Constants.CREATE_DEAL_REQ_CODE);

                    Intent intent1 = new Intent(Constants.REFRESH_RECCEIVER_KEY);
                    // You can also include some extra data.
                    intent1.putExtra("refresh", true);
                    LocalBroadcastManager.getInstance(mActivity).sendBroadcast(intent1);

                    mActivity.finish();

                } else if (createDealModel.getCODE() == ApiKeys.UNAUTHORISED_CODE) {
                    appUtils.logoutFromApp(mActivity, createDealModel.getMESSAGE());
                } else {
                    appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), createDealModel.getMESSAGE());
                }

                appUtils.hideProgressDialog(mActivity);
            }

            @Override
            public void onSuccessErrorBody(String response) {

            }

            @Override
            public void onFailure() {
                appUtils.hideProgressDialog(mActivity);
                appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.txt_something_went_wrong));

            }
        });

    }

    /**
     * @param time
     * @param endtime
     * @return true if time is less then endtime
     */
    private boolean checktimings(String time, String endtime) {
        String pattern = "HH:mm dd,MMM";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if (date1.before(date2)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * method to parse date
     *
     * @param time
     * @return
     */
    public String parseTime(String time) {
        Date date = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String todaysDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            date = simpleDateFormat.parse(todaysDate + " " + time.split(" ")[0]);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * method to parse time to date time
     *
     * @param time
     * @return
     */
    public String parseTimeToTimeDate(String time) {
        Date date = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            String todaysDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            date = simpleDateFormat.parse(todaysDate + " " + time);

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd,MMM", Locale.getDefault());
            //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return sdf.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * hit api for activating deal
     */
    private void hitApiForActivateDeal() {
        appUtils.showProgressDialog(mActivity, false);
        HashMap<String, String> params = new HashMap<>();
        params.put(ApiKeys.DEAL_ID, dealData.getDealId());
        params.put(ApiKeys.DEAL_STATUS, "1");

        params.put(ApiKeys.DEAL_TITLE, etTitle.getText().toString().trim());
        params.put(ApiKeys.DEAL_DESCRIPTION, etDescription.getText().toString().trim());
        params.put(ApiKeys.TOTAL_ITEMS, etTotalItems.getText().toString().trim());
        params.put(ApiKeys.ORIGINAL_PRICE, etOriginalPrice.getText().toString().trim());
        params.put(ApiKeys.NEW_PRICE, tvNewPrice.getText().toString());
        params.put(ApiKeys.START_DATE_TIME, parseTime(etBeginTime.getText().toString().trim()));
        params.put(ApiKeys.END_DATE_TIME, parseTime(etEndTime.getText().toString().trim()));

        ApiInterface service = RestApi.createService(ApiInterface.class);
        Call<ResponseBody> call = service.deactivateDeal(AppSharedPrefs.getInstance(mActivity).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, ""), appUtils.encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                try {
                    JSONObject mainObject = new JSONObject(response);
                    if (mainObject.getInt("CODE") == 200) {
                        appUtils.showToast(mActivity, getString(R.string.deal_activated));
                        Intent intent = new Intent();
                        intent.putExtra(Constants.DEAL_POSITION_KEY, position);
                        intent.putExtra(Constants.DEAL_STATUS_KEY, "1");
                        setResult(Constants.DEAL_ACTIVIATE_RESULT_CODE, intent);
                        Intent intent1 = new Intent(Constants.REFRESH_RECCEIVER_KEY);
                        // You can also include some extra data.
                        intent1.putExtra("refresh", true);
                        LocalBroadcastManager.getInstance(mActivity).sendBroadcast(intent1);
                        finish();
                    } else if (mainObject.getInt("CODE") == ApiKeys.UNAUTHORISED_CODE) {
                        appUtils.logoutFromApp(mActivity, mainObject.optString("MESSAGE"));
                    } else {
                        appUtils.showSnackBar(activityCreateDeal, mainObject.optString("MESSAGE"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccessErrorBody(String response) {

            }

            @Override
            public void onFailure() {
                appUtils.showSnackBar(activityCreateDeal, getString(R.string.txt_something_went_wrong));
            }
        });

    }


}
