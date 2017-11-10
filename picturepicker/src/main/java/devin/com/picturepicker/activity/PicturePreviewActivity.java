package devin.com.picturepicker.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import devin.com.picturepicker.R;
import devin.com.picturepicker.adapter.PicturePreviewAdapter;
import devin.com.picturepicker.constant.PreviewAction;
import devin.com.picturepicker.helper.pick.PicturePicker;
import devin.com.picturepicker.javabean.PictureItem;
import devin.com.picturepicker.utils.Utils;

public class PicturePreviewActivity extends BaseActivity implements PicturePicker.OnPictureSelectedListener {

    public static final String EXTRA_RESULT_PREVIEW_IMAGES = "extra_result_preview_pictures";


    /**
     * 因为intent不能传递大数据，故对于大集合数据 采用static 赋值方式
     */
    private static List<PictureItem> toLargePictureItems;

    private List<PictureItem> pictureItemList;
    private PicturePreviewAdapter adapter;
    private PicturePicker picturePicker;

    private View titleBar;
    private TextView titleTextView;
    private Button titleCompleteButton;
    private ImageView ivDelete;

    private ViewPager vpPicturePreview;
    private LinearLayout llFootBar;
    private TextView tvPicturePreviewSelect;

    private PreviewAction previewAction;


    private void assignViews() {
        vpPicturePreview = (ViewPager) findViewById(R.id.vp_picture_preview);
        llFootBar = (LinearLayout) findViewById(R.id.ll_foot_bar);
        tvPicturePreviewSelect = (TextView) findViewById(R.id.tv_picture_preview_select);

        int i = titleBar.getLayoutParams().height + Utils.getStatusHeight(this);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) vpPicturePreview.getLayoutParams();
        layoutParams.topMargin = -i;

    }

    private void setListener() {

        vpPicturePreview.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                refreshTitleText(position + 1, pictureItemList.size());
                PictureItem pictureItem = pictureItemList.get(position);
                tvPicturePreviewSelect.setSelected(picturePicker.isSelected(pictureItem));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        adapter.setOnPictureClickListener(new PicturePreviewAdapter.OnPictureClickListener() {
            @Override
            public void onPictureClick() {
                if (titleBar.getVisibility() == View.VISIBLE) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) vpPicturePreview.getLayoutParams();
                    layoutParams.topMargin = 0;
                    fullScreen(true);
                    titleBar.setVisibility(View.GONE);
                    llFootBar.setVisibility(View.GONE);
                } else {
                    fullScreen(false);
                    int i = titleBar.getLayoutParams().height + Utils.getStatusHeight(PicturePreviewActivity.this);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) vpPicturePreview.getLayoutParams();
                    layoutParams.topMargin = -i;

                    titleBar.setVisibility(View.VISIBLE);

                    if (previewAction == PreviewAction.PREVIEW_PICK) {
                        llFootBar.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        titleCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (previewAction == PreviewAction.PREVIEW_CAMERA_IMAGE) {
                    picturePicker.getSelectedPictureList().clear();
                    picturePicker.getSelectedPictureList().addAll(pictureItemList);
                }
                setResult(RESULT_OK);
                finish();
            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(PicturePreviewActivity.this)
                        .setTitle("提示")
                        .setMessage("要删除这张照片吗？")
                        .setCancelable(true)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (pictureItemList.size() == 1) {
                                    pictureItemList.remove(vpPicturePreview.getCurrentItem());
                                    onBackPressed();
                                } else {
                                    pictureItemList.remove(vpPicturePreview.getCurrentItem());
                                    refreshTitleText(vpPicturePreview.getCurrentItem() + 1, pictureItemList.size());
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });


        tvPicturePreviewSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pictureItemList.size() > 0) {
                    PictureItem pictureItem = pictureItemList.get(vpPicturePreview.getCurrentItem());
                    boolean isSelected = picturePicker.isSelected(pictureItem);

                    if (isSelected) {
                        tvPicturePreviewSelect.setSelected(false);
                        picturePicker.addOrRemovePicture(pictureItem, false);
                    } else {
                        int maxCount = picturePicker.getPickPictureOptions().getPickMaxCount();
                        if (picturePicker.getCurrentSelectedCount() >= maxCount) {
                            Utils.showToast(v.getContext(), v.getContext().getResources().getString(R.string.select_limit_tips, maxCount + ""));
                        } else {
                            tvPicturePreviewSelect.setSelected(true);
                            picturePicker.addOrRemovePicture(pictureItem, true);
                        }
                    }
                }
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_preview);
        assignViews();

        picturePicker = PicturePicker.getInstance();

        pictureItemList = (List<PictureItem>) getIntent().getSerializableExtra("pictureItems");

        if (pictureItemList == null) {
            pictureItemList = toLargePictureItems;
        }

        adapter = new PicturePreviewAdapter(pictureItemList, this);
        vpPicturePreview.setAdapter(adapter);

        setListener();

        previewAction = (PreviewAction) getIntent().getSerializableExtra("previewAction");
        initTitleBarAndFootBar(previewAction);

        int startPosition = getIntent().getIntExtra("startPosition", 0);
        if (startPosition < pictureItemList.size()) {
            refreshTitleText(startPosition + 1, pictureItemList.size());
            vpPicturePreview.setCurrentItem(startPosition);
            tvPicturePreviewSelect.setSelected(picturePicker.isSelected(pictureItemList.get(startPosition)));
        }

        picturePicker.registerPictureSelectedListener(this);

    }

    /**
     * 根据行为初始化titleBar和footBar
     * @param action
     */
    private void initTitleBarAndFootBar(PreviewAction action) {

        switch (action) {
            case ONLY_PREVIEW:
                titleCompleteButton.setVisibility(View.GONE);
                ivDelete.setVisibility(View.GONE);
                llFootBar.setVisibility(View.GONE);
                break;
            case PREVIEW_CAMERA_IMAGE:
                titleCompleteButton.setVisibility(View.VISIBLE);
                refreshTitleCompleteButton();
                ivDelete.setVisibility(View.GONE);
                llFootBar.setVisibility(View.GONE);
                break;
            case PREVIEW_PICK:
                titleCompleteButton.setVisibility(View.VISIBLE);
                refreshTitleCompleteButton();
                ivDelete.setVisibility(View.GONE);
                llFootBar.setVisibility(View.VISIBLE);
                break;
            case PREVIEW_DELETE:
                titleCompleteButton.setVisibility(View.GONE);
                ivDelete.setVisibility(View.VISIBLE);
                llFootBar.setVisibility(View.GONE);
                break;
        }
    }


    public static void startPicturePreviewActivity(android.app.Fragment fragment, List<PictureItem> pictureItems, int startPosition, PreviewAction action, int requestCode) {
        Intent intent = createIntent(fragment.getActivity(), pictureItems, startPosition, action);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startPicturePreviewActivity(android.support.v4.app.Fragment fragment, List<PictureItem> pictureItems, int startPosition, PreviewAction action, int requestCode) {
        Intent intent = createIntent(fragment.getActivity(), pictureItems, startPosition, action);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startPicturePreviewActivity(Activity activity, List<PictureItem> pictureItems, int startPosition, PreviewAction action, int requestCode) {
        Intent intent = createIntent(activity, pictureItems, startPosition, action);
        activity.startActivityForResult(intent, requestCode);
    }


    private static Intent createIntent(Activity activity, List<PictureItem> pictureItems, int startPosition, PreviewAction action) {
        Intent intent = new Intent(activity, PicturePreviewActivity.class);
        intent.putExtra("startPosition", startPosition);
        intent.putExtra("previewAction", action);

        if (pictureItems.size() > 800) {
            toLargePictureItems = pictureItems;
        } else {
            intent.putExtra("pictureItems", (Serializable) pictureItems);
        }
        return intent;
    }


    @Override
    protected void resetTitleBar(TitleBarHelper titleBarHelper) {
        super.resetTitleBar(titleBarHelper);

        titleBar = titleBarHelper.getTitleBarView();
        //将背景设为半透明的
        titleBar.setBackgroundResource(R.color.footBarBackground);

        titleTextView = titleBarHelper.getTitleTextView();
        refreshTitleText(0, 0);

        titleCompleteButton = titleBarHelper.getCompleteButton();

        ivDelete = titleBarHelper.getIvDelete();

    }

    private void refreshTitleText(int position, int size) {
        titleTextView.setText(getString(R.string.img_preview_count, position + "", size + ""));
    }


    /**
     * 刷新titleBar的完成按钮
     */
    private void refreshTitleCompleteButton() {
        if (previewAction == PreviewAction.PREVIEW_CAMERA_IMAGE) {
            titleCompleteButton.setText(getString(R.string.complete_button_enable_false));
        } else if (previewAction == PreviewAction.PREVIEW_PICK) {
            int currentSelectedCount = picturePicker.getCurrentSelectedCount();
            int maxCount = picturePicker.getPickPictureOptions().getPickMaxCount();
            if (currentSelectedCount == 0) {
                titleCompleteButton.setEnabled(false);
                titleCompleteButton.setText(getString(R.string.complete_button_enable_false));
            } else {
                titleCompleteButton.setEnabled(true);
                titleCompleteButton.setText(getString(R.string.complete_button_enable_true, currentSelectedCount + "", maxCount + ""));
            }
        }
    }


    @Override
    public void onPictureSelected(List<PictureItem> selectedPictureList, PictureItem pictureItem, boolean isSelected) {
        refreshTitleCompleteButton();
    }

    /**
     * 是否开启全屏
     *
     * @param enable
     */
    private void fullScreen(boolean enable) {

        if (enable) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        } else {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(lp);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        toLargePictureItems = null;
        picturePicker.unregisterPictureSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (previewAction == PreviewAction.PREVIEW_DELETE) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_RESULT_PREVIEW_IMAGES, (Serializable) pictureItemList);
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }
}
