package com.zhouyijin.zyj.myprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zhouyijin on 2016/11/17.
 *
 */

public class MyProgressBar extends View {

    public MyProgressBar(Context context) {
        this(context, null);
    }

    public MyProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyProgressBar);
        int count = a.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.MyProgressBar_FirstProgressColor) {
                firstProgressColor = a.getColor(R.styleable.MyProgressBar_FirstProgressColor, Color.GREEN);
            } else if (attr == R.styleable.MyProgressBar_SecondaryProgressColor) {
                secondaryProgressColor = a.getColor(R.styleable.MyProgressBar_SecondaryProgressColor, Color.RED);
            } else if (attr == R.styleable.MyProgressBar_NumberTextSize) {
                textSize = a.getDimension(R.styleable.MyProgressBar_NumberTextSize, sp2px(12));
            } else if (attr == R.styleable.MyProgressBar_Max) {
                max = a.getInt(R.styleable.MyProgressBar_Max, 100);
            } else if (attr == R.styleable.MyProgressBar_Progress) {
                progress = a.getInt(R.styleable.MyProgressBar_Progress, 0);
            } else if (attr == R.styleable.MyProgressBar_SecondaryProgress) {
                secondaryProgress = a.getInt(R.styleable.MyProgressBar_SecondaryProgress, 0);
            }
        }

        mPaint = new Paint();
        initPaint();
    }

    private void initPaint() {
        mPaint.setTextSize(textSize);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setAntiAlias(true);
    }

    public void setSecondaryProgress(int secondaryProgress) {
        if (secondaryProgress < 0) secondaryProgress = 0;
        int maxProgress = getMax();
        if (secondaryProgress > maxProgress) secondaryProgress = maxProgress;
        int firstProgress = getProgress();
        if (secondaryProgress >= maxProgress - firstProgress) {
            secondaryProgress = maxProgress - firstProgress;
        }
        this.secondaryProgress = secondaryProgress;
        invalidate();
    }

    public void setProgress(int progress) {
        if (progress < 0) progress = 0;
        int maxProgress = getMax();
        if (progress > maxProgress) progress = maxProgress;
        int secondaryProgress = getSecondaryProgress();
        if (progress > maxProgress - secondaryProgress) {
            int gap = progress - (maxProgress - secondaryProgress);
            setSecondaryProgress(secondaryProgress - gap);
        }
        this.progress = progress;
        invalidate();
    }

    public void setMax(int max) {
        if (max < 0) max = 0;
        if (max > 1000) max = 1000;
        this.max = max;
        invalidate();
    }

    private int max = 100;
    private int progress = 0;
    private int secondaryProgress = 0;

    public int getMax() {
        return max;
    }

    public int getProgress() {
        return progress;
    }

    public int getSecondaryProgress() {
        return secondaryProgress;
    }

    private Paint mPaint;

    private int firstProgressColor = Color.GREEN;
    private int secondaryProgressColor = Color.RED;

    private float textSize = sp2px(12);

    public void setSecondaryProgressColor(int secondaryProgressColor) {
        this.secondaryProgressColor = secondaryProgressColor;
        invalidate();
    }

    public void setTextSize(float textSize) {
        textSize = sp2px(textSize);
        this.textSize = textSize;
        invalidate();
    }

    public void setFirstProgressColor(int firstProgressColor) {
        this.firstProgressColor = firstProgressColor;
        invalidate();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMeasuredSpec = getWidthMeasuredSpec(widthMeasureSpec);
        int heightMeasuredSpec = getHeightMeasuredSpec(heightMeasureSpec);
        setMeasuredDimension(widthMeasuredSpec, heightMeasuredSpec);
    }

    private int getHeightMeasuredSpec(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int result;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = size;
                break;
            default:    //那就看里面的文字大小了
                float height = getFontHeight(textSize) + 4 + getPaddingBottom() + getPaddingTop();
                result = (int) Math.min(height, size);
                break;
        }
        return result;
    }

    private int getWidthMeasuredSpec(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        int result;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = size;
                break;
            default:    //那就看里面的文字大小了
                float width = 200 + getPaddingLeft() + getPaddingRight();
                result = (int) Math.min(width, size);
                break;
        }
        return result;
    }


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        int left = getPaddingLeft();
        int top = getPaddingTop();
        canvas.translate(left, top);
        RectF firstProgressRect = new RectF();
        RectF secondaryProgressRect = new RectF();
        RectF residualProgressRect = new RectF();
        getALLRect(firstProgressRect, secondaryProgressRect, residualProgressRect);
        drawLeft(canvas, firstProgressRect);
        drawRight(canvas, secondaryProgressRect);
        drawText(canvas, firstProgressRect, secondaryProgressRect, residualProgressRect);
        canvas.restore();
    }

    private void getALLRect(RectF firstProgressRect, RectF secondaryProgressRect, RectF residualProgressRect) {
        int maxWidth = getMeasuredWidth() - getPaddingRight() - getPaddingLeft();
        int maxHeight = getMeasuredHeight() - getPaddingTop() - getPaddingRight();

        float firstRight = maxWidth * (getProgress() * 1.0f / getMax());
        firstProgressRect.set(0, 0, firstRight, maxHeight);
        float secondaryLeft = maxWidth - maxWidth * (getSecondaryProgress() * 1.0f / getMax());
        secondaryProgressRect.set(secondaryLeft, 0, maxWidth, maxHeight);
        residualProgressRect.set(firstRight, 0, secondaryLeft, maxHeight);
    }

    private void drawLeft(Canvas canvas, RectF firstProgressRect) {
        mPaint.setColor(firstProgressColor);
        canvas.drawRect(firstProgressRect, mPaint);
    }

    private void drawRight(Canvas canvas, RectF secondaryProgressRect) {
        mPaint.setColor(secondaryProgressColor);
        canvas.drawRect(secondaryProgressRect, mPaint);
    }

    private void drawText(Canvas canvas, RectF firstProgressRect, RectF secondaryProgressRect, RectF residualProgressRect) {
        float textHeight = getFontHeight(textSize);
        if (firstProgressRect.height() < textHeight) {
            requestLayout();
            return;
        }
        mPaint.setTextSize(textSize);
        mPaint.setColor(Color.WHITE);
        //左边进度的文字
        String firstProgress = String.valueOf(getProgress());
        float firstProgressTextWidth = mPaint.measureText(firstProgress);
        if (firstProgressTextWidth < firstProgressRect.width()) {
            float firstX = firstProgressRect.left + firstProgressRect.width() / 2;
            float firstY = getFontY(firstProgressRect.top + firstProgressRect.height() / 2);
            canvas.drawText(firstProgress, firstX, firstY, mPaint);
        }
        //右边进度的文字
        String secondaryProgress = String.valueOf(getSecondaryProgress());
        float secondaryProgressTextWidth = mPaint.measureText(secondaryProgress);
        if (secondaryProgressTextWidth < secondaryProgressRect.width()) {
            float secondX = secondaryProgressRect.left + secondaryProgressRect.width() / 2;
            float secondY = getFontY(secondaryProgressRect.top + secondaryProgressRect.height() / 2);
            canvas.drawText(secondaryProgress, secondX, secondY, mPaint);
        }
        //剩余进度的文字
        String residualProgress = String.valueOf(getMax() - getProgress() - getSecondaryProgress());
        float residualProgressTextWidth = mPaint.measureText(residualProgress);
        if (residualProgressTextWidth < residualProgressRect.width()) {
            mPaint.setColor(Color.BLACK);
            float rX = residualProgressRect.left + residualProgressRect.width() / 2;
            float rY = getFontY(residualProgressRect.top + residualProgressRect.height() / 2);
            canvas.drawText(residualProgress, rX, rY, mPaint);
        }

    }


    private float sp2px(float sp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return scale * sp;
    }

    private float px2sp(float px) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return px / scale;
    }

    private float getFontHeight(float size) {
        mPaint.setTextSize(size);
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    private float getFontY(float centerY) {
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        return centerY + (fm.descent - fm.ascent) / 2 - fm.descent;
    }
}
