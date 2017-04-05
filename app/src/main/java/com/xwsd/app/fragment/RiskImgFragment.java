package com.xwsd.app.fragment;

import android.app.Dialog;
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
import com.xwsd.app.base.BaseFragment;
import com.xwsd.app.bean.OddrmBean;
import com.xwsd.app.constant.UserParam;
import com.xwsd.app.tools.BuriedPointUtil;

import java.util.List;

/**
 * Created by Gx on 2016/9/14.
 * 风险控制-图片
 */
public class RiskImgFragment extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.view_pager)
    ViewPager view_pager;

    public static final int TYPE_PROPERTY = 1;
    public static final int TYPE_BORROW_PROCEDURE = 2;
    public static final int TYPE_RISK = 3;
    public static final int TYPE_CHECKOUT = 4;
    public static final int TYPE_INVESTIGATION = 5;

    List<OddrmBean.Data.OddrmImg> oddrmImgs;

    @Override
    protected View setContentView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_risk_img, null);
    }

    @Override
    protected void init() {
        if (RiskControlFragment.oddrmBean != null && RiskControlFragment.oddrmBean.data != null) {

            switch (getArguments().getInt(UserParam.TYPE)) {
                case TYPE_PROPERTY:
                    oddrmImgs = RiskControlFragment.oddrmBean.data.oddPropertyPhotos;
                    break;
                case TYPE_BORROW_PROCEDURE:
                    oddrmImgs = RiskControlFragment.oddrmBean.data.oddLoanPhotos;
                    break;
                case TYPE_RISK:
                    oddrmImgs = RiskControlFragment.oddrmBean.data.controlPhotos;
                    break;
                case TYPE_CHECKOUT:
                    oddrmImgs = RiskControlFragment.oddrmBean.data.validateCarPhotos;
                    break;
                case TYPE_INVESTIGATION:
                    oddrmImgs = RiskControlFragment.oddrmBean.data.bankCreditReport;
                    break;
            }

            if (oddrmImgs != null && oddrmImgs.size() > 0) {
                //设置轮播图
                view_pager.setAdapter(new PicAdapter());
            }
        }

    }

    @OnClick({R.id.iv_left, R.id.iv_right})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                view_pager.setCurrentItem(view_pager.getCurrentItem() - 1);

                //埋点开始
                BuriedPointUtil.buriedPoint("项目介绍风控材料产权图片点击");

                break;
            case R.id.iv_right:
                view_pager.setCurrentItem(view_pager.getCurrentItem() + 1);

                //埋点开始
                BuriedPointUtil.buriedPoint("项目介绍风控材料产权图片点击");

                break;
        }
    }

    class PicAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return oddrmImgs.size();
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
                                return oddrmImgs.size();
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
                                Glide.with(getActivity())
                                        .load(oddrmImgs.get(position).normal)
                                        .crossFade()
                                        .into(view);
/*                                ApiHttpClient.getMita(oddrmImgs.get(position).normal, new BitmapCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {

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
                                view_pager.setCurrentItem(position);
                                tv_index.setText((position + 1) + "/" + oddrmImgs.size());
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                            }
                        });
                    }

                    mPager.setCurrentItem(position);
                    tv_index.setText((position + 1) + "/" + oddrmImgs.size());
                    dialog.show();
                }
            });
            final SmartImageView imageView = (SmartImageView) view.findViewById(R.id.image);
            //imageView.setImageUrl(oddrmImgs.get(position).normal, R.drawable.ic_load, R.drawable.ic_load);
            Glide.with(getActivity())
                    .load(oddrmImgs.get(position).min)
                    .crossFade()
                    .into(imageView);
/*            ApiHttpClient.getMita(oddrmImgs.get(position).min, new BitmapCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public void onResponse(Bitmap response, int id) {
                    imageView.setImageBitmap(response);
                }
            });*/
            container.addView(view);

            //埋点开始
            BuriedPointUtil.buriedPoint("项目介绍风控材料产权图片点击");

            return view;
        }
    }
}
