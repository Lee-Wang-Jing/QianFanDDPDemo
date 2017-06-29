package com.qianfan.qianfanddpdemo.ddp.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ddp.sdk.base.DDPSDK;
import com.ddp.sdk.cambase.CameraServer;
import com.ddp.sdk.cambase.model.Camera;
import com.ddp.sdk.cambase.utils.VTask;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.base.BaseFragment;
import com.qianfan.qianfanddpdemo.utils.ToastUtil;
import com.vyou.app.sdk.utils.VLog;

/**
 * 摄像机名称设置
 *
 * @author xyx on 2017/1/23 14:56
 * @e-mail 384744573@qq.com
 * @see [相关类/方法](可选)
 */

public class SettingCameraNameFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = SettingCameraNameFragment.class.getSimpleName();
    private Toolbar toolbar;
    private EditText et_camera_name;
    private TextView tv_save;

    private Camera cam;
    private CameraServer cameraServer;
    private ProgressDialog progressDialog;

    public static SettingCameraNameFragment newInstance() {
        Bundle args = new Bundle();
        SettingCameraNameFragment fragment = new SettingCameraNameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_setting_camera_name;
    }

    @Override
    protected void init(View view) {
        initView(view);
    }

    private void initView(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        tv_save = (TextView) view.findViewById(R.id.tv_save);
        et_camera_name = (EditText) view.findViewById(R.id.et_camera_name);

        initLazyView();
    }

    private void initLazyView() {
        setBaseBackToolbar(toolbar, "设置摄像机名称");

        // SDK的初始化必须在主线程中运行,必须先初始化SDK，后面的才能操作,AndroidManifest文件需要声明 DDP_APPKEY 和 DDP_APPSECRET 才能通过认证
        DDPSDK.init(_mActivity, "");
        cameraServer = CameraServer.intance();
        cam = cameraServer.getCurrentConnectCamera();
        if (cam == null || !cam.isConnected)
        {
            ToastUtil.TShort(_mActivity,"摄像机连接异常，请重新连接");
            _mActivity.finish();
            return;
        }

        et_camera_name.setText(""+cam.getName());
        tv_save.setEnabled(false);
        tv_save.setOnClickListener(this);

        et_camera_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    tv_save.setEnabled(true);
                    tv_save.setTextColor(getResources().getColor(R.color.color_666666));
                } else {
                    tv_save.setEnabled(false);
                    tv_save.setTextColor(getResources().getColor(R.color.color_cccccc));
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_save://保存
                hideSoftInput();
                String camera_name = et_camera_name.getText().toString().trim();
                if (TextUtils.isEmpty(camera_name)) {
                    return;
                }
                if (progressDialog==null){
                    progressDialog=new ProgressDialog(_mActivity);
                }
                progressDialog.setMessage("正在保存中");
                progressDialog.show();
                setCameraWifiAccount();
                break;
        }
    }

    private void setCameraWifiAccount()
    {
        new VTask<Object, Integer>()
        {
            @Override
            protected Integer doBackground(Object o)
            {
                int errcode = cameraServer.setCameraWifiAccount(cam, et_camera_name.getText().toString(), cam.netInfo.wifiPWD);
                return errcode;
            }

            @Override
            protected void doPost(Integer faultNo)
            {
                progressDialog.dismiss();
                VLog.v(TAG, "faultNo = " + faultNo);
                if (faultNo == com.ddp.sdk.base.ErrCode.COMN_OK)
                {
                    Toast.makeText(_mActivity, "保存成功。", Toast.LENGTH_LONG).show();
                    pop();
                }
                else if (faultNo == CameraServer.WIFI_ACCOUNT_PARAMS_EXCEPTION)
                {
                    Toast.makeText(_mActivity, "参数错误。", Toast.LENGTH_LONG).show();
                }
                else if (faultNo == CameraServer.WIFI_SAVE_FAIL)
                {
                    Toast.makeText(_mActivity, "保存失败。", Toast.LENGTH_LONG).show();
                }
                else if (faultNo == CameraServer.WIFI_CONNECTED_FAIL)
                {
                    Toast.makeText(_mActivity, "WIFI连接失败。", Toast.LENGTH_LONG).show();
                }
                else if (faultNo == CameraServer.WIFI_RESTART_FAIL)
                {
                    Toast.makeText(_mActivity, "WIFI重启失败。", Toast.LENGTH_LONG).show();
                }
            }
        };
    }
}
