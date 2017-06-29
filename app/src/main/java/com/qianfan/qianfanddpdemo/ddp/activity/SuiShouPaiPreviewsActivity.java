package com.qianfan.qianfanddpdemo.ddp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ddp.sdk.base.DDPSDK;
import com.ddp.sdk.cam.resmgr.model.Album;
import com.ddp.sdk.cam.resmgr.utils.UtilPath;
import com.ddp.sdk.cambase.CameraServer;
import com.ddp.sdk.cambase.model.CamConfig;
import com.ddp.sdk.cambase.model.Camera;
import com.ddp.sdk.cambase.model.GeneralParam;
import com.ddp.sdk.cambase.model.PlayFile;
import com.ddp.sdk.cambase.model.ResFile;
import com.ddp.sdk.cambase.utils.VTask;
import com.ddp.sdk.player.controller.PlayerControllerFactory;
import com.ddp.sdk.player.controller.VLiveAndPlaybackController;
import com.ddp.sdk.player.view.VVideoPlayerView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.base.BaseDDPActivity;
import com.qianfan.qianfanddpdemo.utils.ImageLoader;
import com.qianfan.qianfanddpdemo.utils.LogUtil;
import com.qianfan.qianfanddpdemo.utils.ToastUtil;
import com.vyou.app.sdk.player.VPlayerConst;
import com.vyou.app.sdk.player.utils.VPlayerUtil;
import com.vyou.app.sdk.transport.ErrCode;
import com.vyou.app.sdk.transport.exception.TransportException;
import com.vyou.app.sdk.transport.listener.DownloadProgressListener;
import com.vyou.app.sdk.transport.model.JsonRspMsg;
import com.vyou.app.sdk.utils.JsonUtils;
import com.vyou.app.sdk.utils.VLog;
import com.vyou.app.sdk.widget.WeakHandler;

import org.videolan.libvlc.EventHandler;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by zengziming on 2016/12/2.
 */
public class SuiShouPaiPreviewsActivity extends BaseDDPActivity implements View.OnClickListener {
    private static final String TAG = "SuiShouPaiPreviewsActivity";
    private static final int SWITCH_MIC = 1;
    private static final int SWITCH_IMG_FETCH_VIDEO = 2;

    private Context context;

    // 摄像机服务类对象
    private CameraServer cameraServer;
    private Camera cam;

    // 播放器view
    private VVideoPlayerView videoPlayerView;
    // 播放控制器
    private VLiveAndPlaybackController playerController;

    private Button btn_photo, btn_ddp_setting;
    private SimpleDraweeView sdv_allFiles;
    private CheckBox cb_voice, cb_needVideo;
    private Button btn_HD;
    private TextView tv_huifang;

    private View waitProgress;
    private PopupWindow hd_Popupwindow;

    private boolean isFirstVideoOpen = true;
    private boolean isFirstVoiceOpen = true;

    private static int surfaceViewWithPortrait;    // 竖屏时的surfaceView的长度
    private static int surfaceViewHeightPortrait;   // 竖屏时的surfaceView的高度
    private static int surfaceViewWithLandscape;    // 横屏时的surfaceView的长度
    private static int surfaceViewHeightLandscape;   // 横屏时的surfaceView的高度

    private DisplayMetrics disp;

    private ProgressDialog progressDialog;
    private long paiImageSize = 0;
    private String fileFirstPath = null;

