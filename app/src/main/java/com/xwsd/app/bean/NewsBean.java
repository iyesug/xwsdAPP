package com.xwsd.app.bean;

import java.io.Serializable;

/**
 * Created by Gx on 2016/9/25.
 * 资讯详情的实体类
 */
public class NewsBean {

    public Records data;

    public static class Records implements Serializable {
        public String id;
        public String time;
        public String title;
        public String click;
        public String content;
        public String answer;
    }
}
