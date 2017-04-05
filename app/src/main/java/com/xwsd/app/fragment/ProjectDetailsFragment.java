package com.xwsd.app.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.bm.library.PhotoView;
import com.bumptech.glide.Glide;
import com.gnwai.smartimageview.SmartImageView;
import com.xwsd.app.R;
import com.xwsd.app.activity.BidDetailsActivity;
import com.xwsd.app.activity.ProjectDetailTabActivity;
import com.xwsd.app.base.BaseFragment;
import com.xwsd.app.bean.OddBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.BuriedPointUtil;
import com.xwsd.app.view.TitleTextView;

/**
 * Created by Gx on 2016/8/23.
 * 标的详情-项目详情
 */
public class ProjectDetailsFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.ttv_1)
    TitleTextView ttv_1;

    @Bind(R.id.ttv_2)
    TitleTextView ttv_2;

    @Bind(R.id.ttv_3)
    TitleTextView ttv_3;

    @Bind(R.id.ttv_11)
    TitleTextView ttv_11;

    @Bind(R.id.ttv_12)
    TitleTextView ttv_12;

    @Bind(R.id.ttv_13)
    TitleTextView ttv_13;

    @Bind(R.id.ttv_14)
    TitleTextView ttv_14;

    @Bind(R.id.ttv_15)
    TitleTextView ttv_15;

    @Bind(R.id.ttv_16)
    TitleTextView ttv_16;

/*    @Bind(R.id.view_pager)
    ViewPager view_pager;*/

