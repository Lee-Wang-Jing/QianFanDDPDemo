package com.qianfan.qianfanddpdemo.ddp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ddp.sdk.cam.resmgr.model.BaseFile;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.ddp.activity.VideoViewActivity;
import com.qianfan.qianfanddpdemo.entity.CheckBaseFile;
import com.qianfan.qianfanddpdemo.myinterface.OnDDPSelectFilesLinstener;
import com.qianfan.qianfanddpdemo.utils.DensityUtils;
import com.qianfan.qianfanddpdemo.utils.ImageLoader;
import com.qianfan.qianfanddpdemo.utils.LogUtil;
import com.qianfan.qianfanddpdemo.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangjing on 2017/2/8.
 */

public class SSPDateListGridVideoAdapter extends BaseAdapter {
    private List<CheckBaseFile> infos = new ArrayList<>();
    private int loadSize = 0;
    private BaseFile readyDeleteFile = null;

    private LayoutInflater inflater;
    private Context mContext;
    private OnDDPSelectFilesLinstener onDDPSelectFilesLinstener;
    private boolean isneedEdit = false;//是否需要编辑，默认false
    private boolean isAll = false;//是否需要全选，默认false

    public SSPDateListGridVideoAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        loadSize = DensityUtils.getScreenWidth(context) / 4;
    }

    public void setOnDDPSelectFilesLinstener(OnDDPSelectFilesLinstener onDDPSelectFilesLinstener) {
        this.onDDPSelectFilesLinstener = onDDPSelectFilesLinstener;
    }

    public boolean isAll() {
        return isAll;
    }

    public void setAll(boolean all) {
        isAll = all;
    }

    @Override
    public int getCount() {
        return infos == null ? 0 : infos.size();
    }

    @Override
    public Object getItem(int position) {
        return infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_sspdatelistvideoadapter, parent, false);
            holder.simpleDraweeView = (SimpleDraweeView) convertView.findViewById(R.id.simpleDraweeView);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        LogUtil.e("filepath", "" + infos.get(position).getBaseFile().filePath);
//        ImageLoader.loadLocalResize(holder.simpleDraweeView, "/storage/emulated/0/ddpaiSDK/video/video.M6.00e00100b534/N_20170606113234_91_10.mp4", loadSize, loadSize);
        ImageLoader.loadLocalResize(holder.simpleDraweeView, "" + infos.get(position).getBaseFile().filePath, loadSize, loadSize);
        holder.tv_time.setText("" + TimeUtils.formatTime1(infos.get(position).getBaseFile().duration));
        if (isneedEdit) {
            if (isAll) {
                infos.get(position).setIscheck(true);
            } else {
                infos.get(position).setIscheck(false);
            }
            holder.checkbox.setVisibility(View.VISIBLE);
            if (infos.get(position).ischeck()) {
                holder.checkbox.setChecked(true);
            } else {
                holder.checkbox.setChecked(false);
            }
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (onDDPSelectFilesLinstener != null) {
                        if (!isChecked && isAll) {
                            isAll = false;
                        }
                        infos.get(position).setIscheck(isChecked);
                        onDDPSelectFilesLinstener.onSelectClick(isChecked, infos.get(position).getBaseFile());
                    }
                    LogUtil.e("onCheckedChanged", "isChecked==>" + isChecked);
                }
            });
        } else {
            holder.checkbox.setVisibility(View.GONE);
            holder.simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    readyDeleteFile = infos.get(position).getBaseFile();
//                    IntentUtils.jumpVideoViewActivity(mContext, infos.get(position).getBaseFile().filePath,
//                            infos.get(position).getBaseFile().fileName,
//                            infos.get(position).getBaseFile().time);
                    Intent intent = new Intent(mContext, VideoViewActivity.class);
                    intent.putExtra("path", "" + infos.get(position).getBaseFile().filePath);
                    intent.putExtra("name", "" +   infos.get(position).getBaseFile().fileName);
                    intent.putExtra("startTime",    infos.get(position).getBaseFile().time);
                    mContext.startActivity(intent);
                }
            });
        }
        return convertView;
    }


    static class ViewHolder {
        SimpleDraweeView simpleDraweeView;
        TextView tv_time;
        CheckBox checkbox;

    }


    public BaseFile getReadyDeleteFile() {
        return readyDeleteFile;
    }

    public void addDatas(List<CheckBaseFile> infos, boolean isneedEdit, boolean isAll) {
        if (infos != null) {
            this.infos = infos;
            this.isneedEdit = isneedEdit;
            this.isAll = isAll;
            notifyDataSetChanged();
        }

    }
}
