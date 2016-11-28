# PicturePicker
一个高仿微信朋友圈图片选择的项目

项目为高仿微信图片选择模块，目前可实现图片的单选，多选，拍照，预览，选择后删除等  

项目参考了其他类似开源项目的部分实现，并针对一些边界问题做了修复，例如初始时无图片或者图片过多导致的问题，并针对特殊机型的拍照carsh问题做了修复，代码更加简洁。  

后期会加入图片裁剪功能。  

 欢迎star 和 Fork,有优化建议和bug可以提issue,我会及时处理。有好的需求，我也会考虑加入。
 
###效果演示：  
 
 ![效果](https://raw.githubusercontent.com/sundevin/PicturePicker/master/Screenshot/0.gif)


###使用：

####适用版本 minSdkVersion 14及以上

#### 1，导入依赖库 picturepicker
 
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



