package com.xwsd.app.api;

/**
 * Created by Gx on 2016/8/31.
 * 小微时贷请求地址
 */
public interface XWSDRequestAdresse {

    /**
     * 主机地址
     */
    String HOST = "www.xwsd.com";

    /**
     * 接口根地址
     */
    String API_URL = "https://app.xwsd.com/api/";//正式
    // String API_URL = "http://app.xwsdvip.com/api/";//测试

    // String API_URL = "http://app.xwshidai.com/api/";//生产

    /**
     * 签名密钥
     */
    String AUTH_KEY = "abcdeft12345";

    /**
     * 注册
     */
    String REGISTER = API_URL + "register";

    /**
     * 发送验证码
     */
    String SMS = API_URL + "sms";

    /**
     * 登录
     */
    String LOGIN = API_URL + "login";

    /**
     * 修改登录密码
     */
    String UPDATE_PASSWORD = API_URL + "updatePassword";

   /**
    * 修改手机号【存管】
    */
   String UPDATE_PHONE = API_URL + "updatePhone";


   /**
    * 修改登录密码【存管】
    */
   String UPDATE_LOGIN_PASS = API_URL + "updateLoginpass";

   /**
    * 设置存管密码【存管】
    */
   String SET_CUSTODY_PASS = API_URL + "setCustodypass";

   /**
    * 修改存管密码【存管】
    */
   String UPDATE_CUSTODY_PASS = API_URL + "updateCustodypass";

   /**
     * 首页
     */
    String INDEX = API_URL + "index";

    /**
     * 账户信息
     */
    String ACCOUNT = API_URL + "account";

    /**
     * 项目-标-列表
     */
    String ODDS = API_URL + "odds";

    /**
     * 项目-标-详情
     */
    String ODD = API_URL + "odd";

    /**
     * 充值记录
     */
    String RECHARGE_RECORDS = API_URL + "rechargeRecords";

    /**
     * 提现记录
     */
    String WITHDRAW_RECORDS = API_URL + "withdrawRecords";

    /**
     * 用户所有银行卡列表
     */
    String GET_BANK_CARDS = API_URL + "userBankCards";

 /**
  * 用户所有银行卡列表
  */
 String GET_BANK_CARD = API_URL + "userBankCard";
    /**
     * 可使用加息券
     */
    String INVESTLOTTER = API_URL + "investlotteries";
    /**
     * 使用加息券
     */
    String USEINVESTLOTTER = API_URL + "useinvestlottery";

    /**
     * 详情
     */
    String REPAYMENT = API_URL + "repaymentDetail";

    /**
     * 添加银行卡
     */
    String ADD_BANK_CARD = API_URL + "addBankCard";

    /**
     * 实名认证
     */
    String CERTIFICATION = API_URL + "certification";

    /**
     * 银行列表
     */
    String BANKS = API_URL + "banks";

    /**
     * 省市
     */
    String AREAS = API_URL + "areas";

    /**
     * 设置支付密码
     */
    String SET_PAY_PASS = API_URL + "setPaypass";

    /**
     * 绑定第三方账号
     */
//    String BIND_THIRD = API_URL + "bindThird";
    String OPEN_CUSTODY = API_URL + "openCustody";

    /**
     * 存管协议
     */
//    String BIND_THIRD = API_URL + "bindThird";
    String OPEN_CUSTODY_AGREEMENT = "https://app.xwsd.com/v2page/cuspro.html";
    /**
     * 获取已经认证的银行卡
     */
    String AGREE_CARD = API_URL + "agreeCard";

    /**
     * 获取用户信息
     */
    String GET_USER_INFO = API_URL + "getUserInfo";

    /**
     * 获取自动投标队列信息
     */
    String QUEUE_INFO = API_URL + "queueInfo";

    /**
     * 授权 自动投标
     */
    String AUTO_AUTH = API_URL + "autoAuth";

    /**
     * 删除银行卡
     */
    String DEL_BANK_CARD = API_URL + "delBankCard";

    /**
     * 修改银行卡
     */
    String UPDATE_BANK_CARD = API_URL + "updateBankCard";

    /**
     * 充值
     */
    String RECHARGE = API_URL + "recharge";

    /**
     * 提现
     */
    String WITHDRAW = API_URL + "withdraw";

    /**
     * 提现手续费
     */
    String WITHDRAW_FEE = API_URL + "withdrawFee";

    /**
     * 项目-标-风控信息
     */
    String ODDRM = API_URL + "oddrm";

    /**
     * 项目-标-投资记录
     */
    String ODDTENDERS = API_URL + "oddtenders";

    /**
     * 在投记录
     */
    String ODDTENDERS_NOW = API_URL + "oddBuy";

