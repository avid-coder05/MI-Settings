package com.android.settings.accessibility.voiceaccess;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.accessibility.utils.MiuiAccessibilityUtils;
import com.android.settings.cloudbackup.AccessibilityCloudBackupHelper;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.device.MiuiAboutPhoneUtils;
import com.android.settings.slices.SliceBackgroundWorker;

/* loaded from: classes.dex */
public class VoiceAccessCategoryController extends BasePreferenceController {
    private static final String TAG = "VoiceAccessCategoryController";

    public VoiceAccessCategoryController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return isShowVoiceAccess() ? 0 : 2;
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

    public boolean isShowVoiceAccess() {
        return AccessibilityCloudBackupHelper.isServiceInstalled(this.mContext, VoiceAccessController.VOICEACCESS_A11y_SERVICE) && MiuiAboutPhoneUtils.isLocalCnAndChinese() && !MiuiAccessibilityUtils.hideAllMiuiAccessibilityService(this.mContext);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        PreferenceCategory preferenceCategory = (PreferenceCategory) preference;
        if (preferenceCategory != null) {
            setVisible(preferenceCategory, isShowVoiceAccess());
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
