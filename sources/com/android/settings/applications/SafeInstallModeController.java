package com.android.settings.applications;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import miui.os.Build;

/* loaded from: classes.dex */
public class SafeInstallModeController extends BasePreferenceController {
    private Context mContext;

    public SafeInstallModeController(Context context, String str) {
        super(context, str);
        this.mContext = context;
    }

    public boolean canResolveIntent(Context context, Intent intent) {
        return (intent == null || context.getPackageManager().resolveActivity(intent, 0) == null) ? false : true;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (Build.IS_INTERNATIONAL_BUILD) {
            return 2;
        }
        Intent intent = new Intent();
        intent.setClassName("com.miui.packageinstaller", "com.miui.packageInstaller.ui.secure.SecureModeActivity");
        return canResolveIntent(this.mContext, intent) ? 0 : 2;
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
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(this.mPreferenceKey, preference.getKey()) || preference.getIntent() == null) {
            return super.handlePreferenceTreeClick(preference);
        }
        Intent intent = preference.getIntent();
        intent.putExtra("safe_mode_ref", "setting_entry");
        intent.putExtra("safe_mode_type", "setting");
        this.mContext.startActivity(intent);
        return true;
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
