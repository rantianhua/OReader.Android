package cn.bandu.oreader.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.bandu.oreader.OReaderApplication;
import cn.bandu.oreader.OReaderConst;
import cn.bandu.oreader.R;
import cn.bandu.oreader.activity.ChatActivity_;
import cn.bandu.oreader.activity.DetailActivity_;
import cn.bandu.oreader.activity.FavoritesActivity_;
import cn.bandu.oreader.dao.Fav;
import cn.bandu.oreader.dao.User;
import cn.bandu.oreader.tools.CommonUtil;
import cn.bandu.oreader.tools.ParseResponse;
import cn.bandu.oreader.tools.VolleyErrorHelper;

/**
 * Created by yangmingfu on 14/11/13.
 */
@EFragment(R.layout.fragment_sliding_menu)
public class SlidingMenuFragment extends Fragment{

    private final static String TAG = SlidingMenuFragment.class.getSimpleName();

    @ViewById
    TextView clear_cacahe;
    @ViewById
    RelativeLayout reg_login;
    @ViewById
    RelativeLayout userinfo;
    @ViewById
    NetworkImageView avatar;
    @ViewById
    TextView username;

    LayoutInflater inflater;
    View dialogView;
    AlertDialog dialog;
    EditText usernameText;
    EditText passwdText;

    @AfterViews
    public void afterViews() {
        User user = CommonUtil.getUserInfo(getActivity());
        if (user != null) {
            reg_login.setVisibility(View.GONE);
            userinfo.setVisibility(View.VISIBLE);
            ImageLoader imageLoader = OReaderApplication.getInstance().getImageLoader();
            avatar.setDefaultImageResId(R.drawable.small_pic_loading);
            avatar.setErrorImageResId(R.drawable.small_load_png_failed);
            avatar.setImageUrl(user.getAvatar(), imageLoader);
            Log.e("avatar=", user.getAvatar());
            username.setText(user.getName());
        } else {
            reg_login.setVisibility(View.VISIBLE);
            userinfo.setVisibility(View.GONE);
        }
    }

    @Click
    public void favorite_btn() {
        Intent intent = new Intent();
        intent.setClass(this.getActivity(), FavoritesActivity_.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        this.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    @Click
    public void clear_cacahe_btn() {
        new AlertDialog.Builder(getActivity()).setTitle("确认清除缓存吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //TODO 删除缓存
                float size = OReaderApplication.getInstance().getDiskLruCache(OReaderConst.DISK_IMAGE_CACHE_DIR).size()/1024/1024;
                try {
                    OReaderApplication.getInstance().getDiskLruCache(OReaderConst.DISK_IMAGE_CACHE_DIR).delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getActivity(), "清除成功,共删除" + size + "M", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        }).show();
    }

    @Click
    public void about_btn() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        Fav data = new Fav();
        data.setSid(0);
        data.setWebUrl(OReaderConst.ABOUT_URL);
        data.setCateName("关于我们");
        bundle.putSerializable("data", data);
        intent.putExtras(bundle);
        intent.setClass(getActivity(), DetailActivity_.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        this.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Click
    public void feedback_btn() {
        if (CommonUtil.getUserInfo(getActivity()) == null) {
            login_btn();
            return;
        }
        Intent intent = new Intent();
        intent.setClass(getActivity(), ChatActivity_.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        this.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Click
    public void login_btn() {
        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.dialog_login, null);
        final AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());
        dialog = alertDialog.create();
        usernameText = (EditText) dialogView.findViewById(R.id.username);
        passwdText = (EditText) dialogView.findViewById(R.id.passwd);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(usernameText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        dialog.setView(dialogView);
        dialog.show();

        dialogView.findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = usernameText.getText().toString();
                final String passwd = passwdText.getText().toString();
                String url = OReaderConst.QUERY_LOGIN_URL;
                StringRequest req = new StringRequest(StringRequest.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            User user = ParseResponse.parseUserInfo(response);
                            Log.e("user.name", response);

                            Log.e("user.name", user.getName());
                            if (user == null) {
                                CommonUtil.setUserInfo(getActivity(), null);
                            } else {
                                CommonUtil.setUserInfo(getActivity(), response);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        afterViews();
                        dialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = VolleyErrorHelper.getMessage(error, getActivity());
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        //在这里设置需要post的参数
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("username", username);
                        map.put("password", passwd);
                        map.put("autologin", "1");
                        return map;
                    }
                };
                //timeout 3s retry 0
                req.setRetryPolicy(new DefaultRetryPolicy(3 * 1000, 0, 1.0f));
                OReaderApplication.getInstance().addToRequestQueue(req, TAG);
            }
        });

        dialogView.findViewById(R.id.canel_btn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
    @Click
    public void reg_btn() {

    }
    @Click
    public void logout() {

    }
}
