package com.xwsd.app.view;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.gnwai.loadingview.DialogControl;
import com.gnwai.loadingview.LoadDialog;
import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.bean.calculateBean;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;


/*
* 投资计算器
*
* */
public class DemoPopupWindow extends BottomPushPopupWindow<Void> implements DialogControl {

    private boolean isVisible = true;
    private LoadDialog zProgressHUD;
    private RequestCall call;
    int period =1;
    int type=2;
    public DemoPopupWindow(Context context) {
        super(context, null);
    }

    @Override
    protected View generateCustomView(Void data) {
        View root = View.inflate(context, R.layout.popup_demo, null);
//        final Spinner spinner = (Spinner)root.findViewById(R.id.spinner);

        final TextView pupo_money = (TextView)root.findViewById(R.id.pupo_money);
        final EditText toubiaoMoney = (EditText)root.findViewById(R.id.toubiaoMoney);
        final EditText nianhualv = (EditText)root.findViewById(R.id.nianhualv);
        final EditText qixian = (EditText)root.findViewById(R.id.qixian);

        final Button yue = (Button)root.findViewById(R.id.yue);
        final Button zhou = (Button)root.findViewById(R.id.zhou);
        final Button type1 = (Button)root.findViewById(R.id.type1);
        final Button type2 = (Button)root.findViewById(R.id.type2);
        final Button type3 = (Button)root.findViewById(R.id.type3);
        yue.setSelected(true);
        yue.setTextColor(context.getResources().getColor(R.color.tv_white));
        type2.setSelected(true);
        type2.setTextColor(context.getResources().getColor(R.color.tv_white));

        //按月
        yue.setOnClickListener(v -> {
            period =1;
            yue.setSelected(true);
            yue.setTextColor(context.getResources().getColor(R.color.tv_white));
            zhou.setSelected(false);
            zhou.setTextColor(context.getResources().getColor(R.color.black));


        });
        //按周
        zhou.setOnClickListener(v -> {
            period =2;
            zhou.setSelected(true);
            zhou.setTextColor(context.getResources().getColor(R.color.tv_white));
            yue.setSelected(false);
            yue.setTextColor(context.getResources().getColor(R.color.black));


        });

        //先息后本
        type1.setOnClickListener(v -> {
            type =1;
            type1.setSelected(true);
            type1.setTextColor(context.getResources().getColor(R.color.tv_white));

            type2.setSelected(false);
            type2.setTextColor(context.getResources().getColor(R.color.black));
            type3.setSelected(false);
            type3.setTextColor(context.getResources().getColor(R.color.black));
        });
        //等额本息
        type2.setOnClickListener(v -> {
            type =2;
            type1.setSelected(false);
            type1.setTextColor(context.getResources().getColor(R.color.black));
            type2.setSelected(true);
            type2.setTextColor(context.getResources().getColor(R.color.tv_white));

            type3.setSelected(false);
            type3.setTextColor(context.getResources().getColor(R.color.black));
        });
        //等额本金
        type3.setOnClickListener(v -> {
            type =3;
            type1.setSelected(false);
            type1.setTextColor(context.getResources().getColor(R.color.black));
            type2.setSelected(false);
            type2.setTextColor(context.getResources().getColor(R.color.black));
            type3.setSelected(true);
            type3.setTextColor(context.getResources().getColor(R.color.tv_white));

        });

        View jisuan = root.findViewById(R.id.jisuan);
        jisuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mode = "";
                if(type==1){
                    mode = "matchpay";
                }else if(type==2){
                    mode = "monthpay";
                }else{
                    mode = "avg_c";
                }
                String periodString = "";
                if(period==1){
                    periodString = "month";
                }else{
                    periodString = "week";
                }

                if (TextUtils.isEmpty(nianhualv.getText().toString())){
                    ToastUtil.showToastShort("年化收益率不能为空");
                }
                else if (TextUtils.isEmpty(qixian.getText().toString())){
                    ToastUtil.showToastShort("项目期限不能为空");
                }
                else   if(TextUtils.isEmpty(toubiaoMoney.getText().toString())){
                ToastUtil.showToastShort("投资金额不能为空");}
                else {
                    showWaitDialog(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            if (call != null) {
                                call.cancel();
                            }
                        }
                    });
                    call = ApiHttpClient.calculate(mode, toubiaoMoney.getText().toString(), qixian.getText().toString(),periodString, "0."+nianhualv.getText().toString(), new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            hideWaitDialog();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            TLog.error("计算：" + response);
                            hideWaitDialog();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getInt("status") == 1) {
                                    calculateBean mcalculateBean = GsonUtils.jsonToBean(response, calculateBean.class);
                                    pupo_money.setText(mcalculateBean.data.interest);
                                } else {
                                    ToastUtil.showToastShort(jsonObject.getString("msg"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        }
                    });
                }
            }
        });

        View chongzhi = root.findViewById(R.id.chongzhi);
        chongzhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toubiaoMoney.setText("");
                nianhualv.setText("");
                qixian.setText("");
                pupo_money.setText("0.00");
            }
        });
        View cancelView = root.findViewById(R.id.guanbi);
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return root;
    }

    @Override
    public LoadDialog showWaitDialog(DialogInterface.OnCancelListener onCancelListener) {
        return showWaitDialog(R.string.loading, onCancelListener);
    }

    @Override
    public LoadDialog showWaitDialog(int resid, DialogInterface.OnCancelListener onCancelListener) {
        return showWaitDialog(context.getString(resid), onCancelListener);
    }

    @Override
    public LoadDialog showWaitDialog(String message, DialogInterface.OnCancelListener onCancelListener) {
        if (isVisible) {
            if (zProgressHUD == null) {
                zProgressHUD = new LoadDialog(context);
                zProgressHUD.setOnCancelListener(onCancelListener);
            }
            if (zProgressHUD != null) {
                zProgressHUD.setMessage(message);
                zProgressHUD.show();
            }
            return zProgressHUD;
        }
        return null;
    }

    @Override
    public void hideWaitDialog() {
        if (isVisible && zProgressHUD != null) {
            try {
                zProgressHUD.dismiss();
                zProgressHUD = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}