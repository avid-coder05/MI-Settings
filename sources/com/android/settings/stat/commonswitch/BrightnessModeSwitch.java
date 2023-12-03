package com.android.settings.stat.commonswitch;

import android.content.Context;
import android.os.SystemProperties;
import android.provider.Settings;
import com.android.settings.stat.commonswitch.SwitchStat;
import java.util.ArrayList;
import java.util.List;
import miui.util.FeatureParser;

/* loaded from: classes2.dex */
public class BrightnessModeSwitch extends SwitchStat {
    private boolean mSunlightModeAvailable;

    private boolean isAutomaticBrightnessModeEnable(Context context) {
        return Settings.System.getIntForUser(context.getContentResolver(), "screen_brightness_mode", 0, -2) != 0;
    }

    private boolean isSunlightModeSettingsEnable(Context context) {
        return Settings.System.getIntForUser(context.getContentResolver(), "sunlight_mode", 0, -2) != 0;
    }

    private boolean smoothAdjustLightAvailable() {
        return FeatureParser.getBoolean("support_backlight_bit_switch", false);
    }

    private boolean sunlightModeAvailiable(Context context) {
        boolean z = FeatureParser.getBoolean("config_sunlight_mode_available", true);
        this.mSunlightModeAvailable = z;
        return z;
    }

    @Override // com.android.settings.stat.commonswitch.SwitchStat
    List<SwitchStat.Info> getInfoList(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new SwitchStat.Info("screen_brightness_mode", isAutomaticBrightnessModeEnable(context)));
        if (sunlightModeAvailiable(context)) {
            arrayList.add(new SwitchStat.Info("sunlight_mode", isSunlightModeSettingsEnable(context) && isAutomaticBrightnessModeEnable(context)));
        }
        if (smoothAdjustLightAvailable()) {
            arrayList.add(new SwitchStat.Info("smooth_adjust_light_mode", SystemProperties.getBoolean("persist.vendor.light.bit.switch", false)));
        }
        return arrayList;
    }
}
