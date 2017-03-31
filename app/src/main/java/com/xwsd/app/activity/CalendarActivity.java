package com.xwsd.app.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.*;
import butterknife.Bind;
import butterknife.OnClick;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.adapter.BaseAdapterHelper;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.RepaymentsBean;
import com.xwsd.app.calendar.CalendarAdapter;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.tools.GsonUtils;
import com.xwsd.app.tools.TLog;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.CalendarGridView;
import com.xwsd.app.view.NavbarManage;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;
import okhttp3.Call;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * Created by Gx on 2016/8/24.
 * 回款日历
 */
public class CalendarActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    private CalendarAdapter calV = null;

    Calendar calendar = Calendar.getInstance();

    private int year_c;

    private int month_c;

    private int day_c;

    private int jumpMonth = 0; // 每次滑动，增加或减去一个月,默认为0（即显示当前月）

    private int jumpYear = 0; // 滑动跨越一年，则增加或者减去一年,默认为0(即当前年)

    @Bind(R.id.tv_month)
    TextView tv_month;

    @Bind(R.id.flipper)
    ViewFlipper flipper;

    private CalendarGridView gridView;

    private GestureDetector gestureDetector;

    /**
     * 每次添加gridview到viewflipper中时给的标记
     */
    private int gvFlag = 0;

    /**
     * 列表
     */
    @Bind(R.id.list_view)
    ListView list_view;

    /**
     * 回款时间横向列表
     */
    @Bind(R.id.returned_time_list)
    LinearLayout returned_time_list;

    @Bind(R.id.returned_time_hsv)
    HorizontalScrollView returned_time_hsv;

    @Bind(R.id.horizontalScrollView)
    HorizontalScrollView mhorizontalScrollView;


    @Bind(R.id.ll_returned_money_month)
    LinearLayout ll_returned_money_month;

    @Bind(R.id.ll_returned_money_month_lixi)
    LinearLayout ll_returned_money_month_lixi;

    @Bind(R.id.ll_returned_money_day)
    LinearLayout ll_returned_money_day;

    @Bind(R.id.ll_returned_money_day_lixi)
    LinearLayout ll_returned_money_day_lixi;

    @Bind(R.id.ll_returned_money_practical)
    LinearLayout ll_returned_money_practical;

    @Bind(R.id.ll_returned_money_practical_lixi)
    LinearLayout ll_returned_money_practical_lixi;

    @Bind(R.id.tv_selected_time)
    TextView tv_selected_time;
    //初始化偏移量
    private int offset = 0;

    private int scrollViewWidth = 0;

    RepaymentsBean repaymentsBean;

    DecimalFormat decimalFormat = new DecimalFormat("0.00");

    RequestCall call;

    @Bind(R.id.vertical_scroll_view)
    ScrollView vertical_scroll_view;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_calendar);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        // 设置导航栏
        navbarManage.setCentreStr(getString(R.string.returned_calendar));
        navbarManage.showLeft(true);
        navbarManage.showRight(false);
        navbarManage.setBackground(R.color.navbar_bg);
        navbarManage.setLeftImg(R.mipmap.ic_back_b);
        navbarManage.setOnLeftClickListener(new NavbarManage.OnLeftClickListener() {
            @Override
            public void onLeftClick() {
                onBackPressed();
            }
        });

        //设置日历
        year_c = calendar.get(Calendar.YEAR);
        month_c = calendar.get(Calendar.MONTH) + 1;
        day_c = calendar.get(Calendar.DAY_OF_MONTH);

        gestureDetector = new GestureDetector(this, new MyGestureListener());

