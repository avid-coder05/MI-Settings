package com.android.settings.wifi;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;

/* loaded from: classes2.dex */
public class CmccPreferenceController extends AbstractPreferenceController implements Preference.OnPreferenceChangeListener {
    private Context mContext;
    private PreferenceScreen mPreferenceScreen;

    public CmccPreferenceController(Context context) {
        super(context);
        this.mContext = context;
    }

    private List<Preference> getPreferenceList(PreferenceScreen preferenceScreen) {
        String[] strArr = {"connect_type", "select_ssid_type", "dialog_remind_type", "priority_type", "priority_settings"};
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < 5; i++) {
            Preference findPreference = preferenceScreen.findPreference(strArr[i]);
            if (findPreference != null) {
                arrayList.add(findPreference);
            }
        }
        return arrayList;
    }

    private void setWifiPriorityPreferenceState(Preference preference) {
        preference.setEnabled(Settings.System.getInt(this.mContext.getContentResolver(), "wifi_priority_type", 0) != 0);
    }

    private void updateWifiPriorityEnabledState() {
        for (Preference preference : getPreferenceList(this.mPreferenceScreen)) {
            if ("priority_settings".equals(preference.getKey())) {
                setWifiPriorityPreferenceState(preference);
            }
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
        for (Preference preference : getPreferenceList(preferenceScreen)) {
            if (isAvailable()) {
                updateState(preference);
                preference.setOnPreferenceChangeListener(this);
                updateWifiPriorityEnabledState();
            } else {
                preferenceScreen.removePreference(preference);
            }
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (preference.getKey().equals("priority_settings")) {
            Intent intent = new Intent();
            intent.setClassName("com.android.settings", "com.android.settings.wifi.WifiPrioritySettings");
            this.mContext.startActivity(intent);
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return Build.IS_CM_CUSTOMIZATION;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        if ("priority_type".equals(key)) {
            Settings.System.putInt(this.mContext.getContentResolver(), "wifi_priority_type", ((Boolean) obj).booleanValue() ? 1 : 0);
            updateWifiPriorityEnabledState();
            return true;
        } else if ("connect_type".equals(key)) {
            Settings.System.putInt(this.mContext.getContentResolver(), "wifi_connect_type", Integer.parseInt((String) obj));
            DropDownPreference dropDownPreference = (DropDownPreference) preference;
            dropDownPreference.setValue(String.valueOf(obj));
            dropDownPreference.setSummary(dropDownPreference.getEntry());
            return true;
        } else if ("select_ssid_type".equals(key)) {
            Settings.System.putInt(this.mContext.getContentResolver(), "wifi_select_ssid_type", Integer.parseInt((String) obj));
            DropDownPreference dropDownPreference2 = (DropDownPreference) preference;
            dropDownPreference2.setValue(String.valueOf(obj));
            dropDownPreference2.setSummary(dropDownPreference2.getEntry());
            return true;
        } else if ("dialog_remind_type".equals(key)) {
            Settings.System.putInt(this.mContext.getContentResolver(), "wifi_dialog_remind_type", ((Boolean) obj).booleanValue() ? 1 : 0);
            return true;
        } else {
            return true;
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        String key = preference.getKey();
        if ("priority_type".equals(key)) {
            ((CheckBoxPreference) preference).setChecked(Settings.System.getInt(this.mContext.getContentResolver(), "wifi_priority_type", 0) != 0);
        } else if ("priority_settings".equals(key)) {
            setWifiPriorityPreferenceState(preference);
        } else if ("connect_type".equals(key)) {
            DropDownPreference dropDownPreference = (DropDownPreference) preference;
            dropDownPreference.setValue(String.valueOf(Settings.System.getInt(this.mContext.getContentResolver(), "wifi_connect_type", 0)));
            dropDownPreference.setSummary(dropDownPreference.getEntry());
        } else if ("select_ssid_type".equals(key)) {
            DropDownPreference dropDownPreference2 = (DropDownPreference) preference;
            dropDownPreference2.setValue(String.valueOf(Settings.System.getInt(this.mContext.getContentResolver(), "wifi_select_ssid_type", 0)));
            dropDownPreference2.setSummary(dropDownPreference2.getEntry());
        } else if ("dialog_remind_type".equals(key)) {
            ((CheckBoxPreference) preference).setChecked(Settings.System.getInt(this.mContext.getContentResolver(), "wifi_dialog_remind_type", 0) != 0);
        }
    }
}
