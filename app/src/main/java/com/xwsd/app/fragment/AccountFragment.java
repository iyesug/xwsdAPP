package com.xwsd.app.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.gnwai.iosdialog.AlertDialog;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.activity.AboutXWActivity;
import com.xwsd.app.activity.AccountSafetyActivity;
import com.xwsd.app.activity.AutoBidActivity;
import com.xwsd.app.activity.BankCardActivity;
import com.xwsd.app.activity.BorrowingActivity;
import com.xwsd.app.activity.CalendarActivity;
import com.xwsd.app.activity.CertificationActivity;
import com.xwsd.app.activity.CreditorTransferActivity;
import com.xwsd.app.activity.DredgeTrusteeshipActivity;
import com.xwsd.app.activity.FundRecordActivity;
import com.xwsd.app.activity.InvestManageActivity;
import com.xwsd.app.activity.MainActivity;
import com.xwsd.app.activity.ModifyPayPasswordActivity;
import com.xwsd.app.activity.NoviceActivity;
import com.xwsd.app.activity.PropertyDetailsActivity;
import com.xwsd.app.activity.RechargeActivity;
import com.xwsd.app.activity.RechargeWithdrawActivity;
import com.xwsd.app.activity.RecommendFriendActivity;
import com.xwsd.app.activity.SettingsPayBankActivity;
import com.xwsd.app.activity.UserActivity;
import com.xwsd.app.activity.UserInfoActiviy;
import com.xwsd.app.activity.VIPActivity;
import com.xwsd.app.activity.WithdrawActivity;
import com.xwsd.app.adapter.BaseAdapterHelper;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseFragment;
import com.xwsd.app.bean.AccountBean;
import com.xwsd.app.bean.AccountItemBean;
import com.xwsd.app.bean.AgreeCardBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.view.EmptyLayout;
import com.xwsd.app.view.NavbarManage;
import com.xwsd.app.view.RiseNumberTextView;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Gx on 2016/8/18.
 * 账户
 */
public class AccountFragment extends BaseFragment implements View.OnClickListener {

    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    /**
     * 网格列表
     */
    @Bind(R.id.grid_view)
    GridView grid_view;

    /**
     * 利息
     */
    @Bind(R.id.rise_number_text_view)
    RiseNumberTextView rise_number_text_view;

    /**
     * 待收总额
     */
    @Bind(R.id.tv_due_in)
    TextView tv_due_in;

    /**
     * 投资总额
     */
    @Bind(R.id.tv_invest)
    TextView tv_invest;

    /**
     * 回款总额
     */
    @Bind(R.id.tv_returned_money_all)
    TextView tv_returned_money_all;

    /**
     * 回款数目
     */
    @Bind(R.id.tv_returned_money)
    TextView tv_returned_money;

    /**
     * 当前日期
     */
    @Bind(R.id.tv_day)
    TextView tv_day;

    @Bind(R.id.error_layout)
    protected EmptyLayout mErrorLayout;

    RequestCall call;

    private List<AccountItemBean> accountItems;

    AccountBean accountBean;

    private AgreeCardBean agreeCardBeanBaofu;
    private AgreeCardBean agreeCardBeanFuyou;

    private boolean isCard = false;

