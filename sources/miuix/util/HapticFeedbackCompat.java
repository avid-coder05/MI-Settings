package miuix.util;

import android.content.Context;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import miui.util.HapticFeedbackUtil;
import miuix.view.HapticCompat;
import miuix.view.PlatformConstants;

/* loaded from: classes5.dex */
public class HapticFeedbackCompat {
    private static boolean mAvailable;
    private static boolean mCanCheckExtHaptic;
    private static boolean mCanStop;
    private static boolean mExtHapticAlways;
    private static boolean mIsSupportExtHapticWithReason;
    private static boolean mIsSupportHapticWithReason;
    private static final Executor sSingleThread = Executors.newSingleThreadExecutor();
    private HapticFeedbackUtil hapticFeedbackUtil;

    static {
        if (PlatformConstants.VERSION >= 1) {
            try {
                mAvailable = HapticFeedbackUtil.isSupportLinearMotorVibrate();
            } catch (Throwable th) {
                android.util.Log.w("HapticFeedbackCompat", "MIUI Haptic Implementation is not available", th);
                mAvailable = false;
            }
            if (mAvailable) {
                try {
                    HapticFeedbackUtil.class.getMethod("performHapticFeedback", Integer.TYPE, Double.TYPE, String.class);
                    mIsSupportHapticWithReason = true;
                } catch (Throwable th2) {
                    android.util.Log.w("HapticFeedbackCompat", "Not support haptic with reason", th2);
                    mIsSupportHapticWithReason = false;
                }
                try {
                    HapticFeedbackUtil.class.getMethod("isSupportExtHapticFeedback", Integer.TYPE);
                    mCanCheckExtHaptic = true;
                } catch (Throwable unused) {
                    mCanCheckExtHaptic = false;
                }
                try {
                    HapticFeedbackUtil.class.getMethod("performExtHapticFeedback", Integer.TYPE, Boolean.TYPE);
                    mExtHapticAlways = true;
                } catch (Throwable unused2) {
                    mExtHapticAlways = false;
                }
                try {
                    HapticFeedbackUtil.class.getMethod("stop", new Class[0]);
                    mCanStop = true;
                } catch (Throwable unused3) {
                    mCanStop = false;
                }
                try {
                    HapticFeedbackUtil.class.getMethod("performExtHapticFeedback", Integer.TYPE, Double.TYPE, String.class);
                    mIsSupportExtHapticWithReason = true;
                } catch (Throwable th3) {
                    android.util.Log.w("HapticFeedbackCompat", "Not support ext haptic with reason", th3);
                    mIsSupportExtHapticWithReason = false;
                }
            }
        }
    }

    public HapticFeedbackCompat(Context context) {
        this(context, true);
    }

    @Deprecated
    public HapticFeedbackCompat(Context context, boolean z) {
        if (PlatformConstants.VERSION < 1) {
            android.util.Log.w("HapticFeedbackCompat", "MiuiHapticFeedbackConstants not found or not compatible for LinearVibrator.");
        } else if (mAvailable) {
            this.hapticFeedbackUtil = new HapticFeedbackUtil(context, z);
        } else {
            android.util.Log.w("HapticFeedbackCompat", "linear motor is not supported in this platform.");
        }
    }

    public boolean performExtHapticFeedback(int i) {
        HapticFeedbackUtil hapticFeedbackUtil = this.hapticFeedbackUtil;
        if (hapticFeedbackUtil != null) {
            return hapticFeedbackUtil.performExtHapticFeedback(i);
        }
        return false;
    }

    public boolean performHapticFeedback(int i) {
        return performHapticFeedback(i, false);
    }

    public boolean performHapticFeedback(int i, boolean z) {
        int obtainFeedBack;
        if (this.hapticFeedbackUtil == null || (obtainFeedBack = HapticCompat.obtainFeedBack(i)) == -1) {
            return false;
        }
        return this.hapticFeedbackUtil.performHapticFeedback(obtainFeedBack, z);
    }

    public boolean supportLinearMotor() {
        return mAvailable;
    }
}
