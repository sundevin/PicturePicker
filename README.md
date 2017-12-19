# PicturePicker

[![Download](https://api.bintray.com/packages/sundevin/PicturePicker/picturepicker/images/download.svg)](https://bintray.com/sundevin/PicturePicker/picturepicker/_latestVersion)
[![Lisense](https://img.shields.io/badge/License-Apache%202-lightgrey.svg)](https://www.apache.org/licenses/LICENSE-2.0)

一个仿微信朋友圈图片选择的相册库

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

### 更新日志
- 2.0.0
```
    2017/12/12
    修复已知的 bug
    不再强制进行全局初始化
    图库支持 gif 过滤选项
    预览图片时 api 调用更方便
    将查看图片的逻辑放在 fragment 里，方便自定义。
```
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
---
### 使用：

适用版本 minSdkVersion 19及以上

#### 添加依赖 

1,gradle
```
dependencies {
    // lastVersion 替换为最新版本号
    compile 'com.sundevin:picturepicker:{lastVersion}'
}
```

2,下载 library,以 module 的方式导入。

#### 权限问题
图片库只在 `AndroidManifest` 声明了存储和相机权限，暂未适配23及以上的权限系统，如主项目 target >= 23, 请在使用前自行申请相关权限，
 
#### 初始化全局配置（非必须）
```
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化全局配置(如果不需要指定存储路径，可省略)
        PickerGlobalConfig config = new PickerGlobalConfig.Builder()
            .setCacheFolderPath("xxxx")//设置拍照的路径，默认sdcard/data/data/package/files
            .build();
        PicturePicker.getInstance().init(config);
        //...
    }
```
#### 打开图片选择页面
```
        PickOptions options = new PickOptions.Builder()
                //是否仅拍照 默认false
                .setJustTakePhoto(rgPickType.getCheckedRadioButtonId() == R.id.rb_just_take_photo)
                //是否可多选 默认true
                .setMultiMode(rgPickType.getCheckedRadioButtonId() == R.id.rb_multi)
                //多选时最大选择数量 默认 9
                .setPickMaxCount(seekBar.getProgress())
                //选择图片时点击是否可查看大图，默认true（多选模式有效）
                .setCanPreviewImg(cbCanPreview.isChecked())
                //选择图片时是否可拍照 默认true
                .setShowCamera(cbShowCamera.isChecked())
                //是否显示 gif ，默认true
                .setSelectGif(cbShowGif.isChecked())
                .build();

        //默认配置
//        PicturePicker.getInstance().startPickPicture(MainActivity.this, PICK_IMG_REQUEST);
          PicturePicker.getInstance().startPickPicture(MainActivity.this, PICK_IMG_REQUEST, options);
```
#### 获取选择的图片
```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (data != null && requestCode == PICK_IMG_REQUEST && resultCode == Activity.RESULT_OK) {
        List<PictureItem> tempList = (List<PictureItem>) data.getSerializableExtra(PictureGridActivity.EXTRA_RESULT_PICK_IMAGES);
        //...
    }
}
```
#### 进入裁剪功能

```
DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
//具体方法看注释和demo
CropOptions cropOptions = new CropOptions.Builder()
    .setOutPutX(800)
    .setOutPutY(800)
    .setStyle(CropImageView.Style.RECTANGLE)
    .setFocusWidth(displayMetrics.widthPixels)
    .setFocusHeight(displayMetrics.widthPixels)
    .setSaveRectangle(false)
    .build();
//（支持activity，app.fragment，v4.fragment 进入）
PictureCropActivity.startPictureCropActivity(MainActivity.this, pictureItemList.get(0).pictureAbsPath, cropOptions, CROP_IMG_REQUEST);
```

#### 获取裁剪后的图片

```
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (data != null && requestCode == CROP_IMG_REQUEST && resultCode == Activity.RESULT_OK) {
        String cropImgPath = data.getStringExtra(PictureCropActivity.EXTRA_NAME_CROP_IMG_PATH);
        //...
    }
}
```
#### 查看图片
```
//仅查看图片时
PicturePreviewActivity.startActivityWithOnlyPreview(...);

//预览且图片可删除时（支持activity，app.fragment，v4.fragment 进入）
PicturePreviewActivity.startActivityWithPreviewDel(...)
```
如果需要对图片进行长按或者自定义 ui，可自己创建 `Activity`，加入 `PreviewPictureFragment`，调用其监听方法即可，具体参见 demo 的实现。