    @Override
    protected View setContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_account, null);
        navbarManage = new NavbarManage(getActivity(), view);
        return view;
    }
    @Override
    protected void init() {
        accountItems = new ArrayList<AccountItemBean>() {{
            add(new AccountItemBean(getString(R.string.invest_manage), R.mipmap.ic_invest_manage, InvestManageActivity.class));
            add(new AccountItemBean(getString(R.string.unite_deposit), R.mipmap.ic_unite_deposit, DredgeTrusteeshipActivity.class));
            add(new AccountItemBean(getString(R.string.recharge_withdraw), R.mipmap.ic_recharge_withdraw, RechargeWithdrawActivity.class));
            add(new AccountItemBean(getString(R.string.bank_card), R.mipmap.ic_bank_card, BankCardActivity.class));
            add(new AccountItemBean(getString(R.string.creditor_transfer), R.mipmap.ic_creditor_transfer, CreditorTransferActivity.class));
            add(new AccountItemBean(getString(R.string.security_account), R.mipmap.ic_security_account, AccountSafetyActivity.class));
            add(new AccountItemBean(getString(R.string.borrowing), R.mipmap.ic_borrowing, BorrowingActivity.class));
            add(new AccountItemBean(getString(R.string.aboutus), R.mipmap.ic_aboutus, AboutXWActivity.class));
            add(new AccountItemBean(getString(R.string.vip), R.mipmap.ic_vip, VIPActivity.class));
            add(new AccountItemBean(getString(R.string.guide), R.mipmap.ic_guide, NoviceActivity.class));
            add(new AccountItemBean(getString(R.string.recommend_award), R.mipmap.ic_recommend_award, RecommendFriendActivity.class));
            add(new AccountItemBean(getString(R.string.Fund_record), R.mipmap.zijin_icon, FundRecordActivity.class));
        }};

//        设置导航栏
        navbarManage.setCentreStr(getString(R.string.account_info));
        navbarManage.showLeft(true);
        navbarManage.showRight(true);
        navbarManage.setTextColor(R.color.white);
        navbarManage.setRightStr(getString(R.string.details));
        navbarManage.setLeftImg(R.mipmap.ic_user_info);
        navbarManage.setRightImg(R.mipmap.ic_in_w);
        navbarManage.setOnLeftClickListener(new NavbarManage.OnLeftClickListener() {
            @Override
            public void onLeftClick() {
                Intent intent = new Intent(getActivity(), UserInfoActiviy.class);
                BuriedPointUtil.buriedPoint("账户个人资料");
                startActivity(intent);
            }
        });

        navbarManage.setOnRightClickListener(new NavbarManage.OnRightClickListener() {
            @Override
            public void onRightClick() {
                Intent intent = new Intent(getActivity(), PropertyDetailsActivity.class);
                startActivity(intent);
            }
        });

        //设置日历时间
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        String currentDate = sdf.format(date); // 当期日期
        tv_day.setText(currentDate.split("-")[2]);

//        设置菜单
        grid_view.setAdapter(new QuickAdapter<AccountItemBean>(getActivity(), R.layout.item_account, accountItems) {
            @Override
            protected void convert(BaseAdapterHelper helper, AccountItemBean item) {
                helper.setText(R.id.item_account_text, item.title);
                helper.setImageResource(R.id.item_account_iv, item.icoId);
            }
        });
        grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        BuriedPointUtil.buriedPoint("账户投资管理");
                        break;
                    case 1:
                        BuriedPointUtil.buriedPoint("账户资金托管");
                        break;
                    case 2:
                        BuriedPointUtil.buriedPoint("账户充值提现记录");
                        break;
                    case 3:
                        BuriedPointUtil.buriedPoint("账户银行卡");
                        break;
                    case 4:
                        BuriedPointUtil.buriedPoint("账户债权转让");
                        break;
                    case 5:
                        BuriedPointUtil.buriedPoint("账户账号安全");
                        break;
                    case 6:
                        BuriedPointUtil.buriedPoint("账户借款");
                        break;
                    case 7:
                        BuriedPointUtil.buriedPoint("账户关于小微");
                        break;
                    case 8:
                        BuriedPointUtil.buriedPoint("账户VIP");
                        break;
                    case 9:
                        BuriedPointUtil.buriedPoint("账户新手指南");
                        break;
                    case 10:
                        BuriedPointUtil.buriedPoint("账户推荐奖励");
                        break;
                    case 11:
                        BuriedPointUtil.buriedPoint("账户资金记录");
                        break;
                }

                //更多
                if (accountItems.get(position).activity == null) {
                    AppContext.showToastShort(R.string.expect);
                    return;
                }

                //进入联合存款前先判断是否进行了实名认证
                if (accountItems.get(position).activity.equals(DredgeTrusteeshipActivity.class) &&
                        !AppContext.getUserBean().data.cardstatus.equals(ApiHttpClient.YES)) {
                    guideCertification();
                    return;
                }

                //进入借款前先判断是否进行了实名认证
                if (accountItems.get(position).activity.equals(BorrowingActivity.class) &&
                        !AppContext.getUserBean().data.cardstatus.equals(ApiHttpClient.YES)) {
                    guideCertification();
                    return;
                }

                //进入银行卡前，先判断用户是否已实名认证
                if (accountItems.get(position).activity.equals(BankCardActivity.class) &&
                        !AppContext.getUserBean().data.cardstatus.equals(ApiHttpClient.YES)) {
                    guideCertification();
                    return;
                }

                //进入银行卡前，先判断用户是否设置了支付密码
                if (accountItems.get(position).activity.equals(BankCardActivity.class) &&
                        AppContext.getUserBean().data.payPassStatus.equals(ApiHttpClient.NO)) {
                    guidePay();
                    return;
                }

                Intent intent = new Intent(getActivity(), accountItems.get(position).activity);
                startActivity(intent);
            }
        });

        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
                // 宝付
                agreeCard("baofoo");
                //富有
                agreeCard("fuiou");
            }
        });

        getData();
        // 宝付
        agreeCard("baofoo");
        //富有
        agreeCard("fuiou");
    }

    /**
     * 引导支付
     */
    private void guidePay() {
        new AlertDialog(getActivity())
                .builder()
                .setTitle("温馨提示：")
                .setMsg("您尚未设置支付密码，是否前往设置。")
                .setPositiveButton("前往", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ModifyPayPasswordActivity.class);
                        intent.putExtra("type", 0);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
    }

    /**
     * 引导新手
     */
    private void guideNovice() {
        //判断该用户是否第一次进入账户页面
        if (!(boolean) ((MainActivity) getActivity()).getParam(AppContext.getUserBean().data.userId, false)) {
            //进入新手指南
            Intent intent = new Intent(getActivity(), NoviceActivity.class);
            startActivity(intent);
            ((MainActivity) getActivity()).setParam(AppContext.getUserBean().data.userId, true);
        }
    }

    /**
     * 引导实名认证
     */
    private void guideCertification() {
        new AlertDialog(getActivity())
                .builder()
                .setTitle("温馨提示：")
                .setMsg("您尚未进行实名认证，是否前往认证。")
                .setPositiveButton("前往", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), CertificationActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
    }

    /**
     * 引导开通托管
     */
    private void guideTrusteeship() {
        new AlertDialog(getActivity())
                .builder()
                .setTitle("温馨提示：")
                .setMsg("您尚未开通托管，是否前往开通。")
                .setPositiveButton("前往", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), DredgeTrusteeshipActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
    }

    /**
     * 得到数据
     */
    public void getData() {

        guideNovice();

        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);

        if (call != null) {
            call.cancel();
        }
        call = ApiHttpClient.account(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("账户信息:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        accountBean = GsonUtils.jsonToBean(response, AccountBean.class);
                        setData();
                    } else if (jsonObject.getInt("status") == 88){
                        AppContext.showToast("用户密码已修改，请重新登录");
                        Intent Fintent = new Intent(getActivity(), UserActivity.class);
                        Fintent.putExtra(UserParam.TYPE, 0);
                        Fintent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                        startActivity(Fintent);
                        getActivity().finish();
                    }
                    else {
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
     * 设置数据
     */
    private void setData() {
//        设置账户余额
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        setRiseNumber();
        tv_due_in.setText(decimalFormat.format(accountBean.data.stayAllMoney));
        tv_invest.setText(decimalFormat.format(accountBean.data.fundMoney));
        tv_returned_money_all.setText(decimalFormat.format(accountBean.data.monthStayMoney));
        tv_returned_money.setText(accountBean.data.monthStayCount);
    }

    /**
     * 设置自动增加的数字
     */
    public void setRiseNumber() {
        rise_number_text_view.withNumber(accountBean.data.stayInterest);
        rise_number_text_view.setDuration(1500);
        rise_number_text_view.start();
    }

    @OnClick({R.id.ll_auto_bid, R.id.iv_calendar, R.id.bt_recharge, R.id.bt_withdraw, R.id.ll_property_details})
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ll_auto_bid:
                //进入自动投标前先判断是否进行了实名认证
                if (!AppContext.getUserBean().data.cardstatus.equals(ApiHttpClient.YES)) {
                    guideCertification();
                    return;
                }
                //进入自动投标前判断是否开通托管
                if (AppContext.getUserBean().data.thirdAccountStatus == 0) {
                    guideTrusteeship();
                    return;
                }
                intent = new Intent(getActivity(), AutoBidActivity.class);
                startActivity(intent);
                break;

            case R.id.iv_calendar:
                intent = new Intent(getActivity(), CalendarActivity.class);
                startActivity(intent);
                break;

            case R.id.bt_recharge://充值
//                充值前，先判断是否进行了实名认证
                if (!AppContext.getUserBean().data.cardstatus.equals(ApiHttpClient.YES)) {
                    guideCertification();
                    return;
                }

//                充值前，先判断是否开通了托管
                if (AppContext.getUserBean().data.thirdAccountStatus == 0) {
                    guideTrusteeship();
                    return;
                }

//                充值前，先判断是否认证了充值银行卡
                ((MainActivity) getActivity()).showWaitDialog(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (call != null) {
                            call.cancel();
                        }
                    }
                });
                ((MainActivity) getActivity()).hideWaitDialog();
                if(isCard){
                    intent = new Intent(getActivity(), RechargeActivity.class);
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable(AgreeCardBean.class.getName(), agreeCardBean);
                        intent.putExtra(UserParam.DATA, agreeCardBeanBaofu.data.agreeCard);
                        intent.putExtra(UserParam.DATA2, agreeCardBeanFuyou.data.agreeCard);
                    startActivityForResult(intent,1234);
          //          startActivity(intent);
                }else {
                    new AlertDialog(getActivity())
                            .builder()
                            .setTitle("温馨提示：")
                            .setMsg("您尚未设置充值银行卡，是否前往设置。")
                            .setPositiveButton("前往", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), SettingsPayBankActivity.class);
                                    startActivityForResult(intent,1234);
                                }
                            })
                            .setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).show();
                }
                break;
            //账号信息
            case R.id.ll_property_details:
                intent = new Intent(getActivity(), PropertyDetailsActivity.class);
                startActivity(intent);
                break;

            case R.id.bt_withdraw:
                //进入提现前，先判断用户是否已实名认证
                if (!AppContext.getUserBean().data.cardstatus.equals(ApiHttpClient.YES)) {
                    guideCertification();
                    return;
                }

                //进入提现前，先判断用户是否设置了支付密码
                if (AppContext.getUserBean().data.payPassStatus.equals(ApiHttpClient.NO)) {
                    guidePay();
                    return;
                }
                intent = new Intent(getActivity(), WithdrawActivity.class);
                intent.putExtra(UserParam.MONEY,accountBean.data.fundMoney);
                startActivityForResult(intent,1234);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1234) {
            getData();
        }
    }
    public void agreeCard(final String flag){
        call = ApiHttpClient.agreeCard(AppContext.getUserBean().data.userId,flag, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ((MainActivity) getActivity()).hideWaitDialog();
                AppContext.showToastShort(R.string.network_exception);
            }
            @Override
            public void onResponse(String response, int id) {
                TLog.error("获取认证银行：" + response);
                ((MainActivity) getActivity()).hideWaitDialog();
//                        这边捕获下异常，data.agreeCard为fals
                try {
//                            将充值银行卡的信息传给充值页面
                    if(flag.equals("baofoo")){
                        agreeCardBeanBaofu = GsonUtils.jsonToBean(response, AgreeCardBean.class);
                        isCard = true;
                    }else{
                        agreeCardBeanFuyou = GsonUtils.jsonToBean(response, AgreeCardBean.class);
                        isCard = true;
                    }
                } catch (Exception e) {
                    isCard = false;

                }
            }
        });
    }
}
