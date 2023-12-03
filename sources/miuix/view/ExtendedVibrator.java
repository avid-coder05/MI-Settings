package miuix.view;

import android.util.Log;
import android.view.View;
import androidx.annotation.Keep;

@Keep
/* loaded from: classes5.dex */
class ExtendedVibrator implements HapticFeedbackProvider {
    private static final String TAG = "ExtendedVibrator";

    static {
        initialize();
    }

    private ExtendedVibrator() {
    }

    private static void initialize() {
        if (PlatformConstants.VERSION < 0) {
            Log.w(TAG, "MiuiHapticFeedbackConstants not found.");
            return;
        }
        HapticCompat.registerProvider(new ExtendedVibrator());
        Log.i(TAG, "setup ExtendedVibrator success.");
    }

    @Override // miuix.view.HapticFeedbackProvider
    public boolean performHapticFeedback(View view, int i) {
        if (i == HapticFeedbackConstants.MIUI_VIRTUAL_RELEASE) {
            return view.performHapticFeedback(2);
        }
        return false;
    }
}
