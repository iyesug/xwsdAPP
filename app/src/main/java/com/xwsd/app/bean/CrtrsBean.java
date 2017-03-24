package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gx on 2016/9/20.
 * 债权转让列表的实体类
 */
public class CrtrsBean implements Serializable {

    public Data data;

    public static class Data implements Serializable {
        public int count;
        public int page;
        public List<Records> records;

        public static class Records implements Serializable {
            public float money;
            public float moneyLast;
            public String oddNumber;
            public float remainInterest;
            public String remainDay;
            public String progress;
            public float oddYearRate;
            public String id;
            public String oddReward;
            public String time;
            public String title;
            public int schedule;
        }
    }
}