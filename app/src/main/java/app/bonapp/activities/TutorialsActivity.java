package app.bonapp.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;

import com.app.bonapp.R;

import app.bonapp.utils.AppSharedPrefs;
import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TutorialsActivity extends AppCompatActivity {


    @BindView(R.id.video_view)
    VideoView videoView;
    private int lastSeekTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorials);
        ButterKnife.bind(this);
        //setUpViewPager();
        AppSharedPrefs.getInstance(this).putBoolean(AppSharedPrefs.PREF_KEY.IS_FIRST_TIME, false);
        loadVideo();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    /**
     * Commented as there can be an alternate tutorial flow with few tutorial pages .
     */
    /*private void setUpViewPager() {
        vpTutorials.setAdapter(new TutorialPagerAdapter(TutorialsActivity.this));
        circleIndicator.setViewPager(vpTutorials);
    }*/

/*
    @OnClick(R.id.btn_find_great_deals)
    public void onClick() {
        Intent intent = new Intent(TutorialsActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
*/

    /**
     * method to load video from raw folder and play it.
     */
    private void loadVideo() {
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.tutorial);
        videoView.setVideoURI(video);
        videoView.start();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Intent intent = new Intent(TutorialsActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
        //lastSeekTime=videoView.getCurrentPosition();
    }


    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();
    }
}
