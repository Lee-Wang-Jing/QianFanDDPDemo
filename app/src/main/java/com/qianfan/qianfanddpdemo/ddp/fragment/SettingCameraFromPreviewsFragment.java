package com.qianfan.qianfanddpdemo.ddp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ddp.sdk.base.DDPSDK;
import com.ddp.sdk.cambase.CameraServer;
import com.ddp.sdk.cambase.model.CamConfig;
import com.ddp.sdk.cambase.model.Camera;
import com.ddp.sdk.cambase.model.GeneralParam;
import com.ddp.sdk.cambase.network.NetworkMgr;
import com.ddp.sdk.cambase.utils.VTask;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.base.BaseFragment;
import com.qianfan.qianfanddpdemo.ddp.activity.CameraSearchActivity;
import com.qianfan.qianfanddpdemo.utils.LogUtil;
import com.qianfan.qianfanddpdemo.utils.ToastUtil;
import com.qianfan.qianfanddpdemo.widgets.Custom2btnDialog;
import com.qianfan.qianfanddpdemo.widgets.SwitchView;
import com.vyou.app.sdk.transport.ErrCode;
import com.vyou.app.sdk.transport.model.RspMsg;
import com.vyou.app.sdk.utils.VLog;

/**
 * 摄像机设置
 * 功能详细描述
 *
 * @author xyx on 2017/1/23 11:40
 * @e-mail 384744573@qq.com
 * @see [相关类/方法](可选)
 */

public class SettingCameraFromPreviewsFragment extends BaseFragment implements View.OnClickListener, SwitchView.OnStateChangedListener {
    private final static String TAG = SettingCameraFromPreviewsFragment.class.getSimpleName();
    private final static int REQUEST_CAMERA_NAME = 100;//摄像机名称
    private final static int REQUEST_CONNECTION = 101;//链接
    private static final int SWITCH_MIC = 3;
    private static final int SWITCH_IMG_FETCH_VIDEO = 4;

    private Toolbar toolbar;
    private LinearLayout ll_camera_name, ll_camera_volume, ll_camera_quality;
    private TextView tv_camera_name, tv_camera_volume, tv_camera_quality, tv_camera_format, tv_camera_password, tv_camera_setting;
    private SwitchView switch_camera_recording, switch_camera_video;

    private Custom2btnDialog dialog;
    private Camera cam;
    private CameraServer cameraServer;
    // 网络服务类
    private NetworkMgr networkMgr;

    public static SettingCameraFromPreviewsFragment newInstance() {
        Bundle args = new Bundle();
        SettingCameraFromPreviewsFragment fragment = new SettingCameraFromPreviewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_setting_camera;
    }

    @Override
    protected void init(View view) {
        initView(view);
    }

    private void initView(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ll_camera_name = (LinearLayout) view.findViewById(R.id.ll_camera_name);
        ll_camera_volume = (LinearLayout) view.findViewById(R.id.ll_camera_volume);
        ll_camera_quality = (LinearLayout) view.findViewById(R.id.ll_camera_quality);
        tv_camera_name = (TextView) view.findViewById(R.id.tv_camera_name);
        tv_camera_volume = (TextView) view.findViewById(R.id.tv_camera_volume);
        tv_camera_quality = (TextView) view.findViewById(R.id.tv_camera_quality);
        tv_camera_format = (TextView) view.findViewById(R.id.tv_camera_format);
        tv_camera_password = (TextView) view.findViewById(R.id.tv_camera_password);
        tv_camera_setting = (TextView) view.findViewById(R.id.tv_camera_setting);
        switch_camera_recording = (SwitchView) view.findViewById(R.id.switch_camera_recording);
        switch_camera_video = (SwitchView) view.findViewById(R.id.switch_camera_video);

        initLazyView();
    }

    private void initLazyView() {
        setBaseBackToolbar(toolbar, "摄像机设置");
        try {
            // SDK的初始化必须在主线程中运行,必须先初始化SDK，后面的才能操作,AndroidManifest文件需要声明 DDP_APPKEY 和 DDP_APPSECRET 才能通过认证
            DDPSDK.init(_mActivity, "");
            cameraServer = CameraServer.intance();
            cam = cameraServer.getCurrentConnectCamera();
        } catch (Exception e) {
            e.printStackTrace();
        }
        networkMgr = NetworkMgr.instance();

        ll_camera_name.setOnClickListener(this);
        tv_camera_password.setOnClickListener(this);
        ll_camera_volume.setOnClickListener(this);
        ll_camera_quality.setOnClickListener(this);
        tv_camera_setting.setOnClickListener(this);
        tv_camera_format.setOnClickListener(this);
        switch_camera_recording.setOnStateChangedListener(this);
        switch_camera_video.setOnStateChangedListener(this);

        boolean cameraIsConnected = cam != null && cam.isConnected
                && networkMgr != null && networkMgr.isCameraWifiConnected(cam);

        if (!cameraIsConnected) {
            Intent intent = new Intent(_mActivity, CameraSearchActivity.class);
            startActivityForResult(intent, REQUEST_CONNECTION);
            return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_camera_name://摄像机名称
                startForResult(SettingCameraNameFragment.newInstance(), REQUEST_CAMERA_NAME);
                break;
            case R.id.tv_camera_password://摄像机密码
                startForResult(SettingCameraPasswordFragment.newInstance(), REQUEST_CAMERA_NAME);
                break;
            case R.id.ll_camera_volume://音量调节
                startForResult(SettingCameravolumeFragment.newInstance(), REQUEST_CAMERA_NAME);
                break;
            case R.id.ll_camera_quality://图像质量
                startForResult(SettingCameraQualityFragment.newInstance(), REQUEST_CAMERA_NAME);
                break;
            case R.id.tv_camera_setting://高级设置
                startForResult(SettingCameraAdvancedFragment.newInstance(), REQUEST_CAMERA_NAME);
                break;
            case R.id.tv_camera_format://格式化照相机
                boolean cameraIsConnected = cam != null && cam.isConnected
                        && networkMgr != null && networkMgr.isCameraWifiConnected(cam);
                if (cameraIsConnected){
                    showFormatDialog();
                }else{
                    ToastUtil.TShort(_mActivity,"连接已断开");
                    Intent intent = new Intent(_mActivity, CameraSearchActivity.class);
                    startActivityForResult(intent, REQUEST_CONNECTION);
                    return;
                }
                break;
        }
    }

