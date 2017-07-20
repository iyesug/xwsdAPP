package com.xwsd.app.bean;

import java.io.Serializable;

/**
 * Created by Gx on 2016/9/22.
 * 资产明细的实体类
 */
public class UserAccountBean implements Serializable {

    public Data data;

    public static class Data implements Serializable {
        public String transferMoney;
        public String tenderAll;
        public String stayAll;
        public String transferInterest;
        public String fundMoney;
        public String frozenMoney;
        public String realInterest;
        public String allMoney;
        public String hasInterest;
        public String actLot;
        public String actMoney;
        public String crtrInterest;
        public String integral;
        public String stayInterest;
        public String crtrMoney;
        public String stayPrincipal;
        public String serviceMoney;
        public String freeWithdraw;

        public String spreadMoney;
        public String lastSpreadMoney;
        public Lotteries lotteries ;

        public static class Lotteries implements Serializable {
            public String interest;
            public String withdraw;
            public String invest_money;
        }

    }
}