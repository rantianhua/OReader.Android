package cn.bandu.oreader.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.io.File;
import java.io.IOException;

import cn.bandu.oreader.OReaderApplication;
import cn.bandu.oreader.R;
import cn.bandu.oreader.data.AppPrefs_;
import cn.bandu.oreader.tools.BitMapUtil;
import cn.bandu.oreader.tools.DataTools;

/**
 * Created by yangmingfu on 14/11/14.
 */
@Fullscreen
@EActivity(R.layout.activity_image_show)
public class ImageShowActivity extends Activity {
    @ViewById
    NetworkImageView imgView;
    @ViewById
    ImageView download;
    @ViewById
    ImageView localImage;

    String imgUri;

    @Pref
    AppPrefs_ appPrefs;

    ImageLoader imageLoader = OReaderApplication.getInstance().getImageLoader();

    @AfterViews
    public void initImageView() {
        Intent intent = getIntent();
        imgUri = intent.getStringExtra("imgUri");
        if (new File(imgUri).exists()) {
            Bitmap bitMap = BitMapUtil.file2Bitmap(imgUri);
            localImage.setImageBitmap(bitMap);
            imgView.setVisibility(View.GONE);
            download.setVisibility(View.GONE);
        } else {
            imgView.setDefaultImageResId(R.drawable.small_pic_loading);
            imgView.setErrorImageResId(R.drawable.small_load_png_failed);
            imgView.setImageUrl(imgUri, imageLoader);
            download.setVisibility(View.VISIBLE);
            imgView.setVisibility(View.VISIBLE);
            localImage.setVisibility(View.GONE);
        }
    }

    @Click
    public void imgView() {
        this.finish();
    }


    @Click
    public void localImage() {
        this.finish();
    }

    @Click
    public void download() {
        Bitmap image = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
        try {
            DataTools.saveImageToGallery(ImageShowActivity.this, image);
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }
}