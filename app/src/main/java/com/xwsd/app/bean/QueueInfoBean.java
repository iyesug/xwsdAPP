package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gx on 2016/9/29.
 * 自动投标队列信息的实体类
 */
public class QueueInfoBean implements Serializable {

    public Data data;

    public static class Data implements Serializable {
        public String inQueue;
        public String location;
        public String thirdAccountAuth;
        public PreInfo preInfo;

        public static class PreInfo implements Serializable {
            public Double allMoney;
            public String invalidNum;
            public String validNum;
            public String invalidMoney;
            public String validMoney;
            public String allNum;

            public List<MonthNum> monthNums;

            public static class MonthNum implements Serializable {
                public String key;
                public String value;
            }

            public List<MonthMoney> monthMoneys;

            public static class MonthMoney implements Serializable {
                public String key;
                public String value;
            }
        }
    }
}