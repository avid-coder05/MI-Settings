package com.android.settings.datetime;

import android.content.Context;
import android.icu.text.TimeZoneFormat;
import android.icu.text.TimeZoneNames;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.datetime.ZoneGetter;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import com.miui.enterprise.RestrictionsHelper;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: classes.dex */
public class TimeZonePreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private final AutoTimeZonePreferenceController mAutoTimeZonePreferenceController;

    public TimeZonePreferenceController(Context context, AutoTimeZonePreferenceController autoTimeZonePreferenceController) {
        super(context);
        this.mAutoTimeZonePreferenceController = autoTimeZonePreferenceController;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "timezone";
    }

    CharSequence getTimeZoneOffsetAndName() {
        Calendar calendar = Calendar.getInstance();
        return ZoneGetter.getTimeZoneOffsetAndName(this.mContext, calendar.getTimeZone(), calendar.getTime());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof ValuePreference) {
            Calendar calendar = Calendar.getInstance();
            Locale locale = Locale.getDefault();
            TimeZone timeZone = calendar.getTimeZone();
            Date time = calendar.getTime();
            ValuePreference valuePreference = (ValuePreference) preference;
            valuePreference.setShowRightArrow(true);
            valuePreference.setValue(ZoneGetter.getGmtOffsetText(TimeZoneFormat.getInstance(locale), locale, timeZone, time).toString());
            valuePreference.setSummary(ZoneGetter.getZoneLongName(TimeZoneNames.getInstance(locale), timeZone, time));
        } else {
            preference.setSummary(getTimeZoneOffsetAndName());
        }
        preference.setEnabled(!this.mAutoTimeZonePreferenceController.isEnabled());
        if (RestrictionsHelper.hasRestriction(preference.getContext(), "disallow_timeset")) {
            preference.setEnabled(false);
        }
    }
}
