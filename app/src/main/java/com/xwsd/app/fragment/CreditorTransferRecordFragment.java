package com.xwsd.app.fragment;

import android.view.LayoutInflater;
import android.view.View;

import com.xwsd.app.R;
import com.xwsd.app.adapter.BaseAdapterHelper;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BasePullUpListFragment;
import com.xwsd.app.bean.BuyRecordsBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.view.EmptyLayout;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by Gx on 2016/9/20.
 * 债权转让-详情-记录
 */
public class CreditorTransferRecordFragment extends BasePullUpListFragment {

    String id;

    @Override
    protected View setContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_common_list, null);

        id = getArguments().getString(UserParam.DATA);
        TLog.error("id：" + id);
        return view;
    }

    @Override
    public void pullUpCallBack() {

        ApiHttpClient.buyRecords(id, currentPages, each_page_num, new StringCallback() {
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
                        BuyRecordsBean buyRecordsBean = GsonUtils.jsonToBean(response, BuyRecordsBean.class);
                        setData(buyRecordsBean,TYPE_PULLUP);
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

        ApiHttpClient.buyRecords(id, currentPages, each_page_num, new StringCallback() {
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
                        BuyRecordsBean buyRecordsBean = GsonUtils.jsonToBean(response, BuyRecordsBean.class);
                        setData(buyRecordsBean,TYPE_FIRST);
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
    private void setData(BuyRecordsBean bean, int type) {
        currentPages = bean.data.page;
        allItemCount = bean.data.count;

        if (bean.data.records == null || bean.data.records.size() <= 0) {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
        }

        if (mAdapter == null) {
            mAdapter = new QuickAdapter<BuyRecordsBean.Data.Records>(getActivity(), R.layout.item_transfer, bean.data.records) {
                @Override
                protected void convert(BaseAdapterHelper helper, BuyRecordsBean.Data.Records item) {
                    helper.setText(R.id.tv_name, item.username);
                    helper.setText(R.id.tv_money, item.money + "元");
                    String[] arr = item.time.split(" ");
                    helper.setText(R.id.tv_profit, arr[0] + "\n\r" + arr[1]);
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