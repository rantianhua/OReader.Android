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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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

    private boolean isRefreshing = false;

    List<ListDo> datas = new ArrayList<ListDo>();


    @ViewById
    SwipeRefreshLayout listSwipeContainer;

    @ViewById
    ListView list;

    @AfterViews
    public void afterViews() {
        i = 0;

        genPullDatas();

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

    private void genPullDatas() {
        Random random = new Random();
        List<ListDo> datasTmp = new ArrayList<ListDo>();
        //重构datas，将新数据放到上面
        for (int j=0 ; j<2; j++) {
            ListDo listDo = new ListDo();
            listDo.title = "在流行歌曲还被认为是“靡靡之音”的时候 item - " + i;
            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd");
            listDo.createTime = sfd.format(new Date());
            int tmp = random.nextInt(20) % 4;
            listDo.description = "著名歌唱家王昆去世。上世纪80年代，在流行歌曲还被认为是“靡靡之音”的时候，她就曾力挺流行乐。歌很能打动人，所以我就批准他唱";


            listDo.imageUrls = new ArrayList<String>();
            listDo.model = tmp;
            listDo.imageUrls.add("http://ww3.sinaimg.cn/thumbnail/7d47b003jw1emgmzkh5wsj20c3085ab3.jpg?" + random.nextInt(20));
            listDo.imageUrls.add("http://ww2.sinaimg.cn/thumbnail/60718250jw1emida7kpogj20fa0ahwfc.jpg?" + random.nextInt(20));
            listDo.imageUrls.add("http://ww2.sinaimg.cn/bmiddle/d0513221jw1emicwxta35j20dw0993zk.jpg?" + random.nextInt(20));

            datasTmp.add(listDo);
            i++;
        }
        for (int m=datas.size()-1;m>=0;m--){
            datasTmp.add(datas.get(m));
        }
        datas.clear();
        for (int m=0;m<datasTmp.size();m++){
            datas.add(datasTmp.get(m));
        }

    }

    public void onRefresh() {
        if (!isRefreshing) {
            isRefreshing = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    genPullDatas();
                    adapter.notifyDataSetChanged();
                    listSwipeContainer.setRefreshing(false);
                    isRefreshing = false;
                }
            }, 500);
        } else {
            return;
        }
    }
    private void genPushDatas() {
        Random random = new Random();
        //重构datas，将新数据放到上面
        for (int j=0 ; j<2; j++) {
            ListDo listDo = new ListDo();
            listDo.title = "在流行歌曲还被认为是“靡靡之音”的时候 item - " + i;
            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd");
            listDo.createTime = sfd.format(new Date());
            int tmp = random.nextInt(20) % 4;
            listDo.description = "著名歌唱家王昆去世。上世纪80年代，在流行歌曲还被认为是“靡靡之音”的时候，她就曾力挺流行乐。歌很能打动人，所以我就批准他唱";


            listDo.imageUrls = new ArrayList<String>();
            listDo.model = tmp;
            listDo.imageUrls.add("http://ww3.sinaimg.cn/thumbnail/7d47b003jw1emgmzkh5wsj20c3085ab3.jpg?" + random.nextInt(20));
            listDo.imageUrls.add("http://ww2.sinaimg.cn/thumbnail/60718250jw1emida7kpogj20fa0ahwfc.jpg?" + random.nextInt(20));
            listDo.imageUrls.add("http://ww2.sinaimg.cn/bmiddle/d0513221jw1emicwxta35j20dw0993zk.jpg?" + random.nextInt(20));

            datas.add(listDo);
            i++;
        }

    }
    public void onLoad() {
        if (!isRefreshing) {
            isRefreshing = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    genPushDatas();
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
        Intent intent = new Intent();
        String webUrl = "http://m2.people.cn/r/MV80XzEzMjI1NTBfODNfMTQxNjU1ODc0Ng==";
        String cateName = "英语教学";
        intent.putExtra("webUrl", webUrl);
        intent.putExtra("cateName", cateName);

        intent.setClass(getActivity(), DetailActivity_.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(intent);
//        getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void setContent(String mContent) {
        Bundle args = new Bundle();
        args.putString("mContent", mContent);
        this.setArguments(args);
    }
}
