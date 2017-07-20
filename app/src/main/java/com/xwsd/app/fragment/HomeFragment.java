package com.xwsd.app.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import butterknife.Bind;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.gnwai.smartimageview.SmartImageView;
import com.hejunlin.superindicatorlibray.LoopViewPager;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.activity.*;
import com.xwsd.app.adapter.BaseAdapterHelper;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseFragment;
import com.xwsd.app.bean.IndexBean;
import com.xwsd.app.bean.Odds;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.EmptyLayout;
import com.xwsd.app.view.ObserveScrollView;
import com.xwsd.app.view.PicDialog;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import static com.xwsd.app.R.id.commit;

/**
 * Created by Gx on 2016/8/18.
 * 首页
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener {

    /**
     * 顶部轮播图
     */
    @Bind(R.id.icv_home_top)
    LoopViewPager icv_home_top;

    /**
     * 跑马灯
     */
    @Bind(R.id.text_switcher)
    TextSwitcher text_switcher;

    /**
     * 小喇叭
     */
    @Bind(R.id.iv_trumpet)
    ImageView iv_trumpet;

    /**
     * 交易量
     */
    @Bind(R.id.tv_turnover_price)
    TextView tv_turnover_price;

    /**
     * 用户受益
     */
    @Bind(R.id.tv_benefit_price)
    TextView tv_benefit_price;

    /**
     * 今日推荐
     */
    @Bind(R.id.tv_today)
    TextView tv_today;

    /**
     * 自动轮播时间间隔默认5秒
     */
    private long mCycleDelayed = 3000;

    /**
     * 跑马灯当前显示的位置
     */
    private int current;

    /**
     * 列表
     */
    @Bind(R.id.list_view)
    ListView list_view;

    /**
     * 错误布局
     */
    @Bind(R.id.error_layout)
    EmptyLayout error_layout;

    /**
     * 新手标滑动布局
     */
    @Bind(R.id.vp_newbie_bid)
    ViewPager vp_newbie_bid;

    /**
     * 输入金额
     */
    @Bind(R.id.et_new_bid)
    EditText et_new_bid;

    /**
     * 预计收益
     */
    @Bind(R.id.tv_predict)
    TextView tv_predict;

    @Bind(R.id.tv_month)
    TextView tv_month;

    @Bind(R.id.scroll_view)
    ObserveScrollView scroll_view;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipe_refresh_layout;
    /**
     * 首页信息
     */
    public static IndexBean indexBean;

    ViewSwitcher.ViewFactory viewFactory;

    PicAdapter picAdapter;

    NewHandOddsAdapter newHandOddsAdapter;

    /**
     * 为你推荐适配器
     */
    QuickAdapter adapter;
    DecimalFormat decimalFormat = new DecimalFormat("0.0");

    @Override
    protected View setContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        AppContext.setNeedLock(false);
        return view;
    }

    private static String getThreeString(String str) {
        DecimalFormat df = new DecimalFormat("###,###");
        return df.format(Double.parseDouble(str));
    }

    @Override
    protected void init() {
        error_layout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(1);
            }
        });
