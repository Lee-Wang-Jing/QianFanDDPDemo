package com.qianfan.qianfanddpdemo.ddp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.qianfan.qianfanddpdemo.MyApplication;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.base.BaseActivity;
import com.qianfan.qianfanddpdemo.event.VideoDelEvent;
import com.qianfan.qianfanddpdemo.utils.LogUtil;
import com.qianfan.qianfanddpdemo.utils.TimeUtils;
import com.qianfan.qianfanddpdemo.utils.ToastUtil;
import com.qianfan.qianfanddpdemo.widgets.MyMediaController;

import org.greenrobot.eventbus.EventBus;

/**
 * 盯盯拍视频播放页面
 * Created by wangjing on 2017/3/16.
 */

public class VideoViewActivity extends BaseActivity {
    private VideoView videoView;
    private RelativeLayout rel_top;
    private TextView tv_name;
    private TextView tv_share;
    private ImageView imv_jubao, imv_del, imv_back;

    private String path;
    private String name;
    private long startTime;

    private MyMediaController mediaController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoview);
        videoView = (VideoView) findViewById(R.id.videoView);
        rel_top = (RelativeLayout) findViewById(R.id.rel_top);
        imv_back = (ImageView) findViewById(R.id.imv_back);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_share = (TextView) findViewById(R.id.tv_share);
        imv_jubao = (ImageView) findViewById(R.id.imv_jubao);
        imv_del = (ImageView) findViewById(R.id.imv_del);


        path = getIntent().getStringExtra("path");
        name = getIntent().getStringExtra("name");
        startTime = getIntent().getLongExtra("startTime", 0);
        LogUtil.e("startTime", "" + startTime);
        LogUtil.e("path", "path==>" + path);
        if (TextUtils.isEmpty(path)) {
            Toast.makeText(this, "请先选择需要播放的视频", Toast.LENGTH_SHORT).show();
            return;
        }
        tv_name.setText("" + name);
        tv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.pause();
                long duration = videoView.getDuration() / 1000;
                LogUtil.e("imv_jubao", "" + duration);
                if (duration > 15) {//若视频超出15秒，则跳转至1.1.5.3.5.6-视频裁剪分享
                    LogUtil.e("视频时间", "若视频超出15秒，则跳转至1.1.5.3.5.6-视频裁剪分享");
                    Intent intent = new Intent(VideoViewActivity.this, VideoCropPaiActivity.class);
                    intent.putExtra("video_Path", "" + path);
                    intent.putExtra("date", startTime);
                    startActivity(intent);
//                    IntentUtils.jumpVideoCropPaiActivity(VideoViewActivity.this, path,startTime);
                    finish();
                } else if (duration < 5) {//若小于5秒，则弹窗提醒【该视频小于5秒，无法上传】
                    ToastUtil.TLong(VideoViewActivity.this, "该视频小于5秒，无法上传");
                } else {//若在5-15秒以内，则跳转至本地圈视频发布
                    ToastUtil.TLong(VideoViewActivity.this, "跳转至本地圈视频发布");
//                    IntentUtils.jumpPaiPublishVideoActivity(VideoViewActivity.this, path, 0L);
//                    finish();
                }
            }
        });
        imv_jubao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.pause();
//                if (!MyApplication.isLogin()){
//                    IntentUtils.jumpLogin(VideoViewActivity.this);
//                    return;
//                }
                if (TimeUtils.isThan48Hour(startTime)) {//若拍摄时间到上传时间超过48小时，提醒【该视频已超过48小时，不能进行违法举报】
                    ToastUtil.TLong(VideoViewActivity.this, "该视频已超过48小时，不能进行违法举报");
                } else {
                    long duration = videoView.getDuration() / 1000;
                    LogUtil.e("imv_jubao", "" + duration);
                    if (duration > 10) {//若视频超出10秒，则跳转至1.1.5.3.5.1-违法视频剪辑
                        LogUtil.e("视频时间", "若视频超出15秒，则跳转至1.1.5.3.5.1-违法视频剪辑");
                        Intent intent = new Intent(VideoViewActivity.this, VideoCropActivity.class);
                        intent.putExtra("video_Path", "" + path);
                        intent.putExtra("date", startTime);
                        startActivity(intent);
                        finish();
                    } else if (duration < 5) {//若小于5秒，则弹窗提醒【该视频小于5秒，无法上传】
                        ToastUtil.TLong(VideoViewActivity.this, "该视频小于5秒，无法上传");
                    } else {//若在5-15秒以内，则跳转至1.1.5.3.5-违法举报
                        Intent intent = new Intent(VideoViewActivity.this, WeiZhangJuBaoActivity.class);
                        intent.putExtra("time", startTime);
                        intent.putExtra("path", path);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
        imv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(VideoViewActivity.this);
                builder.setMessage("删除后不可恢复，确定删除吗？");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO 执行视屏删除逻辑
                        EventBus.getDefault().post(new VideoDelEvent());
                        dialog.dismiss();
                        finish();
                    }
                });
                builder.create().show();
            }
        });

        mediaController = new MyMediaController(this);
        videoView.setVideoPath("" + path);
        videoView.setMediaController(mediaController);
        videoView.start();
        videoView.requestFocus();
        mediaController.setOnWindowVisibilityChangedListener(new MyMediaController.onWindowVisibilityChangedListener() {
            @Override
            public void onWindowChangeListener(int visibility) {
                LogUtil.e("onWindowChangeListener", "==>" + visibility);
                if (visibility == View.VISIBLE) {
                    rel_top.setVisibility(View.VISIBLE);
                } else {
                    rel_top.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null) {
            videoView.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (videoView != null) {
            videoView.stopPlayback();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
