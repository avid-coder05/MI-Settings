package com.android.settings;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.license.LicenseHtmlLoaderCompat;
import java.io.File;
import miui.os.Build;
import miui.settings.commonlib.MemoryOptimizationUtil;

/* loaded from: classes.dex */
public class Legal extends DashboardFragment implements LoaderManager.LoaderCallbacks<File> {
    private static final String TAG = Legal.class.getSimpleName();
    private String mLicenseGplHtmlPath = "/system/etc/NOTICE_GPL.html.gz";
    private Preference mMiuiCopyright;
    private Preference mMiuiPrivacyPolicy;
    private Preference mMiuiSar;
    private Preference mMiuiUserAgreement;
    private Preference mMiuiUserExperienceProgram;
    private Preference mPocoPrivacyPolicy;
    private Preference mPocoUserAgreement;
    private Preference mWirttenOffer;

    private void generateHTMLUriForLicense() {
        File file = new File("/system/etc/NOTICE.html.gz");
        if (!file.exists() || file.length() == 0) {
            getLoaderManager().initLoader(0, Bundle.EMPTY, this);
        } else {
            startActivityForLicense(Uri.fromFile(file));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onCreate$0(Preference preference) {
        generateHTMLUriForLicense();
        return true;
    }

    private void showLicenseLoadErrorToast() {
        Toast.makeText(getActivity(), R.string.settings_license_activity_unavailable, 1).show();
    }

    private void startActivityForLicense(Uri uri) {
        Intent intent = new Intent();
        intent.setData(uri);
        if ("content".equals(uri.getScheme())) {
            getActivity().grantUriPermission(MemoryOptimizationUtil.CONTROLLER_PKG, uri, 1);
        }
        intent.setClassName(MemoryOptimizationUtil.CONTROLLER_PKG, "com.android.settings.SettingsLicenseActivity");
        try {
            getActivity().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Failed to find viewer", e);
            showLicenseLoadErrorToast();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return TAG;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return Legal.class.getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.device_info_legal;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Utils.updatePreferenceToSpecificActivityOrRemove(activity, preferenceScreen, "terms", 1);
        Utils.updatePreferenceToSpecificActivityOrRemove(activity, preferenceScreen, "license", 1);
        Utils.updatePreferenceToSpecificActivityOrRemove(activity, preferenceScreen, "copyright", 1);
        this.mMiuiCopyright = findPreference("miuiCopyright");
        this.mMiuiUserAgreement = findPreference("miuiUserAgreement");
        this.mMiuiPrivacyPolicy = findPreference("miuiPrivacyPolicy");
        this.mMiuiSar = findPreference("miuiSar");
        this.mMiuiUserExperienceProgram = findPreference("miuiUserExperienceProgram");
        if (!Build.IS_GLOBAL_BUILD) {
            preferenceScreen.removePreference(this.mMiuiSar);
        }
        this.mWirttenOffer = findPreference("writtenOffer");
        this.mLicenseGplHtmlPath = SystemProperties.get("ro.config.license_gpl_path", "/system/etc/NOTICE_GPL.html.gz");
        if (SettingsFeatures.isNeedRemoveWrittenOffer()) {
            preferenceScreen.removePreference(this.mWirttenOffer);
        }
        this.mPocoUserAgreement = findPreference("pocoUserAgreement");
        this.mPocoPrivacyPolicy = findPreference("pocoPrivacyPolicy");
        if (!SettingsFeatures.hasPocoLauncherDefault()) {
            preferenceScreen.removePreference(this.mPocoUserAgreement);
            preferenceScreen.removePreference(this.mPocoPrivacyPolicy);
        }
        if (Build.IS_INTERNATIONAL_BUILD && this.mMiuiCopyright != null && getPreferenceScreen() != null) {
            getPreferenceScreen().removePreference(this.mMiuiCopyright);
        }
        Preference findPreference = findPreference("license");
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.Legal$$ExternalSyntheticLambda0
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    boolean lambda$onCreate$0;
                    lambda$onCreate$0 = Legal.this.lambda$onCreate$0(preference);
                    return lambda$onCreate$0;
                }
            });
        }
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public Loader<File> onCreateLoader(int i, Bundle bundle) {
        return new LicenseHtmlLoaderCompat(getActivity());
    }

    /* renamed from: onLoadFinished  reason: avoid collision after fix types in other method */
    public void onLoadFinished2(Loader loader, File file) {
        if (file != null) {
            startActivityForLicense(FileProvider.getUriForFile(getContext(), "com.android.settings.files", file));
            return;
        }
        Log.e(TAG, "Failed to generate.");
        showLicenseLoadErrorToast();
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public /* bridge */ /* synthetic */ void onLoadFinished(Loader<File> loader, File file) {
        onLoadFinished2((Loader) loader, file);
    }

    @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<File> loader) {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        getLoaderManager().destroyLoader(0);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        Intent intent = new Intent(MiuiUtils.getInstance().getViewLicenseAction());
        if (preference == this.mMiuiCopyright) {
            intent.putExtra("android.intent.extra.LICENSE_TYPE", 0);
        } else if (preference == this.mMiuiUserAgreement) {
            intent.putExtra("android.intent.extra.LICENSE_TYPE", 2);
        } else if (preference == this.mMiuiPrivacyPolicy) {
            intent.putExtra("android.intent.extra.LICENSE_TYPE", 1);
        } else if (preference == this.mMiuiUserExperienceProgram) {
            intent.putExtra("android.intent.extra.LICENSE_TYPE", 8);
        } else if (preference == this.mPocoUserAgreement) {
            intent.putExtra("android.intent.extra.LICENSE_TYPE", 16);
        } else if (preference == this.mPocoPrivacyPolicy) {
            intent.putExtra("android.intent.extra.LICENSE_TYPE", 17);
        }
        if (MiuiUtils.getInstance().canFindActivity(getContext(), intent) && intent.hasExtra("android.intent.extra.LICENSE_TYPE")) {
            preference.setIntent(intent);
        } else if (preference == this.mMiuiSar) {
            intent.putExtra("android.intent.extra.LICENSE_TYPE", 7);
            preference.setIntent(intent);
        } else if (preference == this.mWirttenOffer) {
            Intent intent2 = new Intent("android.settings.LICENSE");
            intent2.putExtra("license_path", this.mLicenseGplHtmlPath);
            intent2.putExtra("license_type", "written_offer");
            preference.setIntent(intent2);
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.legal_information);
        }
    }
}
