package com.android.settings.applications.appinfo;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.provider.DeviceConfig;
import android.text.TextUtils;
import android.util.Slog;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.slices.SliceBackgroundWorker;

/* loaded from: classes.dex */
public final class HibernationSwitchPreferenceController extends AppInfoPreferenceControllerBase implements LifecycleObserver, AppOpsManager.OnOpChangedListener, Preference.OnPreferenceChangeListener {
    private static final String TAG = "HibernationSwitchPrefController";
    private final AppOpsManager mAppOpsManager;
    private boolean mIsPackageExemptByDefault;
    boolean mIsPackageSet;
    private String mPackageName;
    private int mPackageUid;

    public HibernationSwitchPreferenceController(Context context, String str) {
        super(context, str);
        this.mAppOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
    }

    private static boolean hibernationTargetsPreSApps() {
        return DeviceConfig.getBoolean("app_hibernation", "app_hibernation_targets_pre_s_apps", false);
    }

    private static boolean isHibernationEnabled() {
        return DeviceConfig.getBoolean("app_hibernation", "app_hibernation_enabled", true);
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (isHibernationEnabled() && this.mIsPackageSet) ? 0 : 2;
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    boolean isPackageHibernationExemptByUser() {
        if (this.mIsPackageSet) {
            int unsafeCheckOpNoThrow = this.mAppOpsManager.unsafeCheckOpNoThrow("android:auto_revoke_permissions_if_unused", this.mPackageUid, this.mPackageName);
            return unsafeCheckOpNoThrow == 3 ? this.mIsPackageExemptByDefault : unsafeCheckOpNoThrow != 0;
        }
        return true;
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // android.app.AppOpsManager.OnOpChangedListener
    public void onOpChanged(String str, String str2) {
        if ("android:auto_revoke_permissions_if_unused".equals(str) && TextUtils.equals(this.mPackageName, str2)) {
            updateState(this.mPreference);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        this.mAppOpsManager.stopWatchingMode(this);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        try {
            this.mAppOpsManager.setUidMode("android:auto_revoke_permissions_if_unused", this.mPackageUid, ((Boolean) obj).booleanValue() ? 0 : 1);
            return true;
        } catch (RuntimeException unused) {
            return false;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        if (this.mIsPackageSet) {
            this.mAppOpsManager.startWatchingMode("android:auto_revoke_permissions_if_unused", this.mPackageName, this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPackage(String str) {
        this.mPackageName = str;
        PackageManager packageManager = this.mContext.getPackageManager();
        int i = packageManager.hasSystemFeature("android.hardware.type.automotive") ? 30 : 29;
        try {
            this.mPackageUid = packageManager.getPackageUid(str, 0);
            this.mIsPackageExemptByDefault = !hibernationTargetsPreSApps() && packageManager.getTargetSdkVersion(str) <= i;
            this.mIsPackageSet = true;
        } catch (PackageManager.NameNotFoundException unused) {
            Slog.w(TAG, "Package [" + this.mPackageName + "] is not found!");
            this.mIsPackageSet = false;
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        ((SwitchPreference) preference).setChecked(!isPackageHibernationExemptByUser());
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
