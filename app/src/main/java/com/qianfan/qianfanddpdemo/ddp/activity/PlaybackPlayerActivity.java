package com.qianfan.qianfanddpdemo.ddp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.ddp.sdk.base.DDPSDK;
import com.ddp.sdk.cam.resmgr.CameraResMgr;
import com.ddp.sdk.cam.resmgr.listener.onEventDownloadListener;
import com.ddp.sdk.cam.resmgr.model.BaseFile;
import com.ddp.sdk.cam.resmgr.model.EventVideo;
import com.ddp.sdk.cambase.CameraServer;
import com.ddp.sdk.cambase.model.Camera;
import com.ddp.sdk.cambase.model.FileLoadTask;
import com.ddp.sdk.cambase.model.PlayFile;
import com.ddp.sdk.cambase.network.NetworkMgr;
import com.ddp.sdk.cambase.utils.VTask;
import com.ddp.sdk.player.controller.PlayerControllerFactory;
import com.ddp.sdk.player.controller.VLiveAndPlaybackController;
import com.ddp.sdk.player.view.VVideoPlayerView;
import com.ddp.sdk.video.operation.VideoOperateServer;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.base.BaseDDPActivity;
import com.qianfan.qianfanddpdemo.dialog.DDPVideoDownLoadDialog;
import com.qianfan.qianfanddpdemo.utils.DensityUtils;
import com.qianfan.qianfanddpdemo.utils.ImageLoader;
import com.qianfan.qianfanddpdemo.utils.LogUtil;
import com.qianfan.qianfanddpdemo.utils.ToastUtil;
import com.vyou.app.sdk.player.VPlayerConst;
import com.vyou.app.sdk.player.utils.VPlayerUtil;
import com.vyou.app.sdk.utils.VLog;
import com.vyou.app.sdk.widget.WeakHandler;

import org.videolan.libvlc.EventHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 盯盯拍-查看回放视频
 * Created by wangjing on 2017/1/10.
 */
public class PlaybackPlayerActivity extends BaseDDPActivity implements View.OnClickListener {
    private static final String TAG = "PlaybackPlayerActivity";

    private Context context;

    // 这些操作结果的key由用户自己定义，自己保存
    private static final String RESULT_KEY_CAPTURE_PIC = "result_key_capture_pic";      // 截取图片，查询操作结果
//    private String lastResultKey;   // 最近一次操作的key


    private Toolbar toolbar;
    private SimpleDraweeView simpleDraweeView;
    private FrameLayout fl_normal;
    private TextView tv_shipinjietu, tv_shipinjianji, tv_download;

    private String outPutPicPath; // 截取当前正在播放的画面输出的图片路径

    // 摄像机服务类对象
    private CameraServer cameraServer;
    private CameraResMgr cameraResMgr;
    private Camera cam;

    private NetworkMgr networkMgr;

    private MediaController mediaController;
    private VideoView videoView;
    // 播放器view
    private VVideoPlayerView videoPlayerView;
    // 播放控制器
    private VLiveAndPlaybackController playerController;

    //    private View waitProgress;
    private ProgressDialog progressDialog;
    private DDPVideoDownLoadDialog ddpVideoDownLoadDialog;

//    private static int surfaceViewWithPortrait;    // 竖屏时的surfaceView的长度
//    private static int surfaceViewHeightPortrait;   // 竖屏时的surfaceView的高度
//    private static int surfaceViewWithLandscape;    // 横屏时的surfaceView的长度
//    private static int surfaceViewHeightLandscape;   // 横屏时的surfaceView的高度

    private DisplayMetrics disp;

    private PlayFile playFile;

    private long playbackTime;
    private String filename;
    private String filePath;
    private String videoPath;
//    private long backtime;

    private EventVideo playbackVideo;

    final List<BaseFile> downloadlist = new ArrayList<>();

    private VideoOperateServer videoServer;

    /**
     * 裁剪的视频
     */
//    private EventVideo cropVideo;

    private onEventDownloadListener playbackDownloadLsn;

