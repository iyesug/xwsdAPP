package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gx on 2016/9/5.
 * 项目-标-列表-实体类
 */
public class OddsBean implements Serializable {

    public Data data;

    public static class Data implements Serializable {
        public int count;
        public int page;
        public List<Odds> records;
    }

    public List<Periods> mperiods;
    public List<Types> mtypes;

    public static class Periods implements Serializable {
        public String id;
        public String content;
    }

    public static class Types implements Serializable {
        public String id;
        public String content;
    }
}
