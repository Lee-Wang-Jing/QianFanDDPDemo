package com.qianfan.qianfanddpdemo.ddp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ddp.sdk.cam.resmgr.CameraResMgr;
import com.ddp.sdk.cambase.model.Camera;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.entity.BackListEntity;
import com.qianfan.qianfanddpdemo.utils.TimeUtils;
import com.qianfan.qianfanddpdemo.widgets.MyGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * 盯盯拍-回放列表
 * Created by Administrator on 2017/2/17.
 */

public class BackListActivityAdapter extends RecyclerView.Adapter<BackListActivityAdapter.ItemHolder> {
    private Context mContext;
    private List<BackListEntity> infos = new ArrayList<>();
    private CameraResMgr cameraResMgr;
    private Camera camera;

    public BackListActivityAdapter(Context mContext, CameraResMgr cameraResMgr, Camera camera) {
        this.mContext = mContext;
        if (cameraResMgr == null) {
            this.cameraResMgr = CameraResMgr.instance();
        } else {
            this.cameraResMgr = cameraResMgr;
        }
        this.camera = camera;
    }

    public void addData(List<BackListEntity> infos) {
        if (infos != null) {
            this.infos = infos;
            notifyDataSetChanged();
        }
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_backlistactivity, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.tv_time.setText("" + TimeUtils.millis2String(infos.get(position).getTime(), "yyyy年MM月dd日"));
        holder.gridView.setAdapter(holder.adapter);
        holder.adapter.addData(infos.get(position).getPlayFiles());
    }

    @Override
    public int getItemCount() {
        return infos == null ? 0 : infos.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        private TextView tv_time;
        private MyGridView gridView;
        private BackListAdapter adapter;

        public ItemHolder(View itemView) {
            super(itemView);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            gridView = (MyGridView) itemView.findViewById(R.id.gridView);
            adapter = new BackListAdapter(mContext, cameraResMgr, camera);
            gridView.setAdapter(adapter);
        }
    }
}
