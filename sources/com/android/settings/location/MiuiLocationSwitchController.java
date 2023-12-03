package com.android.settings.location;

import android.content.Context;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.RegionUtils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.location.LocationEnabler;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

/* loaded from: classes.dex */
public class MiuiLocationSwitchController extends BasePreferenceController implements Preference.OnPreferenceChangeListener, LocationEnabler.LocationModeChangeListener, LifecycleObserver, OnStart, OnStop {
    private static final String TAG = "MiuiLocationSwitchController";
    private static final String hpKey = "xiaomi_hp_location_toggle";
    private Preference hpLocationPref;
    private final LocationEnabler mLocationEnabler;
    private RestrictedSwitchPreference mSwitchBar;
    private boolean mValidListener;

    public MiuiLocationSwitchController(Context context, String str, Lifecycle lifecycle) {
        super(context, str);
        this.mLocationEnabler = new LocationEnabler(context, this, lifecycle);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSwitchBar = (RestrictedSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.hpLocationPref = preferenceScreen.findPreference(hpKey);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
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

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.location.LocationEnabler.LocationModeChangeListener
    public void onLocationModeChanged(int i, boolean z) {
        Log.i(TAG, "onLocationModeChanged: " + i);
        boolean isEnabled = this.mLocationEnabler.isEnabled(i);
        int myUserId = UserHandle.myUserId();
        RestrictedLockUtils.EnforcedAdmin shareLocationEnforcedAdmin = this.mLocationEnabler.getShareLocationEnforcedAdmin(myUserId);
        if (this.mLocationEnabler.hasShareLocationRestriction(myUserId) || shareLocationEnforcedAdmin == null) {
            this.mSwitchBar.setEnabled(!z);
        } else {
            this.mSwitchBar.setDisabledByAdmin(shareLocationEnforcedAdmin);
        }
        if (isEnabled != this.mSwitchBar.isChecked()) {
            if (this.mValidListener) {
                this.mSwitchBar.setOnPreferenceChangeListener(null);
            }
            this.mSwitchBar.setChecked(isEnabled);
            if (this.mValidListener) {
                this.mSwitchBar.setOnPreferenceChangeListener(this);
            }
        }
        Preference preference = this.hpLocationPref;
        if (preference != null) {
            preference.setEnabled(this.mSwitchBar.isChecked());
        }
        if (RegionUtils.IS_MEXICO_TELCEL && SettingsFeatures.isCMTCallingAppAdmin(this.mContext)) {
            this.mSwitchBar.setShouldDisableView(true);
            this.mSwitchBar.setEnabled(false);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (getPreferenceKey().equals(preference.getKey())) {
            this.mLocationEnabler.setLocationEnabled(((Boolean) obj).booleanValue());
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        if (this.mValidListener) {
            return;
        }
        this.mSwitchBar.setOnPreferenceChangeListener(this);
        this.mValidListener = true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        if (this.mValidListener) {
            this.mSwitchBar.setOnPreferenceChangeListener(null);
            this.mValidListener = false;
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
