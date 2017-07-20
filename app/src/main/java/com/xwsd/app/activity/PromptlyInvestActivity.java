package com.xwsd.app.activity;

import android.content.*;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.gnwai.iosdialog.AlertDialog;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.api.XWSDRequestAdresse;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.AgreeCardBean;
import com.xwsd.app.constant.BroadcastParam;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.EmptyLayout;
import com.xwsd.app.view.NavbarManage;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * Created by Gx on 2016/8/24.
 * 立即支付
 */
public class PromptlyInvestActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    public final static int TYPE_ALL_BID = 0;

    public final static int TYPE_TRANSFER = 1;

    private int type;

    private String id;

    private float oddMoneyLast;

    RequestCall call;

    private float maxMoney;

    private String investMoney;

    @Bind(R.id.error_layout)
    EmptyLayout error_layout;

    @Bind(R.id.tv_money)
    TextView tv_money;

    @Bind(R.id.tv_balance)
    TextView tv_balance;

    @Bind(R.id.et_money)
    EditText et_money;
    @Bind(R.id.ll_red_packet)
    LinearLayout ll_red_packet;

    private AgreeCardBean agreeCardBeanBaofu;
    private AgreeCardBean agreeCardBeanFuyou;
    Intent intent;
    private boolean isCard = false;
    /**
     * 广播接收者，用于处理相对应的Action
     */
    private MyBroadcastReciever myBroadcastReciever = new MyBroadcastReciever();

    DecimalFormat decimalFormat = new DecimalFormat("0.00");

    /**
     * 广播类(用来更新数据)
     */
    class MyBroadcastReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {

                case BroadcastParam.BUY_BID:
                    getData();
                    break;
                case BroadcastParam.BUY_CREDITORS:
                    getData();
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解绑广播
        unregisterReceiver(myBroadcastReciever);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        intent = getIntent();
        type = intent.getIntExtra(UserParam.TYPE, TYPE_ALL_BID);
        id = intent.getStringExtra(UserParam.DATA);
        if(type == TYPE_ALL_BID){
            setContentView(R.layout.activity_promptly_invest);
        }else{
            setContentView(R.layout.activity_promptly_invest_transfer);
        }
        investMoney = intent.getStringExtra("investMoney");
//        oddMoneyLast =intent.getFloatExtra(UserParam.MONEY,0);

        navbarManage = new NavbarManage(this);
        navbarManage.setCentreStr(intent.getStringExtra(UserParam.TITLE));
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        title=intent.getStringExtra(UserParam.TITLE);
        //设置导航栏
        navbarManage.showLeft(true);
        navbarManage.showRight(false);
        navbarManage.setLeftImg(R.mipmap.ic_back_b);
        navbarManage.setBackground(R.color.navbar_bg);
        navbarManage.setOnLeftClickListener(new NavbarManage.OnLeftClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }
        });






        error_layout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastParam.BUY_BID);
        intentFilter.addAction(BroadcastParam.BUY_CREDITORS);
        registerReceiver(myBroadcastReciever, intentFilter);

        //                判断用户是否登录
        if (AppContext.getUserBean() == null||null==AppContext.getUserBean().data) {
            Intent intentUser = new Intent(this, UserActivity.class);
            intentUser.putExtra(UserParam.TYPE, UserActivity.TYPE_LOGIN);
            intentUser.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
            startActivity(intentUser);
            return;
        }

        getData();
//        // 宝付
//        agreeCard("baofoo");
//        //富有
//        agreeCard("fuiou");
    }

    private void getData() {
        error_layout.setErrorType(EmptyLayout.NETWORK_LOADING);
        //获取最大可投金额
        if (type == TYPE_ALL_BID) {
            ApiHttpClient.getMaxInvest(AppContext.getUserBean().data.userId, id, new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    error_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }

                @Override
                public void onResponse(String response, int id) {
                    TLog.error("获取标最大可投金额：" + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getInt("status") == 1) {
                            error_layout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                            maxMoney = (float) jsonObject.getJSONObject("data").getDouble("money");
                            oddMoneyLast = (float) jsonObject.getJSONObject("data").getDouble("oddMoneyLast");
                            setData();
                        } else {
                            error_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        error_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
                    }
                }
            });
        } else {
            ApiHttpClient.getMaxBuy(AppContext.getUserBean().data.userId, id, new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    error_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }

                @Override
                public void onResponse(String response, int id) {
                    TLog.error("获取债权最大可投金额：" + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getInt("status") == 1) {
                            error_layout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                            maxMoney = (float) jsonObject.getJSONObject("data").getDouble("money");
                            oddMoneyLast = (float) jsonObject.getJSONObject("data").getDouble("moneyLast");
                            setData();
                        } else {
                            error_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        error_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
                    }
                }
            });
        }
    }

    private void setData() {
        tv_money.setText(decimalFormat.format(oddMoneyLast));
        tv_balance.setText(AppContext.getUserBean().data.fundMoney);
        et_money.setText(investMoney);
    }

    @OnClick({R.id.bt_pay, R.id.commit,R.id.bt_pay_max,R.id.ll_red_packet})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_pay:
