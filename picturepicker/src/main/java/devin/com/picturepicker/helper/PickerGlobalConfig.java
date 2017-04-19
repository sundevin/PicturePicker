package devin.com.picturepicker.helper;

import java.io.Serializable;

/**

 * <p>   Created by Devin Sun on 2016/10/16.
 */

public class PickerGlobalConfig implements Serializable {

    /**
     * 缓存路径
     */
    private String cacheFolderPath;

    private PickerGlobalConfig(String cacheFolderPath) {
        this.cacheFolderPath = cacheFolderPath;
    }

    public String getCacheFolderPath() {
        return cacheFolderPath;
    }

    public static class Builder {

        /**
         * 缓存路径
         */
        private String cacheFolderPath;

        public PickerGlobalConfig build() {
            return new PickerGlobalConfig(cacheFolderPath);
        }

        public Builder setCacheFolderPath(String cacheFolderPath) {
            this.cacheFolderPath = cacheFolderPath;
            return this;
        }
    }
}
