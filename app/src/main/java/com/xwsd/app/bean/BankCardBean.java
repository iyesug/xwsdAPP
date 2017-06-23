package com.xwsd.app.bean;

import java.io.Serializable;

/**
 * Created by Gy on 2017/6/8.
 * 银行卡实体类
 */
public class BankCardBean implements Serializable {

    public Data data;




    public static class Data implements Serializable {


        public int lotteryCount;//	提现卷数量
        public String id;//	银行卡id
        public String bankNum;//	银行卡账号
        public int bank;//	对应银行名称的取值（暂时没有）
        public String bankIco;//	银行卡图标（暂时没有）
        public String binInfo;//	银行卡信息
    }
}