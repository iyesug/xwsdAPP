package com.xwsd.app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.AutoInfoBean;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.EmptyLayout;
import com.xwsd.app.view.NavbarManage;
import com.xwsd.app.view.SpinnerDialog;
import com.xwsd.app.view.SwitchView;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class AutoBidJianSettingActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    /**
     * 金额类型
     */
    private SpinnerDialog amountType;

    @Bind(R.id.tv_balance)
    TextView tv_balance;

    String autostatus = ApiHttpClient.CLOSE;

    String staystatus = ApiHttpClient.CLOSE;

    String moneyType = ApiHttpClient.CLOSE;

    RequestCall call;

    AutoInfoBean autoInfoBean;

    DecimalFormat decimalFormat = new DecimalFormat("0.0");
    /**
     * 类型
     */
    private String mode = "1";
    /**
     * 选择列表是否显示
     */


    @Bind(R.id.et_scope_money_1)
    EditText et_scope_money_1;

    @Bind(R.id.et_scope_money_2)
    EditText et_scope_money_2;

    @Bind(R.id.error_layout)
    EmptyLayout mErrorLayout;

    @Bind(R.id.tb_auto_bid)
    SwitchView tb_auto_bid;

    @Bind(R.id.cb_title1)
    CheckBox cb_title1;

    @Bind(R.id.cb_title2)
    CheckBox cb_title2;
    @Bind(R.id.cb_title3)
    CheckBox cb_title3;
    @Bind(R.id.cb_title4)
    CheckBox cb_title4;
    @Bind(R.id.cb_title5)
    CheckBox cb_title5;
    @Bind(R.id.cb_title6)
    CheckBox cb_title6;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_auto_bid_jian_setting);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        title=getString(R.string.auto_bid);
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.auto_bid));
        navbarManage.showLeft(true);
        navbarManage.showRight(true);
        navbarManage.setLeftImg(R.mipmap.ic_back_b);
        navbarManage.setRightStr("高级");
        navbarManage.setBackground(R.color.navbar_bg);
        navbarManage.setOnLeftClickListener(this::
                onBackPressed
        );

        navbarManage.setOnRightClickListener(() -> {
            BuriedPointUtil.buriedPoint("自动投标简版-高级");
            Intent intent = new Intent();
            intent.setClass(AutoBidJianSettingActivity.this,AutoBidSettingActivity.class);
            startActivity(intent);
            AutoBidJianSettingActivity.this.finish();
        });


        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                getData();
            }
        });

        getData();
    }

    /**
     * 获取设置数据
     */
    private void getData() {
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        if (call != null) {
            call.cancel();
        }

        call = ApiHttpClient.autoInfo(AppContext.getUserBean().data.userId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("获取自动投标设置：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        autoInfoBean = GsonUtils.jsonToBean(response, AutoInfoBean.class);
                        setData();
                    } else {
                        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                }
            }
        });
    }

    /**
     * 设置数据
     */
    private void setData() {
        //账户余额
        tv_balance.setText(autoInfoBean.data.fundMoney);
        //设置投资金额
            moneyType = ApiHttpClient.OPEN;
            et_scope_money_1.setText(autoInfoBean.data.autoInvest.investMoneyLower);
            et_scope_money_2.setText(autoInfoBean.data.autoInvest.investMoneyUper);

        //设置投资类型
        for (AutoInfoBean.Data.Types types : autoInfoBean.data.types) {

                for (String id : autoInfoBean.data.autoInvest.types) {
                    if (types.id.equals(id)) {
                        if(types.period == 1){
                            cb_title1.setChecked(true);
                        } else if(types.period == 2){
                            cb_title2.setChecked(true);
                        }else if(types.period == 3){
                            cb_title3.setChecked(true);
                        }else if(types.period == 6){
                            cb_title4.setChecked(true);
                        }else if(types.period == 12){
                            cb_title5.setChecked(true);
                        }else if(types.period == 24){
                            cb_title6.setChecked(true);
                        }
                    }

            }
        }
        //        设置自动投标
        if (autoInfoBean.data.autoInvest.autostatus == 1) {
            autostatus = ApiHttpClient.OPEN;
            tb_auto_bid.setState(true);
        } else {
            autostatus = ApiHttpClient.CLOSE;
            tb_auto_bid.setState(false);
        }
        tb_auto_bid.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                tb_auto_bid.toggleSwitch(true);
                autostatus = ApiHttpClient.OPEN;
                BuriedPointUtil.buriedPoint("自动投标设置开启自动投标");
            }

            @Override
            public void toggleToOff() {
                tb_auto_bid.toggleSwitch(false);
                autostatus = ApiHttpClient.CLOSE;
            }
        });
    }

    @OnClick({R.id.commit})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit://保存设置
                String rangeBegin;
                String rangeEnd;
                //得到保留金额
                    //得到投资范围
                    if (TextUtils.isEmpty(et_scope_money_1.getText().toString().trim())) {
                        ToastUtil.showToastShort(R.string.inpt_scope_money_1);
                        return;
                    }
                    if (TextUtils.isEmpty(et_scope_money_2.getText().toString().trim())) {
                        ToastUtil.showToastShort(R.string.inpt_scope_money_2);
                        return;
                    }

                    rangeBegin = et_scope_money_1.getText().toString().trim();
                    rangeEnd = et_scope_money_2.getText().toString().trim();
                showWaitDialog(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (call != null) {
                            call.cancel();
                        }
                    }
                });
                BuriedPointUtil.buriedPoint("自动投标简版-保存");
                String types=jointInvestType();

                call = ApiHttpClient.autoSet(AppContext.getUserBean().data.userId,
                        autostatus,
                        "0",
                        "0",
                        "1",
                        rangeBegin,
                        rangeEnd,
                        "100",
                        types,
                        mode,
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                hideWaitDialog();
                                ToastUtil.showToastShort(R.string.network_exception);
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                TLog.error("自动投标设置：" + response);
                                hideWaitDialog();
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    ToastUtil.showToastShort(jsonObject.getString("msg"));
                                    if (jsonObject.getInt("status") == 1) {

                                    } else {

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    ToastUtil.showToastShort(getString(R.string.network_exception));
                                }
                            }
                        });
                break;
        }
    }

    /**
     * 拼接投资类型的参数
     */
    private String jointInvestType() {
        StringBuffer typesStr = new StringBuffer();
        //        遍历所有元素，设置选中状态
        if(cb_title1.isChecked()){
            for (AutoInfoBean.Data.Types types : autoInfoBean.data.types) {
                    if (types.period == 1) {
                        typesStr.append(types.id);
                        typesStr.append(",");
                    }

            }
        }
        if(cb_title2.isChecked()){
            for (AutoInfoBean.Data.Types types : autoInfoBean.data.types) {

                    if (types.period == 2) {
                        typesStr.append(types.id);
                        typesStr.append(",");
                    }

            }
        }
        if(cb_title3.isChecked()){
            for (AutoInfoBean.Data.Types types : autoInfoBean.data.types) {

                    if (types.period == 3) {
                        typesStr.append(types.id);
                        typesStr.append(",");
                    }

            }
        }
        if(cb_title4.isChecked()){
            for (AutoInfoBean.Data.Types types : autoInfoBean.data.types) {
                    if (types.period == 6) {
                        typesStr.append(types.id);
                        typesStr.append(",");
                    }

            }
        }
        if(cb_title5.isChecked()){
            for (AutoInfoBean.Data.Types types : autoInfoBean.data.types) {
                    if (types.period == 12) {
                        typesStr.append(types.id);
                        typesStr.append(",");
                    }

            }
        }
        if(cb_title6.isChecked()){
            for (AutoInfoBean.Data.Types types : autoInfoBean.data.types) {
                    if (types.period == 24) {
                        typesStr.append(types.id);
                        typesStr.append(",");
                    }

            }
        }
        Log.d("types:",typesStr.toString());

        if (typesStr.toString().contains(",")) {
            return typesStr.replace(typesStr.length() - 1, typesStr.length(), "").toString().trim();
        } else {
            return null;
        }
    }
}
