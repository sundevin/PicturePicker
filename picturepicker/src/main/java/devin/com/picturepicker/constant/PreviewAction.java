package devin.com.picturepicker.constant;

import java.io.Serializable;

/**
 * Created by admin on 2016/10/30.
 */

public enum PreviewAction implements Serializable {

    /**
     * 只预览
     */
    ONLY_PREVIEW,
    /**
     * 预览相机拍摄的图片
     */
    @Deprecated
    PREVIEW_CAMERA_IMAGE,
    /**
     * 预览并选择图库相片
     */
    PREVIEW_PICK,
    /**
     * 预览并可删除图片
     */
    PREVIEW_DELETE


}
