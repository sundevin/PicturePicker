package devin.com.picturepicker.helper;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import devin.com.picturepicker.javabean.PictureFolder;
import devin.com.picturepicker.javabean.PictureItem;


/**

 * <p>   Created by Devin Sun on 2016/10/14.
 */

public class PictureScanner implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int SCANNER_ID = 0;

    private final String[] IMAGE_PROJECTION = {     //查询图片需要的数据列
            MediaStore.Images.Media.DISPLAY_NAME,   //图片的显示名称  aaa.jpg
            MediaStore.Images.Media.DATA,           //图片的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
            MediaStore.Images.Media.SIZE,           //图片的大小，long型  132492
            MediaStore.Images.Media.WIDTH,          //图片的宽度，int型  1920
            MediaStore.Images.Media.HEIGHT,         //图片的高度，int型  1080
            MediaStore.Images.Media.DATE_ADDED,  //图片被添加的时间，long型  1450518608
            MediaStore.Images.Media.MIME_TYPE};     //图片的类型     image/jpeg


    /**
     * 图片扫描成的回调接口
     */
    public interface OnScanFinishListener {
        void onScanFinish(List<PictureFolder> pictureFolders);
    }

    /**
     * 所有的目录集合
     */
    private List<PictureFolder> pictureFolderList = new ArrayList<>();
    private LoaderManager loaderManager;

    private Activity activity;
    private String scanFolderAbsPath;
    private OnScanFinishListener scanFinishListener;


    public PictureScanner() {
    }

    public PictureScanner(String scanFolderAbsPath) {
        this.scanFolderAbsPath = scanFolderAbsPath;
    }

    /**
     * 开始扫描
     *
     * @param scanFinishListener 扫描完成监听
     */
    public void startScanPicture(Activity activity, OnScanFinishListener scanFinishListener) {

        this.activity = activity;
        this.scanFinishListener = scanFinishListener;

        //先停止扫描
        stopScanPicture();

        loaderManager = activity.getLoaderManager();
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

        CursorLoader loader = new CursorLoader(activity);

        loader.setUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //要查询的数据
        loader.setProjection(IMAGE_PROJECTION);

        String selection = MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?";

        if (TextUtils.isEmpty(scanFolderAbsPath)) {

            loader.setSelection(selection);
            String selection_args[] = {"image/jpeg", "image/png", "image/jpg", "image/gif"};
            loader.setSelectionArgs(selection_args);

        } else {

            selection = selection + " " + MediaStore.Images.Media.DATA + " like '%" + scanFolderAbsPath + "%'";
            String selection_args[] = {"image/jpeg", "image/png", "image/jpg", "image/gif", scanFolderAbsPath};
            loader.setSelectionArgs(selection_args);

        }
        loader.setSelection(selection);

        //按加入时间降序排列
        loader.setSortOrder(MediaStore.Images.Media.DATE_ADDED + " DESC");

        return loader;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return createLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        pictureFolderList.clear();

        if (data != null) {
            /**
             * new一个集合存储所有图片
             */
            List<PictureItem> allPictureList = new ArrayList<>();

            /**
             * 图片目录路径和图片目录对象的映射
             */
            ArrayMap<String, PictureFolder> path2PictureFolderMap = new ArrayMap<>();

            while (data.moveToNext()) {
                //得到图片的路径
                String picturePath = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                if (!TextUtils.isEmpty(picturePath)) {

                    File pictureFile = new File(picturePath);

                    if (pictureFile.exists()&&pictureFile.length()!=0) {

                        PictureItem pictureItem = new PictureItem();
                        pictureItem.pictureAbsPath = picturePath;
                        pictureItem.pictureAddTime = data.getLong(data.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
                        pictureItem.pictureHeight = data.getInt(data.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT));
                        pictureItem.pictureWidth = data.getInt(data.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH));
                        pictureItem.pictureMimeType = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
                        pictureItem.pictureSize = data.getInt(data.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
                        pictureItem.pictureName = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));

                        //得到文件的父目录文件
                        File parentFile = pictureFile.getParentFile();

                        //得到父目录的路径并统一转为小写
                        String parentFilePath = parentFile.getAbsolutePath().toLowerCase();

                        //如果图片文件夹已经被存储
                        if (null!=path2PictureFolderMap.get(parentFilePath)) {
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
                folder.folderName = "全部图片";
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
