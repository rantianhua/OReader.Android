package cn.bandu.oreader.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import cn.bandu.oreader.OReaderApplication;
import cn.bandu.oreader.R;
import cn.bandu.oreader.adapter.ListAdapter;
import cn.bandu.oreader.dao.Fav;
import cn.bandu.oreader.dao.FavDao;
import cn.bandu.oreader.fragments.MainListViewFragment_;
import cn.bandu.oreader.tools.DataTools;


/**
 * Created by yangmingfu on 14/11/24.
 */
@Fullscreen
@EActivity(R.layout.activity_favorites)
public class FavoritesActivity extends FragmentActivity {
    private MainListViewFragment_ mainListViewFragment;

    @ViewById
    TextView title;
    @ViewById
    View notice;

    private ListAdapter adapter;

    private List<Fav> datas;
    private FavDao favDao;

    @ViewById
    ListView favList;

    @Override
    protected void onStart() {
        datas = new ArrayList<Fav>();
        title.setText("我的收藏");
        favDao = OReaderApplication.getDaoSession(this).getFavDao();

        SQLiteDatabase db = OReaderApplication.getDaoMaster(this).getDatabase();
        String textColumn = FavDao.Properties.CreateTime.columnName;
        String orderBy = textColumn + " COLLATE LOCALIZED DESC";
        Cursor cursor = db.query(favDao.getTablename(), favDao.getAllColumns(), null, null, null, null, orderBy);

        adapter = new ListAdapter(this, DataTools.cursorToFav(cursor, datas));
        favList.setAdapter(adapter);

        if (datas.size() <=0 ) {
            notice.setVisibility(View.VISIBLE);
            favList.setVisibility(View.GONE);
        } else {
            notice.setVisibility(View.GONE);
            favList.setVisibility(View.VISIBLE);
        }
        super.onStart();
    }

    @Click
    public void backTextView() {
        this.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @ItemClick
    void favList(int position) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", datas.get(position));
        intent.putExtras(bundle);

        intent.setClass(this, DetailActivity_.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);

        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }
}