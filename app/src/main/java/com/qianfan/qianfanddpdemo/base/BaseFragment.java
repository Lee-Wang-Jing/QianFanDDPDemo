package com.qianfan.qianfanddpdemo.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.recyclerview.ILayoutManager;
import com.qianfan.qianfanddpdemo.recyclerview.MyLinearLayoutManager;
import com.qianfan.qianfanddpdemo.widgets.LoadingView;

import me.yokeyword.fragmentation.SupportFragment;

/**
 * Fragment基类
 *
 * @author WangJing on 2016/10/9 0009 10:26
 * @e-mail wangjinggm@gmail.com
 * @see [相关类/方法](可选)
 */

public abstract class BaseFragment extends SupportFragment {

    //供子类使用的加载对话框
    public LoadingView mLoadingView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(container.getContext());
        View viewRoot = inflater.inflate(getLayoutID(),
                frameLayout, false);
        frameLayout.addView(viewRoot);
        mLoadingView = new LoadingView(container.getContext());
        frameLayout.addView(mLoadingView);
        init(frameLayout);
        return frameLayout;
    }

    public abstract int getLayoutID();

    protected abstract void init(View view);

    protected void setBaseBackToolbar(Toolbar toolbar, String title) {
        toolbar.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.color_f2f2f2));
        toolbar.setContentInsetsAbsolute(0, 0);
        TextView tv_title = (TextView) toolbar.findViewById(R.id.tv_toolbar_title);
        if (tv_title != null) {
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(title);
        }
        ImageButton leftButton = (ImageButton) toolbar.findViewById(R.id.ib_toolbar_left);
        if (leftButton != null) {
            leftButton.setVisibility(View.VISIBLE);
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideSoftInput();
                    _mActivity.onBackPressedSupport();
                }
            });
        }
    }

    /**
     * 设置toobar ，没有返回键
     *
     * @param toolbar    控件
     * @param title      标题
     * @param resId_menu menu的名字
     * @param listener   点击事件
     */
    protected void setToolbarWithoutBack(Toolbar toolbar, String title, int resId_menu, Toolbar.OnMenuItemClickListener listener) {
        toolbar.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.colorPrimary));
        if (!TextUtils.isEmpty(title)) {
            toolbar.setTitle(title);
            toolbar.setTitleTextColor(ContextCompat.getColor(_mActivity, R.color.white));
        }
        if (resId_menu != 0) {
            toolbar.inflateMenu(resId_menu);
            toolbar.setOnMenuItemClickListener(listener);
        }
    }

    /**
     * 设置带有title和返回按钮的Toolbar----绿色
     *
     * @param toolbar
     * @param title
     */
    protected void setBaseBackToolbarGreen(Toolbar toolbar, String title) {
        toolbar.setBackgroundColor(ContextCompat.getColor(_mActivity, R.color.colorPrimary));
        toolbar.setContentInsetsAbsolute(0, 0);
        TextView tv_title = (TextView) toolbar.findViewById(R.id.tv_toolbar_title);
        if (tv_title != null) {
            tv_title.setTextColor(Color.WHITE);
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(title);
        }
        ImageButton leftButton = (ImageButton) toolbar.findViewById(R.id.ib_toolbar_left);
        if (leftButton != null) {
            leftButton.setVisibility(View.VISIBLE);
            leftButton.setImageResource(R.mipmap.icon_arrow_left_white);
            leftButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _mActivity.onBackPressedSupport();
                }
            });
        }
    }

    protected ILayoutManager getLayoutManager() {
        return new MyLinearLayoutManager(_mActivity);
    }


}
