package com.xwsd.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import butterknife.Bind;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.view.NavbarManage;

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
        title=getString(R.string.mobile_certification);
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.mobile_certification));
        navbarManage.showLeft(true);
        navbarManage.showRight(true);
        navbarManage.setBackground(R.color.navbar_bg);
        navbarManage.setLeftImg(R.mipmap.ic_back_b);
        navbarManage.setRightStr(getResources().getString(R.string.modification));
        navbarManage.setOnRightClickListener(new NavbarManage.OnRightClickListener() {
            @Override
            public void onRightClick() {
                BuriedPointUtil.buriedPoint("账户安全修改手机号码");
                Intent intent = new Intent(MobileCertificationActivity.this, ModifyPhoneActivity.class);
                startActivity(intent);
            }
        });
        navbarManage.setOnLeftClickListener(new NavbarManage.OnLeftClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }
        });

        et_phone.setText(AppContext.getUserBean().data.phone.replace(AppContext.getUserBean().data.phone.substring(5, 9), "****"));
    }
}
