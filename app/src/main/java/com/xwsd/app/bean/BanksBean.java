package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gx on 2016/9/7.
 * 银行列表的实体类
 */
public class BanksBean implements Serializable {
    public Data data;

    public static class Data implements Serializable {
        public List<Records> records;

        public static class Records implements Serializable {
            public String name;
            public int key;
        }
    }
}