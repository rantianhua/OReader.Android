package cn.bandu.oreader.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import cn.bandu.oreader.R;
import cn.bandu.oreader.activity.DetailActivity_;
import cn.bandu.oreader.activity.MainActivity_;
import cn.bandu.oreader.adapter.ListAdapter;

@EFragment(R.layout.fragment_list)
public class MainListViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final static String TAG = MainListViewFragment.class.getSimpleName();

    private ListAdapter adapter;
    private ArrayList<String> datas;
    private int i;

    private MainActivity_ mainActivity;


    @ViewById
    SwipeRefreshLayout listSwipeContainer;

    @ViewById
    ListView list;

    @AfterViews
    public void afterViews() {
        i = 0;
        mainActivity = (MainActivity_) getActivity();
        datas = new ArrayList<String>();
        genPullDatas();

        adapter = new ListAdapter(mainActivity, datas);
        list.setAdapter(adapter);

        listSwipeContainer.setOnRefreshListener(this);
        listSwipeContainer.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void genPullDatas() {
        //重构datas，将新数据放到上面
        for (int j=0 ; j<20; j++) {
            i++;
            datas.add("item - " + i);
        }
        ArrayList<String> tmp = new ArrayList<String>();

        for(int t=datas.size()-1;t>=0;t--) {
            tmp.add(datas.get(t));
        }

        datas.clear();

        for(int t=0;t<tmp.size();t++) {
            datas.add(tmp.get(t));
        }
    }

    public void onRefresh() {
        listSwipeContainer.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                genPullDatas();
                adapter.notifyDataSetChanged();
                listSwipeContainer.setRefreshing(false);
            }
        }, 5000);
    }

    @ItemClick
    void list(int position) {
        Intent intent = new Intent();
        String webUrl = "http://news.163.com/14/1114/05/AB04M0UI00014AED.html";
        String cateName = "英语教学";
        intent.putExtra("webUrl", webUrl);
        intent.putExtra("cateName", cateName);

        intent.setClass(getActivity(), DetailActivity_.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(intent);
    }

    public void setContent(String mContent) {
        Bundle args = new Bundle();
        args.putString("mContent", mContent);
        this.setArguments(args);
    }
}
