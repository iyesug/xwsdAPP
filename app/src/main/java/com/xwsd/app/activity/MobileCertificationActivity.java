package com.xwsd.app.activity;

import android.os.Bundle;
import android.widget.EditText;

import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.view.NavbarManage;

import butterknife.Bind;

/**
 * Created by Gx on 2016/8/29.
 * 手机认证
 */
public class MobileCertificationActivity extends BaseActivity {

    @Bind(R.id.et_phone)
    EditText et_phone;

    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_mobile_certification);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.mobile_certification));
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

        et_phone.setText(AppContext.getUserBean().data.phone.replace(AppContext.getUserBean().data.phone.substring(5, 9), "****"));
    }
}
