package com.devin.demo;

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

import devin.com.picturepicker.activity.PictureCropActivity;
import devin.com.picturepicker.activity.PictureGridActivity;
import devin.com.picturepicker.activity.PicturePreviewActivity;
import devin.com.picturepicker.constant.PreviewAction;
import devin.com.picturepicker.helper.crop.CropOptions;
import devin.com.picturepicker.helper.pick.PickOptions;
import devin.com.picturepicker.helper.pick.PicturePicker;
import devin.com.picturepicker.javabean.PictureItem;
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
    private Button button;
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
        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        rgPickType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_multi) {
                    seekBar.setEnabled(true);
                    cbCanPreview.setEnabled(true);
                } else {
                    seekBar.setProgress(1);
                    seekBar.setEnabled(false);
                    cbCanPreview.setChecked(false);
                    cbCanPreview.setEnabled(false);
                }
            }
        });

        tvMaxCount.setText(seekBar.getProgress() + "");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    seekBar.setProgress(1);
                }
                tvMaxCount.setText(progress + "");
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
                        .setMultiMode(rgPickType.getCheckedRadioButtonId() == R.id.rb_multi)
                        .setPickMaxCount(seekBar.getProgress())
                        .setCanPreviewImg(cbCanPreview.isChecked())
                        .setShowCamera(cbShowCamera.isChecked())
                        .build();

                //默认配置
//                PicturePicker.getInstance().startPickPicture(MainActivity.this, PICK_IMG_REQUEST);
                PicturePicker.getInstance().startPickPicture(MainActivity.this, PICK_IMG_REQUEST, options);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pictureItemList.isEmpty()) {
                    Toast.makeText(MainActivity.this, "先选图片", Toast.LENGTH_SHORT).show();
                } else {

                    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

                    CropOptions cropOptions = new CropOptions.Builder()
                            .setStyle(CropImageView.Style.CIRCLE)
                            .setFocusWidth(displayMetrics.widthPixels)
                            .setFocusHeight(displayMetrics.widthPixels)
                            .setSaveRectangle(false)
                            .build();

                    PictureCropActivity.startPictureCropActivity(MainActivity.this, pictureItemList.get(0).pictureAbsPath, cropOptions, CROP_IMG_REQUEST);
                }
            }
        });

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
                    PicturePreviewActivity.startPicturePreviewActivity(MainActivity.this, pictureItemList, holder.getAdapterPosition(), PreviewAction.PREVIEW_DELETE, PREVIEW_IMG_REQUEST);
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

        if (data != null && requestCode == PICK_IMG_REQUEST) {
            List<PictureItem> tempList = (List<PictureItem>) data.getSerializableExtra(PictureGridActivity.EXTRA_RESULT_PICK_IMAGES);
            pictureItemList.clear();
            pictureItemList.addAll(tempList);
            sampleAdapter.notifyDataSetChanged();
        } else if (data != null && requestCode == PREVIEW_IMG_REQUEST) {
            List<PictureItem> tempList = (List<PictureItem>) data.getSerializableExtra(PicturePreviewActivity.EXTRA_RESULT_PREVIEW_IMAGES);
            pictureItemList.retainAll(tempList);
            sampleAdapter.notifyDataSetChanged();
        } else if (data != null && requestCode == CROP_IMG_REQUEST) {

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
