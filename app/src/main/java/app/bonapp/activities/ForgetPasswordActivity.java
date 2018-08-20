package app.bonapp.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.bonapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import app.bonapp.constants.ApiKeys;
import app.bonapp.constants.Constants;
import app.bonapp.interfaces.DialogButtonClickListener;
import app.bonapp.network.ApiCall;
import app.bonapp.network.ApiInterface;
import app.bonapp.network.NetworkListener;
import app.bonapp.network.RestApi;
import app.bonapp.utils.AppUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ForgetPasswordActivity extends AppCompatActivity {

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
    @BindView(R.id.rl_toolbar)
    RelativeLayout rlToolbar;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.activity_forgot_pwd)
    LinearLayout activityForgotPwd;
    private AppUtils appUtils;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pwd);
        ButterKnife.bind(this);
        mActivity = ForgetPasswordActivity.this;
        appUtils = AppUtils.getInstance();
        setUpViews();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    private void setUpViews() {
        tvTitle.setText(R.string.forget_pwdf);
        ivBack.setVisibility(View.VISIBLE);
        tvEnd.setVisibility(View.VISIBLE);
        tvEnd.setText(getString(R.string.txt_send));

    }

    @OnClick({R.id.iv_back, R.id.tv_end})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_end:
                appUtils.hideNativeKeyboard(mActivity);
                if (appUtils.isInternetOn(mActivity)) {
                    if (TextUtils.isEmpty(etEmail.getText().toString().trim())) {
                        appUtils.showSnackBar(activityForgotPwd, getString(R.string.empty_email));
                    } else if (!appUtils.isValidEmail(etEmail.getText().toString().trim())) {
                        appUtils.showSnackBar(activityForgotPwd, getString(R.string.valid_email_war));
                    } else {
                        hitForgetPasswordApi();
                    }
                }
                else {
                    appUtils.showSnackBar(activityForgotPwd, getString(R.string.internet_offline));
                }
                break;
        }
    }

    /**
     * hit api for forget password
     */
    private void hitForgetPasswordApi() {
        appUtils.showProgressDialog(mActivity,false);

        HashMap<String, String> params = new HashMap<>();
        params.put(ApiKeys.EMAIL, etEmail.getText().toString().trim());
        ApiInterface service = RestApi.createService(ApiInterface.class);
        Call<ResponseBody> call = service.forgetPassword(Constants.API_KEY, appUtils.encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                try {
                    JSONObject mainObject = new JSONObject(response);
                    if (mainObject.getInt(ApiKeys.CODE) == 200) {
                        appUtils.showAlertDialog(mActivity, getString(R.string.forgot_pwd_alert_title), getString(R.string.forgot_pwd_message),
                                getString(R.string.okay), "",new DialogButtonClickListener() {
                            @Override
                            public void positiveButtonClick() {
                                finish();
                            }

                            @Override
                            public void negativeButtonClick() {

                            }
                        });
                    } else {
                        appUtils.showSnackBar(activityForgotPwd,mainObject.optString(ApiKeys.MESSAGE));
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
