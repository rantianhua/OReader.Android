package cn.bandu.oreader;

import java.util.ArrayList;
import java.util.List;

import cn.bandu.oreader.dao.Cate;

/**
 * Created by yangmingfu on 14/11/24.
 */
public class OReaderConst {

    //图片缓存模式，默认是disk，可选lru与disk
    public static String IMAGE_CACHE_PATTERN = "disk";

    public static String DATABASE_NAME = "OREADER";
    public static int onceNum = 10;


    public static String QUERY_HOST = "http://app.ymf.bandu.in/";
    public static String QUERY_CATE_URL = QUERY_HOST + "getCate.php?appid=%d";
    public static String QUERY_LIST_URL = QUERY_HOST + "getList.php?appid=" + OReaderApplication.getInstance().getAppid() + "&nav=%d&page=%d&size=" + onceNum;
    public static Cate DEFAULT_CATE = new Cate((long) 1, 0, "推荐");
    public static List<Cate> CONTENT = new ArrayList<Cate>();
    static{
        CONTENT.add(DEFAULT_CATE);
    }
}