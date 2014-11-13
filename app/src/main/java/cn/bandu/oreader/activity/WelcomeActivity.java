package cn.bandu.oreader.activity;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.androidannotations.annotations.sharedpreferences.Pref;

import cn.bandu.oreader.R;
import cn.bandu.oreader.data.AppPrefs_;


@WindowFeature({ Window.FEATURE_NO_TITLE, Window.FEATURE_INDETERMINATE_PROGRESS })
@Fullscreen
@EActivity(R.layout.layout_welcome)
public class WelcomeActivity extends Activity{

    private final static String TAG = WelcomeActivity.class.getSimpleName();

    @Pref
    AppPrefs_ appPrefs;

    @ViewById
    View root;

    @AfterViews
    public void afterViews() {
        Log.d(TAG, "toast app prefs: splash");
        Toast.makeText(this, appPrefs.splash().get() ? "show splash" : "do not show" , Toast.LENGTH_SHORT) ;
    }

    @Click
    public void root()
    {
        Log.d(TAG, "root view clicked");
        MainActivity_.intent(this).start();
        appPrefs.splash().put(false);
        this.finish();
    }
}
