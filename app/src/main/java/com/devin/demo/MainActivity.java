package com.devin.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import devin.com.picturepicker.activity.PictureCropActivity;
import devin.com.picturepicker.activity.PictureGridActivity;
import devin.com.picturepicker.activity.PicturePreviewActivity;
import devin.com.picturepicker.constant.PreviewAction;
import devin.com.picturepicker.javabean.PictureItem;
import devin.com.picturepicker.options.CropOptions;
import devin.com.picturepicker.options.PickOptions;
import devin.com.picturepicker.pick.PicturePicker;
import devin.com.picturepicker.utils.PictureLangUtils;
import devin.com.picturepicker.view.CropImageView;


public class MainActivity extends AppCompatActivity {

    private final int PICK_IMG_REQUEST = 1;
    private final int PREVIEW_IMG_REQUEST = 2;
    private final int CROP_IMG_REQUEST = 3;


    private List<PictureItem> pictureItemList = new ArrayList<>();
    private SampleAdapter sampleAdapter;

    private ScrollView activityMain;
    private RadioGroup rgPickType;
    private RadioButton rbSingle;
    private RadioButton rbMulti;
    private TextView tvMaxCount;
    private SeekBar seekBar;
    private CheckBox cbCanPreview;
    private CheckBox cbShowCamera;
    private CheckBox cbShowGif;
    private Button button;
    private EditText etOutPutX;
    private EditText etOutPutY;
    private RadioGroup rgFarmShape;
    private RadioButton rbRectangle;
    private RadioButton rbCircle;
    private EditText etFarmWidth;
    private EditText etFarmHeight;
    private CheckBox cbSaveByFarmShape;
    private Button button2;
    private RecyclerView recyclerView;