//        触碰软键盘外面收缩键盘
        scroll_view.setGestureDetector(new GestureDetector(getActivity(), new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                ((MainActivity) getActivity()).hideSoftKeyboard(getActivity().getCurrentFocus());
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        }));

        //启动喇叭动画
        final AnimationDrawable anim = (AnimationDrawable) iv_trumpet.getBackground();
        anim.start();
        //启动倒计时
        handler.sendEmptyMessageDelayed(1, mCycleDelayed);//跑马灯
        handler.sendEmptyMessageDelayed(2, 1000);//标的倒计时
        getData(1);
        initRefresh();
        //判断是否开通存管
        if (AppContext.getUserBean() != null ) {
            {
                if (AppContext.getUserBean().data.custodyId == null || "0".equals(AppContext.getUserBean().data.custodyId)
                        || "".equals(AppContext.getUserBean().data.custodyId)) {
                    showDialog();
                }
            }
        }else {
            showDialog();
        }
    }

    /**
     * 初始化下拉刷新
     */
    private void initRefresh() {

        swipe_refresh_layout.setOnRefreshListener(this);
        swipe_refresh_layout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
    }

    public void getData(final int type) {
        //请求首页数据
        if (type == 1) {
            error_layout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }
        ApiHttpClient.index(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (type == 1) {
                    error_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
                } else {
                    swipe_refresh_layout.setRefreshing(false);
                    ToastUtil.showToastShort(R.string.refurbish_failure);
                }
                System.out.println("e = " + e.toString());
            }
            @Override
            public void onResponse(String response, int id) {
                TLog.error("首页数据:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        if (type == 1) {
                            error_layout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        } else {
                            swipe_refresh_layout.setRefreshing(false);
                        }
                        indexBean = GsonUtils.jsonToBean(response, IndexBean.class);
                        setData();
                    } else {
                        if (type == 1) {
                            error_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
                        } else {
                            swipe_refresh_layout.setRefreshing(false);
                            ToastUtil.showToastShort(R.string.refurbish_failure);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (type == 1) {
                        error_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
                    } else {
                        swipe_refresh_layout.setRefreshing(false);
                        ToastUtil.showToastShort(R.string.refurbish_failure);
                    }
                }
            }
        });
    }

    /**
     * 设置数据
     */
    private void setData() {

        tv_turnover_price.setText(getThreeString(indexBean.data.allVolume));
        tv_benefit_price.setText(getThreeString(indexBean.data.allInterest));

        //设置轮播图
        if (picAdapter == null) {
            picAdapter = new PicAdapter();
            icv_home_top.setOnDispatchTouchEventListener(mDispatchOnTouchListener);
            if (indexBean.data.banners.size() > 0) {
                icv_home_top.setLooperPic(true);
                icv_home_top.setAdapter(picAdapter);
            }
        } else {
            picAdapter.notifyDataSetChanged();
        }

        //初始化月份
        if (indexBean.data.newHandOdds != null && indexBean.data.newHandOdds.size()>0 && indexBean.data.newHandOdds.get(0) != null) {
            tv_month.setText(indexBean.data.newHandOdds.get(0).oddPeriod);
        }
        et_new_bid.setText("");

        //设置新手标
//        if (newHandOddsAdapter == null) {
            newHandOddsAdapter = new NewHandOddsAdapter(getChildFragmentManager());
        if(null!=vp_newbie_bid&&isAdded()) {
            vp_newbie_bid.setAdapter(newHandOddsAdapter);

            vp_newbie_bid.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    et_new_bid.setText("");
                    tv_predict.setText("预计收益");
                    tv_month.setText(indexBean.data.newHandOdds.get(position).oddPeriod);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

            //设置文本监听
            et_new_bid.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0) {
                        //得到当前新手标的位置
                        int position = vp_newbie_bid.getCurrentItem();
                        //计算收益
                        float price = 0;
                        if(indexBean.data!=null&&indexBean.data.newHandOdds!=null&&indexBean.data.newHandOdds.size()!=0){
                             price = (Float.valueOf(s.toString()) * indexBean.data.newHandOdds.get(position).oddYearRate) * (Float.valueOf(indexBean.data.newHandOdds.get(position).oddPeriod.split("个")[0]) / 12f);
                        }
                        tv_predict.setText(decimalFormat.format(price));
                    } else {
                        tv_predict.setText("预计收益");
                    }
                }
            });
