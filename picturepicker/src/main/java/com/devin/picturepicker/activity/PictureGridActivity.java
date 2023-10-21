package com.devin.picturepicker.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.devin.picturepicker.R;
import com.devin.picturepicker.adapter.PictureGridAdapter;
import com.devin.picturepicker.adapter.PopupFolderListAdapter;
import com.devin.picturepicker.adapter.viewholder.ItemPictureGridHolder;
import com.devin.picturepicker.constant.PreviewAction;
import com.devin.picturepicker.javabean.PictureFolder;
import com.devin.picturepicker.javabean.PictureItem;
import com.devin.picturepicker.options.PickOptions;
import com.devin.picturepicker.pick.PicturePicker;
import com.devin.picturepicker.pick.PictureScanner;
import com.devin.picturepicker.utils.Utils;

public class PictureGridActivity extends PictureBaseActivity implements View.OnClickListener, PicturePicker.OnPictureSelectedListener {

    public static final int RECYCLER_VIEW_COLUMN = 4;

    /**
     * 打开相机请求码
     */
    private final int OPEN_CAMERA_REQUEST_CODE = 1001;

    /**
     * 去预览并选择图片请求码
     */
    private final int PREVIEW_IMAGE_REQUEST_CODE = 1002;


    public static final String EXTRA_RESULT_PICK_IMAGES = "extra_result_pick_pictures";

    private String takePhotoPath;

    private PictureScanner pictureScanner;
    private PicturePicker picturePicker = PicturePicker.getInstance();

    private RecyclerView rvPictures;
    private LinearLayout llFootBar;
    private TextView btnImgFolder;
    private Button btnImgPreview;

    private PictureGridAdapter pictureGridAdapter;
    private List<PictureItem> currentPictureItemList = new ArrayList<>();

    private PopupFolderListAdapter folderListAdapter;
    private List<PictureFolder> pictureFolderList = new ArrayList<>();

