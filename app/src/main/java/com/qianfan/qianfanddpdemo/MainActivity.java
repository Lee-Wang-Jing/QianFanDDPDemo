package com.qianfan.qianfanddpdemo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.qianfan.qianfanddpdemo.ddp.activity.SuiShouPaiMainActivity;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int RC_WRITE_PERM = 100;//存储读写权限

    private Button btn_ddp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_ddp = (Button) findViewById(R.id.btn_ddp);
        btn_ddp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPermissions();

            }
        });
    }

    private void getPermissions() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        // 先判断是否有权限。
        if (AndPermission.hasPermission(this, perms)) {
            // 有权限，直接do anything.
            Intent intent=new Intent(MainActivity.this, SuiShouPaiMainActivity.class);
            startActivity(intent);
        } else {
            // 申请权限。
            AndPermission.with(this).requestCode(RC_WRITE_PERM).permission(perms).callback(listener).start();
        }
    }


    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。
            Intent intent=new Intent(MainActivity.this, SuiShouPaiMainActivity.class);
            startActivity(intent);
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。

            // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
            if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, deniedPermissions)) {
                // 第一种：用默认的提示语。
                AndPermission.defaultSettingDialog(MainActivity.this, RC_WRITE_PERM).show();
            }
        }
    };
}
