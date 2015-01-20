package cn.bandu.oreader.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

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
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import cn.bandu.oreader.OReaderApplication;
import cn.bandu.oreader.OReaderConst;
import cn.bandu.oreader.R;
import cn.bandu.oreader.data.AppPrefs_;
import cn.bandu.oreader.tools.CommonUtil;
import cn.bandu.oreader.tools.DataTools;
import cn.bandu.oreader.tools.FileDownloadThread;
import cn.bandu.oreader.tools.NetCheck;
import cn.bandu.oreader.tools.Stat;
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
    @ViewById
    ProgressBar downloadProgressBar;

    private int downloadedSize = 0;
    private int fileSize = 0;
    private File file;

    @AfterViews
    public void afterViews() {
        //没有网络 && 第一次使用
        if (NetCheck.isNetworkConnected(this) == false && CommonUtil.isFirstUsed(this) == true) {
            startActivityForResult(new Intent(this, AlertDialogActivity_.class).putExtra("titleIsCancel", true).putExtra("msg", "网络连接不可用,是否进行设置?").putExtra("cancel", true), 1);
            return;
        }
        if (NetCheck.isNetworkConnected(this) == false && CommonUtil.isFirstUsed(this) == false) {
            startMain();
        }
        if (CommonUtil.isFirstUsed(this) == true) {
            Stat.sendInstallStat();
            CommonUtil.updateFirestUsed(this);
        }
        if (NetCheck.isNetworkConnected(this) == true) {
            verifyDataVersion();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                //跳转到系统设置
                Intent intent=null;
                //判断手机系统的版本  即API大于10 就是3.0或以上版本
                if(android.os.Build.VERSION.SDK_INT>10){
                    intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                } else {
                    intent = new Intent();
                    ComponentName component = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                this.startActivity(intent);
            }
        }
    }
    /**
     * 启动mainactivity
     */
    private void startMain() {
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                MainActivity_.intent(WelcomeActivity.this).start();
                appPrefs.splash().put(false);
                WelcomeActivity.this.finish();
                WelcomeActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_in);
            }
        }, 2000);
    }

    /**
     * 接受文件下载消息
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.e("msg", String.valueOf(msg.what));
            if (msg.what == 0) {
                int progress = (Double.valueOf((downloadedSize * 1.0 / fileSize * 100))).intValue();
                if (progress == 100) {
                    refreshDatabase();
                }
                downloadProgressBar.setDrawingCacheBackgroundColor(getResources().getColor(R.color.black));
                downloadProgressBar.setProgress(progress);
            } else if (msg.what == 1) {
                startMain();
            }
        }
    };

    /**
     * 更新数据库
     */
    private void refreshDatabase() {
        File dataFile = new File(OReaderApplication.getInstance().getDaoMaster(this, 0).getDatabase().getPath());
        DataTools.copyfile(file, dataFile, true);
        OReaderApplication.setDaoMasterNull(0);
        startMain();
    }

    private boolean verifyDataVersion() {
         //从接口获取
        String url = OReaderConst.VERIFY_URL;

        OReaderApplication.getInstance().getRequestQueue().getCache().remove(url);
        StringRequest req = new StringRequest(StringRequest.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String md5String = jsonObject.getJSONObject("data").getString("md5");
                    String dbUrl = jsonObject.getJSONObject("data").getString("url");
                    String dbVersion = CommonUtil.getDBVersion(WelcomeActivity.this);
                    if (md5String.equals(dbVersion)) {
                        Log.e("md5equail", "md");
                        handler.sendEmptyMessage(1);
                    } else {
                        CommonUtil.updateDBVersion(WelcomeActivity.this, md5String);
                        startDownLoadDatabase(dbUrl);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = VolleyErrorHelper.getMessage(error, WelcomeActivity.this);
            }
        });
        //timeout 1s retry 1
        req.setRetryPolicy(new DefaultRetryPolicy(1 * 1000, 1, 1.0f));
        OReaderApplication.getInstance().addToRequestQueue(req, TAG);

        return true;
    }
    /**
     * 开始下载sqlite
     */
    private void startDownLoadDatabase(String dbUrl) {
        String urlStr = dbUrl;
        File dir = OReaderApplication.getInstance().getDiskCacheDir("download");
        String fileName = OReaderConst.DATABASE_NAME[0];
        file = new File(dir + fileName);
        if (file.exists() == true) {
            file.delete();
        }
        if (dir.exists() == false) {
            dir.mkdir();
        }
        downloadProgressBar.setVisibility(View.VISIBLE);
        downloadProgressBar.setMax(100);
        downloadProgressBar.setProgress(0);
        new downloadTask(urlStr, 5, file + "").start();
    }
    /**
     * 下载文件
     */
    public class downloadTask extends Thread {
        private int blockSize, downloadSizeMore;
        private int threadNum = 5;
        String urlStr, fileName;

        public downloadTask(String urlStr, int threadNum, String fileName) {
            this.urlStr = urlStr;
            this.threadNum = threadNum;
            this.fileName = fileName;
        }

        @Override
        public void run() {
            FileDownloadThread[] fds = new FileDownloadThread[threadNum];
            try {
                URL url = new URL(urlStr);
                URLConnection conn = url.openConnection();
                InputStream in = conn.getInputStream();
                fileSize = conn.getContentLength();
                blockSize = fileSize / threadNum;
                downloadSizeMore = (fileSize % threadNum);
                File file = new File(fileName);
                for (int i = 0; i < threadNum; i++) {
                    //启动线程，分别下载自己需要下载的部分
                    FileDownloadThread fdt = new FileDownloadThread(url, file, i * blockSize, (i + 1) * blockSize - 1);
                    fdt.setName("Thread" + i);
                    fdt.start();
                    fds[i] = fdt;
                }
                boolean finished = false;
                while (!finished) {
                    // 先把整除的余数搞定
                    downloadedSize = downloadSizeMore;
                    finished = true;
                    for (int i = 0; i < fds.length; i++) {
                        downloadedSize += fds[i].getDownloadSize();
                        if (!fds[i].isFinished()) {
                            finished = false;
                        }
                    }
                    Log.e("download size = ", String.valueOf(downloadedSize));
                    handler.sendEmptyMessage(0);
                    //线程暂停一秒
                    sleep(1000);
                }
            } catch (Exception e) {
                handler.sendEmptyMessage(1);
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OReaderApplication.getDaoMaster(this, 0).getDatabase().close();
    }
}
