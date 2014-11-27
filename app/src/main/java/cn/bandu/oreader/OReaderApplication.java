package cn.bandu.oreader;

import android.app.Application;
import android.content.Context;

import org.androidannotations.annotations.EApplication;

import cn.bandu.oreader.dao.DaoMaster;
import cn.bandu.oreader.dao.DaoSession;

/**
 * Created by wanghua on 14/11/12.
 */
@EApplication
public class OReaderApplication extends Application {

//    @Override
//    public void onCreate(){
//        super.onCreate();
//        MainActivity_.intent(this).start();
//    }
    private static DaoMaster daoMaster;

    private static DaoSession daoSession;

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
}