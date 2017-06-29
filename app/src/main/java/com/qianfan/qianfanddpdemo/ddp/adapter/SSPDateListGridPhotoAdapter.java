package com.qianfan.qianfanddpdemo.ddp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.ddp.sdk.cam.resmgr.model.BaseFile;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qianfan.qianfanddpdemo.MyApplication;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.entity.CheckBaseFile;
import com.qianfan.qianfanddpdemo.myinterface.OnDDPSelectFilesLinstener;
import com.qianfan.qianfanddpdemo.utils.DensityUtils;
import com.qianfan.qianfanddpdemo.utils.ImageLoader;
import com.qianfan.qianfanddpdemo.utils.LogUtil;
import com.qianfan.qianfanddpdemo.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangjing on 2017/2/8.
 */

public class SSPDateListGridPhotoAdapter extends BaseAdapter {
    private List<CheckBaseFile> infos = new ArrayList<>();
    private int loadSize = 0;
    private LayoutInflater inflater;
    private Context mContext;
    private boolean isneedEdit = false;//是否需要编辑，默认false
    private boolean isAll = false;//是否需要全选，默认false
    private OnDDPSelectFilesLinstener onDDPSelectFilesLinstener;

    public SSPDateListGridPhotoAdapter(Context context) {
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
            convertView = inflater.inflate(R.layout.item_sspdatelistphotoadapter, parent, false);
            holder.simpleDraweeView = (SimpleDraweeView) convertView.findViewById(R.id.simpleDraweeView);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final BaseFile info = infos.get(position).getBaseFile();
        ImageLoader.loadResize(holder.simpleDraweeView, "file://" + info.filePath, loadSize, loadSize);
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
                        onDDPSelectFilesLinstener.onSelectClick(isChecked, info);
                    }
                    LogUtil.e("onCheckedChanged", "isChecked==>" + isChecked);
                }
            });
        } else {
            holder.checkbox.setVisibility(View.GONE);
            holder.simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isneedEdit) {

//                        List<AttachEntity> attachEntities = new ArrayList<AttachEntity>();
//                        AttachEntity attachEntity = new AttachEntity();
//                        attachEntity.setType(1);
//                        attachEntity.setUrl("file://" + info.filePath);
//                        attachEntities.add(attachEntity);
//                        MyApplication.clearSeletedBaseFile();
//                        MyApplication.getmSeletedBaseFile().add(info);
//                        IntentUtils.jumpPhotoViewDDPActivity(mContext);
                        ToastUtil.TLong(mContext,"跳转图片查看页面");
                    }
                }
            });
        }
        return convertView;
    }

    class ViewHolder {
        SimpleDraweeView simpleDraweeView;
        CheckBox checkbox;
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
