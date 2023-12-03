package com.android.settings.development;

import android.app.UiModeManager;
import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;

/* loaded from: classes.dex */
public class DarkUIPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    private final UiModeManager mUiModeManager;

    public DarkUIPreferenceController(Context context) {
        this(context, (UiModeManager) context.getSystemService(UiModeManager.class));
    }

    DarkUIPreferenceController(Context context, UiModeManager uiModeManager) {
        super(context);
        this.mUiModeManager = uiModeManager;
    }

    private String modeToDescription(int i) {
        String[] stringArray = this.mContext.getResources().getStringArray(R.array.dark_ui_mode_entries);
        return i != 2 ? stringArray[1] : stringArray[0];
    }

    private int modeToInt(String str) {
        char c;
        int hashCode = str.hashCode();
        if (hashCode != 3521) {
            if (hashCode == 119527 && str.equals("yes")) {
                c = 0;
            }
            c = 65535;
        } else {
            if (str.equals("no")) {
                c = 1;
            }
            c = 65535;
        }
        return c != 0 ? 1 : 2;
    }

    private String modeToString(int i) {
        return i != 2 ? "no" : "yes";
    }

    private void updateSummary(Preference preference) {
        int nightMode = this.mUiModeManager.getNightMode();
        ((DropDownPreference) preference).setValue(modeToString(nightMode));
        preference.setSummary(modeToDescription(nightMode));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "dark_ui_mode";
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        this.mUiModeManager.setNightMode(modeToInt((String) obj));
        updateSummary(preference);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updateSummary(preference);
    }
}
