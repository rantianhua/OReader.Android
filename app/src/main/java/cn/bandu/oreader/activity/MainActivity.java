package cn.bandu.oreader.activity;


import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Window;

import com.viewpagerindicator.TabPageIndicator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.WindowFeature;

import cn.bandu.oreader.R;
import cn.bandu.oreader.adapter.MainViewPagerAdapter;
import cn.bandu.oreader.fragments.ItemViewFragment_;
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
