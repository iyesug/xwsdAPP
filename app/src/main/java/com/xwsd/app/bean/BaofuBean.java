package com.xwsd.app.bean;

import java.io.Serializable;

/**
 * Created by admin on 2016/12/1.
 */

public class BaofuBean  implements Serializable {
    public String msg;
    public int status;
    public Data data;
    public static class Data implements Serializable {
        public String retCode;
        public String tradeNo ;

    }

}
