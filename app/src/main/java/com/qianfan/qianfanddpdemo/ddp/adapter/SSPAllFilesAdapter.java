package com.qianfan.qianfanddpdemo.ddp.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

import com.qianfan.qianfanddpdemo.ddp.fragment.SSPAllFiles_PhotosFragment;
import com.qianfan.qianfanddpdemo.ddp.fragment.SSPAllFiles_SSOFragment;
import com.qianfan.qianfanddpdemo.ddp.fragment.SSPAllFiles_VideosFragment;
import com.qianfan.qianfanddpdemo.myinterface.OnNoFileListener;

/**
 * Created by wangjing on 2017/1/11.
 */

public class SSPAllFilesAdapter extends FragmentStatePagerAdapter {

    private String[] mTitle;
    private int albumId;
    private SSPAllFiles_PhotosFragment sspAllFilesPhotosFragment;
    private SSPAllFiles_VideosFragment sspAllFilesVideosFragment;
    private SSPAllFiles_SSOFragment sspAllFilesSsoFragment;
    private OnNoFileListener onNoFileListener;

    public SSPAllFilesAdapter(FragmentManager fm, String[] title, int albumId, OnNoFileListener noFileListener) {
        super(fm);
        this.albumId = albumId;
        this.mTitle = title;
        this.onNoFileListener = noFileListener;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitle[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (sspAllFilesPhotosFragment == null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("albumId", albumId);
                    sspAllFilesPhotosFragment = SSPAllFiles_PhotosFragment.newInstance(bundle);
                    sspAllFilesPhotosFragment.setNoFileListener(onNoFileListener);
                }
                return sspAllFilesPhotosFragment;
            case 1:
                if (sspAllFilesVideosFragment == null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("albumId", albumId);
                    sspAllFilesVideosFragment = SSPAllFiles_VideosFragment.newInstance(bundle);
                    sspAllFilesVideosFragment.setNoFileListener(onNoFileListener);
                }
                return sspAllFilesVideosFragment;
            default:
                if (sspAllFilesSsoFragment == null) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("albumId", albumId);
                    sspAllFilesSsoFragment = SSPAllFiles_SSOFragment.newInstance(bundle);
                    sspAllFilesSsoFragment.setNoFileListener(onNoFileListener);
                }
                return sspAllFilesSsoFragment;
        }
    }

    public void setEdit(int currentItem, boolean isEdit,boolean isAll) {
        switch (currentItem) {
            case 0:
                if (sspAllFilesPhotosFragment != null) {
                    sspAllFilesPhotosFragment.setEditState(isEdit,isAll);
                }
                break;
            case 1:
                if (sspAllFilesVideosFragment != null) {
                    sspAllFilesVideosFragment.setEditState(isEdit,isAll);
                }
                break;
            case 2:
                if (sspAllFilesSsoFragment != null) {
                    sspAllFilesSsoFragment.setEditState(isEdit,isAll);
                }
                break;
        }
    }

    public int hasData(int currentItem) {
        switch (currentItem) {
            case 0:
                if (sspAllFilesPhotosFragment != null) {
                    return sspAllFilesPhotosFragment.getAdapterCount();
                }
                break;
            case 1:
                if (sspAllFilesVideosFragment != null) {
                    return sspAllFilesVideosFragment.getAdapterCount();
                }
                break;
            case 2:
                if (sspAllFilesSsoFragment != null) {
                    return sspAllFilesSsoFragment.getAdapterCount();
                }
                break;
        }
        return 0;
    }

    @Override
    public int getCount() {
        return mTitle == null ? 0 : mTitle.length;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        FragmentManager manager = ((Fragment) object).getFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove((Fragment) object);
        trans.commit();
    }
}
