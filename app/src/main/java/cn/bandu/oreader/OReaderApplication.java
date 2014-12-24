package cn.bandu.oreader;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.jakewharton.disklrucache.DiskLruCache;

import org.androidannotations.annotations.EApplication;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import cn.bandu.oreader.dao.DaoMaster;
import cn.bandu.oreader.dao.DaoSession;
import cn.bandu.oreader.tools.CommonUtil;
import cn.bandu.oreader.tools.LruBitmapCache;
import cn.bandu.oreader.tools.Stat;

/**
 * Created by wanghua on 14/11/12.
 */
@EApplication
public class OReaderApplication extends Application {

    private final static String TAG = OReaderApplication_.class.getSimpleName();

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
        if (CommonUtil.isFirstUsed(this) == true) {
            Stat.sendInstallStat();
            CommonUtil.updateFirestUsed(this);
        }
        initHuanxin();
    }

    private void initHuanxin() {
        int pid = android.os.Process.myPid();
        String processAppName = getProcessName(pid);
        // 如果使用到百度地图或者类似启动remote service的第三方库，这个if判断不能少
        if (processAppName == null || processAppName.equals("")) {
            return;
        }

        //初始化环信SDK
        Log.d("DemoApplication", "Initialize EMChat SDK");
        EMChat.getInstance().init(this);
        //获取到EMChatOptions对象
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        //添加好友不需要验证
        options.setAcceptInvitationAlways(true);
        //设置收到消息是否有新消息通知，默认为true
        options.setNotificationEnable(false);
        //设置收到消息是否有声音提示，默认为true
        options.setNoticeBySound(false);
        //设置收到消息是否震动 默认为true
        options.setNoticedByVibrate(false);
        //设置语音消息播放是否设置为扬声器播放 默认为true
        options.setUseSpeaker(false);
    }
    private String getProcessName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this
                .getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i
                    .next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm
                            .getApplicationInfo(info.processName,PackageManager.GET_META_DATA));
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
            }
        }
        return processName;
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

    public DiskLruCache getDiskLruCache(String uniqueName) {
        try {
            diskLruCache = DiskLruCache.open(getDiskCacheDir(uniqueName), CommonUtil.getAppVersion(this), 1, OReaderConst.DISK_MAX_SIZE);
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
}