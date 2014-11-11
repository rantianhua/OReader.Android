package cn.bandu.oreader.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import cn.bandu.oreader.R;
import cn.bandu.oreader.activity.MainActivity_;

@EFragment(R.layout.fragment_main_view)
public class MainViewFragment extends Fragment {

    private final static String TAG  =  MainViewFragment.class.getSimpleName();

    MainActivity_ activity;

    String mContent;

    @ViewById
    TextView mainViewText;

    @ViewById
    Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Starting Main View Fragment");
        super.onCreate(savedInstanceState);
        activity = (MainActivity_) getActivity();
        Log.d(TAG, "constructor content" + mContent);
    }

    @AfterViews
    public void afterViews() {
        Log.d(TAG, "set content" + mContent);
        mainViewText.setText(mContent);
    }

    @Click
    public void buttonClicked() {
        Log.e(TAG, "clicked");
        activity.showItemView();
    }


    public void setContent(String content) {
        this.mContent = content;
    }
}