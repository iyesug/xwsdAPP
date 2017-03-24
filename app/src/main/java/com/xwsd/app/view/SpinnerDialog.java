package com.xwsd.app.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.xwsd.app.R;

/**
 * Created by Gx on 2016/8/30.
 * 下拉列表对话框
 */
public class SpinnerDialog {

    private Context mContext;
    /**
     * 参照View,pop会显示在这个View的正下方
     */
    private View referView;

    private PopupWindow popupWindow;

    private ListView lsit_view;

    private String[] data;

    View contentView;

    public SpinnerDialog(Context context, View view) {
        this.mContext = context;
        this.referView = view;
        init();
    }

    /**
     * 初始化对话框
     */
    private void init() {
        // 一个自定义的布局，作为显示的内容
        contentView = LayoutInflater.from(mContext).inflate(
                R.layout.dialog_spinner, null);

        lsit_view = (ListView) contentView.findViewById(R.id.lsit_view);

        popupWindow = new PopupWindow(
                contentView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(
                R.drawable.bg_spinner_dialog));
    }

    /**
     * 设置数据
     *
     * @param arr 数据
     */
    public void setData(String[] arr) {
        this.data = arr;
//        初始化适配器
        ArrayAdapter<String> leftArrayAdapter = new ArrayAdapter<String>(
                mContext,
                R.layout.item_screen_text,
                data);
        lsit_view.setAdapter(leftArrayAdapter);
//        设置条目的点击事件
        lsit_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(position, data[position]);
                }
                dismiss();
            }
        });
    }


    public interface OnItemClickListener {
        /**
         * 条目点击监听
         *
         * @param position 点击条目位置
         * @param data     数据
         */
        void onClick(int position, String data);
    }

    OnItemClickListener onItemClickListener;


    /**
     * 暴露条目点击接口
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 显示筛选窗
     */
    public void show() {
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAsDropDown(referView);
        }
    }

    /**
     * 显示筛选窗
     */
    public void show(int width) {
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.setWidth(width);
            popupWindow.showAsDropDown(referView);
        }
    }

    /**
     * 显示筛选窗
     */
    public void show(int width, boolean isDown) {
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.setWidth(width);
            if (isDown) {
                popupWindow.showAsDropDown(referView);
            } else {
                int[] location = new int[2];
                //手动测量一边，防止位置获取不准确
                contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                int popupWidth = contentView.getMeasuredWidth();
                int popupHeight = contentView.getMeasuredHeight();
                referView.getLocationOnScreen(location);

                popupWindow.showAtLocation(referView,
                        Gravity.NO_GRAVITY,
                        (location[0] + referView.getWidth() / 2) - popupWidth / 2,
                        location[1] - popupHeight);
            }
        }
    }

    /**
     * 关闭筛选窗
     */
    public void dismiss() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    /**
     * 设置窗体消失监听
     *
     * @param windowDismissListener
     */
    public void setWindowDismissListener(PopupWindow.OnDismissListener windowDismissListener) {
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.setOnDismissListener(windowDismissListener);
        }
    }
}
