package com.qianfan.qianfanddpdemo.ddp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ddp.sdk.base.DDPSDK;
import com.ddp.sdk.base.tools.VTask;
import com.ddp.sdk.cambase.CameraServer;
import com.ddp.sdk.cambase.exception.CamConnTaskException;
import com.ddp.sdk.cambase.listener.OnCamConnTaskListener;
import com.ddp.sdk.cambase.model.CamConnTask;
import com.ddp.sdk.cambase.model.Camera;
import com.ddp.sdk.cambase.network.NetworkMgr;
import com.ddp.sdk.cambase.network.WifiHandler;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.base.BaseActivity;
import com.qianfan.qianfanddpdemo.entity.FooterEntity;
import com.qianfan.qianfanddpdemo.recyclerview.BaseAdapter;
import com.qianfan.qianfanddpdemo.recyclerview.BaseViewHolder;
import com.qianfan.qianfanddpdemo.recyclerview.PullRecyclerView;
import com.qianfan.qianfanddpdemo.utils.LogUtil;
import com.qianfan.qianfanddpdemo.utils.ToastUtil;
import com.qianfan.qianfanddpdemo.utils.Utils;

import java.util.ArrayList;
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
 * 随手拍--相机搜索添加
 *
 * @author WangJing on 2016/12/21 0021 14:10
 * @e-mail wangjinggm@gmail.com
 * @see [相关类/方法](可选)
 */

public class CameraSearchActivity extends BaseActivity implements PullRecyclerView.OnRecyclerRefreshListener {
    private static final int RequestCode = 110;

    private Toolbar toolbar;
    private TextView tv_help;
    private EditText et_pw;
    private Button btn_connect;
    private ImageView iv_empty;
    private PullRecyclerView pullRecyclerView;
    private CameraSearchActivityAdapter adapter;
    // 搜索到的摄像机wifi列表
    private List<WifiHandler.VWifi> cameraWifiList = new ArrayList<>();

    // 网络服务类对象
    private NetworkMgr netMgr;
    // 摄像机服务类对象
    private CameraServer cameraServer;
    private Camera camera;

    private int now_position = -1;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWhite(R.layout.activity_camerasearch);
        DDPSDK.init(this, "");
        initP();
        initView();
    }


    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tv_help = (TextView) findViewById(R.id.tv_help);
        pullRecyclerView = (PullRecyclerView) findViewById(R.id.pullRecyclerView);
        et_pw = (EditText) findViewById(R.id.et_pw);
        btn_connect = (Button) findViewById(R.id.btn_connect);
        iv_empty = (ImageView) findViewById(R.id.iv_empty);
        setBaseBackToolbar(toolbar, "附近摄像机");
        et_pw.clearFocus();
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftInput();
                if (!TextUtils.isEmpty(et_pw.getText().toString().trim())) {
                    if (now_position >= 0 && now_position < cameraWifiList.size()) {
                        btn_connect.setEnabled(false);
                        btn_connect.setText("正在连接");
                        showDialog();
                        WifiHandler.VWifi vwifi = cameraWifiList.get(now_position);
                        vwifi.wifiPwd = et_pw.getText().toString();
                        addCamera(vwifi.BSSID, vwifi.SSID, vwifi.wifiPwd);

                    } else {
                        ToastUtil.TShort(CameraSearchActivity.this, "请先选择对应的wifi");
                    }
                } else {
                    ToastUtil.TShort(CameraSearchActivity.this, "登录密码不能为空哦！");
                }
            }
        });
        adapter = new CameraSearchActivityAdapter(this);
        pullRecyclerView.setOnRefreshListener(this);
        pullRecyclerView.setLayoutManager(getLayoutManager());
        pullRecyclerView.setAdapter(adapter);
//        pullRecyclerView.setRefreshing();
        tv_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                ToastUtil.TLong(CameraSearchActivity.this,"跳转到连接帮助");
