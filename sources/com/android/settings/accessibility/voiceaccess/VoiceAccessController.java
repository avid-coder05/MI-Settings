package com.android.settings.accessibility.voiceaccess;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.accessibility.AccessibilityUtils;

/* loaded from: classes.dex */
public class VoiceAccessController extends BasePreferenceController {
    private static final String TAG = "VoiceAccessController";
    public static final String VOICEACCESS_A11y_SERVICE = "com.miui.accessibility/com.miui.accessibility.voiceaccess.VoiceAccessAccessibilityService";

    public VoiceAccessController(Context context, String str) {
        super(context, str);
    }

    public static boolean isVoiceAccessOn(Context context) {
        return AccessibilityUtils.getEnabledServicesFromSettings(context).contains(ComponentName.unflattenFromString(VOICEACCESS_A11y_SERVICE));
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return isVoiceAccessOn(this.mContext) ? this.mContext.getResources().getString(R.string.accessibility_feature_state_on) : this.mContext.getResources().getString(R.string.accessibility_feature_state_off);
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
