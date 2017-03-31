package com.xwsd.app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.OnClick;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.NavbarManage;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gx on 2016/8/29.
 * 修改登录密码
 */
public class ModifyLoginPasswordActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    /**
     * 旧密码
     */
    @Bind(R.id.et_oldpass)
    EditText et_oldpass;

    /**
     * 新密码
     */
    @Bind(R.id.et_password)
    EditText et_password;

    /**
     * 确认新密码
     */
    @Bind(R.id.et_passwordSure)
    EditText et_passwordSure;

    RequestCall call;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_modify_login_password);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.modify_login_password));
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
    }

    @OnClick({R.id.commit, R.id.tv_forget_password})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:
                if (TextUtils.isEmpty(et_oldpass.getText().toString().trim())) {
                    ToastUtil.showToastShort(getString(R.string.please_write_full));
                    return;
                }

                if (TextUtils.isEmpty(et_password.getText().toString().trim())) {
                    ToastUtil.showToastShort(getString(R.string.please_write_full));
                    return;
                }

                if (TextUtils.isEmpty(et_passwordSure.getText().toString().trim())) {
                    ToastUtil.showToastShort(getString(R.string.please_write_full));
                    return;
                }

                if (!et_password.getText().toString().trim().equals(et_passwordSure.getText().toString().trim())) {
                    ToastUtil.showToastShort(getString(R.string.user_password_inconformity));
                    return;
                }


                //                开启对话框
                showWaitDialog(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (call != null) {
                            call.cancel();
                        }
                    }
                });

                call = ApiHttpClient.updatePassword(
                        AppContext.getUserBean().data.userId,
                        et_oldpass.getText().toString().trim(),
                        et_password.getText().toString().trim(),
                        et_passwordSure.getText().toString().trim(),
                        ApiHttpClient.LOGINPASS,
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                hideWaitDialog();
                                ToastUtil.showToastShort(getString(R.string.network_exception));
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                TLog.error("response:" + response);
                                hideWaitDialog();
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    ToastUtil.showToastShort(jsonObject.getString("msg"));
//                                if (jsonObject.getInt("status") == 1) {
//                                } else {

//                                }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    ToastUtil.showToastShort(getString(R.string.network_exception));
                                }
                            }
                        });
                break;

            case R.id.tv_forget_password:
                Intent intent = new Intent(ModifyLoginPasswordActivity.this, UserActivity.class);
                //跳转到找回密码页面
                intent.putExtra(UserParam.TYPE, UserActivity.TYPE_FORGOT);
                startActivity(intent);
                break;
        }
    }
}
