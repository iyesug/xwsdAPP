package com.xwsd.app.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.xwsd.app.R;
import com.xwsd.app.activity.BidDetailsActivity;
import com.xwsd.app.activity.ProjectDetailTabActivity;
import com.xwsd.app.activity.ProjectDetailsActivity;
import com.xwsd.app.adapter.BaseAdapterHelper;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BasePullUpListFragment;
import com.xwsd.app.bean.InvestRecordBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.view.EmptyLayout;
import com.xwsd.app.view.MDialog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by Gx on 2016/8/23.
 * 标的详情-投资记录
 */
public class InvestRecordFragment extends BasePullUpListFragment {

//    ProjectDetailsActivity projectDetailsActivity;
    String oddNum;
    @Override
    protected View setContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_invest_record, null);

        return view;
    }

    @Override
    public void pullUpCallBack() {

        ApiHttpClient.oddtenders(oddNum, currentPages, each_page_num, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                setPullUpState(BasePullUpListFragment.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("投资记录：" + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        setPullUpState(BasePullUpListFragment.SUCCEED);
                        InvestRecordBean investRecordBean = GsonUtils.jsonToBean(response, InvestRecordBean.class);
                        setData(investRecordBean,TYPE_PULLUP);
                    } else {
                        setPullUpState(BasePullUpListFragment.LOAD_ERROR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    setPullUpState(BasePullUpListFragment.LOAD_ERROR);
                }
            }
        });
    }

    @Override
    protected void init() {
        super.init();
//        projectDetailsActivity = (ProjectDetailsActivity) getActivity();
        if (getArguments().getInt(UserParam.DATA) == 0) {
            oddNum = ((ProjectDetailTabActivity) getActivity()).oddNum;
        } else {
            oddNum = ((BidDetailsActivity) getActivity()).oddNum;
        }
        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

        getData();

    }

    private void getData() {

        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);

        ApiHttpClient.oddtenders(oddNum, currentPages, each_page_num, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("投资记录：" + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        InvestRecordBean investRecordBean = GsonUtils.jsonToBean(response, InvestRecordBean.class);
                        setData(investRecordBean,TYPE_FIRST);
                    } else {
                        mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }
            }
        });
    }
    /**
     * 设置列表信息
     *
     * @param bean
     */
    private void setData(InvestRecordBean bean , int type) {
        currentPages = bean.data.page;
        allItemCount = bean.data.count;

        if (bean.data.records == null || bean.data.records.size() <= 0) {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
        }

        if (mAdapter == null) {
            mAdapter = new QuickAdapter<InvestRecordBean.Data.Records>(getActivity(), R.layout.item_invest_record, bean.data.records) {
                @Override
                protected void convert(BaseAdapterHelper helper, InvestRecordBean.Data.Records item) {
                    helper.setText(R.id.tv_tag, String.valueOf(helper.getPosition()+1));
                    helper.setText(R.id.tv_name, item.username);
                    helper.setText(R.id.tv_money, item.money);
                    String[] arr = item.time.split(" ");
                    helper.setText(R.id.tv_time, arr[0] + "\n\r" + arr[1]);
                }
            };

            list_view.setAdapter(mAdapter);

        } else {
            if (type == 0) {
                mAdapter.replaceAll(bean.data.records);
            } else {
                mAdapter.addAll(bean.data.records);
            }
        }
    }
}