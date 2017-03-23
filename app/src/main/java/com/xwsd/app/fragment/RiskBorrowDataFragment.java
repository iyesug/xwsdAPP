package com.xwsd.app.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;

import com.xwsd.app.R;
import com.xwsd.app.adapter.BaseAdapterHelper;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.base.BaseFragment;
import com.xwsd.app.bean.OddrmBean;
import com.xwsd.app.constant.UserParam;

import butterknife.Bind;

/**
 * Created by Gx on 2016/9/14.
 * 风险控制-借款资料
 */
public class RiskBorrowDataFragment extends BaseFragment {
    @Bind(R.id.grid_view)
    GridView grid_view;

    @Override
    protected View setContentView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_risk_borrow_data, null);
    }

    @Override
    protected void init() {
        if (getArguments().getSerializable(UserParam.DATA) != null) {
            OddrmBean oddrmBean = (OddrmBean) getArguments().getSerializable(UserParam.DATA);
            grid_view.setAdapter(new QuickAdapter<String>(getActivity(), R.layout.item_risk_borrow_data, oddrmBean.data.controlVars) {
                @Override
                protected void convert(BaseAdapterHelper helper, String item) {
                    helper.setText(R.id.tv_tag, item);
                }
            });
        }
    }
}
