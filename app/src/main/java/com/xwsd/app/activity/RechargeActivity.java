package com.xwsd.app.activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import com.xwsd.app.R;
import com.xwsd.app.adapter.MyPagerAdapter;
import com.xwsd.app.base.MyBaseActivity;
import com.xwsd.app.bean.AgreeCardBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.fragment.fuyouMoneyFragment;
import com.xwsd.app.tools.ToastUtil;
import com.xwsd.app.view.MainViewPager;
import com.xwsd.app.view.NavbarManage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gx on 2016/8/24.
 * 充值
 */
public class RechargeActivity extends MyBaseActivity {
    //以下为富友支付相关参数
//    private String mMchnt_Key = "5old71wihg2tqjug9kkpxnhx9hiujoqj";测试
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    public  AgreeCardBean.Data.AgreeCard dataBaofu;
    public  AgreeCardBean.Data.AgreeCard dataFuyou;
    private List<android.app.Fragment> mFragments = new ArrayList<android.app.Fragment>();
    private FragmentManager fragmentManager;
    public MainViewPager viewPager;

/*
    @Bind(R.id.tab_project_left)
    TextView tab_project_left;*/

    @Bind(R.id.tab_project_right)
    TextView tab_project_right;
/*    *//**
     * 左边指示器
     *//*
    @Bind(R.id.indicator_left)
    View indicator_left;*/

    /**
     * 右边指示器
     */
    @Bind(R.id.indicator_right)
    View indicator_right;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_pay);
        navbarManage = new NavbarManage(this);
    }


    @Override
    protected void init(Bundle savedInstanceState) {
        title=getString(R.string.pay);
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.pay));
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
        dataBaofu = (AgreeCardBean.Data.AgreeCard)getIntent().getSerializableExtra(UserParam.DATA);
        dataFuyou = (AgreeCardBean.Data.AgreeCard)getIntent().getSerializableExtra(UserParam.DATA2);
        int num = 0;
        if(dataBaofu!=null && dataBaofu.bank_code.length() > 0){
       /*     mFragments.add(new baofuMoneyFragment());*/
        /*    tab_project_left.setOnClickListener(new MyOnClickListener(num));*/
            num++;
        }
        else {
        /*    tab_project_left.setOnClickListener(new MyNullClickListener());*/
            indicator_right.setVisibility(View.VISIBLE);
  /*          indicator_left.setVisibility(View.INVISIBLE);*/
        }
        if(dataFuyou!=null && dataFuyou.bank_code.length() > 0){
            mFragments.add(new fuyouMoneyFragment());
            tab_project_right.setOnClickListener(new MyOnClickListener(num));
            num++;
        }  else {
            tab_project_right.setOnClickListener(new MyNullClickListener());
        }
        fragmentManager = this.getFragmentManager();
        viewPager = new MainViewPager(this);
        viewPager = (MainViewPager) findViewById(R.id.viewPager);
        //viewPager.setSlipping(false);//设置ViewPager是否可以滑动
        viewPager.setOffscreenPageLimit(num);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        viewPager.setAdapter(new MyPagerAdapter(mFragments,fragmentManager));

    }


    @Override
    protected void onResume() {

        super.onResume();
    }
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        public MyOnPageChangeListener(){

        }

        @Override
        public void onPageSelected(int arg0) {
            switch (arg0) {
                case 0:
   /*                 indicator_left.setVisibility(View.VISIBLE);
                    indicator_right.setVisibility(View.INVISIBLE);
                    BuriedPointUtil.buriedPoint("充值页面-宝付支付");*/
                    break;
                case 1:
/*                    indicator_right.setVisibility(View.VISIBLE);
                    indicator_left.setVisibility(View.INVISIBLE);
                    BuriedPointUtil.buriedPoint("充值页面-富友支付");*/
                    break;
            }
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
    private class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }
    }
    private class MyNullClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ToastUtil.showToastShort("暂时没有开通");
        }
    }


}