//        } else {
//            newHandOddsAdapter.notifyDataSetChanged();
//        }

            if (isAdded()&&viewFactory == null) {
                viewFactory = new ViewSwitcher.ViewFactory() {
                    //这里 用来创建内部的视图，这里创建TextView，用来显示文字
                    public View makeView() {

                        TextView tv = new TextView(AppContext.context());
                        //设置文字大小
                        if(isAdded()){
                            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.DIMEN_14));
                        }
                        tv.setSingleLine(true);
                        tv.setEllipsize(TextUtils.TruncateAt.END);
                        //设置文字 颜色
                        tv.setTextColor(getResources().getColor(R.color.gray_2));
                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.gravity = Gravity.CENTER;
                        tv.setLayoutParams(lp);
                        return tv;
                    }


                };
                text_switcher.setFactory(viewFactory);

                text_switcher.setInAnimation(getActivity(), R.anim.slide_in_bottom);
                text_switcher.setOutAnimation(getActivity(), R.anim.slide_out_top);
                text_switcher.setText(indexBean.data.notices.get(0).news_title);
            }


        //设置今日可投推荐
        if(null==indexBean.data.todayLast){
            tv_today.setText("今日可投：" + 0);
        }else{
            tv_today.setText("今日可投：" + indexBean.data.todayLast);
        }


        //设置推荐列表
        if (adapter == null) {
            adapter = new QuickAdapter<Odds>(getActivity(), R.layout.item_project, indexBean.data.odds) {
                @Override
                protected void convert(BaseAdapterHelper helper, Odds item) {
                    helper.setText(R.id.tv_oddTitle, item.oddTitle);
                    helper.setText(R.id.tv_oddMoney, item.oddMoney);
                    helper.setText(R.id.tv_oddReward, decimalFormat.format(item.oddYearRate * 100));
                    helper.setText(R.id.tv_oddPeriod, item.oddPeriod.replace("个月", ""));
                    DonutProgress dp_schedule = helper.getView(R.id.dp_schedule);
                    dp_schedule.setProgress(item.schedule);
                    if(item.oddStyle.equals("newhand")){
                        helper.setBackgroundRes(R.id.flag_icon,R.mipmap.xin);
                    }else {
                        if (item.investType.equals("0")) {
                            helper.setBackgroundRes(R.id.flag_icon, R.mipmap.zi);
                        } else if (item.investType.equals("1")) {
                            helper.setBackgroundRes(R.id.flag_icon, R.mipmap.shoudong);
                        }
                    }

//                progress.setLoadAnimation(item.schedule, 70);
                    //判断是否是新手标
                    if (item.oddReward > 0) {
                        helper.setVisible(R.id.tv_rewards, true);
                        helper.setText(R.id.tv_rewards, "+" + decimalFormat.format(item.oddReward * 100) + "%");
                    } else {
                        helper.setVisible(R.id.tv_rewards, false);
                    }
                    switch (item.progress) {
                        case "start":
                            if (item.schedule == 100) {
                                dp_schedule.setText("复审中");
                            } else {
                                switch (item.second) {
                                    case -1://立即投标
                                        dp_schedule.setText(item.schedule + "%");
                                        break;
                                    case 0://显示开始时间 2016-10-20 14:00:00
                                        dp_schedule.setText(item.openTime.substring(11, 16) + "&开始");
                                        break;
                                    default://开始倒计时
                                        if (item.countdown == -1) {
                                            item.countdown = item.second;
                                        }
                                        StringBuffer timeStr = new StringBuffer();
                                        int second = item.countdown % 60;
                                        int minute = (item.countdown - second) / 60;
/*                                        TLog.error("minute:" + minute);
                                        TLog.error("second:" + second);*/
                                        if (minute > 0) {
                                            if (minute < 10) {
                                                timeStr.append("0");
                                            }
                                            timeStr.append(minute);
                                        } else {
                                            timeStr.append("00");
                                        }

                                        timeStr.append(":");

                                        if (second > 0) {
                                            if (second < 10) {
                                                timeStr.append("0");
                                            }
                                            timeStr.append(second);
                                        } else {
                                            timeStr.append("00");
                                        }
                                        dp_schedule.setText(timeStr.toString());
                                        break;
                                }
                            }
                            break;
                        case "run":
                            dp_schedule.setText("还款中");
                            break;
                        default:
                            dp_schedule.setText("已结束");
                            break;
                    }
                }
            };

            list_view.setAdapter(adapter);
            list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), ProjectDetailsActivity.class);
                    intent.putExtra("oddNumber", ((Odds) adapter.getItem(position)).oddNumber);
                    startActivity(intent);
                }
            });
        } else {
            adapter.replaceAll(indexBean.data.odds);
        }
    }

    /**
     * 修改数据源
     */
    private void modifyRecylerData() {
        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                Odds temp = (Odds) adapter.getItem(i);
                if (temp.countdown >= 1) {
                    TLog.error("");
                    temp.countdown = temp.countdown - 1;
                    if (temp.countdown != 0) {
                    }
                    adapter.set(i, temp);
                } else {
                    continue;
                }
            }
        }
    }

    @Override
    public void onRefresh() {
        getData(2);
    }

    /**
     * 新手标的适配器
     */
    private class NewHandOddsAdapter extends FragmentPagerAdapter {

        public NewHandOddsAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            NewbieBidFragment newbieBidFragment = new NewbieBidFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(UserParam.POSITION, position);
            newbieBidFragment.setArguments(bundle);
            return newbieBidFragment;
        }

        @Override
        public int getCount() {
            return indexBean.data.newHandOdds == null ? 0 : indexBean.data.newHandOdds.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            NewbieBidFragment obj = (NewbieBidFragment) super.instantiateItem(container, position);
            return obj;
        }
    }

    /**
     * 跑马灯开始轮播
     */
    public void startImageCycle() {
        handler.sendEmptyMessageDelayed(0, mCycleDelayed);
    }

    /**
     * 跑马灯暂停轮播
     */
    public void stopImageCycle() {
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * 跑马灯自动轮播
     */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (text_switcher != null) {

                        current++;
                        //复位
                        if (indexBean!=null && current >= indexBean.data.notices.size()) {
                            current = 0;
                        }
                        //设置数据
                        if (indexBean!=null&&text_switcher!=null) {

                            if (viewFactory == null&&isAdded()) {
                                viewFactory = new ViewSwitcher.ViewFactory() {
                                    //这里 用来创建内部的视图，这里创建TextView，用来显示文字
                                    public View makeView() {
                                        TextView tv = new TextView(AppContext.context());
                                        //设置文字大小
                                        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.DIMEN_14));
                                        tv.setSingleLine(true);
                                        tv.setEllipsize(TextUtils.TruncateAt.END);
                                        //设置文字 颜色
                                        tv.setTextColor(getResources().getColor(R.color.gray_2));
                                        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT);
                                        lp.gravity = Gravity.CENTER;
                                        tv.setLayoutParams(lp);
                                        return tv;
                                    }
                                };
                                text_switcher.setFactory(viewFactory);
                            }
                            if(isAdded()&&indexBean!=null&&indexBean.data!=null){
                                text_switcher.setText(indexBean.data.notices.get(current).news_title + "");

                                text_switcher.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getActivity(), NewsDetailsActivity.class);
                                        intent.putExtra(UserParam.DATA, indexBean.data.notices.get(current).id);
                                        startActivity(intent);
                                    }
                                });
                            }

                        }
                        //循环
                        handler.sendEmptyMessageDelayed(1, mCycleDelayed);
                    }
                    break;
                case 2:
                    modifyRecylerData();
                    //循环
                    handler.sendEmptyMessageDelayed(2, 1000);
                    break;
            }
            return false;
        }
    }

    );

    @OnClick({commit, R.id.iv_left, R.id.iv_right, R.id.iv_new_menu})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case commit://立即投
                if(indexBean.data.newHandOdds.size()>vp_newbie_bid.getCurrentItem()){

                switch (indexBean.data.newHandOdds.get(vp_newbie_bid.getCurrentItem()).progress) {
                    case "start":
                        if (indexBean.data.newHandOdds.get(vp_newbie_bid.getCurrentItem()).schedule == 100) {//复审中
                            ToastUtil.showToastShort("已结束");
                        } else {
                            switch (indexBean.data.newHandOdds.get(vp_newbie_bid.getCurrentItem()).second) {
                                case -1://立即投标
                                    //判断用户是否登录
                                    if (AppContext.getUserBean() == null) {
                                        Intent intent = new Intent(getActivity(), UserActivity.class);
                                        intent.putExtra(UserParam.CLASS, PromptlyInvestActivity.class);
                                        intent.putExtra(UserParam.TYPE, PromptlyInvestActivity.TYPE_ALL_BID);
                                        intent.putExtra(UserParam.DATA, indexBean.data.newHandOdds.get(vp_newbie_bid.getCurrentItem()).oddNumber);
                                        intent.putExtra(UserParam.TITLE, indexBean.data.newHandOdds.get(vp_newbie_bid.getCurrentItem()).oddTitle);
                                        //intent.putExtra(UserParam.MONEY, indexBean.data.newHandOdds.get(vp_newbie_bid.getCurrentItem()).oddMoneyLast);
                                        startActivity(intent);
                                        return;
                                    } else {
                                        Intent intent = new Intent(getActivity(), PromptlyInvestActivity.class);
                                        intent.putExtra(UserParam.TYPE, PromptlyInvestActivity.TYPE_ALL_BID);
                                        intent.putExtra(UserParam.DATA, indexBean.data.newHandOdds.get(vp_newbie_bid.getCurrentItem()).oddNumber);
                                        intent.putExtra(UserParam.TITLE, indexBean.data.newHandOdds.get(vp_newbie_bid.getCurrentItem()).oddTitle);
                                        //intent.putExtra(UserParam.MONEY, indexBean.data.newHandOdds.get(vp_newbie_bid.getCurrentItem()).oddMoneyLast);
                                        startActivity(intent);
                                    }
                                    break;
                                case 0://显示开始时间 2016-10-20 14:00:00
                                    ToastUtil.showToastShort("尚未开始");
                                    break;
                                default://开始倒计时
                                    ToastUtil.showToastShort("尚未开始");
                                    break;
                            }
                        }
                        break;
                    case "run"://还款中
                        ToastUtil.showToastShort("已结束");
                        break;
                    default://已结束
                        ToastUtil.showToastShort("已结束");
                        break;
                }
                }
                break;
            case R.id.iv_left://新手标左边
                vp_newbie_bid.setCurrentItem(vp_newbie_bid.getCurrentItem() - 1);
                break;
            case R.id.iv_right://新手标右边
                vp_newbie_bid.setCurrentItem(vp_newbie_bid.getCurrentItem() + 1);
                break;
            case R.id.iv_new_menu://讯息菜单
