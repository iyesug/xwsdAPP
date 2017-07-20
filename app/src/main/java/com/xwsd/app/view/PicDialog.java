package com.xwsd.app.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.xwsd.app.R;


public class PicDialog extends Dialog implements View.OnClickListener {
	private DialogClickListener listener;
	Context context;
	private String money;
	private ImageView close;
	private Button next;

	public PicDialog(Context context, int theme,
                     DialogClickListener listener) {
		super(context, theme);
		this.context = context;
		this.listener = listener;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.picdialog);
		init();
	}
	private void init(){
		next = (Button) findViewById(R.id.commit);
		close = (ImageView) findViewById(R.id.close);

		next.setOnClickListener(this);
		close.setOnClickListener(this);
		initDialog(context);
	}
	private void initDialog(Context context) {
		setCanceledOnTouchOutside(true);
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
		void close(Dialog dialog, String pass);

		void commit(Dialog dialog);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.close:
				listener.close(this,null);
					break;
			case R.id.commit:
				listener.commit(this);
				break;
		default:
			break;
		}
	}
}