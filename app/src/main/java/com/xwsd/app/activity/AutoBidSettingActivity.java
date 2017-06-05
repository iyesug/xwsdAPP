package com.xwsd.app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import butterknife.Bind;
import butterknife.OnClick;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.adapter.BaseAdapterHelper;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.AutoInfoBean;
import com.xwsd.app.constant.UserParam;
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
import java.util.List;


/**
 * Created by Gx on 2016/8/25.
 * 自动投标-设置
 */
public class AutoBidSettingActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    /**
     * 金额类型
     */
    private SpinnerDialog amountType;

    @Bind(R.id.ll_amount_type)
    LinearLayout ll_amount_type;

    @Bind(R.id.ll_retain_balance)
    LinearLayout ll_retain_balance;

    @Bind(R.id.tv_amount_type)
    TextView tv_amount_type;

    @Bind(R.id.tv_retain)
    SwitchView tv_retain;

    @Bind(R.id.ll_invest_section)
    LinearLayout ll_invest_section;

    @Bind(R.id.ll_invest_fixed)
    LinearLayout ll_invest_fixed;

    @Bind(R.id.tv_pledge_bid)
    TextView tv_pledge_bid;

    @Bind(R.id.tv_mortgage_bid)
    TextView tv_mortgage_bid;

    @Bind(R.id.tv_finance_lease_bid)
    TextView tv_finance_lease_bid;

    @Bind(R.id.iv_pledge_bid)
    ImageView iv_pledge_bid;

    @Bind(R.id.iv_mortgage_bid)
    ImageView iv_mortgage_bid;

    @Bind(R.id.iv_finance_lease_bid)
    ImageView iv_finance_lease_bid;

    @Bind(R.id.screen_list_left)
    ListView screen_list_left;

    @Bind(R.id.screen_list_centre)
    ListView screen_list_centre;

    @Bind(R.id.screen_list_right)
    ListView screen_list_right;

    @Bind(R.id.tv_balance)
    TextView tv_balance;

    @Bind(R.id.et_retain_balance)
    EditText et_retain_balance;

    @Bind(R.id.ll_list)
    LinearLayout ll_list;

    @Bind(R.id.tb_auto_bid)
    SwitchView tb_auto_bid;

    @Bind(R.id.tb_queue_bid)
    SwitchView tb_queue_bid;

    @Bind(R.id.tb_type)
    SwitchView tb_type;

    QuickAdapter pledgeBidAdapter;

    QuickAdapter mortgageBidAdapter;

    QuickAdapter financeLeaseBidAdapter;

    String autostatus = ApiHttpClient.CLOSE;

    String staystatus = ApiHttpClient.CLOSE;

    String moneyType = ApiHttpClient.CLOSE;

    RequestCall call;

    AutoInfoBean autoInfoBean;

    DecimalFormat decimalFormat = new DecimalFormat("0.0");

    /**
     * 类型
     */
    private String mode = "0";
    /**
     * 选择列表是否显示
     */
    boolean selectListShow = false;

    @Bind(R.id.et_assign_money)
    EditText et_assign_money;

    @Bind(R.id.et_scope_money_1)
    EditText et_scope_money_1;

    @Bind(R.id.et_scope_money_2)
    EditText et_scope_money_2;

    @Bind(R.id.error_layout)
    EmptyLayout mErrorLayout;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_auto_bid_setting);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.auto_bid));
        navbarManage.showLeft(true);
        navbarManage.showRight(true);
        navbarManage.setLeftImg(R.mipmap.ic_back_b);
        navbarManage.setRightStr("简版");
        navbarManage.setBackground(R.color.navbar_bg);
        navbarManage.setOnLeftClickListener(new NavbarManage.OnLeftClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }
        });

        navbarManage.setOnRightClickListener(new NavbarManage.OnRightClickListener(){
            @Override
            public void onRightClick() {
                BuriedPointUtil.buriedPoint("自动投标高级-简版");
                Intent intent = new Intent();
                intent.setClass(AutoBidSettingActivity.this,AutoBidJianSettingActivity.class);
                startActivity(intent);
                AutoBidSettingActivity.this.finish();
            }
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
        //判断是否登陆
        if (AppContext.getUserBean() == null||null==AppContext.getUserBean().data) {
            Intent intent = new Intent(this, UserActivity.class);
            intent.putExtra(UserParam.TYPE, UserActivity.TYPE_LOGIN);
            intent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
            startActivity(intent);
            return;
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
        //设置保留余额
        if (Long.valueOf(autoInfoBean.data.autoInvest.investEgisMoney) > 0) {

            tv_retain.setState(true);
            et_retain_balance.setText(autoInfoBean.data.autoInvest.investEgisMoney);
        } else {
            et_retain_balance.setText("00.00");
            tv_retain.setState(false);
            et_retain_balance.setEnabled(false);

        }

        //SwitchView监听
        tv_retain.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                et_retain_balance.setEnabled(true);
                et_retain_balance.setText("");
                tv_retain.toggleSwitch(true);
            }

            @Override
            public void toggleToOff() {
                et_retain_balance.setText("00.00");
                et_retain_balance.setEnabled(false);

                tv_retain.toggleSwitch(false);

            }
        });


        //设置投资金额
        if (autoInfoBean.data.autoInvest.moneyType == 0) {
            moneyType = ApiHttpClient.CLOSE;
            tv_amount_type.setText("指定金额");
            ll_invest_fixed.setVisibility(View.VISIBLE);
            ll_invest_section.setVisibility(View.GONE);
            et_assign_money.setText(autoInfoBean.data.autoInvest.investMoneyLower);
        } else {
            moneyType = ApiHttpClient.OPEN;
            tv_amount_type.setText("投资范围");
            ll_invest_fixed.setVisibility(View.GONE);
            ll_invest_section.setVisibility(View.VISIBLE);
            et_scope_money_1.setText(autoInfoBean.data.autoInvest.investMoneyLower);
            et_scope_money_2.setText(autoInfoBean.data.autoInvest.investMoneyUper);
        }
