package com.xwsd.app.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.*;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.bigkoo.pickerview.OptionsPickerView;
import com.gnwai.iosdialog.ActionSheetDialog;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.bean.BankCardBean;
import com.xwsd.app.bean.CardBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.tools.SpanUtils;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.MADialog;
import com.xwsd.app.view.NavbarManage;
import com.zhy.http.okhttp.request.RequestCall;
import me.drakeet.materialdialog.MaterialDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.R.attr.textSize;

/**
 * Created by Gy on 2017/7/21.
 * 申请约标
 */
public class BookingActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;
    public static boolean needRefresh = false;
    RequestCall call;
    OptionsPickerView pvCustomOptions;
    MaterialDialog bankDialog;

    BankCardBean banks;
    float bigAmount= (float) 50000;
    boolean big=false;


    @Bind(R.id.tv_money)
    TextView tv_money;


    @Bind(R.id.tv_duration)
    TextView tv_duration;


    @Bind(R.id.tv_date)
    TextView tv_date;


    Dialog payDialog;

    DecimalFormat decimalFormat = new DecimalFormat("0.00");

    private ArrayList<CardBean> moneyList = new ArrayList<>();
    private ArrayList<CardBean> durationList = new ArrayList<>();
    DatePickerDialog datePickerDialog;
    Calendar calendar = Calendar.getInstance();

    private int year_c;

    private int month_c;

    private int day_c;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_booking);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        title=getString(R.string.booking);
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.booking));
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
//        判断用户是否登录
        if (AppContext.getUserBean() == null||null== AppContext.getUserBean().data) {
            Intent intent = new Intent(this, UserActivity.class);
            intent.putExtra(UserParam.TYPE, UserActivity.TYPE_LOGIN);
            intent.putExtra(UserParam.NEED_ENTER_ACCOUNT, true);
            startActivity(intent);
            return;
        }
        setPickView();

        tv_money.setText(new SpanUtils()
                .appendLine("SpanUtils").setBackgroundColor(Color.LTGRAY).setBold().setForegroundColor(Color.YELLOW).setAlign(Layout.Alignment.ALIGN_CENTER)
                .appendLine("前景色").setForegroundColor(Color.GREEN)
                .appendLine("背景色").setBackgroundColor(Color.LTGRAY)

                .appendLine("测试段落缩，首行缩进两字，其他行不缩进").setLeadingMargin((int) textSize * 2, 10).setBackgroundColor(Color.GREEN)
                .appendLine("测试引用，后面的字是为了凑到两行的效果").setQuoteColor(Color.GREEN, 10, 10).setBackgroundColor(Color.LTGRAY)
                .appendLine("测试列表项，后面的字是为了凑到两行的效果").setBullet(Color.GREEN, 20, 10).setBackgroundColor(Color.LTGRAY).setBackgroundColor(Color.GREEN)

                .appendLine("2倍字体").setFontProportion(2)
                .appendLine("横向2倍字体").setFontXProportion(1.5f)
                .appendLine("删除线").setStrikethrough()
                .appendLine("下划线").setUnderline()
                .append("测试").appendLine("上标").setSuperscript()
                .append("测试").appendLine("下标").setSubscript()
                .appendLine("粗体").setBold()
                .appendLine("斜体").setItalic()
                .appendLine("粗斜体").setBoldItalic()
                .appendLine("monospace字体").setFontFamily("monospace")
                .appendLine("相反对齐").setAlign(Layout.Alignment.ALIGN_OPPOSITE)
                .appendLine("居中对齐").setAlign(Layout.Alignment.ALIGN_CENTER)
                .appendLine("正常对齐").setAlign(Layout.Alignment.ALIGN_NORMAL)
                .append("测试").appendLine("Url").setUrl("https://github.com/Blankj/AndroidUtilCode")
                .append("测试").appendLine("模糊").setBlur(3, BlurMaskFilter.Blur.NORMAL)


                .append("测试空格").appendSpace(30, Color.LTGRAY).appendSpace(50, Color.GREEN).appendSpace(100).appendSpace(30, Color.LTGRAY).appendSpace(50, Color.GREEN)
                .create());



    }

    @OnClick({R.id.ll_money,  R.id.ll_duration, R.id.ll_date,R.id.commit,R.id.iv_money,R.id.iv_duration,R.id.iv_date,R.id.tv_date,R.id.tv_duration,R.id.tv_money})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commit://提交
                BuriedPointUtil.buriedPoint("申请约标-提交");
                if ("请选择".equals(tv_money.getText().toString())) {
                    ToastUtil.showToastShort("请选择约标金额");
                    return;
                }
                if ("请选择".equals(tv_duration.getText().toString())) {
                    ToastUtil.showToastShort("请选择约标期限");
                    return;
                }
                if ("请选择".equals(tv_date.getText().toString())) {
                    ToastUtil.showToastShort("请选择约标日期");
                    return;
                }
                //确认对话框
                final MADialog mMDialog = new MADialog(this);
                mMDialog.setMessage("确认提交约标信息?");//
                mMDialog.setBtnOK("确定", v1 -> {
                    mMDialog.miss();
                    // TODO: 2017/8/2 提交约标信息 
                });
                mMDialog.setBtnCancel("暂不提交", v12 -> mMDialog.miss());
                break;


            case R.id.tv_date://日期
                //弹出日期选择对话框

                if (datePickerDialog == null) {

                    //设置日历
                    year_c = calendar.get(Calendar.YEAR);
                    month_c = calendar.get(Calendar.MONTH);
                    day_c = calendar.get(Calendar.DAY_OF_MONTH);
                    datePickerDialog = new DatePickerDialog(
                            BookingActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                    year_c = year;
                                    month_c = monthOfYear + 1;
                                    day_c = dayOfMonth;
                                    tv_date.setText(year_c+"-"+month_c+"-"+day_c);
                                }
                            },
                            year_c,
                            month_c,
                            day_c);
                }

                datePickerDialog.show();



                break;
            case R.id.ll_duration://期限
            case R.id.iv_duration://期限
            case R.id.tv_duration://期限
                new ActionSheetDialog(this)
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .setTitle("预约期限")
                        .addSheetItem(durationList.get(0).getCardNo(), ActionSheetDialog.SheetItemColor.Gray,
                                which -> {
                                    tv_duration.setText(durationList.get(0).getCardNo());
                                })
                        .addSheetItem(durationList.get(1).getCardNo(), ActionSheetDialog.SheetItemColor.Gray,
                                which -> {
                                    tv_duration.setText(durationList.get(1).getCardNo());

                                })
                        .addSheetItem(durationList.get(2).getCardNo(), ActionSheetDialog.SheetItemColor.Gray,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        tv_duration.setText(durationList.get(2).getCardNo());

                                    }
                                })
                        .show();

                break;
            case R.id.ll_money:
            case R.id.iv_money://金额
            case R.id.tv_money://金额
                new ActionSheetDialog(this)
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .setTitle("预约金额")
                        .addSheetItem(moneyList.get(0).getCardNo(), ActionSheetDialog.SheetItemColor.Gray,
                                which -> {
                                    tv_money.setText(moneyList.get(0).getCardNo());
                                })
                        .addSheetItem(moneyList.get(1).getCardNo(), ActionSheetDialog.SheetItemColor.Gray,
                                which -> {
                                    tv_money.setText(moneyList.get(1).getCardNo());

                                })
                        .addSheetItem(moneyList.get(2).getCardNo(), ActionSheetDialog.SheetItemColor.Gray,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        tv_money.setText(moneyList.get(2).getCardNo());

                                    }
                                })
                        .show();
                break;
        }
    }

    private void setPickView() {
        moneyList.add(0,new CardBean(1,"5万—10万"));
        moneyList.add(1,new CardBean(2,"10万—20万"));
        moneyList.add(2,new CardBean(3,"20万以上"));

        durationList.add(0,new CardBean(1,"6月标"));
        durationList.add(1,new CardBean(2,"12月标"));
        durationList.add(2,new CardBean(3,"24月标"));
    }


    @Override
    public void onResume() {
        super.onResume();
        if (needRefresh) {
            needRefresh = false;
        }
    }
}
