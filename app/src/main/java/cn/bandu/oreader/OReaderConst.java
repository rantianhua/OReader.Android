package cn.bandu.oreader;

import cn.bandu.oreader.tools.CommonUtil;

/**
 * Created by yangmingfu on 14/11/24.
 */
public class OReaderConst {

    public static final String PREFIX_TABLE = "LIST_";
    public static String DISK_IMAGE_CACHE_DIR = "image";


    public static String[] DATABASE_NAME = {"OREADER", "OREADER_FAV"};

    public static int onceNum = 10;

    public static long expired = 86400000;
    //硬盘存储空间
    public static final long DISK_MAX_SIZE = 10 * 1024 * 1024;


//    public static String QUERY_HOST = "http://app.51tbzb.cn/";
    public static String QUERY_HOST = "http://app.ymf.bandu.in/";
    public static final String STAT_URL = QUERY_HOST + "stat.gif?appid=%s";
    public static final String ABOUT_URL = QUERY_HOST + "about.html";
    public static final String VERIFY_URL = QUERY_HOST + "verify?appid=" + CommonUtil.getAppid() ;

    public static String QUERY_CATE_URL = QUERY_HOST + "getCate.php?appid=%d";
    public static String QUERY_LIST_URL = QUERY_HOST + "getList.php?appid=" + CommonUtil.getAppid() + "&nav=%d&page=%d&size=" + onceNum;
    public static String QUERY_COMMENT_COMMIT_URL = QUERY_HOST + "comment.php";
    public static final String DATA_URL = QUERY_HOST + "OREADER";

    public static final String QUERY_LOGIN_URL = "http://bandu.ymf.bandu.in/app/login";
    public static final String QUERY_REGHUANXIN_URL = "http://bandu.ymf.bandu.in/app/regHuanXin?username=%s";


    public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
    public static final String GROUP_USERNAME = "item_groups";
    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";

}