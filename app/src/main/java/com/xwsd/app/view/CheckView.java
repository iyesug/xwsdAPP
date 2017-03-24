package com.xwsd.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.xwsd.app.constant.CheckConfig;
import com.xwsd.app.tools.CheckUtil;

/**
 * Created by Gx on 2016/8/30.
 * 自定义验证码控件
 */
public class CheckView extends View {
    Context mContext;
    int[] CheckNum = null;
    Paint mTempPaint = new Paint(); // 验证码

    public CheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mTempPaint.setAntiAlias(true);
        mTempPaint.setTextSize(CheckConfig.TEXT_SIZE);
        mTempPaint.setStrokeWidth(3);
        setCheckNum(CheckUtil.getCheckNum());
    }

    public void onDraw(Canvas canvas) {
        canvas.drawColor(CheckConfig.COLOR);
        final int height = getHeight();//获得CheckView控件的高度
        final int width = getWidth();//获得CheckView控件的宽度
        int dx = 40;
        for (int i = 0; i < 4; i++) {
            // 绘制验证控件上的文本
            canvas.drawText("" + CheckNum[i], dx, CheckUtil.getPositon(height), mTempPaint);
            dx += width / 5;
        }

        int[] line;
        for (int i = 0; i < CheckConfig.LINE_NUM; i++) {
            //划线
            line = CheckUtil.getLine(height, width);
            canvas.drawLine(line[0], line[1], line[2], line[3], mTempPaint);
        } // 绘制小圆点
        int[] point;
        for (int i = 0; i < CheckConfig.POINT_NUM; i++) {
            // 画点
            point = CheckUtil.getPoint(height, width);
            canvas.drawCircle(point[0], point[1], 1, mTempPaint);
        }

    }

    public void setCheckNum(int[] chenckNum) {
        //设置验证码
        CheckNum = chenckNum;
    }

    public String getCheckNum() {
        //获得验证码
        StringBuffer str = new StringBuffer();
        for (int i : CheckNum) {
            str.append(String.valueOf(i));
        }
        return str.toString();
    }

    public void invaliChenkNum() {
        invalidate();
    }

}