    /**
     * 找回登录密码
     */
    String FORGET_LOGIN_PASS = API_URL + "forgetLoginpass";

    /**
     * 找回支付密码
     */
    String FORGET_PAYPASS = API_URL + "forgetPaypass";

    /**
     * 获取自动投标设置信息
     */
    String AUTO_INFO = API_URL + "autoInfo";

    /**
     * 设置自动投标
     */
    String AUTO_SET = API_URL + "autoSet";

    /**
     * 债权转让列表
     */
    String CRTRS = API_URL + "crtrs";

    /**
     * 债权转让详情
     */
    String CRTR = API_URL + "crtr";

    /**
     * 获取标的最大可投金额
     */
    String GET_MAX_INVEST = API_URL + "getMaxInvest";

    /**
     * 获取债权的最大可投金额
     */
    String GET_MAX_BUY = API_URL + "getMaxBuy";

    /**
     * 投标
     */
    String BID = API_URL + "bid";

    /**
     * 债权
     */
    String BUY = API_URL + "buy";

    /**
     * 债权购买记录
     */
    String BUY_RECORDS = API_URL + "buyrecords";
    /**
     * 债权在买记录
     */
    String BUYING_RECORDS = API_URL + "crtrBuy";

    /**
     * 回款日历
     */
    String REPAYMENTS = API_URL + "repayments";

    /**
     * 用户个人投资记录
     */
    String USER_TENDERS = API_URL + "usertenders";

    /**
     * 获取讯息接口
     */
    String INFOS = API_URL + "infos";

    /**
     * 获取讯息接口
     */
    String USER_CRTRS = API_URL + "usercrtrs";

    /**
     * 债权转让
     */
    String TRANSFER = API_URL + "transfer";

    /**
     * 债券撤销转让
     */
    String DEL_TRANSFER = API_URL + "deltransfer";

    /**
     * 用户资金账户
     */
    String USER_ACCOUNT = API_URL + "useraccount";

    /**
     * 申请借款
     */
    String ORDER = API_URL + "order";

    /**
     * 设置邮箱
     */
    String SET_EMAIL = API_URL + "setEmail";

    /**
     * 获取用户推广链接
     */
    String SPREAD = API_URL + "spread";

    /**
     * 讯息详情
     */
    String NEWS = API_URL + "news";

    /**
     * 推荐奖励
     */
    String FRIEND = API_URL + "spread";
    /**
     * 提取推荐奖励
     */
    String GETFRIEND = API_URL + "extractMoney";
    /**
     * 计算
     */
    String crtrs= API_URL + "calculate";
    /**
     * 资金记录
     */
    String ZIJIN = API_URL + "moneyLog";

    /**
     * 讯息详情
     */
    String ANSWERS = API_URL + "answers";

    /**
     * 回复
     */
    String REPLY = API_URL + "reply";

    /**
     * 提问
     */
    String ASK = API_URL + "ask";

    /**
     * 用户协议
     */
    String AGREEMENT = "https://app.xwsd.com/v2page/agreement.html";

    /**
     * 帮助中心
     */
    String QUESTION = "https://app.xwsd.com/v2page/question.html";

    /**
     * 关于我们
     * file:///android_asset/single.html
     * http://app.xwsd.com/v2page/about.html
     */
    String ABOUT_US = "https://app.xwsd.com/v2page/about.html";

    /**
     * 收费标准
     */
  //  String FEES = "file:///android_asset/fee.html";
    String FEES = "https://app.xwsd.com/v2page/fee.html";

    /**
     * 设置用户头像
     */
    String SET_USER_PHOTO = API_URL + "setUserPhoto";

    /**
     * 设置用户信息
     */
    String SET_USER_INFO = API_URL + "setUserInfo";

    /**
     * 获取VIP信息
     */
    String USER_VIP = API_URL + "userVip";

    /**
     * 申请VIP
     */
    String VIP_APPLY = API_URL + "vipApply";

    /**
     * 埋点
     */
    String PV = API_URL + "pv";

    /**
     * 版本更新
     */
    String VERSION = API_URL + "version";

   /**
    * 用户银行卡解绑
    */
   String CARD_UNBIND = API_URL + "cardUnbind";

   /**
    * 用户银行卡绑定【存管】
    */
   String CARD_BIND = API_URL + "cardBind";

   /**
    * 用户银行卡同步
    */
   String CARD_REFRESH = API_URL + "cardRefresh";


    /**
     * 银行卡充值限额列表
     */
    String CARD_LIMIT = API_URL + "cardLimit";

    /**
     * 同步资金
     */
    String SYNC_MONEY = API_URL + "syncMoney";
}
