package com.xiaomi.micloudsdk.utils;

import android.util.Log;

/* loaded from: classes2.dex */
public class MiCloudSDKDependencyUtil {
    public static final int SDKEnvironment;

    static {
        int sDKEnvironment = getSDKEnvironment();
        SDKEnvironment = sDKEnvironment;
        Log.i("MiCloudSDKDependencyUtil", "MiCloudSDK environment: " + sDKEnvironment);
    }

    private static int getSDKEnvironment() {
        Class loadClass = ReflectUtils.loadClass("com.xiaomi.micloudsdk.os.MiCloudSdkVersion");
        if (loadClass != null) {
            return ReflectUtils.getStaticFieldIntValue(loadClass, "version");
        }
        if (ReflectUtils.loadClass("miui.cloud.helper.BroadcastIntentHelper") != null) {
            return 25;
        }
        return ReflectUtils.loadClass("com.xiaomi.micloudsdk.utils.MiCloudRuntimeConstants") != null ? 18 : -1;
    }
}
