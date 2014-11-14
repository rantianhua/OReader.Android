package cn.bandu.oreader.fragments;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import cn.bandu.oreader.R;
import cn.bandu.oreader.activity.MainActivity_;


/**
 * Created by wanghua on 14/11/11.
 */
@EFragment(R.layout.fragment_item_view)
public class ItemViewFragment extends Fragment {

    final static String TAG = ItemViewFragment.class.getSimpleName();

    @ViewById
    ImageView imgView;

    @AfterViews
    void afterViews() {
        Log.e(TAG, "loading img");
        Picasso.with(this.getActivity()).load("http://i.imgur.com/DvpvklR.png").into(imgView);
    }

    public void onBackPressed() {
        MainActivity_ a = (MainActivity_) this.getActivity();
        a.hideItemView();
    }

    @Click
    public void root() {
        MainActivity_ a = (MainActivity_) this.getActivity();
        a.hideItemView();
    }
}
