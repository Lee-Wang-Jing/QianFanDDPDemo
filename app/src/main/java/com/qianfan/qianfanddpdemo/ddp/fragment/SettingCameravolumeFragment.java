package com.qianfan.qianfanddpdemo.ddp.fragment;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ddp.sdk.base.DDPSDK;
import com.ddp.sdk.cambase.CameraServer;
import com.ddp.sdk.cambase.model.CamConfig;
import com.ddp.sdk.cambase.model.Camera;
import com.ddp.sdk.cambase.model.GeneralParam;
import com.ddp.sdk.cambase.utils.VTask;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.base.BaseFragment;
import com.qianfan.qianfanddpdemo.utils.ToastUtil;
import com.vyou.app.sdk.transport.ErrCode;
import com.vyou.app.sdk.utils.VLog;

/**
 * 摄像机音量设置
 * 功能详细描述
 *
 * @author xyx on 2017/1/23 16:12
 * @e-mail 384744573@qq.com
 * @see [相关类/方法](可选)
 */

public class SettingCameravolumeFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = SettingCameravolumeFragment.class.getSimpleName();
    private Toolbar toolbar;
    private ImageView iv_volume_subtract, iv_volume_add;
    private SeekBar seekBar;
    private TextView tv_volume_value;

    private Camera cam;
    private CameraServer cameraServer;


    public static SettingCameravolumeFragment newInstance() {
        Bundle args = new Bundle();
        SettingCameravolumeFragment fragment = new SettingCameravolumeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_setting_camera_volume;
    }

    @Override
    protected void init(View view) {
        initView(view);
    }

    private void initView(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        tv_volume_value = (TextView) view.findViewById(R.id.tv_volume_value);
        iv_volume_subtract = (ImageView) view.findViewById(R.id.iv_volume_subtract);
        iv_volume_add = (ImageView) view.findViewById(R.id.iv_volume_add);

        initLazyView();
    }

    private void initLazyView() {
        setBaseBackToolbar(toolbar, "音量调节");

        // SDK的初始化必须在主线程中运行,必须先初始化SDK，后面的才能操作,AndroidManifest文件需要声明 DDP_APPKEY 和 DDP_APPSECRET 才能通过认证
        DDPSDK.init(_mActivity, "");
        cameraServer = CameraServer.intance();
        cam = cameraServer.getCurrentConnectCamera();
        if (cam == null || !cam.isConnected) {
            VLog.e(TAG, "cam == null || !cam.isConnected finish.");
            ToastUtil.TShort(_mActivity,"摄像机连接异常，请重新连接");
            _mActivity.finish();
            return;
        }

        tv_volume_value.setText("摄像机音量（" + cam.config.sound + "）");
        seekBar.setProgress(cam.config.sound);

        iv_volume_subtract.setOnClickListener(this);
        iv_volume_add.setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_volume_value.setText("摄像机音量（" + progress + "）");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                final int progress = seekBar.getProgress();
                new VTask<Object, Integer>() {

                    @Override
                    protected Integer doBackground(Object o) {
                        GeneralParam param = new GeneralParam();
                        param.intParam.put(CamConfig.PARAMS_KEY.SPEAKER.key, progress);
                        return cameraServer.generalSaveParams(cam, param).faultNo;
                    }

                    @Override
                    protected void doPost(Integer faultNo) {
                        if (faultNo == ErrCode.COMN_OK) {
                            cam.config.sound = seekBar.getProgress();
                            // VToast.makeText(getActivity(), R.string.save_success, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(_mActivity, "保存失败。", Toast.LENGTH_LONG).show();
                        }
                    }
                };
            }
        });


    }

    @Override
    public void onClick(View v) {
        int value = seekBar.getProgress();
        switch (v.getId()) {
            case R.id.iv_volume_add://增加音量
                if (value <= 90) {
                    value = value + 10;
                } else {
                    value = 100;
                }
                seekBar.setProgress(value);
                break;
            case R.id.iv_volume_subtract://减少音量
                if (value >= 10) {
                    value = value - 10;
                } else {
                    value = 0;
                }
                seekBar.setProgress(value);
                break;
        }
    }
}
