package com.android.settings.stat.commonswitch;

import android.content.Context;
import android.provider.MiuiSettings;
import android.provider.Settings;
import com.android.settings.stat.commonswitch.SwitchStat;

/* loaded from: classes2.dex */
public class PaperModeSwitch extends SwitchStat {
    public static boolean isPaperModeOn(Context context) {
        return MiuiSettings.System.getBoolean(context.getContentResolver(), "screen_paper_mode_enabled", false) && 1 == Settings.System.getInt(context.getContentResolver(), "screen_mode_type", 0);
    }

    @Override // com.android.settings.stat.commonswitch.SwitchStat
    SwitchStat.Info getInfo(Context context) {
        return new SwitchStat.Info("paper_mode_status", isPaperModeOn(context));
    }
}
