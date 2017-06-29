package com.qianfan.qianfanddpdemo.widgets;

import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qianfan.qianfanddpdemo.R;


/**
 * @author linghong@xizi.com
 * @date 2013-6-6 上午11:32:09
 * @description: 自定义通用dialog样式
 * 调用过程如下：
 * 1.Custom2btnDialog dialog = new Custom2btnDialog(mContext,R.style.CustomDialogTheme);指定dialog样式 *
 * 2.dialog.showInfo(); 显示不同内容
 */
public class Custom2btnDialog extends Dialog {

    private TextView mContentTextView;
    private Button mOkButton;
    private Button mCancelButton;

    /**
     * @param context
     */
    public Custom2btnDialog(Context context) {
        this(context, R.style.DialogTheme);
        init();
    }

    /**
     * @param context
     * @param theme
     */
    public Custom2btnDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_custom);
        mContentTextView = (TextView) findViewById(R.id.content);
        mOkButton = (Button) findViewById(R.id.ok);
        mCancelButton = (Button) findViewById(R.id.cancel);
    }

    /**
     * @param content
     * @param ok
     * @param cancel
     * @return void
     * @description: 有title的dialog
     */
    public void showInfo(String content, String ok, String cancel) {
        mContentTextView.setText(content);
        mContentTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        mOkButton.setText(ok);
        mCancelButton.setText(cancel);
        mOkButton.setVisibility(View.VISIBLE);
        mCancelButton.setVisibility(View.VISIBLE);
        this.show();
    }


    /**
     * 只有一个按钮的dialog
     *
     * @param content
     * @param ok
     */
    public void showOneBtn(String content, String ok) {
        mContentTextView.setText(content);
        mContentTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        mOkButton.setText(ok);
        mOkButton.setVisibility(View.VISIBLE);
        mCancelButton.setVisibility(View.GONE);
        this.show();
    }

    /**
     * @return the mOkButton
     */
    public Button getOkButton() {
        return mOkButton;
    }

    /**
     * @return the mCancelButton
     */
    public Button getCancelButton() {
        return mCancelButton;
    }

}
