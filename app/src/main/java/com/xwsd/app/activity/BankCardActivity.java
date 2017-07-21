package com.xwsd.app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.gnwai.iosdialog.AlertDialog;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.BankCardBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.EmptyLayout;
import com.xwsd.app.view.MADialog;
import com.xwsd.app.view.NavbarManage;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import static com.xwsd.app.R.id.error_layout;

/**
 * Created by Gx on 2016/8/29.
 * 银行卡
 */
public class BankCardActivity extends BaseActivity implements View.OnClickListener {



    @Bind(R.id.ll_bank_card_bg)
    FrameLayout ll_bank_card_bg;
    @Bind(R.id.ll_add_bank_card_bg)
    LinearLayout ll_add_bank_card_bg;

    @Bind(R.id.ll_sync)
    LinearLayout ll_sync;

    @Bind(R.id.ll_unbind)
    LinearLayout ll_unbind;

    RequestCall call;

    QuickAdapter adapter;

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.tv_type)
    TextView tv_type;

    @Bind(R.id.tv_num)
    TextView num;

    @Bind(R.id.iv_logo)
    ImageView logo;

    BankCardBean bankCardBean;
    public static boolean needRefresh = false;

    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Bind(error_layout)
    EmptyLayout mErrorLayout;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_bank_card);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        title=getString(R.string.bank_card_manage);
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.bank_card_manage));
        navbarManage.showLeft(true);
        navbarManage.showRight(false);
        navbarManage.setBackground(R.color.navbar_bg);
        navbarManage.setLeftImg(R.mipmap.ic_back_b);
        navbarManage.setOnLeftClickListener(new NavbarManage.OnLeftClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }
        });

        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                getData();
            }
        });



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
                        bankCardBean=bean;
                        setBankCard(bean);
                    } else if (jsonObject.getInt("status") == 88){
                        ToastUtil.showToast(getString(R.string.please_relogin));
                        Intent Fintent = new Intent(BankCardActivity.this, UserActivity.class);
                        Fintent.putExtra(UserParam.TYPE, 0);
                        Fintent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                        startActivity(Fintent);
                        finish();
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

//    /**
//     * 初始化下拉刷新
//     */
//    private void initRefresh() {
//        swipe_refresh_layout.setOnRefreshListener(this);
//        swipe_refresh_layout.setColorSchemeResources(
//                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
//                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
//    }


    /**
     * 设置数据
     *
     * @param bean
     */
    private void setBankCard(BankCardBean bean) {
        if (bean.data!=null&&Integer.parseInt(bean.data.id)!=0) {

            if(!"".equals(bean.data.binInfo)){
                String bankinfo=bean.data.binInfo;
                String[] s=bankinfo.split("-");
                tv_title.setText(s[0]+"");
                tv_type.setText(s[1]+"");
            }
            if((bean.data.bankNum.length()>=0)){
                num.setText(bean.data.bankNum.substring(0, 3)
                        +" **** **** " +
                        bean.data.bankNum.substring(bean.data.bankNum.length() - 4, bean.data.bankNum.length()));
            }
//            else {
//                ll_bank_card_bg.setVisibility(View.GONE);
//            }

            ApiHttpClient.lodCircleImg(logo, bean.data.bankIco, R.drawable.ic_load, R.drawable.ic_load);
            ll_add_bank_card_bg.setVisibility(View.GONE);
            ll_bank_card_bg.setVisibility(View.VISIBLE);
        }else {
            ll_bank_card_bg.setVisibility(View.GONE);
            ll_add_bank_card_bg.setVisibility(View.VISIBLE);
        }
    }


//
//    /**
//     * 设置数据
//     *
//     * @param bean
//     */
//    private void setData(BankCardsBean bean) {
//
////        if (adapter == null) {
//        if (bean.data.records.size() > 0) {
//            adapter = new QuickAdapter<BankCardsBean.records>(BankCardActivity.this, R.layout.item_bank_card, bean.data.records.subList(0, 1)) {
//                @Override
//                protected void convert(BaseAdapterHelper helper, BankCardsBean.records item) {
//
//                    helper.setText(R.id.tv_title, item.bankName);
//                    helper.setText(R.id.tv_num, "***" + item.bankNum.substring(item.bankNum.length() - 4, item.bankNum.length()));
//////                    判断是否默认
////                    if (item.isDefault.equals(ApiHttpClient.YES)) {
////                        helper.setText(R.id.tv_default, "默认");
////                    }
////
////                    if (!TextUtils.isEmpty(item.noAgree)) {
////                        helper.setText(R.id.tv_default, "支付");
////                    }
//
//                    ApiHttpClient.lodCircleImg((ImageView) helper.getView(R.id.iv_logo),
//                            item.bankIco,
//                            R.drawable.ic_load, R.drawable.ic_load);
//                }
//            };
//            list_view.setAdapter(adapter);
//
//            list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
////                    点击了绑定的充值银行卡
//                    if (((BankCardsBean.records) adapter.getItem(position)).allowDel.equals(ApiHttpClient.NO)) {
//                        new AlertDialog(BankCardActivity.this)
//                                .builder()
//                                .setTitle("温馨提示：")
//                                .setMsg("该卡已绑定，如需更换请联系客服，现在您可修改一些银行卡信息。")
//                                .setPositiveButton("修改", new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Intent intent = new Intent(BankCardActivity.this, AddCardActivity.class);
//                                        intent.putExtra(UserParam.DATA, (BankCardsBean.records) adapter.getItem(position));
//                                        startActivity(intent);
//                                    }
//                                })
//                                .setNegativeButton("取消", new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//
//                                    }
//                                }).show();
//                        return;
//                    }
////                    弹出底部菜单
//                    new ActionSheetDialog(BankCardActivity.this)
//                            .builder()
//                            .setCancelable(true)
//                            .setCanceledOnTouchOutside(true)
//                            .addSheetItem(getString(R.string.modification), ActionSheetDialog.SheetItemColor.Blue,
//                                    new ActionSheetDialog.OnSheetItemClickListener() {
//                                        @Override
//                                        public void onClick(int which) {
//                                            Intent intent = new Intent(BankCardActivity.this, AddCardActivity.class);
//                                            intent.putExtra(UserParam.DATA, (BankCardsBean.records) adapter.getItem(position));
//                                            startActivity(intent);
//                                        }
//                                    }).show();
////                            .addSheetItem(getString(R.string.delete), ActionSheetDialog.SheetItemColor.Orange,
////                                    new ActionSheetDialog.OnSheetItemClickListener() {
////                                        @Override
////                                        public void onClick(int which) {
//////                                            请求接口删除银行卡
////                                            showWaitDialog(new DialogInterface.OnCancelListener() {
////                                                @Override
////                                                public void onCancel(DialogInterface dialog) {
////                                                    if (call != null) {
////                                                        call.cancel();
////                                                    }
////                                                }
////                                            });
////                                            call = ApiHttpClient.delBankCard(AppContext.getUserBean().data.userId,
////                                                    ((BankCardsBean.Data) adapter.getItem(position)).id,
////                                                    new StringCallback() {
////                                                        @Override
////                                                        public void onError(Call call, Exception e, int id) {
////                                                            hideWaitDialog();
////                                                            ToastUtil.showToastShort(R.string.network_exception);
////                                                        }
////
////                                                        @Override
////                                                        public void onResponse(String response, int id) {
////                                                            TLog.error("删除银行卡：" + response);
////                                                            hideWaitDialog();
////                                                            try {
////                                                                JSONObject jsonObject = new JSONObject(response);
////                                                                ToastUtil.showToastShort(jsonObject.getString("msg"));
////                                                                if (jsonObject.getInt("status") == 1) {
////                                                                    adapter.remove(position);
////                                                                    ll_add_bank_card_bg.setVisibility(View.VISIBLE);
////                                                                } else {
////                                                                }
////                                                            } catch (JSONException e) {
////                                                                e.printStackTrace();
////                                                                ToastUtil.showToastShort(getString(R.string.network_exception));
////                                                            }
////                                                        }
////                                                    });
////                                        }
////                                    })
////                            .addSheetItem(getString(R.string.set_default), ActionSheetDialog.SheetItemColor.Blue,
////                                    new ActionSheetDialog.OnSheetItemClickListener() {
////                                        @Override
////                                        public void onClick(int which) {
////                                            BankCardsBean.Data data = (BankCardsBean.Data) adapter.getItem(position);
////                                            modifyDefaultCard(data.id,
////                                                    AppContext.getUserBean().data.userId,
////                                                    data.bankNum,
////                                                    String.valueOf(data.bank),
////                                                    String.valueOf(data.province),
////                                                    String.valueOf(data.city),
////                                                    data.subbranch,
////                                                    ApiHttpClient.YES);
////
////                                        }
////                                    }).show();
//                }
//            });
//
//            if (adapter.getCount() <= 0) {
//                mErrorLayout.setErrorType(EmptyLayout.NODATA);
//                ll_add_bank_card_bg.setVisibility(View.VISIBLE);
//            }
//
//            if (adapter.getCount() > 0) {
//                ll_add_bank_card_bg.setVisibility(View.GONE);
//            }
//        }
//
//    }

    /**
     * 修改为银行卡
     *
     * @param userId
     * @param bankNum
     * @param bank
     * @param province
     * @param city
     * @param subbranch
     * @param isDefault
     */
    private void modifyDefaultCard(
            String id,
            String userId,
            String bankNum,
            String bank,
            String province,
            String city,
            String subbranch,
            String isDefault) {

        showWaitDialog(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (call != null) {
                    call.cancel();
                }
            }
        });
        call = ApiHttpClient.updateBankCard(
                id,
                userId,
                bankNum,
                bank,
                province,
                city,
                subbranch,
                isDefault,
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        hideWaitDialog();
                        ToastUtil.showToastShort(getString(R.string.network_exception));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        TLog.error("修改为默认:" + response);
                        hideWaitDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            ToastUtil.showToastShort(jsonObject.getString("msg"));
                            if (jsonObject.getInt("status") == 1) {
                                getData();
                            } else if (jsonObject.getInt("status") == 88){
                                ToastUtil.showToast(getString(R.string.please_relogin));
                                Intent Fintent = new Intent();
                                Fintent.putExtra(UserParam.TYPE, 0);
                                Fintent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                                startActivity(Fintent);
                                finish();
                            }else {

                                ToastUtil.showToastShort(getString(R.string.network_exception));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideWaitDialog();
                            ToastUtil.showToastShort(getString(R.string.network_exception));
                        }
                    }
                });

    }


    @OnClick({R.id.ll_add_bank_card,R.id.ll_unbind,R.id.ll_sync})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_add_bank_card:
                //                充值前，先判断是否开通了存管
                if(AppContext.getUserBean().data.custodyId==null||"0".equals(AppContext.getUserBean().data.custodyId)||"".equals(AppContext.getUserBean().data.custodyId)){
                    new AlertDialog(this)
                            .builder()
                            .setTitle("温馨提示：")
                            .setMsg("您尚未开通存管，是否前往开通。")
                            .setPositiveButton("前往", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(BankCardActivity.this, OpenDepositoryActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).show();
                    return;
                }



//                if (AppContext.getUserBean().data.cardstatus.equals(ApiHttpClient.YES)) {
                Intent intent = new Intent(BankCardActivity.this, AddCardActivity.class);
                startActivity(intent);
//                } else {
//                    ToastUtil.showToastShort("请先实名认证");
//                }
                break;

            //解绑
            case R.id.ll_unbind:
                final MADialog mMDialog = new MADialog(this);
                mMDialog.setMessage("是否确认解绑银行卡");
                mMDialog.setBtnOK("确定", v1 -> {
                    mMDialog.miss();
                    //跳转到解绑页面
                    mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);

                    ApiHttpClient.cardUnbind(AppContext.getUserBean().data.userId,bankCardBean.data.bankNum,new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            TLog.error("解绑银行卡:" + response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getInt("status") == 1) {
                                    mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                                    ToastUtil.showToast(jsonObject.getString("msg"));
                                    getData();
                                } else if (jsonObject.getInt("status") == 88){
                                    ToastUtil.showToast(getString(R.string.please_relogin));
                                    Intent Fintent = new Intent(AppContext.context(), UserActivity.class);
                                    Fintent.putExtra(UserParam.TYPE, 0);
                                    Fintent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                                    startActivity(Fintent);
                                    finish();
                                }else {
                                    ToastUtil.showToast(jsonObject.getString("msg"));
                                    mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                            }
                        }
                    });

                });
                mMDialog.setBtnCancel("取消", v12 -> mMDialog.miss());

                break;
            //同步
            case R.id.ll_sync:
                ApiHttpClient.cardRefresh(AppContext.getUserBean().data.userId,new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        TLog.error("同步银行卡:" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("status") == 1) {
                                mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                                ToastUtil.showToast(jsonObject.getString("msg"));
                                getData();
                            } else if (jsonObject.getInt("status") == 88){
                                ToastUtil.showToast(getString(R.string.please_relogin));
                                Intent Fintent = new Intent(AppContext.context(), UserActivity.class);
                                Fintent.putExtra(UserParam.TYPE, 0);
                                Fintent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                                startActivity(Fintent);
                                finish();
                            }else {
                                ToastUtil.showToast(jsonObject.getString("msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                        }
                    }
                });

                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needRefresh) {
            getData();
            needRefresh = false;
        }
    }


//    @Override
//    public void onRefresh() {
//        ApiHttpClient.getBankCard(AppContext.getUserBean().data.userId, new StringCallback() {
//            @Override
//            public void onError(Call call, Exception e, int id) {
//                swipe_refresh_layout.setRefreshing(false);
//                ToastUtil.showToastShort(R.string.network_exception);
//            }
//
//            @Override
//            public void onResponse(String response, int id) {
//                TLog.error("银行卡列表：" + response);
//                swipe_refresh_layout.setRefreshing(false);
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    if (jsonObject.getInt("status") == 1) {
//                        BankCardBean bean = GsonUtils.jsonToBean(response, BankCardBean.class);
//                        setBankCard(bean);
//                    } else if (jsonObject.getInt("status") == 88){
//                        ToastUtil.showToast(getString(R.string.please_relogin));
//                        Intent Fintent = new Intent();
//                        Fintent.putExtra(UserParam.TYPE, 0);
//                        Fintent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
//                        startActivity(Fintent);
//                        finish();
//                    }else {
//                        ToastUtil.showToastShort(R.string.network_exception);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    ToastUtil.showToastShort(R.string.network_exception);
//                }
//            }
//        });
//    }
}
