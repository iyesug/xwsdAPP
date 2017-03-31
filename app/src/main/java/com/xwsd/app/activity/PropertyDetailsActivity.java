package com.xwsd.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.UserAccountBean;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.EmptyLayout;
import com.xwsd.app.view.NavbarManage;
import com.zhy.http.okhttp.callback.StringCallback;
import me.drakeet.materialdialog.MaterialDialog;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gx on 2016/8/25.
 * 资产明细
 */
public class PropertyDetailsActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener{

    @Bind(R.id.error_layout)
    protected EmptyLayout mErrorLayout;

    @Bind(R.id.tv_property_all)
    protected TextView tv_property_all;

    @Bind(R.id.tv_balance_usable)
    protected LinearLayout tv_balance_usable;

    @Bind(R.id.tv_balance_freeze)
    protected LinearLayout tv_balance_freeze;

    @Bind(R.id.tv_due_all)
    protected TextView tv_due_all;

    @Bind(R.id.tv_due_principal)
    protected LinearLayout tv_due_principal;

    @Bind(R.id.tv_due_interest)
    protected LinearLayout tv_due_interest;

    @Bind(R.id.tv_invest_all)
    protected TextView tv_invest_all;

    @Bind(R.id.tv_interest_awarded)
    protected LinearLayout tv_interest_awarded;

    @Bind(R.id.tv_interest_due)
    protected LinearLayout tv_interest_due;

    @Bind(R.id.tv_interest_deduct)
    protected LinearLayout tv_interest_deduct;

    @Bind(R.id.tv_interest_practical)
    protected LinearLayout tv_interest_practical;

    @Bind(R.id.tv_activity_cash)
    protected LinearLayout tv_activity_cash;

    @Bind(R.id.tv_invest_activity)
    protected LinearLayout tv_invest_activity;

    @Bind(R.id.tv_invest_integral)
    protected LinearLayout tv_invest_integral;

    @Bind(R.id.tv_transfer_money)
    protected LinearLayout tv_transfer_money;

    @Bind(R.id.tv_transfer_interest)
    protected LinearLayout tv_transfer_interest;

    @Bind(R.id.tv_continue_money)
    protected LinearLayout tv_continue_money;

    @Bind(R.id.tv_continue_interest)
    protected LinearLayout tv_continue_interest;

    @Bind(R.id.tv_increase_interest)
    protected LinearLayout tv_increase_interest;

    @Bind(R.id.tv_get_money)
    protected LinearLayout tv_get_money;

    @Bind(R.id.tv_recommended_award)
    protected LinearLayout tv_recommended_award;

