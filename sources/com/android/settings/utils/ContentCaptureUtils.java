package com.android.settings.utils;

import android.content.ComponentName;
import android.content.Context;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.view.contentcapture.ContentCaptureManager;

/* loaded from: classes2.dex */
public final class ContentCaptureUtils {
    private static final int MY_USER_ID = UserHandle.myUserId();
    private static final String TAG = "ContentCaptureUtils";

    public static ComponentName getServiceSettingsComponentName() {
        try {
            return ContentCaptureManager.getServiceSettingsComponentName();
        } catch (RuntimeException e) {
            Log.w(TAG, "Could not get service settings: " + e);
            return null;
        }
    }

    public static boolean isEnabledForUser(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), "content_capture_enabled", 1, MY_USER_ID) == 1;
    }

    public static boolean isFeatureAvailable() {
        return ServiceManager.checkService("content_capture") != null;
    }

    public static void setEnabledForUser(Context context, boolean z) {
        Settings.Secure.putIntForUser(context.getContentResolver(), "content_capture_enabled", z ? 1 : 0, MY_USER_ID);
    }
}
