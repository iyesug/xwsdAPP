package com.xwsd.app.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import butterknife.Bind;
import butterknife.OnClick;
import com.gnwai.iosdialog.AlertDialog;
import com.xwsd.app.AppContext;
import com.xwsd.app.AppManager;
import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.BankCardBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.NavbarManage;
import com.xwsd.app.view.SwitchView;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import me.drakeet.materialdialog.MaterialDialog;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * Created by Gx on 2016/8/29.
 * 提现
 */
public class WithdrawActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;
    public static boolean needRefresh = false;
    RequestCall call;

    MaterialDialog bankDialog;

    BankCardBean banks;
    float bigAmount= (float) 50000;
    boolean big=false;
    /**
     * 选中的银行卡
     */
    BankCardBean checkedBank;

    @Bind(R.id.tv_bank)
    TextView tv_bank;

    @Bind(R.id.et_money)
    EditText et_money;

    @Bind(R.id.et_cashBankNum)
    EditText et_cashBankNum;

    @Bind(R.id.tv_poundage)
    TextView tv_poundage;

    @Bind(R.id.et_name)
    EditText et_name;

    @Bind(R.id.tv_balance)
    TextView tv_balance;

    @Bind(R.id.tv_true)
    TextView tv_true;

    @Bind(R.id.toggle_button)
    SwitchView toggle_button;

    @Bind(R.id.money_ticket)
    RelativeLayout money_ticket;


    @Bind(R.id.ll_big)
    LinearLayout ll_big;

    @Bind(R.id.et_how_get_bankNum)
    ImageView et_how_get_bankNum;

    Dialog payDialog;

    DecimalFormat decimalFormat = new DecimalFormat("0.00");

    String fee="0";
    String tureMoney="0";
    String allMoney="0";
    String withdrawMoney="0";

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_withdraw);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        title=getString(R.string.withdraw);
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.withdraw));
        navbarManage.showLeft(true);
        navbarManage.showRight(false);
        navbarManage.setBackground(R.color.navbar_bg);
        navbarManage.setLeftImg(R.mipmap.ic_back_b);
        navbarManage.setOnLeftClickListener(new NavbarManage.OnLeftClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }
        });
//        判断用户是否登录
        if (AppContext.getUserBean() == null||null== AppContext.getUserBean().data) {
            Intent intent = new Intent(this, UserActivity.class);
            intent.putExtra(UserParam.TYPE, UserActivity.TYPE_LOGIN);
            intent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
            startActivity(intent);
            return;
        }
        et_how_get_bankNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WithdrawActivity.this, GetBankNumInfoActivity.class);
                startActivity(intent);

            }
        });

        //提现卷开关监听

        toggle_button.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                toggle_button.toggleSwitch(true);

                if(!"0".equals(fee)){
                    fee=(Float.parseFloat(fee)-2)+"";
                    tv_poundage.setText(fee);
                    tureMoney=decimalFormat.format(Float.parseFloat(withdrawMoney)-Float.parseFloat(fee));
                    tv_true.setText(tureMoney);
                }
                tv_true.setText(tureMoney);
            }

            @Override
            public void toggleToOff() {
                toggle_button.toggleSwitch(false);

                //获取手续费
                String s=et_money.getText().toString().trim();
                if("".equals(s)){
                    withdrawMoney="0";
                    fee="0";
                    tureMoney="0";
                    tv_poundage.setText(fee);
                    tv_true.setText(tureMoney);

                }else{
                    withdrawMoney=s.toString();
                    setPoundage(withdrawMoney);
                }
            }
        });


//        设置开户名
        et_name.setText(AppContext.getUserBean().data.name);
//        设置余额
        allMoney=getIntent().getSerializableExtra(UserParam.MONEY)+"";
        tv_balance.setText(allMoney);
        et_money.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                /*隐藏软键盘*/
                    hideSoftKeyboard(getCurrentFocus());

                    return true;
                }
                return false;
            }
        });

//        et_money.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(!hasFocus){
//
//                }
//
//            }
//        });

