package com.xwsd.app.view;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xwsd.app.R;
import com.xwsd.app.tools.QqUtil;
import com.yanzhenjie.permission.AndPermission;


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
    public void setBtnGone() {
        Button btnCancel = (Button) layout.findViewById(R.id.dialog_cancel);
        btnCancel.setVisibility(View.GONE);
    }

    // 6. 隐藏确定按钮
    public void setBtnOKGone() {
        Button btnCancel = (Button) layout.findViewById(R.id.dialog_ok);
        btnCancel.setVisibility(View.GONE);
    }
    // 4. 电话按钮
    public void setphone(View.OnClickListener listener) {
        Button btnOK = (Button) layout.findViewById(R.id.dialog_ok);
        btnOK.setOnClickListener(listener);
    }
    // 6. 隐藏文本
    public void setmsgGone(Activity context) {
        TextView dialog_msg1 = (TextView) layout.findViewById(R.id.dialog_msg1);
        TextView dialog_msg2 = (TextView) layout.findViewById(R.id.dialog_msg2);
        TextView dialog_msg3 = (TextView) layout.findViewById(R.id.dialog_msg3);
        dialog_msg1.setVisibility(View.GONE);
        dialog_msg2.setVisibility(View.VISIBLE);
        dialog_msg3.setVisibility(View.VISIBLE);
        dialog_msg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                QqUtil.callQq(context);

            }
        });
        dialog_msg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                checkPermission(context);
            }
        });
    }

    private void checkPermission(Activity context) {
        // 先判断是否有权限。
        if (AndPermission.hasPermission(context, Manifest.permission.CALL_PHONE)) {
            // 有权限，直接do anything.

            call(context);
        } else if (!AndPermission.hasPermission(context, Manifest.permission.CALL_PHONE)) {
            // 申请单个权限。
            System.out.println("申请电话权限");
            AndPermission.with(context)
                    .requestCode(100)
                    .permission(Manifest.permission.CALL_PHONE)
                    // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
                    .rationale((requestCode, rationale) ->
                            // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                            AndPermission.rationaleDialog(context, rationale).show()
                    )
                    .send();
            checkPermission(context);
        }
    }

    private void call(Context context) {
        final MADialog mMDialog = new MADialog(context);
        mMDialog.setMessage("确认拨打：400 8659 993");
        mMDialog.setBtnOK("确定", v1 -> {
            mMDialog.miss();
            Intent intentPhone = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "4008659993"));
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            context.startActivity(intentPhone);
        });
        mMDialog.setBtnCancel("取消", v12 -> mMDialog.miss());
    }
}
