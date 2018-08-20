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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
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
import app.bonapp.models.loginmodel.LoginModel;
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

    public class LoginActivity extends AppCompatActivity implements FBSignCallback {

    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_email_login)
    EditText etEmailRegister;
    @BindView(R.id.et_password_login)
    EditText etPasswordRegister;
    @BindView(R.id.tv_forgot_pwd)
    TextView tvForgotPwd;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_register_new_acnt)
    TextView tvRegisterNewAcnt;
    @BindView(R.id.activity_login)
    RelativeLayout activityLogin;
    @BindView(R.id.tv_old_toggle)
    TextView tvOldToggle;
    private AppUtils appUtils;
    private Activity mActivity;
    private FBSignInAI mFBSignInAI;
    private boolean isFromCheckout = false;
    private boolean isPwdVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mActivity = LoginActivity.this;
        appUtils = AppUtils.getInstance();
        mFBSignInAI = FBSignInAI.getInstance(mActivity, this);
        setUpViews();
        if (getIntent().getExtras() != null) {
            isFromCheckout = getIntent().getBooleanExtra(Constants.FROM_CHECKOUT_KEY, false);
        }
        showToggleOnInput(etPasswordRegister,tvOldToggle );
    }

    private void setUpViews() {
        tvTitle.setText(getString(R.string.login));
        tvCancel.setVisibility(View.VISIBLE);
        tvForgotPwd.setPaintFlags(tvForgotPwd.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvRegisterNewAcnt.setPaintFlags(tvRegisterNewAcnt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        etPasswordRegister.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnLogin.performClick();
                    return true;
                }
                return false;
            }
        });

        appUtils.highlightIcons(etEmailRegister, R.drawable.ic_register_greymail, R.drawable.ic_mail);
        appUtils.highlightIcons(etPasswordRegister, R.drawable.ic_register_greypassword, R.drawable.ic_lock);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @OnClick({R.id.tv_cancel, R.id.tv_forgot_pwd, R.id.btn_login, R.id.rl_login_fb, R.id.tv_register_new_acnt, R.id.activity_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                onBackPressed();
                break;
            case R.id.tv_forgot_pwd:
                Intent intent = new Intent(mActivity, ForgetPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_login:
                appUtils.hideNativeKeyboard(mActivity);
                if (checkValidations()) {
                    if (appUtils.isInternetOn(mActivity)) {
                        HashMap<String, String> params = new HashMap<>();
                        params.put(ApiKeys.EMAIL, etEmailRegister.getText().toString().trim());
                        params.put(ApiKeys.PASSWROD, etPasswordRegister.getText().toString().trim());
                        params.put(ApiKeys.DEVICE_TYPE, Constants.ANDROID_TYPE);
                        params.put(ApiKeys.DEVICE_TOKEN, FirebaseInstanceId.getInstance().getToken());

                        hitApiForLogin(params, false);
                    } else {
                        appUtils.showSnackBar(activityLogin, getString(R.string.internet_offline));
                    }
                }
                break;
            case R.id.rl_login_fb:
                mFBSignInAI.doSignOut();
                mFBSignInAI.doSignIn();
                //appUtils.showSnackBar(activityLogin,"Under Development!");
                break;
            case R.id.tv_register_new_acnt:
                Intent intent1 = new Intent(mActivity, RegisterActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.activity_login:
                appUtils.hideNativeKeyboard(mActivity);
                break;
        }
    }

    /**
     * method to check validations
     *
     * @return
     */
    private boolean checkValidations() {
        if (TextUtils.isEmpty(etEmailRegister.getText().toString().trim())) {
            appUtils.showSnackBar(activityLogin, getString(R.string.empty_email));
            return false;
        } else if (!appUtils.isValidEmail(etEmailRegister.getText().toString().trim())) {
            appUtils.showSnackBar(activityLogin, getString(R.string.valid_email_war));
            return false;
        } else if (TextUtils.isEmpty(etPasswordRegister.getText().toString().trim())) {
            appUtils.showSnackBar(activityLogin, getString(R.string.empty_pwd_war));
            return false;
        } else
            return true;
    }

    /**
     * hit api for login
     */
    private void hitApiForLogin(HashMap<String, String> hashMap, boolean isSocial) {
        appUtils.showProgressDialog(mActivity,false);

        ApiInterface service = RestApi.createService(ApiInterface.class);
        Call<ResponseBody> call;
        if (isSocial) {
            call = service.social_login(Constants.API_KEY, appUtils.encryptData(hashMap));
        } else {
            call = service.login(Constants.API_KEY, appUtils.encryptData(hashMap));
        }

        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                if (!mActivity.isFinishing()) {
                    LoginModel loginModel = new Gson().fromJson(response, LoginModel.class);
                    if (loginModel.getCODE() == 200) {
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.USER_NAME, loginModel.getDATA().getName());
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.USER_TYPE, loginModel.getDATA().getUserType());
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, loginModel.getDATA().getAccessToken());
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.USER_ID, loginModel.getDATA().getUserId());
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.USER_EMAIL, loginModel.getDATA().getEmail());
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.USER_MOBILE, loginModel.getDATA().getMobileNumber());

                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.ADDRESS, loginModel.getDATA().getAddress());
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.COUNTRY_NAME, loginModel.getDATA().getCountryName());
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.STATE_NAME, loginModel.getDATA().getStateName());
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.CITY_NAME, loginModel.getDATA().getCityName());
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.COUNTRY_ID, loginModel.getDATA().getCountryId());
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.ISO_CODE, loginModel.getDATA().getIso3Code());
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.STATE_ID, loginModel.getDATA().getStateId());
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.CITY_ID, loginModel.getDATA().getCityId());
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.SOCIAL_TYPE, loginModel.getDATA().getSocialType());
                        AppSharedPrefs.getInstance(mActivity).putString(AppSharedPrefs.PREF_KEY.POSTAL_CODE, loginModel.getDATA().getPostalCode());

                        AppSharedPrefs.getInstance(mActivity).putBoolean(AppSharedPrefs.PREF_KEY.IS_LOGIN, true);

                        if (isFromCheckout) {
                            finish();
                        } else {
                            if (AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.USER_TYPE,"1")
                                    .equals(Constants.MERCHANT_TYPE)){
                            Intent intent = new Intent(mActivity, MyAccountActivity.class);
                            startActivity(intent);
                            }
                            finish();
                        }
                    } else {
                        appUtils.showSnackBar(activityLogin, loginModel.getmESSAGE());
                    }
                }
            }

            @Override
            public void onSuccessErrorBody(String response) {

            }

            @Override
            public void onFailure() {
                appUtils.showSnackBar(activityLogin, getString(R.string.txt_something_went_wrong));
            }
        });
    }

    @Override
    public void fbSignInSuccessResult(JSONObject jsonObject) {
        if (!mActivity.isFinishing()) {
            try {
                String name = jsonObject.getString("name");
                String email = "";
                String social_id = jsonObject.getString("id");
                if (jsonObject.has("picture")) {
                    String userProfilePicUrl = "https://graph.facebook.com/" + jsonObject.getString("id") + "/picture?width=2000";
                }
                if (jsonObject.has("email")) {
                    email = jsonObject.getString("email");
                }
                HashMap<String, String> socialParams = new HashMap<>();
                //socialParams.put(ApiKeys.EMAIL, email);
                socialParams.put(ApiKeys.SOCIAL_ID, social_id);
                socialParams.put(ApiKeys.DEVICE_TYPE, Constants.ANDROID_TYPE);
                socialParams.put(ApiKeys.DEVICE_TOKEN, FirebaseInstanceId.getInstance().getToken());
                socialParams.put(ApiKeys.SOCIAL_TYPE, "facebook");

                if (appUtils.isInternetOn(mActivity)) {
                    hitApiForLogin(socialParams, true);
                } else {
                    appUtils.showSnackBar(activityLogin, getString(R.string.internet_offline));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void fbSignOutSuccessResult() {

    }

    @Override
    public void fbSignInFailure(FacebookException exception) {
        appUtils.showSnackBar(activityLogin, getString(R.string.fb_failure_msg));
    }

    @Override
    public void fbSignInCancel() {
        appUtils.showSnackBar(activityLogin, getString(R.string.fb_signin_canceled));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (64206 == requestCode)
            mFBSignInAI.setActivityResult(requestCode, resultCode, data);
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
                    TransitionManager.beginDelayedTransition(activityLogin);
                } else {
                    textView.setVisibility(View.VISIBLE);
                    editText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_lock, 0, 0, 0);
                    TransitionManager.beginDelayedTransition(activityLogin);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @OnClick(R.id.tv_old_toggle)
    public void onViewClicked() {
        if (isPwdVisible){
            isPwdVisible=false;
            etPasswordRegister.setTransformationMethod(new PasswordTransformationMethod());
            tvOldToggle.setText(getString(R.string.show));
        }else {
            isPwdVisible=true;
            etPasswordRegister.setTransformationMethod(null);
            tvOldToggle.setText(getString(R.string.hide));
        }
        etPasswordRegister.setSelection(etPasswordRegister.length());
    }
}
