package cn.bandu.oreader.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import cn.bandu.oreader.fragments.MainListViewFragment_;

public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

    private final static String TAG = MainViewPagerAdapter.class.getSimpleName();

    protected static final String[] CONTENT = new String[]{"语文", "数学", "化学", "物理", "政治", "天文", "语文", "数学", "化学", "物理", "政治", "天文", "地理"};
    private int mCount = CONTENT.length;
    Activity activity;

    public MainViewPagerAdapter(FragmentManager fm, Activity activity) {
        super(fm);
        this.activity = activity;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "get Item: " + position);
        MainListViewFragment_ mainListViewFragment = new MainListViewFragment_();
        mainListViewFragment.setContent(CONTENT[position % CONTENT.length]);
        mainListViewFragment.setLoadDatasListener((cn.bandu.oreader.fragments.MainListViewFragment.LoadDatasListener) activity);
        return mainListViewFragment;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return MainViewPagerAdapter.CONTENT[position % CONTENT.length];
    }
}
