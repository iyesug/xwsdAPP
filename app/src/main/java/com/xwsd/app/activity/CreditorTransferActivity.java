package com.xwsd.app.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import com.xwsd.app.R;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.fragment.CanTurnCreditorFragment;
import com.xwsd.app.fragment.OverusercrtrsFragment;
import com.xwsd.app.fragment.TransferRecordFragment;
import com.xwsd.app.fragment.TransfereeRecordFragment;
import com.xwsd.app.fragment.TransferingCreditorFragment;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.view.NavbarManage;

import butterknife.Bind;

/**
 *
 * Created by Gx on 2016/8/29.
 * 债权转让
 */
public class CreditorTransferActivity extends BaseActivity implements TabLayout.OnTabSelectedListener{

    @Bind(R.id.view_pager)
    ViewPager view_pager;

    @Bind(R.id.tab_layout)
    TabLayout tab_FindFragment_title;

    @Bind(R.id.horizontalScrollView)
    HorizontalScrollView mhorizontalScrollView;

    private String[] titles;

    CanTurnCreditorFragment canTurnCreditorFragment;
    TransferingCreditorFragment transferingCreditorFragment;
    TransferRecordFragment transferRecordFragment;
    TransfereeRecordFragment transfereeRecordFragment;
    OverusercrtrsFragment overusercrtrsFragment;

    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_creditor_transfer);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        //设置导航栏
        navbarManage.setCentreStr(getString(R.string.creditor_transfer));
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
                getString(R.string.can_turn_creditor),
                getString(R.string.transfering_creditor),
                getString(R.string.transfer_record),
                "持有中"
                ,"已回款"};

        canTurnCreditorFragment = new CanTurnCreditorFragment();
        transferingCreditorFragment = new TransferingCreditorFragment();
        transferRecordFragment = new TransferRecordFragment();
        transfereeRecordFragment = new TransfereeRecordFragment();
        overusercrtrsFragment = new OverusercrtrsFragment();

        //TODO 设置缓存页数，防止出现高度计算错误的问题，影响上拉加载
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
            case 3:
                BuriedPointUtil.buriedPoint("账户债权转让-持有中");
                break;
            case 4:
                BuriedPointUtil.buriedPoint("账户债权转让-已回款");
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
                    fragment = canTurnCreditorFragment;
                    //埋点开始
                    BuriedPointUtil.buriedPoint("账户债权转让-可转让");
                    //埋点结束
                    break;
                case 1:
                    fragment = transferingCreditorFragment;
                    //埋点开始
                    BuriedPointUtil.buriedPoint("账户债权转让-转让中");
                    //埋点结束
                    break;
                case 2:
                    //埋点开始
                    BuriedPointUtil.buriedPoint("账户债权转让-转让记录");
                    //埋点结束
                    fragment = transferRecordFragment;
                    break;
                case 3:
                    fragment = transfereeRecordFragment;
                    //埋点开始
                    BuriedPointUtil.buriedPoint("账户债权转让-持有中");
                    //埋点结束
                    break;
                case 4:
                    fragment = overusercrtrsFragment;
                    BuriedPointUtil.buriedPoint("账户债权转让-已回款");
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

                case 3:
                    //埋点开始
                    BuriedPointUtil.buriedPoint("账户债权转让-持有中");
                    break;
                case 4:
                    //埋点开始
                    BuriedPointUtil.buriedPoint("账户债权转让-已回款");
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
