package devin.com.picturepicker.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import devin.com.picturepicker.R;

/**
 * <p>Description:
 * <p>Company:
 * <p>Email:bjxm2013@163.com
 * <p>@author:Created by Devin Sun on 2017/12/8.
 */

public class ImageLoader {

    public static void load(Context context, String path, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .placeholder(R.drawable.default_picture)
                .error(R.drawable.default_picture)
                .into(imageView);
    }
}
