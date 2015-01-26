package cn.bandu.oreader.tools;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import cn.bandu.oreader.OReaderApplication;
import cn.bandu.oreader.OReaderConst;

/**
 * Created by yangmingfu on 14/12/19.
 */
public class Stat {

    private final static String TAG = Stat.class.getSimpleName();

    public static void sendInstallStat() {
        String url = OReaderConst.STAT_URL;
        OReaderApplication.getInstance().getRequestQueue().getCache().invalidate(url, true);
        StringRequest req = new StringRequest(StringRequest.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("stat start", "stat start");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        //timeout 3s retry 1
        req.setRetryPolicy(new DefaultRetryPolicy(3 * 1000, 1, 1.0f));
        OReaderApplication.getInstance().addToRequestQueue(req, TAG);
    }
}
