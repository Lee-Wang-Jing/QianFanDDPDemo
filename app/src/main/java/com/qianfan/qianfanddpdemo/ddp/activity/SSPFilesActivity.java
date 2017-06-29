package com.qianfan.qianfanddpdemo.ddp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.ddp.sdk.base.DDPSDK;
import com.ddp.sdk.cam.resmgr.CameraResMgr;
import com.ddp.sdk.cam.resmgr.ICameraImageVideo;
import com.ddp.sdk.cam.resmgr.listener.onEventDownloadListener;
import com.ddp.sdk.cam.resmgr.model.Album;
import com.ddp.sdk.cam.resmgr.model.BaseFile;
import com.ddp.sdk.cambase.CameraServer;
import com.ddp.sdk.cambase.model.Camera;
import com.ddp.sdk.cambase.model.FileLoadTask;
import com.ddp.sdk.cambase.utils.VTask;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.base.BaseDDPActivity;
import com.qianfan.qianfanddpdemo.ddp.adapter.SSPFilesAdapter;
import com.qianfan.qianfanddpdemo.recyclerview.MyGridLayoutManager;
import com.qianfan.qianfanddpdemo.recyclerview.PullRecyclerView;
import com.qianfan.qianfanddpdemo.utils.LogUtil;
import com.qianfan.qianfanddpdemo.utils.ToastUtil;
import com.vyou.app.sdk.utils.VLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 盯盯拍拍照文件列表--（显示未下载的图片和视频）
 * Created by wangjing on 2017/1/11.
 */

public class SSPFilesActivity extends BaseDDPActivity implements PullRecyclerView.OnRecyclerRefreshListener {
    private static final String TAG = "SSPFilesActivity";

    private Toolbar toolbar;
    private PullRecyclerView pullRecyclerView;
    private TextView tv_progress;

    private SSPFilesAdapter adapter;

    //    private List<BaseFile> listDownloaded = new ArrayList<>();
    private List<BaseFile> listUnDownloaded = new ArrayList<>();
    private BaseFile baseFileFail;

    //private Context context;
    private Camera cam;
    private CameraResMgr cameraResMgr;
    private CameraServer cameraServer;
    private onEventDownloadListener albumEventDownLsn;
    private int albumId;

    List<BaseFile> downfiles = new ArrayList<BaseFile>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWhite(R.layout.activity_sspfiles);
        mLoadingView.showLoading();
        initP();
        initView();
//        updateListData(albumId);
    }


    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        pullRecyclerView = (PullRecyclerView) findViewById(R.id.pullRecyclerView);
        tv_progress = (TextView) findViewById(R.id.tv_progress);

        setBaseBackToolbar(toolbar, "拍照文件列表");

        adapter = new SSPFilesAdapter();
        pullRecyclerView.setOnRefreshListener(this);
        pullRecyclerView.setLayoutManager(new MyGridLayoutManager(this, 4));
        pullRecyclerView.setAdapter(adapter);
