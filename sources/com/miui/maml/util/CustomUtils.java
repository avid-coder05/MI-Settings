package com.miui.maml.util;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;

/* loaded from: classes2.dex */
public class CustomUtils {
    public static void replaceCameraIntentInfoOnF3M(String str, String str2, Intent intent) {
        if ("vela".equals(Build.DEVICE) && intent != null && "com.android.camera".equals(str) && "com.android.camera.Camera".equals(str2)) {
            intent.setComponent(new ComponentName("com.mlab.cam", "com.mtlab.camera.CameraActivity"));
            if ("android.intent.action.MAIN".equals(intent.getAction())) {
                intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
            }
        }
    }
}
