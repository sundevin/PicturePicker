package devin.com.picturepicker.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;

import devin.com.picturepicker.javabean.PictureItem;
import devin.com.picturepicker.utils.ImageLoader;
import devin.com.picturepicker.view.ZoomImageView;

/**
 * <p>   Created by Devin Sun on 2016/10/16.
 * <p>   版权@2016北京优闲科技发展有限公司 保留所有权
 */

public class PicturePreviewAdapter extends PagerAdapter {


    private Context context;
    private List<PictureItem> pictureItemList;
    private OnPictureClickListener clickListener;
    private OnPictureLongClickListener longClickListener;


    public PicturePreviewAdapter(List<PictureItem> pictureItemList, Context context) {
        this.pictureItemList = pictureItemList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return pictureItemList == null ? 0 : pictureItemList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        ZoomImageView pictureView = new ZoomImageView(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pictureView.setLayoutParams(layoutParams);

        final String url = pictureItemList.get(position).pictureAbsPath;

        ImageLoader.load(context, url, pictureView);


        pictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onPictureClick(position, url);
                }
            }
        });
        pictureView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return longClickListener != null && longClickListener.onPictureLongClick(position, url);
            }
        });

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


    public interface OnPictureClickListener {
        /**
         * 点击回调
         *
         * @param position current position
         * @param url      图片地址
         */
        void onPictureClick(int position, String url);
    }

    public void setOnPictureClickListener(OnPictureClickListener listener) {
        this.clickListener = listener;
    }


    public interface OnPictureLongClickListener {
        /**
         * 长按回调
         *
         * @param position current position
         * @param url      图片地址
         */
        boolean onPictureLongClick(int position, String url);
    }

    public void setOnPictureLongClickListener(OnPictureLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }
}
