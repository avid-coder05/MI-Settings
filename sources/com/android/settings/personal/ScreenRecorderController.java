package com.android.settings.personal;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.MiuiUtils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.utils.SettingsFeatures;

/* loaded from: classes2.dex */
public class ScreenRecorderController extends BasePreferenceController {
    private static final String FIRST_PAGE = "com.miui.screenrecorder.activity.ScreenRecorderHomeActivity";
    private static final String PKG_COM_MIUI_SCREENRECORDER = "com.miui.screenrecorder";
    private static final String PREF_KEY = "screen_recorder";
    private static final String SECOND_PAGE = "com.miui.screenrecorder.activity.ScreenRecorderSettingActivity";
    private final boolean mEnable;

    public ScreenRecorderController(Context context) {
        this(context, PREF_KEY);
    }

    public ScreenRecorderController(Context context, String str) {
        super(context, str);
        this.mEnable = (isActivityExist(context, PKG_COM_MIUI_SCREENRECORDER, FIRST_PAGE) || !isActivityExist(context, PKG_COM_MIUI_SCREENRECORDER, SECOND_PAGE) || TextUtils.isEmpty(getTitle(context))) ? false : true;
    }

    public static String getTitle(Context context) {
        return MiuiUtils.getStringByResName(context, PKG_COM_MIUI_SCREENRECORDER, "app_notification_title");
    }

    public static boolean isActivityExist(Context context, String str, String str2) {
        if (context == null) {
            return false;
        }
        Intent intent = new Intent();
        intent.setClassName(str, str2);
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                return context.getPackageManager().queryIntentActivities(intent, 131072).size() != 0;
            }
            return false;
        } catch (Exception unused) {
            return false;
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mEnable ? 0 : 2;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public boolean getVisibility() {
        return this.mEnable;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(PREF_KEY, preference.getKey()) && SettingsFeatures.isSplitTablet(this.mContext)) {
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
