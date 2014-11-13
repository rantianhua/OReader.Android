package cn.bandu.oreader.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;

import com.viewpagerindicator.TabPageIndicator;


public class CustomTabPageIndicator extends TabPageIndicator {

    private final static String TAG = CustomTabPageIndicator.class.getSimpleName();

    private final int SCROLL_LEFT_EDGE = 1;
    private final int SCROLL_RIGHT_EDGE = 2;

    private boolean isScrolling;

    private ViewPager pager;

    public CustomTabPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTabPageIndicator(Context context) {
        super(context);
    }

    public void setViewPager(ViewPager pager)
    {
        super.setViewPager(pager);
        this.pager = pager;
    }

    @Override
    public void onPageScrollStateChanged(int state){
        isScrolling = (state != 0);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        switch (scrollEdage(position, positionOffset, positionOffsetPixels)) {
            case(SCROLL_LEFT_EDGE):
                //TODO ...
                Log.e(TAG, "left");
                break;
            case(SCROLL_RIGHT_EDGE):
                //TODO ...
                Log.e(TAG, "right");
                break;
            default:
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                break;
        }
    }

    private int scrollEdage(int position, float positionOffset, int positionOffsetPixels) {
        if ((position <= 0 ) && (positionOffset <= 0f) && (positionOffsetPixels <= 0) && isScrolling) {
            return SCROLL_LEFT_EDGE;
        }
        if ((position == this.pager.getChildCount() - 1 ) && (positionOffset <= 0f) && (positionOffsetPixels <= 0) && isScrolling) {
            return SCROLL_RIGHT_EDGE;
        }
        return 0;
    }

    public void onPageSelected(int position){
        Log.e(TAG, "selcted: " + position);
        super.onPageSelected(position);
    }




}
