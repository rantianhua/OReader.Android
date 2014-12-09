package cn.bandu.oreader.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

import cn.bandu.oreader.OReaderConst;
import cn.bandu.oreader.R;
import cn.bandu.oreader.activity.DetailActivity_;
import cn.bandu.oreader.activity.FavoritesActivity_;
import cn.bandu.oreader.dao.Fav;

/**
 * Created by yangmingfu on 14/11/13.
 */
@EFragment(R.layout.fragment_sliding_menu)
public class SlidingMenuFragment extends Fragment{

    @AfterViews
    public void afterViews() {

    }

    @Click
    public void favorite_btn() {

        Intent intent = new Intent();
        intent.setClass(this.getActivity(), FavoritesActivity_.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);

        this.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    @Click
    public void clear_cacahe_btn() {
        new AlertDialog.Builder(getActivity()).setTitle("确认清除缓存吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //TODO 删除缓存
                Toast.makeText(getActivity(), "清除成功", Toast.LENGTH_LONG).show();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        }).show();
    }

    @Click
    public void about_btn() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        Fav data = new Fav();
        data.setSid(0);
        data.setWebUrl(OReaderConst.ABOUT_URL);
        data.setCateName("关于我们");
        bundle.putSerializable("data", data);
        intent.putExtras(bundle);
        intent.setClass(getActivity(), DetailActivity_.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        this.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Click
    public void feedback_btn() {

    }
}
