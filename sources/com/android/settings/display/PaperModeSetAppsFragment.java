package com.android.settings.display;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MiuiSettings;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.miuisettings.preference.CheckBoxPreference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import miuix.os.AsyncTaskWithProgress;

/* loaded from: classes.dex */
public class PaperModeSetAppsFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final HashSet<String> WHITE_LIST = DisplayUtils.WHITE_LIST;
    private Context mContext;
    private BroadcastReceiver mPackageChangeReceiver = new BroadcastReceiver() { // from class: com.android.settings.display.PaperModeSetAppsFragment.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            PaperModeSetAppsFragment.this.loadPackages();
        }
    };
    private PackageManager mPackageManager;
    private PreferenceCategory mPaperModeAppsCategory;
    private PreferenceCategory mPaperModeOffAppsCategory;
    private HashMap<String, Boolean> mPkg2PaperMode;
    private AsyncTaskWithProgress mUpdatepkgListTask;

    /* loaded from: classes.dex */
    private class FilterItemPreference extends CheckBoxPreference {
        public FilterItemPreference(Context context, ApplicationInfo applicationInfo, boolean z) {
            super(context);
            setTitle(applicationInfo.loadLabel(PaperModeSetAppsFragment.this.mPackageManager));
            setIcon(applicationInfo.loadIcon(PaperModeSetAppsFragment.this.mPackageManager));
            setKey(applicationInfo.packageName);
            setPersistent(false);
            setChecked(z);
            setOnPreferenceChangeListener(PaperModeSetAppsFragment.this);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadPackages() {
        final List<ApplicationInfo> installedApplications = this.mPackageManager.getInstalledApplications(0);
        this.mPkg2PaperMode = MiuiSettings.ScreenEffect.getScreenModePkgList(this.mContext, "screen_paper_mode_white_list");
        if (this.mUpdatepkgListTask == null) {
            AsyncTaskWithProgress<Object, Object> asyncTaskWithProgress = new AsyncTaskWithProgress<Object, Object>(getFragmentManager()) { // from class: com.android.settings.display.PaperModeSetAppsFragment.2
                private List<FilterItemPreference> mPaperModeApps = new ArrayList();
                private List<FilterItemPreference> mPaperModeOffApps = new ArrayList();
                private final Comparator<FilterItemPreference> mComparator = new Comparator<FilterItemPreference>() { // from class: com.android.settings.display.PaperModeSetAppsFragment.2.1
                    private final Collator sCollator = Collator.getInstance();

                    @Override // java.util.Comparator
                    public int compare(FilterItemPreference filterItemPreference, FilterItemPreference filterItemPreference2) {
                        return this.sCollator.compare(filterItemPreference.getTitle(), filterItemPreference2.getTitle());
                    }
                };

                @Override // android.os.AsyncTask
                protected Object doInBackground(Object... objArr) {
                    this.mPaperModeApps.clear();
                    this.mPaperModeOffApps.clear();
                    for (ApplicationInfo applicationInfo : installedApplications) {
                        if (PaperModeSetAppsFragment.this.mPkg2PaperMode.containsKey(applicationInfo.packageName) && ((Boolean) PaperModeSetAppsFragment.this.mPkg2PaperMode.get(applicationInfo.packageName)).booleanValue()) {
                            List<FilterItemPreference> list = this.mPaperModeApps;
                            PaperModeSetAppsFragment paperModeSetAppsFragment = PaperModeSetAppsFragment.this;
                            list.add(new FilterItemPreference(paperModeSetAppsFragment.getPrefContext(), applicationInfo, true));
                        } else if ((applicationInfo.flags & 1) == 0 || PaperModeSetAppsFragment.WHITE_LIST.contains(applicationInfo.packageName)) {
                            List<FilterItemPreference> list2 = this.mPaperModeOffApps;
                            PaperModeSetAppsFragment paperModeSetAppsFragment2 = PaperModeSetAppsFragment.this;
                            list2.add(new FilterItemPreference(paperModeSetAppsFragment2.getPrefContext(), applicationInfo, false));
                        }
                    }
                    Collections.sort(this.mPaperModeApps, this.mComparator);
                    Collections.sort(this.mPaperModeOffApps, this.mComparator);
                    return null;
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // miuix.os.AsyncTaskWithProgress, android.os.AsyncTask
                public void onPostExecute(Object obj) {
                    super.onPostExecute(obj);
                    PaperModeSetAppsFragment.this.updatePkgList(this.mPaperModeApps, this.mPaperModeOffApps);
                    PaperModeSetAppsFragment.this.mUpdatepkgListTask = null;
                }
            };
            this.mUpdatepkgListTask = asyncTaskWithProgress;
            asyncTaskWithProgress.setMessage(R.string.screen_paper_mode_apps_loading).setCancelable(false).execute(new Object[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePkgList(List<FilterItemPreference> list, List<FilterItemPreference> list2) {
        getPreferenceScreen().removeAll();
        this.mPaperModeAppsCategory.removeAll();
        this.mPaperModeOffAppsCategory.removeAll();
        if (list.size() > 0) {
            getPreferenceScreen().addPreference(this.mPaperModeAppsCategory);
            for (int i = 0; i < list.size(); i++) {
                this.mPaperModeAppsCategory.addPreference(list.get(i));
            }
        }
        if (list2.size() > 0) {
            getPreferenceScreen().addPreference(this.mPaperModeOffAppsCategory);
            for (int i2 = 0; i2 < list2.size(); i2++) {
                this.mPaperModeOffAppsCategory.addPreference(list2.get(i2));
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return PaperModeSetAppsFragment.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getActivity();
        this.mPackageManager = getPackageManager();
        addPreferencesFromResource(R.xml.paper_mode_apps);
        this.mPaperModeAppsCategory = (PreferenceCategory) getPreferenceScreen().findPreference("paper_mode_pkg_list");
        this.mPaperModeOffAppsCategory = (PreferenceCategory) getPreferenceScreen().findPreference("paper_mode_off_pkg_list");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGE_FULLY_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        getActivity().registerReceiver(this.mPackageChangeReceiver, intentFilter);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mContext.unregisterReceiver(this.mPackageChangeReceiver);
        AsyncTaskWithProgress asyncTaskWithProgress = this.mUpdatepkgListTask;
        if (asyncTaskWithProgress != null) {
            asyncTaskWithProgress.cancel(true);
            this.mUpdatepkgListTask = null;
        }
        super.onDestroy();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        String key = preference.getKey();
        if (preference instanceof FilterItemPreference) {
            this.mPkg2PaperMode.put(key, (Boolean) obj);
            MiuiSettings.ScreenEffect.setScreenModePkgList(this.mContext, this.mPkg2PaperMode, "screen_paper_mode_white_list");
            loadPackages();
            return true;
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        loadPackages();
    }
}
