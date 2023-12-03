package com.android.settings;

/* loaded from: classes.dex */
public interface FingerprintAddListener {
    void addFingerprintCompleted();

    void addFingerprintFailed();

    void addFingerprintProgress(int i);

    void onEnrollmentHelp(int i, CharSequence charSequence);
}
