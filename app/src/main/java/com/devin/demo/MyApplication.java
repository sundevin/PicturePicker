package com.devin.demo;

import android.app.Application;

import devin.com.picturepicker.helper.pick.PicturePicker;
import devin.com.picturepicker.helper.PickerGlobalConfig;

/**
 * Created by admin on 2016/10/30.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化全局配置
        PickerGlobalConfig config = new PickerGlobalConfig.Builder()
//                .setCacheFolderPath("xxxx")//设置拍照的路径，默认sdcard/data/data/package/files
                .build();
        PicturePicker.getInstance().init(config);

    }
}
