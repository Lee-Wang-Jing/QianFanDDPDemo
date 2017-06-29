package com.qianfan.qianfanddpdemo.entity;

import java.util.List;

/**
 * Created by wangjing on 2017/2/8.
 */

public class DateListBaseFile {
    private long date;

    private List<CheckBaseFile> datas;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public List<CheckBaseFile> getDatas() {
        return datas;
    }

    public void setDatas(List<CheckBaseFile> datas) {
        this.datas = datas;
    }
}
