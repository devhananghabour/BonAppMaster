package app.bonapp.fragments.accountfragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.app.bonapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import app.bonapp.constants.ApiKeys;
import app.bonapp.interfaces.DialogButtonClickListener;
import app.bonapp.network.ApiCall;
import app.bonapp.network.ApiInterface;
import app.bonapp.network.NetworkListener;
import app.bonapp.network.RestApi;
import app.bonapp.utils.AppSharedPrefs;
import app.bonapp.utils.AppUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;


public class ChangeEmailFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    @BindView(R.id.et_email)
    EditText etEmail;
    private boolean isEmailChange;
    private Activity mActivity;
    private AppUtils appUtils;

    public ChangeEmailFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_email, container, false);
        ButterKnife.bind(this, view);
        mActivity=getActivity();
        appUtils=AppUtils.getInstance();
            etEmail.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_,0,0,0);
            etEmail.setHint(R.string.e_mail_address);
            etEmail.setText(AppSharedPrefs.getInstance(mActivity).getString(AppSharedPrefs.PREF_KEY.USER_EMAIL,""));

        return view;
    }


    /**
     * method to show alert for changing email of the user
     */
    public void showEmailChangeAlert(){
        appUtils.hideNativeKeyboard(mActivity);
        if (appUtils.isInternetOn(mActivity)) {
            if (TextUtils.isEmpty(etEmail.getText().toString().trim())) {
                appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.empty_email));
            } else if (!appUtils.isValidEmail(etEmail.getText().toString().trim())) {
                appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.valid_email_war));
            } else {
                appUtils.showAlertDialog(mActivity, getString(R.string.alert), getString(R.string.a_confirmation_mail_has),
                        getString(R.string.log_out), getString(R.string.txt_cancel), new DialogButtonClickListener() {
                            @Override
                            public void positiveButtonClick() {
                                hitChangeEmailApi();
                            }

                            @Override
                            public void negativeButtonClick() {

                            }
                        });
            }
        }
        else {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.internet_offline));
        }

    }


    /**
     * method to change pwd api
     */
    private void hitChangeEmailApi() {
        appUtils.showProgressDialog(mActivity,false);
        HashMap<String, String> params = new HashMap<>();
        params.put(ApiKeys.EMAIL, etEmail.getText().toString());

        ApiInterface service = RestApi.createService(ApiInterface.class);
        Call<ResponseBody> call = service.changeEmail(AppSharedPrefs.getInstance(mActivity).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, ""), appUtils.encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                try {
                    JSONObject mainObject = new JSONObject(response);
                    if (mainObject.getInt("CODE") == 200) {
                        appUtils.logoutFromApp(mActivity, "");

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

}