    /**
     * 显示格式化弹窗
     */
    private void showFormatDialog() {
        if (dialog == null) {
            dialog = new Custom2btnDialog(_mActivity);
        }
        dialog.showInfo("格式化会清楚摄像机SD卡内容，请确认是否执行此操作？", "确定", "取消");
        dialog.getOkButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                formatCameraSDCard();
            }
        });
        dialog.getCancelButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    @Override
    public void toggleToOn(SwitchView view) {
        view.toggleSwitch(true);
        GeneralParam param = new GeneralParam();
        switch (view.getId()) {
            case R.id.switch_camera_recording://录像时录音
                param.strParam.put(CamConfig.PARAMS_KEY.MIC.key, CamConfig.SWITCH.ON.key);
                switchOpt(SWITCH_MIC, param, CamConfig.SWITCH.ON);
                break;
            case R.id.switch_camera_video://拍照关联视频
                param.intParam.put(CamConfig.PARAMS_KEY.IMG_FETCH_VIDEO_BEFORE.key, 5);
                param.intParam.put(CamConfig.PARAMS_KEY.IMG_FETCH_VIDEO_AFTER.key, 5);
                switchOpt(SWITCH_IMG_FETCH_VIDEO, param, CamConfig.SWITCH.ON);
                break;
        }
    }

    @Override
    public void toggleToOff(SwitchView view) {
        view.toggleSwitch(false);
        GeneralParam param = new GeneralParam();
        switch (view.getId()) {
            case R.id.switch_camera_recording://录像时录音
                param.strParam.put(CamConfig.PARAMS_KEY.MIC.key, CamConfig.SWITCH.OFF.key);
                switchOpt(SWITCH_MIC, param, CamConfig.SWITCH.OFF);
                break;
            case R.id.switch_camera_video://拍照关联视频
                param.intParam.put(CamConfig.PARAMS_KEY.IMG_FETCH_VIDEO_BEFORE.key, 0);
                param.intParam.put(CamConfig.PARAMS_KEY.IMG_FETCH_VIDEO_AFTER.key, 0);
                switchOpt(SWITCH_IMG_FETCH_VIDEO, param, CamConfig.SWITCH.OFF);
                break;
        }
    }

    private void switchOpt(final int switchType, final GeneralParam param, final CamConfig.SWITCH config) {
        new VTask<Object, Integer>() {

            @Override
            protected Integer doBackground(Object o) {
                RspMsg rsp = null;
                GeneralParam mParam = param;
                return cameraServer.generalSaveParams(cam, mParam).faultNo;
            }

            @Override
            protected void doPost(Integer result) {
                if (result == ErrCode.COMN_OK) {
                    switch (switchType) {
                        case SWITCH_MIC: {
                            cam.config.mic = config;
                            break;
                        }
                        case SWITCH_IMG_FETCH_VIDEO: {
                            cam.config.imageVideoAssociated = config;
                            break;
                        }
                        default:
                            break;
                    }
                } else {
                    Toast.makeText(_mActivity, "保存失败。", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    @Override
    public void onResume() {
        LogUtil.e(TAG, "onResume");
        if (cam != null && cam.isConnected
                && networkMgr != null && networkMgr.isCameraWifiConnected(cam)) {
            LogUtil.e(TAG, "onResume and isConnected");
            getData();
        } else {
            LogUtil.e(TAG, "onResume and unConnected");
        }
        super.onResume();
    }

    private void getData() {
        tv_camera_name.setText("" + cam.getName());
        tv_camera_volume.setText(cam.config.sound + "%");
        switch_camera_recording.setOpened(CamConfig.SWITCH.ON.equals(cam.config.mic));
        switch_camera_video.setOpened(CamConfig.SWITCH.ON.equals(cam.config.imageVideoAssociated));
        if (cam.config.graphicQC == CamConfig.LEVEL.HIGH) {
            tv_camera_quality.setText("(全高清)1080P");
        } else if (cam.config.graphicQC == CamConfig.LEVEL.MIDDLE) {
            tv_camera_quality.setText("(高清)720P");
        } else if (cam.config.graphicQC == CamConfig.LEVEL.LOW) {
            tv_camera_quality.setText("(标清)480P");
        }
    }


    /**
     * 格式化
     */
    private void formatCameraSDCard() {
        new VTask<Object, Integer>() {
            @Override
            protected Integer doBackground(Object o) {
                return cameraServer.formatCameraSDCard(cam).faultNo;
            }

            @Override
            protected void doPost(Integer rst) {
                VLog.v(TAG, "rst = " + rst);
            }
        };
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CONNECTION:
                if (resultCode == 110) {
                    if (cameraServer == null) {
                        cameraServer = CameraServer.intance();
                    }
                    cam = cameraServer.getCurrentConnectCamera();
                    getData();
                } else {
                    _mActivity.finish();
                }
                break;
        }
    }

    @Override
    protected void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CAMERA_NAME:
                getData();
                break;
        }
    }
}
