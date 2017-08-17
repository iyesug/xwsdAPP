package com.xwsd.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.AutoInfoBean;
import com.xwsd.app.bean.QueueInfoBean;
import com.xwsd.app.bean.UserBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.view.EmptyLayout;
import com.xwsd.app.view.MADialog;
import com.xwsd.app.view.NavbarManage;
import com.xwsd.app.view.SwitchView;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by Gx on 2016/8/25.
 * 自动投标
 */
public class AutoBidActivity extends BaseActivity {

    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Bind(R.id.toggle_button)
    SwitchView toggle_button;

    @Bind(R.id.ll_ranking_valid)
    LinearLayout ll_ranking_valid;

    @Bind(R.id.ll_ranking_invalid)
    LinearLayout ll_ranking_invalid;

    @Bind(R.id.ll_bid_1_price)
    LinearLayout ll_bid_1_price;

    @Bind(R.id.ll_bid_2_price)
    LinearLayout ll_bid_2_price;

    @Bind(R.id.ll_bid_3_price)
    LinearLayout ll_bid_3_price;

    @Bind(R.id.ll_bid_4_price)
    LinearLayout ll_bid_4_price;

    @Bind(R.id.ll_bid_5_price)
    LinearLayout ll_bid_5_price;

    @Bind(R.id.ll_bid_6_price)
    LinearLayout ll_bid_6_price;

    @Bind(R.id.ll_queue)
    LinearLayout ll_queue;

    RequestCall call;
    RequestCall call2;

    @Bind(R.id.error_layout)
    EmptyLayout mErrorLayout;

    @Bind(R.id.tv_queue)
    TextView tv_queue;

    @Bind(R.id.tv_valid_ranking)
    TextView tv_valid_ranking;

    @Bind(R.id.tv_my_ranking)
    TextView tv_my_ranking;

    @Bind(R.id.tv_invalid_ranking)
    TextView tv_invalid_ranking;

    @Bind(R.id.tv_month_num_1)
    TextView tv_month_num_1;

    @Bind(R.id.tv_month_num_2)
    TextView tv_month_num_2;

    @Bind(R.id.tv_month_num_3)
    TextView tv_month_num_3;

    @Bind(R.id.tv_month_num_4)
    TextView tv_month_num_4;

    @Bind(R.id.tv_month_num_5)
    TextView tv_month_num_5;

    @Bind(R.id.tv_month_num_6)
    TextView tv_month_num_6;


    @Bind(R.id.tv_month_money_1)
    TextView tv_month_money_1;

    @Bind(R.id.tv_month_money_2)
    TextView tv_month_money_2;

    @Bind(R.id.tv_month_money_3)
    TextView tv_month_money_3;

    @Bind(R.id.tv_month_money_4)
    TextView tv_month_money_4;

    @Bind(R.id.tv_month_money_5)
    TextView tv_month_money_5;

    @Bind(R.id.tv_month_money_6)
    TextView tv_month_money_6;

    @Bind(R.id.tv_valid_money)
    TextView tv_valid_money;

    @Bind(R.id.tv_invalid_money)
    TextView tv_invalid_money;

    @Bind(R.id.tv_ranking_title)
    TextView tv_ranking_title;

    @Bind(R.id.tv_hint)
    TextView tv_hint;

    MADialog mMDialog;
    private boolean needRefresh = false;

    private boolean needSkip = true;

    QueueInfoBean queueInfoBean;

    private JSONObject preInfo;

