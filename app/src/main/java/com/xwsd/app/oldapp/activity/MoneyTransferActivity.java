package com.xwsd.app.oldapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.OnClick;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.oldapp.api.ApiHttpClient;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.MADialog;
import com.xwsd.app.view.NavbarManage;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Created by Gx on 2016/8/29.
 * 手机认证
 */
public class MoneyTransferActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.et_money)
    EditText et_money;
    @Bind(R.id.et_fundMoney)
    EditText et_fundMoney;

    float max;
    RequestCall call;
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;
    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_money_transfer);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {


        Intent intent=getIntent();
        max=intent.getFloatExtra("fundMoney",0);
        et_fundMoney.setText(""+max);
        et_fundMoney.setEnabled(false);

        title=getString(R.string.money_transfer);
        //设置导航栏
        navbarManage.setCentreStr(title);
        navbarManage.showLeft(true);
        navbarManage.setBackground(R.color.navbar_bg);
        navbarManage.setLeftImg(R.mipmap.ic_back_b);

        navbarManage.setOnLeftClickListener(new NavbarManage.OnLeftClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }
        });


        et_money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    if (Float.valueOf(s.toString()) > Float.valueOf(max)) {
                        ToastUtil.showToastShort(R.string.Transfer_money_exceed);

                        et_money.setText(max+"");
                        if(20000<Float.valueOf(max)){
                            et_money.setText("20000");
                        }
                        return;
                    }
                    else if(20000<Float.valueOf(s.toString())){
                        ToastUtil.showToastShort(getString(R.string.once_max));
                        et_money.setText("20000");
                    }
                }

            }
        });
    }


    @OnClick({R.id.commit,R.id.tv_max})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_max:
                et_money.setText(""+max);
                if(20000<Float.valueOf(max)){
                    et_money.setText("20000");
            }
                break;
            case R.id.commit:
                if (TextUtils.isEmpty(et_money.getText().toString().trim())) {
                    ToastUtil.showToastShort(getString(R.string.please_write_full));
                    return;
                }
                //单笔最大20000
                if (20000<Float.valueOf(et_money.getText().toString().trim())) {
                    ToastUtil.showToastShort(getString(R.string.once_max));
                    return;
                }




                final MADialog mMDialog = new MADialog(this);
                mMDialog.setMessage("是否确认迁移资金？");
                mMDialog.setBtnOK("确定", v1 -> {
                    mMDialog.miss();
                    //跳转到解绑页面
//                    //                开启对话框
//                    showWaitDialog(new DialogInterface.OnCancelListener() {
//                        @Override
//                        public void onCancel(DialogInterface dialog) {
//                            if (call != null) {
//                                call.cancel();
//                            }
//                        }
//                    });

                    call = ApiHttpClient.tranMoney(AppContext.getUserBean().data.userId,et_money.getText().toString().trim(),

                            new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {

                                    ToastUtil.showToastShort(getString(R.string.network_exception));
                                    finish();
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    TLog.error("资金迁移:" + response);

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        ToastUtil.showToastShort(jsonObject.getString("msg"));
                                        if (jsonObject.getInt("status") == 1) {

                                            finish();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        ToastUtil.showToastShort(getString(R.string.network_exception));
                                        finish();
                                    }
                                }
                            });

                });
                mMDialog.setBtnCancel("取消", v12 -> mMDialog.miss());






                break;


        }
    }

}
