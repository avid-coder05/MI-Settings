package com.android.settings.applications;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.miuisettings.preference.ValuePreference;

/* loaded from: classes.dex */
public class ManageVoice extends SettingsPreferenceFragment {
    private VoiceWakePref mBluetoothWakePref;
    private PackageManager mPackageManager;
    private PreferenceScreen mPreferenceScreen;
    private VoiceWakePref mSysShortcutWakePref;

    /* loaded from: classes.dex */
    class VoiceWakePref {
        public String curPkgName;
        public Intent intent;
        public IntentFilter intentFilter;
        public String label;
        public ValuePreference wakePref;

        public VoiceWakePref(String str, String str2) {
            ActivityInfo activityInfo;
            ValuePreference valuePreference = (ValuePreference) ManageVoice.this.findPreference(str2);
            this.wakePref = valuePreference;
            if (valuePreference != null) {
                valuePreference.setShowRightArrow(true);
                IntentFilter intentFilter = new IntentFilter(str);
                this.intentFilter = intentFilter;
                this.intent = DefaultAppsHelper.getIntent(intentFilter);
                ResolveInfo resolveActivity = ManageVoice.this.mPackageManager.resolveActivity(this.intent, 0);
                if (resolveActivity != null && (activityInfo = resolveActivity.activityInfo) != null) {
                    this.curPkgName = activityInfo.packageName;
                    String applicationLabel = DefaultAppsHelper.getApplicationLabel(ManageVoice.this.getContext(), this.curPkgName, ManageVoice.this.mPackageManager);
                    this.label = applicationLabel;
                    this.wakePref.setValue(applicationLabel);
                    return;
                }
                this.wakePref.setValue(R.string.preferred_app_settings_default);
                Log.e("ManageVoice", "Can not resolve this intent " + this.intent.toString());
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.manage_voice);
        this.mPreferenceScreen = getPreferenceScreen();
        this.mPackageManager = getPackageManager();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        Intent intent = new Intent(getActivity(), PreferredSettings.class);
        boolean equals = preference.getKey().equals("system_shortcut_wake");
        intent.putExtra("preferred_app_intent", (equals ? this.mSysShortcutWakePref : this.mBluetoothWakePref).intent);
        intent.putExtra("preferred_app_intent_filter", (equals ? this.mSysShortcutWakePref : this.mBluetoothWakePref).intentFilter);
        intent.putExtra("preferred_app_package_name", (equals ? this.mSysShortcutWakePref : this.mBluetoothWakePref).curPkgName);
        intent.putExtra("preferred_label", getContext().getResources().getString(R.string.voice_helper_title));
        startActivity(intent);
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        VoiceWakePref voiceWakePref = new VoiceWakePref("android.intent.action.ASSIST", "system_shortcut_wake");
        this.mSysShortcutWakePref = voiceWakePref;
        this.mPreferenceScreen.addPreference(voiceWakePref.wakePref);
        VoiceWakePref voiceWakePref2 = new VoiceWakePref("android.intent.action.VOICE_COMMAND", "bluetooth_wake");
        this.mBluetoothWakePref = voiceWakePref2;
        this.mPreferenceScreen.addPreference(voiceWakePref2.wakePref);
    }
}
