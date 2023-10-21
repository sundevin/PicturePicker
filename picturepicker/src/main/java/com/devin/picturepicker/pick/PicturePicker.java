package com.devin.picturepicker.pick;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import com.devin.picturepicker.activity.CheckPermissionActivity;
import com.devin.picturepicker.javabean.PictureItem;
import com.devin.picturepicker.options.PickOptions;
import com.devin.picturepicker.provider.ILanguageProvider;

/**

 *
 * @author Devin Sun
 * @date 2016/10/15
 */

public class PicturePicker {

    private ILanguageProvider languageProvider;


    private List<OnPictureSelectedListener> pictureSelectedListeners = new ArrayList<>();
    private List<PictureItem> selectedPictureList = new ArrayList<>();

    private static PicturePicker picturePicker;
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
        Intent intent = new Intent(activity, CheckPermissionActivity.class);
        activity.startActivityForResult(intent, requestCode);

    }

    public void startPickPicture(android.app.Fragment fragment, int requestCode) {
        startPickPicture(fragment, requestCode, PickOptions.getDefaultOptions());
    }

    public void startPickPicture(android.app.Fragment fragment, int requestCode, PickOptions options) {
        this.pickPictureOptions = options;
        Intent intent = new Intent(fragment.getActivity(), CheckPermissionActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    public void startPickPicture(Fragment fragment, int requestCode) {
        startPickPicture(fragment, requestCode, PickOptions.getDefaultOptions());
    }

    public void startPickPicture(Fragment fragment, int requestCode, PickOptions options) {
        this.pickPictureOptions = options;
        Intent intent = new Intent(fragment.getActivity(), CheckPermissionActivity.class);
        fragment.startActivityForResult(intent, requestCode);
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
        if (listener != null) {
            pictureSelectedListeners.add(listener);
        }
    }

    /**
     * 反注册图片选择的监听器
     *
     * @param listener
     */
    public void unregisterPictureSelectedListener(OnPictureSelectedListener listener) {
        if (listener != null) {
            pictureSelectedListeners.remove(listener);
        }
    }

    public ILanguageProvider getLanguageProvider() {
        return languageProvider;
    }

    public void setLanguageProvider(ILanguageProvider languageProvider) {
        this.languageProvider = languageProvider;
    }
}
