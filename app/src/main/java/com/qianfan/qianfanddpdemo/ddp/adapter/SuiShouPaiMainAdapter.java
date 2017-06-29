package com.qianfan.qianfanddpdemo.ddp.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ddp.sdk.cam.resmgr.CameraResMgr;
import com.ddp.sdk.cam.resmgr.ICameraImageVideo;
import com.ddp.sdk.cam.resmgr.model.Album;
import com.ddp.sdk.cam.resmgr.model.EventImage;
import com.ddp.sdk.cambase.CameraServer;
import com.ddp.sdk.cambase.model.Camera;
import com.ddp.sdk.cambase.network.NetworkMgr;
import com.facebook.drawee.view.SimpleDraweeView;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.ddp.activity.SSPAllFilesActivity;
import com.qianfan.qianfanddpdemo.ddp.activity.SuiShouPaiPreviewsActivity;
import com.qianfan.qianfanddpdemo.myinterface.OOnConnectDDPListener;
import com.qianfan.qianfanddpdemo.myinterface.OnDDPCameraEmptyListener;
import com.qianfan.qianfanddpdemo.recyclerview.BaseAdapter;
import com.qianfan.qianfanddpdemo.recyclerview.BaseViewHolder;
import com.qianfan.qianfanddpdemo.recyclerview.DividerGridItemDecoration;
import com.qianfan.qianfanddpdemo.utils.DensityUtils;
import com.qianfan.qianfanddpdemo.utils.ImageLoader;
import com.qianfan.qianfanddpdemo.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wangjing on 2017/1/11.
 */

public class SuiShouPaiMainAdapter extends BaseAdapter {
    private Context mContext;
    private List<Camera> infos = new ArrayList<>();
    private Camera currentConnectCamera;
    private CameraServer cameraServer;
    private CameraResMgr cameraResMgr;
    private NetworkMgr networkMgr;
    private OOnConnectDDPListener onConnectDDPListener;
    private OnDDPCameraEmptyListener onDDPCameraEmptyListener;

    private String fileFirstPath;

    public SuiShouPaiMainAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected int getDataCount() {
        return infos != null ? infos.size() : 0;
    }

