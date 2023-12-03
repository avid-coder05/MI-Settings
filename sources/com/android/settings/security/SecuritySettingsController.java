package com.android.settings.security;

import android.app.AppGlobals;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.search.tree.SecuritySettingsTree;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.List;
import miui.os.Build;

/* loaded from: classes2.dex */
public class SecuritySettingsController extends BasePreferenceController {
    public SecuritySettingsController(Context context, String str) {
        super(context, str);
    }

    public static boolean hasSecurityCenterSecureEntry() {
        if (Build.IS_TABLET || Build.IS_INTERNATIONAL_BUILD || "cetus".equals(android.os.Build.DEVICE)) {
            return false;
        }
        Intent intent = new Intent("miui.intent.action.SECURITY_CENTER_SETTINGS");
        intent.setPackage(SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME);
        List<ResolveInfo> queryIntentActivities = AppGlobals.getInitialApplication().getPackageManager().queryIntentActivities(intent, 0);
        return (queryIntentActivities == null || queryIntentActivities.isEmpty()) ? false : true;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return hasSecurityCenterSecureEntry() ? 3 : 0;
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
