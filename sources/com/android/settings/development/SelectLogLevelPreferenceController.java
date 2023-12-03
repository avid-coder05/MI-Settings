package com.android.settings.development;

import android.content.Context;
import android.os.SystemProperties;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import miui.os.Build;

/* loaded from: classes.dex */
public class SelectLogLevelPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener {
    private DropDownPreference mLogdLogLevel;

    public SelectLogLevelPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mLogdLogLevel = (DropDownPreference) preferenceScreen.findPreference("select_logd_system_level");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "select_logd_system_level";
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        writeLogdLevelOption(obj);
        return true;
    }

    public void updateLogdLogLevel() {
        if (this.mLogdLogLevel != null) {
            String str = SystemProperties.get("persist.logd.limit", "");
            if (TextUtils.isEmpty(str)) {
                str = Build.IS_STABLE_VERSION ? "Warn" : "Info";
            }
            this.mLogdLogLevel.setValue(str);
            this.mLogdLogLevel.setSummary(str);
            this.mLogdLogLevel.setOnPreferenceChangeListener(this);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        updateLogdLogLevel();
    }

    public void writeLogdLevelOption(Object obj) {
        if (obj == null) {
            return;
        }
        String str = SystemProperties.get("persist.logd.limit", "");
        String obj2 = obj.toString();
        if (TextUtils.equals(str, obj2)) {
            return;
        }
        SystemProperties.set("persist.logd.limit", obj2);
        SystemProperties.set("ctl.start", "logd-reinit");
        updateLogdLogLevel();
    }
}
