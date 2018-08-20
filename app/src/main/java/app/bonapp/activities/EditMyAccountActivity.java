package app.bonapp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bonapp.R;

import app.bonapp.constants.Constants;
import app.bonapp.fragments.accountfragments.ChangeEmailFragment;
import app.bonapp.fragments.accountfragments.ChangePasswordFragment;
import app.bonapp.fragments.accountfragments.MyDetailsFragment;
import app.bonapp.utils.AppSharedPrefs;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * This class is landing activity of my account where MyAccountHomeFragment,MyDetailsFragment,ChangeEmailFragment
 * ChangePasswordFragment,AboutBonAppFragment fragments are loaded.
 */
public class EditMyAccountActivity extends AppCompatActivity {

    public static final String OPEN_FRAGMENT_FLAG = "open_fragment_flag";
    public static final String EDIT_ACCOUNT       = "edit_account";

    @BindView(R.id.iv_back)
    ImageView      ivBack;
    @BindView(R.id.rl_toolbar)
    RelativeLayout rlToolbar;
    @BindView(R.id.fl_frag_container)
    FrameLayout    flFragContainer;
    @BindView(R.id.activity_my_account)
    RelativeLayout activityMyAccount;
    @BindView(R.id.tv_title)
    TextView       tvTitle;
    @BindView(R.id.tv_save)
    TextView       tvSave;
    private int FRAG_CONTAINER = R.id.fl_frag_container;

    public static void openEditAccount(Context context) {
        Intent intent = new Intent(context, EditMyAccountActivity.class);
        intent.putExtra(OPEN_FRAGMENT_FLAG, EDIT_ACCOUNT);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_my_account);
        ButterKnife.bind(this);
        String extras = getExtras();
        if (extras != null) {
            switch (extras){
                case EDIT_ACCOUNT:
                    pushMyDetailsFragment();
                    break;
            }
        } else {
            //no extras something went wrong go back
            Toast.makeText(this, "Error opening EditMyAccountActivity", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    /**
     * method to set toolbar according to my account fragment.
     */
    public void setMyAccountToolbar() {
        tvTitle.setText(R.string.my_account);
        tvSave.setVisibility(View.GONE);
        ivBack.setVisibility(View.VISIBLE);
    }

    public void setMyDetailToolbar() {
        tvTitle.setText(getString(R.string.my_details));
        ivBack.setImageResource(R.drawable.ic_greencross);
        tvSave.setVisibility(View.VISIBLE);
        tvSave.setText(R.string.edit);
    }

    public void pushMyDetailsFragment() {
        if (AppSharedPrefs.getInstance(this).getString(AppSharedPrefs.PREF_KEY.USER_TYPE, "").equals(Constants.CUSTOMER_TYPE)){
            tvSave.setVisibility(View.VISIBLE);
            tvSave.setText(R.string.edit);
        }
        MyDetailsFragment myDetailsFragment = new MyDetailsFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right);
        fragmentTransaction.replace(FRAG_CONTAINER, myDetailsFragment).commit();
    }

    public void pushChangeEmailAddressFragment() {
        tvTitle.setText(getString(R.string.change_e_mail_address));
        ivBack.setImageResource(R.drawable.ic_greenback);
        tvSave.setVisibility(View.VISIBLE);
        tvSave.setText(R.string.save);

        ChangeEmailFragment changeEmailFragment = new ChangeEmailFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(FRAG_CONTAINER, changeEmailFragment).commit();
    }

    public void pushChangePasswordFragment() {
        tvTitle.setText(getString(R.string.change_password));
        ivBack.setImageResource(R.drawable.ic_greenback);
        tvSave.setVisibility(View.VISIBLE);
        tvSave.setText(R.string.save);

        ChangePasswordFragment changeEmailFragment = new ChangePasswordFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right);
        fragmentTransaction.add(FRAG_CONTAINER, changeEmailFragment).commit();
    }


    @OnClick({R.id.iv_back, R.id.tv_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_save:
                Fragment fragment = getSupportFragmentManager().findFragmentById(FRAG_CONTAINER);
                if (fragment instanceof ChangeEmailFragment) {
                    ((ChangeEmailFragment) fragment).showEmailChangeAlert();
                } else if (fragment instanceof ChangePasswordFragment) {
                    ((ChangePasswordFragment) fragment).hitApiForChangePwd();
                } else if (fragment instanceof MyDetailsFragment) {
                    ((MyDetailsFragment) fragment).onEditClickListener();
                }
                break;
        }
    }

    public void setEditButtonLabel(String text) {
        tvSave.setText(text);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(FRAG_CONTAINER);
        if (fragment instanceof ChangeEmailFragment || fragment instanceof ChangePasswordFragment) {
            setMyDetailToolbar();
        }
        super.onBackPressed();

    }

    private String getExtras() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            return bundle.getString(OPEN_FRAGMENT_FLAG, null);
        }

        return null;
    }

}


