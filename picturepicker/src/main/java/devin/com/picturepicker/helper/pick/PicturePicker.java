package devin.com.picturepicker.helper.pick;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import devin.com.picturepicker.activity.PictureGridActivity;
import devin.com.picturepicker.helper.PickerGlobalConfig;
import devin.com.picturepicker.javabean.PictureItem;

/**

 * <p>   Created by Devin Sun on 2016/10/15.
 */

public class PicturePicker {

    private static final String ERROR_NOT_INIT = "PicturePicker must be init with configuration before using";

    private List<OnPictureSelectedListener> pictureSelectedListeners = new ArrayList<>();
    private List<PictureItem> selectedPictureList = new ArrayList<>();

    private static PicturePicker picturePicker;
    private PickerGlobalConfig globalConfig;
    private PickOptions pickPictureOptions;

    private PicturePicker() {
    }

    public static PicturePicker getInstance() {
        if (picturePicker == null) {
            synchronized (PicturePicker.class) {
                if (picturePicker == null) {
                    picturePicker = new PicturePicker();
                }
            }
        }

        return picturePicker;
    }


    /**
     * 默认配置下选择图片
     *
     * @param activity
     * @param requestCode
     */
    public void startPickPicture(Activity activity, int requestCode) {

        startPickPicture(activity, requestCode, PickOptions.getDefaultOptions());

    }

    /**
     * @param activity
     * @param requestCode
     * @param options     选择图片时的配置
     */
    public void startPickPicture(Activity activity, int requestCode, PickOptions options) {

        this.pickPictureOptions = options;
        Intent intent = new Intent(activity, PictureGridActivity.class);
        activity.startActivityForResult(intent, requestCode);

    }


    public void startPickPicture(Fragment fragment, int requestCode) {

        startPickPicture(fragment, requestCode, PickOptions.getDefaultOptions());

    }

    public void startPickPicture(Fragment fragment, int requestCode, PickOptions options) {

       checkConfiguration();

        this.pickPictureOptions = options;
        Intent intent = new Intent(fragment.getActivity(), PictureGridActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }


    private void checkConfiguration() {
        if (globalConfig == null) {
            throw new IllegalStateException(ERROR_NOT_INIT);
        }
    }

    /**
     * 清理数据
     */
    public void cleanData() {
        pictureSelectedListeners.clear();
        selectedPictureList.clear();
        pickPictureOptions=null;
    }

    /**
     * 初始化全局配置
     *
     * @param config
     */
    public void init(@NonNull PickerGlobalConfig config) {
        this.globalConfig = config;
    }

    public PickerGlobalConfig getGlobalConfig() {
        return globalConfig;
    }

    /**
     * 获取当前的选择配置
     * @return
     */
    public PickOptions getPickPictureOptions() {
        return pickPictureOptions;
    }

    public void setPickPictureOptions(PickOptions pickPictureOptions) {
        this.pickPictureOptions = pickPictureOptions;
    }

    /**
     * 获取当前已经选择的图片
     * @return
     */
    public  List<PictureItem> getSelectedPictureList(){
        return selectedPictureList;
    }


    /**
     * 判断某个图片是否被选中
     *
     * @param pictureItem
     * @return
     */
    public boolean isSelected(PictureItem pictureItem) {
        return selectedPictureList.contains(pictureItem);
    }

    /**
     * 获取当前已经选择的图片的数量
     * @return
     */
    public int getCurrentSelectedCount() {
        return selectedPictureList.size();
    }


    public void addOrRemovePicture(PictureItem pictureItem, boolean isSelected) {

        if (isSelected) {
            selectedPictureList.add(pictureItem);
        } else {
            selectedPictureList.remove(pictureItem);
        }

        for (OnPictureSelectedListener listener : pictureSelectedListeners) {
            listener.onPictureSelected(selectedPictureList, pictureItem, isSelected);
        }

    }

    /**
     * 图片选择的监听
     */
    public interface OnPictureSelectedListener {
        void onPictureSelected(List<PictureItem> selectedPictureList, PictureItem pictureItem, boolean isSelected);
    }

    /**
     * 注册图片选择的监听器
     *
     * @param listener
     */
    public void registerPictureSelectedListener(OnPictureSelectedListener listener) {
        if (listener != null)
            pictureSelectedListeners.add(listener);
    }

    /**
     * 反注册图片选择的监听器
     *
     * @param listener
     */
    public void unregisterPictureSelectedListener(OnPictureSelectedListener listener) {
        if (listener != null)
            pictureSelectedListeners.remove(listener);
    }

}
