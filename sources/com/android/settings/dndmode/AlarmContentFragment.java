package com.android.settings.dndmode;

import android.app.ExtraNotificationManager;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.service.notification.ZenModeConfig;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import miui.os.Build;

/* loaded from: classes.dex */
public class AlarmContentFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String TAG = AlarmContentFragment.class.getSimpleName();
    private CheckBoxPreference mCalls;
    private ZenModeConfig mConfig;
    private Context mContext;
    private CheckBoxPreference mEvents;
    private CheckBoxPreference mMessages;
    private final Handler mHandler = new Handler();
    private final SettingsObserver mSettingsObserver = new SettingsObserver();

    /* loaded from: classes.dex */
    private final class SettingsObserver extends ContentObserver {
        private final Uri ZEN_MODE_CONFIG_ETAG_URI;

        public SettingsObserver() {
            super(AlarmContentFragment.this.mHandler);
            this.ZEN_MODE_CONFIG_ETAG_URI = Settings.Global.getUriFor("zen_mode_config_etag");
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            AlarmContentFragment.this.updateControls();
        }

        public void register() {
            AlarmContentFragment.this.getContentResolver().registerContentObserver(this.ZEN_MODE_CONFIG_ETAG_URI, false, this, -1);
        }

        public void unregister() {
            AlarmContentFragment.this.getContentResolver().unregisterContentObserver(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateControls() {
        this.mConfig = ExtraNotificationManager.getZenModeConfig(this.mContext);
    }

    private void updateUI() {
        this.mCalls.setChecked(this.mConfig.allowCalls);
        this.mMessages.setChecked(this.mConfig.allowMessages);
        this.mEvents.setChecked(this.mConfig.allowEvents);
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return AlarmContentFragment.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getContext();
        boolean booleanExtra = getActivity().getIntent().getBooleanExtra("isCts", false);
        this.mSettingsObserver.register();
        addPreferencesFromResource(R.xml.dnd_alarm_content);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("events");
        this.mEvents = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(this);
        CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) findPreference("phone_calls");
        this.mCalls = checkBoxPreference2;
        checkBoxPreference2.setOnPreferenceChangeListener(this);
        CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) findPreference("messages");
        this.mMessages = checkBoxPreference3;
        checkBoxPreference3.setOnPreferenceChangeListener(this);
        if (Build.IS_TABLET && !booleanExtra) {
            preferenceScreen.removePreference(this.mCalls);
            preferenceScreen.removePreference(this.mMessages);
        }
        updateControls();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mSettingsObserver.unregister();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mCalls) {
            ZenModeConfig copy = this.mConfig.copy();
            copy.allowCalls = ((Boolean) obj).booleanValue();
            ExtraNotificationManager.setZenModeConfig(this.mContext, copy);
            return true;
        } else if (preference == this.mMessages) {
            ZenModeConfig copy2 = this.mConfig.copy();
            copy2.allowMessages = ((Boolean) obj).booleanValue();
            ExtraNotificationManager.setZenModeConfig(this.mContext, copy2);
            return true;
        } else if (preference == this.mEvents) {
            ZenModeConfig copy3 = this.mConfig.copy();
            copy3.allowEvents = ((Boolean) obj).booleanValue();
            ExtraNotificationManager.setZenModeConfig(this.mContext, copy3);
            return true;
        } else {
            return true;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateUI();
    }
}
