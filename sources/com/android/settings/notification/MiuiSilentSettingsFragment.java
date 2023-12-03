package com.android.settings.notification;

import android.app.ExtraNotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.media.AudioServiceInjector;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.service.notification.ZenModeConfig;
import android.util.Log;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.dndmode.LabelPreference;
import com.android.settings.report.InternationalCompat;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.miuisettings.preference.RadioButtonPreference;
import com.android.settingslib.miuisettings.preference.RadioButtonPreferenceCategory;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import java.util.HashMap;
import java.util.Iterator;
import miui.os.Build;
import miui.provider.ExtraTelephony;

/* loaded from: classes2.dex */
public class MiuiSilentSettingsFragment extends SettingsPreferenceFragment {
    private PreferenceCategory mAdvancedSettings;
    private PreferenceCategory mAdvancedSettings2;
    private ZenModeConfig mConfig;
    private Context mContext;
    private Handler mHandler;
    private CheckBoxPreference mMuteMusic;
    private CheckBoxPreference mMuteVoiceAssist;
    private CheckBoxPreference mNetWorkAlarmPreference;
    private SilentModeNetWorkAlarmSummaryPreference mNetWorkAlarmSummaryPreference;
    private RadioButtonPreference mNormal;
    private CheckBoxPreference mPopup;
    private CheckBoxPreference mRepeat;
    private PreferenceScreen mRoot;
    private RadioButtonPreferenceCategory mSilentModeSettings;
    private RadioButtonPreference mStandard;
    private LabelPreference mTimedMute;
    private RadioButtonPreference mTotal;
    private DropDownPreference mVipList;
    private final SettingsObserver mSettingsObserver = new SettingsObserver();
    private Preference.OnPreferenceClickListener mRadioButtonClickListner = new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.MiuiSilentSettingsFragment.9
        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            MiuiSilentSettingsFragment.this.mSilentModeSettings.setCheckedPreference(preference);
            ZenModeConfig zenModeConfig = ExtraNotificationManager.getZenModeConfig(MiuiSilentSettingsFragment.this.mContext);
            ZenModeConfig.ZenRule zenRule = zenModeConfig.manualRule;
            Uri uri = (zenRule == null || !ZenModeConfig.isValidCountdownConditionId(zenRule.conditionId)) ? null : zenModeConfig.manualRule.conditionId;
            HashMap hashMap = new HashMap();
            if (preference.getKey().equals("key_normal")) {
                hashMap.put("status", 0);
                MiuiSettings.SilenceMode.setSilenceMode(MiuiSilentSettingsFragment.this.mContext, 0, uri);
            } else if (preference.getKey().equals("key_standard")) {
                hashMap.put("status", 1);
                MiuiSettings.SilenceMode.setSilenceMode(MiuiSilentSettingsFragment.this.mContext, 4, uri);
            } else if (preference.getKey().equals("key_total")) {
                hashMap.put("status", 2);
                MiuiSettings.SilenceMode.setSilenceMode(MiuiSilentSettingsFragment.this.mContext, 1, uri);
            }
            InternationalCompat.trackReportObjectEvent("setting_sound_mute_DT", hashMap);
            return true;
        }
    };

    /* loaded from: classes2.dex */
    private final class SettingsObserver extends ContentObserver {
        private final Uri ZEN_MODE_CONFIG_ETAG_URI;
        private final Uri ZEN_MODE_URI;

        public SettingsObserver() {
            super(MiuiSilentSettingsFragment.this.mHandler);
            this.ZEN_MODE_URI = Settings.Global.getUriFor(ExtraTelephony.ZEN_MODE);
            this.ZEN_MODE_CONFIG_ETAG_URI = Settings.Global.getUriFor("zen_mode_config_etag");
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            MiuiSilentSettingsFragment.this.mHandler.sendEmptyMessage(1);
        }

        public void register() {
            MiuiSilentSettingsFragment.this.getContentResolver().registerContentObserver(this.ZEN_MODE_URI, false, this, -1);
            MiuiSilentSettingsFragment.this.getContentResolver().registerContentObserver(this.ZEN_MODE_CONFIG_ETAG_URI, false, this, -1);
        }

        public void unregister() {
            MiuiSilentSettingsFragment.this.getContentResolver().unregisterContentObserver(this);
        }
    }

    private boolean isAutomationRuleEnabled(ZenModeConfig zenModeConfig) {
        Iterator it = zenModeConfig.automaticRules.values().iterator();
        while (it.hasNext()) {
            if (((ZenModeConfig.ZenRule) it.next()).enabled) {
                return true;
            }
        }
        return false;
    }

    public static String turnMillSecondsToHour(long j) {
        StringBuilder sb = new StringBuilder();
        int i = (int) (j / 3600000);
        int i2 = ((int) (j % 3600000)) / 60000;
        if (i < 10) {
            sb.append("0");
        }
        sb.append(i);
        sb.append(":");
        if (i2 < 10) {
            sb.append("0");
        }
        sb.append(i2);
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateControl() {
        int zenMode = MiuiSettings.SilenceMode.getZenMode(this.mContext);
        this.mHandler.removeMessages(1);
        if (zenMode == 4) {
            this.mSilentModeSettings.setCheckedPreference(this.mStandard);
            this.mRoot.addPreference(this.mAdvancedSettings);
            this.mAdvancedSettings.removePreference(this.mRepeat);
            this.mAdvancedSettings.removePreference(this.mPopup);
            this.mAdvancedSettings.addPreference(this.mMuteMusic);
            this.mAdvancedSettings.addPreference(this.mMuteVoiceAssist);
            this.mAdvancedSettings.removePreference(this.mVipList);
            this.mAdvancedSettings.removePreference(this.mNetWorkAlarmPreference);
            this.mRoot.removePreference(this.mNetWorkAlarmSummaryPreference);
        } else if (zenMode == 1) {
            this.mSilentModeSettings.setCheckedPreference(this.mTotal);
            this.mRoot.addPreference(this.mAdvancedSettings);
            if (SettingsFeatures.isSupportCustomZenPriorityPkg()) {
                this.mRoot.addPreference(this.mNetWorkAlarmSummaryPreference);
                this.mAdvancedSettings.addPreference(this.mNetWorkAlarmPreference);
            } else {
                this.mRoot.removePreference(this.mNetWorkAlarmSummaryPreference);
                this.mAdvancedSettings.removePreference(this.mNetWorkAlarmPreference);
            }
            this.mAdvancedSettings.addPreference(this.mVipList);
            this.mAdvancedSettings.addPreference(this.mPopup);
            this.mAdvancedSettings.removePreference(this.mMuteMusic);
            this.mAdvancedSettings.removePreference(this.mMuteVoiceAssist);
            this.mAdvancedSettings.addPreference(this.mRepeat);
            if (Build.IS_TABLET) {
                this.mAdvancedSettings.removePreference(this.mVipList);
                this.mAdvancedSettings.removePreference(this.mRepeat);
            }
        } else {
            this.mSilentModeSettings.setCheckedPreference(this.mNormal);
            this.mRoot.removePreference(this.mAdvancedSettings);
            this.mRoot.removePreference(this.mNetWorkAlarmSummaryPreference);
        }
        if (AudioServiceInjector.getVoiceAssistNum() == -1 || Build.IS_GLOBAL_BUILD) {
            this.mAdvancedSettings.removePreference(this.mMuteVoiceAssist);
        }
        this.mConfig = ExtraNotificationManager.getZenModeConfig(this.mContext);
        if (!Build.IS_TABLET) {
            String[] stringArray = getResources().getStringArray(R.array.vip_mode_text);
            ZenModeConfig zenModeConfig = this.mConfig;
            int length = zenModeConfig.allowCalls ? zenModeConfig.allowCallsFrom : stringArray.length - 1;
            this.mVipList.setSummary(stringArray[length]);
            this.mVipList.setValue(String.valueOf(length));
        }
        if (isAutomationRuleEnabled(this.mConfig)) {
            this.mTimedMute.setLabel(getResources().getString(R.string.mode_enable));
        } else {
            this.mTimedMute.setLabel(getResources().getString(R.string.mode_disable));
        }
        this.mContext.getResources().getString(R.string.always);
        ZenModeConfig.ZenRule zenRule = ExtraNotificationManager.getZenModeConfig(this.mContext).manualRule;
        if (zenRule != null) {
            long tryParseCountdownConditionId = ZenModeConfig.tryParseCountdownConditionId(zenRule.conditionId);
            if (tryParseCountdownConditionId != 0) {
                turnMillSecondsToHour(tryParseCountdownConditionId - System.currentTimeMillis());
                Handler handler = this.mHandler;
                handler.sendMessageDelayed(handler.obtainMessage(1), 1000L);
            }
        }
    }

    public static void updateSilentMode(Context context, boolean z) {
        if (MiuiSettings.SilenceMode.getZenMode(context) != 4) {
            return;
        }
        Settings.Global.putInt(context.getContentResolver(), "miui_unmute_by_settings", !z ? 1 : 0);
        Log.e("MiuiSilentSettingsFragment", "[updateSilentMode]Set flag indicate update by settings.....");
        Uri uri = null;
        ZenModeConfig zenModeConfig = ExtraNotificationManager.getZenModeConfig(context);
        ZenModeConfig.ZenRule zenRule = zenModeConfig.manualRule;
        if (zenRule != null && ZenModeConfig.isValidCountdownConditionId(zenRule.conditionId)) {
            uri = zenModeConfig.manualRule.conditionId;
        }
        MiuiSettings.SilenceMode.setSilenceMode(context, 4, uri);
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiSilentSettingsFragment.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.silent_settings);
        this.mContext = getActivity();
        this.mHandler = new Handler(Looper.getMainLooper()) { // from class: com.android.settings.notification.MiuiSilentSettingsFragment.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what != 1) {
                    return;
                }
                MiuiSilentSettingsFragment.this.updateControl();
            }
        };
        this.mRoot = (PreferenceScreen) findPreference("silent_mode_settings");
        this.mSettingsObserver.register();
        RadioButtonPreferenceCategory radioButtonPreferenceCategory = (RadioButtonPreferenceCategory) findPreference("key_silent_mode_settings");
        this.mSilentModeSettings = radioButtonPreferenceCategory;
        RadioButtonPreference radioButtonPreference = (RadioButtonPreference) radioButtonPreferenceCategory.findPreference("key_normal");
        this.mNormal = radioButtonPreference;
        radioButtonPreference.setOnPreferenceClickListener(this.mRadioButtonClickListner);
        RadioButtonPreference radioButtonPreference2 = (RadioButtonPreference) this.mSilentModeSettings.findPreference("key_standard");
        this.mStandard = radioButtonPreference2;
        radioButtonPreference2.setOnPreferenceClickListener(this.mRadioButtonClickListner);
        this.mTotal = (RadioButtonPreference) this.mSilentModeSettings.findPreference("key_total");
        this.mTotal.setSummary(((Vibrator) getSystemService("vibrator")).hasVibrator() ? getActivity().getResources().getString(R.string.total_info) : getActivity().getResources().getString(R.string.total_info_no_vibrator));
        this.mTotal.setOnPreferenceClickListener(this.mRadioButtonClickListner);
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("key_advanced_settings");
        this.mAdvancedSettings = preferenceCategory;
        DropDownPreference dropDownPreference = (DropDownPreference) preferenceCategory.findPreference("key_vip_list");
        this.mVipList = dropDownPreference;
        boolean z = Build.IS_TABLET;
        if (z) {
            this.mAdvancedSettings.removePreference(dropDownPreference);
        } else {
            dropDownPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.MiuiSilentSettingsFragment.2
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    int parseInt = Integer.parseInt((String) obj);
                    boolean z2 = parseInt <= 2;
                    MiuiSettings.SilenceMode.enableVIPMode(MiuiSilentSettingsFragment.this.mContext, z2);
                    if (z2) {
                        ZenModeConfig zenModeConfig = ExtraNotificationManager.getZenModeConfig(MiuiSilentSettingsFragment.this.mContext);
                        zenModeConfig.allowCallsFrom = parseInt;
                        zenModeConfig.allowMessagesFrom = parseInt;
                        ExtraNotificationManager.setZenModeConfig(MiuiSilentSettingsFragment.this.mContext, zenModeConfig);
                    }
                    MiuiSilentSettingsFragment.this.updateControl();
                    return false;
                }
            });
        }
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("key_repeat");
        this.mRepeat = checkBoxPreference;
        checkBoxPreference.setSummary(String.format(getResources().getString(R.string.repeat_call_info), 15));
        if (z) {
            this.mAdvancedSettings.removePreference(this.mRepeat);
        } else {
            this.mRepeat.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.MiuiSilentSettingsFragment.3
                @Override // androidx.preference.Preference.OnPreferenceChangeListener
                public boolean onPreferenceChange(Preference preference, Object obj) {
                    MiuiSettings.AntiSpam.setRepeatedCallActionEnable(MiuiSilentSettingsFragment.this.mContext, ((Boolean) obj).booleanValue());
                    return true;
                }
            });
            this.mRepeat.setChecked(MiuiSettings.AntiSpam.isRepeatedCallActionEnable(this.mContext));
        }
        CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) findPreference("key_popup_window");
        this.mPopup = checkBoxPreference2;
        checkBoxPreference2.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.MiuiSilentSettingsFragment.4
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                Settings.System.putIntForUser(MiuiSilentSettingsFragment.this.mContext.getContentResolver(), "show_notification", ((Boolean) obj).booleanValue() ? 1 : 0, -3);
                return true;
            }
        });
        this.mPopup.setChecked(1 == Settings.System.getIntForUser(this.mContext.getContentResolver(), "show_notification", 1, -3));
        CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) findPreference("key_mute_music");
        this.mMuteMusic = checkBoxPreference3;
        checkBoxPreference3.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.MiuiSilentSettingsFragment.5
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                Boolean bool = (Boolean) obj;
                Settings.System.putIntForUser(MiuiSilentSettingsFragment.this.mContext.getContentResolver(), "mute_music_at_silent", bool.booleanValue() ? 1 : 0, -3);
                MiuiSilentSettingsFragment.updateSilentMode(MiuiSilentSettingsFragment.this.mContext, bool.booleanValue());
                return true;
            }
        });
        this.mMuteMusic.setChecked(1 == Settings.System.getIntForUser(this.mContext.getContentResolver(), "mute_music_at_silent", 0, -3));
        CheckBoxPreference checkBoxPreference4 = (CheckBoxPreference) findPreference("key_mute_voiceassist");
        this.mMuteVoiceAssist = checkBoxPreference4;
        checkBoxPreference4.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.MiuiSilentSettingsFragment.6
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                Boolean bool = (Boolean) obj;
                Settings.System.putIntForUser(MiuiSilentSettingsFragment.this.mContext.getContentResolver(), "mute_voiceassit_at_silent", bool.booleanValue() ? 1 : 0, -3);
                MiuiSilentSettingsFragment.updateSilentMode(MiuiSilentSettingsFragment.this.mContext, bool.booleanValue());
                return true;
            }
        });
        this.mMuteVoiceAssist.setChecked(1 == Settings.System.getIntForUser(this.mContext.getContentResolver(), "mute_voiceassit_at_silent", 1, -3));
        PreferenceCategory preferenceCategory2 = (PreferenceCategory) findPreference("key_advanced_settings2");
        this.mAdvancedSettings2 = preferenceCategory2;
        LabelPreference labelPreference = (LabelPreference) preferenceCategory2.findPreference("key_timing_mute");
        this.mTimedMute = labelPreference;
        labelPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.MiuiSilentSettingsFragment.7
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                MiuiSilentSettingsFragment.this.mContext.startActivity(new Intent(MiuiSilentSettingsFragment.this.mContext, SilentModeSettings.class));
                return true;
            }
        });
        this.mNetWorkAlarmSummaryPreference = (SilentModeNetWorkAlarmSummaryPreference) findPreference("key_network_alarm_summary");
        CheckBoxPreference checkBoxPreference5 = (CheckBoxPreference) findPreference("key_network_alarm");
        this.mNetWorkAlarmPreference = checkBoxPreference5;
        checkBoxPreference5.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.notification.MiuiSilentSettingsFragment.8
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                Boolean bool = (Boolean) obj;
                Settings.System.putIntForUser(MiuiSilentSettingsFragment.this.mContext.getContentResolver(), "allow_network_call_ring", bool.booleanValue() ? 1 : 0, -3);
                MiStatInterfaceUtils.trackSwitchEvent("netWorkAlarm", bool.booleanValue());
                OneTrackInterfaceUtils.trackSwitchEvent("netWorkAlarm", bool.booleanValue());
                return true;
            }
        });
        this.mNetWorkAlarmPreference.setChecked(1 == Settings.System.getIntForUser(this.mContext.getContentResolver(), "allow_network_call_ring", 0, -3));
        this.mRoot.addPreference(this.mSilentModeSettings);
        updateControl();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mHandler.removeMessages(1);
        this.mSettingsObserver.unregister();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateControl();
    }
}
