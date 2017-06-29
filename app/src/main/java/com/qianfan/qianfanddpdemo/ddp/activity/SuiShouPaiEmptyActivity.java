package com.qianfan.qianfanddpdemo.ddp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ddp.sdk.base.DDPSDK;
import com.ddp.sdk.cam.resmgr.CameraResMgr;
import com.ddp.sdk.cambase.CameraServer;
import com.ddp.sdk.cambase.model.Camera;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.base.BaseActivity;
import com.qianfan.qianfanddpdemo.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 随手拍为空页面
 *
 * @author WangJing on 2016/12/22 0022 09:56
 * @e-mail wangjinggm@gmail.com
 * @see [相关类/方法](可选)
 */

public class SuiShouPaiEmptyActivity extends BaseActivity {
    private Context context;
    private Toolbar toolbar;
    private LinearLayout layout_noadd;
    private Button btn_add;


    // 已添加的摄像机列表
    private List<Camera> cameraList = new ArrayList<>();

//    private SuiShouPaiAdapter adapter;


    // 摄像机服务类
    private CameraServer cameraServer;
    // 升级服务类
//    private UpdateServer updateServer;
    // 网络服务类
//    private NetworkMgr networkMgr;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWhite(R.layout.activity_suishoupai);
        initP();
        initView();
    }


    private void initP() {
        context = getBaseContext();
        // SDK的初始化必须在主线程中运行,必须先初始化SDK，后面的才能操作,AndroidManifest文件需要声明 DDP_APPKEY 和 DDP_APPSECRET 才能通过认证
        DDPSDK.init(context, "");
        // 设置日志级别
        DDPSDK.logDetail(true);
        // 获取摄像机服务类对象 在onDestory() 方法中需要调用cameraServer.destory()来销毁
        cameraServer = CameraServer.intance();
//        updateServer = UpdateServer.intance();
//        networkMgr = NetworkMgr.instance();

        // 启动APP后，必须启用资源管理模块
        CameraResMgr resMgr = CameraResMgr.instance();
        // 如果需要使用轨迹资源，需要优先配置参数
        // 设置自动下载，轨迹数据默认不自动下载，
        resMgr.setTrackAutoDownload(false);
        // 设置本地轨迹最大保留数，超过则循环覆盖最老轨迹，默认20
        resMgr.setTrackKeepNumber(50);
        // 轨迹压缩文件存放路径Track.pathCompress，默认最高1万点，压缩后的轨迹，在地图上画线不失真
        resMgr.setTrackCompressionPointSize(1000);

        // 记录原始网络
//        VLog.v("SuiShouPaiActivity", "保存原始网络");
//        networkMgr.recordOriginalNetwork();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btn_add = (Button) findViewById(R.id.btn_add);
        layout_noadd = (LinearLayout) findViewById(R.id.layout_noadd);
        setBaseBackToolbar(toolbar, "附近摄像机");
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuiShouPaiEmptyActivity.this, CameraSearchActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCameraListData();
//        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (networkMgr != null) {
//            networkMgr.destroy();
//        }
//        if (cameraServer!=null){
//            cameraServer.destroy();
//        }
    }

    /**
     * 更新已添加的摄像机列表
     */
    private void updateCameraListData() {
        // 初始化摄像机列表
        cameraList = cameraServer.getCameras();
        if (cameraList != null && !cameraList.isEmpty()) {
            LogUtil.e("updateCameraListData", "不为空");
            Intent intent = new Intent(SuiShouPaiEmptyActivity.this, SuiShouPaiMainActivity.class);
            startActivity(intent);
            finish();
        } else {
            LogUtil.e("updateCameraListData", "为空");
            layout_noadd.setVisibility(View.VISIBLE);
        }
    }
}
