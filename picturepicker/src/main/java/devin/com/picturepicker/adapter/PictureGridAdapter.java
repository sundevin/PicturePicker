package devin.com.picturepicker.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.List;

import devin.com.picturepicker.R;
import devin.com.picturepicker.adapter.viewholder.ItemPictureGridHolder;
import devin.com.picturepicker.helper.pick.PicturePicker;
import devin.com.picturepicker.javabean.PictureItem;
import devin.com.picturepicker.utils.Utils;

/**

 * <p>   Created by Devin Sun on 2016/10/15.
 */

public class PictureGridAdapter extends RecyclerView.Adapter<ItemPictureGridHolder> {

    private final int ITEM_TYPE_VIEW_CAMERA = 1;
    private final int ITEM_TYPE_VIEW_IMAGE = 2;

    private OnItemClickListener onItemClickListener;

    private Activity activity;
    private List<PictureItem> pictureItemList;

    private PicturePicker picturePicker;
    private boolean isShowCamera;
    private boolean isMultiMode;

    public PictureGridAdapter(Activity activity, List<PictureItem> pictureItemList) {
        this.activity = activity;
        this.pictureItemList = pictureItemList;

        picturePicker = PicturePicker.getInstance();

        isShowCamera = picturePicker.getPickPictureOptions().isShowCamera();
        isMultiMode = picturePicker.getPickPictureOptions().isMultiMode();
    }


    @Override
    public int getItemCount() {

        if (pictureItemList == null) {
            return isShowCamera ? 1 : 0;
        } else {
            return isShowCamera ? pictureItemList.size() + 1 : pictureItemList.size();
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 && isShowCamera ? ITEM_TYPE_VIEW_CAMERA : ITEM_TYPE_VIEW_IMAGE;
    }

    @Override
    public ItemPictureGridHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(activity).inflate(R.layout.item_picture_grid, parent, false);

        int itemViewWidth = (parent.getRight() - parent.getLeft()) / 3;

        //设置宽高 使其为正方形
        itemView.setLayoutParams(new RecyclerView.LayoutParams(itemViewWidth, itemViewWidth));

        return new ItemPictureGridHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ItemPictureGridHolder holder, final int position) {

        switch (getItemViewType(position)) {
            case ITEM_TYPE_VIEW_CAMERA:

                holder.getIvPickPicture().setImageResource(R.drawable.release_takepic);

                holder.getIvImgSelected().setVisibility(View.GONE);
                holder.getViewPickerPictureCover().setVisibility(View.GONE);

                break;
            case ITEM_TYPE_VIEW_IMAGE:

                final PictureItem pictureItem = isShowCamera ? pictureItemList.get(position - 1) : pictureItemList.get(position);

                Glide.with(activity)
                        .load(pictureItem.pictureAbsPath)
                        .centerCrop()
                        .placeholder(R.drawable.default_picture)
                        .error(R.drawable.default_picture)
                        .into(holder.getIvPickPicture());

                if (isMultiMode) {
                    holder.getIvImgSelected().setVisibility(View.VISIBLE);
                } else {
                    holder.getIvImgSelected().setVisibility(View.GONE);
                }

                holder.getViewPickerPictureCover().setVisibility(View.VISIBLE);

                refreshItemView(picturePicker.isSelected(pictureItem), holder);

                holder.getIvImgSelected().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        addOrRemovePicture(pictureItem, holder);

                    }
                });

                break;
        }


        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder, position);
                }
            });
        }

    }

    public void addOrRemovePicture(PictureItem pictureItem, ItemPictureGridHolder holder) {

        if (picturePicker.isSelected(pictureItem)) {
            picturePicker.addOrRemovePicture(pictureItem, false);
            refreshItemView(false, holder);
        } else {

            int maxCount = picturePicker.getPickPictureOptions().getPickMaxCount();

            if (picturePicker.getCurrentSelectedCount() >= maxCount) {
                Utils.showToast(activity, activity.getResources().getString(R.string.select_limit_tips, maxCount + ""));
            } else {
                picturePicker.addOrRemovePicture(pictureItem, true);
                refreshItemView(true, holder);
            }
        }

    }


    private void refreshItemView(boolean isSelected, ItemPictureGridHolder holder) {

        holder.getViewPickerPictureCover().setSelected(isSelected);
        holder.getIvImgSelected().setSelected(isSelected);
    }


    public interface OnItemClickListener {

        void onItemClick(ItemPictureGridHolder holder, int position);

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


}
