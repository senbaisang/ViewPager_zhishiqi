package com.sally.viewpager.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by sally on 16/4/12.
 *
 * viewpageindicator
 */
public class ViewPagerIndicator extends LinearLayout {

    private Paint mPaint;

    // 三角形路径，宽高，大小比例
    private Path mPath;
    private int mTriangleWidth;
    private int mTriangleHeight;
    private static final float RATIO_TRIANGLE = 1/6f;

    // 初始位置， 移动时偏移位置
    private int mOriginTranslationX;
    private int mTranslationX;

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#ffffffff"));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(6));  // 设置圆角效果
    }

    /**
     * 指示器跟随手指滑动
     * @param position : 当前tab的位置
     * @param offset : 当前tab向下一个tab滑动的偏移量
     */
    public void scroll(int position, float offset) {
        int tabWidth = getWidth() / 3;
        mTranslationX = (int) ((position + offset) * tabWidth);
        invalidate();
    }

    /**
     * 在该方法中绘制三角形
     * @param canvas
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mOriginTranslationX + mTranslationX, getHeight() + 2);
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
        super.dispatchDraw(canvas);
    }

    /**
     * 当控件的宽高发生变化时，都会调用
     * 获得控件的宽高，或者根据控件的宽高设置某些宽高
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mTriangleWidth = (int) (w / 3 * RATIO_TRIANGLE);
        mOriginTranslationX = (w /3 - mTriangleWidth) / 2;
        mTriangleHeight = mTriangleWidth / 2;

        initTriangle();

    }

    /**
     * 初始化三角形
     */
    private void initTriangle() {
        mPath = new Path();
        mPath.moveTo(0, 0);
        mPath.lineTo(mTriangleWidth, 0);
        mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
        mPath.close();
    }
}
