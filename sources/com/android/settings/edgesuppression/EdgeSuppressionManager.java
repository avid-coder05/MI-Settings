package com.android.settings.edgesuppression;

import android.content.Context;
import android.graphics.Point;
import android.hardware.input.InputManager;
import android.view.WindowManager;
import com.android.settings.edgesuppression.LaySensorWrapper;
import miui.util.FeatureParser;
import miui.util.ReflectionUtils;

/* loaded from: classes.dex */
public class EdgeSuppressionManager {
    private static volatile EdgeSuppressionManager sInstance;
    private int[] mConditionSize;
    private final Context mContext;
    private LaySensorWrapper mLaySensorWrapper;
    public int mScreenHeight;
    public int mScreenWidth;
    private final boolean mSupportSensor;
    private final WindowManager mWindowManager;
    public boolean mSupportHighResolution = false;
    private boolean mIsReflectionFailed = false;
    private Point mCurrentScreenSize = new Point(0, 0);

    private EdgeSuppressionManager(Context context) {
        this.mContext = context;
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        setScreenSize();
        getEdgeSuppressionSizeArray();
        this.mSupportSensor = FeatureParser.getBoolean("support_edgesuppression_with_sensor", false);
    }

    private void getEdgeSuppressionSizeArray() {
        try {
            this.mConditionSize = (int[]) ReflectionUtils.findMethodExact(InputManager.class, "getEdgeSuppressionSize", new Class[]{Boolean.TYPE}).invoke(InputManager.getInstance(), Boolean.FALSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.mConditionSize == null) {
            this.mConditionSize = new int[5];
            this.mIsReflectionFailed = true;
            this.mSupportHighResolution = FeatureParser.getBoolean("support_high_resolution", false);
            int[] intArray = FeatureParser.getIntArray("edge_suppresson_condition");
            float f = this.mSupportHighResolution ? 100.0f : 10.0f;
            if (intArray == null || intArray.length < 6) {
                return;
            }
            int[] iArr = this.mConditionSize;
            iArr[0] = (int) ((intArray[1] / f) * intArray[0]);
            iArr[1] = (int) ((intArray[2] / f) * intArray[0]);
            iArr[2] = (int) ((intArray[3] / f) * intArray[0]);
            iArr[3] = (int) ((intArray[4] / f) * intArray[0]);
            iArr[4] = (int) ((intArray[5] / f) * intArray[0]);
        }
    }

    public static EdgeSuppressionManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (EdgeSuppressionManager.class) {
                if (sInstance == null) {
                    sInstance = new EdgeSuppressionManager(context);
                }
            }
        }
        return sInstance;
    }

    public int getAllowAdjustRange() {
        int[] iArr = this.mConditionSize;
        return iArr[4] - iArr[0];
    }

    public int getConditionSize(int i) {
        return this.mConditionSize[i];
    }

    public float getOldConditionSize(int i) {
        int[] iArr = this.mConditionSize;
        return iArr[i] / iArr[4];
    }

    public int getOldMaxAdjustValue() {
        int[] intArray = FeatureParser.getIntArray("edge_suppresson_condition");
        if (intArray != null) {
            return intArray[0];
        }
        return 0;
    }

    public int getSizeOfInputMethod(float f, String str) {
        int i;
        if ("custom_suppression".equals(str)) {
            f -= r1[0];
            i = this.mConditionSize[3];
        } else {
            i = this.mConditionSize[4];
        }
        return Math.round((f / i) * 35.0f) + 10;
    }

    public boolean isReflectionFailed() {
        return this.mIsReflectionFailed;
    }

    public boolean isSupportSensor() {
        return this.mSupportSensor;
    }

    public void registerLaySensor(LaySensorWrapper.LaySensorChangeListener laySensorChangeListener) {
        if (laySensorChangeListener != null) {
            if (this.mLaySensorWrapper == null) {
                this.mLaySensorWrapper = new LaySensorWrapper(this.mContext);
            }
            if (this.mLaySensorWrapper.hasListenerRegistered(laySensorChangeListener)) {
                return;
            }
            this.mLaySensorWrapper.registerListener(laySensorChangeListener);
        }
    }

    public void setScreenSize() {
        this.mWindowManager.getDefaultDisplay().getRealSize(this.mCurrentScreenSize);
        Point point = this.mCurrentScreenSize;
        this.mScreenWidth = Math.min(point.x, point.y) - 1;
        Point point2 = this.mCurrentScreenSize;
        this.mScreenHeight = Math.max(point2.x, point2.y) - 1;
    }

    public void unRegisterLaySensor() {
        LaySensorWrapper laySensorWrapper = this.mLaySensorWrapper;
        if (laySensorWrapper != null) {
            laySensorWrapper.unregisterAllListener();
        }
    }
}
