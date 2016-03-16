package com.example.janiszhang.locationdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mLocationSurfaceViewButton;
    private Button mStepCounterButton;
    private Button mTrajectory;

    private Button mOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationSurfaceViewButton = (Button) findViewById(R.id.bt_location_surface_view);
        mLocationSurfaceViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LocationSurfaceViewActivity.class));
                finish();
            }
        });

        mStepCounterButton = (Button) findViewById(R.id.bt_step_counter);
        mStepCounterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, StepCounterActivity.class));
                finish();
            }
        });

        mTrajectory = (Button) findViewById(R.id.bt_trajectory);
        mTrajectory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,TrajectoryActivity.class));
                finish();
            }
        });

        mOrientation = (Button) findViewById(R.id.bt_orientation);
        mOrientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, OrientationActivity.class));
            }
        });
    }
}