    private WeakHandler<SuiShouPaiPreviewsActivity> uiHandler = new WeakHandler<SuiShouPaiPreviewsActivity>(this) {
        @Override
        public void handleMessage(Message msg) {
            VLog.v(TAG, "msg.what = " + msg.what);
            switch (msg.what) {
                // 画面出来了
                case EventHandler.MediaPlayerVout:
                case VPlayerConst.CMD_VIDEO_OUT: {
                    VLog.v(TAG, "VPlayerConst.CMD_VIDEO_OUT : " + msg.what);
                    hideWaitProgress();
                    break;
                }
                // 播放异常
                case EventHandler.MediaPlayerEncounteredError: {
                    VLog.v(TAG, "EventHandler.MediaPlayerEncounteredError");
                    hideWaitProgress();
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
        setContentView(R.layout.activity_suishoupaipreview);
        initP();
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerController.play(CameraServer.LIVE_SWITCH_TYPE, -1);
        // 获取回放列表
        new VTask<Object, List<PlayFile>>() {

            @Override
            protected List<PlayFile> doBackground(Object o) {
                List<PlayFile> playFileList = cameraServer.getResPlayFiles(cam);
                if (playFileList == null) {
                    return null;
                }
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
                if (list != null && list.size() > 0) {
                    playerController.setPlaybackList(cam, list);
                    LogUtil.e("onResume", "doPost");
                }
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        VLog.v(TAG, "onPause");
        if (playerController != null) {
            playerController.stop();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        VLog.v(TAG, "onStop");
        if (playerController != null) {
            playerController.stop();
        }
    }

    private void initData() {
        // 注册视频播放事件监听器
        registPalyerEventHandler(EventHandler.getInstance());
        updateDDPSetting();
        // 显示等待框，当画面出来后，隐藏等待框
        showWaitProgress();
//        playerController.play(CameraServer.LIVE_SWITCH_TYPE, -1);
    }

    private void updateDDPSetting() {
        new VTask<Object, Integer>() {

            @Override
            protected Integer doBackground(Object o) {
                String[] keyList = {
                        CamConfig.PARAMS_KEY.SPEAKER.key, CamConfig.PARAMS_KEY.MIC.key, CamConfig.PARAMS_KEY.IMG_FETCH_VIDEO_BEFORE.key,
                        CamConfig.PARAMS_KEY.IMG_FETCH_VIDEO_AFTER.key, CamConfig.PARAMS_KEY.IMG_QUALITY.key,
                        CamConfig.PARAMS_KEY.DISPLAY_MODE.key, CamConfig.PARAMS_KEY.TIME_OSD.key, CamConfig.PARAMS_KEY.SPEED_OSD.key,
                        CamConfig.PARAMS_KEY.BOOT_SOUND.key, CamConfig.PARAMS_KEY.PARKING_MODE.key,
                        CamConfig.PARAMS_KEY.HORIZONTAL_MIRROR.key, CamConfig.PARAMS_KEY.GSENSOR_MODE.key,
                        CamConfig.PARAMS_KEY.PARKING_POWER_MGR.key, CamConfig.PARAMS_KEY.EDOG.key,
                        CamConfig.PARAMS_KEY.SYSTEM_RUN_TIME.key,};
                JsonRspMsg rspMsg = cameraServer.generalQueryParams(cam, keyList);
                return rspMsg.faultNo;
            }

            @Override
            protected void doPost(Integer integer) {
                boolean isVoice = CamConfig.SWITCH.ON.equals(cam.config.mic);
                boolean isNeedVideo = CamConfig.SWITCH.ON.equals(cam.config.imageVideoAssociated);
                if (isVoice && isFirstVoiceOpen) {
                    isFirstVoiceOpen = true;
                } else {
                    isFirstVoiceOpen = false;
                }
                if (isNeedVideo && isFirstVideoOpen) {
                    isFirstVideoOpen = true;
                } else {
                    isFirstVideoOpen = false;
                }
                cb_voice.setChecked(isVoice);
                cb_needVideo.setChecked(isNeedVideo);
                switch (turnLevel2int(cam.config.graphicQC)) {
                    case 0://1080
                        btn_HD.setBackgroundResource(R.mipmap.icon_ddp_1080_selected);
                        break;
                    case 1://720
                        btn_HD.setBackgroundResource(R.mipmap.icon_ddp_720_selected);
                        break;
                    case 2://480
                        btn_HD.setBackgroundResource(R.mipmap.icon_ddp_480_selected);
                        break;
                    case 3://off
                        break;
                }
            }
        };
    }

    private void initView() {
        fileFirstPath = getIntent().getStringExtra("filePath");
        videoPlayerView = (VVideoPlayerView) findViewById(R.id.videoPlayerView);
        btn_photo = (Button) findViewById(R.id.btn_photo);
        cb_voice = (CheckBox) findViewById(R.id.cb_voice);
        cb_needVideo = (CheckBox) findViewById(R.id.cb_needVideo);
        sdv_allFiles = (SimpleDraweeView) findViewById(R.id.sdv_allFiles);
        btn_ddp_setting = (Button) findViewById(R.id.btn_ddp_setting);
        btn_HD = (Button) findViewById(R.id.btn_HD);
        tv_huifang = (TextView) findViewById(R.id.tv_huifang);
        btn_HD.setOnClickListener(this);
        tv_huifang.setOnClickListener(this);
        btn_photo.setOnClickListener(this);
        sdv_allFiles.setOnClickListener(this);
        btn_ddp_setting.setOnClickListener(this);
        ImageLoader.loadResize(sdv_allFiles, "file://" + fileFirstPath, 50, 50);
        cb_voice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isFirstVoiceOpen) {
                    isFirstVoiceOpen = false;
                    return;
                }
                GeneralParam param = new GeneralParam();
                param.strParam.put(CamConfig.PARAMS_KEY.MIC.key,
                        CamConfig.SWITCH.ON.equals(cam.config.mic) ? CamConfig.SWITCH.OFF.key : CamConfig.SWITCH.ON.key);
                switchOpt(SWITCH_MIC, cb_voice, param);
            }
        });
        cb_needVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LogUtil.e("setOnCheckedChangeListener", "onCheckedChanged");
                if (isFirstVideoOpen) {
                    isFirstVideoOpen = false;
                    return;
                }
                GeneralParam param = new GeneralParam();
                if (CamConfig.SWITCH.ON.equals(cam.config.imageVideoAssociated)) {
                    param.intParam.put(CamConfig.PARAMS_KEY.IMG_FETCH_VIDEO_BEFORE.key, 0);
                    param.intParam.put(CamConfig.PARAMS_KEY.IMG_FETCH_VIDEO_AFTER.key, 0);
                } else {
                    param.intParam.put(CamConfig.PARAMS_KEY.IMG_FETCH_VIDEO_BEFORE.key, 5);
                    param.intParam.put(CamConfig.PARAMS_KEY.IMG_FETCH_VIDEO_AFTER.key, 5);
                }
                switchOpt(SWITCH_MIC, cb_needVideo, param);
            }
        });
        // 获取播放控制器
        playerController = (VLiveAndPlaybackController) videoPlayerView.getPlayerController(PlayerControllerFactory.TYPE_PLAYER_LIVE);
        // 给播放控制器设置摄像机
        playerController.setCamera(cam);

