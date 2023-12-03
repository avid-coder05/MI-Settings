package com.android.settings.applications.specialaccess.zenaccess;

import android.content.Context;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.R;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.applications.AppInfoWithHeader;
import com.android.settings.applications.specialaccess.zenaccess.ZenAccessSettingObserverMixin;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class ZenAccessDetails extends AppInfoWithHeader implements ZenAccessSettingObserverMixin.Listener {

    /* loaded from: classes.dex */
    public interface OnCheckResult {
        void onResult(boolean z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updatePreference$0(CharSequence charSequence, OnCheckResult onCheckResult, Preference preference, Object obj) {
        if (((Boolean) obj).booleanValue()) {
            ScaryWarningDialogFragment scaryWarningDialogFragment = new ScaryWarningDialogFragment();
            scaryWarningDialogFragment.setPkgInfo(this.mPackageName, charSequence, this);
            scaryWarningDialogFragment.setResultCallback(onCheckResult);
            scaryWarningDialogFragment.show(getFragmentManager(), "dialog");
            return true;
        }
        FriendlyWarningDialogFragment friendlyWarningDialogFragment = new FriendlyWarningDialogFragment();
        friendlyWarningDialogFragment.setPkgInfo(this.mPackageName, charSequence, this);
        friendlyWarningDialogFragment.setResultCallback(onCheckResult);
        friendlyWarningDialogFragment.show(getFragmentManager(), "dialog");
        return true;
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1692;
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.zen_access_permission_details);
        getSettingsLifecycle().addObserver(new ZenAccessSettingObserverMixin(getContext(), this));
    }

    @Override // com.android.settings.applications.specialaccess.zenaccess.ZenAccessSettingObserverMixin.Listener
    public void onZenAccessPolicyChanged() {
        refreshUi();
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected boolean refreshUi() {
        Context context = getContext();
        if (ZenAccessController.getPackagesRequestingNotificationPolicyAccess().contains(this.mPackageName)) {
            updatePreference(context, (SwitchPreference) findPreference("zen_access_switch"));
            return true;
        }
        return false;
    }

    public void updatePreference(final Context context, final SwitchPreference switchPreference) {
        final CharSequence loadLabel = this.mPackageInfo.applicationInfo.loadLabel(this.mPm);
        if (ZenAccessController.getAutoApprovedPackages(context).contains(this.mPackageName)) {
            switchPreference.setEnabled(false);
            switchPreference.setSummary(getString(R.string.zen_access_disabled_package_warning));
            return;
        }
        switchPreference.setChecked(ZenAccessController.hasAccess(context, this.mPackageName));
        final OnCheckResult onCheckResult = new OnCheckResult() { // from class: com.android.settings.applications.specialaccess.zenaccess.ZenAccessDetails.1
            @Override // com.android.settings.applications.specialaccess.zenaccess.ZenAccessDetails.OnCheckResult
            public void onResult(boolean z) {
                switchPreference.setChecked(ZenAccessController.hasAccess(context, ((AppInfoBase) ZenAccessDetails.this).mPackageName));
            }
        };
        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.applications.specialaccess.zenaccess.ZenAccessDetails$$ExternalSyntheticLambda0
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public final boolean onPreferenceChange(Preference preference, Object obj) {
                boolean lambda$updatePreference$0;
                lambda$updatePreference$0 = ZenAccessDetails.this.lambda$updatePreference$0(loadLabel, onCheckResult, preference, obj);
                return lambda$updatePreference$0;
            }
        });
    }
}
