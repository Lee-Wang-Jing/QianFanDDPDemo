package com.qianfan.qianfanddpdemo.ddp.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ddp.sdk.base.DDPSDK;
import com.ddp.sdk.cam.resmgr.CameraResMgr;
import com.ddp.sdk.cambase.CameraServer;
import com.ddp.sdk.cambase.exception.CamConnTaskException;
import com.ddp.sdk.cambase.listener.OnCamConnTaskListener;
import com.ddp.sdk.cambase.model.CamConnTask;
import com.ddp.sdk.cambase.model.Camera;
import com.ddp.sdk.cambase.network.NetworkMgr;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.base.BaseDDPActivity;
import com.qianfan.qianfanddpdemo.ddp.adapter.SuiShouPaiMainAdapter;
import com.qianfan.qianfanddpdemo.entity.FooterEntity;
import com.qianfan.qianfanddpdemo.myinterface.OOnConnectDDPListener;
import com.qianfan.qianfanddpdemo.myinterface.OnDDPCameraEmptyListener;
import com.qianfan.qianfanddpdemo.recyclerview.PullRecyclerView;
import com.qianfan.qianfanddpdemo.utils.LogUtil;
import com.qianfan.qianfanddpdemo.utils.ToastUtil;
import com.qianfan.qianfanddpdemo.utils.Utils;
import com.vyou.app.sdk.utils.VLog;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.ddp.sdk.cambase.model.UCode.DEV_AUTH_LOGON_FAILED;
import static com.ddp.sdk.cambase.model.UCode.DEV_CAPABLE_NEGOTIATION_FAILED;
import static com.ddp.sdk.cambase.model.UCode.DEV_GET_BASEINFO_FAILED;
import static com.ddp.sdk.cambase.model.UCode.DEV_LEGAL_ERROR;
import static com.ddp.sdk.cambase.model.UCode.DEV_OBTAIN_SESSION_FAILED;
import static com.ddp.sdk.cambase.model.UCode.DEV_OBTAIN_TYPE_FAILED;
import static com.ddp.sdk.cambase.model.UCode.DEV_UNKNOWN_ERROR;
import static com.ddp.sdk.cambase.model.UCode.DEV_WIFI_CANNOT_USE;
import static com.ddp.sdk.cambase.model.UCode.METHOD_PARAMS_ERROR;
import static com.ddp.sdk.cambase.model.UCode.WIFI_CONN_TIMEOUT;
import static com.ddp.sdk.cambase.model.UCode.WIFI_PWD_ERROR;

/**
 * Created by wangjing on 2017/1/11.
 */

