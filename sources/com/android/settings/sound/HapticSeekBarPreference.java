package com.android.settings.sound;

import android.content.Context;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.widget.SeekBarPreference;
import miui.util.HapticFeedbackUtil;
import miui.vip.VipService;
import miuix.animation.Folme;

/* loaded from: classes2.dex */
public class HapticSeekBarPreference extends SeekBarPreference {
    private int disableForegroundColor;
    private Context mContext;
    private float mDegreePerGear;
    private HapticFeedbackUtil mHapticFeedbackUtil;
    private boolean mIsHapticVideoPlaying;
    private float mLastLevel;
    private int mMinProgress;
    private SeekBar mSeekBar;
    private int primaryForegroundColor;

    public HapticSeekBarPreference(Context context) {
        this(context, null);
    }

    public HapticSeekBarPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public HapticSeekBarPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mLastLevel = -1.0f;
        this.mMinProgress = 0;
        setLayoutResource(R.layout.preference_volume_seekbar);
        setMax(VipService.VIP_SERVICE_FAILURE);
        this.mDegreePerGear = toFloaWith2Bit(0.02f);
        this.mContext = getContext();
        this.mHapticFeedbackUtil = new HapticFeedbackUtil(this.mContext, true);
        this.disableForegroundColor = getContext().getColor(R.color.miuix_appcompat_progress_disable_color_light);
        this.primaryForegroundColor = getContext().getColor(R.color.miuix_appcompat_progress_primary_colors_light);
    }

    private float getHapticLevel() {
        return Settings.System.getFloat(this.mContext.getContentResolver(), "haptic_feedback_infinite_intensity", 1.0f);
    }

    private void performHaptic() {
        if (this.mIsHapticVideoPlaying) {
            return;
        }
        this.mHapticFeedbackUtil.performHapticFeedback("mesh_light", false);
    }

    private float progressToLevel(int i) {
        float floaWith2Bit = toFloaWith2Bit(((i / 20) * this.mDegreePerGear) + 0.5f);
        return (floaWith2Bit != 0.5f || i == 0) ? floaWith2Bit : floaWith2Bit + this.mDegreePerGear;
    }

    private void setHapticLevel(float f) {
        if (this.mIsHapticVideoPlaying) {
            VibratorFeatureUtil.getInstance(this.mContext).setAmplitude(f);
        }
        Settings.System.putFloat(this.mContext.getContentResolver(), "haptic_feedback_infinite_intensity", f);
    }

    private float toFloaWith2Bit(float f) {
        return ((int) (f * 100.0f)) / 100.0f;
    }

    @Override // com.android.settingslib.widget.TwoTargetPreference, com.android.settingslib.miuisettings.preference.PreferenceFeature
    public boolean hasIcon() {
        return true;
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        if (MiuiUtils.isMiuiSdkSupportFolme()) {
            Folme.clean(view);
        }
        view.setBackgroundColor(0);
        view.setPaddingRelative(0, view.getPaddingTop(), 0, view.getPaddingBottom());
        miuix.androidbasewidget.widget.SeekBar seekBar = (miuix.androidbasewidget.widget.SeekBar) view.findViewById(R.id.seekbar);
        this.mSeekBar = seekBar;
        int paddingStart = ((LinearLayout) view.findViewById(R.id.title_view)).getPaddingStart() - seekBar.getPaddingStart();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seekBar.getLayoutParams());
        layoutParams.setMarginsRelative(paddingStart, 0, paddingStart, getContext().getResources().getDimensionPixelSize(R.dimen.volume_seekbar_margin_bottom));
        seekBar.setLayoutParams(layoutParams);
        VolumeStreamStateView volumeStreamStateView = (VolumeStreamStateView) view.findViewById(16908294);
        if (volumeStreamStateView.getDrawable() != null && seekBar.getProgressDrawable() != null) {
            volumeStreamStateView.getDrawable().setAlpha(seekBar.getProgressDrawable().getAlpha());
        }
        if (seekBar.getProgress() == 0) {
            int hapticLevel = (int) ((getHapticLevel() - 0.5f) * 1000.0f);
            seekBar.setProgress(hapticLevel);
            setProgress(hapticLevel, false);
        }
        seekBar.setOnSeekBarChangeListener(this);
        if (SettingsFeatures.isSupportSettingsHaptic(getContext())) {
            seekBar.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.settings.sound.HapticSeekBarPreference.1
                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View view2, MotionEvent motionEvent) {
                    int action = motionEvent.getAction();
                    if (action == 0) {
                        MiuiUtils.enableSpringBackLayout(view2, false);
                    } else if (action == 1 || action == 3) {
                        MiuiUtils.enableSpringBackLayout(view2, true);
                    }
                    return false;
                }
            });
        }
        if (isDisabledByAdmin()) {
            view.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.sound.HapticSeekBarPreference.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    HapticSeekBarPreference.this.performClick();
                }
            });
        }
        setEnabled(true);
        seekBar.setForegroundPrimaryColor(this.primaryForegroundColor, this.disableForegroundColor);
    }

    @Override // com.android.settings.widget.SeekBarPreference, android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        if (z) {
            int i2 = this.mMinProgress;
            if (i < i2) {
                seekBar.setProgress(i2, false);
                return;
            }
            float progressToLevel = progressToLevel(i);
            if (this.mLastLevel != progressToLevel) {
                this.mLastLevel = progressToLevel;
                setHapticLevel(progressToLevel);
                performHaptic();
            }
            setProgress(i, false);
        }
    }

    @Override // com.android.settings.widget.SeekBarPreference, android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
        performHaptic();
    }

    @Override // com.android.settings.widget.SeekBarPreference, android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekBar) {
        performHaptic();
    }

    public void setIsHapticVideoPlaying(boolean z) {
        this.mIsHapticVideoPlaying = z;
        SeekBar seekBar = this.mSeekBar;
        if (seekBar == null) {
            return;
        }
        seekBar.setHapticFeedbackEnabled(!z);
    }
}
