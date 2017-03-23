package com.xwsd.app.bean;

import java.io.Serializable;

/**
 * Created by Gx on 2016/9/7.
 * 认证的银行卡实体类
 */
public class AgreeCardBean implements Serializable {
    public Data data;

    public static class Data implements Serializable {
        public AgreeCard agreeCard;

        public static class AgreeCard implements Serializable {
            public String bank_code;
            public String card_no;
            public String no_agree;
            public String bank_name;
            public int card_type;
            public String card_num;
        }
    }
}