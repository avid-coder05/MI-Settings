package com.android.settings.faceunlock;

import android.content.Context;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.face.BaseMiuiFaceManager;
import android.hardware.face.Face;
import android.hardware.face.FaceManager;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.os.UserHandle;
import android.util.Slog;
import android.view.Surface;
import miui.util.FeatureParser;

/* loaded from: classes.dex */
public class KeyguardSettingsFaceUnlockManager {
    protected static int mFaceUnlockType;
    private static volatile KeyguardSettingsFaceUnlockManager sInstance;
    protected Context mContext;
    private CancellationSignal mEnrollCancelSignal;
    private BaseMiuiFaceManager mFaceManager;
    private FaceRemoveCallback mFaceRemoveCallback;
    protected Handler mWorkerHandler;
    protected HandlerThread mHandlerThread = new HandlerThread("face_unlock_enroll");
    boolean isSupportTee = FeatureParser.getBoolean("support_tee_face_unlock", false);
    FaceManager.RemovalCallback mRemovalCallback = new FaceManager.RemovalCallback() { // from class: com.android.settings.faceunlock.KeyguardSettingsFaceUnlockManager.1
        public void onRemovalError(Face face, int i, CharSequence charSequence) {
            Slog.i("miui_face", "mRemovalCallback, onRemovalError code:" + i + " msg:" + ((Object) charSequence) + ";id=");
            if (KeyguardSettingsFaceUnlockManager.this.mFaceRemoveCallback != null) {
                KeyguardSettingsFaceUnlockManager.this.mFaceRemoveCallback.onFailed();
            }
        }

        public void onRemovalSucceeded(Face face, int i) {
            Slog.i("miui_face", "mRemovalCallback, onRemovalSucceeded id=;remaining=" + i);
            if (KeyguardSettingsFaceUnlockManager.this.mFaceRemoveCallback != null) {
                KeyguardSettingsFaceUnlockManager.this.mFaceRemoveCallback.onRemoved();
            }
            KeyguardSettingsFaceUnlockUtils.resetFaceUnlockSettingValues(KeyguardSettingsFaceUnlockManager.this.mContext);
        }
    };

    public KeyguardSettingsFaceUnlockManager(Context context) {
        this.mContext = context.getApplicationContext();
        this.mHandlerThread.start();
        this.mWorkerHandler = new Handler(this.mHandlerThread.getLooper());
        this.mFaceManager = (BaseMiuiFaceManager) context.getSystemService("miui_face");
    }

    public static KeyguardSettingsFaceUnlockManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (KeyguardSettingsFaceUnlockManager.class) {
                if (sInstance == null) {
                    sInstance = new KeyguardSettingsFaceUnlockManager(context);
                }
            }
        }
        mFaceUnlockType = 0;
        return sInstance;
    }

    public void deleteFeature(String str, FaceRemoveCallback faceRemoveCallback) {
        if (KeyguardSettingsFaceUnlockUtils.hasEnrolledFaces(this.mContext)) {
            Slog.i("miui_face", "deleteFeature faceId=" + str);
            this.mFaceRemoveCallback = faceRemoveCallback;
            this.mFaceManager.remove(new Face((CharSequence) null, Integer.parseInt(str), 0L), UserHandle.myUserId(), this.mRemovalCallback);
        }
    }

    public void generateFaceEnrollChallenge(FaceManager.GenerateChallengeCallback generateChallengeCallback) {
        if (this.isSupportTee) {
            getFaceManager().generateChallenge(UserHandle.myUserId(), generateChallengeCallback);
        } else {
            generateChallengeCallback.onGenerateChallengeResult(-1, -1, 0L);
        }
    }

    public BaseMiuiFaceManager getFaceManager() {
        if (this.mFaceManager == null) {
            this.mFaceManager = (BaseMiuiFaceManager) this.mContext.getSystemService("miui_face");
        }
        return this.mFaceManager;
    }

    public void runOnFaceUnlockWorkerThread(Runnable runnable) {
        HandlerThread handlerThread = this.mHandlerThread;
        if (handlerThread == null || this.mWorkerHandler == null) {
            return;
        }
        if (handlerThread.getThreadId() == Process.myTid()) {
            runnable.run();
        } else {
            this.mWorkerHandler.post(runnable);
        }
    }

    public void sendEnrollCommand(int i) {
        getFaceManager().extCmd(i, 0);
    }

    public boolean startEnrollFace(byte[] bArr, SurfaceTexture surfaceTexture, FaceManager.EnrollmentCallback enrollmentCallback, RectF rectF, RectF rectF2, int i, int i2) {
        CancellationSignal cancellationSignal = this.mEnrollCancelSignal;
        if (cancellationSignal != null && !cancellationSignal.isCanceled()) {
            Slog.i("miui_face", "call mEnrollCancelSignal.cancel(), return.");
            this.mEnrollCancelSignal.cancel();
            this.mEnrollCancelSignal = null;
            return false;
        }
        Slog.i("miui_face", "start enrollFace");
        surfaceTexture.setDefaultBufferSize(640, 480);
        Surface surface = new Surface(surfaceTexture);
        CancellationSignal cancellationSignal2 = new CancellationSignal();
        this.mEnrollCancelSignal = cancellationSignal2;
        this.mFaceManager.enroll(bArr, cancellationSignal2, 0, enrollmentCallback, surface, rectF, rectF2, i);
        return true;
    }

    public void stopEnrollFace() {
        Slog.i("miui_face", "stop enrollFace");
        CancellationSignal cancellationSignal = this.mEnrollCancelSignal;
        if (cancellationSignal == null || cancellationSignal.isCanceled()) {
            return;
        }
        Slog.i("miui_face", "call mEnrollCancelSignal.cancel(), return.");
        this.mEnrollCancelSignal.cancel();
        this.mEnrollCancelSignal = null;
    }
}
