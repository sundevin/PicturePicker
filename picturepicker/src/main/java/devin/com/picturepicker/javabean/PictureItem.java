package devin.com.picturepicker.javabean;

import android.text.TextUtils;

import java.io.Serializable;

/**

 * <p>   Created by Devin Sun on 2016/10/14.
 */

public class PictureItem implements Serializable {

    /**
     * 图片名称
     */
    public String pictureName;
    /**
     * 图片绝对路径
     */
    public String pictureAbsPath;
    /**
     * 图片类型
     */
    public String pictureMimeType;
    /**
     * 图片大小
     */
    public long pictureSize;
    /**
     * 图片加入的时间
     */
    public long pictureAddTime;
    /**
     * 图片的宽
     */
    public int pictureWidth;
    /**
     * 图片的高
     */
    public int pictureHeight;


    @Override
    public boolean equals(Object o) {
        if (o instanceof PictureItem) {
            PictureItem pictureItem = ((PictureItem) o);
            return TextUtils.equals(pictureItem.pictureAbsPath, pictureAbsPath)
                    &&
                    pictureItem.pictureAddTime == pictureAddTime;
        }
        return super.equals(o);
    }
}
