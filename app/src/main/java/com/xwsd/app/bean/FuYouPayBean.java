package com.xwsd.app.bean;

import java.io.Serializable;

/**
 * 富友后台返回数据实体类
 * Created by Lj on 2016/10/30.
 */

public class FuYouPayBean implements Serializable {
    public String version;
    public String type;
    public String resCode;
    public String resMsg;
    public String mchntcd;
    public String mchntorderid;
    public String orderid;
    public String amt;
    public String bankcard;
    public String sign;

}
