package com.qianfan.qianfanddpdemo;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

/**
 * 作者：Created by WangJing on 2017/6/29.
 * 邮箱：wangjinggm@gmail.com
 * 描述：TODO
 * 最近修改：2017/6/29 09:08 by WangJing
 */

public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initFresco();
    }

    private void initFresco() {
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .setResizeAndRotateEnabledForNetwork(true)
                .setBitmapsConfig(Bitmap.Config.RGB_565)
                .build();
        //初始化Fresco
        Fresco.initialize(this, config);
    }

    public static Context getmContext() {
        return instance.getApplicationContext();
    }
}
