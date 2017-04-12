package com.asha.vrlib.strategy.interactive;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.asha.vrlib.MD360Director;
import com.asha.vrlib.common.VRUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hzqiujiadi on 16/3/19.
 * hzqiujiadi ashqalcn@gmail.com
 */
public class MotionStrategy extends AbsInteractiveStrategy implements SensorEventListener {

    private static final String TAG = "MotionStrategy";

    private int mDeviceRotation;

    private float[] mSensorMatrix = new float[16];

    private boolean mRegistered = false;

    private Boolean mIsSupport = null;
    private MyRecever recever;

    public MotionStrategy(InteractiveModeManager.Params params) {
        super(params);
    }

    @Override
    public void onResume(Context context) {
        registerSensor(context);
    }

    @Override
    public void onPause(Context context) {
//        unregisterSensor(context);
    }

    @Override
    public boolean handleDrag(int distanceX, int distanceY) {
        return false;
    }

    @Override
    public void on(Activity activity) {
        mDeviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        for (MD360Director director : getDirectorList()){
            director.reset();
        }
        //注册广播
        registBroadcastRececver(activity);
    }

    private void registBroadcastRececver(Activity activity) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        recever = new MyRecever();
        activity.registerReceiver(recever, filter);
    }

    @Override
    public void off(Activity activity) {
        unregisterSensor(activity);
    }

    @Override
    public boolean isSupport(Activity activity) {
        if (mIsSupport == null){
            SensorManager mSensorManager = (SensorManager) activity
                    .getSystemService(Context.SENSOR_SERVICE);
            Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            mIsSupport = (sensor != null);
        }
        return mIsSupport;
    }

    protected void registerSensor(Context context){
        if (mRegistered) return;

        SensorManager mSensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        if (sensor == null){
            Log.e(TAG,"TYPE_ROTATION_VECTOR sensor not support!");
            return;
        }

        mSensorManager.registerListener(this, sensor, getParams().mMotionDelay);

        mRegistered = true;
    }

    protected void unregisterSensor(Context context){
        if (!mRegistered) return;

        SensorManager mSensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.unregisterListener(this);

        context.unregisterReceiver(recever);
        mRegistered = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.accuracy != 0){
            if (getParams().mSensorListener != null){
                getParams().mSensorListener.onSensorChanged(event);
            }

            int type = event.sensor.getType();
            switch (type){
                case Sensor.TYPE_ROTATION_VECTOR:
                    VRUtil.sensorRotationVector2Matrix(event, mDeviceRotation, mSensorMatrix);
//                    for (MD360Director director : getDirectorList()){
//                        director.updateSensorMatrix(mSensorMatrix);
//                        // if (mDisplayMode == DISPLAY_MODE_NORMAL) break;
//                    }
                    break;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (getParams().mSensorListener != null){
            getParams().mSensorListener.onAccuracyChanged(sensor,accuracy);
        }
    }


    private static final String ACTION = "rececycer_client_relation";
    private static final String RELATIONDATA = "data";
    class MyRecever extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String stringExtra = intent.getStringExtra(RELATIONDATA);
            String[] split = stringExtra.split(",");
            if(split == null || split.length == 0) {
                return;
            }
            String pattern = "\\-?[0-9]\\.[0-9]+";
            float[] value = new float[split.length];
            for(int i = 0; i < split.length; i++) {

                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(split[i]);
                while(m.find()){
                    String group = m.group();
                    value[i] = Float.valueOf(group);
                }
            }

            VRUtil.sensorRotationVector(value, mDeviceRotation, mSensorMatrix);
            for (MD360Director director : getDirectorList()){
                director.updateSensorMatrix(mSensorMatrix);
                // if (mDisplayMode == DISPLAY_MODE_NORMAL) break;
            }
        }
    }

}
