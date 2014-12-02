package cn.bandu.oreader.tools;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bandu.oreader.dao.Fav;

/**
 * Created by yangmingfu on 14/12/2.
 */
public class ParseResponse {
    public static List<Fav> parseFav(String str) throws JSONException {
        List<Fav> data = new ArrayList<Fav>();
        JSONArray dataJson = new JSONArray(str);
        int dataLength = dataJson.length();
        for (int i=0;i<dataLength;i++) {
            JSONObject item = new JSONObject(dataJson.get(i).toString());
            Log.i("item=", item.getString("image0"));
            Fav fav = new Fav(null, item.getInt("sid"), item.getString("title"),
                    item.getString("description"), item.getString("date"),
                    item.getString("webUrl"),
                    item.getString("image0") == null || item.getString("image0") == "" ? null : item.getString("image0"),
                    item.getString("image1") == null || item.getString("image1") == "" ? null : item.getString("image1"),
                    item.getString("image2") == null || item.getString("image2") == "" ? null : item.getString("image2"),
                    item.getInt("model"));
            Log.i("item title=", fav.getTitle());

            data.add(fav);
        }
        Log.i("data.size", String.valueOf(data.size()));
        return data;
    }
}
