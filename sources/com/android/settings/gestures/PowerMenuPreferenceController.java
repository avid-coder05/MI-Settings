package com.android.settings.gestures;

import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;

/* loaded from: classes.dex */
public class PowerMenuPreferenceController extends BasePreferenceController {
    @VisibleForTesting
    static final int LONG_PRESS_POWER_ASSISTANT_VALUE = 5;
    @VisibleForTesting
    static final int LONG_PRESS_POWER_GLOBAL_ACTIONS = 1;
    private static final String POWER_BUTTON_LONG_PRESS_SETTING = "power_button_long_press";

    public PowerMenuPreferenceController(Context context, String str) {
        super(context, str);
    }

    private static int getPowerButtonLongPressValue(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), POWER_BUTTON_LONG_PRESS_SETTING, context.getResources().getInteger(17694847));
    }

    private boolean isAssistInvocationAvailable() {
        return this.mContext.getResources().getBoolean(17891589);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return isAssistInvocationAvailable() ? 0 : 3;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int powerButtonLongPressValue = getPowerButtonLongPressValue(this.mContext);
        return powerButtonLongPressValue == 5 ? this.mContext.getText(R.string.power_menu_summary_long_press_for_assist_enabled) : powerButtonLongPressValue == 1 ? this.mContext.getText(R.string.power_menu_summary_long_press_for_assist_disabled_with_power_menu) : this.mContext.getText(R.string.power_menu_summary_long_press_for_assist_disabled_no_action);
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