//        设置文本监听
        et_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


                if (s.length() > 0) {
//                    提现金额不能大于余额
                    if (Float.valueOf(s.toString()) > Float.valueOf(getIntent().getSerializableExtra(UserParam.MONEY).toString())) {
                        ToastUtil.showToastShort(R.string.withdraw_money_exceed);
                        withdrawMoney=getIntent().getSerializableExtra(UserParam.MONEY).toString();
                        et_money.setText(withdrawMoney);
                        Editable etext = et_money.getText();
                        Selection.setSelection(etext, etext.length());
                        return;
                    }
//                    获取手续费
                    withdrawMoney=s.toString();
                    setPoundage(withdrawMoney);
                    if("".equals(s)){
                        withdrawMoney="0";
                        fee="0";
                        tv_poundage.setText(fee);
                        tureMoney="0";
                        tv_true.setText(tureMoney);
                    }

                    if(Float.valueOf(s.toString()) >= bigAmount){
                        ll_big.setVisibility(View.VISIBLE);
                        big=true;
                    }
                    if(Float.valueOf(s.toString()) < bigAmount){
                        ll_big.setVisibility(View.GONE);
                        big=false;
                    }
                } else {
                    withdrawMoney="0";
                    fee="0";
                    tv_poundage.setText(fee);
                    tureMoney="0";
                    tv_true.setText(tureMoney);
                }
            }
        });
        getBanks(1);
    }

    @OnClick({R.id.commit,  R.id.bt_all_withdraw, R.id.et_money})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit://提现
                BuriedPointUtil.buriedPoint("提现页面-提现按钮");
                if (checkedBank == null) {
                    ToastUtil.showToastShort(R.string.withdraw_bank_card_null);
                    return;
                }

                if (TextUtils.isEmpty(et_money.getText().toString().trim())) {
                    ToastUtil.showToastShort(R.string.withdraw_money_null);
                    return;
                }
                if (big&&TextUtils.isEmpty(et_cashBankNum.getText().toString().trim())) {
                    ToastUtil.showToastShort(R.string.bank_num_null);
                    return;
                }
                if (decimalFormat.format(Float.valueOf(et_money.getText().toString().trim())).equals("0.00")) {
                    ToastUtil.showToastShort(R.string.withdraw_money_small);
                    return;
                }

                if(Float.parseFloat(tureMoney)<1){
                    ToastUtil.showToastShort(R.string.money_no_one);
                    return;
                }
                withdraw(et_money.getText().toString().trim());
//                showPayDialog();
                break;
