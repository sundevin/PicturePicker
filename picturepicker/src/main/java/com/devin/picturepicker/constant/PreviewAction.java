package com.devin.picturepicker.constant;

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
     * 预览并选择图库相片
     */
    PREVIEW_PICK,
    /**
     * 预览并可删除图片
     */
    PREVIEW_DELETE


}
