package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by iyesug on 2017/06/29.
 * status	返回为1为成功，其他为失败
 msg	失败时内容为错误说明
 data.id	银行卡ID [用户没有绑定卡时该值为0]
 data.bankNum	银行卡号
 data.bank	银行编号 0
 data.binInfo	银行卡信息
 data.bankIco	银行图标
 data.limitList[0].bankName	银行名称
 data.limitList[0].limit	限额
 */

public class BankListBean {
    public Data data;

    public class   Data implements Serializable {
        public String id;
        public String bankNum;
        public String binInfo;
        public String bankIco;
        public List<limitList> limitList;


        public class  limitList implements Serializable {
            public String bankName;
            public String limit;

        }


    }
}
