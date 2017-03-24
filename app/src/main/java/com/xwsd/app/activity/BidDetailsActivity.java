package com.xwsd.app.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.OddBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.fragment.InvestRecordFragment;
import com.xwsd.app.fragment.ProjectDetailsFragment;
import com.xwsd.app.fragment.RiskControlFragment;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.view.EmptyLayout;
import com.xwsd.app.view.NavbarManage;
import com.xwsd.app.view.TitleTextView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by Gx on 2016/8/22.
 * 标的详情页
 */
public class BidDetailsActivity extends BaseActivity {

    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Bind(R.id.tab_layout)
    TabLayout tab_FindFragment_title;

    @Bind(R.id.error_layout)
    EmptyLayout error_layout;

    private String[] titles;

    ProjectDetailsFragment projectDetailsFragment;

    RiskControlFragment riskControlFragment;

    InvestRecordFragment investRecordFragment;

    @Bind(R.id.tv_oddTitle)
    TextView tv_oddTitle;

    @Bind(R.id.tv_oddYearRate)
    TextView tv_oddYearRate;

    @Bind(R.id.tv_oddMoney)
    TextView tv_oddMoney;

    /**
     * 还款方式
     */
    @Bind(R.id.ttv_1)
    TitleTextView ttv_1;


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
     *奖励年化率
     */
    @Bind(R.id.ttv_6)
    TitleTextView ttv_6;


    DecimalFormat decimalFormat = new DecimalFormat("0.0");

    public OddBean oddBean;

    public String oddNum;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_bid_details);
        navbarManage = new NavbarManage(this);

    }

    @Override
    protected void init(Bundle savedInstanceState) {
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

        oddNum = getIntent().getStringExtra("oddNumber");


        //设置错误布局点击事件
        error_layout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

        getData();
    }

    private void getData() {
        error_layout.setErrorType(EmptyLayout.NETWORK_LOADING);
//        得到详细信息
        ApiHttpClient.odd(oddNum, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                error_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("标的详情：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        error_layout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        oddBean = GsonUtils.jsonToBean(response, OddBean.class);

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

    /**
     * 设置数据
     */
    private void setData() {
        tv_oddYearRate.setText(decimalFormat.format(oddBean.data.oddYearRate * 100f));
        tv_oddTitle.setText(oddBean.data.oddTitle);
        tv_oddMoney.setText(oddBean.data.oddMoney);

        ttv_1.setTitle("还款方式：");
        ttv_1.setContent(oddBean.data.oddRepayType);
        ttv_3.setTitle("发布时间：");
        ttv_3.setContent(oddBean.data.addtime.split(" ")[0]);
        ttv_4.setTitle("借款方式：");
        ttv_4.setContent(oddBean.data.oddUse);
        ttv_5.setTitle("还款日期：");
        ttv_5.setContent(oddBean.data.oddPeriod);
        ttv_6.setTitle("奖励年化率：");
        ttv_6.setContent(decimalFormat.format(oddBean.data.oddReward * 100f)+"%");

//        初始化TAB
        initTab();
    }

    /**
     * 初始化TAB
     */
    private void initTab() {
        titles = new String[]{
                getString(R.string.project_details),
                getString(R.string.risk_control),
                getString(R.string.invest_record)};

        //设置TabLayout的模式
        tab_FindFragment_title.setTabMode(TabLayout.MODE_FIXED);
        //为TabLayout添加tab名称
        for (int i = 0; i < titles.length; i++) {
            tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(titles[i]));
        }

        tab_FindFragment_title.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setTabSelection(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setTabSelection(0);
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
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
                if (projectDetailsFragment == null) {
                    projectDetailsFragment = new ProjectDetailsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(UserParam.DATA,1);
                    projectDetailsFragment.setArguments(bundle);
                    transaction.add(R.id.frame_content, projectDetailsFragment);
                } else {
                    transaction.show(projectDetailsFragment);
                }
                break;
            case 1:
                if (riskControlFragment == null) {
                    riskControlFragment = new RiskControlFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(UserParam.DATA,1);
                    riskControlFragment.setArguments(bundle);
                    transaction.add(R.id.frame_content, riskControlFragment);
                } else {
                    transaction.show(riskControlFragment);
                }
                break;
            case 2:
                if (investRecordFragment == null) {
                    investRecordFragment = new InvestRecordFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(UserParam.DATA,1);
                    investRecordFragment.setArguments(bundle);
                    transaction.add(R.id.frame_content, investRecordFragment);
                } else {
                    transaction.show(investRecordFragment);
                }
                break;
        }
        transaction.commitAllowingStateLoss();
    }
}
