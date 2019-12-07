package com.mohsen.speedmeter.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.mohsen.speedmeter.R;


/**
 * Created by mahdiarezoumandi on 8/23/2016 AD.
 */
public class RoundCornerFrameLayout extends FrameLayout {
    private static final int DEFAULT_CORNER_RADIUS = 0;

    private float mTopLeftRadius;
    private float mTopRightRadius;
    private float mBottomLeftRadius;
    private float mBottomRightRadius;

    public RoundCornerFrameLayout(Context context) {
        this(context, null);
    }

    public RoundCornerFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundCornerFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerFrameLayout, defStyle, 0);
        mTopLeftRadius = a.getDimensionPixelSize(R.styleable.RoundCornerFrameLayout_top_left, DEFAULT_CORNER_RADIUS);
        mTopRightRadius = a.getDimensionPixelSize(R.styleable.RoundCornerFrameLayout_top_right, DEFAULT_CORNER_RADIUS);
        mBottomLeftRadius = a.getDimensionPixelSize(R.styleable.RoundCornerFrameLayout_bottom_left, DEFAULT_CORNER_RADIUS);
        mBottomRightRadius = a.getDimensionPixelSize(R.styleable.RoundCornerFrameLayout_bottom_right, DEFAULT_CORNER_RADIUS);
        a.recycle();

//        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
//        cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CORNER_RADIUS, metrics);
//        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int count = canvas.save();

        final Path path = new Path();
        float roundRadius[] = new float[8];
        roundRadius[0] = mTopLeftRadius;
        roundRadius[1] = mTopLeftRadius;
        roundRadius[2] = mTopRightRadius;
        roundRadius[3] = mTopRightRadius;
        roundRadius[4] = mBottomRightRadius;
        roundRadius[5] = mBottomRightRadius;
        roundRadius[6] = mBottomLeftRadius;
        roundRadius[7] = mBottomLeftRadius;
        path.addRoundRect(new RectF(0, 0, canvas.getWidth(), canvas.getHeight()), roundRadius, Path.Direction.CW);
//        canvas.clipPath(path, Region.Op.REPLACE);//todo alz  uncomment
//        canvas.drawColor(getResources().getColor(R.color.white));

        canvas.clipPath(path);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(count);
    }

}
