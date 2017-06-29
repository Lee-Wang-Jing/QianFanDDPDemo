package com.qianfan.qianfanddpdemo.entity;

/**
 * 违法类型
 *
 * @author xyx on 2017/3/31
 * @e-mail 384744573@qq.com
 * @see [相关类/方法](可选)
 */

public class IllegalCategoriesEntity {
    private int id;
    private String name;//违法名

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        if (name != null) {
            return name;
        } else {
            return "";
        }
    }

    public void setName(String name) {
        this.name = name;
    }
}
