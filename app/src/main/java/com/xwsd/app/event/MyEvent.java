package com.xwsd.app.event;


import com.xwsd.app.bean.RedpackageBean;

/**
 * Created by iyesug on 2017/6/20.
 */

public class MyEvent {

    RedpackageBean.Data.records chooseRedpackage;

    public MyEvent(RedpackageBean.Data.records chooseRedpackage) {
        this.chooseRedpackage=chooseRedpackage;
    }

    public RedpackageBean.Data.records getUserData() {
        return chooseRedpackage;
    }


}
