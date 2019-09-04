package com.robot.zhenai.xntest;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class RotateView extends View {

    Paint bgPaint;
    Paint frontPaint;

    Paint fanPaint;

    private int fanRaduis = 0;

    private int smallCircleRaduis = 0;
    private int middleWidth = 300;

    private int fanWidth = 10;
    private int mCurrentValue = 0;
    private int mCurrentAngle = 0;
    Bitmap bm;
    private Matrix matrix;

    private int mSmallRaduis = 0;

    private int mCenterRaduis = 200;

    public RotateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {

        bgPaint = new Paint();
        bgPaint.setColor(Color.YELLOW);
//        bgPaint.setAlpha(200);

        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setStrokeCap(Paint.Cap.ROUND);
        frontPaint = new Paint();
        frontPaint.setColor(Color.WHITE);
        frontPaint.setAntiAlias(true);
        frontPaint.setStyle(Paint.Style.FILL);
        frontPaint.setStrokeCap(Paint.Cap.ROUND);

        fanPaint = new Paint();
        fanPaint.setColor(Color.RED);
        fanPaint.setStyle(Paint.Style.STROKE);
        fanPaint.setStrokeWidth(fanWidth);
        fanRaduis = 120;

        smallCircleRaduis = 100;

        bm = BitmapFactory.decodeResource(getResources(), R.mipmap.fan);

        matrix = new Matrix();
        startAniamtion();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        //画背景Round

        drawBgRound(canvas);
        //画前景Round

        drawFrontRound(canvas);

        //画右边的圆 

        drawRightCircle(canvas);

        //画自我旋转的风扇

        drawFanRotate(canvas);

        //在中心画圆
        drawCenterCircle(canvas);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void drawCenterCircle(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        canvas.rotate(mCurrentAngle);//旋转画布也是可以达到view 旋转的效果
        canvas.drawLine(0, 0, getWidth(), 0, fanPaint);
        canvas.drawLine(0, 0, 0, getHeight(), fanPaint);
        canvas.drawCircle(0, 0, mCenterRaduis, fanPaint);
//        Matrix matrix = new Matrix();
//        matrix.postScale(0.5f,0.5f);
//        matrix.postTranslate(-bm.getWidth() / 2, -bm.getHeight() / 2);
//
//        matrix.postRotate(mCurrentAngle, -bm.getWidth() / 4, -bm.getHeight() / 4);
//        canvas.drawBitmap(bm, matrix, fanPaint);
        mSmallRaduis = mCenterRaduis / 2;


        for (int i = 0; i < 4; i++) {
            Path path1 = new Path();
            Path path2 = new Path();
            Path path3 = new Path();
            float mCircelX = (float) (mSmallRaduis * Math.cos(Math.toRadians(45 + 90 * i)));
            float mCircelY = (float) (mSmallRaduis * Math.sin(Math.toRadians(45 + 90 * i)));
            path1.addCircle(mCircelX, mCircelY, mSmallRaduis, Path.Direction.CW);
            float mCircelX1 = (float) (mSmallRaduis * Math.cos(Math.toRadians(135 + 90 * i)));
            float mCircelY1 = (float) (mSmallRaduis * Math.sin(Math.toRadians(135 + 90 * i)));
            path2.addCircle(mCircelX1, mCircelY1, mSmallRaduis, Path.Direction.CW);
            path3.op(path1, path2, Path.Op.INTERSECT);
            canvas.drawPath(path3, frontPaint);
        }
        canvas.restore();
    }

    private void drawFanRotate(Canvas canvas) {
        canvas.save();
        canvas.translate(fanRaduis / 2 + middleWidth, fanRaduis / 2);
        float realW = bm.getWidth();
        float realH = bm.getHeight();
        float expireW = (float) (Math.cos(Math.toRadians(45)) * (fanRaduis * 1.0f));
        float expireH = (float) (Math.cos(Math.toRadians(45)) * (fanRaduis * 1.0f));
        float scaleX = expireW / realW;
        float scaleY = expireH / realH;
        matrix.reset();
        matrix.postTranslate(-bm.getWidth() / 2, -bm.getHeight() / 2);
        matrix.postScale(scaleX, scaleY);
        canvas.drawBitmap(bm, matrix, frontPaint);
        canvas.restore();
    }

    private void drawRightCircle(Canvas canvas) {

        canvas.drawCircle(middleWidth + fanRaduis / 2, fanRaduis / 2, fanRaduis / 2 - fanWidth / 2, fanPaint);
        frontPaint.setStrokeWidth(0);
        canvas.drawCircle(middleWidth + fanRaduis / 2, fanRaduis / 2, fanRaduis / 2 - fanWidth, frontPaint);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void drawFrontRound(Canvas canvas) {
        canvas.save();
        Path path = new Path();
        path.addRoundRect((fanRaduis - smallCircleRaduis) / 2, (fanRaduis - smallCircleRaduis) / 2,
                middleWidth + smallCircleRaduis, smallCircleRaduis + (fanRaduis - smallCircleRaduis) / 2
                , 90, 90, Path.Direction.CW);
        canvas.clipPath(path);
        canvas.drawRect((fanRaduis - smallCircleRaduis) / 2, (fanRaduis - smallCircleRaduis) / 2, mCurrentValue, smallCircleRaduis + (fanRaduis - smallCircleRaduis) / 2
                , frontPaint);
        canvas.restore();
    }

    private void drawBgRound(Canvas canvas) {

        bgPaint.setStrokeWidth(fanRaduis);
        canvas.drawLine(fanRaduis / 2, fanRaduis / 2, middleWidth + fanRaduis / 2, fanRaduis / 2, bgPaint);
    }

    private void startAniamtion() {

        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, middleWidth + smallCircleRaduis);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentValue = (int) animation.getAnimatedValue();
                invalidate();
            }
        });


        valueAnimator.setDuration(5000);
        valueAnimator.setRepeatCount(-1);
        valueAnimator.start();

        ValueAnimator rotateAnimator = ValueAnimator.ofInt(0, 360);

        rotateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentAngle = (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        rotateAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        rotateAnimator.setDuration(10000);
        rotateAnimator.setRepeatCount(-1);
        rotateAnimator.start();

    }
}
