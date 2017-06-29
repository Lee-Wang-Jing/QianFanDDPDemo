package com.qianfan.qianfanddpdemo.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Toast Utils
 *
 * @author wangjing on 2016/10/21 15:34
 * @e-mail wangjinggm@gmail.com
 */

public class ToastUtil {

    /**
     * Toast short
     *
     * @param context
     * @param msg
     */
    public static void TShort(Context context, String msg) {
        if (context != null && !TextUtils.isEmpty(msg)) {
            Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Toast short
     *
     * @param context
     * @param info
     */
    public static void TShort(Context context, int info) {
        Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
    }

    /**
     * Toast long
     *
     * @param context
     * @param msg
     */
    public static void TLong(Context context, int msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Toast long
     *
     * @param context
     * @param msg
     */
    public static void TLong(Context context, String msg) {
        Toast.makeText(context, "" + msg, Toast.LENGTH_LONG).show();
    }

    public static void TDuration(Context context, String msg, int duration) {
        if (duration <= 0) {
            return;
        }
        Toast.makeText(context, "" + msg, duration).show();
    }

//    /**
//     * 弹出有图片的文字
//     *
//     * @param context
//     * @param imageId
//     * @param content
//     */
//    public static void TShortImage(Context context, int imageId, String content) {
//        //new一个toast传入要显示的activity的上下文
//        Toast toast = new Toast(context);
//        //显示的时间
//        toast.setDuration(Toast.LENGTH_SHORT);
//        //显示的位置
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        //重新给toast进行布局
//        LinearLayout toastLayout = new LinearLayout(context);
//        toastLayout.setOrientation(LinearLayout.VERTICAL);
//        toastLayout.setGravity(Gravity.CENTER);
//
//        ImageView imageView = new ImageView(context);
//        imageView.setImageResource(imageId);
//        //把imageView添加到toastLayout的布局当中
//        toastLayout.addView(imageView);
//
//        TextView textView = new TextView(context);
//        textView.setText(content);
//        textView.setTextColor(context.getResources().getColor(R.color.color_93afc5));
//        textView.setTextSize(13);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        lp.setMargins(0, DensityUtils.dip2px(context, 7), 0, 0);
//        textView.setLayoutParams(lp);
//        //把textView添加到toastLayout的布局当中
//        toastLayout.addView(textView);
//
//        toastLayout.setBackgroundColor(Color.TRANSPARENT);
//        //把toastLayout添加到toast的布局当中
//        toast.setView(toastLayout);
//        toast.show();
//    }
}
