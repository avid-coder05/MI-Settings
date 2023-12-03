package com.android.settings.datetime;

import android.content.Context;
import android.icu.text.TimeZoneFormat;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.AodStylePreferenceController;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.datetime.timezone.TimeZoneSettings;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.datetime.ZoneGetter;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* loaded from: classes.dex */
public class DualClockZoneController extends AbstractPreferenceController {
    private Context mContext;

    public DualClockZoneController(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return AodStylePreferenceController.RESIDENT_TIMEZONE;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(preference.getKey(), AodStylePreferenceController.RESIDENT_TIMEZONE)) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("miui_launch", true);
            new SubSettingLauncher(this.mContext).setDestination(TimeZoneSettings.class.getCanonicalName()).setArguments(bundle).setTitleText(preference.getTitle()).setSourceMetricsCategory(0).launch();
            return true;
        }
        return super.handlePreferenceTreeClick(preference);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof ValuePreference) {
            ((ValuePreference) preference).setShowRightArrow(true);
            TimeZone timeZone = TimeZone.getTimeZone(DualClockHealper.getDualTimeZoneID(preference.getContext()));
            Locale locale = Locale.getDefault();
            String dualZoneDisplayName = DualClockHealper.getDualZoneDisplayName(preference.getContext());
            CharSequence gmtOffsetText = ZoneGetter.getGmtOffsetText(TimeZoneFormat.getInstance(locale), locale, timeZone, new Date());
            preference.setSummary(dualZoneDisplayName);
            ((ValuePreference) preference).setValue(gmtOffsetText.toString());
        }
    }
}
