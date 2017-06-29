package com.qianfan.qianfanddpdemo.recyclerview;

import android.support.v7.widget.RecyclerView;


/**
 * ILayoutManager
 *
 * @author WangJing on 2016/11/28 0028 14:23
 * @e-mail wangjinggm@gmail.com
 * @see [相关类/方法](可选)
 */

public interface ILayoutManager {
    RecyclerView.LayoutManager getLayoutManager();
    int findLastVisiblePosition();
    void setUpAdapter(BaseAdapter adapter);
}
