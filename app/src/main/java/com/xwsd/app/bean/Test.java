package com.xwsd.app.bean;

import java.util.List;

/**
 * Created by iyesug on 2017/7/13.
 */

public class Test {

    /**
     * status : 1
     * msg : 获取成功！
     * data : {"fundMoney":5089075,"autoInvest":{"autostatus":null,"investMoneyUper":"0","investMoneyLower":"0","investEgisMoney":"0","moneyType":null,"staystatus":null,"mode":0,"types":[]},"types":[{"name":"1月标(14%)","rate":0.14,"period":1,"periodType":"month","type":"diya","status":1,"id":11},{"name":"2月标(15%)","rate":0.15,"period":2,"periodType":"month","type":"diya","status":1,"id":12},{"name":"3月标(15.5%)","rate":0.155,"period":3,"periodType":"month","type":"diya","status":1,"id":13},{"name":"6月标(16%)","rate":0.16,"period":6,"periodType":"month","type":"diya","status":1,"id":14},{"name":"12月标(18%)","rate":0.18,"period":12,"periodType":"month","type":"diya","status":1,"id":15},{"name":"24月标(19%)","rate":0.19,"period":24,"periodType":"month","type":"diya","status":1,"id":16},{"name":"1月标(14%)","rate":0.14,"period":1,"periodType":"month","type":"xingyong","status":1,"id":21},{"name":"2月标(14.5%)","rate":0.145,"period":2,"periodType":"month","type":"xingyong","status":1,"id":23},{"name":"3月标(15%)","rate":0.15,"period":3,"periodType":"month","type":"xingyong","status":1,"id":22},{"name":"1月标(14%)","rate":0.14,"period":1,"periodType":"month","type":"danbao","status":1,"id":31},{"name":"2月标(15%)","rate":0.15,"period":2,"periodType":"month","type":"danbao","status":1,"id":32},{"name":"3月标(15.5%)","rate":0.155,"period":3,"periodType":"month","type":"danbao","status":1,"id":33},{"name":"6月标(16%)","rate":0.16,"period":6,"periodType":"month","type":"danbao","status":1,"id":34},{"name":"12月标(18%)","rate":0.18,"period":12,"periodType":"month","type":"danbao","status":1,"id":35},{"name":"24月标(19%)","rate":0.19,"period":24,"periodType":"month","type":"danbao","status":1,"id":36},{"name":"30周(16%)","rate":0.16,"period":30,"periodType":"week","type":"xiaojin","status":0,"id":41},{"name":"35周(16.5%)","rate":0.165,"period":35,"periodType":"week","type":"xiaojin","status":0,"id":42},{"name":"40周(17%)","rate":0.17,"period":40,"periodType":"week","type":"xiaojin","status":0,"id":43},{"name":"45周(17.5%)","rate":0.175,"period":45,"periodType":"week","type":"xiaojin","status":0,"id":44},{"name":"50周(18%)","rate":0.18,"period":50,"periodType":"week","type":"xiaojin","status":0,"id":45}]}
     */

    private int status;
    private String msg;
    private DataBean data;

    public static class DataBean {
        /**
         * fundMoney : 5089075
         * autoInvest : {"autostatus":null,"investMoneyUper":"0","investMoneyLower":"0","investEgisMoney":"0","moneyType":null,"staystatus":null,"mode":0,"types":[]}
         * types : [{"name":"1月标(14%)","rate":0.14,"period":1,"periodType":"month","type":"diya","status":1,"id":11},{"name":"2月标(15%)","rate":0.15,"period":2,"periodType":"month","type":"diya","status":1,"id":12},{"name":"3月标(15.5%)","rate":0.155,"period":3,"periodType":"month","type":"diya","status":1,"id":13},{"name":"6月标(16%)","rate":0.16,"period":6,"periodType":"month","type":"diya","status":1,"id":14},{"name":"12月标(18%)","rate":0.18,"period":12,"periodType":"month","type":"diya","status":1,"id":15},{"name":"24月标(19%)","rate":0.19,"period":24,"periodType":"month","type":"diya","status":1,"id":16},{"name":"1月标(14%)","rate":0.14,"period":1,"periodType":"month","type":"xingyong","status":1,"id":21},{"name":"2月标(14.5%)","rate":0.145,"period":2,"periodType":"month","type":"xingyong","status":1,"id":23},{"name":"3月标(15%)","rate":0.15,"period":3,"periodType":"month","type":"xingyong","status":1,"id":22},{"name":"1月标(14%)","rate":0.14,"period":1,"periodType":"month","type":"danbao","status":1,"id":31},{"name":"2月标(15%)","rate":0.15,"period":2,"periodType":"month","type":"danbao","status":1,"id":32},{"name":"3月标(15.5%)","rate":0.155,"period":3,"periodType":"month","type":"danbao","status":1,"id":33},{"name":"6月标(16%)","rate":0.16,"period":6,"periodType":"month","type":"danbao","status":1,"id":34},{"name":"12月标(18%)","rate":0.18,"period":12,"periodType":"month","type":"danbao","status":1,"id":35},{"name":"24月标(19%)","rate":0.19,"period":24,"periodType":"month","type":"danbao","status":1,"id":36},{"name":"30周(16%)","rate":0.16,"period":30,"periodType":"week","type":"xiaojin","status":0,"id":41},{"name":"35周(16.5%)","rate":0.165,"period":35,"periodType":"week","type":"xiaojin","status":0,"id":42},{"name":"40周(17%)","rate":0.17,"period":40,"periodType":"week","type":"xiaojin","status":0,"id":43},{"name":"45周(17.5%)","rate":0.175,"period":45,"periodType":"week","type":"xiaojin","status":0,"id":44},{"name":"50周(18%)","rate":0.18,"period":50,"periodType":"week","type":"xiaojin","status":0,"id":45}]
         */

        private int fundMoney;
        private AutoInvestBean autoInvest;
        private List<TypesBean> types;


        public static class AutoInvestBean {
            /**
             * autostatus : null
             * investMoneyUper : 0
             * investMoneyLower : 0
             * investEgisMoney : 0
             * moneyType : null
             * staystatus : null
             * mode : 0
             * types : []
             */

            private Object autostatus;
            private String investMoneyUper;
            private String investMoneyLower;
            private String investEgisMoney;
            private Object moneyType;
            private Object staystatus;
            private int mode;
            private List<?> types;

        }

        public static class TypesBean {
            /**
             * name : 1月标(14%)
             * rate : 0.14
             * period : 1
             * periodType : month
             * type : diya
             * status : 1
             * id : 11
             */

            private String name;
            private double rate;
            private int period;
            private String periodType;
            private String type;
            private int status;
            private int id;

        }
    }
}
