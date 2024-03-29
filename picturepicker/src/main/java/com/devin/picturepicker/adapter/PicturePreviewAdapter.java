package com.devin.picturepicker.adapter;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.io.File;
import java.util.List;

import com.devin.picturepicker.javabean.PictureItem;
import com.devin.picturepicker.utils.ImageLoader;
import com.devin.picturepicker.utils.UriUtils;
import com.devin.picturepicker.view.photoview.PhotoView;
import com.devin.picturepicker.view.photoview.PhotoViewAttacher;

/**
 * <p>   Created by Devin Sun on 2016/10/16.
 * <p>
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

        PhotoView pictureView=new PhotoView(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pictureView.setLayoutParams(layoutParams);


        final String uriStr = pictureItemList.get(position).uriString;

        if (!TextUtils.isEmpty(uriStr)) {
            ImageLoader.load(context, UriUtils.uriStr2Uri(uriStr), pictureView);
        } else {
            String pictureAbsPath = pictureItemList.get(position).pictureAbsPath;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
                    || !new File(pictureAbsPath).exists()) {
                ImageLoader.load(context, pictureAbsPath, pictureView);
            } else {
                ImageLoader.load(context, UriUtils.getImageContentUri(context, pictureAbsPath), pictureView);
            }
        }

        pictureView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                if (clickListener != null) {
                    clickListener.onPictureClick(position);
                }
            }
        });


        pictureView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                if (clickListener != null) {
                    clickListener.onPictureClick(position);
                }
            }
        });

//        pictureView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (clickListener != null) {
//                    clickListener.onPictureClick(position, uriStr);
//                }
//            }
//        });
        pictureView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return longClickListener != null && longClickListener.onPictureLongClick(position, uriStr);
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

//    @Override
//    public void finishUpdate(ViewGroup container) {
//    }


    public interface OnPictureClickListener {
        /**
         * 点击回调
         *
         * @param position current position
         */
        void onPictureClick(int position);
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
