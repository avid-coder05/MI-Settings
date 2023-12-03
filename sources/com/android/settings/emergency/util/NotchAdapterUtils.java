package com.android.settings.emergency.util;

import android.app.Activity;
import android.view.WindowManager;

/* loaded from: classes.dex */
public class NotchAdapterUtils {
    public static void fitNotchForFullScreen(Activity activity) {
        WindowManager.LayoutParams attributes = activity.getWindow().getAttributes();
        attributes.layoutInDisplayCutoutMode = 1;
        activity.getWindow().setAttributes(attributes);
    }
}
