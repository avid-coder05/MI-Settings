package com.android.settings.inputmethod;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.inputmethod.InputMethodAndSubtypeUtil;
import com.android.settingslib.inputmethod.InputMethodAndSubtypeUtilCompat;
import com.android.settingslib.inputmethod.InputMethodPreference;
import com.android.settingslib.inputmethod.InputMethodSettingValuesWrapper;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import miui.os.Build;

/* loaded from: classes.dex */
public final class AvailableVirtualKeyboardFragment extends SettingsPreferenceFragment implements InputMethodPreference.OnSavePreferenceListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.inputmethod.AvailableVirtualKeyboardFragment.1
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = R.xml.available_virtual_keyboard;
            arrayList.add(searchIndexableResource);
            return arrayList;
        }
    };
    private Context mContext;
    private DevicePolicyManager mDpm;
    private List<InputMethodInfo> mEnabledList;
    private InputMethodManager mImm;
    private InputMethodSettingValuesWrapper mInputMethodSettingValues;
    private final ArrayList<InputMethodPreference> mInputMethodPreferenceList = new ArrayList<>();
    private final ArrayList<Preference> mAllInputMethodPreferenceList = new ArrayList<>();
    private List<String> mCustomizedInputMethod = Arrays.asList("com.sohu.inputmethod.sogou.xiaomi", "com.baidu.input_mi", "com.iflytek.inputmethod.miui");

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$updateInputMethodPreferenceViews$0(Collator collator, InputMethodPreference inputMethodPreference, InputMethodPreference inputMethodPreference2) {
        return inputMethodPreference.compareTo(inputMethodPreference2, collator);
    }

    private void updateInputMethodPreferenceViews() {
        this.mInputMethodSettingValues.refreshAllInputMethodAndSubtypes();
        this.mInputMethodPreferenceList.clear();
        this.mAllInputMethodPreferenceList.clear();
        List permittedInputMethodsForCurrentUser = this.mDpm.getPermittedInputMethodsForCurrentUser();
        Context prefContext = getPrefContext();
        List<InputMethodInfo> inputMethodList = this.mInputMethodSettingValues.getInputMethodList();
        List<InputMethodInfo> enabledInputMethodList = this.mImm.getEnabledInputMethodList();
        int size = inputMethodList == null ? 0 : inputMethodList.size();
        for (int i = 0; i < size; i++) {
            InputMethodInfo inputMethodInfo = inputMethodList.get(i);
            this.mInputMethodPreferenceList.add(new CustomInputMethodPreference(prefContext, inputMethodInfo, true, permittedInputMethodsForCurrentUser == null || permittedInputMethodsForCurrentUser.contains(inputMethodInfo.getPackageName()) || enabledInputMethodList.contains(inputMethodInfo), this));
        }
        final Collator collator = Collator.getInstance();
        this.mInputMethodPreferenceList.sort(new Comparator() { // from class: com.android.settings.inputmethod.AvailableVirtualKeyboardFragment$$ExternalSyntheticLambda0
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int lambda$updateInputMethodPreferenceViews$0;
                lambda$updateInputMethodPreferenceViews$0 = AvailableVirtualKeyboardFragment.lambda$updateInputMethodPreferenceViews$0(collator, (InputMethodPreference) obj, (InputMethodPreference) obj2);
                return lambda$updateInputMethodPreferenceViews$0;
            }
        });
        PreferenceCategory preferenceCategory = new PreferenceCategory(prefContext);
        PreferenceCategory preferenceCategory2 = new PreferenceCategory(prefContext);
        if (!Build.IS_GLOBAL_BUILD) {
            preferenceCategory.setTitle(this.mContext.getResources().getString(R.string.xiaomi_custom_input_method));
            preferenceCategory2.setTitle(this.mContext.getResources().getString(R.string.other_input_method));
        }
        getPreferenceScreen().removeAll();
        getPreferenceScreen().addPreference(preferenceCategory);
        getPreferenceScreen().addPreference(preferenceCategory2);
        for (int i2 = 0; i2 < size; i2++) {
            CustomInputMethodPreference customInputMethodPreference = (CustomInputMethodPreference) this.mInputMethodPreferenceList.get(i2);
            if (this.mCustomizedInputMethod.contains(this.mInputMethodPreferenceList.get(i2).getInputMethodInfo().getPackageName())) {
                preferenceCategory.addPreference(customInputMethodPreference);
            } else {
                preferenceCategory2.addPreference(customInputMethodPreference);
            }
            this.mAllInputMethodPreferenceList.add(customInputMethodPreference);
            if (this.mEnabledList.contains(customInputMethodPreference.getInputMethodInfo()) && this.mEnabledList.size() == 1) {
                customInputMethodPreference.setEnableModeText(false);
            } else {
                customInputMethodPreference.setEnableModeText(true);
            }
            InputMethodAndSubtypeUtil.removeUnnecessaryNonPersistentPreference(customInputMethodPreference);
            customInputMethodPreference.updatePreferenceViews();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 347;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        addPreferencesFromResource(R.xml.available_virtual_keyboard);
        FragmentActivity activity = getActivity();
        this.mInputMethodSettingValues = InputMethodSettingValuesWrapper.getInstance(activity);
        this.mImm = (InputMethodManager) activity.getSystemService(InputMethodManager.class);
        this.mDpm = (DevicePolicyManager) activity.getSystemService(DevicePolicyManager.class);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mEnabledList = this.mImm.getEnabledInputMethodList();
        this.mInputMethodSettingValues.refreshAllInputMethodAndSubtypes();
        updateInputMethodPreferenceViews();
    }

    @Override // com.android.settingslib.inputmethod.InputMethodPreference.OnSavePreferenceListener
    public void onSaveInputMethodPreference(InputMethodPreference inputMethodPreference) {
        InputMethodAndSubtypeUtilCompat.saveInputMethodSubtypeList(this, getContentResolver(), this.mImm.getInputMethodList(), getResources().getConfiguration().keyboard == 2);
        this.mInputMethodSettingValues.refreshAllInputMethodAndSubtypes();
        Iterator<InputMethodPreference> it = this.mInputMethodPreferenceList.iterator();
        while (it.hasNext()) {
            it.next().updatePreferenceViews();
        }
        if (this.mInputMethodPreferenceList.contains(inputMethodPreference)) {
            this.mAllInputMethodPreferenceList.get(this.mInputMethodPreferenceList.indexOf(inputMethodPreference)).setEnabled(inputMethodPreference.isChecked());
        }
        this.mEnabledList = this.mImm.getEnabledInputMethodList();
        for (int i = 0; i < this.mAllInputMethodPreferenceList.size(); i++) {
            CustomInputMethodPreference customInputMethodPreference = (CustomInputMethodPreference) this.mAllInputMethodPreferenceList.get(i);
            if (this.mEnabledList.contains(customInputMethodPreference.getInputMethodInfo()) && this.mEnabledList.size() == 1) {
                customInputMethodPreference.setEnableModeText(false);
            } else {
                customInputMethodPreference.setEnableModeText(true);
            }
        }
    }
}
