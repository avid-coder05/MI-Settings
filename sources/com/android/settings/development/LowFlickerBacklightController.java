package com.android.settings.development;

import android.content.Context;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import miui.util.FeatureParser;

/* loaded from: classes.dex */
public class LowFlickerBacklightController extends AbstractPreferenceController {
    public LowFlickerBacklightController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "low_flicker_backlight";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return FeatureParser.getBoolean("hide_flicker_backlight", false);
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
