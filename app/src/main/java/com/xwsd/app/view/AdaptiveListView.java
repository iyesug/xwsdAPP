package com.xwsd.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Gx on 2016/8/18.
 * ScrollView嵌套ListView，高度计算错误问题
 */
public class AdaptiveListView extends ListView {
    public AdaptiveListView(Context context) {
        super(context);
    }

    public AdaptiveListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdaptiveListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 只重写该方法，达到使ListView适应ScrollView的效果
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
