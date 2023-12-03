package com.android.settings.applications.appinfo;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.security.AdbUtils;
import com.android.settings.R;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.AppInfoWithHeader;
import com.android.settings.applications.AppStateInstallAppsBridge;
import com.android.settings.search.tree.SecuritySettingsTree;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.applications.ApplicationsState;
import miui.os.Build;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class ExternalSourcesDetails extends AppInfoWithHeader implements Preference.OnPreferenceChangeListener {
    private AppStateInstallAppsBridge mAppBridge;
    private AppOpsManager mAppOpsManager;
    private Context mContext;
    private AppStateInstallAppsBridge.InstallAppsState mInstallAppsState;
    private RestrictedSwitchPreference mSwitchPref;
    private UserManager mUserManager;

    private boolean doUnknownSourceVerify() {
        if (Build.IS_INTERNATIONAL_BUILD || Build.IS_TABLET || UserHandle.myUserId() != 0) {
            return false;
        }
        Intent intent = new Intent("com.miui.securitycenter.action.UNKNOWN_SOURCE_VERIFY");
        intent.setPackage(SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME);
        startActivityForResult(intent, 101);
        return true;
    }

    public static CharSequence getPreferenceSummary(Context context, ApplicationsState.AppEntry appEntry) {
        UserHandle userHandleForUid = UserHandle.getUserHandleForUid(appEntry.info.uid);
        UserManager userManager = UserManager.get(context);
        int userRestrictionSource = userManager.getUserRestrictionSource("no_install_unknown_sources_globally", userHandleForUid) | userManager.getUserRestrictionSource("no_install_unknown_sources", userHandleForUid);
        if ((userRestrictionSource & 1) != 0) {
            return context.getString(R.string.disabled_by_admin);
        }
        if (userRestrictionSource != 0) {
            return context.getString(R.string.disabled);
        }
        AppStateInstallAppsBridge appStateInstallAppsBridge = new AppStateInstallAppsBridge(context, null, null);
        ApplicationInfo applicationInfo = appEntry.info;
        return context.getString(appStateInstallAppsBridge.createInstallAppsStateFor(applicationInfo.packageName, applicationInfo.uid).canInstallApps() ? R.string.app_permission_summary_allowed : R.string.app_permission_summary_not_allowed);
    }

    private void setCanInstallApps(boolean z) {
        this.mAppOpsManager.setMode(66, this.mPackageInfo.applicationInfo.uid, this.mPackageName, z ? 0 : 2);
    }

    private void setUnknownSourceResult(boolean z) {
        setResult(z ? -1 : 0);
        setCanInstallApps(z);
        refreshUi();
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 808;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 101) {
            setUnknownSourceResult(i2 == -1);
        } else if (i == 102) {
            boolean z = i2 == -1;
            AppStateInstallAppsBridge.InstallAppsState installAppsState = this.mInstallAppsState;
            if (installAppsState == null || z == installAppsState.canInstallApps()) {
                return;
            }
            if (z && doUnknownSourceVerify()) {
                return;
            }
            setUnknownSourceResult(true);
        }
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mAppBridge = new AppStateInstallAppsBridge(activity, ((AppInfoBase) this).mState, null);
        this.mAppOpsManager = (AppOpsManager) activity.getSystemService("appops");
        this.mUserManager = UserManager.get(activity);
        addPreferencesFromResource(R.xml.external_sources_details);
        RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) findPreference("external_sources_settings_switch");
        this.mSwitchPref = restrictedSwitchPreference;
        restrictedSwitchPreference.setOnPreferenceChangeListener(this);
        this.mContext = activity;
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mAppBridge.release();
        super.onDestroy();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (preference == this.mSwitchPref) {
            AppStateInstallAppsBridge.InstallAppsState installAppsState = this.mInstallAppsState;
            if (installAppsState != null && booleanValue != installAppsState.canInstallApps()) {
                if (booleanValue) {
                    Intent interceptIntent = AdbUtils.getInterceptIntent(this.mPackageName, "perm_install_unknown", this.mContext.getResources().getString(R.string.install_other_apps));
                    if (AdbUtils.isIntentEnable(this.mContext, interceptIntent)) {
                        startActivityForResult(interceptIntent, 102);
                        return true;
                    } else if (doUnknownSourceVerify()) {
                        return true;
                    }
                }
                setUnknownSourceResult(booleanValue);
            }
            return true;
        }
        return false;
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected boolean refreshUi() {
        PackageInfo packageInfo = this.mPackageInfo;
        if (packageInfo == null || packageInfo.applicationInfo == null) {
            return false;
        }
        if (this.mUserManager.hasBaseUserRestriction("no_install_unknown_sources", UserHandle.of(UserHandle.myUserId()))) {
            this.mSwitchPref.setChecked(false);
            this.mSwitchPref.setSummary(R.string.disabled);
            this.mSwitchPref.setEnabled(false);
            return true;
        }
        this.mSwitchPref.checkRestrictionAndSetDisabled("no_install_unknown_sources");
        if (!this.mSwitchPref.isDisabledByAdmin()) {
            this.mSwitchPref.checkRestrictionAndSetDisabled("no_install_unknown_sources_globally");
        }
        if (this.mSwitchPref.isDisabledByAdmin()) {
            return true;
        }
        AppStateInstallAppsBridge.InstallAppsState createInstallAppsStateFor = this.mAppBridge.createInstallAppsStateFor(this.mPackageName, this.mPackageInfo.applicationInfo.uid);
        this.mInstallAppsState = createInstallAppsStateFor;
        if (createInstallAppsStateFor.isPotentialAppSource()) {
            this.mSwitchPref.setChecked(this.mInstallAppsState.canInstallApps());
            return true;
        }
        this.mSwitchPref.setEnabled(false);
        return true;
    }
}
