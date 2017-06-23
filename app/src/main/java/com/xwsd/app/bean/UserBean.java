package com.xwsd.app.bean;

import java.io.Serializable;

/**
 * Created by Gx on 2016/8/31.
 * 用户的实体类
 */
public class UserBean implements Serializable {

    public Data data;

    public class Data implements Serializable {

        public String userimg;
        public String payPassStatus;
        public String fundMoney;
        public String frozenMoney;
        public String tenderMoney;
        public String userName;
        public String userId;
        public int thirdAccountStatus;
        public String hasInterest;
        public String cardnum;
        public String cardstatus;
        public String integral;
        public String name;
        public int thirdAccountAuth;
        public String stayAllMoney;
        public String  emailstatus;
        public String  email;
        public String  phone;
        public String  sex;
        public String  city;
        public String  maritalstatus;
        public String  userSecret;
        //存管新增：
        public String  custodyId;//存管账号
        public String  autoBidAuth;//自动投标签约单号
        public String  autoCreditAuth;//自动债转签约单号
        public String  custodyPwd;//是否设置存管密码 0 未设置 1未设置

    }
}
