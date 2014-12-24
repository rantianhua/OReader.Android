package cn.bandu.oreader.tools;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.List;

import cn.bandu.oreader.OReaderApplication;
import cn.bandu.oreader.R;
import cn.bandu.oreader.dao.ArticleList;
import cn.bandu.oreader.dao.ArticleListDao;
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
    /**
     * 获取栏目
     * @param context
     * @return
     */
    public static List<Cate> getCateDataFromDB(Context context) {
        DaoSession daoSession = OReaderApplication.getDaoSession(context, 0);
        CateDao cateDao = daoSession.getCateDao();
        List<Cate> data = cateDao.queryBuilder()
                .orderAsc(CateDao.Properties.Sort)
                .list();
        Log.e("data.size=", String.valueOf(data.size()));
        if (data != null && data.size() > 0) {
            return data;
        }
        return null;
    }

    /**
     * 判断article是否被收藏
     * @param context
     * @param sid
     * @return
     */
    public static boolean isFavExists(Context context, long sid) {
        DaoSession daoSession = OReaderApplication.getDaoSession(context, 1);
        FavDao faveDao = daoSession.getFavDao();
        Fav fav = faveDao.load(sid);
        Log.e("fav", String.valueOf(fav));
        if (fav != null && fav.getSid() == sid) {
            return true;
        }
        return false;
    }

    /**
     * 取出收藏列表
     * @param context
     * @return
     */
    public static List<Fav> getFavList(Context context) {
        List<Fav> datas = new ArrayList<Fav>();
        DaoSession daoSession = OReaderApplication.getDaoSession(context, 1);
        FavDao favDao = daoSession.getFavDao();
        datas = favDao.queryBuilder()
                .orderDesc(FavDao.Properties.CreateTime)
                .list();
        return datas;
    }

    /**
     * 获取文章俩表
     * @param context
     * @param datas
     * @param tableName
     * @param size
     * @param offset
     * @return
     */
    public static List<ArticleList> getArticleList(Context context, List<ArticleList> datas,  String tableName, int size, int offset) {
        SQLiteDatabase db = OReaderApplication.getDaoMaster(context, 0).getDatabase();

        String orderBy = "SID COLLATE LOCALIZED ASC";
        ArticleListDao articleListDao = OReaderApplication.getDaoSession(context, 0).getArticleListDao();
        Cursor cursor = db.query(tableName, articleListDao.getAllColumns(), null, null, null, null, orderBy, offset + "," + size);
        Log.e("cursor count=", String.valueOf(cursor.getCount()));
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
            ArticleList articleList = new ArticleList();
            int sidColumn = cursor.getColumnIndex(ArticleListDao.Properties.Sid.columnName);
            int titleColumn = cursor.getColumnIndex(ArticleListDao.Properties.Title.columnName);
            int descriptionColumn = cursor.getColumnIndex(ArticleListDao.Properties.Description.columnName);
            int dateColumn = cursor.getColumnIndex(ArticleListDao.Properties.Date.columnName);
            int webUrlColumn = cursor.getColumnIndex(ArticleListDao.Properties.WebUrl.columnName);
            int image0Column = cursor.getColumnIndex(ArticleListDao.Properties.Image0.columnName);
            int image1Column = cursor.getColumnIndex(ArticleListDao.Properties.Image1.columnName);
            int image2Column = cursor.getColumnIndex(ArticleListDao.Properties.Image2.columnName);
            int modelColumn = cursor.getColumnIndex(ArticleListDao.Properties.Model.columnName);

            articleList.setSid(cursor.getLong(sidColumn));
            articleList.setTitle(cursor.getString(titleColumn));
            articleList.setDescription(cursor.getString(descriptionColumn));
            articleList.setDate(cursor.getString(dateColumn));
            articleList.setWebUrl(cursor.getString(webUrlColumn));
            articleList.setImage0(cursor.getString(image0Column));
            articleList.setImage1(cursor.getString(image1Column));
            articleList.setImage2(cursor.getString(image2Column));
            articleList.setModel(cursor.getInt(modelColumn));
            datas.add(articleList);
        }
        Log.e("data.size=", String.valueOf(datas.size()));
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
                file.getAbsolutePath(), fileName, CommonUtil.getAppName(context));
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
    }

    public static void showShare(Context context, String title, String webUrl, String content, String imageUrl) {
        ShareSDK.initSDK(context);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // 分享时Notification的图标和文字
        oks.setNotification(R.drawable.ic_launcher, CommonUtil.getAppName(context));
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
        oks.setSite(CommonUtil.getAppName(context));
        // 启动分享GUI
        oks.show(context);
    }

    public static void copyfile(File fromFile, File toFile,Boolean rewrite ) {
        if (!fromFile.exists()) {
            return;
        }
        if (!fromFile.isFile()) {
            return;
        }
        if (!fromFile.canRead()) {
            return ;
        }
        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }
        if (toFile.exists() && rewrite) {
            toFile.delete();
        }
        try {
            java.io.FileInputStream fosfrom = new java.io.FileInputStream(fromFile);
            java.io.FileOutputStream fosto = new FileOutputStream(toFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c); //将内容写到新文件当中
            }
            fosfrom.close();
            fosto.close();
        } catch (Exception ex) {
            Log.e("readfile", ex.getMessage());
        }
    }

    /**
     *
     * @param articleList
     * @param createTime
     * @param cateSid
     * @param cateName
     * @return
     */
    public static Fav articleListToFav(ArticleList articleList, long createTime, long cateSid, String cateName) {
        Fav fav = new Fav();
        fav.setSid(articleList.getSid());
        fav.setTitle(articleList.getTitle());
        fav.setDescription(articleList.getDescription());
        fav.setDate(articleList.getDate());
        fav.setWebUrl(articleList.getWebUrl());
        fav.setImage0(articleList.getImage0());
        fav.setImage1(articleList.getImage1());
        fav.setImage2(articleList.getImage2());
        fav.setModel(articleList.getModel());
        fav.setCreateTime(createTime);
        fav.setCateid(cateSid);
        fav.setCateName(cateName);
        return fav;
    }

    /**
     *
     * @param fav
     * @return
     */
    public static ArticleList favToArticleList(Fav fav) {
        ArticleList articleList = new ArticleList();
        articleList.setSid(fav.getSid());
        articleList.setTitle(fav.getTitle());
        articleList.setDescription(fav.getDescription());
        articleList.setDate(fav.getDate());
        articleList.setWebUrl(fav.getWebUrl());
        articleList.setImage0(fav.getImage0());
        articleList.setImage1(fav.getImage1());
        articleList.setImage2(fav.getImage2());
        articleList.setModel(fav.getModel());
        return articleList;
    }

}
