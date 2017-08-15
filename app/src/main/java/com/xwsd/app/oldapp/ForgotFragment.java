package com.xwsd.app.oldapp;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import butterknife.Bind;
import butterknife.OnClick;
import com.gnwai.groupeeditview.GroupeEditView;
import com.xwsd.app.R;

import com.xwsd.app.oldapp.api.ApiHttpClient;
import com.xwsd.app.base.BaseFragment;
import com.xwsd.app.tools.PatternUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gx on 2016/8/30.
 * 找回登录密码/找回支付密码
 */
public class ForgotFragment extends BaseFragment implements View.OnClickListener {

    private UserActivity userActivity;

    public static final String TAG = "ForgotFragment";

    /**
     * 用户名
     */
    @Bind(R.id.user_name)
    GroupeEditView user_name;

    /**
     * 验证码
     */
    @Bind(R.id.user_verify)
    GroupeEditView user_verify;

    /**
     * 新密码
     */
    @Bind(R.id.user_password_1)
    GroupeEditView user_password_1;

    /**
     * 确认新密码
     */
    @Bind(R.id.user_password_2)
    GroupeEditView user_password_2;

    RequestCall call;

    String title;
    String msgType;

    @Override
    protected View setContentView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_forgot, null);
    }


    @Override
    protected void init() {
        userActivity = (UserActivity) getActivity();

//        判断当前是登录，还是支付
        if (userActivity.type == UserActivity.TYPE_FORGOT) {
            title = getString(R.string.forget_login_password);
            msgType = ApiHttpClient.MSG_TYPE_FORGET;
        } else {
            title = getString(R.string.forget_pay_password);
            msgType = ApiHttpClient.MSG_TYPE_FORGET_PAYPASS;
        }

        userActivity.navbarManage.setCentreStr(title);
        //        设置验证码按钮
        user_verify.showAuthButton();
        user_verify.setOnVerifyClickListen(new GroupeEditView.OnVerifyClickListen() {
            @Override
            public boolean onClick() {

                if (TextUtils.isEmpty(user_name.getEditTextInfo())) {
                    ToastUtil.showToastShort(getString(R.string.user_phone_null));
                    return false;
                }

                if (!PatternUtils.matchesPhone(user_name.getEditTextInfo())) {
                    ToastUtil.showToastShort(getString(R.string.user_phone_format_error));
                    return false;
                }

                ApiHttpClient.sendMessage(msgType, user_name.getEditTextInfo(), new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.showToastShort(getString(R.string.network_exception));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        TLog.error("发送验证码:" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("status") == 1) {
                                ToastUtil.showToastShort(getString(R.string.send_succeed));
                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.showToastShort(getString(R.string.network_exception));
                        }
                    }
                });

                return true;
            }
        });
    }

    @OnClick({R.id.commit})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:
//                判断输入是否为空
                if (TextUtils.isEmpty(user_name.getEditTextInfo())) {
                    ToastUtil.showToastShort(getString(R.string.user_name_null));
                    return;
                }

//                判断格式
                if (!PatternUtils.matchesPhone(user_name.getEditTextInfo())) {
                    ToastUtil.showToastShort(getString(R.string.user_phone_format_error));
                    return;
                }

                if (TextUtils.isEmpty(user_verify.getEditTextInfo())) {
                    ToastUtil.showToastShort(getString(R.string.user_verify_null));
                    return;
                }

                if (TextUtils.isEmpty(user_password_1.getEditTextInfo())) {
                    ToastUtil.showToastShort(getString(R.string.user_password_null));
                    return;
                }

                if (TextUtils.isEmpty(user_password_2.getEditTextInfo())) {
                    ToastUtil.showToastShort(getString(R.string.user_password_null));
                    return;
                }

                if (!PatternUtils.matchesNum(user_password_1.getEditTextInfo())) {
                    ToastUtil.showToastShort(getString(R.string.user_password_error));
                    return;
                }

                if (!user_password_1.getEditTextInfo().equals(user_password_2.getEditTextInfo())) {
                    ToastUtil.showToastShort(getString(R.string.user_password_inconformity));
                    return;
                }

                //                开启对话框
                userActivity.showWaitDialog(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (call != null) {
                            call.cancel();
                        }
                    }
                });

                if (userActivity.type == UserActivity.TYPE_FORGOT) {

                    call = ApiHttpClient.forgetLoginpass(user_name.getEditTextInfo(),
                            user_verify.getEditTextInfo(),
                            user_password_1.getEditTextInfo(),
                            user_password_2.getEditTextInfo(),
                            new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    userActivity.hideWaitDialog();
                                    ToastUtil.showToastShort(getString(R.string.network_exception));
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    TLog.error("找回登录密码:" + response);
                                    userActivity.hideWaitDialog();
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        ToastUtil.showToastShort(jsonObject.getString("msg"));
                                        if (jsonObject.getInt("status") == 1) {
                                            userActivity.onBackPressed();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        ToastUtil.showToastShort(getString(R.string.network_exception));
                                    }
                                }
                            });
                } else {
                    call = ApiHttpClient.forgetPaypass(user_name.getEditTextInfo(),
                            user_verify.getEditTextInfo(),
                            user_password_1.getEditTextInfo(),
                            user_password_2.getEditTextInfo(),
                            new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    userActivity.hideWaitDialog();
                                    ToastUtil.showToastShort(getString(R.string.network_exception));
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    TLog.error("找回支付密码:" + response);
                                    userActivity.hideWaitDialog();
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        ToastUtil.showToastShort(jsonObject.getString("msg"));
                                        if (jsonObject.getInt("status") == 1) {
                                            userActivity.onBackPressed();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        ToastUtil.showToastShort(getString(R.string.network_exception));
                                    }
                                }
                            });
                }
                break;
        }
    }
}
