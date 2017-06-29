package com.qianfan.qianfanddpdemo.widgets;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qianfan.qianfanddpdemo.R;


/**
 * Created by wangjing on 2017/3/17.
 */

public class LoadingView extends FrameLayout {

    private Context mcontext;
    //加载动画progressbar
    private LinearLayout ll_loadingview_prograss;
    private ImageView loadingview_progressbar;
    private TextView loadingview_progressbar_text;
    //加载失败
    private LinearLayout ll_loadingview_failed;
    //数据为空
    private LinearLayout ll_loadingview_empty;
    private TextView text_loadingview_empty,tv_failed_content;
    private ImageView imv_empty;
    private View title;

    private boolean isShowLoadingView = false;
    private boolean isShowEmpty = false;
    private AnimationDrawable frameAnim;

    public LoadingView(@NonNull Context context) {
        super(context);
        this.mcontext = context;
        init();
        //初始化默认隐藏
        this.setVisibility(View.GONE);
    }

    /**
     * 初始化
     */
    private void init() {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.activity_loading_view, this);
        ll_loadingview_prograss = (LinearLayout) view.findViewById(R.id.ll_loadingview_prograss);
        loadingview_progressbar = (ImageView) view.findViewById(R.id.loadingview_progressbar);
        loadingview_progressbar_text = (TextView) view.findViewById(R.id.loadingview_progressbar_text);
        ll_loadingview_failed = (LinearLayout) view.findViewById(R.id.ll_loadingview_failed);
        ll_loadingview_empty = (LinearLayout) view.findViewById(R.id.ll_loadingview_empty);
        text_loadingview_empty = (TextView) view.findViewById(R.id.text_loadingview_empty);
        tv_failed_content = (TextView) view.findViewById(R.id.tv_failed_content);
        title =  view.findViewById(R.id.title);
    }

    /**
     * 正在加载..
     */
    public void showLoading() {
        loadingview_progressbar.setBackgroundResource(R.drawable.selector_loading);
        frameAnim = (AnimationDrawable) loadingview_progressbar.getBackground();
        if (frameAnim != null && !frameAnim.isRunning()) {
            frameAnim.start();
        }
        ll_loadingview_prograss.setVisibility(View.VISIBLE);
        ll_loadingview_failed.setVisibility(View.GONE);
        ll_loadingview_empty.setVisibility(View.GONE);
        this.setVisibility(View.VISIBLE);
        this.isShowLoadingView = true;
    }

    /**
     * 正在加载..
     */
    public void showLoading(boolean isShowActionBar) {
        if (!isShowActionBar) {
            title.setVisibility(GONE);
        }else{
            title.setVisibility(VISIBLE);
        }
        showLoading();
    }

    /**
     * Loading message
     *
     * @param message
     */
    public void showLoading(String message) {
        loadingview_progressbar.setBackgroundResource(R.drawable.selector_loading);
        frameAnim = (AnimationDrawable) loadingview_progressbar.getBackground();
        if (frameAnim != null && !frameAnim.isRunning()) {
            frameAnim.start();
        }
        ll_loadingview_prograss.setVisibility(View.VISIBLE);
        loadingview_progressbar_text.setText("" + message);
        ll_loadingview_failed.setVisibility(View.GONE);
        ll_loadingview_empty.setVisibility(View.GONE);
        this.setVisibility(View.VISIBLE);
        this.isShowLoadingView = true;
    }

    /**
     * 获取数据为空
     */
    public void showEmpty() {
        if (frameAnim != null && !frameAnim.isRunning()) {
            frameAnim.stop();
        }
        text_loadingview_empty.setText(mcontext.getString(R.string.loading_empty));
        ll_loadingview_prograss.setVisibility(View.GONE);
        ll_loadingview_failed.setVisibility(View.GONE);
        ll_loadingview_empty.setVisibility(View.VISIBLE);
        this.setVisibility(View.VISIBLE);
        this.isShowEmpty = true;
    }

    /**
     * 获取数据为空
     */
    public void showEmpty(boolean isShowActionBar) {
        if (!isShowActionBar) {
            title.setVisibility(GONE);
        }else{
            title.setVisibility(VISIBLE);
        }
        showEmpty();
    }

    /**
     * 获取数据为空
     */
    public void showEmpty(String emptyContent) {
        if (TextUtils.isEmpty(emptyContent)) {
            emptyContent = mcontext.getString(R.string.loading_empty);
        }
        if (frameAnim != null && !frameAnim.isRunning()) {
            frameAnim.stop();
        }
        text_loadingview_empty.setText(emptyContent + "");
        ll_loadingview_prograss.setVisibility(View.GONE);
        ll_loadingview_failed.setVisibility(View.GONE);
        ll_loadingview_empty.setVisibility(View.VISIBLE);
        this.setVisibility(View.VISIBLE);
        this.isShowEmpty = true;
    }

    /**
     * 获取数据为空
     */
    public void showEmpty(String emptyContent,boolean isShowActionBar) {
        if (!isShowActionBar) {
            title.setVisibility(GONE);
        }else{
            title.setVisibility(VISIBLE);
        }
        showEmpty(emptyContent);
    }

    /**
     * 获取数据失败
     */
    public void showFailed() {
        if (frameAnim != null && !frameAnim.isRunning()) {
            frameAnim.stop();
        }
        ll_loadingview_prograss.setVisibility(View.GONE);
        ll_loadingview_failed.setVisibility(View.VISIBLE);
        ll_loadingview_empty.setVisibility(View.GONE);
        this.setVisibility(View.VISIBLE);
    }

    /**
     * 获取数据失败
     */
    public void showFailed(boolean isShowActionBar) {
        if (!isShowActionBar) {
            title.setVisibility(GONE);
        }
        showFailed();
    }

    /**
     * 获取数据失败
     */
    public void showFailed(boolean isShowActionBar,String message) {
        if (!isShowActionBar) {
            title.setVisibility(GONE);
        }
        showFailed();
        tv_failed_content.setText(message);
    }

    /**
     * 加载失败点击事件
     *
     * @param listener
     */
    public void setOnFailedClickListener(OnClickListener listener) {
        ll_loadingview_failed.setOnClickListener(listener);
    }

    /**
     * 数据为空点击事件
     *
     * @param listener
     */
    public void setOnEmptyClickListener(OnClickListener listener) {
        ll_loadingview_empty.setOnClickListener(listener);
    }

    /**
     * 隐藏
     */
    public void dismissLoadingView() {
        if (frameAnim != null && frameAnim.isRunning()) {
            frameAnim.stop();
        }
        this.setVisibility(View.GONE);
        this.isShowLoadingView = false;
        this.isShowEmpty = false;
    }

    public boolean isShowLoadingView() {
        return isShowLoadingView;
    }

    public boolean isShowEmpty() {
        return isShowEmpty;
    }
}
