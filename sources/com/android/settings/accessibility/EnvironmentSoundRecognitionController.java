package com.android.settings.accessibility;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.accessibility.utils.MiuiAccessibilityUtils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.accessibility.AccessibilityUtils;

/* loaded from: classes.dex */
public class EnvironmentSoundRecognitionController extends BasePreferenceController {
    public static final String ENVIRONMENT_SOUND_RECOGNITION = "environment_sound_recognition";
    public static final String ENVIRONMENT_SOUND_RECOGNITION_SERVICE = "com.miui.accessibility/com.miui.accessibility.environment.sound.recognition.EnvSoundRecognitionService";

    public EnvironmentSoundRecognitionController(Context context, String str) {
        super(context, str);
    }

    private boolean isSupportedEnvironmentSoundRecognition() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(MiuiAccessibilityAsrController.MIUI_ACCESSIBILITY_ASR_PACKAGE_NAME, "com.miui.accessibility.environment.sound.recognition.Settings"));
        return MiuiUtils.isActivityAvalible(this.mContext, intent);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (MiuiAccessibilityUtils.hideAllMiuiAccessibilityService(this.mContext) || !isSupportedEnvironmentSoundRecognition()) ? 2 : 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "environment_sound_recognition";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mContext.getResources().getText(AccessibilityUtils.getEnabledServicesFromSettings(this.mContext).contains(ComponentName.unflattenFromString(ENVIRONMENT_SOUND_RECOGNITION_SERVICE)) ? R.string.accessibility_feature_state_on : R.string.accessibility_feature_state_off);
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

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
