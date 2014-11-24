package cn.bandu.oreader.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import cn.bandu.oreader.R;
import cn.bandu.oreader.data.AppPrefs_;

/**
 * Created by yangmingfu on 14/11/14.
 */
@Fullscreen
@EActivity(R.layout.activity_image_show)
public class ImageShowActivity extends Activity implements Picasso.Listener {
    @ViewById
    ImageView imgView;

    String imgUri;

    @Pref
    AppPrefs_ appPrefs;

    @AfterViews
    public void initImageView() {
        Intent intent = getIntent();
        imgUri = intent.getStringExtra("imgUri");
        Picasso.with(this)
                .load(imgUri)
                .placeholder(R.drawable.small_image_holder_listpage_loading)
                .error(R.drawable.example)
                .into(imgView);
    }

    @Click
    public void imgView() {
        this.finish();
    }

    @Override
    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
        Log.i("failed", "failed");
    }
}