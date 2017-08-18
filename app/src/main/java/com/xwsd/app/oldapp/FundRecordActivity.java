package com.xwsd.app.oldapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Bind;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.activity.UserActivity;
import com.xwsd.app.adapter.BaseAdapterHelper;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.oldapp.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.FundRecordBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.*;
import com.xwsd.app.view.EmptyLayout;
import com.xwsd.app.view.NavbarManage;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

public class FundRecordActivity extends BaseActivity implements View.OnClickListener,AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener
{
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;
    RequestCall call;
    private ResideMenu resideMenu;
    private ResideMenuItem all ;
    private ResideMenuItem tuiguang;
    private ResideMenuItem zaiquan;
    private ResideMenuItem benjin;
    private ResideMenuItem lixi;
    private ResideMenuItem fuwu;
    private ResideMenuItem chongzhi;
    private ResideMenuItem tixian;
    private ResideMenuItem jine;
    private ResideMenuItem jiangli;
    private ResideMenuItem xunijin;
    private ResideMenuItem touzhi;
    private ResideMenuItem tiqu;
    private ResideMenuItem goumai;
    private ResideMenuItem zaiquanfuwu;
    private ResideMenuItem zaiquanlixi;

    private ResideMenuItem fine;
    private ResideMenuItem subsidy;
    private ResideMenuItem lottery;

    private TextView text;
    private ProgressBar progressbar;
    private EmptyLayout mErrorLayout;
    /**
     * 底部显示正在加载的页面
     */
    private View footerView = null;
    private FundRecordBean mFundRecordBean;
    /**
     * 列表
     */
    @Bind(R.id.list_view)
    ListView list_view;

    SwipeRefreshLayout swipe_refresh_layout;

    private String FLAG = "all";

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

