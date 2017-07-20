package com.xwsd.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import butterknife.Bind;
import butterknife.OnClick;
import com.xwsd.app.AppContext;
import com.xwsd.app.AppManager;
import com.xwsd.app.R;
import com.xwsd.app.adapter.BaseAdapterHelper;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.RedpackageBean;
import com.xwsd.app.event.MyEvent;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.EmptyLayout;
import com.xwsd.app.view.NavbarManage;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import okhttp3.Call;
import org.greenrobot.eventbus.EventBus;
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
    private RedpackageBean mprot_jiaBean;
    private static RedpackageBean.Data.records chooseRedpackage;

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

        call = ApiHttpClient.moneyLotteries(AppContext.getUserBean().data.userId, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                TLog.error("抵扣红包:" + e);
            }
            @Override
            public void onResponse(String response, int id) {
                TLog.error("抵扣红包:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        mprot_jiaBean = GsonUtils.jsonToBean(response, RedpackageBean.class);
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
            ToastUtil.showToast("您当前没有抵扣红包");

        }
        if (adapter == null) {
            adapter = new QuickAdapter<RedpackageBean.Data.records>(this, R.layout.item_hongbao, mprot_jiaBean.data.records) {
                @Override
                protected void convert(BaseAdapterHelper helper, RedpackageBean.Data.records item) {
                    helper.setText(R.id.money_rate, "￥"+item.money_rate);
                    helper.setText(R.id.name, item.name);

                    //有效期
                    helper.setText(R.id.endtime, item.endtime);

                    //设置金额
                    String low=item.money_lower;
                    String up=item.money_uper;
                    if(item.money_lower==null){
                        low="0";
                    }
                    if(item.money_uper==null){
                        up="无限";
                    }
                    helper.setText(R.id.money_lower, low+" - "+up);

                    //设置使用月标
                    if(item.period_lower==null&&item.period_uper==null){
                        helper.setText(R.id.period, "无限制");
                    }else if(item.period_lower!=null&&item.period_uper==null){
                        helper.setText(R.id.period, "大于"+item.period_lower);
                    }else if(item.period_lower==null&&item.period_uper!=null){
                        helper.setText(R.id.period, "小于"+item.period_uper);
                    }else{
                        helper.setText(R.id.period, "大于"+item.period_lower+"且小于"+item.period_uper);
                    }

                }
            };
            list_view.setAdapter(adapter);
            list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    RedPacketActivity.chooseRedpackage = mprot_jiaBean.data.records.get(position);
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


    @OnClick({R.id.sure, R.id.cancel})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sure://使用
                if(chooseRedpackage != null ){
//                    uesTicket(id);

                    EventBus.getDefault().post(new MyEvent(chooseRedpackage));
                    AppManager.getAppManager().finishActivity();
                }else {
                    ToastUtil.showToast("请选择抵扣红包");
                }
                break;
            case R.id.cancel://取消
                EventBus.getDefault().post(new MyEvent(null));
                finish();
                break;
        }
    }
}
