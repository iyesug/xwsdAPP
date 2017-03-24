package com.xwsd.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xwsd.app.R;


/**
 * Created：Gx on 2016/3/1 16:24
 * E-mail：guzwang@126.com
 * 带Title的TextView
 */
public class TitleTextView extends LinearLayout {
    public TextView title;
    public TextView content;

    public TitleTextView(Context context) {
        this(context, null);
    }

    public TitleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
        init(attrs);
    }

    protected void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TitleTextView);
            setTitle(a.getString(R.styleable.TitleTextView_text_title));
            setContent(a.getString(R.styleable.TitleTextView_text_content));
            setSize(a.getDimensionPixelSize(R.styleable.TitleTextView_text_size, 14));
            a.recycle();
        }
    }

    private View initView(Context context) {
        View view = View.inflate(context, R.layout.view_title_text, this);
        title = (TextView) view.findViewById(R.id.title_text_t);
        content = (TextView) view.findViewById(R.id.title_text_c);
        return view;
    }

    public void setSize(int size) {
        title.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        content.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public void setTitle(String tilte) {
        title.setText(tilte);
    }

    public void setContent(String strContent) {
        content.setText(strContent);
    }

}
