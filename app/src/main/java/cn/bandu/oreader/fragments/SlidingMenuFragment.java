package cn.bandu.oreader.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

import cn.bandu.oreader.R;
import cn.bandu.oreader.activity.FavoritesActivity_;

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

    }

    @Click
    public void about_btn() {

    }

    @Click
    public void feedback_btn() {

    }
}
