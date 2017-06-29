package com.qianfan.qianfanddpdemo.ddp.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.ddp.sdk.cam.resmgr.CameraResMgr;
import com.ddp.sdk.cam.resmgr.ICameraImageVideo;
import com.ddp.sdk.cam.resmgr.model.BaseFile;
import com.qianfan.qianfanddpdemo.MyApplication;
import com.qianfan.qianfanddpdemo.R;
import com.qianfan.qianfanddpdemo.base.BaseFragment;
import com.qianfan.qianfanddpdemo.ddp.adapter.SSPAllFiles_SSOFragmentAdapter;
import com.qianfan.qianfanddpdemo.entity.CheckBaseFile;
import com.qianfan.qianfanddpdemo.entity.DateListBaseFile;
import com.qianfan.qianfanddpdemo.entity.FooterEntity;
import com.qianfan.qianfanddpdemo.event.RefreshEvent;
import com.qianfan.qianfanddpdemo.event.VideoDelEvent;
import com.qianfan.qianfanddpdemo.myinterface.OnDDPSelectFilesLinstener;
import com.qianfan.qianfanddpdemo.myinterface.OnNoFileListener;
import com.qianfan.qianfanddpdemo.recyclerview.PullRecyclerView;
import com.qianfan.qianfanddpdemo.utils.LogUtil;
import com.qianfan.qianfanddpdemo.utils.TimeUtils;
import com.qianfan.qianfanddpdemo.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 随手拍-所有文件-紧急fragment
 *
 * @author wangjing on 2017/1/6 11:04
 * @e-mail 919335417@qq.com
 * @see [相关类/方法](可选)
 */
public class SSPAllFiles_SSOFragment extends BaseFragment implements PullRecyclerView.OnRecyclerRefreshListener, OnDDPSelectFilesLinstener {
    private PullRecyclerView pullRecyclerView;
    private LinearLayout ll_bottom, ll_share, ll_del,ll_dowmload;

    private OnNoFileListener noFileListener;
    private List<BaseFile> listSSODownloaded = new ArrayList<>();
    private SSPAllFiles_SSOFragmentAdapter adapter;
    private List<DateListBaseFile> infos = new ArrayList<>();
    private List<BaseFile> selectinfos = new ArrayList<>();
    private DeleteImageTask task;

    private CameraResMgr cameraResMgr;
    //    private Camera cam;
    private static int albumId;

    private ProgressDialog delDialog;

