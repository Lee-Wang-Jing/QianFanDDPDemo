package com.qianfan.qianfanddpdemo.common;

import android.os.Environment;


import com.qianfan.qianfanddpdemo.MyApplication;

import java.io.File;

/**
 * 一句话功能简述
 * 功能详细描述
 *
 * @author WangJing on 2016/10/10 0010 14:45
 * @e-mail wangjinggm@gmail.com
 * @see [相关类/方法](可选)
 */

public class AppConfig {
    // sd卡路径
    public static final String SDCARD_PATH = getSDPath();
    // 客户端文件夹路径
    public static final String APP_FOLDER = SDCARD_PATH + File.separator + "ZongHeng" + File.separator;
    // 临时文件存放区--发布本地圈压缩的图片
    public static final String TEMP = APP_FOLDER + "temp" + File.separator;
    // 图片保存路径
    public static final String SAVE_PATH = APP_FOLDER + "images"+ File.separator;

    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取根目录
        } else {
            MyApplication.getmContext().getCacheDir().getAbsolutePath(); // 获取内置内存卡目录
        }
        return sdDir.toString();
    }

}
