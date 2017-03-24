package com.xwsd.app.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xwsd.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gx on 2016/8/19.
 * 主题筛选对话框
 */
public class ThemeScreenDialog {

    private Context mContext;
    /**
     * 参照View,pop会显示在这个View的正下方
     */
    private View referView;

    private PopupWindow popupWindow;

    View contentView;

    private ExpandableListView lsit_view;

    private String[] titles;

    private List<String[]> data = new ArrayList<String[]>() {
        {
            add(new String[]{"全部"});
            add(new String[]{"项目信息", "支付问题", "债权转让", "投资记录", "标的问题"});
            add(new String[]{"提现"});
            add(new String[]{"充值"});
            add(new String[]{"托管问题", "自动投标设置 ", "排名", "一麻袋 "});
            add(new String[]{"新手红包", "新手标"});
            add(new String[]{"新标预告", "公告", "登陆", "信息认证", "推荐奖励", "利息管理费", "积分", "回款日历", "手续费", "收费标准"});
        }
    };

    public ThemeScreenDialog(Context context, View view, boolean showAll) {
        if (showAll) {
            titles = new String[]{"全部", "投资详情", "提现", "充值", "自动投标", "新手问题", "其他"};

            data = new ArrayList<String[]>() {
                {
                    add(new String[]{"全部"});
                    add(new String[]{"项目信息", "支付问题", "债权转让", "投资记录", "标的问题"});
                    add(new String[]{"提现"});
                    add(new String[]{"充值"});
                    add(new String[]{"托管问题", "自动投标设置 ", "排名", "一麻袋 "});
                    add(new String[]{"新手红包", "新手标"});
                    add(new String[]{"新标预告", "公告", "登陆", "信息认证", "推荐奖励", "利息管理费", "积分", "回款日历", "手续费", "收费标准"});
                }
            };
        } else {
            titles = new String[]{"投资详情", "提现", "充值", "自动投标", "新手问题", "其他"};

            data = new ArrayList<String[]>() {
                {
                    add(new String[]{"项目信息", "支付问题", "债权转让", "投资记录", "标的问题"});
                    add(new String[]{"提现"});
                    add(new String[]{"充值"});
                    add(new String[]{"托管问题", "自动投标设置 ", "排名", "一麻袋 "});
                    add(new String[]{"新手红包", "新手标"});
                    add(new String[]{"新标预告", "公告", "登陆", "信息认证", "推荐奖励", "利息管理费", "积分", "回款日历", "手续费", "收费标准"});
                }
            };
        }
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
                R.layout.dialog_theme_screen, null);

        lsit_view = (ExpandableListView) contentView.findViewById(R.id.lsit_view);

        popupWindow = new PopupWindow(
                contentView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(
                R.drawable.bg_screed_dialog));

        final ThemeAdapter leftArrayAdapter = new ThemeAdapter();
        lsit_view.setAdapter(leftArrayAdapter);
//        设置条目的点击事件
        lsit_view.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(data.get(groupPosition)[childPosition]);
                }
                dismiss();
                return true;
            }
        });
    }

    public interface OnItemClickListener {
        /**
         * 条目点击监听
         *
         * @param data 数据
         */
        void onClick(String data);
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
                popupWindow.showAtLocation(referView, Gravity.NO_GRAVITY, (location[0] + referView.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);
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
     * 聊天列表的适配器
     */
    private class ThemeAdapter extends BaseExpandableListAdapter {
        @Override
        public int getGroupCount() {
            return titles.length;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return data.get(groupPosition).length;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return titles[groupPosition];
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return data.get(groupPosition)[childPosition];
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_screen_text, null);
            TextView text = (TextView) convertView;
            text.setText(titles[groupPosition]);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_screen_text, null);
            TextView text = (TextView) convertView;
            text.setText(data.get(groupPosition)[childPosition]);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
