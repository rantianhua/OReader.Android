package cn.bandu.oreader.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import cn.bandu.oreader.R;
import cn.bandu.oreader.activity.DetailActivity_;
import cn.bandu.oreader.adapter.ListAdapter;
import cn.bandu.oreader.model.ListDo;
import cn.bandu.oreader.view.SwipeRefreshLayout;

@EFragment(R.layout.fragment_list)
public class MainListViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SwipeRefreshLayout.OnLoadListener {

    private final static String TAG = MainListViewFragment.class.getSimpleName();

    private ListAdapter adapter;
    private int i;
    private LoadDatasListener loadDatasListener;

    private boolean isRefreshing = false;

    List<ListDo> datas = new ArrayList<ListDo>();


    @ViewById
    SwipeRefreshLayout listSwipeContainer;

    @ViewById
    ListView list;

    @AfterViews
    public void afterViews() {
        i = 0;
        loadDatasListener.refreshData(datas);
        adapter = new ListAdapter(getActivity(), datas);
        list.setAdapter(adapter);

        listSwipeContainer.setOnRefreshListener(this);
        listSwipeContainer.setOnLoadListener(this);
        listSwipeContainer.setColor(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        listSwipeContainer.setMode(SwipeRefreshLayout.Mode.BOTH);
        listSwipeContainer.setLoadNoFull(false);
     }

    public void setLoadDatasListener(LoadDatasListener listener) {
        loadDatasListener = listener;
    }

    public void onRefresh() {
        loadDatasListener.refreshData(datas);
        if (!isRefreshing) {
            isRefreshing = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadDatasListener.refreshData(datas);
                    adapter.notifyDataSetChanged();
                    listSwipeContainer.setRefreshing(false);
                    isRefreshing = false;
                }
            }, 500);
        } else {
            return;
        }
    }
    public void onLoad() {
        if (!isRefreshing) {
            isRefreshing = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadDatasListener.loadData(datas);
                    adapter.notifyDataSetChanged();
                    listSwipeContainer.setLoading(false);
                    isRefreshing = false;
                }
            }, 500);
        } else {
            return;
        }
    }

    @ItemClick
    void list(int position) {
        String cateName = "英语教学";
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

    public void setContent(String mContent) {
        Bundle args = new Bundle();
        args.putString("mContent", mContent);
        this.setArguments(args);
    }
    public interface LoadDatasListener {
        public void refreshData(List<ListDo> datas);
        public void loadData(List<ListDo> datas);
    }
}
