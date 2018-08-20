package app.bonapp.activities;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
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
import app.bonapp.interfaces.DialogButtonClickListener;
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
 * In this class deal detail of merchants are shown, from here merchant can edit their activated deal also .
 */

public class MerchantDealDetailActivity extends AppCompatActivity {

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
    @BindView(R.id.btn_deactivate_deal)
    Button btnDeactivateDeal;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_end_btn)
    ImageView ivEndBtn;
    @BindView(R.id.tv_end)
    TextView tvEnd;
    @BindView(R.id.et_description)
    EditText etDescription;
    @BindView(R.id.ll_description)
    LinearLayout llDescription;
    @BindView(R.id.tv_discount_perc)
    TextView tvDiscountPerc;
    @BindView(R.id.seekbar_newprice)
    AppCompatSeekBar seekbarNewprice;
    @BindView(R.id.ll_slider)
    LinearLayout llSlider;
    @BindView(R.id.tv_new_price)
    TextView tvNewPrice;
    @BindView(R.id.activity_merchant_detail)
    LinearLayout activityMerchantDetail;
    private AppUtils appUtils;
    private Activity mActivity;
    private String dealId;
    private boolean isEditModeOn = false;
    private int position;
    private DATum dealData;
    private String pickUpTime;
    private double discount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_deal_detail);
        ButterKnife.bind(this);
        mActivity = MerchantDealDetailActivity.this;
        appUtils = AppUtils.getInstance();
        setUpView();
        if (getIntent().getExtras() != null) {
            //dealId = getIntent().getStringExtra(Constants.DEAL_ID_KEY);
            dealData = getIntent().getParcelableExtra("deal_model");
            position = getIntent().getIntExtra(Constants.DEAL_POSITION_KEY, 0);
            dealId = dealData.getDealId();
            pickUpTime = getIntent().getStringExtra(Constants.PICKUP_TIME_MERCHANT);

        }
        setData();
        setDiscountedPrice();
    }

    /**
     * method to setup view
     */
    private void setUpView() {
        tvTitle.setText("");
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setImageResource(R.drawable.ic_greenback);
        tvEnd.setText(R.string.edit);
//        tvEnd.setVisibility(View.VISIBLE);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    /**
     * method to set data in views
     *
     * @param
     */
    private void setData() {
        etTitle.setText(dealData.getDealTitle());
        etTotalItems.setText(dealData.getTotalItems());
        etOriginalPrice.setText(dealData.getOriginalPrice());
        tvNewPrice.setText(dealData.getNewPrice());
        etBeginTime.setText(appUtils.parseToTimeAndDate(dealData.getStartTime()));
        etEndTime.setText(appUtils.parseToTimeAndDate(dealData.getEndTime()));
        etDescription.setText(dealData.getDealDescription());
        discount = (Double.parseDouble(dealData.getOriginalPrice()) -
                Double.parseDouble(dealData.getNewPrice())) / Double.parseDouble(dealData.getOriginalPrice()) * 100;
        String perc = discount + getString(R.string.percent_symbol);
        tvDiscountPerc.setText(perc);
        seekbarNewprice.setProgress((int) discount / 5);
        if (dealData.getDealStatus().equals("1")) {
            tvEnd.setVisibility(View.VISIBLE);
            btnDeactivateDeal.setVisibility(View.VISIBLE);
            tvTitle.setText(R.string.active_deal);
        }

        etOriginalPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isEditModeOn) {
                    if (etOriginalPrice.getText().length() > 0) {
                        try {
                            double originalPrice = Double.parseDouble(etOriginalPrice.getText().toString());
                            double sp = (discount * originalPrice) / 100;

                            tvNewPrice.setText(String.format(Locale.getDefault(), "%.2f", originalPrice - sp));
                        } catch (NumberFormatException ne) {
                            ne.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * method to set discounted price
     */
    private void setDiscountedPrice() {
        seekbarNewprice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                discount = progress * 5;
                tvDiscountPerc.setText(progress * 5 + "%");
                try {
                    double originalPrice = Double.parseDouble(etOriginalPrice.getText().toString());
                    double sp = (discount * originalPrice) / 100;

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

    @OnClick({R.id.iv_back, R.id.tv_end, R.id.btn_deactivate_deal, R.id.activity_merchant_detail, R.id.et_begin_time,
            R.id.et_end_time, R.id.ll_new_price, R.id.tv_new_price})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                appUtils.hideNativeKeyboard(mActivity);
                onBackPressed();
                break;
            case R.id.tv_end:
                appUtils.hideNativeKeyboard(mActivity);

                changeEditmode(true);
                isEditModeOn = true;

                break;
            case R.id.btn_deactivate_deal:
                appUtils.hideNativeKeyboard(mActivity);
                if (isEditModeOn) {
                    if (checkValidations()) {
                        if (appUtils.isInternetOn(mActivity)) {
                            hitApiForEditDeal();
                        } else {
                            appUtils.showSnackBar(activityMerchantDetail, getString(R.string.internet_offline));
                        }
                    }
                } else {
                    appUtils.showAlertDialog(mActivity, getString(R.string.deactivate_deal),
                            getString(R.string.deactivate_popup_msg), getString(R.string.yes), getString(R.string.cancel), new DialogButtonClickListener() {
                                @Override
                                public void positiveButtonClick() {
                                    if (appUtils.isInternetOn(mActivity)) {
                                        hitApiForDeactivateDeal();
                                    } else {
                                        appUtils.showSnackBar(activityMerchantDetail, getString(R.string.internet_offline));
                                    }

                                }

                                @Override
                                public void negativeButtonClick() {

                                }
                            });
                }
                break;
            case R.id.activity_merchant_detail:
                appUtils.hideNativeKeyboard(mActivity);
                break;
            case R.id.et_begin_time:
                appUtils.hideNativeKeyboard(mActivity);
                if (isEditModeOn) {
                    selectDeselectNewPrice(false);
                    showTimePicker(etBeginTime, true);
                }
                break;
            case R.id.et_end_time:
                appUtils.hideNativeKeyboard(mActivity);
                if (isEditModeOn) {
                    selectDeselectNewPrice(false);
                    if (TextUtils.isEmpty(etBeginTime.getText().toString())) {
                        appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.empty_begin_time));

                    } else {
                        showTimePicker(etEndTime, false);
                    }
                }
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
        }
    }

    /**
     * this method is to change edit mode of the deal
     *
     * @param on true for enabling edit mode and false for disabling
     */
    private void changeEditmode(boolean on) {
        if (on) {
            //tvEnd.setText(getString(R.string.done));
            tvEnd.setVisibility(View.GONE);
            btnDeactivateDeal.setVisibility(View.VISIBLE);
            btnDeactivateDeal.setText(R.string.done);
            etTitle.setEnabled(true);
            etTitle.setSelection(etTitle.getText().length());
            etDescription.setEnabled(true);
            etOriginalPrice.setEnabled(true);
            tvNewPrice.setEnabled(true);
            etTotalItems.setEnabled(true);
            etBeginTime.setEnabled(true);
            etBeginTime.setFocusable(false);
            etEndTime.setEnabled(true);
            etEndTime.setFocusable(false);

        } else {
            tvEnd.setVisibility(View.VISIBLE);
            tvEnd.setText(R.string.edit);
            btnDeactivateDeal.setVisibility(View.GONE);
            btnDeactivateDeal.setText(R.string.deactivate_deal);
            etTitle.setEnabled(false);
            etDescription.setEnabled(false);
            etOriginalPrice.setEnabled(false);
            tvNewPrice.setEnabled(false);
            etTotalItems.setEnabled(false);
            etBeginTime.setEnabled(false);
            etEndTime.setEnabled(false);
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
     * hit api for deactivating deal
     */
    private void hitApiForDeactivateDeal() {
        appUtils.showProgressDialog(mActivity, false);
        HashMap<String, String> params = new HashMap<>();
        params.put(ApiKeys.DEAL_ID, dealId);
        params.put(ApiKeys.DEAL_STATUS, "2");
        ApiInterface service = RestApi.createService(ApiInterface.class);
        Call<ResponseBody> call = service.deactivateDeal(AppSharedPrefs.getInstance(mActivity).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, ""), appUtils.encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                try {
                    JSONObject mainObject = new JSONObject(response);
                    if (mainObject.getInt("CODE") == 200) {
                        appUtils.showToast(mActivity, getString(R.string.deal_deactivated));
                        Intent intent = new Intent();
                        intent.putExtra(Constants.DEAL_POSITION_KEY, position);
                        intent.putExtra(Constants.DEAL_STATUS_KEY, "2");
                        setResult(Constants.DEAL_DEACTIVIATE_RESULT_CODE, intent);
                        finish();
                    } else if (mainObject.getInt("CODE") == ApiKeys.UNAUTHORISED_CODE) {
                        appUtils.logoutFromApp(mActivity, mainObject.optString("MESSAGE"));
                    } else {
                        appUtils.showSnackBar(activityMerchantDetail, mainObject.optString("MESSAGE"));
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
                appUtils.showSnackBar(activityMerchantDetail, getString(R.string.txt_something_went_wrong));
            }
        });

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
        } else {
            llNewPrice.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
            llSlider.setVisibility(View.GONE);
        }
    }

    /**
     * method to check validations
     *
     * @return
     */
    private boolean checkValidations() throws NumberFormatException {
        if (TextUtils.isEmpty(etTitle.getText().toString().trim())) {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.empty_title_war));
            return false;
        }
        if (TextUtils.isEmpty(etDescription.getText().toString().trim())) {
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
        } else if (Double.parseDouble(etOriginalPrice.getText().toString().trim()) < Double.parseDouble(tvNewPrice.getText().toString().trim())) {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.new_price_cant_greater));
            return false;
        } else
            return true;
    }

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
     * method to hit api for creating deal
     */
    private void hitApiForEditDeal() {
        appUtils.showProgressDialog(mActivity, false);
        HashMap<String, String> params = new HashMap<>();
        params.put(ApiKeys.DEAL_ID, dealId);
        params.put(ApiKeys.DEAL_TITLE, etTitle.getText().toString().trim());
        params.put(ApiKeys.DEAL_DESCRIPTION, etDescription.getText().toString().trim());
        params.put(ApiKeys.TOTAL_ITEMS, etTotalItems.getText().toString().trim());
        params.put(ApiKeys.ORIGINAL_PRICE, etOriginalPrice.getText().toString().trim());
        params.put(ApiKeys.NEW_PRICE, tvNewPrice.getText().toString());
        params.put(ApiKeys.START_DATE_TIME, parseTime(etBeginTime.getText().toString().trim()));
        params.put(ApiKeys.END_DATE_TIME, parseTime(etEndTime.getText().toString().trim()));

        ApiInterface service = RestApi.createService(ApiInterface.class);
        Call<ResponseBody> call = service.editDeal(AppSharedPrefs.getInstance(mActivity).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, ""), appUtils.encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                CreateDealModel createDealModel = new Gson().fromJson(response, CreateDealModel.class);
                if (createDealModel.getCODE() == 200) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.DEAL_POSITION_KEY, position);
                    intent.putExtra(Constants.DEAL_TITLE, createDealModel.getDATA().getDealTitle());
                    intent.putExtra(Constants.TOTAL_ITEMS_KEY, createDealModel.getDATA().getTotalItems());
                    intent.putExtra(Constants.BEGIN_TIME_KEY, createDealModel.getDATA().getStartTime());
                    intent.putExtra(Constants.END_TIME_KEY, createDealModel.getDATA().getEndTime());
                    intent.putExtra(Constants.DEAL_CREATED_DATE_KEY, createDealModel.getDATA().getDealCreatedOn());
                    setResult(Constants.DEAL_EDIT_RESULT_CODE, intent);
                    finish();
                } else if (createDealModel.getCODE() == ApiKeys.UNAUTHORISED_CODE) {
                    appUtils.logoutFromApp(mActivity, createDealModel.getMESSAGE());
                } else {
                    appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), createDealModel.getMESSAGE());
                }
            }

            @Override
            public void onSuccessErrorBody(String response) {

            }

            @Override
            public void onFailure() {
                appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.txt_something_went_wrong));

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (isEditModeOn) {
            changeEditmode(false);
            isEditModeOn = false;
        } else
            super.onBackPressed();
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
            return sdf.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * method to parse time to send in api
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
}
