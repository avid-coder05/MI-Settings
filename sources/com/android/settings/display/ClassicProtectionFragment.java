package com.android.settings.display;

import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.view.View;
import androidx.preference.Preference;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.PaperModeTipPreference;
import com.android.settings.widget.SeekBarPreference;
import miui.util.FeatureParser;
import miui.vip.VipService;
import miuix.util.Log;

/* loaded from: classes.dex */
public class ClassicProtectionFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final float PER_LEVEL;
    private static final float TEXTURE_MAX_LEVEL;
    private static final float TEXTURE_MIN_LEVEL;
    private static Boolean sIsLcd;
    private final String DEVICE_INFORMATION_FILE = "/sys/devices/soc0/soc_id";
    private boolean isSupportHDRMode;
    private PaperModeTipPreference mPaperHintPref;
    private String sDeviceInformation;
    private Boolean sIsSupportedHdr;
    private TemperatureSeekBarPreference tempPreference;

    static {
        float f = MiuiSettings.ScreenEffect.PAPER_MODE_MAX_LEVEL;
        TEXTURE_MAX_LEVEL = f;
        float floatValue = FeatureParser.getFloat("paper_mode_min_level", 1.0f).floatValue();
        TEXTURE_MIN_LEVEL = floatValue;
        PER_LEVEL = (f - floatValue) / 1000.0f;
    }

    private int getPaperModeLevel() {
        return Settings.System.getInt(getContext().getContentResolver(), "screen_paper_mode_level", MiuiSettings.ScreenEffect.DEFAULT_PAPER_MODE_LEVEL);
    }

    public static boolean isLcd() {
        if (sIsLcd == null) {
            sIsLcd = Boolean.valueOf(("oled".equals(SystemProperties.get("ro.vendor.display.type")) || "oled".equals(SystemProperties.get("ro.display.type"))) ? false : true);
        }
        return sIsLcd.booleanValue();
    }

    /* JADX WARN: Removed duplicated region for block: B:44:0x004f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:22:0x0047 -> B:41:0x0058). Please submit an issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean isSupportedHdrDevice() {
        /*
            r7 = this;
            java.lang.String r0 = "IOException"
            java.lang.String r1 = "ClassicProtectionFragment"
            java.lang.Boolean r2 = r7.sIsSupportedHdr
            if (r2 != 0) goto L58
            java.lang.Boolean r2 = java.lang.Boolean.FALSE
            r7.sIsSupportedHdr = r2
            r2 = 0
            java.io.BufferedReader r3 = new java.io.BufferedReader     // Catch: java.lang.Throwable -> L37 java.io.IOException -> L39
            java.io.FileReader r4 = new java.io.FileReader     // Catch: java.lang.Throwable -> L37 java.io.IOException -> L39
            java.lang.String r5 = "/sys/devices/soc0/soc_id"
            r4.<init>(r5)     // Catch: java.lang.Throwable -> L37 java.io.IOException -> L39
            r3.<init>(r4)     // Catch: java.lang.Throwable -> L37 java.io.IOException -> L39
            java.lang.String r2 = r3.readLine()     // Catch: java.io.IOException -> L35 java.lang.Throwable -> L4b
            r7.sDeviceInformation = r2     // Catch: java.io.IOException -> L35 java.lang.Throwable -> L4b
            if (r2 == 0) goto L31
            java.lang.String r2 = r2.trim()     // Catch: java.io.IOException -> L35 java.lang.Throwable -> L4b
            java.lang.String r4 = "321"
            boolean r2 = r2.equals(r4)     // Catch: java.io.IOException -> L35 java.lang.Throwable -> L4b
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r2)     // Catch: java.io.IOException -> L35 java.lang.Throwable -> L4b
            r7.sIsSupportedHdr = r2     // Catch: java.io.IOException -> L35 java.lang.Throwable -> L4b
        L31:
            r3.close()     // Catch: java.io.IOException -> L46
            goto L58
        L35:
            r2 = move-exception
            goto L3d
        L37:
            r7 = move-exception
            goto L4d
        L39:
            r3 = move-exception
            r6 = r3
            r3 = r2
            r2 = r6
        L3d:
            miuix.util.Log.e(r1, r0, r2)     // Catch: java.lang.Throwable -> L4b
            if (r3 == 0) goto L58
            r3.close()     // Catch: java.io.IOException -> L46
            goto L58
        L46:
            r2 = move-exception
            miuix.util.Log.e(r1, r0, r2)
            goto L58
        L4b:
            r7 = move-exception
            r2 = r3
        L4d:
            if (r2 == 0) goto L57
            r2.close()     // Catch: java.io.IOException -> L53
            goto L57
        L53:
            r2 = move-exception
            miuix.util.Log.e(r1, r0, r2)
        L57:
            throw r7
        L58:
            java.lang.Boolean r0 = r7.sIsSupportedHdr
            boolean r0 = r0.booleanValue()
            if (r0 != 0) goto L67
            boolean r7 = r7.isSupportHDRMode
            if (r7 == 0) goto L65
            goto L67
        L65:
            r7 = 0
            goto L68
        L67:
            r7 = 1
        L68:
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.display.ClassicProtectionFragment.isSupportedHdrDevice():boolean");
    }

    private void setPaperModeLevel(int i) {
        if (i != getPaperModeLevel()) {
            Settings.System.putInt(getContext().getContentResolver(), "screen_paper_mode_level", i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateHintPref() {
        getPreferenceScreen().removePreference(this.mPaperHintPref);
        updateSideEffectPref();
        updateHDRTipPref();
    }

    private void updatePreference() {
        int i = Settings.System.getInt(getContext().getContentResolver(), "screen_auto_adjust", 1);
        if (MiuiUtils.supportSmartEyeCare() && i == 1) {
            this.tempPreference.setEnabled(false);
        }
        if (MiuiUtils.supportSmartEyeCare() && i == 1) {
            this.mPaperHintPref.setTitle(R.string.hint_unadjustable_text);
        }
    }

    private void updateSideEffectPref() {
        if (isLcd()) {
            if (((float) this.tempPreference.getProgress()) / 1000.0f > 0.7f) {
                this.mPaperHintPref.setTitle(R.string.paper_mode_side_effect_hint);
                getPreferenceScreen().addPreference(this.mPaperHintPref);
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.classic_protection_mode);
        this.isSupportHDRMode = MiuiUtils.isSupportHDRMode();
        TemperatureSeekBarPreference temperatureSeekBarPreference = (TemperatureSeekBarPreference) findPreference("adjust_temperature");
        this.tempPreference = temperatureSeekBarPreference;
        temperatureSeekBarPreference.setMax(VipService.VIP_SERVICE_FAILURE);
        this.tempPreference.setProgress((int) ((getPaperModeLevel() - TEXTURE_MIN_LEVEL) / PER_LEVEL));
        this.tempPreference.setContinuousUpdates(true);
        this.tempPreference.setOnPreferenceChangeListener(this);
        this.tempPreference.setStopTrackingTouchListener(new SeekBarPreference.StopTrackingTouchListener() { // from class: com.android.settings.display.ClassicProtectionFragment.1
            @Override // com.android.settings.widget.SeekBarPreference.StopTrackingTouchListener
            public void onStopTrackingTouch() {
                ClassicProtectionFragment.this.updateHintPref();
            }
        });
        this.mPaperHintPref = (PaperModeTipPreference) findPreference("paper_mode_hdr_hint");
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if ("adjust_temperature".equals(preference.getKey())) {
            setPaperModeLevel((int) ((((Integer) obj).intValue() * PER_LEVEL) + TEXTURE_MIN_LEVEL));
            return true;
        }
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        updatePreference();
    }

    public void updateHDRTipPref() {
        if ((this.tempPreference.getProgress() * PER_LEVEL) + 1.0f >= 4.0f && isSupportedHdrDevice()) {
            this.mPaperHintPref.setTitle(R.string.screen_paper_mode_hdr_toast_new);
            getPreferenceScreen().addPreference(this.mPaperHintPref);
            Log.d("ClassicProtectionFragment", "updateHDRTipPref: true");
            return;
        }
        Log.d("ClassicProtectionFragment", "updateHDRTipPref: false " + isSupportedHdrDevice());
    }
}
