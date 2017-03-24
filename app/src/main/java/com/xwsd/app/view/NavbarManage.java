package com.xwsd.app.view;

import android.app.Activity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xwsd.app.R;


/**
 * 管理导航栏各控件的显示影藏
 */
public class NavbarManage {
    private Activity activity;
    private View rootView;
    private Toolbar toolbar;
    public TextView navbar_left_text;
    public TextView navbar_centre;
    public TextView navbar_right_text;
    private ImageView navbar_left_image;
    private ImageView navbar_right_image;
    public LinearLayout navbar_left;
    public LinearLayout navbar_right;
    private OnLeftClickListener onLeftClickListener;
    private OnRightClickListener onRightClickListener;

    /**
     * 在Activity中使用导航栏
     *
     * @param activity
     */
    public NavbarManage(Activity activity) {
        this.activity = activity;
        init();
    }

    /**
     * 在Fragment中使用导航栏
     *
     * @param view
     */
    public NavbarManage(Activity activity, View view) {
        this.activity = activity;
        this.rootView = view;
        init();
    }


    private void init() {
        if (rootView == null) {
            toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
            navbar_left_text = (TextView) activity.findViewById(R.id.navbar_left_text);
            navbar_centre = (TextView) activity.findViewById(R.id.navbar_centre);
            navbar_right_text = (TextView) activity.findViewById(R.id.navbar_right_text);
            navbar_left = (LinearLayout) activity.findViewById(R.id.navbar_left);
            navbar_right = (LinearLayout) activity.findViewById(R.id.navbar_right);
            navbar_left_image = (ImageView) activity.findViewById(R.id.navbar_left_image);
            navbar_right_image = (ImageView) activity.findViewById(R.id.navbar_right_image);

        } else {
            toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
            navbar_left_text = (TextView) rootView.findViewById(R.id.navbar_left_text);
            navbar_centre = (TextView) rootView.findViewById(R.id.navbar_centre);
            navbar_right_text = (TextView) rootView.findViewById(R.id.navbar_right_text);
            navbar_left = (LinearLayout) rootView.findViewById(R.id.navbar_left);
            navbar_right = (LinearLayout) rootView.findViewById(R.id.navbar_right);
            navbar_left_image = (ImageView) rootView.findViewById(R.id.navbar_left_image);
            navbar_right_image = (ImageView) rootView.findViewById(R.id.navbar_right_image);

        }
    }

    //设置控件显示----------------------------------------------------------------------

    /**
     * 设置显示中间标题
     */
    public void showCentre(boolean showTitle) {
        if (showTitle) {
            navbar_centre.setVisibility(View.VISIBLE);
        } else {
            navbar_centre.setVisibility(View.GONE);
        }
    }


    /**
     * 设置显示左边按钮
     */
    public void showLeft(boolean showBack) {
        if (showBack) {
            navbar_left.setVisibility(View.VISIBLE);
        } else {
            navbar_left.setVisibility(View.GONE);
        }
    }

    /**
     * 设置显示右边按钮
     */
    public void showRight(boolean showBill) {
        if (showBill) {
            navbar_right.setVisibility(View.VISIBLE);
        } else {
            navbar_right.setVisibility(View.GONE);
        }
    }


    //设置控件内容----------------------------------------------------------------------


    /**
     * 设置背景颜色
     */
    public void setBackground(int colorId) {
        toolbar.setBackgroundColor(activity.getResources().getColor(colorId));
    }

    /**
     * 设置字体颜色
     */
    public void setTextColor(int colorId) {
        navbar_left_text.setTextColor(activity.getResources().getColor(colorId));
        navbar_centre.setTextColor(activity.getResources().getColor(colorId));
        navbar_right_text.setTextColor(activity.getResources().getColor(colorId));
    }

    /**
     * 设置左边文本
     */
    public void setLeftStr(String str) {
        navbar_left_text.setText(str);
    }


    /**
     * 设置中间文本
     */
    public void setCentreStr(String str) {
        navbar_centre.setText(str);
    }

    /**
     * 设置右边文本
     */
    public void setRightStr(String str) {
        navbar_right_text.setText(str);
    }

    /**
     * 设置左边图片
     */
    public void setLeftImg(int imgId) {
        navbar_left_image.setBackgroundDrawable(activity.getResources().getDrawable(imgId));
    }

    /**
     * 设置右边图片
     */
    public void setRightImg(int imgId) {
        navbar_right_image.setBackgroundDrawable(activity.getResources().getDrawable(imgId));
    }

    //设置按钮监听器----------------------------------------------------------------------

    /**
     * 设置左边按钮监听器
     */
    public void setOnLeftClickListener(OnLeftClickListener onLeftClickListener) {
        this.onLeftClickListener = onLeftClickListener;
        navbar_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NavbarManage.this.onLeftClickListener != null) {
                    NavbarManage.this.onLeftClickListener.onLeftClick();
                }
            }
        });

    }

    /**
     * 设置右边按钮监听器
     */
    public void setOnRightClickListener(OnRightClickListener onRightClickListener) {
        this.onRightClickListener = onRightClickListener;
        navbar_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NavbarManage.this.onRightClickListener != null) {
                    NavbarManage.this.onRightClickListener.onRightClick();
                }
            }
        });

    }


    //按钮监听接口----------------------------------------------------------------------

    /**
     * Menu按钮的事件监听
     */
    public interface OnRightClickListener {
        void onRightClick();
    }

    /**
     * Back按钮的事件监听
     */
    public interface OnLeftClickListener {
        void onLeftClick();
    }
}
