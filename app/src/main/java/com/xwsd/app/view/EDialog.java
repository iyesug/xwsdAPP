package com.xwsd.app.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xwsd.app.R;


public class EDialog extends Dialog implements
		View.OnClickListener {
	private DialogClickListener listener;
	Context context;
	private Button button_sure;
	private Button button_cancle;
	private TextView shengyu;
	private EditText edit_mima;
	private String money;

	public EDialog(Context context, int theme,String m,
				   DialogClickListener listener) {
		super(context, theme);
		this.context = context;
		this.money = m;
		this.listener = listener;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.edialog);
		init();

	}
	private void init(){
		button_sure = (Button) findViewById(R.id.sure);
		button_cancle = (Button) findViewById(R.id.cancel);
		edit_mima = (EditText) findViewById(R.id.edit_mima);
		shengyu = (TextView) findViewById(R.id.shengyu);
		shengyu.setText(money);
		button_sure.setOnClickListener(this);
		button_cancle.setOnClickListener(this);
		initDialog(context);
	}
	private void initDialog(Context context) {
		setCanceledOnTouchOutside(false);
		setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
								 KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getRepeatCount() == 0) {
					return true;
				} else {
					return false;
				}
			}
		});
	}

	public interface DialogClickListener {
		void sure(Dialog dialog,String pass);

		void cancle(Dialog dialog);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.sure:
				listener.sure(this,edit_mima.getText().toString());
					break;
			case R.id.cancel:
				listener.cancle(this);
				break;
		default:
			break;
		}
	}
}