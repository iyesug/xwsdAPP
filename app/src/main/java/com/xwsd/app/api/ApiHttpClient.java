package com.xwsd.app.api;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;
import com.xwsd.app.AppContext;
import com.xwsd.app.tools.ImgUtil;
import com.xwsd.app.tools.MD5;
import com.xwsd.app.tools.TLog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.request.RequestCall;
import okhttp3.Call;

import java.io.File;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Gx on 2016/8/31.
 * 应用网络请求统一调用工具类
 */
public class ApiHttpClient implements XWSDRequestAdresse {

    public static String MSG_TYPE_REGISTER = "register";
    public static String MSG_TYPE_FORGET = "forget";
    public static String MSG_TYPE_FORGET_PAYPASS = "forgetPaypass";

    public static String LOGINPASS = "loginpass";
    public static String PAYPASS = "paypass";
    public static String ALL = "all";
    public static String SUCCESS = "success";
    public static String FAIL = "fail";

    public static String YES = "y";
    public static String NO = "n";

//    /**
//     * 旧充值接口，充值渠道和充值方式(宝付)
//     */
//    public static String PAY_TYPE = "baofoo";
//    public static String PAY_WAY = "3";

    /**
     * 新充值接口，充值渠道和充值方式(富友)
     */
    public static String PAY_TYPE = "fuiou";
    public static String PAY_WAY = "02";

    /**
     * 新充值接口，充值渠道和充值方式(宝付)
     */
    public static String PAY_TYPE_baofu = "baofoo";
    public static String PAY_WAY_baofu = "3";

    public static String TYPE_TENDERS_RUN = "run";
    public static String TYPE_TENDERS_END = "end";

    public static String TYPE_INFOS_NOTICE = "notice";
    public static String TYPE_INFOS_ANNOUNCE = "announce";
    public static String TYPE_INFOS_QUESTION = "question";

    public static String TYPE_QUESTIONS_CEO = "ceo";
    public static String TYPE_QUESTIONS_NORMAL = "normal";

    public static String OPEN = "1";
    public static String CLOSE = "0";
    public static String userSecret = "";

