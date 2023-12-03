package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.PreferenceScreen;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.display.DarkModeTimeModeUtil;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import miui.os.Build;

/* loaded from: classes.dex */
public class LottieAnimationPreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private LottieAnimationPreference mAnimatedImagePreference;

    public LottieAnimationPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mAnimatedImagePreference = (LottieAnimationPreference) preferenceScreen.findPreference(getPreferenceKey());
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (MiuiUtils.isLower4GB() || this.mAnimatedImagePreference == null) ? 3 : 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        if (this.mAnimatedImagePreference != null) {
            boolean z = this.mContext.getResources().getConfiguration().orientation == 1;
            boolean z2 = Build.IS_TABLET;
            String str = z2 ? z ? "accessibility_waiting_time_vertical.json" : "accessibility_waiting_time_horizontal.json" : "accessibility_waiting_time_phone.json";
            int screenHeightPixels = AccessibilityUtil.getScreenHeightPixels(this.mContext) / 2;
            LottieAnimationPreference lottieAnimationPreference = this.mAnimatedImagePreference;
            if (DarkModeTimeModeUtil.isDarkModeEnable(this.mContext)) {
                str = "night/" + str;
            }
            lottieAnimationPreference.setAssetName(str);
            this.mAnimatedImagePreference.setSelectable(false);
            LottieAnimationPreference lottieAnimationPreference2 = this.mAnimatedImagePreference;
            if (z2) {
                screenHeightPixels = z ? this.mContext.getResources().getDimensionPixelSize(R.dimen.accessibility_animated_max_height_pad_vertical) : this.mContext.getResources().getDimensionPixelSize(R.dimen.accessibility_animated_max_height_pad_horizontal);
            }
            lottieAnimationPreference2.setMaxHeight(screenHeightPixels);
            this.mAnimatedImagePreference.setMargin(0, z2 ? z ? this.mContext.getResources().getDimensionPixelSize(R.dimen.accessibility_animated_margin_top_pad_vertical) : this.mContext.getResources().getDimensionPixelSize(R.dimen.accessibility_animated_margin_top_pad_horizontal) : 0, 0, z2 ? z ? this.mContext.getResources().getDimensionPixelSize(R.dimen.accessibility_animated_margin_bottom_pad_vertical) : this.mContext.getResources().getDimensionPixelSize(R.dimen.accessibility_animated_margin_bottom_pad_horizontal) : 0);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        LottieAnimationPreference lottieAnimationPreference = this.mAnimatedImagePreference;
        if (lottieAnimationPreference != null) {
            lottieAnimationPreference.cancelAnimation();
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
