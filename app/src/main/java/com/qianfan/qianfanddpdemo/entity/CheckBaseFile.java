package com.qianfan.qianfanddpdemo.entity;

import com.ddp.sdk.cam.resmgr.model.BaseFile;

/**
 * Created by wangjing on 2017/2/9.
 */

public class CheckBaseFile {
    private boolean ischeck=false;
    private BaseFile baseFile;

    public boolean ischeck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    public BaseFile getBaseFile() {
        return baseFile;
    }

    public void setBaseFile(BaseFile baseFile) {
        this.baseFile = baseFile;
    }
}
