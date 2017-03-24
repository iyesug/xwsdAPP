package com.xwsd.app.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.activity.RechargeActivity;
import com.xwsd.app.adapter.BaseAdapterHelper;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.bean.AgreeCardBean;
import com.xwsd.app.bean.BanksLimitBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.tools.ImgUtil;
import com.xwsd.app.tools.PatternUtils;
import com.xwsd.app.view.WheelView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by admin on 2016/12/1.
 */

public class BaofooFragment extends Fragment implements View.OnClickListener {

    GridView grid_view;
    TextView tv_name;
    TextView tv_identity;
    EditText tv_bank_card;
    TextView tv_open_bank;

    Button commit;
    Dialog dialog;

    ImageView iv_bank;

    TextView tv_once_limit;

    TextView tv_day_limit;

    LinearLayout ll_open_bank;
    MaterialDialog bankDialog;

    String bankName;

    String bankCode;

    String bankId;

    private List<BanksLimitBean> banksLimitBeens;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_c_baofoo, container, false);
        grid_view = (GridView) view.findViewById(R.id.grid_view);
        tv_name= (TextView) view.findViewById(R.id.tv_name);
        tv_identity= (TextView) view.findViewById(R.id.tv_identity);
        tv_bank_card= (EditText) view.findViewById(R.id.tv_bank_card);
        tv_open_bank= (TextView) view.findViewById(R.id.tv_open_bank);
        ll_open_bank = (LinearLayout) view.findViewById(R.id.ll_open_bank);
        ll_open_bank.setOnClickListener(this);
        commit = (Button) view.findViewById(R.id.commit);
        commit.setOnClickListener(this);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init(){

//        设置用户数据
        tv_name.setText(AppContext.getUserBean().data.name);
        if(AppContext.getUserBean().data.cardnum!= null && AppContext.getUserBean().data.cardnum.length()>0){
            tv_identity.setText(AppContext.getUserBean().data.cardnum.replace(AppContext.getUserBean().data.cardnum.substring(3, 13), "**********"));
        }

//        设置银行卡列表
        banksLimitBeens = getBanksLimits();
        grid_view.setAdapter(new QuickAdapter<BanksLimitBean>(getActivity(), R.layout.item_pay_bank_img, banksLimitBeens) {
            @Override
            protected void convert(BaseAdapterHelper helper, BanksLimitBean item) {
                helper.setImageResource(R.id.iv_bank, ImgUtil.getResource(getActivity(), item.imgName));
            }
        });
        grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showBankDialog(position);
                BuriedPointUtil.buriedPoint("充值页面-银行限额查询");
            }
        });
    }


    @OnClick({R.id.commit, R.id.ll_open_bank,R.id.tv_bank_card})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:
