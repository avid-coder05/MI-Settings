package com.android.settings.stat.commonswitch;

import android.content.Context;
import com.android.settings.MiuiSoundSettings;
import com.android.settings.stat.commonswitch.SwitchStat;

/* loaded from: classes2.dex */
public class HapticSwitch extends SwitchStat {
    @Override // com.android.settings.stat.commonswitch.SwitchStat
    SwitchStat.Info getInfo(Context context) {
        return new SwitchStat.Info("haptic_switch", MiuiSoundSettings.isSystemHapticEnable(context));
    }
}
