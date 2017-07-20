package com.xwsd.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import butterknife.Bind;
import butterknife.OnClick;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.OddBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.fragment.InvestRecordFragment;
import com.xwsd.app.fragment.InvestRecordNowFragment;
import com.xwsd.app.fragment.ProjectDetailsFragment;
import com.xwsd.app.fragment.RiskControlFragment;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.view.NavbarManage;

/**
 * Created by Gy on 2017/3/27.
 * 项目详情选项卡
 */
public class ProjectDetailTabActivity extends BaseActivity  implements View.OnClickListener {
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;
    private  int id ;

    ProjectDetailsFragment projectDetailsFragment;

    RiskControlFragment riskControlFragment;

    InvestRecordFragment investRecordFragment;

    InvestRecordNowFragment investRecordNowFragment;
    public OddBean oddBean;
    public String oddNum;
    @Bind(R.id.commitdetail)
    Button commit;
    private ProjectDetailTabActivity.TimeCount time;
    @Bind(R.id.ll_commit)
    LinearLayout ll_commit;
    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_project_tab);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        id = getIntent().getIntExtra(UserParam.ID,1);
        oddBean=(OddBean)getIntent().getSerializableExtra(UserParam.OBJ);
        oddNum=getIntent().getStringExtra(UserParam.NUM);
        TLog.error("next:oddBean:"+oddBean);
        title = null;
        switch (id) {
            case 1:
                title=getString(R.string.particulars);
                break;
            case 2:
                title=getString(R.string.notes);
                break;
            case 3:
                title=getString(R.string.control);
                break;
            case 4:
                title=getString(R.string.investment);
                break;
        }

        //设置导航栏
        navbarManage.setCentreStr(title);
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
                            time = new ProjectDetailTabActivity.TimeCount(oddBean.data.second * 1000, 1000);
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
        setFragment(id);

    }

    private void setFragment(int id) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Bundle bundle = new Bundle();
        switch (id) {
            case 1:
                    projectDetailsFragment = new ProjectDetailsFragment();

                    bundle.putInt(UserParam.DATA, 0);
                    projectDetailsFragment.setArguments(bundle);
                transaction.add(R.id.frameLayout, projectDetailsFragment).commit();
                //埋点开始
                BuriedPointUtil.buriedPoint("项目介绍项目详细");

                break;
            case 3:

                    riskControlFragment = new RiskControlFragment();

                    bundle.putInt(UserParam.DATA, 0);
                    riskControlFragment.setArguments(bundle);
                transaction.add(R.id.frameLayout, riskControlFragment).commit();

                //埋点开始
                BuriedPointUtil.buriedPoint("项目介绍风控材料");

                break;
            case 2:

                    investRecordFragment = new InvestRecordFragment();

                    bundle.putInt(UserParam.DATA, 0);
                    investRecordFragment.setArguments(bundle);
                transaction.add(R.id.frameLayout, investRecordFragment).commit();
                //埋点开始
                BuriedPointUtil.buriedPoint("项目介绍投资记录");

                break;
            case 4:
                investRecordNowFragment = new InvestRecordNowFragment();

                bundle.putInt(UserParam.DATA, 0);
                investRecordNowFragment.setArguments(bundle);
                transaction.add(R.id.frameLayout, investRecordNowFragment).commit();
                //埋点开始

                BuriedPointUtil.buriedPoint("项目介绍在投记录");

                break;
        }

    }



    @OnClick({R.id.commitdetail})
    @Override
    public void onClick(View v) {

//                判断用户是否登录
                if (AppContext.getUserBean() == null) {
                    Intent intent = new Intent(ProjectDetailTabActivity.this, UserActivity.class);
                    startActivity(intent);
                    return;
                }
                Intent intent = new Intent(ProjectDetailTabActivity.this, PromptlyInvestActivity.class);
                intent.putExtra(UserParam.TYPE, PromptlyInvestActivity.TYPE_ALL_BID);
                intent.putExtra(UserParam.DATA, oddBean.data.oddNumber);
//                intent.putExtra(UserParam.MONEY, oddBean.data.oddMoneyLast);
                intent.putExtra(UserParam.TITLE, oddBean.data.oddTitle);
                startActivity(intent);

                //埋点开始
                BuriedPointUtil.buriedPoint("项目详情立即投资");


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
