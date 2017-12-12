package devin.com.picturepicker.options;

import java.io.Serializable;

/**
 * <p>   Created by Devin Sun on 2016/10/16.
 */

public class PickerGlobalConfig implements Serializable {

    /**
     * 缓存路径
     */
    private String cacheFolderPath;


    private PickerGlobalConfig(Builder builder) {
        this.cacheFolderPath = builder.cacheFolderPath;
    }

    public String getCacheFolderPath() {
        return cacheFolderPath;
    }


    public static PickerGlobalConfig getDefaultOptions() {
        return new PickerGlobalConfig(new Builder());
    }

    public static class Builder {

        /**
         * 缓存路径
         */
        private String cacheFolderPath;

        public PickerGlobalConfig build() {
            return new PickerGlobalConfig(this);
        }

        /**
         * @param cacheFolderPath 拍照和截图使用的缓存路径
         * @return Builder
         */
        public Builder setCacheFolderPath(String cacheFolderPath) {
            this.cacheFolderPath = cacheFolderPath;
            return this;
        }
    }
}
