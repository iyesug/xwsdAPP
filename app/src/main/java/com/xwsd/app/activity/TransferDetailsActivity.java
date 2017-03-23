package com.xwsd.app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.BuyRecordsBean;
import com.xwsd.app.bean.CrtrBean;
import com.xwsd.app.constant.BroadcastParam;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.fragment.CreditorTransferRecordFragment;
import com.xwsd.app.fragment.CredotorTransferBuyNowFragment;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.view.EmptyLayout;
import com.xwsd.app.view.NavbarManage;
import com.xwsd.app.view.ObserveScrollView;
import com.xwsd.app.view.TitleTextView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.OnClick;
import me.drakeet.materialdialog.MaterialDialog;
import okhttp3.Call;

/**
 * Created by Gx on 2016/8/24.
 * 债权转让详情
 */
public class TransferDetailsActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    /**
     * 错误布局
     */
    @Bind(R.id.error_layout)
    EmptyLayout error_layout;

    @Bind(R.id.ttv_creditor_principal)
    TitleTextView ttv_creditor_principal;


    @Bind(R.id.ttv_residue_principal)
    TitleTextView ttv_residue_principal;


    @Bind(R.id.ttv_raw_rate)
    TitleTextView ttv_raw_rate;

    @Bind(R.id.ttv_residue_day)
    TitleTextView ttv_residue_day;

    @Bind(R.id.ttv_transfer_time)
    TitleTextView ttv_transfer_time;

    @Bind(R.id.ttv_expire_time)
    TitleTextView ttv_expire_time;

    @Bind(R.id.tv_people_num)
    TextView tv_people_num;

    @Bind(R.id.iv_progress)
    ImageView iv_progress;

    @Bind(R.id.commit)
    Button commit;

    @Bind(R.id.tv_oddTitle)
    TextView tv_oddTitle;

    @Bind(R.id.tv_hint)
    TextView tv_hint;

    @Bind(R.id.tv_formula)
    TextView tv_formula;

    @Bind(R.id.iv_arrows)
    TextView iv_arrows;

