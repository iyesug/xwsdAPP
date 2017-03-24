package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gx on 2016/9/21.
 * 待收/回款项目的实体类
 */
public class DueReturnedProjectBean {

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
            public float money;
            public String interest;
            public String time;
            public String endtime;
            public String lotteryId;
        }
    }
}
