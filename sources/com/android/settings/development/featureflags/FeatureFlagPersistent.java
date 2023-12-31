package com.android.settings.development.featureflags;

import android.content.Context;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.FeatureFlagUtils;
import java.util.HashSet;

/* loaded from: classes.dex */
public class FeatureFlagPersistent {
    private static final HashSet<String> PERSISTENT_FLAGS;

    static {
        HashSet<String> hashSet = new HashSet<>();
        PERSISTENT_FLAGS = hashSet;
        hashSet.add("settings_bluetooth_hearing_aid");
    }

    static HashSet<String> getAllPersistentFlags() {
        return PERSISTENT_FLAGS;
    }

    public static boolean isEnabled(Context context, String str) {
        String str2 = SystemProperties.get("persist.sys.fflag.override." + str);
        return !TextUtils.isEmpty(str2) ? Boolean.parseBoolean(str2) : FeatureFlagUtils.isEnabled(context, str);
    }

    public static boolean isPersistent(String str) {
        return PERSISTENT_FLAGS.contains(str);
    }

    public static void setEnabled(Context context, String str, boolean z) {
        SystemProperties.set("persist.sys.fflag.override." + str, z ? "true" : "false");
        FeatureFlagUtils.setEnabled(context, str, z);
    }
}
