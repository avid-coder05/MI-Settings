package com.android.settings.biometrics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.android.settings.biometrics.face.FaceEnrollParentalConsent;
import com.android.settings.biometrics.fingerprint.FingerprintEnrollParentalConsent;
import com.google.android.setupcompat.util.WizardManagerHelper;

/* loaded from: classes.dex */
public class ParentalConsentHelper {
    private Boolean mConsentFace;
    private Boolean mConsentFingerprint;
    private long mGkPwHandle;
    private final boolean mRequireFace;
    private final boolean mRequireFingerprint;

    public ParentalConsentHelper(boolean z, boolean z2, Long l) {
        this.mRequireFace = z;
        this.mRequireFingerprint = z2;
        this.mGkPwHandle = l != null ? l.longValue() : 0L;
    }

    private Intent getNextConsentIntent(Context context) {
        if (this.mRequireFace && this.mConsentFace == null) {
            return new Intent(context, FaceEnrollParentalConsent.class);
        }
        if (this.mRequireFingerprint && this.mConsentFingerprint == null) {
            return new Intent(context, FingerprintEnrollParentalConsent.class);
        }
        return null;
    }

    public static boolean hasFaceConsent(Bundle bundle) {
        return bundle.getBoolean("face", false);
    }

    public static boolean hasFingerprintConsent(Bundle bundle) {
        return bundle.getBoolean("fingerprint", false);
    }

    private static Boolean isConsent(int i, Boolean bool) {
        return i != 4 ? i != 5 ? bool : Boolean.FALSE : Boolean.TRUE;
    }

    public Bundle getConsentResult() {
        Bundle bundle = new Bundle();
        Boolean bool = this.mConsentFace;
        bundle.putBoolean("face", bool != null ? bool.booleanValue() : false);
        Boolean bool2 = this.mConsentFingerprint;
        bundle.putBoolean("fingerprint", bool2 != null ? bool2.booleanValue() : false);
        bundle.putBoolean("iris", false);
        return bundle;
    }

    public boolean launchNext(Activity activity, int i) {
        Intent nextConsentIntent = getNextConsentIntent(activity);
        if (nextConsentIntent != null) {
            WizardManagerHelper.copyWizardManagerExtras(activity.getIntent(), nextConsentIntent);
            long j = this.mGkPwHandle;
            if (j != 0) {
                nextConsentIntent.putExtra("gk_pw_handle", j);
            }
            activity.startActivityForResult(nextConsentIntent, i);
            return true;
        }
        return false;
    }

    public boolean launchNext(Activity activity, int i, int i2, Intent intent) {
        if (intent != null) {
            int intExtra = intent.getIntExtra("sensor_modality", 0);
            if (intExtra == 2) {
                this.mConsentFingerprint = isConsent(i2, this.mConsentFingerprint);
            } else if (intExtra == 8) {
                this.mConsentFace = isConsent(i2, this.mConsentFace);
            }
        }
        return launchNext(activity, i);
    }

    public void updateGatekeeperHandle(Intent intent) {
        this.mGkPwHandle = BiometricUtils.getGatekeeperPasswordHandle(intent);
    }
}
