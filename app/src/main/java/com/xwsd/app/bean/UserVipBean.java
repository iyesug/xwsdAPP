package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gx on 2016/9/28.
 * Vip的实体类
 */
public class UserVipBean implements Serializable {

    public Data data;

    public static class Data implements Serializable {
        public int isVip;
        public String customService;
        public String endTime;

        public List<CustomServices> customServices;

        public static class CustomServices implements Serializable {
            public String name;
            public String id;
        }

        public List<Times> times;

        public static class Times implements Serializable {
            public String name;
            public String id;
        }
    }
}