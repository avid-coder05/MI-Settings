package com.android.settings.device;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.widget.CustomTextColorValuePreference;
import com.android.settingslib.util.ToastUtil;

/* loaded from: classes.dex */
public class PreInstallApplication extends DashboardFragment {
    private static final String TAG = PreInstallApplication.class.getSimpleName();
    private CustomTextColorValuePreference mMorePreInstallAppPref;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return TAG;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return PreInstallApplication.class.getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.pre_install_app;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mMorePreInstallAppPref = (CustomTextColorValuePreference) findPreference("more_pre_install_app");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == this.mMorePreInstallAppPref) {
            FragmentActivity activity = getActivity();
            if (!MiuiUtils.isNetworkConnected(activity)) {
                ToastUtil.show(activity, R.string.pre_install_network_unavailable, 0);
                return true;
            }
            Intent intent = new Intent("miui.intent.action.VIEW_PRE_INSTALLED_APPLICATION");
            if (MiuiUtils.getInstance().canFindActivity(activity, intent)) {
                preference.setIntent(intent);
                activity.startActivity(intent);
                return true;
            }
            return true;
        }
        return true;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.pre_install_application);
        }
    }
}
