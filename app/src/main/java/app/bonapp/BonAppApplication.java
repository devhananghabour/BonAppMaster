package app.bonapp;

import android.app.Application;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.app.bonapp.BuildConfig;
import com.app.bonapp.R;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import app.bonapp.constants.Constants;
import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by admin1 on 9/10/17.
 */
//@ReportsCrashes(formKey = "", mailTo = Constants.TO, mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text)

public class BonAppApplication extends MultiDexApplication{
    private static GoogleAnalytics sAnalytics;
    private static Tracker sTracker;
    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
        sAnalytics = GoogleAnalytics.getInstance(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/AvenirNext-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        MultiDex.install(this);

        if(!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics());

            new Instabug.Builder(this, "bc31aa9bcda13cf3c0dad0ae7f4c4b8e")
                    .setInvocationEvent(InstabugInvocationEvent.NONE)
                    .build();
        }

    }


     /* Gets the default {@link Tracker} for this {@link Application}.
            *
            * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }

}
