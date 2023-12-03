package com.android.settings.utils;

import android.content.Context;
import android.hardware.SensorPrivacyManager;
import android.util.ArraySet;
import android.util.SparseArray;
import com.android.settings.utils.SensorPrivacyManagerHelper;
import java.util.Iterator;
import java.util.concurrent.Executor;

/* loaded from: classes2.dex */
public class SensorPrivacyManagerHelper {
    private static SensorPrivacyManagerHelper sInstance;
    private final SensorPrivacyManager mSensorPrivacyManager;
    private final SparseArray<Boolean> mCurrentUserCachedState = new SparseArray<>();
    private final SparseArray<SparseArray<Boolean>> mCachedState = new SparseArray<>();
    private final SparseArray<SensorPrivacyManager.OnSensorPrivacyChangedListener> mCurrentUserServiceListeners = new SparseArray<>();
    private final SparseArray<SparseArray<SensorPrivacyManager.OnSensorPrivacyChangedListener>> mServiceListeners = new SparseArray<>();
    private final ArraySet<CallbackInfo> mCallbacks = new ArraySet<>();
    private final Object mLock = new Object();

    /* loaded from: classes2.dex */
    public interface Callback {
        void onSensorPrivacyChanged(int i, boolean z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class CallbackInfo {
        Callback mCallback;
        Executor mExecutor;
        int mSensor;
        int mUserId;

        CallbackInfo(Callback callback, Executor executor, int i, int i2) {
            this.mCallback = callback;
            this.mExecutor = executor;
            this.mSensor = i;
            this.mUserId = i2;
        }
    }

    private SensorPrivacyManagerHelper(Context context) {
        this.mSensorPrivacyManager = (SensorPrivacyManager) context.getSystemService(SensorPrivacyManager.class);
    }

    private void dispatchStateChangedLocked(final int i, final boolean z, int i2) {
        Iterator<CallbackInfo> it = this.mCallbacks.iterator();
        while (it.hasNext()) {
            CallbackInfo next = it.next();
            if (next.mUserId == i2 && next.mSensor == i) {
                final Callback callback = next.mCallback;
                next.mExecutor.execute(new Runnable() { // from class: com.android.settings.utils.SensorPrivacyManagerHelper$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        SensorPrivacyManagerHelper.Callback.this.onSensorPrivacyChanged(i, z);
                    }
                });
            }
        }
    }

    public static SensorPrivacyManagerHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SensorPrivacyManagerHelper(context);
        }
        return sInstance;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$registerCurrentUserListenerIfNeeded$1(int i, int i2, boolean z) {
        this.mCurrentUserCachedState.put(i, Boolean.valueOf(z));
        dispatchStateChangedLocked(i, z, -1);
    }

    private void registerCurrentUserListenerIfNeeded(final int i) {
        synchronized (this.mLock) {
            if (!this.mCurrentUserServiceListeners.contains(i)) {
                SensorPrivacyManager.OnSensorPrivacyChangedListener onSensorPrivacyChangedListener = new SensorPrivacyManager.OnSensorPrivacyChangedListener() { // from class: com.android.settings.utils.SensorPrivacyManagerHelper$$ExternalSyntheticLambda0
                    public final void onSensorPrivacyChanged(int i2, boolean z) {
                        SensorPrivacyManagerHelper.this.lambda$registerCurrentUserListenerIfNeeded$1(i, i2, z);
                    }
                };
                this.mCurrentUserServiceListeners.put(i, onSensorPrivacyChangedListener);
                this.mSensorPrivacyManager.addSensorPrivacyListener(i, onSensorPrivacyChangedListener);
            }
        }
    }

    public void addSensorBlockedListener(int i, Callback callback, Executor executor) {
        synchronized (this.mLock) {
            this.mCallbacks.add(new CallbackInfo(callback, executor, i, -1));
        }
    }

    public boolean isSensorBlocked(int i) {
        boolean booleanValue;
        synchronized (this.mLock) {
            Boolean bool = this.mCurrentUserCachedState.get(i);
            if (bool == null) {
                registerCurrentUserListenerIfNeeded(i);
                bool = Boolean.valueOf(this.mSensorPrivacyManager.isSensorPrivacyEnabled(i));
                this.mCurrentUserCachedState.put(i, bool);
            }
            booleanValue = bool.booleanValue();
        }
        return booleanValue;
    }

    public void setSensorBlockedForProfileGroup(int i, int i2, boolean z) {
        this.mSensorPrivacyManager.setSensorPrivacyForProfileGroup(i, i2, z);
    }

    public boolean supportsSensorToggle(int i) {
        return this.mSensorPrivacyManager.supportsSensorToggle(i);
    }
}
