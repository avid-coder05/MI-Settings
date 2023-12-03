package com.android.settings.development;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import miui.hardware.display.DisplayFeatureManager;
import miui.os.DeviceFeature;

/* loaded from: classes.dex */
public class SimulateColorSpacePreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static final int SETTING_VALUE_OFF = 0;
    static final int SETTING_VALUE_ON = 1;

    public SimulateColorSpacePreferenceController(Context context) {
        super(context);
    }

    private void updateSimulateColorSpace() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        boolean z = Settings.Secure.getInt(contentResolver, "accessibility_display_daltonizer_enabled", 0) != 0;
        DropDownPreference dropDownPreference = (DropDownPreference) this.mPreference;
        if (!z) {
            dropDownPreference.setValue(Integer.toString(-1));
            return;
        }
        String num = Integer.toString(Settings.Secure.getInt(contentResolver, "accessibility_display_daltonizer", -1));
        dropDownPreference.setValue(num);
        if (dropDownPreference.findIndexOfValue(num) >= 0) {
            dropDownPreference.setSummary("%s");
            return;
        }
        Resources resources = this.mContext.getResources();
        dropDownPreference.setSummary(resources.getString(R.string.daltonizer_type_overridden, resources.getString(R.string.accessibility_display_daltonizer_preference_title)));
    }

    private boolean usingDevelopmentColorSpace() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        if (Settings.Secure.getInt(contentResolver, "accessibility_display_daltonizer_enabled", 0) != 0) {
            if (((DropDownPreference) this.mPreference).findIndexOfValue(Integer.toString(Settings.Secure.getInt(contentResolver, "accessibility_display_daltonizer", -1))) >= 0) {
                return true;
            }
        }
        return false;
    }

    private void writeSimulateColorSpace(Object obj) {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        int parseInt = Integer.parseInt(obj.toString());
        if (parseInt < 0) {
            Settings.Secure.putInt(contentResolver, "accessibility_display_daltonizer_enabled", 0);
            if (DeviceFeature.SCREEN_EFFECT_CONFLICT) {
                DisplayFeatureManager.getInstance().setScreenEffect(15, 0);
                return;
            }
            return;
        }
        if (DeviceFeature.SCREEN_EFFECT_CONFLICT && Settings.Secure.getInt(contentResolver, "accessibility_display_daltonizer_enabled", 0) == 0) {
            DisplayFeatureManager.getInstance().setScreenEffect(15, 1);
        }
        Settings.Secure.putInt(contentResolver, "accessibility_display_daltonizer_enabled", 1);
        Settings.Secure.putInt(contentResolver, "accessibility_display_daltonizer", parseInt);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "simulate_color_space";
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsDisabled() {
        super.onDeveloperOptionsDisabled();
        if (usingDevelopmentColorSpace()) {
            writeSimulateColorSpace(-1);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        writeSimulateColorSpace(obj);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updateSimulateColorSpace();
    }
}
