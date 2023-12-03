package miuix.view;

import android.util.Log;
import android.view.View;
import androidx.annotation.Keep;
import androidx.collection.SparseArrayCompat;
import miui.util.HapticFeedbackUtil;

/* JADX INFO: Access modifiers changed from: package-private */
@Keep
/* loaded from: classes5.dex */
public class LinearVibrator implements HapticFeedbackProvider {
    private static final String TAG = "LinearVibrator";
    private final SparseArrayCompat<Integer> mIds = new SparseArrayCompat<>();

    static {
        initialize();
    }

    private LinearVibrator() {
        buildIds();
    }

    private void buildIds() {
        this.mIds.append(HapticFeedbackConstants.MIUI_TAP_NORMAL, 268435456);
        this.mIds.append(HapticFeedbackConstants.MIUI_TAP_LIGHT, 268435457);
        this.mIds.append(HapticFeedbackConstants.MIUI_FLICK, 268435458);
        this.mIds.append(HapticFeedbackConstants.MIUI_SWITCH, 268435459);
        this.mIds.append(HapticFeedbackConstants.MIUI_MESH_HEAVY, 268435460);
        this.mIds.append(HapticFeedbackConstants.MIUI_MESH_NORMAL, 268435461);
        this.mIds.append(HapticFeedbackConstants.MIUI_MESH_LIGHT, 268435462);
        this.mIds.append(HapticFeedbackConstants.MIUI_LONG_PRESS, 268435463);
        this.mIds.append(HapticFeedbackConstants.MIUI_POPUP_NORMAL, 268435464);
        this.mIds.append(HapticFeedbackConstants.MIUI_POPUP_LIGHT, 268435465);
        int i = PlatformConstants.VERSION;
        if (i < 2) {
            return;
        }
        this.mIds.append(HapticFeedbackConstants.MIUI_PICK_UP, 268435466);
        this.mIds.append(HapticFeedbackConstants.MIUI_SCROLL_EDGE, 268435467);
        this.mIds.append(HapticFeedbackConstants.MIUI_TRIGGER_DRAWER, 268435468);
        if (i < 3) {
            return;
        }
        this.mIds.append(HapticFeedbackConstants.MIUI_FLICK_LIGHT, 268435469);
        if (i < 4) {
            return;
        }
        this.mIds.append(HapticFeedbackConstants.MIUI_HOLD, 268435470);
    }

    private static void initialize() {
        boolean z;
        if (PlatformConstants.VERSION < 1) {
            Log.w(TAG, "MiuiHapticFeedbackConstants not found or not compatible for LinearVibrator.");
            return;
        }
        try {
            z = HapticFeedbackUtil.isSupportLinearMotorVibrate();
        } catch (Throwable th) {
            Log.w(TAG, "MIUI Haptic Implementation is not available", th);
            z = false;
        }
        if (!z) {
            Log.w(TAG, "linear motor is not supported in this platform.");
            return;
        }
        HapticCompat.registerProvider(new LinearVibrator());
        Log.i(TAG, "setup LinearVibrator success.");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int obtainFeedBack(int i) {
        int indexOfKey = this.mIds.indexOfKey(i);
        if (indexOfKey >= 0) {
            return this.mIds.valueAt(indexOfKey).intValue();
        }
        return -1;
    }

    @Override // miuix.view.HapticFeedbackProvider
    public boolean performHapticFeedback(View view, int i) {
        int indexOfKey = this.mIds.indexOfKey(i);
        if (indexOfKey < 0) {
            Log.w(TAG, String.format("feedback(0x%08x-%s) is not found in current platform(v%d)", Integer.valueOf(i), HapticFeedbackConstants.nameOf(i), Integer.valueOf(PlatformConstants.VERSION)));
            return false;
        }
        int intValue = this.mIds.valueAt(indexOfKey).intValue();
        if (HapticFeedbackUtil.isSupportLinearMotorVibrate(intValue)) {
            return view.performHapticFeedback(intValue);
        }
        Log.w(TAG, String.format("unsupported feedback: 0x%08x. platform version: %d", Integer.valueOf(intValue), Integer.valueOf(PlatformConstants.VERSION)));
        return false;
    }

    public boolean supportLinearMotor(int i) {
        int indexOfKey = this.mIds.indexOfKey(i);
        if (indexOfKey < 0) {
            Log.w(TAG, String.format("feedback(0x%08x-%s) is not found in current platform(v%d)", Integer.valueOf(i), HapticFeedbackConstants.nameOf(i), Integer.valueOf(PlatformConstants.VERSION)));
            return false;
        }
        int intValue = this.mIds.valueAt(indexOfKey).intValue();
        if (HapticFeedbackUtil.isSupportLinearMotorVibrate(intValue)) {
            return HapticFeedbackUtil.isSupportLinearMotorVibrate(intValue);
        }
        Log.w(TAG, String.format("unsupported feedback: 0x%08x. platform version: %d", Integer.valueOf(intValue), Integer.valueOf(PlatformConstants.VERSION)));
        return false;
    }
}
