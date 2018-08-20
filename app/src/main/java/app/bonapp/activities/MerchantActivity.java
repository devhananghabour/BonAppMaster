package app.bonapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.bonapp.R;

import app.bonapp.fragments.merchant.MerchantDealsFragment;
import app.bonapp.fragments.merchant.MerchantStatsFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * This is the class where merchant deals fragment and merchant stats fragment are loaded.
 */

public class MerchantActivity extends AppCompatActivity {

    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_end_btn)
    ImageView ivEndBtn;
    @BindView(R.id.fl_merchant_frag_container)
    FrameLayout flMerchantFragContainer;
    @BindView(R.id.tv_end)
    TextView tvEnd;

    private int FRAG_CONTAINER = R.id.fl_merchant_frag_container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);
        ButterKnife.bind(this);
        setUpView();
        pushMerchantDealsFragment();

    }

    /**
     * method to set up view
     */
    private void setUpView() {
        ivBack.setImageResource(R.drawable.ic_greenback);
        ivEndBtn.setImageResource(R.drawable.ic_merchant__bar);
        ivBack.setVisibility(View.VISIBLE);
        ivEndBtn.setVisibility(View.VISIBLE);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    public void showMerchantHomeToolbar(){
        tvTitle.setText(getString(R.string.merchant_mode));
        ivBack.setVisibility(View.VISIBLE);
        ivEndBtn.setVisibility(View.VISIBLE);
        tvCancel.setVisibility(View.GONE);
        tvEnd.setVisibility(View.GONE);
        ivEndBtn.setImageResource(R.drawable.ic_merchant__bar);
    }

    @OnClick({R.id.iv_back, R.id.iv_end_btn,R.id.tv_end,R.id.tv_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_end_btn:
                if (getSupportFragmentManager().findFragmentById(FRAG_CONTAINER) instanceof MerchantDealsFragment){
                    pushMerchantStatsFragment();
                }
                break;
            case R.id.tv_end:

                break;
            case R.id.tv_cancel:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentById(FRAG_CONTAINER) instanceof MerchantStatsFragment){
            showMerchantHomeToolbar();
        }
        super.onBackPressed();
    }

    public void clearBackStack(){
        for (int i=0;i<getSupportFragmentManager().getBackStackEntryCount();i++){
            getSupportFragmentManager().popBackStack();
        }
    }


    public void pushMerchantDealsFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(FRAG_CONTAINER, new MerchantDealsFragment(), MerchantDealsFragment.class.getName());
        fragmentTransaction.commit();
    }



    public void pushMerchantStatsFragment() {
        tvTitle.setText(getString(R.string.merchant_mode));
        ivBack.setVisibility(View.VISIBLE);
        ivEndBtn.setVisibility(View.GONE);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(FRAG_CONTAINER, new MerchantStatsFragment(), MerchantStatsFragment.class.getName()).addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void pushMerchantStatsToolbar(){
        tvTitle.setText(getString(R.string.merchant_mode));
        ivBack.setVisibility(View.VISIBLE);
        ivEndBtn.setVisibility(View.GONE);
    }

}
