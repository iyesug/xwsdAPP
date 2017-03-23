package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gx on 2016/9/21.
 * 新闻列表的实体类
 */
public class InfosBean implements Serializable {

    public Data data;

    public static class Data implements Serializable {
        public int count;
        public int page;
        public List<Records> records;

        public static class Records implements Serializable {
            public String id;
            public String time;
            public String title;
            public String click;
            public String content;
            public String answer;
        }
    }
}