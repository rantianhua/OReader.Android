package cn.bandu.oreader.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.bandu.oreader.OReaderApplication;
import cn.bandu.oreader.OReaderConst;
import cn.bandu.oreader.R;
import cn.bandu.oreader.activity.DetailActivity_;
import cn.bandu.oreader.adapter.ListAdapter;
import cn.bandu.oreader.dao.ArticleList;
import cn.bandu.oreader.dao.Cate;
import cn.bandu.oreader.tools.DataTools;
import cn.bandu.oreader.view.SwipeRefreshLayout;

@EFragment(R.layout.fragment_list)
public class MainListViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SwipeRefreshLayout.OnLoadListener {

    private final static String TAG = MainListViewFragment.class.getSimpleName();

    private ListAdapter adapter;

    private List<ArticleList> datas = new ArrayList<ArticleList>();

    private int page = 1;

    private Cate cate;

    @ViewById
    SwipeRefreshLayout listSwipeContainer;

    @ViewById
    ListView list;

    @AfterViews
    public void afterViews() {
        cate = (Cate) this.getArguments().getSerializable("cate");

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
        onRefresh();
    }

    @Override
    public void onRefresh() {
        listSwipeContainer.setRefreshing(true);
        datas.clear();
        page = 1;
        String tableName = OReaderConst.PREFIX_TABLE + cate.getSid();
        datas = DataTools.getArticleList(getActivity(), datas, tableName, OReaderConst.onceNum, 0);
        adapter.notifyDataSetChanged();
        enableSwipe();
    }

    @Override
    public void onLoad() {
        listSwipeContainer.setLoading(true);
        page++;
        int offset = (page-1)*OReaderConst.onceNum;
        String tableName = OReaderConst.PREFIX_TABLE + cate.getSid();
        datas = DataTools.getArticleList(getActivity(), datas, tableName, OReaderConst.onceNum, offset);
        adapter.notifyDataSetChanged();
        enableSwipe();
    }

    @ItemClick
    void list(int position) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", datas.get(position));
        bundle.putSerializable("cate", cate);
        intent.putExtras(bundle);

        intent.setClass(getActivity(), DetailActivity_.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(intent);

        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void setContent(Cate cate) {
        Bundle args = new Bundle();
        args.putSerializable("cate", cate);
        this.setArguments(args);
    }
    /**
     * 开启swipe组件
     */
    private void enableSwipe(){
        listSwipeContainer.setRefreshing(false);
        listSwipeContainer.setLoading(false);
        listSwipeContainer.setMode(SwipeRefreshLayout.Mode.BOTH);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            OReaderApplication.getInstance().getDiskLruCache(OReaderConst.DISK_IMAGE_CACHE_DIR).flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
