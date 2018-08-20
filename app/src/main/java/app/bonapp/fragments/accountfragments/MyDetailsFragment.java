package app.bonapp.fragments.accountfragments;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.bonapp.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.bonapp.activities.EditMyAccountActivity;
import app.bonapp.adapters.ListingAdapter;
import app.bonapp.constants.ApiKeys;
import app.bonapp.constants.Constants;
import app.bonapp.fragments.BaseFragment;
import app.bonapp.interfaces.DialogButtonClickListener;
import app.bonapp.interfaces.OnSelectItemListener;
import app.bonapp.models.countrystatecity.DropDownData;
import app.bonapp.models.countrystatecity.ListModel;
import app.bonapp.network.ApiCall;
import app.bonapp.network.ApiInterface;
import app.bonapp.network.NetworkListener;
import app.bonapp.network.RestApi;
import app.bonapp.utils.AppSharedPrefs;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class MyDetailsFragment extends BaseFragment {

    @BindView(R.id.tv_city)
    TextView    tvCity;
    @BindView(R.id.tv_state)
    TextView    tvState;
    @BindView(R.id.tv_country)
    TextView    tvCountry;
    @BindView(R.id.tv_change_email)
    TextView    tvChangeEmail;
    @BindView(R.id.tv_change_pwd)
    TextView    tvChangePwd;
    @BindView(R.id.view_change_email)
    View        viewChangeEmail;
    @BindView(R.id.view_change_pwd)
    View        viewChangePwd;
    @BindView(R.id.et_first_name)
    EditText    etFirstName;
    @BindView(R.id.et_phone_no)
    EditText    etPhoneNo;
    @BindView(R.id.et_adress)
    EditText    etAdress;
    @BindView(R.id.et_postal)
    EditText    etPostal;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private List<DropDownData> countryList;
    private List<DropDownData> stateList;
    private List<DropDownData> cityList;
    private final int COUNTRY_TYPE = 1, STATE_TYPE = 2, CITY_TYPE = 3;
    private boolean isEditModeOn = false;
    private String countryId, stateId, cityId;
    private ListingAdapter countryAdapter;
    private ListingAdapter stateAdapter;
    private ListingAdapter cityAdapter;


    @Override
    protected int getResourceLayoutId() {
        return R.layout.fragment_my_details;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setData();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        ((EditMyAccountActivity) getActivity()).setMyDetailToolbar();
    }

    /**
     * method to set data in fields
     */
    private void setData() {
        countryList = new ArrayList<>();
        stateList = new ArrayList<>();
        cityList = new ArrayList<>();
        etFirstName.setText(AppSharedPrefs.getInstance(getActivity()).getString(AppSharedPrefs.PREF_KEY.USER_NAME, ""));
        etPhoneNo.setText(AppSharedPrefs.getInstance(getActivity()).getString(AppSharedPrefs.PREF_KEY.USER_MOBILE, ""));
        etAdress.setText(AppSharedPrefs.getInstance(getActivity()).getString(AppSharedPrefs.PREF_KEY.ADDRESS, ""));
        tvCountry.setText(AppSharedPrefs.getInstance(getActivity()).getString(AppSharedPrefs.PREF_KEY.COUNTRY_NAME, ""));
        tvState.setText(AppSharedPrefs.getInstance(getActivity()).getString(AppSharedPrefs.PREF_KEY.STATE_NAME, ""));
        tvCity.setText(AppSharedPrefs.getInstance(getActivity()).getString(AppSharedPrefs.PREF_KEY.CITY_NAME, ""));
        etPostal.setText(AppSharedPrefs.getInstance(getActivity()).getString(AppSharedPrefs.PREF_KEY.POSTAL_CODE, ""));
        countryId = AppSharedPrefs.getInstance(getActivity()).getString(AppSharedPrefs.PREF_KEY.COUNTRY_ID, "");
        stateId = AppSharedPrefs.getInstance(getActivity()).getString(AppSharedPrefs.PREF_KEY.STATE_ID, "");
        cityId = AppSharedPrefs.getInstance(getActivity()).getString(AppSharedPrefs.PREF_KEY.CITY_ID, "");


        if (AppSharedPrefs.getInstance(getActivity()).getString(AppSharedPrefs.PREF_KEY.USER_TYPE, "2").
                equals(Constants.MERCHANT_TYPE)) {
            tvChangeEmail.setVisibility(View.GONE);
            viewChangeEmail.setVisibility(View.GONE);
        } else {
            if (AppSharedPrefs.getInstance(getActivity()).getString(AppSharedPrefs.PREF_KEY.SOCIAL_TYPE, "").equals("facebook")) {
                tvChangeEmail.setVisibility(View.GONE);
                tvChangePwd.setVisibility(View.GONE);
                viewChangeEmail.setVisibility(View.GONE);
                viewChangePwd.setVisibility(View.GONE);
            } else {
                tvChangeEmail.setVisibility(View.VISIBLE);
                viewChangeEmail.setVisibility(View.VISIBLE);
                viewChangePwd.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnClick({R.id.tv_change_email, R.id.tv_change_pwd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_change_email:
                if (AppSharedPrefs.getInstance(getActivity()).getString(AppSharedPrefs.PREF_KEY.USER_TYPE, "2").
                        equals(Constants.MERCHANT_TYPE)) {
                    appUtils.showAlertDialog(getActivity(), getString(R.string.access_denied), getString(R.string.merchant_email_change_message),
                                             getString(R.string.okay), "", new DialogButtonClickListener() {
                                @Override
                                public void positiveButtonClick() {

                                }

                                @Override
                                public void negativeButtonClick() {

                                }
                            });
                } else {
                    replaceFragmentInActivity(new ChangeEmailFragment());
                }
                break;
            case R.id.tv_change_pwd:
                replaceFragmentInActivity(new ChangePasswordFragment());
                break;
        }
    }

    /**
     * method to hit listing api for countries,states and cities
     *
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
        ApiCall.getInstance().hitService(getActivity(), call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                ListModel listModel = new Gson().fromJson(response, ListModel.class);
                if (listModel.getCODE() == 200) {
                    switch (type) {
                        case COUNTRY_TYPE:
                            countryList.clear();
                            countryList.addAll(listModel.getCountryList());

                            if (dialog.isShowing()) {
                                countryAdapter = new ListingAdapter(getActivity(), countryList, tvNoRecord);
                                int resId = R.anim.layout_animation_fall_down;
                                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);
                                recyclerView.setLayoutAnimation(animation);
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
                                stateAdapter = new ListingAdapter(getActivity(), stateList, tvNoRecord);
                                int resId = R.anim.layout_animation_fall_down;
                                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);
                                recyclerView.setLayoutAnimation(animation);

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
                                cityAdapter = new ListingAdapter(getActivity(), cityList, tvNoRecord);
                                int resId = R.anim.layout_animation_fall_down;
                                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), resId);
                                recyclerView.setLayoutAnimation(animation);

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
                    appUtils.showSnackBar(getActivity().findViewById(android.R.id.content), listModel.getMESSAGE());
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
        final Dialog popUpDialogCountry = new Dialog(getActivity());
        popUpDialogCountry.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popUpDialogCountry.setContentView(R.layout.dialog_country_code_picker);
        popUpDialogCountry.setCancelable(true);

        WindowManager.LayoutParams lp = popUpDialogCountry.getWindow().getAttributes();
        lp.dimAmount = 0.5f;
        popUpDialogCountry.getWindow().setAttributes(lp);
        popUpDialogCountry.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        popUpDialogCountry.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RecyclerView rv_dialog_list = (RecyclerView) popUpDialogCountry.findViewById(R.id.rv_country_code);
        rv_dialog_list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        EditText etSearch = (EditText) popUpDialogCountry.findViewById(R.id.et_search);
        etSearch.setHint(R.string.hint_search_country);
        ProgressBar progressBar = (ProgressBar) popUpDialogCountry.findViewById(R.id.progress_bar);
        TextView tvNoRecords = (TextView) popUpDialogCountry.findViewById(R.id.tv_no_records);
        //    dropDownDataList.clear();
        //    dropDownDataList.addAll(countryList);


        //filter list
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (countryAdapter != null)
                    countryAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (appUtils.isInternetOn(getActivity())) {
            hitListingApi(COUNTRY_TYPE, "", progressBar, rv_dialog_list, popUpDialogCountry, tvNoRecords);
        } else {
            appUtils.showSnackBar(getView(), getString(R.string.internet_offline));
        }
        popUpDialogCountry.show();

    }

    /*
     * Method to show country picker
     *
     **/
    private void showStatePickerDialog() {
        final Dialog popUpDialogCountry = new Dialog(getActivity());
        popUpDialogCountry.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popUpDialogCountry.setContentView(R.layout.dialog_country_code_picker);
        popUpDialogCountry.setCancelable(true);

        WindowManager.LayoutParams lp = popUpDialogCountry.getWindow().getAttributes();
        lp.dimAmount = 0.5f;
        popUpDialogCountry.getWindow().setAttributes(lp);
        popUpDialogCountry.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        popUpDialogCountry.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RecyclerView rv_dialog_list = (RecyclerView) popUpDialogCountry.findViewById(R.id.rv_country_code);
        rv_dialog_list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        EditText etSearch = (EditText) popUpDialogCountry.findViewById(R.id.et_search);
        etSearch.setHint(R.string.hint_search_state);
        ProgressBar progressBar = (ProgressBar) popUpDialogCountry.findViewById(R.id.progress_bar);
        TextView tvNoRecords = (TextView) popUpDialogCountry.findViewById(R.id.tv_no_records);


        //filter list
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (stateAdapter != null)
                    stateAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (appUtils.isInternetOn(getActivity())) {
            hitListingApi(STATE_TYPE, countryId, progressBar, rv_dialog_list, popUpDialogCountry, tvNoRecords);
        } else {
            appUtils.showSnackBar(getView(), getString(R.string.internet_offline));
        }

        popUpDialogCountry.show();

    }

    /*
     * Method to show city picker
     *
     **/
    private void showCityPickerDialog() {
        final Dialog popUpDialogCountry = new Dialog(getActivity());
        popUpDialogCountry.requestWindowFeature(Window.FEATURE_NO_TITLE);
        popUpDialogCountry.setContentView(R.layout.dialog_country_code_picker);
        popUpDialogCountry.setCancelable(true);

        WindowManager.LayoutParams lp = popUpDialogCountry.getWindow().getAttributes();
        lp.dimAmount = 0.5f;
        popUpDialogCountry.getWindow().setAttributes(lp);
        popUpDialogCountry.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        popUpDialogCountry.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RecyclerView rv_dialog_list = (RecyclerView) popUpDialogCountry.findViewById(R.id.rv_country_code);
        rv_dialog_list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        EditText etSearch = (EditText) popUpDialogCountry.findViewById(R.id.et_search);
        etSearch.setHint(R.string.hint_search_city);
        ProgressBar progressBar = (ProgressBar) popUpDialogCountry.findViewById(R.id.progress_bar);
        TextView tvNoRecords = (TextView) popUpDialogCountry.findViewById(R.id.tv_no_records);

        //filter list
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (cityAdapter != null)
                    cityAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (appUtils.isInternetOn(getActivity())) {
            hitListingApi(CITY_TYPE, stateId, progressBar, rv_dialog_list, popUpDialogCountry, tvNoRecords);
        } else {
            appUtils.showSnackBar(getView(), getString(R.string.internet_offline));
        }
        popUpDialogCountry.show();

    }


    @OnClick({R.id.tv_country, R.id.tv_state, R.id.tv_city})
    public void onViewClicked(View view) {
        appUtils.hideNativeKeyboard(getActivity());
        switch (view.getId()) {
            case R.id.tv_country:
                if (isEditModeOn) {
                    showCountryPickerDialog();
                }
                break;
            case R.id.tv_state:
                if (isEditModeOn) {
                    if (countryId != null && !countryId.equals("")) {
                        showStatePickerDialog();
                    } else {
                        appUtils.showSnackBar(getActivity().findViewById(android.R.id.content), getString(R.string.plz_slect_country_first));
                    }

                }
                break;
            case R.id.tv_city:
                if (isEditModeOn) {
                    if (stateId != null && !stateId.equals("")) {
                        showCityPickerDialog();
                    } else {
                        appUtils.showSnackBar(getActivity().findViewById(android.R.id.content), getString(R.string.plz_select_state_first));
                    }
                }
                break;
        }
    }

    /**
     * this method is to change edit mode of the deal
     *
     * @param on
     */
    private void changeEditMode(boolean on) {
        if (on) {
            etFirstName.setEnabled(true);
            etFirstName.setSelection(etFirstName.getText().length());
            etPhoneNo.setEnabled(true);
            etAdress.setEnabled(true);
            etPostal.setEnabled(true);

            tvCountry.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
            tvState.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
            tvCity.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
        } else {
            etFirstName.setEnabled(false);
            etPhoneNo.setEnabled(false);
            etAdress.setEnabled(false);
            etPostal.setEnabled(false);

            tvCountry.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            tvState.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
            tvCity.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    public void onEditClickListener() {
        appUtils.hideNativeKeyboard(getActivity());
        if (isEditModeOn) {
            if (checkValidations()) {
                if (appUtils.isInternetOn(getActivity())) {
                    hitEditProfileApi();
                } else {
                    appUtils.showSnackBar(getActivity().findViewById(android.R.id.content), getString(R.string.internet_offline));
                }
            }
        } else {
            isEditModeOn = true;
            ((EditMyAccountActivity) getActivity()).setEditButtonLabel(getString(R.string.save));
            changeEditMode(true);
        }
    }

    /**
     * method to check validations
     *
     * @return
     */
    private boolean checkValidations() {
        if (TextUtils.isEmpty(etFirstName.getText().toString().trim())) {
            appUtils.showSnackBar(getActivity().findViewById(android.R.id.content), getString(R.string.empty_name_war));
            return false;
        } else if (etPhoneNo.getText().length() > 0 && etPhoneNo.getText().toString().trim().length() < 7) {
            appUtils.showSnackBar(getActivity().findViewById(android.R.id.content), getString(R.string.valid_mo_no_war));
            return false;
        }
        /*else if (TextUtils.isEmpty(etAdress.getText().toString().trim())) {
            appUtils.showSnackBar(getActivity().findViewById(android.R.id.content), getString(R.string.empty_address_war));
            return false;
        } else if (countryId==null || countryId.equals("")) {
            appUtils.showSnackBar(getActivity().findViewById(android.R.id.content), getString(R.string.country_war));
            return false;
        } else if (stateId==null || stateId.equals("")) {
            appUtils.showSnackBar(getActivity().findViewById(android.R.id.content), getString(R.string.state_war));
            return false;
        }*/ /*else if (cityId==null || cityId.equals("")) {
            appUtils.showSnackBar(getActivity().findViewById(android.R.id.content), getString(R.string.city_war));
            return false;
        }*/
        else
            return true;
    }

    /**
     * hit edit profile api
     */
    private void hitEditProfileApi() {
        appUtils.showProgressDialog(getActivity(), false);
        HashMap<String, String> params = new HashMap<>();
        params.put(ApiKeys.NAME, etFirstName.getText().toString().trim());
        params.put(ApiKeys.PHONE_NO, etPhoneNo.getText().toString().trim());
        params.put(ApiKeys.ADDRESS, etAdress.getText().toString().trim());
        params.put(ApiKeys.POSTAL_CODE, etPostal.getText().toString().trim());

        if (countryId != null) {
            params.put(ApiKeys.COUNTRY, countryId);
        }
        if (stateId != null) {
            params.put(ApiKeys.STATE, stateId);
        }
        if (cityId != null) {
            params.put(ApiKeys.CITY, cityId);
        }

        ApiInterface service = RestApi.createService(ApiInterface.class);
        Call<ResponseBody> call = service.editProfile(AppSharedPrefs.getInstance(getActivity()).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, ""), appUtils.encryptData(params));
        ApiCall.getInstance().hitService(getActivity(), call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                try {
                    JSONObject mainObject = new JSONObject(response);
                    if (mainObject.getInt(ApiKeys.CODE) == 200) {
                        JSONObject dataObject = mainObject.getJSONObject("DATA");
                        changeEditMode(false);
                        isEditModeOn = false;
                        ((EditMyAccountActivity) getActivity()).setEditButtonLabel(getString(R.string.edit));

                        AppSharedPrefs.getInstance(getActivity()).putString(AppSharedPrefs.PREF_KEY.USER_NAME, dataObject.getString(ApiKeys.NAME));
                        AppSharedPrefs.getInstance(getActivity()).putString(AppSharedPrefs.PREF_KEY.USER_MOBILE, dataObject.getString(ApiKeys.MOBILE_NUMBER));
                        AppSharedPrefs.getInstance(getActivity()).putString(AppSharedPrefs.PREF_KEY.ADDRESS, dataObject.getString(ApiKeys.ADDRESS));
                        JSONObject countryObject = dataObject.getJSONObject(ApiKeys.COUNTRY_ID);
                        JSONObject stateObject = dataObject.getJSONObject(ApiKeys.STATE_ID);
                        JSONObject cityObject = dataObject.getJSONObject(ApiKeys.CITY_ID);
                        AppSharedPrefs.getInstance(getActivity()).putString(AppSharedPrefs.PREF_KEY.COUNTRY_NAME, countryObject.getString(ApiKeys.NAME));
                        AppSharedPrefs.getInstance(getActivity()).putString(AppSharedPrefs.PREF_KEY.STATE_NAME, stateObject.getString(ApiKeys.NAME));
                        AppSharedPrefs.getInstance(getActivity()).putString(AppSharedPrefs.PREF_KEY.CITY_NAME, cityObject.getString(ApiKeys.NAME));
                        AppSharedPrefs.getInstance(getActivity()).putString(AppSharedPrefs.PREF_KEY.ISO_CODE, countryObject.getString(ApiKeys.ISO_CODE));
                        AppSharedPrefs.getInstance(getActivity()).putString(AppSharedPrefs.PREF_KEY.COUNTRY_ID, countryObject.getString(ApiKeys.ID));
                        AppSharedPrefs.getInstance(getActivity()).putString(AppSharedPrefs.PREF_KEY.STATE_ID, stateObject.getString(ApiKeys.ID));
                        AppSharedPrefs.getInstance(getActivity()).putString(AppSharedPrefs.PREF_KEY.CITY_ID, cityObject.getString(ApiKeys.ID));
                        AppSharedPrefs.getInstance(getActivity()).putString(AppSharedPrefs.PREF_KEY.POSTAL_CODE, dataObject.optString(ApiKeys.POSTAL_CODE));

                    } else if (mainObject.getInt(ApiKeys.CODE) == ApiKeys.UNAUTHORISED_CODE) {
                        appUtils.logoutFromApp(getActivity(), mainObject.optString(ApiKeys.MESSAGE));
                    } else {
                        appUtils.showSnackBar(getActivity().findViewById(android.R.id.content), mainObject.optString(ApiKeys.MESSAGE));
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