public class SuiShouPaiMainActivity extends BaseDDPActivity implements PullRecyclerView.OnRecyclerRefreshListener, OOnConnectDDPListener
        , OnDDPCameraEmptyListener {
    private final int BASIC_PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = "SuiShouPaiMainActivity";
    private static final int RequestCode = 110;

    private Toolbar toolbar;
    private PullRecyclerView pullRecyclerView;
    private LinearLayout ll_empty;
    private Button btn_add;

    private SuiShouPaiMainAdapter adapter;

    private ProgressDialog progressDialog;


    private Context context;
    // 摄像机服务类
    private CameraServer cameraServer;
    private Camera camera;
    // 升级服务类
//    private UpdateServer updateServer;
    // 启动APP后，必须启用资源管理模块
    private CameraResMgr cameraResMgr;
    // 网络服务类
    private NetworkMgr networkMgr;
//    private NetworkSwitchListener networkSwitchListener;

    // 已添加的摄像机列表
    private List<Camera> cameraList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWhite(R.layout.activity_suishoupaimain);
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
        networkMgr = NetworkMgr.instance();

        // 启动APP后，必须启用资源管理模块
        cameraResMgr = CameraResMgr.instance();
        // 如果需要使用轨迹资源，需要优先配置参数
        // 设置自动下载，轨迹数据默认不自动下载，
        cameraResMgr.setTrackAutoDownload(false);
        // 设置本地轨迹最大保留数，超过则循环覆盖最老轨迹，默认20
        cameraResMgr.setTrackKeepNumber(20);
        // 轨迹压缩文件存放路径Track.pathCompress，默认最高1万点，压缩后的轨迹，在地图上画线不失真
        cameraResMgr.setTrackCompressionPointSize(1000);

        // 记录原始网络
        networkMgr.recordOriginalNetwork();
//        cameraServer.addCameraStateChangeListener(camLifeCycleListener);

//        networkSwitchListener = new NetworkSwitchListener() {
//            @Override
//            public void onCameraWifiDisconnected(boolean isPreDisConn) {
//
//            }
//
//            @Override
//            public void onCameraWifiConnected(Camera dev) {
//
//            }
//
//            @Override
//            public void onInternetConnected(VNetworkInfo curInfo) {
//
//            }
//        };
//
//        networkMgr.registerNetworkSwitchListener(networkSwitchListener);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        pullRecyclerView = (PullRecyclerView) findViewById(R.id.pullRecyclerView);
        ll_empty = (LinearLayout) findViewById(R.id.ll_empty);
        btn_add = (Button) findViewById(R.id.btn_add);
        setBaseBackToolbar(toolbar, "随手拍");
        toolbar.inflateMenu(R.menu.menu_suishoupaimainactivity);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add:
                        Intent intent = new Intent(SuiShouPaiMainActivity.this, CameraSearchActivity.class);
                        startActivityForResult(intent, RequestCode);
                        break;
                    case R.id.action_setting:
                        startActivity(new Intent(SuiShouPaiMainActivity.this, SettingCameraActivity.class)
                                .putExtra("type", 1)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        break;
                }
                return true;
            }
        });
        adapter = new SuiShouPaiMainAdapter(this);
        adapter.setOnConnectDDPListener(this);
        adapter.setOnDDPCameraEmptyListener(this);
        adapter.setCameraServer(cameraServer);
        adapter.notifyFooterState(new FooterEntity(2));
        pullRecyclerView.setOnRefreshListener(this);
        pullRecyclerView.setLayoutManager(getLayoutManager());
        pullRecyclerView.setAdapter(adapter);
        pullRecyclerView.enableLoadMore(false);

        requestBasicPermission();
    }

    /**
     * 更新已添加的摄像机列表
     */
    private void updateCameraListData() {
        // 初始化摄像机列表
        cameraList = cameraServer.getCameras();
        Camera camera = cameraServer.getCurrentConnectCamera();
        if (cameraList == null || cameraList.isEmpty()) {
            ll_empty.setVisibility(View.VISIBLE);
            pullRecyclerView.setVisibility(View.GONE);
            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SuiShouPaiMainActivity.this, CameraSearchActivity.class);
                    startActivityForResult(intent, RequestCode);
                }
            });
        } else {
            Collections.sort(cameraList, new Comparator<Camera>() {
                @Override
                public int compare(Camera o1, Camera o2) {
                    if (o1.isConnected && !o2.isConnected) {
                        return -1;
                    } else if (!o1.isConnected && o2.isConnected) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            ll_empty.setVisibility(View.GONE);
            pullRecyclerView.setVisibility(View.VISIBLE);
            LogUtil.e("size", "cameraList.size==>" + cameraList.size());
            adapter.clear();
            adapter.addData(cameraList, camera, cameraResMgr, networkMgr);
            adapter.notifyFooterState(new FooterEntity(4));
        }
        pullRecyclerView.onRefreshCompleted();
    }

    private void showProgress() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在连接盯盯拍…");
        }
        progressDialog.show();
    }

    private void dissmissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * 基本权限管理
     */
    private void requestBasicPermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE};
        // 先判断是否有权限。
        if (AndPermission.hasPermission(this, perms)) {
            // 有权限，直接do anything.
            CheckLocationStatus();
        } else {
            // 申请权限。
            AndPermission.with(this).requestCode(BASIC_PERMISSION_REQUEST_CODE).permission(perms).callback(listener).start();
        }

    }

    /**
     * 判断用户位置权限是否开启
     */
    private void CheckLocationStatus() {
        if (!Utils.isGPSOpen(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("链接盯盯拍最好打开手机位置服务，否则可能搜索不到附近的盯盯拍");
            builder.setPositiveButton("查看", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("忽略", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。
            CheckLocationStatus();
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。

            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(SuiShouPaiMainActivity.this, deniedPermissions)) {
                // 第一种：用默认的提示语。
                AndPermission.defaultSettingDialog(SuiShouPaiMainActivity.this, BASIC_PERMISSION_REQUEST_CODE).show();
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        if (cameraServer != null) {
            cameraServer.addCameraStateChangeListener(camLifeCycleListener);
        }
        updateCameraListData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraServer != null) {
            cameraServer.removeCameraStateChangeListener(camLifeCycleListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBackground(context)) {
            VLog.v(TAG, "后台运行，恢复网络");
            networkMgr.restoreOriginalNetwork();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (cameraServer != null) {
                if (camera != null && camera.isConnected) {
                    cameraServer.stopCameraConnTask(camera);
                }
                cameraServer.removeCameraStateChangeListener(camLifeCycleListener);
//                cameraServer.destroy();
            }
//            if (CameraResMgr.instance() != null) {
//                CameraResMgr.instance().destory();
//                if (cameraResMgr != null) {
//                    cameraResMgr.destory();
//                }
//            }
            if (networkMgr != null) {
                networkMgr.restoreOriginalNetwork();
                networkMgr.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e("requestCode", "requestCode==>" + requestCode);
        LogUtil.e("resultCode", "resultCode==>" + resultCode);
        if (requestCode == RequestCode && resultCode == RequestCode) {
            updateCameraListData();
        }
    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                    /*
                    BACKGROUND=400 EMPTY=500 FOREGROUND=100
                    GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                     */
                LogUtil.i(context.getPackageName(), "此appimportace =" + appProcess.importance + ",context.getClass().getName()=" + context
                        .getClass().getName());
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    LogUtil.i(context.getPackageName(), "处于后台" + appProcess.processName);
                    return true;
                } else {
                    LogUtil.i(context.getPackageName(), "处于前台" + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public void onRefresh(int action) {
        updateCameraListData();
    }

    @Override
    public void onConnectDDPClick(Camera camera) {
        if (cameraServer != null && camera != null) {
            this.camera = camera;
            cameraServer.startCameraConnTask(camera, true, new OnCamConnTaskListener() {
                @Override
                public void onConnecting(CamConnTask camConnTask) {
                    LogUtil.i("onConnectDDPClick", "onConnecting");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showProgress();
                        }
                    });
                }

                @Override
                public void onCancel(CamConnTask camConnTask) {
                    LogUtil.i("onConnectDDPClick", "onCancel");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dissmissProgress();
                        }
                    });
                }

                @Override
                public void onException(final CamConnTaskException e, CamConnTask camConnTask) {
                    LogUtil.e("onException e = " + e.toString() + "e.getcode==>" + e.getCode());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String error = null;
                            switch (e.getCode()) {
                                case DEV_OBTAIN_TYPE_FAILED:
                                    error = "设备认证，获取设备类型失败";
                                    break;
                                case DEV_OBTAIN_SESSION_FAILED:
                                    error = "设备认证，获取session失败";
                                    break;
                                case DEV_GET_BASEINFO_FAILED:
                                    error = "设备认证，获取baseInfo失败";
                                    break;
                                case DEV_AUTH_LOGON_FAILED:
                                    error = "设备认证，登录认证失败（被拒绝了）";
                                    break;
                                case DEV_CAPABLE_NEGOTIATION_FAILED:
                                    error = "设备认证，能力协商失败 ";
                                    break;
                                case DEV_WIFI_CANNOT_USE:
                                    error = "设备WIFI，不可用（连接了还是用不了） ";
                                    break;
                                case DEV_LEGAL_ERROR:
                                    error = "设备非法，盗版设备";
                                    break;
                                case DEV_UNKNOWN_ERROR:
                                    error = "设备未知错误";
                                    break;
                                case METHOD_PARAMS_ERROR:
                                    error = "方法参数错误";
                                    break;
                                case WIFI_CONN_TIMEOUT:
                                    error = "WiFi连接超时";
                                    break;
                                case WIFI_PWD_ERROR:
                                    error = "WiFi密码错误";
                                    break;

                                default:
                                    error = "连接失败,请检查密码是否正确";
                                    break;
                            }
                            ToastUtil.TShort(SuiShouPaiMainActivity.this, "" + error);
                            dissmissProgress();
                        }
                    });
                }

                @Override
                public void onSucceed(Camera camera, CamConnTask camConnTask) {
                    LogUtil.i("onConnectDDPClick", "onSucceed");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.TShort(SuiShouPaiMainActivity.this, "连接成功");
                            dissmissProgress();
                            updateCameraListData();
                        }
                    });
                }
            });
        } else {
            if (cameraServer == null) {
                LogUtil.e("onConnectDDPClick", "cameraServer==null");
            }
            if (camera == null) {
                LogUtil.e("onConnectDDPClick", "camera==null");
            }
        }
    }

    @Override
    public void onDDPCameraEmptyListener() {
        Intent intent = new Intent(SuiShouPaiMainActivity.this, CameraSearchActivity.class);
        startActivityForResult(intent, RequestCode);
    }
}
