package com.android.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.stat.commonpreference.KeySettingsStatHelper;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import miui.cloud.sync.MiCloudStatusInfo;
import miui.os.Build;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class KeyShortcutSettingsFragment extends KeyAndGestureShortcutStatHelperFragment implements Preference.OnPreferenceChangeListener {
    private Context mContext;
    private boolean mPressMenuToAppSwitch;
    private Resources mResources;
    private Map<String, DropDownPreference> mShortcutPreferenceMap = new HashMap();
    private AlertDialog mFunctionChangeDialog = null;

    private void bringUpActionChooseDlg(final String str, final String str2, final DropDownPreference dropDownPreference) {
        if (this.mFunctionChangeDialog != null) {
            return;
        }
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.KeyShortcutSettingsFragment.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                String str3 = MiCloudStatusInfo.QuotaInfo.WARN_NONE;
                if (i == -1) {
                    MiuiSettings.System.putStringForUser(KeyShortcutSettingsFragment.this.mContext.getContentResolver(), dropDownPreference.getKey(), str, -2);
                    dropDownPreference.setValue(str);
                    MiuiSettings.System.putStringForUser(KeyShortcutSettingsFragment.this.mContext.getContentResolver(), str2, MiCloudStatusInfo.QuotaInfo.WARN_NONE, -2);
                    ((DropDownPreference) KeyShortcutSettingsFragment.this.mShortcutPreferenceMap.get(str2)).setValue(MiCloudStatusInfo.QuotaInfo.WARN_NONE);
                } else {
                    String keyAndGestureShortcutSetFunction = MiuiShortcut$Key.getKeyAndGestureShortcutSetFunction(KeyShortcutSettingsFragment.this.mContext, dropDownPreference.getKey());
                    DropDownPreference dropDownPreference2 = dropDownPreference;
                    if (keyAndGestureShortcutSetFunction != null) {
                        str3 = keyAndGestureShortcutSetFunction;
                    }
                    dropDownPreference2.setValue(str3);
                }
                if (KeyShortcutSettingsFragment.this.mFunctionChangeDialog != null) {
                    KeyShortcutSettingsFragment.this.mFunctionChangeDialog.dismiss();
                    KeyShortcutSettingsFragment.this.mFunctionChangeDialog = null;
                }
            }
        };
        String charSequence = this.mShortcutPreferenceMap.get(str2).getTitle().toString();
        String charSequence2 = this.mShortcutPreferenceMap.get(dropDownPreference.getKey()).getTitle().toString();
        AlertDialog create = new AlertDialog.Builder(this.mContext).setTitle((CharSequence) null).setMessage(this.mResources.getString(R.string.key_gesture_function_dialog_message, charSequence, MiuiShortcut$Key.getResourceForKey(MiuiShortcut$Key.getKeyAndGestureShortcutSetFunction(this.mContext, str2), this.mContext), charSequence2)).setPositiveButton(R.string.key_gesture_function_dialog_positive, onClickListener).setNegativeButton(R.string.key_gesture_function_dialog_negative, onClickListener).setCancelable(false).create();
        this.mFunctionChangeDialog = create;
        create.show();
    }

    private CharSequence[] getSummary(String str, Map<String, String> map) {
        resetSummary(map);
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -163377242:
                if (str.equals("long_press_back_key")) {
                    c = 0;
                    break;
                }
                break;
            case 1406870558:
                if (str.equals("long_press_menu_key")) {
                    c = 1;
                    break;
                }
                break;
            case 1524450206:
                if (str.equals("long_press_home_key")) {
                    c = 2;
                    break;
                }
                break;
            case 1871272923:
                if (str.equals("press_menu")) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 1:
            case 2:
                if (SettingsFeatures.hasSplitScreen()) {
                    map.put("split_screen", this.mResources.getString(R.string.split_screen));
                }
                map.put("go_to_sleep", this.mResources.getString(R.string.go_to_sleep));
                break;
            case 3:
                map.clear();
                map.put("show_menu", this.mResources.getString(R.string.show_menu));
                map.put("launch_recents", this.mResources.getString(R.string.launch_recents));
                break;
        }
        if (!str.equals("press_menu")) {
            map.put(MiCloudStatusInfo.QuotaInfo.WARN_NONE, this.mResources.getString(R.string.key_none));
        }
        return (CharSequence[]) map.values().toArray(new CharSequence[map.values().size()]);
    }

    private CharSequence[] getValue(Map<String, String> map) {
        return (CharSequence[]) map.keySet().toArray(new CharSequence[map.keySet().size()]);
    }

    private void resetSummary(Map<String, String> map) {
        map.put("launch_camera", this.mResources.getString(R.string.launch_camera));
        map.put("screen_shot", this.mResources.getString(R.string.screen_shot));
        if (MiuiShortcut$System.supportPartialScreenShot()) {
            map.put("partial_screen_shot", this.mResources.getString(R.string.regional_screen_shot));
        }
        boolean z = Build.IS_GLOBAL_BUILD;
        map.put(z ? "launch_google_search" : "launch_voice_assistant", this.mResources.getString(z ? R.string.launch_google_search : R.string.launch_voice_assistant));
        map.put("close_app", this.mResources.getString(R.string.close_app));
        if (Build.hasCameraFlash(getActivity())) {
            map.put("turn_on_torch", this.mResources.getString(R.string.turn_on_torch));
        }
        if (Build.IS_DEVELOPMENT_VERSION) {
            map.put("dump_log", this.mResources.getString(R.string.dump_log));
        }
        if (this.mPressMenuToAppSwitch) {
            map.put("show_menu", this.mResources.getString(R.string.show_menu));
        } else {
            map.put("launch_recents", this.mResources.getString(R.string.launch_recents));
        }
    }

    private void updateDropPreferenceInfo(String str) {
        List<String> keyShortcutAction = MiuiShortcut$Key.getKeyShortcutAction(this.mContext, str);
        if (keyShortcutAction != null) {
            for (Map.Entry<String, DropDownPreference> entry : this.mShortcutPreferenceMap.entrySet()) {
                if (!"press_menu".equals(entry.getKey())) {
                    LinkedHashMap linkedHashMap = new LinkedHashMap();
                    entry.getValue().setEntries(getSummary(entry.getKey(), linkedHashMap));
                    entry.getValue().setEntryValues(getValue(linkedHashMap));
                    if (keyShortcutAction.contains(entry.getKey())) {
                        entry.getValue().setValue(MiCloudStatusInfo.QuotaInfo.WARN_NONE);
                        MiuiSettings.System.putStringForUser(this.mContext.getContentResolver(), entry.getKey(), MiCloudStatusInfo.QuotaInfo.WARN_NONE, -2);
                    }
                }
            }
        }
    }

    @Override // com.android.settings.KeyAndGestureShortcutStatHelperFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mContext = activity;
        this.mResources = activity.getResources();
        this.mPageTitle = KeySettingsStatHelper.KEY_PAGE_KEY;
        addPreferencesFromResource(R.xml.keyshortcut_settings);
        this.mPressMenuToAppSwitch = MiuiSettings.System.getBoolean(getContentResolver(), "screen_key_press_app_switch", true);
        for (String str : MiuiShortcut$Key.KEY_SHORTCUT_ACTION) {
            DropDownPreference dropDownPreference = (DropDownPreference) findPreference(str);
            if (dropDownPreference != null) {
                this.mShortcutPreferenceMap.put(str, dropDownPreference);
                Map<String, String> linkedHashMap = new LinkedHashMap<>();
                dropDownPreference.setEntries(getSummary(str, linkedHashMap));
                dropDownPreference.setEntryValues(getValue(linkedHashMap));
                if ("press_menu".equals(str)) {
                    dropDownPreference.setValue(this.mPressMenuToAppSwitch ? "launch_recents" : "show_menu");
                } else {
                    String keyAndGestureShortcutSetFunction = MiuiShortcut$Key.getKeyAndGestureShortcutSetFunction(this.mContext, str);
                    if (TextUtils.isEmpty(keyAndGestureShortcutSetFunction) || !linkedHashMap.containsKey(keyAndGestureShortcutSetFunction)) {
                        dropDownPreference.setValue(MiCloudStatusInfo.QuotaInfo.WARN_NONE);
                    } else {
                        dropDownPreference.setValue(keyAndGestureShortcutSetFunction);
                    }
                }
                dropDownPreference.setOnPreferenceChangeListener(this);
            }
        }
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("pref_fingerprint_nav_center_to_home");
        CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) findPreference("pref_single_key_use");
        if (checkBoxPreference == null || checkBoxPreference2 == null) {
            return;
        }
        if (!MiuiShortcut$System.supportFpNavCenterToHome()) {
            getPreferenceScreen().removePreference(checkBoxPreference);
            getPreferenceScreen().removePreference(checkBoxPreference2);
            return;
        }
        int i = Settings.System.getInt(this.mContext.getContentResolver(), "single_key_use_enable", 0);
        int i2 = Settings.System.getInt(this.mContext.getContentResolver(), "fingerprint_nav_center_action", 0);
        checkBoxPreference.setChecked(i == 1);
        checkBoxPreference2.setChecked(i2 == 1);
        checkBoxPreference.setOnPreferenceChangeListener(this);
        checkBoxPreference2.setOnPreferenceChangeListener(this);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if ("pref_fingerprint_nav_center_to_home".equals(preference.getKey())) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            Settings.System.putIntForUser(getContentResolver(), "fingerprint_nav_center_action", booleanValue ? 1 : 0, -2);
            this.mShortcutMap.put("fingerprint_nav_center_action", booleanValue ? "TRUE" : "FALSE");
        } else if ("pref_single_key_use".equals(preference.getKey())) {
            boolean booleanValue2 = ((Boolean) obj).booleanValue();
            Settings.System.putIntForUser(getContentResolver(), "single_key_use_enable", booleanValue2 ? 1 : 0, -2);
            this.mShortcutMap.put("single_key_use_enable", booleanValue2 ? "TRUE" : "FALSE");
        } else {
            List<String> keyShortcutAction = !MiCloudStatusInfo.QuotaInfo.WARN_NONE.equals(obj) ? MiuiShortcut$Key.getKeyShortcutAction(this.mContext, (String) obj) : null;
            if ("press_menu".equals(preference.getKey())) {
                String str = (String) obj;
                this.mPressMenuToAppSwitch = "launch_recents".equals(str);
                updateDropPreferenceInfo(str);
                MiuiSettings.System.putBoolean(getContentResolver(), "screen_key_press_app_switch", this.mPressMenuToAppSwitch);
                this.mShortcutMap.put("press_menu", this.mPressMenuToAppSwitch ? "launch_recents" : "show_menu");
            } else if (keyShortcutAction == null || keyShortcutAction.size() == 0) {
                String str2 = (String) obj;
                MiuiSettings.System.putStringForUser(this.mContext.getContentResolver(), preference.getKey(), str2, -2);
                this.mShortcutMap.put(preference.getKey(), str2);
            } else {
                int i = 0;
                for (String str3 : keyShortcutAction) {
                    if (str3 != null && this.mShortcutPreferenceMap.get(str3) != null) {
                        i++;
                        bringUpActionChooseDlg((String) obj, str3, (DropDownPreference) preference);
                    }
                    if (i > 1) {
                        this.mShortcutPreferenceMap.get(str3).setValue(MiCloudStatusInfo.QuotaInfo.WARN_NONE);
                        MiuiSettings.System.putStringForUser(this.mContext.getContentResolver(), str3, MiCloudStatusInfo.QuotaInfo.WARN_NONE, -2);
                    }
                }
            }
        }
        return true;
    }
}
