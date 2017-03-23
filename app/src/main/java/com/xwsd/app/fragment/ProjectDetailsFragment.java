package com.xwsd.app.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
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

import com.bm.library.PhotoView;
import com.gnwai.smartimageview.SmartImageView;
import com.xwsd.app.R;
import com.xwsd.app.activity.BidDetailsActivity;
import com.xwsd.app.activity.ProjectDetailsActivity;
import com.xwsd.app.api.ApiHttpClient;
import com.xwsd.app.base.BaseFragment;
import com.xwsd.app.bean.OddBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.BuriedPointUtil;
import com.zhy.http.okhttp.callback.BitmapCallback;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Gx on 2016/8/23.
 * 标的详情-项目详情
 */
public class ProjectDetailsFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.view_pager)
    ViewPager view_pager;

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
            oddBean = ((ProjectDetailsActivity) getActivity()).oddBean;
        } else {
            oddBean = ((BidDetailsActivity) getActivity()).oddBean;
        }
        view_pager.setAdapter(new TabFragmentPagerAdapter(getChildFragmentManager()));
        //设置轮播图
        view_pager_botton.setAdapter(new PicAdapter());
    }

    @OnClick({R.id.iv_left, R.id.iv_right, R.id.iv_left_botton, R.id.iv_right_botton})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                view_pager.setCurrentItem(view_pager.getCurrentItem() - 1);
                break;
            case R.id.iv_right:
                view_pager.setCurrentItem(view_pager.getCurrentItem() + 1);
                break;
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
                                ApiHttpClient.getMita(oddBean.data.oddExteriorPhotos.get(position).normal,new BitmapCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        System.out.println("e = " + e.toString());
                                    }
                                    @Override
                                    public void onResponse(Bitmap response, int id) {
                                        view.setImageBitmap(response);
                                    }
                                });
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

            ApiHttpClient.getMita(oddBean.data.oddExteriorPhotos.get(position).normal,new BitmapCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    System.out.println("e = " + e.toString());
                }
                @Override
                public void onResponse(Bitmap response, int id) {
                    imageView.setImageBitmap(response);
                }
            });
            container.addView(view);

            // 埋点开始
            BuriedPointUtil.buriedPoint("项目介绍项目详情图片");

            return view;
        }
    }
}
