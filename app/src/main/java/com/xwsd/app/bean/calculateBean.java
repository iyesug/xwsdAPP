package com.xwsd.app.bean;

import java.io.Serializable;

/**
 * Created by admin on 2016/12/23.
 */

public class calculateBean implements Serializable {

    public Data data;
    public static class Data implements Serializable {
        public String interest;
    }
}
