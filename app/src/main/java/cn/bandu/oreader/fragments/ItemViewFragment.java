package cn.bandu.oreader.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import cn.bandu.oreader.R;
import cn.bandu.oreader.activity.ImageShowActivity_;
import cn.bandu.oreader.activity.MainActivity_;


/**
 * Created by wanghua on 14/11/11.
 */
@EFragment(R.layout.fragment_item_view)
public class ItemViewFragment extends Fragment {

    private String webUrl;


    @ViewById
    TextView backTextView;

    @ViewById
    WebView webView;

    private MainActivity_ mainActivity;

    @AfterViews
    public void afterViews() {
        mainActivity  = (MainActivity_) this.getActivity();

        webUrl = "http://news.163.com/14/1114/05/AB04M0UI00014AED.html";
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        webView.addJavascriptInterface(new JavascriptInterface(getActivity().getApplicationContext()), "imagelistner");
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

    public class JavascriptInterface {

        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

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
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageFinished(view, url);
            addImageClickListner();
            webView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
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
        mainActivity.removeItemView();
        mainActivity.showSlidingMenu();
    }
}
