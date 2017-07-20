package com.xwsd.app.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.PatternUtils;
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
 * 修改支付密码
 */
public class ModifyPayPasswordActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Bind(R.id.ll_original_password)
    LinearLayout ll_original_password;

    @Bind(R.id.v_original_password)
    View v_original_password;

    @Bind(R.id.et_original_password)
    EditText et_original_password;

    @Bind(R.id.et_password_1)
    EditText et_password_1;

    @Bind(R.id.et_password_2)
    EditText et_password_2;

    int type;

    RequestCall call;

    Dialog dialog;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_modify_pay_password);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        title=getString(R.string.setting_pay_password);
        //设置导航栏
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

//        判断是加载设置布局还是修改布局
        type = getIntent().getIntExtra("type", 1);
        if (type == 0) {
            navbarManage.setCentreStr(getString(R.string.setting_pay_password));
            ll_original_password.setVisibility(View.GONE);
            v_original_password.setVisibility(View.GONE);
        } else {
            navbarManage.setCentreStr(getString(R.string.modify_pay_password));
        }
    }

    @OnClick({R.id.commit, R.id.tv_forget_password})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:

                if (type == 1 && TextUtils.isEmpty(et_original_password.getText().toString().trim())) {
                    ToastUtil.showToastShort(getString(R.string.user_password_null));
                    return;
                }

                if (TextUtils.isEmpty(et_password_1.getText().toString().trim())) {
                    ToastUtil.showToastShort(getString(R.string.user_password_null));
                    return;
                }

                if (TextUtils.isEmpty(et_password_2.getText().toString().trim())) {
                    ToastUtil.showToastShort(getString(R.string.user_password_null));
                    return;
                }

                if (!et_password_1.getText().toString().trim().equals(et_password_2.getText().toString().trim())) {
                    ToastUtil.showToastShort(getString(R.string.user_password_inconformity));
                    return;
                }

                if (!PatternUtils.matchesNum(et_password_1.getText().toString().trim())) {
                    ToastUtil.showToastShort(getString(R.string.user_password_error));
                    return;
                }

                if (type == 0) {
                    showLogPassword();
                } else {
                    modifyPayPassword(
                            et_original_password.getText().toString().trim(),
                            et_password_1.getText().toString().trim(),
                            et_password_2.getText().toString().trim()
                    );
                }

                break;
            case R.id.tv_forget_password:
                Intent intent = new Intent(ModifyPayPasswordActivity.this,UserActivity.class);
                //跳转到找回密码页面
                intent.putExtra(UserParam.TYPE,UserActivity.TYPE_FORGET_PAYPASS);
                startActivity(intent);
                break;
        }
    }

    /**
     * 修改支付密码
     */
    private void modifyPayPassword(String oldPassword, String password_1, String password_2) {

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
                oldPassword,
                password_1,
                password_2,
                ApiHttpClient.PAYPASS,
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        hideWaitDialog();
                        ToastUtil.showToastShort(getString(R.string.network_exception));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        TLog.error("修改支付密码:" + response);
                        hideWaitDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            ToastUtil.showToastShort(jsonObject.getString("msg"));
                            if (jsonObject.getInt("status") == 1) {

                            } else {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.showToastShort(getString(R.string.network_exception));
                        }
                    }
                });

    }

    /**
     * 显示输入登录密码的对话框
     */
    private void showLogPassword() {
        //弹出输入登录密码的对话框
        if (dialog == null) {

            dialog = new Dialog(ModifyPayPasswordActivity.this, R.style.BankDialog);
            View view = getLayoutInflater().inflate(R.layout.dialog_pay, null);
            TextView textView = (TextView) view.findViewById(R.id.tv_title);
            final EditText editText = (EditText) view.findViewById(R.id.et_password);
            textView.setText("登录密码");
            editText.setHint("输入登录密码");
            //找回登录密码
            view.findViewById(R.id.tv_forget_password).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(ModifyPayPasswordActivity.this, UserActivity.class);
                    //跳转到找回密码页面
                    intent.putExtra(UserParam.TYPE, UserActivity.TYPE_FORGET_PAYPASS);
                    startActivity(intent);

                }
            });
            //取消
            view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            //确定
            view.findViewById(R.id.commit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    settingPayPassword(
                            editText.getText().toString(),
                            et_password_1.getText().toString().trim(),
                            et_password_2.getText().toString().trim());
                }
            });
            dialog.setContentView(view);
            Window window = dialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.CENTER;
            lp.width = window.getWindowManager().getDefaultDisplay().getWidth() - 100;
            lp.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(lp);

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    editText.setText("");
                }
            });
        }

        dialog.show();
    }

    /**
     * 设置支付密码
     *
     * @param loginPassword
     * @param password_1
     * @param password_2
     */
    private void settingPayPassword(String loginPassword, String password_1, String password_2) {
        showWaitDialog(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (call != null) {
                    call.cancel();
                }
            }
        });

        call = ApiHttpClient.setPaypass(
                AppContext.getUserBean().data.userId,
                loginPassword,
                password_1,
                password_2,
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        hideWaitDialog();
                        ToastUtil.showToastShort(getString(R.string.network_exception));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        TLog.error("设置支付密码:" + response);
                        hideWaitDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            ToastUtil.showToastShort(jsonObject.getString("msg"));
                            if (jsonObject.getInt("status") == 1) {
                                ToastUtil.showToastShort(R.string.setting_succeed);
                                AppContext.getUserBean().data.payPassStatus = ApiHttpClient.YES;
                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.showToastShort(getString(R.string.network_exception));
                        }
                    }
                });
    }
}
