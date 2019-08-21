package com.wudengwei.ratingbarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.gson.Gson;

/**
 * Copyright (C)
 * FileName: RatingBarView
 * Author: wudengwei
 * Date: 2019/8/20 20:58
 * Description: ${DESCRIPTION}
 */
public class RatingBarView extends View {
    private int mStarHeight;//星星高度（一般高宽相等）
    private int mStarWidth;//星星宽度
    private int mStarNum;//星星数量
    private int mStarSpace;//星星之间的空隙（不包括第一个左边，最后一个右边）

    private float mStarRating;
    private int mStarStep;//进度方式，1: 整星, 2:半星
    private boolean mStarClickable;

    private Drawable mStarDrawableFill;//选中，进度是整星
    private Drawable mStarDrawableHalf;//选中，进度是半星
    private Drawable mStarDrawableEmpty;//没选中

    private Rect[] mStarRect;//保存每个星星绘制区域

    //渐变
    private GradientHelper mGradientHelper;

    //评分改变事件监听
    private OnRatingBarChangeListener mOnRatingBarChangeListener;

    public void setOnRatingBarChangeListener(OnRatingBarChangeListener mOnRatingBarChangeListener) {
        this.mOnRatingBarChangeListener = mOnRatingBarChangeListener;
    }

    public RatingBarView(Context context) {
        this(context, null);
    }

    public RatingBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatingBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGradientHelper = new GradientHelper(context,attrs);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatingBarView);
            mStarWidth = typedArray.getDimensionPixelSize(R.styleable.RatingBarView_starWidth, 30);
            mStarHeight = typedArray.getDimensionPixelSize(R.styleable.RatingBarView_starHeight, 30);
            mStarNum = typedArray.getInt(R.styleable.RatingBarView_starNum, 5);
            mStarSpace = typedArray.getDimensionPixelSize(R.styleable.RatingBarView_starSpace, 0);
            mStarStep = typedArray.getInt(R.styleable.RatingBarView_starStep, 1);
            mStarRating = typedArray.getDimension(R.styleable.RatingBarView_starRating, 3.5f);
            mStarDrawableEmpty = typedArray.getDrawable(R.styleable.RatingBarView_starDrawableEmpty);
            mStarDrawableHalf = typedArray.getDrawable(R.styleable.RatingBarView_starDrawableHalf);
            mStarDrawableFill = typedArray.getDrawable(R.styleable.RatingBarView_starDrawableFill);
            mStarClickable = typedArray.getBoolean(R.styleable.RatingBarView_starClickable,true);

            if (mStarStep == 1) {
                mStarRating = (int) mStarRating;
            }
            typedArray.recycle();
        }
        mStarRect = new Rect[mStarNum];
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        //宽度=星星宽度*星星数量+（星星数量-1）*星星间隙+ getPaddingLeft() + getPaddingRight()
        int starSumWidth = mStarWidth*mStarNum + (mStarNum-1)*mStarSpace + getPaddingLeft() + getPaddingRight();
        int starSumHeight = mStarHeight + getPaddingTop() + getPaddingBottom();
        if (modeWidth == MeasureSpec.EXACTLY && getMeasuredWidth() > starSumWidth) {
            starSumWidth = getMeasuredWidth();
            mStarSpace = (starSumWidth-getPaddingLeft()-getPaddingRight())/(mStarNum-1);
        }
        if (modeHeight == MeasureSpec.EXACTLY && getMeasuredHeight() > starSumHeight) {
            starSumHeight = getMeasuredHeight();
        }
        int measureWidth = MeasureSpec.makeMeasureSpec(starSumWidth, MeasureSpec.EXACTLY);
        int measureHeight = MeasureSpec.makeMeasureSpec(starSumHeight, MeasureSpec.EXACTLY);
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int left = getPaddingLeft();
        int top = getPaddingTop() + (h-getPaddingTop()-getPaddingBottom()-mStarHeight)/2;
        for (int i=0;i<mStarNum;i++) {
            Rect rect = new Rect(left, top, mStarWidth+left,mStarHeight+top);
            mStarRect[i] = rect;
            left += (mStarWidth+mStarSpace);
        }
        //渐变
        int offset = 0;
        mGradientHelper.setGradientRect(-offset,-offset,getWidth()+offset,getHeight()+offset);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //渐变背景
        mGradientHelper.draw(canvas);
        super.onDraw(canvas);
        drawStar(canvas);
    }

    private void drawStar(Canvas canvas) {
        for (int i=0;i<mStarRect.length;i++) {
            Drawable drawable = new BitmapDrawable(null,drawableToBitmap(mStarDrawableEmpty));
            if (mStarStep == 2) {
                if (mStarRating - (int) mStarRating > 0) {
                    if (i <= mStarRating-1) {
                        drawable = new BitmapDrawable(null,drawableToBitmap(mStarDrawableFill));
                    } else if (i == (int) mStarRating) {
                        drawable = new BitmapDrawable(null,drawableToBitmap(mStarDrawableHalf));
                    }
                } else {
                    if (i <= mStarRating-1) {
                        drawable = new BitmapDrawable(null,drawableToBitmap(mStarDrawableFill));
                    }
                }
            } else {
                if (i <= mStarRating-1) {
                    drawable = new BitmapDrawable(null,drawableToBitmap(mStarDrawableFill));
                }
            }
            drawable.setBounds(mStarRect[i]);
            drawable.draw(canvas);
        }
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(mStarWidth, mStarHeight, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, mStarWidth, mStarHeight);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mStarClickable) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                checkUpdate(MotionEvent.ACTION_DOWN, event.getX());
                break;
            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);
                checkUpdate(MotionEvent.ACTION_MOVE, event.getX());
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    //记录临时的星星进度
    private float tempStarRating;
    private void checkUpdate(int action, float x) {
        for (int i=0;i<mStarRect.length;i++) {
            if (mStarRect[i].left <= x && mStarRect[i].right >= x) {
                if (x <= mStarRect[i].left + mStarRect[i].width()/2) {
                    tempStarRating = i+0.5f;
                } else {
                    tempStarRating = i+1;
                }
                if (action == MotionEvent.ACTION_DOWN) {
                    mStarRating = tempStarRating;
                    invalidate();
                    if (mOnRatingBarChangeListener != null)
                        mOnRatingBarChangeListener.onRatingChanged(this,mStarRating,true);
                } else {
                    if (mStarRating != tempStarRating) {
                        mStarRating = tempStarRating;
                        invalidate();
                        if (mOnRatingBarChangeListener != null)
                            mOnRatingBarChangeListener.onRatingChanged(this,mStarRating,true);
                    }
                }
                break;
            }
        }
    }

    public interface OnRatingBarChangeListener {
        void onRatingChanged(RatingBarView ratingBarView, float rating, boolean fromUser);
    }
}