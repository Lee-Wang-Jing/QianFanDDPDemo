package com.qianfan.qianfanddpdemo.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.MediaController;

/**
 * Created by Administrator on 2017/3/16.
 */

public class MyMediaController extends MediaController {

    public interface onWindowVisibilityChangedListener {
        void onWindowChangeListener(int visibility);
    }

    private onWindowVisibilityChangedListener onWindowVisibilityChangedListener;

    public void setOnWindowVisibilityChangedListener(MyMediaController.onWindowVisibilityChangedListener onWindowVisibilityChangedListener) {
        this.onWindowVisibilityChangedListener = onWindowVisibilityChangedListener;
    }

    public MyMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyMediaController(Context context, boolean useFastForward) {
        super(context, useFastForward);
    }

    public MyMediaController(Context context) {
        super(context);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (onWindowVisibilityChangedListener!=null){
            onWindowVisibilityChangedListener.onWindowChangeListener(visibility);
        }
    }
}
