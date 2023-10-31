package com.devin.picturepicker.pick;

import android.content.ContentUris;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.collection.ArrayMap;
import androidx.loader.content.CursorLoader;

import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.devin.picturepicker.R;
import com.devin.picturepicker.javabean.PictureFolder;
import com.devin.picturepicker.javabean.PictureItem;


/**
 * <p>   Created by Devin Sun on 2016/10/14.
 */

public class PictureScanner implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int SCANNER_ID = 0;

    /**
     * 查询图片需要的数据列
     */
    private final String[] IMAGE_PROJECTION = {
            ////图片的id
            MediaStore.Images.Media._ID,
            ////图片的显示名称  aaa.jpg
            MediaStore.Images.Media.DISPLAY_NAME,
            //图片的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
            MediaStore.Images.Media.DATA,
            //图片的大小，long型  132492
            MediaStore.Images.Media.SIZE,
            //图片的宽度，int型  1920
            MediaStore.Images.Media.WIDTH,
            //图片的高度，int型  1080
            MediaStore.Images.Media.HEIGHT,
            //图片被添加的时间，long型  1450518608
            MediaStore.Images.Media.DATE_ADDED,
            //图片的类型     image/jpeg
            MediaStore.Images.Media.MIME_TYPE};


    /**
     * 图片扫描成的回调接口
     */
    public interface OnScanFinishListener {
        /**
         * 扫描完成
         *
         * @param pictureFolders
         */
        void onScanFinish(List<PictureFolder> pictureFolders);
    }

    /**
     * 所有的目录集合
     */
    private final List<PictureFolder> pictureFolderList = new ArrayList<>();
    private LoaderManager loaderManager;

    private Context context;
    private String scanFolderAbsPath;
    private OnScanFinishListener scanFinishListener;


    public PictureScanner() {
    }

    /**
     * 扫描指定路径下图片
     *
     * @param scanFolderAbsPath
     */
    public PictureScanner(String scanFolderAbsPath) {
        this.scanFolderAbsPath = scanFolderAbsPath;
    }

    /**
     * 开始扫描
     *
     * @param scanFinishListener 扫描完成监听
     */
    public void startScanPicture(AppCompatActivity activity, OnScanFinishListener scanFinishListener) {

        this.context = activity.getApplication();
        this.scanFinishListener = scanFinishListener;

        //先停止扫描
        stopScanPicture();

        loaderManager = LoaderManager.getInstance(activity);
        loaderManager.initLoader(SCANNER_ID, null, this);
    }

    /**
     * 停止扫描
     */
    public void stopScanPicture() {
        if (loaderManager != null) {
            loaderManager.destroyLoader(SCANNER_ID);
        }
    }


    private Loader<Cursor> createLoader() {

        CursorLoader loader = new CursorLoader(context);

        loader.setUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //要查询的数据
        loader.setProjection(IMAGE_PROJECTION);

        String selection = MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?";

        if (TextUtils.isEmpty(scanFolderAbsPath)) {
            loader.setSelection(selection);
            String[] selectionArgs;
            if (PicturePicker.getInstance().getPickPictureOptions().isSelectGif()) {
                selectionArgs = new String[]{"image/jpeg", "image/png", "image/jpg", "image/gif"};
            } else {
                selectionArgs = new String[]{"image/jpeg", "image/png", "image/jpg"};
            }
            loader.setSelectionArgs(selectionArgs);
        } else {
            selection = selection + " " + MediaStore.Images.Media.DATA + " like '%" + scanFolderAbsPath + "%'";
            loader.setSelection(selection);
            String[] selectionArgs;
            if (PicturePicker.getInstance().getPickPictureOptions().isSelectGif()) {
                selectionArgs = new String[]{"image/jpeg", "image/png", "image/jpg", "image/gif", scanFolderAbsPath};
            } else {
                selectionArgs = new String[]{"image/jpeg", "image/png", "image/jpg", scanFolderAbsPath};
            }

            loader.setSelectionArgs(selectionArgs);
        }
        //按加入时间降序排列
        loader.setSortOrder(MediaStore.Images.Media.DATE_ADDED + " DESC");
        return loader;
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return createLoader();
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        pictureFolderList.clear();

        if (data != null) {

            // new一个集合存储所有图片
            List<PictureItem> allPictureList = new ArrayList<>();

            //             图片目录路径和图片目录对象的映射
            ArrayMap<String, PictureFolder> path2PictureFolderMap = new ArrayMap<>();

            while (data.moveToNext()) {
                //得到图片的路径
                String picturePath = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                if (!TextUtils.isEmpty(picturePath)) {
                    File pictureFile = new File(picturePath);
                    if (pictureFile.exists() && pictureFile.length() != 0) {

                        PictureItem pictureItem = new PictureItem();
                        pictureItem.pictureAbsPath = picturePath;
                        pictureItem.pictureAddTime = data.getLong(data.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
                        pictureItem.pictureHeight = data.getInt(data.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT));
                        pictureItem.pictureWidth = data.getInt(data.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH));
                        pictureItem.pictureMimeType = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
                        pictureItem.pictureSize = data.getInt(data.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
                        pictureItem.pictureName = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));

                        long id = data.getLong(data.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                        pictureItem.uriString = uri.toString();

                        //得到文件的父目录文件
                        File parentFile = pictureFile.getParentFile();

                        //得到父目录的路径并统一转为小写
                        String parentFilePath = parentFile.getAbsolutePath().toLowerCase();

                        //如果图片文件夹已经被存储
                        if (null != path2PictureFolderMap.get(parentFilePath)) {
                            path2PictureFolderMap.get(parentFilePath).pictureItemList.add(pictureItem);
                        } else {
                            PictureFolder pictureFolder = new PictureFolder();
                            pictureFolder.folderName = parentFile.getName();
                            pictureFolder.folderAbsPath = parentFilePath;
                            pictureFolder.pictureItemList = new ArrayList<>();
                            pictureFolder.pictureItemList.add(pictureItem);
                            //因为扫描时是按照加入时间降序排序，所以第一个图片为最新的，设为封面
                            pictureFolder.folderCover = pictureItem;
                            pictureFolderList.add(pictureFolder);
                            path2PictureFolderMap.put(parentFilePath, pictureFolder);
                        }

                        //把此图片加到“全部图片”里
                        allPictureList.add(pictureItem);
                    }
                }
            }

            if (allPictureList.size() > 0) {
                PictureFolder folder = new PictureFolder();
                folder.folderName = context.getResources().getString(R.string.all_pictures);
                folder.pictureItemList = allPictureList;
                folder.folderCover = allPictureList.get(0);
                pictureFolderList.add(0, folder);
            }

            if (scanFinishListener != null) {
                scanFinishListener.onScanFinish(pictureFolderList);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        loader.stopLoading();
    }
}
