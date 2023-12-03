package com.android.settings.display;

import android.content.Context;
import android.os.Bundle;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.SubSettings;
import com.android.settingslib.miuisettings.preference.ValuePreference;

/* loaded from: classes.dex */
public class ScreenEnhanceEngineActivity extends SubSettings {

    /* loaded from: classes.dex */
    public static class ScreenEnhanceEngineFragment extends SettingsPreferenceFragment {
        private Context context;
        private ValuePreference mScreenEnhanceEngineSrPref = null;
        private ValuePreference mScreenEnhanceEngineAiPref = null;
        private ValuePreference mScreenEnhanceEngineS2hPref = null;
        private ValuePreference mScreenEnhanceEngineMemcPref = null;
        private boolean mIsSrSupport = false;
        private boolean mIsAiSupport = false;
        private boolean mIsS2hSupport = false;
        private boolean mIsMemcSupport = false;

        private <T extends Preference> T findPreferenceImpl(String str) {
            return (T) super.findPreference(str);
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            addPreferencesFromResource(R.xml.screen_enhance_engine_settings);
            ValuePreference valuePreference = (ValuePreference) findPreferenceImpl("screen_enhance_engine_sr");
            this.mScreenEnhanceEngineSrPref = valuePreference;
            boolean z = true;
            valuePreference.setShowRightArrow(true);
            this.mScreenEnhanceEngineSrPref.setVisible(false);
            ValuePreference valuePreference2 = (ValuePreference) findPreferenceImpl("screen_enhance_engine_ai");
            this.mScreenEnhanceEngineAiPref = valuePreference2;
            valuePreference2.setShowRightArrow(true);
            this.mScreenEnhanceEngineAiPref.setVisible(false);
            ValuePreference valuePreference3 = (ValuePreference) findPreferenceImpl("screen_enhance_engine_s2h");
            this.mScreenEnhanceEngineS2hPref = valuePreference3;
            valuePreference3.setShowRightArrow(true);
            this.mScreenEnhanceEngineS2hPref.setVisible(false);
            ValuePreference valuePreference4 = (ValuePreference) findPreferenceImpl("screen_enhance_engine_memc");
            this.mScreenEnhanceEngineMemcPref = valuePreference4;
            valuePreference4.setShowRightArrow(true);
            this.mScreenEnhanceEngineMemcPref.setVisible(false);
            this.context = getActivity();
            if (!ScreenEnhanceEngineStatusCheck.isSrForImageSupport() && !ScreenEnhanceEngineStatusCheck.isSrForVideoSupport()) {
                z = false;
            }
            this.mIsSrSupport = z;
            this.mIsAiSupport = ScreenEnhanceEngineStatusCheck.isAiSupport(this.context);
            this.mIsS2hSupport = ScreenEnhanceEngineStatusCheck.isS2hSupport();
            this.mIsMemcSupport = ScreenEnhanceEngineStatusCheck.isMemcSupport();
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onStart() {
            super.onStart();
            if (this.mIsSrSupport) {
                this.mScreenEnhanceEngineSrPref.setVisible(true);
            }
            if (this.mIsAiSupport) {
                this.mScreenEnhanceEngineAiPref.setVisible(true);
                this.mScreenEnhanceEngineAiPref.setValue(ScreenEnhanceEngineStatusCheck.getAiStatus(this.context) ? R.string.screen_enhance_status_enable : R.string.screen_enhance_status_disable);
            }
            if (this.mIsS2hSupport) {
                this.mScreenEnhanceEngineS2hPref.setVisible(true);
                this.mScreenEnhanceEngineS2hPref.setValue(ScreenEnhanceEngineStatusCheck.getS2hStatus(this.context) ? R.string.screen_enhance_status_enable : R.string.screen_enhance_status_disable);
            }
            if (this.mIsMemcSupport) {
                this.mScreenEnhanceEngineMemcPref.setVisible(true);
                this.mScreenEnhanceEngineMemcPref.setValue(ScreenEnhanceEngineStatusCheck.getMemcStatus(this.context) ? R.string.screen_enhance_status_enable : R.string.screen_enhance_status_disable);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle(R.string.screen_enhance_engine_title);
    }
}
