package com.android.settings;

import android.content.Context;
import com.android.settings.utils.AodUtils;

/* loaded from: classes.dex */
public class AodCompatibilityHelper {
    public static boolean isAodAvailable(Context context) {
        return AodUtils.isAodAvailable(context);
    }
}
