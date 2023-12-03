package com.android.settings.display;

import android.animation.ValueAnimator;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.service.vr.IVrManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.MathUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import androidx.preference.PreferenceViewHolder;
import com.android.internal.display.BrightnessSynchronizer;
import com.android.settings.R;
import com.android.settings.widget.SeekBarPreference;
import com.android.settingslib.display.BrightnessUtils;

/* loaded from: classes.dex */
public class BrightnessSeekBarPreference extends SeekBarPreference {
    private static final String TAG = BrightnessSeekBarPreference.class.getSimpleName();
    private Context mContext;
    private int mDisplayId;
    private DisplayManager mDisplayManager;
    private boolean mIsVrModeEnabled;
    private float mMaximumBrightness;
    private float mMaximumBrightnessForVr;
    private float mMinimumBrightness;
    private float mMinimumBrightnessForVr;
    private SeekBar mSeekBar;
    private ValueAnimator mSeekBarAnimator;
    private IVrManager mVrManager;

    public BrightnessSeekBarPreference(Context context) {
        this(context, null);
    }

    public BrightnessSeekBarPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BrightnessSeekBarPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setMax(BrightnessUtils.GAMMA_SPACE_MAX);
        setLayoutResource(R.layout.preference_brightness_seekbar);
        this.mContext = context;
        initValue();
    }

    private void animateSeekBarTo(int i) {
        if (this.mSeekBarAnimator == null) {
            SeekBar seekBar = this.mSeekBar;
            if (seekBar != null) {
                seekBar.setProgress(i);
            } else {
                setProgress(i, true);
            }
        }
        ValueAnimator valueAnimator = this.mSeekBarAnimator;
        if (valueAnimator != null && valueAnimator.isStarted()) {
            this.mSeekBarAnimator.cancel();
        }
        SeekBar seekBar2 = this.mSeekBar;
        if (seekBar2 != null) {
            ValueAnimator ofInt = ValueAnimator.ofInt(seekBar2.getProgress(), i);
            this.mSeekBarAnimator = ofInt;
            ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.display.BrightnessSeekBarPreference$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    BrightnessSeekBarPreference.this.lambda$animateSeekBarTo$0(valueAnimator2);
                }
            });
        } else {
            ValueAnimator ofInt2 = ValueAnimator.ofInt(getProgress(), i);
            this.mSeekBarAnimator = ofInt2;
            ofInt2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.display.BrightnessSeekBarPreference$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    BrightnessSeekBarPreference.this.lambda$animateSeekBarTo$1(valueAnimator2);
                }
            });
        }
        this.mSeekBarAnimator.setDuration(1500L);
        this.mSeekBarAnimator.start();
    }

    private void initValue() {
        PowerManager powerManager = (PowerManager) this.mContext.getSystemService(PowerManager.class);
        this.mMinimumBrightness = 0.0f;
        this.mMaximumBrightness = 1.0f;
        this.mMinimumBrightnessForVr = powerManager.getBrightnessConstraint(5);
        this.mMaximumBrightnessForVr = powerManager.getBrightnessConstraint(6);
        this.mDisplayManager = (DisplayManager) this.mContext.getApplicationContext().getSystemService("display");
        this.mVrManager = IVrManager.Stub.asInterface(ServiceManager.getService("vrmanager"));
        this.mDisplayId = this.mContext.getDisplayId();
        IVrManager iVrManager = this.mVrManager;
        if (iVrManager != null) {
            try {
                this.mIsVrModeEnabled = iVrManager.getVrModeState();
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to register VR mode state listener: ", e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$animateSeekBarTo$0(ValueAnimator valueAnimator) {
        this.mSeekBar.setProgress(((Integer) valueAnimator.getAnimatedValue()).intValue());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$animateSeekBarTo$1(ValueAnimator valueAnimator) {
        setProgress(((Integer) valueAnimator.getAnimatedValue()).intValue());
    }

    private void setBrightness(int i, boolean z) {
        float f;
        float f2;
        if (this.mIsVrModeEnabled) {
            f = this.mMinimumBrightnessForVr;
            f2 = this.mMaximumBrightnessForVr;
        } else {
            f = this.mMinimumBrightness;
            f2 = this.mMaximumBrightness;
        }
        float min = MathUtils.min(BrightnessUtils.convertGammaToLinearFloat(i, f, f2), f2);
        if (!z) {
            this.mDisplayManager.setTemporaryBrightness(this.mDisplayId, min);
        } else if (min == this.mDisplayManager.getBrightness(this.mDisplayId)) {
            this.mDisplayManager.setTemporaryBrightness(this.mDisplayId, Float.NaN);
        } else {
            this.mDisplayManager.setBrightness(this.mDisplayId, min);
        }
    }

    @Override // com.android.settingslib.widget.TwoTargetPreference, com.android.settingslib.miuisettings.preference.PreferenceFeature
    public boolean hasIcon() {
        return true;
    }

    @Override // com.android.settings.widget.SeekBarPreference, com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference, com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        view.setPaddingRelative(0, view.getPaddingTop(), 0, view.getPaddingBottom());
        if (this.mSeekBar == null) {
            this.mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
            int paddingStart = ((LinearLayout) view.findViewById(R.id.title_view)).getPaddingStart() - this.mSeekBar.getPaddingStart();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(this.mSeekBar.getLayoutParams());
            layoutParams.setMarginsRelative(paddingStart, 0, paddingStart, getContext().getResources().getDimensionPixelSize(R.dimen.brightness_seekbar_margin_bottom));
            this.mSeekBar.setLayoutParams(layoutParams);
            this.mSeekBar.setMax(BrightnessUtils.GAMMA_SPACE_MAX);
            this.mSeekBar.setOnSeekBarChangeListener(this);
        }
    }

    @Override // com.android.settings.widget.SeekBarPreference, android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        setProgress(i, false);
        if (z) {
            ValueAnimator valueAnimator = this.mSeekBarAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            setBrightness(i, false);
        }
    }

    @Override // com.android.settings.widget.SeekBarPreference, android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override // com.android.settings.widget.SeekBarPreference, android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekBar) {
        setBrightness(getProgress(), true);
    }

    public void updateBrightnessSeekBar(float f, boolean z, float f2, float f3) {
        this.mMinimumBrightness = f2;
        this.mMaximumBrightness = f3;
        if (z) {
            f2 = this.mMinimumBrightnessForVr;
        }
        if (z) {
            f3 = this.mMaximumBrightnessForVr;
        }
        ValueAnimator valueAnimator = this.mSeekBarAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (!BrightnessSynchronizer.floatEquals(f, BrightnessUtils.convertGammaToLinearFloat(getProgress(), f2, f3)) || this.mSeekBar == null) {
            animateSeekBarTo(BrightnessUtils.convertLinearToGammaFloat(f, f2, f3));
        }
    }

    public void updateVrMode(boolean z) {
        if (this.mIsVrModeEnabled != z) {
            this.mIsVrModeEnabled = z;
        }
    }
}
