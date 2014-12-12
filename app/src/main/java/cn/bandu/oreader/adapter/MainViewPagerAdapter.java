package cn.bandu.oreader.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.bandu.oreader.dao.Cate;
import cn.bandu.oreader.fragments.MainListViewFragment_;
import cn.bandu.oreader.tools.DataTools;

public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

    private final static String TAG = MainViewPagerAdapter.class.getSimpleName();

    public List<Cate> CATEGORY;

    Activity activity;

    public MainViewPagerAdapter(FragmentManager fm, Activity activity) {
        super(fm);
        this.activity = activity;
        getCateGory();
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "get Item: " + position);
        MainListViewFragment_ mainListViewFragment = new MainListViewFragment_();
        mainListViewFragment.setContent(CATEGORY.get(position % CATEGORY.size()));
        return mainListViewFragment;
    }

    @Override
    public int getCount() {
        return CATEGORY.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return CATEGORY.get(position % CATEGORY.size()).getName();
    }

    private void getCateGory() {
        CATEGORY = DataTools.getCateDataFromDB(activity);
        if (CATEGORY == null || CATEGORY.size() == 0) {
            CATEGORY = new ArrayList<Cate>();
            CATEGORY.add(new Cate(0, "推荐", 0, 1));
        }
    }
}