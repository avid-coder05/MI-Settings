package com.android.settings.accessibility;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.settings.R;
import com.android.settings.accessibility.VibrationPreferenceFragment;
import com.android.settings.widget.RadioButtonPickerFragment;
import com.android.settingslib.widget.CandidateInfo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/* loaded from: classes.dex */
public abstract class VibrationPreferenceFragment extends RadioButtonPickerFragment {
    static final String KEY_INTENSITY_HIGH = "intensity_high";
    static final String KEY_INTENSITY_LOW = "intensity_low";
    static final String KEY_INTENSITY_MEDIUM = "intensity_medium";
    static final String KEY_INTENSITY_OFF = "intensity_off";
    static final String KEY_INTENSITY_ON = "intensity_on";
    private final Map<String, VibrationIntensityCandidateInfo> mCandidates = new ArrayMap();
    private final SettingsObserver mSettingsObserver = new SettingsObserver();

    /* loaded from: classes.dex */
    private class SettingsObserver extends ContentObserver {
        public SettingsObserver() {
            super(new Handler());
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            VibrationPreferenceFragment.this.updateCandidates();
            VibrationPreferenceFragment.this.playVibrationPreview();
        }

        public void register() {
            VibrationPreferenceFragment.this.getContext().getContentResolver().registerContentObserver(Settings.System.getUriFor(VibrationPreferenceFragment.this.getVibrationIntensitySetting()), false, this);
        }

