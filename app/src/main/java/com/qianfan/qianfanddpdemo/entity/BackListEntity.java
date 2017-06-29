package com.qianfan.qianfanddpdemo.entity;

import com.ddp.sdk.cambase.model.PlayFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangjing on 2017/2/17.
 */

public class BackListEntity {

    private Long time;
    private List<PlayFile> playFiles = new ArrayList<>();

    public List<PlayFile> getPlayFiles() {
        return playFiles;
    }

    public void setPlayFiles(List<PlayFile> playFiles) {
        this.playFiles = playFiles;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
