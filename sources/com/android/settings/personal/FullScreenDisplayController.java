package com.android.settings.personal;

import android.content.Context;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.IWindowManager;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.utils.SettingsFeatures;
import miui.os.Build;

/* loaded from: classes2.dex */
public class FullScreenDisplayController extends BasePreferenceController {
    public static final String INFINITY_DISPLAY_REMOVE_SETTINGS = "infinity_display_remove_from_other_personal_settings";
    public static final int LEAST_VERSION_CODE = 20200703;
    public static final String QUICKSEARCHBOX_PKG_NAME = "com.android.quicksearchbox";
    private static final String TAG = "FullScreenDisplayController";

    public FullScreenDisplayController(Context context, String str) {
        super(context, str);
    }

    public static void initInfinityDisplaySettings(Context context) {
        if (needHideFullScreenDisplay()) {
            return;
        }
        Settings.Secure.putInt(context.getContentResolver(), INFINITY_DISPLAY_REMOVE_SETTINGS, isRemoveEntryFromSettings(context) ? 1 : 0);
    }

    public static boolean isRemoveEntryFromSettings(Context context) {
        return !SettingsFeatures.hasPocoLauncherDefault();
    }

    public static boolean needHideFullScreenDisplay() {
        try {
            if (IWindowManager.Stub.asInterface(ServiceManager.getService("window")).hasNavigationBar(0)) {
                if (!Build.IS_TABLET) {
                    return false;
                }
            }
            return true;
        } catch (RemoteException unused) {
            Log.e(TAG, "window manager error");
            return false;
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (needHideFullScreenDisplay() || isRemoveEntryFromSettings(this.mContext)) ? 2 : 0;
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