    private WeakHandler<PlaybackPlayerActivity> uiHandler = new WeakHandler<PlaybackPlayerActivity>(this) {
        @Override
        public void handleMessage(Message msg) {
            VLog.v(TAG, "msg.what = " + msg.what);
            switch (msg.what) {
                case EventHandler.MediaPlayerBuffering:
                    //hideWaitProgress();
                    break;
                // 画面出来了
                case EventHandler.MediaPlayerVout:
                    hideWaitProgress();
                    break;
                // 画面出来了
                case VPlayerConst.CMD_VIDEO_OUT:
                    VLog.v(TAG, "VPlayerConst.CMD_VIDEO_OUT : " + msg.what);
                    hideWaitProgress();
                    break;
                // 播放异常
                case EventHandler.MediaPlayerEncounteredError: {
                    VLog.v(TAG, "EventHandler.MediaPlayerEncounteredError");
                    hideWaitProgress();
                    ToastUtil.TShort(PlaybackPlayerActivity.this,"播放异常");
                    break;
                }
                //单个回放文件播放结束
                case EventHandler.MediaPlaySingleEnd: {
                    VLog.v(TAG, "EventHandler.MediaPlaySingleEnd");
                    playerController.stop();
                    videoPlayerView.setVisibility(View.GONE);
                    fl_normal.setVisibility(View.VISIBLE);
                    break;
                }

                default:
                    break;

            }
        }
    };

    private void registPalyerEventHandler(EventHandler eventHandler) {
        eventHandler.addHandler(uiHandler);
    }

    private void unRegistPlayEventHandler(EventHandler eventHandler) {
        eventHandler.removeHandler(uiHandler);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWhite(R.layout.layout_playbackplayer_activity);

        initP();
        initView();
        initData();
    }

    private void initP() {
        DDPSDK.init(this, "");
        context = getBaseContext();
        cameraServer = CameraServer.intance();
        cameraResMgr = CameraResMgr.instance();
        networkMgr = NetworkMgr.instance();
        videoServer = VideoOperateServer.instance();

        cam = cameraServer.getCurrentConnectCamera();
        if (cam == null || !cam.isConnected || !networkMgr.isCameraWifiConnected(cam)) {
            VLog.e(TAG, "cam == null || !cam.isConnected, finish");
            ToastUtil.TLong(this, "盯盯拍已经断开连接，请连接后再试");
            finish();
        }

        disp = VPlayerUtil.getDisplaySize(context);

        playbackTime = getIntent().getLongExtra("playbacktime", -1);
        filename = getIntent().getStringExtra("filename");
        filePath = getIntent().getStringExtra("filePath");
        LogUtil.e("playbackTime", "playbackTime_P = " + playbackTime);

        cameraServer.addCameraStateChangeListener(camLifeCycleListener);
    }


    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        simpleDraweeView = (SimpleDraweeView) findViewById(R.id.simpleDraweeView);
        fl_normal = (FrameLayout) findViewById(R.id.fl_normal);
        tv_shipinjietu = (TextView) findViewById(R.id.tv_shipinjietu);
        tv_shipinjianji = (TextView) findViewById(R.id.tv_shipinjianji);
        tv_download = (TextView) findViewById(R.id.tv_download);

        videoView = (VideoView) findViewById(R.id.videoView);
        videoPlayerView = (VVideoPlayerView) findViewById(R.id.videoPlayerView);

        mediaController = new MediaController(this);
        setBaseBackToolbar(toolbar, "" + filename);
        ImageLoader.load(simpleDraweeView, "" + filePath);

