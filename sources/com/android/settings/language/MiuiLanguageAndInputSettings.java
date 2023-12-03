package com.android.settings.language;

import android.app.ActionBar;
import android.app.AppGlobals;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.database.ContentObserver;
import android.hardware.input.InputDeviceIdentifier;
import android.hardware.input.InputManager;
import android.hardware.input.KeyboardLayout;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemProperties;
import android.provider.MiuiSettings;
import android.provider.SearchIndexableData;
import android.provider.Settings;
import android.speech.tts.TtsEngines;
import android.text.TextUtils;
import android.util.Log;
import android.view.InputDevice;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.view.textservice.SpellCheckerInfo;
import android.view.textservice.TextServicesManager;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import com.android.internal.app.LocalePicker;
import com.android.settings.MiuiUtils;
import com.android.settings.MiuiValuePreference;
import com.android.settings.R;
import com.android.settings.Settings;
import com.android.settings.SubSettings;
import com.android.settings.Utils;
import com.android.settings.applications.defaultapps.DefaultAutofillPreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.inputmethod.DropDownPreferenceAdapter;
import com.android.settings.inputmethod.InputMethodFunctionSelectUtils;
import com.android.settings.inputmethod.KeyboardLayoutDialogFragment;
import com.android.settings.inputmethod.SpellCheckersSettings;
import com.android.settings.inputmethod.UserDictionaryList;
import com.android.settings.inputmethod.UserDictionaryListPreferenceController;
import com.android.settings.inputmethod.UserDictionarySettings;
import com.android.settings.report.InternationalCompat;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.inputmethod.InputMethodAndSubtypeUtil;
import com.android.settingslib.inputmethod.InputMethodPreference;
import com.android.settingslib.inputmethod.InputMethodSettingValuesWrapper;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.android.settingslib.search.SearchIndexableRaw;
import com.miui.enterprise.RestrictionsHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import miui.os.Build;
import miuix.preference.DropDownPreference;

