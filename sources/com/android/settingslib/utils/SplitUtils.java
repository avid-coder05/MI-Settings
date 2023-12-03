package com.android.settingslib.utils;

import android.content.ComponentName;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes2.dex */
public class SplitUtils {
    private static final Set<String> UNSPLIT_PACKAGES;
    private static HashSet<ComponentName> mUnSplitSet = new HashSet<>();

    static {
        HashSet hashSet = new HashSet();
        UNSPLIT_PACKAGES = hashSet;
        mUnSplitSet.add(new ComponentName("com.google.android.gms", "com.google.android.gms.app.settings.GoogleSettingsLink"));
        mUnSplitSet.add(new ComponentName("com.google.android.gms", "com.google.android.gms.app.settings.GoogleSettingsIALink"));
        mUnSplitSet.add(new ComponentName("com.nttdocomo.android.docomoset", "com.nttdocomo.android.docomoset.DocomoServiceSetting"));
        mUnSplitSet.add(new ComponentName("com.nttdocomo.android.osv", "com.nttdocomo.android.osv.StartupActivity"));
        hashSet.add("com.huawei.hwid");
        hashSet.add("com.amazon.alexa.multimodal.lyra");
    }

    public static boolean isSplitAllowed() {
        return true;
    }
}
