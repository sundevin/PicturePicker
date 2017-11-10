# PicturePicker

[![Download](https://api.bintray.com/packages/sundevin/PicturePicker/picturepicker/images/download.svg)](https://bintray.com/sundevin/PicturePicker/picturepicker/_latestVersion)
[![Lisense](https://img.shields.io/badge/License-Apache%202-lightgrey.svg)](https://www.apache.org/licenses/LICENSE-2.0)

一个高仿微信朋友圈图片选择的项目

项目为高仿微信图片选择模块，目前可实现图片的单选，多选，拍照，预览，或者只拍照不选图，选择后删除等

图片裁剪功能支持自定义裁剪框样式，大小，形状，方向旋转，裁剪后图片的尺寸等

后期将会添加自定义主题

项目参考了其他类似开源项目的部分实现，并针对一些边界问题做了修复，例如初始时无图片或者图片过多导致的问题，并针对特殊机型的拍照 crash 问题做了修复，代码更加简洁。

 欢迎star 和 Fork,有优化建议和 bug 可以提 issue,我会及时处理。
 
### 效果演示：
 
 ![效果](https://raw.githubusercontent.com/sundevin/Screenshot/master/picturepicker-img/0.gif)
 ![效果](https://raw.githubusercontent.com/sundevin/Screenshot/master/picturepicker-img/1.png)
 ![效果](https://raw.githubusercontent.com/sundevin/Screenshot/master/picturepicker-img/2.png)
 ![效果](https://raw.githubusercontent.com/sundevin/Screenshot/master/picturepicker-img/3.png)
 ![效果](https://raw.githubusercontent.com/sundevin/Screenshot/master/picturepicker-img/4.png)
 ![效果](https://raw.githubusercontent.com/sundevin/Screenshot/master/picturepicker-img/5.png)

### 使用：

#### 适用版本 minSdkVersion 14及以上


### 更新日志

- 1.0.4
    ```
     2017/7/14
     修复裁剪图片时 crash 的问题；
     拍摄完成后不再跳转到预览页面，直接返回图片。
    ```


- 1.0.3
    ```
     2017/7/13
     修复 Fragment 导包不统一的问题，增加对 Fragment 和 v4.Fragment 的支持。
    ```


- 1.0.2
    ```
     2017/7/13
     修复在“只拍照不可选图”模式下打开相机后取消拍照，或者拍照后舍弃图片返回时页面空白的bug；
     修复裁剪图片时在部分机型上crash的问题
    ```

- 1.0.1
    ```
     2017/7/12
     添加“只拍照不可选图”模式；
     修复裁剪图片时在部分机型上图片被旋转的问题。
    ```

#### 1，导入依赖库 picturepicker

1,gradle
```
dependencies {
  compile 'com.sundevin:picturepicker:{lastVersion}'
}
```

2,下载 library,以 module 的方式导入。
 
#### 2，Application 初始化全局配置
 
    @Override
    public void onCreate() {
        super.onCreate();

        //初始化全局配置
        PickerGlobalConfig config = new PickerGlobalConfig.Builder()
    //                .setCacheFolderPath("xxxx")//设置拍照的路径，默认sdcard/data/data/package/files
                .build();
        PicturePicker.getInstance().init(config);

    }
        
#### 3，打开图片选择页面
       button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickOptions options = new PickOptions.Builder()
                .setJustTakePhoto(rgPickType.getCheckedRadioButtonId() == R.id.rb_just_take_photo)
                .setMultiMode(rgPickType.getCheckedRadioButtonId() == R.id.rb_multi)
                .setPickMaxCount(seekBar.getProgress())
                .setCanPreviewImg(cbCanPreview.isChecked())
                .setShowCamera(cbShowCamera.isChecked())
                .build();
                
                //默认配置
                // PicturePicker.getInstance().startPickPicture(MainActivity.this, PICK_IMG_REQUEST);
                PicturePicker.getInstance().startPickPicture(MainActivity.this, PICK_IMG_REQUEST, options);
            }
            });   
        

#### 4，获取选择的图片

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && requestCode == PICK_IMG_REQUEST) {
            List<PictureItem> tempList = (List<PictureItem>) data.getSerializableExtra(PictureGridActivity.EXTRA_RESULT_PICK_IMAGES);
            pictureItemList.clear();
            pictureItemList.addAll(tempList);
            sampleAdapter.notifyDataSetChanged();
        } 
    }

#### 5, 进入裁剪功能

```
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

                    CropOptions cropOptions = new CropOptions.Builder()
                            .setOutPutX(800)
                            .setOutPutY(800)
                            .setStyle(CropImageView.Style.RECTANGLE)
                            .setFocusWidth(displayMetrics.widthPixels)
                            .setFocusHeight(displayMetrics.widthPixels)
                            .setSaveRectangle(false)
                            .build();

                    PictureCropActivity.startPictureCropActivity(MainActivity.this, pictureItemList.get(0).pictureAbsPath, cropOptions, CROP_IMG_REQUEST);

```

#### 6, 获取裁剪后的图片

```
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (data != null && requestCode == CROP_IMG_REQUEST) {

            String cropImgPath = data.getStringExtra(PictureCropActivity.EXTRA_NAME_CROP_IMG_PATH);
            PictureItem pictureItem = new PictureItem();
            pictureItem.pictureAbsPath = cropImgPath;

            pictureItemList.clear();
            pictureItemList.add(pictureItem);
            sampleAdapter.notifyDataSetChanged();
            }
        }
```