//            case R.id.ll_banks://选择提现银行卡
//                BuriedPointUtil.buriedPoint("提现页面-选择提现银行卡");
//                if (banks == null) {
//                    getBanks(0);
//                } else {
//                    showBankDialog(banks);
//                }
//                break;
            case R.id.bt_all_withdraw://全部提现
                BuriedPointUtil.buriedPoint("提现页面-全部提现");
                withdrawMoney=getIntent().getSerializableExtra(UserParam.MONEY).toString();
                et_money.setText(withdrawMoney);
                break;
            case R.id.et_money:
                BuriedPointUtil.buriedPoint("提现页面-写入提现金额");
                break;
        }
    }

    /**
     * 设置手续费
     */
    private void setPoundage(String money) {
        if (call != null) {
            call.cancel();
        }
        call = ApiHttpClient.withdrawFee(AppContext.getUserBean().data.userId, money, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (!call.isCanceled()) {
                    ToastUtil.showToastShort(R.string.network_exception);
                }
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("获取提现手续费：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        if(toggle_button.getState() != SwitchView.STATE_SWITCH_ON){
                            fee=jsonObject.getJSONObject("data").getString("fee");
                            tv_poundage.setText(fee);
                            tureMoney=decimalFormat.format(Float.parseFloat(withdrawMoney)-Float.parseFloat(fee));
                            tv_true.setText(tureMoney);
                        }else{
                            fee=Float.parseFloat(jsonObject.getJSONObject("data").getString("fee"))-2+"";
                            tv_poundage.setText(fee);
                            tureMoney=decimalFormat.format(Float.parseFloat(withdrawMoney)-Float.parseFloat(fee));
                            tv_true.setText(tureMoney);
                        }

                    } else {
                        ToastUtil.showToastShort(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.showToastShort(getString(R.string.network_exception));
                }
            }
        });
    }

    /**
     * 得到银行列表
     */
    private void getBanks(final int type) {
        showWaitDialog(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (call != null) {
                    call.cancel();
                }
            }
        });
        call = ApiHttpClient.getBankCard(AppContext.getUserBean().data.userId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                TLog.error("银行卡列表：" + e);
                hideWaitDialog();
                ToastUtil.showToastShort(R.string.network_exception);
            }

            @Override
            public void onResponse(String response, int id) {
                hideWaitDialog();
                TLog.error("获取用户所有银行卡：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        banks = GsonUtils.jsonToBean(response, BankCardBean.class);

//                        判断是否有可操作的银行卡
                        if (banks.data != null && !"0".equals(banks.data.id )) {
                            checkedBank=banks;
                            if(type == 0){
                            }else {
                                if(!"".equals(checkedBank.data.binInfo)){
                                    String bankinfo=checkedBank.data.binInfo;
                                    String[] s=bankinfo.split("-");
                                    tv_bank.setText(s[0]+"");
                                }



//                                ApiHttpClient.lodCircleImg(tv_bank, banks.data.bankIco, R.drawable.ic_load, R.drawable.ic_load);
                                showTicket();
                            }
                        } else {
                            banks = null;
                            new AlertDialog(WithdrawActivity.this)
                                    .builder()
                                    .setTitle("温馨提示：")
                                    .setMsg("您没有可提现的银行卡，是否前往添加。")
                                    .setPositiveButton("前往", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(WithdrawActivity.this, AddCardActivity.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("取消", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    }).show();
                        }
                    } else {
                        ToastUtil.showToastShort(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.showToastShort(getString(R.string.network_exception));
                }
            }
        });


    }

//    /**
//     * 弹出银行选择对话框
//     */
//    private void showBankDialog(BankCardsBean bean) {
////        隐藏软键盘
//        hideSoftKeyboard(getCurrentFocus());
//        if (bankDialog == null) {
//            View outerView = LayoutInflater.from(WithdrawActivity.this).inflate(R.layout.view_wheel, null);
//            final WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
//            List<String> strings = new ArrayList();
//            for (BankCardsBean.records data : bean.data.records.subList(0, 1)) {
//                strings.add(data.bankName + "（" + "***" + data.bankNum.substring(data.bankNum.length() - 4, data.bankNum.length()) + "）");
//            }
//            wv.setOffset(2);
//            wv.setItems(strings);
//            wv.setSeletion(0);
//            bankDialog = new MaterialDialog(WithdrawActivity.this).setTitle(R.string.banks_select)
//                    .setContentView(outerView)
//                    .setPositiveButton(getString(R.string.confirm),
//                            new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    bankDialog.dismiss();
//                                    checkedBank = getCheckedBank(banks, wv.getSeletedItem());
//                                    tv_bank.setText(wv.getSeletedItem());
//                                    showTicket();
//                                }
//                            })
//                    .setCanceledOnTouchOutside(true);
//        }
//        bankDialog.show();
//    }
    private void showTicket(){
        if(banks.data.lotteryCount > 0){
            money_ticket.setVisibility(View.VISIBLE);
        }
    }

