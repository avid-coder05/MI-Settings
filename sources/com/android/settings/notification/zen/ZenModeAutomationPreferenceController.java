package com.android.settings.notification.zen;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.zen.ZenModeSettings;
import com.android.settingslib.core.AbstractPreferenceController;

/* loaded from: classes2.dex */
public class ZenModeAutomationPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final ZenModeSettings.SummaryBuilder mSummaryBuilder;

    public ZenModeAutomationPreferenceController(Context context) {
        super(context);
        this.mSummaryBuilder = new ZenModeSettings.SummaryBuilder(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "zen_mode_automation_settings";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        preference.setSummary(this.mSummaryBuilder.getAutomaticRulesSummary());
    }
}
