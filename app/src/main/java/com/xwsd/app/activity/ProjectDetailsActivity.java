package com.xwsd.app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.InvestRecordNowBean;
import com.xwsd.app.bean.OddBean;
import com.xwsd.app.constant.BroadcastParam;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.EmptyLayout;
import com.xwsd.app.view.NavbarManage;
import com.xwsd.app.view.ObserveScrollView;
import com.xwsd.app.view.TitleTextView;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Created by Gx on 2016/8/24.
 * 项目-标-详情（deprecate）
 */
public class ProjectDetailsActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Bind(R.id.error_layout)
    EmptyLayout error_layout;

    private String[] titles;



    @Bind(R.id.tv_oddTitle)
    TextView tv_oddTitle;

    @Bind(R.id.tv_oddYearRate)
    TextView tv_oddYearRate;

    @Bind(R.id.pb_schedule)
    ProgressBar pb_schedule;


    @Bind(R.id.ll_oddMoney)
    LinearLayout ll_oddMoney;

    @Bind(R.id.ll_oddMoneyLast)
    LinearLayout ll_oddMoneyLast;
    /**
     * 还款方式
     */
    @Bind(R.id.ttv_1)
    TitleTextView ttv_1;

    /**
     * 奖励年化率
     */
    @Bind(R.id.tv_rewards)
    TextView tv_rewards;

    /**
     * 还款期限
     */
    @Bind(R.id.ttv_3)
    TitleTextView ttv_3;

    /**
     * 还款方式
     */
    @Bind(R.id.ttv_4)
    TitleTextView ttv_4;

    /**
     * 发布时间
     */
    @Bind(R.id.ttv_5)
    TitleTextView ttv_5;

    /**
     * 发布时间
     */
    @Bind(R.id.ttv_6)
    TitleTextView ttv_6;

    @Bind(R.id.commit)
    Button commit;

    @Bind(R.id.ll_commit)
    LinearLayout ll_commit;

    DecimalFormat decimalFormat = new DecimalFormat("0.0");

    public OddBean oddBean;

    public String oddNum;

    public String type;

     private TimeCount time;

    int progress = 0;

    @Bind(R.id.scroll_view)
    ObserveScrollView scroll_view;

    @Bind(R.id.text_particulars)
    TextView tab_project_1;

    @Bind(R.id.text_control)
    TextView tab_project_2;

    @Bind(R.id.text_notes)
    TextView tab_project_3;

    @Bind(R.id.text_investment)
    TextView tab_project_4;



    @Bind(R.id.iv_arrows)
    TextView iv_arrows;

