package com.xwsd.app.activity;

import android.os.Bundle;

import com.xwsd.app.R;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.view.NavbarManage;

/**
 * Created by Gx on 2016/8/25.
 * 修改昵称
 */
public class NicknameActivity extends BaseActivity {

    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_nickname);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        title=getString(R.string.nickname);
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.nickname));
        navbarManage.showLeft(true);
        navbarManage.showRight(true);
        navbarManage.setRightStr(getString(R.string.save));
        navbarManage.setBackground(R.color.navbar_bg);
        navbarManage.setOnLeftClickListener(new NavbarManage.OnLeftClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }
        });
        navbarManage.setOnRightClickListener(new NavbarManage.OnRightClickListener() {
            @Override
            public void onRightClick() {

            }
        });
    }
}
