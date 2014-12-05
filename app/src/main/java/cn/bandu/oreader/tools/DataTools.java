package cn.bandu.oreader.tools;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

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
        if (cateDao.loadAll().size() > 0) {
            return cateDao.loadAll();
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

    public static List<Fav> cursorToFav(Cursor cursor, List<Fav> datas) {
        for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
            Fav fav = new Fav(
                    cursor.getLong(cursor.getColumnIndex(FavDao.Properties.Sid.columnName)),
                    cursor.getString(cursor.getColumnIndex(FavDao.Properties.Title.columnName)),
                    cursor.getString(cursor.getColumnIndex(FavDao.Properties.Description.columnName)),
                    cursor.getString(cursor.getColumnIndex(FavDao.Properties.Date.columnName)),
                    cursor.getString(cursor.getColumnIndex(FavDao.Properties.WebUrl.columnName)),
                    cursor.getString(cursor.getColumnIndex(FavDao.Properties.Image0.columnName)),
                    cursor.getString(cursor.getColumnIndex(FavDao.Properties.Image1.columnName)),
                    cursor.getString(cursor.getColumnIndex(FavDao.Properties.Image2.columnName)),
                    cursor.getInt(cursor.getColumnIndex(FavDao.Properties.Model.columnName)),
                    cursor.getLong(cursor.getColumnIndex(FavDao.Properties.Cateid.columnName)),
                    cursor.getString(cursor.getColumnIndex(FavDao.Properties.CateName.columnName)),
                    cursor.getLong(cursor.getColumnIndex(FavDao.Properties.CreateTime.columnName))
            );
            datas.add(fav);
        }
        return datas;
    }
}
