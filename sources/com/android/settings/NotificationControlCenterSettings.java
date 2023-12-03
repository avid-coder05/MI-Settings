package com.android.settings;

import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.utils.StatusBarUtils;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import miui.content.res.ThemeResources;

/* loaded from: classes.dex */
public class NotificationControlCenterSettings extends BaseSettingsPreferenceFragment {
    private ValuePreference mNotificationAnimation;
    private ContentObserver mNotificationAnimationStyleObserver;

    private int getDefaultKeyguardNotificationAnimationStyle() {
        return Settings.Global.getInt(getContext().getContentResolver(), "new_device_after_support_notification_animation", 0) != 0 ? 2 : 1;
    }

    private String getNotificationAnimationStyle() {
        int i = Settings.System.getInt(getContext().getContentResolver(), "wakeup_for_keyguard_notification", getDefaultKeyguardNotificationAnimationStyle());
        String[] stringArray = getContext().getResources().getStringArray(R.array.aod_notification_status_entries);
        return (i < 0 || i > 2) ? stringArray[0] : stringArray[i];
    }

    private boolean isAODAvailable() {
        int identifier = getContext().getResources().getIdentifier("config_dozeAlwaysOnDisplayAvailable", "bool", ThemeResources.FRAMEWORK_PACKAGE);
        if (identifier > 0) {
            return getContext().getResources().getBoolean(identifier);
        }
        return false;
    }

    private void registerNotificationAnimationObserver() {
        if (isAODAvailable()) {
            getContext().getContentResolver().registerContentObserver(Settings.System.getUriFor("wakeup_for_keyguard_notification"), false, this.mNotificationAnimationStyleObserver);
            this.mNotificationAnimationStyleObserver.onChange(false);
        }
    }

    private void setupControlCenter() {
        if (StatusBarUtils.isForceUseControlPanel(getContext())) {
            ((PreferenceCategory) findPreference("control_center")).removePreference(findPreference("control_center_style"));
        }
        if (!StatusBarUtils.isForceUseControlPanel(getContext()) || com.android.settings.utils.Utils.isPad() || com.android.settings.utils.Utils.isFold()) {
            ((PreferenceCategory) findPreference("control_center")).removePreference(findPreference("style_delete_guide"));
        }
    }

    private void setupNotificationAnimation() {
        PreferenceScreen preferenceScreen = (PreferenceScreen) findPreference("notification_control_center_settings");
        this.mNotificationAnimation = (ValuePreference) findPreference("notification_light_effect");
        if (isAODAvailable()) {
            return;
        }
        preferenceScreen.removePreferenceRecursively("notification_light_effect");
    }

    private void unregisterNotificationAnimationObserver() {
        if (this.mNotificationAnimationStyleObserver != null) {
            getContext().getContentResolver().unregisterContentObserver(this.mNotificationAnimationStyleObserver);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNotificationAnimationPref() {
        this.mNotificationAnimation.setValue(getNotificationAnimationStyle());
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return getClass().getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.notification_control_center_settings);
        getPreferenceScreen().setTitle(R.string.notification_control_center);
        setupControlCenter();
        setupNotificationAnimation();
        this.mNotificationAnimationStyleObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.NotificationControlCenterSettings.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                NotificationControlCenterSettings.this.updateNotificationAnimationPref();
            }
        };
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        unregisterNotificationAnimationObserver();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        registerNotificationAnimationObserver();
    }
}
