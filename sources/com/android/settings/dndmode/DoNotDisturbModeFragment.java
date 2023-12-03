package com.android.settings.dndmode;

import android.app.Activity;
import android.os.Bundle;
import android.provider.MiuiSettings;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import miui.provider.ExtraTelephony;

/* loaded from: classes.dex */
public class DoNotDisturbModeFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private Activity mActivity;
    private CheckBoxPreference mAutoButton;
    private PreferenceGroup mAutoSettingGroup;
    private PreferenceScreen mAutoTimeSetting;
    private CheckBoxPreference mDoNotDisturbMode;
    private final ExtraTelephony.QuietModeEnableListener mQuietModeObserver = new ExtraTelephony.QuietModeEnableListener() { // from class: com.android.settings.dndmode.DoNotDisturbModeFragment.1
        @Override // miui.provider.ExtraTelephony.QuietModeEnableListener
        public void onQuietModeEnableChange(boolean z) {
            DoNotDisturbModeFragment.this.onQuietModeChanged();
        }
    };
    private CheckBoxPreference mRepeatedCallButton;
    private PreferenceScreen mVipCallSetting;

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0029, code lost:
    
        if (r1 != null) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x0032, code lost:
    
        if (r1 == null) goto L18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0034, code lost:
    
        r1.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0037, code lost:
    
        return 0;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private int getCustomVipListCount() {
        /*
            r8 = this;
            r0 = 0
            r1 = 0
            android.app.Activity r8 = r8.mActivity     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2e
            android.content.ContentResolver r2 = r8.getContentResolver()     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2e
            android.net.Uri r3 = miui.provider.ExtraTelephony.Phonelist.CONTENT_URI     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2e
            java.lang.String r8 = "count(*)"
            java.lang.String[] r4 = new java.lang.String[]{r8}     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2e
            java.lang.String r5 = "type='3'"
            r6 = 0
            r7 = 0
            android.database.Cursor r1 = r2.query(r3, r4, r5, r6, r7)     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2e
            if (r1 == 0) goto L29
            boolean r8 = r1.moveToNext()     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2e
            if (r8 == 0) goto L29
            int r8 = r1.getInt(r0)     // Catch: java.lang.Throwable -> L2c java.lang.Exception -> L2e
            r1.close()
            return r8
        L29:
            if (r1 == 0) goto L37
            goto L34
        L2c:
            r8 = move-exception
            goto L38
        L2e:
            r8 = move-exception
            r8.printStackTrace()     // Catch: java.lang.Throwable -> L2c
            if (r1 == 0) goto L37
        L34:
            r1.close()
        L37:
            return r0
        L38:
            if (r1 == 0) goto L3d
            r1.close()
        L3d:
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.dndmode.DoNotDisturbModeFragment.getCustomVipListCount():int");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onQuietModeChanged() {
        this.mDoNotDisturbMode.setChecked(MiuiSettings.AntiSpam.isQuietModeEnable(this.mActivity));
    }

    private void setTimeSummary() {
        Activity activity = this.mActivity;
        String formatTime = DoNotDisturbModeUtils.formatTime(activity, MiuiSettings.AntiSpam.getStartTimeForQuietMode(activity));
        Activity activity2 = this.mActivity;
        this.mAutoTimeSetting.setSummary(getResources().getString(R.string.dndm_auto_time_setting_summary, formatTime, DoNotDisturbModeUtils.formatTime(activity2, MiuiSettings.AntiSpam.getEndTimeForQuietMode(activity2))));
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.dndm_fragment);
        setHasOptionsMenu(true);
        this.mActivity = getActivity();
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("key_do_not_disturb_mode");
        this.mDoNotDisturbMode = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(this);
        this.mAutoSettingGroup = (PreferenceGroup) findPreference("key_auto_setting_group");
        CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) findPreference("key_auto_button");
        this.mAutoButton = checkBoxPreference2;
        checkBoxPreference2.setOnPreferenceChangeListener(this);
        this.mAutoTimeSetting = (PreferenceScreen) findPreference("key_auto_time_setting");
        this.mVipCallSetting = (PreferenceScreen) findPreference("key_vip_call_setting");
        this.mAutoButton.setOnPreferenceChangeListener(this);
        CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) findPreference("key_repeated_call_button");
        this.mRepeatedCallButton = checkBoxPreference3;
        checkBoxPreference3.setOnPreferenceChangeListener(this);
        ExtraTelephony.registerQuietModeEnableListener(this.mActivity, this.mQuietModeObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        ExtraTelephony.unRegisterQuietModeEnableListener(this.mActivity, this.mQuietModeObserver);
        super.onDestroy();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Boolean bool = (Boolean) obj;
        if (preference == this.mDoNotDisturbMode) {
            MiuiSettings.AntiSpam.setQuietMode(this.mActivity, bool.booleanValue());
            return true;
        } else if (preference == this.mAutoButton) {
            MiuiSettings.AntiSpam.setAutoTimerOfQuietMode(this.mActivity, bool.booleanValue());
            if (!bool.booleanValue()) {
                this.mAutoSettingGroup.removePreference(this.mAutoTimeSetting);
                return true;
            }
            this.mAutoSettingGroup.addPreference(this.mAutoTimeSetting);
            DoNotDisturbModeUtils.startAutoTime(this.mActivity);
            setTimeSummary();
            return true;
        } else if (preference == this.mRepeatedCallButton) {
            MiuiSettings.AntiSpam.setRepeatedCallActionEnable(this.mActivity, bool.booleanValue());
            if (bool.booleanValue()) {
                this.mRepeatedCallButton.setSummary(R.string.dndm_repeated_call_summary_3min);
                return true;
            }
            this.mRepeatedCallButton.setSummary(R.string.dndm_repeated_call_summary_none);
            return true;
        } else {
            return true;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mDoNotDisturbMode.setChecked(MiuiSettings.AntiSpam.isQuietModeEnable(this.mActivity));
        if (MiuiSettings.AntiSpam.isVipCallActionEnable(this.mActivity)) {
            int vipListForQuietMode = MiuiSettings.AntiSpam.getVipListForQuietMode(this.mActivity);
            if (vipListForQuietMode == 2) {
                this.mVipCallSetting.setSummary(getResources().getString(R.string.dndm_vip_call_summary_custom, Integer.valueOf(getCustomVipListCount())));
            } else if (vipListForQuietMode == 0) {
                this.mVipCallSetting.setSummary(R.string.dndm_vip_call_summary_contact);
            }
        } else {
            this.mVipCallSetting.setSummary(R.string.dndm_vip_call_summary_none);
        }
        if (MiuiSettings.AntiSpam.isAutoTimerOfQuietModeEnable(this.mActivity)) {
            this.mAutoButton.setChecked(true);
            this.mAutoSettingGroup.addPreference(this.mAutoTimeSetting);
            setTimeSummary();
        } else {
            this.mAutoButton.setChecked(false);
            this.mAutoSettingGroup.removePreference(this.mAutoTimeSetting);
        }
        if (MiuiSettings.AntiSpam.isRepeatedCallActionEnable(this.mActivity)) {
            this.mRepeatedCallButton.setChecked(true);
            this.mRepeatedCallButton.setSummary(R.string.dndm_repeated_call_summary_3min);
            return;
        }
        this.mRepeatedCallButton.setChecked(false);
        this.mRepeatedCallButton.setSummary(R.string.dndm_repeated_call_summary_none);
    }
}
