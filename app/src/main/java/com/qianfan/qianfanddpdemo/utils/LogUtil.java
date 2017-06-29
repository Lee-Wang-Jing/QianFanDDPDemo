package com.qianfan.qianfanddpdemo.utils;

import android.util.Log;

import com.qianfan.qianfanddpdemo.BuildConfig;


/**
 * 一句话功能简述
 * 功能详细描述
 *
 * @author wangjing on 2016/10/20 14:46
 * @e-mail wangjinggm@gmail.com
 * @see [相关类/方法](可选)
 */

public class LogUtil {
    private static final String TAG = "ZongHeng";

    /**
     * 以级别为 i 的形式输出LOG,一般提示性的消息information
     *
     * @param tag
     * @param msg
     */
    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.i("" + tag, "" + msg);
        }
    }

    /**
     * 以级别为 i 的形式输出LOG,一般提示性的消息information
     *
     * @param msg
     */
    public static void i(String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "" + msg);
        }
    }

    /**
     * 以级别为 d 的形式输出LOG,输出debug调试信息
     *
     * @param msg
     */
    public static void d(String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "" + msg);
        }
    }

    /**
     * 以级别为 d 的形式输出LOG,输出debug调试信息
     *
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d("" + tag, "" + msg);
        }
    }

    /**
     * 以级别为 e 的形式输出LOG ，红色的错误信息，查看错误源的关键
     *
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e("" + tag, "" + msg);
        }
    }

    /**
     * 以级别为 e 的形式输出LOG ，红色的错误信息，查看错误源的关键
     *
     * @param msg
     */
    public static void e(String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "" + msg);
        }
    }
}
