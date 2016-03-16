package com.example.janiszhang.locationdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

/**
 * Created by janiszhang on 2016/3/2.
 */
public class TrajectoryActivity extends Activity{

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
        private Paint mPaint;
        private Path mPath;
        private SensorManager mSensorManager;
        private Sensor mAcceleromererSensor;
        private Sensor mMagneticFieldSensor;
        private SensorEventListener mSensorEventListener;
        private double arc_x,arc_y;
        private float x = 0, y = 0, z = 0;
        float[] accelerometerValues = new float[3];
        float[] mValues = new float[3];
        float  mStepLongth = 20 * 3;
        boolean first = true;
        private Paint mTextPaint;

        public MySurfaceView(Context context) {
            super(context);
            this.setKeepScreenOn(true);
            mSurfaceHolder =this.getHolder();
            mSurfaceHolder.addCallback(this);
            setFocusable(true);
            setFocusableInTouchMode(true);

            mCanvas = new Canvas();
//            mCanvaCanvas.drawColor(Color.WHITE);

            mPaint = new Paint();
            mPaint.setColor(Color.BLACK);
            mPaint.setAntiAlias(true);//抗锯齿
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(6);//6
            mPath = new Path();
            mPath.moveTo((int) arc_x, (int) arc_y);


            mTextPaint = new Paint();
            mTextPaint.setColor(Color.BLACK);
            mTextPaint.setTextSize(60);

            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

            mAcceleromererSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mMagneticFieldSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            mSensorEventListener = new SensorEventListener() {
//                float[] accelerometerValues = new float[3];
                float[] magneticValues = new float[3];

                private float lastRotateDegree;

                @Override
                public void onSensorChanged(SensorEvent event) {
                    //判断当前是加速度传感器还是地磁传感器
                    if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                        //注意赋值时要调用clone()方法
                        accelerometerValues = event.values.clone();
                        for (int i = 0; i < 3; i++) {
                            oriValues[i] = accelerometerValues[i];
                        }
                        gravityNew = (float) Math.sqrt(oriValues[0] * oriValues[0]
                                + oriValues[1] * oriValues[1] + oriValues[2] * oriValues[2]);
                    } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                        magneticValues = event.values.clone();
                        float[] R = new float[9];
//                    float[] values = new float[3];

                        //get R 一个长度为9的float数组,包换旋转矩阵的数组
                        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticValues);
                        SensorManager.getOrientation(R, mValues);
                        Log.i("zhangbz", "orientationVals[0] = " + mValues[0] + " orientationVals[1] = " + mValues[1] + " orientationVals[2] = " + mValues[2]  + gravityNew + " : " +(float) Math.toDegrees(mValues[0]));
                        //get values 长度为3,zxy的旋转弧度
//                       float[] mRotationMatrix = new float[9];
//                       SensorManager.remapCoordinateSystem(R,  SensorManager.AXIS_X, SensorManager.AXIS_Z, mRotationMatrix);
//                        float[] mValues2 = new float[3];
//                        SensorManager.getOrientation(R, mValues2);
//                        Log.i("zhangbz", "orientationVals22222222[0] = " + mValues2[0] + " orientationVals[1] = " + mValues2[1] + " orientationVals[2] = " + mValues2[2]  + gravityNew + " : " +(float) Math.toDegrees(mValues2[0]));
                    }

//            Log.d("Mainactivity", "value[0] is " + Math.toDegrees(values[0]));//转换成角度
                    //降级算出的旋转角度取反,用于旋转指南针背景图
//                    float rotateDegree = - (float) Math.toDegrees(values[0]);
//                    if(Math.abs(rotateDegree - lastRotateDegree ) > 1) {
//                        RotateAnimation animation = new RotateAnimation(lastRotateDegree,rotateDegree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);//0.5f中的f不能落下
//                        animation.setFillAfter(true);
//                        compassImg.startAnimation(animation);
//                        lastRotateDegree = rotateDegree;
//                    }
                    Log.i("zhangbz", "调用DetectorNewStep之前的值 : " + gravityNew + " : " +(float) Math.toDegrees(mValues[0]));
                    DetectorNewStep(gravityNew, (int) Math.toDegrees(mValues[0]));
                }
                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };

            mSensorManager.registerListener(mSensorEventListener, mAcceleromererSensor,SensorManager.SENSOR_DELAY_UI);
            mSensorManager.registerListener(mSensorEventListener,mMagneticFieldSensor,SensorManager.SENSOR_DELAY_UI);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            arc_x = this.getWidth() / 2 - 25;
            arc_y = this.getHeight() /2 - 25;
            Log.i("zhangbz", "getWidth : " + arc_x );
            Log.i("zhangbz", "getHeight : " + arc_y);
            mThread.start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
                if(mSensorManager != null) {
                    mSensorManager.unregisterListener(mSensorEventListener);
                }
        }

        @Override
        public void run() {
//            while(true){

                synchronized (mSurfaceHolder) {
                    mCanvas =mSurfaceHolder.lockCanvas();
                    if(first) {
                        mPath.moveTo((int)arc_x,(int)arc_y);
                        first = false;
                    }
                    mCanvas.drawColor(Color.WHITE);
                    mCanvas.drawPath(mPath, mPaint);
                    mCanvas.drawText("当前的方向 : " + Math.toDegrees(mValues[0]), 0, 20, mTextPaint);
                    mCanvas.drawText("方向变化的角度 : " + orientation , 0, 40, mTextPaint);
//                    DetectorNewStep(gravityNew);
                    mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                }
//            }
        }

        /**************************************************/
        //存放三轴数据
        float[] oriValues = new float[3];
        final int valueNum = 4;
        //用于存放计算阈值的波峰波谷差值
        float[] tempValue = new float[valueNum];
        int tempCount = 0;
        //是否上升的标志位
        boolean isDirectionUp = false;
        //持续上升次数
        int continueUpCount = 0;
        //上一点的持续上升的次数，为了记录波峰的上升次数
        int continueUpFormerCount = 0;
        //上一点的状态，上升还是下降
        boolean lastStatus = false;
        //波峰值
        float peakOfWave = 0;
        //波谷值
        float valleyOfWave = 0;
        //此次波峰的时间
        long timeOfThisPeak = 0;
        //上次波峰的时间
        long timeOfLastPeak = 0;
        //当前的时间
        long timeOfNow = 0;
        //当前传感器的值
        float gravityNew = 0;
        //上次传感器的值
        float gravityOld = 0;
        //动态阈值需要动态的数据，这个值用于这些动态数据的阈值
        final float initialValue = (float) 1.3;
        //初始阈值
        float ThreadValue = (float) 2.0;
    /*private StepListener mStepListeners;*/


        //上次的方向值
        int orientationOld = 0;
        int orientation = 0;
        /**************************************************/

    /*
 * 检测步子，并开始计步
 * 1.传入sersor中的数据
 * 2.如果检测到了波峰，并且符合时间差以及阈值的条件，则判定为1步
 * 3.符合时间差条件，波峰波谷差值大于initialValue，则将该差值纳入阈值的计算中
 * */
        public void DetectorNewStep(float values, final int orientationValue) {
            if (gravityOld == 0) {
                gravityOld = values;
                orientationOld = orientationValue;
            } else {
                if (DetectorPeak(values, gravityOld)) {

                    timeOfLastPeak = timeOfThisPeak;
                    timeOfNow = System.currentTimeMillis();
                    if (timeOfNow - timeOfLastPeak >= 250
                            && (peakOfWave - valleyOfWave >= ThreadValue)) {
                        timeOfThisPeak = timeOfNow;
					/*
					 * 更新界面的处理，不涉及到算法
					 * 一般在通知更新界面之前，增加下面处理，为了处理无效运动：
					 * 1.连续记录10才开始计步
					 * 2.例如记录的9步用户停住超过3秒，则前面的记录失效，下次从头开始
					 * 3.连续记录了9步用户还在运动，之前的数据才有效
					 * */
//                        double xtemp = mStepLongth * 2;
//                        double ytemp = 0;
//
//                        mPath.quadTo((float) (arc_x + xtemp), (float) (arc_y + ytemp), (float) arc_x, (float) arc_y);
////                      mCanvas.drawPath(mPath, mPaint);
//                        arc_x = arc_x + xtemp;
//                        arc_y = arc_y + ytemp;



                        double xtemp = 0;
                        double ytemp = 0;
                        int orientationTemp = (int) (Math.toDegrees(mValues[0]));

                        orientation = orientationTemp  - orientationOld;
                        Log.i("zhangbz2", "orientationOld = " + orientationOld + " ; orientationTemp = " + " : " + orientationTemp + " ; orientiation = " + orientation);

//                        if(Math.abs(orientation) > 5) {
                            if(orientation>=0&& orientation<90) {
//                                float temp = 90 - mValues[0];
                                if(orientation< 10) {
                                    orientation = 0;
                                } else if (orientation>= 10 &&orientation < 30){
                                    orientation = 20;
                                } else if (orientation>= 30 && orientation < 50) {
                                    orientation = 40;
                                } else if (orientation >= 50 && orientation < 70) {
                                    orientation = 60;
                                } else if(orientation >= 70 && orientation< 90) {
                                    orientation = 80;
                                }
                                xtemp = Math.sin(orientation)*mStepLongth;
                                ytemp = Math.cos(orientation)* mStepLongth;
//                            mCanvas.drawColor(Color.WHITE);
                                mPath.lineTo((float) (arc_x + xtemp), (float) (arc_y - ytemp));
//                            mCanvas.drawPath(mPath, mPaint);
                                arc_x = arc_x + xtemp;
                                arc_y = arc_y - ytemp;
                            }
                            else if (orientation>= 90 && orientation<= 180) {
                                if(orientation>= 90 &&orientation< 110) {
                                    orientation = 100;
                                } else if (orientation>= 110 &&orientation < 130){
                                    orientation = 120;
                                } else if (orientation>= 130 && orientation < 150) {
                                    orientation = 140;
                                } else if (orientation >= 150 && orientation < 170) {
                                    orientation = 160;
                                } else if(orientation >= 170 && orientation<= 180) {
                                    orientation = 180;
                                }
                                float temp = 180 - orientation;
                                xtemp = Math.cos(temp) * mStepLongth;
                                ytemp = Math.sin(temp) * mStepLongth;
//                            mCanvas.drawColor(Color.WHITE);
//                            mPath.quadTo((float) (arc_x + xtemp), (float) (arc_y + ytemp), (float) arc_x, (float) arc_y);
                                mPath.lineTo((float) (arc_x + xtemp), (float) (arc_y + ytemp));
//                            mCanvas.drawPath(mPath, mPaint);
                                arc_x = arc_x +xtemp;
                                arc_y = arc_y +ytemp;
                            } else if (orientation<0&& orientation>= -90) {
                                if(orientation>=-10&&orientation< 0) {
                                    orientation = 0;
                                } else if (orientation>= -30 &&orientation < -10){
                                    orientation = -20;
                                } else if (orientation>= -50 && orientation < -30) {
                                    orientation = -40;
                                } else if (orientation >= -70 && orientation < -50) {
                                    orientation = -60;
                                } else if(orientation >= -90 && orientation< -70) {
                                    orientation = -80;
                                }
                                float temp = Math.abs(orientation);
                                xtemp = Math.sin(temp) * mStepLongth;
                                ytemp = Math.cos(temp) * mStepLongth;
//                            mCanvas.drawColor(Color.WHITE);
//                            mPath.quadTo((float) (arc_x - xtemp), (float) (arc_y - ytemp), (float) arc_x, (float) arc_y);
                                mPath.lineTo((float) (arc_x - xtemp), (float) (arc_y - ytemp));
//                            mCanvas.drawPath(mPath, mPaint);
                                arc_x = arc_x -xtemp;
                                arc_y = arc_y -ytemp;
                            } else {
                                if(orientation>=-110&&orientation< -90) {
                                    orientation = -100;
                                } else if (orientation>= -130 &&orientation < -110){
                                    orientation = -120;
                                } else if (orientation>= -150 && orientation < -130) {
                                    orientation = -140;
                                } else if (orientation >= -170 && orientation < -150) {
                                    orientation = -160;
                                } else if(orientation > -180 && orientation< -170) {
                                    orientation = 180;
                                }
                                float temp = 180 -Math.abs(orientation);
                                xtemp = Math.sin(temp) * mStepLongth;
                                ytemp = Math.cos(temp) * mStepLongth;
//                            mCanvas.drawColor(Color.WHITE);
//                            mPath.quadTo((float) (arc_x - xtemp), (float) (arc_y + ytemp), (float) arc_x, (float) arc_y);
                                mPath.lineTo((float) (arc_x - xtemp), (float) (arc_y + ytemp));
//                            mCanvas.drawPath(mPath, mPaint);
                                arc_x = arc_x - xtemp;
                                arc_y = arc_y+ytemp;
                            }



//                        } else {
////                            xtemp = Math.cos(orientation)*mStepLongth;
//                            ytemp = mStepLongth;
////                            mCanvas.drawColor(Color.WHITE);
//                            mPath.lineTo((float) arc_x, (float) (arc_y - ytemp));
////                            mCanvas.drawPath(mPath, mPaint);
////                            arc_x = arc_x - xtemp;
//                            arc_y = arc_y - ytemp;
//                        }

//                        mCanvas.drawColor(Color.WHITE);//?
                        //mPath.quadTo(mPosX, mPosY, x, y);
//                        mCanvas.drawPath(mPath, mPaint);

//                    mStepListeners.onStep()
//                    mStepCounterText.setText((step++) + "步");



                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (mSurfaceHolder) {
                                    mCanvas =mSurfaceHolder.lockCanvas();
                                    if(first) {
                                        mPath.moveTo((int)arc_x,(int)arc_y);
                                        first = false;
                                    }
                                    mCanvas.drawColor(Color.WHITE);
                                    mCanvas.drawPath(mPath, mPaint);
                                    mCanvas.drawText("上一步的方向 : " + orientationOld, 0, 50, mTextPaint);
                                    mCanvas.drawText("当前的方向 : " + orientationValue, 0,100, mTextPaint);
                                    mCanvas.drawText("方向变化的角度 : " + orientation , 0, 150, mTextPaint);
//                    DetectorNewStep(gravityNew);
                                    mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                                }
                            }
                        }).start();
//                        orientationOld = (int)orientationValue;
                    }
                    if (timeOfNow - timeOfLastPeak >= 250
                            && (peakOfWave - valleyOfWave >= initialValue)) {
                        timeOfThisPeak = timeOfNow;
                        ThreadValue = Peak_Valley_Thread(peakOfWave - valleyOfWave);
                    }
                }
            }
            gravityOld = values;
