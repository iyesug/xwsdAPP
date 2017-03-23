package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gx on 2016/9/6.
 * 银行卡列表实体类
 */
public class BankCardsBean implements Serializable {

    public Data data;


    public static class records implements Serializable {
        public String noAgree;
        public String bankNum;
        public int bank;
        public String isDefault;
        public String allowDel;
        public int province;
        public int city;
        public String bankUsername;
        public String id;
        public String subbranch;
        public String bankName;
        public String bankIco;
    }

    public static class Data implements Serializable {
        public int lotteryCount;
        public List<records> records;
    }
}