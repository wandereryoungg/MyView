package com.young.myview;

import android.animation.AnimatorSet;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

public class OkView extends View {

    private Paint yesPaint;
    private Paint linePaint;
    private int mCenterX;
    private int mCenterY;
    private int radius;
    private Point startPoint;
    private Point midPoint;
    private Point endPoint;
    private Path yesPath;
    private Point pointHead;
    private Point pointTail;
    private int degree;

    public OkView(Context context) {
        this(context, null);
    }

    public OkView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OkView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        setBackgroundColor(0xffAECC21);

        yesPaint = new Paint();
        yesPaint.setStyle(Paint.Style.STROKE);
        yesPaint.setColor(Color.WHITE);
        yesPaint.setAntiAlias(true);
        yesPaint.setStrokeWidth(40);
        yesPaint.setStrokeCap(Paint.Cap.ROUND);
        yesPaint.setStrokeJoin(Paint.Join.ROUND);

        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setColor(Color.WHITE);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(40);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setStrokeJoin(Paint.Join.ROUND);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;
        radius = mCenterX / 2;
        Log.e("young", "mCenterX=" + mCenterX + "  mCenterY=" + mCenterY + "  radius=" + radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (yesPath == null || degree == 0) {
            return;
        }
        canvas.save();
        canvas.rotate(degree, mCenterX, mCenterY);
        for (int i = 0; i < 8; i++) {
            canvas.rotate(i * 45, mCenterX, mCenterY);
            canvas.drawLine(pointHead.x, pointHead.y, pointTail.x, pointTail.y, linePaint);
        }
        canvas.restore();
        canvas.drawPath(yesPath, yesPaint);

    }

    public void startAnimator() {
        initAnimatorValue();

        ValueAnimator animatorHead = ValueAnimator.ofInt(50);
        animatorHead.setInterpolator(new LinearInterpolator());
        //animatorHead.setDuration(860);
        animatorHead.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                pointHead.x = mCenterX - radius + (50 - value);
                pointHead.y = mCenterY;
                Log.e("young","pointHead.x="+pointHead.x);
            }
        });
        ValueAnimator animatorTail = ValueAnimator.ofInt(100);
        animatorTail.setInterpolator(new LinearInterpolator());
        //animatorTail.setDuration(860);
        animatorTail.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                pointTail.x = mCenterX - radius + (100 - value);
                pointTail.y = mCenterY;
                Log.e("young","pointTail.x= "+pointTail.x);
            }
        });
        AnimatorSet lineSet = new AnimatorSet();
        lineSet.play(animatorHead).with(animatorTail);
        lineSet.setStartDelay(140);
        lineSet.setDuration(860);

        ValueAnimator animatorScroll = ValueAnimator.ofInt(94);
        animatorScroll.setInterpolator(new LinearInterpolator());
        animatorScroll.setDuration(1000);
        animatorScroll.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                degree = (int) animation.getAnimatedValue();
                Log.e("young", "degree=" + degree);
            }
        });

        ValueAnimator animatorYes = ValueAnimator.ofObject(new MyTypeEvaluator(midPoint), startPoint, endPoint);
        animatorYes.setInterpolator(new LinearInterpolator());
        animatorYes.setDuration(1000);
        animatorYes.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Point point = (Point) animation.getAnimatedValue();
                yesPath.lineTo(point.x, point.y);
                invalidate();
            }
        });
        AnimatorSet allSet = new AnimatorSet();
        allSet.play(animatorYes).with(animatorScroll).with(lineSet);
        allSet.start();
    }

    private void initAnimatorValue() {
        startPoint = new Point(mCenterX - 36, mCenterY);
        midPoint = new Point(mCenterX - 8, mCenterY + 28);
        endPoint = new Point(mCenterX + 36, mCenterY - 16);
        yesPath = new Path();
        yesPath.moveTo(startPoint.x, startPoint.y);

        pointHead = new Point();
        pointTail = new Point();

    }

    private class MyTypeEvaluator implements TypeEvaluator<Point> {

        private Point midPoint;

        public MyTypeEvaluator(Point midPoint) {
            this.midPoint = midPoint;
        }

        @Override
        public Point evaluate(float fraction, Point startValue, Point endValue) {
            int cx = (int) (startValue.x + (endValue.x - startValue.x) * fraction);
            int cy = 0;
            if (cx < midPoint.x) {
                cy = (int) (startValue.y + (endValue.x - startValue.x) * fraction);
            } else {
                cy = midPoint.y - (cx - midPoint.x);
            }
            return new Point(cx, cy);
        }
    }
}