//        vertical_scroll_view.setGestureDetector(gestureDetector);

        calV = new CalendarAdapter(this, year_c, month_c, day_c);
        addGridView();
        gridView.setAdapter(calV);
        flipper.addView(gridView, 0);
        addTextToTopTextView(tv_month);

        getData();
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            int gvFlag = 0; // 每次添加gridview到viewflipper中时给的标记
            if (e1.getX() - e2.getX() > 60) {
                // 像左滑动
                enterNextMonth(gvFlag);
                return true;
            } else if (e1.getX() - e2.getX() < -60) {
                // 向右滑动
                enterPrevMonth(gvFlag);
                return true;
            }
            return false;
        }
    }

    /**
     * 得到数据
     */
    private void getData() {
        if (call != null) {
            call.cancel();
        }
        call = ApiHttpClient.repayments(
                AppContext.getUserBean().data.userId,
                String.valueOf(calV.getShowYear()),
                String.valueOf(calV.getShowMonth()),
                "1",
                new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.showToastShort(R.string.network_exception);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        TLog.error("回款日历:" + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getInt("status") == 1) {
                                repaymentsBean = GsonUtils.jsonToBean(response, RepaymentsBean.class);
                                setData();
                            } else {
                                ToastUtil.showToastShort(R.string.network_exception);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtil.showToastShort(R.string.network_exception);
                        }
                    }
                }
        );
    }

    /**
     * 设置数据
     */
    private void setData() {

        ((TextView) ll_returned_money_month.findViewById(R.id.tv_title)).setText("当月回款本金");
        ((TextView) ll_returned_money_month_lixi.findViewById(R.id.tv_title)).setText("当月回款利息");
        ((TextView) ll_returned_money_day.findViewById(R.id.tv_title)).setText("当天回款本金");
        ((TextView) ll_returned_money_day_lixi.findViewById(R.id.tv_title)).setText("当天回款利息");
        ((TextView) ll_returned_money_practical.findViewById(R.id.tv_title)).setText("当天实回本金");
        ((TextView) ll_returned_money_practical_lixi.findViewById(R.id.tv_title)).setText("当天实回利息");

        // ((TextView) ll_returned_money_month.findViewById(R.id.tv_money)).setText(repaymentsBean.data.allMoney);
        if(repaymentsBean!=null){
        ((TextView) ll_returned_money_month.findViewById(R.id.tv_money)).setText(repaymentsBean.data.benJin);
        ((TextView) ll_returned_money_month_lixi.findViewById(R.id.tv_money)).setText(repaymentsBean.data.interest);

        //循环遍历日期，给已回款的日子加上戳
        for (int i = 0; i < repaymentsBean.data.repayments.size(); i++) {

            switch (repaymentsBean.data.repayments.get(i).status) {
                case "over":
                    calV.updataView(calV.getStartPositon() + i, gridView, R.mipmap.ic_already_back);
                    break;
                case "stay":
                    calV.updataView(calV.getStartPositon() + i, gridView, R.mipmap.ic_none_returned);
                    break;
            }
        }
    }

        StringBuffer textDate = new StringBuffer();
        textDate.append(calV.getShowYear()).append("-").append(calV.getShowMonth()).append("-").append(day_c);
        tv_selected_time.setText(textDate);

        setOddsList(day_c);
        setCheckedData(day_c);
        setDataList();
    }

    /**
     * 设置选中日期的数据
     */
    private void setCheckedData(int day) {
        if(repaymentsBean!=null){
            ((TextView) ll_returned_money_day.findViewById(R.id.tv_money)).setText(repaymentsBean.data.repayments.get(day - 1).benJin);
            ((TextView) ll_returned_money_day_lixi.findViewById(R.id.tv_money)).setText(repaymentsBean.data.repayments.get(day - 1).interest);
            ((TextView) ll_returned_money_practical.findViewById(R.id.tv_money)).setText(repaymentsBean.data.repayments.get(day - 1).realBenjin);
            ((TextView) ll_returned_money_practical_lixi.findViewById(R.id.tv_money)).setText(repaymentsBean.data.repayments.get(day - 1).realInterest);
        }

    }

    /**
     * 设置横向时间列表
     */
    private void setDataList() {

        returned_time_list.removeAllViews();

        if (repaymentsBean.data.stayDays != null && repaymentsBean.data.stayDays.size() > 0) {
            for (final String stayDays : repaymentsBean.data.stayDays) {
                View view = getLayoutInflater().inflate(R.layout.item_returned_time, returned_time_list, false);
                TextView tv_month = (TextView) view.findViewById(R.id.tv_month);
                TextView tv_day = (TextView) view.findViewById(R.id.tv_day);
                tv_month.setText(stayDays.substring(4, 6));
                tv_day.setText(stayDays.substring(6, 8));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //初始化跳转参数
                        jumpMonth = 0;
                        jumpYear = 0;

                        //得到年月日
                        year_c = Integer.valueOf(stayDays.substring(0, 4));
                        month_c = Integer.valueOf(stayDays.substring(4, 6));
                        day_c = Integer.valueOf(stayDays.substring(6, 8));


                        //重新添加一个日历
                        calV = new CalendarAdapter(
                                CalendarActivity.this,
                                year_c,
                                month_c,
                                day_c);

                        addGridView();
                        gvFlag = 0;
                        gridView.setAdapter(calV);
                        flipper.removeViewAt(0);
                        flipper.addView(gridView, gvFlag);

                        addTextToTopTextView(CalendarActivity.this.tv_month);
                        getData();

                        //埋点开始
                        BuriedPointUtil.buriedPoint("回款日历回款时间详细");
                        //埋点结束

                    }
                });
                returned_time_list.addView(view);
            }

            //得到滚动条的宽度
            int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            returned_time_hsv.measure(w, h);
            scrollViewWidth = returned_time_hsv.getMeasuredWidth();
            offset = scrollViewWidth / 10;
        }
    }

    /**
     * 设置项目列表
     */
    private void setOddsList(final int day) {

        if (repaymentsBean != null) {
            list_view.setAdapter(new QuickAdapter<RepaymentsBean.Data.Repayments.Records>(CalendarActivity.this, R.layout.item_project, repaymentsBean.data.repayments.get(day - 1).odds) {
                @Override
                protected void convert(BaseAdapterHelper helper, RepaymentsBean.Data.Repayments.Records item) {

                    helper.setText(R.id.tv_oddTitle, item.oddTitle);
                    helper.setText(R.id.tv_oddMoney, item.money);
                    helper.setText(R.id.tv_oddReward, decimalFormat.format(item.oddYearRate * 100));
                    helper.setText(R.id.tv_oddPeriod, item.oddPeriod.replace("个月", ""));
                    DonutProgress progress = helper.getView(R.id.dp_schedule);
                    progress.setProgress(100);
                    if (item.status == 0) {
                        progress.setText("未回款");
                    } else {
                        progress.setText("已回款");
                    }

                    //判断是否是新手标
                    if (item.oddReward > 0) {
                        helper.setVisible(R.id.tv_rewards, true);
                        helper.setText(R.id.tv_rewards, "+" + decimalFormat.format(item.oddReward * 100) + "%");
                    } else {
                        helper.setVisible(R.id.tv_rewards, false);
                    }
                }

            });
        }
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                BuriedPointUtil.buriedPoint("回款日历标的详情");

                Intent intent = new Intent(CalendarActivity.this, ProjectDetailsActivity.class);
                intent.putExtra("oddNumber", repaymentsBean.data.repayments.get(day - 1).odds.get(position).oddNumber);
                startActivity(intent);
            }
        });
    }

    /**
     * 添加头部的年份 闰哪月等信息
     *
     * @param view
     */
    public void addTextToTopTextView(TextView view) {
        StringBuffer textDate = new StringBuffer();
        textDate.append(calV.getShowYear())
                .append("年")
                .append(calV.getShowMonth())
                .append("月")
                .append("\t");
        view.setText(textDate);
    }

    /**
     * 移动到下一个月
     *
     * @param gvFlag
     */
    private void enterNextMonth(int gvFlag) {
        addGridView(); // 添加一个gridView
        jumpMonth++; // 下一个月
        day_c = 1;

        calV = new CalendarAdapter(this, jumpMonth, jumpYear, year_c, month_c, day_c);
        gridView.setAdapter(calV);
        addTextToTopTextView(tv_month); // 移动到下一月后，将当月显示在头标题中
        gvFlag++;
        flipper.addView(gridView, gvFlag);
        flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
        flipper.showNext();
        flipper.removeViewAt(0);

        getData();
    }

    /**
     * 移动到上一个月
     *
     * @param gvFlag
     */
    private void enterPrevMonth(int gvFlag) {
        addGridView(); // 添加一个gridView
        jumpMonth--; // 上一个月
        day_c = 1;

        calV = new CalendarAdapter(this, jumpMonth, jumpYear, year_c, month_c, day_c);
        gridView.setAdapter(calV);
        gvFlag++;
        addTextToTopTextView(tv_month); // 移动到上一月后，将当月显示在头标题中
        flipper.addView(gridView, gvFlag);

        flipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_in));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.push_right_out));
        flipper.showPrevious();
        flipper.removeViewAt(0);

        getData();
    }

    /**
     * 添加日历
     */
    private void addGridView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        // 取得屏幕的宽度和高度
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        int Width = display.getWidth();
        int Height = display.getHeight();

        gridView = new CalendarGridView(this);
        gridView.setNumColumns(7);
        gridView.setColumnWidth(40);
        if (Width == 720 && Height == 1280) {
            gridView.setColumnWidth(40);
        }
        gridView.setGravity(Gravity.CENTER_VERTICAL);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        // 去除gridView边框
        gridView.setVerticalSpacing(1);
        gridView.setHorizontalSpacing(1);

        gridView.setOnTouchListener(new View.OnTouchListener() {
            // 将gridview中的触摸事件回传给gestureDetector
            public boolean onTouch(View v, MotionEvent event) {
                return CalendarActivity.this.gestureDetector.onTouchEvent(event);
            }
        });
        gridView.setParentScrollView(vertical_scroll_view);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                //屏蔽掉其他月份的点击
                if (position < calV.getEndPosition() && position >= calV.getStartPositon()) {

                    String scheduleYear = calV.getShowYear();
                    String scheduleMonth = calV.getShowMonth();
                    String scheduleDay = calV.getDateByClickItem(position).split("\\.")[0]; // 这一天的阳历

                    StringBuffer textDate = new StringBuffer();
                    textDate.append(scheduleYear).append("-").append(scheduleMonth).append("-").append(scheduleDay);
                    tv_selected_time.setText(textDate);

                    setOddsList(Integer.valueOf(calV.getDateByClickItem(position).split("\\.")[0]));
                    setCheckedData(Integer.valueOf(calV.getDateByClickItem(position).split("\\.")[0]));

                    //埋点开始
                    BuriedPointUtil.buriedPoint("回款日历日期点击");
                }
            }
        });
        gridView.setLayoutParams(params);
    }

    DatePickerDialog datePickerDialog;

    @OnClick({R.id.btn_prev_month, R.id.btn_next_month, R.id.ll_month, R.id.returned_time_left, R.id.returned_time_right,R.id.buf_left,R.id.buf_right})
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_month://点击选择月份
                //弹出日期选择对话框
                if (datePickerDialog == null) {
                    datePickerDialog = new DatePickerDialog(
                            CalendarActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                    //初始化跳转参数
                                    jumpMonth = 0;
                                    jumpYear = 0;

                                    //重新添加一个日历
                                    calV = new CalendarAdapter(
                                            CalendarActivity.this,
                                            year,
                                            monthOfYear + 1,
                                            dayOfMonth);

                                    year_c = year;
                                    month_c = monthOfYear + 1;
                                    day_c = dayOfMonth;

                                    addGridView();
                                    gvFlag = 0;
                                    gridView.setAdapter(calV);
                                    flipper.removeViewAt(0);
                                    flipper.addView(gridView, gvFlag);

                                    addTextToTopTextView(tv_month);

                                    getData();
                                }
                            },
                            year_c,
                            month_c,
                            day_c);
                }

                datePickerDialog.show();
                break;

            case R.id.btn_prev_month:
                enterPrevMonth(gvFlag);
                //埋点
                BuriedPointUtil.buriedPoint("回款日历上个月按钮");
                break;
            case R.id.btn_next_month:
                enterNextMonth(gvFlag);
                //埋点
                BuriedPointUtil.buriedPoint("回款日历下个月按钮");
                break;
            case R.id.returned_time_left://底部日期滚动条向左边滚动
                returned_time_hsv.smoothScrollBy(-offset, 0);
                //埋点
                BuriedPointUtil.buriedPoint("回款日历回款时间左");
                break;
            case R.id.returned_time_right:
                returned_time_hsv.smoothScrollBy(+offset, 0);
                //埋点
                BuriedPointUtil.buriedPoint("回款日历回款时间右");
                break;
            case R.id.buf_left://底部日期滚动条向左边滚动
                mhorizontalScrollView.smoothScrollBy(-offset, 0);
                //埋点
                BuriedPointUtil.buriedPoint("回款日历回款时间左");
                break;
            case R.id.buf_right:
                mhorizontalScrollView.smoothScrollBy(+offset, 0);
                //埋点
                BuriedPointUtil.buriedPoint("回款日历回款时间右");
                break;
        }
    }
}
