package cn.bandu.oreader.tools;

import android.content.Context;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bandu.oreader.OReaderApplication;
import cn.bandu.oreader.OReaderConst;
import cn.bandu.oreader.dao.Cate;
import cn.bandu.oreader.dao.CateDao;
import cn.bandu.oreader.dao.DaoSession;
import cn.bandu.oreader.dao.Fav;
import cn.bandu.oreader.dao.FavDao;

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
     * @param string
     * @return
     */
    public static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }
}
