package com.qianfan.qianfanddpdemo;

import android.app.Application;
import android.content.Context;

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
    }

    public static Context getmContext() {
        return instance.getApplicationContext();
    }
}
