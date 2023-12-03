package com.android.settings.location;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.miui.AppOpsUtils;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import android.permission.PermissionControllerManager;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.search.tree.SecuritySettingsTree;
import com.android.settings.slices.SliceBackgroundWorker;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes.dex */
public class AppLocationPermissionPreferenceController extends LocationBasePreferenceController implements PreferenceControllerMixin {
    private static final String KEY_APP_LEVEL_PERMISSIONS = "app_level_permissions";
    final AtomicInteger loadingInProgress;
    private final LocationManager mLocationManager;
    int mNumHasLocation;
    private int mNumHasLocationLoading;
    int mNumTotal;
    private int mNumTotalLoading;
    private Preference mPreference;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class MiuiPermissionControllerManager implements PermissionControllerManager.OnCountPermissionAppsResultCallback {
        private final int argFlag;
        private final WeakReference<AppLocationPermissionPreferenceController> mRef;

        public MiuiPermissionControllerManager(AppLocationPermissionPreferenceController appLocationPermissionPreferenceController, int i) {
            this.mRef = new WeakReference<>(appLocationPermissionPreferenceController);
            this.argFlag = i;
        }

        public void onCountPermissionApps(int i) {
            AppLocationPermissionPreferenceController appLocationPermissionPreferenceController;
            WeakReference<AppLocationPermissionPreferenceController> weakReference = this.mRef;
            if (weakReference == null || (appLocationPermissionPreferenceController = weakReference.get()) == null) {
                return;
            }
            if (this.argFlag == 0) {
                AppLocationPermissionPreferenceController.access$012(appLocationPermissionPreferenceController, i);
            } else {
                AppLocationPermissionPreferenceController.access$112(appLocationPermissionPreferenceController, i);
            }
            if (appLocationPermissionPreferenceController.loadingInProgress.decrementAndGet() == 0) {
                appLocationPermissionPreferenceController.setAppCounts(appLocationPermissionPreferenceController.mNumTotalLoading, appLocationPermissionPreferenceController.mNumHasLocation);
            }
        }
    }

    public AppLocationPermissionPreferenceController(Context context, String str) {
        super(context, str);
        this.mNumTotal = -1;
        this.mNumHasLocation = -1;
        this.loadingInProgress = new AtomicInteger(0);
        this.mNumTotalLoading = 0;
        this.mNumHasLocationLoading = 0;
        this.mLocationManager = (LocationManager) context.getSystemService("location");
    }

    static /* synthetic */ int access$012(AppLocationPermissionPreferenceController appLocationPermissionPreferenceController, int i) {
        int i2 = appLocationPermissionPreferenceController.mNumTotalLoading + i;
        appLocationPermissionPreferenceController.mNumTotalLoading = i2;
        return i2;
    }

    static /* synthetic */ int access$112(AppLocationPermissionPreferenceController appLocationPermissionPreferenceController, int i) {
        int i2 = appLocationPermissionPreferenceController.mNumHasLocationLoading + i;
        appLocationPermissionPreferenceController.mNumHasLocationLoading = i2;
        return i2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAppCounts(int i, int i2) {
        this.mNumTotal = i;
        this.mNumHasLocation = i2;
        refreshSummary(this.mPreference);
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "location_settings_link_to_permissions_enabled", 1) == 1 ? 0 : 3;
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mContext.getText(R.string.recent_location_requests_summary);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        Intent intent;
        if (KEY_APP_LEVEL_PERMISSIONS.equals(preference.getKey())) {
            if (AppOpsUtils.isXOptMode() || AppOpsUtils.isXOptMode()) {
                intent = new Intent("android.intent.action.MANAGE_PERMISSION_APPS");
                intent.putExtra("android.intent.extra.PERMISSION_NAME", "android.permission-group.LOCATION");
            } else {
                intent = new Intent("com.miui.permission.single_item");
                intent.setPackage(SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME);
                intent.putExtra("permissionID", "32");
            }
            this.mContext.startActivity(intent);
            return true;
        }
        return false;
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
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
        Preference preference = this.mPreference;
        if (preference != null) {
            updateState(preference);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mPreference = preference;
        refreshSummary(preference);
        if (this.mLocationManager.isLocationEnabled() && this.loadingInProgress.get() == 0) {
            this.mNumTotalLoading = 0;
            this.mNumHasLocationLoading = 0;
            Context context = this.mContext;
            if (context == null || context.getApplicationContext() == null) {
                return;
            }
            Context applicationContext = this.mContext.getApplicationContext();
            List<UserHandle> userProfiles = ((UserManager) this.mContext.getSystemService(UserManager.class)).getUserProfiles();
            this.loadingInProgress.set(userProfiles.size() * 2);
            Iterator<UserHandle> it = userProfiles.iterator();
            while (it.hasNext()) {
                if (Utils.createPackageContextAsUser(this.mContext, it.next().getIdentifier()) == null) {
                    for (int i = 0; i < 2; i++) {
                        if (this.loadingInProgress.decrementAndGet() == 0) {
                            setAppCounts(this.mNumTotalLoading, this.mNumHasLocationLoading);
                        }
                    }
                } else {
                    PermissionControllerManager permissionControllerManager = (PermissionControllerManager) applicationContext.getSystemService(PermissionControllerManager.class);
                    permissionControllerManager.countPermissionApps(Arrays.asList("android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"), 0, new MiuiPermissionControllerManager(this, 0), (Handler) null);
                    permissionControllerManager.countPermissionApps(Arrays.asList("android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"), 1, new MiuiPermissionControllerManager(this, 1), (Handler) null);
                }
            }
        }
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
