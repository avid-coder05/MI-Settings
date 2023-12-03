package com.android.settings.applications;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.ArraySet;
import android.view.View;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.search.FunctionColumns;
import com.android.settingslib.applications.AppUtils;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class AppLaunchSettings extends AppInfoWithHeader implements View.OnClickListener, Preference.OnPreferenceChangeListener {
    private AppDomainsPreference mAppDomainUrls;
    private Preference mAppLinkState;
    private ClearDefaultsPreference mClearDefaultsPreference;
    private boolean mHasDomainUrls;
    private boolean mIsBrowser;
    private PackageManager mPm;

    private CharSequence[] getEntries(String str) {
        ArraySet<String> handledDomains = Utils.getHandledDomains(this.mPm, str);
        return (CharSequence[]) handledDomains.toArray(new CharSequence[handledDomains.size()]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onCreate$0(Preference preference) {
        Bundle bundle = new Bundle();
        bundle.putString(FunctionColumns.PACKAGE, this.mPackageName);
        bundle.putInt("uid", this.mUserId);
        new SubSettingLauncher(getContext()).setDestination("com.android.settings.applications.OpenSupportedLinks").setArguments(bundle).setSourceMetricsCategory(17).setTitleRes(-1).launch();
        return true;
    }

    private int linkStateToResourceId(int i) {
        return i != 2 ? i != 3 ? R.string.app_link_open_ask : R.string.app_link_open_never : R.string.app_link_open_always;
    }

    private void setAppLinkStateSummary() {
        this.mAppLinkState.setSummary(linkStateToResourceId(this.mPm.getIntentVerificationStatusAsUser(this.mPackageName, UserHandle.myUserId())));
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 17;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.installed_app_launch_settings);
        this.mAppDomainUrls = (AppDomainsPreference) findPreference("app_launch_supported_domain_urls");
        this.mClearDefaultsPreference = (ClearDefaultsPreference) findPreference("app_launch_clear_defaults");
        Preference findPreference = findPreference("app_link_state");
        this.mAppLinkState = findPreference;
        findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.applications.AppLaunchSettings$$ExternalSyntheticLambda0
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                boolean lambda$onCreate$0;
                lambda$onCreate$0 = AppLaunchSettings.this.lambda$onCreate$0(preference);
                return lambda$onCreate$0;
            }
        });
        this.mPm = getActivity().getPackageManager();
        boolean isBrowserApp = AppUtils.isBrowserApp(getContext(), this.mPackageName, UserHandle.myUserId());
        this.mIsBrowser = isBrowserApp;
        this.mHasDomainUrls = (this.mAppEntry.info.privateFlags & 16) != 0;
        if (isBrowserApp) {
            this.mAppLinkState.setShouldDisableView(true);
            this.mAppLinkState.setEnabled(false);
            this.mAppDomainUrls.setShouldDisableView(true);
            this.mAppDomainUrls.setEnabled(false);
            return;
        }
        CharSequence[] entries = getEntries(this.mPackageName);
        this.mAppDomainUrls.setTitles(entries);
        this.mAppDomainUrls.setValues(new int[entries.length]);
        this.mAppLinkState.setEnabled(this.mHasDomainUrls);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return true;
    }

    @Override // com.android.settings.applications.AppInfoBase
    protected boolean refreshUi() {
        if (this.mHasDomainUrls) {
            setAppLinkStateSummary();
        }
        this.mClearDefaultsPreference.setPackageName(this.mPackageName);
        this.mClearDefaultsPreference.setAppEntry(this.mAppEntry);
        return true;
    }
}
