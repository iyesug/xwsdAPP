package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin on 2016/11/30.
 */

public class ProtDetilabean implements Serializable {
    public Data data;

    public static class Data implements Serializable {
        public String count;
        public String page;
        public List<PreInfo> records;

        public static class PreInfo implements Serializable {
            public String qishu;
            public String endtime;
            public String amount;
            public String realMoney;
            public String serviceMoney;
            public String extra;
            public String subsidy;
            public String status;
        }
    }
}
