package com.android.settings.display.darkmode;

import android.app.TimePickerDialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.widget.TimePicker;
import androidx.preference.Preference;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/* loaded from: classes.dex */
public class DarkModeCustomPreferenceController extends BasePreferenceController {
    private static final String END_TIME_KEY = "dark_theme_end_time";
    private static final String START_TIME_KEY = "dark_theme_start_time";
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
    private TimeFormatter mFormat;
    private DarkModeSettingsFragment mFragmet;
    private final UiModeManager mUiModeManager;

    public DarkModeCustomPreferenceController(Context context, String str) {
        super(context, str);
        this.mFormat = new TimeFormatter(this.mContext);
        this.mUiModeManager = (UiModeManager) context.getSystemService(UiModeManager.class);
    }

    public DarkModeCustomPreferenceController(Context context, String str, DarkModeSettingsFragment darkModeSettingsFragment) {
        this(context, str);
        this.mFragmet = darkModeSettingsFragment;
    }

    public DarkModeCustomPreferenceController(Context context, String str, DarkModeSettingsFragment darkModeSettingsFragment, TimeFormatter timeFormatter) {
        this(context, str, darkModeSettingsFragment);
        this.mFormat = timeFormatter;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getDialog$0(TimePicker timePicker, int i, int i2) {
        LocalTime of = LocalTime.of(i, i2);
        if (TextUtils.equals(getPreferenceKey(), START_TIME_KEY)) {
            this.mUiModeManager.setCustomNightModeStart(of);
        } else {
            this.mUiModeManager.setCustomNightModeEnd(of);
        }
        DarkModeSettingsFragment darkModeSettingsFragment = this.mFragmet;
        if (darkModeSettingsFragment != null) {
            darkModeSettingsFragment.refresh();
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public TimePickerDialog getDialog() {
        LocalTime customNightModeStart = TextUtils.equals(getPreferenceKey(), START_TIME_KEY) ? this.mUiModeManager.getCustomNightModeStart() : this.mUiModeManager.getCustomNightModeEnd();
        return new TimePickerDialog(this.mContext, new TimePickerDialog.OnTimeSetListener() { // from class: com.android.settings.display.darkmode.DarkModeCustomPreferenceController$$ExternalSyntheticLambda0
            @Override // android.app.TimePickerDialog.OnTimeSetListener
            public final void onTimeSet(TimePicker timePicker, int i, int i2) {
                DarkModeCustomPreferenceController.this.lambda$getDialog$0(timePicker, i, i2);
            }
        }, customNightModeStart.getHour(), customNightModeStart.getMinute(), this.mFormat.is24HourFormat());
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void refreshSummary(Preference preference) {
        if (this.mUiModeManager.getNightMode() != 3) {
            preference.setVisible(false);
            return;
        }
        preference.setVisible(true);
        preference.setSummary(this.mFormat.of(TextUtils.equals(getPreferenceKey(), START_TIME_KEY) ? this.mUiModeManager.getCustomNightModeStart() : this.mUiModeManager.getCustomNightModeEnd()));
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
