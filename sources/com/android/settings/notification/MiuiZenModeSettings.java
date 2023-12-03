package com.android.settings.notification;

import android.app.ExtraNotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.service.notification.ZenModeConfig;
import android.util.ArrayMap;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.dndmode.AlarmContentActivity;
import com.android.settings.dndmode.AutoTimeSettings;
import com.android.settings.dndmode.DoNotDisturbModeUtils;
import com.android.settings.dndmode.LabelPreference;
import com.android.settings.dndmode.VipCallSettings;
import miui.os.Build;
import miui.provider.ExtraTelephony;

/* loaded from: classes2.dex */
public class MiuiZenModeSettings extends SettingsPreferenceFragment {
    private LabelPreference mAlarmContnt;
    private PreferenceCategory mAlarmUse;
    private CheckBoxPreference mAutoButton;
    private LabelPreference mAutoTimeSetting;
    private ZenModeConfig mConfig;
    private Context mContext;
    private PreferenceCategory mDowntime;
    private CheckBoxPreference mModeSwitch;
    private PackageManager mPm;
    private CheckBoxPreference mRepeat;
    private PreferenceScreen mRoot;
    private UpdateVipLabelTask mUpdateVipLabelTask;
    private LabelPreference mVip;
    private final Handler mHandler = new Handler();
    private final SettingsObserver mSettingsObserver = new SettingsObserver();
    private boolean isCts = false;

    /* loaded from: classes2.dex */
    private final class SettingsObserver extends ContentObserver {
        private final Uri ZEN_MODE_CONFIG_ETAG_URI;
        private final Uri ZEN_MODE_URI;

        public SettingsObserver() {
            super(MiuiZenModeSettings.this.mHandler);
            this.ZEN_MODE_URI = Settings.Secure.getUriFor("quiet_mode_enable");
            this.ZEN_MODE_CONFIG_ETAG_URI = Settings.Global.getUriFor("zen_mode_config_etag");
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            MiuiZenModeSettings.this.updateControls();
        }

        public void register() {
            MiuiZenModeSettings.this.getContentResolver().registerContentObserver(this.ZEN_MODE_URI, false, this, -1);
            MiuiZenModeSettings.this.getContentResolver().registerContentObserver(this.ZEN_MODE_CONFIG_ETAG_URI, false, this);
        }

