package cn.bandu.oreader.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;

import org.json.JSONException;

import cn.bandu.oreader.OReaderApplication_;
import cn.bandu.oreader.OReaderConst;
import cn.bandu.oreader.R;
import cn.bandu.oreader.dao.User;

/**
 * Created by yangmingfu on 14/12/19.
 */
public class CommonUtil {
    /**
     * 判断是否是第一次使用
     *
     * @return false:已经使用过 true:第一次使用
     */
    public static boolean isFirstUsed(Context context) {
        Boolean isFirstIn = false;
        SharedPreferences pref = context.getSharedPreferences("OREADER", 0);
        isFirstIn = pref.getBoolean("isFirstIn", true);
        return isFirstIn;
    }

    /**
     * 更新第一次使用标识
     */
    public static void updateFirestUsed(Context context) {
        SharedPreferences pref = context.getSharedPreferences("OREADER", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isFirstIn", false);
        editor.commit();
    }

    /**
     * 取出db md5值
     * @param context
     * @return
     */
    public static String getDBVersion(Context context) {
        SharedPreferences pref = context.getSharedPreferences("OREADER", 0);
        String dbVersion  = pref.getString("dbversion", null);
        return dbVersion;
    }

    /**
     * 设置db md5值
     * @param context
     * @param str
     */
    public static void updateDBVersion(Context context, String str) {
        SharedPreferences pref = context.getSharedPreferences("OREADER", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("dbversion", str);
        editor.commit();
    }

    /**
     * 获取用户登录信息
     */
    public static User getUserInfo(Context context) {
        SharedPreferences pref = context.getSharedPreferences("OREADER", 0);
        String userinfo = pref.getString("userinfo", null);
        User user = null;
        try {
            user = ParseResponse.parseUserInfo(userinfo);
        } catch (JSONException e) {
            return user;
        }
        return user;
    }

    /**
     * 更新用户登录信息
     */
    public static void setUserInfo(Context context, String userinfo) {
        SharedPreferences pref = context.getSharedPreferences("OREADER", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("userinfo", userinfo);
        editor.commit();
    }

    public static String getAppid() {
        return OReaderApplication_.getInstance().getResources().getString(R.string.appid);
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static String getAppName(Context context) {
        String appName = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appName = info.applicationInfo.loadLabel(context.getPackageManager()).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }

    /**
     * 根据消息内容和消息类型获取消息内容提示
     *
     * @param message
     * @param context
     * @return
     */
    public static String getMessageDigest(EMMessage message, Context context) {
        String digest = "";
        switch (message.getType()) {
            case LOCATION: // 位置消息
                if (message.direct == EMMessage.Direct.RECEIVE) {
                    //从sdk中提到了ui中，使用更简单不犯错的获取string方法
//              digest = EasyUtils.getAppResourceString(context, "location_recv");
                    digest = getStrng(context, R.string.location_recv);
                    digest = String.format(digest, message.getFrom());
                    return digest;
                } else {
//              digest = EasyUtils.getAppResourceString(context, "location_prefix");
                    digest = getStrng(context, R.string.location_prefix);
                }
                break;
            case IMAGE: // 图片消息
                digest = getStrng(context, R.string.picture);
                break;
            case VOICE:// 语音消息
                digest = getStrng(context, R.string.voice);
                break;
            case VIDEO: // 视频消息
                digest = getStrng(context, R.string.video);
                break;
            case TXT: // 文本消息
                if(!message.getBooleanAttribute(OReaderConst.MESSAGE_ATTR_IS_VOICE_CALL,false)){
                    TextMessageBody txtBody = (TextMessageBody) message.getBody();
                    digest = txtBody.getMessage();
                }else{
                    TextMessageBody txtBody = (TextMessageBody) message.getBody();
                    digest = getStrng(context, R.string.voice_call) + txtBody.getMessage();
                }
                break;
            case FILE: //普通文件消息
                digest = getStrng(context, R.string.file);
                break;
            default:
                System.err.println("error, unknow type");
                return "";
        }

        return digest;
    }
    public static String getStrng(Context context, int resId){
        return context.getResources().getString(resId);
    }
}
