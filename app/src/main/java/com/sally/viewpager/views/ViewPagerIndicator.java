package com.sally.viewpager.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sally.viewpager.R;

import java.util.List;

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

    // 三角形最大宽度限制
    private final int TRIANGLE_WIDTH_MAX = (int)(getScreenWidth()/3*RATIO_TRIANGLE);

    // 初始位置， 移动时偏移位置
    private int mOriginTranslationX;
    private int mTranslationX;

    // 自定义属性
    private int mVisableTabCount;
    private static final int DEFAULT_TAB_COUNT = 4;

    // 自动生成标题数据
    private static final int DEFAULT_COLOR = 0x77ffffff;
    private List<String> mTitles;

    private static final int NOMAL_COLOR = 0x44ffffff;
    private static final int LIGNT_COLOR = 0xffffffff;

    // mIndicator 关联的 viewpager
    private ViewPager mViewPager;
    public void setViewPager(ViewPager viewPager, int position) {
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                scroll(position, positionOffset);
                if(mListener != null) {
                    mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }

                // 选中时高亮
                setHightLightTextView(position);
            }

            @Override
            public void onPageSelected(int position) {
                if(mListener != null) {
                    mListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(mListener != null) {
                    mListener.onPageScrollStateChanged(state);
                }
            }
        });
        mViewPager.setCurrentItem(position);

        // 初始化时，高亮
        setHightLightTextView(position);
    }

    // 我们使用了onpagechangelistener，所以提供一个接口
    public interface OnMyPageChangeListener {

        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        void onPageSelected(int position);

        void onPageScrollStateChanged(int state);
    }
    public OnMyPageChangeListener mListener;
    public void setOnMyPageChangeListener(OnMyPageChangeListener listener) {
        mListener = listener;
    }

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        mVisableTabCount = typedArray.getInt(R.styleable.ViewPagerIndicator_visible_tab_count, DEFAULT_TAB_COUNT);
        if(mVisableTabCount < 0) {
            mVisableTabCount = DEFAULT_TAB_COUNT;
        }
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#ffffffff"));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(6));  // 设置圆角效果
    }

    /**
     * 当xml布局文件加载完成，会回调此方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if(childCount == 0) {
            return;
        }
        for(int i=0; i<childCount; i++) {
            View view = getChildAt(i);
            LinearLayout.LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.weight = 0;
            lp.width = getScreenWidth() / mVisableTabCount;
            view.setLayoutParams(lp);
        }

        // 设置点击事件
        setItemClick();
    }

    /**
     * 获得屏幕的宽度
     * @return
     */
    private int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 指示器跟随手指滑动
     * @param position : 当前tab的位置
     * @param offset : 当前tab向下一个tab滑动的偏移量
     */
    public void scroll(int position, float offset) {
//        int tabWidth = getWidth() / 3;

        int tabWidth = getWidth() / mVisableTabCount;
        // 当tab移动至最后一个时
        if(position >= mVisableTabCount-2 && offset > 0 && getChildCount() > mVisableTabCount && position < getChildCount()-2) {
            if(mVisableTabCount != 1) {
                this.scrollTo((position-(mVisableTabCount-2))*tabWidth + (int)(offset*tabWidth),0);
            } else {
                this.scrollTo(position*tabWidth + (int)(offset*tabWidth), 0);
            }
        }
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

//        mTriangleWidth = (int) (w / 3 * RATIO_TRIANGLE);
//        mOriginTranslationX = (w /3 - mTriangleWidth) / 2;

        mTriangleWidth = (int) (w / mVisableTabCount * RATIO_TRIANGLE);
        mTriangleWidth = Math.min(mTriangleHeight, TRIANGLE_WIDTH_MAX);
        mOriginTranslationX = (w / mVisableTabCount - mTriangleWidth) / 2;

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

    /**
     * 自动生成title栏
     * @param titles
     */
    public void setTabItemTitles(List<String> titles) {
        if(titles != null && titles.size() > 0) {
            removeAllViews();
            mTitles = titles;
            for(String title : mTitles) {
                addView(generateTextView(title));
            }
        }

        // 设置点击事件
        setItemClick();
    }

    /**
     * 设置可见标题的数量。
     * 注： 该方法必须在setTabItemTitles()方法之前调用
     * @param count
     */
    public void setVisibleTabCount(int count) {
        mVisableTabCount = count;
    }

    /**
     * 生成textview
     * @param title
     * @return
     */
    private View generateTextView(String title) {
        TextView tv = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth() / mVisableTabCount;
        tv.setText(title);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setTextColor(DEFAULT_COLOR);
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(lp);
        return tv;
    }

    /**
     * 高亮textview的文本
     * @param position
     */
    public void setHightLightTextView(int position) {
        resetTextView();
        View view = getChildAt(position);
        if(view instanceof TextView) {
            ((TextView) view).setTextColor(LIGNT_COLOR);
        }
    }

    /**
     * 重置文本的颜色
     */
    public void resetTextView() {
        for(int i=0; i<getChildCount(); i++) {
            View view = getChildAt(i);
            if(view instanceof TextView) {
                ((TextView) view).setTextColor(NOMAL_COLOR);
            }
        }
    }

    /**
     * tab的点击事件
     * 1. 在finishinflact方法中调用
     * 2. 动态生成所有标题后，调用
     */
    public void setItemClick() {
        int count = getChildCount();
        for(int i=0; i<count; i++) {
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }
}
