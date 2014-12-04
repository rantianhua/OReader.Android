package cn.bandu.oreader.activity;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.json.JSONException;

import java.util.List;

import cn.bandu.oreader.OReaderApplication;
import cn.bandu.oreader.OReaderConst;
import cn.bandu.oreader.R;
import cn.bandu.oreader.dao.Cate;
import cn.bandu.oreader.dao.CateDao;
import cn.bandu.oreader.dao.DaoSession;
import cn.bandu.oreader.data.AppPrefs_;
import cn.bandu.oreader.tools.ParseResponse;
import cn.bandu.oreader.tools.VolleyErrorHelper;


@WindowFeature({ Window.FEATURE_NO_TITLE, Window.FEATURE_INDETERMINATE_PROGRESS })
@Fullscreen
@EActivity(R.layout.activity_welcome)
public class WelcomeActivity extends Activity {

    private final static String TAG = WelcomeActivity.class.getSimpleName();

    @Pref
    AppPrefs_ appPrefs;

    @ViewById
    View root;

    @AfterViews
    public void afterViews() {
        String url = String.format(OReaderConst.QUERY_CATE_URL, 1);

        OReaderApplication.getInstance().getRequestQueue().getCache().invalidate(url, true);
        Log.i("cat url = ", url);
        StringRequest req = new StringRequest(StringRequest.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    List<Cate> cate = ParseResponse.parseCate(response);
                    OReaderConst.CONTENT.clear();
                    for (int i=0;i<cate.size();i++) {
                        OReaderConst.CONTENT.add(cate.get(i));
                        DaoSession daoSession = OReaderApplication.getDaoSession(WelcomeActivity.this);
                        CateDao cateDao = daoSession.getCateDao();
                        cateDao.insertOrReplace(cate.get(i));
                    }
                    startMain(true);
                } catch (JSONException e) {
                    startMain(false);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = VolleyErrorHelper.getMessage(error, WelcomeActivity.this);
                startMain(false);
            }
        });
        //timeout 1s retry 0
        req.setRetryPolicy(new DefaultRetryPolicy(1 * 1000, 0, 1.0f));
        OReaderApplication.getInstance().addToRequestQueue(req, TAG);
    }

    private void startMain(boolean success) {
        Log.i("success = ", String.valueOf(success));

        if (success == false) {
            DaoSession daoSession = OReaderApplication.getDaoSession(WelcomeActivity.this);
            CateDao cateDao = daoSession.getCateDao();
            if (cateDao.loadAll().size() > 0) {
                OReaderConst.CONTENT.clear();
                OReaderConst.CONTENT = cateDao.loadAll();
            }
        }
        MainActivity_.intent(WelcomeActivity.this).start();
        appPrefs.splash().put(false);
        WelcomeActivity.this.finish();
        WelcomeActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);
    }
}
