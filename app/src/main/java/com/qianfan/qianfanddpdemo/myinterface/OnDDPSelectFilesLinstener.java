package com.qianfan.qianfanddpdemo.myinterface;

import com.ddp.sdk.cam.resmgr.model.BaseFile;

/**
 * Created by wangjing on 2017/2/10.
 */

public interface OnDDPSelectFilesLinstener {
    void onSelectClick(boolean ischecked, BaseFile baseFile);
}
