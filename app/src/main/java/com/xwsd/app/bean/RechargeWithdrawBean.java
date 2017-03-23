package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gx on 2016/9/6.
 * 充值提现记录的实体类
 */
public class RechargeWithdrawBean implements Serializable {
    public Data data;

    public class Data implements Serializable {
        public int count;
        public int page;
        public List<Records> records;
        public String totalCount;
        public String totalMoney;

        public class Records implements Serializable {
            public String payType;
            public String tradeNo;
            public float money;
            public String payWay;
            public String time;
            public String media;
            public int status;
        }
    }
}
