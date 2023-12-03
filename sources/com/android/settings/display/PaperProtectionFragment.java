package com.android.settings.display;

import android.os.Bundle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.view.View;
import androidx.preference.Preference;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.display.util.PaperConstants;
import com.android.settings.widget.MiuiSeekBarPreference;
import com.android.settings.widget.PaperModeTipPreference;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import miui.util.FeatureParser;
import miui.vip.VipService;
import miuix.util.Log;

/* loaded from: classes.dex */
public class PaperProtectionFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final int EYECARE_MAX_LEVEL;
    private static final float EYECARE_MIN_LEVEL;
    private static final int PAPER_MODE_MAX_LEVEL;
    private static final float PAPER_MODE_MIN_LEVEL;
    private static final float PER_LEVEL;
    private static final float PER_TEXTURE_LEVEL;
    private DropDownPreference colorTypePreference;
    private PaperModeTipPreference hintPreference;
    private boolean mAtuoAdjustState;
    private TemperatureSeekBarPreference paperTempPreference;
    private Preference resetPreference;
    private MiuiSeekBarPreference texturePreference;

    static {
        int i = MiuiSettings.ScreenEffect.PAPER_MODE_MAX_LEVEL;
        PAPER_MODE_MAX_LEVEL = i;
        float floatValue = FeatureParser.getFloat("paper_mode_min_level", 1.0f).floatValue();
        PAPER_MODE_MIN_LEVEL = floatValue;
        PER_LEVEL = (i - floatValue) / 1000.0f;
        int integer = FeatureParser.getInteger("paper_eyecare_max_texture", 25);
        EYECARE_MAX_LEVEL = integer;
        float floatValue2 = FeatureParser.getFloat("paper_eyecare_min_texture", 1.0f).floatValue();
        EYECARE_MIN_LEVEL = floatValue2;
        PER_TEXTURE_LEVEL = (integer - floatValue2) / 1000.0f;
    }

    private int getPaperEyeCareLevel() {
        return Settings.System.getInt(getContext().getContentResolver(), "screen_texture_eyecare_level", PaperConstants.DEFAULT_TEXTURE_EYECARE_LEVEL);
    }

    private int getPaperModeLevel() {
        return Settings.System.getInt(getContext().getContentResolver(), "screen_paper_texture_level", (int) PaperConstants.DEFAULT_TEXTURE_MODE_LEVEL);
    }

    private int getTextureColorType() {
        return Settings.System.getInt(getContext().getContentResolver(), "screen_texture_color_type", 0);
    }

    private void resetDefault() {
        if (!MiuiUtils.supportSmartEyeCare() || !this.mAtuoAdjustState) {
            TemperatureSeekBarPreference temperatureSeekBarPreference = this.paperTempPreference;
            float f = PaperConstants.DEFAULT_TEXTURE_MODE_LEVEL;
            temperatureSeekBarPreference.setProgress((int) ((f - PAPER_MODE_MIN_LEVEL) / PER_LEVEL));
            Settings.System.putInt(getContext().getContentResolver(), "screen_paper_texture_level", (int) f);
        }
        MiuiSeekBarPreference miuiSeekBarPreference = this.texturePreference;
        int i = PaperConstants.DEFAULT_TEXTURE_EYECARE_LEVEL;
        miuiSeekBarPreference.setProgress((int) ((i - EYECARE_MIN_LEVEL) / PER_TEXTURE_LEVEL));
        this.colorTypePreference.setValueIndex(0);
        Settings.System.putInt(getContext().getContentResolver(), "screen_texture_eyecare_level", i);
        Settings.System.putInt(getContext().getContentResolver(), "screen_texture_color_type", 0);
    }

    private void setEyeCareLevel(int i) {
        if (i != getPaperEyeCareLevel()) {
            Log.d("setEyeCareLevel ", "previous level " + getPaperEyeCareLevel() + ", new level " + i);
            Settings.System.putInt(getContext().getContentResolver(), "screen_texture_eyecare_level", i);
        }
    }

    private void setPaperModeLevel(int i) {
        if (i != getPaperModeLevel()) {
            Settings.System.putInt(getContext().getContentResolver(), "screen_paper_texture_level", i);
        }
    }

    private void setTextureColorType(int i) {
        Settings.System.putInt(getContext().getContentResolver(), "screen_texture_color_type", i);
    }

    private void updatePreference() {
        PaperModeTipPreference paperModeTipPreference;
        if (MiuiUtils.supportSmartEyeCare() && this.mAtuoAdjustState) {
            this.paperTempPreference.setEnabled(false);
        }
        if (MiuiUtils.supportSmartEyeCare() && (paperModeTipPreference = this.hintPreference) != null) {
            paperModeTipPreference.setVisible(this.mAtuoAdjustState);
        }
        if (!MiuiUtils.isSecondSpace(getContext()) || this.texturePreference == null) {
            return;
        }
        getPreferenceScreen().removePreference(this.texturePreference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.paper_protection_mode);
        TemperatureSeekBarPreference temperatureSeekBarPreference = (TemperatureSeekBarPreference) findPreference("adjust_paper_mode");
        this.paperTempPreference = temperatureSeekBarPreference;
        temperatureSeekBarPreference.setMax(VipService.VIP_SERVICE_FAILURE);
        this.paperTempPreference.setProgress((int) ((getPaperModeLevel() - PAPER_MODE_MIN_LEVEL) / PER_LEVEL));
        this.paperTempPreference.setContinuousUpdates(true);
        this.paperTempPreference.setOnPreferenceChangeListener(this);
        this.paperTempPreference.setOnPreferenceClickListener(this);
        MiuiSeekBarPreference miuiSeekBarPreference = (MiuiSeekBarPreference) findPreference("adjust_paper_texture");
        this.texturePreference = miuiSeekBarPreference;
        miuiSeekBarPreference.setMax(VipService.VIP_SERVICE_FAILURE);
        this.texturePreference.setProgress((int) ((getPaperEyeCareLevel() - EYECARE_MIN_LEVEL) / PER_TEXTURE_LEVEL));
        this.texturePreference.setContinuousUpdates(true);
        this.texturePreference.setOnPreferenceChangeListener(this);
        this.texturePreference.setOnPreferenceClickListener(this);
        DropDownPreference dropDownPreference = (DropDownPreference) findPreference("paper_color");
        this.colorTypePreference = dropDownPreference;
        dropDownPreference.setOnPreferenceChangeListener(this);
        this.colorTypePreference.setValueIndex(getTextureColorType());
        Preference findPreference = findPreference("paper_reset");
        this.resetPreference = findPreference;
        findPreference.setOnPreferenceClickListener(this);
        PaperModeTipPreference paperModeTipPreference = (PaperModeTipPreference) findPreference("hint_unadjustable");
        this.hintPreference = paperModeTipPreference;
        paperModeTipPreference.setTitle(R.string.hint_unadjustable_text);
        this.hintPreference.setVisible(false);
        this.mAtuoAdjustState = Settings.System.getInt(getContext().getContentResolver(), "screen_auto_adjust", 1) == 1;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        if ("adjust_paper_texture".equals(key)) {
            int intValue = ((Integer) obj).intValue();
            Log.d("eyecare_level", "" + intValue);
            setEyeCareLevel((int) ((((float) intValue) * PER_TEXTURE_LEVEL) + EYECARE_MIN_LEVEL));
            return true;
        } else if (!"adjust_paper_mode".equals(key)) {
            if ("paper_color".equals(key)) {
                setTextureColorType(Integer.parseInt((String) obj));
                return true;
            }
            return true;
        } else {
            int intValue2 = ((Integer) obj).intValue();
            Log.d("temp_mode_level", "" + intValue2);
            setPaperModeLevel((int) ((((float) intValue2) * PER_LEVEL) + PAPER_MODE_MIN_LEVEL));
            return true;
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if ("paper_reset".equals(preference.getKey())) {
            resetDefault();
            return true;
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        updatePreference();
    }
}
