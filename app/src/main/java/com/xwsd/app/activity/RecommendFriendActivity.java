package com.xwsd.app.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import butterknife.Bind;
import butterknife.OnClick;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.adapter.BaseAdapterHelper;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.FriendsBean;
import com.xwsd.app.bean.FundRecordBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.NetWorkUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.view.EDialog;
import com.xwsd.app.view.NavbarManage;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gx on 2016/9/23.
 * 推荐好友
 */
public class RecommendFriendActivity extends BaseActivity implements View.OnClickListener,AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener {

    /**
     * 导航栏
     */
    private NavbarManage navbarManage;


    private FriendsBean mFriendsBean;

    private String  spreadMoney;
    private String lastSpreadMoney;
    /**
     * 加载
     */
    public static final int LOAD = 0;

    /**
     * 加载成功
     */
    public static final int SUCCEED = 1;

    /**
     * 加载错误
     */
    public static final int LOAD_ERROR = 2;

    /**
     * 网络错误
     */
    public static final int NETWORK_ERROR = 3;

    /**
     * 没有更多
     */
    public static final int NO_MORE = 4;
    /**
     * 当前页数
     */
    protected int currentPages = 1;

    /**
     * 总页数
     */
    protected int allItemCount = 1;

    /**
     * 每页条目数
     */
    protected int each_page_num = 10;
    /**
     * 是否正在加载
     */
    private boolean isLaod = false;
    /**
     * 上拉加载布局延迟消失时间
     */
    private long DELYED = 2000;
    /**
     * 列表
     */
    @Bind(R.id.list_view)
    ListView list_view;

    QuickAdapter adapter;
    RequestCall call;

