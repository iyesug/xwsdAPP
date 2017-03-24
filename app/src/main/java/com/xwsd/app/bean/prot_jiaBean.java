package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mo on 2016/11/28.
 */

public class prot_jiaBean implements Serializable {
    public Data data;

    public static class Data implements Serializable {
        public String count;
        public String page;
        public List<PreInfo> records;

        public static class PreInfo implements Serializable {
            public String id;
            public String name;
            public String type;
            public String money;
            public String endtime;
        }
    }
}
