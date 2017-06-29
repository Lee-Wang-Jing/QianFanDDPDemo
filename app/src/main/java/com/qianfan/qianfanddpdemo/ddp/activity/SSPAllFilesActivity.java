package com.qianfan.qianfanddpdemo.ddp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddp.sdk.base.DDPSDK;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.base.BaseActivity;
import com.qianfan.qianfanddpdemo.ddp.adapter.SSPAllFilesAdapter;
import com.qianfan.qianfanddpdemo.myinterface.OnNoFileListener;
import com.qianfan.qianfanddpdemo.utils.LogUtil;

/**
 * 盯盯拍所有文件页面
 * Created by wangjing on 2017/1/11.
 */

public class SSPAllFilesActivity extends BaseActivity implements OnNoFileListener {
    private static final String TAG = "SSPAllFilesActivity";

    private static final String[] mTitle = {"图片", "视频", "紧急"};


    private ImageView imv_back;
    private static TextView tv_bianji;
    private TextView tv_cancel, tv_selectAll;
    private ViewPager viewpager;
    private TabLayout tablayout;
    private SSPAllFilesAdapter mAdapter;
    private static boolean isEdit = false;//是否是编辑状态
    private static boolean isAll = false;//是否是全选状态

    private int albumId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWhite(R.layout.activity_sspallfiles);
        albumId = getIntent().getIntExtra("albumId", 0);
        initView();
    }

    private void initView() {
        imv_back = (ImageView) findViewById(R.id.imv_back);
        tv_bianji = (TextView) findViewById(R.id.tv_bianji);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_selectAll = (TextView) findViewById(R.id.tv_selectAll);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        tablayout = (TabLayout) findViewById(R.id.tablayout);


        // SDK的初始化必须在主线程中运行,必须先初始化SDK，后面的才能操作,AndroidManifest文件需要声明 DDP_APPKEY 和 DDP_APPSECRET 才能通过认证
        DDPSDK.init(this, "");

        mAdapter = new SSPAllFilesAdapter(getSupportFragmentManager(), mTitle, albumId, this);
        viewpager.setAdapter(mAdapter);
        viewpager.setOffscreenPageLimit(3);
        tablayout.setupWithViewPager(viewpager);
        imv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressedSupport();
            }
        });
        tv_bianji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e("onClick", "tv_bianji");
                if (mAdapter.hasData(viewpager.getCurrentItem()) <= 1) {
                    return;
                }
                if (isEdit == true) {
                    isEdit = false;
                    isAll = false;
                    tv_bianji.setText("编辑");
                    tv_cancel.setVisibility(View.GONE);
                    tv_selectAll.setVisibility(View.GONE);
                    imv_back.setVisibility(View.VISIBLE);
                    tv_bianji.setVisibility(View.VISIBLE);
                } else {
                    isEdit = true;
                    isAll = false;
                    tv_bianji.setVisibility(View.GONE);
                    imv_back.setVisibility(View.GONE);
                    tv_cancel.setVisibility(View.VISIBLE);
                    tv_selectAll.setVisibility(View.VISIBLE);
                    tv_selectAll.setText("全选");
                }
                mAdapter.setEdit(viewpager.getCurrentItem(), isEdit, isAll);
            }
        });
        tv_selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.e("onClick", "tv_selectAll");
                LogUtil.e("onClick", "aa isAll==>" + isAll);
                isEdit = true;
                if (isAll) {
                    isAll = false;
                    tv_selectAll.setText("全选");
                } else {
                    isAll = true;
                    tv_selectAll.setText("取消\n全选");
                }
                LogUtil.e("onClick", "bb isAll==>" + isAll);
                mAdapter.setEdit(viewpager.getCurrentItem(), isEdit, isAll);
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEdit = false;
                isAll = false;
                tv_cancel.setVisibility(View.GONE);
                tv_selectAll.setVisibility(View.GONE);
                imv_back.setVisibility(View.VISIBLE);
                tv_bianji.setVisibility(View.VISIBLE);
                tv_bianji.setText("编辑");
                mAdapter.setEdit(viewpager.getCurrentItem(), isEdit, isAll);
            }
        });
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (isEdit) {
                    isEdit = false;
                    isAll = false;
                    tv_cancel.setVisibility(View.GONE);
                    tv_selectAll.setVisibility(View.GONE);
                    imv_back.setVisibility(View.VISIBLE);
                    tv_bianji.setVisibility(View.VISIBLE);
                    tv_bianji.setText("编辑");
                    for (int i = 0; i < 3; i++) {
                        mAdapter.setEdit(i, false, false);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewpager.setCurrentItem(1);
    }

    public int getAlbumId() {
        return albumId;
    }

    @Override
    public void noFileListener() {
        isEdit = false;
        isAll = false;
        tv_cancel.setVisibility(View.GONE);
        tv_selectAll.setVisibility(View.GONE);
        imv_back.setVisibility(View.VISIBLE);
        tv_bianji.setVisibility(View.VISIBLE);
        tv_bianji.setText("编辑");
    }

    @Override
    public void noSelectAll() {
        isAll = false;
        tv_selectAll.setText("全选");
    }

}
