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
import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.BankCardsBean;
import com.xwsd.app.constant.BroadcastParam;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.NavbarManage;
import com.xwsd.app.view.WheelView;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import me.drakeet.materialdialog.MaterialDialog;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gx on 2016/8/29.
 * 提现
 */
public class WithdrawActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    RequestCall call;

    MaterialDialog bankDialog;

    BankCardsBean banks;

    /**
     * 选中的银行卡
     */
    BankCardsBean.records checkedBank;

    @Bind(R.id.tv_bank)
    TextView tv_bank;

    @Bind(R.id.et_money)
    EditText et_money;

    @Bind(R.id.tv_poundage)
    TextView tv_poundage;

    @Bind(R.id.et_name)
    EditText et_name;

    @Bind(R.id.tv_balance)
    TextView tv_balance;

    @Bind(R.id.get_ismoney)
    CheckBox get_ismoney;

    @Bind(R.id.money_ticket)
    RelativeLayout money_ticket;

    Dialog payDialog;

    DecimalFormat decimalFormat = new DecimalFormat("0.0");

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_withdraw);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
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
        if (AppContext.getUserBean() == null||null==AppContext.getUserBean().data) {
            Intent intent = new Intent(this, UserActivity.class);
            intent.putExtra(UserParam.TYPE, UserActivity.TYPE_LOGIN);
            intent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
            startActivity(intent);
            return;
        }
//        设置开户名
        et_name.setText(AppContext.getUserBean().data.name);
//        设置余额
        tv_balance.setText("当前余额：" + getIntent().getSerializableExtra(UserParam.MONEY) + "  元");
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
                        et_money.setText(getIntent().getSerializableExtra(UserParam.MONEY).toString());
                        Editable etext = et_money.getText();
                        Selection.setSelection(etext, etext.length());
                        return;
                    }
//                    获取手续费
                    setPoundage(s.toString());
                } else {
                    tv_poundage.setText("0");
                }
            }
        });
        getBanks(1);
    }

    @OnClick({R.id.commit, R.id.ll_banks, R.id.bt_all_withdraw, R.id.et_money})
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

                if (decimalFormat.format(Float.valueOf(et_money.getText().toString().trim())).equals("0.00")) {
                    ToastUtil.showToastShort(R.string.withdraw_money_small);
                    return;
                }

                showPayDialog();
                break;
            case R.id.ll_banks://选择提现银行卡
                BuriedPointUtil.buriedPoint("提现页面-选择提现银行卡");
                if (banks == null) {
                    getBanks(0);
                } else {
                    showBankDialog(banks);
                }
                break;
            case R.id.bt_all_withdraw://全部提现
                BuriedPointUtil.buriedPoint("提现页面-全部提现");
                et_money.setText(getIntent().getSerializableExtra(UserParam.MONEY).toString());
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
                        if(!get_ismoney.isChecked()){
                            tv_poundage.setText(jsonObject.getJSONObject("data").getString("fee"));
                        }else{
                            tv_poundage.setText("0");
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
        call = ApiHttpClient.getBankCards(AppContext.getUserBean().data.userId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
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
                        banks = GsonUtils.jsonToBean(response, BankCardsBean.class);
//                        判断是否有可操作的银行卡
                        if (banks.data.records != null && banks.data.records.size() > 0) {
                            if(type == 0){
                                showBankDialog(banks);
                            }else {
                                tv_bank.setText(banks.data.records.get(0).bankName + "（" + "***" + banks.data.records.get(0).bankNum.substring(banks.data.records.get(0).bankNum.length() - 4, banks.data.records.get(0).bankNum.length()) + "）");
                                checkedBank = banks.data.records.get(0);
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

    /**
     * 弹出银行选择对话框
     */
    private void showBankDialog(BankCardsBean bean) {
//        隐藏软键盘
        hideSoftKeyboard(getCurrentFocus());
        if (bankDialog == null) {
            View outerView = LayoutInflater.from(WithdrawActivity.this).inflate(R.layout.view_wheel, null);
            final WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
            List<String> strings = new ArrayList();
            for (BankCardsBean.records data : bean.data.records.subList(0, 1)) {
                strings.add(data.bankName + "（" + "***" + data.bankNum.substring(data.bankNum.length() - 4, data.bankNum.length()) + "）");
            }
            wv.setOffset(2);
            wv.setItems(strings);
            wv.setSeletion(0);
            bankDialog = new MaterialDialog(WithdrawActivity.this).setTitle(R.string.banks_select)
                    .setContentView(outerView)
                    .setPositiveButton(getString(R.string.confirm),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    bankDialog.dismiss();
                                    checkedBank = getCheckedBank(banks, wv.getSeletedItem());
                                    tv_bank.setText(wv.getSeletedItem());
                                    showTicket();
                                }
                            })
                    .setCanceledOnTouchOutside(true);
        }
        bankDialog.show();
    }
    private void showTicket(){
        if(banks.data.lotteryCount > 0){
            money_ticket.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取选中的银行
     *
     * @param bankCardsBean
     * @param str
     * @return
     */
    private BankCardsBean.records getCheckedBank(BankCardsBean bankCardsBean, String str) {
        String bankName = str.split("（")[0];
        for (BankCardsBean.records data : bankCardsBean.data.records) {
            if (data.bankName.equals(bankName)) {
                return data;
            }
        }
        return null;
    }

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
                    withdraw(et_money.getText().toString().trim(),
                            editText.getText().toString().trim(),
                            checkedBank.id);
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
     * @param bankId      银行卡ID
     * @param payPassword 支付密码
     */
    private void withdraw(String money, String payPassword, String bankId) {

        showWaitDialog(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (call != null) {
                    call.cancel();
                }
            }
        });
        String isLottery;
        if(get_ismoney.isChecked()){
            isLottery = "1";
            tv_poundage.setText("0");
        }else{
            isLottery = "0";
        }
        call = ApiHttpClient.withdraw(AppContext.getUserBean().data.userId, money, payPassword, bankId,isLottery, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hideWaitDialog();
                ToastUtil.showToastShort(R.string.network_exception);
            }

            @Override
            public void onResponse(String response, int id) {
                hideWaitDialog();
                TLog.error("提现：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    ToastUtil.showToastShort(jsonObject.getString("msg"));
                    if (jsonObject.getInt("status") == 1) {

//                        发送广播通知提现成功
                        Intent intent = new Intent();
                        intent.setAction(BroadcastParam.ACCOUNT_UPDATE_WITHDRAW);
                        sendBroadcast(intent);
                        finish();

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.showToastShort(getString(R.string.network_exception));
                }
            }
        });
    }
}
