package com.xwsd.app.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.AreasBean;
import com.xwsd.app.bean.BankCardsBean;
import com.xwsd.app.bean.BanksBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.PatternUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.view.NavbarManage;
import com.xwsd.app.view.WheelView;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import me.drakeet.materialdialog.MaterialDialog;
import okhttp3.Call;

/**
 * Created by Gx on 2016/8/29.
 * 添加银行卡
 */
public class
AddCardActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Bind(R.id.et_name)
    EditText et_name;

    @Bind(R.id.et_identity)
    EditText et_identity;

    @Bind(R.id.et_card_num)
    EditText et_card_num;

    @Bind(R.id.et_branch_name)
    EditText et_branch_name;

    @Bind(R.id.tv_open_bank)
    TextView tv_open_bank;

    @Bind(R.id.tv_address_province)
    TextView tv_address_province;

    @Bind(R.id.tv_address_city)
    TextView tv_address_city;

    BanksBean banksBean;

    AreasBean provinces;

    AreasBean citys;

    int banksId = -1;

    int provincesId = -1;

    int citysId = -1;

    RequestCall call;

    MaterialDialog bankDialog;

    /**
     * 添加银行卡是 1；修改银行卡是 0；
     */
    private int type;

    private String bankId;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_add_card);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.bank_card_manage));
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

//        设置用户信息
        et_name.setText(AppContext.getUserBean().data.name.replace(AppContext.getUserBean().data.name.substring(0, 1), "*"));
        et_identity.setText(AppContext.getUserBean().data.cardnum.replace(AppContext.getUserBean().data.cardnum.substring(3, 13), "**********"));

