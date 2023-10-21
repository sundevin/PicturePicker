package com.devin.picturepicker.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.devin.picturepicker.R;

public class ItemFolderListHolder extends RecyclerView.ViewHolder {
    private ImageView ivFolderCover;
    private TextView tvFolderName;
    private TextView tvPictureCount;
    private ImageView ivFolderCheck;

    public ItemFolderListHolder(LayoutInflater inflater, ViewGroup parent) {
        this(inflater.inflate(R.layout.item_folder_list, parent, false));
    }

    public ItemFolderListHolder(View view) {
        super(view);
        ivFolderCover = (ImageView) view.findViewById(R.id.iv_folder_cover);
        tvFolderName = (TextView) view.findViewById(R.id.tv_folder_name);
        tvPictureCount = (TextView) view.findViewById(R.id.tv_picture_count);
        ivFolderCheck = (ImageView) view.findViewById(R.id.iv_folder_check);
    }

    public TextView getTvFolderName() {
        return tvFolderName;
    }

    public TextView getTvPictureCount() {
        return tvPictureCount;
    }

    public ImageView getIvFolderCheck() {
        return ivFolderCheck;
    }

    public ImageView getIvFolderCover() {
        return ivFolderCover;
    }
}
