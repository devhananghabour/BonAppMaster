package app.bonapp.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.bonapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import app.bonapp.constants.ApiKeys;
import app.bonapp.constants.Constants;
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
 * This class is used for taking user card's last four digit and an unique name to recognize the card.
 */

public class AddCardActivity extends AppCompatActivity {

    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_end)
    TextView tvEnd;
    @BindView(R.id.et_card_no)
    EditText etCardNo;
    @BindView(R.id.et_card_name)
    EditText etCardName;
    @BindView(R.id.activity_add_card)
    LinearLayout activityAddCard;
    @BindView(R.id.iv_back)
    ImageView ivBack;

    private AppUtils appUtils;
    private Activity mActivity;
    private Intent paytabsIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        ButterKnife.bind(this);
        setUpViews();
        mActivity = AddCardActivity.this;
        appUtils = AppUtils.getInstance();
        if (getIntent().getExtras() != null) {
            paytabsIntent = getIntent().getParcelableExtra("paytabs_intent");
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    /**
     * method to set up views for this activity
     */
    private void setUpViews() {
        tvCancel.setVisibility(View.VISIBLE);
        tvTitle.setText(R.string.add_card_title);
        tvEnd.setVisibility(View.VISIBLE);
        tvEnd.setText(R.string.save);

    }

    @OnClick({R.id.tv_cancel, R.id.tv_end, R.id.activity_add_card})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                onBackPressed();
                break;
            case R.id.tv_end:
                appUtils.hideNativeKeyboard(mActivity);
                if (checkValidations()) {
                    if (appUtils.isInternetOn(mActivity)) {
                        hitApiForValidatingName(etCardName.getText().toString().trim());
                    } else {
                        appUtils.showSnackBar(activityAddCard, getString(R.string.internet_offline));
                    }

                }
                break;
            case R.id.activity_add_card:
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
        if (TextUtils.isEmpty(etCardNo.getText().toString().trim())) {
            appUtils.showSnackBar(activityAddCard, getString(R.string.empty_card_no_war));
            return false;
        } else if (TextUtils.isEmpty(etCardName.getText().toString().trim())) {
            appUtils.showSnackBar(activityAddCard, getString(R.string.empty_unique_name_war));
            return false;
        } else
            return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ADD_CARD_REQ_CODE) {
            Intent intent = new Intent();
            intent.putExtra(Constants.CARD_NAME_KEY, etCardName.getText().toString());
            intent.putExtra(Constants.CARD_NO_KEY, etCardNo.getText().toString());
            setResult(Constants.ADD_CARD_REQ_CODE, intent);
            finish();
        }
    }

    /**
     * method to hit api for validating unique name
     * @param name
     */
    private void hitApiForValidatingName(String name) {
        appUtils.showProgressDialog(mActivity, true);
        ApiInterface service = RestApi.createService(ApiInterface.class);
        HashMap<String, String> params = new HashMap<>();
        params.put(ApiKeys.CARD_NAME_KEY, name);

        Call<ResponseBody> call = service.validateCardName(AppSharedPrefs.getInstance(mActivity).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN, ""), appUtils.encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                try {
                    JSONObject mainObject = new JSONObject(response);
                    if (mainObject.getInt(ApiKeys.CODE) == 468) {
                        appUtils.showAlertDialog(mActivity, "", getString(R.string.name_already_used), getString(R.string.ok), "", null);
                        //appUtils.showSnackBar(activityAddCard,getString(R.string.name_already_used));
                    } else if (mainObject.getInt(ApiKeys.CODE) == 467) {
                        startActivityForResult(paytabsIntent, Constants.ADD_CARD_REQ_CODE);
                        findTopActivity();
                    } else {
                        appUtils.showSnackBar(activityAddCard, mainObject.optString(ApiKeys.MESSAGE));
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

    private void findTopActivity() {
        ActivityManager m = (ActivityManager) mActivity.getSystemService(mActivity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfoList = m.getRunningTasks(10);
        Iterator<ActivityManager.RunningTaskInfo> itr = runningTaskInfoList.iterator();
        while (itr.hasNext()) {
            ActivityManager.RunningTaskInfo runningTaskInfo = (ActivityManager.RunningTaskInfo) itr.next();
            int id = runningTaskInfo.id;
            CharSequence desc = runningTaskInfo.description;
            int numOfActivities = runningTaskInfo.numActivities;
            String topActivity = runningTaskInfo.topActivity.getShortClassName();

        }
    }
}
