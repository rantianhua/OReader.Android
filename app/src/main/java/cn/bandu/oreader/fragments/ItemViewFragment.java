package cn.bandu.oreader.fragments;

import android.support.v4.app.Fragment;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

import cn.bandu.oreader.R;
import cn.bandu.oreader.activity.MainActivity_;


/**
 * Created by wanghua on 14/11/11.
 */
@EFragment(R.layout.fragment_item_view)
public class ItemViewFragment extends Fragment {

    public void onBackPressed() {
        MainActivity_ a = (MainActivity_) this.getActivity();
        a.hideItemView();
    }

    @Click
    public void root(){
        MainActivity_ a = (MainActivity_) this.getActivity();
        a.hideItemView();
    }
}
