package cn.bandu.oreader.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bandu.oreader.OReaderApplication;
import cn.bandu.oreader.OReaderConst;
import cn.bandu.oreader.R;
import cn.bandu.oreader.activity.DetailActivity_;
import cn.bandu.oreader.adapter.ListAdapter;
import cn.bandu.oreader.dao.Fav;
import cn.bandu.oreader.tools.ParseResponse;
import cn.bandu.oreader.view.SwipeRefreshLayout;

@EFragment(R.layout.fragment_list)
public class MainListViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SwipeRefreshLayout.OnLoadListener {

    private final static String TAG = MainListViewFragment.class.getSimpleName();

    private ListAdapter adapter;

    private boolean isRefreshing = false;

    private List<Fav> datas = new ArrayList<Fav>();
    private int total;
    private int page = 1;

    private String cateName;

    String FirstPage = String.format(OReaderConst.QUERY_LIST_URL, 1, 1, 1);

    @ViewById
    SwipeRefreshLayout listSwipeContainer;

    @ViewById
    ListView list;

    @AfterViews
    public void afterViews() {
        cateName = this.getArguments().getString("cateName");

        listSwipeContainer.setOnRefreshListener(this);
        listSwipeContainer.setOnLoadListener(this);
        listSwipeContainer.setColor(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        listSwipeContainer.setMode(SwipeRefreshLayout.Mode.BOTH);
        listSwipeContainer.setLoadNoFull(false);

        adapter = new ListAdapter(getActivity(), datas);
        list.setAdapter(adapter);

        initDefaultData();
    }
    public void initDefaultData() {
        datas.clear();
        if (requestDataFromCache(FirstPage) == false) {
            requestDataFromNet(FirstPage);
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        datas.clear();
        requestDataFromNet(FirstPage);
    }

    @Override
    public void onLoad() {
        page++;
        if (OReaderConst.onceNum * (page-1) >= total) {
            listSwipeContainer.setLoading(false);
            listSwipeContainer.setMode(SwipeRefreshLayout.Mode.PULL_FROM_START);
            Toast.makeText(getActivity(), "没有更多数据了", Toast.LENGTH_SHORT).show();
            return;
        }
        String PageUrl = String.format(OReaderConst.QUERY_LIST_URL, 111, 1, page);
        requestDataFromNet(PageUrl);
    }

    @ItemClick
    void list(int position) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", datas.get(position));
        bundle.putString("cateName", cateName);
        intent.putExtras(bundle);

        intent.setClass(getActivity(), DetailActivity_.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(intent);

        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void setContent(String cateName) {
        Bundle args = new Bundle();
        args.putString("cateName", cateName);
        this.setArguments(args);
    }

    public boolean requestDataFromCache(String url) {
        Cache cache = OReaderApplication.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(url);
        if(entry != null) {
            try {
                String data = new String(entry.data, "UTF-8");
                HashMap dataMap = ParseResponse.parseList(data);
                total = (Integer) dataMap.get("total");
                datas = (List<Fav>) dataMap.get("list");
                adapter.setDatas(datas);
                adapter.notifyDataSetChanged();
                listSwipeContainer.setRefreshing(false);
                return true;
            } catch (UnsupportedEncodingException e) {
                return false;
            } catch (JSONException e) {
                return false;
            }
        }
        return false;
    }

    public void requestDataFromNet(String url) {
        listSwipeContainer.setMode(SwipeRefreshLayout.Mode.DISABLED);
        OReaderApplication.getInstance().getRequestQueue().getCache().invalidate(url, true);
        StringRequest req = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    HashMap dataMap = ParseResponse.parseList(response);
                    total = (Integer) dataMap.get("total");
                    List<Fav> tmpDatas = (List<Fav>) dataMap.get("list");
                    for (int i =0; i<tmpDatas.size();i++) {
                        datas.add(tmpDatas.get(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.setDatas(datas);
                adapter.notifyDataSetChanged();
                listSwipeContainer.setRefreshing(false);
                listSwipeContainer.setLoading(false);
                listSwipeContainer.setMode(SwipeRefreshLayout.Mode.BOTH);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ", error.getMessage());
            }
        });
        //timeout 3s retry 1
        req.setRetryPolicy(new DefaultRetryPolicy(3 * 1000, 1, 1.0f));
        OReaderApplication.getInstance().addToRequestQueue(req, TAG);
    }
}
