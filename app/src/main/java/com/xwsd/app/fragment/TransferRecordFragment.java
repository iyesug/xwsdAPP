package com.xwsd.app.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.activity.ProjectDetailsActivity;
import com.xwsd.app.adapter.BaseAdapterHelper;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BasePullUpListFragment;
import com.xwsd.app.base.BaseUpDownListFragment;
import com.xwsd.app.bean.CreditorTransferBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.EmptyLayout;
import com.xwsd.app.view.TitleTextView;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gx on 2016/8/29.
 * 转让记录
 */
public class TransferRecordFragment extends BaseUpDownListFragment {

    String type = "sell";

   // DecimalFormat decimalFormat = new DecimalFormat("0.0");

    @Override
    protected View setContentView(LayoutInflater inflater) {

        return inflater.inflate(R.layout.fragment_refresh_lists, null);
    }

    /**
     * 上拉加载
     */
    @Override
    public void pullUpCallBack() {
        ApiHttpClient.usercrtrs(AppContext.getUserBean().data.userId, currentPages, each_page_num, type, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                setPullUpState(BasePullUpListFragment.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("转让记录：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        setPullUpState(BasePullUpListFragment.SUCCEED);
                        CreditorTransferBean infosBean = GsonUtils.jsonToBean(response, CreditorTransferBean.class);
                        setData(infosBean,TYPE_PULLUP);
                    }else if (jsonObject.getInt("status") == 88){
                        ToastUtil.showToast(jsonObject.getString("msg"));
                        Intent Fintent = new Intent();
                        Fintent.putExtra(UserParam.TYPE, 0);
                        Fintent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                        startActivity(Fintent);
                        getActivity().finish();
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

    /**
     * 下拉刷新
     */
    @Override
    public void pullDownCallBack() {
        currentPages = 1;
        ApiHttpClient.usercrtrs(AppContext.getUserBean().data.userId, currentPages, each_page_num, type, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showToastShort(R.string.refurbish_failure);
                swipe_refresh_layout.setRefreshing(false);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("转让记录：" + response);
                swipe_refresh_layout.setRefreshing(false);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        CreditorTransferBean infosBean = GsonUtils.jsonToBean(response, CreditorTransferBean.class);
                        currentPages = infosBean.data.page;
                        allItemCount = infosBean.data.count;
                        mAdapter.replaceAll(infosBean.data.records);
                    } else if (jsonObject.getInt("status") == 88){
                        ToastUtil.showToast(jsonObject.getString("msg"));
                        Intent Fintent = new Intent();
                        Fintent.putExtra(UserParam.TYPE, 0);
                        Fintent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                        startActivity(Fintent);
                        getActivity().finish();
                    }else {
                        ToastUtil.showToastShort(R.string.refurbish_failure);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.showToastShort(R.string.refurbish_failure);
                }
            }
        });
    }

    @Override
    public void firstRequestData() {
        super.firstRequestData();
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        ApiHttpClient.usercrtrs(AppContext.getUserBean().data.userId, currentPages, each_page_num, type, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("转让记录：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        CreditorTransferBean infosBean = GsonUtils.jsonToBean(response, CreditorTransferBean.class);
                        setData(infosBean,TYPE_FIRST);
                    } else if (jsonObject.getInt("status") == 88){
                        ToastUtil.showToast(jsonObject.getString("msg"));
                        Intent Fintent = new Intent();
                        Fintent.putExtra(UserParam.TYPE, 0);
                        Fintent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                        startActivity(Fintent);
                        getActivity().finish();
                    }else {
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
    private void setData(CreditorTransferBean bean, int type) {
        currentPages = bean.data.page;
        allItemCount = bean.data.count;

        if (bean.data.records == null || bean.data.records.size() <= 0) {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
        }

        if (mAdapter == null) {
            mAdapter = new QuickAdapter<CreditorTransferBean.Data.Records>(getActivity(), R.layout.item_transfer_record, bean.data.records) {
                @Override
                protected void convert(BaseAdapterHelper helper, final CreditorTransferBean.Data.Records item) {
                    ((TitleTextView) helper.getView(R.id.tv_time)).setContent(item.time);
                    ((TitleTextView) helper.getView(R.id.tv_principal)).setContent((item.money) + "元");
                    ((TitleTextView) helper.getView(R.id.tv_interest)).setContent((item.interest) + "元");

                    helper.setOnClickListener(R.id.prot_bid, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BuriedPointUtil.buriedPoint("账户债权转让转让记录原标查询");

                            Intent intent = new Intent(getActivity(), ProjectDetailsActivity.class);
                            intent.putExtra("oddNumber", item.oddNumber);
                            intent.putExtra("type", "transfer");
                            startActivity(intent);
                        }
                    });
                }
            };

            list_view.setAdapter(mAdapter);
//            list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    if (position < mAdapter.getCount()) {
//                        Intent intent = new Intent(getActivity(), BidDetailsActivity.class);
//                        intent.putExtra("oddNumber", ((CreditorTransferBean.Data.Records) mAdapter.getItem(position)).oddNumber);
//                        startActivity(intent);
//                    }
//                }
//            });
        } else {
            if (type == 0) {
                mAdapter.replaceAll(bean.data.records);
            } else {
                mAdapter.addAll(bean.data.records);
            }
        }
    }
}