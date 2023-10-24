package com.devin.picturepicker.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.devin.picturepicker.R;
import com.devin.picturepicker.pick.PicturePicker;

public class CheckPermissionActivity extends AppCompatActivity {

    private final PicturePicker picturePicker = PicturePicker.getInstance();
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
        int res = R.string.pick_permission_desc;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            res = R.string.pick_permission_desc_api_33;
        }

        new AlertDialog.Builder(this)
                .setMessage(res)
                .setCancelable(false)
                .setPositiveButton(R.string.del_dialog_positive_button, new DialogInterface.OnClickListener() {
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                return new String[]{
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.CAMERA};
            } else {
                return new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA};
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                return new String[]{
                        Manifest.permission.READ_MEDIA_IMAGES};
            } else {
                return new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE};
            }
        }
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
