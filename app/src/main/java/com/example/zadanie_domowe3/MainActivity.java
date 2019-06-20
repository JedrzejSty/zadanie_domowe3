package com.example.zadanie_domowe3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.animation.DynamicAnimation;
import android.support.animation.FlingAnimation;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Math.abs;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static Sensor mSensor;
    private boolean start = false;
    private long lastUpdate = -1;
    private ImageView ballEmpty;
    private ImageView ballEight;
    private TextView ballAnwser;
    private int screenWidth;
    private int screenHeight;
    private int EdgeSize;
    private boolean layoutReady;
    private ConstraintLayout mainContainer;
    private boolean animationFlag = false;
    private static SensorManager mSensorManager;
    private int rand;
    private String[] anwser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        anwser = getResources().getStringArray(R.array.answers);
        ballEmpty = findViewById(R.id.imageView2);
        ballEight = findViewById(R.id.imageView);
        ballAnwser = findViewById(R.id.answer);
        ballEmpty.setVisibility(View.INVISIBLE);
        ballAnwser.setVisibility(View.INVISIBLE);
        ballEight.setVisibility(View.VISIBLE);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if(mSensor == null){
                Toast.makeText(this,"No accelerometer", Toast.LENGTH_SHORT).show();
            }
        }

        layoutReady = false;
        mainContainer = findViewById(R.id.magic8ball);
        mainContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                EdgeSize = ballEmpty.getWidth();
                screenWidth = mainContainer.getWidth();
                screenHeight = mainContainer.getHeight();
                mainContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                layoutReady = true;
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long timeMicro;
        if(lastUpdate == -1){
            lastUpdate = event.timestamp;
            timeMicro = 0;
        }else{
            timeMicro = (event.timestamp - lastUpdate)/1000L;
            lastUpdate = event.timestamp;
        }

        if(layoutReady){
            function1(event.values[0]);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onResume(){
        super.onResume();
        if(mSensor !=null){
            mSensorManager.registerListener(this, mSensor, 100000);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(mSensor != null){
            mSensorManager.unregisterListener(this, mSensor);
        }
    }

    private void function1(final float sensorValue){
        if(!animationFlag){
            if(abs(sensorValue)>5){
                start = true;
                animationFlag = true;

                ballEmpty.setVisibility(View.INVISIBLE);
                ballEight.setVisibility(View.VISIBLE);
                ballAnwser.setVisibility(View.INVISIBLE);

                FlingAnimation anim1 = new FlingAnimation(ballEight, DynamicAnimation.X);

                anim1.setStartVelocity(-1 * sensorValue *screenWidth / 2f).setMinValue(5).setMaxValue(screenWidth- EdgeSize + 1).setFriction(1f);


                anim1.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                    @Override
                    public void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean b, float v, float v1) {
                        if(v1 != 0){
                            FlingAnimation anim2 = new FlingAnimation(ballEight, DynamicAnimation.X);

                            anim2.setStartVelocity(-1 *v1).setMinValue(5).setMaxValue(screenWidth- EdgeSize + 1).setFriction(1.25f).start();

                            anim2.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                                @Override
                                public void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean b, float v, float v1) {
                                    animationFlag = false;
                                    rand = ((int) abs((sensorValue * 100))) %20;
                                }
                            });
                        }else{
                            animationFlag = false;
                            start = false;
                        }
                    }
                });anim1.start();

            }else if(start){
                ballEmpty.setVisibility(View.VISIBLE);
                ballEight.setVisibility(View.INVISIBLE);
                ballAnwser.setText(anwser[rand]);
                ballAnwser.setVisibility(View.VISIBLE);
                start = false;
            }
        }
    }





}
