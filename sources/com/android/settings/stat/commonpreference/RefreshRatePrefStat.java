package com.android.settings.stat.commonpreference;

import android.content.Context;
import android.provider.Settings;
import com.android.settings.MiuiUtils;
import com.android.settings.stat.commonpreference.PreferenceStat;
import java.util.ArrayList;
import java.util.List;
import miui.util.FeatureParser;
import miuix.core.util.SystemProperties;

/* loaded from: classes2.dex */
public class RefreshRatePrefStat extends PreferenceStat {
    private static final boolean FPS_SWITCH_DEFAULT = SystemProperties.getBoolean("ro.vendor.fps.switch.default", false);
    private static final int SCREEN_DEFAULT_FPS = getDefaultFps();
    private static final boolean IS_SUPPORT_REFRESH_RATE = MiuiUtils.isSupportScreenFps();

    private static int getDefaultFps() {
        return FeatureParser.getInteger("defaultFps", 0);
    }

    private static int getScreenDpiMode(Context context) {
        return FPS_SWITCH_DEFAULT ? Settings.System.getInt(context.getContentResolver(), "peak_refresh_rate", SCREEN_DEFAULT_FPS) : SystemProperties.getInt("persist.vendor.dfps.level", SCREEN_DEFAULT_FPS);
    }

    @Override // com.android.settings.stat.commonpreference.PreferenceStat
    public List<PreferenceStat.Info> getInfoList(Context context) {
        ArrayList arrayList = new ArrayList();
        if (IS_SUPPORT_REFRESH_RATE) {
            arrayList.add(new PreferenceStat.Info("refresh_rate", Integer.valueOf(getScreenDpiMode(context))));
        }
        return arrayList;
    }
}
