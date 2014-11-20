package cn.bandu.oreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import cn.bandu.oreader.R;

/**
 * Created by yangmingfu on 14-11-11.
 */

public class ListAdapter extends BaseAdapter {

    private Context myContext;
    private ArrayList datas;

    private LayoutInflater mInflater = null;

    TextView title;

    public ListAdapter(Context context, ArrayList datas) {
        this.myContext = context;
        this.datas = datas;
        mInflater = (LayoutInflater) myContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return datas.size();
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
        View v = null;
        Random random = new Random();
        switch (random.nextInt(20) % 4) {
            case 0 : v = initTextView(i);break;
            case 1 : v = initImgTextView(i);break;
            case 2 : v = initImgsTextView(i);break;
            case 3 : v = initImgView(i);break;
        }
        return v;
    }

    private View initTextView(int i) {
        View convertView = mInflater.inflate(R.layout.fragment_item_text, null);
        title = (TextView) convertView.findViewById(R.id.title);
        title.setText((String)datas.get(i));
        return convertView;
    }

    private View initImgTextView(int i) {
        View convertView = mInflater.inflate(R.layout.fragment_item_img_text, null);
        title = (TextView) convertView.findViewById(R.id.title);
        title.setText((String)datas.get(i));
        return convertView;
    }

    private View initImgsTextView(int i) {
        View convertView = mInflater.inflate(R.layout.fragment_item_imgs_text, null);
        title = (TextView) convertView.findViewById(R.id.title);
        title.setText((String)datas.get(i));
        return convertView;
    }

    private View initImgView(int i) {
        View convertView = mInflater.inflate(R.layout.fragment_item_large_img, null);
        title = (TextView) convertView.findViewById(R.id.title);
        title.setText((String)datas.get(i));
//        ImageView imageView = (ImageView) convertView.findViewById(R.id.img);
//        imageView.setImageResource();
        return convertView;
    }
}
