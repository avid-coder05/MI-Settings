package com.android.settings.inputmethod;

import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.textservice.SpellCheckerInfo;
import android.view.textservice.SpellCheckerSubtype;
import android.view.textservice.TextServicesManager;
import android.widget.Switch;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class SpellCheckersSettings extends SettingsPreferenceFragment implements OnMainSwitchChangeListener, Preference.OnPreferenceChangeListener {
    private static final String TAG = SpellCheckersSettings.class.getSimpleName();
    private SpellCheckerInfo mCurrentSci;
    private AlertDialog mDialog = null;
    private SpellCheckerInfo[] mEnabledScis;
    private Preference mSpellCheckerLanaguagePref;
    private CheckBoxPreference mSwitch;
    private TextServicesManager mTsm;

    /* JADX INFO: Access modifiers changed from: private */
    public void changeCurrentSpellChecker(SpellCheckerInfo spellCheckerInfo) {
        Settings.Secure.putString(getContentResolver(), "selected_spell_checker", spellCheckerInfo.getId());
        Settings.Secure.putInt(getContentResolver(), "selected_spell_checker_subtype", 0);
        updatePreferenceScreen();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int convertDialogItemIdToSubtypeIndex(int i) {
        return i - 1;
    }

    private static int convertSubtypeIndexToDialogItemId(int i) {
        return i + 1;
    }

    private CharSequence getSpellCheckerSubtypeLabel(SpellCheckerInfo spellCheckerInfo, SpellCheckerSubtype spellCheckerSubtype) {
        return spellCheckerInfo == null ? getString(R.string.spell_checker_not_selected) : spellCheckerSubtype == null ? getString(R.string.use_system_language_to_select_input_method_subtypes) : spellCheckerSubtype.getDisplayName(getActivity(), spellCheckerInfo.getPackageName(), spellCheckerInfo.getServiceInfo().applicationInfo);
    }

    private void populatePreferenceScreen() {
        SpellCheckerPreference spellCheckerPreference = new SpellCheckerPreference(getPrefContext(), this.mEnabledScis);
        spellCheckerPreference.setTitle(R.string.default_spell_checker);
        SpellCheckerInfo[] spellCheckerInfoArr = this.mEnabledScis;
        if ((spellCheckerInfoArr == null ? 0 : spellCheckerInfoArr.length) > 0) {
            spellCheckerPreference.setSummary("%s");
        } else {
            spellCheckerPreference.setSummary(R.string.spell_checker_not_selected);
        }
        spellCheckerPreference.setKey("default_spellchecker");
        spellCheckerPreference.setOnPreferenceChangeListener(this);
        getPreferenceScreen().addPreference(spellCheckerPreference);
    }

    private void showChooseLanguageDialog() {
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mDialog.dismiss();
        }
        final SpellCheckerInfo currentSpellChecker = this.mTsm.getCurrentSpellChecker();
        if (currentSpellChecker == null) {
            return;
        }
        SpellCheckerSubtype currentSpellCheckerSubtype = this.mTsm.getCurrentSpellCheckerSubtype(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.phone_language);
        int subtypeCount = currentSpellChecker.getSubtypeCount();
        CharSequence[] charSequenceArr = new CharSequence[subtypeCount + 1];
        charSequenceArr[0] = getSpellCheckerSubtypeLabel(currentSpellChecker, null);
        int i = 0;
        for (int i2 = 0; i2 < subtypeCount; i2++) {
            SpellCheckerSubtype subtypeAt = currentSpellChecker.getSubtypeAt(i2);
            int convertSubtypeIndexToDialogItemId = convertSubtypeIndexToDialogItemId(i2);
            charSequenceArr[convertSubtypeIndexToDialogItemId] = getSpellCheckerSubtypeLabel(currentSpellChecker, subtypeAt);
            if (subtypeAt.equals(currentSpellCheckerSubtype)) {
                i = convertSubtypeIndexToDialogItemId;
            }
        }
        builder.setSingleChoiceItems(charSequenceArr, i, new DialogInterface.OnClickListener() { // from class: com.android.settings.inputmethod.SpellCheckersSettings.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i3) {
                int hashCode;
                if (i3 == 0) {
                    hashCode = 0;
                } else {
                    hashCode = currentSpellChecker.getSubtypeAt(SpellCheckersSettings.convertDialogItemIdToSubtypeIndex(i3)).hashCode();
                }
                Settings.Secure.putInt(SpellCheckersSettings.this.getContentResolver(), "selected_spell_checker_subtype", hashCode);
                dialogInterface.dismiss();
                SpellCheckersSettings.this.updatePreferenceScreen();
            }
        });
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.show();
    }

    private void showSecurityWarnDialog(final SpellCheckerInfo spellCheckerInfo) {
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(17039380);
        builder.setMessage(getString(R.string.spellchecker_security_warning, spellCheckerInfo.loadLabel(getPackageManager())));
        builder.setCancelable(true);
        builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.inputmethod.SpellCheckersSettings.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                SpellCheckersSettings.this.changeCurrentSpellChecker(spellCheckerInfo);
            }
        });
        builder.setNegativeButton(17039360, new DialogInterface.OnClickListener() { // from class: com.android.settings.inputmethod.SpellCheckersSettings.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePreferenceScreen() {
        SpellCheckerInfo currentSpellChecker = this.mTsm.getCurrentSpellChecker();
        this.mCurrentSci = currentSpellChecker;
        if (currentSpellChecker == null) {
            return;
        }
        boolean isSpellCheckerEnabled = this.mTsm.isSpellCheckerEnabled();
        this.mSwitch.setChecked(isSpellCheckerEnabled);
        boolean z = false;
        this.mSpellCheckerLanaguagePref.setSummary(getSpellCheckerSubtypeLabel(this.mCurrentSci, this.mCurrentSci != null ? this.mTsm.getCurrentSpellCheckerSubtype(false) : null));
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int preferenceCount = preferenceScreen.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = preferenceScreen.getPreference(i);
            if (preference != this.mSwitch) {
                preference.setEnabled(isSpellCheckerEnabled);
            }
            if (preference instanceof SpellCheckerPreference) {
                ((SpellCheckerPreference) preference).setSelected(this.mCurrentSci);
            }
        }
        Preference preference2 = this.mSpellCheckerLanaguagePref;
        if (isSpellCheckerEnabled && this.mCurrentSci != null) {
            z = true;
        }
        preference2.setEnabled(z);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 59;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.spellchecker_prefs);
        this.mSpellCheckerLanaguagePref = findPreference("spellchecker_language");
        TextServicesManager textServicesManager = (TextServicesManager) getSystemService("textservices");
        this.mTsm = textServicesManager;
        this.mCurrentSci = textServicesManager.getCurrentSpellChecker();
        this.mEnabledScis = this.mTsm.getEnabledSpellCheckers();
        populatePreferenceScreen();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mSwitch && (obj instanceof Boolean)) {
            Settings.Secure.putInt(getContentResolver(), "spell_checker_enabled", ((Boolean) obj).booleanValue() ? 1 : 0);
            updatePreferenceScreen();
            return true;
        }
        SpellCheckerInfo spellCheckerInfo = (SpellCheckerInfo) obj;
        ServiceInfo serviceInfo = spellCheckerInfo != null ? spellCheckerInfo.getServiceInfo() : null;
        ApplicationInfo applicationInfo = serviceInfo != null ? serviceInfo.applicationInfo : null;
        if (((applicationInfo == null || (applicationInfo.flags & 1) == 0) ? null : 1) != null) {
            changeCurrentSpellChecker(spellCheckerInfo);
            return true;
        }
        showSecurityWarnDialog(spellCheckerInfo);
        return false;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if ("spellchecker_language".equals(preference.getKey())) {
            writePreferenceClickMetric(preference);
            showChooseLanguageDialog();
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("spellchecker_switch");
        this.mSwitch = checkBoxPreference;
        checkBoxPreference.setOnPreferenceChangeListener(this);
        updatePreferenceScreen();
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r2, boolean z) {
        Settings.Secure.putInt(getContentResolver(), "spell_checker_enabled", z ? 1 : 0);
        updatePreferenceScreen();
    }
}
