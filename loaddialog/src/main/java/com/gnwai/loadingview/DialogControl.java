package com.gnwai.loadingview;

import android.content.DialogInterface;

/**
 * 装载对话框的接口
 */
public interface DialogControl {

    void hideWaitDialog();

    LoadDialog showWaitDialog(DialogInterface.OnCancelListener onCancelListener);

    LoadDialog showWaitDialog(int resid,DialogInterface.OnCancelListener onCancelListener);

    LoadDialog showWaitDialog(String text,DialogInterface.OnCancelListener onCancelListener);
}
