package com.android.settings.inputmethod;

import android.content.Context;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.widget.RadioButtonPreference;
import java.util.ArrayList;

/* loaded from: classes.dex */
public class InputMethodCloudPasteModeManager extends SettingsPreferenceFragment {
    private Context mContext;
    private InputMethodCloudPastePreference mInputMethodCloudPastePreference;
    private int mSelectItem;
    private ArrayList<String> sCloudQuickPasteKeyList;
    private ArrayList<Integer> sCloudQuickPasteSelectDesList;
    private ArrayList<Integer> sCloudQuickPasteSelectTitleList;

    private void addPreferences() {
        if (this.sCloudQuickPasteSelectTitleList == null || this.sCloudQuickPasteSelectDesList == null) {
            return;
        }
        this.mInputMethodCloudPastePreference = new InputMethodCloudPastePreference(this.mContext);
        getPreferenceScreen().addPreference(this.mInputMethodCloudPastePreference);
        int i = 0;
        while (i < this.sCloudQuickPasteSelectTitleList.size()) {
            RadioButtonPreference radioButtonPreference = new RadioButtonPreference(this.mContext);
            radioButtonPreference.setKey(this.sCloudQuickPasteKeyList.get(i));
            radioButtonPreference.setTitle(this.sCloudQuickPasteSelectTitleList.get(i).intValue());
            radioButtonPreference.setSummary(this.sCloudQuickPasteSelectDesList.get(i).intValue());
            radioButtonPreference.setLayoutResource(R.layout.miuix_preference_radiobutton_two_state_background);
            getPreferenceScreen().addPreference(radioButtonPreference);
            radioButtonPreference.setChecked(i == this.mSelectItem);
            i++;
        }
    }

    private void setSelect() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int preferenceCount = preferenceScreen.getPreferenceCount();
        int i = 1;
        while (i < preferenceCount) {
            ((RadioButtonPreference) preferenceScreen.getPreference(i)).setChecked(this.mSelectItem + 1 == i);
            i++;
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        this.sCloudQuickPasteSelectTitleList = InputMethodFunctionSelectUtils.getCloudQuickPasteTitleList();
        this.sCloudQuickPasteSelectDesList = InputMethodFunctionSelectUtils.getCloudQuickPasteDesList();
        this.sCloudQuickPasteKeyList = InputMethodFunctionSelectUtils.getCloudQuickPasteKeyList();
        addPreferencesFromResource(R.xml.input_method_cloud_paste);
        addPreferences();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        if (this.sCloudQuickPasteKeyList.contains(key)) {
            this.mSelectItem = this.sCloudQuickPasteKeyList.indexOf(key);
            setSelect();
            this.mInputMethodCloudPastePreference.setImageShow(this.mSelectItem);
            InputMethodFunctionSelectUtils.setPreferenceCheckedValue(this.mContext, "input_method_cloud_clipboard_quick_paste_mode", this.mSelectItem);
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mSelectItem = InputMethodFunctionSelectUtils.getCloudClipboardQuickPasteMode(this.mContext);
        setSelect();
    }
}
