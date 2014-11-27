package cn.bandu.oreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import cn.bandu.oreader.R;
import cn.bandu.oreader.dao.Fav;

/**
 * Created by yangmingfu on 14-11-11.
 */

public class ListAdapter extends BaseAdapter {

    private Context myContext;
    private List<Fav> datas;

    private LayoutInflater mInflater = null;

    public ListAdapter(Context context, List<Fav> datas) {
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        switch (datas.get(i).getModel()) {
            case 0 : convertView = initTextView(i, convertView);break;
            case 1 : convertView = initImgTextView(i, convertView);break;
            case 2 : convertView = initImgsTextView(i, convertView);break;
            case 3 : convertView = initLargeImgView(i, convertView);break;
            default: convertView = initTextView(i, convertView);break;
        }
        return convertView;
    }

    private View initTextView(int i, View convertView) {
        ViewTextHolder viewTextHolder = null;
        if (null == convertView) {
            viewTextHolder = new ViewTextHolder();
            convertView = mInflater.inflate(R.layout.fragment_item_text, null);
            viewTextHolder.title = (TextView) convertView
                    .findViewById(R.id.title);
            viewTextHolder.description = (TextView) convertView
                    .findViewById(R.id.description);
            viewTextHolder.createTime = (TextView) convertView
                    .findViewById(R.id.createTime);
            convertView.setTag(viewTextHolder);
        } else {
            viewTextHolder = (ViewTextHolder) convertView.getTag();
        }

        Fav fav = datas.get(i);
        if (null != fav) {
            viewTextHolder.title.setText(fav.getTitle());
            if (fav.getDescription() == "" || fav.getDescription() == null) {
                viewTextHolder.description.setVisibility(View.GONE);
            } else {
                viewTextHolder.description.setVisibility(View.VISIBLE);
                viewTextHolder.description.setText(fav.getDescription());
            }
            viewTextHolder.createTime.setText((CharSequence) fav.getDate());
        }
        return convertView;
    }

    private View initImgTextView(int i, View convertView) {
        ViewImageTextHolder viewImageTextHolder = null;
        if (null == convertView) {
            viewImageTextHolder = new ViewImageTextHolder();
            convertView = mInflater.inflate(R.layout.fragment_item_img_text, null);
            viewImageTextHolder.title = (TextView) convertView
                    .findViewById(R.id.title);
            viewImageTextHolder.description = (TextView) convertView
                    .findViewById(R.id.description);
            viewImageTextHolder.createTime = (TextView) convertView
                    .findViewById(R.id.createTime);
            viewImageTextHolder.image0 = (ImageView) convertView
                    .findViewById(R.id.right_image);
            convertView.setTag(viewImageTextHolder);
        } else {
            viewImageTextHolder = (ViewImageTextHolder) convertView.getTag();
        }

        Fav fav = datas.get(i);
        if (null != fav) {
            viewImageTextHolder.title.setText(fav.getTitle());
            if (fav.getDescription() == "" || fav.getDescription() == null) {
                viewImageTextHolder.description.setVisibility(View.GONE);
            } else {
                viewImageTextHolder.description.setVisibility(View.VISIBLE);
                viewImageTextHolder.description.setText(fav.getDescription());
            }
            viewImageTextHolder.createTime.setText(fav.getDate());
            Picasso.with(myContext)
                    .load(fav.getImage0())
                    .placeholder(R.drawable.small_image_holder_listpage_loading)
                    .error(R.drawable.small_image_holder_listpage_loading)
                    .into(viewImageTextHolder.image0);
        }
        return convertView;
    }

