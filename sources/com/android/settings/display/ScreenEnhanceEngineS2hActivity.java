package com.android.settings.display;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.SubSettings;
import com.android.settings.widget.CustomCheckBoxPreference;
import com.android.settings.widget.ScreenEnhanceEngineNotePreference;
import com.android.settings.widget.ScreenEnhanceEngineTopPreference;

/* loaded from: classes.dex */
public class ScreenEnhanceEngineS2hActivity extends SubSettings {

    /* loaded from: classes.dex */
    public static class ScreenEnhanceEngineS2hFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
        private Context context;
        private CustomCheckBoxPreference mEnableS2hPref;
        private ScreenEnhanceEngineNotePreference noteInfoPref;
        private ScreenEnhanceEngineTopPreference topViewPref;

        private <T extends Preference> T findPreferenceImpl(String str) {
            return (T) super.findPreference(str);
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            addPreferencesFromResource(R.xml.screen_enhance_engine_s2h_settings);
            this.context = getActivity();
            this.topViewPref = (ScreenEnhanceEngineTopPreference) findPreferenceImpl("screen_enhance_top_view");
            this.mEnableS2hPref = (CustomCheckBoxPreference) findPreferenceImpl("screen_enhance_engine_s2h_enable");
            this.noteInfoPref = (ScreenEnhanceEngineNotePreference) findPreferenceImpl("screen_enhance_note_info");
            this.mEnableS2hPref.setOnPreferenceChangeListener(this);
            this.topViewPref.addVideoView(R.raw.screen_enhance_engine_s2h_video);
            this.topViewPref.setEnabled(false);
            this.noteInfoPref.setNoteInfo(this.context.getString(R.string.screen_enhance_engine_s2h_pic_summary));
            this.noteInfoPref.setEnabled(false);
        }

        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            if (preference.getKey().equals("screen_enhance_engine_s2h_enable")) {
                if (ScreenEnhanceEngineStatusCheck.setS2hStatus(this.context, booleanValue)) {
                    this.mEnableS2hPref.setChecked(booleanValue);
                    return true;
                }
                this.mEnableS2hPref.setChecked(!booleanValue);
                StringBuilder sb = new StringBuilder();
                sb.append("Screen Enhance SDR to HDR turn ");
                sb.append(booleanValue ? "on" : "off");
                sb.append(" failed!");
                Log.e("ScreenEnhanceS2H", sb.toString());
                return true;
            }
            return true;
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onStart() {
            super.onStart();
            this.mEnableS2hPref.setChecked(ScreenEnhanceEngineStatusCheck.getS2hStatus(this.context));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle(R.string.screen_enhance_engine_s2h_title);
    }
}
