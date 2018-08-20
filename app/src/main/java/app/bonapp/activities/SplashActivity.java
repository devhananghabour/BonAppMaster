package app.bonapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.app.bonapp.R;

import app.bonapp.utils.AppSharedPrefs;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * This class is used for showing splash screen for 2.5 secs
 */
public class SplashActivity extends AppCompatActivity {
    private static final long DELAY_TIME = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setDelayTime();

    }

    /**
     * this method is used to check user is already logged in or not
     */
    private void setDelayTime() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, DELAY_TIME);
    }
}
