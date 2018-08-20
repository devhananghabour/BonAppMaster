package app.bonapp.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import app.bonapp.utils.AppSharedPrefs;


public class MyFireBaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService ";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);
        AppSharedPrefs.getInstance(getApplicationContext()).putString(AppSharedPrefs.PREF_KEY.DEVICE_TOKEN,refreshedToken);
    }

}
