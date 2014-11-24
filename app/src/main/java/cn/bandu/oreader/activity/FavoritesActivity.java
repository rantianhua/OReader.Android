package cn.bandu.oreader.activity;

import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cn.bandu.oreader.R;
import cn.bandu.oreader.fragments.MainListViewFragment_;
import cn.bandu.oreader.model.ListDo;


/**
 * Created by yangmingfu on 14/11/24.
 */
@Fullscreen
@EActivity(R.layout.activity_favorites)
public class FavoritesActivity extends FragmentActivity implements MainListViewFragment_.LoadDatasListener{
    private MainListViewFragment_ mainListViewFragment;

    @ViewById
    TextView title;
    private Cursor cursor;

    @AfterViews
    public void afterViews() {
        title.setText("我的收藏");

        mainListViewFragment = new MainListViewFragment_();
        mainListViewFragment.setLoadDatasListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.favList, mainListViewFragment).commit();
    }

    @Click
    public void backTextView() {
        this.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void refreshData(List<ListDo> datas) {
        loadData(datas);
    }

    @Override
    public void loadData(List<ListDo> datas) {
        Random random = new Random();
        //重构datas，将新数据放到上面
        for (int j=0 ; j<2; j++) {
            ListDo listDo = new ListDo();
            listDo.setTitle("在流行歌曲还被认为是“靡靡之音”的时候 item - " + j);
            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd");
            listDo.setCreateTime(sfd.format(new Date()));
            int tmp = random.nextInt(20) % 4;
            listDo.setDescription("著名歌唱家王昆去世。上世纪80年代，在流行歌曲还被认为是“靡靡之音”的时候，她就曾力挺流行乐。歌很能打动人，所以我就批准他唱");
            listDo.setWebUrl("http://m2.people.cn/r/MV80XzEzMjI1NTBfODNfMTQxNjU1ODc0Ng==");

            listDo.setImageUrls(new ArrayList<String>());
            listDo.setModel(tmp);
            listDo.getImageUrls().add("http://ww3.sinaimg.cn/thumbnail/7d47b003jw1emgmzkh5wsj20c3085ab3.jpg?" + random.nextInt(20));
            listDo.getImageUrls().add("http://ww2.sinaimg.cn/thumbnail/60718250jw1emida7kpogj20fa0ahwfc.jpg?" + random.nextInt(20));
            listDo.getImageUrls().add("http://ww2.sinaimg.cn/bmiddle/d0513221jw1emicwxta35j20dw0993zk.jpg?" + random.nextInt(20));

            datas.add(listDo);
        }
    }
}