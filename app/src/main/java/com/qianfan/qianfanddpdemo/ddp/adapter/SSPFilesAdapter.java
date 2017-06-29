package com.qianfan.qianfanddpdemo.ddp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ddp.sdk.cam.resmgr.model.BaseFile;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.recyclerview.BaseAdapter;
import com.qianfan.qianfanddpdemo.recyclerview.BaseViewHolder;
import com.qianfan.qianfanddpdemo.utils.ImageLoader;
import com.qianfan.qianfanddpdemo.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangjing on 2017/1/11.
 */

public class SSPFilesAdapter extends BaseAdapter {
//    private final static int TYPE_IMAGE = 30;
//    private final static int TYPE_VIDEO = 31;

    private List<BaseFile> infos = new ArrayList<>();
    //    private BaseFile baseFile;
//    private long progress;

    @Override
    protected int getDataCount() {
        return infos != null ? infos.size() : 0;
    }

    public void addData(List<BaseFile> datas) {
        if (datas != null) {
            this.infos.addAll(datas);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        infos.clear();
    }

//    public void updateProgress(long progress) {
////        this.baseFile = baseFile;
//        this.progress = progress;
//        notifyItemChanged(0);
//    }

    @Override
    protected BaseViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sspfilesadapter, parent, false);
        return new ItemViewHolder(view);
    }

    class ItemViewHolder extends BaseViewHolder {
//        View view_bg;
        SimpleDraweeView sdv_image;
        LinearLayout ll_video;
        TextView tv_time;// tv_progress;

        public ItemViewHolder(View itemView) {
            super(itemView);
//            view_bg = itemView.findViewById(R.id.view_bg);
            sdv_image = (SimpleDraweeView) itemView.findViewById(R.id.sdv_image);
            ll_video = (LinearLayout) itemView.findViewById(R.id.ll_video);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
//            tv_progress = (TextView) itemView.findViewById(R.id.tv_progress);
        }

        @Override
        public void onBindViewHolder(int position) {
            BaseFile info = infos.get(position);
//            LogUtil.e("info", "fileName==>" + info.fileName);
//            LogUtil.e("info", "filePath==>" + info.filePath);
//            LogUtil.e("info", "fileThumb==>" + info.fileThumb);
//            LogUtil.e("info", "fileSize==>" + info.fileSize);
//            LogUtil.e("info", "fileState==>" + info.fileState);
//            LogUtil.e("info", "fileType==>" + info.fileType);
//            LogUtil.e("info", "cameraMAC==>" + info.cameraMAC);
//            LogUtil.e("info", "getDownloadName==>" + info.getDownloadName());
//            LogUtil.e("info", "time==>" + info.time);
//            LogUtil.e("info", "time==>" + TimeUtils.millis2String(info.time, "yyyy-MM-dd"));
//            LogUtil.e("info", "isVideo==>" + info.isVideo());
//            LogUtil.e("info", "duration==>" + info.duration);
//            if (position == 0) {
//                view_bg.setVisibility(View.VISIBLE);
//            } else {
//                view_bg.setVisibility(View.GONE);
//            }
            ImageLoader.loadResize(sdv_image, "file://" + info.fileThumb);
            if (info.isVideo()) {
                ll_video.setVisibility(View.VISIBLE);
                tv_time.setText("" + TimeUtils.formatTime1(info.duration));
            } else {
                ll_video.setVisibility(View.GONE);
            }
//            if (position == 0) {
//                tv_progress.setVisibility(View.GONE);
//                tv_progress.setText("" + progress);
//            } else {
//                tv_progress.setVisibility(View.GONE);
//            }
        }

        @Override
        public void onItemClick(View view, int position) {

        }
    }
}
