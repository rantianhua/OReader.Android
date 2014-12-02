package cn.bandu.oreader.activity;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.androidannotations.annotations.sharedpreferences.Pref;

import cn.bandu.oreader.R;
import cn.bandu.oreader.data.AppPrefs_;


@WindowFeature({ Window.FEATURE_NO_TITLE, Window.FEATURE_INDETERMINATE_PROGRESS })
@Fullscreen
@EActivity(R.layout.activity_welcome)
public class WelcomeActivity extends Activity{

    private final static String TAG = WelcomeActivity.class.getSimpleName();

    @Pref
    AppPrefs_ appPrefs;

    @ViewById
    View root;

    @AfterViews
    public void afterViews() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "root view clicked");
                MainActivity_.intent(WelcomeActivity.this).start();
                appPrefs.splash().put(false);
                WelcomeActivity.this.finish();
                WelcomeActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);
            }
        }, 1000);
    }

//    @Click
//    public void root()
//    {
//        Log.d(TAG, "root view clicked");
//        MainActivity_.intent(this).start();
//        appPrefs.splash().put(false);
//        this.finish();
//    }
}
