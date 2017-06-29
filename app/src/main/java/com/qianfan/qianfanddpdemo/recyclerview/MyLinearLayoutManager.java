package com.qianfan.qianfanddpdemo.recyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.qianfan.qianfanddpdemo.utils.LogUtil;

/**
 * MyLinearLayoutManager
 *
 * @author WangJing on 2016/11/28 0028 15:04
 * @e-mail wangjinggm@gmail.com
 * @see [相关类/方法](可选)
 */

public class MyLinearLayoutManager extends LinearLayoutManager implements ILayoutManager {
    public MyLinearLayoutManager(Context context) {
        super(context);
    }

    public MyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public MyLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return this;
    }

    @Override
    public int findLastVisiblePosition() {
        return findLastVisibleItemPosition();
    }

    @Override
    public void setUpAdapter(BaseAdapter adapter) {

    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
//        super.smoothScrollToPosition(recyclerView, state, position);
        RecyclerView.SmoothScroller smoothScroller = new TopSmoothScroller(recyclerView.getContext(),recyclerView.getLayoutManager().getHeight());
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);

    }

    private static class TopSmoothScroller extends LinearSmoothScroller {

        private int height;
        TopSmoothScroller(Context context,int height) {
            super(context);
            this.height = height;
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return (boxStart + (boxEnd - boxStart) / 2) - viewStart -height/2 ;
        }
    }
}
