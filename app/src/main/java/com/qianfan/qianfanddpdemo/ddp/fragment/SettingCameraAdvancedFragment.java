package com.qianfan.qianfanddpdemo.ddp.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ddp.sdk.base.DDPSDK;
import com.ddp.sdk.cambase.CameraServer;
import com.ddp.sdk.cambase.listener.OnCamRemoteButtonListener;
import com.ddp.sdk.cambase.model.CamButtonInfo;
import com.ddp.sdk.cambase.model.CamConfig;
import com.ddp.sdk.cambase.model.Camera;
import com.ddp.sdk.cambase.model.GeneralParam;
import com.ddp.sdk.cambase.utils.VTask;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.base.BaseFragment;
import com.qianfan.qianfanddpdemo.utils.LogUtil;
import com.qianfan.qianfanddpdemo.utils.ToastUtil;
import com.qianfan.qianfanddpdemo.widgets.Custom2ItemDialog;
import com.qianfan.qianfanddpdemo.widgets.RemoteTimeDialog;
import com.qianfan.qianfanddpdemo.widgets.SwitchView;
import com.vyou.app.sdk.transport.ErrCode;
import com.vyou.app.sdk.transport.model.RspMsg;
import com.vyou.app.sdk.utils.VLog;

import java.util.ArrayList;

/**
 * 摄像机高级设置
 * 功能详细描述
 *
 * @author xyx on 2017/1/24 09:44
 * @e-mail 384744573@qq.com
 * @see [相关类/方法](可选)
 */

public class SettingCameraAdvancedFragment extends BaseFragment implements View.OnClickListener, SwitchView.OnStateChangedListener {
    private static final String TAG = SettingCameraAdvancedFragment.class.getSimpleName();
    private static final int SWITCH_BOOT_SOUND = 2;
    private static final int SWITCH_PARKING_MODE = 5;
    private static final int SWITCH_HORIZONAL_MIRROR = 6;

    private Toolbar toolbar;
    private Button btn_open;
    private LinearLayout ll_duration, ll_sensitivity, ll_car_protect;
    private TextView tv_duration, tv_sensitivity;
    private SwitchView switch_monitor, switch_sounds, switch_reversal;

    private Custom2ItemDialog dialog;
    private ProgressDialog progressDialog;
    private ArrayList<String> durationList = new ArrayList<String>() {{
        add("15分钟");
        add("1小时");
        add("6小时");
        add("24小时");
        add("不关机");
    }};
    private ArrayList<String> sensitivityList = new ArrayList<String>() {{
        add("高");
        add("中");
        add("低");
        add("关闭");
    }};

    private Camera cam;
    private CameraServer cameraServer;
    private OnCamRemoteButtonListener btnPairListener;
    private RemoteTimeDialog remoteTimeDialog;

    public static SettingCameraAdvancedFragment newInstance() {
        Bundle args = new Bundle();
        SettingCameraAdvancedFragment fragment = new SettingCameraAdvancedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_setting_camera_advance;
    }

    @Override
    protected void init(View view) {
        initView(view);
    }

    private void initView(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        btn_open = (Button) view.findViewById(R.id.btn_open);
        ll_duration = (LinearLayout) view.findViewById(R.id.ll_duration);
        ll_sensitivity = (LinearLayout) view.findViewById(R.id.ll_sensitivity);
        ll_car_protect = (LinearLayout) view.findViewById(R.id.ll_car_protect);
        switch_monitor = (SwitchView) view.findViewById(R.id.switch_monitor);
        switch_sounds = (SwitchView) view.findViewById(R.id.switch_sounds);
        switch_reversal = (SwitchView) view.findViewById(R.id.switch_reversal);
        tv_duration = (TextView) view.findViewById(R.id.tv_duration);
        tv_sensitivity = (TextView) view.findViewById(R.id.tv_sensitivity);

        initLazyView();
    }