//        设置自动投标
        if (autoInfoBean.data.autoInvest.autostatus == 1) {
            autostatus = ApiHttpClient.OPEN;
            tb_auto_bid.setState(true);
        } else {
            autostatus = ApiHttpClient.CLOSE;
            tb_auto_bid.setState(false);
        }
//        设置排队不投
        if (autoInfoBean.data.autoInvest.staystatus == 1) {
            staystatus = ApiHttpClient.OPEN;
            tb_queue_bid.setState(true);
        } else {
            staystatus = ApiHttpClient.CLOSE;
            tb_queue_bid.setState(false);
        }

        //设置投资类型
        for (AutoInfoBean.Data.Types types : autoInfoBean.data.types) {
            for (AutoInfoBean.Data.Types.BidList bidList : types.list) {
                for (String id : autoInfoBean.data.autoInvest.types) {
                    if (bidList.id.equals(id)) {
                        bidList.selectType = true;
                    }
                }
            }
        }

        initSelectList();

        //更新适配器
        if (pledgeBidAdapter != null) {
            pledgeBidAdapter.notifyDataSetChanged();
            mortgageBidAdapter.notifyDataSetChanged();
            financeLeaseBidAdapter.notifyDataSetChanged();
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

        tb_queue_bid.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                tb_queue_bid.toggleSwitch(true);

                staystatus = ApiHttpClient.OPEN;
                BuriedPointUtil.buriedPoint("自动投标设置排队不投");
            }

            @Override
            public void toggleToOff() {
                tb_queue_bid.toggleSwitch(false);
                staystatus = ApiHttpClient.CLOSE;
            }
        });

        tb_type.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                tb_type.toggleSwitch(true);
                BuriedPointUtil.buriedPoint("自动投标设置全部类型");
            }

            @Override
            public void toggleToOff() {
                tb_type.toggleSwitch(false);
            }
        });

    }

    @OnClick({R.id.tv_retain, R.id.ll_amount_type, R.id.ll_tab, R.id.commit, R.id.tb_type_v, R.id.ll_invest_section, R.id.et_assign_money})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_amount_type:
                if (amountType == null) {
                    amountType = new SpinnerDialog(AutoBidSettingActivity.this, ll_amount_type);
                    amountType.setData(getResources().getStringArray(R.array.amount_type));
                    amountType.setOnItemClickListener(new SpinnerDialog.OnItemClickListener() {
                        @Override
                        public void onClick(int position, String data) {
                            tv_amount_type.setText(data);
                            switch (position) {
                                case 0:
                                    moneyType = ApiHttpClient.CLOSE;
                                    ll_invest_fixed.setVisibility(View.VISIBLE);
                                    ll_invest_section.setVisibility(View.GONE);
                                    break;
                                case 1:
                                    moneyType = ApiHttpClient.OPEN;
                                    ll_invest_fixed.setVisibility(View.GONE);
                                    ll_invest_section.setVisibility(View.VISIBLE);
                                    break;
                            }
                        }
                    });
                }
                amountType.show(ll_amount_type.getWidth());
                BuriedPointUtil.buriedPoint("自动投标设置指定金额");
                break;
 /*           case R.id.tv_retain:

                if (tv_retain.get) {
                    ll_retain_balance.setVisibility(View.VISIBLE);
                    tv_retain.setText(getString(R.string.no_retain));

                } else {
                    ll_retain_balance.setVisibility(View.GONE);
                    tv_retain.setText(getString(R.string.retain));
                    et_retain_balance.setText("");
                }



                break;*/
            case R.id.ll_tab:
                TLog.error("点击了TAB");
                if (autoInfoBean != null) {
                    if (selectListShow) {
                        selectListShow = false;
                        ll_list.setVisibility(View.INVISIBLE);
                    } else {
                        selectListShow = true;
                        ll_list.setVisibility(View.VISIBLE);
                    }
                }
                BuriedPointUtil.buriedPoint("自动投标设置类型收起展开");
                break;
            case R.id.commit://保存设置
                String investEgisMoney;
                String rangeBegin;
                String rangeEnd;
                String fixedMoney;
                BuriedPointUtil.buriedPoint("自动投标高级-保存");
                //得到保留金额
                if (3==tv_retain.getState()||4==tv_retain.getState()) {

                    if (TextUtils.isEmpty(et_retain_balance.getText().toString().trim())) {
                        ToastUtil.showToastShort(R.string.inpt_retain_money);
                        return;
                    }

                    investEgisMoney = et_retain_balance.getText().toString().trim();

                } else {
                    investEgisMoney = "0";
                }

                //得到投资金额
                if (moneyType.equals("0")) {
                    if (TextUtils.isEmpty(et_assign_money.getText().toString().trim())) {
                        ToastUtil.showToastShort(R.string.inpt_assign_money);
                        return;
                    }
                    fixedMoney = rangeBegin = rangeEnd = et_assign_money.getText().toString().trim();
                } else {
                    //得到投资范围
                    if (TextUtils.isEmpty(et_scope_money_1.getText().toString().trim())) {
                        ToastUtil.showToastShort(R.string.inpt_scope_money_1);
                        return;
                    }
                    if (TextUtils.isEmpty(et_scope_money_2.getText().toString().trim())) {
                        ToastUtil.showToastShort(R.string.inpt_scope_money_2);
                        return;
                    }
                    fixedMoney = "1";
                    rangeBegin = et_scope_money_1.getText().toString().trim();
                    rangeEnd = et_scope_money_2.getText().toString().trim();
                }


                showWaitDialog(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (call != null) {
                            call.cancel();
                        }
                    }
                });
                call = ApiHttpClient.autoSet(AppContext.getUserBean().data.userId,
                        autostatus,
                        staystatus,
                        investEgisMoney,
                        moneyType,
                        rangeBegin,
                        rangeEnd,
                        fixedMoney,
                        jointInvestType(),
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

            case R.id.tb_type_v://选中/取消/类型

                if (tb_type.getState() == SwitchView.STATE_SWITCH_ON) {

                    changeAllSelectState(false);
                } else {
                    changeAllSelectState(true);
                }

                break;

            case R.id.ll_invest_section:
                BuriedPointUtil.buriedPoint("自动投标设置金额范围");
                break;

            case R.id.et_assign_money:
                BuriedPointUtil.buriedPoint("自动投标设置金额输入");
                break;
        }
    }

    /**
     * 拼接投资类型的参数
     */
    private String jointInvestType() {
        StringBuffer typesStr = new StringBuffer();
        //        遍历所有元素，设置选中状态
        for (AutoInfoBean.Data.Types types : autoInfoBean.data.types) {
            for (AutoInfoBean.Data.Types.BidList bidList : types.list) {
                if (bidList.selectType) {
                    typesStr.append(bidList.id);
                    typesStr.append(",");
                }
            }
        }
        if (typesStr.toString().contains(",")) {
            return typesStr.replace(typesStr.length() - 1, typesStr.length(), "").toString().trim();
        } else {
            return null;
        }
    }

    /**
     * 选中所有
     *
     * @param selectType true:正选，false:反选
     */
    private void changeAllSelectState(boolean selectType) {
        if (!selectType) {
            selectListShow = false;
            ll_list.setVisibility(View.INVISIBLE);
        } else {
            selectListShow = true;
            ll_list.setVisibility(View.VISIBLE);
        }

        //遍历所有元素，设置选中状态
        for (AutoInfoBean.Data.Types types : autoInfoBean.data.types) {

            for (AutoInfoBean.Data.Types.BidList bidList : types.list) {
                bidList.selectType = selectType;
            }
        }

        //更新适配器
        if (pledgeBidAdapter != null) {
            pledgeBidAdapter.notifyDataSetChanged();
            mortgageBidAdapter.notifyDataSetChanged();
            financeLeaseBidAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 初始化下拉列表
     */
    private void initSelectList() {

        if (pledgeBidAdapter == null) {

            if (autoInfoBean.data.types.get(0).name.equals("抵押标")) {
                screen_list_left.setAdapter(pledgeBidAdapter = new QuickAdapter<AutoInfoBean.Data.Types.BidList>(AutoBidSettingActivity.this, R.layout.item_check_box, autoInfoBean.data.types.get(0).list) {
                    @Override
                    protected void convert(BaseAdapterHelper helper, final AutoInfoBean.Data.Types.BidList item) {
//                        监听选中状态
                        helper.setOnCheckedChangeListener(R.id.cb_title, new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                item.selectType = isChecked;

//                                判断是否有选中条目
                                if (checkSelected(autoInfoBean.data.types.get(0).list)) {
                                    setArrowsType(tv_pledge_bid, iv_pledge_bid, 2);
                                } else {
                                    setArrowsType(tv_pledge_bid, iv_pledge_bid, 0);
                                }
                                judgeAllSelected();
                            }
                        });
                        helper.setText(R.id.cb_title, item.name);
                        helper.setChecked(R.id.cb_title, item.selectType);
                    }
                });
            }

            if (autoInfoBean.data.types.get(1).name.equals("质押标")) {
                screen_list_centre.setAdapter(mortgageBidAdapter = new QuickAdapter<AutoInfoBean.Data.Types.BidList>(AutoBidSettingActivity.this, R.layout.item_check_box, autoInfoBean.data.types.get(1).list) {
                    @Override
                    protected void convert(BaseAdapterHelper helper, final AutoInfoBean.Data.Types.BidList item) {
                        helper.setOnCheckedChangeListener(R.id.cb_title, new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                item.selectType = isChecked;

                                if (checkSelected(autoInfoBean.data.types.get(1).list)) {
                                    setArrowsType(tv_mortgage_bid, iv_mortgage_bid, 2);
                                } else {
                                    setArrowsType(tv_mortgage_bid, iv_mortgage_bid, 0);
                                }
                                judgeAllSelected();
                            }
                        });
                        helper.setText(R.id.cb_title, item.name);
                        helper.setChecked(R.id.cb_title, item.selectType);
                    }
                });
            }

            if (autoInfoBean.data.types.get(2).name.equals("融资租赁标")) {
                screen_list_right.setAdapter(financeLeaseBidAdapter = new QuickAdapter<AutoInfoBean.Data.Types.BidList>(AutoBidSettingActivity.this, R.layout.item_check_box, autoInfoBean.data.types.get(2).list) {
                    @Override
                    protected void convert(BaseAdapterHelper helper, final AutoInfoBean.Data.Types.BidList item) {
                        helper.setOnCheckedChangeListener(R.id.cb_title, new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                item.selectType = isChecked;

                                if (checkSelected(autoInfoBean.data.types.get(2).list)) {
                                    setArrowsType(tv_finance_lease_bid, iv_finance_lease_bid, 2);
                                } else {
                                    setArrowsType(tv_finance_lease_bid, iv_finance_lease_bid, 0);
                                }
                                judgeAllSelected();
                            }
                        });

                        helper.setText(R.id.cb_title, item.name);
                        helper.setChecked(R.id.cb_title, item.selectType);
                    }
                });
            }
        }
    }

    /**
     * 设置箭头的类型
     *
     * @param tv
     * @param iv
     * @param type 0：初始状态；1：选中状态；2：下拉状态
     */
    private void setArrowsType(TextView tv, ImageView iv, int type) {

        switch (type) {
            case 0:
                tv.setTextColor(getResources().getColor(R.color.gray_text));
                iv.setImageResource(R.mipmap.ic_arrows_u_g);
                break;
            case 1:
                tv.setTextColor(getResources().getColor(R.color.blue_simple));
                iv.setImageResource(R.mipmap.ic_arrows_u_b);
                break;
            case 2:
                tv.setTextColor(getResources().getColor(R.color.blue_simple));
                iv.setImageResource(R.mipmap.ic_arrows_d_b);
                break;
        }
    }

    /**
     * 判断是否全部选中
     */
    private synchronized void judgeAllSelected() {

        int checkedNum = 0;
        int allNum = 0;

        for (AutoInfoBean.Data.Types types : autoInfoBean.data.types) {

            for (AutoInfoBean.Data.Types.BidList bidList : types.list) {
                allNum++;
                if (bidList.selectType) {
                    checkedNum++;
                }
            }
        }

        if (checkedNum == allNum &&
                tb_type.getState() == SwitchView.STATE_SWITCH_OFF) {
            tb_type.setState(true);
        } else if (checkedNum != allNum &&
                tb_type.getState() == SwitchView.STATE_SWITCH_ON) {
            tb_type.setState(false);
        }
    }

    /**
     * 检查选中状态
     */
    private boolean checkSelected(List<AutoInfoBean.Data.Types.BidList> bidLists) {
        for (AutoInfoBean.Data.Types.BidList bidList : bidLists) {
            if (bidList.selectType) {
                return true;
            }
        }
        return false;
    }
}
