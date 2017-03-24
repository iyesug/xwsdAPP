package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mo on 2016/11/25.
 */

public class InvestRecordNowBean implements Serializable {
    public Data data;

    public static class Data implements Serializable {
        public int count;
        public int page;
        public List<Records> records;

        public static class Records implements Serializable {
            public String money;
            public String time;
            public String lastTime;
            public int key;
            public String username;
        }
    }
}
