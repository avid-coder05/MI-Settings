package com.android.settings.backup;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.report.InternationalCompat;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.utils.TabletUtils;
import miui.cloud.Constants;
import miuix.appcompat.app.ActionBar;

/* loaded from: classes.dex */
public class PrivacySettings extends DashboardFragment implements Preference.OnPreferenceClickListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.privacy_settings) { // from class: com.android.settings.backup.PrivacySettings.3
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            BackupSettingsHelper backupSettingsHelper = new BackupSettingsHelper(context);
            return (backupSettingsHelper.isBackupProvidedByManufacturer() || backupSettingsHelper.isIntentProvidedByTransport()) ? false : true;
        }
    };
    private ActionBar actionBar;
    private ContentObserver mObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.backup.PrivacySettings.2
        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (TextUtils.equals(uri.getLastPathSegment(), "local_backup_disable_service")) {
                PrivacySettings.this.lambda$onStart$0();
            }
        }
    };

    private void updatePrivacySettingsConfigData(Context context) {
        if (PrivacySettingsUtils.isAdminUser(context)) {
            PrivacySettingsUtils.updatePrivacyBuffer(context, PrivacySettingsConfigData.getInstance());
        }
    }

    public void customActionBar() {
        ActionBar appCompatActionBar = getAppCompatActionBar();
        this.actionBar = appCompatActionBar;
        if (appCompatActionBar == null) {
            return;
        }
        ImageView imageView = new ImageView(getActivity());
        if (isDarkModeEnable()) {
            imageView.setImageResource(R.drawable.miuix_appcompat_icon_settings_dark);
        } else {
            imageView.setImageResource(R.drawable.miuix_appcompat_icon_settings_light);
        }
        imageView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.backup.PrivacySettings.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("miui.intent.backup.LOCAL_SETTINGS_ACTIVITY");
                intent.setPackage("com.miui.backup");
                PrivacySettings.this.startActivity(intent);
            }
        });
        imageView.setContentDescription(getActivity().getResources().getString(R.string.backup_more_settings));
        this.actionBar.setEndView(imageView);
        if (TabletUtils.IS_TABLET) {
            this.actionBar.setDisplayOptions(8);
        } else {
            this.actionBar.setDisplayOptions(12);
        }
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_backup_reset;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "PrivacySettings";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 81;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.privacy_settings;
    }

    public boolean isDarkModeEnable() {
        return ((UiModeManager) getSystemService("uimode")).getNightMode() == 2;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        updatePrivacySettingsConfigData(context);
        getLifecycle().addObserver((LifecycleObserver) use(MiuiFlashDriveBackupController.class));
        InternationalCompat.trackReportEvent("setting_About_phone_backup");
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if ("cloud_restore".equals(preference.getKey())) {
            Intent intent = new Intent();
            intent.setAction("miui.intent.action.APP_SETTINGS");
            intent.setClassName(Constants.CLOUDSERVICE_PACKAGE_NAME, "com.miui.cloudservice.ui.MiCloudMainActivity");
            if (SettingsFeatures.isSplitTabletDevice() || SettingsFeatures.isFoldDevice()) {
                intent.addMiuiFlags(16);
            }
            startActivity(intent);
            return true;
        }
        return false;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        customActionBar();
        getContext().getContentResolver().registerContentObserver(Settings.System.getUriFor("local_backup_disable_service"), false, this.mObserver);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.setEndView(null);
        }
        getContext().getContentResolver().unregisterContentObserver(this.mObserver);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    /* renamed from: updatePreferenceStates */
    public void lambda$onStart$0() {
        updatePrivacySettingsConfigData(getContext());
        PreferenceGroup preferenceGroup = (PreferenceGroup) findPreference("cloud_backup_category");
        findPreference("cloud_restore").setOnPreferenceClickListener(this);
        if (MiuiUtils.isDeviceManaged(getContext()) && !MiuiUtils.isDeviceFinanceOwner(getContext())) {
            getPreferenceScreen().removePreference(preferenceGroup);
        }
        boolean z = false;
        boolean z2 = MiuiSettings.System.getBoolean(getContext().getContentResolver(), "local_backup_disable_service", false);
        Preference findPreference = findPreference("phone_backup");
        Preference findPreference2 = findPreference("computer_backup");
        Preference findPreference3 = findPreference("local_backup_usestatus");
        findPreference.setVisible(!z2);
        findPreference2.setVisible(!z2);
        if (MiuiUtils.isUsbBackupEnable(getContext())) {
            Preference findPreference4 = findPreference("flash_drive_backup");
            if (!z2 && ((MiuiFlashDriveBackupController) use(MiuiFlashDriveBackupController.class)).isAvailable()) {
                z = true;
            }
            findPreference4.setVisible(z);
        }
        findPreference3.setVisible(z2);
        super.lambda$onStart$0();
    }
}