/*    @Bind(R.id.indicator_1)
    View indicator_1;
    @Bind(R.id.indicator_2)
    View indicator_2;
    @Bind(R.id.indicator_3)
    View indicator_3;
    @Bind(R.id.indicator_4)
    View indicator_4;*/

    @Bind(R.id.process)
    TextView process;

    @Bind(R.id.ll_time)
    TextView ll_time;

    /**
     * 广播接收者，用于处理相对应的Action
     */
    private MyBroadcastReciever myBroadcastReciever = new MyBroadcastReciever();

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipe_refresh_layout;


    /**
     * 广播类(用来更新数据)
     */
    class MyBroadcastReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {

                case BroadcastParam.BUY_BID:
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
        setContentView(R.layout.activity_project_details);
        navbarManage = new NavbarManage(this);

    }

    @Override
    protected void init(Bundle savedInstanceState) {
        title=getString(R.string.bid_details);
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.bid_details));
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
        ttv_6.setVisibility(View.GONE);
        oddNum = getIntent().getStringExtra("oddNumber");
        type = getIntent().getStringExtra("type");
        //设置错误布局点击事件
        error_layout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(1);
            }
        });

        //触碰软键盘外面收缩键盘
        scroll_view.setGestureDetector(new GestureDetector(ProjectDetailsActivity.this, new GestureDetector.OnGestureListener() {
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
        intentFilter.addAction(BroadcastParam.BUY_BID);
        registerReceiver(myBroadcastReciever, intentFilter);

        initRefresh();
        getData(1);
        getNum();
    }

    private void getNum(){
        ApiHttpClient.oddtendersNow(oddNum, 1, 30, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }
            @Override
            public void onResponse(String response, int id) {
                TLog.error("投资记录：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        InvestRecordNowBean investRecordBean = GsonUtils.jsonToBean(response, InvestRecordNowBean.class);
                        int num = investRecordBean.data.count;
                        if(num > 0){
                            iv_arrows.setVisibility(View.VISIBLE);
                            iv_arrows.setText(num+"");
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
    @Override
    public void onRefresh() {
        getData(2);
        getNum();
    }

    /**
     * 初始化下拉刷新
     */
    private void initRefresh() {

        swipe_refresh_layout.setOnRefreshListener(this);
        swipe_refresh_layout.setColorSchemeResources(
                R.color.swiperefresh_color1,
                R.color.swiperefresh_color2,
                R.color.swiperefresh_color3,
                R.color.swiperefresh_color4);
    }

    /**
     * @param type
     */
    private void getData(final int type) {
        if (type == 1) {
            error_layout.setErrorType(EmptyLayout.NETWORK_LOADING);
            ll_commit.setVisibility(View.GONE);
        }

//        得到详细信息
        ApiHttpClient.odd(oddNum, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

                if (type == 1) {
                    error_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
                } else {
                    swipe_refresh_layout.setRefreshing(false);
                    ToastUtil.showToastShort(R.string.refurbish_failure);
                }

            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("标的详情：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {

                        if (type == 1) {
                            error_layout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        } else {
                            swipe_refresh_layout.setRefreshing(false);
                        }
                        oddBean = GsonUtils.jsonToBean(response, OddBean.class);
                        setData();
                    } else if (jsonObject.getInt("status") == 88){
                        ToastUtil.showToast(getString(R.string.please_relogin));
                        Intent Fintent = new Intent();
                        Fintent.putExtra(UserParam.TYPE, 0);
                        Fintent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                        startActivity(Fintent);
                        finish();
                    }else {

                        if (type == 1) {
                            error_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
                        } else {
                            swipe_refresh_layout.setRefreshing(false);
                            ToastUtil.showToastShort(R.string.refurbish_failure);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    if (type == 1) {
                        error_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
                    } else {
                        swipe_refresh_layout.setRefreshing(false);
                        ToastUtil.showToastShort(R.string.refurbish_failure);
                    }
                }
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pb_schedule.setProgress(msg.arg1);
            handler.postDelayed(updateProgress, 50);
        }
    };

    Runnable updateProgress = new Runnable() {
        @Override
        public void run() {
            progress += 10;

            if (progress >= (int) oddBean.data.schedule) {
                pb_schedule.setProgress((int) oddBean.data.schedule);
                handler.removeCallbacks(updateProgress);
            } else {
                Message msg = handler.obtainMessage();
                msg.arg1 = progress;
                handler.sendMessage(msg);
            }
        }
    };

    /**
     * 设置数据
     */

    private void setData() {
        ll_commit.setVisibility(View.VISIBLE);
        tv_oddYearRate.setText(decimalFormat.format(oddBean.data.oddYearRate * 100f));
//        pb_schedule.setProgress((int) oddBean.data.schedule);
        progress = 0;
        handler.removeCallbacks(updateProgress);
        handler.post(updateProgress);
        tv_oddTitle.setText(oddBean.data.oddTitle);

        //oddBean.data.oddPeriod
        ((TextView) ll_oddMoneyLast.findViewById(R.id.tv_title)).setText("剩余可投金额");
        ((TextView) ll_oddMoneyLast.findViewById(R.id.tv_content)).setText(String.valueOf((int) oddBean.data.oddMoneyLast));

        ((TextView) ll_oddMoney.findViewById(R.id.tv_title)).setText("项目金额");
        ((TextView) ll_oddMoney.findViewById(R.id.tv_content)).setText(oddBean.data.oddMoney);

        ll_time.setText(oddBean.data.oddPeriod);

        process.setText((int)oddBean.data.schedule + "%");

        ttv_1.setTitle("还款方式：");
        ttv_1.setContent(oddBean.data.oddRepayType);
       // ttv_2.setTitle("奖励年化率：");
        String rewards = decimalFormat.format(oddBean.data.oddReward * 100f);
        if(rewards.equals("0.0")){
            tv_rewards.setVisibility(View.GONE);
        }else {
            tv_rewards.setText("+" + rewards + "%");
        }
        ttv_3.setTitle("还款期限：");
        ttv_3.setContent(oddBean.data.oddPeriod);
        ttv_4.setTitle("借款用途：");
        ttv_4.setContent(oddBean.data.oddUse);
        ttv_5.setTitle("发布时间：");
        ttv_5.setContent(oddBean.data.addtime.split(" ")[0]);

        commit.setBackgroundResource(R.drawable.bg_button_sharp_gray);
        commit.setClickable(false);
        switch (oddBean.data.progress) {
            case "start":
                if (oddBean.data.schedule == 100) {//复审中
                    commit.setText("复审中");
                } else {
                    switch (oddBean.data.second) {
                        case -1://立即投标
                            commit.setBackgroundResource(R.drawable.select_submit_button_simple);
                            commit.setClickable(true);
                            break;
                        case 0://显示开始时间 2016-10-20 14:00:00
                            commit.setText("尚未开始");
                            break;
                        default://开始倒计时
                            time = new TimeCount(oddBean.data.second * 1000, 1000);
                            time.start();
                            break;
                    }
                }
                break;
            case "run"://还款中
                commit.setText("还款中");
                break;
            default://已结束
                commit.setText("已结束");
                break;
        }
//        初始化TAB
        initTab();
    }

    /**
     * 初始化TAB
     */
    private void initTab() {
//        titles = new String[]{
//                getString(R.string.project_details),
//                getString(R.string.risk_control),
//                getString(R.string.invest_record),
//                getString(R.string.invest_record_now)};
//
//        //设置TabLayout的模式
//        tab_FindFragment_title.removeAllTabs();
//        tab_FindFragment_title.setTabMode(TabLayout.MODE_FIXED);
//        //为TabLayout添加tab名称
//        for (int i = 0; i < titles.length; i++) {
//            tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(titles[i]));
//        }
//
//        tab_FindFragment_title.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                setTabSelection(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
/*        setTabSelection(0);*/
    }

/*
    */
/**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     *//*

    private void hideFragments(FragmentTransaction transaction) {

        if (projectDetailsFragment != null) {
            transaction.hide(projectDetailsFragment);
        }
        if (riskControlFragment != null) {
            transaction.hide(riskControlFragment);
        }
        if (investRecordFragment != null) {
            transaction.hide(investRecordFragment);
        }
        if (investRecordNowFragment != null) {
            transaction.hide(investRecordNowFragment);
        }
    }
*/


/*    *//**
     * 加载相应的Fragment
     *

     *//*
    private void setTabSelection(int position) {
        // 开启一个Fragment事务
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (position) {
            case 0:
                if (projectDetailsFragment == null) {
                    projectDetailsFragment = new ProjectDetailsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(UserParam.DATA, 0);
                    projectDetailsFragment.setArguments(bundle);
                    transaction.add(R.id.frame_content, projectDetailsFragment);
                } else {
                    transaction.show(projectDetailsFragment);
                }

                //埋点开始
                BuriedPointUtil.buriedPoint("项目介绍项目详细");

*//*                tab_project_1.setTextColor(getResources().getColor(R.color.blue_simple));
                tab_project_2.setTextColor(getResources().getColor(R.color.backk));
                tab_project_3.setTextColor(getResources().getColor(R.color.backk));
                tab_project_right.setTextColor(getResources().getColor(R.color.backk));
                indicator_1.setVisibility(View.VISIBLE);
                indicator_2.setVisibility(View.INVISIBLE);
                indicator_3.setVisibility(View.INVISIBLE);
                indicator_4.setVisibility(View.INVISIBLE);*//*

                break;
            case 1:
                if (riskControlFragment == null) {
                    riskControlFragment = new RiskControlFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(UserParam.DATA, 0);
                    riskControlFragment.setArguments(bundle);
                    transaction.add(R.id.frame_content, riskControlFragment);
                } else {
                    transaction.show(riskControlFragment);
                }

                //埋点开始
                BuriedPointUtil.buriedPoint("项目介绍风控材料");
                tab_project_1.setTextColor(getResources().getColor(R.color.backk));
                tab_project_2.setTextColor(getResources().getColor(R.color.blue_simple));
                tab_project_3.setTextColor(getResources().getColor(R.color.backk));
                tab_project_right.setTextColor(getResources().getColor(R.color.backk));
                indicator_1.setVisibility(View.INVISIBLE);
                indicator_2.setVisibility(View.VISIBLE);
                indicator_3.setVisibility(View.INVISIBLE);
                indicator_4.setVisibility(View.INVISIBLE);
                break;
            case 2:
                if (investRecordFragment == null) {
                    investRecordFragment = new InvestRecordFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(UserParam.DATA, 0);
                    investRecordFragment.setArguments(bundle);
                    transaction.add(R.id.frame_content, investRecordFragment);
                } else {
                    transaction.show(investRecordFragment);
                }
                //埋点开始
                BuriedPointUtil.buriedPoint("项目介绍投资记录");
                tab_project_1.setTextColor(getResources().getColor(R.color.backk));
                tab_project_2.setTextColor(getResources().getColor(R.color.backk));
                tab_project_3.setTextColor(getResources().getColor(R.color.blue_simple));
                tab_project_right.setTextColor(getResources().getColor(R.color.backk));
                indicator_1.setVisibility(View.INVISIBLE);
                indicator_2.setVisibility(View.INVISIBLE);
                indicator_3.setVisibility(View.VISIBLE);
                indicator_4.setVisibility(View.INVISIBLE);
                break;
            case 3:
                    investRecordNowFragment = new InvestRecordNowFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(UserParam.DATA, 0);
                    investRecordNowFragment.setArguments(bundle);
                    transaction.add(R.id.frame_content, investRecordNowFragment);
                //埋点开始

                BuriedPointUtil.buriedPoint("项目介绍在投记录");
                tab_project_1.setTextColor(getResources().getColor(R.color.backk));
                tab_project_2.setTextColor(getResources().getColor(R.color.backk));
                tab_project_3.setTextColor(getResources().getColor(R.color.backk));
                tab_project_right.setTextColor(getResources().getColor(R.color.blue_simple));
                indicator_1.setVisibility(View.INVISIBLE);
                indicator_2.setVisibility(View.INVISIBLE);
                indicator_3.setVisibility(View.INVISIBLE);
                indicator_4.setVisibility(View.VISIBLE);
                break;
        }
        transaction.commitAllowingStateLoss();
    }*/

    @OnClick({R.id.commit,
            R.id.text_particulars,R.id.tab_particulars,
            R.id.text_notes,R.id.tab_notes,
            R.id.text_control,R.id.tab_control,
            R.id.text_investment,R.id.tab_investment})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:
//                判断用户是否登录
                if (AppContext.getUserBean() == null) {
                    Intent intent = new Intent(ProjectDetailsActivity.this, UserActivity.class);
                    startActivity(intent);
                    return;
                }
                Intent intent = new Intent(ProjectDetailsActivity.this, PromptlyInvestActivity.class);
                intent.putExtra(UserParam.TYPE, PromptlyInvestActivity.TYPE_ALL_BID);
                intent.putExtra(UserParam.DATA, oddBean.data.oddNumber);
//                intent.putExtra(UserParam.MONEY, oddBean.data.oddMoneyLast);
                intent.putExtra(UserParam.TITLE, oddBean.data.oddTitle);
                startActivity(intent);

                //埋点开始
                BuriedPointUtil.buriedPoint("项目介绍立即投资");

                break;
            case R.id.text_particulars:

                jump(1);

                break;
            case R.id.tab_particulars:

                jump(1);

                break;
            case R.id.text_notes:
                jump(2);
                break;
            case R.id.tab_notes:
                jump(2);
                break;
            case R.id.text_control:
                jump(3);
                break;
            case R.id.tab_control:
                jump(3);
                break;
            case R.id.text_investment:
                jump(4);
                break;
            case R.id.tab_investment:
                jump(4);
                break;
        }
    }

    private void jump(int id) {
        Intent intent;
        intent = new Intent(this, ProjectDetailTabActivity.class);
        intent.putExtra(UserParam.ID, id);
        intent.putExtra(UserParam.OBJ,oddBean);
        intent.putExtra(UserParam.NUM,oddNum);
        TLog.error("this:oddBean:"+oddBean);
        startActivity(intent);
    }


    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {// 计时完毕
            commit.setBackgroundResource(R.drawable.select_submit_button_simple);
            commit.setText(getString(R.string.immediate_investment));
            commit.setClickable(true);
        }
        @Override
        public void onTick(long millisUntilFinished) {// 计时过程
            StringBuffer timeStr = new StringBuffer();
            int second = (int)(millisUntilFinished / 1000) % 60;
            int minute = ((int)(millisUntilFinished / 1000) - second) / 60;
            if (minute > 0) {
                if (minute < 10) {
                    timeStr.append("0");
                }
                timeStr.append(minute);
            } else {
                timeStr.append("00");
            }

            timeStr.append(":");

            if (second > 0) {
                if (second < 10) {
                    timeStr.append("0");
                }
                timeStr.append(second);
            } else {
                timeStr.append("00");
            }
            commit.setText(timeStr.toString());

        }
    }
}
