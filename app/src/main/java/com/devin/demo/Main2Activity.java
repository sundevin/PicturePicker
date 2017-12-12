package com.devin.demo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import devin.com.picturepicker.fragment.PreviewPictureFragment;
import devin.com.picturepicker.adapter.PicturePreviewAdapter;
import devin.com.picturepicker.javabean.PictureItem;
import devin.com.picturepicker.utils.Utils;

public class Main2Activity extends AppCompatActivity {


    public static void startActivity(Activity activity, List<PictureItem> pictureItemList) {
        Intent intent = new Intent(activity, Main2Activity.class);
        intent.putExtra("pictureItemList", (Serializable) pictureItemList);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Utils.hideStatusBar(this, true);
        PreviewPictureFragment fragment = (PreviewPictureFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        List<PictureItem> pictureItemList = (List<PictureItem>) getIntent().getSerializableExtra("pictureItemList");
        fragment.setPictureItems(pictureItemList, 0);
        fragment.setOnPictureLongClickListener(new PicturePreviewAdapter.OnPictureLongClickListener() {
            @Override
            public boolean onPictureLongClick(int position, final String url) {
                new AlertDialog.Builder(Main2Activity.this)
                        .setItems(new CharSequence[]{"下载", "取消"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(Main2Activity.this, "下载--" + url, Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                return true;
            }
        });
    }
}
