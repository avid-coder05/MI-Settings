package com.android.settings.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.service.notification.ZenModeConfig;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import com.android.settingslib.util.MiStatInterfaceUtils;
import miui.os.Build;
import miui.provider.ExtraTelephony;

/* loaded from: classes2.dex */
public class MiuiZenModeSettingsFragment extends SettingsPreferenceFragment {
    private Context mContext;
    private PreferenceCategory mExceptionalCaseCategory;
    private Handler mHandler;
    private NotificationManager mNotificationManager;
    private CheckBoxPreference mRepeatedIncallPref;
    private DropDownPreference mVipListPref;
    private final SettingsObserver mSettingsObserver = new SettingsObserver();
    private Preference.OnPreferenceChangeListener mOnPrefChangeListener = new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.MiuiZenModeSettingsFragment.1
        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            int i;
            int i2;
            int i3;
            if (preference == MiuiZenModeSettingsFragment.this.mVipListPref) {
                int parseInt = Integer.parseInt((String) obj);
                boolean z = parseInt <= 2;
                NotificationManager.Policy notificationPolicy = MiuiZenModeSettingsFragment.this.mNotificationManager.getNotificationPolicy();
                int i4 = notificationPolicy.priorityCategories;
                int i5 = notificationPolicy.priorityCallSenders;
                int i6 = notificationPolicy.priorityMessageSenders;
                if (z) {
                    i2 = parseInt;
                    i3 = i2;
                    i = i4 | 8 | 4;
                } else {
                    i = i4 & (-9) & (-5);
                    i2 = i5;
                    i3 = i6;
                }
                MiuiZenModeSettingsFragment.this.mNotificationManager.setNotificationPolicy(new NotificationManager.Policy(i, i2, i3, notificationPolicy.suppressedVisualEffects, notificationPolicy.state));
            } else if (preference == MiuiZenModeSettingsFragment.this.mRepeatedIncallPref) {
                NotificationManager.Policy notificationPolicy2 = MiuiZenModeSettingsFragment.this.mNotificationManager.getNotificationPolicy();
                int i7 = notificationPolicy2.priorityCategories;
                MiuiZenModeSettingsFragment.this.mNotificationManager.setNotificationPolicy(new NotificationManager.Policy(((Boolean) obj).booleanValue() ? i7 | 16 : i7 & (-17), notificationPolicy2.priorityCallSenders, notificationPolicy2.priorityMessageSenders, notificationPolicy2.suppressedVisualEffects, notificationPolicy2.state));
            }
            return true;
        }
    };

    /* loaded from: classes2.dex */
    private final class SettingsObserver extends ContentObserver {
        private final Uri ZEN_MODE_CONFIG_ETAG_URI;
        private final Uri ZEN_MODE_URI;

        public SettingsObserver() {
            super(MiuiZenModeSettingsFragment.this.mHandler);
            this.ZEN_MODE_URI = Settings.Global.getUriFor(ExtraTelephony.ZEN_MODE);
            this.ZEN_MODE_CONFIG_ETAG_URI = Settings.Global.getUriFor("zen_mode_config_etag");
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            MiuiZenModeSettingsFragment.this.mHandler.sendEmptyMessage(1);
        }

        public void register() {
            MiuiZenModeSettingsFragment.this.getContentResolver().registerContentObserver(this.ZEN_MODE_URI, false, this, -1);
            MiuiZenModeSettingsFragment.this.getContentResolver().registerContentObserver(this.ZEN_MODE_CONFIG_ETAG_URI, false, this, -1);
        }

        public void unregister() {
            MiuiZenModeSettingsFragment.this.getContentResolver().unregisterContentObserver(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshUI() {
        ZenModeConfig zenModeConfig = this.mNotificationManager.getZenModeConfig();
        boolean z = Build.IS_TABLET;
        if (!z) {
            NotificationManager.Policy notificationPolicy = this.mNotificationManager.getNotificationPolicy();
            Log.d("MiuiZenModeSettingsFragment", "refreshUI(), current policy : " + notificationPolicy);
            this.mRepeatedIncallPref.setChecked((notificationPolicy.priorityCategories & 16) != 0);
        }
        if (z) {
            return;
        }
        this.mVipListPref.setValue(String.valueOf(zenModeConfig.allowCalls ? zenModeConfig.allowCallsFrom : getResources().getStringArray(R.array.vip_mode_text).length - 1));
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.dnd_mode_settings);
        FragmentActivity activity = getActivity();
        this.mContext = activity;
        this.mNotificationManager = NotificationManager.from(activity);
        this.mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.settings.notification.MiuiZenModeSettingsFragment.2
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what == 1) {
                    MiuiZenModeSettingsFragment.this.refreshUI();
                }
            }
        };
        this.mSettingsObserver.register();
        this.mExceptionalCaseCategory = (PreferenceCategory) findPreference("exceptional_case_category");
        DropDownPreference dropDownPreference = (DropDownPreference) findPreference("vip_list_setting");
        this.mVipListPref = dropDownPreference;
        boolean z = Build.IS_TABLET;
        if (z) {
            this.mExceptionalCaseCategory.removePreference(dropDownPreference);
        } else {
            dropDownPreference.setOnPreferenceChangeListener(this.mOnPrefChangeListener);
        }
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("repeated_incall_notification");
        this.mRepeatedIncallPref = checkBoxPreference;
        if (z) {
            this.mExceptionalCaseCategory.removePreference(checkBoxPreference);
        } else {
            checkBoxPreference.setOnPreferenceChangeListener(this.mOnPrefChangeListener);
            this.mRepeatedIncallPref.setChecked(MiuiSettings.AntiSpam.isRepeatedCallActionEnable(this.mContext));
        }
        refreshUI();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mHandler.removeMessages(1);
        this.mSettingsObserver.unregister();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        if (Build.IS_INTERNATIONAL_BUILD) {
            return;
        }
        ZenModeConfig zenModeConfig = this.mNotificationManager.getZenModeConfig();
        MiStatInterfaceUtils.trackPreferenceClick("MiuiZenModeSettingsFragment", "vip_list_setting_" + String.valueOf(zenModeConfig.allowCalls ? zenModeConfig.allowCallsFrom : getResources().getStringArray(R.array.vip_mode_text).length - 1));
        MiStatInterfaceUtils.trackSwitchEvent("repeated_incall_notification", this.mRepeatedIncallPref.isChecked());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        refreshUI();
    }
}
