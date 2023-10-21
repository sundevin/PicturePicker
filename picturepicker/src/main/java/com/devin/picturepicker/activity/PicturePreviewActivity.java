package com.devin.picturepicker.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.devin.picturepicker.fragment.PreviewPictureFragment;
import com.devin.picturepicker.R;
import com.devin.picturepicker.adapter.PicturePreviewAdapter;
import com.devin.picturepicker.constant.PreviewAction;
import com.devin.picturepicker.javabean.PictureItem;
import com.devin.picturepicker.pick.PicturePicker;
import com.devin.picturepicker.utils.Utils;

public class PicturePreviewActivity extends PictureBaseActivity implements PicturePicker.OnPictureSelectedListener {

    public static final String EXTRA_RESULT_PREVIEW_IMAGES = "extra_result_preview_pictures";


    /**
     * 因为intent不能传递大数据，故对于大集合数据 采用static 赋值方式
     */
    private static List<PictureItem> toLargePictureItems;

    private List<PictureItem> pictureItemList;
    private PicturePicker picturePicker;

    private View titleBar;
    private TextView titleTextView;
    private Button titleCompleteButton;
    private ImageView ivDelete;


    private LinearLayout llFootBar;
    private TextView tvPicturePreviewSelect;

    private PreviewAction previewAction;
    protected PreviewPictureFragment pictureFragment;


    /**
     * 只预览图片
     *
     * @param activity
     * @param url
     */
    public static void startActivityWithOnlyPreview(Activity activity, String url) {
        List<PictureItem> pictureItems = new ArrayList<>();
        PictureItem pictureItem = new PictureItem();
        pictureItem.pictureAbsPath = url;
        pictureItems.add(pictureItem);

        startActivityWithOnlyPreview(activity, pictureItems, 0);
    }

    /**
     * 只预览图片
     *
     * @param activity
     * @param urls
     * @param currPosition
     */
    public static void startActivityWithOnlyPreview(Activity activity, int currPosition, List<String> urls) {
        List<PictureItem> pictureItems = new ArrayList<>();
        for (String url : urls) {
            PictureItem pictureItem = new PictureItem();
            pictureItem.pictureAbsPath = url;
            pictureItems.add(pictureItem);
        }
        startActivityWithOnlyPreview(activity, pictureItems, currPosition);
    }


    /**
     * 只预览图片
     *
     * @param activity
     * @param pictureItems
     * @param currPosition
     */
    public static void startActivityWithOnlyPreview(Activity activity, List<PictureItem> pictureItems, int currPosition) {
        Intent intent = createIntent(activity, pictureItems, currPosition, PreviewAction.ONLY_PREVIEW);
        activity.startActivity(intent);
    }


    /**
     * 预览可删除
     *
     * @param fragment     android.app.Fragment fragment
     * @param urls         图片url list
     * @param currPosition 开始预览的index
     * @param requestCode  请求码
     */
    public static void startActivityWithPreviewDel(android.app.Fragment fragment, List<String> urls, int currPosition, int requestCode) {
        List<PictureItem> pictureItems = new ArrayList<>();
        for (String url : urls) {
            PictureItem pictureItem = new PictureItem();
            pictureItem.pictureAbsPath = url;
            pictureItems.add(pictureItem);
        }
        Intent intent = createIntent(fragment.getActivity(), pictureItems, currPosition, PreviewAction.PREVIEW_DELETE);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 预览可删除
     *
     * @param fragment     android.support.v4.app.Fragment fragment
     * @param urls         图片url list
     * @param currPosition 开始预览的index
     * @param requestCode  请求码
     */
    public static void startActivityWithPreviewDel(Fragment fragment, List<String> urls, int currPosition, int requestCode) {
        List<PictureItem> pictureItems = new ArrayList<>();
        for (String url : urls) {
            PictureItem pictureItem = new PictureItem();
            pictureItem.pictureAbsPath = url;
            pictureItems.add(pictureItem);
        }
        Intent intent = createIntent(fragment.getActivity(), pictureItems, currPosition, PreviewAction.PREVIEW_DELETE);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 预览可删除
     *
     * @param activity     activity
     * @param urls         图片url list
     * @param currPosition 开始预览的index
     * @param requestCode  请求码
     */
    public static void startActivityWithPreviewDel(Activity activity, List<String> urls, int currPosition, int requestCode) {
        List<PictureItem> pictureItems = new ArrayList<>();
        for (String url : urls) {
            PictureItem pictureItem = new PictureItem();
            pictureItem.pictureAbsPath = url;
            pictureItems.add(pictureItem);
        }
        Intent intent = createIntent(activity, pictureItems, currPosition, PreviewAction.PREVIEW_DELETE);
        activity.startActivityForResult(intent, requestCode);
    }


    /**
     * 使用者一般用不上此方法
     *
     * @param activity     activity
     * @param pictureItems PictureItem list
     * @param currPosition 开始预览的index
     * @param action       打开本页面的意图
     * @param requestCode  请求码
     */
    public static void startActivity(Activity activity, List<PictureItem> pictureItems, int currPosition, PreviewAction action, int requestCode) {
        Intent intent = createIntent(activity, pictureItems, currPosition, action);
        activity.startActivityForResult(intent, requestCode);
    }

    private static Intent createIntent(Context context, List<PictureItem> pictureItems, int currPosition, PreviewAction action) {
        Intent intent = new Intent(context, getClazz());
        intent.putExtra("currPosition", currPosition);
        intent.putExtra("previewAction", action);

        if (pictureItems.size() > 1000) {
            toLargePictureItems = pictureItems;
        } else {
            intent.putExtra("pictureItems", (Serializable) pictureItems);
        }
        return intent;
    }


    protected static Class<? extends PicturePreviewActivity> getClazz() {
        return PicturePreviewActivity.class;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_preview);
        assignViews();

        picturePicker = PicturePicker.getInstance();

        previewAction = (PreviewAction) getIntent().getSerializableExtra("previewAction");
        initTitleBarAndFootBar(previewAction);

        int currPosition = getIntent().getIntExtra("currPosition", 0);
        pictureItemList = (List<PictureItem>) getIntent().getSerializableExtra("pictureItems");
        if (pictureItemList == null) {
            pictureItemList = toLargePictureItems;
            toLargePictureItems = null;
        }
        pictureFragment = (PreviewPictureFragment) getFragmentManager().findFragmentById(R.id.fragment);
        int i = titleBar.getLayoutParams().height + Utils.getStatusHeight(this);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) pictureFragment.getView().getLayoutParams();
        layoutParams.topMargin = -i;

        pictureFragment.setPictureItems(pictureItemList, currPosition);

        if (currPosition < pictureItemList.size()) {
            refreshTitleText(currPosition + 1, pictureItemList.size());
            tvPicturePreviewSelect.setSelected(picturePicker.isSelected(pictureItemList.get(currPosition)));
        }

        if (previewAction == PreviewAction.ONLY_PREVIEW) {
            fullScreen(true);
        }
        setListener();
        picturePicker.registerPictureSelectedListener(this);

    }

