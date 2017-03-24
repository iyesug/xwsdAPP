package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gx on 2016/9/13.
 * 标的详情-投资记录-实体类
 */
public class InvestRecordBean implements Serializable {
    public Data data;

    public static class Data implements Serializable {
        public int count;
        public int page;
        public List<Records> records;

        public  static class Records implements Serializable {
            public String money;
            public String time;
            public String media;
            public String autoOrder;
            public int key;
            public String username;
            public String bidType;
        }
    }
}
