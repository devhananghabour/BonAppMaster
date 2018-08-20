package app.bonapp.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.bonapp.R;

import app.bonapp.activities.RatingActivity;
import app.bonapp.activities.WebviewActivity;
import app.bonapp.constants.Constants;
import app.bonapp.utils.AppUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * this class shows about app
 */
public class AboutBonAppFragment extends Fragment {


    private static final String INSTA_URL = "https://www.instagram.com/bonapp_uae";
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_contact_us)
    TextView tvContactUs;
    @BindView(R.id.tv_rate_us)
    TextView tvRateUs;
    @BindView(R.id.tv_bon_app_fb_page)
    TextView tvBonAppFbPage;
    private Activity mActivity;
    private AppUtils appUtils;
    public static final String FACEBOOK_URL = "https://www.facebook.com/bonappuae";
    public static String FACEBOOK_PAGE_ID = "425733341109824";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_bon_app, container, false);
        ButterKnife.bind(this, view);
        mActivity=getActivity();
        appUtils=AppUtils.getInstance();
        tvVersion.setText(getString(R.string.version)+" "+getVersion());
        return view;
    }

    /**
     * method to get version of app
     */
    private String getVersion(){
        PackageManager manager = mActivity.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    mActivity.getPackageName(), 0);
            return info.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    @OnClick({R.id.tv_contact_us, R.id.tv_rate_us, R.id.tv_bon_app_fb_page, R.id.tv_terms_conditions,R.id.tv_bon_app_insta_page})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_contact_us:
                appUtils.sendEmail(mActivity,Constants.BONAPP_SUPPORT_EMAIL,"","");
                break;
            case R.id.tv_rate_us:
                Intent intent1=new Intent(mActivity, RatingActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_bon_app_fb_page:
                //startActivity(getOpenFacebookIntent(mActivity));
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(FACEBOOK_URL));
                startActivity(facebookIntent);
                break;
            case R.id.tv_terms_conditions:
                Intent intent2=new Intent(mActivity, WebviewActivity.class);
                startActivity(intent2);
                break;
            case R.id.tv_bon_app_insta_page:
                Intent instaIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(INSTA_URL));
                startActivity(instaIntent);
                break;
        }
    }

}
