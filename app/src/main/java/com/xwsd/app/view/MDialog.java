package com.xwsd.app.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xwsd.app.R;
import com.xwsd.app.adapter.BaseAdapterHelper;
import com.xwsd.app.adapter.QuickAdapter;
import com.xwsd.app.bean.InvestRecordBean;


/**
 * Created by Administrator on 2016/6/14.
 */
public class MDialog implements AbsListView.OnScrollListener{
    private Dialog dialog;
    private LinearLayout layout;
    protected QuickAdapter mAdapter;
    private Context mContext;
    private ListView list_view;
    public MDialog(Context context) {
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        layout = (LinearLayout) inflater.inflate(R.layout.mdialog, null);
        // 2. 新建对话框对象
        dialog = new AlertDialog.Builder(context).create();
        dialog.setCanceledOnTouchOutside(true);
     //   dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setContentView(layout);
    }
    public void show(){
        dialog.show();
    }
    public void miss(){
        dialog.dismiss();
    }

    public void setAdapter(InvestRecordBean bean) {
        if (list_view == null) {
            list_view = (ListView) layout.findViewById(R.id.list_view);
        }
        list_view.setOnScrollListener(this);
        if (mAdapter == null) {
            mAdapter = new QuickAdapter<InvestRecordBean.Data.Records>(mContext, R.layout.item_invest_record, bean.data.records) {
                @Override
                protected void convert(BaseAdapterHelper helper, InvestRecordBean.Data.Records item) {
                    helper.setText(R.id.tv_tag, String.valueOf(helper.getPosition() + 1));
                    helper.setText(R.id.tv_name, item.username);
                    helper.setText(R.id.tv_money, item.money);
                    String[] arr = item.time.split(" ");
                    helper.setText(R.id.tv_time, arr[0] + "\n\r" + arr[1]);
                }
            };
            list_view.setAdapter(mAdapter);
        } else {
            mAdapter.addAll(bean.data.records);
        }
    }
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
