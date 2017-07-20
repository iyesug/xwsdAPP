package com.xwsd.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.UserBean;
import com.xwsd.app.constant.BroadcastParam;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.fragment.fuyouMoneyFragment;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.NavbarManage;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Gx on 2016/8/29.
 * 账户安全
 */
public class AccountSafetyActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Bind(R.id.ll_name_certification)
    LinearLayout ll_name_certification;

    @Bind(R.id.ll_mobile_certification)
    LinearLayout ll_mobile_certification;

    @Bind(R.id.ll_email_certification)
    LinearLayout ll_email_certification;

    @Bind(R.id.ll_bank_card)
    LinearLayout ll_bank_card;

    @Bind(R.id.ll_unite_deposit)
    LinearLayout ll_unite_deposit;

    @Bind(R.id.ll_modify_update_phone)
    LinearLayout ll_modify_update_phone;

    @Bind(R.id.ll_modify_login_password)
    LinearLayout ll_modify_login_password;

    @Bind(R.id.ll_modify_pay_password)
    LinearLayout ll_modify_pay_password;

    @Bind(R.id.ll_gesture_password)
    LinearLayout ll_gesture_password;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_account_safety);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        title=getString(R.string.account_safety);
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.account_safety));
        navbarManage.showLeft(true);
        navbarManage.showRight(false);
        navbarManage.setLeftImg(R.mipmap.ic_back_b);
        navbarManage.setBackground(R.color.navbar_bg);
        navbarManage.setOnLeftClickListener(new NavbarManage.OnLeftClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }
        });

        ((ImageView) ll_name_certification.findViewById(R.id.iv_tag)).setImageResource(R.mipmap.icon_red_name);
        ((ImageView) ll_mobile_certification.findViewById(R.id.iv_tag)).setImageResource(R.mipmap.icon_green_phone);
        ((ImageView) ll_email_certification.findViewById(R.id.iv_tag)).setImageResource(R.mipmap.icon_orange_email);

        ((ImageView) ll_bank_card.findViewById(R.id.iv_tag)).setImageResource(R.mipmap.icon_blue_card);
        ((ImageView) ll_unite_deposit.findViewById(R.id.iv_tag)).setImageResource(R.mipmap.icon_purple_hand);
        ((ImageView) ll_modify_update_phone.findViewById(R.id.iv_tag)).setImageResource(R.mipmap.icon_blue_phone);
        ((ImageView) ll_modify_login_password.findViewById(R.id.iv_tag)).setImageResource(R.mipmap.icon_pink_mima);
        ((ImageView) ll_modify_pay_password.findViewById(R.id.iv_tag)).setImageResource(R.mipmap.icon_blue_money);
        ((ImageView) ll_gesture_password.findViewById(R.id.iv_tag)).setImageResource(R.mipmap.icon_hrey_hand);

        ((TextView) ll_name_certification.findViewById(R.id.tv_title)).setText(getString(R.string.certification));
        ((TextView) ll_mobile_certification.findViewById(R.id.tv_title)).setText(getString(R.string.mobile_certification));
        ((TextView) ll_email_certification.findViewById(R.id.tv_title)).setText(getString(R.string.email_certification));

        ((TextView) ll_bank_card.findViewById(R.id.tv_title)).setText(getString(R.string.bank_card));
        ((TextView) ll_unite_deposit.findViewById(R.id.tv_title)).setText(getString(R.string.open_depository));

        ((TextView) ll_modify_update_phone.findViewById(R.id.tv_title)).setText(getString(R.string.ll_modify_update_phone));
        ((TextView) ll_modify_login_password.findViewById(R.id.tv_title)).setText(getString(R.string.modify_login_password));

        if (null!=AppContext.getUserBean()&&null!=AppContext.getUserBean().data&&null!=AppContext.getUserBean().data.custodyPwd&&AppContext.getUserBean().data.custodyPwd.equals(ApiHttpClient.ONE)) {
            ((TextView) ll_modify_pay_password.findViewById(R.id.tv_title)).setText(getString(R.string.modify_custody_password));
        } else {
            ((TextView) ll_modify_pay_password.findViewById(R.id.tv_title)).setText(getString(R.string.setting_custody_password));
        }
        ((TextView) ll_gesture_password.findViewById(R.id.tv_title)).setText(getString(R.string.gesture_password));

    }

    @OnClick({R.id.ll_name_certification,
            R.id.ll_mobile_certification,
            R.id.ll_email_certification,
            R.id.ll_bank_card,
            R.id.ll_unite_deposit,
            R.id.ll_modify_update_phone,
            R.id.ll_modify_login_password,
            R.id.ll_modify_pay_password,
            R.id.ll_gesture_password,
            R.id.ll_out})
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ll_name_certification:
                BuriedPointUtil.buriedPoint("账户安全实名认证");
                intent = new Intent(AccountSafetyActivity.this, CertificationActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_mobile_certification:
                BuriedPointUtil.buriedPoint("账户安全手机认证");
                intent = new Intent(AccountSafetyActivity.this, MobileCertificationActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_email_certification:
                BuriedPointUtil.buriedPoint("账户安全邮箱认证");
                intent = new Intent(AccountSafetyActivity.this, EmailCertificationActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_bank_card:
                BuriedPointUtil.buriedPoint("账户安全银行卡");
                intent = new Intent(AccountSafetyActivity.this, BankCardActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_unite_deposit:
                BuriedPointUtil.buriedPoint("账户安全绑定第三方账户");
                if(AppContext.getUserBean().data.custodyId==null||"0".equals(AppContext.getUserBean().data.custodyId)||"".equals(AppContext.getUserBean().data.custodyId)){
                    intent = new Intent(AccountSafetyActivity.this, OpenDepositoryActivity.class);
                    startActivity(intent);
                }else{
                    ToastUtil.showToastShort(getString(R.string.alredy_open_depository));

                }

                break;
            case R.id.ll_modify_update_phone:
                BuriedPointUtil.buriedPoint("账户安全修改手机号码");
                intent = new Intent(AccountSafetyActivity.this, ModifyPhoneActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_modify_login_password:
                BuriedPointUtil.buriedPoint("账户安全修改登录密码");
                intent = new Intent(AccountSafetyActivity.this, ModifyLoginPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_modify_pay_password:
                BuriedPointUtil.buriedPoint("账户安全修改存管密码");
                Intent webintent = new Intent(this, WebApproveActivity.class);
                Log.e("custodyPwd",AppContext.getUserBean().data.custodyPwd);
                if (AppContext.getUserBean().data.custodyPwd.equals(ApiHttpClient.ZERO)) {

                    //设置存管密码【存管】，跳转到设置页面

                    Map<String, String> map = ApiHttpClient.getSortMap();
                    map.put("userId", AppContext.getUserBean().data.userId);
                    map.put("media", "Android");
                    webintent.putExtra(UserParam.URL, ApiHttpClient.SET_CUSTODY_PASS +
                            "?userId=" + AppContext.getUserBean().data.userId +
                            "&media=" + "Android"+
                            "&sign=" + ApiHttpClient.sign(map));

                    webintent.putExtra(UserParam.TITLE, getString(R.string.setting_custody_password));

                } else {

                    //修改存管密码【存管】，跳转到修改页面

                    Map<String, String> map = ApiHttpClient.getSortMap();
                    map.put("userId", AppContext.getUserBean().data.userId);
                    map.put("media", "Android");
                    webintent.putExtra(UserParam.URL, ApiHttpClient.UPDATE_CUSTODY_PASS +
                            "?userId=" + AppContext.getUserBean().data.userId +
                            "&media=" + "Android"+
                            "&sign=" + ApiHttpClient.sign(map));

                    webintent.putExtra(UserParam.TITLE, getString(R.string.modify_pay_password));


                }
                startActivity(webintent);
                break;
            case R.id.ll_gesture_password:
                BuriedPointUtil.buriedPoint("账户安全设置手势密码");
                intent = new Intent(AccountSafetyActivity.this, GestureLockSettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_out:
                //发送广播通知用户退出
                BuriedPointUtil.buriedPoint("账户安全退出登录");
                intent = new Intent();
                intent.setAction(BroadcastParam.USER_CHANGE_OUT);
                sendBroadcast(intent);
                //清空保存的用户数据
                AppContext.setUserBean(null);

                fuyouMoneyFragment.dayRechargemoney = 0;//退出账户后，另外一个账户每日充值的金额从零开始

                setParam(UserParam.USER_ID, "");
//                关闭手势锁
                AppContext.setNeedLock(false);
                finish();
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();


        RequestCall  call = ApiHttpClient.getUserInfo(AppContext.getUserBean().data.userId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("获取用户信息:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        UserBean userBean = GsonUtils.jsonToBean(response, UserBean.class);
                        AppContext.setUserBean(userBean);
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        });
    }
}
