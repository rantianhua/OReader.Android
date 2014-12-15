package cn.bandu.oreader.tools;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bandu.oreader.OReaderApplication;
import cn.bandu.oreader.OReaderConst;
import cn.bandu.oreader.R;
import cn.bandu.oreader.dao.Cate;
import cn.bandu.oreader.dao.CateDao;
import cn.bandu.oreader.dao.DaoSession;
import cn.bandu.oreader.dao.Fav;
import cn.bandu.oreader.dao.FavDao;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by yangmingfu on 14/12/5.
 */
public class DataTools {

    public static List<Cate> getCateDataFromDB(Context context) {
        DaoSession daoSession = OReaderApplication.getDaoSession(context);
        CateDao cateDao = daoSession.getCateDao();
        List<Cate> data = cateDao.queryBuilder()
                .orderAsc(CateDao.Properties.Sort)
                .list();
        if (data != null && data.size() > 0) {
            return data;
        }
        return null;
    }

    public static boolean isExpired(Long date) {
        long interval = new Date().getTime() - date;
        Log.i("interval", String.valueOf(interval));
        if (interval < OReaderConst.expired) {
            return false;
        }
        return true;
    }

    public static boolean isFavExists(Context context, long sid) {
        DaoSession daoSession = OReaderApplication.getDaoSession(context);
        FavDao faveDao = daoSession.getFavDao();
        Fav fav = faveDao.load(sid);
        Log.e("fav", String.valueOf(fav));
        if (fav != null && fav.getSid() == sid) {
            return true;
        }
        return false;
    }
    public static List<Fav> getFavList(Context context) {
        List<Fav> datas = new ArrayList<Fav>();
        DaoSession daoSession = OReaderApplication.getDaoSession(context);
        FavDao favDao = daoSession.getFavDao();
        datas = favDao.queryBuilder()
                .orderDesc(FavDao.Properties.CreateTime)
                .list();
        return datas;
    }

    /**
     * 将字符串转成MD5值
     *
     * @param key
     * @return
     */
    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                stringBuilder.append('0');
            }
            stringBuilder.append(hex);
        }
        return stringBuilder.toString();
    }

    public static void saveImageToGallery(Context context, Bitmap bmp) throws IOException{
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Bandu");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        Log.e("appDir", String.valueOf(appDir));
        FileOutputStream fos = new FileOutputStream(file);
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
        // 其次把文件插入到系统图库
        MediaStore.Images.Media.insertImage(context.getContentResolver(),
                file.getAbsolutePath(), fileName, OReaderApplication.getInstance().getAppName());
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
    }

    public static void showShare(Context context, String title, String webUrl, String content, String imageUrl) {
        ShareSDK.initSDK(context);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字
        oks.setNotification(R.drawable.ic_launcher, OReaderApplication.getInstance().getAppName());
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        if (title != null && title != "") {
            oks.setTitle(title);
        }
        if (webUrl != null && webUrl != "") {
            // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
            oks.setTitleUrl(webUrl);
            // url仅在微信（包括好友和朋友圈）中使用
            oks.setUrl(webUrl);
            // siteUrl是分享此内容的网站地址，仅在QQ空间使用
            oks.setSiteUrl(webUrl);
        }
        if (content != null && content != "") {
            // text是分享文本，所有平台都需要这个字段
            oks.setText(content);
        }
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        if (imageUrl != null) {
            oks.setImageUrl(imageUrl);
        }
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(OReaderApplication.getInstance().getAppName());
        // 启动分享GUI
        oks.show(context);
    }
}
