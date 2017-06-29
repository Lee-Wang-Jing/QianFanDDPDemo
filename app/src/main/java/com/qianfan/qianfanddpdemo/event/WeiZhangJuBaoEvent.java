package com.qianfan.qianfanddpdemo.event;

/**
 * 功能详细描述
 *
 * @author xyx on 2017/4/26
 * @e-mail 384744573@qq.com
 * @see [相关类/方法](可选)
 */

public class WeiZhangJuBaoEvent {
    private boolean isSuccess;

    public WeiZhangJuBaoEvent(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
