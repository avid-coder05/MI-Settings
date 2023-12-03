package com.android.settings.biometrics.fingerprint;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import com.android.settings.R;
import com.android.settings.Utils;

/* loaded from: classes.dex */
public class FingerprintSuggestionActivity extends SetupFingerprintEnrollIntroduction {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isFingerprintEnabled(Context context) {
        return (((DevicePolicyManager) context.getSystemService("device_policy")).getKeyguardDisabledFeatures(null, context.getUserId()) & 32) == 0;
    }

    private static boolean isNotSingleFingerprintEnrolled(Context context) {
        FingerprintManager fingerprintManagerOrNull = Utils.getFingerprintManagerOrNull(context);
        return fingerprintManagerOrNull == null || fingerprintManagerOrNull.getEnrolledFingerprints().size() != 1;
    }

    public static boolean isSuggestionComplete(Context context) {
        return (Utils.hasFingerprintHardware(context) && isFingerprintEnabled(context) && !isNotSingleFingerprintEnrolled(context)) ? false : true;
    }

    @Override // miuix.appcompat.app.AppCompatActivity, android.app.Activity
    public void finish() {
        setResult(0);
        super.finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.biometrics.BiometricEnrollIntroduction, com.android.settings.biometrics.BiometricEnrollBase
    public void initViews() {
        super.initViews();
        getCancelButton().setText(this, R.string.security_settings_fingerprint_enroll_introduction_cancel);
    }
}
