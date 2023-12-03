package com.android.settings.applications.appinfo;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
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
import com.android.settings.Utils;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.AppInfoWithHeader;
import com.android.settings.applications.AppStateAppOpsBridge;
import com.android.settings.applications.AppStateOverlayBridge;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.FunctionColumns;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import miui.os.Build;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class DrawOverlayDetails extends AppInfoWithHeader implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final int[] APP_OPS_OP_CODE = {24};
    private AppOpsManager mAppOpsManager;
    private AppStateOverlayBridge mOverlayBridge;
    private AppStateOverlayBridge.OverlayState mOverlayState;
    private SwitchPreference mSwitchPref;

    public static CharSequence getSummary(Context context, AppStateOverlayBridge.OverlayState overlayState) {
        return context.getString(overlayState.isPermissible() ? R.string.app_permission_summary_allowed : R.string.app_permission_summary_not_allowed);
    }

    public static CharSequence getSummary(Context context, ApplicationsState.AppEntry appEntry) {
        AppStateOverlayBridge.OverlayState overlayInfo;
        if (appEntry == null) {
            return "";
        }
        Object obj = appEntry.extraInfo;
        if (obj instanceof AppStateOverlayBridge.OverlayState) {
            overlayInfo = (AppStateOverlayBridge.OverlayState) obj;
        } else if (obj instanceof AppStateAppOpsBridge.PermissionState) {
            overlayInfo = new AppStateOverlayBridge.OverlayState((AppStateAppOpsBridge.PermissionState) appEntry.extraInfo);
        } else {
            AppStateOverlayBridge appStateOverlayBridge = new AppStateOverlayBridge(context, null, null);
            ApplicationInfo applicationInfo = appEntry.info;
            overlayInfo = appStateOverlayBridge.getOverlayInfo(applicationInfo.packageName, applicationInfo.uid);
        }
        return getSummary(context, overlayInfo);
    }

    private void setCanDrawOverlay(boolean z) {
        logSpecialPermissionChange(z, this.mPackageName);
        this.mAppOpsManager.setMode(24, this.mPackageInfo.applicationInfo.uid, this.mPackageName, z ? 0 : 2);
        if (Build.IS_CTS_BUILD) {
            return;
        }
        Intent intent = new Intent("miui.intent.action.PERMISSION_CHANGE");
        intent.setPackage("com.lbe.security.miui");
        intent.putExtra("type", "system_alert_window");
        intent.putExtra(FunctionColumns.PACKAGE, this.mPackageName);
        intent.putExtra("status", z ? "accept" : "reject");
        getActivity().sendBroadcast(intent, "miui.permission.READ_AND_WIRTE_PERMISSION_MANAGER");
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 221;
    }

    void logSpecialPermissionChange(boolean z, String str) {
        int i = z ? 770 : 771;
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider();
        metricsFeatureProvider.action(metricsFeatureProvider.getAttribution(getActivity()), i, getMetricsCategory(), str, 0);
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mOverlayBridge = new AppStateOverlayBridge(activity, ((AppInfoBase) this).mState, null);
        this.mAppOpsManager = (AppOpsManager) activity.getSystemService("appops");
        if (!Utils.isSystemAlertWindowEnabled(activity)) {
            this.mPackageInfo = null;
            return;
        }
        addPreferencesFromResource(R.xml.draw_overlay_permissions_details);
        SwitchPreference switchPreference = (SwitchPreference) findPreference("app_ops_settings_switch");
        this.mSwitchPref = switchPreference;
        switchPreference.setOnPreferenceChangeListener(this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return this.mPackageInfo == null ? layoutInflater.inflate(R.layout.manage_applications_apps_unsupported, (ViewGroup) null) : super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mOverlayBridge.release();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mSwitchPref) {
            if (this.mOverlayState != null && ((Boolean) obj).booleanValue() != this.mOverlayState.isPermissible()) {
                setCanDrawOverlay(!this.mOverlayState.isPermissible());
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
        PackageInfo packageInfo = this.mPackageInfo;
        if (packageInfo == null) {
            return true;
        }
        AppStateOverlayBridge.OverlayState overlayInfo = this.mOverlayBridge.getOverlayInfo(this.mPackageName, packageInfo.applicationInfo.uid);
        this.mOverlayState = overlayInfo;
        this.mSwitchPref.setChecked(overlayInfo.isPermissible());
        SwitchPreference switchPreference = this.mSwitchPref;
        AppStateOverlayBridge.OverlayState overlayState = this.mOverlayState;
        switchPreference.setEnabled(overlayState.permissionDeclared && overlayState.controlEnabled);
        return true;
    }
}
