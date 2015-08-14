package com.mckuai.imc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mckuai.bean.SkinItem;
import com.mckuai.widget.ProgressButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.nio.channels.NonWritableChannelException;

public class SkinDetailedActivity extends BaseActivity {
    private SkinItem item;

    private ProgressButton btn_operation;
    private TextView tv_skinNmae;
    private TextView tv_skinOwner;
    private TextView tv_skinRank;
    private TextView tv_desc;
    private ImageView iv_skinCover;
    private LinearLayout ll_pics;
    private ImageView btn_return;
    private ImageView btn_share;

    private ImageLoader mLoader;
    private DisplayImageOptions mOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin_detailed);
        setTitle("皮肤详情");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        item = (SkinItem) intent.getSerializableExtra("SKIN_ITEM");
        if (null != item) {
            showData();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void showData() {
        if (null == mLoader) {
            mLoader = ImageLoader.getInstance();
            mOptions = MCkuai.getInstance().getNormalOption();
            initView();
        }
        showPics();
        mLoader.displayImage(item.getIcon(),iv_skinCover);
        tv_skinNmae.setText(item.getViewName() + "");
        tv_skinOwner.setText(item.getUploadMan() + "");
        tv_skinRank.setText("下载：0");
        tv_desc.setText(item.getDesc()+"");
    }

    private void showPics() {
        if (null != item.getPics() && 10 < item.getPics().length()) {
            String[] pics = item.getPics().split(",");
            if (1 == pics.length) {
                //只有一张图
                ImageView imageView = new ImageView(this);
                mLoader.displayImage(item.getPics(), imageView, mOptions, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
                /*LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,20,0,20);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(params);
                mLoader.displayImage(item.getPics(),imageView);*/
                ll_pics.removeAllViews();
                ll_pics.addView(imageView);
            } else {
                //有多张图

            }
        }
    }

    private void initView() {
        btn_operation = (ProgressButton) findViewById(R.id.btn_operation);
        tv_skinNmae = (TextView) findViewById(R.id.tv_skinname);
        tv_skinRank = (TextView) findViewById(R.id.tv_skinrank);
        tv_skinOwner = (TextView) findViewById(R.id.tv_skinowner);
        tv_desc = (TextView) findViewById(R.id.tv_skindes);
        iv_skinCover = (ImageView) findViewById(R.id.iv_skincover);
        ll_pics = (LinearLayout) findViewById(R.id.ll_skinpics);
        btn_return = (ImageView) findViewById(R.id.btn_left);
        btn_share = (ImageView) findViewById(R.id.btn_right);
        btn_share.setImageResource(R.drawable.btn_titlebar_share);
        btn_share.setVisibility(View.VISIBLE);
    }
}
