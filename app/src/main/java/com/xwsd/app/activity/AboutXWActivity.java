package com.xwsd.app.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xwsd.app.R;
import com.xwsd.app.api.XWSDRequestAdresse;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.view.MADialog;
import com.xwsd.app.view.NavbarManage;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Gx on 2016/8/29.
 * 关于小微
 */
public class AboutXWActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Bind(R.id.tv_versions)
    TextView tv_versions;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_about_xw);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.about_xw));
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

        tv_versions.setText(getVersion());
    }

    @OnClick({R.id.ll_about_us, R.id.ll_help_center, R.id.ll_charging_standard, R.id.ll_update,R.id.call})
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ll_about_us:
                intent = new Intent(AboutXWActivity.this, WebDetailsActivity.class);
                intent.putExtra(UserParam.TITLE, "关于我们");
                intent.putExtra(UserParam.URL, XWSDRequestAdresse.ABOUT_US);
                intent.putExtra(UserParam.TYPE, WebDetailsActivity.TYPE_NATIVE);
                startActivity(intent);
                break;
            case R.id.ll_help_center:
                intent = new Intent(AboutXWActivity.this, WebDetailsActivity.class);
                intent.putExtra(UserParam.TITLE, "帮助中心");
                intent.putExtra(UserParam.URL, XWSDRequestAdresse.QUESTION);
                intent.putExtra(UserParam.TYPE, WebDetailsActivity.TYPE_NETWORK);
                startActivity(intent);
                break;
            case R.id.ll_charging_standard:
                intent = new Intent(AboutXWActivity.this, WebDetailsActivity.class);
                intent.putExtra(UserParam.TITLE, "收费标准");
                intent.putExtra(UserParam.URL, XWSDRequestAdresse.FEES);
                intent.putExtra(UserParam.TYPE, WebDetailsActivity.TYPE_NETWORK);
                startActivity(intent);
                break;
            case R.id.ll_update:
                break;
            case R.id.call:
                final MADialog mMDialog = new MADialog(this);
                mMDialog.setMessage("确认拨打：400 8659 993");
                mMDialog.setBtnOK("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMDialog.miss();
                        Intent intentPhone = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "4008659993"));
                        startActivity(intentPhone);
                    }
                });
                mMDialog.setBtnCancel("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMDialog.miss();
                    }
                });



        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return this.getString(R.string.can_not_find_version_name);
        }
    }
}
