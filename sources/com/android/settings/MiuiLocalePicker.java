package com.android.settings;

import android.app.Activity;
import android.app.ActivityManagerNative;
import android.app.Dialog;
import android.app.IActivityManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.LocaleList;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.app.LocaleHelper;
import com.android.internal.app.LocalePicker;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.LocaleRadioButtonPreference;
import java.util.ArrayList;
import java.util.Locale;
import miui.os.Build;
import miuix.appcompat.app.ActionBar;

/* loaded from: classes.dex */
public class MiuiLocalePicker extends SettingsPreferenceFragment implements LocalePicker.LocaleSelectionListener {
    private ActionBar mActionBar;
    private ArrayAdapter<LocalePicker.LocaleInfo> mAdapter;
    private SettingsPreferenceFragment.SettingsDialogFragment mDialogFragment;
    private ArrayList<LocalePicker.LocaleInfo> mLanguageList;
    private RecyclerView mListView;
    LocalePicker.LocaleSelectionListener mListener;
    private RecyclerView.OnScrollListener mOnListScrollListener;
    private ArrayList<LocalePicker.LocaleInfo> mOriginLanguageList;
    private int mSavedSoftInputMode;
    private String mSearchText;
    private EditText mSearchView;
    private String mSelectedLanguage;
    private Locale mTargetLocale;

    public MiuiLocalePicker() {
        setLocaleSelectionListener(this);
    }

    private void addPreferences(ArrayList<LocalePicker.LocaleInfo> arrayList) {
        if (arrayList == null || arrayList.size() == 0) {
            return;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            LocalePicker.LocaleInfo localeInfo = arrayList.get(i);
            if (localeInfo != null) {
                LocaleRadioButtonPreference localeRadioButtonPreference = new LocaleRadioButtonPreference(getPrefContext());
                boolean equals = localeInfo.getLocale().toString().equals(this.mSelectedLanguage);
                localeRadioButtonPreference.setLocaleInfo(localeInfo);
                localeRadioButtonPreference.setKey(localeInfo.getLocale().toString());
                localeRadioButtonPreference.setTitle(localeInfo.getLabel());
                localeRadioButtonPreference.setLayoutResource(R.layout.miuix_preference_radiobutton_two_state_background);
                getPreferenceScreen().addPreference(localeRadioButtonPreference);
                localeRadioButtonPreference.setChecked(equals);
            }
        }
    }

    private ArrayList<LocalePicker.LocaleInfo> constructLanguageList(ArrayAdapter<LocalePicker.LocaleInfo> arrayAdapter) {
        ArrayList<LocalePicker.LocaleInfo> arrayList = new ArrayList<>();
        for (int i = 0; i < arrayAdapter.getCount(); i++) {
            LocalePicker.LocaleInfo item = arrayAdapter.getItem(i);
            if (item != null && !LocaleList.isPseudoLocale(item.getLocale())) {
                arrayList.add(item);
            }
        }
        return arrayList;
    }

