package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gx on 2016/9/18.
 * 自动投标设置页面的实体类
 */
public class AutoInfoBean implements Serializable {

    public Data data;

    public static class Data implements Serializable {
        public String fundMoney;

        public List<Types> types;

        public static class Types implements Serializable {
            public String name;
            public List<BidList> list;

            public class BidList implements Serializable {
                public int month;
                public Double rate;
                public String name;
                public String id;
                public String type;
                public boolean selectType;
            }
        }

        public AutoInvest autoInvest;

        public static class AutoInvest implements Serializable {
            public int autostatus;
            public int moneyType;
            public int staystatus;
            public String lotteryID;//	当前使用的红包ID，没有用为0
            public String lotteryName;
            public String investMoneyUper;
            public String investMoneyLower;
            public String investEgisMoney;
            public List<String> types;
            public String mode;

        }
    }
}