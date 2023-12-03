package com.android.settingslib.users;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import com.android.settings.search.SearchUpdater;

/* loaded from: classes2.dex */
public class PhotoCapabilityUtils {
    public static boolean canChoosePhoto(Context context) {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        return (context.getPackageManager().queryIntentActivities(intent, 0).size() > 0) && !isDeviceLocked(context);
    }

    public static boolean canCropPhoto(Context context) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        return (context.getPackageManager().queryIntentActivities(intent, 0).size() > 0) && !isDeviceLocked(context);
    }

    public static boolean canTakePhoto(Context context) {
        return context.getPackageManager().queryIntentActivities(new Intent("android.media.action.IMAGE_CAPTURE"), SearchUpdater.GOOGLE).size() > 0;
    }

    private static boolean isDeviceLocked(Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(KeyguardManager.class);
        return keyguardManager == null || keyguardManager.isDeviceLocked();
    }
}
