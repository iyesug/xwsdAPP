package com.xwsd.app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.UserVipBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.view.EmptyLayout;
import com.xwsd.app.view.NavbarManage;
import com.xwsd.app.view.SpinnerDialog;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Gx on 2016/9/28.
 * VIP
 */
public class VIPActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Bind(R.id.error_layout)
    EmptyLayout mErrorLayout;

    @Bind(R.id.ll_default)
    LinearLayout ll_default;

    @Bind(R.id.ll_enabled)
    LinearLayout ll_enabled;

    @Bind(R.id.ll_service_select)
    LinearLayout ll_service_select;

    @Bind(R.id.tv_service)
    TextView tv_service;

    @Bind(R.id.commit)
    Button commit;

    @Bind(R.id.tv_name)
    TextView tv_name;

    @Bind(R.id.tv_time)
    TextView tv_time;

    RequestCall call;

    UserVipBean userVipBean;

    SpinnerDialog timeScreenDialog;

    String servicesId;


    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_vip);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.vip));
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

        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                getData();
            }
        });

        getData();
    }

    private void getData() {
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        if (call != null) {
            call.cancel();
        }
        call = ApiHttpClient.userVip(AppContext.getUserBean().data.userId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("获取VIP信息:" + response);
                mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        userVipBean = GsonUtils.jsonToBean(response, UserVipBean.class);
                        setData();
                    } else if (jsonObject.getInt("status") == 88){
                        AppContext.showToast("用户密码已修改，请重新登录");
                        Intent Fintent = new Intent();
                        Fintent.putExtra(UserParam.TYPE, 0);
                        Fintent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                        startActivity(Fintent);
                        finish();
                    }else {
                        AppContext.showToastShort(jsonObject.getString("msg"));
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

        if (userVipBean.data.isVip == 0) {
            ll_default.setVisibility(View.GONE);
            ll_enabled.setVisibility(View.VISIBLE);

        } else {
            ll_default.setVisibility(View.VISIBLE);
            ll_enabled.setVisibility(View.GONE);
            commit.setVisibility(View.GONE);
            tv_name.setText(userVipBean.data.customService);
            tv_time.setText(userVipBean.data.endTime);
        }
    }

    @OnClick({R.id.commit, R.id.ll_service})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:

                if (TextUtils.isEmpty(servicesId)) {
                    AppContext.showToastShort(R.string.services_null);
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

                call = ApiHttpClient.vipApply(AppContext.getUserBean().data.userId, userVipBean.data.times.get(0).id, servicesId, new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        hideWaitDialog();
                        AppContext.showToastShort(getString(R.string.network_exception));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        TLog.error("申请VIP：" + response);
                        hideWaitDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            AppContext.showToastShort(jsonObject.getString("msg"));
                            if (jsonObject.getInt("status") == 1) {
                                getData();
                            } else {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            AppContext.showToastShort(getString(R.string.network_exception));
                        }
                    }
                });

                break;
            case R.id.ll_service:
                if (timeScreenDialog == null) {
                    timeScreenDialog = new SpinnerDialog(VIPActivity.this, ll_service_select);
                    String[] data = new String[userVipBean.data.customServices.size()];
                    for (int i = 0; i < userVipBean.data.customServices.size(); i++) {
                        data[i] = userVipBean.data.customServices.get(i).name;
                    }
                    timeScreenDialog.setData(data);
                    timeScreenDialog.setOnItemClickListener(new SpinnerDialog.OnItemClickListener() {
                        @Override
                        public void onClick(int position, String data) {
                            tv_service.setText(data);
                            servicesId = userVipBean.data.customServices.get(position).id;
                        }
                    });
                }
                timeScreenDialog.show(ll_service_select.getWidth());
                break;
        }
    }
}