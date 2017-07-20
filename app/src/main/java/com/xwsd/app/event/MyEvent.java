package com.xwsd.app.event;


import com.xwsd.app.bean.BankListBean;
import com.xwsd.app.bean.RedpackageBean;

/**
 * Created by iyesug on 2017/6/20.
 */

public class MyEvent {

    RedpackageBean.Data.records chooseRedpackage;
    BankListBean.Data.limitList chooseBank;

    public MyEvent(RedpackageBean.Data.records chooseRedpackage) {
        this.chooseRedpackage=chooseRedpackage;
    }
    public MyEvent(BankListBean.Data.limitList chooseBank) {
        this.chooseBank=chooseBank;
    }
    public RedpackageBean.Data.records getUserData() {
        return chooseRedpackage;
    }

    public BankListBean.Data.limitList getChooseBank() {
        return chooseBank;
    }

}
