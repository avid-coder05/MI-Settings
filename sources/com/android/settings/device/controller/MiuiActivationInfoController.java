package com.android.settings.device.controller;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.utils.ActivationInfoUtil;
import com.android.settings.utils.SettingsFeatures;
import miui.os.Build;

/* loaded from: classes.dex */
public class MiuiActivationInfoController extends BasePreferenceController {
    private static final String ACTION_PACKAGE_NAME = "com.miui.cloudservice";
    private static final String ACTIVATION_INFO_INTENT_ACTION = "com.xiaomi.VIEW_ACTIVATION_INFO";
    private static final String KEY_DEVICE_ACTIVATION_INFO = "device_activation_info";

    public MiuiActivationInfoController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        Intent intent = new Intent(ACTIVATION_INFO_INTENT_ACTION);
        intent.setPackage("com.miui.cloudservice");
        return !Build.IS_TABLET && !Build.IS_GLOBAL_BUILD && this.mContext.getPackageManager().resolveActivity(intent, 0) != null && !ActivationInfoUtil.isCurrentDeviceInBlockList() ? 0 : 2;
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
        return KEY_DEVICE_ACTIVATION_INFO;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(KEY_DEVICE_ACTIVATION_INFO, preference.getKey()) && (SettingsFeatures.isSplitTabletDevice() || SettingsFeatures.isFoldDevice())) {
            preference.getIntent().addMiuiFlags(16);
        }
        return super.handlePreferenceTreeClick(preference);
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
