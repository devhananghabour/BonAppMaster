package app.bonapp.serivces;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.app.bonapp.R;

public class SoundService extends Service{

    private static MediaPlayer mediaPlayer;


    @Override
    public void onCreate() {
        super.onCreate();
        startSound();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSound();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startSound(){
        mediaPlayer = MediaPlayer.create(this, R.raw.order_received_alarm);
        if(mediaPlayer!=null && !mediaPlayer.isPlaying()) {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    private void stopSound(){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
        }
    }
}
