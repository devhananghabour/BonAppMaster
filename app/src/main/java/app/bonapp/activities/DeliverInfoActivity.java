package app.bonapp.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.bonapp.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.bonapp.adapters.ListingAdapter;
import app.bonapp.constants.ApiKeys;
import app.bonapp.constants.Constants;
import app.bonapp.interfaces.OnSelectItemListener;
import app.bonapp.models.countrystatecity.DropDownData;
import app.bonapp.models.countrystatecity.ListModel;
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
 * this class is used for filling the user's address or mobile no in case if address or mob no is
 * not filled already while placing the order.
 */
public class DeliverInfoActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_end)
    TextView tvEnd;
    @BindView(R.id.et_adress)
    EditText etAdress;
    @BindView(R.id.tv_country)
    TextView tvCountry;
    @BindView(R.id.tv_state)
    TextView tvState;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.et_postal)
    EditText etPostal;
    @BindView(R.id.activity_delivery_info)
    RelativeLayout activityDeliveryInfo;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.et_mobile_no)
    EditText etMobileNo;

    private Activity mActivity;
    private AppUtils appUtils;
    private final int COUNTRY_TYPE = 1, STATE_TYPE = 2, CITY_TYPE = 3;
    private String countryId, stateId, cityId;
    private List<DropDownData> countryList;
    private List<DropDownData> stateList;
    private List<DropDownData> cityList;
    private ListingAdapter countryAdapter;
    private ListingAdapter stateAdapter;
    private ListingAdapter cityAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver_info);
        ButterKnife.bind(this);
        appUtils = AppUtils.getInstance();
        mActivity = DeliverInfoActivity.this;
        setUpViews();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    private void setUpViews() {
        countryList = new ArrayList<>();
        stateList = new ArrayList<>();
        cityList = new ArrayList<>();

        tvTitle.setText(R.string.deliver_info);
        ivBack.setVisibility(View.VISIBLE);
        tvEnd.setVisibility(View.VISIBLE);
        tvEnd.setText(R.string.next);
        etAdress.setText(AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.ADDRESS,""));
        etMobileNo.setText(AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.USER_MOBILE,""));
        etPostal.setText(AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.POSTAL_CODE,""));
    }

    @OnClick({R.id.iv_back, R.id.tv_end, R.id.tv_country, R.id.tv_state, R.id.tv_city})
    public void onViewClicked(View view) {
        appUtils.hideNativeKeyboard(mActivity);
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_end:
                if (checkValidations()) {
                    if (appUtils.isInternetOn(mActivity)) {
                        hitAddAddressApi();
                    } else {
                        appUtils.showSnackBar(activityDeliveryInfo, getString(R.string.internet_offline));
                    }
                }
                break;
            case R.id.tv_country:
                showCountryPickerDialog();

                break;
            case R.id.tv_state:
                if (countryId != null) {
                    showStatePickerDialog();
                } else {
                    appUtils.showSnackBar(tvState, getString(R.string.plz_slect_country_first));
                }
                break;
            case R.id.tv_city:
                if (stateId != null) {
                    showCityPickerDialog();
                } else {
                    appUtils.showSnackBar(activityDeliveryInfo, getString(R.string.plz_select_state_first));
                }
                break;
        }
    }


    /**
     * method to check validations
     *
     * @return
     */
    private boolean checkValidations() {
        if (TextUtils.isEmpty(etAdress.getText().toString().trim())) {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.empty_address_war));
            return false;
        } else if (countryId == null || countryId.equals("")) {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.country_war));
            return false;
        } else if (stateId == null || stateId.equals("")) {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.state_war));
            return false;
        }
        else  if (TextUtils.isEmpty(etPostal.getText().toString().trim())) {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.empty_postal));
            return false;
        }
        else if (TextUtils.isEmpty(etMobileNo.getText().toString().trim())) {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.empty_mob_no));
            return false;
        }
        else if (etMobileNo.getText().toString().trim().length()<7) {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.mob_no_7_digit));
            return false;
        } /*else if (cityId==null || cityId.equals("")) {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.city_war));
            return false;
        }*/ else
            return true;
    }


    /**
     * method to hit listing api for countries,states and cities
     * @param type
     * @param id
     */
    private void hitListingApi(final int type, String id, final ProgressBar progressBar,
                               final RecyclerView recyclerView, final Dialog dialog, final TextView tvNoRecord) {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, String> params = new HashMap<>();
        if (type == STATE_TYPE) {
            params.put("countryId", id);
        } else if (type == CITY_TYPE) {
            params.put("stateId", id);
        }
        ApiInterface service = RestApi.createService(ApiInterface.class);
        Call<ResponseBody> call = service.getCountryCityStateDropdown(Constants.API_KEY, appUtils.encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                ListModel listModel = new Gson().fromJson(response, ListModel.class);
                if (listModel.getCODE() == 200) {
                    switch (type) {
                        case COUNTRY_TYPE:
                            countryList.clear();
                            countryList.addAll(listModel.getCountryList());

                            if (dialog.isShowing()) {
                                countryAdapter = new ListingAdapter(mActivity, countryList,tvNoRecord);
                                recyclerView.setAdapter(countryAdapter);
                                countryAdapter.setOnSelectItemListener(new OnSelectItemListener() {
                                    @Override
                                    public void onSelectItem(String id, String name) {
                                        countryId = id;
                                        tvCountry.setText(name);

                                        tvState.setText("");
                                        tvCity.setText("");

                                        dialog.dismiss();
                                    }
                                });
                            }
                            break;
                        case STATE_TYPE:
                            stateList.clear();
                            stateList.addAll(listModel.getStateList());

                            if (dialog.isShowing()) {
                                stateAdapter = new ListingAdapter(mActivity, stateList, tvNoRecord);
                                recyclerView.setAdapter(stateAdapter);
                                stateAdapter.setOnSelectItemListener(new OnSelectItemListener() {
                                    @Override
                                    public void onSelectItem(String id, String name) {
                                        stateId = id;
                                        tvState.setText(name);

                                        tvCity.setText("");

                                        dialog.dismiss();
                                    }
                                });
                            }
                            break;
                        case CITY_TYPE:
                            cityList.clear();
                            cityList.addAll(listModel.getCityList());

                            if (dialog.isShowing()) {
                                cityAdapter = new ListingAdapter(mActivity, cityList, tvNoRecord);
                                recyclerView.setAdapter(cityAdapter);
                                cityAdapter.setOnSelectItemListener(new OnSelectItemListener() {
                                    @Override
                                    public void onSelectItem(String id, String name) {
                                        cityId = id;
                                        tvCity.setText(name);

                                        dialog.dismiss();
                                    }
                                });
                            }
                            break;
                    }

                } else {
                    appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), listModel.getMESSAGE());
                }
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onSuccessErrorBody(String response) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure() {
                progressBar.setVisibility(View.GONE);

            }
        });
    }
    /*
    * Method to show country picker
    *
    **/
    private void showCountryPickerDialog() {
        final Dialog popUpDialogCountry = new Dialog(mActivity);
        popUpDialogCountry.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popUpDialogCountry.setContentView(R.layout.dialog_country_code_picker);
        popUpDialogCountry.setCancelable(true);

        WindowManager.LayoutParams lp = popUpDialogCountry.getWindow().getAttributes();
        lp.dimAmount = 0.5f;
        popUpDialogCountry.getWindow().setAttributes(lp);
        popUpDialogCountry.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        popUpDialogCountry.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RecyclerView rv_dialog_list = (RecyclerView) popUpDialogCountry.findViewById(R.id.rv_country_code);
        rv_dialog_list.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        EditText etSearch = (EditText) popUpDialogCountry.findViewById(R.id.et_search);
        etSearch.setHint(R.string.hint_search_country);
        ProgressBar progressBar=(ProgressBar)popUpDialogCountry.findViewById(R.id.progress_bar);
        TextView tvNoRecords=(TextView)popUpDialogCountry.findViewById(R.id.tv_no_records);
        //    dropDownDataList.clear();
        //    dropDownDataList.addAll(countryList);


        //filter list
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (countryAdapter!=null)
                    countryAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (appUtils.isInternetOn(mActivity)){
            hitListingApi(COUNTRY_TYPE,"",progressBar,rv_dialog_list,popUpDialogCountry,tvNoRecords);
        }else {
            appUtils.showSnackBar(activityDeliveryInfo,getString(R.string.internet_offline));
        }
        popUpDialogCountry.show();

    }

    /*
       * Method to show country picker
       *
       **/
    private void showStatePickerDialog() {
        final Dialog popUpDialogCountry = new Dialog(mActivity);
        popUpDialogCountry.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popUpDialogCountry.setContentView(R.layout.dialog_country_code_picker);
        popUpDialogCountry.setCancelable(true);

        WindowManager.LayoutParams lp = popUpDialogCountry.getWindow().getAttributes();
        lp.dimAmount = 0.5f;
        popUpDialogCountry.getWindow().setAttributes(lp);
        popUpDialogCountry.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        popUpDialogCountry.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RecyclerView rv_dialog_list = (RecyclerView) popUpDialogCountry.findViewById(R.id.rv_country_code);
        rv_dialog_list.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        EditText etSearch = (EditText) popUpDialogCountry.findViewById(R.id.et_search);
        etSearch.setHint(R.string.hint_search_state);
        ProgressBar progressBar=(ProgressBar)popUpDialogCountry.findViewById(R.id.progress_bar);
        TextView tvNoRecords=(TextView)popUpDialogCountry.findViewById(R.id.tv_no_records);



        //filter list
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (stateAdapter!=null)
                    stateAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (appUtils.isInternetOn(mActivity)){
            hitListingApi(STATE_TYPE,countryId,progressBar,rv_dialog_list,popUpDialogCountry,tvNoRecords);
        }else {
            appUtils.showSnackBar(activityDeliveryInfo,getString(R.string.internet_offline));
        }

        popUpDialogCountry.show();

    }

    /*
        * Method to show city picker
        *
        **/
    private void showCityPickerDialog() {
        final Dialog popUpDialogCountry = new Dialog(mActivity);
        popUpDialogCountry.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popUpDialogCountry.setContentView(R.layout.dialog_country_code_picker);
        popUpDialogCountry.setCancelable(true);

        WindowManager.LayoutParams lp = popUpDialogCountry.getWindow().getAttributes();
        lp.dimAmount = 0.5f;
        popUpDialogCountry.getWindow().setAttributes(lp);
        popUpDialogCountry.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        popUpDialogCountry.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RecyclerView rv_dialog_list = (RecyclerView) popUpDialogCountry.findViewById(R.id.rv_country_code);
        rv_dialog_list.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        EditText etSearch = (EditText) popUpDialogCountry.findViewById(R.id.et_search);
        etSearch.setHint(R.string.hint_search_city);
        ProgressBar progressBar=(ProgressBar)popUpDialogCountry.findViewById(R.id.progress_bar);
        TextView tvNoRecords=(TextView)popUpDialogCountry.findViewById(R.id.tv_no_records);

        //filter list
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (cityAdapter!=null)
                    cityAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (appUtils.isInternetOn(mActivity)){
            hitListingApi(CITY_TYPE,stateId,progressBar,rv_dialog_list,popUpDialogCountry,tvNoRecords);
        }else {
            appUtils.showSnackBar(activityDeliveryInfo,getString(R.string.internet_offline));
        }
        popUpDialogCountry.show();

    }



    /**
     * method to hit api for adding address
     */
    private void hitAddAddressApi() {
        appUtils.showProgressDialog(mActivity,false);
        HashMap<String, String> params = new HashMap<>();
        params.put(ApiKeys.ADDRESS, etAdress.getText().toString().trim());
        params.put(ApiKeys.POSTAL_CODE, etPostal.getText().toString().trim());
        params.put(ApiKeys.MOBILE_NUMBER,etMobileNo.getText().toString().trim());
        if (countryId != null) {
            params.put(ApiKeys.COUNTRY_ID, countryId);
        }
        if (stateId != null) {
            params.put(ApiKeys.STATE_ID, stateId);
        }
        if (cityId != null) {
            params.put(ApiKeys.CITY_ID, cityId);
        }

        ApiInterface service = RestApi.createService(ApiInterface.class);
        Call<ResponseBody> call = service.addAdress(AppSharedPrefs.getInstance(mActivity).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, ""), appUtils.encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                try {
                    JSONObject mainObject = new JSONObject(response);
                    if (mainObject.getInt(ApiKeys.CODE) == 200) {
                        JSONObject dataObject = mainObject.getJSONObject("DATA");

                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.ADDRESS, dataObject.getString(ApiKeys.ADDRESS));
                        JSONObject countryObject = dataObject.getJSONObject(ApiKeys.COUNTRY_ID);
                        JSONObject stateObject = dataObject.getJSONObject(ApiKeys.STATE_ID);
                        JSONObject cityObject = dataObject.getJSONObject(ApiKeys.CITY_ID);
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.COUNTRY_NAME, countryObject.getString(ApiKeys.NAME));
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.STATE_NAME, stateObject.getString(ApiKeys.NAME));
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.CITY_NAME, cityObject.getString(ApiKeys.NAME));
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.COUNTRY_ID, countryObject.getString(ApiKeys.ID));
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.ISO_CODE, countryObject.getString(ApiKeys.ISO_CODE));
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.STATE_ID, stateObject.getString(ApiKeys.ID));
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.CITY_ID, cityObject.getString(ApiKeys.ID));
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.POSTAL_CODE, dataObject.optString(ApiKeys.POSTAL_CODE));
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.USER_MOBILE, dataObject.optString(ApiKeys.MOBILE_NUMBER));

                        finish();

                    } else if (mainObject.getInt(ApiKeys.CODE) == ApiKeys.UNAUTHORISED_CODE) {
                        appUtils.logoutFromApp(mActivity, mainObject.optString(ApiKeys.MESSAGE));
                    } else {
                        appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), mainObject.optString(ApiKeys.MESSAGE));
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

            }
        });

    }

}