        waitProgress = findViewById(R.id.wait_progress);
    }

    private void initP() {
        DDPSDK.init(this, "");
        context = getBaseContext();
        cameraServer = CameraServer.intance();

        cam = cameraServer.getCurrentConnectCamera();
        if (cam == null || !cam.isConnected) {
            VLog.e(TAG, "cam == null || !cam.isConnected, finish");
            finish();
        }

        disp = VPlayerUtil.getDisplaySize(context);
        cameraServer.addCameraStateChangeListener(camLifeCycleListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 反注册视频播放事件监听器
        unRegistPlayEventHandler(EventHandler.getInstance());
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (cameraServer != null) {
            cameraServer.removeCameraStateChangeListener(camLifeCycleListener);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        VLog.v(TAG, "playerController.updateZoomMode();");
        // 竖屏
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (surfaceViewWithPortrait == 0 && surfaceViewHeightPortrait == 0) {
                surfaceViewWithPortrait = disp.widthPixels;
                surfaceViewHeightPortrait = surfaceViewWithPortrait * 9 / 16;
            }

            playerController.updateSurfaceParent(surfaceViewWithPortrait, surfaceViewHeightPortrait);
        }
        // 横屏
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (surfaceViewWithLandscape == 0 && surfaceViewHeightLandscape == 0) {
                surfaceViewHeightLandscape = disp.widthPixels;
                surfaceViewWithLandscape = surfaceViewHeightLandscape * 16 / 9;
            }
            playerController.updateSurfaceParent(surfaceViewWithLandscape, surfaceViewHeightLandscape);
        }
        playerController.updateZoomMode();
    }


    private void showWaitProgress() {
        if (waitProgress.getVisibility() != View.VISIBLE) {
            waitProgress.setVisibility(View.VISIBLE);
        }
    }

    private void hideWaitProgress() {
        waitProgress.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_photo:
                SimpleDateFormat formater = new SimpleDateFormat(ResFile.DATE_FILE_NAME);
                String localSavePath = UtilPath.getImage(cam.netInfo.camMAC) + "A_" + formater.format(new Date()) + ".jpg";
                DownloadProgressListener listener = new DownloadProgressListener() {
                    @Override
                    public void onStart(long l) {
                        VLog.v(TAG, "onStart l = " + l);
                        paiImageSize = l;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showDialog("正在下载 0%");
                            }
                        });
                    }

                    @Override
                    public void onDownloadSize(final long l) {
                        VLog.v(TAG, "onDownloadSize l = " + l);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int progress = (int) (l * 100 * 1f / paiImageSize);
                                showDialog("正在下载 " + progress + "%");
                            }
                        });

                    }

                    @Override
                    public void onDownError(TransportException e) {
                        VLog.v(TAG, "onDownError e = " + e.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "拍照失败。", Toast.LENGTH_LONG).show();
                                disMissDialog();
                            }
                        });
                    }

                    @Override
                    public void onStopped(String s) {
                        VLog.v(TAG, "onStopped");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                disMissDialog();
                            }
                        });
                    }

                    @Override
                    public void onFinish(String s) {
                        VLog.v(TAG, "onFinish s = " + s);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "拍照成功。", Toast.LENGTH_LONG).show();
                                disMissDialog();
                            }
                        });
                    }

                    @Override
                    public boolean isInterrupt() {
                        return false;
                    }
                };

                playerController.snapshot(localSavePath, listener);
                break;
            case R.id.sdv_allFiles:
                Intent intent1 = new Intent(context, SSPAllFilesActivity.class);
                intent1.putExtra("albumId", (int) Album.get(cam).id);
                context.startActivity(intent1);