    private void initLazyView() {
        setBaseBackToolbar(toolbar, "图像质量");

        // SDK的初始化必须在主线程中运行,必须先初始化SDK，后面的才能操作,AndroidManifest文件需要声明 DDP_APPKEY 和 DDP_APPSECRET 才能通过认证
        DDPSDK.init(_mActivity, "");
        cameraServer = CameraServer.intance();
        cam = cameraServer.getCurrentConnectCamera();

        if (cam == null || !cam.isConnected) {
            LogUtil.e(TAG, "cam == null || !cam.isConnected finish.");
            ToastUtil.TShort(_mActivity, "摄像机连接异常，请重新连接");
            _mActivity.finish();
            return;
        }


        btn_open.setOnClickListener(this);
        ll_duration.setOnClickListener(this);
        ll_sensitivity.setOnClickListener(this);
        switch_monitor.setOnStateChangedListener(this);
        switch_sounds.setOnStateChangedListener(this);
        switch_reversal.setOnStateChangedListener(this);
        if (cam.config.parkingPowerMode.key >= 0) {
            ll_car_protect.setVisibility(View.VISIBLE);
            tv_duration.setText(durationList.get(cam.config.parkingPowerMode.key));
        } else {
            ll_car_protect.setVisibility(View.GONE);
        }
        switch_monitor.setOpened(CamConfig.SWITCH.ON.equals(cam.config.parkingMode));
        switch_sounds.setOpened(CamConfig.SWITCH.ON.equals(cam.config.bootSound));
        switch_reversal.setOpened(CamConfig.SWITCH.ON.equals(cam.config.horizontalMirrorMode));
        if (cam.config.gSensor == CamConfig.LEVEL.HIGH) {
            tv_sensitivity.setText(sensitivityList.get(0));
        } else if (cam.config.gSensor == CamConfig.LEVEL.MIDDLE) {
            tv_sensitivity.setText(sensitivityList.get(1));
        } else if (cam.config.gSensor == CamConfig.LEVEL.LOW) {
            tv_sensitivity.setText(sensitivityList.get(2));
        } else {
            tv_sensitivity.setText(sensitivityList.get(3));
        }


        btnPairListener = new OnCamRemoteButtonListener() {
            @Override
            public void onMatchStart(Camera camera, final int matchTime) {
                _mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.e(TAG, "配对开始 ：" + matchTime);
                        if (remoteTimeDialog == null) {
                            remoteTimeDialog = new RemoteTimeDialog(_mActivity);
                        }
                        if (!remoteTimeDialog.isShowing()) {
                            remoteTimeDialog.startRemoteTime();
                            remoteTimeDialog.show();
                        }
                    }
                });
            }

            @Override
            public void onMatchResult(Camera camera, final boolean success) {
                _mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (remoteTimeDialog != null && remoteTimeDialog.isShowing()) {
                            remoteTimeDialog.dismiss();
                        }
                        Toast.makeText(_mActivity, "配对结果 ： " + (success ? "成功" : "失败"), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onBatteryChange(Camera camera, CamButtonInfo info) {

            }
        };

        cameraServer.addCameraStateChangeListener(btnPairListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraServer != null) {
            cameraServer.removeCameraStateChangeListener(btnPairListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open://开启
                enableButtonPairMode();
                break;
            case R.id.ll_duration://时长
                if (dialog == null) {
                    dialog = new Custom2ItemDialog(_mActivity);
                }
                dialog.setText("15分钟", "1小时", "6小时", "24小时", "不关机");
                dialog.setOnItemClickListener(new Custom2ItemDialog.OnItemClickListener() {
                    @Override
                    public void OnClick(TextView view, int postion) {
                        dialog.dismiss();
                        pickDuration(postion - 1);
                    }
                });
                dialog.show();
                break;
            case R.id.ll_sensitivity://灵敏度
                if (dialog == null) {
                    dialog = new Custom2ItemDialog(_mActivity);
                }
                dialog.setText("高", "中", "低", "关闭");
                dialog.setOnItemClickListener(new Custom2ItemDialog.OnItemClickListener() {
                    @Override
                    public void OnClick(TextView view, int postion) {
                        dialog.dismiss();
                        if (progressDialog == null) {
                            progressDialog = new ProgressDialog(_mActivity);
                        }
                        progressDialog.setMessage("正在保存中");
                        progressDialog.show();
                        pickSensitity(postion - 1);
                    }
                });
                dialog.show();
                break;
        }
    }


    @Override
    public void toggleToOn(SwitchView view) {
        view.toggleSwitch(true);
        GeneralParam param = new GeneralParam();
        switch (view.getId()) {
            case R.id.switch_monitor://停车监控
                param.strParam.put(CamConfig.PARAMS_KEY.PARKING_MODE.key, CamConfig.SWITCH.ON.key);
                switchOpt(SWITCH_PARKING_MODE, param, CamConfig.SWITCH.ON);
                break;
            case R.id.switch_sounds://开机提示音
                param.strParam.put(CamConfig.PARAMS_KEY.BOOT_SOUND.key, CamConfig.SWITCH.ON.key);
                switchOpt(SWITCH_BOOT_SOUND, param, CamConfig.SWITCH.ON);
                break;
            case R.id.switch_reversal://图像左右翻转
                param.strParam.put(CamConfig.PARAMS_KEY.HORIZONTAL_MIRROR.key, CamConfig.SWITCH.ON.key);
                switchOpt(SWITCH_HORIZONAL_MIRROR, param, CamConfig.SWITCH.ON);
                break;
        }
    }

    @Override
    public void toggleToOff(SwitchView view) {
        view.toggleSwitch(false);
        GeneralParam param = new GeneralParam();
        switch (view.getId()) {
            case R.id.switch_monitor://停车监控
                param.strParam.put(CamConfig.PARAMS_KEY.PARKING_MODE.key, CamConfig.SWITCH.OFF.key);
                switchOpt(SWITCH_PARKING_MODE, param, CamConfig.SWITCH.OFF);
                break;
            case R.id.switch_sounds://开机提示音
                param.strParam.put(CamConfig.PARAMS_KEY.BOOT_SOUND.key, CamConfig.SWITCH.OFF.key);
                switchOpt(SWITCH_BOOT_SOUND, param, CamConfig.SWITCH.OFF);
                break;
            case R.id.switch_reversal://图像左右翻转
                param.strParam.put(CamConfig.PARAMS_KEY.HORIZONTAL_MIRROR.key, CamConfig.SWITCH.ON.key);
                switchOpt(SWITCH_HORIZONAL_MIRROR, param, CamConfig.SWITCH.ON);
                break;
        }
    }


    private void enableButtonPairMode() {
        new VTask<Object, Integer>() {
            @Override
            protected Integer doBackground(Object o) {
                return cameraServer.enableButtonPairMode(cam).faultNo;
            }

            @Override
            protected void doPost(Integer rst) {
                VLog.v(TAG, "rst = " + rst);
            }
        };
    }

    /**
     * 设置时长
     *
     * @param position
     */
    private void pickDuration(final int position) {
        new VTask<Object, Integer>() {
            @Override
            protected Integer doBackground(Object o) {
                GeneralParam param = new GeneralParam();
                param.intParam.put(CamConfig.PARAMS_KEY.PARKING_POWER_MGR.key, position);

                return cameraServer.generalSaveParams(cam, param).faultNo;
            }

            @Override
            protected void doPost(Integer result) {
                if (result == ErrCode.COMN_OK) {
                    if (position == 0) {
                        cam.config.parkingPowerMode = CamConfig.PARKING_POWER_MODE.MINUTE_15;
                    } else if (position == 1) {
                        cam.config.parkingPowerMode = CamConfig.PARKING_POWER_MODE.HOUR_1;
                    } else if (position == 2) {
                        cam.config.parkingPowerMode = CamConfig.PARKING_POWER_MODE.HOUR_6;
                    } else if (position == 3) {
                        cam.config.parkingPowerMode = CamConfig.PARKING_POWER_MODE.DAY_1;
                    } else {
                        cam.config.parkingPowerMode = CamConfig.PARKING_POWER_MODE.NEVER;
                    }
                    tv_duration.setText(durationList.get(position));
                } else {
                    ToastUtil.TShort(_mActivity, "设置失败");
                }
            }
        };
    }

    /**
     * 设置灵敏度
     *
     * @param position
     */
    private void pickSensitity(final int position) {
        new VTask<Object, Integer>() {
            @Override
            protected Integer doBackground(Object o) {
                GeneralParam param = new GeneralParam();
                param.intParam.put(CamConfig.PARAMS_KEY.GSENSOR_MODE.key, position);

                return cameraServer.generalSaveParams(cam, param).faultNo;
            }

            @Override
            protected void doPost(Integer result) {
                if (result == ErrCode.COMN_OK) {
                    if (position == 0) {
                        cam.config.gSensor = CamConfig.LEVEL.HIGH;
                    } else if (position == 1) {
                        cam.config.gSensor = CamConfig.LEVEL.MIDDLE;
                    } else if (position == 2) {
                        cam.config.gSensor = CamConfig.LEVEL.LOW;
                    } else if (position == 3) {
                        cam.config.gSensor = CamConfig.LEVEL.OFF;
                    }
                    tv_sensitivity.setText(sensitivityList.get(position));
                } else {
                    Toast.makeText(_mActivity, "保存失败。", Toast.LENGTH_LONG).show();
                }
            }
        };
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
                        case SWITCH_BOOT_SOUND: {
                            cam.config.bootSound = config;
                            break;
                        }
                        case SWITCH_PARKING_MODE: {
                            cam.config.parkingMode = config;
                            break;
                        }
                        case SWITCH_HORIZONAL_MIRROR: {
                            cam.config.horizontalMirrorMode = config;
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
}
