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

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;
import org.androidannotations.annotations.sharedpreferences.Pref;

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
        Log.e("NetCheck.isNetworkConnected(this)=", String.valueOf(NetCheck.isNetworkConnected(this)));
        Log.e("CommonUtil.isFirstUsed(this)=", String.valueOf(CommonUtil.isFirstUsed(this)));
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
            startDownLoadDatabase();
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
            int progress = (Double.valueOf((downloadedSize * 1.0 / fileSize * 100))).intValue();
            if (progress == 100) {
                refreshDatabase();
            }
            downloadProgressBar.setDrawingCacheBackgroundColor(R.color.black);
            downloadProgressBar.setProgress(progress);
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

    /**
     * 开始下载sqlite
     */
    private void startDownLoadDatabase() {
        String urlStr = OReaderConst.DATA_URL;
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
                e.printStackTrace();
            }
        }
    }
}
