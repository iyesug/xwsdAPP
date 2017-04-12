package com.xwsd.app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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
 * 实名认证
 */
public class CertificationActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Bind(R.id.et_name)
    EditText et_name;

    @Bind(R.id.et_identity)
    EditText et_identity;

    @Bind(R.id.et_state)
    EditText et_state;

    @Bind(R.id.commit)
    Button commit;

    RequestCall call;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_certification);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.certification));
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
        //判断是否登陆
        if (AppContext.getUserBean() == null||null==AppContext.getUserBean().data) {
            Intent intent = new Intent(this, UserActivity.class);
            intent.putExtra(UserParam.TYPE, UserActivity.TYPE_LOGIN);
            intent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
            startActivity(intent);
            return;
        }
        //判断用户是否已实名认证
        if (AppContext.getUserBean().data.cardstatus.equals(ApiHttpClient.YES)) {
            commit.setVisibility(View.GONE);
            et_name.setText(AppContext.getUserBean().data.name.replace(AppContext.getUserBean().data.name.substring(0, 1), "*"));
            et_name.setEnabled(false);
            et_identity.setText(AppContext.getUserBean().data.cardnum.replace(AppContext.getUserBean().data.cardnum.substring(3, 13), "**********"));
            et_identity.setEnabled(false);
            et_state.setText("已认证");
        }
    }


    @OnClick({R.id.commit})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:
                //                    判断输入是否为空
                if (TextUtils.isEmpty(et_name.getText().toString().trim())) {
                    ToastUtil.showToastShort(getString(R.string.name_null));
                    return;
                }

                if (TextUtils.isEmpty(et_identity.getText().toString().trim())) {
                    ToastUtil.showToastShort(getString(R.string.identity_null));
                    return;
                }

//                    开启对话框
                showWaitDialog(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (call != null) {
                            call.cancel();
                        }
                    }
                });

                call = ApiHttpClient.certification(
                        AppContext.getUserBean().data.userId,
                        et_name.getText().toString().trim(),
                        et_identity.getText().toString().trim(),
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                hideWaitDialog();
                                ToastUtil.showToastShort(getString(R.string.network_exception));
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                TLog.error("实名认证:" + response);
                                hideWaitDialog();
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    ToastUtil.showToastShort(jsonObject.getString("msg"));
                                    if (jsonObject.getInt("status") == 1) {
                                        AppContext.getUserBean().data.cardstatus = ApiHttpClient.YES;
                                        et_name.setEnabled(false);
                                        et_identity.setEnabled(false);
                                        et_state.setText("已认证");
                                        commit.setVisibility(View.GONE);
                                    } else {

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    ToastUtil.showToastShort(getString(R.string.network_exception));
                                }
                            }
                        });
                break;
        }
    }
}
