package devin.com.picturepicker.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.List;

import devin.com.picturepicker.R;
import devin.com.picturepicker.javabean.PictureItem;
import devin.com.picturepicker.view.ZoomImageView;

/**

 * <p>   Created by Devin Sun on 2016/10/16.
 * <p>   版权@2016北京优闲科技发展有限公司 保留所有权
 */

public class PicturePreviewAdapter extends PagerAdapter {


    private Context context;
    private List<PictureItem> pictureItemList;
    private OnPictureClickListener listener;


    public PicturePreviewAdapter(List<PictureItem> pictureItemList, Context context) {
        this.pictureItemList = pictureItemList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return pictureItemList == null ? 0 : pictureItemList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ZoomImageView pictureView = new ZoomImageView(context);
        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pictureView.setLayoutParams(layoutParams);

        Glide.with(context)
                .load(pictureItemList.get(position).pictureAbsPath)
                .placeholder(R.drawable.default_picture)
                .error(R.drawable.default_picture)
                .into(pictureView);

        if (listener != null) {
            pictureView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPictureClick();
                }
            });
        }

        container.addView(pictureView);

        return pictureView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void finishUpdate(ViewGroup container) {
    }

    public void setOnPictureClickListener(OnPictureClickListener listener) {
        this.listener = listener;
    }


    public interface OnPictureClickListener {

         void onPictureClick();

    }

}
