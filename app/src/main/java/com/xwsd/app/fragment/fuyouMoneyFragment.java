package com.xwsd.app.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fuiou.pay.FyPay;
import com.fuiou.pay.activity.RequestOrder;
import com.fuiou.pay.util.AppConfig;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.activity.RechargeActivity;
import com.xwsd.app.activity.UserActivity;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.bean.AgreeCardBean;
import com.xwsd.app.bean.BanksLimitBean;
import com.xwsd.app.bean.RechargeBean;
import com.xwsd.app.constant.BroadcastParam;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Created by mo on 2016/11/22.
 */

public class fuyouMoneyFragment extends Fragment implements View.OnClickListener{
    public RequestOrder requestOrder;

    private TextView tv_bank;
    private LinearLayout ll_quota_stroke;
    private LinearLayout ll_quota_day;
    private EditText et_money;
    private Button commit;


    String bankName;

    RequestCall call;

    AgreeCardBean.Data.AgreeCard data;
    private BanksLimitBean banksLimitBean;

    private boolean isCanClick = true;
    private boolean isAble = true;
    public static int dayRechargemoney = 0;//每日充值的金额
//    private SharedPreferences dayRechargeMoney;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fuyou, container, false);

        tv_bank = (TextView) view.findViewById(R.id.tv_bank);
        ll_quota_stroke = (LinearLayout) view.findViewById(R.id.ll_quota_stroke);
        ll_quota_day = (LinearLayout) view.findViewById(R.id.ll_quota_day);
        et_money = (EditText) view.findViewById(R.id.et_money);
        commit = (Button) view.findViewById(R.id.commit);
        commit.setOnClickListener(this);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void init() {
        BuriedPointUtil.buriedPoint("充值页面-富友支付");
        data = (AgreeCardBean.Data.AgreeCard)getActivity().getIntent().getSerializableExtra(UserParam.DATA2);
        requestOrder = new RequestOrder(getActivity());//富友支付RequestOrder
        FyPay.init(getActivity());//初始化富友支付

//        得到银行卡信息
//        设置数据
        bankName = data.bank_name;
        tv_bank.setText(bankName);
        banksLimitBean = getBanksLimit(data.bank_code);
        if(banksLimitBean != null) {
            ((TextView) ll_quota_stroke.findViewById(R.id.tv_title)).setText(R.string.quota_stroke);
            ((TextView) ll_quota_day.findViewById(R.id.tv_title)).setText(R.string.quota_day);
            ((TextView) ll_quota_stroke.findViewById(R.id.tv_content)).setText(banksLimitBean.onceLimit);
            ((TextView) ll_quota_day.findViewById(R.id.tv_content)).setText(banksLimitBean.dayLimit);
        }else{
            ToastUtil.showToast("此银行卡不支持宝付充值"  + data.bank_code);
            isAble = false;
            commit.setBackgroundResource(R.drawable.bg_button_sharp_gray);
        }
     }
    private BanksLimitBean getBanksLimit(String bank_code) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            //设置输入的内容
            xmlPullParser.setInput(getActivity().getAssets().open("BankBaoLimit.xml"), "UTF-8");
            //获取当前解析事件，返回的是数字
            int eventType = xmlPullParser.getEventType();
            //保存内容
            String BankCode = "";
            String dayLimit = "";
            String onceLimit = "";

