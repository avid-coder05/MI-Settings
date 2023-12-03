package com.android.settings.stat.commonswitch;

import android.content.Context;
import android.provider.Settings;
import com.android.settings.stat.commonswitch.SwitchStat;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class HapticSeekBarLevel extends SwitchStat {
    private float getHapticLevel(Context context) {
        return Settings.System.getFloat(context.getContentResolver(), "haptic_feedback_infinite_intensity", 1.0f);
    }

    @Override // com.android.settings.stat.commonswitch.SwitchStat
    List<SwitchStat.Info> getInfoList(Context context) {
        float hapticLevel = getHapticLevel(context);
        ArrayList arrayList = new ArrayList();
        arrayList.add(new SwitchStat.Info("haptic_seek_bar_level_low", hapticLevel < 0.83f));
        arrayList.add(new SwitchStat.Info("haptic_seek_bar_level_mid", hapticLevel >= 0.83f && hapticLevel <= 1.17f));
        arrayList.add(new SwitchStat.Info("haptic_seek_bar_level_high", hapticLevel > 1.17f));
        return arrayList;
    }
}
