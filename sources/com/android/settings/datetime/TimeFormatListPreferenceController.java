package com.android.settings.datetime;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import com.miui.enterprise.RestrictionsHelper;

/* loaded from: classes.dex */
public class TimeFormatListPreferenceController extends AbstractPreferenceController implements Preference.OnPreferenceChangeListener {
    private DropDownPreference mListPreference;
    private final UpdateTimeAndDateCallback mUpdateTimeAndDateCallback;

    public TimeFormatListPreferenceController(Context context, UpdateTimeAndDateCallback updateTimeAndDateCallback) {
        super(context);
        this.mUpdateTimeAndDateCallback = updateTimeAndDateCallback;
    }

    static String getTimeFormatSelection(Context context) {
        return Settings.System.getString(context.getContentResolver(), "time_12_24");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mListPreference = (DropDownPreference) preferenceScreen.findPreference("select_time_format");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "select_time_format";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (obj == null) {
            return false;
        }
        Settings.System.putInt(this.mContext.getContentResolver(), "settings_set_time_format", 1);
        String obj2 = obj.toString();
        TimeFormatPreferenceController.update24HourFormat(this.mContext, TextUtils.equals("null", obj2) ? null : Boolean.valueOf(TextUtils.equals("24", obj2)));
        this.mUpdateTimeAndDateCallback.updateTimeAndDateDisplay(this.mContext);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (this.mListPreference == null) {
            return;
        }
        if (RestrictionsHelper.hasRestriction(preference.getContext(), "disallow_timeset")) {
            this.mListPreference.setEnabled(false);
        }
        String timeFormatSelection = getTimeFormatSelection(this.mContext);
        DropDownPreference dropDownPreference = this.mListPreference;
        if (timeFormatSelection == null) {
            timeFormatSelection = "null";
        }
        dropDownPreference.setValue(timeFormatSelection);
        DropDownPreference dropDownPreference2 = this.mListPreference;
        dropDownPreference2.setSummary(dropDownPreference2.getEntry());
        this.mListPreference.setOnPreferenceChangeListener(this);
    }
}
