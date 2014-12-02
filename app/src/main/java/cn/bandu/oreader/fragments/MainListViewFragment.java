package cn.bandu.oreader.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;

import java.util.ArrayList;
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

    List<Fav> datas = new ArrayList<Fav>();

    private String cateName;

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
    }

    /**
     * 初始化数据
     *
     */
    public void initDefaultDatas() {
        //TODO 从volley cache中获取数据或者加载其他默认数据

    }

    public void onRefresh() {
        Log.i("onRefresh", "onRefresh");
        String URL = OReaderConst.QUERY_LIST_URL.replace("$page", "1").replace("$appid", "1").replace("$nav", "1");
        Log.i("onRefresh URL=", URL);

        StringRequest req = new StringRequest(URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    datas = ParseResponse.parseFav(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("onRefresh", String.valueOf(datas.size()));
                adapter.setDatas(datas);
                adapter.notifyDataSetChanged();
                listSwipeContainer.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error: ", error.getMessage());
            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        OReaderApplication.getInstance().addToRequestQueue(req, TAG);
    }
    public void onLoad() {
        listSwipeContainer.setMode(SwipeRefreshLayout.Mode.DISABLED);
        if (!isRefreshing) {
            isRefreshing = true;
            loadData(datas, cateName);
            adapter.notifyDataSetChanged();
            listSwipeContainer.setLoading(false);
            isRefreshing = false;
            listSwipeContainer.setMode(SwipeRefreshLayout.Mode.BOTH);
        } else {
            return;
        }
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

    public void loadData(List<Fav> datas, String cateName) {
        if (datas.size() > 100) {
            Toast.makeText(getActivity(), "没有更多数据了", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
