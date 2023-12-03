package com.android.settings.applications.specialaccess.financialapps;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.util.ArrayUtils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.miuisettings.preference.SwitchPreference;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class FinancialAppsController extends BasePreferenceController implements ApplicationsState.Callbacks {
    private static final String TAG = "FinancialAppsController";
    PreferenceScreen mRoot;

    public FinancialAppsController(Context context, String str) {
        super(context, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updateList$0(PackageManager packageManager, AppOpsManager appOpsManager, int i, String str, Preference preference, Object obj) {
        try {
            appOpsManager.setMode(i >= 29 ? 80 : 14, packageManager.getPackageInfo(preference.getKey(), 0).applicationInfo.uid, str, !((Boolean) obj).booleanValue());
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e(TAG, "onPreferenceChange: Failed to get uid for " + preference.getKey());
            return false;
        }
    }

    private void updateList() {
        this.mRoot.removeAll();
        final PackageManager packageManager = this.mContext.getPackageManager();
        final AppOpsManager appOpsManager = (AppOpsManager) this.mContext.getSystemService(AppOpsManager.class);
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(4096);
        int size = installedPackages.size();
        for (int i = 0; i < size; i++) {
            PackageInfo packageInfo = installedPackages.get(i);
            String[] strArr = packageInfo.requestedPermissions;
            if (strArr != null) {
                final int i2 = packageInfo.applicationInfo.targetSdkVersion;
                final String str = packageInfo.packageName;
                if ((i2 >= 29 && ArrayUtils.contains(strArr, "android.permission.SMS_FINANCIAL_TRANSACTIONS")) || (i2 < 29 && ArrayUtils.contains(packageInfo.requestedPermissions, "android.permission.READ_SMS"))) {
                    SwitchPreference switchPreference = new SwitchPreference(this.mRoot.getContext());
                    switchPreference.setTitle(packageInfo.applicationInfo.loadLabel(packageManager));
                    switchPreference.setKey(str);
                    switchPreference.setChecked(appOpsManager.checkOp(i2 >= 29 ? 80 : 14, packageInfo.applicationInfo.uid, str) == 0);
                    switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.applications.specialaccess.financialapps.FinancialAppsController$$ExternalSyntheticLambda0
                        @Override // androidx.preference.Preference.OnPreferenceChangeListener
                        public final boolean onPreferenceChange(Preference preference, Object obj) {
                            boolean lambda$updateList$0;
                            lambda$updateList$0 = FinancialAppsController.lambda$updateList$0(packageManager, appOpsManager, i2, str, preference, obj);
                            return lambda$updateList$0;
                        }
                    });
                    this.mRoot.addPreference(switchPreference);
                }
            }
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mRoot = preferenceScreen;
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

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onAllSizesComputed() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLauncherInfoChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLoadEntriesCompleted() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageIconChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageListChanged() {
        updateList();
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageSizeChanged(String str) {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRunningStateChanged(boolean z) {
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updateList();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
