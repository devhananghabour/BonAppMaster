package app.bonapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.app.bonapp.R;

import app.bonapp.constants.Constants;
import app.bonapp.fragments.DiscoverFragment;
import app.bonapp.fragments.FavoritesFragment;
import app.bonapp.fragments.MyAccountHomeFragment;
import app.bonapp.fragments.NotSignedInFragment;
import app.bonapp.utils.AppSharedPrefs;
import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.activity_main_bottom_navigation_view) BottomNavigationView bottomNavigationView;
    @BindView(R.id.main_toolbar_toolbar)                 Toolbar              toolbar;

    private DiscoverFragment      discoverFragment;
    private FavoritesFragment     favoritesFragment;
    private MyAccountHomeFragment myAccountHomeFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        setupToolbar();
        openDiscoverFragment();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle("");
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener
            onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_discover:
                    openDiscoverFragment();
                    return true;
                case R.id.navigation_favorites:
                    openFavoritesFragment();
                    return true;
                case R.id.navigation_profile:
                    openProfileFragment();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        bottomNavigationView.setSelectedItemId(bottomNavigationView.getSelectedItemId());
    }

    private void openDiscoverFragment() {
        if (discoverFragment == null) {
            discoverFragment = DiscoverFragment.newInstance();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_home_container, discoverFragment).commit();
    }

    private void openFavoritesFragment() {
        if (favoritesFragment == null) {
            favoritesFragment = FavoritesFragment.newInstances();
        }
        if (AppSharedPrefs.getInstance(this).getBoolean(AppSharedPrefs.PREF_KEY.IS_LOGIN, false)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.activity_home_container, favoritesFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.activity_home_container, new NotSignedInFragment()).commit();
        }
    }

    private void openProfileFragment() {
        if (myAccountHomeFragment == null) {
            myAccountHomeFragment = new MyAccountHomeFragment();
        }
        if (AppSharedPrefs.getInstance(this).getBoolean(AppSharedPrefs.PREF_KEY.IS_LOGIN, false)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.activity_home_container, myAccountHomeFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.activity_home_container, new NotSignedInFragment()).commit();
        }
    }

    private void openInbox() {
        if (AppSharedPrefs.getInstance(this).getBoolean(AppSharedPrefs.PREF_KEY.IS_LOGIN, false)) {
            if (AppSharedPrefs.getInstance(this).getString(AppSharedPrefs.PREF_KEY.USER_TYPE, "2").equals(Constants.MERCHANT_TYPE)) {
                Intent intent = new Intent(this, MerchantActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, OrderHistoryActivity.class);
                intent.putExtra(Constants.FROM_CHECKOUT_KEY, true);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }

}
