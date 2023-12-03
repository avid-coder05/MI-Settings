package com.android.settings;

import android.content.Context;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.os.UserHandle;
import android.security.FingerprintIdUtils;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class FingerprintHelper {
    private CancellationSignal mCancellationSignal;
    private boolean mCompleteFingerEnroll = false;
    private final Context mContext;
    private boolean mEnroll;
    private FingerprintManager mFingerprintMgr;

    public FingerprintHelper(Context context) {
        this.mContext = context;
    }

    private void initFingerprintManager() {
        if (this.mFingerprintMgr == null && this.mContext.getPackageManager().hasSystemFeature("android.hardware.fingerprint")) {
            this.mFingerprintMgr = (FingerprintManager) this.mContext.getSystemService("fingerprint");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFingerprintId(int i, List<String> list, List<String> list2) {
        for (String str : list2) {
            if (!list.contains(str)) {
                HashMap userFingerprintIds = FingerprintIdUtils.getUserFingerprintIds(this.mContext, i);
                if (userFingerprintIds == null || userFingerprintIds.size() == 0) {
                    userFingerprintIds = new HashMap(1);
                }
                userFingerprintIds.put(str, Integer.valueOf(i));
                FingerprintIdUtils.putUserFingerprintIds(this.mContext, userFingerprintIds);
            }
        }
    }

    public void cancelEnrol() {
        CancellationSignal cancellationSignal = this.mCancellationSignal;
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }
    }

    public void cancelIdentify() {
        CancellationSignal cancellationSignal = this.mCancellationSignal;
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }
    }

    public void generateChallenge(int i, FingerprintManager.GenerateChallengeCallback generateChallengeCallback) {
        initFingerprintManager();
        this.mFingerprintMgr.generateChallenge(i, generateChallengeCallback);
    }

    public List<String> getFingerprintIds() {
        initFingerprintManager();
        FingerprintManager fingerprintManager = this.mFingerprintMgr;
        if (fingerprintManager == null) {
            return new ArrayList();
        }
        List enrolledFingerprints = fingerprintManager.getEnrolledFingerprints();
        ArrayList arrayList = new ArrayList();
        if (enrolledFingerprints != null && enrolledFingerprints.size() > 0) {
            Iterator it = enrolledFingerprints.iterator();
            while (it.hasNext()) {
                arrayList.add(Integer.toString(FingerprintCompat.getFingerIdForFingerprint((Fingerprint) it.next())));
            }
        }
        return arrayList;
    }

    public boolean hasEnrolledFingerprintsAppLock() {
        FingerprintManager fingerprintManager = this.mFingerprintMgr;
        if (fingerprintManager == null) {
            return false;
        }
        try {
            return fingerprintManager.hasEnrolledFingerprints();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void identify(FingerprintIdentifyCallback fingerprintIdentifyCallback, List<String> list) {
        identify(fingerprintIdentifyCallback, list, 0);
    }

    public void identify(final FingerprintIdentifyCallback fingerprintIdentifyCallback, List<String> list, int i) {
        if (fingerprintIdentifyCallback == null || list == null || list.size() == 0) {
            throw new IllegalArgumentException("callback can not be null, and ids can not be null or empty");
        }
        initFingerprintManager();
        if (this.mFingerprintMgr == null) {
            return;
        }
        this.mCancellationSignal = new CancellationSignal();
        this.mFingerprintMgr.authenticate(null, this.mCancellationSignal, i, new FingerprintManager.AuthenticationCallback() { // from class: com.android.settings.FingerprintHelper.3
            public void onAuthenticationAcquired(int i2) {
                super.onAuthenticationAcquired(i2);
            }

            @Override // android.hardware.fingerprint.FingerprintManager.AuthenticationCallback
            public void onAuthenticationError(int i2, CharSequence charSequence) {
                super.onAuthenticationError(i2, charSequence);
                if (i2 == 7 || i2 == 9) {
                    fingerprintIdentifyCallback.onLockout();
                }
            }

            @Override // android.hardware.fingerprint.FingerprintManager.AuthenticationCallback
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                fingerprintIdentifyCallback.onFailed();
            }

            @Override // android.hardware.fingerprint.FingerprintManager.AuthenticationCallback
            public void onAuthenticationHelp(int i2, CharSequence charSequence) {
                super.onAuthenticationHelp(i2, charSequence);
            }

            @Override // android.hardware.fingerprint.FingerprintManager.AuthenticationCallback
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult authenticationResult) {
                super.onAuthenticationSucceeded(authenticationResult);
                fingerprintIdentifyCallback.onIdentified(FingerprintCompat.getFingerIdForFingerprint(authenticationResult.getFingerprint()));
            }
        }, null);
    }

    public boolean isHardwareDetected() {
        initFingerprintManager();
        FingerprintManager fingerprintManager = this.mFingerprintMgr;
        return fingerprintManager != null && fingerprintManager.isHardwareDetected();
    }

    public void removeAllFingerprint(FingerprintRemoveCallback fingerprintRemoveCallback) {
        removeFingerprint("0", fingerprintRemoveCallback, UserHandle.myUserId());
    }

    public void removeFingerprint(String str, FingerprintRemoveCallback fingerprintRemoveCallback) {
        removeFingerprint(str, fingerprintRemoveCallback, UserHandle.myUserId());
    }

    public void removeFingerprint(String str, final FingerprintRemoveCallback fingerprintRemoveCallback, int i) {
        initFingerprintManager();
        if (this.mFingerprintMgr == null) {
            return;
        }
        Fingerprint fingerprint = new Fingerprint((CharSequence) null, 0, Integer.parseInt(str), 0L);
        FingerprintManager.RemovalCallback removalCallback = new FingerprintManager.RemovalCallback() { // from class: com.android.settings.FingerprintHelper.2
            public void onRemovalError(Fingerprint fingerprint2, int i2, CharSequence charSequence) {
                FingerprintRemoveCallback fingerprintRemoveCallback2 = fingerprintRemoveCallback;
                if (fingerprintRemoveCallback2 != null) {
                    fingerprintRemoveCallback2.onFailed();
                }
            }

            public void onRemovalSucceeded(Fingerprint fingerprint2, int i2) {
                FingerprintRemoveCallback fingerprintRemoveCallback2 = fingerprintRemoveCallback;
                if (fingerprintRemoveCallback2 == null || i2 != 0) {
                    return;
                }
                fingerprintRemoveCallback2.onRemoved();
            }
        };
        if (TextUtils.equals("0", str)) {
            this.mFingerprintMgr.removeAll(i, removalCallback);
        } else {
            this.mFingerprintMgr.remove(fingerprint, i, removalCallback);
        }
    }

    public void startEnrol(FingerprintAddListener fingerprintAddListener, byte[] bArr) {
        startEnrol(fingerprintAddListener, bArr, UserHandle.myUserId());
    }

    public void startEnrol(final FingerprintAddListener fingerprintAddListener, byte[] bArr, int i) {
        initFingerprintManager();
        if (this.mFingerprintMgr == null) {
            return;
        }
        this.mCancellationSignal = new CancellationSignal();
        final List<String> fingerprintIds = getFingerprintIds();
        this.mEnroll = true;
        FingerprintManager.EnrollmentCallback enrollmentCallback = new FingerprintManager.EnrollmentCallback() { // from class: com.android.settings.FingerprintHelper.1
            public void onEnrollmentError(int i2, CharSequence charSequence) {
                super.onEnrollmentError(i2, charSequence);
                if (FingerprintHelper.this.mEnroll) {
                    fingerprintAddListener.addFingerprintFailed();
                    FingerprintHelper.this.mEnroll = !r0.mEnroll;
                }
            }

            public void onEnrollmentHelp(int i2, CharSequence charSequence) {
                super.onEnrollmentHelp(i2, charSequence);
                fingerprintAddListener.onEnrollmentHelp(i2, charSequence);
            }

            public void onEnrollmentProgress(int i2) {
                super.onEnrollmentProgress(i2);
                if (i2 != 0) {
                    fingerprintAddListener.addFingerprintProgress(i2);
                    return;
                }
                fingerprintAddListener.addFingerprintCompleted();
                FingerprintHelper.this.mCompleteFingerEnroll = true;
                if (UserHandle.myUserId() != 0) {
                    FingerprintHelper.this.setFingerprintId(UserHandle.myUserId(), fingerprintIds, FingerprintHelper.this.getFingerprintIds());
                }
            }
        };
        if (bArr != null) {
            this.mFingerprintMgr.enroll(bArr, this.mCancellationSignal, i, enrollmentCallback, 0);
            return;
        }
        for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
            Log.d(FingerprintHelper.class.getSimpleName(), stackTraceElement.getFileName() + " " + stackTraceElement.getMethodName() + " " + stackTraceElement.getLineNumber());
        }
    }
}
