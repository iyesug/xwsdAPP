package com.xwsd.app.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xwsd.app.R;
import com.xwsd.app.activity.BidDetailsActivity;
import com.xwsd.app.activity.ProjectDetailsActivity;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseFragment;
import com.xwsd.app.bean.OddrmBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.view.EmptyLayout;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by Gx on 2016/8/23.
 * 标的详情-风险控制
 */
public class RiskControlFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener {

    @Bind(R.id.tab_radio_group)
    RadioGroup tab_radio_group;

    @Bind(R.id.empty_layout)
    EmptyLayout empty_layout;

//    ProjectDetailsActivity projectDetailsActivity;

    String oddNum;

    FragmentManager fragmentManager;

    public static OddrmBean oddrmBean;

    @Override
    protected View setContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_risk_control, null);
        return view;
    }

    @Override
    protected void init() {

//        projectDetailsActivity = (ProjectDetailsActivity) getActivity();
        if (getArguments().getInt(UserParam.DATA) == 0) {
            oddNum = ((ProjectDetailsActivity) getActivity()).oddNum;
        } else {
            oddNum = ((BidDetailsActivity) getActivity()).oddNum;
        }
        empty_layout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
        fragmentManager = getChildFragmentManager();
        getData();
    }


    /**
     * 请求数据
     */
    private void getData() {

        empty_layout.setErrorType(EmptyLayout.NETWORK_LOADING);

        ApiHttpClient.oddrm(oddNum, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                empty_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("风控信息：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        empty_layout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        oddrmBean = GsonUtils.jsonToBean(response, OddrmBean.class);
                        setData();
                    } else {
                        empty_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    empty_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }
            }
        });
    }

    /**
     * 设置数据
     */
    private void setData() {
//        设置Tab
        tab_radio_group.setOnCheckedChangeListener(this);
        ((RadioButton) tab_radio_group.getChildAt(0)).setChecked(true);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        switch (checkedId) {
            case R.id.tab_radio_borrow_data://借款资料

//                埋点开始
                BuriedPointUtil.buriedPoint("项目介绍风控材料借款资料");

                RiskBorrowDataFragment borrowDataFragment = new RiskBorrowDataFragment();
                bundle.putSerializable(UserParam.DATA, oddrmBean);
                borrowDataFragment.setArguments(bundle);
                transaction.replace(R.id.frame_content, borrowDataFragment).commit();
                return;
            case R.id.tab_radio_risk_describe://风控详情

//                埋点开始
                BuriedPointUtil.buriedPoint("项目介绍风控材料风控描述");

                RiskDescribeFragment describeFragment = new RiskDescribeFragment();
                transaction.replace(R.id.frame_content, describeFragment).commit();
                return;
            case R.id.tab_radio_property_img://产权图片

//                //埋点开始
                BuriedPointUtil.buriedPoint("项目介绍风控材料产权图片");

                bundle.putInt(UserParam.TYPE, RiskImgFragment.TYPE_PROPERTY);
                break;
            case R.id.tab_radio_borrow_procedure://借款手续

                //埋点开始
                BuriedPointUtil.buriedPoint("项目介绍风控材料借款手续");

                bundle.putInt(UserParam.TYPE, RiskImgFragment.TYPE_BORROW_PROCEDURE);
                break;
            case R.id.tab_radio_risk_img://风控图片

                //埋点开始
                BuriedPointUtil.buriedPoint("项目介绍风控材料风控图片");

                bundle.putInt(UserParam.TYPE, RiskImgFragment.TYPE_RISK);
                break;
            case R.id.tab_radio_checkout_img://验车图片

                //埋点开始
                BuriedPointUtil.buriedPoint("项目介绍风控材料验车图片");

                bundle.putInt(UserParam.TYPE, RiskImgFragment.TYPE_CHECKOUT);
                break;
            case R.id.tab_radio_investigation://征信报告

                //埋点开始
                BuriedPointUtil.buriedPoint("项目介绍风控材料征信报告");

                bundle.putInt(UserParam.TYPE, RiskImgFragment.TYPE_INVESTIGATION);
                break;
        }

        RiskImgFragment imgFragment = new RiskImgFragment();
        imgFragment.setArguments(bundle);
        transaction.replace(R.id.frame_content, imgFragment).commit();
    }
}