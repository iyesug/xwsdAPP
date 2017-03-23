package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gx on 2016/9/5.
 * 首页的实体类
 */
public class IndexBean implements Serializable {

    public Data data;

    public static class Data implements Serializable {
        public String todayLast;
        public String allVolume;
        public String allInterest;
        public List<Notices> notices;

        public static class Notices implements Serializable {
            public String id;
            public String news_title;
        }

        public List<Odds> odds;

        public List<Odds> newHandOdds;

        public List<Banners> banners;

        public static class Banners {
            public String id;
            public String title;
            public String link;
            public String banner;
        }
    }
}