//        pullRecyclerView.setRefreshing();
        pullRecyclerView.enableLoadMore(false);

        if (cam != null) {
            albumId = (int) Album.get(cam).id;
        } else {
            Intent intent = new Intent(SSPFilesActivity.this, CameraSearchActivity.class);
            startActivity(intent);
            onBackPressedSupport();
        }
        LogUtil.e(TAG, "albumId = " + albumId);
    }

    private void initP() {
        DDPSDK.init(this, "");
        //context = getBaseContext();
        cameraResMgr = CameraResMgr.instance();
        cameraServer = CameraServer.intance();

        cam = CameraServer.intance().getCurrentConnectCamera();

        LogUtil.e(TAG, "cam = " + cam);
        albumEventDownLsn = new onEventDownloadListener() {
            @Override
            public void onStart(BaseFile file, FileLoadTask downloadTask) {
                VLog.v(TAG, "onStart ");
            }

            @Override
            public void onLoad(BaseFile file, final FileLoadTask downloadTask) {
                VLog.v(TAG, "onLoad " + downloadTask.loadLength * 100 / downloadTask.length);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int progress = (int) (downloadTask.loadLength * 100 / downloadTask.length);
                        tv_progress.setText("" + progress + "%");
                        if (progress == 100) {
                            tv_progress.setVisibility(View.GONE);
                        } else {
                            tv_progress.setVisibility(View.VISIBLE);
                        }
//                        adapter.updateProgress(downloadTask.loadLength * 100 / downloadTask.length);
                    }
                });
            }

            @Override
            public void onCancel(BaseFile file, FileLoadTask downloadTask) {
                VLog.v(TAG, "onCancel ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateListData(albumId);
                    }
                });
            }

            @Override
            public void onException(final BaseFile file, FileLoadTask downloadTask, Exception e) {
                VLog.v(TAG, "onException ");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.TLong(SSPFilesActivity.this, "文件" + file.getDownloadName() + "下载失败");
                        baseFileFail = file;
                        if (listUnDownloaded.size() > 1) {
                            updateListData(albumId);
                        }
                    }
                });
            }

            @Override
            public void onFinish(BaseFile file, FileLoadTask downloadTask) {
                VLog.v(TAG, "onFinish ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateListData(albumId);
                    }
                });
            }
        };
        cameraResMgr.addEventDownloadListener(albumEventDownLsn);
        cameraServer.addCameraStateChangeListener(camLifeCycleListener);
    }


    @Override
    protected void onResume() {
        super.onResume();
        queryUndownLoadList();
    }

    private void queryUndownLoadList(){
        // 从摄像机中查询未下载列表
        new VTask<Object, Integer>() {
            @Override
            protected Integer doBackground(Object o) {
                //cameraServer.getResEventFiles(cam);
                cameraResMgr.doQueryUndownloadImageVideoAndUpdateDb(cam);
                return null;
            }

            @Override
            protected void doPost(Integer integer) {
                updateListData(albumId);
            }
        };
    }

    private void updateListData(final int albumId) {
        listUnDownloaded.clear();
        listUnDownloaded.addAll(cameraResMgr.getImages(albumId, ICameraImageVideo.COMPLETE.UN_DOWN, -1));
        listUnDownloaded.addAll(cameraResMgr.getVideos(albumId, ICameraImageVideo.COMPLETE.UN_DOWN, -1));
        LogUtil.d("updateListData", "listUnDownloaded.size==>" + listUnDownloaded.size());
        if (listUnDownloaded == null || listUnDownloaded.isEmpty()) {
            mLoadingView.showEmpty("您还没有未下载的拍照文件哦~");
            mLoadingView.setOnEmptyClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLoadingView.showLoading();
                    queryUndownLoadList();
                }
            });
            return;
        } else {
            mLoadingView.dismissLoadingView();
        }
        adapter.clear();
//        adapter.addData(listDownloaded);
        adapter.addData(listUnDownloaded);
        pullRecyclerView.onRefreshCompleted();
        if (listUnDownloaded != null && listUnDownloaded.size() > 0) {
            if (baseFileFail == null) {
                downUndownload(listUnDownloaded.get(0));
            } else {//存在下载失败的文件
                if (listUnDownloaded.size() > 1) {
                    for (int i = 0; i < listUnDownloaded.size(); i++) {
                        if (!listUnDownloaded.get(i).equals(baseFileFail)) {
                            downUndownload(listUnDownloaded.get(i));
                            break;
                        }
                    }
                } else {
                    downUndownload(listUnDownloaded.get(0));
                }
            }
        }
    }

    private void downUndownload(BaseFile baseFile) {
        LogUtil.e("downUndownload", "downUndownload");
        if (ICameraImageVideo.DOWN_STATE.FREE.equals(cameraResMgr.getDownState(baseFile))) {// 未下载的去下载
            LogUtil.e("downUndownload", "未下载的去下载");
            downfiles.clear();
            downfiles.add(baseFile);
            cameraResMgr.download(downfiles);
        }
    }


    @Override
    public void onRefresh(int action) {
//        if (action == PullRecyclerView.ACTION_PULL_TO_REFRESH) {
//            page = 0;
//        } else {
//            page++;
//        }
//        getData();
        queryUndownLoadList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraResMgr != null) {
            cameraResMgr.cancelDownload(downfiles);
            cameraResMgr.removeEventDownloadListener(albumEventDownLsn);
        }
        if (cameraServer != null) {
            cameraServer.removeCameraStateChangeListener(camLifeCycleListener);
        }
    }
}