//    ProjectDetailsActivity projectDetailsActivity;

    OddBean oddBean;
    int data;

    @Bind(R.id.view_pager_botton)
    ViewPager view_pager_botton;

    @Override
    protected View setContentView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_project_details, null);
        return view;
    }

    @Override
    protected void init() {
//        projectDetailsActivity = (ProjectDetailsActivity) getActivity();
        data = getArguments().getInt(UserParam.DATA);
        if (data == 0) {
            oddBean = ((ProjectDetailTabActivity) getActivity()).oddBean;
        } else {
            oddBean = ((BidDetailsActivity) getActivity()).oddBean;
        }
      /*  view_pager.setAdapter(new TabFragmentPagerAdapter(getChildFragmentManager()));*/
        //设置轮播图
        view_pager_botton.setAdapter(new PicAdapter());
        setData();
    }

    private void setData() {
        //设置用户信息数据
        ttv_1.setTitle("昵称:");
        ttv_1.setContent(oddBean.data.user1.username);
        ttv_2.setTitle("年龄:");
        ttv_2.setContent(oddBean.data.user1.age);
        ttv_3.setTitle("婚姻:");
        ttv_3.setContent(oddBean.data.user1.marital);

        ttv_11.setTitle("车辆品牌型号:");
        ttv_12.setTitle("行驶公里数:");
        ttv_13.setTitle("车身颜色:");
        ttv_14.setTitle("排量:");
        ttv_15.setTitle("购买价格:");
        ttv_16.setTitle("抵押估价:");
        if (oddBean.data.oddLoanRemark != null) {

            if (oddBean.data.oddLoanRemark.车辆品牌型号 != null) {
                ttv_11.setContent(oddBean.data.oddLoanRemark.车辆品牌型号);
            }

            if (oddBean.data.oddLoanRemark.行驶公里数 != null) {
                ttv_12.setContent(oddBean.data.oddLoanRemark.行驶公里数);
            }
            if (oddBean.data.oddLoanRemark.车身颜色 != null) {
                ttv_13.setContent(oddBean.data.oddLoanRemark.车身颜色);
            }

            if (oddBean.data.oddLoanRemark.排量 != null) {
                ttv_14.setContent(oddBean.data.oddLoanRemark.排量);
            }

            if (oddBean.data.oddLoanRemark.购买价格 != null) {
                ttv_15.setContent(oddBean.data.oddLoanRemark.购买价格);
            }

            if (oddBean.data.oddLoanRemark.抵押估价 != null) {
                ttv_16.setContent(oddBean.data.oddLoanRemark.抵押估价);
            }
        }
    }

    @OnClick({R.id.iv_left_botton, R.id.iv_right_botton})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_left_botton:
                view_pager_botton.setCurrentItem(view_pager_botton.getCurrentItem() - 1);
                break;
            case R.id.iv_right_botton:
                view_pager_botton.setCurrentItem(view_pager_botton.getCurrentItem() + 1);
                break;
        }
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
            ProjectDetailsTabFragment projectDetailsTabFragment = new ProjectDetailsTabFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(UserParam.DATA, data);
            bundle.putInt(UserParam.TYPE, position);
            projectDetailsTabFragment.setArguments(bundle);


            return projectDetailsTabFragment;
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
            // 埋点开始
            BuriedPointUtil.buriedPoint("项目介绍项目详细信息滑动");

            return obj;
        }
    }

    class PicAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (oddBean == null || oddBean.data == null || oddBean.data.oddExteriorPhotos == null) {
                return 0;
            } else {
                return oddBean.data.oddExteriorPhotos.size();
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

        Dialog dialog;
        ViewPager mPager;
        TextView tv_index;

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.view_recommend_page_item, container, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog == null) {
                        dialog = new Dialog(getActivity(), R.style.DialogFullscreen);
                        dialog.setContentView(R.layout.dialog_photo_image);
                        //单击取消对话框
                        dialog.findViewById(R.id.tv_close).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        tv_index = (TextView) dialog.findViewById(R.id.tv_index);
                        mPager = (ViewPager) dialog.findViewById(R.id.pager);
                        mPager.setAdapter(new PagerAdapter() {
                            @Override
                            public int getCount() {
                                if (oddBean == null || oddBean.data == null || oddBean.data.oddExteriorPhotos == null) {
                                    return 0;
                                } else {
                                    return oddBean.data.oddExteriorPhotos.size();
                                }
                            }

                            @Override
                            public boolean isViewFromObject(View view, Object object) {
                                return view == object;
                            }

                            @Override
                            public Object instantiateItem(ViewGroup container, int position) {
                                final PhotoView view = new PhotoView(getActivity());
                                view.enable();
                                view.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                                view.setImageUrl(oddBean.data.oddExteriorPhotos.get(position).normal,
//                                        R.drawable.ic_load, R.drawable.ic_load);

                                Glide.with(getActivity())
                                        .load(oddBean.data.oddExteriorPhotos.get(position).normal)
                                        .crossFade()
                                        .into(view);
       /*                         ApiHttpClient.getMita(oddBean.data.oddExteriorPhotos.get(position).normal,new BitmapCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        System.out.println("e = " + e.toString());
                                    }
                                    @Override
                                    public void onResponse(Bitmap response, int id) {
                                        view.setImageBitmap(response);
                                    }
                                });*/
                                container.addView(view);

                                return view;
                            }

                            @Override
                            public void destroyItem(ViewGroup container, int position, Object object) {
                                container.removeView((View) object);
                            }
                        });

                        //设置索引
                        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                            }

                            @Override
                            public void onPageSelected(int position) {

                                tv_index.setText((position + 1) + "/" + oddBean.data.oddExteriorPhotos.size());
                                view_pager_botton.setCurrentItem(position);
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                                    //正在滑动   pager处于正在拖拽中

                                }
                            }
                        });
                    }

                    mPager.setCurrentItem(position);
                    tv_index.setText((position + 1) + "/" + oddBean.data.oddExteriorPhotos.size());
                    dialog.show();
                }
            });
            final SmartImageView imageView = (SmartImageView) view.findViewById(R.id.image);
//            imageView.setImageUrl(oddBean.data.oddExteriorPhotos.get(position).normal,
//                    R.drawable.ic_load, R.drawable.ic_load);
            Glide.with(getActivity())
                    .load(oddBean.data.oddExteriorPhotos.get(position).min)
                    .crossFade()
                    .into(imageView);
/*            ApiHttpClient.getMita(oddBean.data.oddExteriorPhotos.get(position).normal,new BitmapCallback() {
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

            // 埋点开始
            BuriedPointUtil.buriedPoint("项目介绍项目详情图片");

            return view;
        }
    }
}
