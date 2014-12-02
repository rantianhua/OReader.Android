package cn.bandu.oreader;

/**
 * Created by yangmingfu on 14/11/24.
 */
public class OReaderConst {
    public static String DATABASE_NAME = "OREADER";
    public static int onceNum = 10;


    public static String QUERY_HOST = "http://app.ymf.bandu.in/";
    public static String QUERY_NAV_URL = QUERY_HOST + "getNav.php?appid=";
    public static String QUERY_LIST_URL = QUERY_HOST + "getList.php?appid=%d&nav=%d&page=%d&size=" + onceNum;

    public static final String[] CONTENT = new String[]{"学词", "短语地带", "跟踪训练"};
}