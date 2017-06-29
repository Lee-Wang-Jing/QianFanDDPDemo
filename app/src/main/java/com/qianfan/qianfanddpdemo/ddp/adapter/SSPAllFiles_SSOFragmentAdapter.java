package com.qianfan.qianfanddpdemo.ddp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.entity.DateListBaseFile;
import com.qianfan.qianfanddpdemo.myinterface.OnDDPSelectFilesLinstener;
import com.qianfan.qianfanddpdemo.recyclerview.BaseAdapter;
import com.qianfan.qianfanddpdemo.recyclerview.BaseViewHolder;
import com.qianfan.qianfanddpdemo.utils.LogUtil;
import com.qianfan.qianfanddpdemo.utils.TimeUtils;
import com.qianfan.qianfanddpdemo.widgets.MyGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangjing on 2017/2/8.
 */

public class SSPAllFiles_SSOFragmentAdapter extends BaseAdapter {
    private List<DateListBaseFile> infos = new ArrayList<>();
    private Context mContext;
    private boolean isneedEdit = false;//是否需要编辑，默认false
    private OnDDPSelectFilesLinstener onDDPSelectFilesLinstener;

    private SSPDateListGridSSOAdapter gridAdapter;

    private boolean isAll = false;//是否需要全选，默认false

    public SSPDateListGridSSOAdapter getGridAdapter() {
        return gridAdapter;
    }


    public SSPAllFiles_SSOFragmentAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setOnDDPSelectFilesLinstener(OnDDPSelectFilesLinstener onDDPSelectFilesLinstener) {
        this.onDDPSelectFilesLinstener = onDDPSelectFilesLinstener;
    }

    public void setAll(boolean all) {
        isAll = all;
    }


    public void setData(List<DateListBaseFile> infos) {
        if (infos != null && !infos.isEmpty()) {
            this.infos.clear();
            this.infos.addAll(infos);
            notifyItemRangeChanged(0, infos.size());
        }
    }

    public void clear() {
        if (infos != null) {
            infos.clear();
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sspallfiles_ssofragmentadapter, parent, false);
        return new ItemViewHolder(view);
    }


    class ItemViewHolder extends BaseViewHolder {
        private TextView tv_time;
        MyGridView gridView;
        SSPDateListGridSSOAdapter adapter;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            gridView = (MyGridView) itemView.findViewById(R.id.gridView);
            adapter = new SSPDateListGridSSOAdapter(mContext);
            adapter.setOnDDPSelectFilesLinstener(onDDPSelectFilesLinstener);
            gridView.setAdapter(adapter);
        }

        @Override
        public void onBindViewHolder(int position) {
            tv_time.setText("" + TimeUtils.millis2String(infos.get(position).getDate(), "yyyy年MM月dd日"));
            LogUtil.e("onBindViewHolder", "" + TimeUtils.millis2String(infos.get(position).getDate(), "yyyy年MM月dd日"));
            gridView.setAdapter(adapter);
            adapter.addDatas(infos.get(position).getDatas(), isneedEdit, isAll);
        }

        @Override
        public void onItemClick(View view, int position) {

        }
    }
}
