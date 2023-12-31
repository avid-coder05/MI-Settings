package com.android.settings.applications.appinfo;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.R;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.AppInfoWithHeader;
import com.android.settings.applications.AppStateAppOpsBridge;
import com.android.settings.applications.AppStateManageExternalStorageBridge;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class ManageExternalStorageDetails extends AppInfoWithHeader implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private AppOpsManager mAppOpsManager;
    private AppStateManageExternalStorageBridge mBridge;
    private MetricsFeatureProvider mMetricsFeatureProvider;
    private AppStateAppOpsBridge.PermissionState mPermissionState;
    private SwitchPreference mSwitchPref;

    private static CharSequence getSummary(Context context, AppStateAppOpsBridge.PermissionState permissionState) {
        return context.getString(permissionState.isPermissible() ? R.string.app_permission_summary_allowed : R.string.app_permission_summary_not_allowed);
    }

    public static CharSequence getSummary(Context context, ApplicationsState.AppEntry appEntry) {
        AppStateAppOpsBridge.PermissionState manageExternalStoragePermState;
        Object obj = appEntry.extraInfo;
        if (obj instanceof AppStateAppOpsBridge.PermissionState) {
            manageExternalStoragePermState = (AppStateAppOpsBridge.PermissionState) obj;
        } else {
            AppStateManageExternalStorageBridge appStateManageExternalStorageBridge = new AppStateManageExternalStorageBridge(context, null, null);
            ApplicationInfo applicationInfo = appEntry.info;
            manageExternalStoragePermState = appStateManageExternalStorageBridge.getManageExternalStoragePermState(applicationInfo.packageName, applicationInfo.uid);
        }
        return getSummary(context, manageExternalStoragePermState);
    }

    private void logSpecialPermissionChange(boolean z, String str) {
        int i = z ? 1730 : 1731;
        MetricsFeatureProvider metricsFeatureProvider = this.mMetricsFeatureProvider;
        metricsFeatureProvider.action(metricsFeatureProvider.getAttribution(getActivity()), i, getMetricsCategory(), str, 0);
    }

    private void setManageExternalStorageState(boolean z) {
        logSpecialPermissionChange(z, this.mPackageName);
        this.mAppOpsManager.setUidMode(92, this.mPackageInfo.applicationInfo.uid, z ? 0 : 2);
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1822;
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mBridge = new AppStateManageExternalStorageBridge(activity, ((AppInfoBase) this).mState, null);
        this.mAppOpsManager = (AppOpsManager) activity.getSystemService("appops");
        addPreferencesFromResource(R.xml.manage_external_storage_permission_details);
        SwitchPreference switchPreference = (SwitchPreference) findPreference("app_ops_settings_switch");
        this.mSwitchPref = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return this.mPackageInfo == null ? layoutInflater.inflate(R.layout.manage_applications_apps_unsupported, (ViewGroup) null) : super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mBridge.release();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mSwitchPref) {
            AppStateAppOpsBridge.PermissionState permissionState = this.mPermissionState;
            if (permissionState == null || obj.equals(Boolean.valueOf(permissionState.isPermissible()))) {
                return true;
            }
            setManageExternalStorageState(((Boolean) obj).booleanValue());
            refreshUi();
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
        PackageInfo packageInfo = this.mPackageInfo;
        if (packageInfo == null) {
            return true;
        }
        AppStateAppOpsBridge.PermissionState manageExternalStoragePermState = this.mBridge.getManageExternalStoragePermState(this.mPackageName, packageInfo.applicationInfo.uid);
        this.mPermissionState = manageExternalStoragePermState;
        this.mSwitchPref.setChecked(manageExternalStoragePermState.isPermissible());
        this.mSwitchPref.setEnabled(this.mPermissionState.permissionDeclared);
        return true;
    }
}
