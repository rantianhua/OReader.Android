package cn.bandu.oreader;

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
    public static final String VERIFY_URL = QUERY_HOST + "verify?appid=" + OReaderApplication.getInstance().getAppid() ;

    public static String QUERY_CATE_URL = QUERY_HOST + "getCate.php?appid=%d";
    public static String QUERY_LIST_URL = QUERY_HOST + "getList.php?appid=" + OReaderApplication.getInstance().getAppid() + "&nav=%d&page=%d&size=" + onceNum;
    public static String QUERY_COMMENT_COMMIT_URL = QUERY_HOST + "comment.php";
    public static final String DATA_URL = QUERY_HOST + "OREADER";
}