package com.xwsd.app.oldapp.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.activity.UserActivity;
import com.xwsd.app.adapter.BaseAdapterHelper;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.base.BasePullUpListFragment;
import com.xwsd.app.base.BaseUpDownListFragment;
import com.xwsd.app.bean.MoneyTransferRecordBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.oldapp.api.ApiHttpClient;
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
 * Created by Gy on 2017/8/14.
 * 迁移记录
 */
public class TransferRecordFragment extends BaseUpDownListFragment {

    DecimalFormat decimalFormat = new DecimalFormat("0.0");



    @Override
    protected View setContentView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_refresh_list_transfer, null);
    }

    /**
     * 上拉加载
     * 1000015697
     */
    @Override
    public void pullUpCallBack() {
        ApiHttpClient.tranlogs(AppContext.getUserBean().data.userId, currentPages, each_page_num, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                setPullUpState(BasePullUpListFragment.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("迁移记录列表：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        setPullUpState(BasePullUpListFragment.SUCCEED);
                        MoneyTransferRecordBean bean = GsonUtils.jsonToBean(response, MoneyTransferRecordBean.class);
                        setData(bean,TYPE_PULLUP);
                    } else if (jsonObject.getInt("status") == 88){
                        ToastUtil.showToast("登陆信息已失效，请重新登录");
                        Intent Fintent = new Intent();
                        Fintent.putExtra(UserParam.TYPE, 0);
                        Fintent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                        startActivity(Fintent);
                        getActivity().finish();
                    }else {
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
        ApiHttpClient.tranlogs(AppContext.getUserBean().data.userId, currentPages, each_page_num, new StringCallback() {
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
                        MoneyTransferRecordBean bean = GsonUtils.jsonToBean(response, MoneyTransferRecordBean.class);
                        currentPages = bean.data.page;
                        allItemCount = bean.data.count;
                        mAdapter.replaceAll(bean.data.records);
                    } else if (jsonObject.getInt("status") == 88){
                        ToastUtil.showToast("用户密码已修改，请重新登录");
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
        ApiHttpClient.tranlogs(AppContext.getUserBean().data.userId, currentPages, each_page_num, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("充值记录：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        MoneyTransferRecordBean bean = GsonUtils.jsonToBean(response, MoneyTransferRecordBean.class);
                        setData(bean,TYPE_FIRST);
                    } else if (jsonObject.getInt("status") == 88){
                        ToastUtil.showToast("登陆信息已失效，请重新登录");
                        Intent Fintent = new Intent(AppContext.context(), UserActivity.class);
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
    private void setData(MoneyTransferRecordBean bean, int type) {
        currentPages = bean.data.page;
        allItemCount = bean.data.count;

        if (bean.data.records == null || bean.data.records.size() <= 0) {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
        }

        if (mAdapter == null) {
            mAdapter = new QuickAdapter<MoneyTransferRecordBean.Data.Records>(getActivity(), R.layout.item_recharge_withdtaw_record, bean.data.records) {
                @Override
                protected void convert(BaseAdapterHelper helper, MoneyTransferRecordBean.Data.Records item) {
                    helper.setText(R.id.tv_price, decimalFormat.format(item.money));
                    switch (item.status) {
                        case 0:
                            helper.setText(R.id.tv_result, "申请中");
                            break;
                        case 1:
                            helper.setText(R.id.tv_result, "迁移成功");
                            break;
                        case 2:
                            helper.setText(R.id.tv_result, "迁移失败");
                            break;

                    }

                    if(!"".equals(item.addTime)){
                        String[] arr = item.addTime.split(" ");
                        helper.setText(R.id.tv_tiem, arr[0] + "\n\r" + arr[1]);
                    }


                }
            };

            list_view.setAdapter(mAdapter);
            list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Intent intent = new Intent(getActivity(), ProjectDetailsActivity.class);
//                    intent.putExtra("oddNumber", ((OddsBean.Data.Records) mAdapter.getItem(position)).oddNumber);
//                    startActivity(intent);
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
