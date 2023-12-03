package com.android.settings.display;

import android.app.UiModeManager;
import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.R;

/* loaded from: classes.dex */
public class DarkUISettingsRadioButtonsController {
    private Preference mFooter;
    UiModeManager mManager;

    public DarkUISettingsRadioButtonsController(Context context, Preference preference) {
        this.mManager = (UiModeManager) context.getSystemService(UiModeManager.class);
        this.mFooter = preference;
    }

    public static String modeToDescription(Context context, int i) {
        String[] stringArray = context.getResources().getStringArray(R.array.dark_ui_mode_entries);
        return i != 2 ? stringArray[1] : stringArray[0];
    }

    public String getDefaultKey() {
        int nightMode = this.mManager.getNightMode();
        updateFooter();
        return nightMode == 2 ? "key_dark_ui_settings_dark" : "key_dark_ui_settings_light";
    }

    public boolean setDefaultKey(String str) {
        str.hashCode();
        if (str.equals("key_dark_ui_settings_dark")) {
            this.mManager.setNightMode(2);
        } else if (!str.equals("key_dark_ui_settings_light")) {
            throw new IllegalStateException("Not a valid key for " + DarkUISettingsRadioButtonsController.class.getSimpleName() + ": " + str);
        } else {
            this.mManager.setNightMode(1);
        }
        updateFooter();
        return true;
    }

    public void updateFooter() {
        if (this.mManager.getNightMode() != 2) {
            this.mFooter.setSummary(R.string.dark_ui_settings_light_summary);
        } else {
            this.mFooter.setSummary(R.string.dark_ui_settings_dark_summary);
        }
    }
}
