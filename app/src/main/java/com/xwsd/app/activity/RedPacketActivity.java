package com.xwsd.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import butterknife.Bind;
import butterknife.OnClick;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.adapter.BaseAdapterHelper;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.prot_jiaBean;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.EmptyLayout;
import com.xwsd.app.view.NavbarManage;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gy on 2017/6/16.
 * 红包
 */
public class RedPacketActivity extends BaseActivity implements View.OnClickListener {
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

    private static LinearLayout mVIew;
    private prot_jiaBean mprot_jiaBean;
    private static String id;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_red_packet);
        navbarManage = new NavbarManage(this);
        if (mErrorLayout == null) {
            mErrorLayout = (EmptyLayout) findViewById(R.id.error_layout);
        }
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        title=getString(R.string.my_red_packet);
        navbarManage.setCentreStr(getResources().getString(R.string.my_red_packet));
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
    //oddNumber
    private void getData() {
        call = ApiHttpClient.getjiaxiTicket(AppContext.getUserBean().data.userId,oddNumber, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }
            @Override
            public void onResponse(String response, int id) {
                TLog.error("加息券:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        mprot_jiaBean = GsonUtils.jsonToBean(response, prot_jiaBean.class);
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
        if (mprot_jiaBean.data.records == null || mprot_jiaBean.data.records.size() <= 0) {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
        }
        if (adapter == null) {
            adapter = new QuickAdapter<prot_jiaBean.Data.PreInfo>(this, R.layout.item_jiaxi, mprot_jiaBean.data.records) {
                @Override
                protected void convert(BaseAdapterHelper helper, prot_jiaBean.Data.PreInfo item) {
                    helper.setText(R.id.jiaxi_num, item.name.split("\\%")[0] + "%");
                    helper.setText(R.id.jiaxi_time, item.endtime.split(" ")[0]);
                    helper.setText(R.id.use_type, item.type);
                    helper.setText(R.id.use_money, item.money);
                }
            };
            list_view.setAdapter(adapter);
            list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    RedPacketActivity.id = mprot_jiaBean.data.records.get(position).id + "";
                    LinearLayout mView = (LinearLayout)view.findViewById(R.id.mview);
                    if(mVIew!=null){
                        mVIew.setBackgroundResource(R.mipmap.jiaxiquan);
                    }
                    mView.setBackgroundResource(R.mipmap.hongkuang);
                    RedPacketActivity.mVIew = mView;
                    adapter.notifyDataSetChanged();
                }
            });
        } else {
            adapter.replaceAll(mprot_jiaBean.data.records);
        }
    }

    private void uesTicket(String usejiaxiTicket) {
        if (call != null) {
            call.cancel();
        }
        call = ApiHttpClient.usejiaxiTicket(AppContext.getUserBean().data.userId, usejiaxiTicket, oddNumber, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                System.out.println("Exception = " + e);
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("使用加息券:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        ToastUtil.showToast("加息成功");
                        finish();
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

    @OnClick({R.id.sure, R.id.cancel})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sure://使用
                if(id != null && id.length() > 0){
                    uesTicket(id);
                }else {
                    ToastUtil.showToast("请选择加息券");
                }
                break;
            case R.id.cancel://取消
                finish();
                break;
        }
    }
}