//                IntentUtils.jumpWebviewActivity(CameraSearchActivity.this, "file:///android_asset/linkhelp_cn.html", "连接帮助");
            }
        });
        CheckLocationStatus();
        et_pw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    iv_empty.setVisibility(View.VISIBLE);
                } else {
                    iv_empty.setVisibility(View.GONE);
                }
            }
        });
        iv_empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_pw.setText("");
            }
        });
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


    private void initP() {
        netMgr = NetworkMgr.instance();
        cameraServer = CameraServer.intance();
    }


    @Override
    protected void onResume() {
        super.onResume();
        searchCamera();
    }

    /**
     * 搜索摄像机
     */
    private void searchCamera() {
        mLoadingView.showLoading();
        // 异步查找
        new VTask<Object, List<WifiHandler.VWifi>>() {
            @Override
            protected void doPre() {
                super.doPre();
            }

            @Override
            protected List<WifiHandler.VWifi> doBackground(Object o) {
                // 过滤"vyou_", "wifi_"开头的wifi
                return netMgr.wifiHandler.getCameraWifiList(new String[]{"vyou_", "wifi_"});
            }

            @Override
            protected void doPost(List<WifiHandler.VWifi> list) {
                if (list != null && list.size() > 0) {
                    LogUtil.e("doPost", "list.size() = " + list.size());
//                    cameraWifiAdapter.notifyDataSetInvalidated();
                    cameraWifiList = list;
                    adapter.notifyDataSetChanged();
                    adapter.notifyFooterState(new FooterEntity(2));
                    pullRecyclerView.onRefreshCompleted();
                    pullRecyclerView.enableLoadMore(false);
                    mLoadingView.dismissLoadingView();
                } else {
                    mLoadingView.showEmpty("未检测到摄像机");
                    mLoadingView.setOnEmptyClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            searchCamera();
                        }
                    });
                    LogUtil.e("doPost", "list == null || list.size() = 0");
                }


            }
        };
    }

    @Override
    public void onRefresh(int action) {
//        if (action == PullRecyclerView.ACTION_PULL_TO_REFRESH) {
//            page = 0;
//        } else {
//            page++;
//        }
        searchCamera();
    }

    private void showDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("正在连接盯盯拍…");
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (cameraServer != null && camera != null) {
                        cameraServer.stopCameraConnTask(camera);
                    }
                }
            });
        }
        progressDialog.show();
    }

    private void dissMissDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 添加摄像机
     *
     * @param bssid 摄像机bssid
     * @param ssid  摄像机 ssid
     * @param psw   摄像机 密码
     */
    private void addCamera(String bssid, String ssid, String psw) {
        LogUtil.e("addCamera bssid = " + bssid + ", ssid = " + ssid + ", psw = " + psw);
        camera = new Camera(bssid, ssid, psw);
        cameraServer.startCameraConnTask(camera, true, new OnCamConnTaskListener() {
            @Override
            public void onConnecting(CamConnTask camConnTask) {
                LogUtil.e("onConnect");
            }

            @Override
            public void onCancel(CamConnTask task) {
                LogUtil.e("onCancel");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_connect.setEnabled(true);
                        btn_connect.setText("连接");
                        dissMissDialog();
                        Toast.makeText(CameraSearchActivity.this, "连接取消", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onException(final CamConnTaskException e, CamConnTask task) {
                LogUtil.e("onException e = " + e.toString() + "e.getcode==>" + e.getCode());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_connect.setEnabled(true);
                        btn_connect.setText("连接");
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
                        Toast.makeText(CameraSearchActivity.this, "" + error, Toast.LENGTH_LONG).show();
                        dissMissDialog();
                    }
                });
            }

            @Override
            public void onSucceed(Camera camera, CamConnTask task) {
                LogUtil.e("onSucceed add camera successful");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_connect.setEnabled(true);
                        btn_connect.setText("连接");
                        dissMissDialog();
                        Toast.makeText(CameraSearchActivity.this, "连接成功", Toast.LENGTH_LONG).show();
//                        Intent intent = new Intent(CameraSearchActivity.this, SuiShouPaiMainActivity.class);
                        setResult(RequestCode);
//                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }


//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (cameraServer != null) {
//            cameraServer.destroy();
//        }
//    }

    public class CameraSearchActivityAdapter extends BaseAdapter {
        private Context mContext;

        public CameraSearchActivityAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        protected int getDataCount() {
            return cameraWifiList != null ? cameraWifiList.size() : 0;
        }

        @Override
        protected BaseViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_camerasearchadapter, parent, false);
            return new ItemViewHolder(view);
        }


        class ItemViewHolder extends BaseViewHolder {

            TextView tv_name;
            ImageView iv_choose;

            public ItemViewHolder(View itemView) {
                super(itemView);
                tv_name = (TextView) itemView.findViewById(R.id.tv_name);
                iv_choose = (ImageView) itemView.findViewById(R.id.iv_choose);
            }

            @Override
            public void onBindViewHolder(int position) {
                if (now_position == position) {
                    tv_name.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                    iv_choose.setVisibility(View.VISIBLE);
                } else {
                    tv_name.setTextColor(ContextCompat.getColor(mContext, R.color.color_222222));
                    iv_choose.setVisibility(View.INVISIBLE);
                }
                tv_name.setText(cameraWifiList.get(position).SSID);
            }

            @Override
            public void onItemClick(View view, int position) {
                now_position = position;
                notifyDataSetChanged();
                et_pw.setText("1234567890");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (cameraServer != null) {
//            cameraServer.destroy();
//        }
    }
}