    /**
     * 当前请求的是哪个接口
     */
    private int type = 0;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_auto_bid);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        title=getString(R.string.auto_bid);
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.auto_bid));
        navbarManage.showLeft(true);
        navbarManage.setLeftImg(R.mipmap.ic_back_w);
        navbarManage.setBackground(R.color.ink);
        navbarManage.setTextColor(R.color.white);
        navbarManage.setOnLeftClickListener(new NavbarManage.OnLeftClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }
        });
        initLayout();
        setSwitch();
        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                if (type == 0) {
                    initQueue();
                } else {
                    getData();
                }
            }
        });
        getAuto();
        initQueue();
    }



    /**
     * 初始化布局
     */
    private void initLayout() {
        tv_hint.setText("投标金额上限\n\n" +
                "若发标金额≤3万元，自动投出上限为发标金额的50%；3万元<发标金额≤10万元，自动投出上限为标的金额的25%；发标金额>10万元，自动投出上限为标的金额的20%。\n\n" +
                "相关名词解释\n\n" +
                "排队资金：系统仅会识别排队账户实际余额计入排队资金中，设置的金额不会被计入。\n" +
                "有效排名：系统将投标设置正确且账户余额符合设置的待投用户计入有效排名中。\n" +
                "无效排名：系统将投标设置“排队不投”、“关闭自动投标”、“不选择任一投资类型”或账户余额不符合投资金额设置、保留金额设置的用户计入无效排名。\n" +
                "排队不投：不进行投资，只排队占位(不退出排名，且排名会前移)。\n\n" +
                "投标规则说明：\n\n" +
                "1、自动投标设置提交后即进入投标队列；\n" +
                "2、起投金额：大于等于50的整数；\n" +
                "3、排队过程中可自由修改设置，任意设置皆不影响其排队位置；\n" +
                "4、用户仅投标成功后，才会重新排队；\n" +
                "5、关闭第三方资金托管授权则退出自动投标队列。\n");

        //判断是否登陆
        if (AppContext.getUserBean() == null||null==AppContext.getUserBean().data) {
            Intent intent = new Intent(this, UserActivity.class);
            intent.putExtra(UserParam.TYPE, UserActivity.TYPE_LOGIN);
            intent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
            startActivity(intent);
            return;
        }
//        设置用户信息
        if(null!=AppContext.getUserBean()&&null!=AppContext.getUserBean().data) {
            //判断是否授权小微
//            Log.e("autoBidAuth:",AppContext.getUserBean().data.autoBidAuth);
            if (!("".equals(AppContext.getUserBean().data.autoBidAuth)) ) {

                    navbarManage.setRightImg(R.mipmap.ic_auto_bid_setting);
                navbarManage.showRight(true);
                toggle_button.setState(true);

            } else {
                navbarManage.showRight(false);
                toggle_button.setState(false);
                ll_ranking_valid.setVisibility(View.GONE);
                ll_ranking_invalid.setVisibility(View.GONE);
                ll_bid_1_price.setVisibility(View.GONE);
                ll_bid_2_price.setVisibility(View.GONE);
                ll_bid_3_price.setVisibility(View.GONE);
                ll_bid_4_price.setVisibility(View.GONE);
                ll_bid_5_price.setVisibility(View.GONE);
                ll_bid_6_price.setVisibility(View.GONE);
                ll_queue.setVisibility(View.GONE);
            }
        }

    }

    private void getAuto(){
     //   mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        //判断是否登陆
        if (AppContext.getUserBean() == null||null==AppContext.getUserBean().data) {
            Intent intent = new Intent(this, UserActivity.class);
            intent.putExtra(UserParam.TYPE, UserActivity.TYPE_LOGIN);
            intent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
            startActivity(intent);
            return;
        }
        ApiHttpClient.autoInfo(AppContext.getUserBean().data.userId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }
            @Override
            public void onResponse(String response, int id) {
                TLog.error("获取自动投标设置：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
         //               mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        AutoInfoBean autoInfoBean = GsonUtils.jsonToBean(response, AutoInfoBean.class);
                        if(autoInfoBean.data.autoInvest.mode.equals("0")){
                            navbarManage.setOnRightClickListener(new NavbarManage.OnRightClickListener() {
                                @Override
                                public void onRightClick() {
                                    Intent intent = new Intent(AutoBidActivity.this, AutoBidSettingActivity.class);
                                    startActivity(intent);
                                    needRefresh = true;
                                }
                            });
                        }else {
                            navbarManage.setOnRightClickListener(new NavbarManage.OnRightClickListener() {
                                @Override
                                public void onRightClick() {
                                    Intent intent = new Intent(AutoBidActivity.this, AutoBidJianSettingActivity.class);
                                    startActivity(intent);
                                    needRefresh = true;
                                }
                            });
                        }
                    } else {
        //                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
          //          mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                }
            }
        });
    }

    /**
     * 设置开关
     */
    private void setSwitch() {
        toggle_button.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                needRefresh = true;


                if (needSkip) {
                    //跳转到授权页面
                    Intent intent = new Intent(AutoBidActivity.this, WebApproveActivity.class);
                    Map<String, String> map = ApiHttpClient.getSortMap();
                    map.put("userId", AppContext.getUserBean().data.userId);
                    map.put("mode", "bid");
                    intent.putExtra(UserParam.URL, ApiHttpClient.AUTO_AUTH +
                            "?userId=" + AppContext.getUserBean().data.userId +
                            "&mode=" + "bid" +
                            "&sign=" + ApiHttpClient.sign(map));
                    TLog.error("url:"+ ApiHttpClient.AUTO_AUTH +
                            "?userId=" + AppContext.getUserBean().data.userId +
                            "&mode=" + "bid" +
                            "&sign=" + ApiHttpClient.sign(map));
                    intent.putExtra(UserParam.TITLE, getString(R.string.auth_xw));
                    startActivity(intent);
                }
            }

            @Override
            public void toggleToOff() {
                needRefresh = true;
                toggle_button.toggleSwitch(true);

                if(mMDialog==null){
                    mMDialog = new MADialog(AutoBidActivity.this);
                    mMDialog.setMessage("系统暂不支持取消授权！");
                    mMDialog.setBtnOK("确定", v1 -> {
                        mMDialog.miss();
                    });
                    mMDialog.setBtnCancelGone();
                }

                mMDialog.show();

//                toggle_button.toggleSwitch(false);
//                if (needSkip) {
//                    Intent intent = new Intent(AutoBidActivity.this, WebApproveActivity.class);
//                    Map<String, String> map = ApiHttpClient.getSortMap();
//                    map.put("userId", AppContext.getUserBean().data.userId);
//                    intent.putExtra(UserParam.URL, ApiHttpClient.AUTO_AUTH +
//                            "?userId=" + AppContext.getUserBean().data.userId +
//                            "?mode=" + "bid" +
//                            "&sign=" + ApiHttpClient.sign(map));
//                    intent.putExtra(UserParam.TITLE, getString(R.string.auth_cancel));
//                    startActivity(intent);
//                }
            }
        });
    }

    /**
     * 初始化队列信息
     */
    private void initQueue() {
        type = 0;
//        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        //重新获取用户的托管状态
        if (call != null) {
            call.cancel();
        }

        call = ApiHttpClient.queueInfo(AppContext.getUserBean().data.userId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("自动投标队列：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    preInfo = jsonObject.getJSONObject("data").getJSONObject("preInfo");

                    if (jsonObject.getInt("status") == 1) {
                        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        queueInfoBean = GsonUtils.jsonToBean(response, QueueInfoBean.class);

                        //解析月份标-人数
                        JSONObject monthNumJSON = preInfo.getJSONObject("monthNum");
                        Iterator monthNumIterator = monthNumJSON.keys();
                        List<QueueInfoBean.Data.PreInfo.MonthNum> monthNumList = new ArrayList<QueueInfoBean.Data.PreInfo.MonthNum>();
                        while (monthNumIterator.hasNext()) {
                            QueueInfoBean.Data.PreInfo.MonthNum monthNum = new QueueInfoBean.Data.PreInfo.MonthNum();
                            monthNum.key = String.valueOf(monthNumIterator.next());
                            monthNum.value = monthNumJSON.getString(monthNum.key);
                            monthNumList.add(monthNum);
                        }
                        queueInfoBean.data.preInfo.monthNums = monthNumList;

                        //解析月份标-金额
                        JSONObject monthMoneyJSON = preInfo.getJSONObject("monthMoney");
                        Iterator iterator = monthMoneyJSON.keys();
                        List<QueueInfoBean.Data.PreInfo.MonthMoney> monthMoneyList = new ArrayList<QueueInfoBean.Data.PreInfo.MonthMoney>();
                        while (iterator.hasNext()) {
                            QueueInfoBean.Data.PreInfo.MonthMoney monthMoney = new QueueInfoBean.Data.PreInfo.MonthMoney();
                            monthMoney.key = String.valueOf(iterator.next());
                            monthMoney.value = monthMoneyJSON.getString(monthMoney.key);
                            monthMoneyList.add(monthMoney);
                        }
                        queueInfoBean.data.preInfo.monthMoneys = monthMoneyList;

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

//        tv_month_num_1.setText(queueInfoBean.data.preInfo.monthNums.get(0).value);
//        tv_month_num_2.setText(queueInfoBean.data.preInfo.monthNums.get(1).value);
//        tv_month_num_3.setText(queueInfoBean.data.preInfo.monthNums.get(2).value);
//        tv_month_num_4.setText(queueInfoBean.data.preInfo.monthNums.get(3).value);

        try {
            tv_month_num_1.setText((new JSONObject(preInfo.getString("monthNum"))).getInt("1") + "");
            tv_month_num_2.setText((new JSONObject(preInfo.getString("monthNum"))).getInt("2") + "");
            tv_month_num_3.setText((new JSONObject(preInfo.getString("monthNum"))).getInt("3") + "");
            tv_month_num_4.setText((new JSONObject(preInfo.getString("monthNum"))).getInt("6") + "");
            tv_month_num_5.setText((new JSONObject(preInfo.getString("monthNum"))).getInt("12") + "");
            tv_month_num_6.setText((new JSONObject(preInfo.getString("monthNum"))).getInt("24") + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!("".equals(AppContext.getUserBean().data.autoBidAuth)) ) {
            tv_queue.setText(queueInfoBean.data.inQueue);
            tv_valid_ranking.setText(queueInfoBean.data.preInfo.validNum);
            tv_ranking_title.setText("我的排名");
            tv_my_ranking.setText(queueInfoBean.data.location);
            tv_invalid_ranking.setText(queueInfoBean.data.preInfo.invalidNum);


//            tv_month_money_1.setText(new BigDecimal(queueInfoBean.data.preInfo.monthMoneys.get(0).value).toPlainString());
//            tv_month_money_2.setText(new BigDecimal(queueInfoBean.data.preInfo.monthMoneys.get(1).value).toPlainString());
//            tv_month_money_3.setText(new BigDecimal(queueInfoBean.data.preInfo.monthMoneys.get(2).value).toPlainString());
//            tv_month_money_4.setText(new BigDecimal(queueInfoBean.data.preInfo.monthMoneys.get(3).value).toPlainString());
            try {
                tv_month_money_1.setText((new JSONObject(preInfo.getString("monthMoney"))).getInt("1") + "");
                tv_month_money_2.setText((new JSONObject(preInfo.getString("monthMoney"))).getInt("2") + "");
                tv_month_money_3.setText((new JSONObject(preInfo.getString("monthMoney"))).getInt("3") + "");
                tv_month_money_4.setText((new JSONObject(preInfo.getString("monthMoney"))).getInt("6") + "");
                tv_month_money_5.setText((new JSONObject(preInfo.getString("monthMoney"))).getInt("12") + "");
                tv_month_money_6.setText((new JSONObject(preInfo.getString("monthMoney"))).getInt("24") + "");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            tv_valid_money.setText(queueInfoBean.data.preInfo.validMoney);
            tv_invalid_money.setText(queueInfoBean.data.preInfo.invalidMoney);
        } else {
            tv_ranking_title.setText("排队总人数");
            tv_my_ranking.setText(queueInfoBean.data.preInfo.allNum);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needRefresh) {
            getData();
            getAuto();
            initQueue();
            needRefresh = false;
        }
    }

    /**
     * 获取数据
     */
    private void getData() {
        type = 1;
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        if (call2 != null) {
            call2.cancel();
        }

        call2 = ApiHttpClient.getUserInfo(AppContext.getUserBean().data.userId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                TLog.error("获取用户信息:" + e);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("获取用户信息:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        UserBean userBean = GsonUtils.jsonToBean(response, UserBean.class);
                        AppContext.setUserBean(userBean);
                        //判断是否开通托管
                        needSkip = false;
                        initLayout();
                        needSkip = true;
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
}
