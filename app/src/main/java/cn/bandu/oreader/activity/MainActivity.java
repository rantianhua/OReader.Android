package cn.bandu.oreader.activity;


import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import cn.bandu.oreader.R;
import cn.bandu.oreader.adapter.MainViewPagerAdapter;
import cn.bandu.oreader.fragments.SlidingMenuFragment_;
import cn.bandu.oreader.view.CustomTabPageIndicator;

//@WindowFeature({Window.FEATURE_NO_TITLE, Window.FEATURE_INDETERMINATE_PROGRESS})
//@Fullscreen
@EActivity(R.layout.activity_main)
public class MainActivity extends FragmentActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @ViewById
    ViewPager mainPager;
    @ViewById
    CustomTabPageIndicator tabTitle;

    long lastBackPressed = 0;
    Toast quitToast;

    SlidingMenuFragment_ menuFragment;
    SlidingMenu menu;

    MainViewPagerAdapter mainPagerAdapter;

    @AfterViews
    public void afterViews() {
        mainPagerAdapter = new MainViewPagerAdapter(this.getSupportFragmentManager(), this);
        mainPager.setAdapter(mainPagerAdapter);
        Log.e("cate name", mainPagerAdapter.CATEGORY.get(0).getName());
        if(mainPagerAdapter.getCount() < 2) {
            //TODO only one pager, we should hide indicator.
//            tabTitle.setBackgroundResource(R.drawable.tab_indicator_none);
        }
        tabTitle.setViewPager(mainPager);
        initSlidingMenu();
    }

    private void initSlidingMenu() {
        menuFragment = new SlidingMenuFragment_();
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.RIGHT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        menu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.fragment_sliding_menu);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.slidingMenu, menuFragment).commit();
    }


    public void onBackPressed() {
        if (menu.isMenuShowing()) {
            menu.toggle();
            menu.findViewById(R.id.processBar).setVisibility(View.GONE);
            menu.findViewById(R.id.msg_btn).setClickable(true);
            menu.findViewById(R.id.feedback_btn).setClickable(true);
            return;
        }
        long now = System.currentTimeMillis();
        if((now - lastBackPressed) < 2000) {
            if(null != quitToast) {
                Log.e(TAG, "cancel toast");
                quitToast.cancel();
                quitToast = null;
            }
            super.onBackPressed();
        } else {
            lastBackPressed = now;
            Log.e(TAG, "last back pressed: " + now);
            quitToast = Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT);
            quitToast.show();
        }
    }

    @Click
    public void moreBtn() {
        if (menu.isMenuShowing()) {
            menu.toggle();
            return;
        }
        menu.showMenu();
    }
}
