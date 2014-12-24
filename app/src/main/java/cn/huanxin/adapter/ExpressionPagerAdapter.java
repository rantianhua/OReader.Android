package cn.huanxin.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by yangmingfu on 14/12/24.
 */
public class ExpressionPagerAdapter extends PagerAdapter {

    private List<View> views;

    public ExpressionPagerAdapter(List<View> views) {
        this.views = views;
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public Object instantiateItem(ViewGroup arg0, int arg1) {
        ((ViewPager) arg0).addView(views.get(arg1));
        return views.get(arg1);
    }

    @Override
    public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView(views.get(arg1));
    }
}