//                判断输入时候完整

                if (TextUtils.isEmpty(tv_bank_card.getText().toString().trim())) {
                    AppContext.showToastShort(getString(R.string.card_num_null));
                    return;
                }

                if (!PatternUtils.matchesNum(tv_bank_card.getText().toString().trim(), 16, 19)) {
                    AppContext.showToastShort(getString(R.string.card_num_length));
                    return;
                }

                if (TextUtils.isEmpty(bankName)) {
                    AppContext.showToastShort(getString(R.string.banks_null));
                    return;
                }

                Intent intent = new Intent(getActivity(), RechargeActivity.class);
                AgreeCardBean.Data.AgreeCard data = new AgreeCardBean.Data.AgreeCard();
                data.bank_code = bankCode;
                data.bank_name = bankName;
                data.card_num = tv_bank_card.getText().toString().trim();
                intent.putExtra(UserParam.DATA, data);
                startActivity(intent);

                fuyouMoneyFragment.dayRechargemoney = 0;//换银行卡后,每个卡当日充值的金额从零开始

                break;
            case R.id.ll_open_bank:
                showBankDialog();
                BuriedPointUtil.buriedPoint("设置充值银行卡页面-选择银行");
                break;
            case R.id.tv_bank_card:
                BuriedPointUtil.buriedPoint("设置充值银行卡页面-写入银行卡号");
                break;
        }
    }

    /**
     * 弹出银行选择对话框
     */
    private void showBankDialog() {
//        隐藏软键盘
        hideSoftKeyboard(getActivity().getCurrentFocus());
        if (bankDialog == null) {
            View outerView = LayoutInflater.from(getActivity()).inflate(R.layout.view_wheel, null);
            final WheelView wv = (WheelView) outerView.findViewById(R.id.wheel_view_wv);
            List<String> strings = new ArrayList();
            for (BanksLimitBean records : banksLimitBeens) {
                strings.add(records.bankName);
            }
            wv.setOffset(2);
            wv.setItems(strings);
            wv.setSeletion(0);
            bankDialog = new MaterialDialog(getActivity()).setTitle(R.string.banks_select)
                    .setContentView(outerView)
                    .setPositiveButton(getString(R.string.confirm),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    bankDialog.dismiss();

                                    bankName = banksLimitBeens.get(wv.getSeletedIndex()).bankName;
                                    bankCode = banksLimitBeens.get(wv.getSeletedIndex()).bankCode;
                                    bankId = banksLimitBeens.get(wv.getSeletedIndex()).bankId;

                                    tv_open_bank.setText(wv.getSeletedItem());
                                }
                            })
                    .setCanceledOnTouchOutside(true);
        }
        bankDialog.show();
    }


    /**
     * 隐藏软键盘
     *
     * @param view
     */
    public void hideSoftKeyboard(View view) {
        if (view == null)
            return;
        ((InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                view.getWindowToken(), 0);
    }
    /**
     * 显示银行详细信息对话框
     *
     * @param position
     */
    private void showBankDialog(int position) {
        if (dialog == null) {
            dialog = new Dialog(getActivity(), R.style.BankDialog);
            View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_bank_info, null);
            iv_bank = (ImageView) view.findViewById(R.id.iv_bank);
            tv_once_limit = (TextView) view.findViewById(R.id.tv_once_limit);
            tv_day_limit = (TextView) view.findViewById(R.id.tv_day_limit);
            view.findViewById(R.id.commit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            Window window = dialog.getWindow();
//        可以在此设置显示动画
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.x = 0;
            wl.y = getActivity().getWindowManager().getDefaultDisplay().getHeight();
//        以下这两句是为了保证按钮可以水平满屏
            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        设置显示位置
            dialog.onWindowAttributesChanged(wl);
        }
        iv_bank.setImageResource(ImgUtil.getResource(getActivity(), banksLimitBeens.get(position).imgName));
        tv_once_limit.setText(banksLimitBeens.get(position).onceLimit);
        tv_day_limit.setText(banksLimitBeens.get(position).dayLimit);
        dialog.show();
    }
    /**
     * 获得所有支持的银行列表
     *
     * @return
     */
    private List<BanksLimitBean> getBanksLimits() {
        List<BanksLimitBean> banksLimitBeens = new ArrayList<>();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            //设置输入的内容
            xmlPullParser.setInput(getActivity().getAssets().open("BankBaoLimit.xml"), "UTF-8");
            //获取当前解析事件，返回的是数字
            int eventType = xmlPullParser.getEventType();
            //保存内容
            String bankName = "";
            String dayLimit = "";
            String onceLimit = "";
            String imgName = "";
            String bankCode = "";
            String bankId = "";

            while (eventType != (XmlPullParser.END_DOCUMENT)) {
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    //开始解析XML
                    case XmlPullParser.START_TAG: {
                        //nextText()用于获取结点内的具体内容
                        if ("BankName".equals(nodeName))
                            bankName = xmlPullParser.nextText();
                        else if ("DayLimit".equals(nodeName))
                            dayLimit = xmlPullParser.nextText();
                        else if ("OnceLimit".equals(nodeName))
                            onceLimit = xmlPullParser.nextText();
                        else if ("ImgName".equals(nodeName))
                            imgName = xmlPullParser.nextText();
                        else if ("BankCode".equals(nodeName))
                            bankCode = xmlPullParser.nextText();
                        else if ("BankId".equals(nodeName))
                            bankId = xmlPullParser.nextText();
                    }
                    break;
                    //结束解析
                    case XmlPullParser.END_TAG: {
                        if ("bank".equals(nodeName)) {
                            BanksLimitBean banksLimitBean = new BanksLimitBean();
                            banksLimitBean.bankName = bankName;
                            banksLimitBean.dayLimit = dayLimit;
                            banksLimitBean.onceLimit = onceLimit;
                            banksLimitBean.imgName = imgName;
                            banksLimitBean.bankCode = bankCode;
                            banksLimitBean.bankId = bankId;
                            banksLimitBeens.add(banksLimitBean);
                        }
                    }
                    break;
                    default:
                        break;
                }
                //下一个
                eventType = xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return banksLimitBeens;
    }
}