package cn.bandu.oreader.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import cn.bandu.oreader.R;
import cn.bandu.oreader.adapter.SlidingMenuAdapter;

/**
 * Created by yangmingfu on 14/11/13.
 */
@EFragment(R.layout.fragment_sliding_menu)
public class SlidingMenuFragment extends Fragment{

    @ViewById
    ListView menuList;


    @AfterViews
    public void afterViews() {
        menuList.setAdapter(new SlidingMenuAdapter(getActivity()));
    }

//    @ItemClick
//    public void itemClicked(int postion) {
//
//    }
}
