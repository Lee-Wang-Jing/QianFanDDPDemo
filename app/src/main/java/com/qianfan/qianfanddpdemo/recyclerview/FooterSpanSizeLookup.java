package com.qianfan.qianfanddpdemo.recyclerview;

import android.support.v7.widget.GridLayoutManager;

/**
 * Created by Stay on 6/3/16.
 * Powered by www.stay4it.com
 */
public class FooterSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
    private BaseAdapter adapter;
    private int spanCount;

    public FooterSpanSizeLookup(BaseAdapter adapter, int spanCount) {
        this.adapter = adapter;
        this.spanCount = spanCount;
    }

    @Override
    public int getSpanSize(int position) {
        if (adapter.isLoadMoreFooter(position) || adapter.isSectionHeader(position)) {
            return spanCount;
        }
        return 1;
    }
}