//                IntentUtils.jumpSSPAllFilesActivity(this, (int) Album.get(cam).id);
                break;
            case R.id.btn_HD:
                showPopupwindow(btn_HD);
                break;
            case R.id.tv_huifang:
                Intent intent2 = new Intent(context, BackListActivity.class);
                startActivity(intent2);
                finish();
                break;
            case R.id.btn_ddp_setting:
                startActivity(new Intent(this, SettingCameraActivity.class)
                        .putExtra("type", 1)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                IntentUtils.jumpSettingCameraActivity(this, 1);
                break;
        }
    }

    /**
     * 显示切换地图类型PopupWindow
     *
     * @param view
     */
    private void showPopupwindow(View view) {
        if (hd_Popupwindow == null) {
            View type_view = LayoutInflater.from(this).inflate(R.layout.ddp_popwindow_hd, null);
            final RadioGroup radioGroup = (RadioGroup) type_view.findViewById(R.id.radioGroup);
            final RadioButton rb_480 = (RadioButton) type_view.findViewById(R.id.rb_480);
            final RadioButton rb_720 = (RadioButton) type_view.findViewById(R.id.rb_720);
            final RadioButton rb_1080 = (RadioButton) type_view.findViewById(R.id.rb_1080);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    LogUtil.e("onCheckedChanged", "checkedId==>" + checkedId);
                    int position = -1;
                    if (rb_480.getId() == checkedId) {
                        position = 0;
                        btn_HD.setBackgroundResource(R.mipmap.icon_ddp_480_selected);
                    } else if (rb_720.getId() == checkedId) {
                        position = 1;
                        btn_HD.setBackgroundResource(R.mipmap.icon_ddp_720_selected);
                    } else if (rb_1080.getId() == checkedId) {
                        position = 2;
                        btn_HD.setBackgroundResource(R.mipmap.icon_ddp_1080_selected);
                    }
//                    btn_HD.setBackgroundResource(R.mipmap.icon_ddp_1440_selected);
                    setQuality(position);
                    hd_Popupwindow.dismiss();
                }
            });
