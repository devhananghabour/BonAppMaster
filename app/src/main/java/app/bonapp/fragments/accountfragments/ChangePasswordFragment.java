package app.bonapp.fragments.accountfragments;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.bonapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import app.bonapp.constants.ApiKeys;
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

/**
 * this class contains the logic of changing password.
 */

public class ChangePasswordFragment extends Fragment {

    @BindView(R.id.et_old_pwd)
    EditText etOldPwd;
    @BindView(R.id.et_new_pwd)
    EditText etNewPwd;
    @BindView(R.id.et_confirm_pwd)
    EditText etConfirmPwd;
    @BindView(R.id.ll_change_pwd)
    LinearLayout llChangePwd;
    @BindView(R.id.tv_old_toggle)
    TextView tvOldToggle;
    @BindView(R.id.tv_new_toggle)
    TextView tvNewToggle;
    @BindView(R.id.tv_confirm_toggle)
    TextView tvConfirmToggle;

    private AppUtils appUtils;
    private Activity mActivity;
    private boolean isOldVisible=false,isNewVisible=false,isConfirmVisible=false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        ButterKnife.bind(this, view);
        mActivity = getActivity();
        appUtils = AppUtils.getInstance();
        showToggleOnInput(etOldPwd,tvOldToggle);
        showToggleOnInput(etNewPwd,tvNewToggle);
        showToggleOnInput(etConfirmPwd,tvConfirmToggle);
        return view;
    }

    /**
     * show password toggle on inputing password
     * @param editText
     * @param textView
     */
    private void showToggleOnInput(final EditText editText, final TextView textView){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText.getText().length()==0){
                    textView.setVisibility(View.GONE);
                    editText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_register_greypassword,0,0,0);
                    TransitionManager.beginDelayedTransition(llChangePwd);
                }else {
                    textView.setVisibility(View.VISIBLE);
                    editText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_lock,0,0,0);
                    TransitionManager.beginDelayedTransition(llChangePwd);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    /**
     * method to hit api for change pwd
     */
    public void hitApiForChangePwd() {
        appUtils.hideNativeKeyboard(mActivity);
        if (checkValidations()) {
            if (appUtils.isInternetOn(mActivity))
                hitChangePwdApi();
            else {
                appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.internet_offline));
            }
        }
    }

    /**
     * method to check validations
     *
     * @return
     */
    private boolean checkValidations() {
        String oldPwd = etOldPwd.getText().toString().trim();
        String newPwd = etNewPwd.getText().toString().trim();
        String confirmPwd = etConfirmPwd.getText().toString().trim();

        if (oldPwd.length() == 0) {
            appUtils.showSnackBar(llChangePwd, getString(R.string.empty_old_pwd_war));

        } else if (newPwd.length() == 0) {
            appUtils.showSnackBar(llChangePwd, getString(R.string.empty_new_pwd_war));

        } else if (newPwd.length() < 6) {
            appUtils.showSnackBar(llChangePwd, getString(R.string.pwd_length_war));

        } else if (confirmPwd.length() == 0) {
            appUtils.showSnackBar(llChangePwd, getString(R.string.empty_confirm_pwd_war));
        } else if (!newPwd.equals(confirmPwd)) {
            appUtils.showSnackBar(llChangePwd, getString(R.string.pwd_mismatch_war));

        } else
            return true;

        return false;
    }

    /**
     * method to change pwd api
     */
    private void hitChangePwdApi() {
        appUtils.showProgressDialog(mActivity,false);
        HashMap<String, String> params = new HashMap<>();
        params.put("old_password", etOldPwd.getText().toString().trim());
        params.put("new_password", etNewPwd.getText().toString().trim());

        ApiInterface service = RestApi.createService(ApiInterface.class);
        Call<ResponseBody> call = service.changePassword(AppSharedPrefs.getInstance(mActivity).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, ""), appUtils.encryptData(params));        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                try {
                    JSONObject mainObject = new JSONObject(response);
                    if (mainObject.getInt("CODE") == 200) {
                        etNewPwd.setText("");
                        etOldPwd.setText("");
                        etConfirmPwd.setText("");
                        showPwdChangedSuccessDialog();


                    } else if (mainObject.getInt("CODE") == ApiKeys.UNAUTHORISED_CODE) {
                        appUtils.logoutFromApp(mActivity, mainObject.getString("MESSAGE"));
                    } else {
                        appUtils.showToast(mActivity, mainObject.getString("MESSAGE"));
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


    /**
     * method to show dialog of successful payment
     * @param
     */
    private void showPwdChangedSuccessDialog(){
        final Dialog dialog=new Dialog(mActivity);
        if (dialog.getWindow()!=null)
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_success_payment);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.DIM_AMOUNT_CHANGED);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.9f;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvTitle=(TextView)dialog.findViewById(R.id.tv_title);
        tvTitle.setText(R.string.congrats);
        tvTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_register_congrats,0,0);

        ((TextView)dialog.findViewById(R.id.tv_message)).setText(R.string.pwd_changed_msg);
        if (dialog.getWindow()!=null)
            dialog.getWindow().getAttributes().windowAnimations= R.style.DialogAnimationZoom;
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                mActivity.onBackPressed();

            }
        },2000);
    }


    @OnClick(R.id.ll_change_pwd)
    public void onClick() {
        appUtils.hideNativeKeyboard(mActivity);
    }

    @OnClick({R.id.tv_old_toggle, R.id.tv_new_toggle, R.id.tv_confirm_toggle})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_old_toggle:
                if (isOldVisible){
                    isOldVisible=false;
                    etOldPwd.setTransformationMethod(new PasswordTransformationMethod());
                    tvOldToggle.setText(getString(R.string.show));
                }else {
                    isOldVisible=true;
                    etOldPwd.setTransformationMethod(null);
                    tvOldToggle.setText(getString(R.string.hide));
                }
                etOldPwd.setSelection(etOldPwd.length());
                break;
            case R.id.tv_new_toggle:
                if (isNewVisible){
                    isNewVisible=false;
                    etNewPwd.setTransformationMethod(new PasswordTransformationMethod());
                    tvNewToggle.setText(getString(R.string.show));
                }else {
                    isNewVisible=true;
                    etNewPwd.setTransformationMethod(null);
                    tvNewToggle.setText(getString(R.string.hide));
                }
                etNewPwd.setSelection(etNewPwd.length());
                break;
            case R.id.tv_confirm_toggle:
                if (isConfirmVisible){
                    isConfirmVisible=false;
                    etConfirmPwd.setTransformationMethod(new PasswordTransformationMethod());
                    tvConfirmToggle.setText(getString(R.string.show));
                }else {
                    isConfirmVisible=true;
                    etConfirmPwd.setTransformationMethod(null);
                    tvConfirmToggle.setText(getString(R.string.hide));
                }
                etConfirmPwd.setSelection(etConfirmPwd.length());
                break;
        }
    }
}
