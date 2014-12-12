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
import cn.bandu.oreader.dao.Fav;
import cn.bandu.oreader.dao.FavDao;
import cn.bandu.oreader.tools.DataTools;
import cn.bandu.oreader.tools.VolleyErrorHelper;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by yangmingfu on 14/11/14.
 */
//@Fullscreen
@EActivity(R.layout.activity_detail)
public class DetailActivity extends Activity {

    private final static String TAG = DetailActivity.class.getSimpleName();

    private Fav data;

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
        data = (Fav) getIntent().getSerializableExtra("data");
        initTitleBar();
        initWebView();
        initBottomBar();
    }
    private void initTitleBar() {
        title.setText(data.getCateName());
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
//        webSettings.setCacheMode(webSettings.LOAD_NO_CACHE);
        webSettings.setCacheMode(webSettings.LOAD_CACHE_ELSE_NETWORK);

        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);


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
        inflater = (LayoutInflater) DetailActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.comment_dialog, null);
        final AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
        dialog = alertDialog.create();;
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
                        map.put("appid", OReaderApplication.getInstance().getAppid());
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
            FavDao favDao = OReaderApplication.getDaoSession(this).getFavDao();
            favDao.deleteByKey(data.getSid());
            Drawable drawable= getResources().getDrawable(R.drawable.fav_normal);
            drawable.setBounds(0, 0, inner_bottom_bar_image_height, inner_bottom_bar_image_height);
            favorAction.setCompoundDrawables(null, drawable, null, null);
        } else {
            favorAction.setTextAppearance(this, R.style.tool_item_text_selected);
            favorAction.setTag("selected");
            fav_pressed.setBounds(0, 0, inner_bottom_bar_image_height, inner_bottom_bar_image_height);
            favorAction.setCompoundDrawables(null, fav_pressed, null, null);
            FavDao favDao = OReaderApplication.getDaoSession(this).getFavDao();
            data.setCreateTime(new Date().getTime());
            favDao.insertOrReplace(data);
        }
        Log.i("title", data.getTitle());
    }

    @Click
    public void shareAction() {
        showShare();
    }
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字
        oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(data.getTitle());
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(data.getWebUrl());
        // text是分享文本，所有平台都需要这个字段
        oks.setText(data.getTitle());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        if (data.getImage0() != null && data.getImage0() !=  "") {
            oks.setImageUrl(data.getImage0());
        }
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(data.getWebUrl());
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(data.getWebUrl());
        // 启动分享GUI
        oks.show(this);
    }
}