//        判断是否是修改银行卡
        if (getIntent().getSerializableExtra(UserParam.DATA) != null) {
//            得到银行卡信息
            BankCardsBean.records data = (BankCardsBean.records) getIntent().getSerializableExtra(UserParam.DATA);
            type = 0;
//            设置银行信息
            et_card_num.setText(data.bankNum);
            et_branch_name.setText(data.subbranch);
            tv_open_bank.setText(data.bankName);
            banksId = data.bank;
            provincesId = data.province;
            citysId = data.city;
            bankId = data.id;
            setAreas(0, 0, data.province);
            setAreas(1, provincesId, data.city);
        } else {
            type = 1;
        }

    }

    @OnClick({R.id.commit, R.id.ll_open_bank, R.id.ll_address_province, R.id.ll_address_city})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:
                if (TextUtils.isEmpty(et_name.getText().toString().trim())) {
                    AppContext.showToastShort(getString(R.string.name_null));
                    return;
                }

                if (TextUtils.isEmpty(et_identity.getText().toString().trim())) {
                    AppContext.showToastShort(getString(R.string.identity_null));
                    return;
                }

                if (TextUtils.isEmpty(et_card_num.getText().toString().trim())) {
                    AppContext.showToastShort(getString(R.string.card_num_null));
                    return;
                }

                if (!PatternUtils.matchesNum(et_card_num.getText().toString().trim(), 16, 19)) {
                    AppContext.showToastShort(getString(R.string.card_num_length));
                    return;
                }

                if (banksId == -1) {
                    AppContext.showToastShort(getString(R.string.banks_null));
                    return;
                }

                if (provincesId == -1) {
                    AppContext.showToastShort(getString(R.string.provinces_null));
                    return;
                }

                if (citysId == -1) {
                    AppContext.showToastShort(getString(R.string.citys_null));
                    return;
                }

                if (TextUtils.isEmpty(et_branch_name.getText().toString().trim())) {
                    AppContext.showToastShort(getString(R.string.branch_name_null));
                    return;
                }


                if (type == 1) {//添加
                    addCard(AppContext.getUserBean().data.userId,
                            et_card_num.getText().toString().trim(),
                            String.valueOf(banksId),
                            String.valueOf(provincesId),
                            String.valueOf(citysId),
                            et_branch_name.getText().toString().trim(),
                            ApiHttpClient.NO);
                } else {//修改
                    modifyCard(bankId,
                            AppContext.getUserBean().data.userId,
                            et_card_num.getText().toString().trim(),
                            String.valueOf(banksId),
                            String.valueOf(provincesId),
                            String.valueOf(citysId),
                            et_branch_name.getText().toString().trim(),
                            ApiHttpClient.NO);
                }

                break;
            case R.id.ll_open_bank:
                if (banksBean == null) {
                    getBanks();
                } else {
                    showBankDialog(banksBean);
                }

                break;
            case R.id.ll_address_province:
                if (provinces == null) {
                    getAreas(0);
                } else {
                    showAreasDialog(0, provinces);
                }
                break;
            case R.id.ll_address_city:
                if (provincesId == -1) {
                    AppContext.showToastShort("请先选择省份");
                    return;
                }
                if (citys == null) {
                    getAreas(provincesId);
                } else {
                    showAreasDialog(provincesId, citys);
                }
                break;
        }
    }

    /**
     * 修改银行卡
     *
     * @param userId
     * @param bankNum
     * @param bank
     * @param province
     * @param city
     * @param subbranch
     * @param isDefault
     */
    private void modifyCard(
            String id,
            String userId,
            String bankNum,
            String bank,
            String province,
            String city,
            String subbranch,
            String isDefault) {

        showWaitDialog(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (call != null) {
                    call.cancel();
                }
            }
        });
        call = ApiHttpClient.updateBankCard(
                id,
                userId,
                bankNum,
                bank,
                province,
                city,
                subbranch,
                isDefault,
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        hideWaitDialog();
                        AppContext.showToastShort(getString(R.string.network_exception));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        TLog.error("修改银行卡:" + response);
                        hideWaitDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            AppContext.showToastShort(jsonObject.getString("msg"));
                            if (jsonObject.getInt("status") == 1) {
                                finish();
                                BankCardActivity.needRefresh = true;
                            } else {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            AppContext.showToastShort(getString(R.string.network_exception));
                        }
                    }
                });

    }

    /**
     * 添加银行卡
     *
     * @param userId
     * @param bankNum
     * @param bank
     * @param province
     * @param city
     * @param subbranch
     * @param isDefault
     */
    private void addCard(String userId,
                         String bankNum,
                         String bank,
                         String province,
                         String city,
                         String subbranch,
                         String isDefault) {

        showWaitDialog(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (call != null) {
                    call.cancel();
                }
            }
        });
        call = ApiHttpClient.addBankCard(
                userId,
                bankNum,
                bank,
                province,
                city,
                subbranch,
                isDefault,
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        hideWaitDialog();
                        AppContext.showToastShort(getString(R.string.network_exception));
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        TLog.error("添加银行卡:" + response);
                        hideWaitDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            AppContext.showToastShort(jsonObject.getString("msg"));
                            if (jsonObject.getInt("status") == 1) {
                                finish();
                                BankCardActivity.needRefresh = true;
                            } else {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            AppContext.showToastShort(getString(R.string.network_exception));
                        }
                    }
                });
    }

    /**
     * 设置地址
     *
     * @param areasType 地址类型，0省份，1城市
     * @param fatherId  父亲的ID
     * @param autoId    自己的ID
     */
    private void setAreas(final int areasType, int fatherId, final int autoId) {
        ApiHttpClient.areas(String.valueOf(fatherId), new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                AppContext.showToastShort(R.string.network_exception);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("地址：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        if (areasType == 0) {
                            provinces = GsonUtils.jsonToBean(response, AreasBean.class);
                            tv_address_province.setText(getAreasValue(provinces, autoId));

                        } else {
                            citys = GsonUtils.jsonToBean(response, AreasBean.class);
                            tv_address_city.setText(getAreasValue(citys, autoId));
                        }

                    } else {
                        AppContext.showToastShort(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    AppContext.showToastShort(getString(R.string.network_exception));
                }
            }
        });
    }


    /**
     * 得到地址列表
     */
    private void getAreas(final int areasId) {

        ApiHttpClient.areas(String.valueOf(areasId), new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                AppContext.showToastShort(R.string.network_exception);
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
                        } else {
                            citys = GsonUtils.jsonToBean(response, AreasBean.class);
                            showAreasDialog(areasId, citys);
                        }

                    } else {
                        AppContext.showToastShort(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    AppContext.showToastShort(getString(R.string.network_exception));
                }
            }
        });
    }

    /**
     * 得到银行列表
     */
    private void getBanks() {

        ApiHttpClient.banks(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                AppContext.showToastShort(R.string.network_exception);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("银行列表：" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        banksBean = GsonUtils.jsonToBean(response, BanksBean.class);
                        showBankDialog(banksBean);
                    } else {
                        AppContext.showToastShort(jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    AppContext.showToastShort(getString(R.string.network_exception));
                }
            }
        });
    }

    /**
     * 弹出银行选择对话框
     */
    private void showBankDialog(BanksBean bean) {
//        隐藏软键盘
        hideSoftKeyboard(getCurrentFocus());
        if (bankDialog == null) {
            View outerView = LayoutInflater.from(AddCardActivity.this).inflate(R.layout.view_wheel, null);
            final WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
            List<String> strings = new ArrayList();
            for (BanksBean.Data.Records records : bean.data.records) {
                strings.add(records.name);
            }
            wv.setOffset(2);
            wv.setItems(strings);
            wv.setSeletion(0);
            bankDialog = new MaterialDialog(AddCardActivity.this).setTitle(R.string.banks_select)
                    .setContentView(outerView)
                    .setPositiveButton(getString(R.string.confirm),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    bankDialog.dismiss();
                                    banksId = getBanksKey(banksBean, wv.getSeletedItem());
                                    tv_open_bank.setText(wv.getSeletedItem());
                                }
                            })
                    .setCanceledOnTouchOutside(true);
        }
        bankDialog.show();
    }

    /**
     * 弹出省份选择对话框
     */
    private void showAreasDialog(final int areasId, AreasBean bean) {
        //        隐藏软键盘
        hideSoftKeyboard(getCurrentFocus());
        View outerView = LayoutInflater.from(AddCardActivity.this).inflate(R.layout.view_wheel, null);
        final WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
        List<String> strings = new ArrayList();
        for (AreasBean.Data.Records records : bean.data.records) {
            strings.add(records.name);
        }
        wv.setOffset(2);
        wv.setItems(strings);
        wv.setSeletion(0);
        final MaterialDialog materialDialog = new MaterialDialog(AddCardActivity.this);

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
     * 得到银行的键
     *
     * @param banksBean
     * @param value
     * @return
     */
    private int getBanksKey(BanksBean banksBean, String value) {
        for (BanksBean.Data.Records records : banksBean.data.records) {
            if (records.name.equals(value)) {
                return records.key;
            }
        }
        return 1;
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

    /**
     * 得到地址的值
     *
     * @param areasBean
     * @param key
     * @return
     */
    private String getAreasValue(AreasBean areasBean, int key) {
        for (AreasBean.Data.Records records : areasBean.data.records) {
            if (records.key == key) {
                return records.name;
            }
        }
        return null;
    }
}
