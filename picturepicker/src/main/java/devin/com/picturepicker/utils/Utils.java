package devin.com.picturepicker.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import devin.com.picturepicker.helper.pick.PicturePicker;


/**
 * <p>   Created by Devin Sun on 2016/10/15.
 */

public class Utils {

    public static int getScreenWidth(Context context) {

        return context.getResources().getDisplayMetrics().widthPixels;
    }


    /**
     * 设置屏幕透明度  0.0不透明 ~ 1.0透明
     */
    public static void setActivityBackgroundAlpha(Activity activity, float alpha) {

//        activity. getWindow().getDecorView().setAlpha(alpha);

        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();

        lp.alpha = alpha;

        activity.getWindow().setAttributes(lp);

    }


    private static Toast toast;

    public static void showToast(Context context, String s) {

        if (toast == null) {
            toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        }
        toast.setText(s);
        toast.show();

    }

    /**
     * 获得状态栏的高度
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }


    private static boolean hasSDCard() {

        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED) && !Environment.isExternalStorageRemovable();

    }

    public static String createTakePhotoFolderPath(Context context) {

        String tempPath = PicturePicker.getInstance().getGlobalConfig().getCacheFolderPath();
        if (!TextUtils.isEmpty(tempPath)) {
            return tempPath;
        }

        if (hasSDCard()) {
            File file = context.getExternalFilesDir(null);
            if (file != null) {
                return file.getAbsolutePath();
            }
        }

        return context.getFilesDir().getAbsolutePath();
    }

    public static String createTakePhotoPath(Context context) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
        String pictureFileName = "JPEG_" + timeStamp + ".jpg";

        return new File(createTakePhotoFolderPath(context), pictureFileName).getAbsolutePath();
    }


}
