package com.miui.maml.data;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.ArrayMap;
import android.util.Log;
import java.util.Iterator;
import java.util.WeakHashMap;
import miui.provider.Weather;

/* loaded from: classes2.dex */
public class MamlSensorManager {
    private static ArrayMap<String, MamlSensor> sSensors = new ArrayMap<>();
    private static final Object sLock = new Object();

    /* loaded from: classes2.dex */
    private static class MamlSensor {
        private int mRate;
        private boolean mRegistered;
        private Sensor mSensor;
        private SensorManager mSensorManager;
        private int mType;
        private WeakHashMap<SensorEventListener, Integer> mCallbacks = new WeakHashMap<>();
        private final Object mLock = new Object();
        private SensorEventListener mListener = new SensorEventListener() { // from class: com.miui.maml.data.MamlSensorManager.MamlSensor.1
            @Override // android.hardware.SensorEventListener
            public void onAccuracyChanged(Sensor sensor, int i) {
            }

            @Override // android.hardware.SensorEventListener
            public void onSensorChanged(SensorEvent sensorEvent) {
                synchronized (MamlSensor.this.mLock) {
                    Iterator it = MamlSensor.this.mCallbacks.keySet().iterator();
                    while (it.hasNext()) {
                        ((SensorEventListener) it.next()).onSensorChanged(sensorEvent);
                    }
                }
            }
        };

        public MamlSensor(Context context, int i, int i2) {
            if (i == -1) {
                Log.e("MAML_SensorManager", "Wront sensor type: " + i);
                return;
            }
            this.mType = i;
            this.mRate = i2;
            SensorManager sensorManager = (SensorManager) context.getSystemService("sensor");
            this.mSensorManager = sensorManager;
            Sensor defaultSensor = sensorManager.getDefaultSensor(i);
            this.mSensor = defaultSensor;
            if (defaultSensor == null) {
                Log.e("MAML_SensorManager", "Fail to get sensor! TYPE: " + this.mType);
            }
        }

        private boolean registerListener() {
            Sensor sensor = this.mSensor;
            if (sensor != null && !this.mRegistered) {
                try {
                    this.mRegistered = this.mSensorManager.registerListener(this.mListener, sensor, this.mRate);
                    Log.d("MAML_SensorManager", "registerListener " + this.mType);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return this.mRegistered;
        }

        private void unRegisterListener() {
            Sensor sensor = this.mSensor;
            if (sensor == null || !this.mRegistered) {
                return;
            }
            try {
                this.mSensorManager.unregisterListener(this.mListener, sensor);
                this.mRegistered = false;
                Log.d("MAML_SensorManager", "unregisterListener " + this.mType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void addCallback(int i, SensorEventListener sensorEventListener) {
            synchronized (this.mLock) {
                this.mCallbacks.put(sensorEventListener, Integer.valueOf(i));
            }
            boolean z = false;
            if (this.mRate < i) {
                this.mRate = i;
                z = true;
            }
            if (z && this.mRegistered) {
                unRegisterListener();
            }
            registerListener();
        }

        public void removeCallback(SensorEventListener sensorEventListener) {
            synchronized (this.mLock) {
                Integer num = this.mCallbacks.get(sensorEventListener);
                if (num == null) {
                    return;
                }
                this.mCallbacks.remove(sensorEventListener);
                boolean z = false;
                if (this.mRate == num.intValue()) {
                    this.mRate = 3;
                    for (Integer num2 : this.mCallbacks.values()) {
                        if (this.mRate > num2.intValue()) {
                            this.mRate = num2.intValue();
                        }
                    }
                    if (this.mRate != num.intValue()) {
                        z = true;
                    }
                }
                if (this.mCallbacks.size() == 0) {
                    unRegisterListener();
                } else if (z) {
                    unRegisterListener();
                    registerListener();
                }
            }
        }
    }

    /* loaded from: classes2.dex */
    private static class MamlSensorManagerHolder {
        public static final MamlSensorManager INSTANCE = new MamlSensorManager();
    }

    private MamlSensorManager() {
    }

    public static MamlSensorManager getInstance() {
        return MamlSensorManagerHolder.INSTANCE;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private int getType(String str) {
        char c;
        str.hashCode();
        switch (str.hashCode()) {
            case -1439500848:
                if (str.equals("orientation")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -1276242363:
                if (str.equals(Weather.WeatherBaseColumns.PRESSURE)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -490041217:
                if (str.equals("proximity")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 102970646:
                if (str.equals("light")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 280523342:
                if (str.equals("gravity")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 325741829:
                if (str.equals("gyroscope")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 517518170:
                if (str.equals("linear_acceleration")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 697872463:
                if (str.equals("accelerometer")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return 3;
            case 1:
                return 6;
            case 2:
                return 8;
            case 3:
                return 5;
            case 4:
                return 9;
            case 5:
                return 4;
            case 6:
                return 10;
            case 7:
                return 1;
            default:
                return -1;
        }
    }

    private int getValidRate(int i) {
        if (i == 0 || i == 1) {
            return 1;
        }
        return i != 2 ? 3 : 2;
    }

    public void registerListener(Context context, String str, int i, SensorEventListener sensorEventListener) {
        int validRate = getValidRate(i);
        synchronized (sLock) {
            MamlSensor mamlSensor = sSensors.get(str);
            if (mamlSensor != null) {
                mamlSensor.addCallback(validRate, sensorEventListener);
            } else {
                MamlSensor mamlSensor2 = new MamlSensor(context, getType(str), validRate);
                if (mamlSensor2.mSensor != null) {
                    mamlSensor2.addCallback(validRate, sensorEventListener);
                    sSensors.put(str, mamlSensor2);
                }
            }
        }
    }

    public void unregisterListener(String str, SensorEventListener sensorEventListener) {
        synchronized (sLock) {
            MamlSensor mamlSensor = sSensors.get(str);
            if (mamlSensor != null) {
                mamlSensor.removeCallback(sensorEventListener);
                if (mamlSensor.mCallbacks.size() == 0) {
                    sSensors.remove(str);
                }
            }
        }
    }
}
