package com.qianfan.qianfanddpdemo.entity;


/**
 * 通用的Footer Entity
 *
 * @author WangJing on 2016/11/24 0024 17:05
 * @e-mail wangjinggm@gmail.com
 * @see [相关类/方法](可选)
 */
public class FooterEntity {
    private int type;

    public FooterEntity() {
    }

    /**
     * 默认为0
     * 0:点击查看更多 1：正在努力加载 2：已显示全部  3：加载失败，点击重新加载  4. 空
     */
    public FooterEntity(int type) {
        this.type = type;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


}