package com.android.settings;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.recommend.PageIndexManager;
import com.android.settings.report.InternationalCompat;
import com.android.settings.search.tree.GestureSettingsTree;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.cloud.sync.MiCloudStatusInfo;

/* loaded from: classes.dex */
public class GestureShortcutSettingsFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private Context mContext;
    private DropDownPreference mDropDownFpTap;
    private FingerprintHelper mFingerprintHelper;

    private String getSummary(List<String> list) {
        StringBuilder sb = new StringBuilder();
        if (list.size() != 0) {
            Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                sb.append(MiuiShortcut$Key.getResourceForKey(it.next(), this.mContext));
                sb.append("/");
            }
            return sb.substring(0, sb.length() - 1);
        }
        return "";
    }

    private void initFpDoubleTap() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        MiuiShortcut$Key.initFunctionsAndValues(this.mContext, arrayList, arrayList2, this.mDropDownFpTap.getKey());
        this.mDropDownFpTap.setEntries((CharSequence[]) arrayList.toArray(new String[arrayList.size()]));
        this.mDropDownFpTap.setEntryValues((CharSequence[]) arrayList2.toArray(new String[arrayList2.size()]));
        this.mDropDownFpTap.setOnPreferenceChangeListener(this);
    }

    private void updateState() {
        if (this.mDropDownFpTap != null && SettingsFeatures.IS_SUPPORT_FINGERPRINT_TAP && this.mFingerprintHelper.isHardwareDetected()) {
            String keyAndGestureShortcutSetFunction = MiuiShortcut$Key.getKeyAndGestureShortcutSetFunction(this.mContext, this.mDropDownFpTap.getKey());
            if (keyAndGestureShortcutSetFunction == null || keyAndGestureShortcutSetFunction.isEmpty()) {
                keyAndGestureShortcutSetFunction = MiCloudStatusInfo.QuotaInfo.WARN_NONE;
            }
            this.mDropDownFpTap.setValue(keyAndGestureShortcutSetFunction);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public int getPageIndex() {
        if (MiuiShortcut$System.isFullScreenStatus(this.mContext)) {
            return -1;
        }
        return PageIndexManager.PAGE_GESTURE_FUNCTION_SETTINGS;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.gesture_shortcut_settings);
        Context context = getContext();
        this.mContext = context;
        boolean hasBackTapSensorFeature = SettingsFeatures.hasBackTapSensorFeature(context);
        MiuiShortcut$Key.setGestureMap(this.mContext);
        this.mFingerprintHelper = new FingerprintHelper(this.mContext);
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("function_shortcut");
        PreferenceCategory preferenceCategory2 = (PreferenceCategory) findPreference("advanced_gesture");
        if (preferenceCategory != null) {
            Iterator<String> it = MiuiShortcut$Key.mHidenPreferenceList.iterator();
            while (it.hasNext()) {
                preferenceCategory.removePreference((ValuePreference) findPreference(it.next()));
            }
            for (Map.Entry<String, List<String>> entry : MiuiShortcut$Key.sGestureMap.entrySet()) {
                ValuePreference valuePreference = (ValuePreference) findPreference(entry.getKey());
                if ("mi_pay".equals(entry.getKey())) {
                    if (!MiuiShortcut$Key.isTSMClientInstalled(this.mContext)) {
                        getPreferenceScreen().removePreference(valuePreference);
                    } else if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.nfc")) {
                        valuePreference.setTitle(this.mContext.getResources().getString(R.string.mi_pay_summary_without_nfc));
                    }
                }
                valuePreference.setSummary(getSummary(entry.getValue()));
            }
        }
        if (preferenceCategory2 != null) {
            ValuePreference valuePreference2 = (ValuePreference) preferenceCategory2.findPreference("knock_gesture_v");
            if (valuePreference2 != null) {
                if (MiuiShortcut$System.hasKnockFeature(getContext())) {
                    valuePreference2.setTitle(MiuiShortcut$Key.getResourceForKey(GestureSettingsTree.KNOCK_GESTURE_V_TITLE, this.mContext));
                } else {
                    preferenceCategory2.removePreference(valuePreference2);
                }
            }
            ValuePreference valuePreference3 = (ValuePreference) preferenceCategory2.findPreference("back_tap");
            if (valuePreference3 != null && !hasBackTapSensorFeature) {
                preferenceCategory2.removePreference(valuePreference3);
            }
            DropDownPreference dropDownPreference = (DropDownPreference) preferenceCategory2.findPreference("fingerprint_double_tap");
            this.mDropDownFpTap = dropDownPreference;
            if (dropDownPreference != null) {
                if (SettingsFeatures.IS_SUPPORT_FINGERPRINT_TAP && this.mFingerprintHelper.isHardwareDetected()) {
                    initFpDoubleTap();
                } else {
                    preferenceCategory2.removePreference(this.mDropDownFpTap);
                }
            }
            if (preferenceCategory2.getPreferenceCount() == 0) {
                getPreferenceScreen().removePreference(preferenceCategory2);
            }
        }
        InternationalCompat.trackReportEvent("setting_Additional_settings_btnshortcut");
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        DropDownPreference dropDownPreference = this.mDropDownFpTap;
        if (dropDownPreference != null) {
            dropDownPreference.setOnPreferenceChangeListener(null);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        DropDownPreference dropDownPreference = this.mDropDownFpTap;
        if (dropDownPreference == null || !dropDownPreference.getKey().equals(preference.getKey())) {
            return true;
        }
        String str = (String) obj;
        this.mDropDownFpTap.setValue(str);
        Settings.System.putStringForUser(getContentResolver(), this.mDropDownFpTap.getKey(), str, -2);
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateState();
    }
}
