package com.xwsd.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import com.xwsd.app.AppContext;
import com.xwsd.app.AppManager;
import com.xwsd.app.R;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.fragment.ForgotFragment;
import com.xwsd.app.fragment.LoginFragment;
import com.xwsd.app.view.NavbarManage;

/**
 * Created by Gx on 2016/8/30.
 * 用户界面，管理子Fragment(登录，注册，找回密码,找回支付密码)
 */
public class UserActivity extends BaseActivity {

    public static final int TYPE_LOGIN = 0;

    public static final int TYPE_REGISTER = 1;

    public static final int TYPE_FORGOT = 2;

    public static final int TYPE_FORGET_PAYPASS = 3;

    public int type;

    /**
     * 导航栏
     */
    public NavbarManage navbarManage;

    public FragmentManager fragmentManager;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_user);
        navbarManage = new NavbarManage(this);

        AppContext.setNeedLock(false);      //这里关闭锁防止打开读取短信验证码页面返回需要重新开锁
    }

    @Override
    protected void init(Bundle savedInstanceState) {


        navbarManage.showLeft(true);
        navbarManage.showRight(false);
        navbarManage.setTextColor(R.color.white);
        navbarManage.setLeftImg(R.mipmap.ic_back_w);
        navbarManage.setOnLeftClickListener(new NavbarManage.OnLeftClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }
        });

        type = getIntent().getIntExtra(UserParam.TYPE, TYPE_LOGIN);

//        加载第一个Fragment(登录页面)
        fragmentManager = getSupportFragmentManager();

        switch (type) {
            case TYPE_LOGIN:
                title="登陆";
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_content, new LoginFragment())
                        .commit();
                break;
            case TYPE_FORGOT:
                title="忘记密码";
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_content, new ForgotFragment())
                        .commit();
                break;
            case TYPE_FORGET_PAYPASS:
                title="忘记支付密码";
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_content, new ForgotFragment())
                        .commit();
                break;
        }
    }

    @Override
    public void onBackPressed() { 
        back();
    }

    /**
     * 返回
     */
    private void back() {
//        判断返回栈是否有内容
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            AppManager.getAppManager().finishActivity(MainActivity.class);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
