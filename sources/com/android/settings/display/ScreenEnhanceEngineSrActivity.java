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
public class ScreenEnhanceEngineSrActivity extends SubSettings {

    /* loaded from: classes.dex */
    public static class ScreenEnhanceEngineSrFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
        private Context context;
        private CustomCheckBoxPreference mEnableSrForImagePref;
        private CustomCheckBoxPreference mEnableSrForVideoPref;
        private ScreenEnhanceEngineNotePreference noteInfoListPref;
        private ScreenEnhanceEngineNotePreference noteInfoPref;
        private ScreenEnhanceEngineTopPreference topViewPref;

        private <T extends Preference> T findPreferenceImpl(String str) {
            return (T) super.findPreference(str);
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            addPreferencesFromResource(R.xml.screen_enhance_engine_sr_settings);
            this.context = getActivity();
            this.topViewPref = (ScreenEnhanceEngineTopPreference) findPreferenceImpl("screen_enhance_top_view");
            this.noteInfoPref = (ScreenEnhanceEngineNotePreference) findPreferenceImpl("screen_enhance_note_info");
            this.noteInfoListPref = (ScreenEnhanceEngineNotePreference) findPreferenceImpl("screen_enhance_note_info_list");
            CustomCheckBoxPreference customCheckBoxPreference = (CustomCheckBoxPreference) findPreferenceImpl("screen_enhance_engine_sr_for_video");
            this.mEnableSrForVideoPref = customCheckBoxPreference;
            customCheckBoxPreference.setOnPreferenceChangeListener(this);
            if (!ScreenEnhanceEngineStatusCheck.isSrForVideoSupport()) {
                this.mEnableSrForVideoPref.setVisible(false);
            }
            CustomCheckBoxPreference customCheckBoxPreference2 = (CustomCheckBoxPreference) findPreferenceImpl("screen_enhance_engine_sr_for_image");
            this.mEnableSrForImagePref = customCheckBoxPreference2;
            customCheckBoxPreference2.setOnPreferenceChangeListener(this);
            if (!ScreenEnhanceEngineStatusCheck.isSrForImageSupport()) {
                this.mEnableSrForImagePref.setVisible(false);
            }
            this.topViewPref.addVideoView(R.raw.screen_enhance_engine_sr_video);
            this.topViewPref.setEnabled(false);
            this.noteInfoPref.setNoteInfo(this.context.getString(R.string.screen_enhance_engine_sr_pic_summary_video_only));
            this.noteInfoPref.setEnabled(false);
            this.noteInfoListPref.setNoteInfo(this.context.getString(R.string.screen_enhance_engine_sr_note));
            this.noteInfoListPref.setEnabled(false);
        }

        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            if (preference.getKey().equals("screen_enhance_engine_sr_for_video")) {
                if (ScreenEnhanceEngineStatusCheck.setSrForVideoStatus(this.context, booleanValue)) {
                    this.mEnableSrForVideoPref.setChecked(booleanValue);
                    return true;
                }
                this.mEnableSrForVideoPref.setChecked(!booleanValue);
                StringBuilder sb = new StringBuilder();
                sb.append("Screen Enhance SR for video turn ");
                sb.append(booleanValue ? "on" : "off");
                sb.append(" failed!");
                Log.e("ScreenEnhanceSr", sb.toString());
                return true;
            } else if (preference.getKey().equals("screen_enhance_engine_sr_for_image")) {
                if (ScreenEnhanceEngineStatusCheck.setSrForImageStatus(this.context, booleanValue)) {
                    this.mEnableSrForImagePref.setChecked(booleanValue);
                    return true;
                }
                this.mEnableSrForImagePref.setChecked(!booleanValue);
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Screen Enhance SR for image turn ");
                sb2.append(booleanValue ? "on" : "off");
                sb2.append(" failed!");
                Log.e("ScreenEnhanceSr", sb2.toString());
                return true;
            } else {
                return true;
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onStart() {
            super.onStart();
            if (ScreenEnhanceEngineStatusCheck.isSrForVideoSupport()) {
                this.mEnableSrForVideoPref.setChecked(ScreenEnhanceEngineStatusCheck.getSrForVideoStatus(this.context));
            }
            if (ScreenEnhanceEngineStatusCheck.isSrForImageSupport()) {
                this.mEnableSrForImagePref.setChecked(ScreenEnhanceEngineStatusCheck.getSrForImageStatus(this.context));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setTitle(R.string.screen_enhance_engine_sr_title);
    }
}
