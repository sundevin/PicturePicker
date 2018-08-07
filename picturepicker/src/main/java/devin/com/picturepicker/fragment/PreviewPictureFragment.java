package devin.com.picturepicker.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import devin.com.picturepicker.R;
import devin.com.picturepicker.adapter.PicturePreviewAdapter;
import devin.com.picturepicker.javabean.PictureItem;


/**
 * 展示图片的fragment
 */
public class PreviewPictureFragment extends Fragment {


    private ViewPager vpPicturePreview;
    private List<PictureItem> pictureItems = new ArrayList<>();
    private PicturePreviewAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_preview_picture, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        adapter = new PicturePreviewAdapter(pictureItems, getContext());
        vpPicturePreview.setAdapter(adapter);
    }

    private void initView(View view) {
        vpPicturePreview = (ViewPager) view.findViewById(R.id.vp_picture_preview);
    }


    /**
     * 设置图片数据源
     *
     * @param url 单张图片
     */
    public void setPictureItems(String url) {
        this.pictureItems.clear();
        PictureItem pictureItem = new PictureItem();
        pictureItem.pictureAbsPath = url;
        pictureItems.add(pictureItem);
        notifyData();
    }

    /**
     * 设置图片数据源
     *
     * @param currPosition Current Item position
     * @param urls         图片地址list
     */
    public void setPictureItems(int currPosition, List<String> urls) {
        this.pictureItems.clear();
        for (String url : urls) {
            PictureItem pictureItem = new PictureItem();
            pictureItem.pictureAbsPath = url;
            pictureItems.add(pictureItem);
        }
        notifyData();
        setCurrentItem(currPosition);
    }

    /**
     * 设置图片数据源
     *
     * @param pictureItems picture Items
     * @param currPosition Current Item position
     */
    public void setPictureItems(List<PictureItem> pictureItems, int currPosition) {
        this.pictureItems.clear();
        this.pictureItems.addAll(pictureItems);
        notifyData();
        setCurrentItem(currPosition);
    }

    private void notifyData() {
        adapter.notifyDataSetChanged();
    }

    /**
     * 图片点击监听
     *
     * @param listener
     */
    public void setOnPictureClickListener(PicturePreviewAdapter.OnPictureClickListener listener) {
        adapter.setOnPictureClickListener(listener);
    }

    /**
     * 图片长按监听
     *
     * @param onPictureLongClickListener
     */
    public void setOnPictureLongClickListener(PicturePreviewAdapter.OnPictureLongClickListener onPictureLongClickListener) {
        adapter.setOnPictureLongClickListener(onPictureLongClickListener);
    }

    /**
     * viewPage  addOnPageChangeListener
     *
     * @param listener
     */
    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        vpPicturePreview.addOnPageChangeListener(listener);
    }

    /**
     * viewPage setCurrentItem
     *
     * @param currPosition
     */
    public void setCurrentItem(int currPosition) {
        vpPicturePreview.setCurrentItem(currPosition);
    }

    /**
     * viewPage getCurrentItem
     *
     * @return
     */
    public int getCurrentItem() {
        return vpPicturePreview.getCurrentItem();
    }

}
