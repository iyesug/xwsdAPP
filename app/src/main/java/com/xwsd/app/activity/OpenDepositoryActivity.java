package com.xwsd.app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
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
 * Created by Gy on 2017/7/11.
 * 绑定第三方帐号【存管】开通存管
 */
public class OpenDepositoryActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    /**
     * 姓名
     */
    @Bind(R.id.et_name)
    EditText et_name;

    /**
     * 身份证
     */
    @Bind(R.id.et_cardnum)
    EditText et_cardnum;

    /**
     * 银行卡
     */
    @Bind(R.id.et_bankNum)
    EditText et_bankNum;

    /**
     * 协议
     */
    @Bind(R.id.agreement)
    TextView agreement;

    /**
     * 阅读
     */
    @Bind(R.id.check)
    RadioButton check;

    @Bind(R.id.ll_input)
    LinearLayout ll_input;

    @Bind(R.id.ll_alredy)
    LinearLayout ll_alredy;

    @Bind(R.id.commit)
    Button commit;

    RequestCall call;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_open_depository);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        title=getString(R.string.open_depository);
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.open_depository));
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

//        Log.e("custodyId:",AppContext.getUserBean().data.custodyId);
        if(AppContext.getUserBean().data.custodyId==null||"0".equals(AppContext.getUserBean().data.custodyId)||"".equals(AppContext.getUserBean().data.custodyId)){
            ll_alredy.setVisibility(View.GONE);
            ll_input.setVisibility(View.VISIBLE);
            commit.setVisibility(View.VISIBLE);
        }
//
//        check.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(check.isChecked()){
//                    check.setChecked(false);
//                }else{
//                    check.setChecked(true);
//                }
//            }
//        });

    }

    @OnClick({R.id.commit,R.id.agreement})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.agreement:
                //绑定第三方帐号【存管】，跳转到绑定第三方帐号页面
                Intent fintent = new Intent(this, WebApproveActivity.class);


                fintent.putExtra(UserParam.URL, ApiHttpClient.OPEN_CUSTODY_AGREEMENT );

                fintent.putExtra(UserParam.TITLE, "《江西银行账户存管第三方协议》");
                startActivity(fintent);


                break;

            case R.id.commit:
                String name=et_name.getText().toString().trim();
                String cardnum=et_cardnum.getText().toString().trim();
                String bankNum=et_bankNum.getText().toString().trim();


                if (TextUtils.isEmpty(name)) {
                    ToastUtil.showToastShort(getString(R.string.please_write_full));
                    return;
                }

                if (TextUtils.isEmpty(cardnum)) {
                    ToastUtil.showToastShort(getString(R.string.please_write_full));
                    return;
                }

                if (TextUtils.isEmpty(bankNum)) {
                    ToastUtil.showToastShort(getString(R.string.please_write_full));
                    return;
                }
                if (!check.isChecked()) {
                    ToastUtil.showToastShort(getString(R.string.please_check));
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
                //绑定第三方帐号【存管】，跳转到绑定第三方帐号页面
                Intent intent = new Intent(this, WebApproveActivity.class);
                Map<String, String> map = ApiHttpClient.getSortMap();
                map.put("userId", AppContext.getUserBean().data.userId);
                map.put("cardnum", cardnum);
                map.put("name", name);
                map.put("bankNum", bankNum);

                map.put("media", "Android");
                intent.putExtra(UserParam.URL, ApiHttpClient.OPEN_CUSTODY +
                        "?userId=" + AppContext.getUserBean().data.userId +
                        "&cardnum=" + cardnum+
                        "&name=" + name+
                        "&bankNum=" + bankNum+

                        "&media=" + "Android"+
                        "&sign=" + ApiHttpClient.sign(map));

                intent.putExtra(UserParam.TITLE, getString(R.string.open_depository));
                startActivity(intent);
                AppManager.getAppManager().finishActivity();

                break;


        }
    }
}
