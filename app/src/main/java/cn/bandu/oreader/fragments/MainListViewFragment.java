package cn.bandu.oreader.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import cn.bandu.oreader.R;
import cn.bandu.oreader.activity.MainActivity_;
import cn.bandu.oreader.adapter.ListAdapter;

/**
 * Created by yangmingfu on 14-11-11.
 */

public class MainListViewFragment extends ListFragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_list, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.initAdapter();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        MainActivity_ a = (MainActivity_) this.getActivity();
        a.showItemView();
    }

    public void setContent(String mContent) {
        Bundle args = new Bundle();
        args.putString("mContent", mContent);
        this.setArguments(args);
    }

    public void initAdapter() {
        setListAdapter(new ListAdapter(getActivity()));
    }
}
