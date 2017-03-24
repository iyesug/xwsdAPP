package com.xwsd.app.view;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by Gx on 2016/9/13.
 * 能在滑动布局兼容的ListView
 */
public class MyListView extends ListView {

    public MyListView(Context context) {
        super(context);
    }

    public MyListView(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);//这句话的作用 告诉父view，我的单击事件我自行处理，不要阻碍我。
        return super.dispatchTouchEvent(ev);
    }
}
