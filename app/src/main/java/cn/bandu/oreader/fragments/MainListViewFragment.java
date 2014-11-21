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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cn.bandu.oreader.R;
import cn.bandu.oreader.activity.DetailActivity_;
import cn.bandu.oreader.activity.MainActivity_;
import cn.bandu.oreader.adapter.ListAdapter;
import cn.bandu.oreader.model.ListDo;

@EFragment(R.layout.fragment_list)
public class MainListViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final static String TAG = MainListViewFragment.class.getSimpleName();

    private ListAdapter adapter;
    private int i;

    List<ListDo> datas = new ArrayList<ListDo>();

    private MainActivity_ mainActivity;

    boolean freshing = false;

    @ViewById
    SwipeRefreshLayout listSwipeContainer;

    @ViewById
    ListView list;

    @AfterViews
    public void afterViews() {
        i = 0;
        mainActivity = (MainActivity_) getActivity();

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
        Random random = new Random();
        //重构datas，将新数据放到上面
        for (int j=0 ; j<20; j++) {
            ListDo listDo = new ListDo();
            listDo.title = "item - " + i;
            listDo.description = "";
            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd");
            listDo.createTime = sfd.format(new Date());
            Random random1 = new Random();
            int tmp = random1.nextInt(20) % 4;
            listDo.imageUrls = new ArrayList<String>();
            listDo.model = tmp;
            if (tmp == 2) {
                listDo.imageUrls.add("http://ww3.sinaimg.cn/thumbnail/7d47b003jw1emgmzkh5wsj20c3085ab3.jpg");
                listDo.imageUrls.add("http://ww2.sinaimg.cn/thumbnail/60718250jw1emida7kpogj20fa0ahwfc.jpg");
                listDo.imageUrls.add("http://ww2.sinaimg.cn/bmiddle/d0513221jw1emicwxta35j20dw0993zk.jpg");
            } else if (tmp > 0) {
                listDo.imageUrls.add("http://ww3.sinaimg.cn/thumbnail/7d47b003jw1emgmzkh5wsj20c3085ab3.jpg");
            }
            datas.add(listDo);
            i++;
        }
    }

    public void onRefresh() {
        if (!freshing) {
            freshing = true;
            listSwipeContainer.setRefreshing(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    genPullDatas();
                    adapter.notifyDataSetChanged();
                    listSwipeContainer.setRefreshing(false);
                    freshing = false;
                }
            }, 5000);
        }
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
