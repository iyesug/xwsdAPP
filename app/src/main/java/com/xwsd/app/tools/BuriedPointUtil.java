package com.xwsd.app.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import com.xwsd.app.AppContext;

import java.io.*;
import java.util.Map;

/**
 * 埋点处理的单例
 * Created by Lj on 2016/11/2.
 */

public class BuriedPointUtil {

    // private static SharePreferUtil instance;
    private static final BuriedPointUtil instance = new BuriedPointUtil();

    public static SharedPreferences sp;
    public static SharedPreferences.Editor ed;
    private final static String name = "BuriedPoint";// 表名

    static {
        sp = AppContext.context().getSharedPreferences(name, Context.MODE_PRIVATE);
        ed = sp.edit();
    }

    /**
     * 私有的默认构造函数
     */
    private BuriedPointUtil() {
        super();
    }

    /**
     * 静态工厂方法
     */
    public static BuriedPointUtil getInstance() {

        return instance;
    }


    /**
     * @param key
     * @param value
     * @return 添加成功返回true，否则false
     * @Description: 添加boolean
     * @author liliwei
     * @create 2013-8-20 下午4:49:07
     * @updateTime 2013-8-20 下午4:49:07
     */
    public static boolean putBoolean(String key, boolean value) {
        try {
            ed.putBoolean(key, value);
            ed.commit();

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }

    /**
     * @param key
     * @param value
     * @return
     * @Description: TODO
     * @author liliwei
     * @create 2013-8-20 下午4:55:51
     * @updateTime 2013-8-20 下午4:55:51
     */
    public static boolean getBoolean(String key, boolean value) {
        return sp.getBoolean(key, value);
    }

    public static boolean putFloat(String key, float value) {
        try {
            ed.putFloat(key, value);
            ed.commit();

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;

    }

    public static float getFloat(String key, float value) {
        return sp.getFloat(key, 0f);

    }

    public static boolean putInt(String key, int value) {
        try {

            ed.putInt(key, value);
            ed.commit();
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;

    }

    public static int getInt(String key, int value) {
        return sp.getInt(key, value);

    }

    public static boolean putLong(String key, Long value) {
        try {
            ed.putFloat(key, value);
            ed.commit();
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;

    }

    public static long getLong(String key, Long value) {
        return sp.getLong(key, value);

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

    public static void removeShare(String key) {
        ed.remove(key);
        ed.commit();
    }

    /**
     * 清空sharePreference里面的所有数据
     */
    public static void clearData() {
        ed.clear().commit();
    }

    /**
     * 判断sp是否存在名字为key的键
     *
     * @param key
     * @return
     */
    public static boolean hasKey(String key) {
        return sp.contains(key);
    }

    public static Map<String, ?> getAll() {
        return sp.getAll();
    }


    /**
     * 埋点方法（计算各种按键被按的次数或其它事件监听其次数）
     *
     * @param key 生成埋点的键值
     */
    public static void buriedPoint(String key) {

        //埋点开始
        if (!hasKey(key)) {
            BuriedPointUtil.putInt(key, 1);
        } else {
            int i = BuriedPointUtil.getInt(key, 1);
            i++;
            BuriedPointUtil.putInt(key, i);
        }
        //埋点结束

    }


    /**
     * 针对复杂类型存储<对象>
     *
     * @param key
     * @param object
     */
    public static void setObject(String key, Object object) {


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {

            out = new ObjectOutputStream(baos);
            out.writeObject(object);
            String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, objectVal);
            editor.commit();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getObject(String key, Class<T> clazz) {

        if (sp.contains(key)) {
            String objectVal = sp.getString(key, null);
            byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bais);
                T t = (T) ois.readObject();
                return t;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }
                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


}
