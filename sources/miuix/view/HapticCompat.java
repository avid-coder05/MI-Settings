package miuix.view;

import android.os.Looper;
import android.util.Log;
import android.view.View;
import androidx.annotation.Keep;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/* loaded from: classes5.dex */
public class HapticCompat {
    private static List<HapticFeedbackProvider> sProviders = new ArrayList();
    private static final Executor sSingleThread = Executors.newSingleThreadExecutor();

    /* loaded from: classes5.dex */
    private static class WeakReferenceHandler implements Runnable {
        private final int mFeedbackConstant;
        private final WeakReference<View> mViewReference;

        public WeakReferenceHandler(View view, int i) {
            this.mViewReference = new WeakReference<>(view);
            this.mFeedbackConstant = i;
        }

        @Override // java.lang.Runnable
        public void run() {
            View view = this.mViewReference.get();
            if (view == null || !view.isAttachedToWindow()) {
                return;
            }
            try {
                HapticCompat.performHapticFeedback(view, this.mFeedbackConstant);
            } catch (Exception unused) {
            }
        }
    }

    static {
        loadProviders("miuix.view.LinearVibrator", "miuix.view.ExtendedVibrator");
    }

    private static void loadProviders(String... strArr) {
        for (String str : strArr) {
            Log.i("HapticCompat", "loading provider: " + str);
            try {
                Class.forName(str, true, HapticCompat.class.getClassLoader());
            } catch (ClassNotFoundException e) {
                Log.w("HapticCompat", String.format("load provider %s failed.", str), e);
            }
        }
    }

    public static int obtainFeedBack(int i) {
        for (HapticFeedbackProvider hapticFeedbackProvider : sProviders) {
            if (hapticFeedbackProvider instanceof LinearVibrator) {
                return ((LinearVibrator) hapticFeedbackProvider).obtainFeedBack(i);
            }
        }
        return -1;
    }

    @Keep
    public static boolean performHapticFeedback(View view, int i) {
        if (i < 268435456) {
            Log.i("HapticCompat", String.format("perform haptic: 0x%08x", Integer.valueOf(i)));
            return view.performHapticFeedback(i);
        }
        int i2 = HapticFeedbackConstants.MIUI_HAPTIC_END;
        if (i > i2) {
            Log.w("HapticCompat", String.format("illegal feedback constant, should be in range [0x%08x..0x%08x]", 268435456, Integer.valueOf(i2)));
            return false;
        }
        Iterator<HapticFeedbackProvider> it = sProviders.iterator();
        while (it.hasNext()) {
            if (it.next().performHapticFeedback(view, i)) {
                return true;
            }
        }
        return false;
    }

    @Keep
    public static void performHapticFeedbackAsync(View view, int i) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            sSingleThread.execute(new WeakReferenceHandler(view, i));
        } else {
            performHapticFeedback(view, i);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Keep
    public static void registerProvider(HapticFeedbackProvider hapticFeedbackProvider) {
        sProviders.add(hapticFeedbackProvider);
    }
}
