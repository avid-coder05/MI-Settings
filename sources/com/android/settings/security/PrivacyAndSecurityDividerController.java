package com.android.settings.security;

import android.content.Context;
import android.content.IntentFilter;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.notification.EmergencyBroadcastPreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.utils.SettingsFeatures;

/* loaded from: classes2.dex */
public class PrivacyAndSecurityDividerController extends BasePreferenceController {
    public PrivacyAndSecurityDividerController(Context context, String str) {
        super(context, str);
    }

    public static boolean isNotSupported(Context context) {
        return SettingsFeatures.IS_NEED_REMOVE_SOS && !new EmergencyBroadcastPreferenceController(context, "check_status").isAvailable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return isNotSupported(this.mContext) ? 3 : 0;
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

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
