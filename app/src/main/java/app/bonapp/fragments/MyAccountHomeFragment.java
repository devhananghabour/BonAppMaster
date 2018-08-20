package app.bonapp.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.app.bonapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import app.bonapp.activities.AddressListActivity;
import app.bonapp.activities.EditMyAccountActivity;
import app.bonapp.activities.HomeActivity;
import app.bonapp.activities.MerchantActivity;
import app.bonapp.activities.OrderHistoryActivity;
import app.bonapp.activities.PaymentDetailsActivity;
import app.bonapp.constants.ApiKeys;
import app.bonapp.constants.Constants;
import app.bonapp.interfaces.DialogButtonClickListener;
import app.bonapp.network.ApiCall;
import app.bonapp.network.ApiInterface;
import app.bonapp.network.NetworkListener;
import app.bonapp.network.RestApi;
import app.bonapp.utils.AppSharedPrefs;
import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountHomeFragment extends BaseFragment {

    @BindView(R.id.logged_in_email)
    TextView loggedInEmail;


    @Override
    protected int getResourceLayoutId() {
        return R.layout.fragment_my_account_home;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loggedInEmail.setText(getString(R.string.logged_in_as) + " " + AppSharedPrefs.getInstance(getActivity()).getString(AppSharedPrefs.PREF_KEY.USER_EMAIL, ""));
    }


    @OnClick({R.id.tv_for_merchants, R.id.tv_my_orders, R.id.tv_my_details, R.id.tv_my_addresses, R.id.tv_payment_details, R.id.tv_about_app, R.id.tv_logout})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.tv_for_merchants:
                if (AppSharedPrefs.getInstance(getActivity()).getString(AppSharedPrefs.PREF_KEY.USER_TYPE, Constants.CUSTOMER_TYPE).equals(Constants.MERCHANT_TYPE)) {
                    //user is merchant type
                    intent = new Intent(getActivity(), MerchantActivity.class);
                    startActivity(intent);
                } else {
                    //user is customer type
                    showDontAccessDialog();
                }
                break;
            case R.id.tv_my_orders:
                intent = new Intent(getActivity(), OrderHistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_my_details:
                EditMyAccountActivity.openEditAccount(getContext());
                break;
            case R.id.tv_my_addresses:
                startActivity(new Intent(getActivity(), AddressListActivity.class));
                break;
            case R.id.tv_payment_details:
                intent = new Intent(getActivity(), PaymentDetailsActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_about_app:
                replaceFragmentInActivity(new AboutBonAppFragment());
                break;
            case R.id.tv_logout:
                showLogoutAlertDialog();
                break;
            default:
                break;
        }
    }

    /**
     * show alert for logout
     */
    private void showLogoutAlertDialog() {
        appUtils.showAlertDialog(getActivity(), "", getString(R.string.txt_logout_laert), getString(R.string.txt_ok), getString(R.string.txt_cancel), new DialogButtonClickListener() {
            @Override
            public void positiveButtonClick() {
                if (appUtils.isInternetOn(getActivity())) {
                    hitApiForLogout();
                } else {
                    appUtils.showToast(getActivity(), getString(R.string.internet_offline));
                }
            }

            @Override
            public void negativeButtonClick() {
            }
        });
    }


    /**
     * method to show not access dialog
     */
    private void showDontAccessDialog() {
        appUtils.showAlertDialog(getActivity(), getString(R.string.you_dont_have_acess),
                getString(R.string.you_need_to_be), getString(R.string.email), getString(R.string.cancel),
                new DialogButtonClickListener() {
                    @Override
                    public void positiveButtonClick() {
                        appUtils.sendEmail(getActivity(), Constants.BONAPP_SUPPORT_EMAIL, getString(R.string.partnership_mail_subject), "");
                    }

                    @Override
                    public void negativeButtonClick() {

                    }
                });
    }

    /**
     * hit api for logout
     */
    private void hitApiForLogout() {
        appUtils.showProgressDialog(getActivity(), false);
        ApiInterface service = RestApi.createService(ApiInterface.class);
        Call<ResponseBody> call = service.logout(AppSharedPrefs.getInstance(getActivity()).getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, ""));
        ApiCall.getInstance().hitService(getActivity(), call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                try {
                    JSONObject mainobject = new JSONObject(response);
                    if (mainobject.getInt("CODE") == 200 && getActivity() != null) {
                        AppSharedPrefs.getInstance(getActivity()).clearPrefs(getActivity());
                        AppSharedPrefs.getInstance(getActivity()).putBoolean(AppSharedPrefs.PREF_KEY.IS_FIRST_TIME, false);
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        getActivity().startActivity(intent);
                    } else if (mainobject.getInt("CODE") == ApiKeys.UNAUTHORISED_CODE) {
                        appUtils.logoutFromApp(getActivity(), mainobject.optString("MESSAGE"));
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
