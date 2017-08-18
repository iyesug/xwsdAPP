package com.xwsd.app.bean;

import java.io.Serializable;

/**
 * Created by iyesug on 2017/8/17.
 */

public class SyncMoneyBean implements Serializable{
    public Data data;

    public class   Data implements Serializable {
        public String money;
        public String frozen;


    }
}
