package devin.com.picturepicker.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import devin.com.picturepicker.R;

public class BaseActivity extends AppCompatActivity {

    protected TitleBarHelper titleBarHelper;

    private LinearLayout titleBar;
    private FrameLayout flChildViewContent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        assignViews();
        titleBarHelper = new TitleBarHelper(titleBar);
        resetTitleBar(titleBarHelper);
    }


    private void assignViews() {
        titleBar = (LinearLayout) findViewById(R.id.titleBar);
        flChildViewContent = (FrameLayout) findViewById(R.id.fl_child_view_content);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(flChildViewContent.getLayoutParams());
        layoutParams.addRule(RelativeLayout.BELOW, R.id.titleBar);
        flChildViewContent.setLayoutParams(layoutParams);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        View view = getLayoutInflater().inflate(layoutResID, null);
        this.setContentView(view);
    }

    @Override
    public void setContentView(View view) {
        flChildViewContent.removeAllViews();
        flChildViewContent.addView(view);
    }


    /**
     * 子类可重写此方法重置利用TitleBarHelper对TitleBar进行处理
     *
     * @param titleBarHelper
     */
    protected void resetTitleBar(TitleBarHelper titleBarHelper) {
        titleBarHelper.getBackImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    static class TitleBarHelper {

        private View titleBarView;
        private ImageView ivBack;
        private TextView tvTitle;
        private Button btnComplete;
        private ImageView ivDelete;


        private TitleBarHelper(View titleBarView) {
            this.titleBarView = titleBarView;
            ivBack = (ImageView) titleBarView.findViewById(R.id.iv_back);
            tvTitle = (TextView) titleBarView.findViewById(R.id.tv_title);
            btnComplete = (Button) titleBarView.findViewById(R.id.btn_complete);
            ivDelete = (ImageView) titleBarView.findViewById(R.id.iv_delete);
        }

        public View getTitleBarView() {
            return titleBarView;
        }

        public ImageView getBackImageView() {
            return ivBack;
        }

        public TextView getTitleTextView() {
            return tvTitle;
        }

        public Button getCompleteButton() {
            return btnComplete;
        }

        public ImageView getIvDelete() {
            return ivDelete;
        }

    }

}
