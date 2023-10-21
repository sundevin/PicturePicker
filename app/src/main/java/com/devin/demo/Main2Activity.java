package com.devin.demo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import com.devin.picturepicker.adapter.PicturePreviewAdapter;
import com.devin.picturepicker.fragment.PreviewPictureFragment;
import com.devin.picturepicker.javabean.PictureItem;
import com.devin.picturepicker.utils.PictureLangUtils;
import com.devin.picturepicker.utils.Utils;

public class Main2Activity extends AppCompatActivity {


    public static void startActivity(Activity activity, List<PictureItem> pictureItemList) {
        Intent intent = new Intent(activity, Main2Activity.class);
        intent.putExtra("pictureItemList", (Serializable) pictureItemList);
        activity.startActivity(intent);
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = PictureLangUtils.setLanguage(newBase, MyApplication.locale);
        super.attachBaseContext(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Utils.hideStatusBar(this, true);
        PreviewPictureFragment fragment = (PreviewPictureFragment) getFragmentManager().findFragmentById(R.id.fragment);
        List<PictureItem> pictureItemList = (List<PictureItem>) getIntent().getSerializableExtra("pictureItemList");
        fragment.setPictureItems(pictureItemList, 0);
        fragment.setOnPictureLongClickListener(new PicturePreviewAdapter.OnPictureLongClickListener() {
            @Override
            public boolean onPictureLongClick(int position, final String url) {
                new AlertDialog.Builder(Main2Activity.this)
                        .setItems(new CharSequence[]{"下载", "取消"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(Main2Activity.this, "模拟下载--" + url, Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                return true;
            }
        });
    }
}
