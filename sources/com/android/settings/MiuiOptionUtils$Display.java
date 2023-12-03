package com.android.settings;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import com.android.internal.view.RotationPolicy;

/* loaded from: classes.dex */
public class MiuiOptionUtils$Display {
    public static int touchPaperModeState(Context context, int i) {
        ContentResolver contentResolver = context.getContentResolver();
        int i2 = Settings.System.getInt(contentResolver, "screen_paper_mode_enabled", 0);
        if (i == -1 || i == i2) {
            return i2;
        }
        Settings.System.putInt(contentResolver, "screen_paper_mode_enabled", i);
        return i;
    }

    public static int touchRotationLockState(Context context, int i) {
        boolean isRotationLocked = RotationPolicy.isRotationLocked(context);
        if (i == -1 || i == isRotationLocked) {
            return isRotationLocked ? 1 : 0;
        }
        RotationPolicy.setRotationLock(context, i != 0);
        return i;
    }
}
