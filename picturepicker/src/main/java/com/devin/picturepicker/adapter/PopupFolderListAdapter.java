package com.devin.picturepicker.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import com.devin.picturepicker.R;
import com.devin.picturepicker.adapter.viewholder.ItemFolderListHolder;
import com.devin.picturepicker.javabean.PictureFolder;
import com.devin.picturepicker.utils.ImageLoader;
import com.devin.picturepicker.utils.UriUtils;

import static android.view.View.inflate;

/**

 * <p>   Created by Devin Sun on 2016/10/15.

 */

public class PopupFolderListAdapter extends BaseAdapter {


    private String currentSelectFolderPath;

    private Context context;
    private List<PictureFolder> pictureFolderList;

    public PopupFolderListAdapter(Context context, List<PictureFolder> pictureFolderList) {
        this.context = context;
        this.pictureFolderList = pictureFolderList;
    }

    @Override
    public int getCount() {
        return pictureFolderList == null ? 0 : pictureFolderList.size();
    }

    @Override
    public Object getItem(int position) {
        return pictureFolderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ItemFolderListHolder holder;
        if (convertView == null) {
            convertView = inflate(context, R.layout.item_folder_list, null);
            holder = new ItemFolderListHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ItemFolderListHolder) convertView.getTag();
        }

        PictureFolder pictureFolder = pictureFolderList.get(position);
        ImageLoader.load(context, UriUtils.uriStr2Uri(pictureFolder.folderCover.uriString), holder.getIvFolderCover());

        holder.getTvFolderName().setText(pictureFolder.folderName);
        holder.getTvPictureCount().setText(context.getResources().getString(R.string.folder_picture_count,pictureFolder.pictureItemList.size()));

        if (TextUtils.equals(currentSelectFolderPath, pictureFolder.folderAbsPath)) {
            holder.getIvFolderCheck().setVisibility(View.VISIBLE);
        } else {
            holder.getIvFolderCheck().setVisibility(View.GONE);
        }

        return convertView;
    }

    /**
     * 获取单个itemView的高度
     *
     * @return
     */
    public int getItemViewHeight() {

        View itemView = View.inflate(context, R.layout.item_folder_list, null);
        itemView.measure(0, 0);
        return itemView.getMeasuredHeight();
    }

    /**
     * 获取当前选择的文件夹路劲
     *
     * @return
     */
    public String getCurrentSelectFolderPath() {

        return currentSelectFolderPath;
    }


    public void notifyDataSetChanged(String currentSelectFolderPath) {
        this.currentSelectFolderPath = currentSelectFolderPath;
        notifyDataSetChanged();
    }

}