        public void unregister() {
            MiuiZenModeSettings.this.getContentResolver().unregisterContentObserver(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class UpdateVipLabelTask extends AsyncTask<Void, Void, String> {
        private UpdateVipLabelTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public String doInBackground(Void... voidArr) {
            if (isCancelled()) {
                return null;
            }
            String[] stringArray = MiuiZenModeSettings.this.mContext.getResources().getStringArray(MiuiSettings.SilenceMode.isSupported ? R.array.new_dndm_vip_list_group_array : R.array.dndm_vip_list_group_array);
            return MiuiSettings.SilenceMode.isSupported ? stringArray[MiuiZenModeSettings.this.mConfig.allowCallsFrom - 1] : MiuiZenModeSettings.this.mConfig.allowCallsFrom == 3 ? MiuiZenModeSettings.this.mContext.getString(R.string.dndm_vip_call_summary_custom, Integer.valueOf(MiuiZenModeSettings.getCustomVipListCount(MiuiZenModeSettings.this.mContext))) : stringArray[MiuiZenModeSettings.this.mConfig.allowCallsFrom];
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(String str) {
            super.onPostExecute((UpdateVipLabelTask) str);
            MiuiZenModeSettings.this.mVip.setLabel(str);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0027, code lost:
    
        if (r1 != null) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0030, code lost:
    
        if (r1 == null) goto L18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0032, code lost:
    
        r1.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0035, code lost:
    
        return 0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static int getCustomVipListCount(android.content.Context r8) {
        /*
            r0 = 0
            r1 = 0
            android.content.ContentResolver r2 = r8.getContentResolver()     // Catch: java.lang.Throwable -> L2a java.lang.Exception -> L2c
            android.net.Uri r3 = miui.provider.ExtraTelephony.Phonelist.CONTENT_URI     // Catch: java.lang.Throwable -> L2a java.lang.Exception -> L2c
            java.lang.String r8 = "count(*)"
            java.lang.String[] r4 = new java.lang.String[]{r8}     // Catch: java.lang.Throwable -> L2a java.lang.Exception -> L2c
            java.lang.String r5 = "type='3' and sync_dirty <> 1"
            r6 = 0
            r7 = 0
            android.database.Cursor r1 = r2.query(r3, r4, r5, r6, r7)     // Catch: java.lang.Throwable -> L2a java.lang.Exception -> L2c
            if (r1 == 0) goto L27
            boolean r8 = r1.moveToNext()     // Catch: java.lang.Throwable -> L2a java.lang.Exception -> L2c
            if (r8 == 0) goto L27
            int r8 = r1.getInt(r0)     // Catch: java.lang.Throwable -> L2a java.lang.Exception -> L2c
            r1.close()
            return r8
        L27:
            if (r1 == 0) goto L35
            goto L32
        L2a:
            r8 = move-exception
            goto L36
        L2c:
            r8 = move-exception
            r8.printStackTrace()     // Catch: java.lang.Throwable -> L2a
            if (r1 == 0) goto L35
        L32:
            r1.close()
        L35:
            return r0
        L36:
            if (r1 == 0) goto L3b
            r1.close()
        L3b:
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.notification.MiuiZenModeSettings.getCustomVipListCount(android.content.Context):int");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideTimeLabel() {
        this.mAutoTimeSetting.setLabel("");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showTimeLabel() {
        Context context = this.mContext;
        String formatTime = DoNotDisturbModeUtils.formatTime(context, MiuiSettings.AntiSpam.getStartTimeForQuietMode(context));
        Context context2 = this.mContext;
        this.mAutoTimeSetting.setLabel(this.mContext.getString(R.string.dndm_auto_time_setting_summary, formatTime, DoNotDisturbModeUtils.formatTime(context2, MiuiSettings.AntiSpam.getEndTimeForQuietMode(context2))));
    }

    private void updateAlarmContent() {
        ZenModeConfig zenModeConfig = this.mConfig;
        boolean z = zenModeConfig.allowCalls;
        if (z && zenModeConfig.allowMessages && zenModeConfig.allowEvents) {
            this.mAlarmContnt.setLabel(this.mContext.getString(R.string.dndm_alarm_content_all));
        } else if (z && !zenModeConfig.allowMessages && zenModeConfig.allowEvents) {
            this.mAlarmContnt.setLabel(this.mContext.getString(R.string.dndm_alarm_content_event_and_call));
        } else if (!z && zenModeConfig.allowMessages && zenModeConfig.allowEvents) {
            this.mAlarmContnt.setLabel(this.mContext.getString(R.string.dndm_alarm_content_event_and_sms));
        } else if (z && zenModeConfig.allowMessages && !zenModeConfig.allowEvents) {
            this.mAlarmContnt.setLabel(this.mContext.getString(R.string.dndm_alarm_content_call_sms));
        } else if (!z && !zenModeConfig.allowMessages && zenModeConfig.allowEvents) {
            this.mAlarmContnt.setLabel(this.mContext.getString(R.string.dndm_alarm_content_event));
        } else if (z && !zenModeConfig.allowMessages && !zenModeConfig.allowEvents) {
            this.mAlarmContnt.setLabel(this.mContext.getString(R.string.dndm_alarm_content_call));
        } else if (z || !zenModeConfig.allowMessages || zenModeConfig.allowEvents) {
            this.mAlarmContnt.setLabel(this.mContext.getString(R.string.dndm_alarm_content_not_all));
        } else {
            this.mAlarmContnt.setLabel(this.mContext.getString(R.string.dndm_alarm_content_sms));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateControls() {
        this.mModeSwitch.setChecked(MiuiSettings.AntiSpam.isQuietModeEnable(this.mContext));
        this.mConfig = ExtraNotificationManager.getZenModeConfig(this.mContext);
        Log.d("ZenModeSettings", "Loaded mConfig=" + this.mConfig);
        if (!Build.IS_TABLET) {
            UpdateVipLabelTask updateVipLabelTask = this.mUpdateVipLabelTask;
            if (updateVipLabelTask != null && !updateVipLabelTask.isCancelled()) {
                this.mUpdateVipLabelTask.cancel(true);
            }
            UpdateVipLabelTask updateVipLabelTask2 = new UpdateVipLabelTask();
            this.mUpdateVipLabelTask = updateVipLabelTask2;
            updateVipLabelTask2.execute(new Void[0]);
        }
        updateAlarmContent();
        if (MiuiSettings.AntiSpam.isAutoTimerOfQuietModeEnable(this.mContext)) {
            showTimeLabel();
        } else {
            hideTimeLabel();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiZenModeSettings.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.zen_mode_settings);
        FragmentActivity activity = getActivity();
        this.mContext = activity;
        this.mPm = activity.getPackageManager();
        this.mRoot = getPreferenceScreen();
        this.mSettingsObserver.register();
        this.mConfig = ExtraNotificationManager.getZenModeConfig(this.mContext);
        try {
            this.mPm.getPackageInfo("com.android.cts.verifier", 0);
            this.isCts = true;
        } catch (PackageManager.NameNotFoundException unused) {
            this.isCts = false;
        }
        boolean z = Build.IS_TABLET;
        if (z) {
            ZenModeConfig zenModeConfig = this.mConfig;
            if (zenModeConfig.allowCalls) {
                ZenModeConfig copy = zenModeConfig.copy();
                copy.allowCalls = false;
                ExtraNotificationManager.setZenModeConfig(this.mContext, copy);
            }
        }
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) this.mRoot.findPreference("key_do_not_disturb_mode");
        this.mModeSwitch = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.MiuiZenModeSettings.1
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                MiuiSettings.AntiSpam.setQuietMode(MiuiZenModeSettings.this.mContext, ((Boolean) obj).booleanValue());
                return true;
            }
        });
        PreferenceCategory preferenceCategory = (PreferenceCategory) this.mRoot.findPreference("key_auto_setting_group");
        this.mDowntime = preferenceCategory;
        CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) preferenceCategory.findPreference("key_auto_button");
        this.mAutoButton = checkBoxPreference2;
        checkBoxPreference2.setChecked(MiuiSettings.AntiSpam.isAutoTimerOfQuietModeEnable(this.mContext));
        this.mAutoButton.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.MiuiZenModeSettings.2
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                Boolean bool = (Boolean) obj;
                MiuiSettings.AntiSpam.setAutoTimerOfQuietMode(MiuiZenModeSettings.this.mContext, bool.booleanValue());
                if (!bool.booleanValue()) {
                    MiuiZenModeSettings.this.hideTimeLabel();
                    DoNotDisturbModeUtils.cancelAutoTime(MiuiZenModeSettings.this.mContext);
                    return true;
                }
                ZenModeConfig copy2 = MiuiZenModeSettings.this.mConfig.copy();
                if (!copy2.automaticRules.isEmpty()) {
                    copy2.automaticRules = new ArrayMap();
                    ExtraNotificationManager.setZenModeConfig(MiuiZenModeSettings.this.mContext, copy2);
                }
                MiuiZenModeSettings.this.showTimeLabel();
                DoNotDisturbModeUtils.startAutoTime(MiuiZenModeSettings.this.mContext);
                return true;
            }
        });
        LabelPreference labelPreference = (LabelPreference) this.mDowntime.findPreference("key_auto_time_setting");
        this.mAutoTimeSetting = labelPreference;
        labelPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.MiuiZenModeSettings.3
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                MiuiZenModeSettings.this.mContext.startActivity(new Intent(MiuiZenModeSettings.this.mContext, AutoTimeSettings.class));
                return true;
            }
        });
        PreferenceCategory preferenceCategory2 = (PreferenceCategory) this.mRoot.findPreference("alarm_use");
        this.mAlarmUse = preferenceCategory2;
        LabelPreference labelPreference2 = (LabelPreference) preferenceCategory2.findPreference(ExtraTelephony.Whitelist.VIP);
        this.mVip = labelPreference2;
        labelPreference2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.MiuiZenModeSettings.4
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                MiuiZenModeSettings.this.mContext.startActivity(new Intent(MiuiZenModeSettings.this.mContext, VipCallSettings.class));
                return true;
            }
        });
        LabelPreference labelPreference3 = (LabelPreference) this.mAlarmUse.findPreference("alarm_content");
        this.mAlarmContnt = labelPreference3;
        labelPreference3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.MiuiZenModeSettings.5
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(MiuiZenModeSettings.this.mContext, AlarmContentActivity.class);
                intent.putExtra("isCts", MiuiZenModeSettings.this.isCts);
                MiuiZenModeSettings.this.mContext.startActivity(intent);
                return true;
            }
        });
        CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) this.mAlarmUse.findPreference("repeat");
        this.mRepeat = checkBoxPreference3;
        checkBoxPreference3.setChecked(MiuiSettings.AntiSpam.isRepeatedCallActionEnable(this.mContext));
        this.mRepeat.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.MiuiZenModeSettings.6
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                MiuiSettings.AntiSpam.setRepeatedCallActionEnable(MiuiZenModeSettings.this.mContext, ((Boolean) obj).booleanValue());
                return true;
            }
        });
        if (z && !this.isCts) {
            this.mModeSwitch.setSummary(R.string.dndm_summary_for_pad);
            this.mRoot.removePreference(this.mAlarmUse);
        }
        updateControls();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mSettingsObserver.unregister();
        UpdateVipLabelTask updateVipLabelTask = this.mUpdateVipLabelTask;
        if (updateVipLabelTask == null || updateVipLabelTask.isCancelled()) {
            return;
        }
        this.mUpdateVipLabelTask.cancel(true);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateControls();
    }
}
