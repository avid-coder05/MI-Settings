package com.android.settings.development;

import android.content.Context;
import android.os.SystemProperties;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;

/* loaded from: classes.dex */
public class BluetoothMapVersionPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static final String BLUETOOTH_MAP_VERSION_PROPERTY = "persist.bluetooth.mapversion";
    private final String[] mListSummaries;
    private final String[] mListValues;

    public BluetoothMapVersionPreferenceController(Context context) {
        super(context);
        this.mListValues = context.getResources().getStringArray(R.array.bluetooth_map_version_values);
        this.mListSummaries = context.getResources().getStringArray(R.array.bluetooth_map_versions);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_select_map_version";
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        SystemProperties.set(BLUETOOTH_MAP_VERSION_PROPERTY, obj.toString());
        updateState(this.mPreference);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        DropDownPreference dropDownPreference = (DropDownPreference) preference;
        String str = SystemProperties.get(BLUETOOTH_MAP_VERSION_PROPERTY);
        int i = 0;
        int i2 = 0;
        while (true) {
            String[] strArr = this.mListValues;
            if (i2 >= strArr.length) {
                break;
            } else if (TextUtils.equals(str, strArr[i2])) {
                i = i2;
                break;
            } else {
                i2++;
            }
        }
        dropDownPreference.setValue(this.mListValues[i]);
        dropDownPreference.setSummary(this.mListSummaries[i]);
    }
}
