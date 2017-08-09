package com.xwsd.app.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.gnwai.smartimageview.SmartImageView;
import com.xwsd.app.R;
import com.xwsd.app.base.BaseActivity;

import butterknife.Bind;

/**
 * Created by Gx on 2016/9/28.
 * 新手
 */
public class NoviceActivity extends BaseActivity {

    int[] imgId = {R.mipmap.ic_about_xw, R.mipmap.ic_about_xw, R.mipmap.ic_about_xw, R.mipmap.ic_about_xw,};

    @Bind(R.id.view_pager)
    ViewPager view_pager;

    @Override
    protected void onBeforeSetContentLayout() {
        setContentView(R.layout.activity_novice);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        title="新手";
        view_pager.setAdapter(new PicAdapter());
        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == imgId.length) {
                    onBackPressed();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class PicAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imgId.length + 1;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
            view.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            SmartImageView imageView = (SmartImageView) getLayoutInflater().inflate(R.layout.item_novice, container, false);
            if (position < imgId.length) {
                imageView.setImageResource(imgId[position]);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view_pager.setCurrentItem(view_pager.getCurrentItem() + 1);
                    }
                });
            }
            container.addView(imageView);
            return imageView;
        }

    }
}
