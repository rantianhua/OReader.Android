package cn.bandu.oreader.activity;

import android.app.Activity;
import android.content.Intent;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import cn.bandu.oreader.OReaderApplication;
import cn.bandu.oreader.R;
import cn.bandu.oreader.data.AppPrefs_;

/**
 * Created by yangmingfu on 14/11/14.
 */
@Fullscreen
@EActivity(R.layout.activity_image_show)
public class ImageShowActivity extends Activity {
    @ViewById
    NetworkImageView imgView;

    String imgUri;

    @Pref
    AppPrefs_ appPrefs;

    ImageLoader imageLoader = OReaderApplication.getInstance().getImageLoader();

    @AfterViews
    public void initImageView() {
        Intent intent = getIntent();
        imgUri = intent.getStringExtra("imgUri");
        imgView.setDefaultImageResId(R.drawable.small_pic_loading);
        imgView.setErrorImageResId(R.drawable.small_load_png_failed);
        imgView.setImageUrl(imgUri, imageLoader);
    }

    @Click
    public void imgView() {
        this.finish();
    }
}