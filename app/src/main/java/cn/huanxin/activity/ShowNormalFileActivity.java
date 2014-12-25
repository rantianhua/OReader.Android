package cn.huanxin.activity;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.easemob.chat.EMChatConfig;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.FileMessageBody;
import com.easemob.cloud.CloudOperationCallback;
import com.easemob.cloud.HttpFileManager;
import com.easemob.util.FileUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.bandu.oreader.R;

/**
 * Created by yangmingfu on 14/12/25.
 */
@EActivity(R.layout.activity_showfile)
public class ShowNormalFileActivity extends Activity {

    @ViewById
    ProgressBar progressBar;
    private File file;

    @AfterViews
    public void afterViews() {
        final FileMessageBody messageBody = getIntent().getParcelableExtra("msgbody");
        file = new File(messageBody.getLocalUrl());
        //set head map
        final Map<String, String> maps = new HashMap<String, String>();
        String accessToken = EMChatManager.getInstance().getAccessToken();
        maps.put("Authorization", "Bearer " + accessToken);
        if (!TextUtils.isEmpty(messageBody.getSecret())) {
            maps.put("share-secret", messageBody.getSecret());
        }
        maps.put("Accept", "application/octet-stream");

        //下载文件
        new Thread(new Runnable() {
            public void run() {
                HttpFileManager fileManager = new HttpFileManager(ShowNormalFileActivity.this, EMChatConfig.getInstance().getStorageUrl());
                fileManager.downloadFile(messageBody.getRemoteUrl(), messageBody.getLocalUrl(), EMChatConfig.getInstance().APPKEY,maps,
                        new CloudOperationCallback() {

                            @Override
                            public void onSuccess(String result) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        FileUtils.openFile(file, ShowNormalFileActivity.this);
                                        finish();
                                    }
                                });
                            }

                            @Override
                            public void onProgress(final int progress) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressBar.setProgress(progress);
                                    }
                                });
                            }

                            @Override
                            public void onError(final String msg) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        if(file != null && file.exists())
                                            file.delete();
                                        Toast.makeText(ShowNormalFileActivity.this, "下载文件失败: " + msg, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }
                        });
            }
        }).start();

    }
}