    private void assignViews() {
        activityMain = (ScrollView) findViewById(R.id.activity_main);
        rgPickType = (RadioGroup) findViewById(R.id.rg_pick_type);
        rbSingle = (RadioButton) findViewById(R.id.rb_single);
        rbMulti = (RadioButton) findViewById(R.id.rb_multi);
        tvMaxCount = (TextView) findViewById(R.id.tv_max_count);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        cbCanPreview = (CheckBox) findViewById(R.id.cb_canPreview);
        cbShowCamera = (CheckBox) findViewById(R.id.cb_showCamera);
        cbShowGif = (CheckBox) findViewById(R.id.cb_showGif);
        button = (Button) findViewById(R.id.button);
        etOutPutX = (EditText) findViewById(R.id.et_out_put_x);
        etOutPutY = (EditText) findViewById(R.id.et_out_put_y);
        rgFarmShape = (RadioGroup) findViewById(R.id.rg_farm_shape);
        rbRectangle = (RadioButton) findViewById(R.id.rb_rectangle);
        rbCircle = (RadioButton) findViewById(R.id.rb_circle);
        etFarmWidth = (EditText) findViewById(R.id.et_farm_width);
        etFarmHeight = (EditText) findViewById(R.id.et_farm_height);
        cbSaveByFarmShape = (CheckBox) findViewById(R.id.cb_save_by_farm_shape);
        button2 = (Button) findViewById(R.id.button2);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        etFarmWidth.setText(String.valueOf(displayMetrics.widthPixels));
        etFarmHeight.setText(String.valueOf(displayMetrics.widthPixels));


        rgPickType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_multi) {
                    seekBar.setEnabled(true);
                    cbCanPreview.setEnabled(true);
                    cbShowCamera.setEnabled(true);
                    cbShowGif.setEnabled(true);
                } else if (checkedId == R.id.rb_single) {
                    seekBar.setProgress(1);
                    seekBar.setEnabled(false);
                    cbCanPreview.setChecked(false);
                    cbCanPreview.setEnabled(false);
                    cbShowCamera.setEnabled(true);
                    cbShowGif.setEnabled(true);
                } else if (checkedId == R.id.rb_just_take_photo) {
                    seekBar.setProgress(1);
                    seekBar.setEnabled(false);
                    cbCanPreview.setChecked(false);
                    cbCanPreview.setEnabled(false);
                    cbShowCamera.setChecked(false);
                    cbShowCamera.setEnabled(false);
                    cbShowGif.setChecked(false);
                    cbShowGif.setEnabled(false);
                }
            }
        });

        tvMaxCount.setText(seekBar.getProgress() + "");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    seekBar.setProgress(1);
                    tvMaxCount.setText("1");
                } else {
                    tvMaxCount.setText(progress + "");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PickOptions options = new PickOptions.Builder()
                        //是否仅拍照 默认false
                        .setJustTakePhoto(rgPickType.getCheckedRadioButtonId() == R.id.rb_just_take_photo)
                        //是否可多选 默认true
                        .setMultiMode(rgPickType.getCheckedRadioButtonId() == R.id.rb_multi)
                        //多选时最大选择数量 默认 9
                        .setPickMaxCount(seekBar.getProgress())
                        //选择图片时点击是否可查看大图，默认true（多选模式有效）
                        .setCanPreviewImg(cbCanPreview.isChecked())
                        //选择图片时是否可拍照 默认true
                        .setShowCamera(cbShowCamera.isChecked())
                        //是否显示 gif ，默认true
                        .setSelectGif(cbShowGif.isChecked())
                        .build();

                //默认配置
//        PicturePicker.getInstance().startPickPicture(MainActivity.this, PICK_IMG_REQUEST);
                PicturePicker.getInstance().startPickPicture(MainActivity.this, PICK_IMG_REQUEST, options);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pictureItemList.isEmpty()) {
                    Toast.makeText(MainActivity.this, "先选图片", Toast.LENGTH_SHORT).show();
                } else {
                    CropOptions cropOptions = new CropOptions.Builder()
                            .setOutPutX(Integer.parseInt(etOutPutX.getText().toString()))
                            .setOutPutY(Integer.parseInt(etOutPutY.getText().toString()))
                            .setFocusWidth(Integer.parseInt(etFarmWidth.getText().toString()))
                            .setFocusHeight(Integer.parseInt(etFarmHeight.getText().toString()))
                            .setStyle(rbRectangle.isChecked() ? CropImageView.Style.RECTANGLE : CropImageView.Style.CIRCLE)
                            .setSaveRectangle(!cbSaveByFarmShape.isSaveEnabled())
                            .build();

                    PictureCropActivity.startPictureCropActivity(MainActivity.this, pictureItemList.get(0).pictureAbsPath, cropOptions, CROP_IMG_REQUEST);
                }
            }
        });

    }


    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = PictureLangUtils.setLanguage(newBase, MyApplication.locale);
        super.attachBaseContext(context);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        assignViews();
        initRecyclerView();
    }


    private void initRecyclerView() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        sampleAdapter = new SampleAdapter();
        recyclerView.setAdapter(sampleAdapter);
    }


    private class SampleAdapter extends RecyclerView.Adapter<SampleHolder> {

        @Override
        public SampleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView imageView = new ImageView(MainActivity.this);
            int w = (parent.getRight() - parent.getLeft()) / 3;
            imageView.setLayoutParams(new LinearLayout.LayoutParams(w, w));
            imageView.setPadding(1, 1, 1, 1);
            return new SampleHolder(imageView);
        }

        @Override
        public int getItemCount() {
            return pictureItemList.size();
        }

        @Override
        public void onBindViewHolder(final SampleHolder holder, int position) {

            Glide.with(getApplication())
                    .load(pictureItemList.get(position).pictureAbsPath)
                    .placeholder(R.drawable.default_picture)
                    .centerCrop()
                    .into((ImageView) holder.itemView);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(MainActivity.this)
                            .setItems(new CharSequence[]{"仅预览图片", "预览且可删除", "预览且图片可长按"}, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    switch (i) {
                                        case 0:
                                            PicturePreviewActivity.startActivityWithOnlyPreview(MainActivity.this, pictureItemList, holder.getAdapterPosition());
                                            break;
                                        case 1:
                                            // 如果是 string list，可直接使用 PicturePreviewActivity.startActivityWithPreviewDel() 方法
                                            PicturePreviewActivity.startActivity(MainActivity.this, pictureItemList, holder.getAdapterPosition(), PreviewAction.PREVIEW_DELETE, PREVIEW_IMG_REQUEST);
                                            break;
                                        case 2:
                                            Main2Activity.startActivity(MainActivity.this, pictureItemList);
                                            break;

                                    }
                                }
                            }).show();
                }
            });
        }
    }

    class SampleHolder extends RecyclerView.ViewHolder {
        private SampleHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (data != null && requestCode == PICK_IMG_REQUEST && resultCode == Activity.RESULT_OK) {
            List<PictureItem> tempList = (List<PictureItem>) data.getSerializableExtra(PictureGridActivity.EXTRA_RESULT_PICK_IMAGES);
            pictureItemList.clear();
            pictureItemList.addAll(tempList);
            sampleAdapter.notifyDataSetChanged();
        } else if (data != null && requestCode == PREVIEW_IMG_REQUEST) {
            List<PictureItem> tempList = (List<PictureItem>) data.getSerializableExtra(PicturePreviewActivity.EXTRA_RESULT_PREVIEW_IMAGES);
            pictureItemList.retainAll(tempList);
            sampleAdapter.notifyDataSetChanged();
        } else if (data != null && requestCode == CROP_IMG_REQUEST && resultCode == Activity.RESULT_OK) {

            Toast.makeText(this, "图片已裁剪，点击下方图片查看", Toast.LENGTH_SHORT).show();

            String cropImgPath = data.getStringExtra(PictureCropActivity.EXTRA_NAME_CROP_IMG_PATH);
            PictureItem pictureItem = new PictureItem();
            pictureItem.pictureAbsPath = cropImgPath;

            pictureItemList.clear();
            pictureItemList.add(pictureItem);
            sampleAdapter.notifyDataSetChanged();

        }
    }
}