    /**
     * 发送验证码
     *
     * @param msgType  验证码类型 注册：register  找回登录密码：forget  找回支付密码：forgetPaypass
     * @param phone    手机号码
     * @param callback 回调接口
     * @return 返回一个可取消的请求
     */
    public static RequestCall sendMessage(String msgType, String phone, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("msgType", msgType);
        map.put("phone", phone);
        RequestCall call = OkHttpUtils
                .post()
                .url(SMS)
                .addParams("msgType", msgType)
                .addParams("phone", phone)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 注册
     *
     * @param username   用户名
     * @param password   登录密码
     * @param phone      手机号码
     * @param spreadUser 介绍人
     * @param smsCode    短信验证码
     * @param callback   回调接口
     * @return 返回一个可取消的请求
     */
    public static RequestCall registered(String username, String password, String phone, String spreadUser, String smsCode, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("username", username);
        map.put("password", password);
        map.put("phone", phone);
        if (!TextUtils.isEmpty(spreadUser)) {
            map.put("spreadUser", spreadUser);
        }
        map.put("smsCode", smsCode);
        PostFormBuilder builder = OkHttpUtils
                .post()
                .url(REGISTER)
                .addParams("username", username)
                .addParams("password", password)
                .addParams("phone", phone)
                .addParams("smsCode", smsCode)
                .addParams("sign", sign(map));

        if (!TextUtils.isEmpty(spreadUser)) {
            builder.addParams("spreadUser", spreadUser);
        }

        RequestCall call = builder.build();
        call.execute(callback);
        return call;
    }

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 登录密码
     * @param callback 回调接口
     * @return 返回一个可取消的请求
     */
    public static RequestCall login(String username, String password, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("username", username);
        map.put("password", password);
        RequestCall call = OkHttpUtils
                .post()
                .url(LOGIN)
                .addParams("username", username)
                .addParams("password", password)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 修改登录密码
     *
     * @param userId       用户id
     * @param oldpass      旧密码
     * @param password     新密码
     * @param passwordSure 确认密码
     * @param type         loginpass：修改登录密码 paypass：修改支付密码
     * @param callback     回调接口
     * @return 返回一个可取消的请求
     */
    public static RequestCall updatePassword(String userId,
                                             String oldpass,
                                             String password,
                                             String passwordSure,
                                             String type,
                                             Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("oldpass", oldpass);
        map.put("password", password);
        map.put("passwordSure", passwordSure);
        map.put("type", type);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .post()
                .url(UPDATE_PASSWORD)
                .addParams("userId", userId)
                .addParams("oldpass", oldpass)
                .addParams("password", password)
                .addParams("passwordSure", passwordSure)
                .addParams("type", type)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 得到首页信息
     *
     * @param callback 回调接口
     * @return 返回一个可取消的请求
     */
    public static RequestCall index(Callback callback) {
        Map<String, String> map = getSortMap();
        if(AppContext.getUserBean()!=null && AppContext.getUserBean().data.userId.length()>0){
            map.put("userId", AppContext.getUserBean().data.userId);
        }
        GetBuilder build = OkHttpUtils
                .get()
                .url(INDEX)
                .addParams("sign", sign(map));
        if(AppContext.getUserBean()!=null && AppContext.getUserBean().data.userId.length()>0){
            build.addParams("userId", AppContext.getUserBean().data.userId);
        }
        RequestCall call = build.build();
        call.execute(callback);
        return call;
    }

    /**
     * 得到账户信息
     *
     * @param callback 回调接口
     * @return 返回一个可取消的请求
     */
    public static RequestCall account(Callback callback) {
        if(null!=AppContext.getUserBean()){
            Map<String, String> map = getSortMap();
            map.put("userId", AppContext.getUserBean().data.userId);
            map.put("userSecret",userSecret);
            RequestCall call = OkHttpUtils
                    .get()
                    .url(ACCOUNT)
                    .addParams("userId", AppContext.getUserBean().data.userId)
                    .addParams("userSecret",userSecret)
                    .addParams("sign", sign(map))
                    .build();
            call.execute(callback);
            return call;
        }

        return null;
    }

    /**
     * 项目-标-列表
     *
     * @param callback 回调接口
     * @param page     页数，从1开始
     * @param pageSize 每页显示数量
     * @param type     类型（可不传），值返回参数中types的中某一项的键
     * @param period   期限（可不传），值返回参数中periods的中某一项的键
     * @return 返回一个可取消的请求
     */
    public static RequestCall odds(int page, int pageSize, String period, String type, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("page", String.valueOf(page));
        map.put("pageSize", String.valueOf(pageSize));
        if (!TextUtils.isEmpty(period)) {
            map.put("period", period);
        }
        if (!TextUtils.isEmpty(type)) {
            map.put("type", type);
        }
        if(AppContext.getUserBean()!=null && AppContext.getUserBean().data.userId.length()>0){
            map.put("userId", AppContext.getUserBean().data.userId);
        }
        GetBuilder build = OkHttpUtils
                .get()
                .url(ODDS)
                .addParams("page", String.valueOf(page))
                .addParams("pageSize", String.valueOf(pageSize))
                .addParams("sign", sign(map));

        if (!TextUtils.isEmpty(period)) {
            build.addParams("period", period);
        }

        if (!TextUtils.isEmpty(type)) {
            build.addParams("type", type);
        }
        if(AppContext.getUserBean()!=null && AppContext.getUserBean().data.userId.length()>0){
            build.addParams("userId", AppContext.getUserBean().data.userId);
        }
        RequestCall call = build.build();
        call.execute(callback);
        return call;
    }

    /**
     * 项目-标-详情
     *
     * @param callback  回调接口
     * @param oddNumber 标的唯一标识符
     * @return 返回一个可取消的请求
     */
    public static RequestCall odd(String oddNumber, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("oddNumber", oddNumber);
        RequestCall call = OkHttpUtils
                .get()
                .url(ODD)
                .addParams("oddNumber", oddNumber)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 充值记录
     *
     * @param userId    用户id
     * @param page      当前页码
     * @param pageSize  每页长度
     * @param startTime 开始时间(如：2015-12-02)（如果没设置则为空）
     * @param endTime   结束时间(如：2015-12-02)（如果没设置则为空）
     * @param callback  标的唯一标识符
     * @return 返回一个可取消的请求
     */
    public static RequestCall rechargeRecords(
            String userId,
            int page,
            int pageSize,
            String startTime,
            String endTime,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("page", "" + page);
        map.put("pageSize", "" + pageSize);
        map.put("userSecret",userSecret);

//        map.put("startTime", startTime);
//        map.put("endTime", endTime);
        RequestCall call = OkHttpUtils
                .get()
                .url(RECHARGE_RECORDS)
                .addParams("userId", userId)
                .addParams("page", "" + page)
                .addParams("pageSize", "" + pageSize)
//                .addParams("startTime", startTime)
//                .addParams("endTime", endTime)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 提现记录
     *
     * @param page      当前页码
     * @param pageSize  每页长度
     * @param status    all:所有（默认） success:成功   fail：失败
     * @param userId    用户id
     * @param startTime 开始时间(如：2015-12-2)（如果没设置则为空）
     * @param endTime   结束时间(如：2015-12-2)（如果没设置则为空）
     * @return 返回一个可取消的请求
     */
    public static RequestCall withdrawRecords(
            String userId,
            int page,
            int pageSize,
            String status,
            String startTime,
            String endTime,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("page", "" + page);
        map.put("pageSize", "" + pageSize);
        map.put("userSecret",userSecret);
//        map.put("startTime", startTime);
//        map.put("endTime", endTime);
        RequestCall call = OkHttpUtils
                .get()
                .url(WITHDRAW_RECORDS)
                .addParams("userId", userId)
                .addParams("page", "" + page)
                .addParams("pageSize", "" + pageSize)
//                .addParams("startTime", startTime)
//                .addParams("endTime", endTime)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 用户所有银行卡列表
     *
     * @param userId 用户id
     * @return 返回一个可取消的请求
     */
    public static RequestCall getBankCards(
            String userId,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .get()
                .url(GET_BANK_CARDS)
                .addParams("userId", userId)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 获取加息卷
     * @param userId
     * @param oddMoneyId
     * @param callback
     * @return
     */
    public static RequestCall getjiaxiTicket(
            String userId,String oddMoneyId,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("oddMoneyId", oddMoneyId);
        map.put("page", "1");
        map.put("pageSize","50");
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .get()
                .url(INVESTLOTTER)
                .addParams("userId", userId)
                .addParams("oddMoneyId", oddMoneyId)
                .addParams("page", "1")
                .addParams("pageSize", "50")
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }
    public static RequestCall getDetilaInfo(
            String userId,String oddMoneyId,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("oddMoneyId", oddMoneyId);
        map.put("page", "1");
        map.put("pageSize","36");
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .get()
                .url(REPAYMENT)
                .addParams("userId", userId)
                .addParams("oddMoneyId", oddMoneyId)
                .addParams("page", "1")
                .addParams("pageSize", "36")
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 使用加息卷
     * @param userId
     * @param oddMoneyId
     * @param callback
     * @return
     */
    public static RequestCall usejiaxiTicket(
            String userId,String lotteryId,String oddMoneyId,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("oddMoneyId", oddMoneyId);
        map.put("lotteryId", lotteryId);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .post()
                .addParams("userId", userId)
                .addParams("oddMoneyId", oddMoneyId)
                .addParams("lotteryId", lotteryId)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .url(USEINVESTLOTTER)
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 添加银行卡
     *
     * @param userId    用户id
     * @param bankNum   银行卡账号
     * @param bank      对应银行名称的取值
     * @param province  省份对应的取值
     * @param city      城市对应的取值
     * @param subbranch 支行名称
     * @param isDefault 是否默认
     * @return 返回一个可取消的请求
     */
    public static RequestCall addBankCard(
            String userId,
            String bankNum,
            String bank,
            String province,
            String city,
            String subbranch,
            String isDefault,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("bankNum", bankNum);
        map.put("bank", bank);
        map.put("province", province);
        map.put("city", city);
        map.put("subbranch", subbranch);
        map.put("isDefault", isDefault);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .post()
                .url(ADD_BANK_CARD)
                .addParams("userId", userId)
                .addParams("bankNum", bankNum)
                .addParams("bank", bank)
                .addParams("province", province)
                .addParams("city", city)
                .addParams("subbranch", subbranch)
                .addParams("isDefault", isDefault)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }




    /**
     * 加载图片
     *
     * @param imageView
     * @param imgUrl    图片地址
     * @param loadImg   加载中图像
     * @param errorImg  加载错误图像
     * @return 返回一个可取消的请求
     */
    public static void loadImg(final ImageView imageView, String imgUrl, final int loadImg, final int errorImg) {
        imageView.setImageResource(loadImg);
        OkHttpUtils
                .get()
                .url(imgUrl)
                .build()
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        imageView.setImageResource(errorImg);
                    }

                    @Override
                    public void onResponse(Bitmap response, int id) {
                        imageView.setImageBitmap(response);
                    }
                });
    }

    /**
     * 加载环形图片
     *
     * @param imageView
     * @param imgUrl    图片地址
     * @param loadImg   加载中图像
     * @param errorImg  加载错误图像
     * @return 返回一个可取消的请求
     */
    public static void lodCircleImg(final ImageView imageView, String imgUrl, final int loadImg, final int errorImg) {
        imageView.setImageResource(loadImg);
        OkHttpUtils
                .get()
                .url(imgUrl)
                .build()
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        imageView.setImageResource(errorImg);
                    }

                    @Override
                    public void onResponse(Bitmap response, int id) {
                        imageView.setImageBitmap(ImgUtil.makeRoundCorner(response));
                    }
                });
    }

    /**
     * 实名认证
     *
     * @param userId   用户id
     * @param realname 姓名
     * @param cardnum  身份证号
     * @return 返回一个可取消的请求
     */
    public static RequestCall certification(
            String userId,
            String realname,
            String cardnum,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("realname", realname);
        map.put("cardnum", cardnum);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .post()
                .url(CERTIFICATION)
                .addParams("userId", userId)
                .addParams("realname", realname)
                .addParams("cardnum", cardnum)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 银行列表
     *
     * @return 返回一个可取消的请求
     */
    public static RequestCall banks(
            Callback callback) {
        Map<String, String> map = getSortMap();

        RequestCall call = OkHttpUtils
                .get()
                .url(BANKS)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 获取省市列表
     *
     * @return 返回一个可取消的请求
     */
    public static RequestCall areas(
            String id,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("id", id);
        RequestCall call = OkHttpUtils
                .get()
                .url(AREAS)
                .addParams("id", id)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 设置支付密码
     *
     * @param userId      用户Id
     * @param loginpass   登录密码
     * @param paypass     要设置的支付密码
     * @param paypassSure 支付密码确认
     * @return 返回一个可取消的请求
     */
    public static RequestCall setPaypass(
            String userId,
            String loginpass,
            String paypass,
            String paypassSure,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("loginpass", loginpass);
        map.put("paypass", paypass);
        map.put("paypassSure", paypassSure);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .post()
                .url(SET_PAY_PASS)
                .addParams("userId", userId)
                .addParams("loginpass", loginpass)
                .addParams("paypass", paypass)
                .addParams("paypassSure", paypassSure)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 获取认证银行卡（充值银行卡）
     *
     * @param userId 用户IdsetUserPhoto
     */
    public static RequestCall agreeCard(
            String userId,String payType,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("payType",payType);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .post()
                .url(AGREE_CARD)
                .addParams("userId", userId)
                .addParams("payType", payType)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 获取用户信息
     *
     * @param userId 用户Id
     * @return 返回一个可取消的请求
     */
    public static RequestCall getUserInfo(
            String userId,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .post()
                .url(GET_USER_INFO)
                .addParams("userId", userId)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 获取自动投标队列
     *
     * @param userId 用户Id
     * @return 返回一个可取消的请求
     */
    public static RequestCall queueInfo(
            String userId,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .get()
                .url(QUEUE_INFO)
                .addParams("userId", userId)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 删除银行卡
     *
     * @param userId 用户Id
     * @param id     银行卡Id
     * @return 返回一个可取消的请求
     */
    public static RequestCall delBankCard(
            String userId,
            String id,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("id", id);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .post()
                .url(DEL_BANK_CARD)
                .addParams("userId", userId)
                .addParams("id", id)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }
    /**
     * 修改银行卡
     *
     * @param id        银行卡id
     * @param userId    用户id
     * @param bankNum   银行卡账号
     * @param bank      对应银行名称的取值
     * @param province  省份对应的取值
     * @param city      城市对应的取值
     * @param subbranch 支行名称
     * @param isDefault 是否默认
     * @return 返回一个可取消的请求
     */
    public static RequestCall updateBankCard(
            String id,
            String userId,
            String bankNum,
            String bank,
            String province,
            String city,
            String subbranch,
            String isDefault,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("id", id);
        map.put("userId", userId);
        map.put("bankNum", bankNum);
        map.put("bank", bank);
        map.put("province", province);
        map.put("city", city);
        map.put("subbranch", subbranch);
        map.put("isDefault", isDefault);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .post()
                .url(UPDATE_BANK_CARD)
                .addParams("id", id)
                .addParams("userId", userId)
                .addParams("bankNum", bankNum)
                .addParams("bank", bank)
                .addParams("province", province)
                .addParams("city", city)
                .addParams("subbranch", subbranch)
                .addParams("isDefault", isDefault)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }
    /**
     * 充值接口
     *
     * @param userId   用户id
     * @param money    充值金额, 需要大于500，测试时0.01
     * @param payType  充值渠道, 目前只能为富友
     * @param payWay   充值方式, 目前只能为02
     * @param bankCode 银行编号, 不可空，通过获取用户已认证银行卡接口可获得
     * @param noAgree  认证码, 已认证用户必须，未认证用户为空字符串
     * @param bankCard 银行卡号, 未认证用户必须，已认证用户可为空字符串
     * @return 返回一个可取消的请求
     */
    public static RequestCall recharge(
            String userId,
            String money,
            String payType,
            String payWay,
            String bankCode,
            String noAgree,
            String bankCard,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("money", money);
        map.put("payType", payType);
        map.put("payWay", payWay);
        map.put("bankCode", bankCode);
        map.put("userSecret",userSecret);
        if (noAgree != null) {
            map.put("noAgree", noAgree);
        }
        if (bankCard != null) {
            map.put("bankCard", bankCard);
        }
        PostFormBuilder builder = OkHttpUtils
                .post()
                .url(RECHARGE)
                .addParams("userId", userId)
                .addParams("money", money)
                .addParams("payType", payType)
                .addParams("payWay", payWay)
                .addParams("bankCode", bankCode)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map));

        if (noAgree != null) {
            builder.addParams("noAgree", noAgree);
        }
        if (bankCard != null) {
            builder.addParams("bankCard", bankCard);
        }

        RequestCall call = builder.build();
        call.execute(callback);
        return call;
    }

    /**
     * 提现
     *
     * @param userId  用户id
     * @param money   提现金额
     * @param paypass 支付密码
     * @param bank    银行卡ID
     * @param isLottery    是否提现卷
     * @return 返回一个可取消的请求
     */
    public static RequestCall withdraw(
            String userId,
            String money,
            String paypass,
            String bank,
            String isLottery,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("money", money);
        map.put("paypass", paypass);
        map.put("bank", bank);
        map.put("isLottery",isLottery);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .post()
                .url(WITHDRAW)
                .addParams("userId", userId)
                .addParams("money", money)
                .addParams("paypass", paypass)
                .addParams("bank", bank)
                .addParams("isLottery",isLottery)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }


    /**
     * 获取提现手续费
     *
     * @param userId 用户id
     * @param money  提现金额
     * @return 返回一个可取消的请求
     */
    public static RequestCall withdrawFee(
            String userId,
            String money,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("money", money);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .post()
                .url(WITHDRAW_FEE)
                .addParams("userId", userId)
                .addParams("money", money)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 项目-标-风控信息
     *
     * @param oddNumber 标的唯一标识符
     * @return 返回一个可取消的请求
     */
    public static RequestCall oddrm(
            String oddNumber,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("oddNumber", oddNumber);
        RequestCall call = OkHttpUtils
                .get()
                .url(ODDRM)
                .addParams("oddNumber", oddNumber)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 项目-标-投资记录
     *
     * @param oddNumber 标的唯一标识符
     * @param page      当前页码
     * @param pageSize  每页长度
     * @param callback  标的唯一标识符
     * @return 返回一个可取消的请求
     */
    public static RequestCall oddtenders(
            String oddNumber,
            int page,
            int pageSize,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("oddNumber", oddNumber);
        map.put("page", "" + page);
        map.put("pageSize", "" + pageSize);
        RequestCall call = OkHttpUtils
                .get()
                .url(ODDTENDERS)
                .addParams("oddNumber", oddNumber)
                .addParams("page", "" + page)
                .addParams("pageSize", "" + pageSize)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 在投记录
     * @param oddNumber
     * @param page
     * @param pageSize
     * @param callback
     * @return
     */
    public static RequestCall oddtendersNow(
            String oddNumber,
            int page,
            int pageSize,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("oddNumber", oddNumber);
        map.put("page", "" + page);
        map.put("pageSize", "" + pageSize);
        RequestCall call = OkHttpUtils
                .get()
                .url(ODDTENDERS_NOW)
                .addParams("oddNumber", oddNumber)
                .addParams("page", "" + page)
                .addParams("pageSize", "" + pageSize)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 找回登录密码
     *
     * @param phone         手机号码
     * @param phoneCode     短信验证码
     * @param loginpass     新密码
     * @param loginpassSure 确认新密码
     * @return 返回一个可取消的请求
     */
    public static RequestCall forgetLoginpass(
            String phone,
            String phoneCode,
            String loginpass,
            String loginpassSure,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("phone", phone);
        map.put("phoneCode", phoneCode);
        map.put("loginpass", loginpass);
        map.put("loginpassSure", loginpassSure);
        RequestCall call = OkHttpUtils
                .post()
                .url(FORGET_LOGIN_PASS)
                .addParams("phone", phone)
                .addParams("phoneCode", phoneCode)
                .addParams("loginpass", loginpass)
                .addParams("loginpassSure", loginpassSure)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 找回支付密码
     *
     * @param phone       手机号码
     * @param phoneCode   短信验证码
     * @param paypass     新密码
     * @param paypassSure 确认新密码
     * @return 返回一个可取消的请求
     */
    public static RequestCall forgetPaypass(
            String phone,
            String phoneCode,
            String paypass,
            String paypassSure,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("phone", phone);
        map.put("phoneCode", phoneCode);
        map.put("paypass", paypass);
        map.put("paypassSure", paypassSure);
        RequestCall call = OkHttpUtils
                .post()
                .url(FORGET_PAYPASS)
                .addParams("phone", phone)
                .addParams("phoneCode", phoneCode)
                .addParams("paypass", paypass)
                .addParams("paypassSure", paypassSure)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 获取自动投标设置信息
     *
     * @param userId 用户Id
     * @return 返回一个可取消的请求
     */
    public static RequestCall autoInfo(
            String userId,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .get()
                .url(AUTO_INFO)
                .addParams("userId", userId)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 设置自动投标
     *
     * @param userId          用户ID
     * @param autostatus      是否开通自动投标：1开启；0关闭
     * @param staystatus      是否站队：1开启；0关闭
     * @param investEgisMoney 账户保留金额（0为不保留）
     * @param moneyType       投资金额类型：1投资范围；0指定金额
     * @param rangeBegin      最低投资金额(此参数不可省略，moneyType为0时与此值与fixedMoney相同即可)
     * @param rangeEnd        最高投资金额(此参数不可省略，moneyType为0时与此值与fixedMoney相同即可)
     * @param fixedMoney      指定投资金额(此参数不可省略，moneyType为1此值可给1任意大于0的值)
     * @param types           投资类型，将类型 id使用英文逗号(,)拼接起来
     * @return 返回一个可取消的请求
     */
    public static RequestCall autoSet(
            String userId,
            String autostatus,
            String staystatus,
            String investEgisMoney,
            String moneyType,
            String rangeBegin,
            String rangeEnd,
            String fixedMoney,
            String types,
            String mode,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("autostatus", autostatus);
        map.put("staystatus", staystatus);
        map.put("investEgisMoney", investEgisMoney);
        map.put("moneyType", moneyType);
        map.put("rangeBegin", rangeBegin);
        map.put("rangeEnd", rangeEnd);
        map.put("fixedMoney", fixedMoney);
        map.put("mode",mode);
        map.put("userSecret",userSecret);
        if (types != null) {
            map.put("types", types);
        }

        PostFormBuilder build = OkHttpUtils
                .post()
                .url(AUTO_SET)
                .addParams("userId", userId)
                .addParams("autostatus", autostatus)
                .addParams("staystatus", staystatus)
                .addParams("investEgisMoney", investEgisMoney)
                .addParams("moneyType", moneyType)
                .addParams("rangeBegin", rangeBegin)
                .addParams("rangeEnd", rangeEnd)
                .addParams("fixedMoney", fixedMoney)
                .addParams("mode",mode)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map));

        if (types != null) {
            build.addParams("types", types);
        }
        RequestCall call = build.build();
        call.execute(callback);
        return call;
    }

    /**
     * 项目-债权转让列表
     *
     * @param callback 回调接口
     * @param page     页数，从1开始
     * @param pageSize 每页显示数量
     * @return 返回一个可取消的请求
     */
    public static RequestCall crtrs(int page, int pageSize, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("page", String.valueOf(page));
        map.put("pageSize", String.valueOf(pageSize));
        RequestCall call = OkHttpUtils
                .get()
                .url(CRTRS)
                .addParams("page", String.valueOf(page))
                .addParams("pageSize", String.valueOf(pageSize))
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 项目-债权转让-详情
     *
     * @param id       标的唯一标识符
     * @param callback 回调接口
     * @return 返回一个可取消的请求
     */
    public static RequestCall crtr(String id, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("id", id);
        RequestCall call = OkHttpUtils
                .get()
                .url(CRTR)
                .addParams("id", id)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 项目-标-获取最大可投金额
     *
     * @param userId   用户ID
     * @param id       标的唯一标识符
     * @param callback 回调接口
     * @return 返回一个可取消的请求
     */
    public static RequestCall getMaxInvest(String userId, String id, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("oddNumber", id);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .get()
                .url(GET_MAX_INVEST)
                .addParams("userId", userId)
                .addParams("oddNumber", id)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 项目-债权转让-获取最大可投金额
     *
     * @param userId   用户ID
     * @param id       标的唯一标识符
     * @param callback 回调接口
     * @return 返回一个可取消的请求
     */
    public static RequestCall getMaxBuy(String userId, String id, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("id", id);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .get()
                .url(GET_MAX_BUY)
                .addParams("userId", userId)
                .addParams("id", id)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }


    /**
     * 债权购买记录
     *
     * @param callback 回调接口
     * @param id       唯一标识符
     * @param page     页数，从1开始
     * @param pageSize 每页显示数量
     * @return 返回一个可取消的请求
     */
    public static RequestCall buyRecords(String id, int page, int pageSize, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("page", String.valueOf(page));
        map.put("id", id);
        map.put("pageSize", String.valueOf(pageSize));
        RequestCall call = OkHttpUtils
                .get()
                .url(BUY_RECORDS)
                .addParams("page", String.valueOf(page))
                .addParams("id", id)
                .addParams("pageSize", String.valueOf(pageSize))
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }
    /**
     * 债权在买记录
     *
     * @param callback 回调接口
     * @param id       唯一标识符
     * @param page     页数，从1开始
     * @param pageSize 每页显示数量
     * @return 返回一个可取消的请求
     */
    public static RequestCall buyingRecords(String id, int page, int pageSize, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("page", String.valueOf(page));
        map.put("id", id);
        map.put("pageSize", String.valueOf(pageSize));
        RequestCall call = OkHttpUtils
                .get()
                .url(BUYING_RECORDS)
                .addParams("page", String.valueOf(page))
                .addParams("id", id)
                .addParams("pageSize", String.valueOf(pageSize))
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 回款记录
     *
     * @param callback 回调接口
     * @param userId   用户ID
     * @param year     年份（如2016）
     * @param month    月份（如12
     * @param isAll    是否获取所有回款的日期 1 是 0 否
     * @return 返回一个可取消的请求
     */
    public static RequestCall repayments(String userId, String year, String month, String isAll, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("year", year);
        map.put("month", month);
        map.put("isAll", isAll);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .get()
                .url(REPAYMENTS)
                .addParams("userId", userId)
                .addParams("year", year)
                .addParams("month", month)
                .addParams("isAll", isAll)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 用户投资记录
     *
     * @param callback 回调接口
     * @param userId   用户ID
     * @param page     页码
     * @param pageSize 每页数量
     * @param type     run：待收项目 end：回款项目
     * @return 返回一个可取消的请求
     */
    public static RequestCall usertenders(String userId, int page, int pageSize, String type, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("page", String.valueOf(page));
        map.put("pageSize", String.valueOf(pageSize));
        map.put("type", type);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .get()
                .url(USER_TENDERS)
                .addParams("userId", userId)
                .addParams("page", String.valueOf(page))
                .addParams("pageSize", String.valueOf(pageSize))
                .addParams("type", type)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 债权购买记录
     *
     * @param page      页码
     * @param pageSize  每页数量
     * @param type      notice：公告 announce：发标预告 question：问答
     * @param title     主题（非必须）
     * @param startTime 开始时间（非必须）
     * @param endTime   结束时间（非必须）
     * @param callback  回调接口
     * @return 返回一个可取消的请求
     */
    public static RequestCall infos(String type, String title, String startTime, String endTime, int page, int pageSize, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("page", String.valueOf(page));
        map.put("pageSize", String.valueOf(pageSize));
        map.put("type", type);
        if (!TextUtils.isEmpty(title)) {
            map.put("title", title);
        }
        if (!TextUtils.isEmpty(startTime)) {
            map.put("startTime", startTime);
        }
        if (!TextUtils.isEmpty(endTime)) {
            map.put("endTime", endTime);
        }

        GetBuilder build = OkHttpUtils
                .get()
                .url(INFOS)
                .addParams("page", String.valueOf(page))
                .addParams("pageSize", String.valueOf(pageSize))
                .addParams("type", type)
                .addParams("sign", sign(map));

        if (!TextUtils.isEmpty(title)) {
            build.addParams("title", title);
        }
        if (!TextUtils.isEmpty(startTime)) {
            build.addParams("startTime", startTime);
        }
        if (!TextUtils.isEmpty(endTime)) {
            build.addParams("endTime", endTime);
        }

        RequestCall call = build.build();
        call.execute(callback);
        return call;
    }

    /**
     * 用户投资记录
     *
     * @param callback 回调接口
     * @param userId   用户ID
     * @param page     页码
     * @param pageSize 每页数量
     * @param type     类型：sell转让记录  buy受让记录  ing转让中  can可转让
     * @return 返回一个可取消的请求
     */
    public static RequestCall usercrtrs(String userId, int page, int pageSize, String type, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("page", String.valueOf(page));
        map.put("pageSize", String.valueOf(pageSize));
        map.put("type", type);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .get()
                .url(USER_CRTRS)
                .addParams("userId", userId)
                .addParams("page", String.valueOf(page))
                .addParams("pageSize", String.valueOf(pageSize))
                .addParams("type", type)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 债权转让
     *
     * @param callback   回调接口
     * @param userId     用户ID
     * @param oddMoneyId
     * @param paypass    支付密码
     * @return 返回一个可取消的请求
     */
    public static RequestCall transfer(String userId, String oddMoneyId, String paypass, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("oddMoneyId", oddMoneyId);
        map.put("paypass", paypass);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .post()
                .url(TRANSFER)
                .addParams("userId", userId)
                .addParams("oddMoneyId", oddMoneyId)
                .addParams("paypass", paypass)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 债券撤销转让
     *
     * @param callback   回调接口
     * @param userId     用户ID
     * @param oddMoneyId
     * @param paypass    支付密码
     * @return 返回一个可取消的请求
     */
    public static RequestCall deltransfer(String userId, String oddMoneyId, String paypass, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("oddMoneyId", oddMoneyId);
        map.put("paypass", paypass);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .post()
                .url(DEL_TRANSFER)
                .addParams("userId", userId)
                .addParams("oddMoneyId", oddMoneyId)
                .addParams("paypass", paypass)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 用户资金账户
     *
     * @param callback 回调接口
     * @param userId   用户ID
     * @return 返回一个可取消的请求
     */
    public static RequestCall useraccount(String userId, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .get()
                .url(USER_ACCOUNT)
                .addParams("userId", userId)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }


    /**
     * 申请借款接口
     *
     * @param callback 回调接口
     * @param phone    手机号
     * @param name     姓名
     * @param money    借款金额
     * @param province 省ID，见获取省、市信息接口
     * @param city     城市ID, 见获取省、市信息接口
     * @return 返回一个可取消的请求
     */
    public static RequestCall order(String phone,
                                    String name,
                                    String money,
                                    String province,
                                    String city,
                                    Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("phone", phone);
        map.put("name", name);
        map.put("money", money);
        map.put("province", province);
        map.put("city", city);
        RequestCall call = OkHttpUtils
                .post()
                .url(ORDER)
                .addParams("phone", phone)
                .addParams("name", name)
                .addParams("money", money)
                .addParams("province", province)
                .addParams("city", city)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 设置认证邮箱
     *
     * @param callback 回调接口
     * @param userId   用户ID
     * @param email    邮箱
     * @return 返回一个可取消的请求
     */
    public static RequestCall setEmail(String userId, String email, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("email", email);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .post()
                .url(SET_EMAIL)
                .addParams("userId", userId)
                .addParams("email", email)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 取用户推广链接
     *
     * @param callback 回调接口
     * @param userId   用户ID
     * @return 返回一个可取消的请求
     */
    public static RequestCall spread(String userId, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .get()
                .url(SPREAD)
                .addParams("userId", userId)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 讯息详情
     *
     * @param callback 回调接口
     * @param id       文章ID
     * @return 返回一个可取消的请求
     */
    public static RequestCall news(String id, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("id", id);
        RequestCall call = OkHttpUtils
                .get()
                .url(NEWS)
                .addParams("id", id)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 问答内容
     *
     * @param callback 回调接口
     * @param id       问答ID
     * @return 返回一个可取消的请求
     */
    public static RequestCall answers(String id, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("id", id);
        RequestCall call = OkHttpUtils
                .get()
                .url(ANSWERS)
                .addParams("id", id)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 回复
     *
     * @param callback     回调接口
     * @param userId       用户ID
     * @param questionId   问题ID
     * @param answerId     问答ID
     * @param replyContent 回复内容
     * @return 返回一个可取消的请求
     */
    public static RequestCall reply(String userId, String questionId, String answerId, String replyContent, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("questionId", questionId);
        map.put("answerId", answerId);
        map.put("replyContent", replyContent);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .get()
                .url(REPLY)
                .addParams("userId", userId)
                .addParams("questionId", questionId)
                .addParams("answerId", answerId)
                .addParams("replyContent", replyContent)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 回复
     *
     * @param callback 回调接口
     * @param userId   用户ID
     * @param title    提问标题
     * @param content  提问内容
     * @param type     ceo：ceo问答，暂时都用该值  normal：普通问答
     * @return 返回一个可取消的请求
     */
    public static RequestCall ask(String userId, String title, String content, String type, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("title", title);
        map.put("content", content);
        map.put("type", type);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .post()
                .url(ASK)
                .addParams("userId", userId)
                .addParams("title", title)
                .addParams("content", content)
                .addParams("type", type)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }


    /**
     * 修改用户头像
     *
     * @param userId   用户ID
     * @param callback 回调接口
     * @return 返回一个可取消的请求
     * addFile可以传文件的参数
     */
    public static RequestCall setUserHead(String userId, File file, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .post()
                .addParams("userId", userId)
                .addFile("photo", file.getName(), file)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .url(SET_USER_PHOTO)
                .build();
        call.execute(callback);
        return call;
    }


    /**
     * 修改用户信息
     *
     * @param userId        用户ID
     * @param callback      回调接口
     * @param sex           性别  man  women
     * @param maritalstatus 婚姻状况y已婚 n未婚
     * @param city          住址
     * @return 返回一个可取消的请求
     */
    public static RequestCall setUserInfo(String userId, String sex, String maritalstatus, String city, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("sex", sex);
        map.put("maritalstatus", maritalstatus);
        map.put("city", city);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .post()
                .addParams("userId", userId)
                .addParams("sex", sex)
                .addParams("maritalstatus", maritalstatus)
                .addParams("city", city)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .url(SET_USER_INFO)
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 获取VIP信息
     *
     * @param userId   用户ID
     * @param callback 回调接口
     * @return 返回一个可取消的请求
     */
    public static RequestCall userVip(String userId, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .get()
                .addParams("userId", userId)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .url(USER_VIP)
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 申请VIP
     *
     * @param userId        用户ID
     * @param time          时间ID
     * @param customService 客服ID
     * @param callback      回调接口
     * @return 返回一个可取消的请求
     */
    public static RequestCall vipApply(String userId, String time, String customService, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("time", time);
        map.put("customService", customService);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .post()
                .addParams("userId", userId)
                .addParams("time", time)
                .addParams("customService", customService)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .url(VIP_APPLY)
                .build();
        call.execute(callback);
        return call;
    }


    /**
     * 把埋点数据传到服务器
     *
     * @param userId   用户id
     * @param data     用户埋点数据
     * @param callback 回调接口
     * @return
     */
    public static RequestCall pvToservice(String userId, String data, Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("data", data);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .post()
                .addParams("userId", userId)
                .addParams("data", data)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .url(PV)
                .build();
        call.execute(callback);
        return call;
    }

    /**
     *
     * 版本更新
     * @param version
     * @param callback
     * @return
     */
    public static RequestCall updataVersion(String version,Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("version", version);
        RequestCall call = OkHttpUtils
                .get()
                .addParams("version",version)
                .addParams("sign",sign(map))
                .url(VERSION)
                .build();
        call.execute(callback);
        return call;
    }


//    public static RequestCall fuYouPay(String VERSION, String TYPE, String RESPONSECODE,
//                                       String RESPONSEMSG, String MCHNTCD, String MCHNTORDERID,
//                                       String ORDERID, String AMT, String BANKCARD, String SIGN, String url,Callback callback) {
//        RequestCall call = OkHttpUtils
//                .post()
//                .addParams("VERSION", VERSION)
//                .addParams("TYPE", TYPE)
//                .addParams("RESPONSECODE", RESPONSECODE)
//                .addParams("RESPONSEMSG", RESPONSEMSG)
//                .addParams("MCHNTCD",MCHNTCD)
//                .addParams("MCHNTORDERID",MCHNTORDERID)
//                .addParams("ORDERID", ORDERID)
//                .addParams("AMT", AMT)
//                .addParams("BANKCARD", BANKCARD)
//                .addParams("SIGN", SIGN)
//                .url(url)
//                .build();
//        call.execute(callback);
//        return call;
//    }


    /**
     * 得到一个可排序的map
     *
     * @return
     */
    public static Map<String, String> getSortMap() {
        Map<String, String> map = new TreeMap<String, String>(
                new Comparator<String>() {
                    public int compare(String obj1, String obj2) {
                        //升序排序
                        return obj1.compareTo(obj2);
                    }
                });
        return map;
    }

    /**
     * 签名
     *
     * @param param 排序后的参数
     * @return
     */
    public static String sign(Map<String, String> param) {
        StringBuffer stringBuffer = new StringBuffer();
//        遍历拼接参数
        for (String key : param.keySet()) {
            if(param.get(key) != null && param.get(key).length()>0){
                stringBuffer.append(key).append("=").append(param.get(key)).append("&");
            }
        }
//        去掉最后一个“&”字符
        if (stringBuffer.length() > 0) {
            stringBuffer.replace(stringBuffer.length() - 1, stringBuffer.length(), "");
        }

//        拼接上令牌
        stringBuffer.append(AUTH_KEY);

        TLog.error("签名参数：" + stringBuffer.toString());

//        返回MD5加密后的签名
        return MD5.md5(stringBuffer.toString());
    }

    /**
     * 获取推荐奖励列表
     * @param userId
     * @param callback
     * @return
     */
    public static RequestCall frined(
            String userId,
            String page,
            String pageSize,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("page",page);
        map.put("pageSize",pageSize);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .get()
                .url(FRIEND)
                .addParams("userId", userId)
                .addParams("page",page)
                .addParams("pageSize",pageSize)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .build();
        call.execute(callback);
        return call;
    }

    /**
     * 资金记录
     * @param userId
     * @param type
     * @param page
     * @param pageSize
     * @param callback
     * @return
     */
    public static RequestCall getzijinList(
            String userId,String type,String page,String pageSize,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("type", type);
        map.put("page", page);
        map.put("pageSize", pageSize);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .get()
                .url(ZIJIN)
                .addParams("userId", userId)
                .addParams("type",type)
                .addParams("page",page)
                .addParams("pageSize",pageSize)
                .addParams("sign", sign(map))
                .addParams("userSecret",userSecret)
                .build();
        call.execute(callback);
        return call;
    }
    /**
     * 提取推荐奖励
     * @param userId
     * @param money
     * @param callback
     * @return
     */
    public static RequestCall getTuijian(
            String userId,String money,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("userId", userId);
        map.put("money",money);
        map.put("userSecret",userSecret);
        RequestCall call = OkHttpUtils
                .post()
                .addParams("userId", userId)
                .addParams("money",money)
                .addParams("userSecret",userSecret)
                .addParams("sign", sign(map))
                .url(GETFRIEND)
                .build();
        call.execute(callback);
        return call;
    }

    public static RequestCall crtrs(
            String style,String money,String time,String rate,
            Callback callback) {
        Map<String, String> map = getSortMap();
        map.put("style",style);
        map.put("style",style);
        map.put("money",money);
        map.put("time",time);
        map.put("rate",rate);
        RequestCall call = OkHttpUtils
                .get()
                .addParams("style",style)
                .addParams("money",money)
                .addParams("time",time)
                .addParams("rate",rate)
                .addParams("sign", sign(map))
                .url(crtrs)
                .build();
        call.execute(callback);
        return call;
    }

    public static RequestCall getMita(String Imageurl,Callback callback){
        RequestCall call = OkHttpUtils
                .get()//
                .url(Imageurl)//
                .build();
        call.execute(callback);
        return call;
    }
}