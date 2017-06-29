package com.qianfan.qianfanddpdemo.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import org.videolan.libvlc.Util;

/**
 * 单位密度换算方法
 *
 * @author on 2016/10/8 0008 14:19
 * @e-mail wangjinggm@gmail.com
 * @see [相关类/方法](可选)
 */

public class DensityUtils {

    /**
     * 获取屏幕高度(px)
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px
     */
    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
     */
    public static int px2sp(Context context, float pxValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


    /**
     * 获取当前显示的分辨率的情况
     *
     * @param content
     * @return
     */
    @SuppressLint("NewApi")
    public static DisplayMetrics getDisplaySize(Context content) {
        DisplayMetrics dm = new DisplayMetrics();
        if (Util.isJelly_Bean_Mr1OrLater()) {
            WindowManager wm = (WindowManager) content.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getRealSize(size);
            dm.widthPixels = size.x;
            dm.heightPixels = size.y;
        } else {
            dm = content.getResources().getDisplayMetrics();
        }
        return dm;
    }
}
