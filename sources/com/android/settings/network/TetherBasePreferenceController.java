package com.android.settings.network;

import android.content.Context;
import android.content.IntentFilter;
import android.net.TetheringManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settings.network.TetherEnabler;
import com.android.settings.slices.SliceBackgroundWorker;

/* loaded from: classes.dex */
public abstract class TetherBasePreferenceController extends TogglePreferenceController implements LifecycleObserver, DataSaverBackend.Listener, TetherEnabler.OnTetherStateUpdateListener {
    private static final String TAG = "TetherBasePreferenceController";
    private final DataSaverBackend mDataSaverBackend;
    private boolean mDataSaverEnabled;
    Preference mPreference;
    private TetherEnabler mTetherEnabler;
    int mTetheringState;
    final TetheringManager mTm;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TetherBasePreferenceController(Context context, String str) {
        super(context, str);
        this.mTm = (TetheringManager) context.getSystemService(TetheringManager.class);
        DataSaverBackend dataSaverBackend = new DataSaverBackend(context);
        this.mDataSaverBackend = dataSaverBackend;
        this.mDataSaverEnabled = dataSaverBackend.isDataSaverEnabled();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(this.mPreferenceKey);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (shouldShow()) {
            return (this.mDataSaverEnabled || !shouldEnable()) ? 5 : 0;
        }
        return 2;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public abstract int getTetherType();

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return TetherEnabler.isTethering(this.mTetheringState, getTetherType());
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onAllowlistStatusChanged(int i, boolean z) {
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDataSaverChanged(boolean z) {
        this.mDataSaverEnabled = z;
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDenylistStatusChanged(int i, boolean z) {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        TetherEnabler tetherEnabler = this.mTetherEnabler;
        if (tetherEnabler != null) {
            tetherEnabler.removeListener(this);
        }
        this.mDataSaverBackend.remListener(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        TetherEnabler tetherEnabler = this.mTetherEnabler;
        if (tetherEnabler != null) {
            tetherEnabler.addListener(this);
        }
        this.mDataSaverBackend.addListener(this);
    }

    @Override // com.android.settings.network.TetherEnabler.OnTetherStateUpdateListener
    public void onTetherStateUpdated(int i) {
        this.mTetheringState = i;
        updateState(this.mPreference);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        TetherEnabler tetherEnabler = this.mTetherEnabler;
        if (tetherEnabler == null) {
            return false;
        }
        if (z) {
            tetherEnabler.startTethering(getTetherType());
            return true;
        }
        tetherEnabler.stopTethering(getTetherType());
        return true;
    }

    public void setTetherEnabler(TetherEnabler tetherEnabler) {
        this.mTetherEnabler = tetherEnabler;
    }

    public abstract boolean shouldEnable();

    public abstract boolean shouldShow();

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (isAvailable()) {
            preference.setEnabled(getAvailabilityStatus() != 5);
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