//                //                充值前，先判断是否进行了实名认证
//                if (!AppContext.getUserBean().data.cardstatus.equals(ApiHttpClient.YES)) {
//                    new AlertDialog(PromptlyInvestActivity.this)
//                            .builder()
//                            .setTitle("温馨提示：")
//                            .setMsg("您尚未进行实名认证，是否前往认证。")
//                            .setPositiveButton("前往", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent(PromptlyInvestActivity.this, CertificationActivity.class);
//                                    startActivity(intent);
//                                }
//                            })
//                            .setNegativeButton("取消", new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//                                }
//                            }).show();
//
//                    return;
//                }

//                充值前，先判断是否开通了存管
                if(AppContext.getUserBean().data.custodyId==null||"0".equals(AppContext.getUserBean().data.custodyId)||"".equals(AppContext.getUserBean().data.custodyId)){
                    new AlertDialog(PromptlyInvestActivity.this)
                            .builder()
                            .setTitle("温馨提示：")
                            .setMsg("您尚未开通存管，是否前往开通。")
                            .setPositiveButton("前往", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(PromptlyInvestActivity.this, OpenDepositoryActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).show();
                    return;
                }

//                充值前，先判断是否认证了充值银行卡
                showWaitDialog(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (call != null) {
                            call.cancel();
                        }
                    }
                });
                hideWaitDialog();
                if(isCard){
                   Intent intent = new Intent(this, AccountRechargeActivity.class);

                    startActivity(intent);
                }else {
                    new AlertDialog(this)
                            .builder()
                            .setTitle("温馨提示：")
                            .setMsg("您尚未设置充值银行卡，是否前往设置。")
                            .setPositiveButton("前往", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(PromptlyInvestActivity.this, SettingsPayBankActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).show();
                }
                break;

            case R.id.commit:
                if (TextUtils.isEmpty(et_money.getText().toString().trim())) {
                    ToastUtil.showToastShort("请输入购买金额");
                    return;
                }

                if (Float.valueOf(et_money.getText().toString().trim()) < 50f) {
                    ToastUtil.showToastShort("起投金额不能低于50元");
                    return;
                }

                if (Float.valueOf(et_money.getText().toString().trim()) > maxMoney) {
                    ToastUtil.showToastShort("您最多可投" + maxMoney + "元");
                    return;
                }

                if (maxMoney - Float.valueOf(et_money.getText().toString().trim()) > 0 &&
                        maxMoney - Float.valueOf(et_money.getText().toString().trim()) < 50f) {
                    ToastUtil.showToastShort("剩余金额不能低于50元");
                    return;
                }
                Intent intent = new Intent(PromptlyInvestActivity.this, WebDetailsActivity.class);
                String url;
                if (type == TYPE_ALL_BID) {
                    intent.putExtra(UserParam.TITLE, "投标");
                    Map<String, String> map = ApiHttpClient.getSortMap();
                    map.put("userId", AppContext.getUserBean().data.userId);
                    map.put("oddNumber", id);
                    map.put("money", et_money.getText().toString().trim());

                    url = XWSDRequestAdresse.BID + "?userId=" + AppContext.getUserBean().data.userId +
                            "&oddNumber=" + id + "&money=" + et_money.getText().toString().trim() +
                            "&sign=" + ApiHttpClient.sign(map);
                } else {
                    intent.putExtra(UserParam.TITLE, "购买债权");
                    Map<String, String> map = ApiHttpClient.getSortMap();
                    map.put("userId", AppContext.getUserBean().data.userId);
                    map.put("id", id);
                    map.put("money", et_money.getText().toString().trim());

                    url = XWSDRequestAdresse.BUY + "?userId=" + AppContext.getUserBean().data.userId +
                            "&id=" + id + "&money=" + et_money.getText().toString().trim() +
                            "&sign=" + ApiHttpClient.sign(map);
                }

                intent.putExtra(UserParam.URL, url);
                startActivity(intent);
                break;
            case R.id.bt_pay_max:
                String maxMoney = (this.maxMoney)+"";
                et_money.setText(maxMoney);
                break;
            case R.id.ll_red_packet:
                Intent redIntent = new Intent(this, RedPacketActivity.class);
//                redIntent.putExtra(UserParam.DATA, agreeCardBeanBaofu.data.agreeCard);
//                redIntent.putExtra(UserParam.DATA2, agreeCardBeanFuyou.data.agreeCard);
                startActivity(redIntent);
                break;
        }
    }

    private void agreeCard(final String flag){
        call = ApiHttpClient.agreeCard(AppContext.getUserBean().data.userId,flag, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                hideWaitDialog();
                ToastUtil.showToastShort(R.string.network_exception);
            }
            @Override
            public void onResponse(String response, int id) {
                TLog.error("获取认证银行：" + response);
                hideWaitDialog();
//                        这边捕获下异常，data.agreeCard为fals
                try {
//                            将充值银行卡的信息传给充值页面
                    if(flag.equals("baofoo")){
                        agreeCardBeanBaofu = GsonUtils.jsonToBean(response, AgreeCardBean.class);
                        isCard = true;
                    }else{
                        agreeCardBeanFuyou = GsonUtils.jsonToBean(response, AgreeCardBean.class);
                        isCard = true;
                    }
                } catch (Exception e) {
                    isCard = false;

                }
            }
        });
    }
}
