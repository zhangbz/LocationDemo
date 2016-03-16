package com.example.janiszhang.locationdemo;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

/**
 * Created by janiszhang on 2016/3/16.
 */
public class OrientationActivity extends Activity{

    private SensorManager mSensorManager;
    private Sensor mOrientationSensor;
    private float[] mRotationMatrixFromVector = new float[9];
    private float[] mRotationMatrix = new float[9];
    public float[] orientationVals = new float[3];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orientation);


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mSensorManager.registerListener(listener, mOrientationSensor,SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSensorManager != null) {
            mSensorManager.unregisterListener(listener);
        }
    }

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            // Convert the rotation-vector to a 4x4 matrix.
            SensorManager.getRotationMatrixFromVector(mRotationMatrixFromVector,
                    event.values);
            SensorManager.remapCoordinateSystem(mRotationMatrixFromVector,
                    SensorManager.AXIS_X, SensorManager.AXIS_Z, mRotationMatrix);
            SensorManager.getOrientation(mRotationMatrix, orientationVals);

            orientationVals[0] = (float) orientationVals[0];
            orientationVals[1] = (float) orientationVals[1]; // axe de rotation
            orientationVals[2] = (float) orientationVals[2];

            Log.i("zhangbz", "orientationVals[0] = " + orientationVals[0] + " orientationVals[1] = " + orientationVals[1] + " orientationVals[2] = " + orientationVals[2]);

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
