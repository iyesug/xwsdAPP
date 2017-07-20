package com.xwsd.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import butterknife.Bind;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.adapter.BaseAdapterHelper;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.ProtDetilabean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.view.EmptyLayout;
import com.xwsd.app.view.NavbarManage;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

public class ProtDetilaActivity extends BaseActivity {

    /**
     * 导航栏
     */
    private NavbarManage navbarManage;
    /**
     * 列表
     */
    @Bind(R.id.list_view)
    ListView list_view;

    private EmptyLayout mErrorLayout;

    QuickAdapter adapter;
    RequestCall call;
    private String oddNumber;

    private ProtDetilabean mprot_DetilaBean;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_prot_detila);
        navbarManage = new NavbarManage(this);
        if (mErrorLayout == null) {
            mErrorLayout = (EmptyLayout) findViewById(R.id.error_layout);
        }

    }
    @Override
    protected void init(Bundle savedInstanceState) {

        title="明细";
        navbarManage.setCentreStr("明细");
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
        oddNumber = getIntent().getStringExtra("oddMoneyId");
        getData();
    }
    private void getData() {
        //判断是否登陆
        if (AppContext.getUserBean() == null||null==AppContext.getUserBean().data) {
            Intent intent = new Intent(this, UserActivity.class);
            intent.putExtra(UserParam.TYPE, UserActivity.TYPE_LOGIN);
            intent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
            startActivity(intent);
            return;
        }
        ApiHttpClient.getDetilaInfo(AppContext.getUserBean().data.userId, oddNumber, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("详情:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        mprot_DetilaBean = GsonUtils.jsonToBean(response, ProtDetilabean.class);
                        setData();
                    } else {
                        mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }
            }
        });
    }

    private void setData() {

        if (mprot_DetilaBean.data.records == null || mprot_DetilaBean.data.records.size() <= 0) {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
        }
        if (adapter == null) {
            adapter = new QuickAdapter<ProtDetilabean.Data.PreInfo>(this, R.layout.item_detial, mprot_DetilaBean.data.records) {
                @Override
                protected void convert(BaseAdapterHelper helper, ProtDetilabean.Data.PreInfo item) {
                    helper.setText(R.id.qishu, item.qishu);
                    helper.setText(R.id.huankuangTime, item.endtime);
                    helper.setText(R.id.Ybenxi, item.amount);
                    helper.setText(R.id.Sbenxi, item.realMoney);
                    if (item.status.equals("0") || item.status.equals("2")) {
                        helper.getView(R.id.ishuankuan).setVisibility(View.GONE);
                    }else {
                        helper.getView(R.id.ishuankuan).setVisibility(View.VISIBLE);
                    }
                }
            };
            list_view.setAdapter(adapter);
        } else {
            adapter.replaceAll(mprot_DetilaBean.data.records);
        }
    }
}
