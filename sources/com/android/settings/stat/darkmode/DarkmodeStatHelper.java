package com.android.settings.stat.darkmode;

import android.content.Context;
import com.android.settings.display.DarkModeTimeModeUtil;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import java.util.HashMap;

/* loaded from: classes2.dex */
public class DarkmodeStatHelper {
    public static void traceDarkModeEvent(Context context) {
        boolean isDarkModeEnable = DarkModeTimeModeUtil.isDarkModeEnable(context);
        boolean isDarkModeTimeEnable = DarkModeTimeModeUtil.isDarkModeTimeEnable(context);
        HashMap hashMap = new HashMap();
        hashMap.put("version", String.valueOf(1));
        hashMap.put("status", Boolean.valueOf(isDarkModeEnable || isDarkModeTimeEnable));
        OneTrackInterfaceUtils.track("dark_mode_status", hashMap);
        HashMap hashMap2 = new HashMap();
        hashMap.put("version", String.valueOf(1));
        hashMap.put("status", Boolean.valueOf(isDarkModeEnable));
        OneTrackInterfaceUtils.track("dark_mode_switch", hashMap2);
        MiStatInterfaceUtils.trackSwitchEvent("darkModeScheduleStatus", isDarkModeTimeEnable);
        MiStatInterfaceUtils.trackSwitchEvent("forceDarkStatus", DarkModeTimeModeUtil.isSmartDarkEnable());
        OneTrackInterfaceUtils.trackSwitchEvent("darkModeScheduleStatus", isDarkModeTimeEnable);
        OneTrackInterfaceUtils.trackSwitchEvent("forceDarkStatus", DarkModeTimeModeUtil.isSmartDarkEnable());
    }
}
