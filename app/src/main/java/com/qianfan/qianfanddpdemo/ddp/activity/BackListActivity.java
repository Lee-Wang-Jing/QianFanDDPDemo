package com.qianfan.qianfanddpdemo.ddp.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;

import com.ddp.sdk.base.DDPSDK;
import com.ddp.sdk.cam.resmgr.CameraResMgr;
import com.ddp.sdk.cam.resmgr.listener.UpdateThumbListener;
import com.ddp.sdk.cam.resmgr.model.ThumbInfo;
import com.ddp.sdk.cambase.CameraServer;
import com.ddp.sdk.cambase.model.Camera;
import com.ddp.sdk.cambase.model.PlayFile;
import com.ddp.sdk.cambase.utils.VTask;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.base.BaseDDPActivity;
import com.qianfan.qianfanddpdemo.ddp.adapter.BackListActivityAdapter;
import com.qianfan.qianfanddpdemo.entity.BackListEntity;
import com.qianfan.qianfanddpdemo.utils.TimeUtils;
import com.vyou.app.sdk.utils.VLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 回放列表
 * Created by wangjing on 2017/1/9.
 */
public class BackListActivity extends BaseDDPActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "BackListActivity";

    private Toolbar toolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private BackListActivityAdapter adapter;

    private CameraServer cameraServer;
    private CameraResMgr cameraResMgr;
    private Camera camera;

    private List<PlayFile> playFiles = new ArrayList<>();


    private int fileItemHeight;
    private int fileItemWidth;
    private int spacing = 20;
    // 比例
    private final double RADIO = 16.0 / 9;

    private DisplayMetrics dm;

//    private SimpleDateFormat formater = new SimpleDateFormat(ResFile.DATE_FILE_NAME);


    /**
     * 列数
     */
    public static final int NUM_COLUMN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWhite(R.layout.activity_playback_list);
        mLoadingView.showLoading(true);
        initP();
        initView();
        initData();
        getbackDatas();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void getbackDatas() {
        mLoadingView.showLoading(true);
        new VTask<Object, List<PlayFile>>() {
            @Override
            protected List<PlayFile> doBackground(Object o) {
                return cameraServer.getResPlayFiles(camera);
            }

            @Override
            protected void doPost(List<PlayFile> files) {
                if (files != null) {
                    playFiles = files;
                    Collections.sort(playFiles, new Comparator<PlayFile>() {
                        @Override
                        public int compare(PlayFile playFile, PlayFile otherFile) {
                            // 按时间排序
                            if (playFile.start < otherFile.start) {
                                return 1;
                            } else if (playFile.start > otherFile.start) {
                                return -1;
                            }
                            return 0;
                        }
                    });
                    List<BackListEntity> infos = new ArrayList<>();
                    for (int i = 0; i < playFiles.size(); i++) {
                        if (hasThisDate(playFiles.get(i).start, playFiles.get(i), infos)) {//已经存在当前日期
                        } else {//不存在当前日期
                            BackListEntity entity = new BackListEntity();
                            entity.setTime(playFiles.get(i).start);
                            entity.getPlayFiles().add(playFiles.get(i));
                            infos.add(entity);
                        }
                    }
                    adapter.addData(infos);
                    mLoadingView.dismissLoadingView();
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
        };
    }

    private boolean hasThisDate(long time, PlayFile playfile, List<BackListEntity> infos) {
        for (int i = 0; i < infos.size(); i++) {
            String date = TimeUtils.millis2String(infos.get(i).getTime(), "yyyyMMdd");
            if (date.equals(TimeUtils.millis2String(time, "yyyyMMdd"))) {//存在相同日期+加入list
                infos.get(i).getPlayFiles().add(playfile);
                return true;
            }
        }
        return false;
    }

    private void initP() {
        DDPSDK.init(this, "");
        cameraServer = CameraServer.intance();
        camera = cameraServer.getCurrentConnectCamera();
        cameraResMgr = CameraResMgr.instance();

        if (camera == null) {
            VLog.e(TAG, "camera == null");
            finish();
        }

        dm = getResources().getDisplayMetrics();

        fileItemWidth = (Math.min(dm.widthPixels, dm.heightPixels) - (NUM_COLUMN - 1) * spacing) / NUM_COLUMN;
        fileItemHeight = (int) (fileItemWidth / RADIO);

        VLog.v(TAG, "fileItemWidth = " + fileItemWidth + ", fileItemHeight = " + fileItemHeight);

        cameraServer.addCameraStateChangeListener(camLifeCycleListener);
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        setBaseBackToolbar(toolbar, "回放列表");
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(this);
        adapter = new BackListActivityAdapter(this, cameraResMgr, camera);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        cameraResMgr.registerUpdateThumbListener(camera, updateThumbListener, false);
    }

    UpdateThumbListener updateThumbListener = new UpdateThumbListener() {
        @Override
        public void updateThumb(List<ThumbInfo> list) {
            VLog.v(TAG, "updateThumb thumbInfos.size = " + list.size());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    //mLoadingView.dismissLoadingView();
                }
            });
        }
    };

    @Override
    public void onRefresh() {
        getbackDatas();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraResMgr != null && camera != null) {
            cameraResMgr.unRegisterUpdateThumbListener(camera, updateThumbListener);
        }
        if (cameraServer != null) {
            cameraServer.removeCameraStateChangeListener(camLifeCycleListener);
        }
    }
}
