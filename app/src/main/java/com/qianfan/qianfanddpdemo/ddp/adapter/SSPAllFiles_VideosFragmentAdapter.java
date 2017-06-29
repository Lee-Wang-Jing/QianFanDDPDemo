package com.qianfan.qianfanddpdemo.ddp.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.entity.DateListBaseFile;
import com.qianfan.qianfanddpdemo.myinterface.OnDDPSelectFilesLinstener;
import com.qianfan.qianfanddpdemo.recyclerview.BaseAdapter;
import com.qianfan.qianfanddpdemo.recyclerview.BaseViewHolder;
import com.qianfan.qianfanddpdemo.utils.TimeUtils;
import com.qianfan.qianfanddpdemo.widgets.MyGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangjing on 2017/2/8.
 */

public class SSPAllFiles_VideosFragmentAdapter extends BaseAdapter {
    private List<DateListBaseFile> infos = new ArrayList<>();
    private Context mContext;
    private OnDDPSelectFilesLinstener onDDPSelectFilesLinstener;

    private boolean isAll = false;//是否需要全选，默认false

    SSPDateListGridVideoAdapter gridAdapter;


    private boolean isneedEdit = false;//是否需要编辑，默认false

    public SSPAllFiles_VideosFragmentAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setAll(boolean all) {
        isAll = all;
    }

    public void setOnDDPSelectFilesLinstener(OnDDPSelectFilesLinstener onDDPSelectFilesLinstener) {
        this.onDDPSelectFilesLinstener = onDDPSelectFilesLinstener;
    }

    public void setData(List<DateListBaseFile> infos) {
        if (infos != null && !infos.isEmpty()) {
            this.infos.clear();
            this.infos.addAll(infos);
            notifyDataSetChanged();
        }
    }

    public void setIsneedEdit(boolean isneedEdit, boolean isSelectAll) {
        this.isneedEdit = isneedEdit;
        this.isAll = isSelectAll;
        notifyItemRangeChanged(0, infos.size());
//        notifyDataSetChanged();
    }

    @Override
    protected int getDataCount() {
        return infos != null ? infos.size() : 0;
    }

    @Override
    protected BaseViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sspallfiles_videosfragmentadapter, parent, false);
        return new ItemViewHolder(view);
    }


    class ItemViewHolder extends BaseViewHolder {
        private TextView tv_time;
        SSPDateListGridVideoAdapter adapter;
//        RecyclerView recyclerView;
        MyGridView myGridView;
        public ItemViewHolder(View itemView) {
            super(itemView);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            myGridView = (MyGridView) itemView.findViewById(R.id.gridView);
            adapter = new SSPDateListGridVideoAdapter(mContext);
            adapter.setOnDDPSelectFilesLinstener(onDDPSelectFilesLinstener);
            myGridView.setAdapter(adapter);
            gridAdapter = adapter;
        }

        @Override
        public void onBindViewHolder(int position) {
            tv_time.setText("" + TimeUtils.millis2String(infos.get(position).getDate(), "yyyy年MM月dd日"));
            myGridView.setAdapter(adapter);
            adapter.addDatas(infos.get(position).getDatas(), isneedEdit,isAll);
        }

        @Override
        public void onItemClick(View view, int position) {

        }
    }

    public SSPDateListGridVideoAdapter getGridAdapter() {
        return gridAdapter;
    }


    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int left = mItemOffset;
            int top = mItemOffset;
            if (parent.getChildLayoutPosition(view) % 4 == 0) {
                left = 0;
            }
            if (parent.getChildLayoutPosition(view) <= 4) {
                top = 0;
            }
            outRect.set(left, top, 0, 0);
        }
    }
}