//    @Bind(R.id.tab_layout)
//    TabLayout tab_FindFragment_title;


    /**
     * 左边TAB
     */
    @Bind(R.id.tab_project_left)
    TextView tab_project_left;

    @Bind(R.id.tab_project_right)
    TextView tab_project_right;

    /**
     * 左边指示器
     */
    @Bind(R.id.indicator_left)
    View indicator_left;

    /**
     * 右边指示器
     */
    @Bind(R.id.indicator_right)
    View indicator_right;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipe_refresh_layout;

    @Bind(R.id.scroll_view)
    ObserveScrollView scroll_view;
    String id;

    CrtrBean crtrBean;

    private String[] titles;

    MaterialDialog materialDialog;

    CreditorTransferRecordFragment creditorTransferRecordFragment;

    CredotorTransferBuyNowFragment credotorTransferBuyNowFragment;




    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private float maxMoney;

    /**
     * 导航栏
     */
    private NavbarManage navbarManage;


    /**
     * 广播接收者，用于处理相对应的Action
     */
    private MyBroadcastReciever myBroadcastReciever = new MyBroadcastReciever();

    @Override
    public void onRefresh() {
        getData(2);
        getNum();
        setTabSelection(0);
    }

    /**
     * 广播类(用来更新数据)
     */
    class MyBroadcastReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {

                case BroadcastParam.BUY_CREDITORS:
                    getData(1);
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
        setContentView(R.layout.activity_transfer);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.creditor_transfer));
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

        id = getIntent().getStringExtra(UserParam.DATA);

        error_layout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(1);
            }
        });
        //触碰软键盘外面收缩键盘
        scroll_view.setGestureDetector(new GestureDetector(TransferDetailsActivity.this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                hideSoftKeyboard(getCurrentFocus());
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        }));
        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastParam.BUY_CREDITORS);
        registerReceiver(myBroadcastReciever, intentFilter);
        initRefresh();
        getData(1);
        initTab();
        getNum();

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

    private void initTab() {
        setTabSelection(0);
    }


    private void getNum(){
        ApiHttpClient.buyingRecords(id, 1, 30, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("在投资记录：" + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        BuyRecordsBean buyRecordsBean = GsonUtils.jsonToBean(response, BuyRecordsBean.class);
                        if(buyRecordsBean.data.count > 0){
                            iv_arrows.setVisibility(View.VISIBLE);
                            iv_arrows.setText(buyRecordsBean.data.count+"");
                        }else {
                            iv_arrows.setVisibility(View.GONE);
                        }
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void getData(final int type) {
        if (type == 1) {
            error_layout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }
        ApiHttpClient.crtr(id, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (type == 1) {
                    error_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
                } else {
                    swipe_refresh_layout.setRefreshing(false);
                    AppContext.showToastShort(R.string.refurbish_failure);
                }
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("债权转让详情:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        crtrBean = GsonUtils.jsonToBean(response, CrtrBean.class);
                        maxMoney = (float) jsonObject.getJSONObject("data").getDouble("moneyLast");

                        if (type == 1) {
                            error_layout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        } else {
                            swipe_refresh_layout.setRefreshing(false);
                        }
                        setData();
                    } else {
                        if (type == 1) {
                            error_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
                        } else {
                            swipe_refresh_layout.setRefreshing(false);
                            AppContext.showToastShort(R.string.refurbish_failure);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (type == 1) {
                        error_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
                    } else {
                        swipe_refresh_layout.setRefreshing(false);
                        AppContext.showToastShort(R.string.refurbish_failure);
                    }
                }
            }
        });
    }

    /**
     * 加载相应的Fragment
     *
     * @param position
     */
    private void setTabSelection(int position) {
        // 开启一个Fragment事务
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (position) {
            case 0:
                BuriedPointUtil.buriedPoint("债权转让购买记录");
                if (creditorTransferRecordFragment == null) {
                    creditorTransferRecordFragment = new CreditorTransferRecordFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(UserParam.DATA, id);
                    creditorTransferRecordFragment.setArguments(bundle);
                    transaction.add(R.id.frame_content, creditorTransferRecordFragment);
                } else {
                    transaction.show(creditorTransferRecordFragment);
                }
                tab_project_left.setTextColor(getResources().getColor(R.color.blue_simple));
                tab_project_right.setTextColor(getResources().getColor(R.color.backk));
                indicator_left.setVisibility(View.VISIBLE);
                indicator_right.setVisibility(View.INVISIBLE);
                break;
            case 1:
                BuriedPointUtil.buriedPoint("债权转让在买记录");
                    credotorTransferBuyNowFragment = new CredotorTransferBuyNowFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(UserParam.DATA, id);
                    credotorTransferBuyNowFragment.setArguments(bundle);
                    transaction.add(R.id.frame_content, credotorTransferBuyNowFragment);

                tab_project_right.setTextColor(getResources().getColor(R.color.blue_simple));
                tab_project_left.setTextColor(getResources().getColor(R.color.backk));
                indicator_left.setVisibility(View.INVISIBLE);
                indicator_right.setVisibility(View.VISIBLE);
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {

        if (creditorTransferRecordFragment != null) {
            transaction.hide(creditorTransferRecordFragment);
        }
        if (credotorTransferBuyNowFragment != null) {
            transaction.hide(credotorTransferBuyNowFragment);
        }
    }

    /**
     * 设置数据
     */
    private void setData() {
        tv_oddTitle.setText(crtrBean.data.title);
        ttv_creditor_principal.setContent(decimalFormat.format(crtrBean.data.money) + "元");
        ttv_residue_principal.setContent(decimalFormat.format(crtrBean.data.moneyLast) + "元");

        ttv_transfer_time.setContent(crtrBean.data.addtime.split(" ")[0]);
        ttv_expire_time.setContent(crtrBean.data.endtime.split(" ")[0]);
        ttv_residue_day.setContent(crtrBean.data.remainDay + "天");

        if (crtrBean.data.oddRepaymentStyle.equals("monthpay")) {
            tv_formula.setText("先息后本：承接利息=本金*年化率/（12*30）*（剩余天数+1）");
        } else {
            tv_formula.setText("等额本息：承接利息=剩余本金*年化率/（12*30）*（当月剩余天数+1）+原债权持有人利息＊（承接金额／剩余本金）");
        }

        ttv_raw_rate.setContent(decimalFormat.format(crtrBean.data.oddYearRate * 100) + "%");

        tv_people_num.setText(crtrBean.data.buyCount + "人");

        if (crtrBean.data.progress.equals("start")) {
            iv_progress.setVisibility(View.GONE);
        } else {
            commit.setBackgroundResource(R.drawable.bg_button_sharp_gray);
            commit.setText("已出售");
            commit.setClickable(false);
            //     et_money.setVisibility(View.GONE);
            tv_hint.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.commit, R.id.ll_transfer_explain,R.id.tab_project_left,R.id.ll_tab_project_right})
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.commit:

                BuriedPointUtil.buriedPoint("债权转让详情页承接");

//                if (TextUtils.isEmpty(et_money.getText().toString().trim())) {
//                    AppContext.showToastShort("请输入购买金额");
//                    return;
//                }
//
//                if (Float.valueOf(et_money.getText().toString().trim()) < 50f) {
//                    AppContext.showToastShort("起投金额不能低于50元");
//                    return;
//                }
//
//                if (Float.valueOf(et_money.getText().toString().trim()) > maxMoney) {
//                    AppContext.showToastShort("您最多可投" + maxMoney + "元");
//                    return;
//                }
//
//                if (maxMoney - Float.valueOf(et_money.getText().toString().trim()) > 0 &&
//                        maxMoney - Float.valueOf(et_money.getText().toString().trim()) < 50f) {
//                    AppContext.showToastShort("剩余金额不能低于50元");
//                    return;
//                }

//                判断用户是否登录
                if (AppContext.getUserBean() == null) {
                    intent = new Intent(TransferDetailsActivity.this, UserActivity.class);
                    startActivity(intent);
                    return;
                }

                intent = new Intent(TransferDetailsActivity.this, PromptlyInvestActivity.class);
                intent.putExtra(UserParam.TYPE, PromptlyInvestActivity.TYPE_TRANSFER);
                intent.putExtra(UserParam.DATA, id);
                //     intent.putExtra("investMoney",et_money.getText().toString().trim());
//                intent.putExtra(UserParam.MONEY, crtrBean.data.oddMoneyLast);
                intent.putExtra(UserParam.TITLE, crtrBean.data.title);
                startActivity(intent);
                break;

            case R.id.ll_transfer_explain:

                //埋点
                BuriedPointUtil.buriedPoint("债权转让详情页债权转让说明");

                if (materialDialog == null) {
                    materialDialog = new MaterialDialog(TransferDetailsActivity.this)
                            .setTitle("转让说明")
                            .setMessage("1.共同承接：债权发布金额仅为本金金额，单笔债权可由多名投资人共同承接，取消原溢价折让机制。\n\n" +
                                    "例：转让人转让1个债权本金为10万元的债权，可由多名承接人共同承接，比如由3名承接人承接，A承接人承接2万元债权，B承接人承接3万元债权，C承接人承接5万元债权。\n\n" +
                                    "2.承接金额：承接人可输入任意承接金额（可不为整数），也可一人全额承接债权，但设置金额不能超过转让的债权本金。单笔最低承接金额需不低于50元，且不能让剩余债权余额小于50元，以免影响下一位承接人承接。\n\n" +
                                    "3.利息计算：转让成功日之前的利息归转让人所有，在转让成功时系统提前结息给转让人；转让成功日之后（含转让成功日）的利息归承接人所有，在承接后的首个结息日结息给承接人。\n\n" +
                                    "4.积分计算：转让成功日之前的积分归转让人所有，在转让成功时系统结算积分给转让人；转让成功日之后（含转让成功日）的积分归承接人所有，在承接后的首个结息日结算积分给承接人。\n\n" +
                                    "5.债权转让费用：如果转让人在债权转让成功时，转让人持有该债权的天数<30天则收取0.5%的手续费用，持有天数≥30天则不收取任何费用。\n\n" +
                                    "（注：债权转让成功当日不计入持有天数，手续费用为债权本金的0.5%，在转让成功时收取）")
                            .setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    materialDialog.dismiss();
                                }
                            });
                    materialDialog.setCanceledOnTouchOutside(true);
                }

                materialDialog.show();

                break;
            case R.id.tab_project_left:
                setTabSelection(0);
                break;
            case R.id.ll_tab_project_right:
                setTabSelection(1);
                break;
        }

    }
}
