package devin.com.picturepicker.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import devin.com.picturepicker.R;

public class ItemPictureGridHolder extends RecyclerView.ViewHolder {
    private ImageView ivPickPicture;
    private View viewPickerPictureCover;
    private ImageView ivImgSelected;

    public ItemPictureGridHolder(LayoutInflater inflater, ViewGroup parent) {
        this(inflater.inflate(R.layout.item_picture_grid, parent, false));
    }

    public ItemPictureGridHolder(View view) {
        super(view);
        ivPickPicture = (ImageView) view.findViewById(R.id.iv_pick_picture);
        viewPickerPictureCover = view.findViewById(R.id.view_picker_picture_cover);
        ivImgSelected = (ImageView) view.findViewById(R.id.iv_img_selected);
    }

    public ImageView getIvPickPicture() {
        return ivPickPicture;
    }

    public View getViewPickerPictureCover() {
        return viewPickerPictureCover;
    }

    public ImageView getIvImgSelected() {
        return ivImgSelected;
    }
}
