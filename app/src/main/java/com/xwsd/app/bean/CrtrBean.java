package com.xwsd.app.bean;

import java.io.Serializable;

/**
 * Created by Gx on 2016/9/20.
 * 债权转让详情的实体类
 */
public class CrtrBean implements Serializable {

    public Data data;

    public static class Data implements Serializable {
        public int buyCount;
        public int remainDay;
        public String endtime;
        public float oddYearRate;
        public String title;
        public float moneyLast;
        public float money;
        public String addtime;
        public String oddNumber;
        public float remainInterest;
        public String progress;
        public String id;
        public String oddReward;
        public float oddMoneyLast;
        public String oddRepaymentStyle;
        public String schedule;	//投标进度
        public String ingCount;	//在买数量
        public String investedInterest;	//未结利息
    }
}