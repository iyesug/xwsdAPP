package com.xwsd.app.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xwsd.app.R;
import com.xwsd.app.activity.BidDetailsActivity;
import com.xwsd.app.activity.ProjectDetailsActivity;
import com.xwsd.app.base.BaseFragment;
import com.xwsd.app.bean.OddBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.view.TitleTextView;

import butterknife.Bind;

/**
 * Created by Gx on 2016/8/23.
 * 标的详情-项目详情-选项卡
 */
public class ProjectDetailsTabFragment extends BaseFragment {

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.ll_group_1)
    LinearLayout ll_group_1;

    @Bind(R.id.ll_group_2)
    LinearLayout ll_group_2;

    @Bind(R.id.ll_group_3)
    LinearLayout ll_group_3;

    @Bind(R.id.v_1)
    View v_1;

    @Bind(R.id.v_2)
    View v_2;

    @Bind(R.id.v_3)
    View v_3;

    @Bind(R.id.ttv_1)
    TitleTextView ttv_1;

    @Bind(R.id.ttv_2)
    TitleTextView ttv_2;

    @Bind(R.id.ttv_3)
    TitleTextView ttv_3;

    @Bind(R.id.ttv_4)
    TitleTextView ttv_4;

    @Bind(R.id.ttv_5)
    TitleTextView ttv_5;

    @Bind(R.id.ttv_6)
    TitleTextView ttv_6;

    //    ProjectDetailsActivity projectDetailsActivity;
    OddBean oddBean;

    @Override
    protected View setContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_project_details_tab, null);
        return view;
    }

    @Override
    protected void init() {
//        projectDetailsActivity = (ProjectDetailsActivity) getActivity();
        if (getArguments().getInt(UserParam.DATA) == 0) {
            oddBean = ((ProjectDetailsActivity) getActivity()).oddBean;
        } else {
            oddBean = ((BidDetailsActivity) getActivity()).oddBean;
        }
        switch (getArguments().getInt(UserParam.TYPE, 0)) {
            case 0://用户信息
                //隐藏掉不需要的内容
                ll_group_3.setVisibility(View.GONE);
                v_2.setVisibility(View.GONE);
                ttv_4.setVisibility(View.GONE);
                tv_title.setText("用户信息");

                //设置用户信息数据
                ttv_1.setTitle("昵称:");
                ttv_1.setContent(oddBean.data.user1.username);
                ttv_2.setTitle("年龄:");
                ttv_2.setContent(oddBean.data.user1.age);
                ttv_3.setTitle("婚姻:");
                ttv_3.setContent(oddBean.data.user1.marital);

                break;
            case 1://账户详情
                ll_group_3.setVisibility(View.GONE);
                v_2.setVisibility(View.GONE);
                ttv_4.setVisibility(View.GONE);
                tv_title.setText("账户详情");

                ttv_1.setTitle("共计借入(元):");
                ttv_1.setContent(oddBean.data.user3.borrowMoney);
                ttv_2.setTitle("待还金额(元):");
                ttv_2.setContent(oddBean.data.user3.stayMoney);
                ttv_3.setTitle("共计借出(元):");
                ttv_3.setContent(oddBean.data.user3.borrowOut);

                break;
            case 2://个人信用
                v_3.setVisibility(View.GONE);
                ttv_6.setVisibility(View.GONE);
                tv_title.setText("个人信用");

                ttv_1.setTitle("申请借款(笔):");
                ttv_1.setContent(oddBean.data.user2.borrowCount);
                ttv_2.setTitle("成功借款(笔):");
                ttv_2.setContent(oddBean.data.user2.successCount);
                ttv_3.setTitle("借款总额(元):");
                ttv_3.setContent(oddBean.data.user2.borrowMoney);
                ttv_4.setTitle("还清笔数(笔):");
                ttv_4.setContent(oddBean.data.user2.endCount);
                ttv_5.setTitle("还清本息(元):");
                ttv_5.setContent(oddBean.data.user2.stayMoney);

                break;
            case 3://车辆详情
                tv_title.setText("车辆详情");

                ttv_1.setTitle("车辆品牌型号:");
                ttv_2.setTitle("行驶公里数:");
                ttv_3.setTitle("车身颜色:");
                ttv_4.setTitle("排量:");
                ttv_5.setTitle("购买价格:");
                ttv_6.setTitle("抵押估价:");
                if (oddBean.data.oddLoanRemark != null) {

                    if (oddBean.data.oddLoanRemark.车辆品牌型号 != null) {
                        ttv_1.setContent(oddBean.data.oddLoanRemark.车辆品牌型号);
                    }

                    if (oddBean.data.oddLoanRemark.行驶公里数 != null) {
                        ttv_2.setContent(oddBean.data.oddLoanRemark.行驶公里数);
                    }
                    if (oddBean.data.oddLoanRemark.车身颜色 != null) {
                        ttv_3.setContent(oddBean.data.oddLoanRemark.车身颜色);
                    }

                    if (oddBean.data.oddLoanRemark.排量 != null) {
                        ttv_4.setContent(oddBean.data.oddLoanRemark.排量);
                    }

                    if (oddBean.data.oddLoanRemark.购买价格 != null) {
                        ttv_5.setContent(oddBean.data.oddLoanRemark.购买价格);
                    }

                    if (oddBean.data.oddLoanRemark.抵押估价 != null) {
                        ttv_6.setContent(oddBean.data.oddLoanRemark.抵押估价);
                    }
                }
                break;
        }

    }
}
