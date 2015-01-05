package cn.bandu.oreader.activity;

import android.support.v4.app.FragmentActivity;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import cn.bandu.oreader.OReaderApplication;
import cn.bandu.oreader.OReaderConst;
import cn.bandu.oreader.R;
import cn.bandu.oreader.dao.User;
import cn.bandu.oreader.tools.CommonUtil;
import cn.bandu.oreader.tools.ParseResponse;
import cn.bandu.oreader.tools.VolleyErrorHelper;

/**
 * Created by yangmingfu on 14/12/31.
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends FragmentActivity {
    private final static String TAG = LoginActivity.class.getSimpleName();

    @ViewById
    EditText usernameEdit;
    @ViewById
    EditText passwdEdit;

    @AfterViews
    public void afterViews() {

    }

    @Click
    public void backTextView() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        backTextView();
    }

    @Click
    public void loginButton() {
        final String username = usernameEdit.getText().toString();
        final String passwd = passwdEdit.getText().toString();
        String url = OReaderConst.QUERY_LOGIN_URL;
        StringRequest req = new StringRequest(StringRequest.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    User user = ParseResponse.parseUserInfo(response);
                    if (user == null) {
                        CommonUtil.setUserInfo(LoginActivity.this, null);
                    } else {
                        CommonUtil.setUserInfo(LoginActivity.this, response);
                    }
                    backTextView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = VolleyErrorHelper.getMessage(error, LoginActivity.this);
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
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
}
