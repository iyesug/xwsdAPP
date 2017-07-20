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
import com.xwsd.app.bean.BanksLimitBean;
import com.xwsd.app.fragment.FuyouFragment;
import com.xwsd.app.view.MainViewPager;
import com.xwsd.app.view.NavbarManage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gx on 2016/9/8.
 * 设置充值银行卡
 */
public class SettingsPayBankActivity extends MyBaseActivity {
    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

/*    @Bind(R.id.tab_project_left)
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
    private List<android.app.Fragment> mFragments = new ArrayList<android.app.Fragment>();
    private FragmentManager fragmentManager;
    public MainViewPager viewPager;
    private List<BanksLimitBean> banksLimitBeens;
    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_settings_pay_bank);
        navbarManage = new NavbarManage(this);
    }
    @Override
    protected void init(Bundle savedInstanceState) {
        title=getString(R.string.settings_card);
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.settings_card));
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
/*        tab_project_left.setOnClickListener(new MyOnClickListener(0));
        tab_project_right.setOnClickListener(new MyOnClickListener(1));*/
   /*     mFragments.add(new BaofooFragment());*/
        mFragments.add(new FuyouFragment());
        fragmentManager = this.getFragmentManager();
        viewPager = new MainViewPager(this);
        viewPager = (MainViewPager) findViewById(R.id.viewPager);
        //viewPager.setSlipping(false);//设置ViewPager是否可以滑动
        viewPager.setOffscreenPageLimit(2);
        viewPager.setOnPageChangeListener(new SettingsPayBankActivity.MyOnPageChangeListener());
        viewPager.setAdapter(new MyPagerAdapter(mFragments,fragmentManager));
    }
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        public MyOnPageChangeListener(){

        }
        @Override
        public void onPageSelected(int arg0) {
            switch (arg0) {
                case 0:
/*                    indicator_left.setVisibility(View.VISIBLE);
                    indicator_right.setVisibility(View.INVISIBLE);*/
                    break;
                case 1:
/*                    indicator_right.setVisibility(View.VISIBLE);
                    indicator_left.setVisibility(View.INVISIBLE);*/
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
}
