package miuix.view;

import androidx.collection.SparseArrayCompat;

/* loaded from: classes5.dex */
public class HapticFeedbackConstants {
    private static final SparseArrayCompat<String> NAMES = new SparseArrayCompat<>();
    public static final int MIUI_VIRTUAL_RELEASE = 268435456;
    public static final int MIUI_TAP_NORMAL = 268435457;
    public static final int MIUI_TAP_LIGHT = 268435458;
    public static final int MIUI_FLICK = 268435459;
    public static final int MIUI_SWITCH = 268435460;
    public static final int MIUI_MESH_HEAVY = 268435461;
    public static final int MIUI_MESH_NORMAL = 268435462;
    public static final int MIUI_MESH_LIGHT = 268435463;
    public static final int MIUI_LONG_PRESS = 268435464;
    public static final int MIUI_POPUP_NORMAL = 268435465;
    public static final int MIUI_POPUP_LIGHT = 268435466;
    public static final int MIUI_PICK_UP = 268435467;
    public static final int MIUI_SCROLL_EDGE = 268435468;
    public static final int MIUI_TRIGGER_DRAWER = 268435469;
    public static final int MIUI_FLICK_LIGHT = 268435470;
    public static final int MIUI_HOLD = 268435471;
    static final int MIUI_HAPTIC_END = 268435472;

    static {
        buildNames();
    }

    private static void buildNames() {
        SparseArrayCompat<String> sparseArrayCompat = NAMES;
        sparseArrayCompat.append(MIUI_VIRTUAL_RELEASE, "MIUI_VIRTUAL_RELEASE");
        sparseArrayCompat.append(MIUI_TAP_NORMAL, "MIUI_TAP_NORMAL");
        sparseArrayCompat.append(MIUI_TAP_LIGHT, "MIUI_TAP_LIGHT");
        sparseArrayCompat.append(MIUI_FLICK, "MIUI_FLICK");
        sparseArrayCompat.append(MIUI_SWITCH, "MIUI_SWITCH");
        sparseArrayCompat.append(MIUI_MESH_HEAVY, "MIUI_MESH_HEAVY");
        sparseArrayCompat.append(MIUI_MESH_NORMAL, "MIUI_MESH_NORMAL");
        sparseArrayCompat.append(MIUI_MESH_LIGHT, "MIUI_MESH_LIGHT");
        sparseArrayCompat.append(MIUI_LONG_PRESS, "MIUI_LONG_PRESS");
        sparseArrayCompat.append(MIUI_POPUP_NORMAL, "MIUI_POPUP_NORMAL");
        sparseArrayCompat.append(MIUI_POPUP_LIGHT, "MIUI_POPUP_LIGHT");
        sparseArrayCompat.append(MIUI_PICK_UP, "MIUI_PICK_UP");
        sparseArrayCompat.append(MIUI_SCROLL_EDGE, "MIUI_SCROLL_EDGE");
        sparseArrayCompat.append(MIUI_TRIGGER_DRAWER, "MIUI_TRIGGER_DRAWER");
        sparseArrayCompat.append(MIUI_FLICK_LIGHT, "MIUI_FLICK_LIGHT");
        sparseArrayCompat.append(MIUI_HOLD, "MIUI_HOLD");
    }

    public static String nameOf(int i) {
        return NAMES.get(i, "IllegalFeedback");
    }
}
