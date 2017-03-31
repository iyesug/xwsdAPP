package com.xwsd.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import butterknife.Bind;
import com.gnwai.iosdialog.AlertDialog;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.UserBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.EmptyLayout;
import com.xwsd.app.view.NavbarManage;
import com.xwsd.app.view.SwitchView;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Gx on 2016/8/31.
 * 开通托管
 */
public class DredgeTrusteeshipActivity extends BaseActivity {
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Bind(R.id.toggle_button)
    SwitchView toggle_button;

    RequestCall call;

    private boolean needRefresh = false;

    @Bind(R.id.error_layout)
    EmptyLayout mErrorLayout;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_dredge_trusteeship);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.dredge_trusteeship));
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

        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);

        if (AppContext.getUserBean().data.thirdAccountStatus == 0) {
            toggle_button.setState(false);
        } else {
            toggle_button.setState(true);
        }

        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                getData();
            }
        });

        toggle_button.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {
                skip();
            }

            @Override
            public void toggleToOff() {
                new AlertDialog(DredgeTrusteeshipActivity.this)
                        .builder()
                        .setTitle("温馨提示：")
                        .setMsg("暂不支持关闭托管。")
                        .setNegativeButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).show();
//                skip();
            }
        });
    }

    /**
     * 跳转
     */
    private void skip() {
        needRefresh = true;
        Intent intent = new Intent(DredgeTrusteeshipActivity.this, WebApproveActivity.class);
        Map<String, String> map = ApiHttpClient.getSortMap();
        map.put("userId", AppContext.getUserBean().data.userId);
        intent.putExtra(UserParam.URL, ApiHttpClient.BIND_THIRD +
                "?userId=" + AppContext.getUserBean().data.userId +
                "&sign=" + ApiHttpClient.sign(map));
        TLog.error("UserParam.URL:"+ApiHttpClient.BIND_THIRD +
                "?userId=" + AppContext.getUserBean().data.userId +
                "&sign=" + ApiHttpClient.sign(map));

        if (AppContext.getUserBean().data.thirdAccountStatus == 1) {
            intent.putExtra(UserParam.TITLE, getString(R.string.cancel_trusteeship));
        } else {
            intent.putExtra(UserParam.TITLE, getString(R.string.dredge_trusteeship));
        }
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needRefresh) {
            getData();
            needRefresh = false;
        }
    }

    /**
     * 获取数据
     */
    private void getData() {
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        //重新获取用户的托管状态
        if (call != null) {
            call.cancel();
        }

        call = ApiHttpClient.getUserInfo(AppContext.getUserBean().data.userId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("获取用户信息:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
//                        ToastUtil.showToastShort(jsonObject.getString("msg"));
                    if (jsonObject.getInt("status") == 1) {
                        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        UserBean userBean = GsonUtils.jsonToBean(response, UserBean.class);
                        AppContext.setUserBean(userBean);
//                            判断是否开通托管
                        if (userBean.data.thirdAccountStatus == 1) {
//                                保存托管状态
                            toggle_button.setState(true);
                        } else {
                            toggle_button.setState(false);
                        }
                    } else if (jsonObject.getInt("status") == 88){
                        ToastUtil.showToast("用户密码已修改，请重新登录");
                        Intent Fintent = new Intent();
                        Fintent.putExtra(UserParam.TYPE, 0);
                        Fintent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                        startActivity(Fintent);
                        finish();
                    }else {
                        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                }
            }
        });
    }
}
