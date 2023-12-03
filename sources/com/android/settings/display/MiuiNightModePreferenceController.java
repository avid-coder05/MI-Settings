package com.android.settings.display;

import android.content.Context;
import android.os.SystemProperties;
import androidx.preference.Preference;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.miuisettings.preference.ValuePreference;

/* loaded from: classes.dex */
public class MiuiNightModePreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private static final boolean IS_OLED;

    static {
        IS_OLED = "oled".equals(SystemProperties.get("ro.vendor.display.type", "lcd")) || "oled".equals(SystemProperties.get("ro.display.type", "lcd"));
    }

    public MiuiNightModePreferenceController(Context context) {
        super(context);
    }

    private void updateSummary(Preference preference) {
        ValuePreference valuePreference = (ValuePreference) preference;
        valuePreference.setShowRightArrow(true);
        valuePreference.setValue(DarkModeTimeModeUtil.isDarkModeEnable(this.mContext) ? R.string.screen_paper_mode_turn_on : R.string.screen_paper_mode_turn_off);
        preference.setSummary(IS_OLED ? this.mContext.getString(R.string.dark_color_summary_oled) : this.mContext.getString(R.string.dark_color_summary_lcd));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "night_mode";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        MiuiUtils.notifyNightModeShowStateChange(this.mContext);
        return MiuiUtils.isSupportNightMode(this.mContext.getApplicationContext());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updateSummary(preference);
    }
}
