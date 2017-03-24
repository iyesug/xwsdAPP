package com.xwsd.app.bean;

import java.io.Serializable;

/**
 * Created by Gx on 2016/9/5.
 * 账户信息实体类
 */
public class AccountBean implements Serializable {

    public Data data;

    public static class Data {
        public float tenderMoney;
        public float stayAllMoney;
        public float hasInterest;
        public float monthStayMoney;
        public String monthStayCount;
        public float stayInterest;
        public float fundMoney;
    }
}
