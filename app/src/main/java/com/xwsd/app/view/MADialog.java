package com.xwsd.app.view;


import android.app.Activity;
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

    public void show() {
        dialog.show();
    }

    public void miss() {
        dialog.dismiss();
    }

    public void setMessage(String message1) {
        // 3. 消息内容
        TextView dialog_msg1 = (TextView) layout.findViewById(R.id.dialog_msg1);
        dialog_msg1.setText(message1);
    }

    // 5. 取消按钮
    public void setBtnCancel(String buf, View.OnClickListener listener) {
        Button btnCancel = (Button) layout.findViewById(R.id.dialog_cancel);
        btnCancel.setText(buf);
        btnCancel.setOnClickListener(listener);
    }

    // 4. 确定按钮
    public void setBtnOK(String buf, View.OnClickListener listener) {
        Button btnOK = (Button) layout.findViewById(R.id.dialog_ok);
        btnOK.setText(buf);
        btnOK.setOnClickListener(listener);
    }

    // 5. 隐藏取消按钮
    public void setBtnCancelGone() {
        Button btnCancel = (Button) layout.findViewById(R.id.dialog_cancel);
        btnCancel.setVisibility(View.GONE);
    }

    // 6. 隐藏确定按钮
    public void setBtnOKGone() {
        Button btnCancel = (Button) layout.findViewById(R.id.dialog_ok);
        btnCancel.setVisibility(View.GONE);
    }
    // 7. 设置按钮1
    public void setchoose1(View.OnClickListener listener) {
        TextView dialog_msg2 = (TextView) layout.findViewById(R.id.dialog_msg2);
        dialog_msg2.setVisibility(View.VISIBLE);
        dialog_msg2.setOnClickListener(listener);
    }
    // 8. 设置按钮2
    public void setchoose2(View.OnClickListener listener) {
        TextView dialog_msg3 = (TextView) layout.findViewById(R.id.dialog_msg3);
        dialog_msg3.setVisibility(View.VISIBLE);
        dialog_msg3.setOnClickListener(listener);
    }
    // 9. 隐藏文本
    public void setmsgGone(Activity context) {
        TextView dialog_msg1 = (TextView) layout.findViewById(R.id.dialog_msg1);
        dialog_msg1.setVisibility(View.GONE);
    }

}
