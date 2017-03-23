package com.xwsd.app.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.gnwai.groupeeditview.GroupeEditView;
import com.xwsd.app.AppContext;
import com.xwsd.app.AppManager;
import com.xwsd.app.R;
import com.xwsd.app.activity.GestureLockSettingsActivity;
import com.xwsd.app.activity.GestureLoginActivity;
import com.xwsd.app.activity.MainActivity;
import com.xwsd.app.activity.UserActivity;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseFragment;
import com.xwsd.app.bean.UserBean;
import com.xwsd.app.constant.BroadcastParam;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.GesturePassward;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.PatternUtils;
import com.xwsd.app.tools.TLog;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Gx on 2016/8/30.
 * 注册-确认
 */
public class RegisterAffirmFragment extends BaseFragment implements View.OnClickListener {

    private UserActivity userActivity;

    public static final String TAG = "RegisterAffirmFragment";

    private String phone;

    private String verify;

    private String referrer;

    /**
     * 用户名
     */
    @Bind(R.id.user_name)
    GroupeEditView user_name;

    /**
     * 密码一
     */
    @Bind(R.id.user_password_1)
    GroupeEditView user_password_1;

    /**
     * 密码二
     */
    @Bind(R.id.user_password_2)
    GroupeEditView user_password_2;

    RequestCall call;

    @Override
    protected View setContentView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_register_affirm, null);
    }

    @Override
    protected void init() {
        userActivity = (UserActivity) getActivity();
        userActivity.navbarManage.setCentreStr(getString(R.string.register));
        String[] userInfo = getArguments().getStringArray("UserInfo");
        phone = userInfo[0];
        verify = userInfo[1];
        referrer = userInfo[2];
    }

    @OnClick({R.id.commit})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:

                //判断输入是否为空
                if (TextUtils.isEmpty(user_name.getEditTextInfo())) {
                    AppContext.showToastShort(getString(R.string.user_name_null));
                    return;
                }

                if (TextUtils.isEmpty(user_password_1.getEditTextInfo())) {
                    AppContext.showToastShort(getString(R.string.user_password_null));
                    return;
                }

                if (TextUtils.isEmpty(user_password_2.getEditTextInfo())) {
                    AppContext.showToastShort(getString(R.string.user_password_null));
                    return;
                }

                if (!user_password_1.getEditTextInfo().equals(user_password_2.getEditTextInfo())) {
                    AppContext.showToastShort(getString(R.string.user_password_inconformity));
                    return;
                }

                if (!PatternUtils.matchesNum(user_password_1.getEditTextInfo())) {
                    AppContext.showToastShort(getString(R.string.user_password_error));
                    return;
                }

                if (!PatternUtils.matchesNum(user_password_2.getEditTextInfo())) {
                    AppContext.showToastShort(getString(R.string.user_password_error));
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

                call = ApiHttpClient.registered(
                        user_name.getEditTextInfo(),
                        user_password_1.getEditTextInfo(),
                        phone,
                        referrer,
                        verify,
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                userActivity.hideWaitDialog();
                                AppContext.showToastShort(getString(R.string.network_exception));
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                TLog.error("注册:" + response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    AppContext.showToastShort(jsonObject.getString("msg"));
                                    if (jsonObject.getInt("status") == 1) {

                                        login();

                                    } else {
                                        userActivity.hideWaitDialog();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    userActivity.hideWaitDialog();
                                    AppContext.showToastShort(getString(R.string.network_exception));
                                }
                            }
                        });
                break;
        }
    }

    public void login(){
        call = ApiHttpClient.login(phone, user_password_1.getEditTextInfo(), new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                userActivity.hideWaitDialog();
                AppContext.showToastShort(getString(R.string.network_exception));
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("登录:" + response);
                userActivity.hideWaitDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    AppContext.showToastShort(jsonObject.getString("msg"));
                    if (jsonObject.getInt("status") == 1) {
                        UserBean userBean = GsonUtils.jsonToBean(response, UserBean.class);
//                                设置用户信息
                        AppContext.setUserBean(userBean);
                        userActivity.setParam(UserParam.USER_ID, userBean.data.userId);

//                                判断是否是已经加载，如果未加载就跳转到首页，否则关闭当前页面
                        if (AppManager.getActivity(MainActivity.class) == null) {
                            Intent intent = new Intent(userActivity, MainActivity.class);
                            startActivity(intent);
                        }

//                                判断是否存在手势锁页面，如果有就关闭
                        AppManager.getAppManager().finishActivity(GestureLoginActivity.class);

                        //需要跳转到下一个页面
                        if (getActivity().getIntent().getSerializableExtra(UserParam.CLASS) != null) {
                            Intent nextActivity = new Intent(getActivity(), (Class<?>) getActivity().getIntent().getSerializableExtra(UserParam.CLASS));

                            nextActivity.putExtra(UserParam.TYPE, 0);
                            if (!TextUtils.isEmpty(getActivity().getIntent().getStringExtra(UserParam.DATA))) {
                                nextActivity.putExtra(UserParam.DATA, getActivity().getIntent().getStringExtra(UserParam.DATA));
                            }
                            if (!TextUtils.isEmpty(getActivity().getIntent().getStringExtra(UserParam.TITLE))) {
                                nextActivity.putExtra(UserParam.TITLE, getActivity().getIntent().getStringExtra(UserParam.TITLE));
                            }

                            startActivity(nextActivity);
                        }

//                                判断是否设置过手势锁
                        //   ACache.get(userActivity).getAsBinary(AppContext.getUserBean().data.userId) == null
                        if (getActivity().getIntent().getBooleanExtra(UserParam.SETTING_GESTURE, false) || "".equals(GesturePassward.getString((String)userActivity.getParam(UserParam.USER_ID, ""),""))) {
                            Intent intent = new Intent(userActivity, GestureLockSettingsActivity.class);
                            intent.putExtra("showBack", false);
                            startActivity(intent);
                        } else {
//                                    启用手势锁
                            AppContext.setNeedLock(true);
                        }

//                                首页点击账户页面，发送一个广播通知跳转
                        if (getActivity().getIntent().getBooleanExtra(UserParam.NEED_ENTER_ACCOUNT, false)) {
                            Intent intent = new Intent();
                            intent.setAction(BroadcastParam.NEED_ENTER_ACCOUNT);
                            userActivity.sendBroadcast(intent);
                        }

//                                发送广播通知用户登录
                        Intent intent = new Intent();
                        intent.setAction(BroadcastParam.USER_CHANGE_LOGIN);
                        userActivity.sendBroadcast(intent);
                        userActivity.finish();

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    AppContext.showToastShort(getString(R.string.network_exception));
                }
            }
        });
    }

}
