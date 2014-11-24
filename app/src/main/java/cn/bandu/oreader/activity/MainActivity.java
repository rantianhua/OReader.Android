package cn.bandu.oreader.activity;


import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cn.bandu.oreader.R;
import cn.bandu.oreader.adapter.MainViewPagerAdapter;
import cn.bandu.oreader.fragments.MainListViewFragment_;
import cn.bandu.oreader.fragments.SlidingMenuFragment_;
import cn.bandu.oreader.dao.Fav;
import cn.bandu.oreader.view.CustomTabPageIndicator;

//@WindowFeature({Window.FEATURE_NO_TITLE, Window.FEATURE_INDETERMINATE_PROGRESS})
@Fullscreen
@EActivity(R.layout.activity_main)
public class MainActivity extends FragmentActivity implements MainListViewFragment_.LoadDatasListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    @ViewById
    ViewPager mainPager;
    @ViewById
    CustomTabPageIndicator tabTitle;

    long lastBackPressed = 0;
    Toast quitToast;

    FragmentManager sm = getSupportFragmentManager();
    SlidingMenuFragment_ menuFragment;
    SlidingMenu menu;

    MainViewPagerAdapter mainPagerAdapter;

    @AfterViews
    public void afterViews() {
        mainPagerAdapter = new MainViewPagerAdapter(this.getSupportFragmentManager(), this);
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
        menu.setMenu(R.layout.fragment_sliding_menu);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.slidingMenu, menuFragment).commit();
    }


    public void onBackPressed() {
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

    public void showSlidingMenu() {
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
    }

    public void hideSlidingMenu() {
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
    }


    @Override
    public void refreshData(List<Fav> datas) {
        Random random = new Random();
        List<Fav> datasTmp = new ArrayList<Fav>();
        //重构datas，将新数据放到上面
        for (int j=0 ; j<2; j++) {
            Fav fav = new Fav();
            fav.setTitle("在流行歌曲还被认为是“靡靡之音”的时候 item - " + j);
            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd");
            fav.setDate(sfd.format(new Date()));
            int tmp = random.nextInt(20) % 4;
            fav.setModel(tmp);
            fav.setDescription("著名歌唱家王昆去世。上世纪80年代，在流行歌曲还被认为是“靡靡之音”的时候，她就曾力挺流行乐。歌很能打动人，所以我就批准他唱");
            fav.setWebUrl("http://m2.people.cn/r/MV80XzEzMjI1NTBfODNfMTQxNjU1ODc0Ng==");

            fav.setImage0("http://ww3.sinaimg.cn/thumbnail/7d47b003jw1emgmzkh5wsj20c3085ab3.jpg?" + random.nextInt(20));
            fav.setImage1("http://ww2.sinaimg.cn/thumbnail/60718250jw1emida7kpogj20fa0ahwfc.jpg?" + random.nextInt(20));
            fav.setImage2("http://ww2.sinaimg.cn/bmiddle/d0513221jw1emicwxta35j20dw0993zk.jpg?" + random.nextInt(20));

            datasTmp.add(fav);
        }
        for (int m=datas.size()-1;m>=0;m--){
            datasTmp.add(datas.get(m));
        }
        datas.clear();
        for (int m=0;m<datasTmp.size();m++){
            datas.add(datasTmp.get(m));
        }

    }

    @Override
    public void loadData(List<Fav> datas) {
        Random random = new Random();
        //重构datas，将新数据放到上面
        for (int j=0 ; j<2; j++) {
            Fav fav = new Fav();
            fav.setTitle("在流行歌曲还被认为是“靡靡之音”的时候 item - " + j);
            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd");
            fav.setDate(sfd.format(new Date()));
            int tmp = random.nextInt(20) % 4;
            fav.setModel(tmp);
            fav.setDescription("著名歌唱家王昆去世。上世纪80年代，在流行歌曲还被认为是“靡靡之音”的时候，她就曾力挺流行乐。歌很能打动人，所以我就批准他唱");
            fav.setWebUrl("http://m2.people.cn/r/MV80XzEzMjI1NTBfODNfMTQxNjU1ODc0Ng==");

            fav.setImage0("http://ww3.sinaimg.cn/thumbnail/7d47b003jw1emgmzkh5wsj20c3085ab3.jpg?" + random.nextInt(20));
            fav.setImage1("http://ww2.sinaimg.cn/thumbnail/60718250jw1emida7kpogj20fa0ahwfc.jpg?" + random.nextInt(20));
            fav.setImage2("http://ww2.sinaimg.cn/bmiddle/d0513221jw1emicwxta35j20dw0993zk.jpg?" + random.nextInt(20));

            datas.add(fav);
        }
    }
}