    @Override
    protected BaseViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suishoupaimainadapter, parent, false);
        return new ItemViewHolder(view);
    }

    public void setOnConnectDDPListener(OOnConnectDDPListener onConnectDDPListener) {
        this.onConnectDDPListener = onConnectDDPListener;
    }

    public void setOnDDPCameraEmptyListener(OnDDPCameraEmptyListener onDDPCameraEmptyListener) {
        this.onDDPCameraEmptyListener = onDDPCameraEmptyListener;
    }

    public void setCameraServer(CameraServer cameraServer) {
        this.cameraServer = cameraServer;
    }

    class ItemViewHolder extends BaseViewHolder {
        RelativeLayout rel_preview;
        SimpleDraweeView sdv_big;
        ImageView imv_player;
        TextView tv_name;
        ImageView imv_del;
        TextView tv_allfiles;
        RecyclerView recyclerView;
        SuiShouPaiMainMyFilesAdapter adapter;
        LinearLayout ll_file;

        public ItemViewHolder(View itemView) {
            super(itemView);
            rel_preview = (RelativeLayout) itemView.findViewById(R.id.rel_preview);
            sdv_big = (SimpleDraweeView) itemView.findViewById(R.id.sdv_big);
            imv_player = (ImageView) itemView.findViewById(R.id.imv_player);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            imv_del = (ImageView) itemView.findViewById(R.id.imv_del);
            tv_allfiles = (TextView) itemView.findViewById(R.id.tv_allfiles);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
            ll_file = (LinearLayout) itemView.findViewById(R.id.ll_file);
            adapter = new SuiShouPaiMainMyFilesAdapter(itemView.getContext());
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 5, LinearLayoutManager.VERTICAL, false));
            recyclerView.addItemDecoration(new DividerGridItemDecoration(mContext));
            recyclerView.setAdapter(adapter);
        }

        @Override
        public void onBindViewHolder(final int position) {
            final Camera camera = infos.get(position);
            if (camera != null) {
                tv_name.setText("" + camera.getName());
                final boolean cameraIsConnected = camera.isConnected && currentConnectCamera != null && camera.equals(currentConnectCamera)
                        && networkMgr != null && networkMgr.isCameraWifiConnected(camera);
                if (cameraIsConnected) {//已经连接
                    tv_name.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                    imv_player.setVisibility(View.VISIBLE);
                } else {
                    imv_player.setVisibility(View.GONE);
                    tv_name.setTextColor(ContextCompat.getColor(mContext, R.color.color_222222));
                }
                rel_preview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cameraIsConnected) {
                            mContext.startActivity(new Intent(mContext, SuiShouPaiPreviewsActivity.class)
                                    .putExtra("filePath", fileFirstPath)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                        } else {
                            if (onConnectDDPListener != null) {
                                onConnectDDPListener.onConnectDDPClick(camera);
                            }
                        }
                    }
                });
                tv_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cameraIsConnected) {
                            ToastUtil.TShort(mContext, "摄像机已连接");
                        } else {
                            if (onConnectDDPListener != null) {
                                onConnectDDPListener.onConnectDDPClick(camera);
                            }
                        }
                    }
                });
                tv_allfiles.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, SSPAllFilesActivity.class);
                        intent.putExtra("albumId", (int) Album.get(camera).id);
                        mContext.startActivity(intent);
                    }
                });
                imv_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("确定删除" + camera.getName() + "吗?");
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (cameraServer != null && camera != null) {
                                    cameraServer.deleteCamera(camera);
                                    infos.remove(position);
                                    notifyDataSetChanged();
                                    if (infos == null || infos.isEmpty()) {
                                        if (onDDPCameraEmptyListener != null) {
                                            onDDPCameraEmptyListener.onDDPCameraEmptyListener();
                                        }
                                    }
                                    ToastUtil.TShort(mContext, "删除成功");
                                }
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }
                });
                if (cameraIsConnected) {//已连接
                    List<EventImage> listDownloadedImage = new ArrayList<>();
                    if (cameraResMgr == null) {
                        cameraResMgr = CameraResMgr.instance();
                    }
                    listDownloadedImage.addAll(cameraResMgr.getImages((int) Album.get(camera).id, ICameraImageVideo.COMPLETE.DOWN_OK, -1));
                    if (listDownloadedImage != null && listDownloadedImage.size() > 14) {
                        Collections.reverse(listDownloadedImage);
                        listDownloadedImage = listDownloadedImage.subList(0, 14);
                    }
                    if (listDownloadedImage != null && !listDownloadedImage.isEmpty() && camera.isConnected && currentConnectCamera != null && camera.equals(currentConnectCamera)) {
                        fileFirstPath = listDownloadedImage.get(0).filePath;
                        ImageLoader.loadResize(sdv_big, "file:///" + fileFirstPath, DensityUtils.getScreenWidth(mContext), (int) (DensityUtils.getScreenWidth(mContext) / 2.5));
                    } else {
                        ImageLoader.loadResize(sdv_big, "");
                    }
                    adapter.clear();
                    adapter.addData(listDownloadedImage);
//                    if (listDownloadedImage.size() == 0) {
                    ll_file.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
//                    } else {
//                        ll_file.setVisibility(View.GONE);
//                        recyclerView.setVisibility(View.VISIBLE);
//                    }
                } else {
                    ImageLoader.loadResize(sdv_big, "");
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    ll_file.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onItemClick(View view, int position) {

        }
    }

    public void addData(List<Camera> datas, Camera currentConnectCamera, CameraResMgr cameraResMgr, NetworkMgr networkMgr) {
        if (datas != null) {
            this.currentConnectCamera = currentConnectCamera;
            this.cameraResMgr = cameraResMgr;
            this.networkMgr = networkMgr;
            infos = datas;
            notifyDataSetChanged();
        }
    }

    public void clear() {
        infos.clear();
    }
}
