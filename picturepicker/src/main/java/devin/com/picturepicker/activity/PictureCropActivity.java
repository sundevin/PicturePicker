package devin.com.picturepicker.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import com.bumptech.glide.Glide;

import java.io.File;

import devin.com.picturepicker.R;
import devin.com.picturepicker.options.CropOptions;
import devin.com.picturepicker.utils.Utils;
import devin.com.picturepicker.view.CropImageView;

public class PictureCropActivity extends PictureBaseActivity implements CropImageView.OnBitmapSaveCompleteListener {

    public static final String EXTRA_NAME_CROP_IMG_PATH = "cropImgPath";

    private CropOptions cropOptions;

    private CropImageView cvCropImage;


    public static void startPictureCropActivity(Activity activity, String imgPath, CropOptions cropOptions, int requestCode) {
        Intent intent = new Intent(activity, PictureCropActivity.class);
        intent.putExtra("imgPath", imgPath);
        intent.putExtra("CropOptions", cropOptions);
        activity.startActivityForResult(intent, requestCode);
    }


    public static void startPictureCropActivity(android.app.Fragment fragment, String imgPath, CropOptions cropOptions, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), PictureCropActivity.class);
        intent.putExtra("imgPath", imgPath);
        intent.putExtra("CropOptions", cropOptions);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startPictureCropActivity(android.support.v4.app.Fragment fragment, String imgPath, CropOptions cropOptions, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), PictureCropActivity.class);
        intent.putExtra("imgPath", imgPath);
        intent.putExtra("CropOptions", cropOptions);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_crop);
        assignViews();
        titleBarHelper.getTitleTextView().setText(R.string.crop_picture);
        cropOptions = (CropOptions) getIntent().getSerializableExtra("CropOptions");

        cvCropImage.setFocusStyle(cropOptions.getStyle());
        cvCropImage.setFocusWidth(cropOptions.getFocusWidth());
        cvCropImage.setFocusHeight(cropOptions.getFocusHeight());

        final String imgPath = getIntent().getStringExtra("imgPath");

//        cvCropImage.setImageBitmap(createBitmap(imgPath));
        //用glide 可以不用关心图片压缩和角度颠倒问题，asBitmap 使gif只显示第一帧，
        Glide.with(PictureCropActivity.this).asGif().load(imgPath).into(cvCropImage);

        titleBarHelper.getCompleteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvCropImage.saveBitmapToFile(new File(Utils.createTakePhotoFolderPath(getApplication())), cropOptions.getOutPutX(), cropOptions.getOutPutY(), cropOptions.isSaveRectangle());
            }
        });
        cvCropImage.setOnBitmapSaveCompleteListener(PictureCropActivity.this);

    }

    private void assignViews() {
        cvCropImage = (CropImageView) findViewById(R.id.cv_crop_image);
    }


    private Bitmap createBitmap(String imagePath) {
        //缩放图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        options.inSampleSize = calculateInSampleSize(options, displayMetrics.widthPixels, displayMetrics.heightPixels);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = width / reqWidth;
            } else {
                inSampleSize = height / reqHeight;
            }
        }
        return inSampleSize;
    }

    @Override
    public void onBitmapSaveSuccess(File file) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_NAME_CROP_IMG_PATH, file.getAbsolutePath());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBitmapSaveError(File file) {
        Utils.showToast(getApplicationContext(), getResources().getString(R.string.corp_picture_error));
    }


}
