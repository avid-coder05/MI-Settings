package com.android.settings.display;

import android.content.Context;
import android.os.SystemProperties;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import miui.hardware.display.DisplayFeatureManager;
import miui.util.FeatureParser;

/* loaded from: classes.dex */
public class ScreenFpsPreferenceController extends AbstractPreferenceController {
    private static final boolean FPS_SWITCH_DEFAULT;
    private static final int SCREEN_DEFAULT_FPS;
    private DisplayFeatureManager mDisplayFeatureManager;

    static {
        SCREEN_DEFAULT_FPS = FeatureParser.getInteger(FeatureParser.getBoolean("support_smart_fps", false) ? "smart_fps_value" : "defaultFps", 0);
        FPS_SWITCH_DEFAULT = SystemProperties.getBoolean("ro.vendor.fps.switch.default", false);
    }

    public ScreenFpsPreferenceController(Context context) {
        super(context);
        this.mDisplayFeatureManager = DisplayFeatureManager.getInstance();
    }

    private String getRightValue(int i) {
        return (!isAvailable() || i == -1) ? this.mContext.getString(R.string.screen_fps_unit, Integer.valueOf(SCREEN_DEFAULT_FPS)) : (FeatureParser.getBoolean("support_smart_fps", false) && Settings.System.getInt(this.mContext.getContentResolver(), "is_smart_fps", 1) == 1) ? this.mContext.getString(R.string.nature_color) : this.mContext.getString(R.string.screen_fps_unit, Integer.valueOf(i));
    }

    private int getScreenDpiMode() {
        return FPS_SWITCH_DEFAULT ? Settings.System.getInt(this.mContext.getContentResolver(), "user_refresh_rate", SCREEN_DEFAULT_FPS) : SystemProperties.getInt("persist.vendor.dfps.level", SCREEN_DEFAULT_FPS);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "screen_fps";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return MiuiUtils.isSupportScreenFps();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (isAvailable()) {
            ValuePreference valuePreference = (ValuePreference) preference;
            valuePreference.setValue(getRightValue(getScreenDpiMode()));
            valuePreference.setShowRightArrow(true);
        }
    }
}
