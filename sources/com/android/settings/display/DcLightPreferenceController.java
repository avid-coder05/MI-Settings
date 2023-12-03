package com.android.settings.display;

import android.content.Context;
import android.os.SystemProperties;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import miui.util.FeatureParser;

/* loaded from: classes.dex */
public class DcLightPreferenceController extends AbstractPreferenceController {
    public DcLightPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "dc_light";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if (FeatureParser.getBoolean("hide_flicker_backlight", false)) {
            return false;
        }
        if (!FeatureParser.getBoolean("support_secret_dc_backlight", false) || MiuiUtils.isSecondSpace(this.mContext)) {
            return (FeatureParser.getBoolean("support_dc_backlight", false) || FeatureParser.getBoolean("support_low_flicker_backlight", false)) && !MiuiUtils.isSecondSpace(this.mContext);
        }
        boolean z = SystemProperties.getBoolean("debug.secret_dc_backlight.enable", false);
        if (!z) {
            Settings.System.putInt(this.mContext.getContentResolver(), "dc_back_light", 0);
        }
        return z;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof ValuePreference) {
            ValuePreference valuePreference = (ValuePreference) preference;
            valuePreference.setTitle(FeatureParser.getBoolean("support_dc_backlight", false) ? R.string.dc_light_title : R.string.low_dc_light_title);
            valuePreference.setShowRightArrow(true);
            valuePreference.setValue(Settings.System.getInt(this.mContext.getContentResolver(), "dc_back_light", 0) == 1 ? R.string.screen_paper_mode_turn_on : R.string.screen_paper_mode_turn_off);
        }
    }
}
