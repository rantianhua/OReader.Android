package cn.bandu.oreader.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import cn.bandu.oreader.OReaderConst;
import cn.bandu.oreader.fragments.MainListViewFragment_;

public class MainViewPagerAdapter extends FragmentStatePagerAdapter {

    private final static String TAG = MainViewPagerAdapter.class.getSimpleName();


    private int mCount = OReaderConst.CONTENT.size();
    Activity activity;

    public MainViewPagerAdapter(FragmentManager fm, Activity activity) {
        super(fm);
        this.activity = activity;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "get Item: " + position);
        MainListViewFragment_ mainListViewFragment = new MainListViewFragment_();
        mainListViewFragment.setContent(OReaderConst.CONTENT.get(position % OReaderConst.CONTENT.size()));
        return mainListViewFragment;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return OReaderConst.CONTENT.get(position % OReaderConst.CONTENT.size()).getName();
    }
}
