package com.android.settings.datetime;

import android.content.Context;
import android.content.DialogInterface;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.AodStylePreferenceController;
import com.android.settings.R;
import com.android.settings.report.InternationalCompat;
import com.android.settingslib.core.AbstractPreferenceController;
import miui.util.FeatureParser;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class AutoDualClockController extends AbstractPreferenceController implements Preference.OnPreferenceChangeListener {
    public AutoDualClockController(Context context) {
        super(context);
    }

    private void updateAutoDualClockPreference(Preference preference, boolean z) {
        Settings.System.putInt(preference.getContext().getContentResolver(), AodStylePreferenceController.AUTO_DUAL_CLOCK, z ? 1 : 0);
        if (z) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
            if (FeatureParser.getBoolean("support_aod", false)) {
                builder.setMessage(R.string.dual_clock_aod_open_message);
            } else {
                builder.setMessage(R.string.dual_clock_open_message);
            }
            builder.setPositiveButton(R.string.dual_clock_ok, (DialogInterface.OnClickListener) null);
            builder.setCancelable(false);
            builder.show();
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return AodStylePreferenceController.AUTO_DUAL_CLOCK;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (TextUtils.equals(preference.getKey(), AodStylePreferenceController.AUTO_DUAL_CLOCK) && (obj instanceof Boolean)) {
            InternationalCompat.trackReportSwitchStatus("setting_Additional_settings_doubleclock", obj);
            updateAutoDualClockPreference(preference, ((Boolean) obj).booleanValue());
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof SwitchPreference) {
            ((SwitchPreference) preference).setChecked(Settings.System.getInt(preference.getContext().getContentResolver(), AodStylePreferenceController.AUTO_DUAL_CLOCK, 0) == 1);
        }
        if (FeatureParser.getBoolean("support_aod", false)) {
            preference.setSummary(R.string.auto_dual_clock_aod_summary);
        } else {
            preference.setSummary(R.string.auto_dual_clock_summary);
        }
    }
}
