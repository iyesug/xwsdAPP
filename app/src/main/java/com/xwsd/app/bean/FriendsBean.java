package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mo on 2016/11/24.
 */

public class FriendsBean implements Serializable{
    public Data data;

    public class   Data implements Serializable {
        public String link;
        public String  spreadMoney;
        public String lastSpreadMoney;
        public List<records> records;
        public int count;

        public class  records implements Serializable {
            public String friend;
            public String username;
            public String money;
            public String level;
            public String time;

        }
    }
}
