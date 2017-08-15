package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Gy on 2017/8/14.
 * 资金迁移记录的实体类
 *
 *
 *
 status	返回为1为成功，其他为失败
 msg	失败时内容为错误说明
 data.records[0].id	ID
 data.records[0].money	迁移金额
 data.records[0].addTime	申请时间
 data.records[0].status	0申请中 1成功 2失败
 data.count	记录总数
 data.page	当前页面
 */
public class MoneyTransferRecordBean implements Serializable {
    public Data data;

    public class Data implements Serializable {
        public int count;
        public int page;
        public List<Records> records;


        public class Records implements Serializable {
            public String id;
            public float money;
            public String addTime;
            public int status;
        }
    }
}
