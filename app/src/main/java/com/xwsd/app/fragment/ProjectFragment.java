package com.xwsd.app.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.xwsd.app.AppContext;
import com.xwsd.app.R;
import com.xwsd.app.base.BaseFragment;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.view.DemoPopupWindow;
import com.xwsd.app.view.DoubleScreenDialog;
import com.xwsd.app.view.NavbarManage;

/**
 * Created by Gx on 2016/8/18.
 * 项目(管理两个子页面:全部标的/债权转让)
 */
public class ProjectFragment extends BaseFragment implements View.OnClickListener, ViewPager.OnPageChangeListener {

    /**
     * 导航栏
     */
    private NavbarManage navbarManage;

    @Bind(R.id.view_pager)
    ViewPager view_pager;

    /**
     * 左边TAB
     */
    @Bind(R.id.tab_project_left)
    TextView tab_project_left;

    /**
     * 左边指示器
     */
    @Bind(R.id.indicator_left)
    View indicator_left;

    /**
     * 右边指示器
     */
    @Bind(R.id.indicator_right)
    View indicator_right;


    @Bind(R.id.iv_arrows)
    ImageView iv_arrows;

    /**
     * 全部标
     */
    private ALLBidFragment allBidFragment;

    /**
     * 债券转让
     */
    private CreditorTransferFragment creditorTransferFragment;

    /**
     * 双选下拉对话框
     */
    private DoubleScreenDialog screenDialog;

    @Override
    protected View setContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_project, null);
        navbarManage = new NavbarManage(getActivity(), view);
        AppContext.setNeedLock(false);

        return view;
    }

    @Override
    protected void init() {
//        设置导航栏
        navbarManage.setCentreStr(getString(R.string.invest_money));
        navbarManage.showLeft(false);
        navbarManage.showRight(true);
        navbarManage.setRightImg(R.mipmap.jisuan);
        navbarManage.setOnRightClickListener(new NavbarManage.OnRightClickListener() {
            @Override
            public void onRightClick() {
                DemoPopupWindow pw = new DemoPopupWindow(getActivity());
                pw.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                pw.show(getActivity());
            }
        });

        navbarManage.setBackground(R.color.navbar_bg);

        allBidFragment = new ALLBidFragment();
        creditorTransferFragment = new CreditorTransferFragment();
        view_pager.setAdapter(new TabFragmentPagerAdapter(getChildFragmentManager()));
        view_pager.addOnPageChangeListener(this);
    }

    @OnClick({R.id.ll_tab_project_left, R.id.tab_project_right})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_tab_project_left:
                //判断是否重复点击全部标TAB
                if (view_pager.getCurrentItem() == 0 &&
                        allBidFragment.oddsBean != null &&
                        allBidFragment.oddsBean.mperiods != null &&
                        allBidFragment.oddsBean.mtypes != null) {
                    //如果当前显示的是全部标就弹出下拉筛选窗
                    if (screenDialog == null) {

                        screenDialog = new DoubleScreenDialog(getActivity(), indicator_left);

                        String[] types = new String[allBidFragment.oddsBean.mtypes.size()];
                        for (int i = 0; i < allBidFragment.oddsBean.mtypes.size(); i++) {
                            types[i] = allBidFragment.oddsBean.mtypes.get(i).content;
                        }

                        String[] periods = new String[allBidFragment.oddsBean.mperiods.size()];
                        for (int p = 0; p < allBidFragment.oddsBean.mperiods.size(); p++) {
                            periods[p] = allBidFragment.oddsBean.mperiods.get(p).content;
                        }

                        screenDialog.setData(periods, types);
                        screenDialog.setOnItemClickListener(new DoubleScreenDialog.OnItemClickListener() {
                            @Override
                            public void onClick(int type, int position, String data) {

                                allBidFragment.getScreenParam(type, data);
                            }
                        });

                        screenDialog.setWindowDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {

                                if (TextUtils.isEmpty(allBidFragment.period) && TextUtils.isEmpty(allBidFragment.type)) {
                                    setArrowsType(tab_project_left, iv_arrows, 0);
                                } else {
                                    setArrowsType(tab_project_left, iv_arrows, 1);
                                }
                            }
                        });
                    }

                    setArrowsType(tab_project_left, iv_arrows, 2);

                    screenDialog.show();
                    return;
                }
                updateIndicator(R.id.indicator_left);
                view_pager.setCurrentItem(0);

                //埋点开始
                BuriedPointUtil.buriedPoint("项目全部的标");

                break;
            case R.id.tab_project_right:
                if (view_pager.getCurrentItem() == 1) {
                    return;
                }
                updateIndicator(R.id.indicator_right);
                view_pager.setCurrentItem(1);

                //埋点开始
                BuriedPointUtil.buriedPoint("项目债权转让");

                break;
        }
    }

    /**
     * 设置箭头的类型
     *
     * @param tv
     * @param iv
     * @param type 0：初始状态；1：选中状态；2：下拉状态
     */
    private void setArrowsType(TextView tv, ImageView iv, int type) {

        switch (type) {
            case 0:
                tv.setTextColor(getResources().getColor(R.color.gray_text));
                iv.setImageResource(R.mipmap.ic_arrows_u_g);
                break;
            case 1:
                tv.setTextColor(getResources().getColor(R.color.blue_simple));
                iv.setImageResource(R.mipmap.ic_arrows_u_b);
                break;
            case 2:
                tv.setTextColor(getResources().getColor(R.color.blue_simple));
                iv.setImageResource(R.mipmap.ic_arrows_d_b);
                break;
        }
    }

    /**
     * 根据ID 更新指示器的状态
     *
     * @param id
     */
    private void updateIndicator(int id) {

        switch (id) {
            case R.id.indicator_left:
                indicator_left.setVisibility(View.VISIBLE);
                indicator_right.setVisibility(View.INVISIBLE);
                break;
            case R.id.indicator_right:
                indicator_left.setVisibility(View.INVISIBLE);
                indicator_right.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        updateIndicator(position == 0 ? R.id.indicator_left : R.id.indicator_right);
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
            return position == 0 ? allBidFragment : creditorTransferFragment;
        }

        @Override

        public int getCount() {
            return 2;
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
