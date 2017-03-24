package com.xwsd.app.bean;

import java.io.Serializable;

/**
 * Created by Gx on 2016/9/9.
 * 充值的实体类
 */
public class RechargeBean implements Serializable {
//    public String msg;
//    public int status;
//    public Data data;
//
//    public static class Data implements Serializable {
//        public Result result;
//
//        public static class Result implements Serializable {
//            public String tradeNo;
//            public String retCode;
//            public String retMsg;
//        }
//    }

    public String msg;
    public int status;
    public Data data;

    public static class Data implements Serializable {
        public int IDTYPE;
        public int AMT;
        public String NAME;
        public String BANKCARD;
        public String BACKURL;
        public String SIGNTP;
        public String MCHNTCD;
        public String MCHNTORDERID;
        public String USERID;
        public String VERSION;
        public String SIGN;
        public String TYPE;
        public String IDNO;
    }

}