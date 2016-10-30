package devin.com.picturepicker.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
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

import devin.com.picturepicker.R;
import devin.com.picturepicker.adapter.PictureGridAdapter;
import devin.com.picturepicker.adapter.PopupFolderListAdapter;
import devin.com.picturepicker.adapter.viewholder.ItemPictureGridHolder;
import devin.com.picturepicker.constant.PreviewAction;
import devin.com.picturepicker.helper.PicturePicker;
import devin.com.picturepicker.helper.PictureScanner;
import devin.com.picturepicker.helper.PickOptions;
import devin.com.picturepicker.javabean.PictureFolder;
import devin.com.picturepicker.javabean.PictureItem;
import devin.com.picturepicker.utils.Utils;

public class PictureGridActivity extends BaseActivity implements View.OnClickListener, PicturePicker.OnPictureSelectedListener {

    public static final int RECYCLER_VIEW_COLUMN = 3;

    /**
     * 打开相机请求码
     */
    private final int OPEN_CAMERA_REQUEST_CODE = 1001;

    /**
     * 去预览并选择图片请求码
     */
    private final int PREVIEW_IMAGE_REQUEST_CODE = 1002;

    /**
     * 返回选择的图片的结果吗
     */
    private final int PICK_IMAGE_RESULT_CODE = 2001;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState!=null){
            takePhotoPath=savedInstanceState.getString("takePhotoPath");
            picturePicker.setPickPictureOptions((PickOptions) savedInstanceState.getSerializable("pickPictureOptions"));
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
                            PicturePreviewActivity.startPicturePreviewActivity(PictureGridActivity.this, currentPictureItemList, realPosition, PreviewAction.PREVIEW_PICK, PREVIEW_IMAGE_REQUEST_CODE);

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

            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(takePhotoPath)));
            startActivityForResult(intent, OPEN_CAMERA_REQUEST_CODE);

        } else {

            Utils.showToast(this, "系统不支持拍照");

        }

    }

    private void setResult() {

        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT_PICK_IMAGES, (Serializable) picturePicker.getSelectedPictureList());
        setResult(PICK_IMAGE_RESULT_CODE, intent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == OPEN_CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {//拍照返回

            File file = new File(takePhotoPath);

            PictureItem pictureItem = new PictureItem();
            pictureItem.pictureAbsPath = takePhotoPath;
            pictureItem.pictureSize = file.length();
            pictureItem.pictureName = file.getName();

            ArrayList<PictureItem> pictureItems = new ArrayList<>();
            pictureItems.add(pictureItem);

            PicturePreviewActivity.startPicturePreviewActivity(this, pictureItems, 0, PreviewAction.PREVIEW_CAMERA_IMAGE, PREVIEW_IMAGE_REQUEST_CODE);

        } else if (requestCode == PREVIEW_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            setResult();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d("----------","---------------2");

        outState.putString("takePhotoPath", takePhotoPath);
        outState.putSerializable("pickPictureOptions", picturePicker.getPickPictureOptions());

        Log.d("-----------","-------onSaveInstanceState-----"+(picturePicker.getPickPictureOptions()==null));

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        Log.d("----------","---------------3");

        super.onRestoreInstanceState(savedInstanceState);

        takePhotoPath = savedInstanceState.getString("takePhotoPath");
        picturePicker.setPickPictureOptions((PickOptions) savedInstanceState.getSerializable("pickPictureOptions"));

        Log.d("-----------","-------onRestoreInstanceState-----"+(picturePicker.getPickPictureOptions()==null));


    }

    /**
     * 初始化展示文件夹列表的popupWindow
     */
    private ListPopupWindow initFolderListPopupWindow() {

        listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        listPopupWindow.setAnchorView(llFootBar); //ListPopupWindow总会相对于这个View 锚点
        listPopupWindow.setDropDownGravity(Gravity.BOTTOM);
        listPopupWindow.setModal(true);  //是否为模态，影响返回键的处理
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
                    setBtnImgFolderText(pictureFolder.folderName);//更新底部文件夹名称
                    notifyPictureGrid(pictureFolder.pictureItemList);//更新显示的图片
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

        if (pictureScanner == null)
            pictureScanner = new PictureScanner();

        pictureScanner.startScanPicture(this, new PictureScanner.OnScanFinishListener() {
            @Override
            public void onScanFinish(List<PictureFolder> pictureFolders) {

                pictureFolderList.clear();
                pictureFolderList.addAll(pictureFolders);

                if (pictureFolderList.size() > 0) {
                    notifyPictureGrid(pictureFolderList.get(0).pictureItemList);
                    setBtnImgFolderText(pictureFolderList.get(0).folderName);
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

    }


    /**
     * 设置底部当目录名称
     */
    private void setBtnImgFolderText(String folderName) {

        if (TextUtils.isEmpty(folderName)) {
            btnImgFolder.setText(getString(R.string.all_img_folder_name));
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
            PicturePreviewActivity.startPicturePreviewActivity(this, picturePicker.getSelectedPictureList(), 0, PreviewAction.PREVIEW_PICK, PREVIEW_IMAGE_REQUEST_CODE);

        }

    }

    /**
     * 展示文件夹列表的popupWindow
     */
    private void showFolderListPopupWindow() {

        int itemViewHeight = folderListAdapter.getItemViewHeight();

        int maxHeight = (rvPictures.getHeight() - llFootBar.getHeight()) - itemViewHeight / 2;

        int realHeight = itemViewHeight * folderListAdapter.getCount();

        listPopupWindow.setHeight(Math.min(maxHeight, realHeight));

        Utils.setActivityBackgroundAlpha(PictureGridActivity.this, 0.5f);
        listPopupWindow.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pictureScanner != null)
            pictureScanner.stopScanPicture();

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
