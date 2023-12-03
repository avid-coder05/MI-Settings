package com.android.settings.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.widget.RestrictedAppPreference;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.utils.ThreadUtils;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class LocationServicePreferenceController extends LocationBasePreferenceController implements LifecycleObserver, OnResume, OnPause {
    static final IntentFilter INTENT_FILTER_INJECTED_SETTING_CHANGED = new IntentFilter("android.location.InjectedSettingChanged");
    private static final String TAG = "LocationPrefCtrl";
    protected PreferenceCategory mCategoryLocationServices;
    BroadcastReceiver mInjectedSettingsReceiver;
    AppSettingsInjector mInjector;
    private Map<Integer, List<Preference>> mLocationServicesMap;

    public LocationServicePreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mCategoryLocationServices = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    protected Map<Integer, List<Preference>> getLocationServices() {
        final int managedProfileId = Utils.getManagedProfileId(this.mUserManager, UserHandle.myUserId());
        Map<Integer, List<Preference>> map = this.mLocationServicesMap;
        if (map == null) {
            ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.location.LocationServicePreferenceController.2
                @Override // java.lang.Runnable
                public void run() {
                    LocationServicePreferenceController locationServicePreferenceController = LocationServicePreferenceController.this;
                    AppSettingsInjector appSettingsInjector = locationServicePreferenceController.mInjector;
                    Context context = locationServicePreferenceController.mFragment.getPreferenceManager().getContext();
                    int i = managedProfileId;
                    locationServicePreferenceController.mLocationServicesMap = appSettingsInjector.getInjectedSettings(context, (i == -10000 || LocationServicePreferenceController.this.mLocationEnabler.getShareLocationEnforcedAdmin(i) == null) ? -2 : UserHandle.myUserId());
                    ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.location.LocationServicePreferenceController.2.1
                        @Override // java.lang.Runnable
                        public void run() {
                            LocationServicePreferenceController locationServicePreferenceController2 = LocationServicePreferenceController.this;
                            PreferenceCategory preferenceCategory = locationServicePreferenceController2.mCategoryLocationServices;
                            if (preferenceCategory != null) {
                                locationServicePreferenceController2.updateState(preferenceCategory);
                            }
                        }
                    });
                }
            });
            return new ArrayMap();
        }
        return map;
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController
    public void init(DashboardFragment dashboardFragment) {
        super.init(dashboardFragment);
        this.mInjector = new AppSettingsInjector(this.mContext, getMetricsCategory());
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.location.LocationEnabler.LocationModeChangeListener
    public void onLocationModeChanged(int i, boolean z) {
        this.mInjector.reloadStatusMessages();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mContext.unregisterReceiver(this.mInjectedSettingsReceiver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (this.mInjectedSettingsReceiver == null) {
            this.mInjectedSettingsReceiver = new BroadcastReceiver() { // from class: com.android.settings.location.LocationServicePreferenceController.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    if (Log.isLoggable(LocationServicePreferenceController.TAG, 3)) {
                        Log.d(LocationServicePreferenceController.TAG, "Received settings change intent: " + intent);
                    }
                    LocationServicePreferenceController.this.mInjector.reloadStatusMessages();
                }
            };
        }
        this.mContext.registerReceiver(this.mInjectedSettingsReceiver, INTENT_FILTER_INJECTED_SETTING_CHANGED);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        this.mCategoryLocationServices.removeAll();
        boolean z = false;
        for (Map.Entry<Integer, List<Preference>> entry : getLocationServices().entrySet()) {
            for (Preference preference2 : entry.getValue()) {
                if (preference2 instanceof RestrictedAppPreference) {
                    ((RestrictedAppPreference) preference2).checkRestrictionAndSetDisabled();
                }
            }
            if (entry.getKey().intValue() == UserHandle.myUserId()) {
                if (this.mCategoryLocationServices != null) {
                    LocationSettings.addPreferencesSorted(entry.getValue(), this.mCategoryLocationServices);
                }
                z = true;
            }
        }
        setVisible(this.mCategoryLocationServices, z);
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
