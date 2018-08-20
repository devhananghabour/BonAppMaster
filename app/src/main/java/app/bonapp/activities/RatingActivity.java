package app.bonapp.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * From here user can rate the app if the rating is 4 or below then a feedback form will be shown otherwise
 * user will be asked to rate on play store.
 */

public class RatingActivity extends AppCompatActivity {

    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tap_star)
    TextView tapStar;
    @BindView(R.id.rating_bar)
    AppCompatRatingBar ratingBar;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_targeted_logo)
    ImageView ivTargetedLogo;
    @BindView(R.id.iv_targeted_rating)
    RatingBar targetedRating;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.et_review)
    EditText etReview;
    @BindView(R.id.ll_review)
    LinearLayout llReview;
    @BindView(R.id.activity_rating)
    RelativeLayout activityRating;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private Activity mActivity;
    private AppUtils appUtils;
    private double appRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        ButterKnife.bind(this);
        mActivity = RatingActivity.this;
        appUtils = AppUtils.getInstance();

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    appRating=rating;
                    if (rating <= 4) {
                        TransitionManager.beginDelayedTransition(activityRating);
                        scaleAnimation(ivLogo, ivTargetedLogo);
                        scaleAnimation(ratingBar, targetedRating);
                    } else {
                        if (appUtils.isInternetOn(mActivity)) {
                            hitApiForFeedback();
                        }else {
                            appUtils.showSnackBar(activityRating,getString(R.string.internet_offline));
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    /**
     * method for launching play store for rating
     */
    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + mActivity.getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            mActivity.startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            appUtils.showSnackBar(mActivity.findViewById(android.R.id.content), getString(R.string.txt_unable_to_find_app));
        }
    }

    /**
     * this method applies scaling + translating from one view to another view
     *
     * @param startView
     * @param finishView
     */
    private void scaleAnimation(final View startView, final View finishView) {
        int startViewLocation[] = new int[2];
        startView.getLocationInWindow(startViewLocation);
        int finishViewLocation[] = new int[2];
        finishView.getLocationInWindow(finishViewLocation);
        int startX = startViewLocation[0] + startView.getWidth() / 2;
        int startY = startViewLocation[1] + startView.getHeight() / 2;
        int endX = finishViewLocation[0] + finishView.getWidth() / 2;
        int endY = finishViewLocation[1] + finishView.getHeight() / 2;


        ScaleAnimation animation = new ScaleAnimation(1f, 0.3f, 1, 0.3f,
                Animation.ABSOLUTE, (endX - startX + startView.getWidth() / 2) - 100,
                Animation.ABSOLUTE, (endY - startY + startView.getHeight() / 2) - 110);
        // animation.scaleCurrentDuration(6000);

        animation.setDuration(1000);
        animation.setFillAfter(true);
        // animation.setStartOffset(50);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                startView.setVisibility(View.VISIBLE);
                tapStar.setVisibility(View.GONE);
                tvTitle.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //finishView.setVisibility(View.VISIBLE);
                //startView.setVisibility(View.GONE);
                ratingBar.setIsIndicator(true);

                llReview.setVisibility(View.VISIBLE);
                tvSubmit.setVisibility(View.VISIBLE);

            }
        });
        startView.startAnimation(animation);
    }


    private boolean checkValidations() {
        if (appRating<5 && TextUtils.isEmpty(etTitle.getText().toString().trim())) {
            appUtils.showSnackBar(activityRating, getString(R.string.empty_review_war));
            return false;
        } else
            return true;
    }

    @OnClick({R.id.iv_back, R.id.tv_submit, R.id.activity_rating})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_submit:
                appUtils.hideNativeKeyboard(mActivity);
                if (checkValidations()) {
                    //hit api for feedback
                    hitApiForFeedback();
                }
                break;
            case R.id.activity_rating:
                appUtils.hideNativeKeyboard(mActivity);
                break;
        }
    }


    /**
     * method to hit api for submiting feedback
     */
    private void hitApiForFeedback(){
        appUtils.showProgressDialog(mActivity,false);

        ApiInterface service= RestApi.createService(ApiInterface.class);
        HashMap<String,String> params=new HashMap<>();
        params.put(ApiKeys.RATING_KEY, String.valueOf(appRating));
        if (appRating<5) {
            params.put(ApiKeys.TITLE_KEY, etTitle.getText().toString().trim());
            params.put(ApiKeys.REVIEW_KEY, etReview.getText().toString().trim());
        }
        Call<ResponseBody> call=service.feedback(AppSharedPrefs.getInstance(mActivity).
                getString(AppSharedPrefs.PREF_KEY.ACCESS_TOKEN,""),appUtils.encryptData(params));
        ApiCall.getInstance().hitService(mActivity, call, new NetworkListener() {
            @Override
            public void onSuccess(int responseCode, String response) {
                try {
                    JSONObject mainObject=new JSONObject(response);
                    if (mainObject.getInt(ApiKeys.CODE)==200){
                        if (appRating==5){
                            AppUtils.getInstance().showAlertDialog(mActivity, getString(R.string.thank_you),
                                    getString(R.string.playstore_alert_message), getString(R.string.rate),
                                    getString(R.string.cancel), new DialogButtonClickListener() {
                                        @Override
                                        public void positiveButtonClick() {
                                            launchMarket();
                                        }

                                        @Override
                                        public void negativeButtonClick() {

                                        }
                                    });
                        }else {
                            showReviewSuccessDialog();

                        }

                    }else {
                        appUtils.showSnackBar(activityRating,mainObject.optString(ApiKeys.MESSAGE));
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
    private void showReviewSuccessDialog(){
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
        tvTitle.setText(R.string.thank_you);
        tvTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_register_congrats,0,0);

        ((TextView)dialog.findViewById(R.id.tv_message)).setText(R.string.feedback_thanks_message);
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
}
