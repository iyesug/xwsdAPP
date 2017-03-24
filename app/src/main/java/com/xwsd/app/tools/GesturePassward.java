package com.xwsd.app.tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.xwsd.app.AppContext;

/**
 * 用来存储手势密码的SharedPreferences
 * Created by Lj on 2016/11/15.
 */

public class GesturePassward {

    // private static SharePreferUtil instance;
    private static final GesturePassward instance = new GesturePassward();

    public static SharedPreferences sp;
    public static SharedPreferences.Editor ed;
    private final static String name = "gesturePassward";// 表名

    static {
        sp = AppContext.context().getSharedPreferences(name, Context.MODE_PRIVATE);
        ed = sp.edit();
    }

    /**
     * 私有的默认构造函数
     */
    private GesturePassward() {
        super();
    }

    /**
     * 静态工厂方法
     */
    public static GesturePassward getInstance() {
        return instance;
    }

    public static boolean putString(String key, String value) {
        try {
            ed.putString(key, value);
            ed.commit();
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;

    }

    /**
     * @param key
     * @param value 默认
     * @return
     */
    public static String getString(String key, String value) {
        return sp.getString(key, value);

    }

}
