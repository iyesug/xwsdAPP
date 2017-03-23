package com.xwsd.app.view;


import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xwsd.app.R;


/**
 * Created by Administrator on 2016/6/14.
 */
public class MADialog {
    private Dialog dialog;
    private LinearLayout layout;
    public MADialog(Context context) {

        LayoutInflater inflater = LayoutInflater.from(context);
        layout = (LinearLayout) inflater.inflate(R.layout.madialog, null);
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
    public void setMessage(String message1) {
        // 3. 消息内容
        TextView dialog_msg1 = (TextView) layout.findViewById(R.id.dialog_msg1);
        dialog_msg1.setText(message1);
    }
    // 5. 取消按钮
    public void setBtnCancel(String buf, View.OnClickListener listener){
        Button btnCancel = (Button) layout.findViewById(R.id.dialog_cancel);
        btnCancel.setText(buf);
        btnCancel.setOnClickListener(listener);
    }
    // 4. 确定按钮
    public void setBtnOK(String buf, View.OnClickListener listener){
        Button btnOK = (Button) layout.findViewById(R.id.dialog_ok);
        btnOK.setText(buf);
        btnOK.setOnClickListener(listener);
    }
}
