package cn.bandu.oreader.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;

import cn.bandu.oreader.R;

/**
 * Created by yangmingfu on 14/11/14.
 */
@Fullscreen
@EActivity(R.layout.activity_detail)
public class DetailActivity extends Activity {

    private String webUrl;
    private String cateName;

    @ViewById
    WebView webView;
    @ViewById
    TextView title;
    @ViewById
    ProgressBar progressBar;

    private MainActivity_ mainActivity;

    @AfterViews
    public void afterViews() {

        webUrl = getIntent().getStringExtra("webUrl");
        cateName = getIntent().getStringExtra("cateName");

        initTitleBar();
        initWebView();
    }
    private void initTitleBar() {
        Log.e("cateName", cateName);
        title.setText(cateName);
    }
    private void initWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(webSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        webView.addJavascriptInterface(new JsObj(getApplicationContext()), "imagelistner");
        webView.setWebViewClient(new MyWebViewClient());

        webView.loadUrl(webUrl);
    }
    private void addImageClickListner() {
        webView.loadUrl("javascript:(function(){"
                + "var objs = document.getElementsByTagName(\"img\");"
                + "var imgurl=''; " + "for(var i=0;i<objs.length;i++)  " + "{"
                + "imgurl+=objs[i].src+',';"
                + "    objs[i].onclick=function()  " + "    {  "
                + "        window.imagelistner.openImage(this.src);  "
                + "    }  " + "}" + "})()");
    }

    public class JsObj {

        private Context context;

        public JsObj(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void openImage(String img) {
            Intent intent = new Intent();
            intent.putExtra("imgUri", img);
            intent.setClass(context, ImageShowActivity_.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            webView.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageFinished(view, url);
            addImageClickListner();
            progressBar.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressBar.setVisibility(View.VISIBLE);
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    @Click
    public void backTextView() {
        this.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void onBackPressed() {
        backTextView();
    }

    @Click
    public void commentWriteAction() {

    }

    @Click
    public void commentLookAction(){

    }

    @Click
    public void favorAction() {

    }

    @Click
    public void shareAction() {

    }
}
