package cn.bandu.oreader.activity;


import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.viewpagerindicator.TabPageIndicator;

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

@WindowFeature({Window.FEATURE_NO_TITLE, Window.FEATURE_INDETERMINATE_PROGRESS})
@Fullscreen
@EActivity(R.layout.layout_main)
public class MainActivity extends FragmentActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @ViewById
    ViewPager mainPager;
    @ViewById
    CustomTabPageIndicator tabTitle;


    ItemViewFragment_ itemViewFragment;
    boolean onViewItem;

    MainViewPagerAdapter mainPagerAdapter;

    @AfterViews
    public void afterViews() {
        mainPagerAdapter = new MainViewPagerAdapter(this.getSupportFragmentManager());
        mainPager.setAdapter(mainPagerAdapter);
        tabTitle.setViewPager(mainPager);
        //初始化滑动菜单
        initSlidingMenu();
    }

    private void  initSlidingMenu() {
        SlidingMenuFragment_ menuFragment = new SlidingMenuFragment_();
        SlidingMenu menu = new SlidingMenu(this);

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
            hideItemView();
        }
    }

    public void showItemView() {
        //TODO detail page show function init with web view,show a web page
        itemViewFragment = new ItemViewFragment_();
        getSupportFragmentManager().beginTransaction().replace(R.id.itemView, itemViewFragment).commit();
        onViewItem = true;
    }

    public void hideItemView() {
        getSupportFragmentManager().beginTransaction().remove(itemViewFragment).commit();
        onViewItem = false;
    }
}
