package com.qianfan.qianfanddpdemo.ddp.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.ddp.sdk.base.DDPSDK;
import com.ddp.sdk.cambase.utils.VTask;
import com.ddp.sdk.video.operation.VideoOperateServer;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.base.BaseActivity;
import com.qianfan.qianfanddpdemo.common.AppConfig;
import com.qianfan.qianfanddpdemo.utils.DensityUtils;
import com.qianfan.qianfanddpdemo.utils.ImageLoader;
import com.qianfan.qianfanddpdemo.utils.LogUtil;
import com.qianfan.qianfanddpdemo.utils.ToastUtil;
import com.qianfan.qianfanddpdemo.widgets.FileUtils;
import com.qianfan.qianfanddpdemo.widgets.MyMediaController;
import com.qianfan.qianfanddpdemo.widgets.VideoCropSeekBar;
import com.vyou.app.sdk.utils.VLog;

import java.io.File;
import java.util.Arrays;

/**
 * 盯盯拍-视频裁减(交通违法举报使用)
 * Created by wangjing on 2017/3/21.
 */

public class VideoCropActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = VideoCropActivity.class.getSimpleName();

    private static final String RESULT_KEY_CROP_SELECT = "result_key_crop_select";  // 截取选中视频，查询操作结果
    private static final String RESULT_KEY_DEL_SELECT = "result_key_del_select";    // 删除选中视频，查询操作结果
    private static final String RESULT_KEY_CAPTURE_PICS = "result_key_capture_pics";      // 截取图片数组，查询操作结果

    private SimpleDraweeView simpleDraweeView;
    private FrameLayout fl_normal;
    private TextView tv_next;
    private TextView tv_jiequxuanzhong, tv_shanchuxuanzhong;
    private Toolbar toolbar;

    private VideoCropSeekBar videoCropSeekbar;

    // 播放器view
    private VideoView videoView;
    private MyMediaController mediaController;

    // 摄像机服务类对象
//    private CameraServer cameraServer;
//    private CameraResMgr cameraResMgr;
//    private Camera cam;


    // 播放控制器
    private VideoOperateServer videoServer;

    private ProgressDialog progressDialog;

    private String outPutVideoPath; // 输出的视频路径
    private String lastResultKey;   // 最近一次操作的key

    private String videoPath; // 视频路径
    private long totalTime;
    private long startTime;
    private long endTime;
    private long date;
    private boolean isActivityDestory = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWhite(R.layout.activity_videocrop);
        DDPSDK.init(this, "");
        initP();
        initView();
        initData();
        getImagesFromVideo();
    }


    private void initP() {
//        cameraServer = CameraServer.intance();
//        cameraResMgr = CameraResMgr.instance();
        videoServer = VideoOperateServer.instance();

//        cam = cameraServer.getCurrentConnectCamera();
//        if (cam == null || !cam.isConnected) {
//            LogUtil.e(TAG, "cam == null || !cam.isConnected, finish");
//            finish();
//        }
        videoPath = getIntent().getStringExtra("video_Path");
        date = getIntent().getLongExtra("date", 0L);
        LogUtil.e(TAG, "videoPath = " + videoPath);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        simpleDraweeView = (SimpleDraweeView) findViewById(R.id.simpleDraweeView);
        fl_normal = (FrameLayout) findViewById(R.id.fl_normal);
        tv_next = (TextView) findViewById(R.id.tv_next);
        videoCropSeekbar = (VideoCropSeekBar) findViewById(R.id.videoCropSeekbar);
        tv_jiequxuanzhong = (TextView) findViewById(R.id.tv_jiequxuanzhong);
        tv_shanchuxuanzhong = (TextView) findViewById(R.id.tv_shanchuxuanzhong);
        videoView = (VideoView) findViewById(R.id.videoView);
        videoCropSeekbar = (VideoCropSeekBar) findViewById(R.id.videoCropSeekbar);
    }

    private void initData() {
        setBaseBackToolbar(toolbar, "编辑视频");
        videoView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtils.getScreenWidth(this) / 16 * 9));
        tv_next.setOnClickListener(this);
        fl_normal.setOnClickListener(this);
        tv_jiequxuanzhong.setOnClickListener(this);
        tv_shanchuxuanzhong.setOnClickListener(this);
        ImageLoader.load(simpleDraweeView, "file://" + videoPath);

        mediaController = new MyMediaController(this);
        totalTime = videoServer.getVideoDuration(videoPath);
        startTime = 0;
        endTime = totalTime;
