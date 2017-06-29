package com.qianfan.qianfanddpdemo.ddp.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddp.sdk.base.DDPSDK;
import com.ddp.sdk.cambase.CameraServer;
import com.ddp.sdk.cambase.model.CamConfig;
import com.ddp.sdk.cambase.model.Camera;
import com.ddp.sdk.cambase.model.GeneralParam;
import com.ddp.sdk.cambase.utils.VTask;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.base.BaseFragment;
import com.qianfan.qianfanddpdemo.utils.LogUtil;
import com.qianfan.qianfanddpdemo.utils.ToastUtil;
import com.vyou.app.sdk.transport.ErrCode;

/**
 * 摄像机图像质量
 *
 * @author xyx on 2017/1/24 09:06
 * @e-mail 384744573@qq.com
 * @see [相关类/方法](可选)
 */

public class SettingCameraQualityFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = SettingCameraQualityFragment.class.getSimpleName();
    private Toolbar toolbar;
    private TextView tv_qulity_value;
    private ImageView iv_quality_2k, iv_quality_fhd, iv_quality_hd;

    private int currentQuality = 1;//当前选择的图像质量

    private Camera cam;
    private CameraServer cameraServer;
    private ProgressDialog progressDialog;

    public static SettingCameraQualityFragment newInstance() {
        Bundle args = new Bundle();
        SettingCameraQualityFragment fragment = new SettingCameraQualityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_setting_camera_quality;
    }

    @Override
    protected void init(View view) {
        initView(view);
    }


    private void initView(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        tv_qulity_value = (TextView) view.findViewById(R.id.tv_qulity_value);
        iv_quality_2k = (ImageView) view.findViewById(R.id.iv_quality_2k);
        iv_quality_fhd = (ImageView) view.findViewById(R.id.iv_quality_fhd);
        iv_quality_hd = (ImageView) view.findViewById(R.id.iv_quality_hd);

        initLazyView();
    }

    private void initLazyView() {
        setBaseBackToolbar(toolbar, "图像质量");

        iv_quality_2k.setOnClickListener(this);
        iv_quality_fhd.setOnClickListener(this);
        iv_quality_hd.setOnClickListener(this);

        // SDK的初始化必须在主线程中运行,必须先初始化SDK，后面的才能操作,AndroidManifest文件需要声明 DDP_APPKEY 和 DDP_APPSECRET 才能通过认证
        DDPSDK.init(_mActivity, "");
        cameraServer = CameraServer.intance();
        cam = cameraServer.getCurrentConnectCamera();

        if (cam == null || !cam.isConnected) {
            LogUtil.e(TAG, "cam == null || !cam.isConnected finish.");
            ToastUtil.TShort(_mActivity,"摄像机连接异常，请重新连接");
            _mActivity.finish();
            return;
        }

        setConfig();
    }

    private void setConfig() {
        if (cam.config.graphicQC == CamConfig.LEVEL.HIGH) {
            currentQuality = 1;
            tv_qulity_value.setText("图像质量：(全高清)1080P");
            iv_quality_2k.setImageResource(R.mipmap.icon_quality_choose_on);
            iv_quality_fhd.setImageResource(R.mipmap.icon_quality_choose_off);
            iv_quality_hd.setImageResource(R.mipmap.icon_quality_choose_off);
        } else if (cam.config.graphicQC == CamConfig.LEVEL.MIDDLE) {
            currentQuality = 2;
            tv_qulity_value.setText("图像质量：(高清)720P");
            iv_quality_2k.setImageResource(R.mipmap.icon_quality_choose_off);
            iv_quality_fhd.setImageResource(R.mipmap.icon_quality_choose_on);
            iv_quality_hd.setImageResource(R.mipmap.icon_quality_choose_off);
        } else if (cam.config.graphicQC == CamConfig.LEVEL.LOW) {
            currentQuality = 3;
            tv_qulity_value.setText("图像质量：(标清)480P");
            iv_quality_2k.setImageResource(R.mipmap.icon_quality_choose_off);
            iv_quality_fhd.setImageResource(R.mipmap.icon_quality_choose_off);
            iv_quality_hd.setImageResource(R.mipmap.icon_quality_choose_on);
        }
    }

    @Override
    public void onClick(View v) {
        if (progressDialog==null){
            progressDialog = new ProgressDialog(_mActivity);
        }
        progressDialog.setMessage("正在保存中");
        progressDialog.show();
        switch (v.getId()) {
            case R.id.iv_quality_2k://全高清
                if (currentQuality != 1) {
                    currentQuality = 1;
                    iv_quality_2k.setImageResource(R.mipmap.icon_quality_choose_on);
                    iv_quality_fhd.setImageResource(R.mipmap.icon_quality_choose_off);
                    iv_quality_hd.setImageResource(R.mipmap.icon_quality_choose_off);

                    tv_qulity_value.setText("图像质量：(全高清)1080P");
                    changeStatus(CamConfig.LEVEL.HIGH);
                }
                break;
            case R.id.iv_quality_fhd://高清
                if (currentQuality != 2) {
                    currentQuality = 2;
                    iv_quality_2k.setImageResource(R.mipmap.icon_quality_choose_off);
                    iv_quality_fhd.setImageResource(R.mipmap.icon_quality_choose_on);
                    iv_quality_hd.setImageResource(R.mipmap.icon_quality_choose_off);

                    tv_qulity_value.setText("图像质量：(高清)720P");
                    changeStatus(CamConfig.LEVEL.MIDDLE);
                }
                break;
            case R.id.iv_quality_hd://标清
                if (currentQuality != 3) {
                    currentQuality = 3;
                    iv_quality_2k.setImageResource(R.mipmap.icon_quality_choose_off);
                    iv_quality_fhd.setImageResource(R.mipmap.icon_quality_choose_off);
                    iv_quality_hd.setImageResource(R.mipmap.icon_quality_choose_on);

                    tv_qulity_value.setText("图像质量：(标清)480P");
                    changeStatus(CamConfig.LEVEL.LOW);
                }
                break;
        }
    }


    private void changeStatus(final CamConfig.LEVEL level) {
        new VTask<Object, Integer>() {
            @Override
            protected Integer doBackground(Object o) {
                GeneralParam param = new GeneralParam();
//                String value = JsonUtils.turnInt2String(position, new int[]{0, 1, 2}, new String[]{
//                        CamConfig.LEVEL.HIGH.key, CamConfig.LEVEL.MIDDLE.key, CamConfig.LEVEL.LOW.key});
                param.strParam.put(CamConfig.PARAMS_KEY.IMG_QUALITY.key, level.key);

                return cameraServer.generalSaveParams(cam, param).faultNo;
            }

            @Override
            protected void doPost(Integer result) {
                progressDialog.dismiss();
                if (result == ErrCode.COMN_OK) {
                    cam.config.graphicQC = level;
                } else {
                    ToastUtil.TShort(_mActivity, "设置失败");
                    setConfig();
                }
            }
        };
    }


}
