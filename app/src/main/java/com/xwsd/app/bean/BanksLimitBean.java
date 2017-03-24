package com.xwsd.app.bean;

import java.io.Serializable;

/**
 * Created by Gx on 2016/9/8.
 * 银行卡限额的实体类
 */
public class BanksLimitBean implements Serializable {
    public String bankName;
    public String dayLimit;
    public String onceLimit;
    public String imgName;
    public String bankCode;
    public String bankId;
}
