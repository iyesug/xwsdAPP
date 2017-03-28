package com.xwsd.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xwsd.app.R;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseFragment;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.view.SegmentedGroup;

import butterknife.Bind;

/**
 * Created by Gx on 2016/8/18.
 * 资讯(管理三个子页面：新标预告，公告讯息，新标预告)
 */
public class NewsFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    @Bind(R.id.view_pager)
    ViewPager view_pager;

    /**
     * 顶部TAB组
     */
    @Bind(R.id.segmented_group)
    SegmentedGroup segmented_group;

    /**
     * 新标预告
     */
    @Bind(R.id.rb_new_bid_notice)
    RadioButton rb_new_bid_notice;

    /**
     * 公告讯息
     */
    @Bind(R.id.rb_notice_message)
    RadioButton rb_notice_message;

    /**
     * 问题反馈
     */
    @Bind(R.id.rb_feedback)
    RadioButton rb_feedback;

    /**
     * 关于小微
     */
    @Bind(R.id.rb_aboutus)
    RadioButton rb_aboutus;

    /**
     * 新标预告
     */
    private ForeshowListFragment newBidNoticeFragment;

    /**
     * 公告讯息
     */
    private NoticeListFragment noticeMessageFragment;

    /**
     * 问题反馈
     */
    private FeedbackFragment feedbackFragment;

    /**
     * 关于小微
     */
    private AboutXWFragment aboutXWFragment;

    @Override
    protected View setContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_news, null);
        return view;
    }

    @Override
    protected void init() {
        view_pager.setOnPageChangeListener(this);
        rb_new_bid_notice.setChecked(true);
        view_pager.setAdapter(new TabFragmentPagerAdapter(getChildFragmentManager()));
        //TODO 设置缓存页数，防止出现高度计算错误的问题，影响上拉加载
        view_pager.setOffscreenPageLimit(3);
        segmented_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_new_bid_notice://新标预告
                        view_pager.setCurrentItem(0);
                        break;
                    case R.id.rb_notice_message://公告讯息
                        view_pager.setCurrentItem(1);
                        break;
                    case R.id.rb_feedback://问答反馈
                        view_pager.setCurrentItem(2);
                        break;
                    case R.id.rb_aboutus://问答反馈
                        view_pager.setCurrentItem(3);
                        break;
                }
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0://新标预告
                rb_new_bid_notice.setChecked(true);
                break;
            case 1://公告讯息
                rb_notice_message.setChecked(true);
                break;
            case 2://问答反馈
                rb_feedback.setChecked(true);
                break;
            case 3://关于小微
                rb_aboutus.setChecked(true);
                break;
        }
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
            switch (position) {
                case 0:
                    if (newBidNoticeFragment == null) {
                        newBidNoticeFragment = new ForeshowListFragment();
                        Bundle newBidNoticeBundle = new Bundle();
                        newBidNoticeBundle.putString(UserParam.DATA, ApiHttpClient.TYPE_INFOS_ANNOUNCE);
                        newBidNoticeFragment.setArguments(newBidNoticeBundle);
                    }
                    return newBidNoticeFragment;
                case 1:
                    if (noticeMessageFragment == null) {
                        noticeMessageFragment = new NoticeListFragment();
                        Bundle noticeMessageBundle = new Bundle();
                        noticeMessageBundle.putString(UserParam.DATA, ApiHttpClient.TYPE_INFOS_NOTICE);
                        noticeMessageFragment.setArguments(noticeMessageBundle);
                    }
                    return noticeMessageFragment;
                case 2:
                    if (feedbackFragment == null) {
                        feedbackFragment = new FeedbackFragment();
                    }
                    return feedbackFragment;
                case 3:
                    if (aboutXWFragment == null) {
                        aboutXWFragment = new AboutXWFragment();
                    }
                    return aboutXWFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
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