    public static SSPAllFiles_SSOFragment newInstance(Bundle bundle) {
        if (bundle != null) {
            albumId = bundle.getInt("albumId", 0);
        } else {
            albumId = 0;
            bundle = new Bundle();
        }
        SSPAllFiles_SSOFragment fragment = new SSPAllFiles_SSOFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setNoFileListener(OnNoFileListener noFileListener) {
        this.noFileListener = noFileListener;
    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_sspallfiles_sso;
    }

    @Override
    protected void init(View view) {
        EventBus.getDefault().register(this);
        initP();
        initView(view);
    }

    private void initP() {
        cameraResMgr = CameraResMgr.instance();
//        cam = CameraServer.intance().getCurrentConnectCamera();
//        if (cam != null) {
//            albumId = (int) Album.get(cam).id;
//        } else {
//            Intent intent = new Intent(_mActivity, CameraSearchActivity.class);
//            startActivity(intent);
//        }
    }

    private void initView(View view_inflater) {
        pullRecyclerView = (PullRecyclerView) view_inflater.findViewById(R.id.pullRecyclerView);
        ll_bottom = (LinearLayout) view_inflater.findViewById(R.id.ll_bottom);
        ll_share = (LinearLayout) view_inflater.findViewById(R.id.ll_share);
        ll_dowmload = (LinearLayout) view_inflater.findViewById(R.id.ll_dowmload);
        ll_del = (LinearLayout) view_inflater.findViewById(R.id.ll_del);
        initLazyView();
    }


    private void initLazyView() {
        adapter = new SSPAllFiles_SSOFragmentAdapter(getActivity());
        adapter.setOnDDPSelectFilesLinstener(this);
        pullRecyclerView.setOnRefreshListener(this);
        pullRecyclerView.setLayoutManager(getLayoutManager());
        pullRecyclerView.setAdapter(adapter);
        pullRecyclerView.setRefreshing();
        ll_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.TShort(_mActivity, "请先登录");
//                if (!MyApplication.isLogin()){
//                    IntentUtils.jumpLogin(_mActivity);
//                    return;
//                }
//                getSelectInfos();
//                if (selectinfos == null || selectinfos.isEmpty()) {
//                    ToastUtil.TShort(_mActivity, "请先选择分享的文件");
//                    return;
//                }
//                if (selectinfos.size() > 9) {
//                    ToastUtil.TShort(_mActivity, "文件不能大于9个哦！");
//                    return;
//                }
//                boolean hasPhoto = false;
//                boolean hasVideo = false;
//                for (int i = 0; i < selectinfos.size(); i++) {
//                    if (selectinfos.get(i).isVideo()) {
//                        hasVideo = true;
//                    } else {
//                        hasPhoto = true;
//                    }
//                }
//                if (hasPhoto && hasVideo) {
//                    ToastUtil.TShort(_mActivity, "图片和视频不能同时分享哦！");
//                    return;
//                }
//                if (hasVideo && !hasPhoto) {//只有视频没有图片
//                    if (selectinfos.size() > 1) {
//                        ToastUtil.TShort(_mActivity, "视频分享只能选择一个哦！");
//                        return;
//                    } else {
//                        if (selectinfos.get(0) != null) {
//                            long duration = selectinfos.get(0).duration;
//                            if (duration < 5000 || duration > 15000) {
//                                IntentUtils.jumpVideoCropPaiActivity(_mActivity, selectinfos.get(0).filePath, selectinfos.get(0).time);
//                            } else {
//                                IntentUtils.jumpPaiPublishVideoActivity(_mActivity, selectinfos.get(0).filePath, 0L);
//                            }
//                        }
//                    }
//                } else if (hasPhoto && !hasVideo) {//只有图片没有视频
//                    MyApplication.clearSelectedImg();
//                    for (BaseFile file : selectinfos) {
//                        MyApplication.getmSeletedImg().add(file.filePath);
//                    }
//                    IntentUtils.jumpPai_Publish(_mActivity, 0L, "", false, true, 0, "");
//                } else {//没有视频也没有图片
//                    ToastUtil.TShort(_mActivity, "请先选择分享的文件");
//                    return;
//                }
            }
        });
        ll_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDelDialog();
                getSelectInfos();

                LogUtil.e("ll_del", "size==>" + selectinfos.size());
                if (selectinfos != null && !selectinfos.isEmpty()) {
                    task = new DeleteImageTask();
                    task.execute(selectinfos);
                } else {
                    dissDelDialog();
                    ToastUtil.TShort(_mActivity, "您还未选择文件哦！");
                }
            }
        });
        ll_dowmload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDelDialog();
                getSelectInfos();
                if (selectinfos != null && !selectinfos.isEmpty()) {
                    for (int i = 0; i < selectinfos.size(); i++) {
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        File file = new File(selectinfos.get(i).filePath);
                        Uri uri = Uri.fromFile(file);
                        intent.setData(uri);
                        _mActivity.sendBroadcast(intent);
                    }
                    ToastUtil.TShort(_mActivity,"保存到相册成功");
                    dissDelDialog();
                } else {
                    dissDelDialog();
                    ToastUtil.TShort(_mActivity, "您还未选择文件哦！");
                }
            }
        });
    }

    private void getSelectInfos() {
        selectinfos.clear();
        for (DateListBaseFile info : infos) {
            for (int i = 0; i < info.getDatas().size(); i++) {
                if (info.getDatas().get(i).ischeck()) {
                    selectinfos.add(info.getDatas().get(i).getBaseFile());
                }
            }
        }
    }

    @Override
    public void onRefresh(int action) {
        if (action == PullRecyclerView.ACTION_PULL_TO_REFRESH) {
            getData();
        }
    }

    public void setEditState(boolean isneedEdit, boolean isSelectAll) {
        if (adapter != null) {
            if (isneedEdit) {
                ll_bottom.setVisibility(View.VISIBLE);
            } else {
                ll_bottom.setVisibility(View.GONE);
            }
            adapter.setIsneedEdit(isneedEdit, isSelectAll);
        }
    }

    public int getAdapterCount() {
        if (adapter != null) {
            return adapter.getItemCount();
        } else {
            return 0;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(RefreshEvent event) {
        getData();
    }

    /**
     * 获取已下载的SSO图片和视频列表
     */
    private void getData() {
        mLoadingView.showLoading();

        List<BaseFile> allfiles = new ArrayList<>();
        allfiles.addAll(cameraResMgr.getImages(albumId, ICameraImageVideo.COMPLETE.DOWN_OK, -1));
        allfiles.addAll(cameraResMgr.getVideos(albumId, ICameraImageVideo.COMPLETE.DOWN_OK, -1));
        getCommonPhoto(allfiles);
    }

    private void showDelDialog() {
        if (delDialog == null) {
            delDialog = new ProgressDialog(_mActivity);
            delDialog.setCancelable(false);
            delDialog.setMessage("正在删除，请稍候…");
        }
        delDialog.show();
    }

    private void dissDelDialog() {
        if (delDialog != null) {
            delDialog.dismiss();
        }
    }

    private void getCommonPhoto(List<BaseFile> images) {

        listSSODownloaded.clear();
        selectinfos.clear();
        infos.clear();
        adapter.clear();
        if (images != null && !images.isEmpty()) {
            LogUtil.e("getCommonPhoto", "images不为空");
            LogUtil.e("getCommonPhoto", "images size==>" + images.size());
            for (int i = 0; i < images.size(); i++) {
                if (images.get(i).getDownloadName().startsWith("G_")) {
                    listSSODownloaded.add(images.get(i));
                }
            }
        }
        if (listSSODownloaded == null || listSSODownloaded.isEmpty()) {
            pullRecyclerView.onRefreshCompleted();
            mLoadingView.showEmpty("您还没有下载的文件哦~", false);
            mLoadingView.setOnEmptyClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getData();
                }
            });
            if (noFileListener != null) {
                noFileListener.noFileListener();
            }
            return;
        }
        if (listSSODownloaded != null && !listSSODownloaded.isEmpty()) {
            for (int i = listSSODownloaded.size() - 1; i >= 0; i--) {//倒序添加--时间
                long time = listSSODownloaded.get(i).time;
                if (hasThisDate(time, listSSODownloaded.get(i))) {//数据中存在当前日期
                } else {//数据中不存在当前日期
                    DateListBaseFile dateListBaseFile = new DateListBaseFile();
                    dateListBaseFile.setDate(time);
                    List<CheckBaseFile> files = new ArrayList<CheckBaseFile>();
                    CheckBaseFile checkBaseFile = new CheckBaseFile();
                    checkBaseFile.setBaseFile(listSSODownloaded.get(i));
                    files.add(checkBaseFile);
                    dateListBaseFile.setDatas(files);
                    infos.add(dateListBaseFile);
                }
            }
        } else {
            LogUtil.e("getCommonPhoto", "listSSODownloaded ==null");
            mLoadingView.showEmpty("您还没有下载的文件哦~", false);
            mLoadingView.setOnEmptyClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getData();
                }
            });
            if (noFileListener != null) {
                noFileListener.noFileListener();
            }
            return;
        }

        adapter.setData(infos);
        pullRecyclerView.onRefreshCompleted();
        adapter.notifyFooterState(new FooterEntity(2));
        pullRecyclerView.enablePullToRefresh(false);
        LogUtil.e("getCommonPhoto", "size==>" + listSSODownloaded.size());
        LogUtil.e("getCommonPhoto", "size==>" + infos.size());
        mLoadingView.dismissLoadingView();
    }

    private boolean hasThisDate(long time, BaseFile basefile) {
        for (int i = 0; i < infos.size(); i++) {
            String date = TimeUtils.millis2String(infos.get(i).getDate(), "yyyyMMdd");
            if (date.equals(TimeUtils.millis2String(time, "yyyyMMdd"))) {
                CheckBaseFile checkBaseFile = new CheckBaseFile();
                checkBaseFile.setBaseFile(basefile);
                infos.get(i).getDatas().add(checkBaseFile);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSelectClick(boolean ischecked, BaseFile baseFile) {
//        if (ischecked) {
//            if (baseFile != null) {
//                selectinfos.add(baseFile);
//            }
//        } else {
//            if (baseFile != null) {
//                selectinfos.remove(baseFile);
//            }
//        }

        if (!ischecked) {
            adapter.setAll(false);
            if (noFileListener != null) {
                noFileListener.noSelectAll();
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoDelEvent(VideoDelEvent event) {
        LogUtil.e("onVideoDelEvent");
        if (adapter.getGridAdapter() != null && adapter.getGridAdapter().getReadyDeleteFile() != null) {
            List<BaseFile> delinfos = new ArrayList<>();
            delinfos.add(adapter.getGridAdapter().getReadyDeleteFile());
            if (cameraResMgr != null) {
                LogUtil.e("执行了删除");
                task = new DeleteImageTask();
                selectinfos.clear();
                selectinfos.add(adapter.getGridAdapter().getReadyDeleteFile());
                task.execute(selectinfos);
            } else {
                LogUtil.e("为空2");
            }
        } else {
            LogUtil.e("为空1");
        }
    }

    class DeleteImageTask extends AsyncTask<List<BaseFile>, Integer, Integer> {

        @Override
        protected Integer doInBackground(List<BaseFile>... params) {
            if (cameraResMgr != null) {
                cameraResMgr.delete(selectinfos);
                return 1;
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer == 1) {
                ToastUtil.TShort(getActivity(), "删除成功");
            } else {
                ToastUtil.TShort(getActivity(), "删除失败");
            }
            if (pullRecyclerView != null) {
                pullRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dissDelDialog();
                        getData();
                    }
                }, 1000);
            }
        }
    }
}
