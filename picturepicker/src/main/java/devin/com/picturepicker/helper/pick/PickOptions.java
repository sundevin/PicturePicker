package devin.com.picturepicker.helper.pick;

import java.io.Serializable;

/**

 * <p>   Created by Devin Sun on 2016/10/15.
 */

public class PickOptions implements Serializable {

    private boolean justTakePhoto;

    private int pickMaxCount ;
    private boolean multiMode ;

    private boolean showCamera ;
    private boolean canPreviewImg;

    private PickOptions(Builder builder) {
        this.justTakePhoto = builder.justTakePhoto;
        this.pickMaxCount = builder.pickMaxCount;
        this.multiMode = builder.multiMode;
        this.showCamera = builder.showCamera;
        this.canPreviewImg = builder.canPreviewImg;
    }

    private PickOptions() {
    }

    public boolean isJustTakePhoto() {
        return justTakePhoto;
    }

    public int getPickMaxCount() {
        return pickMaxCount;
    }

    public boolean isMultiMode() {
        return multiMode;
    }


    public boolean isShowCamera() {
        return showCamera;
    }

    public boolean isCanPreviewImg() {
        return canPreviewImg;
    }

    public static PickOptions getDefaultOptions() {
        return new PickOptions(new Builder());
    }

    public static class Builder implements Serializable {
        private boolean justTakePhoto;
        private int pickMaxCount = 9;
        private boolean multiMode = true;
        private boolean showCamera = true;
        private boolean canPreviewImg = true;

        public Builder() {
        }

        public PickOptions build() {
            return new PickOptions(this);
        }

        public Builder setJustTakePhoto(boolean justTakePhoto) {
            this.justTakePhoto = justTakePhoto;
            return this;
        }

        public Builder setPickMaxCount(int pickMaxCount) {
            this.pickMaxCount = pickMaxCount;
            return this;
        }

        public Builder setMultiMode(boolean multiMode) {
            this.multiMode = multiMode;
            return this;
        }



        public Builder setShowCamera(boolean showCamera) {
            this.showCamera = showCamera;
            return this;
        }

        public Builder setCanPreviewImg(boolean canPreviewImg) {
            this.canPreviewImg = canPreviewImg;
            return this;
        }
    }

}
