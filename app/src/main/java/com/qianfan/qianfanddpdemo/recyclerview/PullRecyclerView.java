package com.qianfan.qianfanddpdemo.recyclerview;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.entity.FooterEntity;


/**
 * 封装的下拉刷新+加载更多RecyclerView
 *
 * @author WangJing on 2016/11/28 0028 13:56
 * @e-mail wangjinggm@gmail.com
 * @see [相关类/方法](可选)
 */

public class PullRecyclerView extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener {

    public static final int ACTION_PULL_TO_REFRESH = 1;
    public static final int ACTION_LOAD_MORE_REFRESH = 2;
    public static final int ACTION_IDLE = 0;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private OnRecyclerRefreshListener listener;

    private int mCurrentState = ACTION_IDLE;
    private boolean isLoadMoreEnabled = false;
    private boolean isPullToRefreshEnabled = true;

    private ILayoutManager mLayoutManager;
    private BaseAdapter adapter;


    public PullRecyclerView(Context context) {
        super(context);
        setUpView();
    }

    public PullRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpView();
    }

    public PullRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUpView();
    }

    public OnPreDispatchTouchListener getOnPreDispatchTouchListener() {
        return onPreDispatchTouchListener;
    }

    public void setOnPreDispatchTouchListener(OnPreDispatchTouchListener onPreDispatchTouchListener) {
        this.onPreDispatchTouchListener = onPreDispatchTouchListener;
    }

    private void setUpView() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_pull_to_refresh, this, true);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && (mLayoutManager.findLastVisiblePosition() + 1) == mLayoutManager.getLayoutManager().getItemCount()
                        && mCurrentState == ACTION_IDLE
                        && isLoadMoreEnabled) {
                    mCurrentState = ACTION_LOAD_MORE_REFRESH;
                    mSwipeRefreshLayout.setEnabled(false);
                    listener.onRefresh(ACTION_LOAD_MORE_REFRESH);
                    adapter.notifyFooterState(new FooterEntity(1));
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    /**
     * 检查是否可以加载更多
     *
     * @return
     */
    private boolean checkIfNeedLoadMore() {
        int lastVisibleItemPosition = mLayoutManager.findLastVisiblePosition();
        int totalCount = mLayoutManager.getLayoutManager().getItemCount();
        return totalCount - lastVisibleItemPosition < 5;
    }

    public void enableLoadMore(boolean enable) {
        isLoadMoreEnabled = enable;
    }

    public void enablePullToRefresh(boolean enable) {
        isPullToRefreshEnabled = enable;
        mSwipeRefreshLayout.setEnabled(enable);
    }


    public void setLayoutManager(ILayoutManager manager) {
        this.mLayoutManager = manager;
        mRecyclerView.setLayoutManager(manager.getLayoutManager());
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decoration) {
        if (decoration != null) {
            mRecyclerView.addItemDecoration(decoration);
        }
    }

    public void removeItemDecoration(RecyclerView.ItemDecoration decoration) {
        if (decoration != null) {
            mRecyclerView.removeItemDecoration(decoration);
        }
    }

    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
        mRecyclerView.setAdapter(adapter);
        mLayoutManager.setUpAdapter(adapter);
        adapter.setOnClickItemListener(onClickItemListener);
    }

    private BaseAdapter.OnClickItemListener onClickItemListener = new BaseAdapter.OnClickItemListener() {
        @Override
        public void OnClick(int index) {
            if (index==0){
                //点击查看更多
                listener.onRefresh(ACTION_LOAD_MORE_REFRESH);
                adapter.notifyFooterState(new FooterEntity(1));
            }else{
                //加载失败，点击重新加载
                listener.onRefresh(ACTION_PULL_TO_REFRESH);
            }
        }
    };

    public void setRefreshing() {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        });
    }

    public void setOnRefreshListener(OnRecyclerRefreshListener listener) {
        this.listener = listener;
    }


    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        mCurrentState = ACTION_PULL_TO_REFRESH;
        listener.onRefresh(ACTION_PULL_TO_REFRESH);
    }


    public void onRefreshCompleted() {
        switch (mCurrentState) {
            case ACTION_PULL_TO_REFRESH:
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                break;
            case ACTION_LOAD_MORE_REFRESH:
                adapter.onLoadMoreStateChanged(false);
                if (isPullToRefreshEnabled) {
                    if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setEnabled(true);
                    }
                }
                break;
        }
        mCurrentState = ACTION_IDLE;
    }

    public void setSelection(int position) {
        mRecyclerView.scrollToPosition(position);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (onPreDispatchTouchListener!=null){
            onPreDispatchTouchListener.onTouch(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public interface OnRecyclerRefreshListener {
        /**
         * 下拉刷新
         *
         * @param action 1下拉刷新 2 加载更多
         */
        void onRefresh(int action);
    }

    //------------------------------------------interface-----------------------------------------------
    private OnPreDispatchTouchListener onPreDispatchTouchListener;
    public interface OnPreDispatchTouchListener{
        boolean onTouch(MotionEvent ev);
    }

    public RecyclerView getRecyclerView(){
        return mRecyclerView;
    }

    /**
     * 个人中心Style
     */
    public void setMyPersonHomeStyle(int paddingTop){
        mRecyclerView.setPadding(0, paddingTop,0,0);
        mRecyclerView.setClipToPadding(false);
    }
}
