package com.android.settings.edgesuppression;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miuix.util.Log;

/* loaded from: classes.dex */
public class LaySensorWrapper {
    private Context mContext;
    private Sensor mSensor;
    private SensorManager mSensorManager;
    private final List<LaySensorChangeListener> mLaySensorChangeListeners = new ArrayList();
    private int mCurttenState = 0;
    private int mPerspective = 0;
    private int mAttitude = 0;
    private final SensorEventListener mSensorListener = new SensorEventListener() { // from class: com.android.settings.edgesuppression.LaySensorWrapper.1
        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent sensorEvent) {
            float[] fArr = sensorEvent.values;
            if (fArr.length != 0) {
                if (Float.compare(fArr[0], 0.0f) == 0) {
                    LaySensorWrapper.this.mAttitude = 0;
                }
                if (Float.compare(sensorEvent.values[0], 1.0f) == 0) {
                    LaySensorWrapper.this.mAttitude = 1;
                }
                if (Float.compare(sensorEvent.values[0], 2.0f) == 0) {
                    LaySensorWrapper.this.mAttitude = 2;
                }
                if (Float.compare(sensorEvent.values[0], 3.0f) == 0) {
                    LaySensorWrapper.this.mAttitude = 3;
                }
                if (Float.compare(sensorEvent.values[0], 4.0f) == 0) {
                    LaySensorWrapper.this.mAttitude = 4;
                }
                if (Float.compare(sensorEvent.values[1], 0.0f) == 0) {
                    LaySensorWrapper.this.mPerspective = 0;
                }
                if (Float.compare(sensorEvent.values[1], 1.0f) == 0) {
                    LaySensorWrapper.this.mPerspective = 1;
                }
                if (Float.compare(sensorEvent.values[1], 2.0f) == 0) {
                    LaySensorWrapper.this.mPerspective = 2;
                }
                if ((LaySensorWrapper.this.mAttitude == 0 || LaySensorWrapper.this.mPerspective == 1) && LaySensorWrapper.this.mCurttenState != 0) {
                    LaySensorWrapper.this.mCurttenState = 0;
                    LaySensorWrapper.this.mHandler.removeMessages(1);
                    LaySensorWrapper.this.mHandler.removeMessages(2);
                    LaySensorWrapper.this.mHandler.sendEmptyMessageDelayed(0, 1000L);
                } else if ((LaySensorWrapper.this.mAttitude == 4 || LaySensorWrapper.this.mPerspective == 2) && LaySensorWrapper.this.mCurttenState != 1) {
                    LaySensorWrapper.this.mCurttenState = 1;
                    LaySensorWrapper.this.mHandler.removeMessages(0);
                    LaySensorWrapper.this.mHandler.removeMessages(2);
                    LaySensorWrapper.this.mHandler.sendEmptyMessageDelayed(1, 1000L);
                } else if (LaySensorWrapper.this.mAttitude != 1 || LaySensorWrapper.this.mCurttenState == 2) {
                } else {
                    LaySensorWrapper.this.mCurttenState = 2;
                    LaySensorWrapper.this.mHandler.removeMessages(1);
                    LaySensorWrapper.this.mHandler.removeMessages(0);
                    LaySensorWrapper.this.mHandler.sendEmptyMessageDelayed(2, 1000L);
                }
            }
        }
    };
    private Handler mHandler = new Handler() { // from class: com.android.settings.edgesuppression.LaySensorWrapper.2
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                LaySensorWrapper.this.notifyListeners(0);
            } else if (i == 1) {
                LaySensorWrapper.this.notifyListeners(1);
            } else if (i == 2) {
                LaySensorWrapper.this.notifyListeners(2);
            } else if (i == 90) {
                LaySensorWrapper.this.notifyListeners(90);
            } else if (i != 180) {
            } else {
                LaySensorWrapper.this.notifyListeners(180);
            }
        }
    };

    /* loaded from: classes.dex */
    public interface LaySensorChangeListener {
        void onSensorChanged(int i);
    }

    public LaySensorWrapper(Context context) {
        this.mContext = context;
        SensorManager sensorManager = (SensorManager) this.mContext.getSystemService("sensor");
        this.mSensorManager = sensorManager;
        this.mSensor = sensorManager.getDefaultSensor(33171060);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyListeners(int i) {
        synchronized (this.mLaySensorChangeListeners) {
            Iterator<LaySensorChangeListener> it = this.mLaySensorChangeListeners.iterator();
            while (it.hasNext()) {
                it.next().onSensorChanged(i);
            }
        }
    }

    public boolean hasListenerRegistered(LaySensorChangeListener laySensorChangeListener) {
        return this.mLaySensorChangeListeners.contains(laySensorChangeListener);
    }

    public void registerListener(LaySensorChangeListener laySensorChangeListener) {
        synchronized (this.mLaySensorChangeListeners) {
            if (!this.mLaySensorChangeListeners.contains(laySensorChangeListener) && this.mLaySensorChangeListeners.size() == 0) {
                Log.i("LaySensorWrapper", "register LaySensor at Settings");
                this.mSensorManager.registerListener(this.mSensorListener, this.mSensor, 0);
            }
            this.mLaySensorChangeListeners.add(laySensorChangeListener);
        }
    }

    public void unregisterAllListener() {
        synchronized (this.mLaySensorChangeListeners) {
            Log.i("LaySensorWrapper", "unregister LaySensor at Settings");
            this.mLaySensorChangeListeners.clear();
            unregisterSensorEventListenerLocked();
        }
    }

    public void unregisterSensorEventListenerLocked() {
        this.mCurttenState = 0;
        this.mAttitude = 0;
        this.mPerspective = 0;
        if (this.mLaySensorChangeListeners.size() == 0) {
            this.mSensorManager.unregisterListener(this.mSensorListener, this.mSensor);
        }
    }
}
