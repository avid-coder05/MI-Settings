package com.android.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.recommend.PageIndexManager;
import com.android.settings.recommend.RecommendPreference;
import com.android.settings.search.tree.GestureSettingsTree;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import miui.cloud.sync.MiCloudStatusInfo;
import miui.util.FeatureParser;
import miuix.appcompat.app.AlertDialog;
import miuix.preference.RadioButtonPreference;
import miuix.preference.RadioButtonPreferenceCategory;

/* loaded from: classes.dex */
public class WakeupXiaoaiSettingsFragment extends KeyAndGestureShortcutStatHelperFragment implements Preference.OnPreferenceChangeListener {
    private AlertDialog mActionChangeDialog;
    private ContentObserver mContentObserver;
    private Context mContext;
    private RadioButtonPreferenceCategory mKeyCategory;
    private final ArrayList<String> mKeyShortcutList = new ArrayList<>(MiuiShortcut$Key.KEY_SHORTCUT_ACTION.length + 1);
    private boolean mLongPressPowerKeyLaunchXiaoai;
    private RadioButtonPreference mSelectedRadioButton;
    private String mTitle;

    private void bringDialog(final Preference preference, String str) {
        if (this.mActionChangeDialog != null) {
            return;
        }
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.WakeupXiaoaiSettingsFragment.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -1) {
                    if (preference instanceof RadioButtonPreference) {
                        MiuiSettings.System.putStringForUser(WakeupXiaoaiSettingsFragment.this.mContext.getContentResolver(), WakeupXiaoaiSettingsFragment.this.mSelectedRadioButton.getKey(), MiCloudStatusInfo.QuotaInfo.WARN_NONE, -2);
                        WakeupXiaoaiSettingsFragment.this.mSelectedRadioButton = (RadioButtonPreference) preference;
                        WakeupXiaoaiSettingsFragment.this.mKeyCategory.setCheckedPreference(WakeupXiaoaiSettingsFragment.this.mSelectedRadioButton);
                    }
                    Preference preference2 = preference;
                    if (preference2 instanceof CheckBoxPreference) {
                        ((CheckBoxPreference) preference2).setChecked(true);
                    }
                    MiuiSettings.System.putStringForUser(WakeupXiaoaiSettingsFragment.this.mContext.getContentResolver(), preference.getKey(), WakeupXiaoaiSettingsFragment.this.mTitle, -2);
                }
                if (WakeupXiaoaiSettingsFragment.this.mActionChangeDialog != null) {
                    WakeupXiaoaiSettingsFragment.this.mActionChangeDialog.dismiss();
                    WakeupXiaoaiSettingsFragment.this.mActionChangeDialog = null;
                }
            }
        };
        AlertDialog create = new AlertDialog.Builder(this.mContext).setTitle((CharSequence) null).setMessage(this.mContext.getResources().getString(R.string.gesture_function_dialog_message, MiuiShortcut$Key.getResourceForKey(preference.getKey(), this.mContext), MiuiShortcut$Key.getResourceForKey(str, this.mContext), MiuiShortcut$Key.getResourceForKey(this.mTitle, this.mContext))).setPositiveButton(R.string.key_gesture_function_dialog_positive, onClickListener).setNegativeButton(R.string.key_gesture_function_dialog_negative, onClickListener).setCancelable(false).create();
        this.mActionChangeDialog = create;
        create.show();
    }

    private boolean isNoOtherFunction(Preference preference) {
        String keyAndGestureShortcutSetFunction = MiuiShortcut$Key.getKeyAndGestureShortcutSetFunction(this.mContext, preference.getKey());
        if (TextUtils.isEmpty(keyAndGestureShortcutSetFunction) || MiCloudStatusInfo.QuotaInfo.WARN_NONE.equals(keyAndGestureShortcutSetFunction) || this.mTitle.equals(keyAndGestureShortcutSetFunction)) {
            return true;
        }
        bringDialog(preference, keyAndGestureShortcutSetFunction);
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public int getPageIndex() {
        return PageIndexManager.PAGE_WAKE_UP_XIAOAI_SETTINGS;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override // com.android.settings.KeyAndGestureShortcutStatHelperFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.wakeup_xiaoai_settings);
        this.mTitle = "launch_voice_assistant";
        boolean z = FeatureParser.getBoolean("support_ai_task", false);
        final PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("gesture_category");
        if (preferenceCategory != null) {
            preferenceCategory.setTitle(this.mContext.getResources().getString(R.string.xiaoai_global_shortcut));
            MiuiShortcut$Key.setGestureMap(this.mContext);
            for (String str : MiuiShortcut$Key.sGestureMap.get("launch_voice_assistant")) {
                if (!"long_press_power_key".equals(str) || !z) {
                    CheckBoxPreference checkBoxPreference = new CheckBoxPreference(getPrefContext());
                    checkBoxPreference.setTitle(MiuiShortcut$Key.getResourceForKey(str, this.mContext));
                    checkBoxPreference.setKey(str);
                    if (MiuiShortcut$Key.FEATURE_KNOCK.contains(str)) {
                        if (MiuiShortcut$System.hasKnockFeature(this.mContext)) {
                            checkBoxPreference.setSummary(MiuiShortcut$Key.getResourceForKey("knock_edge_area_invalid", this.mContext));
                        }
                    }
                    checkBoxPreference.setChecked(this.mTitle.equals(MiuiShortcut$Key.getKeyAndGestureShortcutSetFunction(this.mContext, str)));
                    checkBoxPreference.setOnPreferenceChangeListener(this);
                    preferenceCategory.addPreference(checkBoxPreference);
                }
            }
            if (preferenceCategory.getPreferenceCount() == 0) {
                getPreferenceScreen().removePreference(preferenceCategory);
            }
        }
        PreferenceCategory preferenceCategory2 = (PreferenceCategory) findPreference("key_item");
        RadioButtonPreferenceCategory radioButtonPreferenceCategory = (RadioButtonPreferenceCategory) findPreference("key_category");
        this.mKeyCategory = radioButtonPreferenceCategory;
        if (radioButtonPreferenceCategory != null && preferenceCategory2 != null) {
            preferenceCategory2.setTitle(this.mContext.getResources().getString(R.string.xiaoai_key_shortcut));
            if (MiuiShortcut$System.isFullScreenStatus(this.mContext)) {
                getPreferenceScreen().removePreference(preferenceCategory2);
            } else {
                Collections.addAll(this.mKeyShortcutList, MiuiShortcut$Key.KEY_SHORTCUT_ACTION);
                ArrayList<String> arrayList = this.mKeyShortcutList;
                arrayList.add(arrayList.size(), "key_none");
                Iterator<String> it = this.mKeyShortcutList.iterator();
                while (it.hasNext()) {
                    String next = it.next();
                    if (!"press_menu".equals(next)) {
                        RadioButtonPreference radioButtonPreference = new RadioButtonPreference(getPrefContext());
                        if (this.mTitle.equals(MiuiShortcut$Key.getKeyAndGestureShortcutSetFunction(this.mContext, next))) {
                            this.mSelectedRadioButton = radioButtonPreference;
                        }
                        radioButtonPreference.setLayoutResource(R.layout.miuix_preference_radiobutton_two_state_background);
                        radioButtonPreference.setKey(next);
                        radioButtonPreference.setTitle(MiuiShortcut$Key.getResourceForKey(next, this.mContext));
                        this.mKeyCategory.addPreference(radioButtonPreference);
                    }
                }
                if (this.mSelectedRadioButton == null) {
                    this.mSelectedRadioButton = (RadioButtonPreference) this.mKeyCategory.findPreference("key_none");
                }
                this.mKeyCategory.setCheckedPreference(this.mSelectedRadioButton);
            }
        }
        PreferenceCategory preferenceCategory3 = (PreferenceCategory) findPreference("other_category");
        if (preferenceCategory3 != null) {
            preferenceCategory3.setTitle(this.mContext.getResources().getString(R.string.xiaoai_other_shortcut));
            if (MiuiShortcut$System.hasKnockFeature(this.mContext)) {
                CheckBoxPreference checkBoxPreference2 = new CheckBoxPreference(getPrefContext());
                checkBoxPreference2.setKey("knock_gesture_v");
                checkBoxPreference2.setTitle(MiuiShortcut$Key.getResourceForKey(GestureSettingsTree.KNOCK_GESTURE_V_TITLE, this.mContext));
                checkBoxPreference2.setChecked(this.mTitle.equals(MiuiShortcut$Key.getKeyAndGestureShortcutSetFunction(this.mContext, "knock_gesture_v")));
                checkBoxPreference2.setOnPreferenceChangeListener(this);
                preferenceCategory3.addPreference(checkBoxPreference2);
            } else {
                getPreferenceScreen().removePreference(preferenceCategory3);
            }
        }
        this.mContentObserver = new ContentObserver(this.mContext.getMainThreadHandler()) { // from class: com.android.settings.WakeupXiaoaiSettingsFragment.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z2) {
                PreferenceCategory preferenceCategory4;
                CheckBoxPreference checkBoxPreference3;
                WakeupXiaoaiSettingsFragment wakeupXiaoaiSettingsFragment = WakeupXiaoaiSettingsFragment.this;
                wakeupXiaoaiSettingsFragment.mLongPressPowerKeyLaunchXiaoai = Settings.System.getIntForUser(wakeupXiaoaiSettingsFragment.mContext.getContentResolver(), "long_press_power_launch_xiaoai", UserHandle.myUserId(), 0) == 1;
                if (WakeupXiaoaiSettingsFragment.this.mLongPressPowerKeyLaunchXiaoai && (preferenceCategory4 = preferenceCategory) != null && (checkBoxPreference3 = (CheckBoxPreference) preferenceCategory4.findPreference("long_press_power_key")) != null) {
                    checkBoxPreference3.setChecked(true);
                }
                super.onChange(z2);
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("long_press_power_launch_xiaoai"), false, this.mContentObserver, -1);
        this.mContentObserver.onChange(false);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mContentObserver);
        super.onDestroy();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (((Boolean) obj).booleanValue()) {
            if (isNoOtherFunction(preference)) {
                MiuiSettings.System.putStringForUser(this.mContext.getContentResolver(), preference.getKey(), this.mTitle, -2);
                return true;
            }
            return false;
        }
        if (preference.getKey().equals("long_press_power_key") && this.mLongPressPowerKeyLaunchXiaoai) {
            Settings.System.putIntForUser(this.mContext.getContentResolver(), "long_press_power_launch_xiaoai", 0, UserHandle.myUserId());
        }
        MiuiSettings.System.putStringForUser(this.mContext.getContentResolver(), preference.getKey(), MiCloudStatusInfo.QuotaInfo.WARN_NONE, -2);
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (!MiuiShortcut$System.isFullScreenStatus(this.mContext) && this.mKeyShortcutList.contains(preference.getKey())) {
            if (!isNoOtherFunction(preference) || this.mSelectedRadioButton.getKey().equals(preference.getKey())) {
                this.mKeyCategory.setCheckedPreference(this.mSelectedRadioButton);
            } else {
                this.mKeyCategory.setCheckedPreference(preference);
                MiuiSettings.System.putStringForUser(this.mContext.getContentResolver(), preference.getKey(), this.mTitle, -2);
                MiuiSettings.System.putStringForUser(this.mContext.getContentResolver(), this.mSelectedRadioButton.getKey(), MiCloudStatusInfo.QuotaInfo.WARN_NONE, -2);
                this.mSelectedRadioButton = (RadioButtonPreference) preference;
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        RecommendPreference recommendPreference = (RecommendPreference) findPreference("miui_settings_recommendref_key");
        if (recommendPreference != null) {
            recommendPreference.setEnabled(false);
            if (MiuiShortcut$System.isFullScreenStatus(this.mContext)) {
                recommendPreference.setRecommendTips(this.mContext.getResources().getString(R.string.recommend_tip_wakeup_xiaoai));
            } else {
                getPreferenceScreen().removePreference(recommendPreference);
            }
        }
    }
}
