package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mo on 2016/11/28.
 */

public class FundRecordBean {
    public Data data;

    public class   Data implements Serializable {
        public String link;
        public String page;
        public String count;
        public List<records> records;
        public List<types> types;

        public class  records implements Serializable {
            public String type;
            public String mode;
            public String money;
            public String time;
        }

        public class types implements Serializable{
            public String id;
            public String content;
        }
    }
}