//                切换到讯息
                ((MainActivity) getActivity()).main_tab.check(R.id.tab_news);
                break;

        }
    }

    private LoopViewPager.OnDispatchTouchEventListener mDispatchOnTouchListener = new LoopViewPager.OnDispatchTouchEventListener() {
        @Override
        public void onDispatchKeyEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                icv_home_top.setLooperPic(false);
            } else if (event.getAction() == MotionEvent.ACTION_UP
                    || event.getAction() == MotionEvent.ACTION_CANCEL) {
                icv_home_top.setLooperPic(true);
            }
        }
    };


    class PicAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (indexBean == null || indexBean.data == null || indexBean.data.banners == null) {
                return 0;
            } else {
                return indexBean.data.banners.size();
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
            view.removeView((View) object);
        }
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.view_recommend_page_item, container, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), WebDetailsActivity.class);
                    intent.putExtra(UserParam.TITLE, indexBean.data.banners.get(position).title);
                    intent.putExtra(UserParam.URL, indexBean.data.banners.get(position).link);
                    startActivity(intent);
                }
            });
            final SmartImageView imageView = (SmartImageView) view.findViewById(R.id.image);

            //设置轮播图图片
            Glide.with(getActivity())
                    .load(indexBean.data.banners.get(position).banner)
                    .crossFade()
                    .into(imageView);
/*            ApiHttpClient.getMita(indexBean.data.banners.get(position).banner,new BitmapCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    System.out.println("e = " + e.toString());
                }
                @Override
                public void onResponse(Bitmap response, int id) {
                    imageView.setImageBitmap(response);
                }
            });*/
            container.addView(view);
            return view;
        }
    }
    //开通存管对话框
    private void showDialog(){


        new PicDialog(this.getActivity(),R.style.Translucent_NoTitle,
                new PicDialog.DialogClickListener() {

                    @Override
                    public void close(Dialog dialog, String pass) {
                        dialog.dismiss();

                    }

                    @Override
                    public void commit(Dialog dialog) {
                        dialog.dismiss();
                        Intent intent;
                        if (AppContext.getUserBean() == null ) {
                            intent = new Intent(getActivity(), UserActivity.class);
                            intent.putExtra(UserParam.TYPE, UserActivity.TYPE_LOGIN);
                            intent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
                            startActivity(intent);
                        }if(AppContext.getUserBean() != null){
                            intent = new Intent(HomeFragment.this.getActivity(), OpenDepositoryActivity.class);
                            startActivity(intent);
                        }



                    }
                }).show();
    }
    @Override
    public void onResume() {
        super.onResume();

    }
}
