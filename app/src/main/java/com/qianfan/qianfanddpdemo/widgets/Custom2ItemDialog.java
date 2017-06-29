package com.qianfan.qianfanddpdemo.widgets;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.qianfan.qianfanddpdemo.R;


/**
 * 性别变换弹窗
 *
 * @author xyx on 2017/1/4 13:56
 * @e-mail 384744573@qq.com
 * @see [相关类/方法](可选)
 */
public class Custom2ItemDialog extends Dialog implements View.OnClickListener {

    private TextView item1, item2, item3, item4, item5;
    private View divider_1, divider_2, divider_3, divider_4;

    /**
     * 设置默认style
     */
    public Custom2ItemDialog(Context context) {
        this(context, R.style.DialogTheme);
    }

    public Custom2ItemDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_custom_item);
        item1 = (TextView) findViewById(R.id.text_1);
        item2 = (TextView) findViewById(R.id.text_2);
        item3 = (TextView) findViewById(R.id.text_3);
        item4 = (TextView) findViewById(R.id.text_4);
        item5 = (TextView) findViewById(R.id.text_5);
        divider_1 = findViewById(R.id.divider_1);
        divider_2 = findViewById(R.id.divider_2);
        divider_3 = findViewById(R.id.divider_3);
        divider_4 = findViewById(R.id.divider_4);
        this.setCanceledOnTouchOutside(true);
    }

    public void setText1(String text1) {
        item1.setText(text1);
        item2.setVisibility(View.GONE);
        item3.setVisibility(View.GONE);
        item4.setVisibility(View.GONE);
        item5.setVisibility(View.GONE);
        divider_1.setVisibility(View.GONE);
        divider_2.setVisibility(View.GONE);
        divider_3.setVisibility(View.GONE);
        divider_4.setVisibility(View.GONE);
    }

    public void setText(String text1, String text2) {
        item1.setText(text1);
        item2.setText(text2);
        item3.setVisibility(View.GONE);
        item4.setVisibility(View.GONE);
        item5.setVisibility(View.GONE);
        divider_1.setVisibility(View.VISIBLE);
        divider_2.setVisibility(View.GONE);
        divider_3.setVisibility(View.GONE);
        divider_4.setVisibility(View.GONE);
    }

    public void setTextForThree(String first, String second, String third) {
        item1.setText(first);
        item2.setText(second);
        item3.setText(third);
        item3.setVisibility(View.VISIBLE);
        item4.setVisibility(View.GONE);
        item5.setVisibility(View.GONE);
        divider_1.setVisibility(View.VISIBLE);
        divider_2.setVisibility(View.VISIBLE);
        divider_3.setVisibility(View.GONE);
        divider_4.setVisibility(View.GONE);
    }

    public void setText(String text1, String text2, String text3, String text4) {
        item1.setText(text1);
        item2.setText(text2);
        item3.setText(text3);
        item4.setText(text4);

        item3.setVisibility(View.VISIBLE);
        item4.setVisibility(View.VISIBLE);
        item5.setVisibility(View.GONE);
        divider_1.setVisibility(View.VISIBLE);
        divider_2.setVisibility(View.VISIBLE);
        divider_3.setVisibility(View.VISIBLE);
        divider_4.setVisibility(View.GONE);
        item1.setOnClickListener(this);
        item2.setOnClickListener(this);
        item3.setOnClickListener(this);
        item4.setOnClickListener(this);
    }

    public void setText(String text1, String text2, String text3, String text4, String text5) {
        item1.setText(text1);
        item2.setText(text2);
        item3.setText(text3);
        item4.setText(text4);
        item5.setText(text5);
        item3.setVisibility(View.VISIBLE);
        item4.setVisibility(View.VISIBLE);
        item5.setVisibility(View.VISIBLE);
        divider_1.setVisibility(View.VISIBLE);
        divider_2.setVisibility(View.VISIBLE);
        divider_3.setVisibility(View.VISIBLE);
        divider_4.setVisibility(View.VISIBLE);
        item1.setOnClickListener(this);
        item2.setOnClickListener(this);
        item3.setOnClickListener(this);
        item4.setOnClickListener(this);
        item5.setOnClickListener(this);
    }

    /**
     * @return the item1
     */
    public TextView getItemFirst() {
        return this.item1;
    }

    /**
     * @return the item2
     */
    public TextView getItemSecond() {
        return this.item2;
    }

    /**
     * @return the item2
     */
    public TextView getItemThree() {
        return this.item3;
    }

    public void setOnItem1ClickListener(
            View.OnClickListener onItem1ClickListener) {
        item1.setOnClickListener(onItem1ClickListener);
    }

    public void setOnItem2ClickListener(
            View.OnClickListener onItem2ClickListener) {
        item2.setOnClickListener(onItem2ClickListener);
    }

    public void setOnItem3ClickListener(
            View.OnClickListener onItem2ClickListener) {
        item3.setOnClickListener(onItem2ClickListener);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    @Override
    public void onClick(View v) {
        if (listener == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.text_1:
                listener.OnClick(item1, 1);
                break;
            case R.id.text_2:
                listener.OnClick(item2, 2);
                break;
            case R.id.text_3:
                listener.OnClick(item3, 3);
                break;
            case R.id.text_4:
                listener.OnClick(item4, 4);
                break;
            case R.id.text_5:
                listener.OnClick(item5, 5);
                break;
        }
    }

    public interface OnItemClickListener {
        void OnClick(TextView view, int postion);
    }
}