//        videoCropSeekbar.updateVideoInfo(videoPath, totalTime);
        videoCropSeekbar.setVideo(videoPath, totalTime);
        videoCropSeekbar.setSeekListener(new VideoCropSeekBar.OnSeekListener() {
            @Override
            public void onSeekProgessChanged(VideoCropSeekBar seekBar, int leftProgress, int rightProgress, boolean fromUser) {

            }

            @Override
            public void onSeekTimeChanged(VideoCropSeekBar seekBar, long leftSeekTime, long rightSeekTime, boolean fromUser) {
                startTime = leftSeekTime;
                endTime = rightSeekTime;
                VLog.v(TAG, "startTime = " + startTime + ", rightSeekTime = " + rightSeekTime + ", totalTime = " + totalTime);
            }

            @Override
            public void onStartTrackingSeek(VideoCropSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingSeek(VideoCropSeekBar seekBar, long leftSeekTime, long rightSeekTime) {

            }
        });
        videoView.setVideoPath("" + videoPath);
        videoView.setMediaController(mediaController);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next:
                ToastUtil.TLong(this,"请先登录");
                return;
//                if (!MyApplication.isLogin()){
//                    IntentUtils.jumpLogin(this);
//                    return;
//                }
//                LogUtil.e("totalTime", "totalTime==>" + totalTime);
//                if (totalTime > 10000) {
//                    ToastUtil.TShort(this, "视频超过10秒，请截取片段");
//                    return;
//                }
//                if (totalTime < 5000 ) {
//                    ToastUtil.TShort(this, "视频不得小于5秒");
//                    return;
//                }
//                isActivityDestory = true;
//                Intent intent = new Intent(this, WeiZhangJuBaoActivity.class);
//                intent.putExtra("time", date);
//                intent.putExtra("path", videoPath);
//                startActivity(intent);
//                finish();
//                break;
            case R.id.fl_normal:
                videoView.setVisibility(View.VISIBLE);
                fl_normal.setVisibility(View.GONE);
                videoView.start();
                videoView.requestFocus();
                break;
            case R.id.tv_jiequxuanzhong://截取选中
                jiequxuanzhong();
                break;
            case R.id.tv_shanchuxuanzhong://删除选中
                shanchuxuanzhong();
                break;
            default:
                break;
        }
    }


    /**
     * 截取选中视频
     */
    private void jiequxuanzhong() {
        if (!FileUtils.SDCardCanUse()) {
            ToastUtil.TLong(this, "sd卡不可用");
            return;
        }
        showWaitProgress("正在截取选中视频……");
        outPutVideoPath = FileUtils.getFileStringByPath(AppConfig.TEMP + "video") + File.separator + "crop_" + System.currentTimeMillis() + ".mp4";
//        outPutVideoPath = DDPSDK.getSDKResRootDir() + File.separator + "video/crop_" + System.currentTimeMillis() + ".mp4";
        new com.ddp.sdk.base.tools.VTask<Object, Integer>() {
            @Override
            protected void doPre() {
                super.doPre();
            }

            @Override
            protected Integer doBackground(Object o) {
                lastResultKey = RESULT_KEY_CROP_SELECT;
                return videoServer
                        .splitVideo(startTime, endTime, totalTime, videoPath, outPutVideoPath, true, RESULT_KEY_CROP_SELECT);
            }

            @Override
            protected void doPost(Integer rst) {
                hideWaitProgress();
                long videoDuration = videoServer.getVideoDuration(outPutVideoPath);
                String videoResolution = videoServer.getVideoResolution(outPutVideoPath);
                VLog.v(TAG, "截取视频时间段 rst = " + rst + ", outPutVideoPath = " + outPutVideoPath + ", videoDuration = " + videoDuration + ", videoResolution = " + videoResolution);
                VLog.v(TAG, "getOperateResult RESULT_KEY_CROP_SELECT = " + videoServer.getOperateResult(RESULT_KEY_CROP_SELECT));
                if (!isActivityDestory) {
                    if (rst == 0) {
                        setVideoAgain();
                        Toast.makeText(VideoCropActivity.this, "截取成功。", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(VideoCropActivity.this, "截取失败。", Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
    }

    /**
     * 截取或者裁剪视频成功之后重新设置视频数据
     */
    private void setVideoAgain() {
        videoPath = outPutVideoPath;
        totalTime = videoServer.getVideoDuration(videoPath);
        startTime = 0;
        endTime = totalTime;
        videoCropSeekbar.setVideo(videoPath, totalTime);
        videoCropSeekbar.initSeekbar();
        videoView.setVideoPath("" + videoPath);
        videoView.setVisibility(View.VISIBLE);
        fl_normal.setVisibility(View.GONE);
        videoView.start();
        getImagesFromVideo();
    }

    /**
     * 删除选中视频
     */
    private void shanchuxuanzhong() {
        if (!FileUtils.SDCardCanUse()) {
            ToastUtil.TLong(this, "sd卡不可用");
            return;
        }
        showWaitProgress("正在删除选中视频……");
        outPutVideoPath = FileUtils.getFileStringByPath(AppConfig.TEMP + "video") + File.separator + "del_" + System.currentTimeMillis() + ".mp4";
//        outPutVideoPath = DDPSDK.getSDKResRootDir() + File.separator + "video/test_del.mp4";

        new com.ddp.sdk.base.tools.VTask<Object, Integer>() {
            @Override
            protected void doPre() {
                super.doPre();
                showWaitProgress(null);
            }

            @Override
            protected Integer doBackground(Object o) {
                lastResultKey = RESULT_KEY_DEL_SELECT;
                return videoServer
                        .splitVideo(startTime, endTime, totalTime, videoPath, outPutVideoPath, false, RESULT_KEY_DEL_SELECT);
            }

            @Override
            protected void doPost(Integer rst) {
                hideWaitProgress();
                long videoDuration = videoServer.getVideoDuration(outPutVideoPath);
                String videoResolution = videoServer.getVideoResolution(outPutVideoPath);
                VLog.v(TAG, "删除视频时间段 rst = " + rst + ", outPutVideoPath = " + outPutVideoPath + ", videoDuration = " + videoDuration + ", videoResolution = " + videoResolution);

                VLog.v(TAG, "getOperateResult RESULT_KEY_DEL_SELECT = " + videoServer.getOperateResult(RESULT_KEY_DEL_SELECT));

                if (rst == 0) {
                    setVideoAgain();
                    Toast.makeText(VideoCropActivity.this, "删除成功。", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(VideoCropActivity.this, "删除失败。", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    /**
     * 通过视频获取缩略图
     * videoServer.getOperateResult 0:空闲等待状态 1：正在处理状态 2：处理完成状态 3：处理失败状态
     * 获取视频截图,并返回截图文件列表名称
     * fps       截图时间间隔（秒）如 5秒 ，对应每五秒截一张，频率=1/fps =0.2张一秒
     * inputFile 视频文件路径
     * outPutDir 截图输出目录(可为null,此时在同级目录下创建一个子目录)
     * resultKey 可以通过这个key来查找操作结果，这个key由用户自己定义和存储
     */
    private void getImagesFromVideo() {
//        showProgressDialog("正在获取视频截图……");
        totalTime = videoServer.getVideoDuration(videoPath);
        LogUtil.e("getImageFromVideo", "totalTime==>" + totalTime + " totalTime / 10==>" + totalTime / 10);
        final String outPutPicPath = FileUtils.getFileNameFromPath(AppConfig.TEMP + "");
        new VTask<Object, String[]>() {

            @Override
            protected String[] doBackground(Object o) {
                try {
                    return videoServer.extractImgsFromVideo(totalTime / 10, videoPath, outPutPicPath, null, RESULT_KEY_CAPTURE_PICS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new String[0];
            }

            @Override
            protected void doPost(final String[] strings) {
                LogUtil.e("getImageFromVideo", "截取视频图片数组 strings.length = " + strings.length);
                LogUtil.e("getImageFromVideo", "getOperateResult RESULT_KEY_CAPTURE_PICS = " + videoServer.getOperateResult(RESULT_KEY_CAPTURE_PICS));
                if (strings.length > 0) {
                    LogUtil.e("getImageFromVideo", outPutPicPath + "截取视频图片成功");
                    if (!isActivityDestory) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    videoCropSeekbar.fillVedioThumb(Arrays.asList(strings));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } else {
                    if (!isActivityDestory) {
                        Toast.makeText(VideoCropActivity.this, "截取视频图片失败1", Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
    }


    private void showWaitProgress(String title) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        if (TextUtils.isEmpty(title)) {
            progressDialog.setMessage("请稍候……");
        } else {
            progressDialog.setMessage("" + title);
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void hideWaitProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
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
    protected void onDestroy() {
        isActivityDestory = true;
        super.onDestroy();
    }
}
