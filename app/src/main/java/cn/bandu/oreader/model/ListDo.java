package cn.bandu.oreader.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yangmingfu on 14/11/20.
 */
public class ListDo implements Serializable {

    public String title;
    public String description;
    public String createTime;
    public ArrayList<String> imageUrls;
    public String webUrl;
    public int model;
}
