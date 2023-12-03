package com.xiaomi.micloudsdk.utils;

import android.util.Log;

/* loaded from: classes2.dex */
public class MiCloudSdkBuild {
    public static final int CURRENT_VERSION;

    static {
        int i = MiCloudSDKDependencyUtil.SDKEnvironment;
        if (i < 0) {
            throw new RuntimeException("No MiCloudSDK runtime!");
        }
        CURRENT_VERSION = i;
        Log.i("MiCloudSdkBuild", "MiCloudSdk version: " + i);
    }
}
