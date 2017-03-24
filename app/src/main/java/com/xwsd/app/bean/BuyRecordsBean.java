package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gx on 2016/9/20.
 * 债权详情-购买记录的实体类
 */
public class BuyRecordsBean implements Serializable {

    public Data data;

    public static class Data implements Serializable {
        public int count;
        public int page;
        public List<Records> records;

        public static class Records implements Serializable {
            public String normalKey;
            public String money;
            public String time;
            public String username;
            public String lastTime;
        }
    }
}