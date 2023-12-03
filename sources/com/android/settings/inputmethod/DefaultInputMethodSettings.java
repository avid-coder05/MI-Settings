package com.android.settings.inputmethod;

import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import java.util.List;

/* loaded from: classes.dex */
public class DefaultInputMethodSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private String mCurrentIme;
    private List<InputMethodInfo> mImis;
    private InputMethodManager mImm;
    private PreferenceScreen mInputMethodsPreference;
    private MenuItem mOkItem;

    private void updatePreference() {
        if (getActivity() == null || this.mImm == null) {
            return;
        }
        this.mInputMethodsPreference.removeAll();
        if (this.mInputMethodsPreference != null) {
            for (InputMethodInfo inputMethodInfo : this.mImis) {
                if (!inputMethodInfo.getPackageName().equals("com.android.inputmethod.latin") && !inputMethodInfo.getPackageName().equals("com.google.android.voicesearch")) {
                    CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getPrefContext(), null);
                    checkBoxPreference.setTitle(inputMethodInfo.loadLabel(getPackageManager()));
                    checkBoxPreference.setOnPreferenceChangeListener(this);
                    checkBoxPreference.setKey(inputMethodInfo.getId());
                    checkBoxPreference.setIcon(inputMethodInfo.loadIcon(getPackageManager()));
                    this.mInputMethodsPreference.addPreference(checkBoxPreference);
                    if (inputMethodInfo.getId().equals(this.mCurrentIme)) {
                        checkBoxPreference.setChecked(true);
                        this.mOkItem.setEnabled(true);
                    } else {
                        checkBoxPreference.setChecked(false);
                    }
                }
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return DefaultInputMethodSettings.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.default_input_method_settings);
        setHasOptionsMenu(true);
        this.mInputMethodsPreference = (PreferenceScreen) findPreference("default_input_method");
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService("input_method");
        this.mImm = inputMethodManager;
        this.mImis = inputMethodManager.getInputMethodList();
        updatePreference();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuItem add = menu.add(0, 1, 0, 17039370);
        this.mOkItem = add;
        add.setShowAsAction(6);
        this.mOkItem.setEnabled(false);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        Settings.Secure.putInt(getActivity().getContentResolver(), "default_input_method_choosed", 1);
        super.onDestroy();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 1) {
            return super.onOptionsItemSelected(menuItem);
        }
        Settings.Secure.putInt(getActivity().getContentResolver(), "default_input_method_choosed", 1);
        Settings.Secure.putString(getActivity().getContentResolver(), "default_input_method", this.mCurrentIme);
        finish();
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        this.mCurrentIme = preference.getKey();
        updatePreference();
        return true;
    }
}
