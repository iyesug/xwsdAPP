package com.xwsd.app.fragment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.activity.ProtDetilaActivity;
import com.xwsd.app.activity.ProtJiaActivity;
import com.xwsd.app.activity.UserActivity;
import com.xwsd.app.adapter.BaseAdapterHelper;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BasePullUpListFragment;
import com.xwsd.app.base.BaseUpDownListFragment;
import com.xwsd.app.bean.DueReturnedProjectBean;
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

/**
 * Created by Gx on 2016/8/29.
 * 待收/回款项目
 */
public class DueReturnedProjectFragment extends BaseUpDownListFragment {

  //  DecimalFormat decimalFormat = new DecimalFormat("0.0");

    String type;

    @Override
    protected View setContentView(LayoutInflater inflater) {

        type = getArguments().getString(UserParam.DATA);

        return inflater.inflate(R.layout.fragment_refresh_lists, null);
    }

    /**
     * 上拉加载
     */
    @Override
    public void pullUpCallBack() {
        ApiHttpClient.usertenders(AppContext.getUserBean().data.userId, currentPages, each_page_num, type,
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        setPullUpState(BasePullUpListFragment.NETWORK_ERROR);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        TLog.error("待收/回款项目：" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("status") == 1) {
                                setPullUpState(BasePullUpListFragment.SUCCEED);
                                DueReturnedProjectBean dueReturnedProjectBean = GsonUtils.jsonToBean(response, DueReturnedProjectBean.class);
                                setData(dueReturnedProjectBean, TYPE_PULLUP);
                            } else if (jsonObject.getInt("status") == 88){
                                ToastUtil.showToast(jsonObject.getString("msg"));
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
        ApiHttpClient.usertenders(AppContext.getUserBean().data.userId, currentPages, each_page_num, type,
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.showToastShort(R.string.refurbish_failure);
                        swipe_refresh_layout.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        TLog.error("待收/回款项目：" + response);
                        swipe_refresh_layout.setRefreshing(false);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("status") == 1) {
                                DueReturnedProjectBean dueReturnedProjectBean = GsonUtils.jsonToBean(response, DueReturnedProjectBean.class);
                                currentPages = dueReturnedProjectBean.data.page;
                                allItemCount = dueReturnedProjectBean.data.count;
                                mAdapter.replaceAll(dueReturnedProjectBean.data.records);
                            }else if (jsonObject.getInt("status") == 88){
                                ToastUtil.showToast(jsonObject.getString("msg"));
                                Intent Fintent = new Intent();
                                Fintent.putExtra(UserParam.TYPE, 0);
                                Fintent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                                startActivity(Fintent);
                                getActivity().finish();
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
        //判断是否登陆
        if (AppContext.getUserBean() == null||null==AppContext.getUserBean().data) {
            Intent intent = new Intent(getActivity(), UserActivity.class);
            intent.putExtra(UserParam.TYPE, UserActivity.TYPE_LOGIN);
            intent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
            startActivity(intent);
            return;
        }
        ApiHttpClient.usertenders(AppContext.getUserBean().data.userId, currentPages, each_page_num, type,
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        TLog.error("待收/回款项目：" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("status") == 1) {
                                mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                                DueReturnedProjectBean dueReturnedProjectBean = GsonUtils.jsonToBean(response, DueReturnedProjectBean.class);
                                setData(dueReturnedProjectBean, TYPE_FIRST);
                            } else if (jsonObject.getInt("status") == 88){
                                if(isAdded()){
                                    ToastUtil.showToast(jsonObject.getString("msg"));
                                    Intent Fintent = new Intent(AppContext.context(), UserActivity.class);
                                    Fintent.putExtra(UserParam.TYPE, 0);
                                    Fintent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                                    startActivity(Fintent);
                                    getActivity().finish();
                                }

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
    private void setData(DueReturnedProjectBean bean, int dataType) {
        currentPages = bean.data.page;
        allItemCount = bean.data.count;

        if (bean.data.records == null || bean.data.records.size() <= 0) {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
        }

        if (mAdapter == null) {
            mAdapter = new QuickAdapter<DueReturnedProjectBean.Data.Records>(getActivity(), R.layout.item_returned_project, bean.data.records) {
                @Override
                protected void convert(BaseAdapterHelper helper, final DueReturnedProjectBean.Data.Records item) {
                    helper.setText(R.id.tv_cast, item.time);
                    helper.setText(R.id.tv_title, item.oddTitle);
                    helper.setText(R.id.tv_money, item.oddMoney + "");
                    helper.setText(R.id.tv_period, item.oddPeriod.split("个")[0]);
//                    helper.setText(R.id.tv_capital, decimalFormat.format(item.money));
                    helper.setText(R.id.tv_capital, String.valueOf((int) item.money));

                        helper.setOnClickListener(R.id.prot_detail,new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                if (type.equals(ApiHttpClient.TYPE_TENDERS_RUN)) {
                                    BuriedPointUtil.buriedPoint("待收项目明细按钮");
                                }else {
                                    BuriedPointUtil.buriedPoint("回款项目明细按钮");
                                }
                                Intent intent = new Intent(getActivity(), ProtDetilaActivity.class);
                                intent.putExtra("oddMoneyId", item.id);
                                startActivity(intent);
                            }
                        });

                    if(item.lotteryId.equals("0")){
                        helper.getView(R.id.yijiaxi).setVisibility(View.GONE);
                    }else {
                        helper.getView(R.id.yijiaxi).setVisibility(View.VISIBLE);
                    }

                    if (type.equals(ApiHttpClient.TYPE_TENDERS_RUN)) {
                        helper.setText(R.id.tv_time, "预计到期时间：" + item.endtime);
                        helper.setText(R.id.tv_interest, "到期收益：" + item.interest + "元");
                        helper.setOnClickListener(R.id.prot_jia,new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                BuriedPointUtil.buriedPoint("待收项目加息按钮");
                                Intent intent = new Intent(getActivity(), ProtJiaActivity.class);
                                intent.putExtra("oddMoneyId", item.id);
                                startActivityForResult(intent,500);
                            }
                        });

                    } else {
                        helper.getView(R.id.prot_jia).setVisibility(View.GONE);
                        helper.setText(R.id.tv_time, "收款时间：" + item.endtime);
                        helper.setText(R.id.tv_interest, "已收利息：" + item.interest + "元");

                    }
                }
            };

            list_view.setAdapter(mAdapter);
//            list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    if (position < mAdapter.getCount()) {
//                        Intent intent = new Intent(getActivity(), ProjectDetailsActivity.class);
//                        intent.putExtra("oddNumber", ((DueReturnedProjectBean.Data.Records) mAdapter.getItem(position)).oddNumber);
//                        startActivity(intent);
//                    }
//                }
//            });
        } else {
            if (dataType == 0) {
                mAdapter.replaceAll(bean.data.records);
            } else {
                mAdapter.addAll(bean.data.records);
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 500:
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                ApiHttpClient.usertenders(AppContext.getUserBean().data.userId, currentPages, each_page_num, type,
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                TLog.error("待收/回款项目：" + response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getInt("status") == 1) {
                                        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                                        DueReturnedProjectBean dueReturnedProjectBean = GsonUtils.jsonToBean(response, DueReturnedProjectBean.class);
                                        setData(dueReturnedProjectBean, TYPE_FIRST);
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
                break;
            default:
                break;
        }
    }
}