    private ArrayList<LocalePicker.LocaleInfo> filterByNativeAndUiNames(CharSequence charSequence) {
        ArrayList<LocalePicker.LocaleInfo> arrayList = this.mOriginLanguageList;
        if (arrayList != null && arrayList.size() != 0) {
            ArrayList<LocalePicker.LocaleInfo> arrayList2 = this.mOriginLanguageList;
            if (charSequence != null && charSequence.length() != 0) {
                Locale locale = Locale.getDefault();
                String normalizeForSearch = LocaleHelper.normalizeForSearch(charSequence.toString(), locale);
                int size = arrayList2.size();
                ArrayList<LocalePicker.LocaleInfo> arrayList3 = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    LocalePicker.LocaleInfo localeInfo = arrayList2.get(i);
                    Locale locale2 = localeInfo.getLocale();
                    String normalizeForSearch2 = LocaleHelper.normalizeForSearch(LocaleHelper.getDisplayName(locale2, true), locale);
                    if (wordMatches(LocaleHelper.normalizeForSearch(LocaleHelper.getDisplayName(locale2, locale2, true), locale), normalizeForSearch) || wordMatches(normalizeForSearch2, normalizeForSearch)) {
                        arrayList3.add(localeInfo);
                    }
                }
                this.mLanguageList = arrayList3;
                return arrayList3;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideSoftKeyboard() {
        if (getActivity() == null) {
            return;
        }
        ((InputMethodManager) getActivity().getSystemService("input_method")).hideSoftInputFromWindow(this.mListView.getWindowToken(), 0);
    }

    private void initSearchBox() {
        if (Build.IS_INTERNATIONAL_BUILD) {
            ActionBar appCompatActionBar = getAppCompatActionBar();
            this.mActionBar = appCompatActionBar;
            appCompatActionBar.setDisplayShowCustomEnabled(true);
            this.mActionBar.setDisplayShowTitleEnabled(false);
            this.mActionBar.setCustomView(R.layout.localePicker_search_titlebar);
            View customView = this.mActionBar.getCustomView();
            EditText editText = (EditText) customView.findViewById(16908297);
            this.mSearchView = editText;
            editText.setContentDescription(getContext().getResources().getString(R.string.camera_key_action_shortcut_search));
            this.mSearchView.addTextChangedListener(new TextWatcher() { // from class: com.android.settings.MiuiLocalePicker.1
                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable editable) {
                }

                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    String charSequence2 = charSequence.toString();
                    if (TextUtils.isEmpty(charSequence2) || !charSequence2.equals(MiuiLocalePicker.this.mSearchText)) {
                        MiuiLocalePicker.this.mSearchText = charSequence2;
                        MiuiLocalePicker miuiLocalePicker = MiuiLocalePicker.this;
                        miuiLocalePicker.onQueryTextChange(miuiLocalePicker.mSearchText);
                        if (MiuiLocalePicker.this.mSearchView != null) {
                            MiuiLocalePicker.this.mSearchView.setContentDescription(TextUtils.isEmpty(MiuiLocalePicker.this.mSearchText) ? MiuiLocalePicker.this.getContext().getResources().getString(R.string.camera_key_action_shortcut_search) : MiuiLocalePicker.this.mSearchText);
                        }
                    }
                }
            });
            View findViewById = customView.findViewById(16908332);
            findViewById.setVisibility(0);
            findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.MiuiLocalePicker.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    MiuiLocalePicker.this.finish();
                }
            });
            this.mListView = getListView();
            this.mOnListScrollListener = new RecyclerView.OnScrollListener() { // from class: com.android.settings.MiuiLocalePicker.3
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                    super.onScrollStateChanged(recyclerView, i);
                    if (i == 1) {
                        MiuiLocalePicker.this.hideSoftKeyboard();
                    }
                }
            };
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onQueryTextChange(String str) {
        if (!TextUtils.isEmpty(str)) {
            getPreferenceScreen().removeAll();
            addPreferences(filterByNativeAndUiNames(str));
        } else if (this.mLanguageList.size() != this.mOriginLanguageList.size()) {
            this.mLanguageList = this.mOriginLanguageList;
            getPreferenceScreen().removeAll();
            addPreferences(this.mLanguageList);
        }
    }

    private void restoreSelectedLanguage(Bundle bundle) {
        String str;
        if (bundle == null || !bundle.containsKey("save_selected_language")) {
            IActivityManager iActivityManager = ActivityManagerNative.getDefault();
            String locale = this.mAdapter.getItem(0).getLocale().toString();
            try {
                str = iActivityManager.getConfiguration().locale.toString();
            } catch (RemoteException unused) {
                str = locale;
            }
        } else {
            str = bundle.getString("save_selected_language");
        }
        this.mSelectedLanguage = str;
    }

    private void setSelect(String str) {
        LocalePicker.LocaleInfo localeInfo;
        ArrayList<LocalePicker.LocaleInfo> arrayList = this.mLanguageList;
        int size = arrayList != null ? arrayList.size() : 0;
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen.getPreferenceCount() != size) {
            return;
        }
        for (int i = 0; i < size; i++) {
            LocaleRadioButtonPreference localeRadioButtonPreference = (LocaleRadioButtonPreference) preferenceScreen.getPreference(i);
            boolean equals = str.equals(localeRadioButtonPreference.getKey());
            localeRadioButtonPreference.setChecked(equals);
            if (equals && (localeInfo = localeRadioButtonPreference.getLocaleInfo()) != null) {
                onLocaleSelected(localeInfo.getLocale());
            }
        }
    }

    private boolean wordMatches(String str, String str2) {
        if (str.startsWith(str2)) {
            return true;
        }
        for (String str3 : str.split(" ")) {
            if (str3.startsWith(str2)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        this.mSavedSoftInputMode = activity.getWindow().getAttributes().softInputMode;
        super.onAttach(activity);
        activity.getWindow().setSoftInputMode(48);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null || !bundle.containsKey("locale")) {
            return;
        }
        this.mTargetLocale = (Locale) bundle.getSerializable("locale");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(final int i) {
        return MiuiUtils.buildGlobalChangeWarningDialog(getActivity(), R.string.global_locale_change_title, new Runnable() { // from class: com.android.settings.MiuiLocalePicker.6
            @Override // java.lang.Runnable
            public void run() {
                MiuiLocalePicker.this.removeDialog(i);
                MiuiLocalePicker.this.getActivity().onBackPressed();
                LocalePicker.updateLocale(MiuiLocalePicker.this.mTargetLocale);
            }
        });
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        ArrayAdapter<LocalePicker.LocaleInfo> constructAdapter = LocalePicker.constructAdapter(getActivity());
        this.mAdapter = constructAdapter;
        this.mLanguageList = constructLanguageList(constructAdapter);
        this.mOriginLanguageList = new ArrayList<>(this.mLanguageList);
        addPreferencesFromResource(R.xml.locale_picker);
        restoreSelectedLanguage(bundle);
        addPreferences(this.mLanguageList);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        if (Build.IS_INTERNATIONAL_BUILD) {
            View inflate = layoutInflater.inflate(R.layout.locale_picker_search_empty, (ViewGroup) null);
            ((ViewGroup) onCreateView).addView(inflate, 0);
            setEmptyView(inflate);
        }
        return onCreateView;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onDetach() {
        getActivity().getWindow().setSoftInputMode(this.mSavedSoftInputMode);
        super.onDetach();
    }

    public void onLocaleSelected(Locale locale) {
        if (Utils.hasMultipleUsers(getActivity())) {
            this.mTargetLocale = locale;
            showDialog(1);
            return;
        }
        getActivity().onBackPressed();
        LocalePicker.updateLocale(locale);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        LocaleRadioButtonPreference localeRadioButtonPreference = (LocaleRadioButtonPreference) preference;
        if (this.mSelectedLanguage == localeRadioButtonPreference.getKey()) {
            localeRadioButtonPreference.setChecked(true);
            return false;
        }
        setSelect(localeRadioButtonPreference.getKey());
        this.mSelectedLanguage = localeRadioButtonPreference.getKey();
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Locale locale = this.mTargetLocale;
        if (locale != null) {
            bundle.putSerializable("locale", locale);
        }
        String str = this.mSelectedLanguage;
        if (str != null) {
            bundle.putString("save_selected_language", str);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        RecyclerView recyclerView;
        super.onStart();
        if (!Build.IS_INTERNATIONAL_BUILD || (recyclerView = this.mListView) == null) {
            return;
        }
        recyclerView.addOnScrollListener(this.mOnListScrollListener);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        RecyclerView recyclerView = this.mListView;
        if (recyclerView == null || !Build.IS_INTERNATIONAL_BUILD) {
            return;
        }
        recyclerView.removeOnScrollListener(this.mOnListScrollListener);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        initSearchBox();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsPreferenceFragment
    public void removeDialog(int i) {
        SettingsPreferenceFragment.SettingsDialogFragment settingsDialogFragment = this.mDialogFragment;
        if (settingsDialogFragment != null && settingsDialogFragment.getDialogId() == i) {
            this.mDialogFragment.dismiss();
        }
        this.mDialogFragment = null;
    }

    public void setLocaleSelectionListener(LocalePicker.LocaleSelectionListener localeSelectionListener) {
        this.mListener = localeSelectionListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsPreferenceFragment
    public void showDialog(int i) {
        if (this.mDialogFragment != null) {
            Log.e("LocalePicker", "Old dialog fragment not null!");
        }
        SettingsPreferenceFragment.SettingsDialogFragment newInstance = SettingsPreferenceFragment.SettingsDialogFragment.newInstance(this, i);
        this.mDialogFragment = newInstance;
        newInstance.mOnDismissListener = new DialogInterface.OnDismissListener() { // from class: com.android.settings.MiuiLocalePicker.4
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                if (MiuiLocalePicker.this.getActivity() != null) {
                    MiuiLocalePicker.this.getActivity().finish();
                }
            }
        };
        this.mDialogFragment.mOnCancelListener = new DialogInterface.OnCancelListener() { // from class: com.android.settings.MiuiLocalePicker.5
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialogInterface) {
                if (MiuiLocalePicker.this.getActivity() != null) {
                    MiuiLocalePicker.this.getActivity().finish();
                }
            }
        };
        this.mDialogFragment.show(getActivity().getSupportFragmentManager(), Integer.toString(i));
    }
}
