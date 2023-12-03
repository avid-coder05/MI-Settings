package com.android.settings.notification.app;

import android.content.Context;
import android.content.Intent;
import android.media.ExtraRingtoneManager;
import android.net.Uri;
import android.provider.MiuiSettings;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.RingtonePreference;
import com.android.settings.notification.BaseNotificationSettings;
import com.android.settings.notification.MiuiNotificationSoundPreference;
import com.android.settings.notification.NotificationSettingsHelper;
import com.android.settings.utils.StatusBarUtils;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import miui.app.constants.ThemeManagerConstants;
import miui.cloud.sync.providers.CalendarSyncInfoProvider;

/* loaded from: classes2.dex */
public class ChannelNotificationSettings extends BaseNotificationSettings {
    private MiuiNotificationSoundPreference mRingtone;

    private boolean checkCanBeVisible(int i) {
        return checkCanBeVisible(this.mChannel.getImportance(), i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Uri handleSystemRingtone(Context context, String str, Uri uri) {
        if (TextUtils.equals(str, Telephony.Sms.getDefaultSmsPackage(context))) {
            ExtraRingtoneManager.saveDefaultSound(context, 16, uri);
            return MiuiSettings.System.DEFAULT_SMS_RECEIVED_RINGTONE_URI;
        } else if (TextUtils.equals(CalendarSyncInfoProvider.AUTHORITY, str) || TextUtils.equals("com.xiaomi.calendar", str)) {
            ExtraRingtoneManager.saveDefaultSound(context, 4096, uri);
            return MiuiSettings.System.DEFAULT_CALENDAR_ALERT_URI;
        } else {
            return uri;
        }
    }

    private void removeDefaultPrefs() {
        setPrefVisible(findPreference("badge"), false);
        setPrefVisible(findPreference("allow_keyguard"), false);
        setPrefVisible(findPreference("importance"), false);
        setPrefVisible(findPreference("allow_float"), canFloat());
        setPrefVisible(findPreference(ThemeManagerConstants.COMPONENT_CODE_RINGTONE), canSound());
        setPrefVisible(findPreference("vibrate"), canVibrate());
        setPrefVisible(findPreference("lights"), canLights());
        setPrefVisible(findPreference("visibility_override"), canShowKeyguard());
    }

    private void setupBlock() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("block");
        this.mBlock = checkBoxPreference;
        if (this.mShowLegacyConfig) {
            removePreference("block_category");
            return;
        }
        checkBoxPreference.setChecked(!isChannelBlocked(this.mChannel));
        this.mBlock.setEnabled(isChannelBlockable(this.mChannel) && isChannelConfigurable(this.mChannel));
        this.mBlock.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.app.ChannelNotificationSettings.1
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                boolean booleanValue = ((Boolean) obj).booleanValue();
                ((BaseNotificationSettings) ChannelNotificationSettings.this).mChannel.setImportance(booleanValue ? ((BaseNotificationSettings) ChannelNotificationSettings.this).mBackupImportance : 0);
                ((BaseNotificationSettings) ChannelNotificationSettings.this).mChannel.lockFields(4);
                ((BaseNotificationSettings) ChannelNotificationSettings.this).mBackend.updateChannel(((BaseNotificationSettings) ChannelNotificationSettings.this).mPkg, ((BaseNotificationSettings) ChannelNotificationSettings.this).mUid, ((BaseNotificationSettings) ChannelNotificationSettings.this).mChannel);
                ChannelNotificationSettings channelNotificationSettings = ChannelNotificationSettings.this;
                channelNotificationSettings.updateDependents(channelNotificationSettings.isChannelBlocked(((BaseNotificationSettings) channelNotificationSettings).mChannel));
                if (!booleanValue) {
                    ChannelNotificationSettings.this.refreshNotificationShade(true);
                }
                return true;
            }
        });
    }

    private void setupChannelDefaultPrefs() {
        setupBlock();
        setupFloat();
        setupRingtone();
        setupVibrate();
        setupLights();
        setupVisOverridePref(canShowKeyguard(this.mChannel.getId()) ? this.mChannel.getLockscreenVisibility() : -1);
    }

    private void setupFloat() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("allow_float");
        this.mAllowFloat = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.app.ChannelNotificationSettings.4
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                boolean booleanValue = ((Boolean) obj).booleanValue();
                NotificationSettingsHelper.setFloat(ChannelNotificationSettings.this.getContext(), ((BaseNotificationSettings) ChannelNotificationSettings.this).mTargetPkg, ((BaseNotificationSettings) ChannelNotificationSettings.this).mChannel.getId(), booleanValue);
                ChannelNotificationSettings channelNotificationSettings = ChannelNotificationSettings.this;
                if (channelNotificationSettings.isChannelBlocked(((BaseNotificationSettings) channelNotificationSettings).mChannel)) {
                    return true;
                }
                ((BaseNotificationSettings) ChannelNotificationSettings.this).mBackupImportance = booleanValue ? 4 : 3;
                ((BaseNotificationSettings) ChannelNotificationSettings.this).mChannel.setImportance(((BaseNotificationSettings) ChannelNotificationSettings.this).mBackupImportance);
                ((BaseNotificationSettings) ChannelNotificationSettings.this).mChannel.lockFields(4);
                ((BaseNotificationSettings) ChannelNotificationSettings.this).mBackend.updateChannel(((BaseNotificationSettings) ChannelNotificationSettings.this).mPkg, ((BaseNotificationSettings) ChannelNotificationSettings.this).mUid, ((BaseNotificationSettings) ChannelNotificationSettings.this).mChannel);
                return true;
            }
        });
    }

    private void setupLights() {
        if (!StatusBarUtils.IS_SUPPORT_LED) {
            setPrefVisible(findPreference("lights"), false);
            return;
        }
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("lights");
        this.mAllowLights = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.app.ChannelNotificationSettings.8
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                boolean booleanValue = ((Boolean) obj).booleanValue();
                NotificationSettingsHelper.setLights(ChannelNotificationSettings.this.getContext(), ((BaseNotificationSettings) ChannelNotificationSettings.this).mTargetPkg, ((BaseNotificationSettings) ChannelNotificationSettings.this).mChannel.getId(), booleanValue);
                ((BaseNotificationSettings) ChannelNotificationSettings.this).mChannel.enableLights(booleanValue);
                ((BaseNotificationSettings) ChannelNotificationSettings.this).mChannel.lockFields(8);
                ((BaseNotificationSettings) ChannelNotificationSettings.this).mBackend.updateChannel(((BaseNotificationSettings) ChannelNotificationSettings.this).mPkg, ((BaseNotificationSettings) ChannelNotificationSettings.this).mUid, ((BaseNotificationSettings) ChannelNotificationSettings.this).mChannel);
                return true;
            }
        });
    }

    private void setupRingtone() {
        MiuiNotificationSoundPreference miuiNotificationSoundPreference = (MiuiNotificationSoundPreference) findPreference(ThemeManagerConstants.COMPONENT_CODE_RINGTONE);
        this.mRingtone = miuiNotificationSoundPreference;
        miuiNotificationSoundPreference.setRingtone(canSound(this.mChannel.getId()) ? this.mChannel.getSound() : null);
        this.mRingtone.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.app.ChannelNotificationSettings.6
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                NotificationSettingsHelper.setSound(ChannelNotificationSettings.this.getContext(), ((BaseNotificationSettings) ChannelNotificationSettings.this).mTargetPkg, ((BaseNotificationSettings) ChannelNotificationSettings.this).mChannel.getId(), true);
                ChannelNotificationSettings channelNotificationSettings = ChannelNotificationSettings.this;
                ((BaseNotificationSettings) ChannelNotificationSettings.this).mChannel.setSound(channelNotificationSettings.handleSystemRingtone(channelNotificationSettings.getContext(), ((BaseNotificationSettings) ChannelNotificationSettings.this).mPkg, (Uri) obj), ((BaseNotificationSettings) ChannelNotificationSettings.this).mChannel.getAudioAttributes());
                ((BaseNotificationSettings) ChannelNotificationSettings.this).mChannel.lockFields(32);
                ((BaseNotificationSettings) ChannelNotificationSettings.this).mBackend.updateChannel(((BaseNotificationSettings) ChannelNotificationSettings.this).mPkg, ((BaseNotificationSettings) ChannelNotificationSettings.this).mUid, ((BaseNotificationSettings) ChannelNotificationSettings.this).mChannel);
                return true;
            }
        });
    }

    private void setupVibrate() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("vibrate");
        this.mAllowVibrate = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.app.ChannelNotificationSettings.7
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                boolean booleanValue = ((Boolean) obj).booleanValue();
                NotificationSettingsHelper.setVibrate(ChannelNotificationSettings.this.getContext(), ((BaseNotificationSettings) ChannelNotificationSettings.this).mTargetPkg, ((BaseNotificationSettings) ChannelNotificationSettings.this).mChannel.getId(), booleanValue);
                ((BaseNotificationSettings) ChannelNotificationSettings.this).mChannel.enableVibration(booleanValue);
                ((BaseNotificationSettings) ChannelNotificationSettings.this).mChannel.lockFields(16);
                ((BaseNotificationSettings) ChannelNotificationSettings.this).mBackend.updateChannel(((BaseNotificationSettings) ChannelNotificationSettings.this).mPkg, ((BaseNotificationSettings) ChannelNotificationSettings.this).mUid, ((BaseNotificationSettings) ChannelNotificationSettings.this).mChannel);
                return true;
            }
        });
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 265;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        log("onActivityResult requestCode=%d resultCode=%d data=%s", Integer.valueOf(i), Integer.valueOf(i2), intent);
        MiuiNotificationSoundPreference miuiNotificationSoundPreference = this.mRingtone;
        if (miuiNotificationSoundPreference == null || intent == null) {
            return;
        }
        miuiNotificationSoundPreference.onActivityResult(i, i2, intent);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference instanceof RingtonePreference) {
            MiuiNotificationSoundPreference miuiNotificationSoundPreference = this.mRingtone;
            miuiNotificationSoundPreference.onPrepareRingtonePickerIntent(miuiNotificationSoundPreference.getIntent());
            startActivityForResult(preference.getIntent(), 200);
            return true;
        }
        return false;
    }

    @Override // com.android.settings.notification.BaseNotificationSettings, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mUid < 0 || TextUtils.isEmpty(this.mPkg) || this.mPkgInfo == null || this.mChannel == null) {
            Log.w("NotifiSettings", "Missing package or uid or packageinfo or channel");
            getActivity().finish();
            return;
        }
        if (getPreferenceScreen() != null) {
            getPreferenceScreen().removeAll();
        }
        setActionBarTitle(this.mChannel.getName());
        addPreferencesFromResource(R.xml.miui_channel_notification_settings);
        getPreferenceScreen().setOrderingAsAdded(false);
        collectConfigActivities();
        int importance = this.mChannel.getImportance();
        boolean isChannelBlocked = isChannelBlocked(this.mChannel);
        boolean equals = "miscellaneous".equals(this.mChannel.getId());
        this.mShowLegacyConfig = equals;
        if (isChannelBlocked) {
            importance = equals ? -1000 : 2;
        }
        this.mBackupImportance = importance;
        setupChannelDefaultPrefs();
        removeDefaultPrefs();
        updateDependents(isChannelBlocked);
        log("onResume mShowLegacyConfig=%b mChannel=%s", Boolean.valueOf(this.mShowLegacyConfig), this.mChannel.toString());
    }

    protected void updateDependents(boolean z) {
        boolean isChannelConfigurable = isChannelConfigurable(this.mChannel);
        setEnabled(this.mImportance, !z);
        boolean z2 = false;
        setEnabled(this.mBadge, !z && isChannelConfigurable);
        setChecked(this.mBadge, !z && this.mChannel.canShowBadge());
        CheckBoxPreference checkBoxPreference = this.mAllowFloat;
        if (checkBoxPreference != null) {
            setEnabled(checkBoxPreference, !z && canFloat());
            setChecked(this.mAllowFloat, !z && canFloat(this.mChannel.getId()) && this.mBackupImportance >= 4);
        }
        CheckBoxPreference checkBoxPreference2 = this.mAllowKeyguard;
        if (checkBoxPreference2 != null) {
            setEnabled(checkBoxPreference2, !z && canShowKeyguard());
            setChecked(this.mAllowKeyguard, !z && canShowKeyguard(this.mChannel.getId()));
        }
        setEnabled(this.mRingtone, !z && isChannelConfigurable);
        setEnabled(this.mAllowVibrate, !z && isChannelConfigurable);
        setChecked(this.mAllowVibrate, !z && (this.mChannel.shouldVibrate() && checkCanBeVisible(3) && canVibrate(this.mChannel.getId())));
        setEnabled(this.mAllowLights, !z && isChannelConfigurable);
        setChecked(this.mAllowLights, !z && (this.mChannel.shouldShowLights() && canLights(this.mChannel.getId())));
        ValuePreference valuePreference = this.mVisibilityOverride;
        if (!z && isChannelConfigurable && canShowKeyguard()) {
            z2 = true;
        }
        setEnabled(valuePreference, z2);
    }
}
