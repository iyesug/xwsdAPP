package com.xwsd.app.oldapp.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import com.xwsd.app.R;
import com.xwsd.app.base.BaseActivity;
import com.xwsd.app.oldapp.fragment.TransferRecordFragment;
import com.xwsd.app.view.NavbarManage;

/**
 * Created by Gy on 2017/8/17.
 * 资金迁移记录
 */
public class TransferRecordActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    @Bind(R.id.view_pager)
    ViewPager view_pager;



    TransferRecordFragment rechargeRecordFragment;



    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_transfer_record);
        navbarManage = new NavbarManage(this);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        title=getString(R.string.money_transfer_record);
        //设置导航栏
        navbarManage.setCentreStr(title);
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



        rechargeRecordFragment = new TransferRecordFragment();
        view_pager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager()));
        view_pager.addOnPageChangeListener(this);
    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
            return rechargeRecordFragment;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            Object obj = super.instantiateItem(container, position);
            return obj;
        }
    }
}
