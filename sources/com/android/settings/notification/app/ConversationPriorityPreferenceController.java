package com.android.settings.notification.app;

import android.app.NotificationChannel;
import android.content.Context;
import android.util.Pair;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.notification.NotificationBackend;
import com.android.settings.notification.app.NotificationSettings;

/* loaded from: classes2.dex */
public class ConversationPriorityPreferenceController extends NotificationPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    private final NotificationSettings.DependentFieldListener mDependentFieldListener;

    public ConversationPriorityPreferenceController(Context context, NotificationBackend notificationBackend, NotificationSettings.DependentFieldListener dependentFieldListener) {
        super(context, notificationBackend);
        this.mDependentFieldListener = dependentFieldListener;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "priority";
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return (!super.isAvailable() || this.mAppRow == null || this.mChannel == null) ? false : true;
    }

    @Override // com.android.settings.notification.app.NotificationPreferenceController
    boolean isIncludedInFilter() {
        return this.mPreferenceFilter.contains("importance") || this.mPreferenceFilter.contains("conversation");
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        NotificationChannel notificationChannel = this.mChannel;
        if (notificationChannel == null) {
            return false;
        }
        boolean isImportantConversation = notificationChannel.isImportantConversation();
        Pair pair = (Pair) obj;
        this.mChannel.setImportance(((Integer) pair.first).intValue());
        this.mChannel.setImportantConversation(((Boolean) pair.second).booleanValue());
        if (((Boolean) pair.second).booleanValue()) {
            this.mChannel.setAllowBubbles(true);
        } else if (isImportantConversation) {
            this.mChannel.setAllowBubbles(false);
        }
        this.mDependentFieldListener.onFieldValueChanged();
        saveChannel();
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (this.mAppRow != null) {
            preference.setEnabled(this.mAdmin == null && !this.mChannel.isImportanceLockedByOEM());
            ConversationPriorityPreference conversationPriorityPreference = (ConversationPriorityPreference) preference;
            conversationPriorityPreference.setConfigurable(!this.mChannel.isImportanceLockedByOEM());
            conversationPriorityPreference.setImportance(this.mChannel.getImportance());
            conversationPriorityPreference.setOriginalImportance(this.mChannel.getOriginalImportance());
            conversationPriorityPreference.setPriorityConversation(this.mChannel.isImportantConversation());
        }
    }
}
