package com.android.settings.inputmethod;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.MiuiValuePreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import miui.os.Build;

/* loaded from: classes.dex */
public class InputMethodClipboardSettingsFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private MiuiValuePreference mCloudQuickPastePref;
    private Context mContext;
    private CheckBoxPreference mQuickPasteCloudPref;
    private CheckBoxPreference mQuickPastePref;
    private CheckBoxPreference mQuickPasteTaoBaoPref;
    private CheckBoxPreference mQuickPasteUrlPref;

    private void initQuickPastePreference() {
        if (Build.IS_DEVELOPMENT_VERSION) {
            boolean clipboardQuickPasteEnable = InputMethodFunctionSelectUtils.getClipboardQuickPasteEnable(this.mContext);
            this.mQuickPastePref.setChecked(clipboardQuickPasteEnable);
            this.mQuickPastePref.setOnPreferenceChangeListener(this);
            this.mQuickPasteUrlPref.setChecked(InputMethodFunctionSelectUtils.getClipboardQuickPasteUrlEnable(this.mContext));
            this.mQuickPasteUrlPref.setEnabled(clipboardQuickPasteEnable);
            this.mQuickPasteUrlPref.setOnPreferenceChangeListener(this);
            this.mQuickPasteTaoBaoPref.setChecked(InputMethodFunctionSelectUtils.getClipboardQuickPasteTaobaoEnable(this.mContext));
            this.mQuickPasteTaoBaoPref.setEnabled(clipboardQuickPasteEnable);
            this.mQuickPasteTaoBaoPref.setOnPreferenceChangeListener(this);
        }
        boolean cloudQuickPasteEnable = InputMethodFunctionSelectUtils.getCloudQuickPasteEnable(this.mContext);
        this.mCloudQuickPastePref.setEnabled(cloudQuickPasteEnable);
        this.mQuickPasteCloudPref.setChecked(cloudQuickPasteEnable);
        this.mQuickPasteCloudPref.setOnPreferenceChangeListener(this);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.input_method_clipboard_settings);
        this.mQuickPastePref = (CheckBoxPreference) findPreference("quick_paste");
        this.mQuickPasteUrlPref = (CheckBoxPreference) findPreference("quick_paste_url");
        this.mQuickPasteTaoBaoPref = (CheckBoxPreference) findPreference("quick_paste_taobao");
        this.mQuickPasteCloudPref = (CheckBoxPreference) findPreference("quick_paste_cloud");
        this.mCloudQuickPastePref = (MiuiValuePreference) findPreference("cloud_paste_mode");
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("quick_access");
        if (Build.IS_DEVELOPMENT_VERSION) {
            return;
        }
        getPreferenceScreen().removePreference(this.mQuickPastePref);
        getPreferenceScreen().removePreference(preferenceCategory);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mQuickPastePref) {
            Log.d("InputMethodClipSettings", "quick_paste click");
            Boolean bool = (Boolean) obj;
            InputMethodFunctionSelectUtils.setPreferenceCheckedValue(this.mContext, "enable_miui_quick_paste", bool.booleanValue() ? 1 : 0);
            this.mQuickPasteUrlPref.setEnabled(bool.booleanValue());
            this.mQuickPasteTaoBaoPref.setEnabled(bool.booleanValue());
            return true;
        } else if (preference == this.mQuickPasteUrlPref) {
            Log.d("InputMethodClipSettings", "quick_paste_url click");
            InputMethodFunctionSelectUtils.setPreferenceCheckedValue(this.mContext, "enable_quick_paste_url", ((Boolean) obj).booleanValue() ? 1 : 0);
            return true;
        } else if (preference == this.mQuickPasteTaoBaoPref) {
            Log.d("InputMethodClipSettings", "quick_paste_taobao click");
            InputMethodFunctionSelectUtils.setPreferenceCheckedValue(this.mContext, "enable_quick_paste_taobao", ((Boolean) obj).booleanValue() ? 1 : 0);
            return true;
        } else if (preference == this.mQuickPasteCloudPref) {
            Log.d("InputMethodClipSettings", "quick_paste_cloud click");
            Boolean bool2 = (Boolean) obj;
            InputMethodFunctionSelectUtils.setPreferenceCheckedValue(this.mContext, "enable_quick_paste_cloud", bool2.booleanValue() ? 1 : 0);
            this.mCloudQuickPastePref.setEnabled(bool2.booleanValue());
            return true;
        } else {
            Log.e("InputMethodClipSettings", "error preference : " + preference);
            return true;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        initQuickPastePreference();
        this.mCloudQuickPastePref.setSummary(InputMethodFunctionSelectUtils.getCloudQuickPasteTitleList().get(InputMethodFunctionSelectUtils.getCloudClipboardQuickPasteMode(this.mContext)).intValue());
    }
}
