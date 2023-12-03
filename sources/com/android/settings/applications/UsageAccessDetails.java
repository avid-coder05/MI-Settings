package com.android.settings.applications;

import android.app.AppOpsManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.security.AdbUtils;
import com.android.settings.R;
import com.android.settings.applications.AppStateUsageBridge;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.miui.enterprise.ApplicationHelper;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class UsageAccessDetails extends AppInfoWithHeader implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private AppOpsManager mAppOpsManager;
    private DevicePolicyManager mDpm;
    private Intent mSettingsIntent;
    private SwitchPreference mSwitchPref;
    private AppStateUsageBridge mUsageBridge;
    private Preference mUsageDesc;
    private AppStateUsageBridge.UsageState mUsageState;

    private static boolean doesAnyPermissionMatch(String str, String[] strArr) {
        for (String str2 : strArr) {
            if (str.equals(str2)) {
                return true;
            }
        }
        return false;
    }

    private void setHasAccess(boolean z) {
        logSpecialPermissionChange(z, this.mPackageName);
        int i = !z ? 1 : 0;
        int i2 = this.mPackageInfo.applicationInfo.uid;
        if (doesAnyPermissionMatch("android.permission.PACKAGE_USAGE_STATS", this.mUsageState.packageInfo.requestedPermissions)) {
            this.mAppOpsManager.setMode(43, i2, this.mPackageName, i);
        }
        if (doesAnyPermissionMatch("android.permission.LOADER_USAGE_STATS", this.mUsageState.packageInfo.requestedPermissions)) {
            this.mAppOpsManager.setMode(95, i2, this.mPackageName, i);
        }
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 183;
    }

    void logSpecialPermissionChange(boolean z, String str) {
        int i = z ? 783 : 784;
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider();
        metricsFeatureProvider.action(metricsFeatureProvider.getAttribution(getActivity()), i, getMetricsCategory(), str, 0);
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 102) {
            boolean z = i2 == -1;
            AppStateUsageBridge.UsageState usageState = this.mUsageState;
            if (usageState == null || z == usageState.isPermissible()) {
                return;
            }
            setHasAccess(!this.mUsageState.isPermissible());
            refreshUi();
        }
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mUsageBridge = new AppStateUsageBridge(activity, ((AppInfoBase) this).mState, null);
        this.mAppOpsManager = (AppOpsManager) activity.getSystemService("appops");
        this.mDpm = (DevicePolicyManager) activity.getSystemService(DevicePolicyManager.class);
        addPreferencesFromResource(R.xml.app_ops_permissions_details);
        this.mSwitchPref = (SwitchPreference) findPreference("app_ops_settings_switch");
        this.mUsageDesc = findPreference("app_ops_settings_description");
        getPreferenceScreen().setTitle(R.string.usage_access);
        this.mSwitchPref.setTitle(R.string.permit_usage_access);
        this.mUsageDesc.setSummary(R.string.usage_access_description);
        this.mSwitchPref.setOnPreferenceChangeListener(this);
        this.mSettingsIntent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.USAGE_ACCESS_CONFIG").setPackage(this.mPackageName);
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        AppStateUsageBridge appStateUsageBridge = this.mUsageBridge;
        if (appStateUsageBridge != null) {
            appStateUsageBridge.release();
        }
        super.onDestroy();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (preference == this.mSwitchPref) {
            AppStateUsageBridge.UsageState usageState = this.mUsageState;
            if (usageState != null && booleanValue != usageState.isPermissible()) {
                if (booleanValue) {
                    Intent interceptIntent = AdbUtils.getInterceptIntent(this.mPackageName, "perm_app_statistics", getContext().getResources().getString(R.string.usage_access));
                    if (AdbUtils.isIntentEnable(getContext(), interceptIntent)) {
                        startActivityForResult(interceptIntent, 102);
                        return true;
                    }
                }
                if (this.mUsageState.isPermissible() && this.mDpm.isProfileOwnerApp(this.mPackageName)) {
                    new AlertDialog.Builder(getContext()).setIcon(17302415).setTitle(17039380).setMessage(R.string.work_profile_usage_access_warning).setPositiveButton(R.string.okay, (DialogInterface.OnClickListener) null).show();
                }
                setHasAccess(!this.mUsageState.isPermissible());
                refreshUi();
            }
            return true;
        }
        return false;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected boolean refreshUi() {
        PackageInfo packageInfo;
        retrieveAppEntry();
        if (this.mAppEntry == null || (packageInfo = this.mPackageInfo) == null) {
            return false;
        }
        AppStateUsageBridge.UsageState usageInfo = this.mUsageBridge.getUsageInfo(this.mPackageName, packageInfo.applicationInfo.uid);
        this.mUsageState = usageInfo;
        this.mSwitchPref.setChecked(usageInfo.isPermissible());
        this.mSwitchPref.setEnabled(this.mUsageState.permissionDeclared);
        ResolveInfo resolveActivityAsUser = this.mPm.resolveActivityAsUser(this.mSettingsIntent, 128, this.mUserId);
        if (resolveActivityAsUser != null) {
            Bundle bundle = resolveActivityAsUser.activityInfo.metaData;
            Intent intent = this.mSettingsIntent;
            ActivityInfo activityInfo = resolveActivityAsUser.activityInfo;
            intent.setComponent(new ComponentName(activityInfo.packageName, activityInfo.name));
            if (bundle != null && bundle.containsKey("android.settings.metadata.USAGE_ACCESS_REASON")) {
                this.mSwitchPref.setSummary(bundle.getString("android.settings.metadata.USAGE_ACCESS_REASON"));
            }
        }
        if (ApplicationHelper.shouldGrantPermission(getActivity(), this.mPackageName)) {
            Log.d("Enterprise", "Package " + this.mPackageName + " is protected");
            this.mSwitchPref.setEnabled(false);
            return true;
        }
        return true;
    }
}
