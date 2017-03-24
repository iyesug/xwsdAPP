package com.gnwai.loadingview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * 装载对话框
 * 可设置消息提示
 */
public class LoadDialog extends Dialog {
    View view;
    TextView tvMessage;
    ImageView ivProgressSpinner;
    AnimationDrawable adProgressSpinner;
    Context context;

    public LoadDialog(Context context) {
        super(context, R.style.DialogTheme);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        this.setCanceledOnTouchOutside(false);
        this.context = context;
        view = getLayoutInflater().inflate(R.layout.dialog_progress, null);
        tvMessage = (TextView) view.findViewById(R.id.textview_message);
        ivProgressSpinner = (ImageView) view
                .findViewById(R.id.imageview_progress_spinner);
        ivProgressSpinner.setImageResource(R.drawable.round_spinner);
        adProgressSpinner = (AnimationDrawable) ivProgressSpinner.getDrawable();
        this.setContentView(view);
    }

    public LoadDialog setMessage(String message) {
        tvMessage.setText(message);
        return this;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        ivProgressSpinner.post(new Runnable() {
            @Override
            public void run() {
                adProgressSpinner.start();
            }
        });
    }
}