    QuickAdapter adapter;
    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_fund_record);
        navbarManage = new NavbarManage(this);
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.mipmap.more_back);
        resideMenu.attachToActivity(this);
        resideMenu.setMenuListener(menuListener);
        resideMenu.setScaleValue(0.6f);
        all = new ResideMenuItem(this,R.mipmap.dianji,"全部");
        resideMenu.addMenuItem(all, ResideMenu.DIRECTION_RIGHT);
        tuiguang = new ResideMenuItem(this,R.mipmap.dianji,"推广提成");
        resideMenu.addMenuItem(tuiguang, ResideMenu.DIRECTION_RIGHT);
        zaiquan = new ResideMenuItem(this,R.mipmap.dianji,"债券买卖");
        resideMenu.addMenuItem(zaiquan, ResideMenu.DIRECTION_RIGHT);
        benjin = new ResideMenuItem(this,R.mipmap.dianji,"本金");
        resideMenu.addMenuItem(benjin, ResideMenu.DIRECTION_RIGHT);
        lixi = new ResideMenuItem(this,R.mipmap.dianji,"利息");
        resideMenu.addMenuItem(lixi, ResideMenu.DIRECTION_RIGHT);
        fuwu = new ResideMenuItem(this,R.mipmap.dianji,"服务费");
        resideMenu.addMenuItem(fuwu, ResideMenu.DIRECTION_RIGHT);
        chongzhi = new ResideMenuItem(this,R.mipmap.dianji,"充值");
        resideMenu.addMenuItem(chongzhi, ResideMenu.DIRECTION_RIGHT);
        tixian = new ResideMenuItem(this,R.mipmap.dianji,"提现");
        resideMenu.addMenuItem(tixian, ResideMenu.DIRECTION_RIGHT);
        jine = new ResideMenuItem(this,R.mipmap.dianji,"金额");
        resideMenu.addMenuItem(jine, ResideMenu.DIRECTION_RIGHT);
        jiangli = new ResideMenuItem(this,R.mipmap.dianji,"奖励");
        resideMenu.addMenuItem(jiangli, ResideMenu.DIRECTION_RIGHT);
        xunijin = new ResideMenuItem(this,R.mipmap.dianji,"虚拟金");
        resideMenu.addMenuItem(xunijin, ResideMenu.DIRECTION_RIGHT);
        touzhi = new ResideMenuItem(this,R.mipmap.dianji,"投资");
        resideMenu.addMenuItem(touzhi, ResideMenu.DIRECTION_RIGHT);
        tiqu = new ResideMenuItem(this,R.mipmap.dianji,"提取");
        resideMenu.addMenuItem(tiqu, ResideMenu.DIRECTION_RIGHT);
        goumai = new ResideMenuItem(this,R.mipmap.dianji,"购买债券");
        resideMenu.addMenuItem(goumai, ResideMenu.DIRECTION_RIGHT);
        zaiquanfuwu = new ResideMenuItem(this,R.mipmap.dianji,"债券转让服务费");
        resideMenu.addMenuItem(zaiquanfuwu, ResideMenu.DIRECTION_RIGHT);
        zaiquanlixi = new ResideMenuItem(this,R.mipmap.dianji,"债券转让未结利息");
        resideMenu.addMenuItem(zaiquanlixi, ResideMenu.DIRECTION_RIGHT);
        fine = new ResideMenuItem(this,R.mipmap.dianji,"逾期补息");
        resideMenu.addMenuItem(fine, ResideMenu.DIRECTION_RIGHT);
        subsidy = new ResideMenuItem(this,R.mipmap.dianji,"提前还款补贴");
        resideMenu.addMenuItem(subsidy, ResideMenu.DIRECTION_RIGHT);
        lottery = new ResideMenuItem(this,R.mipmap.dianji,"加息");
        resideMenu.addMenuItem(lottery, ResideMenu.DIRECTION_RIGHT);

        all.setOnClickListener(this);
        tuiguang.setOnClickListener(this);
        zaiquan.setOnClickListener(this);
        benjin.setOnClickListener(this);
        lixi.setOnClickListener(this);
        fuwu.setOnClickListener(this);
        chongzhi.setOnClickListener(this);
        tixian.setOnClickListener(this);
        jine.setOnClickListener(this);
        jiangli.setOnClickListener(this);
        xunijin.setOnClickListener(this);
        touzhi.setOnClickListener(this);
        tiqu.setOnClickListener(this);
        goumai.setOnClickListener(this);
        zaiquanfuwu.setOnClickListener(this);
        zaiquanlixi.setOnClickListener(this);
        fine.setOnClickListener(this);
        subsidy.setOnClickListener(this);
        lottery.setOnClickListener(this);
        if (mErrorLayout == null) {
            mErrorLayout = (EmptyLayout)findViewById(R.id.error_layout);
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        }
        initRefresh();
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

    @Override
    protected void init(Bundle savedInstanceState) {
        title=getString(R.string.Fund_record);
        navbarManage.setCentreStr(getString(R.string.Fund_record));
        navbarManage.showLeft(true);
        navbarManage.showRight(true);
        navbarManage.setLeftImg(R.mipmap.ic_back_b);
        navbarManage.setRightImg(R.mipmap.gengduo);
        navbarManage.setBackground(R.color.navbar_bg);
        navbarManage.setOnLeftClickListener(new NavbarManage.OnLeftClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }
        });

        navbarManage.setOnRightClickListener(new NavbarManage.OnRightClickListener() {
            @Override
            public void onRightClick() {
                BuriedPointUtil.buriedPoint("资金记录筛选按钮");
                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
            }
        });

        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);

        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                if(!TextUtils.isEmpty(FLAG)){
                    getData(1,FLAG);
                }else {
                    getData(1,"all");
                }

            }
        });
        getData(1,"all");
    }
    private void getData(final int type,String flag){
        if(type == 1){
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }
        //判断是否登陆
        if (AppContext.getUserBean() == null||null==AppContext.getUserBean().data) {
            Intent intent = new Intent(this, UserActivity.class);
            intent.putExtra(UserParam.TYPE, UserActivity.TYPE_LOGIN);
            intent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
            startActivity(intent);
            return;
        }
        ApiHttpClient.getzijinList(AppContext.getUserBean().data.userId,flag,currentPages +"",each_page_num+"",new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if(type == 1) {
                    mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }
            }

            @Override
            public void onResponse(String response, int id) {
                TLog.error("资金数据:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        if(type == 1) {
                         mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        }
                        mFundRecordBean = GsonUtils.jsonToBean(response, FundRecordBean.class);
                        allItemCount = Integer.valueOf(mFundRecordBean.data.count);
                        setData(type);
                    } else if (jsonObject.getInt("status") == 88){
                        ToastUtil.showToast(jsonObject.getString("msg"));
                        Intent Fintent = new Intent(AppContext.context(), UserActivity.class);
                        Fintent.putExtra(UserParam.TYPE, 0);
                        Fintent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                        startActivity(Fintent);
                        finish();
                    }else {
                        if(type == 1) {
                            mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if(type == 1) {
                        mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                    }
                }
            }
        });
    }

    private void setData(int type){
        if (mFundRecordBean.data.records == null || mFundRecordBean.data.records.size() <= 0) {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
        }

        if (adapter == null) {
            adapter = new QuickAdapter<FundRecordBean.Data.records>(this, R.layout.foud_record_list, mFundRecordBean.data.records) {
                @Override
                protected void convert(BaseAdapterHelper helper, FundRecordBean.Data.records item) {
                    helper.setText(R.id.list_money, item.money);
                    helper.setText(R.id.list_time,item.time);
                    if(item.type.equals("推广提成")){
                        helper.setImageResource(R.id.list_logo,R.mipmap.tuiguan_tichen);
                    }else if(item.type.equals("债权买卖")){
                        helper.setImageResource(R.id.list_logo,R.mipmap.zhaiquan_maimai);
                    }else if(item.type.equals("本金")){
                        helper.setImageResource(R.id.list_logo,R.mipmap.benjin);
                    }else if(item.type.equals("利息")){
                        helper.setImageResource(R.id.list_logo,R.mipmap.lixi);
                    }else if(item.type.equals("服务费")){
                        helper.setImageResource(R.id.list_logo,R.mipmap.fuwufei);
                    }else if(item.type.equals("充值")){
                        helper.setImageResource(R.id.list_logo,R.mipmap.chonzhi);
                    }else if(item.type.equals("提现")){
                        helper.setImageResource(R.id.list_logo,R.mipmap.tixian);
                    }else if(item.type.equals("金额操作")){
                        helper.setImageResource(R.id.list_logo,R.mipmap.jine_caoz);
                    }else if(item.type.equals("奖励")){
                        helper.setImageResource(R.id.list_logo,R.mipmap.jiangli);
                    }else if(item.type.equals("虚拟金")){
                        helper.setImageResource(R.id.list_logo,R.mipmap.xunijin);
                    }else if(item.type.equals("投资")){
                        helper.setImageResource(R.id.list_logo,R.mipmap.touzi);
                    }else if(item.type.equals("提取")){
                        helper.setImageResource(R.id.list_logo,R.mipmap.tiqu);
                    }else if(item.type.equals("购买债权")){
                        helper.setImageResource(R.id.list_logo,R.mipmap.goumai_zhaiquan);
                    }else if(item.type.equals("债权转让服务费")){
                        helper.setImageResource(R.id.list_logo,R.mipmap.zhaiquan_fuwufei);
                    }else if(item.type.equals("债权转让未结利息")){
                        helper.setImageResource(R.id.list_logo,R.mipmap.zhaiquan_weijielixi);
                    }else if(item.type.equals("逾期补息")){
                        helper.setImageResource(R.id.list_logo,R.mipmap.yuqi);
                    }else if(item.type.equals("提前还款补贴")){
                        helper.setImageResource(R.id.list_logo,R.mipmap.tiqian);
                    }else if(item.type.equals("加息")){
                        helper.setImageResource(R.id.list_logo,R.mipmap.jiaxi);
                    }else{
                        helper.setImageResource(R.id.list_logo,R.mipmap.moren);
                    }
                    if(item.mode.equals("out")){
                        helper.setImageResource(R.id.list_flag,R.mipmap.zhifu_icon);
                        helper.setTextColor(R.id.list_money,0xfff58101);
                    }
                    else if(item.mode.equals("in")){
                        helper.setImageResource(R.id.list_flag,R.mipmap.shou_icon);
                        helper.setTextColor(R.id.list_money,0xff12A874);
                    }

//                progress.setLoadAnimation(item.schedule, 70);
                    //判断是否是新手标
                }
            };
            list_view.setAdapter(adapter);

        } else {
            if(type == 0){
                adapter.addAll(mFundRecordBean.data.records);
                setPullUpState(SUCCEED);
            }else{
                adapter.replaceAll(mFundRecordBean.data.records);
            }
        }
    }
    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
                 }

        @Override
        public void closeMenu() {
                  }
    };
    @Override
    public void onClick(View v) {
        if (v == all){
            FLAG = "all";
            getData(1,"all");
            resideMenu.closeMenu();
        }else if (v == tuiguang){
            FLAG = "spread";
            getData(1,"spread");
            resideMenu.closeMenu();
        }else if (v == zaiquan){
            FLAG = "odd";
            getData(1,"odd");
            resideMenu.closeMenu();
        }else if (v == benjin){
            FLAG = "capital";
            getData(1,"capital");
            resideMenu.closeMenu();
        }else if (v == lixi){
            FLAG = "interest";
            getData(1,"interest");
            resideMenu.closeMenu();
        }else if (v == fuwu){
            FLAG = "interestService";
            getData(1,"interestService");
            resideMenu.closeMenu();
        }else if (v == chongzhi){
            FLAG = "recharge";
            getData(1,"recharge");
            resideMenu.closeMenu();
        }else if (v == tixian){
            FLAG = "withdraw";
            getData(1,"withdraw");
            resideMenu.closeMenu();
        }else if (v == jine){
            FLAG = "addmoney";
            getData(1,"addmoney");
            resideMenu.closeMenu();
        }else if (v == jiangli){
            FLAG = "reward";
            getData(1,"reward");
            resideMenu.closeMenu();
        }else if (v == xunijin){
            FLAG = "virtual";
            getData(1,"virtual");
            resideMenu.closeMenu();
        }else if (v == touzhi){
            FLAG = "invest";
            getData(1,"invest");
            resideMenu.closeMenu();
        }else if (v == tiqu){
            FLAG = "extract";
            getData(1,"extract");
            resideMenu.closeMenu();
        }else if (v == goumai){
            FLAG = "crtr";
            getData(1,"crtr");
            resideMenu.closeMenu();
        }else if (v == zaiquanfuwu){
            FLAG = "crtrfee";
            getData(1,"crtrfee");
            resideMenu.closeMenu();
        }else if (v == zaiquanlixi){
            FLAG = "crtrinterest";
            getData(1,"crtrinterest");
            resideMenu.closeMenu();
        }else if (v == fine){
            FLAG = "fine";
            getData(1,"fine");
            resideMenu.closeMenu();
        }else if (v == subsidy){
            FLAG = "subsidy";
            getData(1,"subsidy");
            resideMenu.closeMenu();
        }else if (v == lottery){
            FLAG = "lottery";
            getData(1,"lottery");
            resideMenu.closeMenu();
        }
    }

    @Override
    public void onRefresh() {
        swipe_refresh_layout.setRefreshing(false);
        currentPages = 1;
        if(FLAG != null && FLAG.length() > 0){
            getData(1,FLAG);
        }else{
            getData(1,"all");
        }
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
                            getData(0,FLAG);
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

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

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
}
