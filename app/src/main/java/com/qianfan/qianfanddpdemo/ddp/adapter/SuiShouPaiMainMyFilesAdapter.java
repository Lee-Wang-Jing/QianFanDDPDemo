package com.qianfan.qianfanddpdemo.ddp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ddp.sdk.cam.resmgr.model.EventImage;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qianfan.qianfanddpdemo.MyApplication;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.ddp.activity.SSPFilesActivity;
import com.qianfan.qianfanddpdemo.utils.ImageLoader;
import com.qianfan.qianfanddpdemo.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wangjing on 2017/1/13.
 */

public class SuiShouPaiMainMyFilesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int TYPE_MORE = 88;
    private final static int TYPE_NORMAL = 89;
    private Context context;
    private List<EventImage> infos = new ArrayList<>();

    public SuiShouPaiMainMyFilesAdapter(Context context) {
        this.context = context;
    }

    public void addData(List<EventImage> infos) {
        if (infos != null) {
//            Collections.reverse(infos);
            if (infos.size() > 14) {
                this.infos.addAll(infos.subList(0, 14));
            }else{
                this.infos.addAll(infos);
            }
            notifyDataSetChanged();
        }
    }

    public void clear() {
        if (infos != null) {
            this.infos.clear();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == infos.size()) {
            return TYPE_MORE;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        return infos != null ? infos.size() + 1 : 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NORMAL) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suishoupaimainmyfilesadapter, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suishoupaimainmyfilesmoreadapter, parent, false);
            return new MoreViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            ImageLoader.loadResize(itemViewHolder.sdv_image, "file:///" + infos.get(position).filePath,100,100);
            itemViewHolder.sdv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.TLong(context,"跳转图片查看和保存页面");
//                    MyApplication.clearSeletedBaseFile();
//                    MyApplication.getmSeletedBaseFile().add(infos.get(position));
//                    IntentUtils.jumpPhotoViewDDPActivity(context);
//                    mContext.startActivity(new Intent(context, PhotoViewDDPActivity.class)
//                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });
        } else {
            MoreViewHolder moreViewHolder = (MoreViewHolder) holder;
            ImageLoader.load(moreViewHolder.sdv_image_more, "res:///" + R.mipmap.icon_ddp_download1);
            moreViewHolder.sdv_image_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, SSPFilesActivity.class));
                }
            });
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView sdv_image;

        public ItemViewHolder(View itemView) {
            super(itemView);
            sdv_image = (SimpleDraweeView) itemView.findViewById(R.id.sdv_image);
        }
    }


    class MoreViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView sdv_image_more;

        public MoreViewHolder(View itemView) {
            super(itemView);
            sdv_image_more = (SimpleDraweeView) itemView.findViewById(R.id.sdv_image_more);
        }
    }
}
