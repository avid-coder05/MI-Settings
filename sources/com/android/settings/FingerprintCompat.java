package com.android.settings;

import android.hardware.fingerprint.Fingerprint;

/* loaded from: classes.dex */
public class FingerprintCompat {
    public static int getFingerIdForFingerprint(Fingerprint fingerprint) {
        return fingerprint.getBiometricId();
    }
}