            while (eventType != (XmlPullParser.END_DOCUMENT)) {
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    //开始解析XML
                    case XmlPullParser.START_TAG: {
                        //nextText()用于获取结点内的具体内容
                        if ("BankCode".equals(nodeName))
                            BankCode = xmlPullParser.nextText();
                        else if ("DayLimit".equals(nodeName))
                            dayLimit = xmlPullParser.nextText();
                        else if ("OnceLimit".equals(nodeName))
                            onceLimit = xmlPullParser.nextText();
                    }
                    break;
                    //结束解析
                    case XmlPullParser.END_TAG: {
                        if ("bank".equals(nodeName) && bank_code.equals(data.bank_code)) {
                            BanksLimitBean banksLimitBean = new BanksLimitBean();
                            banksLimitBean.bankCode = BankCode;
                            banksLimitBean.dayLimit = dayLimit;
                            banksLimitBean.onceLimit = onceLimit;
                            return banksLimitBean;
                        }
                    }
                    break;
                    default:
                        break;
                }
                //下一个
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 返回订单相关信息(手动退出)
     */
    @Override
    public void onStart() {

        isCanClick = true;//当从富友充值界面跳回此页面时，按钮又可以重新点击

        /**
         * 请求发送成功的返回数据 发起支付
         */
        TLog.error("Lj发送成功请求的返回数据：" + AppConfig.getData(getActivity(),
                AppConfig.RSP_SDK_DATA));

        if (!"".equals(AppConfig.getData(getActivity(),
                AppConfig.RSP_SDK_DATA))) {

            if (AppConfig.getData(getActivity(),
                    AppConfig.RSP_SDK_DATA).contains("<RESPONSECODE>0000")) {

//            发送广播通知充值成功，更新用户数据
                Intent intent = new Intent();
                intent.setAction(BroadcastParam.ACCOUNT_UPDATE_RECHARGE);
                getActivity().sendBroadcast(intent);

                ToastUtil.showToast("成功充值:" + et_money.getText().toString() + "元");

                if(!"".equals(et_money.getText().toString())){
                    dayRechargemoney += Integer.parseInt(et_money.getText().toString());//充值成功，单日充值的金额往上加
                }


            } else {
                ToastUtil.showToast("充值失败");
            }
        }

        reset();
        super.onStart();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:
                BuriedPointUtil.buriedPoint("充值页面-点击充值按钮");
                final RechargeActivity rechargeActivity = (RechargeActivity ) getActivity();
                //跳转按钮做下控制，只能点击一次
                if (isCanClick && isAble) {
                    if (TextUtils.isEmpty(et_money.getText().toString().trim())) {
                        ToastUtil.showToastShort(getString(R.string.pay_money_null));
                        return;
                    }

                    //判断一次充值金额是否超过限额
                    if (banksLimitBean.onceLimit.contains("W")) {
                        if (Integer.parseInt(et_money.getText().toString().trim()) > Integer.parseInt(banksLimitBean.onceLimit.split("W")[0]) * 10000) {
                            ToastUtil.showToast("充值金额超过单次限额");
                            return;
                        }
                    } else {
                        if (Integer.parseInt(et_money.getText().toString().trim()) > Integer.parseInt(banksLimitBean.onceLimit)) {
                            ToastUtil.showToast("充值金额超过单次限额");
                            return;
                        }
                    }

//        int thisTimeRechar = Integer.parseInt(et_money.getText().toString().trim());//本次要充值的金额数
//         本日上次已充的金额
//        dayRechargeMoney  = getSharedPreferences("dayRechargeMoney", Context.MODE_PRIVATE);
//        dayRechargeMoney.edit().putString("DayRecharge", String.valueOf(thisTimeRechar)).commit();


                    //判断每日充值的金额是否超过银行卡的每日限额
                    SharedPreferences bankDayLimit = getActivity().getSharedPreferences("bankDayLimit", Context.MODE_PRIVATE);
                    final Time t = new Time();
                    t.setToNow();
                    int lastmonth = t.month + 1;
                    final String str = t.year + "年" + lastmonth + "月" + t.monthDay + "日";
//        final String nowtime = bankDayLimit.getString("TodayTime", "").toString();
                    if (bankDayLimit.getString("TodayTime", "").toString().equals(str) == true) {
                        //判断限额中是否包含W字
                        if (banksLimitBean.dayLimit.contains("W")) {
                            if (dayRechargemoney + Integer.parseInt(et_money.getText().toString().trim()) > Integer.parseInt(banksLimitBean.dayLimit.split("W")[0]) * 10000) {
                                ToastUtil.showToast("充值金额超过每日限额");
                                return;
                            }
                        } else {
                            if (dayRechargemoney + Integer.parseInt(et_money.getText().toString().trim()) > Integer.parseInt(banksLimitBean.onceLimit)) {
                                ToastUtil.showToast("充值金额超过每日限额");
                                return;
                            }
                        }

                    } else {
                        bankDayLimit.edit()
                                .putString("TodayTime", str)
                                .commit();
                        //第二天，单日已充金额从0开始。
                        dayRechargemoney = 0;
                    }


//        充值
                    rechargeActivity.showWaitDialog(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            if (call != null) {
                                call.cancel();
                            }
                        }
                    });
                    //判断是否登陆
                    if (AppContext.getUserBean() == null||null==AppContext.getUserBean().data) {
                        Intent intent = new Intent(getActivity(), UserActivity.class);
                        intent.putExtra(UserParam.TYPE, UserActivity.TYPE_LOGIN);
                        intent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                        startActivity(intent);
                        return;
                    }
                    call = ApiHttpClient.recharge(AppContext.getUserBean().data.userId,
                            et_money.getText().toString().trim(),
                            ApiHttpClient.PAY_TYPE,
                            ApiHttpClient.PAY_WAY,
                            data.bank_code,
                            data.no_agree,
                            data.card_num,
                            new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    rechargeActivity.hideWaitDialog();
                                    ToastUtil.showToastShort(R.string.network_exception);
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    TLog.error("Lj充值：" + response);
                                    rechargeActivity.hideWaitDialog();
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        if (jsonObject.getInt("status") == 1) {
                                            isCanClick = false;//点击之后再点击就不会跳转到富友界面
                                            RechargeBean rechargeBean = GsonUtils.jsonToBean(response, RechargeBean.class);
//                                //TODO 充值 调用富友支付
//
//                                    String Sing = MD5UtilString.MD5Encode("02" + "|" + "2.0" + "|"
//                                            + rechargeBean.data.MCHNTCD + "|"
//                                            + rechargeBean.data.MCHNTORDERID + "|"
//                                            + rechargeBean.data.USERID + "|"
//                                            + rechargeBean.data.AMT + "|"
//                                            + rechargeBean.data.BANKCARD + "|" + rechargeBean.data.BACKURL + "|"
//                                            + rechargeBean.data.NAME + "|"
//                                            + rechargeBean.data.IDNO + "|"
//                                            + rechargeBean.data.IDTYPE + "|" + mMchnt_Key);

                                            AppConfig.setData(getActivity(), AppConfig.MCHNT_CD, rechargeBean.data.MCHNTCD);

                                            AppConfig.setData(getActivity(), AppConfig.MCHNT_AMT, String.valueOf(rechargeBean.data.AMT));

                                            AppConfig.setData(getActivity(), AppConfig.MCHNT_BACK_URL, rechargeBean.data.BACKURL);

                                            AppConfig.setData(getActivity(), AppConfig.MCHNT_BANK_NUMBER, rechargeBean.data.BANKCARD);

                                            AppConfig.setData(getActivity(), AppConfig.MCHNT_ORDER_ID, rechargeBean.data.MCHNTORDERID);

                                            AppConfig.setData(getActivity(), AppConfig.MCHNT_USER_IDCARD_TYPE, String.valueOf(rechargeBean.data.IDTYPE));

                                            AppConfig.setData(getActivity(), AppConfig.MCHNT_USER_ID, rechargeBean.data.USERID);

                                            AppConfig.setData(getActivity(), AppConfig.MCHNT_USER_IDNU, rechargeBean.data.IDNO);

                                            AppConfig.setData(getActivity(), AppConfig.MCHNT_USER_NAME, rechargeBean.data.NAME);

                                            AppConfig.setData(getActivity(), AppConfig.MCHNT_SING_KEY,
                                                    rechargeBean.data.SIGN);

                                            AppConfig.setData(getActivity(), AppConfig.MCHNT_SDK_SIGNTP,
                                                    "MD5");

                                            AppConfig.setData(getActivity(), AppConfig.MCHNT_SDK_TYPE,
                                                    "02");

                                            AppConfig.setData(getActivity(), AppConfig.MCHNT_SDK_VERSION,
                                                    "2.0");
                                            requestOrder.Request();

                                        } else {
                                            ToastUtil.showToast(jsonObject.getString("msg"));
                                            isCanClick = true;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        isCanClick = true;
                                        ToastUtil.showToastShort(getString(R.string.network_exception));
                                    }
                                }
                            });

                }
                break;

            case R.id.et_money:
                BuriedPointUtil.buriedPoint("充值页面-写入充值金额");
                break;
        }
    }
    /**
     * 清除数据
     */
    private void reset() {
        AppConfig.setData(getActivity(), AppConfig.RSP_CODE, "");
        AppConfig.setData(getActivity(), AppConfig.RSP_DESC, "");
        AppConfig.setData(getActivity(), AppConfig.RSP_SDK_DATA, "");

    }
}
