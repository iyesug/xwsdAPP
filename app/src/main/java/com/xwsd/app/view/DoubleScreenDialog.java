package com.xwsd.app.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xwsd.app.R;

/**
 * Created by Gx on 2016/8/19.
 * 全部标的筛选对话框
 */
public class DoubleScreenDialog {

    public static final int TYPE_LEFT = 0;

    public static final int TYPE_RIGHT = 1;

    private Context mContext;

    private View referView;

    private PopupWindow popupWindow;

    private ListView screen_list_left;

    private ListView screen_list_right;

    private String[] left_data;

    private String[] right_data;

    private int left_last_item = -1;

    private View left_oldView;

    private int right_last_item = -1;

    private View right_oldView;

    OnItemClickListener onItemClickListener;

    public DoubleScreenDialog(Context context, View view) {
        this.mContext = context;
        this.referView = view;
        init();
    }

    /**
     * 初始化对话框
     */
    private void init() {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(mContext).inflate(
                R.layout.dialog_all_bid_screen, null);

        screen_list_left = (ListView) contentView.findViewById(R.id.screen_list_left);
        screen_list_right = (ListView) contentView.findViewById(R.id.screen_list_right);

        contentView.findViewById(R.id.fill_view).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        popupWindow = new PopupWindow(
                contentView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(
                R.drawable.bg_screed_dialog));
    }

    /**
     * 设置数据
     *
     * @param data1 左边列表的数据
     * @param data2 右边列表的数据
     */
    public void setData(String[] data1, String[] data2) {
        this.left_data = data1;
        this.right_data = data2;

        screen_list_left.setAdapter(new MyAdapter(TYPE_LEFT, data1));
//        设置条目的点击事件
        screen_list_left.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                view.setBackgroundResource(R.color.gray);//把选中的颜色改变

                if (left_last_item != -1 && left_last_item != position) {//如果已经单击过条目并且上次保存的item位置和当前位置不同
                    left_oldView.setBackgroundResource(R.color.white);//把上次选中的样式去掉
                }

                left_oldView = view;//把当前的条目保存下来
                left_last_item = position;//把当前的位置保存下来

                if (onItemClickListener != null) {
                    onItemClickListener.onClick(TYPE_LEFT, position, left_data[position]);
                }

                if (left_oldView != null && right_oldView != null) {
                    dismiss();
                }
            }
        });

        screen_list_right.setAdapter(new MyAdapter(TYPE_RIGHT, data2));
        screen_list_right.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setBackgroundResource(R.color.gray);//把选中的颜色改变

                if (right_last_item != -1 && right_last_item != position) {//如果已经单击过条目并且上次保存的item位置和当前位置不同
                    right_oldView.setBackgroundResource(R.color.white);//把上次选中的样式去掉
                }

                right_oldView = view;//把当前的条目保存下来
                right_last_item = position;//把当前的位置保存下来

                if (onItemClickListener != null) {
                    onItemClickListener.onClick(TYPE_RIGHT, position, right_data[position]);
                }

                if (left_oldView != null && right_oldView != null) {
                    dismiss();
                }
            }
        });
    }

    class MyAdapter extends BaseAdapter {
        String[] list = null;
        int type;

        public MyAdapter(int type, String[] list) {
            this.list = list;
            this.type = type;
        }

        @Override
        public int getCount() {
            return list.length;
        }

        @Override
        public Object getItem(int position) {
            return list[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.item_screen_text, null);
            textView.setText(list[position]);
            int checkedNum;
            if (type == 0) {
                checkedNum = left_last_item;
            } else {
                checkedNum = right_last_item;
            }
            if (checkedNum == position) {
                textView.setBackgroundResource(R.color.gray);
            }
            return textView;
        }
    }

    public interface OnItemClickListener {
        /**
         * 条目点击监听
         *
         * @param type     是左边还是右边的列表
         * @param position 点击条目位置
         * @param data     数据
         */
        void onClick(int type, int position, String data);
    }

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
