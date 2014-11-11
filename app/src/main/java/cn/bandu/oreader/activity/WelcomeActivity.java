package cn.bandu.oreader.activity;

import android.app.Activity;
import android.view.View;
import android.view.Window;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import cn.bandu.oreader.R;


@WindowFeature({ Window.FEATURE_NO_TITLE, Window.FEATURE_INDETERMINATE_PROGRESS })
@Fullscreen
@EActivity(R.layout.layout_welcome)
public class WelcomeActivity extends Activity{

    @ViewById
    View root;

    @Click
    public void root()
    {
        MainActivity_.intent(this).start();
        this.finish();
    }
}
