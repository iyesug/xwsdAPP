package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gx on 2016/9/6.
 * 项目-标-详情的实体类
 */
public class OddBean implements Serializable {
    public Data data;

    public static class Data implements Serializable {
        public String oddRepayType;
        public String oddUse;
        public String oddType;
        public String oddPeriod;
        public float oddYearRate;
        public String oddTitle;
        public int second;
        public String oddGarageNum;
        public float schedule;
        public String oddNumber;
        public String oddMoney;
        public String addtime;
        public String progress;
        public float oddReward;
        public String openTime;
        public String oddStyle;
        public float oddMoneyLast;
        public User1 user1;

        public static class User1 implements Serializable {
            public String income;
            public String marital;
            public String sex;
            public String age;
            public String username;
        }

        public User2 user2;

        public static class User2 implements Serializable {
            public String borrowMoney;
            public String borrowCount;
            public String successCount;
            public String endCount;
            public String stayMoney;
        }

        public OddLoanRemark oddLoanRemark;

        public static class OddLoanRemark implements Serializable {
            public String 排量;
            public String 车身颜色;
            public String 行驶公里数;
            public String 购买价格;
            public String 抵押估价;
            public String 车辆品牌型号;
        }

        public List<OddExteriorPhotos> oddExteriorPhotos;

        public static class OddExteriorPhotos implements Serializable{
            public String normal;
            public String min;
            public String max;
        }

        public User3 user3;

        public static class User3 implements Serializable {
            public String borrowMoney;
            public String stayMoney;
            public String borrowOut;
        }
    }
}