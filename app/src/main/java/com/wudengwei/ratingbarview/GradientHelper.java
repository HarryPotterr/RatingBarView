package com.wudengwei.ratingbarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;

/**
 * Copyright (C)
 * FileName: GradientHelper
 * Author: wudengwei
 * Date: 2019/8/20 21:06
 * Description: ${DESCRIPTION}
 */
public class GradientHelper {
    private float[] radiusGradientArray = new float[8];
    private int[] gradientColorArr = new int[2];
    private int gradientColorStart;//渐变色-开始
    private int gradientStrokeColor;//边框颜色
    private float gradientStrokeDashWidth;//边框虚线宽度
    private float gradientStrokeDashGap;//边框虚线间隙
    private int gradientStrokeWidth;//边框宽度
    private int gradientColorEnd;//阴影色-结束
    private Rect gradientRect;//渐变区域

    public GradientHelper(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Strong);
            float gradientRadius = typedArray.getDimension(R.styleable.Strong_gradientRadius, 0);
            float gradientRadiusTopLeft = typedArray.getDimension(R.styleable.Strong_gradientRadiusTopLeft, gradientRadius);
            float gradientRadiusTopRight = typedArray.getDimension(R.styleable.Strong_gradientRadiusTopRight, gradientRadius);
            float gradientRadiusBottomLeft = typedArray.getDimension(R.styleable.Strong_gradientRadiusBottomLeft, gradientRadius);
            float gradientRadiusBottomRight = typedArray.getDimension(R.styleable.Strong_gradientRadiusBottomRight, gradientRadius);
            gradientColorStart = typedArray.getColor(R.styleable.Strong_gradientColorStart, 0);
            gradientColorEnd = typedArray.getColor(R.styleable.Strong_gradientColorEnd, 0);
            gradientStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.Strong_gradientStrokeWidth, 0);
            gradientStrokeColor = typedArray.getColor(R.styleable.Strong_gradientStrokeColor, 0);
            gradientStrokeDashWidth = typedArray.getDimension(R.styleable.Strong_gradientStrokeDashWidth, 0);
            gradientStrokeDashGap = typedArray.getDimension(R.styleable.Strong_gradientStrokeDashGap, 0);
            typedArray.recycle();

            radiusGradientArray[0] = gradientRadiusTopLeft;
            radiusGradientArray[1] = gradientRadiusTopLeft;
            radiusGradientArray[2] = gradientRadiusTopRight;
            radiusGradientArray[3] = gradientRadiusTopRight;
            radiusGradientArray[4] = gradientRadiusBottomRight;
            radiusGradientArray[5] = gradientRadiusBottomRight;
            radiusGradientArray[6] = gradientRadiusBottomLeft;
            radiusGradientArray[7] = gradientRadiusBottomLeft;
        }
        gradientRect = new Rect();
    }

    public void setGradientRect(int left, int top, int right, int bottom) {
        gradientRect.set(left,top,right,bottom);
    }

    public void draw(Canvas canvas) {
        //渐变背景
        if (gradientColorStart != 0 || gradientColorEnd != 0) {
            GradientDrawable gradientDrawable = createGradientDrawable(gradientRect);
            gradientDrawable.draw(canvas);
        }
    }

    private GradientDrawable createGradientDrawable(Rect bounds) {
        gradientColorArr[0] = gradientColorStart;
        gradientColorArr[1] = gradientColorEnd;
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,gradientColorArr);
        gradientDrawable.setBounds(bounds);
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setCornerRadii(radiusGradientArray);
        if (gradientStrokeWidth != 0)
            gradientDrawable.setStroke(gradientStrokeWidth,gradientStrokeColor,gradientStrokeDashWidth,gradientStrokeDashGap);
        return gradientDrawable;
    }
}
