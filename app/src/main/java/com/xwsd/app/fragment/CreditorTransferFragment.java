package com.xwsd.app.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import com.xwsd.app.R;
import com.xwsd.app.activity.BidDetailsActivity;
import com.xwsd.app.activity.TransferDetailsActivity;
import com.xwsd.app.adapter.BaseAdapterHelper;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BasePullUpListFragment;
import com.xwsd.app.base.BaseUpDownListFragment;
import com.xwsd.app.bean.CrtrsBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.EmptyLayout;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Created by Gx on 2016/8/19.
 * 债权转让
 */
public class CreditorTransferFragment extends BaseUpDownListFragment {

    DecimalFormat decimalFormat = new DecimalFormat("0.0");

    @Override
    protected View setContentView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_refresh_lists, null);
    }

    /**
     * 上拉加载
     */
    @Override
    public void pullUpCallBack() {
        ApiHttpClient.crtrs(currentPages, each_page_num, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                setPullUpState(BasePullUpListFragment.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("全部标列表：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        setPullUpState(BasePullUpListFragment.SUCCEED);
                        CrtrsBean oddsBean = GsonUtils.jsonToBean(response, CrtrsBean.class);
                        setData(oddsBean, TYPE_PULLUP);
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
        ApiHttpClient.crtrs(currentPages, each_page_num, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showToastShort(R.string.refurbish_failure);
                swipe_refresh_layout.setRefreshing(false);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("全部标列表：" + response);
                swipe_refresh_layout.setRefreshing(false);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        CrtrsBean oddsBean = GsonUtils.jsonToBean(response, CrtrsBean.class);
                        currentPages = oddsBean.data.page;
                        allItemCount = oddsBean.data.count;
                        mAdapter.replaceAll(oddsBean.data.records);
                    } else {
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
        ApiHttpClient.crtrs(currentPages, each_page_num, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("全部标列表：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        CrtrsBean oddsBean = GsonUtils.jsonToBean(response, CrtrsBean.class);
                        setData(oddsBean, TYPE_FIRST);
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
    private void setData(CrtrsBean bean, int type) {
        currentPages = bean.data.page;
        allItemCount = bean.data.count;

        if (bean.data.records == null || bean.data.records.size() <= 0) {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
        }

        if (mAdapter == null) {
            mAdapter = new QuickAdapter<CrtrsBean.Data.Records>(getActivity(), R.layout.item_creditor, bean.data.records) {
                @Override
                protected void convert(BaseAdapterHelper helper, final CrtrsBean.Data.Records item) {
                    helper.setText(R.id.tv_title, item.title);
                    helper.setText(R.id.tv_oddYearRate, decimalFormat.format(item.oddYearRate * 100));
                    helper.setText(R.id.tv_remainDay, item.remainDay);
                    helper.setText(R.id.tv_money, String.valueOf((int) item.money));
//                    helper.setText(R.id.tv_remainInterest, decimalFormat.format(item.remainInterest));
                    helper.setText(R.id.tv_remainInterest, String.valueOf((int) item.moneyLast));
                    helper.setProgress(R.id.pb_schedule, item.schedule,"");
                    helper.setText(R.id.proess_text,item.schedule + "%");
//                    设置转让状态
                    if (item.progress.equals("start")) {//转让中
                        helper.getView(R.id.iv_progress).setVisibility(View.GONE);
                    } else {//已转让
                        helper.getView(R.id.iv_progress).setVisibility(View.VISIBLE);
                    }
                    helper.setOnClickListener(R.id.prot_bid, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(getActivity(), BidDetailsActivity.class);
                            intent.putExtra("oddNumber", item.oddNumber);
                            startActivity(intent);

                            //埋点开始
                            BuriedPointUtil.buriedPoint("项目债权转让原标");
                        }
                    });
                }
            };

            list_view.setAdapter(mAdapter);
            list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position < mAdapter.getCount()) {
                        Intent intent = new Intent(getActivity(), TransferDetailsActivity.class);
                        intent.putExtra(UserParam.DATA,
                                ((CrtrsBean.Data.Records) mAdapter.getItem(position)).id);
                        startActivity(intent);
                    }

                    //埋点开始
                    BuriedPointUtil.buriedPoint("项目债权转让详情");
                }
            });
        } else {
            if (type == 0) {
                mAdapter.replaceAll(bean.data.records);
            } else {
                mAdapter.addAll(bean.data.records);
            }
        }
    }
}

