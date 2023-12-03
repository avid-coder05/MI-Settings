package com.android.settings.applications.defaultapps;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.android.internal.telephony.SmsApplication;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.applications.DefaultAppsHelper;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
import miui.cloud.sync.providers.ContactsSyncInfoProvider;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiDefaultAppSettings extends DashboardFragment {
    private List<AbstractPreferenceController> mDefaultAppControllers;
    private String[] mEntries;

    /* loaded from: classes.dex */
    public interface ResetToDefault {
        void resetDefaults();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetToDefault() {
        ActivityInfo activityInfo;
        ActivityInfo activityInfo2;
        PackageManager packageManager = getPackageManager();
        ComponentName homeActivities = packageManager.getHomeActivities(new ArrayList());
        if (homeActivities != null && homeActivities.getPackageName() != null) {
            packageManager.clearPackagePreferredActivities(homeActivities.getPackageName());
        }
        TelecomManager.from(getActivity()).setDefaultDialer(ContactsSyncInfoProvider.AUTHORITY);
        SmsApplication.setDefaultApplication("com.android.mms", getActivity());
        Settings.Secure.putInt(getContentResolver(), "assist_structure_enabled", 1);
        Settings.Secure.putInt(getContentResolver(), "assist_screenshot_enabled", 1);
        if (MiuiUtils.isApplicationInstalled(getActivity(), "com.mi.globalbrowser")) {
            packageManager.setDefaultBrowserPackageNameAsUser("com.mi.globalbrowser", getContext().getUserId());
        } else if (MiuiUtils.isApplicationInstalled(getActivity(), "com.android.browser")) {
            packageManager.setDefaultBrowserPackageNameAsUser("com.android.browser", getContext().getUserId());
        } else {
            packageManager.setDefaultBrowserPackageNameAsUser(null, getContext().getUserId());
        }
        ResolveInfo resolveActivity = packageManager.resolveActivity(DefaultAppsHelper.getIntent(new IntentFilter("android.intent.action.ASSIST")), 0);
        if (resolveActivity != null && (activityInfo2 = resolveActivity.activityInfo) != null) {
            packageManager.clearPackagePreferredActivities(activityInfo2.packageName);
        }
        ResolveInfo resolveActivity2 = packageManager.resolveActivity(DefaultAppsHelper.getIntent(new IntentFilter("android.intent.action.VOICE_COMMAND")), 0);
        if (resolveActivity2 != null && (activityInfo = resolveActivity2.activityInfo) != null) {
            packageManager.clearPackagePreferredActivities(activityInfo.packageName);
        }
        for (AbstractPreferenceController abstractPreferenceController : this.mDefaultAppControllers) {
            if (abstractPreferenceController instanceof ResetToDefault) {
                ((ResetToDefault) abstractPreferenceController).resetDefaults();
            }
        }
        DefaultAppsHelper.loadDefaultVideoPlayer(getActivity());
        updatePreferenceStates();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(final Context context) {
        this.mEntries = getResources().getStringArray(R.array.preferred_app_entries);
        ArrayList arrayList = new ArrayList();
        this.mDefaultAppControllers = arrayList;
        arrayList.add(new DefaultHomePreferenceController(context));
        this.mDefaultAppControllers.add(new DefaultPhonePreferenceController(context));
        this.mDefaultAppControllers.add(new DefaultSmsPreferenceController(context));
        this.mDefaultAppControllers.add(new DefaultBrowserPreferenceController(context));
        List<AbstractPreferenceController> list = this.mDefaultAppControllers;
        final int i = 4;
        final String str = this.mEntries[4];
        final String str2 = "default_camera";
        list.add(new DefaultTypePreferencceController(context, str2, i, str) { // from class: com.android.settings.applications.defaultapps.DefaultAppPreferenceControllerImpl$DefaultCameraPreferenceController
        });
        List<AbstractPreferenceController> list2 = this.mDefaultAppControllers;
        final int i2 = 5;
        final String str3 = this.mEntries[5];
        final String str4 = "default_gallery";
        list2.add(new DefaultTypePreferencceController(context, str4, i2, str3) { // from class: com.android.settings.applications.defaultapps.DefaultAppPreferenceControllerImpl$DefaultGalleryPreferenceController
        });
        List<AbstractPreferenceController> list3 = this.mDefaultAppControllers;
        final int i3 = 6;
        final String str5 = this.mEntries[6];
        final String str6 = "default_music";
        list3.add(new DefaultTypePreferencceController(context, str6, i3, str5) { // from class: com.android.settings.applications.defaultapps.DefaultAppPreferenceControllerImpl$DefaultMusicPreferenceController
        });
        List<AbstractPreferenceController> list4 = this.mDefaultAppControllers;
        final int i4 = 7;
        final String str7 = this.mEntries[7];
        final String str8 = "default_email";
        list4.add(new DefaultTypePreferencceController(context, str8, i4, str7) { // from class: com.android.settings.applications.defaultapps.DefaultAppPreferenceControllerImpl$DefaultEmailPreferenceController
        });
        List<AbstractPreferenceController> list5 = this.mDefaultAppControllers;
        final int i5 = 8;
        final String str9 = this.mEntries[8];
        final String str10 = "default_video";
        list5.add(new DefaultTypePreferencceController(context, str10, i5, str9) { // from class: com.android.settings.applications.defaultapps.DefaultAppPreferenceControllerImpl$DefaultVideoPreferenceController
        });
        return this.mDefaultAppControllers;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "MiuiDefaultAppSettings";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.miui_app_default_settings;
    }

    public boolean isEasyMode() {
        return Settings.System.getInt(getContext().getContentResolver(), "elderly_mode", 0) == 1;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        setHasOptionsMenu(true);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (isEasyMode()) {
            getPreferenceScreen().removePreference(getPreferenceScreen().findPreference("default_home"));
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuItem add = menu.add(0, 1, 0, R.string.preferred_app_settings_reset);
        add.setIcon(R.drawable.action_button_clear);
        add.setShowAsAction(5);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 1) {
            new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.preferred_app_settings_reset)).setMessage(getString(R.string.preferred_app_settings_reset_message)).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setPositiveButton(getString(R.string.preferred_app_settings_reset_button), new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.defaultapps.MiuiDefaultAppSettings.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    MiuiDefaultAppSettings.this.resetToDefault();
                }
            }).show();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
