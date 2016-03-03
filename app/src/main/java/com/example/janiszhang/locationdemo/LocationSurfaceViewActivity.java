package com.example.janiszhang.locationdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by janiszhang on 2016/2/27.
 */
public class LocationSurfaceViewActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new LocationSurfaceView(this));
    }

    private class LocationSurfaceView extends SurfaceView implements SurfaceHolder.Callback,Runnable {

        public static final int TIME_IN_FRAME = 50;

        SurfaceHolder mSurfaceHolder = null;
        Canvas mCanvas = null;
        Paint mPaint = null;
        Paint mTextPaint = null;
        private Path mPath;
        private float mPosX, mPosY;
        boolean mRunning  = false;

        public LocationSurfaceView(Context context) {
            super(context);
            this.setFocusable(true);
            this.setFocusableInTouchMode(true);

            mSurfaceHolder = this.getHolder();
            mSurfaceHolder.addCallback(this);

            mCanvas = new Canvas();

            mPaint = new Paint();
            mPaint.setColor(Color.BLACK);
            mPaint.setAntiAlias(true);//抗锯齿
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(6);

            mPath = new Path();
            mTextPaint = new Paint();
            mTextPaint.setColor(Color.BLACK);
            mTextPaint.setTextSize(15);
        }

//        @Override
        int i = 0;
        protected void onDraw(/*Canvas canvas*/) {
//            super.onDraw(canvas);
            mCanvas.drawColor(Color.WHITE);//?
            i++;
            if(i% 10== 0) {
                mCanvas.drawPath(mPath, mPaint);
            }
            mCanvas.drawText("当前触笔X: " + mPosX, 0, 20, mTextPaint);
            mCanvas.drawText("当前触笔Y: " + mPosY , 0, 40, mTextPaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int action = event.getAction();
            float x = event.getX();
            float y = event.getY();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    mPath.moveTo(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    mPath.quadTo(mPosX, mPosY, x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    //mPath.reset();
                    break;
            }
            //记录当前触摸点的坐标
            mPosX = x;
            mPosY = y;
            return true;
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mRunning = true;
            new Thread(this).start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mRunning = false;
        }

        @Override
        public void run() {
            while (mRunning) {
//                long startTime = System.currentTimeMillis();
                synchronized (mSurfaceHolder) {
                    mCanvas =mSurfaceHolder.lockCanvas();
                    onDraw();
                    mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                }
//                long endTime = System.currentTimeMillis();
//                int diffTime = (int) (endTime - startTime);
//                while (diffTime <= TIME_IN_FRAME) {
//                    diffTime = (int) (System.currentTimeMillis() = startTime);
//                    Thread.yield();
//                }
            }
        }
    }
}
