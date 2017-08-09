package com.xwsd.app.tools;/**
 * Created by Administrator on 2017/3/31.
 */

import android.graphics.Point;
import android.os.SystemClock;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 作者:LiuYF on 2017/3/31 16:34
 */

public class Helper {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String RANDOMSEED = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public Helper() {
    }

    /**
     * 获取时间
     * @param dateStr 时间
     * @return 格式化后的时间
     */
    public static Date string2Date(String dateStr) {
        return string2Date(dateStr, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date string2Date(String dateStr, String dateFormat) {
        Date result = null;

        try {
            SimpleDateFormat e = new SimpleDateFormat(dateFormat);
            result = e.parse(dateStr);
        } catch (Exception var4) {
            result = null;
        }

        return result;
    }

    public static String date2String() {
        return date2String(new Date());
    }

    public static String date2String(String dateFormat) {
        return date2String(new Date(), dateFormat);
    }

    public static String date2String(Date date) {
        return date2String(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String date2String(Date date, String dateFormat) {
        String dateStr = "";

        try {
            SimpleDateFormat e = new SimpleDateFormat(dateFormat);
            dateStr = e.format(date);
        } catch (Exception var4) {
            dateStr = "";
        }

        return dateStr;
    }

    public static long date2Long(Date date) {
        return date.getTime();
    }

    public static long date2Long() {
        return (new Date()).getTime();
    }

    public static String date2LongString() {
        return String.valueOf(date2Long());
    }

    public static Date long2Date(long ms) {
        return new Date(ms);
    }

    public static String long2DateString(long ms) {
        return long2DateString(ms, "yyyy-MM-dd HH:mm:ss");
    }

    public static String long2DateString(long ms, String dateFormat) {
        String result = null;
        Date date = long2Date(ms);
        if(isNotEmpty(date)) {
            result = date2String(date, dateFormat);
        }

        if(isNull(result)) {
            result = String.valueOf(ms);
        }

        return result;
    }

    public static long dateString2Long(String dateString) {
        return dateString2Long(dateString, "yyyy-MM-dd HH:mm:ss");
    }

    public static long dateString2Long(String dateString, String dateFormat) {
        Date date = string2Date(dateString, dateFormat);
        return isNotNull(date)?date2Long(date):0L;
    }

    public static boolean string2Boolean(String booleanStr) {
        return booleanStr == null?false:booleanStr.equals("true");
    }

    public static String boolean2String(boolean bool) {
        return bool?"true":"false";
    }

    public static boolean isTrue(String booleanStr) {
        return isEmpty(booleanStr)?false:"true".equals(booleanStr.trim());
    }

    public static int toInt(String data) {
        boolean result = false;

        int result1;
        try {
            result1 = Integer.valueOf(data).intValue();
        } catch (Exception var3) {
            result1 = -2147483648;
        }

        return result1;
    }

    public static int toInt(String data, int defaultValue) {
        boolean result = false;

        int result1;
        try {
            result1 = Integer.valueOf(data).intValue();
        } catch (Exception var4) {
            result1 = defaultValue;
        }

        return result1;
    }

    public static long toLong(String data) {
        long result = 0L;

        try {
            result = Long.valueOf(data).longValue();
        } catch (Exception var4) {
            result = -9223372036854775808L;
        }

        return result;
    }

    public static float toFloat(String data) {
        float result = 0.0F;

        try {
            result = Float.valueOf(data).floatValue();
        } catch (Exception var3) {
            result = 1.4E-45F;
        }

        return result;
    }

    public static double toDouble(String data) {
        double result = 0.0D;

        try {
            result = Double.valueOf(data).doubleValue();
        } catch (Exception var4) {
            result = 4.9E-324D;
        }

        return result;
    }

    public static boolean isEmpty(Object object) {
        boolean result = false;
        if(object == null) {
            result = true;
        } else if(object instanceof String) {
            result = ((String)object).equals("");
        } else if(object instanceof Date) {
            result = ((Date)object).getTime() == 0L;
        } else if(object instanceof Long) {
            result = ((Long)object).longValue() == -9223372036854775808L;
        } else if(object instanceof Integer) {
            result = ((Integer)object).intValue() == -2147483648;
        } else if(object instanceof Collection) {
            result = ((Collection)object).size() == 0;
        } else if(object instanceof Map) {
            result = ((Map)object).size() == 0;
        } else if(object instanceof JSONObject) {
            result = !((JSONObject)object).keys().hasNext();
        } else {
            result = object.toString().equals("");
        }

        return result;
    }

    public static boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    public static String ifNull(Object obj) {
        return ifNull(obj, "");
    }

    public static String ifNull(Object obj, String defaultValue) {
        return isNull(obj)?defaultValue:obj.toString();
    }

    public static int float2Int(float f) {
        return Math.round(f);
    }

    public static int double2Int(double d) {
        return Long.valueOf(Math.round(d)).intValue();
    }

    public static String set2String(Object obj) {
        String result = "";
        if(isNotEmpty(obj)) {
            if(obj instanceof Collection) {
                Object[] objArray = ((Collection)obj).toArray();
                Object[] var3 = objArray;
                int var4 = objArray.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    Object tempObj = var3[var5];
                    if(!isEmpty(tempObj)) {
                        result = result + "," + tempObj.toString();
                    }
                }

                if(result.length() > 0) {
                    result = result.substring(1);
                }
            } else {
                result = obj.toString();
            }
        }

        return result;
    }

    public static <T> List<T> array2List(T[] array) {
        ArrayList result = new ArrayList();
        if(!isEmpty(array)) {
            Object[] var2 = array;
            int var3 = array.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Object entity = var2[var4];
                result.add(entity);
            }
        }

        return result;
    }

    public static Point calcRealPoint(Point srcPoint, Point centerPoint, int degree, boolean isZeroPointAtTop) {
        Point result = new Point();
        double r = Math.hypot((double)(srcPoint.x - centerPoint.x), (double)(srcPoint.y - centerPoint.y));
        double beta = calcAngle(srcPoint, centerPoint, isZeroPointAtTop);
        beta += (double)degree;
        double desY = Math.abs(Math.sin(beta * 3.141592653589793D / 180.0D) * r);
        double desX = Math.abs(Math.sqrt(r * r - desY * desY));
        beta %= 360.0D;
        if(90.0D < beta && beta < 270.0D) {
            desX = -1.0D * desX;
        }

        if(0.0D <= beta && beta <= 180.0D) {
            desY = isZeroPointAtTop?-desY:desY;
        } else {
            desY = isZeroPointAtTop?desY:-desY;
        }

        result.set(double2Int(desX + (double)centerPoint.x), double2Int(desY + (double)centerPoint.y));
        return result;
    }

    public static double calcAngle(Point srcPoint, Point centerPoint, boolean isZeroPointAtTop) {
        Point srcTempPoint = new Point(srcPoint.x - centerPoint.x, isZeroPointAtTop?centerPoint.y - srcPoint.y:srcPoint.y - centerPoint.y);
        double r = Math.hypot((double)srcTempPoint.x, (double)srcTempPoint.y);
        double beta = 0.0D;
        if(srcTempPoint.x >= 0 && srcTempPoint.y >= 0) {
            beta = (double)Math.round(Math.acos((double)srcTempPoint.x / r) * 180.0D / 3.141592653589793D);
        } else if(srcTempPoint.x < 0 && srcTempPoint.y >= 0) {
            beta = (double)(180L - Math.round(Math.asin((double)srcTempPoint.y / r) * 180.0D / 3.141592653589793D));
        } else if(srcTempPoint.x < 0 && srcTempPoint.y < 0) {
            beta = (double)(180L - Math.round(Math.asin((double)srcTempPoint.y / r) * 180.0D / 3.141592653589793D));
        } else if(srcTempPoint.x >= 0 && srcTempPoint.y < 0) {
            beta = (double)(Math.round(Math.asin((double)srcTempPoint.y / r) * 180.0D / 3.141592653589793D) + 360L);
        }

        return beta;
    }

    public static int createIntTag() {
        boolean result = true;

        int result1;
        try {
            result1 = Long.valueOf(SystemClock.currentThreadTimeMillis() % 2147483647L).intValue();
        } catch (Exception var2) {
            result1 = -1;
        }

        return result1;
    }

    public static String getFileName(String path, boolean ignorExtention) {
        String result = path;
        if(isNotEmpty(path)) {
            int startIndex = 0;
            if(path.contains("\\")) {
                startIndex = path.lastIndexOf("\\") + 1;
            } else if(path.contains("/")) {
                startIndex = path.lastIndexOf("/") + 1;
            }

            int endIndex = ignorExtention?path.lastIndexOf("."):path.length();
            result = path.substring(startIndex, endIndex);
        }

        return result;
    }

    public static String getFileName(File file) {
        String result = "";

        try {
            result = file.getName().substring(0, file.getName().lastIndexOf("."));
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return result;
    }

    public static String getFileName(String path) {
        return getFileName(path, true);
    }

    public static String getFileExtension(File file) {
        String result = "";

        try {
            result = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        return result;
    }

    public static String getFileExtension(String path) {
        return isNotEmpty(path)?(path.contains(".")?path.substring(path.lastIndexOf(".") + 1):path):"";
    }


    public static byte[] getBytesFromFile(File file) {
        if(file == null) {
            return null;
        } else {
            FileInputStream stream = null;
            ByteArrayOutputStream out = null;

            try {
                stream = new FileInputStream(file);
                out = new ByteArrayOutputStream(1000);
                byte[] e = new byte[1000];

                int n;
                while((n = stream.read(e)) != -1) {
                    out.write(e, 0, n);
                }

                byte[] var5 = out.toByteArray();
                return var5;
            } catch (IOException var19) {
                var19.printStackTrace();
            } finally {
                if(isNotNull(stream)) {
                    try {
                        stream.close();
                    } catch (IOException var18) {
                        var18.printStackTrace();
                    }
                }

                if(isNotNull(out)) {
                    try {
                        out.close();
                    } catch (IOException var17) {
                        var17.printStackTrace();
                    }
                }

            }

            return null;
        }
    }

    public static boolean equalString(String str1, String str2, boolean ignorSpace) {
        return equalString(str1, str2, ignorSpace, false);
    }

    public static boolean equalString(String str1, String str2, boolean ignorSpace, boolean ignorCase) {
        return isNull(str1) && isNull(str2)?true:(isNull(str1) && isNotNull(str2) || isNull(str2) && isNotNull(str1)?false:(ignorSpace?(ignorCase?str1.trim().toLowerCase().equals(str2.trim().toLowerCase()):str1.trim().equals(str2.trim())):(ignorCase?str1.toLowerCase().equals(str2.toLowerCase()):str1.equals(str2))));
    }

    public static String createRandomString(int length) {
        StringBuffer result = new StringBuffer();
        if(length > 0) {
            Random random = new Random();
            int seedLength = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".length();

            for(int i = 0; i < length; ++i) {
                result.append("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".charAt(random.nextInt(seedLength)));
            }
        }

        return result.toString();
    }

    public static int getRandomNum(int maxNum) {
        return maxNum < 0?0:(new Random()).nextInt(maxNum);
    }
}
