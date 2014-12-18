package cn.bandu.oreader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Fullscreen;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import cn.bandu.oreader.R;
import cn.bandu.oreader.adapter.ListAdapter;
import cn.bandu.oreader.dao.ArticleList;
import cn.bandu.oreader.dao.Cate;
import cn.bandu.oreader.dao.Fav;
import cn.bandu.oreader.tools.DataTools;


/**
 * Created by yangmingfu on 14/11/24.
 */
@Fullscreen
@EActivity(R.layout.activity_favorites)
public class FavoritesActivity extends FragmentActivity {

    @ViewById
    TextView title;
    @ViewById
    View notice;

    private ListAdapter adapter;

    private List<ArticleList> datas = new ArrayList<ArticleList>();

    private List<Cate> cates = new ArrayList<Cate>();

    private List<Fav> fav;

    @ViewById
    ListView favList;
    @ViewById
    TextView editBtn;
    @ViewById
    TextView fontSize;

    int flag = 0;

    public FavoritesActivity() {
    }


    @AfterViews
    public void afterViews() {
        editBtn.setVisibility(View.VISIBLE);
        fontSize.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        title.setText("我的收藏");
        fav = DataTools.getFavList(this);
        int count = fav.size();
        datas.clear();
        for(int i=0;i<count;i++) {
            ArticleList articleList = DataTools.favToArticleList(fav.get(i));
            datas.add(articleList);
            cates.add(new Cate(fav.get(i).getCateid(), fav.get(i).getCateName(), 0, 0));
        }

        adapter = new ListAdapter(this, datas);
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
        Log.e("position", String.valueOf(position));

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", datas.get(position));
        bundle.putSerializable("cate", cates.get(position));
        intent.putExtras(bundle);

        intent.setClass(this, DetailActivity_.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

    }

    @Click
    public void editBtn() {
        flag = (flag+1)%2;
        int count = favList.getChildCount();
        int visible = flag == 1 ?  View.VISIBLE : View.GONE;
          for (int i=0;i<count;i++) {
            favList.getChildAt(i).findViewById(R.id.conDel).setVisibility(visible);
        }
    }
}