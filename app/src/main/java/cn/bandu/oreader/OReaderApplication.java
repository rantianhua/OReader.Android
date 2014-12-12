package cn.bandu.oreader;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
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

    private static DaoMaster daoMaster;

    private static DaoSession daoSession;

    private static OReaderApplication sInstance;

    private static DiskLruCache diskLruCache = null;

    private RequestQueue mRequestQueue;

    private ImageLoader mImageLoader;



    @Override
    public void onCreate(){
        super.onCreate();
        Log.e("APPLICATION CREATE!", "");
        sInstance = this;
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
    public static DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, OReaderConst.DATABASE_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }
    /**
     * 取得DaoSession
     *
     * @param context
     * @return
     */
    public static DaoSession getDaoSession(Context context) {

        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    public static String getAppid() {
        return "111";
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

}