package com.android.settings.controller;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.provider.MiuiSettings;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.utils.Utils;

/* loaded from: classes.dex */
public class FullScreenDisplayController extends BasePreferenceController {
    public static final int LEAST_VERSION_CODE = 20200703;
    public static final String QUICKSEARCHBOX_PKG_NAME = "com.android.quicksearchbox";
    private static final String TAG = "FullScreenDisplayController";
    private Context mContext;

    public FullScreenDisplayController(Context context, String str) {
        super(context, str);
        this.mContext = context;
    }

    public static boolean isRemoveEntryFromSettings(Context context) {
        return !SettingsFeatures.hasPocoLauncherDefault();
    }

    public static boolean isScreenButtonHidden(Context context) {
        return MiuiSettings.Global.getBoolean(context.getContentResolver(), "force_fsg_nav_bar");
    }

    public static boolean isUseFsVersionThree(Context context) {
        return Build.VERSION.SDK_INT >= 29 && Utils.isRecentsWithinLauncher(context.getApplicationContext()) && Utils.useMiuiHomeAsDefaultHome(context.getApplicationContext()) && !Utils.IS_MIUI_LITE_VERSION;
    }

    public static boolean needHideFullScreenDisplay(Context context) {
        return true;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return isRemoveEntryFromSettings(this.mContext) ? 2 : 0;
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