    private void assignViews() {
        llFootBar = (LinearLayout) findViewById(R.id.ll_foot_bar);
        tvPicturePreviewSelect = (TextView) findViewById(R.id.tv_picture_preview_select);
    }

    private void setListener() {

        pictureFragment.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        pictureFragment.setOnPictureClickListener(new PicturePreviewAdapter.OnPictureClickListener() {
            @Override
            public void onPictureClick(int position, String url) {
                fullScreen(titleBar.getVisibility() == View.VISIBLE);
            }
        });

        titleCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(PicturePreviewActivity.this)
                        .setTitle(R.string.del_dialog_title)
                        .setMessage(R.string.del_dialog_msg)
                        .setCancelable(true)
                        .setPositiveButton(R.string.del_dialog_positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (pictureItemList.size() == 1) {
                                    pictureItemList.remove(pictureFragment.getCurrentItem());
                                    setResultOnPreviewDelete();
                                    finish();
                                } else {
                                    pictureItemList.remove(pictureFragment.getCurrentItem());
                                    refreshTitleText(pictureFragment.getCurrentItem() + 1, pictureItemList.size());
                                    pictureFragment.setPictureItems(pictureItemList, pictureFragment.getCurrentItem());
                                }
                            }
                        })
                        .setNegativeButton(R.string.del_dialog_negative_button, null)
                        .show();
            }
        });


        tvPicturePreviewSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pictureItemList.size() > 0) {
                    PictureItem pictureItem = pictureItemList.get(pictureFragment.getCurrentItem());
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
    protected void resetTitleBar(TitleBarHelper titleBarHelper) {

        titleBarHelper.getBackImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResultOnPreviewDelete();
                finish();
            }
        });

        titleBar = titleBarHelper.getTitleBarView();

        titleTextView = titleBarHelper.getTitleTextView();
        refreshTitleText(0, 0);

        titleCompleteButton = titleBarHelper.getCompleteButton();
        ivDelete = titleBarHelper.getIvDelete();

    }

    /**
     * 根据行为初始化titleBar和footBar
     *
     * @param action
     */
    private void initTitleBarAndFootBar(PreviewAction action) {

        switch (action) {
            case ONLY_PREVIEW:
                titleCompleteButton.setVisibility(View.GONE);
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
            default:
                break;
        }
    }

    private void refreshTitleText(int position, int size) {
        titleTextView.setText(getString(R.string.img_preview_count, position + "", size + ""));
    }

    /**
     * 刷新titleBar的完成按钮
     */
    private void refreshTitleCompleteButton() {
        if (previewAction == PreviewAction.PREVIEW_PICK) {
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
     * 是否全屏显示图片
     *
     * @param enable
     */
    private void fullScreen(boolean enable) {

        if (enable) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) pictureFragment.getView().getLayoutParams();
            layoutParams.topMargin = 0;
            titleBar.setVisibility(View.GONE);
            Utils.hideStatusBar(this, true);
            llFootBar.setVisibility(View.GONE);
        } else {
            Utils.hideStatusBar(this, false);
            int i = titleBar.getLayoutParams().height + Utils.getStatusHeight(PicturePreviewActivity.this);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) pictureFragment.getView().getLayoutParams();
            layoutParams.topMargin = -i;
            titleBar.setVisibility(View.VISIBLE);
            if (previewAction == PreviewAction.PREVIEW_PICK) {
                llFootBar.setVisibility(View.VISIBLE);
            }
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        picturePicker.unregisterPictureSelectedListener(this);
        Glide.get(this).clearMemory();
    }

    @Override
    public void onBackPressed() {
        setResultOnPreviewDelete();
        super.onBackPressed();
    }

    private void setResultOnPreviewDelete() {
        if (previewAction == PreviewAction.PREVIEW_DELETE) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_RESULT_PREVIEW_IMAGES, (Serializable) pictureItemList);
            setResult(RESULT_OK, intent);
        }
    }
}
