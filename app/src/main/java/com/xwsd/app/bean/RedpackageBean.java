package com.xwsd.app.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gy on 2017/7/5.
 *
 * 参数	说明
 status	返回为1为成功，其他为失败
 msg	失败时内容为错误说明
 data.records[0].id	抵扣红包ID
 data.records[0].name	名称
 data.records[0].period_lower	最高期限 null为不限
 data.records[0].period_uper	最低期限 null为不限
 data.records[0].money_lower	最低金额 null为不限
 data.records[0].money_uper	最高金额 null为不限
 data.records[0].money_rate	面值
 data.records[0].endtime	过期时间
 data.count	记录总数
 data.page	当前页面
 */

public class RedpackageBean implements Serializable {
    public Data data;

    public static class Data implements Serializable {
        public String count;
        public String page;
        public List<records> records;

        public static class records implements Serializable {
            public String id;
            public String name;
            public String period_lower;
            public String period_uper;
            public String money_lower;
            public String money_uper;
            public String money_rate;
            public String endtime;
        }
    }
}
