package com.qianfan.qianfanddpdemo.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * BaseViewHolder
 *
 * @author WangJing on 2016/11/28 0028 14:05
 * @e-mail wangjinggm@gmail.com
 * @see [相关类/方法](可选)
 */

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(v, getAdapterPosition());
            }
        });
    }

    public abstract void onBindViewHolder(int position);
    public abstract void onItemClick(View view, int position);
}
