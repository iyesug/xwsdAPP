package com.xwsd.app.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import butterknife.Bind;
import com.xwsd.app.R;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.fragment.*;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.view.NavbarManage;

/**
 *
 * Created by Gy on 2017/6/27.
 * 账户充值
 */
public class AccountRechargeActivity extends BaseActivity implements TabLayout.OnTabSelectedListener{

    @Bind(R.id.view_pager)
    ViewPager view_pager;

    @Bind(R.id.tab_layout)
    TabLayout tab_FindFragment_title;



    private String[] titles;

    FastPaymentFragment fastPaymentFragment;
    AliPaymentFragment aliPaymentFragment;
    BankPaymentFragment bankPaymentFragment;




    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_account_recharge);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        title=getString(R.string.account_recharge);
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.account_recharge));
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

        titles = new String[]{
                getString(R.string.Quick_top_up),
                getString(R.string.Alipay_recharge),
                getString(R.string.bank_transfer)};

        fastPaymentFragment = new FastPaymentFragment();
        aliPaymentFragment=new AliPaymentFragment();
        bankPaymentFragment=new BankPaymentFragment();



        //设置缓存页数，防止出现高度计算错误的问题，影响上拉加载
        view_pager.setOffscreenPageLimit(5);
        view_pager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager()));

        tab_FindFragment_title.setOnTabSelectedListener(this);

        //为TabLayout添加tab名称
        for (int i = 0; i < titles.length; i++) {
            tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(titles[i]));
        }
        tab_FindFragment_title.setupWithViewPager(view_pager);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                BuriedPointUtil.buriedPoint("账户债权转让-可转让");
                break;
            case 1:
                BuriedPointUtil.buriedPoint("账户债权转让-转让中");
                break;
            case 2:
                BuriedPointUtil.buriedPoint("账户债权转让-转让记录");
                break;

        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    /**
     * Fragment的适配器
     */
    private class TabFragmentPagerAdapter extends FragmentPagerAdapter {

        public TabFragmentPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = fastPaymentFragment;
                    //埋点开始
                    BuriedPointUtil.buriedPoint("账户充值-快捷充值");
                    //埋点结束
                    break;
                case 1:
                    fragment = aliPaymentFragment;
                    //埋点开始
                    BuriedPointUtil.buriedPoint("账户充值-支付宝充值");
                    //埋点结束
                    break;
                case 2:
                    //埋点开始
                    BuriedPointUtil.buriedPoint("账户充值-银行充值");
                    //埋点结束
                    fragment = bankPaymentFragment;
                    break;

            }
            return fragment;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            switch (position) {
                case 0:
                    //埋点开始
                    BuriedPointUtil.buriedPoint("账户债权转让-可转让");
                    break;

                case 1:
                    //埋点开始
                    BuriedPointUtil.buriedPoint("账户债权转让-转让中");
                    break;

                case 2:
                    //埋点开始
                    BuriedPointUtil.buriedPoint("账户债权转让-转让记录");
                    break;


            }

            Object obj = super.instantiateItem(container, position);
            return obj;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
