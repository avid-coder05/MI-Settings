package com.android.settings.deviceinfo.legal;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.android.settings.slices.SliceBackgroundWorker;

/* loaded from: classes.dex */
public class CopyrightPreferenceController extends LegalPreferenceController {
    private static final Intent INTENT = new Intent("android.settings.COPYRIGHT");

    public CopyrightPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.deviceinfo.legal.LegalPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.deviceinfo.legal.LegalPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.deviceinfo.legal.LegalPreferenceController
    protected Intent getIntent() {
        return INTENT;
    }

    @Override // com.android.settings.deviceinfo.legal.LegalPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.deviceinfo.legal.LegalPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.deviceinfo.legal.LegalPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.deviceinfo.legal.LegalPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.deviceinfo.legal.LegalPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.deviceinfo.legal.LegalPreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