        videoView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtils.getScreenWidth(this) / 16 * 9));
        videoView.setMediaController(mediaController);
        // 获取播放控制器
        playerController = (VLiveAndPlaybackController) videoPlayerView.getPlayerController(PlayerControllerFactory.TYPE_PLAYER_LIVE);
        // 给播放控制器设置摄像机
        playerController.setCamera(cam);

    }

    private void initData() {
        registPalyerEventHandler(EventHandler.getInstance());
        fl_normal.setOnClickListener(this);
        tv_shipinjianji.setOnClickListener(this);
        tv_shipinjietu.setOnClickListener(this);
        tv_download.setOnClickListener(this);

        playbackDownloadLsn = new onEventDownloadListener() {
            @Override
            public void onStart(BaseFile file, FileLoadTask downloadTask) {
                VLog.v(TAG, "onStart");
            }

            @Override
            public void onLoad(final BaseFile file, final FileLoadTask downloadTask) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showDownProgress((int) (downloadTask.loadLength * 1.0f / downloadTask.length * 100));
//                        showWaitProgress("正在下载" + (int) (downloadTask.loadLength * 1.0f / downloadTask.length * 100) + "%");
                    }
                });
                LogUtil.e("onLoad", "百分比==>" + (int) (downloadTask.loadLength * 1.0f / downloadTask.length * 100) + "%");
            }

            @Override
            public void onCancel(BaseFile file, FileLoadTask downloadTask) {
                VLog.v(TAG, "onCancel");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideDownProgress();
//                        hideWaitProgress();
                    }
                });

            }

            @Override
            public void onException(final BaseFile file, FileLoadTask downloadTask, Exception e) {
                VLog.v(TAG, "onException");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        hideWaitProgress();
                        hideDownProgress();
                        Toast.makeText(context, "下载失败", Toast.LENGTH_LONG).show();
                        LogUtil.i(TAG, "下载成功。 filePath = " + file.filePath);
                    }
                });

            }

            @Override
            public void onFinish(final BaseFile file, FileLoadTask downloadTask) {
                VLog.v(TAG, "onFinish");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        hideWaitProgress();
                        hideDownProgress();
                        videoPath = file.filePath;
                        playerController.stop();
                        videoPlayerView.setVisibility(View.GONE);
                        fl_normal.setVisibility(View.GONE);
                        videoView.setVisibility(View.VISIBLE);
                        videoView.setVideoPath(videoPath);
                        videoView.start();
                        videoView.requestFocus();
                        Toast.makeText(context, "已下载到所有文件中", Toast.LENGTH_LONG).show();
                        LogUtil.i(TAG, "下载成功。 filePath = " + file.filePath);
                    }
                });

            }
        };

        try {
            cameraResMgr.addEventDownloadListener(playbackDownloadLsn);
            // 从起始时间找到对应的回放文件
            // 需要找到回放列表
            playFile = cameraResMgr.getPlayFileByStartTime(cam, playbackTime);
           LogUtil.e(TAG, "playFile = " + playFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // 获取回放列表
        new VTask<Object, List<PlayFile>>() {
            @Override
            protected List<PlayFile> doBackground(Object o) {
                List<PlayFile> playFileList = cameraServer.getResPlayFiles(cam);

                Collections.sort(playFileList, new Comparator<PlayFile>() {
                    @Override
                    public int compare(PlayFile thisFile, PlayFile otherFile) {
                        if (thisFile.start > otherFile.start) {
                            return 1;
                        } else if (thisFile.start < otherFile.start) {
                            return -1;
                        }

                        return 0;
                    }
                });
                return playFileList;
            }

            @Override
            protected void doPost(List<PlayFile> list) {
                // TODO : 这里需要整改，回放列表需要统一管理
                if (list.size() > 0) {
                    playerController.setPlaybackList(cam, list);
                }
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (playerController != null) {
            // 离开回放界面时，要通知摄像机停止回放模式。
            playerController.play(CameraServer.LIVE_SWITCH_TYPE, -1);
            playerController.stop();
        }
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_normal:
                if (!checkDDPisConnect()) {
                    Toast.makeText(context, "盯盯拍已经断开连接，请重新连接盯盯拍", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(videoPath)) {
                    showWaitProgress(null);
                    videoView.setVisibility(View.GONE);
                    videoPlayerView.setVisibility(View.VISIBLE);
                    fl_normal.setVisibility(View.GONE);
                    playerController.play(CameraServer.PLAYBACK_SWITCH_TYPE, playbackTime);
                } else {
                    videoView.setVisibility(View.VISIBLE);
                    videoPlayerView.setVisibility(View.GONE);
                    fl_normal.setVisibility(View.GONE);
                    videoView.setVideoPath(videoPath);
                    videoView.start();
                    videoView.requestFocus();
                }
                break;
            case R.id.tv_shipinjietu:
                LogUtil.e("onClick", "tv_shipinjietu");
                if (TextUtils.isEmpty(videoPath)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("视频截图和视频剪辑需要先下载视频才能操作哦！");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (cameraResMgr != null) {
                                cameraResMgr.cancelDownload(downloadlist);
                            }
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            downloadVideo();
                        }
                    });
                    builder.create().show();
                } else {
                    shipinjietu();
                }
                break;
            case R.id.tv_shipinjianji:
                LogUtil.e("onClick", "tv_shipinjianji");
                playerController.pause();
                if (TextUtils.isEmpty(videoPath)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("视频截图和视频剪辑需要先下载视频才能操作哦！");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (cameraResMgr != null) {
                                cameraResMgr.cancelDownload(downloadlist);
                            }
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            downloadVideo();
                        }
                    });
                    builder.create().show();
                } else {
                    Intent intent = new Intent(this, VideoCropBackActivity.class);
                    intent.putExtra("video_Path", "" + videoPath);
                    intent.putExtra("date", playbackTime);
                    intent.putExtra("filename", "" + filename);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.tv_download:
                if (TextUtils.isEmpty(videoPath)) {
                    downloadVideo();
                } else {
                    ToastUtil.TShort(this, videoPath + "视频已下载");
                }
                break;
            default:
                break;
        }
    }


    /**
     * 下载回放视频到本地
     */
    private void downloadVideo() {
        try {
            LogUtil.e("downloadVideo", "checkDDPisConnect==>" + checkDDPisConnect());
            if (!checkDDPisConnect() || playFile == null) {
                Toast.makeText(context, "盯盯拍已经断开连接，请重新连接盯盯拍", Toast.LENGTH_LONG).show();
                return;
            }
            showDownProgress(0);
            VLog.v(TAG, "playFile = " + playFile);
            LogUtil.e("start", "start==>" + playFile.start);
            LogUtil.e("duration", "duration==>" + playFile.duration);
            LogUtil.e("播放时长", "播放时长==>" + (long) (playFile.duration / playFile.compressRaito));
            playbackVideo = EventVideo
                    .bulidCropVideo(cam, playFile.start, playFile.duration, (long) (playFile.duration / playFile.compressRaito));
            VLog.v(TAG, "playbackVideo = " + playbackVideo.toString());
            downloadlist.add(playbackVideo);
            playerController.stop();

            // 下发超级下载。超级下载功能，可以使下载速度增加，但是会影响摄像机的预览功能，在离开裁剪界面时，应该关闭超级下载功能
            new VTask<Object, Integer>() {
                @Override
                protected Integer doBackground(Object o) {
                    cameraServer.enableSuperDownloadMode(cam, true);
                    cameraResMgr.download(downloadlist);
                    return null;
                }

                @Override
                protected void doPost(Integer integer) {
                    LogUtil.e("downloadVideo", "integer==>" + integer);
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
            hideDownProgress();
            Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkDDPisConnect() {
        if (cam != null && cam.isConnected && networkMgr != null && networkMgr.isCameraWifiConnected(cam)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 视频截图
     */
    private void shipinjietu() {
        videoView.pause();
        final int currentTime = videoView.getCurrentPosition();
        LogUtil.e("shipinjietu", "currentTime==>" + currentTime);

        //截取当前正在播放的画面
        outPutPicPath = DDPSDK.getSDKResRootDir() + File.separator + "image/capture" + System.currentTimeMillis() + ".jpg";
        new VTask<Object, Integer>() {
            @Override
            protected void doPre() {
                super.doPre();
                showWaitProgress(null);
            }

            @Override
            protected Integer doBackground(Object o) {
//                lastResultKey = RESULT_KEY_CAPTURE_PIC;
                return videoServer.extractImgFromVideo(videoPath, outPutPicPath, 1280, 720, currentTime, RESULT_KEY_CAPTURE_PIC);
            }

            @Override
            protected void doPost(Integer rst) {
                VLog.v(TAG, "截取视频图片 rst = " + rst);
                VLog.v(TAG, "getOperateResult RESULT_KEY_CAPTURE_PIC = " + videoServer.getOperateResult(RESULT_KEY_CAPTURE_PIC));
                hideWaitProgress();
                if (rst == 0) {
                    Toast.makeText(context, outPutPicPath + "截取视频图片成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, outPutPicPath + "截取视频图片失败", Toast.LENGTH_LONG).show();
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

    private void showDownProgress(int progress) {
        if (ddpVideoDownLoadDialog == null) {
            ddpVideoDownLoadDialog = new DDPVideoDownLoadDialog(this);
            ddpVideoDownLoadDialog.showInfo(progress);
            ddpVideoDownLoadDialog.setCancelable(false);
            ddpVideoDownLoadDialog.getCancelButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cameraResMgr.removeEventDownloadListener(playbackDownloadLsn);
                    ddpVideoDownLoadDialog.dismiss();
                }
            });
        } else {
            ddpVideoDownLoadDialog.setProgress(progress);
        }
        if (!ddpVideoDownLoadDialog.isShowing()) {
            ddpVideoDownLoadDialog.show();
        }
    }

    private void hideDownProgress() {
        if (ddpVideoDownLoadDialog != null) {
            ddpVideoDownLoadDialog.dismiss();
        }
    }

    private void hideWaitProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraServer != null && cam != null) {
            // 关闭超级下载功能
            new VTask<Object, Integer>() {
                @Override
                protected Integer doBackground(Object o) {
                    cameraServer.enableSuperDownloadMode(cam, false);
                    return null;
                }
                @Override
                protected void doPost(Integer integer) {
                    LogUtil.e("downloadVideo", "integer==>" + integer);
                }
            };
        }
        if (cameraServer != null) {
            cameraServer.removeCameraStateChangeListener(camLifeCycleListener);
        }
        if (cameraResMgr != null) {
            cameraResMgr.cancelDownload(downloadlist);
            cameraResMgr.removeEventDownloadListener(playbackDownloadLsn);
        }
        // 反注册视频播放事件监听器
        unRegistPlayEventHandler(EventHandler.getInstance());
    }
}
