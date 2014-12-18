package cn.bandu.oreader;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jakewharton.disklrucache.DiskLruCache;

import org.androidannotations.annotations.EApplication;

import java.io.File;
import java.io.IOException;

import cn.bandu.oreader.dao.DaoMaster;
import cn.bandu.oreader.dao.DaoSession;
import cn.bandu.oreader.tools.LruBitmapCache;

/**
 * Created by wanghua on 14/11/12.
 */
@EApplication
public class OReaderApplication extends Application {

    public static final String TAG = "VolleyPatterns";

    private static DaoMaster[] daoMaster = {null, null};

    private static DaoSession[] daoSession = {null, null};

    private static OReaderApplication sInstance;

    private static DiskLruCache diskLruCache = null;

    private RequestQueue mRequestQueue;

    private ImageLoader mImageLoader;

    @Override
    public void onCreate(){
        super.onCreate();
        Log.e("APPLICATION CREATE!", "");
        sInstance = this;
        if (isFirstUsed() == true) {
            sendStatCode();
            updateFirestUsed();
        }
    }

    /**
     * @return ApplicationController singleton instance
     */
    public static synchronized OReaderApplication getInstance() {
        return sInstance;
    }

    /**
     * @return The Volley Request queue, the queue will be created if it is null
     */
    public RequestQueue getRequestQueue() {
        // lazy initialize the request queue, the queue instance will be
        // created when it is accessed for the first time
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    /**
     * Adds the specified request to the global queue, if tag is specified
     * then it is used else Default TAG is used.
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        VolleyLog.d("Adding request to queue: %s", req.getUrl());
        getRequestQueue().add(req);
    }

    /**
     * Adds the specified request to the global queue using the Default TAG.
     *
     * @param req
     */
    public <T> void addToRequestQueue(Request<T> req) {
        // set the default tag if tag is empty
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    /**
     * 获取imageLoader对象
     * @return ImageLoader
     */
    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    /**
     * 取得DaoMaster
     *
     * @param context
     * @return
     */
    public static DaoMaster getDaoMaster(Context context, int index) {
        if (daoMaster[index] == null) {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, OReaderConst.DATABASE_NAME[index], null);
            daoMaster[index] = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster[index];
    }
    public static void setDaoMasterNull(int index) {
        daoMaster[index] = null;
    }
    /**
     * 取得DaoSession
     *
     * @param context
     * @return
     */
    public static DaoSession getDaoSession(Context context, int index) {
        if (daoSession[index] == null) {
            if (daoMaster[index] == null) {
                daoMaster[index] = getDaoMaster(context, index);
            }
            daoSession[index] = daoMaster[index].newSession();
        }
        return daoSession[index];
    }

    public static String getAppid() {
        return OReaderApplication.getInstance().getResources().getString(R.string.appid);
    }

    public int getAppVersion() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public String getAppName() {
        String appName = "";
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            appName = info.applicationInfo.loadLabel(getPackageManager()).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.e("appname = ", appName);
        return appName;
    }


    public DiskLruCache getDiskLruCache(String uniqueName) {
        try {
            diskLruCache = DiskLruCache.open(getDiskCacheDir(uniqueName), OReaderApplication.getInstance().getAppVersion(), 1, OReaderConst.DISK_MAX_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return diskLruCache;
    }

    /**
     * 获取disk缓存位置
     *
     * @param uniqueName
     * @return
     */
    public File getDiskCacheDir(String uniqueName) {
        String cachePath;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            cachePath = String.valueOf(Environment.getExternalStorageDirectory());
        } else {
            cachePath = this.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 判断是否是第一次使用
     *
     * @return false:已经使用过 true:第一次使用
     */
    public boolean isFirstUsed() {

        Boolean isFirstIn = false;
        SharedPreferences pref = this.getSharedPreferences("OREADER", 0);
        //取得相应的值，如果没有该值，说明还未写入，用true作为默认值
        isFirstIn = pref.getBoolean("isFirstIn", true);
        Log.e("isFirstIn=", String.valueOf(isFirstIn));
        return isFirstIn;
    }

    /**
     * 更新第一次使用标识
     */
    public void updateFirestUsed() {
        SharedPreferences pref = this.getSharedPreferences("OREADER", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isFirstIn", false);
        editor.commit();
    }

    public void sendStatCode() {
        String url = String.format(OReaderConst.STAT_URL, this.getAppid());
        this.getRequestQueue().getCache().invalidate(url, true);
        Log.e("stat url = ", url);
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