    private ListPopupWindow listPopupWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            takePhotoPath = savedInstanceState.getString("takePhotoPath");
            picturePicker.setPickPictureOptions((PickOptions) savedInstanceState.getSerializable("pickPictureOptions"));
        }

        if (picturePicker.getPickPictureOptions().isJustTakePhoto()) {
            openCamera();
            return;
        }

        setContentView(R.layout.activity_picture_grid);
        assignViews();
        setListener();
        initRvPictures();

        picturePicker.registerPictureSelectedListener(this);

        if (!picturePicker.getPickPictureOptions().isMultiMode()) {
            titleBarHelper.getCompleteButton().setVisibility(View.GONE);
            btnImgPreview.setVisibility(View.GONE);
        }

        refreshTitleBarCompleteBtnText(0);
        listPopupWindow = initFolderListPopupWindow();
        scanAllPictureFolder();
    }


    private void assignViews() {
        rvPictures = (RecyclerView) findViewById(R.id.rv_pictures);
        llFootBar = (LinearLayout) findViewById(R.id.ll_foot_bar);
        btnImgFolder = (TextView) findViewById(R.id.btn_img_folder);
        btnImgPreview = (Button) findViewById(R.id.btn_img_preview);
    }

    private void setListener() {
        btnImgFolder.setOnClickListener(this);
        btnImgPreview.setOnClickListener(this);

        titleBarHelper.getCompleteButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult();
            }
        });
    }


    private void initRvPictures() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, RECYCLER_VIEW_COLUMN);
        rvPictures.setLayoutManager(gridLayoutManager);
        pictureGridAdapter = new PictureGridAdapter(this, currentPictureItemList);
        rvPictures.setAdapter(pictureGridAdapter);

        pictureGridAdapter.setOnItemClickListener(new PictureGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ItemPictureGridHolder holder, int position) {

                boolean isShowCamera = picturePicker.getPickPictureOptions().isShowCamera();
                boolean isMultiMode = picturePicker.getPickPictureOptions().isMultiMode();
                boolean isCanPreviewImg = picturePicker.getPickPictureOptions().isCanPreviewImg();

                if (position == 0 && isShowCamera) {
                    openCamera();
                } else {
                    int realPosition = isShowCamera ? position - 1 : position;
                    if (isMultiMode) {
                        if (isCanPreviewImg) {
                            PicturePreviewActivity.startActivity(PictureGridActivity.this, currentPictureItemList, realPosition, PreviewAction.PREVIEW_PICK, PREVIEW_IMAGE_REQUEST_CODE);
                        } else {
                            pictureGridAdapter.addOrRemovePicture(currentPictureItemList.get(realPosition), holder);
                        }
                    } else {
                        picturePicker.addOrRemovePicture(currentPictureItemList.get(realPosition), true);
                        setResult();
                    }
                }
            }
        });
    }


    /**
     * 打开相机
     */
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            takePhotoPath = Utils.createTakePhotoPath(this);
            File file = new File(takePhotoPath);
            Uri uri = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(this, getPackageName() + ".FileProvider", file);
            } else {
                uri = Uri.fromFile(file);
            }

            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            startActivityForResult(intent, OPEN_CAMERA_REQUEST_CODE);
        } else {
            Utils.showToast(this, getResources().getString(R.string.no_camera));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //拍照返回
        if (requestCode == OPEN_CAMERA_REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {
                File file = new File(takePhotoPath);
                PictureItem pictureItem = new PictureItem();
                pictureItem.pictureAbsPath = takePhotoPath;
                pictureItem.pictureSize = file.length();
                pictureItem.pictureName = file.getName();

                //直接返回
                picturePicker.getSelectedPictureList().clear();
                picturePicker.getSelectedPictureList().add(pictureItem);
                setResult();

            } else if (resultCode == Activity.RESULT_CANCELED
                    && picturePicker.getPickPictureOptions().isJustTakePhoto()) {
                finish();
            }

        } else if (requestCode == PREVIEW_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                setResult();
            } else if (resultCode == Activity.RESULT_CANCELED
                    && picturePicker.getPickPictureOptions().isJustTakePhoto()) {
                finish();
            }
        }
    }


    private void setResult() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT_PICK_IMAGES, (Serializable) picturePicker.getSelectedPictureList());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("takePhotoPath", takePhotoPath);
        outState.putSerializable("pickPictureOptions", picturePicker.getPickPictureOptions());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        takePhotoPath = savedInstanceState.getString("takePhotoPath");
        picturePicker.setPickPictureOptions((PickOptions) savedInstanceState.getSerializable("pickPictureOptions"));
    }

    /**
     * 初始化展示文件夹列表的popupWindow
     */
    private ListPopupWindow initFolderListPopupWindow() {

        listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        //ListPopupWindow总会相对于这个View 锚点
        listPopupWindow.setAnchorView(llFootBar);
        listPopupWindow.setDropDownGravity(Gravity.BOTTOM);
        //是否为模态，影响返回键的处理
        listPopupWindow.setModal(true);
        listPopupWindow.setContentWidth(ListPopupWindow.MATCH_PARENT);
        listPopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);

        folderListAdapter = new PopupFolderListAdapter(this, pictureFolderList);
        listPopupWindow.setAdapter(folderListAdapter);

        listPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Utils.setActivityBackgroundAlpha(PictureGridActivity.this, 1.0f);
            }
        });

        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PictureFolder pictureFolder = pictureFolderList.get(position);
                if (!TextUtils.equals(pictureFolder.folderAbsPath, folderListAdapter.getCurrentSelectFolderPath())) {
                    folderListAdapter.notifyDataSetChanged(pictureFolder.folderAbsPath);
                    //更新底部文件夹名称
                    setBtnImgFolderText(pictureFolder.folderName);
                    //更新显示的图片
                    notifyPictureGrid(pictureFolder.pictureItemList);
                }

                listPopupWindow.dismiss();
            }
        });

        return listPopupWindow;
    }


    /**
     * 扫描图片
     */
    private void scanAllPictureFolder() {

        if (pictureScanner == null) {
            pictureScanner = new PictureScanner();
        }

        pictureScanner.startScanPicture(this, new PictureScanner.OnScanFinishListener() {
            @Override
            public void onScanFinish(List<PictureFolder> pictureFolders) {
                pictureFolderList.clear();
                pictureFolderList.addAll(pictureFolders);

                if (pictureFolderList.size() > 0) {
                    notifyPictureGrid(pictureFolderList.get(0).pictureItemList);
                    setBtnImgFolderText(pictureFolderList.get(0).folderName);
                    if (folderListAdapter!=null) {
                        folderListAdapter.notifyDataSetChanged(pictureFolderList.get(0).folderAbsPath);
                    }
                } else {
                    currentPictureItemList.clear();
                    notifyPictureGrid(currentPictureItemList);
                    setBtnImgFolderText(null);
                }

            }
        });

    }

    /**
     * 刷新图片ui
     *
     * @param pictureItemList
     */
    private void notifyPictureGrid(List<PictureItem> pictureItemList) {

        if (currentPictureItemList != pictureItemList) {
            currentPictureItemList.clear();
            currentPictureItemList.addAll(pictureItemList);
        }

        pictureGridAdapter.notifyDataSetChanged();
        rvPictures.scrollToPosition(0);
    }


    /**
     * 设置底部当目录名称
     */
    private void setBtnImgFolderText(String folderName) {
        if (TextUtils.isEmpty(folderName)) {
            btnImgFolder.setText(getString(R.string.all_pictures));
            btnImgFolder.setEnabled(false);
        } else {
            btnImgFolder.setText(folderName);
            btnImgFolder.setEnabled(true);
        }
    }

    /**
     * 刷新底部预览图片的数量
     *
     * @param count
     */
    private void refreshPreviewPictureCount(int count) {
        if (count > 0) {
            btnImgPreview.setText(String.format(getString(R.string.preview_button_enable_true), count + ""));
            btnImgPreview.setEnabled(true);
        } else {
            btnImgPreview.setText(getString(R.string.preview_button_enable_false));
            btnImgPreview.setEnabled(false);
        }
    }

    /**
     * 设置titleBar 右边的完成按钮的文本
     *
     * @param count
     */
    private void refreshTitleBarCompleteBtnText(int count) {
        if (count > 0) {
            titleBarHelper.getCompleteButton().setText(String.format(getString(R.string.complete_button_enable_true), count + "", picturePicker.getPickPictureOptions().getPickMaxCount() + ""));
            titleBarHelper.getCompleteButton().setEnabled(true);
        } else {
            titleBarHelper.getCompleteButton().setText(getString(R.string.complete_button_enable_false));
            titleBarHelper.getCompleteButton().setEnabled(false);
        }
    }


    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.btn_img_folder) {
            showFolderListPopupWindow();
        } else if (i == R.id.btn_img_preview) {
            PicturePreviewActivity.startActivity(this, picturePicker.getSelectedPictureList(), 0, PreviewAction.PREVIEW_PICK, PREVIEW_IMAGE_REQUEST_CODE);
        }
    }

    /**
     * 展示文件夹列表的popupWindow
     */
    private void showFolderListPopupWindow() {

        int itemViewHeight = folderListAdapter.getItemViewHeight();
        int maxHeight = rvPictures.getHeight() - itemViewHeight / 2;
        int realHeight = itemViewHeight * folderListAdapter.getCount();
        listPopupWindow.setHeight(Math.min(maxHeight, realHeight));
        Utils.setActivityBackgroundAlpha(PictureGridActivity.this, 0.5f);
        listPopupWindow.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pictureScanner != null) {
            pictureScanner.stopScanPicture();
        }
        picturePicker.unregisterPictureSelectedListener(this);
        picturePicker.cleanData();
        Glide.get(this).clearMemory();
    }

    @Override
    public void onPictureSelected(List<PictureItem> selectedPictureList, PictureItem pictureItem, boolean isSelected) {
        refreshTitleBarCompleteBtnText(selectedPictureList.size());
        refreshPreviewPictureCount(selectedPictureList.size());
        pictureGridAdapter.notifyDataSetChanged();
    }
}
