package com.qianfan.qianfanddpdemo.base;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.recyclerview.ILayoutManager;
import com.qianfan.qianfanddpdemo.recyclerview.MyLinearLayoutManager;
import com.qianfan.qianfanddpdemo.utils.MiuiStatusBarUtil;
import com.qianfan.qianfanddpdemo.widgets.LoadingView;

import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * Activity基类
 *
 * @author WangJing on 2016/10/8 0008 11:30
 * @e-mail wangjinggm@gmail.com
 * @see [相关类/方法](可选)
 */

public class BaseActivity extends SupportActivity {

    // 供子类使用的加载对话框
    protected LoadingView mLoadingView;
    private boolean isActivityResume;
    private InputMethodManager mIMM;


    /**
     * 重写setContentView，让子类传入的View上方再覆盖一层LoadingView
     */
    @Override
    public void setContentView(int layoutResID) {
        FrameLayout frameLayout = new FrameLayout(this);
//        frameLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.color_eeeeee));
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        frameLayout.addView(view);
        mLoadingView = new LoadingView(this);
        frameLayout.addView(mLoadingView);
        frameLayout.setFitsSystemWindows(true);
        super.setContentView(frameLayout);
    }


    public void setContentViewWhite(int layoutResID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            MiuiStatusBarUtil.setMiuiStatusBarDarkMode(this, true);
        }
        FrameLayout frameLayout = new FrameLayout(this);
//        frameLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.color_eeeeee));
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        frameLayout.addView(view);
        mLoadingView = new LoadingView(this);
        frameLayout.addView(mLoadingView);
        frameLayout.setFitsSystemWindows(true);
        super.setContentView(frameLayout);
    }


    /**
     * 设置带有title和返回按钮的Toolbar
     *
     * @param toolbar
     * @param title
     */
    protected void setBaseBackToolbar(Toolbar toolbar, String title) {
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.color_f2f2f2));
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
                    onBackPressedSupport();
                }
            });
        }
    }

    protected String getRunningActivityName() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        return runningActivity;
    }

    /**
     * 设置带有title和返回按钮的Toolbar----绿色
     *
     * @param toolbar
     * @param title
     */
    protected void setBaseBackToolbarGreen(Toolbar toolbar, String title) {
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
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
                    onBackPressedSupport();
                }
            });
        }
    }

    /**
     * 设置toobar ，有返回键
     *
     * @param toolbar    控件
     * @param title      标题
     * @param resId_menu menu的名字
     * @param listener   点击事件
     */
    protected void setToolbarWhiteWithBack(Toolbar toolbar, String title, int resId_menu, Toolbar.OnMenuItemClickListener listener) {
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.color_f2f2f2));
        toolbar.setTitle(title);
        toolbar.setNavigationIcon(R.mipmap.icon_arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressedSupport();
            }
        });
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.color_222222));
        if (resId_menu != 0) {
            toolbar.inflateMenu(resId_menu);
            toolbar.setOnMenuItemClickListener(listener);
        }
    }

    protected ILayoutManager getLayoutManager() {
        return new MyLinearLayoutManager(this);
    }


    /**
     * 隐藏软键盘
     */
    protected void hideSoftInput() {
        if (mIMM == null) {
            mIMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            mIMM.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    @Override
    protected FragmentAnimator onCreateFragmentAnimator() {
        return super.onCreateFragmentAnimator();
    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }
}
