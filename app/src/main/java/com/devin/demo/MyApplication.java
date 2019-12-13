package com.devin.demo;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

import devin.com.picturepicker.pick.PicturePicker;
import devin.com.picturepicker.provider.ILanguageProvider;
import devin.com.picturepicker.utils.PictureLangUtils;

/**
 * Created by admin on 2016/10/30.
 */

public class MyApplication extends Application {

    public static Locale  locale=Locale.ENGLISH;

    @Override
    protected void attachBaseContext(Context base) {
        Context context = PictureLangUtils.setLanguage(base,locale);
        super.attachBaseContext(context);

    }

    @Override
    public void onCreate() {
        super.onCreate();

        PicturePicker.getInstance().setLanguageProvider(new ILanguageProvider() {
            @Override
            public Locale getLanguageLocale() {

                return locale;
            }
        });
    }



}
