package cn.bandu.oreader.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import cn.bandu.oreader.OReaderApplication;
import cn.bandu.oreader.R;
import cn.bandu.oreader.dao.ArticleList;
import cn.bandu.oreader.dao.FavDao;

/**
 * Created by yangmingfu on 14-11-11.
 */
public class ListAdapter extends BaseAdapter {

    private Context myContext;
    private List<ArticleList> datas;

    ImageLoader imageLoader = OReaderApplication.getInstance().getImageLoader();

    private LayoutInflater mInflater = null;

    public ListAdapter(Context context, List<ArticleList> datas) {
        this.myContext = context;
        setDatas(datas);
        mInflater = (LayoutInflater) myContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setDatas(List<ArticleList> datas) {
        this.datas = (List<ArticleList>) datas;
    }

    public List<ArticleList> getDatas() {
        return datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return datas.get(position).getModel();
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        Log.i("image model=", String.valueOf(datas.get(i).getModel()));
        switch (getItemViewType(i)) {
            case 0 : convertView = initTextView(i, convertView);break;
            case 1 : convertView = initImgTextView(i, convertView);break;
            case 2 : convertView = initLargeImgView(i, convertView);break;
            case 3 : convertView = initImgsTextView(i, convertView);break;
            default: convertView = initTextView(i, convertView);break;
        }
        convertView.findViewById(R.id.conDel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FavDao favDao = OReaderApplication.getDaoSession(myContext, 1).getFavDao();
                favDao.deleteByKey(datas.get(i).getSid());
                datas.remove(i);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    private View initTextView(int i, View convertView) {
        ViewHolder ViewHolder = null;
        if (null == convertView) {
            ViewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.fragment_item_text, null);
            ViewHolder.title = (TextView) convertView
                    .findViewById(R.id.title);
            ViewHolder.description = (TextView) convertView
                    .findViewById(R.id.description);
            ViewHolder.createTime = (TextView) convertView
                    .findViewById(R.id.createTime);
            convertView.setTag(ViewHolder);
        } else {
            ViewHolder = (ViewHolder) convertView.getTag();
        }

        ArticleList articleList = datas.get(i);
        if (null != articleList) {
            ViewHolder.title.setText(articleList.getTitle());
            if (articleList.getDescription() == "" || articleList.getDescription() == null) {
                ViewHolder.description.setVisibility(View.GONE);
            } else {
                ViewHolder.description.setVisibility(View.VISIBLE);
                ViewHolder.description.setText(articleList.getDescription());
            }
            ViewHolder.createTime.setText((CharSequence) articleList.getDate());
        }
        return convertView;
    }

    private View initImgTextView(int i, View convertView) {
        ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.fragment_item_img_text, null);
            viewHolder.title = (TextView) convertView
                    .findViewById(R.id.title);
            viewHolder.description = (TextView) convertView
                    .findViewById(R.id.description);
            viewHolder.createTime = (TextView) convertView
                    .findViewById(R.id.createTime);
            viewHolder.image0 = (NetworkImageView) convertView
                    .findViewById(R.id.right_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ArticleList articleList = datas.get(i);
        if (null != articleList) {
            viewHolder.title.setText(articleList.getTitle());
            if (articleList.getDescription() == "" || articleList.getDescription() == null) {
                viewHolder.description.setVisibility(View.GONE);
            } else {
                viewHolder.description.setVisibility(View.VISIBLE);
                viewHolder.description.setText(articleList.getDescription());
            }

            viewHolder.createTime.setText(articleList.getDate());
            viewHolder.image0.setDefaultImageResId(R.drawable.small_pic_loading);
            viewHolder.image0.setErrorImageResId(R.drawable.small_load_png_failed);
            viewHolder.image0.setImageUrl(articleList.getImage0(), imageLoader);
        }
        return convertView;
    }

    private View initImgsTextView(int i, View convertView) {
        ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.fragment_item_imgs_text, null);

            viewHolder.title = (TextView) convertView
                    .findViewById(R.id.title);
            viewHolder.description = (TextView) convertView
                    .findViewById(R.id.description);
            viewHolder.createTime = (TextView) convertView
                    .findViewById(R.id.createTime);
            viewHolder.image0 = (NetworkImageView) convertView
                    .findViewById(R.id.item_image_0);
            viewHolder.image1 = (NetworkImageView) convertView
                    .findViewById(R.id.item_image_1);
            viewHolder.image2 = (NetworkImageView) convertView
                    .findViewById(R.id.item_image_2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ArticleList articleList = datas.get(i);

        if (null != articleList) {
            viewHolder.title.setText(articleList.getTitle());
            viewHolder.description.setVisibility(View.GONE);
            viewHolder.createTime.setText(articleList.getDate());
            viewHolder.image0.setDefaultImageResId(R.drawable.small_pic_loading);
            viewHolder.image0.setErrorImageResId(R.drawable.small_load_png_failed);
            viewHolder.image0.setImageUrl(articleList.getImage0(), imageLoader);
            viewHolder.image1.setDefaultImageResId(R.drawable.small_pic_loading);
            viewHolder.image1.setErrorImageResId(R.drawable.small_load_png_failed);
            viewHolder.image1.setImageUrl(articleList.getImage1(), imageLoader);
            viewHolder.image2.setDefaultImageResId(R.drawable.small_pic_loading);
            viewHolder.image2.setErrorImageResId(R.drawable.small_load_png_failed);
            viewHolder.image2.setImageUrl(articleList.getImage2(), imageLoader);
        }
        return convertView;
    }

    private View initLargeImgView(int i, View convertView) {
        ViewHolder viewHolder = null;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.fragment_item_large_img, null);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.description = (TextView) convertView.findViewById(R.id.description);
            viewHolder.createTime = (TextView) convertView.findViewById(R.id.createTime);
            viewHolder.image0 = (NetworkImageView) convertView.findViewById(R.id.large_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ArticleList articleList = datas.get(i);
        if (null != articleList) {
            ((TextView) convertView.findViewById(R.id.title)).setText(articleList.getTitle());
            ((TextView) convertView.findViewById(R.id.description)).setVisibility(View.GONE);
            viewHolder.createTime.setText(articleList.getDate());
            viewHolder.image0.setDefaultImageResId(R.drawable.small_pic_loading);
            viewHolder.image0.setErrorImageResId(R.drawable.small_load_png_failed);
            viewHolder.image0.setImageUrl(articleList.getImage0(), imageLoader);
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView title;
        TextView description;
        TextView createTime;
        NetworkImageView image0;
        NetworkImageView image1;
        NetworkImageView image2;
    }
}
