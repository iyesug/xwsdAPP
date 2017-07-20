package com.xwsd.app.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.base.BaseFragment;
import com.xwsd.app.bean.BankCardBean;
import com.zhy.http.okhttp.request.RequestCall;

import java.text.DecimalFormat;

/**
 * Created by Gy on 2017/6/27.
 * 支付宝支付
 */
public class BankPaymentFragment extends BaseFragment implements View.OnClickListener {


    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.custodyId)
    TextView custodyId;
    public static boolean needRefresh = false;
    BankCardBean bankCardBean;
    DecimalFormat decimalFormat = new DecimalFormat("0.0");

    RequestCall call;


    @Override
    protected View setContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_bank_payment, null);
        return view;
    }

    @Override
    protected void init() {
        name.setText(AppContext.getUserBean().data.name);
        custodyId.setText(AppContext.getUserBean().data.custodyId);


    }

    @Override
    public void onClick(View v) {

    }


//
//    @OnClick({ll_add_bank_card, tv_list, R.id.commit})
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case ll_add_bank_card://增加银行卡
//
//
//                break;
//            case tv_list://银行列表
//
//
//                break;
//            case R.id.commit://提交
//
//
//                break;
//
//        }
//
//    }


}