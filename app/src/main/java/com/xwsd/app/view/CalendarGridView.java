package com.xwsd.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;
import android.widget.ScrollView;

/**
 * Created by Gx on 2016/9/21.
 * 日历的GridView（用来兼容ScrollView）
 */
public class CalendarGridView extends GridView {
    //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
    float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;

    ScrollView parentScrollView;

    public CalendarGridView(Context context) {
        super(context);
    }

    public CalendarGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * @param flag
     */
    private void setParentScrollAble(boolean flag) {

        parentScrollView.requestDisallowInterceptTouchEvent(!flag);
    }

    public void setParentScrollView(ScrollView parentScrollView) {
        this.parentScrollView = parentScrollView;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            x1 = ev.getX();
            y1 = ev.getY();
        }
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            //当手指离开的时候
            x2 = ev.getX();
            y2 = ev.getY();
            if (y1 - y2 > 50) {//上
                setParentScrollAble(true);
            } else if (y2 - y1 > 50) {//下
                setParentScrollAble(true);
            } else if (x1 - x2 > 50) {//左
                setParentScrollAble(false);
            } else if (x2 - x1 > 50) {//右
                setParentScrollAble(false);
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 只重写该方法，达到使GridView适应ScrollView的效果
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
