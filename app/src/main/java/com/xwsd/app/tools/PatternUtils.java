package com.xwsd.app.tools;

import java.util.regex.Pattern;

/**
 * 字符串格式判断工具
 */
public class PatternUtils {

    /**
     * 判断手机号码
     *
     * @param phoneNum 手机号码
     * @return true 为手机格式， false 非手机格式
     */
    public static boolean matchesPhone(String phoneNum) {
        if (Pattern.matches("\\d{11,11}$", phoneNum)) {
            return true;
        }
        return false;
    }

    /**
     * 判断密码长度是否大于6位，小于16位
     *
     * @param str 要判断的字符串
     * @return true 符合指定长度， false 不符合指定长度
     */
    public static boolean matchesNum(String str) {
        if (str.length() >= 6 && str.length() <= 16) {
            return true;
        }
        return false;
    }


    /**
     * 判断密码长度是否大于6位，小于16位
     *
     * @param str 要判断的字符串
     * @return true 符合指定长度， false 不符合指定长度
     */
    public static boolean matchesNum(String str, int min, int max) {
        if (str.length() >= min && str.length() <= max) {
            return true;
        }
        return false;
    }

    /**
     * 判断电子邮箱格式
     *
     * @param email 需要判断的邮箱
     * @return true 格式正确， false 格式错误
     */
    public static boolean matchesEmail(String email) {
        if (Pattern
                .matches(
                        "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$", email)) {
            return true;
        }
        return false;
    }
}
