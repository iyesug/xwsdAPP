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
import com.baofoo.sdk.vip.BaofooPayActivity;
import com.fuiou.pay.util.AppConfig;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.activity.RechargeActivity;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.bean.AgreeCardBean;
import com.xwsd.app.bean.BanksLimitBean;
import com.xwsd.app.bean.BaofuBean;
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

public class baofuMoneyFragment extends Fragment implements View.OnClickListener{

   // public RequestOrder requestOrder;

    private TextView tv_bank;
    private LinearLayout ll_quota_stroke;
    private LinearLayout ll_quota_day;
    private EditText et_money;
    private Button commit;

    public final static int REQUEST_CODE_BAOFOO_SDK = 100;
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

        View view = inflater.inflate(R.layout.fragment_baofu, container, false);

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
        BuriedPointUtil.buriedPoint("充值页面-宝付支付");
        data = (AgreeCardBean.Data.AgreeCard)getActivity().getIntent().getSerializableExtra(UserParam.DATA);
//        requestOrder = new RequestOrder(getActivity());//富友支付RequestOrder
//        FyPay.init(getActivity());//初始化富友支付

//        得到银行卡信息
//        设置数据
        bankName = data.bank_name;
        tv_bank.setText(bankName);
        banksLimitBean = getBanksLimit(data.bank_code);
        if(banksLimitBean != null){
            ((TextView) ll_quota_stroke.findViewById(R.id.tv_title)).setText(R.string.quota_stroke);
            ((TextView) ll_quota_day.findViewById(R.id.tv_title)).setText(R.string.quota_day);
            ((TextView) ll_quota_stroke.findViewById(R.id.tv_content)).setText(banksLimitBean.onceLimit);
            ((TextView) ll_quota_day.findViewById(R.id.tv_content)).setText(banksLimitBean.dayLimit);
        }else{
            ToastUtil.showToast("持银行卡不支持宝付充值");
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

            try{
                if (AppConfig.getData(getActivity(),
                        AppConfig.RSP_SDK_DATA).contains("<RESPONSECODE>0000")) {
//                  发送广播通知充值成功，更新用户数据
                    Intent intent = new Intent();
                    intent.setAction(BroadcastParam.ACCOUNT_UPDATE_RECHARGE);
                    getActivity().sendBroadcast(intent);
                    dayRechargemoney += Integer.parseInt(et_money.getText().toString());//充值成功，单日充值的金额往上加
                    ToastUtil.showToast("成功充值:" + et_money.getText().toString() + "元");

                } else {
                    ToastUtil.showToast("充值失败");
                }
            }catch (Exception e){
                ToastUtil.showToast("正在清理缓存。");
                reset();
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
                    Time t = new Time();
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

                    call = ApiHttpClient.recharge(AppContext.getUserBean().data.userId,
                            et_money.getText().toString().trim(),
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
                                            BaofuBean mBaofuBean = GsonUtils.jsonToBean(response, BaofuBean.class);
                    //                        通过startActivityForResult调用宝付支付SDK，需要传入两个参数
                                            if(mBaofuBean.data.retCode.equals("0000")){
                                                Intent payintent = new Intent(getActivity(), BaofooPayActivity.class);
                                                // 通过业务流水请求报文获得的交易号
                                                payintent.putExtra(BaofooPayActivity.PAY_TOKEN, mBaofuBean.data.tradeNo);
                                                // 标记是否为测试，传True为正式环境，不传或者传False则为测试调用
                                                payintent.putExtra(BaofooPayActivity. PAY_BUSINESS,true);
                                                getActivity().startActivityForResult(payintent, REQUEST_CODE_BAOFOO_SDK);
                                            }
                                            else {
                                                ToastUtil.showToast("银行卡信息校验失败");
                                            }
                                        } else {
                                            ToastUtil.showToast(jsonObject.getString("msg"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        ToastUtil.showToastShort(getString(R.string.network_exception));
                                    }
                                }
                            });
                    isCanClick = false;//点击之后再点击就不会跳转到富友界面
                }
                break;

            case R.id.et_money:
                BuriedPointUtil.buriedPoint("充值页面-写入充值金额");
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
  //      ........
        if (requestCode == REQUEST_CODE_BAOFOO_SDK) {
            String result = "", msg = "";
            if (data == null || data.getExtras() == null) {
                msg = "支付已被取消";
            } else {
                //result返回值判断 -1:失败  0:取消  1:成功  10:处理中
                        result = data.getExtras().getString(BaofooPayActivity.PAY_RESULT);
                msg = data.getExtras().getString(BaofooPayActivity.PAY_MESSAGE);
            }
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
