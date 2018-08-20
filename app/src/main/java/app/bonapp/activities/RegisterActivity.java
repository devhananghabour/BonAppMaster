package app.bonapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.bonapp.R;
import com.facebook.FacebookException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import app.bonapp.constants.ApiKeys;
import app.bonapp.constants.Constants;
import app.bonapp.models.signup.SignupModel;
import app.bonapp.network.ApiCall;
import app.bonapp.network.ApiInterface;
import app.bonapp.network.NetworkListener;
import app.bonapp.network.RestApi;
import app.bonapp.utils.AppSharedPrefs;
import app.bonapp.utils.AppUtils;
import app.dnitinverma.fblibrary.FBSignInAI;
import app.dnitinverma.fblibrary.interfaces.FBSignCallback;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * This class is for registering user in to the app from here user will be registered as customer only.
 */
public class RegisterActivity extends AppCompatActivity implements FBSignCallback {

    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_email_login)
    EditText etEmailRegister;
    @BindView(R.id.et_password_login)
    EditText etPasswordRegister;
    @BindView(R.id.et_ph_no_register)
    EditText etPhNoRegister;
    @BindView(R.id.et_name_register)
    EditText etNameRegister;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.tv_login)
    TextView tvLogin;
    @BindView(R.id.activity_register)
    RelativeLayout activityRegister;
    @BindView(R.id.rl_reg_fb)
    RelativeLayout rlRegFb;
    @BindView(R.id.tv_old_toggle)
    TextView tvPwdToggle;
    @BindView(R.id.ll_pwd)
    LinearLayout llPwd;

    private Activity mActivity;
    private AppUtils appUtils;
    private FBSignInAI mFBSignInAI;
    private boolean IS_FB_SIGNUP = false;
    private String social_id;
    private boolean isFromCheckout;
    private boolean isPwdVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mActivity = RegisterActivity.this;
        appUtils = AppUtils.getInstance();
        mFBSignInAI = FBSignInAI.getInstance(mActivity, this);
        tvCancel.setVisibility(View.VISIBLE);
        tvTitle.setText(getText(R.string.register));
        if (getIntent().getExtras() != null) {
            isFromCheckout = getIntent().getBooleanExtra(Constants.FROM_CHECKOUT_KEY, false);
        }
        setUpViews();
        showToggleOnInput(etPasswordRegister, tvPwdToggle);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    /**
     * method to highlight icons on text input
     */
    private void setUpViews() {
        appUtils.highlightIcons(etEmailRegister, R.drawable.ic_register_greymail, R.drawable.ic_mail);
        appUtils.highlightIcons(etPasswordRegister, R.drawable.ic_register_greypassword, R.drawable.ic_lock);
        appUtils.highlightIcons(etNameRegister, R.drawable.ic_register_user, R.drawable.ic_user);
        appUtils.highlightIcons(etPhNoRegister, R.drawable.ic_register_phone, R.drawable.ic_phone);

        tvLogin.setPaintFlags(tvLogin.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    @OnClick({R.id.tv_cancel, R.id.btn_register, R.id.rl_reg_fb, R.id.tv_login, R.id.tv_old_toggle})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                onBackPressed();
                break;
            case R.id.btn_register:
                appUtils.hideNativeKeyboard(mActivity);
                if (checkValidations()) {
                    if (appUtils.isInternetOn(mActivity)) {
                        HashMap<String, String> params = new HashMap<>();
                        if (IS_FB_SIGNUP) {
                            params.put(ApiKeys.SIGNUP_TYPE, "facebook");
                            params.put(ApiKeys.SOCIAL_ID, social_id);
                        } else {
                            params.put(ApiKeys.SIGNUP_TYPE, "email");
                            params.put(ApiKeys.PASSWROD, etPasswordRegister.getText().toString().trim());

                        }

                        params.put(ApiKeys.NAME, etNameRegister.getText().toString().trim());
                        params.put(ApiKeys.EMAIL, etEmailRegister.getText().toString().trim());
                        params.put(ApiKeys.MOBILE_NUMBER, etPhNoRegister.getText().toString().trim());
                        //params.put(ApiKeys.LATITUDE_KEY,etPhNoRegister.getText().toString().trim());
                        //params.put(ApiKeys.LONGITUDE_KEY,etPhNoRegister.getText().toString().trim());
                        params.put(ApiKeys.DEVICE_TYPE, Constants.ANDROID_TYPE);
                        params.put(ApiKeys.DEVICE_TOKEN, FirebaseInstanceId.getInstance().getToken());
                        hitApiForRegister(params);
                    } else {
                        appUtils.showSnackBar(activityRegister, getString(R.string.internet_offline));
                    }
                }
                break;
            case R.id.rl_reg_fb:
                mFBSignInAI.doSignIn();
                //appUtils.showSnackBar(activityRegister,"Under Development!");

                break;
            case R.id.tv_login:
                Intent intent = new Intent(mActivity, LoginActivity.class);
                intent.putExtra(Constants.FROM_CHECKOUT_KEY, isFromCheckout);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_old_toggle:
                if (isPwdVisible) {
                    isPwdVisible = false;
                    etPasswordRegister.setTransformationMethod(new PasswordTransformationMethod());
                    tvPwdToggle.setText(getString(R.string.show));
                } else {
                    isPwdVisible = true;
                    etPasswordRegister.setTransformationMethod(null);
                    tvPwdToggle.setText(getString(R.string.hide));
                }
                etPasswordRegister.setSelection(etPasswordRegister.length());
                break;
        }
    }

    private void showToggleOnInput(final EditText editText, final TextView textView) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText.getText().length() == 0) {
                    textView.setVisibility(View.GONE);
                    editText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_register_greypassword, 0, 0, 0);
                    TransitionManager.beginDelayedTransition(activityRegister);
                } else {
                    textView.setVisibility(View.VISIBLE);
                    editText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_lock, 0, 0, 0);
                    TransitionManager.beginDelayedTransition(activityRegister);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    /**
     * method to check validations
     *
     * @return
     */
    private boolean checkValidations() {
        if (TextUtils.isEmpty(etEmailRegister.getText().toString().trim())) {
            appUtils.showSnackBar(activityRegister, getString(R.string.empty_email));
            return false;
        } else if (!appUtils.isValidEmail(etEmailRegister.getText().toString().trim())) {
            appUtils.showSnackBar(activityRegister, getString(R.string.valid_email_war));
            return false;
        } else if (!IS_FB_SIGNUP && TextUtils.isEmpty(etPasswordRegister.getText().toString().trim())) {
            appUtils.showSnackBar(activityRegister, getString(R.string.empty_pwd_war));
            return false;
        } else if (!IS_FB_SIGNUP && etPasswordRegister.getText().length() < 6) {
            appUtils.showSnackBar(activityRegister, getString(R.string.pwd_length_war));
            return false;
        } else if (TextUtils.isEmpty(etPhNoRegister.getText().toString())) {
            appUtils.showSnackBar(activityRegister, getString(R.string.empty_phno_war));
            return false;
        } else if (etPhNoRegister.getText().toString().trim().length() < 7) {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.mob_no_7_digit));
            return false;
        } else if (TextUtils.isEmpty(etNameRegister.getText().toString().trim())) {
            appUtils.showSnackBar(activityRegister, getString(R.string.empty_name_war));
            return false;
        } else
            return true;
    }


    /**
     * method to hit api for registering user
     */
    private void hitApiForRegister(HashMap<String, String> hashMap) {
        appUtils.showProgressDialog(mActivity,false);
        ApiInterface service = RestApi.createService(ApiInterface.class);
        Call<ResponseBody> call = service.signup(Constants.API_KEY, appUtils.encryptData(hashMap));

        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                SignupModel signupModel = new Gson().fromJson(response, SignupModel.class);
                if (signupModel.getCODE() == 200) {
                    AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.USER_NAME, signupModel.getDATA().getName());
                    AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.USER_EMAIL, signupModel.getDATA().getEmail());
                    AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.USER_MOBILE, signupModel.getDATA().getMobileNumber());
                    AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.USER_TYPE, String.valueOf(signupModel.getDATA().getUserType()));
                    AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, signupModel.getDATA().getAccessToken());
                    AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.USER_ID, String.valueOf(signupModel.getDATA().getUserId()));


                    AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.ADDRESS, signupModel.getDATA().getAddress());
                    AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.COUNTRY_NAME, signupModel.getDATA().getCountryName());
                    AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.STATE_NAME, signupModel.getDATA().getStateName());
                    AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.CITY_NAME, signupModel.getDATA().getCityName());
                    AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.COUNTRY_ID, signupModel.getDATA().getCountryId());
                    AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.STATE_ID, signupModel.getDATA().getStateId());
                    AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.CITY_ID, signupModel.getDATA().getCityId());
                    AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.SOCIAL_TYPE, signupModel.getDATA().getSignupType());
                    AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.POSTAL_CODE, signupModel.getDATA().getPostalCode());
                    AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.ISO_CODE, signupModel.getDATA().getIso3Code());

                    AppSharedPrefs.getInstance(mActivity).putBoolean(AppSharedPrefs.PREF_KEY.IS_LOGIN, true);

                    /*Intent intent = new Intent(mActivity, HomeActivity.class);
                    startActivity(intent);*/
                    finish();
                } else {
                    appUtils.showSnackBar(activityRegister, signupModel.getMESSAGE());
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (64206 == requestCode)
            mFBSignInAI.setActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void fbSignInSuccessResult(JSONObject jsonObject) {
        try {
            String name = jsonObject.getString("name");
            String email = "";
            social_id = jsonObject.getString("id");
            if (jsonObject.has("picture")) {
                String userProfilePicUrl = "https://graph.facebook.com/" + jsonObject.getString("id") + "/picture?width=2000";
            }
            if (jsonObject.has("email")) {
                email = jsonObject.getString("email");
                HashMap<String, String> socialParams = new HashMap<>();
                socialParams.put(ApiKeys.SIGNUP_TYPE, "facebook");
                socialParams.put(ApiKeys.NAME, name);
                socialParams.put(ApiKeys.EMAIL, email);
                socialParams.put(ApiKeys.SOCIAL_ID, social_id);

                socialParams.put(ApiKeys.DEVICE_TYPE, Constants.ANDROID_TYPE);
                socialParams.put(ApiKeys.DEVICE_TOKEN, FirebaseInstanceId.getInstance().getToken());

                if (appUtils.isInternetOn(mActivity)) {
                    hitApiForRegister(socialParams);
                } else {
                    appUtils.showSnackBar(activityRegister, getString(R.string.internet_offline));
                }

            } else {
                IS_FB_SIGNUP = true;
                llPwd.setVisibility(View.GONE);
                etNameRegister.setText(name);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fbSignOutSuccessResult() {

    }

    @Override
    public void fbSignInFailure(FacebookException exception) {
        appUtils.showSnackBar(activityRegister, getString(R.string.fb_failure_msg));
    }

    @Override
    public void fbSignInCancel() {
        appUtils.showSnackBar(activityRegister, getString(R.string.fb_signin_canceled));
    }

    @OnClick(R.id.tv_old_toggle)
    public void onViewClicked() {
    }
}