//            orientationOld = orientationValue;
        }

        /*
         * 检测波峰
         * 以下四个条件判断为波峰：
         * 1.目前点为下降的趋势：isDirectionUp为false
         * 2.之前的点为上升的趋势：lastStatus为true
         * 3.到波峰为止，持续上升大于等于2次
         * 4.波峰值大于20
         * 记录波谷值
         * 1.观察波形图，可以发现在出现步子的地方，波谷的下一个就是波峰，有比较明显的特征以及差值
         * 2.所以要记录每次的波谷值，为了和下次的波峰做对比
         * */
        public boolean DetectorPeak(float newValue, float oldValue) {
            lastStatus = isDirectionUp;
            if (newValue >= oldValue) {
                isDirectionUp = true;
                continueUpCount++;
            } else {
                continueUpFormerCount = continueUpCount;
                continueUpCount = 0;
                isDirectionUp = false;
            }

            if (!isDirectionUp && lastStatus
                    && (continueUpFormerCount >= 2 || oldValue >= 20)) {
                peakOfWave = oldValue;
                return true;
            } else if (!lastStatus && isDirectionUp) {
                valleyOfWave = oldValue;
                return false;
            } else {
                return false;
            }
        }

        /*
         * 阈值的计算
         * 1.通过波峰波谷的差值计算阈值
         * 2.记录4个值，存入tempValue[]数组中
         * 3.在将数组传入函数averageValue中计算阈值
         * */
        public float Peak_Valley_Thread(float value) {
            float tempThread = ThreadValue;
            if (tempCount < valueNum) {
                tempValue[tempCount] = value;
                tempCount++;
            } else {
                tempThread = averageValue(tempValue, valueNum);
                for (int i = 1; i < valueNum; i++) {
                    tempValue[i - 1] = tempValue[i];
                }
                tempValue[valueNum - 1] = value;
            }
            return tempThread;

        }

        /*
         * 梯度化阈值
         * 1.计算数组的均值
         * 2.通过均值将阈值梯度化在一个范围里
         * */
        public float averageValue(float value[], int n) {
            float ave = 0;
            for (int i = 0; i < n; i++) {
                ave += value[i];
            }
            ave = ave / valueNum;
            if (ave >= 8)
                ave = (float) 4.3;
            else if (ave >= 7 && ave < 8)
                ave = (float) 3.3;
            else if (ave >= 4 && ave < 7)
                ave = (float) 2.3;
            else if (ave >= 3 && ave < 4)
                ave = (float) 2.0;
            else {
                ave = (float) 1.3;
            }
            return ave;
        }
    }
}
