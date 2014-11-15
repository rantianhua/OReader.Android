package cn.bandu.oreader.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;

import cn.bandu.oreader.R;
import cn.bandu.oreader.activity.MainActivity_;
import cn.bandu.oreader.adapter.ListAdapter;

@EFragment(R.layout.fragment_list)
public class MainListViewFragment extends ListFragment {

    private final static String TAG = MainListViewFragment.class.getSimpleName();

    private ListAdapter adapter;

    private MainActivity_ mainActivity;

    @AfterViews
    public void afterViews() {
        mainActivity = (MainActivity_) getActivity();
        adapter = new ListAdapter(mainActivity);
        setListAdapter(adapter);
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
