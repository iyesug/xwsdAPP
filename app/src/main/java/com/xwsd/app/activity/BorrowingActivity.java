package com.xwsd.app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.AreasBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.CheckUtil;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.CheckView;
import com.xwsd.app.view.NavbarManage;
import com.xwsd.app.view.WheelView;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import me.drakeet.materialdialog.MaterialDialog;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gx on 2016/8/29.
 * 借款
 */
public class BorrowingActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Bind(R.id.auth_code)
    CheckView auth_code;

    @Bind(R.id.tv_address_province)
    TextView tv_address_province;

    @Bind(R.id.tv_address_city)
    TextView tv_address_city;

    @Bind(R.id.et_name)
    EditText et_name;

    @Bind(R.id.et_phone)
    EditText et_phone;

    @Bind(R.id.et_money)
    EditText et_money;

    @Bind(R.id.et_auth)
    EditText et_auth;

    AreasBean provinces;

    AreasBean citys;

    int provincesId = -1;

    int citysId = -1;

    RequestCall call;

    MaterialDialog materialDialog;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_borrowing);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        title=getString(R.string.borrowing);
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.borrowing));
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
//                判断用户是否登录
        if (AppContext.getUserBean() == null||null==AppContext.getUserBean().data) {
            Intent intent = new Intent(this, UserActivity.class);
            intent.putExtra(UserParam.TYPE, UserActivity.TYPE_LOGIN);
            intent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
            startActivity(intent);
            return;
        }
//        设置用户信息
        if(null!=AppContext.getUserBean()&&null!=AppContext.getUserBean().data&&null!=AppContext.getUserBean().data.name) {
            et_name.setText(AppContext.getUserBean().data.name.replace(AppContext.getUserBean().data.name.substring(0, 1), "*"));
            et_phone.setText(AppContext.getUserBean().data.phone.replace(AppContext.getUserBean().data.phone.substring(5, 9), "****"));
        }
    }

    @OnClick({R.id.ll_address_province, R.id.ll_address_city, R.id.commit, R.id.auth_code})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_address_province:
                if (provinces == null) {
                    getAreas(0);
                } else {
                    showAreasDialog(0, provinces);
                }
                break;
            case R.id.ll_address_city:
                if (provincesId == -1) {
                    ToastUtil.showToastShort("请先选择省份");
                    return;
                }
                if (citys == null) {
                    getAreas(provincesId);
                } else {
                    showAreasDialog(provincesId, citys);
                }
                break;
            case R.id.commit:
                if (provincesId == -1) {
                    ToastUtil.showToastShort(getString(R.string.provinces_null));
                    return;
                }

                if (citysId == -1) {
                    ToastUtil.showToastShort(getString(R.string.citys_null));
                    return;
                }

                if (TextUtils.isEmpty(et_money.getText().toString().trim())) {
                    ToastUtil.showToastShort(getString(R.string.borrowing_money_null));
                    return;
                }

//                判断输入的金额是否是50的倍数
                if (Integer.valueOf(et_money.getText().toString().trim()) % 50 != 0) {
                    ToastUtil.showToastShort(getString(R.string.borrowing_money_error));
                    return;
                }

                if (TextUtils.isEmpty(et_auth.getText().toString().trim())) {
                    ToastUtil.showToastShort(getString(R.string.borrowing_auth_null));
                    return;
                }

                if (!et_auth.getText().toString().trim().equals(auth_code.getCheckNum())) {
                    ToastUtil.showToastShort(getString(R.string.borrowing_auth_incorrect));
                    return;
                }

                showWaitDialog(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (call != null) {
                            call.cancel();
                        }
                    }
                });
