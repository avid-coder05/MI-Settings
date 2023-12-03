package com.android.settings.stat.commonswitch;

import android.content.Context;
import android.view.accessibility.AccessibilityManager;
import com.android.settings.stat.commonswitch.SwitchStat;

/* loaded from: classes2.dex */
public class TalkbackSwitch extends SwitchStat {
    public static boolean isTalkbackEnable(Context context) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService("accessibility");
        return accessibilityManager != null && accessibilityManager.isEnabled() && accessibilityManager.isTouchExplorationEnabled();
    }

    @Override // com.android.settings.stat.commonswitch.SwitchStat
    SwitchStat.Info getInfo(Context context) {
        return new SwitchStat.Info("talkback_is_on", isTalkbackEnable(context));
    }
}
