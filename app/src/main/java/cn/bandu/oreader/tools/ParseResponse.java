package cn.bandu.oreader.tools;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.bandu.oreader.dao.Cate;
import cn.bandu.oreader.dao.Fav;
import cn.bandu.oreader.dao.User;
import cn.huanxin.model.DefaultHXSDKModel;

/**
 * Created by yangmingfu on 14/12/2.
 */
public class ParseResponse {
    public static HashMap parseList(String str) throws JSONException {
        HashMap data = new HashMap();
        List<Fav> listData = new ArrayList<Fav>();

        JSONArray resJson = new JSONArray(str);
        data.put("total", resJson.get(0));
        Log.i("total = %d", String.valueOf(resJson.get(0)));

        JSONArray dataJson = resJson.getJSONArray(1);

        int dataLength = dataJson.length();
        for (int i=0;i<dataLength;i++) {
            JSONObject item = new JSONObject(dataJson.get(i).toString());
            Log.i("item=", item.getString("image0"));
            Fav fav = new Fav(item.getInt("sid"), item.getString("title"),
                    item.getString("description"), item.getString("date"),
                    item.getString("webUrl"),
                    item.getString("image0") == null || item.getString("image0") == "" ? null : item.getString("image0"),
                    item.getString("image1") == null || item.getString("image1") == "" ? null : item.getString("image1"),
                    item.getString("image2") == null || item.getString("image2") == "" ? null : item.getString("image2"),
                    item.getInt("model"), item.getLong("cateId"), item.getString("cateName"), 0);
            Log.i("item title=", fav.getTitle());

            listData.add(fav);
        }
        data.put("list", listData);
        return data;
    }

    public static List<Cate> parseCate(String response) throws JSONException {
        JSONArray resJson = new JSONArray(response);
        int length = resJson.length();
        List<Cate> data = new ArrayList<Cate>();
        Log.i("length=", String.valueOf(length));
        for (int i=0;i<length;i++) {
            JSONObject item = new JSONObject(resJson.get(i).toString());
            Cate cate = new Cate();
            cate.setSid(item.getLong("sid"));
            cate.setName(item.getString("name"));
            cate.setDate(new Date().getTime());
            cate.setSort(item.getInt("sort"));
            data.add(cate);
        }
        return data;
    }

    public static User parseUserInfo(String response) throws JSONException{
        if (response == null) {
            return null;
        }
        JSONObject jo = new JSONObject(response);
        int status = jo.getInt("status");
        Log.e("status=", String.valueOf(status));
        if (status == 0) {
            return null;
        }
        JSONObject jsonObject = new JSONObject(jo.getString("data"));
        Log.e("jsonobject", String.valueOf(jsonObject));
        User user = new User(jsonObject.getLong("id"), jsonObject.getLong("uid"), jsonObject.getString("name"), jsonObject.getString("avatar"));
        return user;
    }

    public static DefaultHXSDKModel parseHXUser(String response, Context context) throws JSONException {
        DefaultHXSDKModel model = new DefaultHXSDKModel(context);
        if (response == null) {
            return null;
        }
        JSONObject jo = new JSONObject(response);
        int status = jo.getInt("status");
        if (status == 0) {
            return null;
        }
        JSONObject jsonObject = new JSONObject(jo.getString("data"));
        model.saveHXId(jsonObject.getString("username"));
        model.savePassword(jsonObject.getString("password"));
        return model;
    }
}
