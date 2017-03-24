package com.xwsd.app.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.tools.PatternUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.view.NavbarManage;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Gx on 2016/8/29.
 * 邮箱认证
 */
public class EmailCertificationActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Bind(R.id.et_email)
    EditText et_email;

    @Bind(R.id.et_state)
    EditText et_state;

    @Bind(R.id.commit)
    Button commit;

    RequestCall call;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_email_certification);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.email_certification));
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

//        判断是否进行了邮箱认证
        if (AppContext.getUserBean().data.emailstatus.equals(ApiHttpClient.YES)) {
            et_email.setText(AppContext.getUserBean().data.email.replace(AppContext.getUserBean().data.email.substring(0, AppContext.getUserBean().data.email.length() / 2), "******"));
            et_email.setEnabled(false);
            et_state.setText("已认证");
            commit.setVisibility(View.GONE);
        }


    }

    @OnClick({R.id.commit})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:

                if (TextUtils.isEmpty(et_email.getText().toString().trim())) {
                    AppContext.showToastShort("邮箱账号不能为空");
                    return;
                }

                if (!PatternUtils.matchesEmail(et_email.getText().toString().trim())) {
                    AppContext.showToastShort("请输入正确的邮箱格式");
                    return;
                }

                showWaitDialog(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (call != null) {
                            call.cancel();
                        }
                    }
                });

                call = ApiHttpClient.setEmail(
                        AppContext.getUserBean().data.userId,
                        et_email.getText().toString().trim(),
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                hideWaitDialog();
                                AppContext.showToastShort(getString(R.string.network_exception));
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                TLog.error("认证邮箱:" + response);
                                hideWaitDialog();
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    AppContext.showToastShort(jsonObject.getString("msg"));
                                    if (jsonObject.getInt("status") == 1) {
                                        finish();
//                                        et_email.setText(et_email.getText().toString().trim().replace(et_email.getText().toString().trim().substring(0, et_email.getText().toString().trim().length() / 2), "******"));
//                                        et_email.setEnabled(false);
//                                        et_state.setText("已认证");
//                                        commit.setVisibility(View.GONE);

//                                        if (!TextUtils.isEmpty((String) getParam(UserParam.USER_ID, ""))) {
//                                            //启动服务获取用户信息
//                                            startService(new Intent(EmailCertificationActivity.this, UserService.class).putExtra(UserParam.USER_ID, (String) getParam(UserParam.USER_ID, "")));
//                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    AppContext.showToastShort(getString(R.string.network_exception));
                                }
                            }
                        });

                break;
        }
    }
}
