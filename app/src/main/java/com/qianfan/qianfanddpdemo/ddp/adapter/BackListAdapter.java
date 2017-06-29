package com.qianfan.qianfanddpdemo.ddp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ddp.sdk.cam.resmgr.CameraResMgr;
import com.ddp.sdk.cam.resmgr.model.ThumbInfo;
import com.ddp.sdk.cambase.model.Camera;
import com.ddp.sdk.cambase.model.PlayFile;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.ddp.activity.PlaybackPlayerActivity;
import com.qianfan.qianfanddpdemo.utils.ImageLoader;
import com.qianfan.qianfanddpdemo.utils.LogUtil;
import com.qianfan.qianfanddpdemo.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangjing on 2017/2/17.
 */

public class BackListAdapter extends BaseAdapter {
    private static final String TAG = "BackListAdapter";
    private Context mContext;
    //    private SimpleDateFormat formater;
    private CameraResMgr cameraResMgr;
    private Camera camera;
    private List<PlayFile> playFiles = new ArrayList<>();


    public BackListAdapter(Context mContext, CameraResMgr cameraResMgr, Camera camera) {
        this.mContext = mContext;
        if (cameraResMgr == null) {
            this.cameraResMgr = CameraResMgr.instance();
        } else {
            this.cameraResMgr = cameraResMgr;
        }
        this.camera = camera;
//        formater = new SimpleDateFormat("HH:mm:ss");
    }

    public void addData(List<PlayFile> playFiles) {
        if (playFiles != null) {
            this.playFiles = playFiles;
//            notifyDataSetInvalidated();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return playFiles == null ? 0 : playFiles.size();
    }

    @Override
    public PlayFile getItem(int position) {
        if (playFiles != null && position < playFiles.size()) {
            return playFiles.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final FileHolder holder;
        if (convertView == null) {
            holder = new FileHolder();
            convertView = View.inflate(mContext, R.layout.alumb_file_item, null);
            holder.simpleDraweeView = (SimpleDraweeView) convertView.findViewById(R.id.simpleDraweeView);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
            convertView.setTag(holder);
        } else {
            holder = (FileHolder) convertView.getTag();
        }
        final PlayFile file = getItem(position);
        String filePath = null;
        holder.tv_time.setText("" + TimeUtils.millis2String(file.start, "HH:mm:ss"));
        try {
            final List<ThumbInfo> thumbsPath = cameraResMgr.getThumbListByPlayFile(camera, file);

            if (thumbsPath != null && !thumbsPath.isEmpty() && thumbsPath.get(0) != null && thumbsPath.get(0).fullPath != null) {
                filePath = "" + thumbsPath.get(0).fullPath;
            } else {
                filePath = "";
            }
            LogUtil.e("filePath", "filePath==>" + filePath);
            ImageLoader.load(holder.simpleDraweeView, "file://" + mContext.getPackageName() + "/" + filePath);

            final String finalFilePath = filePath;
            holder.simpleDraweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.e("locaPath", "" + file.locaPath);
                    Intent intent = new Intent(mContext, PlaybackPlayerActivity.class);
                    intent.putExtra("playbacktime", file.start);
                    intent.putExtra("filename", "" + file.name);
                    intent.putExtra("filePath", "file://" + finalFilePath);

                    mContext.startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    public static class FileHolder {
        SimpleDraweeView simpleDraweeView;
        TextView tv_time, tv_size;
//        int position;
//        PlayFile file;
    }
}