//                判断用户是否登录
                if (AppContext.getUserBean() == null||null==AppContext.getUserBean().data) {
                    Intent intent = new Intent(this, UserActivity.class);
                    intent.putExtra(UserParam.TYPE, UserActivity.TYPE_LOGIN);
                    intent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                    startActivity(intent);
                    return;
                }
                call = ApiHttpClient.order(
                        AppContext.getUserBean().data.phone,
                        AppContext.getUserBean().data.name,
                        et_money.getText().toString().trim(),
                        String.valueOf(provincesId),
                        String.valueOf(citysId),
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                hideWaitDialog();
                                ToastUtil.showToastShort(getString(R.string.network_exception));
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                TLog.error("申请借款:" + response);
                                hideWaitDialog();
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (materialDialog == null) {
                                        materialDialog = new MaterialDialog(BorrowingActivity.this)
                                                .setTitle("温馨提示：")
                                                .setMessage(jsonObject.getString("msg"))
                                                .setPositiveButton("确定", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        materialDialog.dismiss();
                                                    }
                                                });
                                    } else {
                                        materialDialog.show();
                                    }
                                    if (jsonObject.getInt("status") == 1) {
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    ToastUtil.showToastShort(getString(R.string.network_exception));
                                }
                            }
                        }
                );

                break;
            case R.id.auth_code:
                auth_code.setCheckNum(CheckUtil.getCheckNum());
                auth_code.invaliChenkNum();
                break;
        }

    }

    /**
     * 得到地址列表
     */

    private void getAreas(final int areasId) {

        ApiHttpClient.areas(String.valueOf(areasId), new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtil.showToastShort(R.string.network_exception);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("地址：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        if (areasId == 0) {
                            provinces = GsonUtils.jsonToBean(response, AreasBean.class);
                            showAreasDialog(areasId, provinces);
                        } else if (jsonObject.getInt("status") == 88){
                            ToastUtil.showToast(getString(R.string.please_relogin));
                            Intent Fintent = new Intent();
                            Fintent.putExtra(UserParam.TYPE, 0);
                            Fintent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                            startActivity(Fintent);
                            finish();
                        }else {
                            citys = GsonUtils.jsonToBean(response, AreasBean.class);
                            showAreasDialog(areasId, citys);
                        }

                    } else {
                        ToastUtil.showToastShort(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.showToastShort(getString(R.string.network_exception));
                }
            }
        });
    }

    /**
     * 弹出省份选择对话框
     */
    private void showAreasDialog(final int areasId, AreasBean bean) {
        //        隐藏软键盘
        hideSoftKeyboard(getCurrentFocus());
        View outerView = LayoutInflater.from(BorrowingActivity.this).inflate(R.layout.view_wheel, null);
        final WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
        List<String> strings = new ArrayList();
        for (AreasBean.Data.Records records : bean.data.records) {
            strings.add(records.name);
        }
        wv.setOffset(2);
        wv.setItems(strings);
        wv.setSeletion(0);
        final MaterialDialog materialDialog = new MaterialDialog(BorrowingActivity.this);

        if (areasId == 0) {
            materialDialog.setTitle(R.string.province_select);
        } else {
            materialDialog.setTitle(R.string.city_select);
        }

        materialDialog.setContentView(outerView)
                .setPositiveButton(getString(R.string.confirm),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                materialDialog.dismiss();
                                if (areasId == 0) {
                                    provincesId = getAreasKey(provinces, wv.getSeletedItem());
                                    tv_address_province.setText(wv.getSeletedItem());
                                    //重置城市
                                    citys = null;
                                    tv_address_city.setText("选择城市");
                                } else {
                                    citysId = getAreasKey(citys, wv.getSeletedItem());
                                    tv_address_city.setText(wv.getSeletedItem());
                                }
                            }
                        })
                .setCanceledOnTouchOutside(true)
                .show();
    }

    /**
     * 得到地址的键
     *
     * @param areasBean
     * @param value
     * @return
     */
    private int getAreasKey(AreasBean areasBean, String value) {
        for (AreasBean.Data.Records records : areasBean.data.records) {
            if (records.name.equals(value)) {
                return records.key;
            }
        }
        return 1;
    }
}
