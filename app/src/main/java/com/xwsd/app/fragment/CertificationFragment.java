package com.xwsd.app.fragment;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.activity.BankCardActivity;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseFragment;
import com.xwsd.app.tools.TLog;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by Gx on 2016/8/29.
 * 实名认证
 */
public class CertificationFragment extends BaseFragment {

    @Bind(R.id.et_name)
    EditText et_name;

    @Bind(R.id.et_identity)
    EditText et_identity;

    @Bind(R.id.et_state)
    EditText et_state;

    @Bind(R.id.commit)
    Button commit;

    RequestCall call;

    @Override
    protected View setContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_name_certification, null);
        return view;
    }

    @Override
    protected void init() {
//        判断是否进行了实名认证
        if (AppContext.getUserBean().data.cardstatus.equals(ApiHttpClient.YES)) {
            commit.setVisibility(View.GONE);
            et_name.setEnabled(false);
            et_name.setText(AppContext.getUserBean().data.name);
            et_identity.setEnabled(false);
            et_identity.setText(AppContext.getUserBean().data.cardnum.replace(AppContext.getUserBean().data.cardnum.substring(3, 13), "**********"));
            et_state.setText("已认证");
        } else {
            commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    判断输入是否为空
                    if (TextUtils.isEmpty(et_name.getText().toString().trim())) {
                        AppContext.showToastShort(getString(R.string.name_null));
                        return;
                    }

                    if (TextUtils.isEmpty(et_identity.getText().toString().trim())) {
                        AppContext.showToastShort(getString(R.string.identity_null));
                        return;
                    }

//                    开启对话框
                    ((BankCardActivity) getActivity()).showWaitDialog(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            if (call != null) {
                                call.cancel();
                            }
                        }
                    });

                    call = ApiHttpClient.certification(
                            AppContext.getUserBean().data.userId,
                            et_name.getText().toString().trim(),
                            et_identity.getText().toString().trim(),
                            new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                    ((BankCardActivity) getActivity()).hideWaitDialog();
                                    AppContext.showToastShort(getString(R.string.network_exception));
                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    TLog.error("实名认证:" + response);
                                    ((BankCardActivity) getActivity()).hideWaitDialog();
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        AppContext.showToastShort(jsonObject.getString("msg"));
                                        if (jsonObject.getInt("status") == 1) {
                                            AppContext.getUserBean().data.cardstatus = ApiHttpClient.YES;
                                            et_name.setEnabled(false);
                                            et_identity.setEnabled(false);
                                            et_state.setText("已认证");
                                            commit.setVisibility(View.GONE);
                                        } else {

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        AppContext.showToastShort(getString(R.string.network_exception));
                                    }
                                }
                            });
                }
            });
        }
    }
}