    @Bind(R.id.tv_recommended_use_award)
    protected LinearLayout tv_recommended_use_award;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipe_refresh_layout;
    UserAccountBean userAccountBean;
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;


    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_property_details);

        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.property_details));
        navbarManage.showLeft(true);
        navbarManage.showRight(true);
        navbarManage.setLeftImg(R.mipmap.ic_back_b);
        navbarManage.setRightImg(R.mipmap.ic_property_details);
        navbarManage.setBackground(R.color.navbar_bg);
        navbarManage.setOnLeftClickListener(new NavbarManage.OnLeftClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }
        });

        navbarManage.setOnRightClickListener(new NavbarManage.OnRightClickListener() {
            @Override
            public void onRightClick() {

                BuriedPointUtil.buriedPoint("资产明细个人资料");//埋点

                Intent intent = new Intent(PropertyDetailsActivity.this, UserInfoActiviy.class);
                startActivity(intent);
            }
        });
        initRefresh();
        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(1);
            }
        });
        getData(1);
    }
    /**
     * 初始化下拉刷新
     */
    private void initRefresh() {

        swipe_refresh_layout.setOnRefreshListener(this);
        swipe_refresh_layout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
    }
    /**
     * 得到数据
     */
    private void getData(final int type) {
        if (type == 1) {
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }
        ApiHttpClient.useraccount(AppContext.getUserBean().data.userId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                if (type == 1) {
                    mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                } else {
                    swipe_refresh_layout.setRefreshing(false);
                    ToastUtil.showToastShort(R.string.refurbish_failure);
                }
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("用户资金账户：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        if (type == 1) {
                            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        } else {
                            swipe_refresh_layout.setRefreshing(false);
                        }
                        userAccountBean = GsonUtils.jsonToBean(response, UserAccountBean.class);

                        setData();
                    } else {
                        if (type == 1) {
                            mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                        } else {
                            swipe_refresh_layout.setRefreshing(false);
                            ToastUtil.showToastShort(R.string.refurbish_failure);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (type == 1) {
                        mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                    } else {
                        swipe_refresh_layout.setRefreshing(false);
                        ToastUtil.showToastShort(R.string.refurbish_failure);
                    }
                }
            }
        });
    }

    /**
     * 设置数据
     */
    private void setData() {

        //设置标题
        ((TextView) tv_balance_usable.findViewById(R.id.tv_title)).setText("可用余额：");
        ((TextView) tv_balance_freeze.findViewById(R.id.tv_title)).setText("冻结金额：");
//        ((TextView) tv_balance_withdraw.findViewById(R.id.tv_title)).setText("可免费提现余额：");

        ((TextView) tv_due_principal.findViewById(R.id.tv_title)).setText("待收本金：");
        ((TextView) tv_due_interest.findViewById(R.id.tv_title)).setText("待收利息：");

        ((TextView) tv_interest_awarded.findViewById(R.id.tv_title)).setText("已获利息：");
        ((TextView) tv_interest_due.findViewById(R.id.tv_title)).setText("待收利息：");
        ((TextView) tv_interest_deduct.findViewById(R.id.tv_title)).setText("扣除利息管理费：");
        ((TextView) tv_interest_practical.findViewById(R.id.tv_title)).setText("实际获得利息：");
        ((TextView) tv_activity_cash.findViewById(R.id.tv_title)).setText("活动现金券：");
        ((TextView) tv_invest_activity.findViewById(R.id.tv_title)).setText("投资活动金额：");
        ((TextView) tv_invest_integral.findViewById(R.id.tv_title)).setText("投资积分：");

        ((TextView) tv_transfer_money.findViewById(R.id.tv_title)).setText("转让债权金额：");
        ((TextView) tv_transfer_interest.findViewById(R.id.tv_title)).setText("债权转让获得利息：");
        ((TextView) tv_continue_money.findViewById(R.id.tv_title)).setText("承接债权金额：");
        ((TextView) tv_continue_interest.findViewById(R.id.tv_title)).setText("承接债权获得利息：");

        ((TextView) tv_increase_interest.findViewById(R.id.tv_title)).setText("加息券：");
        ((TextView) tv_get_money.findViewById(R.id.tv_title)).setText("提现券：");
        ((TextView) tv_recommended_award.findViewById(R.id.tv_title)).setText("累计推荐奖励：");
        ((TextView) tv_recommended_use_award.findViewById(R.id.tv_title)).setText("剩余可提取推荐奖励：");

        //设置内容
        tv_property_all.setText(userAccountBean.data.allMoney);
        ((TextView) tv_balance_usable.findViewById(R.id.tv_content)).setText(userAccountBean.data.fundMoney + "元");
        ((TextView) tv_balance_freeze.findViewById(R.id.tv_content)).setText(userAccountBean.data.frozenMoney + "元");
//        ((TextView) tv_balance_withdraw.findViewById(R.id.tv_content)).setText(userAccountBean.data.freeWithdraw + "元");

        tv_due_all.setText(userAccountBean.data.stayAll);
        if(TextUtils.isEmpty(userAccountBean.data.stayPrincipal)){
            ((TextView) tv_due_principal.findViewById(R.id.tv_content)).setText("0.00元");
        }else {
            ((TextView) tv_due_principal.findViewById(R.id.tv_content)).setText(userAccountBean.data.stayPrincipal + "元");
        }

        if(TextUtils.isEmpty(userAccountBean.data.stayInterest)){
            ((TextView) tv_due_interest.findViewById(R.id.tv_content)).setText("0.00元");
        }else {
            ((TextView) tv_due_interest.findViewById(R.id.tv_content)).setText(userAccountBean.data.stayInterest + "元");
        }

        tv_invest_all.setText(userAccountBean.data.tenderAll);
        ((TextView) tv_interest_awarded.findViewById(R.id.tv_content)).setText(userAccountBean.data.hasInterest + "元");

        if(TextUtils.isEmpty(userAccountBean.data.stayInterest)){
            ((TextView) tv_interest_due.findViewById(R.id.tv_content)).setText("0.00元");
        }else {
            ((TextView) tv_interest_due.findViewById(R.id.tv_content)).setText(userAccountBean.data.stayInterest + "元");
        }

        ((TextView) tv_interest_deduct.findViewById(R.id.tv_content)).setText(userAccountBean.data.serviceMoney + "元");
        ((TextView) tv_interest_practical.findViewById(R.id.tv_content)).setText(userAccountBean.data.realInterest + "元");
        ((TextView) tv_activity_cash.findViewById(R.id.tv_content)).setText(userAccountBean.data.actLot + "元");
        ((TextView) tv_invest_activity.findViewById(R.id.tv_content)).setText(userAccountBean.data.actMoney + "元");
        ((TextView) tv_invest_integral.findViewById(R.id.tv_content)).setText(userAccountBean.data.integral);

        ((TextView) tv_transfer_money.findViewById(R.id.tv_content)).setText(userAccountBean.data.transferMoney + "元");
        ((TextView) tv_transfer_interest.findViewById(R.id.tv_content)).setText(userAccountBean.data.transferInterest + "元");
        ((TextView) tv_continue_money.findViewById(R.id.tv_content)).setText(userAccountBean.data.crtrMoney + "元");
        ((TextView) tv_continue_interest.findViewById(R.id.tv_content)).setText(userAccountBean.data.crtrInterest + "元");

        ((TextView) tv_increase_interest.findViewById(R.id.tv_content)).setText(userAccountBean.data.lotteries.interest + "张");
        ((TextView) tv_get_money.findViewById(R.id.tv_content)).setText(userAccountBean.data.lotteries.withdraw + "张");
        ((TextView) tv_recommended_award.findViewById(R.id.tv_content)).setText(userAccountBean.data.spreadMoney + "元");
        ((TextView) tv_recommended_use_award.findViewById(R.id.tv_content)).setText(userAccountBean.data.lastSpreadMoney + "元");

    }
    MaterialDialog materialDialog;
    @OnClick(R.id.iv_hint)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_hint) {
            BuriedPointUtil.buriedPoint("资产明细待收总额公式");
            materialDialog = new MaterialDialog(PropertyDetailsActivity.this)
                    .setTitle("计算公式")
                    .setMessage("待收总额=待收本金+待收利息")
                    .setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            materialDialog.dismiss();
                        }
                    });

            materialDialog.show();
        }
    }

    @Override
    public void onRefresh() {
        getData(2);
    }
}
