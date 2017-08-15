package com.xwsd.app.oldapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.*;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.activity.ProtDetilaActivity;
import com.xwsd.app.activity.UserActivity;
import com.xwsd.app.adapter.BaseAdapterHelper;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.base.BasePullUpListFragment;
import com.xwsd.app.base.BaseUpDownListFragment;
import com.xwsd.app.bean.CreditorTransferBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.oldapp.api.ApiHttpClient;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.EmptyLayout;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Created by Gx on 2016/8/29.
 * 可转让债权
 */
public class CanTurnCreditorFragment extends BaseUpDownListFragment {

    String type = "can";

    CreditorTransferBean infosBean;

    DecimalFormat decimalFormat = new DecimalFormat("0.0");

    Dialog payDialog;

    RequestCall call;

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
                if(isAdded()){
                    setPullUpState(BasePullUpListFragment.NETWORK_ERROR);
                }

            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("可转让列表：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        setPullUpState(BasePullUpListFragment.SUCCEED);
                        infosBean = GsonUtils.jsonToBean(response, CreditorTransferBean.class);
                        setData(TYPE_PULLUP);
                    } else if (jsonObject.getInt("status") == 88){
                        ToastUtil.showToast("用户密码已修改，请重新登录");
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
        ApiHttpClient.usercrtrs(AppContext.getUserBean().data.userId, currentPages, each_page_num, type, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showToastShort(R.string.refurbish_failure);
                swipe_refresh_layout.setRefreshing(false);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("可转让列表：" + response);
                swipe_refresh_layout.setRefreshing(false);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        infosBean = GsonUtils.jsonToBean(response, CreditorTransferBean.class);
                        currentPages = infosBean.data.page;
                        allItemCount = infosBean.data.count;
                        mAdapter.replaceAll(infosBean.data.records);
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
        if(null!= AppContext.getUserBean()&&null!= AppContext.getUserBean().data&&null!= AppContext.getUserBean().data.userId) {
            ApiHttpClient.usercrtrs(AppContext.getUserBean().data.userId, currentPages, each_page_num, type, new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }

                @Override
                public void onResponse(String response, int id) {
                    TLog.error("可转让列表：" + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getInt("status") == 1) {
                            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                            infosBean = GsonUtils.jsonToBean(response, CreditorTransferBean.class);
                            setData(TYPE_FIRST);
                        } else if (jsonObject.getInt("status") == 88) {
                            ToastUtil.showToast("用户密码已修改，请重新登录");
                            Intent Fintent = new Intent(getActivity(), UserActivity.class);
                            Fintent.putExtra(UserParam.TYPE, 0);
                            Fintent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                            startActivity(Fintent);
                            getActivity().finish();
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

    }

    /**
     * 设置列表信息
     */
    private void setData(int type) {
        currentPages = infosBean.data.page;
        allItemCount = infosBean.data.count;

        if (infosBean.data.records == null || infosBean.data.records.size() <= 0) {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
        }


        if (mAdapter == null) {
            mAdapter = new QuickAdapter<CreditorTransferBean.Data.Records>(getActivity(), R.layout.item_returned_projects, infosBean.data.records) {
                @Override
                protected void convert(final BaseAdapterHelper helper, final CreditorTransferBean.Data.Records item) {
                    helper.setText(R.id.tv_cast, item.time);
                    helper.setText(R.id.tv_title, item.oddTitle);
                    helper.setText(R.id.tv_money, item.oddMoney+"");
                    helper.setText(R.id.tv_period, item.oddPeriod.split("个")[0]);
                    if(item.lotteryId.equals("0")){
                        helper.getView(R.id.yijiaxi).setVisibility(View.GONE);
                    }else {
                        helper.getView(R.id.yijiaxi).setVisibility(View.VISIBLE);
                    }
//                    helper.setText(R.id.tv_capital, decimalFormat.format(item.money));
                    helper.setText(R.id.tv_capital, (item.money));
                    helper.setText(R.id.tv_time, "预计到期时间：" + item.endtime);
                    helper.setText(R.id.tv_interest, "到期收益：" + item.interest +"元");

                    helper.setOnClickListener(R.id.tv_transfer_creditor, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            BuriedPointUtil.buriedPoint("账户债权转让转让债权按键");

                            //显示支付对话框

                                payDialog = new Dialog(getActivity(), R.style.BankDialog);
                                View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_pay_creditor, null);
                                final EditText pass = (EditText) view.findViewById(R.id.et_password);
                                TextView remain = (TextView) view.findViewById(R.id.tv_remain_velue);
                                TextView crtrSM = (TextView) view.findViewById(R.id.tv_crtrSM_velue);

                                remain.setText(item.remain+"");
                                crtrSM.setText(item.crtrSM+"");

                                view.findViewById(R.id.tv_forget_password).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getActivity(), UserActivity.class);
                                        intent.putExtra(UserParam.TYPE, UserActivity.TYPE_FORGET_PAYPASS);
                                        startActivity(intent);
                                    }
                                });

                                view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        payDialog.dismiss();
                                    }
                                });

                                view.findViewById(R.id.commit).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (TextUtils.isEmpty(pass.getText().toString().trim())) {
                                            ToastUtil.showToastShort(R.string.pay_pasworrd_null);
                                            return;
                                        }
                                        payDialog.dismiss();

                                        //债权转让请求
                                        ((BaseActivity) getActivity()).showWaitDialog(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialog) {
                                                if (call != null) {
                                                    call.cancel();
                                                }
                                            }
                                        });

                                        call = ApiHttpClient.transfer(
                                                AppContext.getUserBean().data.userId,
                                                item.id,
                                                pass.getText().toString().trim(),
                                                new StringCallback() {
                                                    @Override
                                                    public void onError(Call call, Exception e, int id) {
                                                        ((BaseActivity) getActivity()).hideWaitDialog();
                                                        ToastUtil.showToastShort(getString(R.string.network_exception));
                                                    }

                                                    @Override
                                                    public void onResponse(String response, int id) {
                                                        TLog.error("转让债权:" + response);
                                                        ((BaseActivity) getActivity()).hideWaitDialog();
                                                        try {
                                                            JSONObject jsonObject = new JSONObject(response);
                                                            ToastUtil.showToastShort(jsonObject.getString("msg"));
                                                            if (jsonObject.getInt("status") == 1) {
                                                                mAdapter.remove(helper.getPosition());
                                                            }else if (jsonObject.getInt("status") == 88){
                                                                ToastUtil.showToast("用户密码已修改，请重新登录");
                                                                Intent Fintent = new Intent();
                                                                Fintent.putExtra(UserParam.TYPE, 0);
                                                                Fintent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                                                                startActivity(Fintent);
                                                                getActivity().finish();
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                            ToastUtil.showToastShort(getString(R.string.network_exception));
                                                        }
                                                    }
                                                });
                                    }
                                });



                                payDialog.setContentView(view);

                                Window window = payDialog.getWindow();
                                WindowManager.LayoutParams lp = window.getAttributes();
                                lp.gravity = Gravity.CENTER;
                                lp.width = window.getWindowManager().getDefaultDisplay().getWidth() - 100;
                                lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                                payDialog.getWindow().setAttributes(lp);

                                payDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        pass.setText("");
                                    }
                                });


                            payDialog.show();
                        }
                    });
                    helper.setVisible(R.id.prot_detail,false);
                    helper.setOnClickListener(R.id.prot_detail,new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            BuriedPointUtil.buriedPoint("可转让明细按钮");
                            Intent intent = new Intent(getActivity(), ProtDetilaActivity.class);
                            intent.putExtra("oddMoneyId", item.id);
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
//                        Intent intent = new Intent(getActivity(), ProjectDetailsActivity.class);
//                        intent.putExtra("oddNumber", ((CreditorTransferBean.Data.Records) mAdapter.getItem(position)).oddNumber);
//                        startActivity(intent);
//                    }
//                }
//            });
        } else {
            if (type == 0) {
                mAdapter.replaceAll(infosBean.data.records);
            } else {
                mAdapter.addAll(infosBean.data.records);
            }
        }
    }
}