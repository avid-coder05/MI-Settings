package com.android.settings;

import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import miui.util.FeatureParser;

/* loaded from: classes.dex */
public class LedSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private CheckBoxPreference mBatteryLight;
    private DropDownPreference mBreathingLightColor;
    private DropDownPreference mBreathingLightFreq;
    private CheckBoxPreference mButtonLight;
    private DropDownPreference mButtonLightTimout;
    private DropDownPreference mCallBreathingLightColor;
    private DropDownPreference mCallBreathingLightFreq;
    private DropDownPreference mMmsBreathingLightColor;
    private DropDownPreference mMmsBreathingLightFreq;
    private CheckBoxPreference mNotificationPulse;
    private static final boolean SUPPORT_LED_COLOR = FeatureParser.getBoolean("support_led_color", true);
    private static final boolean SUPPORT_LED_FREQ = FeatureParser.getBoolean("support_led_freq", false);
    private static final boolean FRONT_FINGERPRINT_SENSOR = FeatureParser.getBoolean("front_fingerprint_sensor", false);
    private static final int DEFAULT_BATTERY_LED_ON = FeatureParser.getBoolean("default_battery_led_on", true) ? 1 : 0;

    private void removeUnusePreferences() {
        if (!SUPPORT_LED_COLOR) {
            if (this.mBreathingLightColor != null) {
                getPreferenceScreen().removePreference(this.mBreathingLightColor);
                this.mBreathingLightColor = null;
            }
            if (this.mCallBreathingLightColor != null) {
                getPreferenceScreen().removePreference(this.mCallBreathingLightColor);
                this.mCallBreathingLightColor = null;
            }
            if (this.mMmsBreathingLightColor != null) {
                getPreferenceScreen().removePreference(this.mMmsBreathingLightColor);
                this.mMmsBreathingLightColor = null;
            }
        }
        if (!SUPPORT_LED_FREQ) {
            if (this.mBreathingLightFreq != null) {
                getPreferenceScreen().removePreference(this.mBreathingLightFreq);
                this.mBreathingLightFreq = null;
            }
            if (this.mCallBreathingLightFreq != null) {
                getPreferenceScreen().removePreference(this.mCallBreathingLightFreq);
                this.mCallBreathingLightFreq = null;
            }
            if (this.mMmsBreathingLightFreq != null) {
                getPreferenceScreen().removePreference(this.mMmsBreathingLightFreq);
                this.mMmsBreathingLightFreq = null;
            }
        }
        if (Utils.isVoiceCapable(getActivity())) {
            return;
        }
        if (this.mCallBreathingLightColor != null) {
            getPreferenceScreen().removePreference(this.mCallBreathingLightColor);
            this.mCallBreathingLightColor = null;
        }
        if (this.mCallBreathingLightFreq != null) {
            getPreferenceScreen().removePreference(this.mCallBreathingLightFreq);
            this.mCallBreathingLightFreq = null;
        }
        if (this.mMmsBreathingLightColor != null) {
            getPreferenceScreen().removePreference(this.mMmsBreathingLightColor);
            this.mMmsBreathingLightColor = null;
        }
        if (this.mMmsBreathingLightFreq != null) {
            getPreferenceScreen().removePreference(this.mMmsBreathingLightFreq);
            this.mMmsBreathingLightFreq = null;
        }
    }

    private int setSummary(DropDownPreference dropDownPreference, int i, boolean z) {
        CharSequence[] entryValues = dropDownPreference.getEntryValues();
        int length = entryValues.length;
        int i2 = -1;
        int i3 = 0;
        while (true) {
            if (i3 >= length) {
                break;
            }
            CharSequence charSequence = entryValues[i3];
            i2++;
            if (i == (z ? Color.parseColor(charSequence.toString()) : Integer.valueOf(charSequence.toString()).intValue())) {
                dropDownPreference.setSummary(dropDownPreference.getEntries()[i2]);
                break;
            }
            i3++;
        }
        return i2;
    }

    private void setValue(DropDownPreference dropDownPreference, int i, boolean z) {
        int summary = setSummary(dropDownPreference, i, z);
        if (summary > -1) {
            dropDownPreference.setValueIndex(summary);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return LedSettings.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.led_settings);
        getActivity().setTitle(R.string.led_settings);
        this.mButtonLight = (CheckBoxPreference) findPreference("pref_button_light");
        this.mButtonLightTimout = (DropDownPreference) findPreference("button_light_timeout");
        if (FeatureParser.getBoolean("support_button_light", false)) {
            CheckBoxPreference checkBoxPreference = this.mButtonLight;
            if (checkBoxPreference != null) {
                checkBoxPreference.setOnPreferenceChangeListener(this);
            }
            DropDownPreference dropDownPreference = this.mButtonLightTimout;
            if (dropDownPreference != null) {
                dropDownPreference.setOnPreferenceChangeListener(this);
            }
        } else {
            if (this.mButtonLight != null) {
                getPreferenceScreen().removePreference(this.mButtonLight);
                this.mButtonLight = null;
            }
            if (this.mButtonLightTimout != null) {
                getPreferenceScreen().removePreference(this.mButtonLightTimout);
                this.mButtonLightTimout = null;
            }
        }
        if (this.mButtonLightTimout != null) {
            this.mButtonLightTimout.setValue(String.valueOf(Settings.System.getInt(getContentResolver(), "screen_buttons_timeout", 5000)));
            DropDownPreference dropDownPreference2 = this.mButtonLightTimout;
            dropDownPreference2.setSummary(dropDownPreference2.getEntry());
        }
        if (this.mButtonLight != null) {
            this.mButtonLight.setSummary(getActivity().getResources().getString(R.string.pref_button_light_summary, FRONT_FINGERPRINT_SENSOR ? "" : getActivity().getResources().getString(R.string.pref_button_light_summary_sub)));
            this.mButtonLight.setChecked(Settings.Secure.getInt(getContentResolver(), "screen_buttons_turn_on", 1) == 1);
        }
        CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) findPreference("notification_pulse");
        this.mNotificationPulse = checkBoxPreference2;
        checkBoxPreference2.setOnPreferenceChangeListener(this);
        DropDownPreference dropDownPreference3 = (DropDownPreference) findPreference("breathing_light_color");
        this.mBreathingLightColor = dropDownPreference3;
        dropDownPreference3.setOnPreferenceChangeListener(this);
        DropDownPreference dropDownPreference4 = (DropDownPreference) findPreference("breathing_light_freq");
        this.mBreathingLightFreq = dropDownPreference4;
        dropDownPreference4.setOnPreferenceChangeListener(this);
        DropDownPreference dropDownPreference5 = (DropDownPreference) findPreference("call_breathing_light_color");
        this.mCallBreathingLightColor = dropDownPreference5;
        dropDownPreference5.setOnPreferenceChangeListener(this);
        DropDownPreference dropDownPreference6 = (DropDownPreference) findPreference("call_breathing_light_freq");
        this.mCallBreathingLightFreq = dropDownPreference6;
        dropDownPreference6.setOnPreferenceChangeListener(this);
        DropDownPreference dropDownPreference7 = (DropDownPreference) findPreference("mms_breathing_light_color");
        this.mMmsBreathingLightColor = dropDownPreference7;
        dropDownPreference7.setOnPreferenceChangeListener(this);
        DropDownPreference dropDownPreference8 = (DropDownPreference) findPreference("mms_breathing_light_freq");
        this.mMmsBreathingLightFreq = dropDownPreference8;
        dropDownPreference8.setOnPreferenceChangeListener(this);
        int color = getResources().getColor(285605890);
        int i = Settings.System.getInt(getContentResolver(), "breathing_light_color", color);
        int i2 = Settings.System.getInt(getContentResolver(), "call_breathing_light_color", color);
        int i3 = Settings.System.getInt(getContentResolver(), "mms_breathing_light_color", color);
        setValue(this.mBreathingLightColor, i, true);
        setValue(this.mCallBreathingLightColor, i2, true);
        setValue(this.mMmsBreathingLightColor, i3, true);
        int integer = getResources().getInteger(285933599);
        int i4 = Settings.System.getInt(getContentResolver(), "breathing_light_freq", integer);
        int i5 = Settings.System.getInt(getContentResolver(), "call_breathing_light_freq", integer);
        int i6 = Settings.System.getInt(getContentResolver(), "mms_breathing_light_freq", integer);
        setValue(this.mBreathingLightFreq, i4, false);
        setValue(this.mCallBreathingLightFreq, i5, false);
        setValue(this.mMmsBreathingLightFreq, i6, false);
        this.mNotificationPulse.setChecked(Settings.System.getInt(getContentResolver(), "notification_light_pulse", 1) == 1);
        CheckBoxPreference checkBoxPreference3 = (CheckBoxPreference) findPreference("battery_light");
        this.mBatteryLight = checkBoxPreference3;
        checkBoxPreference3.setOnPreferenceChangeListener(this);
        this.mBatteryLight.setChecked(Settings.Secure.getInt(getContentResolver(), "battery_light_turn_on", DEFAULT_BATTERY_LED_ON) == 1);
        removeUnusePreferences();
    }

    /* JADX WARN: Removed duplicated region for block: B:66:0x0145  */
    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onPreferenceChange(androidx.preference.Preference r9, java.lang.Object r10) {
        /*
            Method dump skipped, instructions count: 331
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.LedSettings.onPreferenceChange(androidx.preference.Preference, java.lang.Object):boolean");
    }
}