    SwipeRefreshLayout swipe_refresh_layout;
    private ProgressBar progressbar;
    /**
     * 底部显示正在加载的页面
     */
    private View footerView = null;
    private FundRecordBean mFundRecordBean;
    private TextView text;
    /**
     * 错误布局
     */
//    @Bind(R.id.error_layout)
//    EmptyLayout error_layout;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_recommend_friend);
        navbarManage = new NavbarManage(this);
 //       showDialog();
        getData(1);
    }

    private void showDialog(){
        new EDialog(RecommendFriendActivity.this, R.style.EDialog,lastSpreadMoney,
                new EDialog.DialogClickListener() {
                    @Override
                    public void sure(Dialog dialog,String pass) {
                        sumit(pass);
                        dialog.dismiss();
                    }

                    @Override
                    public void cancle(Dialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }
    private void initRefresh() {
        if(swipe_refresh_layout == null){
            swipe_refresh_layout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        }
        swipe_refresh_layout.setOnRefreshListener(this);
        swipe_refresh_layout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);

        if (footerView == null) {
            footerView = LayoutInflater.from(this).inflate(R.layout.layout_cell_footer, null);
            progressbar = (ProgressBar) footerView.findViewById(R.id.progressbar);
            text = (TextView) footerView.findViewById(R.id.text);
        }
        if (list_view == null) {
            list_view = (ListView)findViewById(R.id.list_view);
        }
        list_view.addFooterView(footerView);
        footerView.setVisibility(View.GONE);
        list_view.setOnScrollListener(this);
    }

    private  void getData(final int type) {
        //请求首页数据
    //    error_layout.setErrorType(EmptyLayout.NETWORK_LOADING);
        ApiHttpClient.frined(AppContext.getUserBean().data.userId,currentPages +"",each_page_num+"",new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
      //          error_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("推荐奖励:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
        //                error_layout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        mFriendsBean = GsonUtils.jsonToBean(response, FriendsBean.class);
                        spreadMoney = mFriendsBean.data.spreadMoney;
                        lastSpreadMoney = mFriendsBean.data.lastSpreadMoney;
                        allItemCount = (mFriendsBean.data.count);
                        setData(type);
                    } else if (jsonObject.getInt("status") == 88){
                        AppContext.showToast("用户密码已修改，请重新登录");
                        Intent Fintent = new Intent();
                        Fintent.putExtra(UserParam.TYPE, 0);
                        Fintent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                        startActivity(Fintent);
                        finish();
                    }else {
           //             error_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
            //        error_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }
            }
        });
    }
    private void setData(int type){
        //设置推荐列表
        if (adapter == null) {
            adapter = new QuickAdapter<FriendsBean.Data.records>(this, R.layout.item_friend, mFriendsBean.data.records) {
                @Override
                protected void convert(BaseAdapterHelper helper, FriendsBean.Data.records item) {
                    helper.setText(R.id.name, item.username);
                    helper.setText(R.id.time, item.time);
                    helper.setText(R.id.jiangli, item.money);
                    helper.setText(R.id.jibie, item.level + "级");

                    //判断是否是新手标
                }
            };

            list_view.setAdapter(adapter);
            list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
        } else {
            if(type == 0){
                adapter.addAll(mFriendsBean.data.records);
                setPullUpState(SUCCEED);
            }else{
                adapter.replaceAll(mFriendsBean.data.records);
            }
        }
    }

    private void sumit(String pass){
        ApiHttpClient.getTuijian(AppContext.getUserBean().data.userId,pass,new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                //          error_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("提取推荐奖励:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        //                error_layout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        AppContext.showToast("提取成功");
                        getData(1);
                    } else if (jsonObject.getInt("status") == 88){
                        AppContext.showToast("用户密码已修改，请重新登录");
                        Intent Fintent = new Intent();
                        Fintent.putExtra(UserParam.TYPE, 0);
                        Fintent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                        startActivity(Fintent);
                        finish();
                    }else {
                        AppContext.showToast("提取失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //        error_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }
            }
        });
    }
    @Override
    protected void init(Bundle savedInstanceState) {
        //设置导航栏
        navbarManage.setCentreStr("推荐奖励");
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
        initRefresh();
    }

    @OnClick({R.id.commit,R.id.get})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit:
                showWaitDialog(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (call != null) {
                            call.cancel();
                        }
                    }
                });
                call = ApiHttpClient.spread(AppContext.getUserBean().data.userId,
                        new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                hideWaitDialog();
                                AppContext.showToastShort(getString(R.string.network_exception));
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                TLog.error("获取推广链接:" + response);
                                hideWaitDialog();
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getInt("status") == 1) {

//                                        //将文本内容放到系统剪贴板里。
//                                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                                        cm.setText(jsonObject.getJSONObject("data").getString("link"));
//                                        AppContext.showToastShort(getString(R.string.clipboard_succeed));

                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                        intent.setType("text/plain");
                                        intent.putExtra(Intent.EXTRA_SUBJECT, "小微时贷推广");
                                        intent.putExtra(Intent.EXTRA_TEXT, "分享小微时贷：" + jsonObject.getJSONObject("data").getString("link"));
                                        intent.putExtra(Intent.EXTRA_TITLE, "小伙伴们快来围观啦~");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(Intent.createChooser(intent, "请选择"));

                                    } else if (jsonObject.getInt("status") == 88){
                                        AppContext.showToast("用户密码已修改，请重新登录");
                                        Intent Fintent = new Intent();
                                        Fintent.putExtra(UserParam.TYPE, 0);
                                        Fintent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                                        startActivity(Fintent);
                                        finish();
                                    }else {
                                        AppContext.showToastShort(jsonObject.getString("msg"));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    AppContext.showToastShort(getString(R.string.network_exception));
                                }
                            }
                        }
                );

                break;
            case R.id.get:
                BuriedPointUtil.buriedPoint("提取推荐奖励按键");
                showDialog();
        }
    }

    @Override
    public void onRefresh() {
        swipe_refresh_layout.setRefreshing(false);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            // 当不滚动时

            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                // 判断滚动到底部，是否正在加载,是否正在下拉刷新
                if (view.getLastVisiblePosition() == (view.getCount() - 1) && !isLaod && !swipe_refresh_layout.isRefreshing()) {
                    //判断是否有网络
                    if (NetWorkUtils.isNetworkConnected(this)) {
                        if (currentPages <= ((allItemCount -1)/each_page_num)) {
                            currentPages++;
                            setPullUpState(LOAD);
                            getData(0);
                        } else {
                            setPullUpState(NO_MORE);
                        }

                    } else {
                        setPullUpState(NETWORK_ERROR);
                    }
                }
                break;
        }
    }
    public void setPullUpState(int state) {
//        显示上拉布局
        footerView.setVisibility(View.VISIBLE);
        int padding = (int) getResources().getDimension(R.dimen.DIMEN_15);
        footerView.setPadding(padding, padding, padding, padding);
        switch (state) {
            case LOAD:
                isLaod = true;
                progressbar.setVisibility(View.VISIBLE);
                text.setText(getString(R.string.load_more));
                break;
            case SUCCEED:
                footerView.setVisibility(View.GONE);
                footerView.setPadding(0, -footerView.getHeight(), 0, 0);
                isLaod = false;
                break;
            case LOAD_ERROR:
                progressbar.setVisibility(View.GONE);
                text.setText(getString(R.string.load_error));
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, DELYED);
                break;
            case NETWORK_ERROR:
                progressbar.setVisibility(View.GONE);
                text.setText(getString(R.string.network_exception));
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, DELYED);
                break;
            case NO_MORE:
                progressbar.setVisibility(View.GONE);
                text.setText(getString(R.string.no_more));
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, DELYED);
                break;
        }

//        滚动到底部
        list_view.setSelection(list_view.getBottom());
    }
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            footerView.setVisibility(View.GONE);
            footerView.setPadding(0, -footerView.getHeight(), 0, 0);
            isLaod = false;
        }
    };
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
