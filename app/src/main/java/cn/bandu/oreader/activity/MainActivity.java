package cn.bandu.oreader.activity;


import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import cn.bandu.oreader.R;
import cn.bandu.oreader.adapter.MainViewPagerAdapter;
import cn.bandu.oreader.fragments.ItemViewFragment_;
import cn.bandu.oreader.fragments.SlidingMenuFragment_;
import cn.bandu.oreader.view.CustomTabPageIndicator;

//@WindowFeature({Window.FEATURE_NO_TITLE, Window.FEATURE_INDETERMINATE_PROGRESS})
@Fullscreen
@EActivity(R.layout.layout_main)
public class MainActivity extends FragmentActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @ViewById
    ViewPager mainPager;
    @ViewById
    CustomTabPageIndicator tabTitle;

    FragmentManager sm = getSupportFragmentManager();
    ItemViewFragment_ itemViewFragment;
    boolean onViewItem;
    SlidingMenuFragment_ menuFragment;
    SlidingMenu menu;

    MainViewPagerAdapter mainPagerAdapter;

    @AfterViews
    public void afterViews() {
        mainPagerAdapter = new MainViewPagerAdapter(this.getSupportFragmentManager());
        mainPager.setAdapter(mainPagerAdapter);
        if(mainPagerAdapter.getCount() < 2) {
            //TODO only one pager, we should hide indicator.
        }
        tabTitle.setViewPager(mainPager);
        //初始化滑动菜单
        initSlidingMenu();
    }

    private void initSlidingMenu() {
        menuFragment = new SlidingMenuFragment_();
        menu = new SlidingMenu(this);

        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//        menu.setShadowDrawable(R.drawable.sliding_shadow);
        menu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0f);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.layout_sliding_menu);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sliding_menu, menuFragment).commit();
    }


    public void onBackPressed() {
       if(onViewItem) {
           removeItemView();
       } else {
           super.onBackPressed();
       }

    }

    public void showSlidingMenu() {
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
    }

    public void hideSlidingMenu() {
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
    }

    public void showItemView() {
        if (null == itemViewFragment) {
            itemViewFragment = new ItemViewFragment_();
            sm.beginTransaction().replace(R.id.itemView, itemViewFragment).commit();
        } else {
            sm.beginTransaction().show(itemViewFragment).commit();
        }
        //TODO detail page show function init with web view,show a web page
        onViewItem = true;
    }

    public void hideItemView() {
        getSupportFragmentManager().beginTransaction().hide(itemViewFragment).commit();
        onViewItem = false;
    }

    public void removeItemView() {
        getSupportFragmentManager().beginTransaction().remove(itemViewFragment).commit();
        itemViewFragment = null;
        onViewItem = false;
    }
}