    private View initImgsTextView(int i, View convertView) {
        ViewImagesHolder viewImagesHolder = null;
        if (null == convertView) {
            viewImagesHolder = new ViewImagesHolder();
            convertView = mInflater.inflate(R.layout.fragment_item_imgs_text, null);

            viewImagesHolder.title = (TextView) convertView
                    .findViewById(R.id.title);
            viewImagesHolder.description = (TextView) convertView
                    .findViewById(R.id.description);
            viewImagesHolder.createTime = (TextView) convertView
                    .findViewById(R.id.createTime);
            viewImagesHolder.image0 = (ImageView) convertView
                    .findViewById(R.id.item_image_0);
            viewImagesHolder.image1 = (ImageView) convertView
                    .findViewById(R.id.item_image_1);
            viewImagesHolder.image2 = (ImageView) convertView
                    .findViewById(R.id.item_image_2);
            convertView.setTag(viewImagesHolder);
        } else {
            viewImagesHolder = (ViewImagesHolder) convertView.getTag();
        }

        Fav fav = datas.get(i);

        if (null != fav) {
            viewImagesHolder.title.setText(fav.getTitle());
            if (fav.getDescription() == "" || fav.getDescription() == null) {
                viewImagesHolder.description.setVisibility(View.GONE);
            } else {
                viewImagesHolder.description.setVisibility(View.VISIBLE);
                viewImagesHolder.description.setText(fav.getDescription());
            }
            viewImagesHolder.createTime.setText(fav.getDate());
            Picasso.with(myContext)
                    .load(fav.getImage0())
                    .placeholder(R.drawable.small_image_holder_listpage_loading)
                    .error(R.drawable.small_image_holder_listpage_loading)
                    .into(viewImagesHolder.image0);
            Picasso.with(myContext)
                    .load(fav.getImage1())
                    .placeholder(R.drawable.small_image_holder_listpage_loading)
                    .error(R.drawable.small_image_holder_listpage_loading)
                    .into(viewImagesHolder.image1);
            Picasso.with(myContext)
                    .load(fav.getImage2())
                    .placeholder(R.drawable.small_image_holder_listpage_loading)
                    .error(R.drawable.small_image_holder_listpage_loading)
                    .into(viewImagesHolder.image2);
        }
        return convertView;
    }

    private View initLargeImgView(int i, View convertView) {
        ViewLargeImageTextHolder viewLargeImageTextHolder = null;
        if (null == convertView) {
            viewLargeImageTextHolder = new ViewLargeImageTextHolder();
            convertView = mInflater.inflate(R.layout.fragment_item_large_img, null);
            viewLargeImageTextHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewLargeImageTextHolder.description = (TextView) convertView.findViewById(R.id.description);
            viewLargeImageTextHolder.createTime = (TextView) convertView.findViewById(R.id.createTime);
            viewLargeImageTextHolder.image0 = (ImageView) convertView.findViewById(R.id.large_image);
            convertView.setTag(viewLargeImageTextHolder);
        } else {
            viewLargeImageTextHolder = (ViewLargeImageTextHolder) convertView.getTag();
        }

        Fav fav = datas.get(i);
        if (null != fav) {
            ((TextView) convertView.findViewById(R.id.title)).setText(fav.getTitle());
            if (fav.getDescription() == ""  || fav.getDescription() == null) {
                ((TextView) convertView.findViewById(R.id.description)).setVisibility(View.GONE);
            } else {
                viewLargeImageTextHolder.description.setVisibility(View.VISIBLE);
                viewLargeImageTextHolder.description.setText(fav.getDescription());
            }
            viewLargeImageTextHolder.createTime.setText(fav.getDate());
            Picasso.with(myContext)
                    .load(fav.getImage0())
                    .placeholder(R.drawable.small_image_holder_listpage_loading)
                    .error(R.drawable.small_image_holder_listpage_loading)
                    .into(viewLargeImageTextHolder.image0);
        }


        return convertView;
    }

    private static class ViewImagesHolder {
        TextView title;
        TextView description;
        TextView createTime;
        ImageView image0;
        ImageView image1;
        ImageView image2;
    }

    private static class ViewImageTextHolder {
        TextView title;
        TextView description;
        TextView createTime;
        ImageView image0;
    }

    private static class ViewLargeImageTextHolder {
        TextView title;
        TextView description;
        TextView createTime;
        ImageView image0;
    }

    private static class ViewTextHolder {
        TextView title;
        TextView description;
        TextView createTime;
    }
}
