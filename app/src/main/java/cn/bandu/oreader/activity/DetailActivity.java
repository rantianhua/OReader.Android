package cn.bandu.oreader.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.DimensionPixelSizeRes;
import org.androidannotations.annotations.res.DrawableRes;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.bandu.oreader.OReaderApplication;
import cn.bandu.oreader.OReaderConst;
import cn.bandu.oreader.R;
import cn.bandu.oreader.dao.ArticleList;
import cn.bandu.oreader.dao.Cate;
import cn.bandu.oreader.dao.Fav;
import cn.bandu.oreader.dao.FavDao;
import cn.bandu.oreader.tools.CommonUtil;
import cn.bandu.oreader.tools.DataTools;
import cn.bandu.oreader.tools.VolleyErrorHelper;

/**
 * Created by yangmingfu on 14/11/14.
 */
//@Fullscreen
@EActivity(R.layout.activity_detail)
public class DetailActivity extends Activity {

    private final static String TAG = DetailActivity.class.getSimpleName();

    private ArticleList data;

    private Cate cate;

    @ViewById
    WebView webView;
    @ViewById
    TextView title;
    @ViewById
    TextView favorAction;
    @ViewById
    ProgressBar progressBar;
    @DrawableRes(R.drawable.fav_pressed)
    Drawable fav_pressed;
    @DimensionPixelSizeRes
    int inner_bottom_bar_image_height;
    @ViewById
    TextView editBtn;
    @ViewById
    TextView fontSize;
    @ViewById
    View tool_bar_layout;
    @ViewById
    View swipeContainer;

    LayoutInflater inflater;
    View dialogView;
    AlertDialog dialog;
    EditText commentText;

    private MainActivity_ mainActivity;

    @AfterViews
    public void afterViews() {
        data = (ArticleList) getIntent().getSerializableExtra("data");
        cate = (Cate) getIntent().getSerializableExtra("cate");
        initTitleBar();
        initWebView();
        initBottomBar();
    }
    private void initTitleBar() {
        title.setText(cate.getName());
        editBtn.setVisibility(View.GONE);
        fontSize.setVisibility(View.VISIBLE);
    }
    private void initBottomBar() {
        if (data.getSid() == 0) {
            tool_bar_layout.setVisibility(View.GONE);
            return;
        }
        if (DataTools.isFavExists(this, data.getSid()) == true) {
            favorAction.setTag("selected");
            favorAction.setTextAppearance(this, R.style.tool_item_text_selected);
            fav_pressed.setBounds(0, 0, inner_bottom_bar_image_height, inner_bottom_bar_image_height);
            favorAction.setCompoundDrawables(null, fav_pressed, null, null);
        }
    }
    private void initWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBlockNetworkImage(true);
//        webSettings.setCacheMode(webSettings.LOAD_NO_CACHE);
        webSettings.setCacheMode(webSettings.LOAD_CACHE_ELSE_NETWORK);

//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);


        webView.addJavascriptInterface(new JsObj(getApplicationContext()), "imagelistner");
        webView.setWebViewClient(new MyWebViewClient());

        webView.loadUrl(data.getWebUrl());

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
            webView.getSettings().setDefaultTextEncodingName("UTF-8");
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
        //TODO login判断
        inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.comment_dialog, null);
        final AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
        dialog = alertDialog.create();
        commentText = (EditText) dialogView.findViewById(R.id.commentText);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(commentText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        dialog.setView(dialogView);
        dialog.show();


        dialogView.findViewById(R.id.publish_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String commentContent = commentText.getText().toString();
                //TODO 内容合法判断

                String url = String.format(OReaderConst.QUERY_COMMENT_COMMIT_URL, data.getSid());
                StringRequest req = new StringRequest(StringRequest.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //TODO 构造一个json对象,带头像的
                            JSONObject obj = new JSONObject();
                            obj.put("content", commentContent);
                            webView.loadUrl("javascript:insertComment('(" + obj.toString() + ")')");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("content = ", response);
                        dialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = VolleyErrorHelper.getMessage(error, DetailActivity.this);
                        Toast.makeText(DetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() {
                        //在这里设置需要post的参数
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("content", commentContent);
                        map.put("appid", CommonUtil.getAppid());
                        map.put("sid", String.valueOf(data.getSid()));
                        return map;
                    }
                };
                //timeout 3s retry 0
                req.setRetryPolicy(new DefaultRetryPolicy(3 * 1000, 0, 1.0f));
                OReaderApplication.getInstance().addToRequestQueue(req, TAG);
            }
        });
    }

    @Click
    public void commentLookAction(){
        webView.loadUrl("javascript:lookComment()");
    }

    @Click
    public void favorAction() {
        Log.i("favorAction.getTag()", String.valueOf(favorAction.getTag()));
        if (favorAction.getTag() == "selected") {
            favorAction.setTextAppearance(this, R.style.tool_item_text);
            favorAction.setTag("");
            FavDao favDao = OReaderApplication.getDaoSession(this, 1).getFavDao();
            favDao.deleteByKey(data.getSid());
            Drawable drawable= getResources().getDrawable(R.drawable.fav_normal);
            drawable.setBounds(0, 0, inner_bottom_bar_image_height, inner_bottom_bar_image_height);
            favorAction.setCompoundDrawables(null, drawable, null, null);
        } else {
            favorAction.setTextAppearance(this, R.style.tool_item_text_selected);
            favorAction.setTag("selected");
            fav_pressed.setBounds(0, 0, inner_bottom_bar_image_height, inner_bottom_bar_image_height);
            favorAction.setCompoundDrawables(null, fav_pressed, null, null);
            FavDao favDao = OReaderApplication.getDaoSession(this, 1).getFavDao();
            Fav fav = DataTools.articleListToFav(data, new Date().getTime(), cate.getSid(), cate.getName());
            favDao.insertOrReplace(fav);
        }
        Log.i("title", data.getTitle());
    }

    @Click
    public void shareAction() {
        DataTools.showShare(this, data.getTitle(), data.getDescription(), data.getWebUrl(), data.getImage0());
    }


}
