package com.android.settings.uwb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.uwb.UwbManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/* loaded from: classes2.dex */
public class UwbPreferenceController extends TogglePreferenceController implements UwbManager.AdapterStateCallback, LifecycleObserver {
    @VisibleForTesting
    static final String KEY_UWB_SETTINGS = "uwb_settings";
    @VisibleForTesting
    private final BroadcastReceiver mAirplaneModeChangedReceiver;
    @VisibleForTesting
    boolean mAirplaneModeOn;
    private final Executor mExecutor;
    private Preference mPreference;
    @VisibleForTesting
    UwbManager mUwbManager;

    public UwbPreferenceController(Context context, String str) {
        super(context, str);
        this.mExecutor = Executors.newSingleThreadExecutor();
        this.mUwbManager = (UwbManager) context.getSystemService(UwbManager.class);
        this.mAirplaneModeOn = Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 1;
        this.mAirplaneModeChangedReceiver = new BroadcastReceiver() { // from class: com.android.settings.uwb.UwbPreferenceController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                UwbPreferenceController uwbPreferenceController = UwbPreferenceController.this;
                uwbPreferenceController.updateState(uwbPreferenceController.mPreference);
            }
        };
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (isUwbSupportedOnDevice()) {
            return this.mAirplaneModeOn ? 5 : 0;
        }
        return 3;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mAirplaneModeOn ? this.mContext.getResources().getString(R.string.uwb_settings_summary_airplane_mode) : this.mContext.getResources().getString(R.string.uwb_settings_summary);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        int adapterState = this.mUwbManager.getAdapterState();
        return adapterState == 2 || adapterState == 1;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @VisibleForTesting
    boolean isUwbSupportedOnDevice() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.uwb");
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        if (isUwbSupportedOnDevice()) {
            this.mUwbManager.registerAdapterStateCallback(this.mExecutor, this);
        }
        BroadcastReceiver broadcastReceiver = this.mAirplaneModeChangedReceiver;
        if (broadcastReceiver != null) {
            this.mContext.registerReceiver(broadcastReceiver, new IntentFilter("android.intent.action.AIRPLANE_MODE"));
        }
        refreshSummary(this.mPreference);
    }

    public void onStateChanged(int i, int i2) {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        if (isUwbSupportedOnDevice()) {
            this.mUwbManager.unregisterAdapterStateCallback(this);
        }
        BroadcastReceiver broadcastReceiver = this.mAirplaneModeChangedReceiver;
        if (broadcastReceiver != null) {
            this.mContext.unregisterReceiver(broadcastReceiver);
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        this.mAirplaneModeOn = Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", 0) == 1;
        if (isUwbSupportedOnDevice()) {
            if (this.mAirplaneModeOn) {
                this.mUwbManager.setUwbEnabled(false);
            } else {
                this.mUwbManager.setUwbEnabled(z);
            }
        }
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setEnabled(!this.mAirplaneModeOn);
        refreshSummary(preference);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
