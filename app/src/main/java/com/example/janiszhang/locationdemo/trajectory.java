package com.example.janiszhang.locationdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by janiszhang on 2016/3/2.
 */
public class trajectory extends Activity{
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //全屏显示
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(new LocationSurfaceView(this));
//    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(new MySurfaceView(this));
    }

    private class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

        private Thread mThread = new Thread(this);
        private SurfaceHolder mSurfaceHolder;
        private Canvas mCanvas;
        private Paint paint;
        private SensorManager mSensorManager;
        private Sensor mSensor;
        private SensorEventListener mSensorEventListener;
        private int arc_x,arc_y;
        private float x = 0, y = 0, z = 0;

        public MySurfaceView(Context context) {
            super(context);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            arc_x = this.getWidth()/2 - 25;
            arc_y = this.getHeight() /2 -25;
            mThread.start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }

        @Override
        public void run() {

        }
    }
}
