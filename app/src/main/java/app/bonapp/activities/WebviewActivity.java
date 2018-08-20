package app.bonapp.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.bonapp.R;

import app.bonapp.utils.AppUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WebviewActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.webview)
    WebView webview;
    private AppUtils appUtils;
    private Activity mActivity;
    private String url="https://www.bonapp.ae/terms";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        setUpViews();
        mActivity=WebviewActivity.this;
        appUtils=AppUtils.getInstance();
        loadWebView();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    /**
     * method to setup views
     */
    private void setUpViews() {
        tvTitle.setText(getString(R.string.terms_and_conditions));
        ivBack.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        onBackPressed();
    }


    /**
     * this method contains the logic of loading the url in webview
     */
    private void loadWebView() {
        webview.loadUrl(url);
        webview.getSettings().setJavaScriptEnabled(true); // enable javascript
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (!mActivity.isFinishing())
                appUtils.showProgressDialog(mActivity,true);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                appUtils.hideProgressDialog(mActivity);
                super.onPageFinished(view, url);
            }
        });


    }
}
