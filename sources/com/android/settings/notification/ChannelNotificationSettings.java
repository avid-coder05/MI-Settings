package com.android.settings.notification;

import android.app.NotificationChannel;
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
import com.android.settings.utils.StatusBarUtils;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import miui.app.constants.ThemeManagerConstants;
import miui.cloud.sync.providers.CalendarSyncInfoProvider;

/* loaded from: classes2.dex */
public class ChannelNotificationSettings extends BaseNotificationSettings {
    private MiuiNotificationSoundPreference mRingtone;

    /* renamed from: com.android.settings.notification.ChannelNotificationSettings$2  reason: invalid class name */
    /* loaded from: classes2.dex */
    class AnonymousClass2 implements Preference.OnPreferenceChangeListener {
        final /* synthetic */ ChannelNotificationSettings this$0;

        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            this.this$0.mChannel.setShowBadge(((Boolean) obj).booleanValue());
            this.this$0.mChannel.lockFields(128);
            ChannelNotificationSettings channelNotificationSettings = this.this$0;
            channelNotificationSettings.mBackend.updateChannel(channelNotificationSettings.mPkg, channelNotificationSettings.mUid, channelNotificationSettings.mChannel);
            this.this$0.refreshNotificationShade(false);
            return true;
        }
    }

    /* renamed from: com.android.settings.notification.ChannelNotificationSettings$3  reason: invalid class name */
    /* loaded from: classes2.dex */
    class AnonymousClass3 implements Preference.OnPreferenceChangeListener {
        final /* synthetic */ ChannelNotificationSettings this$0;

        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            ChannelNotificationSettings channelNotificationSettings = this.this$0;
            if (channelNotificationSettings.isChannelBlocked(channelNotificationSettings.mChannel)) {
                return true;
            }
            this.this$0.mBackupImportance = Integer.parseInt((String) obj);
            ChannelNotificationSettings channelNotificationSettings2 = this.this$0;
            channelNotificationSettings2.mChannel.setImportance(channelNotificationSettings2.mBackupImportance);
            this.this$0.mChannel.lockFields(4);
            ChannelNotificationSettings channelNotificationSettings3 = this.this$0;
            channelNotificationSettings3.mBackend.updateChannel(channelNotificationSettings3.mPkg, channelNotificationSettings3.mUid, channelNotificationSettings3.mChannel);
            this.this$0.updateDependents(false);
            return true;
        }
    }

    /* renamed from: com.android.settings.notification.ChannelNotificationSettings$5  reason: invalid class name */
    /* loaded from: classes2.dex */
    class AnonymousClass5 implements Preference.OnPreferenceChangeListener {
        final /* synthetic */ ChannelNotificationSettings this$0;

        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            Context context = this.this$0.getContext();
            ChannelNotificationSettings channelNotificationSettings = this.this$0;
            NotificationSettingsHelper.setShowKeyguard(context, channelNotificationSettings.mTargetPkg, channelNotificationSettings.mChannel.getId(), booleanValue);
            return true;
        }
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
        this.mBlock.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.ChannelNotificationSettings.1
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                boolean booleanValue = ((Boolean) obj).booleanValue();
                ChannelNotificationSettings channelNotificationSettings = ChannelNotificationSettings.this;
                channelNotificationSettings.mChannel.setImportance(booleanValue ? channelNotificationSettings.mBackupImportance : 0);
                ChannelNotificationSettings.this.mChannel.lockFields(4);
                ChannelNotificationSettings channelNotificationSettings2 = ChannelNotificationSettings.this;
                channelNotificationSettings2.mBackend.updateChannel(channelNotificationSettings2.mPkg, channelNotificationSettings2.mUid, channelNotificationSettings2.mChannel);
                ChannelNotificationSettings channelNotificationSettings3 = ChannelNotificationSettings.this;
                channelNotificationSettings3.updateDependents(channelNotificationSettings3.isChannelBlocked(channelNotificationSettings3.mChannel));
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
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.ChannelNotificationSettings.4
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                boolean booleanValue = ((Boolean) obj).booleanValue();
                Context context = ChannelNotificationSettings.this.getContext();
                ChannelNotificationSettings channelNotificationSettings = ChannelNotificationSettings.this;
                NotificationSettingsHelper.setFloat(context, channelNotificationSettings.mTargetPkg, channelNotificationSettings.mChannel.getId(), booleanValue);
                ChannelNotificationSettings channelNotificationSettings2 = ChannelNotificationSettings.this;
                if (channelNotificationSettings2.isChannelBlocked(channelNotificationSettings2.mChannel)) {
                    return true;
                }
                ChannelNotificationSettings channelNotificationSettings3 = ChannelNotificationSettings.this;
                int i = booleanValue ? 4 : 3;
                channelNotificationSettings3.mBackupImportance = i;
                channelNotificationSettings3.mChannel.setImportance(i);
                ChannelNotificationSettings.this.mChannel.lockFields(4);
                ChannelNotificationSettings channelNotificationSettings4 = ChannelNotificationSettings.this;
                channelNotificationSettings4.mBackend.updateChannel(channelNotificationSettings4.mPkg, channelNotificationSettings4.mUid, channelNotificationSettings4.mChannel);
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
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.ChannelNotificationSettings.8
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                boolean booleanValue = ((Boolean) obj).booleanValue();
                Context context = ChannelNotificationSettings.this.getContext();
                ChannelNotificationSettings channelNotificationSettings = ChannelNotificationSettings.this;
                NotificationSettingsHelper.setLights(context, channelNotificationSettings.mTargetPkg, channelNotificationSettings.mChannel.getId(), booleanValue);
                ChannelNotificationSettings.this.mChannel.enableLights(booleanValue);
                ChannelNotificationSettings.this.mChannel.lockFields(8);
                ChannelNotificationSettings channelNotificationSettings2 = ChannelNotificationSettings.this;
                channelNotificationSettings2.mBackend.updateChannel(channelNotificationSettings2.mPkg, channelNotificationSettings2.mUid, channelNotificationSettings2.mChannel);
                return true;
            }
        });
    }

    private void setupRingtone() {
        MiuiNotificationSoundPreference miuiNotificationSoundPreference = (MiuiNotificationSoundPreference) findPreference(ThemeManagerConstants.COMPONENT_CODE_RINGTONE);
        this.mRingtone = miuiNotificationSoundPreference;
        miuiNotificationSoundPreference.setRingtone(canSound(this.mChannel.getId()) ? this.mChannel.getSound() : null);
        this.mRingtone.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.ChannelNotificationSettings.6
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                Context context = ChannelNotificationSettings.this.getContext();
                ChannelNotificationSettings channelNotificationSettings = ChannelNotificationSettings.this;
                NotificationSettingsHelper.setSound(context, channelNotificationSettings.mTargetPkg, channelNotificationSettings.mChannel.getId(), true);
                ChannelNotificationSettings channelNotificationSettings2 = ChannelNotificationSettings.this;
                Uri handleSystemRingtone = channelNotificationSettings2.handleSystemRingtone(channelNotificationSettings2.getContext(), ChannelNotificationSettings.this.mPkg, (Uri) obj);
                NotificationChannel notificationChannel = ChannelNotificationSettings.this.mChannel;
                notificationChannel.setSound(handleSystemRingtone, notificationChannel.getAudioAttributes());
                ChannelNotificationSettings.this.mChannel.lockFields(32);
                ChannelNotificationSettings channelNotificationSettings3 = ChannelNotificationSettings.this;
                channelNotificationSettings3.mBackend.updateChannel(channelNotificationSettings3.mPkg, channelNotificationSettings3.mUid, channelNotificationSettings3.mChannel);
                return true;
            }
        });
    }

    private void setupVibrate() {
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("vibrate");
        this.mAllowVibrate = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.ChannelNotificationSettings.7
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                boolean booleanValue = ((Boolean) obj).booleanValue();
                Context context = ChannelNotificationSettings.this.getContext();
                ChannelNotificationSettings channelNotificationSettings = ChannelNotificationSettings.this;
                NotificationSettingsHelper.setVibrate(context, channelNotificationSettings.mTargetPkg, channelNotificationSettings.mChannel.getId(), booleanValue);
                ChannelNotificationSettings.this.mChannel.enableVibration(booleanValue);
                ChannelNotificationSettings.this.mChannel.lockFields(16);
                ChannelNotificationSettings channelNotificationSettings2 = ChannelNotificationSettings.this;
                channelNotificationSettings2.mBackend.updateChannel(channelNotificationSettings2.mPkg, channelNotificationSettings2.mUid, channelNotificationSettings2.mChannel);
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
        setChecked(this.mAllowVibrate, !z && (this.mChannel.shouldVibrate() && canVibrate(this.mChannel.getId())));
        setEnabled(this.mAllowLights, !z && isChannelConfigurable);
        setChecked(this.mAllowLights, !z && (this.mChannel.shouldShowLights() && canLights(this.mChannel.getId())));
        ValuePreference valuePreference = this.mVisibilityOverride;
        if (!z && isChannelConfigurable && canShowKeyguard()) {
            z2 = true;
        }
        setEnabled(valuePreference, z2);
    }
}
