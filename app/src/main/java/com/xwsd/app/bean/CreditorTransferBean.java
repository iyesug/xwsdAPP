package com.xwsd.app.bean;

import java.io.PushbackInputStream;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Gx on 2016/9/21.
 * 债权转让的实体类
 */
public class CreditorTransferBean {

    public Data data;

    public static class Data implements Serializable {
        public int count;
        public int page;
        public List<Records> records;

        public static class Records implements Serializable {
            public String id;
            public String oddNumber;
            public String oddTitle;
            public String oddMoney;
            public String oddPeriod;
            public String money;
            public String interest;
            public String time;
            public String endtime;
            public float oddYearRate;
            public String remainDay;
            public String crtrId;
            public String lotteryId;
            public float crtrSM;
            public float remain;
        }
    }
}
