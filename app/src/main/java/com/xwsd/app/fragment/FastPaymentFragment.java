package com.xwsd.app.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.xwsd.app.AppContext;
import com.xwsd.app.AppManager;
import com.xwsd.app.R;
import com.xwsd.app.activity.AddCardActivity;
import com.xwsd.app.activity.BankListActivity;
import com.xwsd.app.activity.UserActivity;
import com.xwsd.app.activity.WebApproveActivity;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseFragment;
import com.xwsd.app.bean.BankCardBean;
import com.xwsd.app.bean.BankListBean;
import com.xwsd.app.constant.UserParam;
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
import java.util.List;
import java.util.Map;

/**
 * Created by Gy on 2017/6/27.
 * 快捷支付
 */
public class FastPaymentFragment extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.ll_add_bank_card)
    LinearLayout ll_add_bank_card;
    @Bind(R.id.ll_bank_card)
    LinearLayout ll_bank_card;
    @Bind(R.id.ll_money)
    LinearLayout ll_money;
    @Bind(R.id.ll_info)
    LinearLayout ll_info;
    @Bind(R.id.ll_commit)
    LinearLayout ll_commit;
    @Bind(R.id.tv_bank_name)
    TextView tv_bank_name;
    @Bind(R.id.iv_bank)
    ImageView iv_bank;
    @Bind(R.id.tv_bank_num)
    TextView tv_bank_num;
    @Bind(R.id.tv_money)
    TextView tv_money;
    @Bind(R.id.tv_list)
    TextView tv_list;

    @Bind(R.id.error_layout)
    EmptyLayout mErrorLayout;

    public static boolean needRefresh = false;
    BankCardBean bankCardBean;
    DecimalFormat decimalFormat = new DecimalFormat("0.0");
    BankListBean bankList;
    RequestCall call;

    boolean getBankcard=false;
    boolean getBanklist=false;

    @Override
    protected View setContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_fast_payment, null);
        return view;
    }

    @Override
    protected void init() {
        tv_list.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tv_list.getPaint().setAntiAlias(true);//抗锯齿
        getData();

    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {


        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        if (call != null) {
            call.cancel();
        }
        call = ApiHttpClient.getBankCard(AppContext.getUserBean().data.userId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                TLog.error("银行卡列表：" + e);
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("银行卡列表：" + response);
                System.out.print(response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        BankCardBean bean = GsonUtils.jsonToBean(response, BankCardBean.class);
                        bankCardBean = bean;
                        getBankcard=true;
                        if(getBanklist){
                            setBankCard(bean);
                        }

                    } else if (jsonObject.getInt("status") == 88) {
                        ToastUtil.showToast(getString(R.string.please_relogin));
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

        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        //得到银行列表详情数据
            ApiHttpClient.cardLimit(AppContext.getUserBean().data.userId,
                    new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            TLog.error("获取资讯内容:" + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getInt("status") == 1) {
                                    mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                                    bankList = GsonUtils.jsonToBean(response, BankListBean.class);
                                   getBanklist=true;
                                    if(getBankcard){
                                        setBankCard(bankCardBean);
                                    }

                                } else {
                                    mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                                    ToastUtil.showToast(jsonObject.getString("msg"));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                            }
                        }
                    }
            );

    }

    /**
     * 设置数据
     *
     * @param bean
     */
    private void setBankCard(BankCardBean bean) {
        if (bean.data != null && Integer.parseInt(bean.data.id) != 0) {

            Glide.with(getActivity())
                    .load(bean.data.bankIco)
                    .crossFade()
                    .into(iv_bank);

            if (!"".equals(bean.data.binInfo)) {
                String bankinfo = bean.data.binInfo;
                String[] s = bankinfo.split("-");
                tv_bank_name.setText(s[0] + "");

            }
            if ((bean.data.bankNum.length() >= 0)) {
                tv_bank_num.setText("（尾号 " + bean.data.bankNum.substring(bean.data.bankNum.length() - 4, bean.data.bankNum.length()) + ")");
            }
//            else {
//                ll_bank_card_bg.setVisibility(View.GONE);
//            }


            ll_bank_card.setVisibility(View.VISIBLE);
            ll_add_bank_card.setVisibility(View.GONE);

            List<BankListBean.Data.limitList> limitList=bankList.data.limitList;
            for(int i=0;i<limitList.size();i++){
                if("不支持快捷充值".equals(limitList.get(i).limit)){
                    if (!"".equals(bean.data.binInfo)) {
                        String bankinfo = bean.data.binInfo;
                        String[] s = bankinfo.split("-");
                        TLog.error("银行列表：" + limitList.get(i).bankName+"；银行卡：" +s[0]);
                        if(limitList.get(i).bankName.equals(s[0])){
                            ll_money.setVisibility(View.GONE);
                            ll_commit.setVisibility(View.GONE);
                            ll_info.setVisibility(View.VISIBLE);

                        }

                    }
                }
            }

        } else {
            ll_bank_card.setVisibility(View.GONE);
            ll_add_bank_card.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.ll_add_bank_card, R.id.tv_list, R.id.commit})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_add_bank_card://增加银行卡

                Intent addintent = new Intent(this.getActivity(), AddCardActivity.class);
                startActivity(addintent);
                break;
            case R.id.tv_list://银行列表
                Intent listintent = new Intent(this.getActivity(), BankListActivity.class);
                listintent.putExtra(UserParam.TITLE,"payment");
                startActivity(listintent);

                break;
            case R.id.commit://提交
                String money = tv_money.getText().toString().trim();
                if ("".equals(money)) {
                    ToastUtil.showToastShort(getString(R.string.pay_money_null));

                } else {
                    //跳转到充值页面
                    Intent intent = new Intent(getActivity(), WebApproveActivity.class);
                    Map<String, String> map = ApiHttpClient.getSortMap();
                    map.put("userId", AppContext.getUserBean().data.userId);
                    map.put("money", money);
                    map.put("media", "Android");
                    intent.putExtra(UserParam.URL, ApiHttpClient.RECHARGE +
                            "?userId=" + AppContext.getUserBean().data.userId +
                            "&money=" + money +
                            "&media=" + "Android" +
                            "&sign=" + ApiHttpClient.sign(map));
                    TLog.error("url:" + ApiHttpClient.RECHARGE +
                            "?userId=" + AppContext.getUserBean().data.userId +
                            "&money=" + money +
                            "&media=" + "Android" +
                            "&sign=" + ApiHttpClient.sign(map));
                    intent.putExtra(UserParam.TITLE, getString(R.string.Quick_top_up));
                    startActivity(intent);
                    AppManager.getAppManager().finishActivity();
                }
                break;

        }

    }
}