package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gx on 2016/9/14.
 * 风险控制的实体类
 */
public class OddrmBean implements Serializable {

    public Data data;

    public static class Data implements Serializable {

        public String oddNumber;

        public List<OddrmImg> controlPhotos;

        public List<OddrmImg> oddLoanPhotos;

        public List<OddrmImg> oddPropertyPhotos;

        public List<OddrmImg> validateCarPhotos;

        public List<String> controlVars;

        public List<OddrmImg> bankCreditReport;

        public String oddLoanControl;

        public static class OddrmImg implements Serializable {
            public String normal;
            public String min;
            public String max;
        }
    }
}