//            final RadioButton rb_satellite = (RadioButton) type_view.findViewById(R.id.rb_satellite);
            hd_Popupwindow = new PopupWindow(type_view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            hd_Popupwindow.setTouchable(true);
            hd_Popupwindow.setOutsideTouchable(true);
            hd_Popupwindow.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.transparent));
        }
//        hd_Popupwindow.showAsDropDown(view);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        hd_Popupwindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0] + view.getWidth(), location[1]);
    }

    private void setQuality(final int position) {
        LogUtil.d("setQuality", "position==>" + position);
        new VTask<Object, Integer>() {
            @Override
            protected Integer doBackground(Object o) {
                GeneralParam param = new GeneralParam();
                String value = JsonUtils.turnInt2String(position, new int[]{0, 1, 2}, new String[]{
                        CamConfig.LEVEL.LOW.key, CamConfig.LEVEL.MIDDLE.key, CamConfig.LEVEL.HIGH.key});
                param.strParam.put(CamConfig.PARAMS_KEY.IMG_QUALITY.key, value);

                return cameraServer.generalSaveParams(cam, param).faultNo;
            }

            @Override
            protected void doPost(Integer result) {
                if (result == ErrCode.COMN_OK) {
                    cam.config.graphicQC = turnInt2Level(position);
                } else {
                    ToastUtil.TShort(context, "设置失败");
                }
            }
        };
    }

    private CamConfig.LEVEL turnInt2Level(int org) {
        CamConfig.LEVEL rst = CamConfig.LEVEL.NA;
        if (0 == (org)) {
            rst = CamConfig.LEVEL.LOW;
        } else if (1 == (org)) {
            rst = CamConfig.LEVEL.MIDDLE;
        } else if (2 == (org)) {
            rst = CamConfig.LEVEL.HIGH;
        }
//        else if (3 == org) {
//            rst = CamConfig.LEVEL.OFF;
//        }
        LogUtil.i(TAG, "turnInt2Level org = " + org + ", rst = " + rst);
        return rst;
    }

    private int turnLevel2int(CamConfig.LEVEL org) {
        int rst = -1;
        if (CamConfig.LEVEL.HIGH.equals(org)) {
            rst = 0;
        } else if (CamConfig.LEVEL.MIDDLE.equals(org)) {
            rst = 1;
        } else if (CamConfig.LEVEL.LOW.equals(org)) {
            rst = 2;
        } else if (CamConfig.LEVEL.OFF.equals(org)) {
            rst = 3;
        }
        VLog.v(TAG, "turnLevel2int org = " + org + ", rst = " + rst);
        return rst;
    }


    /**
     * 开关类的操作
     */
    private void switchOpt(final int switchType, final CheckBox checkBox, final GeneralParam param) {
        new VTask<Object, Integer>() {

            @Override
            protected Integer doBackground(Object o) {
                GeneralParam mParam = param;
                return cameraServer.generalSaveParams(cam, mParam).faultNo;
            }

            @Override
            protected void doPost(Integer result) {
                if (result == ErrCode.COMN_OK) {
                    switch (switchType) {
                        case SWITCH_MIC: {
                            cam.config.mic = checkBox.isChecked() ? CamConfig.SWITCH.ON : CamConfig.SWITCH.OFF;
                            break;
                        }
                        case SWITCH_IMG_FETCH_VIDEO:
                            cam.config.imageVideoAssociated = checkBox.isChecked() ? CamConfig.SWITCH.ON : CamConfig.SWITCH.OFF;
                            break;
                        default:
                            break;
                    }
                } else {
                    checkBox.setChecked(!checkBox.isChecked());
//                    Toast.makeText(context, "保存失败。", Toast.LENGTH_SHORT).show();
                }
            }
        };

    }


    private void showDialog(String text) {
        if (isFinishing()) {
            return;
        }
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        if (TextUtils.isEmpty(text)) {
            progressDialog.setMessage("正在下载");
        } else {
            progressDialog.setMessage(text);
        }
        progressDialog.show();
    }

    private void disMissDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
