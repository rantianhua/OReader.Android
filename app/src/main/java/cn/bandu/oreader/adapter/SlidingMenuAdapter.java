package cn.bandu.oreader.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import cn.bandu.oreader.R;

/**
 * Created by yangmingfu on 14/11/13.
 */
public class SlidingMenuAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater mInflater;

    public SlidingMenuAdapter(Activity activity) {
        this.activity = activity;
        mInflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View convertView = mInflater.inflate(R.layout.fragment_sliding_menu_item, null);
        return convertView;
    }
}
