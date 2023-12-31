package com.android.settings.notification.app;

import android.app.NotificationChannel;
import android.content.Context;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.NotificationBackend;

/* loaded from: classes2.dex */
public class ConversationDemotePreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin {
    SettingsPreferenceFragment mHostFragment;

    public ConversationDemotePreferenceController(Context context, SettingsPreferenceFragment settingsPreferenceFragment, NotificationBackend notificationBackend) {
        super(context, notificationBackend);
        this.mHostFragment = settingsPreferenceFragment;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "demote";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if ("demote".equals(preference.getKey())) {
            this.mChannel.setDemoted(true);
            saveChannel();
            this.mHostFragment.getActivity().finish();
            return true;
        }
        return false;
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        NotificationChannel notificationChannel;
        return (!super.isAvailable() || this.mAppRow == null || (notificationChannel = this.mChannel) == null || TextUtils.isEmpty(notificationChannel.getConversationId()) || this.mChannel.isDemoted()) ? false : true;
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController
    boolean isIncludedInFilter() {
        return this.mPreferenceFilter.contains("conversation");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        preference.setEnabled(this.mAdmin == null);
    }
}
