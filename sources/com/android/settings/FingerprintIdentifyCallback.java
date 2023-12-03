package com.android.settings;

/* loaded from: classes.dex */
public interface FingerprintIdentifyCallback {
    void onFailed();

    void onIdentified(int i);

    default void onLockout() {
    }
}
