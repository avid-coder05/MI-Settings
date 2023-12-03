package com.android.settings.applications.specialaccess.pictureinpicture;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.R;
import com.android.settings.applications.AppInfoWithHeader;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class PictureInPictureDetails extends AppInfoWithHeader implements Preference.OnPreferenceChangeListener {
    private SwitchPreference mSwitchPref;

    static boolean getEnterPipStateForPackage(Context context, int i, String str) {
        return ((AppOpsManager) context.getSystemService(AppOpsManager.class)).checkOpNoThrow(67, i, str) == 0;
    }

    public static int getPreferenceSummary(Context context, int i, String str) {
        return getEnterPipStateForPackage(context, i, str) ? R.string.app_permission_summary_allowed : R.string.app_permission_summary_not_allowed;
    }

    static void setEnterPipStateForPackage(Context context, int i, String str, boolean z) {
        ((AppOpsManager) context.getSystemService(AppOpsManager.class)).setMode(67, i, str, z ? 0 : 2);
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 812;
    }

    void logSpecialPermissionChange(boolean z, String str) {
        int i = z ? 813 : 814;
        MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider();
        metricsFeatureProvider.action(metricsFeatureProvider.getAttribution(getActivity()), i, getMetricsCategory(), str, 0);
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.picture_in_picture_permissions_details);
        SwitchPreference switchPreference = (SwitchPreference) findPreference("app_ops_settings_switch");
        this.mSwitchPref = switchPreference;
        switchPreference.setTitle(R.string.picture_in_picture_app_detail_switch);
        this.mSwitchPref.setOnPreferenceChangeListener(this);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mSwitchPref) {
            Boolean bool = (Boolean) obj;
            logSpecialPermissionChange(bool.booleanValue(), this.mPackageName);
            setEnterPipStateForPackage(getActivity(), this.mPackageInfo.applicationInfo.uid, this.mPackageName, bool.booleanValue());
            return true;
        }
        return false;
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected boolean refreshUi() {
        this.mSwitchPref.setChecked(getEnterPipStateForPackage(getActivity(), this.mPackageInfo.applicationInfo.uid, this.mPackageName));
        return true;
    }
}
