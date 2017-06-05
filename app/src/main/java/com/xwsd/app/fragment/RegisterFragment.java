package com.xwsd.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import butterknife.Bind;
import butterknife.OnClick;
import com.gnwai.groupeeditview.GroupeEditView;
import com.xwsd.app.R;
import com.xwsd.app.activity.UserActivity;
import com.xwsd.app.activity.WebDetailsActivity;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.api.XWSDRequestAdresse;
import com.xwsd.app.base.BaseFragment;
import com.xwsd.app.constant.UserParam;
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
 * 注册
 */
public class RegisterFragment extends BaseFragment implements View.OnClickListener {

    private UserActivity userActivity;

    public static final String TAG = "RegisterFragment";

    RequestCall call;

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
     * 推荐人
     */
    @Bind(R.id.user_referrer)
    GroupeEditView user_referrer;

    /**
     * 用户协议
     */
    @Bind(R.id.cb_user_agreement)
    CheckBox cb_user_agreement;

    @Override
    protected View setContentView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_register, null);
    }

    @Override
    protected void init() {
        userActivity = (UserActivity) getActivity();
        userActivity.navbarManage.setCentreStr(getString(R.string.register));
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

                ApiHttpClient.sendMessage(ApiHttpClient.MSG_TYPE_REGISTER, user_name.getEditTextInfo(), new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if(isAdded()){
                            ToastUtil.showToastShort(getString(R.string.network_exception));
                        }
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


    @OnClick({R.id.commit, R.id.tv_user_agreement})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:

                //判断输入是否为空
                if (TextUtils.isEmpty(user_name.getEditTextInfo())) {
                    ToastUtil.showToastShort(getString(R.string.user_phone_null));
                    return;
                }

                if (TextUtils.isEmpty(user_verify.getEditTextInfo())) {
                    ToastUtil.showToastShort(getString(R.string.user_verify_null));
                    return;
                }

                //判断格式
                if (!PatternUtils.matchesPhone(user_name.getEditTextInfo())) {
                    ToastUtil.showToastShort(getString(R.string.user_phone_format_error));
                    return;
                }
                //判断用户协议是否勾选
                if(!cb_user_agreement.isChecked()){
                    ToastUtil.showToastShort(getString(R.string.user_user_agreement_no));
                    return;
                }

//                将手机号码,验证码，推荐人传给下一个Fragment
                RegisterAffirmFragment registerAffirmFragment = new RegisterAffirmFragment();
                Bundle bundle = new Bundle();
                bundle.putStringArray("UserInfo", new String[]{user_name.getEditTextInfo(), user_verify.getEditTextInfo(), user_referrer.getEditTextInfo()});
                registerAffirmFragment.setArguments(bundle);
//                加载下一个页面
                userActivity.fragmentManager.beginTransaction()
                        .replace(R.id.frame_content, registerAffirmFragment)
                        .addToBackStack(RegisterAffirmFragment.TAG)
                        .commit();

                break;
            case R.id.tv_user_agreement:
                Intent intent = new Intent(getActivity(), WebDetailsActivity.class);
                intent.putExtra(UserParam.TITLE, "用户协议");
                intent.putExtra(UserParam.URL, XWSDRequestAdresse.AGREEMENT);
                intent.putExtra(UserParam.TYPE, WebDetailsActivity.TYPE_NETWORK);
                startActivity(intent);
                break;
        }
    }
}
