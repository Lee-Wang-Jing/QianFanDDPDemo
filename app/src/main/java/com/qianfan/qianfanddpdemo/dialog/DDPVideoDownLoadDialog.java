package com.qianfan.qianfanddpdemo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qianfan.qianfanddpdemo.R;


/**
 * Created by Administrator on 2017/4/17.
 */

public class DDPVideoDownLoadDialog extends Dialog {

    private ProgressBar progressBar;
    private Button btn_cancel;
    private TextView tv_title;

    /**
     * @param context
     */
    public DDPVideoDownLoadDialog(Context context) {
        this(context, R.style.DialogTheme);
    }

    /**
     * @param context
     * @param theme
     */
    public DDPVideoDownLoadDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_ddpvideodownload);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
    }

    public void showInfo(int progress) {
        tv_title.setText(progress + "%");
        progressBar.setProgress(progress);
        this.show();
    }

    public void setProgress(int progress) {
        tv_title.setText(progress + "%");
        progressBar.setProgress(progress);
    }

    /**
     * @return the mCancelButton
     */
    public Button getCancelButton() {
        return btn_cancel;
    }
}
