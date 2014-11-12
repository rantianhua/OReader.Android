package cn.bandu.oreader.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import cn.bandu.oreader.fragments.MainListViewFragment;

public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

    private final static String TAG = MainViewPagerAdapter.class.getSimpleName();

    protected static final String[] CONTENT = new String[]{"语文", "数学", "化学", "物理", "政治", "天文", "语文", "数学", "化学", "物理", "政治", "天文", "地理"};
    private int mCount = CONTENT.length;


    public MainViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "get Item: " + position);
        MainListViewFragment mainListViewFragment = new MainListViewFragment();
        mainListViewFragment.setContent(CONTENT[position % CONTENT.length]);
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
