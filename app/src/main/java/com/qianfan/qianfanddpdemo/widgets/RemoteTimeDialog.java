package com.qianfan.qianfanddpdemo.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.widget.TextView;

import com.qianfan.qianfanddpdemo.R;


/**
 * 遥控器配对时间dialog
 *
 * @author xyx on 2017/6/5
 * @e-mail 384744573@qq.com
 * @see [相关类/方法](可选)
 */

public class RemoteTimeDialog extends Dialog {

    private TextView tv_remote_time;
    private Handler handler;
    private int matchTime = 10;

    public RemoteTimeDialog(@NonNull Context context) {
        this(context, R.style.DialogTheme);
    }

    public RemoteTimeDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_remote_time);
        setCanceledOnTouchOutside(false);
        tv_remote_time = (TextView) findViewById(R.id.tv_remote_time);
        handler = new Handler();
    }

    public void startRemoteTime() {
        matchTime = 10;
        handler.post(runnable);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tv_remote_time.setText("配对开始 ：" + matchTime + "秒");
            if (matchTime > 0) {
                matchTime--;
                handler.postDelayed(runnable, 1000);
            }
        }
    };

    @Override
    public void dismiss() {
        super.dismiss();
        matchTime = 0;
        handler.removeCallbacks(runnable);
    }
}
