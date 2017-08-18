package com.xwsd.app.oldapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import butterknife.Bind;
import butterknife.OnClick;
import com.xwsd.app.AppContext;
import com.xwsd.app.AppManager;
import com.xwsd.app.R;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.UserBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.oldapp.api.ApiHttpClient;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.NavbarManage;
import com.xwsd.app.view.SwitchView;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Created by Gy on 2017/8/17.
 * 资金自动迁移
 */
public class MoneyAutoTransferActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.toggle_button)
    SwitchView toggle_button;

    RequestCall call;
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_money_auto_transfer);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {




        title=getString(R.string.money_transfer);
        //设置导航栏
        navbarManage.setCentreStr(title);
        navbarManage.showLeft(true);
        navbarManage.setBackground(R.color.navbar_bg);
        navbarManage.setLeftImg(R.mipmap.ic_back_b);
        navbarManage.showRight(true);
        navbarManage.setRightStr("手动迁移");
        navbarManage.setRightTextColor(R.color.blue);
        navbarManage.setOnLeftClickListener(new NavbarManage.OnLeftClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }
        });
        navbarManage.setOnRightClickListener(new NavbarManage.OnRightClickListener() {
            @Override
            public void onRightClick() {
                AppManager.getAppManager().finishActivity();
            }
        });
        getData();

        setSwitch();
    }

    private boolean setToggle() {
        //判断是否登陆
        if (AppContext.getUserBean() == null||null== AppContext.getUserBean().data) {
            Intent intent = new Intent(this, com.xwsd.app.activity.UserActivity.class);
            intent.putExtra(UserParam.TYPE, com.xwsd.app.activity.UserActivity.TYPE_LOGIN);
            intent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
            startActivity(intent);
            return true;
        }

//        设置用户信息
        if(null!= AppContext.getUserBean()&&null!= AppContext.getUserBean().data) {
            //判断是否开启自动
            if (AppContext.getUserBean().data.openTranStatus==1) {
                toggle_button.setState(true);
            } else {
                toggle_button.setState(false);
            }
        }
        return false;
    }

    /**
     * 设置开关
     */
    private void setSwitch() {
        toggle_button.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn() {


                openOrClose(1);
            }

            @Override
            public void toggleToOff() {

                openOrClose(0);
            }
        });
    }

    private void openOrClose(int i) {

        call = ApiHttpClient.openAutoTran(AppContext.getUserBean().data.userId,i,
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if(i==0){
                            toggle_button.toggleSwitch(true);
                        }else{
                            toggle_button.toggleSwitch(false);
                        }
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        TLog.error("自动迁移:" + response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            ToastUtil.showToastShort(jsonObject.getString("msg"));
                            if (jsonObject.getInt("status") == 1) {
                                if(i==0){
                                    toggle_button.toggleSwitch(false);
                                }else{
                                    toggle_button.toggleSwitch(true);
                                }
                            }else{
                                if(i==0){
                                    toggle_button.toggleSwitch(true);
                                }else{
                                    toggle_button.toggleSwitch(false);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.showToastShort(getString(R.string.network_exception));

                        }
                    }
                });
    }

    @OnClick({})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {



        }
    }

    /**
     * 获取数据
     */
    private void getData() {

        if (call != null) {
            call.cancel();
        }

        call = ApiHttpClient.getUserInfo(AppContext.getUserBean().data.userId, new StringCallback() {
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
                        //判断是否开通自动迁移
                        setToggle();
                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        });
    }
}
