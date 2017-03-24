package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gx on 2016/9/23.
 * 回款日历的实体类
 */
public class RepaymentsBean implements Serializable {

    public Data data;

    public static class Data implements Serializable {
        public String allMoney;
        public String benJin;
        public String interest;
        public List<String> stayDays;

        public List<Repayments> repayments;

        public  static class Repayments implements Serializable {
            public String oughtMoney;
            public String interest;
            public String benJin;


            public String realMoney;
            public String realBenjin;
            public String realInterest;

            public String serviceMoney;
            public String status;
            public List<Records> odds;

            public static class Records implements Serializable {
                public int schedule;
                public String oddNumber;
                public String oddMoney;
                public String addtime;
                public String oddPeriod;
                public float oddYearRate;
                public float oddReward;
                public String money;
                public int status;
                public String oddTitle;
            }
        }
    }
}
//        implements Serializable {
//
//    public Data data;
//
//    public static class Data implements Serializable {
//        public String allMoney;
//        public List<StayDays> stayDays;
//
//        public static class StayDays {
//            public String time;
//        }
//
//        public List<Repayments> repayments;
//
//        public static class Repayments implements Serializable {
//            public String oughtMoney;
//            public String interest;
//            public String benJin;
//            public String realMoney;
//            public String serviceMoney;
//            public String status;
//            public List<Records> odds;
//
//            public static class Records implements Serializable {
//                public int schedule;
//                public String oddNumber;
//                public String oddMoney;
//                public String addtime;
//                public String oddPeriod;
//                public float oddYearRate;
//                public float oddReward;
//                public String oddTitle;
//            }
//        }
//    }
//}