        public void unregister() {
            VibrationPreferenceFragment.this.getContext().getContentResolver().unregisterContentObserver(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class VibrationIntensityCandidateInfo extends CandidateInfo {
        private int mIntensity;
        private String mKey;
        private int mLabelId;

        public VibrationIntensityCandidateInfo(String str, int i, int i2) {
            super(true);
            this.mKey = str;
            this.mLabelId = i;
            this.mIntensity = i2;
        }

        public int getIntensity() {
            return this.mIntensity;
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public String getKey() {
            return this.mKey;
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public Drawable loadIcon() {
            return null;
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public CharSequence loadLabel() {
            return VibrationPreferenceFragment.this.getContext().getString(this.mLabelId);
        }
    }

    private boolean hasVibrationEnabledSetting() {
        return !TextUtils.isEmpty(getVibrationEnabledSetting());
    }

    private void loadCandidates(Context context) {
        if (!context.getResources().getBoolean(R.bool.config_vibration_supports_multiple_intensities)) {
            this.mCandidates.put(KEY_INTENSITY_OFF, new VibrationIntensityCandidateInfo(KEY_INTENSITY_OFF, R.string.switch_off_text, 0));
            this.mCandidates.put(KEY_INTENSITY_ON, new VibrationIntensityCandidateInfo(KEY_INTENSITY_ON, R.string.switch_on_text, getDefaultVibrationIntensity()));
            return;
        }
        this.mCandidates.put(KEY_INTENSITY_OFF, new VibrationIntensityCandidateInfo(KEY_INTENSITY_OFF, R.string.accessibility_vibration_intensity_off, 0));
        this.mCandidates.put(KEY_INTENSITY_LOW, new VibrationIntensityCandidateInfo(KEY_INTENSITY_LOW, R.string.accessibility_vibration_intensity_low, 1));
        this.mCandidates.put(KEY_INTENSITY_MEDIUM, new VibrationIntensityCandidateInfo(KEY_INTENSITY_MEDIUM, R.string.accessibility_vibration_intensity_medium, 2));
        this.mCandidates.put(KEY_INTENSITY_HIGH, new VibrationIntensityCandidateInfo(KEY_INTENSITY_HIGH, R.string.accessibility_vibration_intensity_high, 3));
    }

    private void updateSettings(VibrationIntensityCandidateInfo vibrationIntensityCandidateInfo) {
        int i = 1;
        int i2 = vibrationIntensityCandidateInfo.getIntensity() != 0 ? 1 : 0;
        if (hasVibrationEnabledSetting()) {
            String vibrationEnabledSetting = getVibrationEnabledSetting();
            if (!TextUtils.equals(vibrationEnabledSetting, "apply_ramping_ringer") && Settings.System.getInt(getContext().getContentResolver(), vibrationEnabledSetting, 1) != 1) {
                i = 0;
            }
            if (i2 != i) {
                if (vibrationEnabledSetting.equals("apply_ramping_ringer")) {
                    Settings.Global.putInt(getContext().getContentResolver(), vibrationEnabledSetting, 0);
                } else {
                    Settings.System.putInt(getContext().getContentResolver(), vibrationEnabledSetting, i2);
                }
                int i3 = Settings.System.getInt(getContext().getContentResolver(), getVibrationIntensitySetting(), 0);
                if (i2 != 0 && i3 == vibrationIntensityCandidateInfo.getIntensity()) {
                    playVibrationPreview();
                }
            }
        }
        if (i2 == 0 && hasVibrationEnabledSetting()) {
            return;
        }
        Settings.System.putInt(getContext().getContentResolver(), getVibrationIntensitySetting(), vibrationIntensityCandidateInfo.getIntensity());
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected List<? extends CandidateInfo> getCandidates() {
        ArrayList arrayList = new ArrayList(this.mCandidates.values());
        arrayList.sort(Comparator.comparing(new Function() { // from class: com.android.settings.accessibility.VibrationPreferenceFragment$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Integer.valueOf(((VibrationPreferenceFragment.VibrationIntensityCandidateInfo) obj).getIntensity());
            }
        }).reversed());
        return arrayList;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected String getDefaultKey() {
        int i = Settings.System.getInt(getContext().getContentResolver(), getVibrationIntensitySetting(), getDefaultVibrationIntensity());
        String vibrationEnabledSetting = getVibrationEnabledSetting();
        if (!(TextUtils.equals(vibrationEnabledSetting, "apply_ramping_ringer") || Settings.System.getInt(getContext().getContentResolver(), vibrationEnabledSetting, 1) == 1)) {
            i = 0;
        }
        for (VibrationIntensityCandidateInfo vibrationIntensityCandidateInfo : this.mCandidates.values()) {
            boolean z = vibrationIntensityCandidateInfo.getIntensity() == i;
            boolean z2 = vibrationIntensityCandidateInfo.getKey().equals(KEY_INTENSITY_ON) && i != 0;
            if (z || z2) {
                return vibrationIntensityCandidateInfo.getKey();
            }
        }
        return null;
    }

    protected abstract int getDefaultVibrationIntensity();

    protected int getPreviewVibrationAudioAttributesUsage() {
        return 0;
    }

    protected abstract String getVibrationEnabledSetting();

    protected abstract String getVibrationIntensitySetting();

    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mSettingsObserver.register();
        if (this.mCandidates.isEmpty()) {
            loadCandidates(context);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        super.onDetach();
        this.mSettingsObserver.unregister();
    }

    protected void onVibrationIntensitySelected(int i) {
    }

    protected void playVibrationPreview() {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Vibrator.class);
        VibrationEffect vibrationEffect = VibrationEffect.get(0);
        AudioAttributes.Builder builder = new AudioAttributes.Builder();
        builder.setUsage(getPreviewVibrationAudioAttributesUsage());
        vibrator.vibrate(vibrationEffect, builder.build());
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected boolean setDefaultKey(String str) {
        VibrationIntensityCandidateInfo vibrationIntensityCandidateInfo = this.mCandidates.get(str);
        if (vibrationIntensityCandidateInfo != null) {
            updateSettings(vibrationIntensityCandidateInfo);
            onVibrationIntensitySelected(vibrationIntensityCandidateInfo.getIntensity());
            return true;
        }
        Log.e("VibrationPreferenceFragment", "Tried to set unknown intensity (key=" + str + ")!");
        return false;
    }
}
