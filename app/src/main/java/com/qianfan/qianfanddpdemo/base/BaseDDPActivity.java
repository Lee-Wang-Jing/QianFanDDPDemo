package com.qianfan.qianfanddpdemo.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.ddp.sdk.cambase.listener.OnCamLifeCycleListener;
import com.ddp.sdk.cambase.model.Camera;
import com.ddp.sdk.cambase.model.UCode;
import com.qianfan.qianfanddpdemo.ddp.activity.SuiShouPaiMainActivity;
import com.qianfan.qianfanddpdemo.utils.LogUtil;

/**
 * Created by wangjing on 2017/6/5.
 */

public class BaseDDPActivity extends BaseActivity {

    private Dialog ddp_Dialog;

    protected OnCamLifeCycleListener camLifeCycleListener = new OnCamLifeCycleListener() {
        @Override
        public void onCameraAdded(Camera camera) {
            LogUtil.e("OnCamLifeCycleListener", "onCameraAdded");
        }

        @Override
        public void onCameraDeleted(Camera camera) {
            LogUtil.e("OnCamLifeCycleListener", "onCameraDeleted");
        }

        @Override
        public void onCameraConnected(Camera camera) {
            LogUtil.e("OnCamLifeCycleListener", "onCameraConnected");
        }

        @Override
        public void onCameraDisConnected(Camera camera, final int cause) {
            LogUtil.e("OnCamLifeCycleListener", "onCameraDisConnected " + cause);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BaseDDPActivity.this);
                    builder.setCancelable(false);
                    if (cause == UCode.DISCONN_CAUSE_OTHERS_USE) {
                        LogUtil.e("OnCamLifeCycleListener", "DISCONN_CAUSE_OTHERS_USE");
                        builder.setMessage("盯盯拍已被其它终端连接!");
                    } else {
                        LogUtil.e("OnCamLifeCycleListener", "onCameraDisConnected others");
                        builder.setMessage("盯盯拍连接已经断开!");
                    }
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(BaseDDPActivity.this, SuiShouPaiMainActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            LogUtil.e("BaseDDPActivity",""+getRunningActivityName());
                            if (!getRunningActivityName().equals("com.qianfan.qianfanddpdemo.activity.ddpai.SuiShouPaiMainActivity")){
                                finish();
                            }
                        }
                    });
                    ddp_Dialog = builder.show();
                }
            });
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.e("BaseDDPActivity","onCreate");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.e("BaseDDPActivity","onNewIntent");
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.e("BaseDDPActivity","onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtil.e("BaseDDPActivity","onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.e("BaseDDPActivity","onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.e("BaseDDPActivity","onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.e("BaseDDPActivity","onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e("BaseDDPActivity","onDestroy");
        if (ddp_Dialog != null) {
            ddp_Dialog.dismiss();
        }
    }


}
