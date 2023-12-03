package com.android.settings.display;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.miuisettings.preference.ValuePreference;

/* loaded from: classes.dex */
public class ScreenEnhanceEngineController extends AbstractPreferenceController {
    private Context context;

    public ScreenEnhanceEngineController(Context context) {
        super(context);
        this.context = null;
        this.context = context;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "screen_enhance_engine";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return ScreenEnhanceEngineStatusCheck.isSrForVideoSupport() || ScreenEnhanceEngineStatusCheck.isSrForImageSupport() || ScreenEnhanceEngineStatusCheck.isAiSupport(this.context) || ScreenEnhanceEngineStatusCheck.isS2hSupport() || ScreenEnhanceEngineStatusCheck.isMemcSupport();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (isAvailable()) {
            ((ValuePreference) preference).setShowRightArrow(true);
        }
    }
}