/* loaded from: classes.dex */
public class MiuiLanguageAndInputSettings extends DashboardFragment implements Preference.OnPreferenceChangeListener, InputManager.InputDeviceListener, KeyboardLayoutDialogFragment.OnSetupKeyboardLayoutsListener, InputMethodPreference.OnSavePreferenceListener {
    public static final Indexable$SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.language.MiuiLanguageAndInputSettings.3
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            String string = context.getString(R.string.language_keyboard_settings_title);
            if (context.getAssets().getLocales().length > 1) {
                String localeNames = MiuiLanguageAndInputSettings.getLocaleNames(context);
                SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(context);
                ((SearchIndexableData) searchIndexableRaw).key = "phone_language";
                searchIndexableRaw.title = context.getString(R.string.phone_language);
                searchIndexableRaw.summaryOn = localeNames;
                searchIndexableRaw.summaryOff = localeNames;
                searchIndexableRaw.screenTitle = string;
                arrayList.add(searchIndexableRaw);
            }
            SearchIndexableRaw searchIndexableRaw2 = new SearchIndexableRaw(context);
            ((SearchIndexableData) searchIndexableRaw2).key = "spellcheckers_settings";
            searchIndexableRaw2.title = context.getString(R.string.spellcheckers_settings_title);
            searchIndexableRaw2.screenTitle = string;
            searchIndexableRaw2.keywords = context.getString(R.string.keywords_spell_checker);
            arrayList.add(searchIndexableRaw2);
            if (UserDictionaryListPreferenceController.getUserDictionaryLocalesSet(context) != null) {
                SearchIndexableRaw searchIndexableRaw3 = new SearchIndexableRaw(context);
                ((SearchIndexableData) searchIndexableRaw3).key = "user_dict_settings";
                searchIndexableRaw3.title = context.getString(R.string.user_dict_settings_title);
                searchIndexableRaw3.screenTitle = string;
                arrayList.add(searchIndexableRaw3);
            }
            SearchIndexableRaw searchIndexableRaw4 = new SearchIndexableRaw(context);
            ((SearchIndexableData) searchIndexableRaw4).key = "keyboard_settings";
            searchIndexableRaw4.title = context.getString(R.string.keyboard_settings_category);
            searchIndexableRaw4.screenTitle = string;
            searchIndexableRaw4.keywords = context.getString(R.string.keywords_keyboard_and_ime);
            arrayList.add(searchIndexableRaw4);
            InputMethodSettingValuesWrapper inputMethodSettingValuesWrapper = InputMethodSettingValuesWrapper.getInstance(context);
            inputMethodSettingValuesWrapper.refreshAllInputMethodAndSubtypes();
            String charSequence = inputMethodSettingValuesWrapper.getCurrentInputMethodName(context).toString();
            SearchIndexableRaw searchIndexableRaw5 = new SearchIndexableRaw(context);
            ((SearchIndexableData) searchIndexableRaw5).key = "current_input_method";
            searchIndexableRaw5.title = context.getString(R.string.current_input_method);
            searchIndexableRaw5.summaryOn = charSequence;
            searchIndexableRaw5.summaryOff = charSequence;
            searchIndexableRaw5.screenTitle = string;
            arrayList.add(searchIndexableRaw5);
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService("input_method");
            List<InputMethodInfo> inputMethodList = inputMethodSettingValuesWrapper.getInputMethodList();
            int size = inputMethodList == null ? 0 : inputMethodList.size();
            for (int i = 0; i < size; i++) {
                InputMethodInfo inputMethodInfo = inputMethodList.get(i);
                String subtypeLocaleNameListAsSentence = InputMethodAndSubtypeUtil.getSubtypeLocaleNameListAsSentence(inputMethodManager.getEnabledInputMethodSubtypeList(inputMethodInfo, true), context, inputMethodInfo);
                ServiceInfo serviceInfo = inputMethodInfo.getServiceInfo();
                ComponentName componentName = new ComponentName(serviceInfo.packageName, serviceInfo.name);
                SearchIndexableRaw searchIndexableRaw6 = new SearchIndexableRaw(context);
                ((SearchIndexableData) searchIndexableRaw6).key = componentName.flattenToString();
                searchIndexableRaw6.title = inputMethodInfo.loadLabel(context.getPackageManager()).toString();
                searchIndexableRaw6.summaryOn = subtypeLocaleNameListAsSentence;
                searchIndexableRaw6.summaryOff = subtypeLocaleNameListAsSentence;
                searchIndexableRaw6.screenTitle = string;
                arrayList.add(searchIndexableRaw6);
            }
            InputManager inputManager = (InputManager) context.getSystemService("input");
            boolean z2 = false;
            for (int i2 : InputDevice.getDeviceIds()) {
                InputDevice device = InputDevice.getDevice(i2);
                if (device != null && !device.isVirtual() && device.isFullKeyboard()) {
                    String currentKeyboardLayoutForInputDevice = inputManager.getCurrentKeyboardLayoutForInputDevice(device.getIdentifier());
                    KeyboardLayout keyboardLayout = currentKeyboardLayoutForInputDevice != null ? inputManager.getKeyboardLayout(currentKeyboardLayoutForInputDevice) : null;
                    String keyboardLayout2 = keyboardLayout != null ? keyboardLayout.toString() : context.getString(R.string.keyboard_layout_default_label);
                    SearchIndexableRaw searchIndexableRaw7 = new SearchIndexableRaw(context);
                    ((SearchIndexableData) searchIndexableRaw7).key = device.getName();
                    searchIndexableRaw7.title = device.getName();
                    searchIndexableRaw7.summaryOn = keyboardLayout2;
                    searchIndexableRaw7.summaryOff = keyboardLayout2;
                    searchIndexableRaw7.screenTitle = string;
                    arrayList.add(searchIndexableRaw7);
                    z2 = true;
                }
            }
            if (z2) {
                SearchIndexableRaw searchIndexableRaw8 = new SearchIndexableRaw(context);
                ((SearchIndexableData) searchIndexableRaw8).key = "builtin_keyboard_settings";
                searchIndexableRaw8.title = context.getString(R.string.builtin_keyboard_settings_title);
                searchIndexableRaw8.screenTitle = string;
                arrayList.add(searchIndexableRaw8);
            }
            if (!new TtsEngines(context).getEngines().isEmpty()) {
                SearchIndexableRaw searchIndexableRaw9 = new SearchIndexableRaw(context);
                ((SearchIndexableData) searchIndexableRaw9).key = "tts_settings";
                searchIndexableRaw9.title = context.getString(R.string.tts_settings_title);
                searchIndexableRaw9.screenTitle = string;
                searchIndexableRaw9.keywords = context.getString(R.string.keywords_text_to_speech_output);
                arrayList.add(searchIndexableRaw9);
            }
            SearchIndexableRaw searchIndexableRaw10 = new SearchIndexableRaw(context);
            ((SearchIndexableData) searchIndexableRaw10).key = "pointer_settings_category";
            searchIndexableRaw10.title = context.getString(R.string.pointer_settings_category);
            searchIndexableRaw10.screenTitle = string;
            arrayList.add(searchIndexableRaw10);
            SearchIndexableRaw searchIndexableRaw11 = new SearchIndexableRaw(context);
            ((SearchIndexableData) searchIndexableRaw11).key = "pointer_speed";
            searchIndexableRaw11.title = context.getString(R.string.pointer_speed);
            searchIndexableRaw11.screenTitle = string;
            arrayList.add(searchIndexableRaw11);
            if (MiuiLanguageAndInputSettings.access$500()) {
                SearchIndexableRaw searchIndexableRaw12 = new SearchIndexableRaw(context);
                ((SearchIndexableData) searchIndexableRaw12).key = "vibrate_input_devices";
                searchIndexableRaw12.title = context.getString(R.string.vibrate_input_devices);
                int i3 = R.string.vibrate_input_devices_summary;
                searchIndexableRaw12.summaryOn = context.getString(i3);
                searchIndexableRaw12.summaryOff = context.getString(i3);
                searchIndexableRaw12.screenTitle = string;
                arrayList.add(searchIndexableRaw12);
            }
            return arrayList;
        }
    };
    private DefaultAutofillPreferenceController mAutofillPreferenceController;
    private MiuiValuePreference mBottomAddPref;
    private Context mContext;
    private ValuePreference mCurrentInputMethodCnPreference;
    private DropDownPreference mCurrentInputMethodPreference;
    private Preference mDefaultAutofillPref;
    private DefaultImeObserver mDefaultImeObserver;
    private DevicePolicyManager mDpm;
    private DropDownPreferenceAdapter mDropDownPreferenceAdapter;
    private PreferenceCategory mGameControllerCategory;
    private Handler mHandler;
    private PreferenceCategory mHardKeyboardCategory;
    private InputManager mIm;
    private InputMethodManager mImm;
    private List<String> mInputMethodIdList;
    private List<InputMethodInfo> mInputMethodInfoList;
    private List<CharSequence> mInputMethodNameList;
    private InputMethodSettingValuesWrapper mInputMethodSettingValues;
    private Intent mIntentWaitingForResult;
    private CheckBoxPreference mKeyBoardSkinPreference;
    private PreferenceCategory mKeyboardSettingsCategory;
    private ValuePreference mLanguagePref;
    private ValuePreference mSecIMEPreference;
    private SettingsObserver mSettingsObserver;
    private ListPreference mShowInputMethodSelectorPref;
    private int mDefaultInputMethodSelectorVisibility = 0;
    private final ArrayList<InputMethodPreference> mInputMethodPreferenceList = new ArrayList<>();
    private final ArrayList<PreferenceScreen> mHardKeyboardPreferenceList = new ArrayList<>();
    private boolean sMiuiImeBottomSupport = false;
    private boolean mIsMiuiImeBottomEnabled = false;
    private boolean mIsSupportMiuiSecurityIME = false;
    private String lastSelectedIme = null;

    /* loaded from: classes.dex */
    public class DefaultImeObserver extends ContentObserver {
        public DefaultImeObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            super.onChange(z);
            MiuiLanguageAndInputSettings.this.updateInputMethodPreferenceViews();
            InputMethodFunctionSelectUtils.addSettingsRecord(MiuiLanguageAndInputSettings.this.mContext, Settings.Secure.getString(MiuiLanguageAndInputSettings.this.mContext.getContentResolver(), "default_input_method"));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EnableFullScreenKeyboardObserver extends ContentObserver {
        public EnableFullScreenKeyboardObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            super.onChange(z);
            MiuiLanguageAndInputSettings.this.updateFunctionPreferenceEnable();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class EnabledMechKeyboardObserver extends ContentObserver {
        public EnabledMechKeyboardObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            super.onChange(z);
            Preference findPreference = MiuiLanguageAndInputSettings.this.findPreference("miui_mechanical_ime");
            if (findPreference != null) {
                findPreference.setEnabled(InputMethodFunctionSelectUtils.isMechKeyboardUsable(MiuiLanguageAndInputSettings.this.mContext));
            }
        }
    }

    /* loaded from: classes.dex */
    private class SettingsObserver extends ContentObserver {
        private Context mContext;
        private EnableFullScreenKeyboardObserver mEnableFullScreenKeyboardObserver;
        private EnabledMechKeyboardObserver mEnabledMechKeyboardObserver;

        public SettingsObserver(Handler handler, Context context) {
            super(handler);
            this.mContext = context;
        }

        public void pause() {
            this.mContext.getContentResolver().unregisterContentObserver(this);
            if (MiuiLanguageAndInputSettings.this.sMiuiImeBottomSupport) {
                this.mContext.getContentResolver().unregisterContentObserver(this.mEnableFullScreenKeyboardObserver);
            }
            if (InputMethodFunctionSelectUtils.isSupportMechKeyboard(this.mContext)) {
                this.mContext.getContentResolver().unregisterContentObserver(this.mEnabledMechKeyboardObserver);
            }
        }

        public void resume() {
            ContentResolver contentResolver = this.mContext.getContentResolver();
            contentResolver.registerContentObserver(Settings.Secure.getUriFor("default_input_method"), false, this);
            contentResolver.registerContentObserver(Settings.Secure.getUriFor("selected_input_method_subtype"), false, this);
            if (MiuiLanguageAndInputSettings.this.sMiuiImeBottomSupport) {
                if (this.mEnableFullScreenKeyboardObserver == null) {
                    this.mEnableFullScreenKeyboardObserver = new EnableFullScreenKeyboardObserver(new Handler());
                }
                contentResolver.registerContentObserver(Settings.Secure.getUriFor("enable_miui_ime_bottom_view"), false, this.mEnableFullScreenKeyboardObserver);
            }
            if (InputMethodFunctionSelectUtils.isSupportMechKeyboard(this.mContext)) {
                if (this.mEnabledMechKeyboardObserver == null) {
                    this.mEnabledMechKeyboardObserver = new EnabledMechKeyboardObserver(new Handler());
                }
                contentResolver.registerContentObserver(Settings.Secure.getUriFor("default_input_method"), false, this.mEnabledMechKeyboardObserver);
            }
        }
    }

    static /* synthetic */ boolean access$500() {
        return haveInputDeviceWithVibrator();
    }

    private CharSequence getImeDisplayName(InputMethodInfo inputMethodInfo) {
        return inputMethodInfo.loadLabel(this.mContext.getPackageManager());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getLocaleNames(Context context) {
        ArrayList arrayList = (ArrayList) LocalePicker.getAllAssetLocales(context, false);
        Locale locale = context.getResources().getConfiguration().getLocales().get(0);
        String locale2 = locale.toString();
        String displayName = locale.getDisplayName(locale);
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            LocalePicker.LocaleInfo localeInfo = (LocalePicker.LocaleInfo) it.next();
            if (localeInfo.getLocale().toString().equals(locale2)) {
                displayName = localeInfo.getLabel();
            }
        }
        if (Build.IS_GLOBAL_BUILD) {
            displayName = MiuiUtils.overlayLocaleLanguageLabel(context, locale2, displayName);
        }
        return (MiuiUtils.needOverlayTwLocale() && locale2.equals("zh_TW") && displayName.length() >= 5) ? displayName.substring(0, 5) : displayName;
    }

    private static boolean haveInputDeviceWithVibrator() {
        for (int i : InputDevice.getDeviceIds()) {
            InputDevice device = InputDevice.getDevice(i);
            if (device != null && !device.isVirtual() && device.getVibrator().hasVibrator()) {
                return true;
            }
        }
        return false;
    }

    private HashMap<String, HashSet<String>> loadPreviouslyEnabledSubtypeIdsMap() {
        return InputMethodAndSubtypeUtil.parseInputMethodsAndSubtypesString(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("previously_enabled_subtypes", null));
    }

    private void restorePreviouslyEnabledSubtypesOf(InputMethodInfo inputMethodInfo) {
        HashMap<String, HashSet<String>> loadPreviouslyEnabledSubtypeIdsMap = loadPreviouslyEnabledSubtypeIdsMap();
        String id = inputMethodInfo.getId();
        HashSet<String> remove = loadPreviouslyEnabledSubtypeIdsMap.remove(id);
        if (remove == null) {
            return;
        }
        savePreviouslyEnabledSubtypeIdsMap(loadPreviouslyEnabledSubtypeIdsMap);
        InputMethodAndSubtypeUtil.enableInputMethodSubtypesOf(getContentResolver(), id, remove);
    }

    private void saveEnabledSubtypesOf(InputMethodInfo inputMethodInfo) {
        HashSet<String> hashSet = new HashSet<>();
        Iterator<InputMethodSubtype> it = this.mImm.getEnabledInputMethodSubtypeList(inputMethodInfo, true).iterator();
        while (it.hasNext()) {
            hashSet.add(Integer.toString(it.next().hashCode()));
        }
        HashMap<String, HashSet<String>> loadPreviouslyEnabledSubtypeIdsMap = loadPreviouslyEnabledSubtypeIdsMap();
        loadPreviouslyEnabledSubtypeIdsMap.put(inputMethodInfo.getId(), hashSet);
        savePreviouslyEnabledSubtypeIdsMap(loadPreviouslyEnabledSubtypeIdsMap);
    }

    private void savePreviouslyEnabledSubtypeIdsMap(HashMap<String, HashSet<String>> hashMap) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        defaultSharedPreferences.edit().putString("previously_enabled_subtypes", InputMethodAndSubtypeUtil.buildInputMethodsAndSubtypesString(hashMap)).apply();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showKeyboardLayoutDialog(InputDeviceIdentifier inputDeviceIdentifier) {
        if (((KeyboardLayoutDialogFragment) getFragmentManager().findFragmentByTag("keyboardLayout")) == null) {
            KeyboardLayoutDialogFragment keyboardLayoutDialogFragment = new KeyboardLayoutDialogFragment(inputDeviceIdentifier);
            keyboardLayoutDialogFragment.setTargetFragment(this, 0);
            keyboardLayoutDialogFragment.show(getActivity().getSupportFragmentManager(), "keyboardLayout");
        }
    }

    public static boolean supportMiuiSecInputMethod() {
        if (!Build.IS_GLOBAL_BUILD && SystemProperties.getInt("ro.miui.has_security_keyboard", 0) == 1) {
            try {
                AppGlobals.getInitialApplication().getPackageManager().getPackageInfo("com.miui.securityinputmethod", 0);
                return true;
            } catch (PackageManager.NameNotFoundException unused) {
                Log.w("MiuiLanguageAndInputSettings", "com.miui.securityinputmethod not installed");
            }
        }
        return false;
    }

    private void updateCurrentImeName() {
        InputMethodManager inputMethodManager;
        FragmentActivity activity = getActivity();
        if (activity == null || (inputMethodManager = this.mImm) == null) {
            return;
        }
        this.mInputMethodInfoList = inputMethodManager.getEnabledInputMethodList();
        this.mInputMethodNameList = new ArrayList();
        this.mInputMethodIdList = new ArrayList();
        CharSequence[] charSequenceArr = new CharSequence[this.mInputMethodNameList.size()];
        for (int i = 0; i < this.mInputMethodInfoList.size(); i++) {
            this.mInputMethodNameList.add(getImeDisplayName(this.mInputMethodInfoList.get(i)));
            this.mInputMethodIdList.add(this.mInputMethodInfoList.get(i).getId());
            if (getPreferenceScreen().findPreference("current_input_method") != null) {
                CharSequence currentInputMethodName = this.mInputMethodSettingValues.getCurrentInputMethodName(activity);
                if (TextUtils.isEmpty(currentInputMethodName)) {
                    continue;
                } else {
                    synchronized (this) {
                        if (!TextUtils.isEmpty(this.lastSelectedIme) && !this.lastSelectedIme.equalsIgnoreCase(currentInputMethodName.toString())) {
                            InternationalCompat.captureChangedKeyboardAnalytics(activity, this.lastSelectedIme);
                            this.lastSelectedIme = null;
                        }
                    }
                }
            }
        }
        String string = Settings.Secure.getString(this.mContext.getContentResolver(), "default_input_method");
        int indexOf = this.mInputMethodIdList.indexOf(string);
        if (indexOf == -1) {
            Log.e("MiuiLanguageAndInputSettings", "updateCurrentImeName: curImeId " + string + " is not in the enabledInputMethodList :" + Arrays.toString(this.mInputMethodIdList.toArray()));
        } else if (!Build.IS_INTERNATIONAL_BUILD) {
            ValuePreference valuePreference = this.mCurrentInputMethodCnPreference;
            if (valuePreference != null) {
                valuePreference.setValue(this.mInputMethodNameList.get(indexOf).toString());
            }
        } else if (this.mCurrentInputMethodPreference != null) {
            CharSequence[] charSequenceArr2 = (CharSequence[]) this.mInputMethodNameList.toArray(charSequenceArr);
            DropDownPreferenceAdapter dropDownPreferenceAdapter = this.mDropDownPreferenceAdapter;
            if (dropDownPreferenceAdapter == null) {
                this.mDropDownPreferenceAdapter = new DropDownPreferenceAdapter(this.mContext, charSequenceArr2);
            } else {
                dropDownPreferenceAdapter.updateEnabledIME(charSequenceArr2);
            }
            this.mCurrentInputMethodPreference.setAdapter(this.mDropDownPreferenceAdapter);
            this.mCurrentInputMethodPreference.setEntries(charSequenceArr2);
            this.mCurrentInputMethodPreference.setValueIndex(indexOf);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFunctionPreferenceEnable() {
        boolean z = Settings.Secure.getInt(getContentResolver(), "enable_miui_ime_bottom_view", 1) == 1;
        this.mIsMiuiImeBottomEnabled = z;
        if (z) {
            this.mBottomAddPref.setSummary(this.mContext.getResources().getString(R.string.input_method_bottom_open));
        } else {
            this.mBottomAddPref.setSummary(this.mContext.getResources().getString(R.string.input_method_bottom_close));
        }
    }

    private void updateGameControllers() {
        if (!haveInputDeviceWithVibrator()) {
            getPreferenceScreen().removePreference(this.mGameControllerCategory);
            return;
        }
        getPreferenceScreen().addPreference(this.mGameControllerCategory);
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) this.mGameControllerCategory.findPreference("vibrate_input_devices");
        checkBoxPreference.setChecked(Settings.System.getInt(getContentResolver(), "vibrate_input_devices", 1) > 0);
        checkBoxPreference.setOnPreferenceChangeListener(this);
    }

    private void updateHardKeyboards() {
        if (this.mHardKeyboardCategory == null) {
            return;
        }
        this.mHardKeyboardPreferenceList.clear();
        for (int i : InputDevice.getDeviceIds()) {
            InputDevice device = InputDevice.getDevice(i);
            if (device != null && !device.isVirtual() && device.isFullKeyboard()) {
                final InputDeviceIdentifier identifier = device.getIdentifier();
                String currentKeyboardLayoutForInputDevice = this.mIm.getCurrentKeyboardLayoutForInputDevice(identifier);
                KeyboardLayout keyboardLayout = currentKeyboardLayoutForInputDevice != null ? this.mIm.getKeyboardLayout(currentKeyboardLayoutForInputDevice) : null;
                PreferenceScreen preferenceScreen = new PreferenceScreen(getPrefContext(), null);
                preferenceScreen.setTitle(device.getName());
                if (keyboardLayout != null) {
                    preferenceScreen.setSummary(keyboardLayout.toString());
                } else {
                    preferenceScreen.setSummary(R.string.keyboard_layout_default_label);
                }
                preferenceScreen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.language.MiuiLanguageAndInputSettings.2
                    @Override // androidx.preference.Preference.OnPreferenceClickListener
                    public boolean onPreferenceClick(Preference preference) {
                        MiuiLanguageAndInputSettings.this.showKeyboardLayoutDialog(identifier);
                        return true;
                    }
                });
                this.mHardKeyboardPreferenceList.add(preferenceScreen);
            }
        }
        if (this.mHardKeyboardPreferenceList.isEmpty()) {
            getPreferenceScreen().removePreference(this.mHardKeyboardCategory);
            return;
        }
        int preferenceCount = this.mHardKeyboardCategory.getPreferenceCount();
        while (true) {
            int i2 = preferenceCount - 1;
            if (preferenceCount <= 0) {
                break;
            }
            Preference preference = this.mHardKeyboardCategory.getPreference(i2);
            if (preference.getOrder() < 1000) {
                this.mHardKeyboardCategory.removePreference(preference);
            }
            preferenceCount = i2;
        }
        Collections.sort(this.mHardKeyboardPreferenceList);
        int size = this.mHardKeyboardPreferenceList.size();
        for (int i3 = 0; i3 < size; i3++) {
            PreferenceScreen preferenceScreen2 = this.mHardKeyboardPreferenceList.get(i3);
            preferenceScreen2.setOrder(i3);
            this.mHardKeyboardCategory.addPreference(preferenceScreen2);
        }
        getPreferenceScreen().addPreference(this.mHardKeyboardCategory);
    }

    private void updateInputDevices() {
        updateHardKeyboards();
        updateGameControllers();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateInputMethodPreferenceViews() {
        if (this.mKeyboardSettingsCategory == null) {
            return;
        }
        updateCurrentImeName();
        if (!Build.IS_INTERNATIONAL_BUILD) {
            this.mKeyBoardSkinPreference.setEnabled(InputMethodFunctionSelectUtils.sCustomIme.contains(InputMethodFunctionSelectUtils.getCurrentInputMethod(this.mContext)));
        }
        InputMethodAndSubtypeUtil.loadInputMethodSubtypeList(this, getContentResolver(), this.mInputMethodSettingValues.getInputMethodList(), null);
    }

    private void updateSecurityImePreference() {
        this.mSecIMEPreference.setValue(MiuiSettings.Secure.getBoolean(getContentResolver(), "enable_miui_security_ime", true) ? R.string.input_method_bottom_open : R.string.input_method_bottom_close);
    }

    private void updateUserDictionaryPreference(Preference preference) {
        final TreeSet<String> userDictionaryLocalesSet = UserDictionaryListPreferenceController.getUserDictionaryLocalesSet(getActivity());
        if (userDictionaryLocalesSet != null) {
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.language.MiuiLanguageAndInputSettings.1
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference2) {
                    Class cls;
                    Bundle bundle = new Bundle();
                    if (userDictionaryLocalesSet.size() <= 1) {
                        if (!userDictionaryLocalesSet.isEmpty()) {
                            bundle.putString("locale", (String) userDictionaryLocalesSet.first());
                        }
                        cls = UserDictionarySettings.class;
                    } else {
                        cls = UserDictionaryList.class;
                    }
                    MiuiLanguageAndInputSettings miuiLanguageAndInputSettings = MiuiLanguageAndInputSettings.this;
                    miuiLanguageAndInputSettings.startFragment(miuiLanguageAndInputSettings, cls.getCanonicalName(), -1, -1, bundle);
                    return true;
                }
            });
        } else if (preference != null) {
            getPreferenceScreen().removePreference(preference);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return MiuiLanguageAndInputSettings.class.getSimpleName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 57;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiLanguageAndInputSettings.class.getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.language_settings;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        Intent intent2 = this.mIntentWaitingForResult;
        if (intent2 != null) {
            InputDeviceIdentifier inputDeviceIdentifier = (InputDeviceIdentifier) intent2.getParcelableExtra("input_device_identifier");
            this.mIntentWaitingForResult = null;
            showKeyboardLayoutDialog(inputDeviceIdentifier);
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        InternationalCompat.trackReportEvent("setting_Additional_settings_keyboard");
        FragmentActivity activity = getActivity();
        this.mImm = (InputMethodManager) getSystemService("input_method");
        this.mInputMethodSettingValues = InputMethodSettingValuesWrapper.getInstance(activity);
        try {
            this.mDefaultInputMethodSelectorVisibility = Integer.valueOf(getString(R.string.input_method_selector_visibility_default_value)).intValue();
        } catch (NumberFormatException unused) {
        }
        if (activity.getAssets().getLocales().length == 1 || RestrictionsHelper.hasRestriction(getActivity(), "disallow_change_language")) {
            getPreferenceScreen().removePreference(findPreference("phone_language"));
        } else {
            this.mLanguagePref = (ValuePreference) findPreference("phone_language");
        }
        this.mHardKeyboardCategory = (PreferenceCategory) findPreference("hard_keyboard");
        this.mKeyboardSettingsCategory = (PreferenceCategory) findPreference("keyboard_settings_category");
        this.mGameControllerCategory = (PreferenceCategory) findPreference("game_controller_settings_category");
        Intent intent = activity.getIntent();
        this.mIm = (InputManager) activity.getSystemService("input");
        updateInputDevices();
        Preference findPreference = findPreference("spellcheckers_settings");
        if (findPreference != null) {
            InputMethodAndSubtypeUtil.removeUnnecessaryNonPersistentPreference(findPreference);
            Intent intent2 = new Intent("android.intent.action.MAIN");
            intent2.setClass(activity, SubSettings.class);
            intent2.putExtra(":settings:show_fragment", SpellCheckersSettings.class.getName());
            intent2.putExtra(":settings:show_fragment_title_resid", R.string.spellcheckers_settings_title);
            findPreference.setIntent(intent2);
            TextServicesManager textServicesManager = (TextServicesManager) getSystemService("textservices");
            SpellCheckerInfo[] enabledSpellCheckers = textServicesManager.getEnabledSpellCheckers();
            if ((!textServicesManager.isSpellCheckerEnabled() && !Build.IS_GLOBAL_BUILD) || enabledSpellCheckers == null || enabledSpellCheckers.length == 0) {
                ((PreferenceCategory) findPreference("other_input_settings")).removePreference(findPreference);
            }
        }
        Preference findPreference2 = findPreference("input_settings");
        if (findPreference2 != null) {
            getPreferenceScreen().removePreference(findPreference2);
        }
        this.mSecIMEPreference = (ValuePreference) findPreference("miui_security_ime");
        boolean supportMiuiSecInputMethod = supportMiuiSecInputMethod();
        this.mIsSupportMiuiSecurityIME = supportMiuiSecInputMethod;
        if (!supportMiuiSecInputMethod) {
            this.mKeyboardSettingsCategory.removePreference(this.mSecIMEPreference);
        }
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("keyboard_skin_follow_system_enable");
        this.mKeyBoardSkinPreference = checkBoxPreference;
        boolean z = Build.IS_INTERNATIONAL_BUILD;
        if (z) {
            this.mKeyboardSettingsCategory.removePreference(checkBoxPreference);
        } else {
            this.mKeyBoardSkinPreference.setChecked(InputMethodFunctionSelectUtils.isKeyBoardSkinFollowSystemEnable(this.mContext));
            this.mKeyBoardSkinPreference.setOnPreferenceChangeListener(this);
        }
        this.mHandler = new Handler();
        this.mSettingsObserver = new SettingsObserver(this.mHandler, activity);
        this.mDpm = (DevicePolicyManager) getActivity().getSystemService("device_policy");
        intent.getParcelableExtra("input_device_identifier");
        this.mDefaultAutofillPref = findPreference("default_autofill");
        this.mAutofillPreferenceController = new DefaultAutofillPreferenceController(activity);
        this.sMiuiImeBottomSupport = InputMethodFunctionSelectUtils.isMiuiImeBottomSupport();
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("full_screen_keyboard_optimization");
        if (this.sMiuiImeBottomSupport) {
            this.mBottomAddPref = (MiuiValuePreference) findPreference("miui_bottom_manager");
        } else {
            getPreferenceScreen().removePreference(preferenceCategory);
        }
        this.mCurrentInputMethodPreference = (DropDownPreference) findPreference("current_input_method");
        ValuePreference valuePreference = (ValuePreference) findPreference("current_input_method_cn");
        this.mCurrentInputMethodCnPreference = valuePreference;
        if (z) {
            this.mKeyboardSettingsCategory.removePreference(valuePreference);
            this.mCurrentInputMethodPreference.setOnPreferenceChangeListener(this);
            return;
        }
        this.mKeyboardSettingsCategory.removePreference(this.mCurrentInputMethodPreference);
        this.mCurrentInputMethodCnPreference.setShowRightArrow(true);
        this.mDefaultImeObserver = new DefaultImeObserver(new Handler());
        this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("default_input_method"), false, this.mDefaultImeObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        if (this.mDefaultImeObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mDefaultImeObserver);
        }
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceAdded(int i) {
        updateInputDevices();
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceChanged(int i) {
        updateInputDevices();
    }

    @Override // android.hardware.input.InputManager.InputDeviceListener
    public void onInputDeviceRemoved(int i) {
        updateInputDevices();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        CheckBoxPreference checkBoxPreference;
        super.onPause();
        this.mIm.unregisterInputDeviceListener(this);
        this.mSettingsObserver.pause();
        if (supportMiuiSecInputMethod()) {
            this.mSecIMEPreference.setOnPreferenceChangeListener(null);
        }
        if (haveInputDeviceWithVibrator() && (checkBoxPreference = (CheckBoxPreference) this.mGameControllerCategory.findPreference("vibrate_input_devices")) != null) {
            checkBoxPreference.setOnPreferenceChangeListener(null);
        }
        InputMethodAndSubtypeUtil.saveInputMethodSubtypeList(this, getContentResolver(), this.mInputMethodSettingValues.getInputMethodList(), !this.mHardKeyboardPreferenceList.isEmpty());
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        if (preference == this.mShowInputMethodSelectorPref) {
            return true;
        }
        if ("vibrate_input_devices".equals(key)) {
            if (obj instanceof Boolean) {
                Settings.System.putInt(getContentResolver(), "vibrate_input_devices", ((Boolean) obj).booleanValue() ? 1 : 0);
                return true;
            }
            return true;
        } else if ("current_input_method".equals(key)) {
            int indexOf = this.mInputMethodNameList.indexOf((String) obj);
            Settings.Secure.putString(this.mContext.getContentResolver(), "default_input_method", this.mInputMethodIdList.get(indexOf));
            InputMethodFunctionSelectUtils.addSettingsRecord(this.mContext, this.mInputMethodInfoList.get(indexOf).getPackageName());
            return true;
        } else if ("keyboard_skin_follow_system_enable".equals(key) && (obj instanceof Boolean)) {
            InputMethodFunctionSelectUtils.setPreferenceCheckedValue(this.mContext, "keyboard_skin_follow_system_enable", ((Boolean) obj).booleanValue() ? 1 : 0);
            return true;
        } else {
            return true;
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        CheckBoxPreference checkBoxPreference;
        if (Utils.isMonkeyRunning()) {
            return false;
        }
        if (preference instanceof Preference) {
            if (preference.getFragment() != null) {
                if (preference.getKey() != null && "phone_language".equals(preference.getKey())) {
                    Intent intent = new Intent();
                    intent.setAction("android.settings.LOCALE_SETTINGS");
                    intent.putExtra(":settings:show_fragment_title_resid", R.string.phone_language);
                    MiuiUtils.cancelSplit(this.mContext, intent);
                    startActivity(intent);
                    return true;
                }
            } else if ("current_input_method_cn".equals(preference.getKey())) {
                FragmentActivity activity = getActivity();
                if (activity != null || this.mImm != null) {
                    CharSequence currentInputMethodName = this.mInputMethodSettingValues.getCurrentInputMethodName(activity);
                    if (!TextUtils.isEmpty(currentInputMethodName)) {
                        this.lastSelectedIme = currentInputMethodName.toString();
                    }
                }
                ((InputMethodManager) getSystemService("input_method")).showInputMethodPicker();
            }
        } else if ((preference instanceof CheckBoxPreference) && (checkBoxPreference = (CheckBoxPreference) preference) == this.mGameControllerCategory.findPreference("vibrate_input_devices")) {
            Settings.System.putInt(getContentResolver(), "vibrate_input_devices", checkBoxPreference.isChecked() ? 1 : 0);
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateActionBarTitleView(R.string.language_settings);
        if (this.sMiuiImeBottomSupport) {
            updateFunctionPreferenceEnable();
        }
        if (this.mIsSupportMiuiSecurityIME) {
            updateSecurityImePreference();
        }
        this.mSettingsObserver.resume();
        this.mIm.registerInputDeviceListener(this, null);
        Preference findPreference = findPreference("spellcheckers_settings");
        if (findPreference != null) {
            TextServicesManager textServicesManager = (TextServicesManager) getSystemService("textservices");
            if (textServicesManager.isSpellCheckerEnabled()) {
                SpellCheckerInfo currentSpellChecker = textServicesManager.getCurrentSpellChecker();
                if (currentSpellChecker != null) {
                    findPreference.setSummary(currentSpellChecker.loadLabel(getPackageManager()));
                } else {
                    findPreference.setSummary(R.string.spell_checker_not_selected);
                }
            } else {
                findPreference.setSummary(R.string.switch_off_text);
            }
        }
        boolean z = true;
        if (this.mLanguagePref != null) {
            this.mLanguagePref.setValue(getLocaleNames(getActivity()));
            this.mLanguagePref.setShowRightArrow(true);
        }
        Preference findPreference2 = findPreference("key_user_dictionary_settings");
        if (findPreference2 == null) {
            findPreference2 = new Preference(getPrefContext());
            findPreference2.setKey("key_user_dictionary_settings");
            findPreference2.setTitle(R.string.user_dict_settings_title);
            findPreference2.setOrder((findPreference("spellcheckers_settings") != null ? findPreference("spellcheckers_settings").getOrder() : this.mLanguagePref.getOrder()) + 1);
            getPreferenceScreen().addPreference(findPreference2);
        }
        List<InputMethodInfo> enabledInputMethodList = this.mImm.getEnabledInputMethodList();
        int size = enabledInputMethodList == null ? 0 : enabledInputMethodList.size();
        int i = 0;
        while (true) {
            if (i < size) {
                CharSequence loadLabel = enabledInputMethodList.get(i).loadLabel(getContext().getPackageManager());
                if (loadLabel != null && loadLabel.toString().contains("AOSP")) {
                    break;
                }
                i++;
            } else {
                z = false;
                break;
            }
        }
        if (z) {
            updateUserDictionaryPreference(findPreference2);
        } else {
            getPreferenceScreen().removePreference(findPreference2);
        }
        updateInputDevices();
        this.mInputMethodSettingValues.refreshAllInputMethodAndSubtypes();
        updateInputMethodPreferenceViews();
        this.mAutofillPreferenceController.updateState(this.mDefaultAutofillPref);
    }

    @Override // com.android.settingslib.inputmethod.InputMethodPreference.OnSavePreferenceListener
    public void onSaveInputMethodPreference(InputMethodPreference inputMethodPreference) {
        InputMethodInfo inputMethodInfo = inputMethodPreference.getInputMethodInfo();
        if (!inputMethodPreference.isChecked()) {
            saveEnabledSubtypesOf(inputMethodInfo);
        }
        InputMethodAndSubtypeUtil.saveInputMethodSubtypeList(this, getContentResolver(), this.mImm.getInputMethodList(), getResources().getConfiguration().keyboard == 2);
        this.mInputMethodSettingValues.refreshAllInputMethodAndSubtypes();
        if (inputMethodPreference.isChecked()) {
            restorePreviouslyEnabledSubtypesOf(inputMethodInfo);
        }
        Iterator<InputMethodPreference> it = this.mInputMethodPreferenceList.iterator();
        while (it.hasNext()) {
            it.next().updatePreferenceViews();
        }
    }

    @Override // com.android.settings.inputmethod.KeyboardLayoutDialogFragment.OnSetupKeyboardLayoutsListener
    public void onSetupKeyboardLayouts(InputDeviceIdentifier inputDeviceIdentifier) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClass(getActivity(), Settings.KeyboardLayoutPickerActivity.class);
        intent.putExtra("input_device_identifier", (Parcelable) inputDeviceIdentifier);
        this.mIntentWaitingForResult = intent;
        startActivityForResult(intent, 0);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.language_settings);
        }
    }
}
