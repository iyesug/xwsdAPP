package com.xwsd.app.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.Bind;
import com.xwsd.app.AppContext;
import com.xwsd.app.AppManager;
import com.xwsd.app.R;
import com.xwsd.app.adapter.BaseAdapterHelper;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.BankListBean;
import com.xwsd.app.constant.UserParam;
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
 * Created by Gy on 2017/6/29.
 * 银行列表
 */
public class BankListActivity extends BaseActivity {
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;


//    @Bind(R.id.load_progress_bar)
//    ProgressBar load_progress_bar;

    /**
     * 列表
     */
    @Bind(R.id.list_view)
    ListView list_view;

    QuickAdapter adapter;
    RequestCall call;

    @Bind(R.id.error_layout)
    EmptyLayout mErrorLayout;

//    @Bind(R.id.tv_title)
//    TextView tv_title;
//
//    @Bind(R.id.tv_name)
//    TextView tv_name;
//
//    @Bind(R.id.tv_time)
//    TextView tv_time;

    String id;
    private BankListBean data;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_bank_list);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        title=getString(R.string.choose_bank);
        //设置导航栏
        navbarManage.showLeft(true);
        navbarManage.showRight(false);
        navbarManage.setLeftImg(R.mipmap.ic_back_b);
        navbarManage.setBackground(R.color.navbar_bg);
        navbarManage.setCentreStr(getString(R.string.choose_bank));
        navbarManage.setOnLeftClickListener(new NavbarManage.OnLeftClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }
        });

//        initProgressBar();

        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                getData(id);
            }
        });

        //得到ID
        id = getIntent().getStringExtra(UserParam.DATA);
        getData(id);
    }

    /**
     * 得到详情数据
     */
    private void getData(String id) {
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        ApiHttpClient.cardLimit(AppContext.getUserBean().data.userId,
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        TLog.error("获取资讯内容:" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("status") == 1) {
                                mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                                data = GsonUtils.jsonToBean(response, BankListBean.class);
                                setData();

                            } else {
                                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                                ToastUtil.showToast(jsonObject.getString("msg"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                        }
                    }
                }
        );
    }


    private void setData() {
        if (data.data.limitList == null || data.data.limitList.size() <= 0) {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
//            ToastUtil.showToast("您当前没有抵扣红包");

        }
        if (adapter == null) {
            adapter = new QuickAdapter<BankListBean.Data.limitList>(this, R.layout.item_bank, data.data.limitList) {
                @Override
                protected void convert(BaseAdapterHelper helper, BankListBean.Data.limitList item) {
                    helper.setText(R.id.bank_name, item.bankName);
                    helper.setText(R.id.limit, item.limit);
                    helper.setImageUrl(BankListActivity.this,R.id.bank_icon,item.bankIcon);

                }
            };
            list_view.setAdapter(adapter);
            list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    EventBus.getDefault().post(new MyEvent(data.data.limitList.get(position)));
                    if("payment".equals(getIntent().getStringExtra(UserParam.TITLE))){
                        return;

                    }else{
                        AppManager.getAppManager().finishActivity();
                    }

                }
            });
        } else {
            adapter.replaceAll(data.data.limitList);
        }
    }

//    /**
//     * 初始化顶部进度条
//     */
//    private void initProgressBar() {
//        web_view.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public void onProgressChanged(WebView view, int newProgress) {
//                if (load_progress_bar.getVisibility() == View.GONE) {
//                    load_progress_bar.setVisibility(View.VISIBLE);
//                }
//                if (newProgress == 100) {
//                    load_progress_bar.setVisibility(View.GONE);
//                } else {
//                    if (View.INVISIBLE == load_progress_bar.getVisibility()) {
//                        load_progress_bar.setVisibility(View.VISIBLE);
//                    }
//                    load_progress_bar.setProgress(newProgress);
//                }
//                super.onProgressChanged(view, newProgress);
//            }
//        });
//    }
}