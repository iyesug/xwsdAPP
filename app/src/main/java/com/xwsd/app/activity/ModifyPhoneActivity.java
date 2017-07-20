package com.xwsd.app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.OnClick;
import com.xwsd.app.AppContext;
import com.xwsd.app.AppManager;
import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.NavbarManage;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.Map;

/**
 * Created by Gy on 2017/6/29.
 * 修改手机号
 */
public class ModifyPhoneActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;



    /**
     * 新手机号
     */
    @Bind(R.id.et_phone)
    EditText et_phone;

    RequestCall call;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_modify_phone);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        title=getString(R.string.ll_modify_update_phone);
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.ll_modify_update_phone));
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
    }

    @OnClick({R.id.commit})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:
                if (TextUtils.isEmpty(et_phone.getText().toString().trim())) {
                    ToastUtil.showToastShort(getString(R.string.please_write_full));
                    return;
                }




                //                开启对话框
                showWaitDialog(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (call != null) {
                            call.cancel();
                        }
                    }
                });

                //修改手机号【存管】，跳转到修改手机号页面
                Intent intent = new Intent(this, WebApproveActivity.class);
                Map<String, String> map = ApiHttpClient.getSortMap();
                map.put("userId", AppContext.getUserBean().data.userId);
                map.put("phone", et_phone.getText().toString().trim());
                map.put("media", "Android");
                intent.putExtra(UserParam.URL, ApiHttpClient.UPDATE_PHONE +
                        "?userId=" + AppContext.getUserBean().data.userId +
                        "&phone=" + et_phone.getText().toString().trim()+
                        "&media=" + "Android"+
                        "&sign=" + ApiHttpClient.sign(map));

                intent.putExtra(UserParam.TITLE, getString(R.string.ll_modify_update_phone));
                startActivity(intent);
                AppManager.getAppManager().finishActivity();



                break;


        }
    }
}