//    /**
//     * 获取选中的银行
//     *
//     * @param bankCardsBean
//     * @param str
//     * @return
//     */
//    private BankCardsBean.records getCheckedBank(BankCardBean bankCardsBean, String str) {
//        String bankName = str.split("（")[0];
//        for (BankCardBean data : bankCardBean.data.records) {
//            if (data.bankName.equals(bankName)) {
//                return data;
//            }
//        }
//        return null;
//    }

    /**
     * 显示支付密码对话框
     */
    private void showPayDialog() {
        //        隐藏软键盘
        hideSoftKeyboard(getCurrentFocus());

        if (payDialog == null) {
            payDialog = new Dialog(WithdrawActivity.this, R.style.BankDialog);
            View view = getLayoutInflater().inflate(R.layout.dialog_pay, null);
            final EditText editText = (EditText) view.findViewById(R.id.et_password);

            view.findViewById(R.id.tv_forget_password).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WithdrawActivity.this, UserActivity.class);
                    intent.putExtra(UserParam.TYPE, UserActivity.TYPE_FORGET_PAYPASS);
                    startActivity(intent);
                }
            });

            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    payDialog.dismiss();
                }
            });

            view.findViewById(R.id.commit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                        ToastUtil.showToastShort(R.string.pay_pasworrd_null);
                        return;
                    }
                    payDialog.dismiss();
                    withdraw(et_money.getText().toString().trim());
                }
            });
            payDialog.setContentView(view);

            Window window = payDialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.CENTER;
            lp.width = window.getWindowManager().getDefaultDisplay().getWidth() - 100;
            lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            payDialog.getWindow().setAttributes(lp);

            payDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    editText.setText("");
                }
            });
        }

        payDialog.show();
    }

    /**
     * 提现
     *
     * @param money       提现金额
     *
     *
     */
    private void withdraw(String money ) {

        showWaitDialog(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (call != null) {
                    call.cancel();
                }
            }
        });
        String isLottery;
        if(toggle_button.getState() == SwitchView.STATE_SWITCH_ON ){
            isLottery = "1";

        }else{
            isLottery = "0";
        }
        String big;
        if(this.big){
            big="1";
        }else{
            big="0";
        }

        //跳转到充值页面
        Intent intent = new Intent(this, WebApproveActivity.class);
        Map<String, String> map = ApiHttpClient.getSortMap();
        map.put("userId", AppContext.getUserBean().data.userId);
        map.put("money", money);
        map.put("type", big);
        map.put("cashBankNum", et_cashBankNum.getText().toString().trim());
        map.put("isLottery", isLottery);

        map.put("media", "Android");
        intent.putExtra(UserParam.URL, ApiHttpClient.WITHDRAW +
                "?userId=" + AppContext.getUserBean().data.userId +
                "&money=" + money +
                "&type=" + big +
                "&cashBankNum=" + et_cashBankNum.getText().toString().trim() +
                "&isLottery=" + isLottery +
                "&media=" + "Android" +
                "&sign=" + ApiHttpClient.sign(map));
        TLog.error("url:" + ApiHttpClient.WITHDRAW +
                "?userId=" + AppContext.getUserBean().data.userId +
                "&money=" + money +
                "&media=" + "Android" +
                "&sign=" + ApiHttpClient.sign(map));
        intent.putExtra(UserParam.TITLE, getString(R.string.withdraw));
        startActivity(intent);
        AppManager.getAppManager().finishActivity();


//        call = ApiHttpClient.withdraw(AppContext.getUserBean().data.userId, money, payPassword, bankId,isLottery, new StringCallback() {
//            @Override
//            public void onError(Call call, Exception e, int id) {
//                hideWaitDialog();
//                ToastUtil.showToastShort(R.string.network_exception);
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//                hideWaitDialog();
//                TLog.error("提现：" + response);
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    ToastUtil.showToastShort(jsonObject.getString("msg"));
//                    if (jsonObject.getInt("status") == 1) {
//
////                        发送广播通知提现成功
//                        Intent intent = new Intent();
//                        intent.setAction(BroadcastParam.ACCOUNT_UPDATE_WITHDRAW);
//                        sendBroadcast(intent);
//                        finish();
//
//                    } else {
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    ToastUtil.showToastShort(getString(R.string.network_exception));
//                }
//            }
//        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needRefresh) {
            getBanks(1);
            needRefresh = false;
        }
    }
}
