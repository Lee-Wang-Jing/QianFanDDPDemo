package com.qianfan.qianfanddpdemo.utils;

import android.app.Activity;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Window;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

/**
 * Created by wangjing on 2017/5/8.
 */

public class MiuiStatusBarUtil {

    /**
     * -测试在小米手机上官方方法无效 需要使用小米的方法设置才行 部分华为手机上无效 比如华为mate8 6.0的系统上 新浪微博同样没有兼容
     */
    public static boolean setMiuiStatusBarDarkMode(Activity activity, boolean darkmode) {
//        if (Utils.isMIUI()) {
            Class<? extends Window> clazz = activity.getWindow().getClass();
            try {
                int darkModeFlag = 0;
                Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
//        }
        return false;
    }

}
