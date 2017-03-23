package com.xwsd.app.bean;

/**
 * Created by Gx on 2016/8/22.
 * 账户条目的实体类
 */
public class AccountItemBean {

    public String title;
    public int icoId;
    public Class activity;

    public AccountItemBean(String title, int icoId, Class activity) {
        this.title = title;
        this.icoId = icoId;
        this.activity = activity;
    }

}
