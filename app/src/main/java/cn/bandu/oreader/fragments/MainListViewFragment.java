package cn.bandu.oreader.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import cn.bandu.oreader.R;
import cn.bandu.oreader.activity.MainActivity_;
import cn.bandu.oreader.adapter.ListAdapter;

@EFragment(R.layout.fragment_list)
public class MainListViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final static String TAG = MainListViewFragment.class.getSimpleName();

    private ListAdapter adapter;

    private MainActivity_ mainActivity;

    @ViewById
    SwipeRefreshLayout listSwipeContainer;

    @ViewById
    ListView list;

    @AfterViews
    public void afterViews() {
        mainActivity = (MainActivity_) getActivity();
        adapter = new ListAdapter(mainActivity);
        list.setAdapter(adapter);
        listSwipeContainer.setOnRefreshListener(this);
        listSwipeContainer.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    public void onRefresh() {
        Toast.makeText(mainActivity, "Refreshing", Toast.LENGTH_SHORT).show();
        //TODO add refreshing act here
        listSwipeContainer.setRefreshing(false);
//        new Handler().postDelayed(new Runnable() {
//            @Override public void run() {
//
//            }
//        }, 5000);
    }

    @ItemClick
    void list(int position) {
        Log.e(TAG, "......." + position);
        mainActivity.showItemView();
        mainActivity.hideSlidingMenu();
    }

    public void setContent(String mContent) {
        Bundle args = new Bundle();
        args.putString("mContent", mContent);
        this.setArguments(args);
    }
}
