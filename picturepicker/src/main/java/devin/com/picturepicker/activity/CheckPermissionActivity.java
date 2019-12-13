package devin.com.picturepicker.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.Serializable;

import devin.com.picturepicker.pick.PicturePicker;

public class CheckPermissionActivity extends AppCompatActivity {

    private PicturePicker picturePicker = PicturePicker.getInstance();
    private static final int REQUEST_PERMISSION = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);

        String[] needPermission = getNeedPermission();

        ActivityCompat.requestPermissions(this, needPermission, REQUEST_PERMISSION);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (isGrant(grantResults)) {
            Intent intent = new Intent(this, PictureGridActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            startActivity(intent);
            finish();
        } else {
            showPermissionDeniedDialog();
        }
    }

    private boolean isGrant(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (PackageManager.PERMISSION_GRANTED != grantResult) {
                return false;
            }
        }
        return true;
    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setMessage("请授予【存储】和【相机】权限后重试")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    private String[] getNeedPermission() {
        boolean showCamera = picturePicker.getPickPictureOptions().isShowCamera();
        boolean justTakePhoto = picturePicker.getPickPictureOptions().isJustTakePhoto();
        if (showCamera || justTakePhoto) {
            return new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA};
        } else {
            return new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};
        }
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
