package com.qianfan.qianfanddpdemo.widgets;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * 作者：Created by WangJing on 2017/6/29.
 * 邮箱：wangjinggm@gmail.com
 * 描述：TODO
 * 最近修改：2017/6/29 13:25 by WangJing
 */

public class FileUtils {

    /**
     * 判断sd卡是否可用
     *
     * @return
     */
    public static boolean SDCardCanUse() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//判断sd卡是否存在
            return true;
        }
        return false;
    }

    /**
     * 根据文件路径获取文件路径 new file 操作，防止文件不存在报错
     *
     * @param filePath
     * @return
     */
    public static String getFileStringByPath(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        File file = new File(filePath);
        if (!file.exists()) {//文件夹不存在
            file.mkdirs();
        }
        return file.toString();
    }


    // 获取文件名
    public static String getFileNameFromPath(String filepath) {
        if ((filepath != null) && (filepath.length() > 0)) {
            int sep = filepath.lastIndexOf('/');
            if ((sep > -1) && (sep < filepath.length() - 1)) {
                return filepath.substring(sep + 1);
            }
        }
        return filepath;
    }
}
