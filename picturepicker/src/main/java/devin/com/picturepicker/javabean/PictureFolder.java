package devin.com.picturepicker.javabean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

/**

 * <p>   Created by Devin Sun on 2016/10/14.

 */

public class PictureFolder implements Serializable {

    /**
     * 图片文件夹名称
     */
    public String folderName;
    /**
     * 图片文件夹绝对路径
     */
    public String folderAbsPath;
    /**
     *  图片文件夹需要要显示的封面
     */
    public PictureItem folderCover;
    /**
     * 图片文件夹下所有的图片
     */
    public List<PictureItem> pictureItemList;

    @Override
    public boolean equals(Object o) {
        if (o instanceof PictureFolder){
            PictureFolder folder= (PictureFolder) o;
            return TextUtils.equals(folder.folderAbsPath,folderAbsPath);
        }
        return super.equals(o);
    }
}
