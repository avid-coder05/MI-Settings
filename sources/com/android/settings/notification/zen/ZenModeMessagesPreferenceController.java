package com.android.settings.notification.zen;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.notification.zen.ZenModeSettings;
import com.android.settingslib.core.lifecycle.Lifecycle;

/* loaded from: classes2.dex */
public class ZenModeMessagesPreferenceController extends AbstractZenModePreferenceController {
    private final String KEY;
    private final ZenModeSettings.SummaryBuilder mSummaryBuilder;

    public ZenModeMessagesPreferenceController(Context context, Lifecycle lifecycle, String str) {
        super(context, str, lifecycle);
        this.KEY = str;
        this.mSummaryBuilder = new ZenModeSettings.SummaryBuilder(context);
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.KEY;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        int zenMode = getZenMode();
        if (zenMode == 2 || zenMode == 3) {
            preference.setEnabled(false);
            preference.setSummary(this.mBackend.getAlarmsTotalSilencePeopleSummary(4));
            return;
        }
        preference.setEnabled(true);
        preference.setSummary(this.mSummaryBuilder.getMessagesSettingSummary(getPolicy()));
    }
}
