package com.android.settings.accessibility;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.MiuiUtils;
import com.android.settings.accessibility.utils.MiuiAccessibilityUtils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.utils.AppMarketUtils;

/* loaded from: classes.dex */
public class MiuiAccessibilityAsrController extends BasePreferenceController {
    public static final String APP_STORE_URI = "https://app.xiaomi.com/details?id=com.miui.accessibility&back=true";
    public static final String LAUNCH_BY = "launch_by";
    public static final String MIUI_ACCESSIBILITY_ASR_CLASS_NAME = "com.miui.accessibility.asr.component.message.MessageActivity";
    public static final String MIUI_ACCESSIBILITY_ASR_PACKAGE_NAME = "com.miui.accessibility";
    public static final String MIUI_ACCESSIBILITY_ASR_PREFERENCE = "miui_accessibility_asr_preference";
    private Context mContext;

    public MiuiAccessibilityAsrController(Context context, String str) {
        super(context, str);
        this.mContext = context;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return !MiuiAccessibilityUtils.hideAllMiuiAccessibilityService(this.mContext) ? 0 : 2;
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
        return "miui_accessibility_asr_preference";
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(getPreferenceKey(), preference.getKey())) {
            if (MiuiUtils.isApplicationInstalled(this.mContext, MIUI_ACCESSIBILITY_ASR_PACKAGE_NAME)) {
                Intent intent = new Intent();
                intent.setClassName(MIUI_ACCESSIBILITY_ASR_PACKAGE_NAME, MIUI_ACCESSIBILITY_ASR_CLASS_NAME);
                MiuiUtils.cancelSplit(this.mContext, intent);
                this.mContext.startActivity(intent);
            } else {
                Context context = this.mContext;
                AppMarketUtils.toMarket(context, AppMarketUtils.getMarketPkgName(context.getPackageManager()), MIUI_ACCESSIBILITY_ASR_PACKAGE_NAME, APP_STORE_URI);
            }
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
