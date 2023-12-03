package com.android.settings.accessibility;

import android.os.Vibrator;
import com.android.settings.R;

/* loaded from: classes.dex */
public class NotificationVibrationPreferenceFragment extends VibrationPreferenceFragment {
    @Override // com.android.settings.accessibility.VibrationPreferenceFragment
    protected int getDefaultVibrationIntensity() {
        return ((Vibrator) getContext().getSystemService(Vibrator.class)).getDefaultNotificationVibrationIntensity();
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1293;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.accessibility_notification_vibration_settings;
    }

    @Override // com.android.settings.accessibility.VibrationPreferenceFragment
    protected int getPreviewVibrationAudioAttributesUsage() {
        return 5;
    }

    @Override // com.android.settings.accessibility.VibrationPreferenceFragment
    protected String getVibrationEnabledSetting() {
        return "";
    }

    @Override // com.android.settings.accessibility.VibrationPreferenceFragment
    protected String getVibrationIntensitySetting() {
        return "notification_vibration_intensity";
    }
}
