package com.devin.picturepicker.options;

import java.io.Serializable;

import com.devin.picturepicker.view.CropImageView;

/**
 * <p>Description:
 * <p>Company：
 * <p>Email:bjxm2013@163.com
 * <p>Created by Devin Sun on 2017/4/19.
 */

public class CropOptions implements Serializable {

    private int outPutX;
    private int outPutY;
    private CropImageView.Style style;
    private int focusWidth;
    private int focusHeight;
    private boolean isSaveRectangle;

    private CropOptions(Builder builder) {
        outPutX = builder.outPutX;
        outPutY = builder.outPutY;
        style = builder.style;
        focusWidth = builder.focusWidth;
        focusHeight = builder.focusHeight;
        isSaveRectangle = builder.isSaveRectangle;
    }

    public int getOutPutX() {
        return outPutX;
    }

    public int getOutPutY() {
        return outPutY;
    }

    public CropImageView.Style getStyle() {
        return style;
    }

    public int getFocusWidth() {
        return focusWidth;
    }

    public int getFocusHeight() {
        return focusHeight;
    }

    public boolean isSaveRectangle() {
        return isSaveRectangle;
    }

    public static class Builder implements Serializable {

        private int outPutX=800;
        private int outPutY=800;
        private CropImageView.Style style=CropImageView.Style.RECTANGLE;
        private int focusWidth=280;
        private int focusHeight=280;
        private boolean isSaveRectangle=false;

        public Builder() {
        }

        public CropOptions build() {
            return new CropOptions(this);
        }


        /**
         * 裁剪后需要保存的图片宽度
         *
         * @param outPutX
         */
        public Builder setOutPutX(int outPutX) {
            this.outPutX = outPutX;
            return this;
        }

        /**
         * 裁剪后需要保存的图片宽度
         *
         * @param outPutY
         */
        public Builder setOutPutY(int outPutY) {
            this.outPutY = outPutY;
            return this;
        }

        /**
         * 裁剪时，裁剪框是矩形还是圆形
         *
         * @param style
         */
        public Builder setStyle(CropImageView.Style style) {
            this.style = style;
            return this;
        }

        /**
         * 矩形裁剪框宽度（圆形自动取宽高最小值）
         *
         * @param focusWidth
         */
        public Builder setFocusWidth(int focusWidth) {
            this.focusWidth = focusWidth;
            return this;
        }

        /**
         * 矩形裁剪框高度（圆形自动取宽高最小值）
         *
         * @param focusHeight
         */
        public Builder setFocusHeight(int focusHeight) {
            this.focusHeight = focusHeight;
            return this;
        }

        /**
         * 裁剪后的图片是按矩形区域保存还是裁剪框的形状，
         * 例如圆形裁剪的时候，该参数给true，那么保存的图片是矩形区域，
         * 如果该参数给false，保存的图片是圆形区域
         *
         * @param saveRectangle
         */
        public Builder setSaveRectangle(boolean saveRectangle) {
            isSaveRectangle = saveRectangle;
            return this;
        }
    }


}
