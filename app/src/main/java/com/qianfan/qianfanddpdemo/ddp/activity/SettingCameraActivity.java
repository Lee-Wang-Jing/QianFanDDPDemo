package com.qianfan.qianfanddpdemo.ddp.activity;

import android.os.Bundle;

import com.ddp.sdk.base.DDPSDK;
import com.ddp.sdk.cambase.CameraServer;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.base.BaseDDPActivity;
import com.qianfan.qianfanddpdemo.ddp.fragment.SettingCameraFragment;
import com.qianfan.qianfanddpdemo.ddp.fragment.SettingCameraFromPreviewsFragment;
import com.qianfan.qianfanddpdemo.utils.LogUtil;

/**
 * 摄像机设置
 * 功能详细描述
 *
 * @author xyx on 2017/1/23 11:40
 * @e-mail 384744573@qq.com
 * @see [相关类/方法](可选)
 */
public class SettingCameraActivity extends BaseDDPActivity {

    private CameraServer cameraServer;
    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWhite(R.layout.activity_my);
        type = getIntent().getIntExtra("type", 0);
        LogUtil.e("onCreate","type==>"+type);
        if (savedInstanceState == null) {
            if (type == 1) {
                loadRootFragment(R.id.fl_container_start, SettingCameraFromPreviewsFragment.newInstance());
            } else {
                loadRootFragment(R.id.fl_container_start, SettingCameraFragment.newInstance());
            }
        }

        try {
            // SDK的初始化必须在主线程中运行,必须先初始化SDK，后面的才能操作,AndroidManifest文件需要声明 DDP_APPKEY 和 DDP_APPSECRET 才能通过认证
            DDPSDK.init(this, "");
            cameraServer = CameraServer.intance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cameraServer != null) {
            cameraServer.addCameraStateChangeListener(camLifeCycleListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraServer != null) {
            cameraServer.removeCameraStateChangeListener(camLifeCycleListener);
            if (type == 0) {
                LogUtil.e("SettingCameraActivity","onDestroy 执行了 cameraServer.destroy()");
//                cameraServer.destroy();
            }
        }
    }
}
