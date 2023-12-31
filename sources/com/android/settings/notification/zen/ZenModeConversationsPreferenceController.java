package com.android.settings.notification.zen;

import android.content.Context;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.core.lifecycle.Lifecycle;

/* loaded from: classes2.dex */
public class ZenModeConversationsPreferenceController extends AbstractZenModePreferenceController {
    private final ZenModeBackend mBackend;
    private Preference mPreference;

    public ZenModeConversationsPreferenceController(Context context, String str, Lifecycle lifecycle) {
        super(context, str, lifecycle);
        this.mBackend = ZenModeBackend.getInstance(context);
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(this.KEY);
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
            this.mPreference.setEnabled(false);
            this.mPreference.setSummary(this.mBackend.getAlarmsTotalSilencePeopleSummary(256));
            return;
        }
        preference.setEnabled(true);
        preference.setSummary(this.mBackend.getConversationSummary());
    }
}
