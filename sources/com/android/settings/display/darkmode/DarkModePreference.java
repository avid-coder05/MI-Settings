package com.android.settings.display.darkmode;

import android.app.UiModeManager;
import android.content.Context;
import android.os.PowerManager;
import android.util.AttributeSet;
import com.android.settings.R;
import com.android.settings.widget.PrimarySwitchPreference;

/* loaded from: classes.dex */
public class DarkModePreference extends PrimarySwitchPreference {
    private Runnable mCallback;
    private DarkModeObserver mDarkModeObserver;
    private TimeFormatter mFormat;
    private PowerManager mPowerManager;
    private UiModeManager mUiModeManager;

    public DarkModePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mDarkModeObserver = new DarkModeObserver(context);
        this.mUiModeManager = (UiModeManager) context.getSystemService(UiModeManager.class);
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mFormat = new TimeFormatter(context);
        Runnable runnable = new Runnable() { // from class: com.android.settings.display.darkmode.DarkModePreference$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DarkModePreference.this.lambda$new$0();
            }
        };
        this.mCallback = runnable;
        this.mDarkModeObserver.subscribe(runnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        boolean isPowerSaveMode = this.mPowerManager.isPowerSaveMode();
        boolean z = (getContext().getResources().getConfiguration().uiMode & 32) != 0;
        setSwitchEnabled(!isPowerSaveMode);
        updateSummary(isPowerSaveMode, z);
    }

    private void updateSummary(boolean z, boolean z2) {
        String string;
        if (z) {
            setSummary(getContext().getString(z2 ? R.string.dark_ui_mode_disabled_summary_dark_theme_on : R.string.dark_ui_mode_disabled_summary_dark_theme_off));
            return;
        }
        int nightMode = this.mUiModeManager.getNightMode();
        if (nightMode == 0) {
            string = getContext().getString(z2 ? R.string.dark_ui_summary_on_auto_mode_auto : R.string.dark_ui_summary_off_auto_mode_auto);
        } else if (nightMode == 3) {
            string = getContext().getString(z2 ? R.string.dark_ui_summary_on_auto_mode_custom : R.string.dark_ui_summary_off_auto_mode_custom, this.mFormat.of(z2 ? this.mUiModeManager.getCustomNightModeEnd() : this.mUiModeManager.getCustomNightModeStart()));
        } else {
            string = getContext().getString(z2 ? R.string.dark_ui_summary_on_auto_mode_never : R.string.dark_ui_summary_off_auto_mode_never);
        }
        setSummary(string);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        this.mDarkModeObserver.subscribe(this.mCallback);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onDetached() {
        super.onDetached();
        this.mDarkModeObserver.unsubscribe();
    }
}
