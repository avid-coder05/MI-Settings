package com.android.settings.datausage;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.os.UserHandle;
import android.telephony.SubscriptionManager;
import android.util.ArraySet;
import android.util.IconDrawableFactory;
import android.view.View;
import android.widget.AdapterView;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.R;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settingslib.AppItem;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.net.NetworkCycleDataForUid;
import com.android.settingslib.net.NetworkCycleDataForUidLoader;
import com.android.settingslib.net.UidDetail;
import com.android.settingslib.net.UidDetailProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class AppDataUsage extends DataUsageBaseFragment implements Preference.OnPreferenceChangeListener, DataSaverBackend.Listener {
    private AppItem mAppItem;
    private PreferenceCategory mAppList;
    private Preference mAppSettings;
    private Intent mAppSettingsIntent;
    private Preference mBackgroundUsage;
    private Context mContext;
    private SpinnerPreference mCycle;
    private CycleAdapter mCycleAdapter;
    private ArrayList<Long> mCycles;
    private DataSaverBackend mDataSaverBackend;
    private Preference mForegroundUsage;
    private Drawable mIcon;
    CharSequence mLabel;
    private PackageManager mPackageManager;
    String mPackageName;
    private RestrictedSwitchPreference mRestrictBackground;
    private long mSelectedCycle;
    NetworkTemplate mTemplate;
    private Preference mTotalUsage;
    private RestrictedSwitchPreference mUnrestrictedData;
    private List<NetworkCycleDataForUid> mUsageData;
    private final ArraySet<String> mPackages = new ArraySet<>();
    private AdapterView.OnItemSelectedListener mCycleListener = new AdapterView.OnItemSelectedListener() { // from class: com.android.settings.datausage.AppDataUsage.1
        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
            AppDataUsage.this.bindData(i);
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };
    final LoaderManager.LoaderCallbacks<List<NetworkCycleDataForUid>> mUidDataCallbacks = new LoaderManager.LoaderCallbacks<List<NetworkCycleDataForUid>>() { // from class: com.android.settings.datausage.AppDataUsage.2
        /* JADX WARN: Type inference failed for: r1v1, types: [androidx.loader.content.Loader<java.util.List<com.android.settingslib.net.NetworkCycleDataForUid>>, com.android.settingslib.net.NetworkCycleDataLoader] */
        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<List<NetworkCycleDataForUid>> onCreateLoader(int i, Bundle bundle) {
            NetworkCycleDataForUidLoader.Builder<?> builder = NetworkCycleDataForUidLoader.builder(AppDataUsage.this.mContext);
            builder.setRetrieveDetail(true).setNetworkTemplate(AppDataUsage.this.mTemplate);
            if (AppDataUsage.this.mAppItem.category == 0) {
                for (int i2 = 0; i2 < AppDataUsage.this.mAppItem.uids.size(); i2++) {
                    builder.addUid(AppDataUsage.this.mAppItem.uids.keyAt(i2));
                }
            } else {
                builder.addUid(AppDataUsage.this.mAppItem.key);
            }
            if (AppDataUsage.this.mCycles != null) {
                builder.setCycles(AppDataUsage.this.mCycles);
            }
            return builder.build();
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoadFinished(Loader<List<NetworkCycleDataForUid>> loader, List<NetworkCycleDataForUid> list) {
            AppDataUsage.this.mUsageData = list;
            AppDataUsage.this.mCycleAdapter.updateCycleList(list);
            int i = 0;
            if (AppDataUsage.this.mSelectedCycle <= 0) {
                AppDataUsage.this.bindData(0);
                return;
            }
            int size = list.size();
            int i2 = 0;
            while (true) {
                if (i2 >= size) {
                    break;
                } else if (list.get(i2).getEndTime() == AppDataUsage.this.mSelectedCycle) {
                    i = i2;
                    break;
                } else {
                    i2++;
                }
            }
            if (i > 0) {
                AppDataUsage.this.mCycle.setSelection(i);
            }
            AppDataUsage.this.bindData(i);
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<List<NetworkCycleDataForUid>> loader) {
        }
    };
    private final LoaderManager.LoaderCallbacks<ArraySet<Preference>> mAppPrefCallbacks = new LoaderManager.LoaderCallbacks<ArraySet<Preference>>() { // from class: com.android.settings.datausage.AppDataUsage.3
        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<ArraySet<Preference>> onCreateLoader(int i, Bundle bundle) {
            return new AppPrefLoader(AppDataUsage.this.getPrefContext(), AppDataUsage.this.mPackages, AppDataUsage.this.getPackageManager());
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoadFinished(Loader<ArraySet<Preference>> loader, ArraySet<Preference> arraySet) {
            if (arraySet == null || AppDataUsage.this.mAppList == null) {
                return;
            }
            Iterator<Preference> it = arraySet.iterator();
            while (it.hasNext()) {
                AppDataUsage.this.mAppList.addPreference(it.next());
            }
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<ArraySet<Preference>> loader) {
        }
    };

    private void addUid(int i) {
        String[] packagesForUid = this.mPackageManager.getPackagesForUid(i);
        if (packagesForUid != null) {
            for (String str : packagesForUid) {
                this.mPackages.add(str);
            }
        }
    }

    private boolean getAppRestrictBackground() {
        return (this.services.mPolicyManager.getUidPolicy(this.mAppItem.key) & 1) != 0;
    }

    private boolean getUnrestrictData() {
        DataSaverBackend dataSaverBackend = this.mDataSaverBackend;
        if (dataSaverBackend != null) {
            return dataSaverBackend.isAllowlisted(this.mAppItem.key);
        }
        return false;
    }

    private void updatePrefs(boolean z, boolean z2) {
        RestrictedLockUtils.EnforcedAdmin checkIfMeteredDataRestricted = RestrictedLockUtilsInternal.checkIfMeteredDataRestricted(this.mContext, this.mPackageName, UserHandle.getUserId(this.mAppItem.key));
        RestrictedSwitchPreference restrictedSwitchPreference = this.mRestrictBackground;
        if (restrictedSwitchPreference != null) {
            restrictedSwitchPreference.setChecked(!z);
            this.mRestrictBackground.setDisabledByAdmin(checkIfMeteredDataRestricted);
        }
        RestrictedSwitchPreference restrictedSwitchPreference2 = this.mUnrestrictedData;
        if (restrictedSwitchPreference2 != null) {
            if (z) {
                restrictedSwitchPreference2.setVisible(false);
                return;
            }
            restrictedSwitchPreference2.setVisible(true);
            this.mUnrestrictedData.setChecked(z2);
            this.mUnrestrictedData.setDisabledByAdmin(checkIfMeteredDataRestricted);
        }
    }

    void bindData(int i) {
        long j;
        long j2;
        List<NetworkCycleDataForUid> list = this.mUsageData;
        if (list == null || i >= list.size()) {
            j = 0;
            this.mCycle.setVisible(false);
            j2 = 0;
        } else {
            this.mCycle.setVisible(true);
            NetworkCycleDataForUid networkCycleDataForUid = this.mUsageData.get(i);
            j = networkCycleDataForUid.getBackgroudUsage();
            j2 = networkCycleDataForUid.getForegroudUsage();
        }
        this.mTotalUsage.setSummary(DataUsageUtils.formatDataUsage(this.mContext, j + j2));
        this.mForegroundUsage.setSummary(DataUsageUtils.formatDataUsage(this.mContext, j2));
        this.mBackgroundUsage.setSummary(DataUsageUtils.formatDataUsage(this.mContext, j));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AppDataUsage";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 343;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.app_data_usage;
    }

    UidDetailProvider getUidDetailProvider() {
        return new UidDetailProvider(this.mContext);
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onAllowlistStatusChanged(int i, boolean z) {
        if (this.mAppItem.uids.get(i, false)) {
            updatePrefs(getAppRestrictBackground(), z);
        }
    }

    @Override // com.android.settings.datausage.DataUsageBaseFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getContext();
        this.mPackageManager = getPackageManager();
        Bundle arguments = getArguments();
        this.mAppItem = arguments != null ? (AppItem) arguments.getParcelable("app_item") : null;
        this.mTemplate = arguments != null ? (NetworkTemplate) arguments.getParcelable("network_template") : null;
        this.mCycles = arguments != null ? (ArrayList) arguments.getSerializable("network_cycles") : null;
        this.mSelectedCycle = arguments != null ? arguments.getLong("selected_cycle") : 0L;
        if (this.mTemplate == null) {
            this.mTemplate = DataUsageUtils.getDefaultTemplate(this.mContext, SubscriptionManager.getDefaultDataSubscriptionId());
        }
        boolean z = false;
        if (this.mAppItem == null) {
            int i = arguments != null ? arguments.getInt("uid", -1) : getActivity().getIntent().getIntExtra("uid", -1);
            if (i == -1) {
                getActivity().finish();
            } else {
                addUid(i);
                AppItem appItem = new AppItem(i);
                this.mAppItem = appItem;
                appItem.addUid(i);
            }
        } else {
            for (int i2 = 0; i2 < this.mAppItem.uids.size(); i2++) {
                addUid(this.mAppItem.uids.keyAt(i2));
            }
        }
        this.mTotalUsage = findPreference("total_usage");
        this.mForegroundUsage = findPreference("foreground_usage");
        this.mBackgroundUsage = findPreference("background_usage");
        this.mCycle = (SpinnerPreference) findPreference("cycle");
        this.mCycleAdapter = new CycleAdapter(this.mContext, this.mCycle, this.mCycleListener);
        UidDetailProvider uidDetailProvider = getUidDetailProvider();
        int i3 = this.mAppItem.key;
        if (i3 <= 0) {
            FragmentActivity activity = getActivity();
            UidDetail uidDetail = uidDetailProvider.getUidDetail(this.mAppItem.key, true);
            this.mIcon = uidDetail.icon;
            this.mLabel = uidDetail.label;
            this.mPackageName = activity.getPackageName();
            removePreference("unrestricted_data_saver");
            removePreference("app_settings");
            removePreference("restrict_background");
            removePreference("app_list");
            return;
        }
        if (UserHandle.isApp(i3)) {
            if (this.mPackages.size() != 0) {
                try {
                    ApplicationInfo applicationInfoAsUser = this.mPackageManager.getApplicationInfoAsUser(this.mPackages.valueAt(0), 0, UserHandle.getUserId(this.mAppItem.key));
                    this.mIcon = IconDrawableFactory.newInstance(getActivity()).getBadgedIcon(applicationInfoAsUser);
                    this.mLabel = applicationInfoAsUser.loadLabel(this.mPackageManager);
                    this.mPackageName = applicationInfoAsUser.packageName;
                } catch (PackageManager.NameNotFoundException unused) {
                }
            }
            RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) findPreference("restrict_background");
            this.mRestrictBackground = restrictedSwitchPreference;
            restrictedSwitchPreference.setOnPreferenceChangeListener(this);
            RestrictedSwitchPreference restrictedSwitchPreference2 = (RestrictedSwitchPreference) findPreference("unrestricted_data_saver");
            this.mUnrestrictedData = restrictedSwitchPreference2;
            restrictedSwitchPreference2.setOnPreferenceChangeListener(this);
        } else {
            UidDetail uidDetail2 = uidDetailProvider.getUidDetail(this.mAppItem.key, true);
            this.mIcon = uidDetail2.icon;
            this.mLabel = uidDetail2.label;
            removePreference("unrestricted_data_saver");
            removePreference("restrict_background");
        }
        this.mDataSaverBackend = new DataSaverBackend(this.mContext);
        this.mAppSettings = findPreference("app_settings");
        Intent intent = new Intent("android.intent.action.MANAGE_NETWORK_USAGE");
        this.mAppSettingsIntent = intent;
        intent.addCategory("android.intent.category.DEFAULT");
        PackageManager packageManager = getPackageManager();
        Iterator<String> it = this.mPackages.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            this.mAppSettingsIntent.setPackage(it.next());
            if (packageManager.resolveActivity(this.mAppSettingsIntent, 0) != null) {
                z = true;
                break;
            }
        }
        if (!z) {
            removePreference("app_settings");
            this.mAppSettings = null;
        }
        if (this.mPackages.size() <= 1) {
            removePreference("app_list");
            return;
        }
        this.mAppList = (PreferenceCategory) findPreference("app_list");
        getLoaderManager().restartLoader(3, Bundle.EMPTY, this.mAppPrefCallbacks);
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDataSaverChanged(boolean z) {
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDenylistStatusChanged(int i, boolean z) {
        if (this.mAppItem.uids.get(i, false)) {
            updatePrefs(z, getUnrestrictData());
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        DataSaverBackend dataSaverBackend = this.mDataSaverBackend;
        if (dataSaverBackend != null) {
            dataSaverBackend.remListener(this);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mRestrictBackground) {
            this.mDataSaverBackend.setIsDenylisted(this.mAppItem.key, this.mPackageName, !((Boolean) obj).booleanValue());
            updatePrefs();
            return true;
        } else if (preference == this.mUnrestrictedData) {
            this.mDataSaverBackend.setIsAllowlisted(this.mAppItem.key, this.mPackageName, ((Boolean) obj).booleanValue());
            return true;
        } else {
            return false;
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == this.mAppSettings) {
            getActivity().startActivityAsUser(this.mAppSettingsIntent, new UserHandle(UserHandle.getUserId(this.mAppItem.key)));
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.datausage.DataUsageBaseFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        DataSaverBackend dataSaverBackend = this.mDataSaverBackend;
        if (dataSaverBackend != null) {
            dataSaverBackend.addListener(this);
        }
        getLoaderManager().restartLoader(2, null, this.mUidDataCallbacks);
        updatePrefs();
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0045  */
    /* JADX WARN: Removed duplicated region for block: B:14:0x0047  */
    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onViewCreated(android.view.View r5, android.os.Bundle r6) {
        /*
            r4 = this;
            super.onViewCreated(r5, r6)
            android.util.ArraySet<java.lang.String> r5 = r4.mPackages
            int r5 = r5.size()
            r6 = 0
            r0 = 0
            if (r5 == 0) goto L16
            android.util.ArraySet<java.lang.String> r5 = r4.mPackages
            java.lang.Object r5 = r5.valueAt(r0)
            java.lang.String r5 = (java.lang.String) r5
            goto L17
        L16:
            r5 = r6
        L17:
            if (r5 == 0) goto L3e
            android.content.pm.PackageManager r1 = r4.mPackageManager     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L28
            com.android.settingslib.AppItem r2 = r4.mAppItem     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L28
            int r2 = r2.key     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L28
            int r2 = android.os.UserHandle.getUserId(r2)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L28
            int r1 = r1.getPackageUidAsUser(r5, r2)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L28
            goto L3f
        L28:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Skipping UID because cannot find package "
            r1.append(r2)
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "AppDataUsage"
            android.util.Log.w(r2, r1)
        L3e:
            r1 = r0
        L3f:
            com.android.settingslib.AppItem r2 = r4.mAppItem
            int r2 = r2.key
            if (r2 <= 0) goto L47
            r2 = 1
            goto L48
        L47:
            r2 = r0
        L48:
            androidx.fragment.app.FragmentActivity r3 = r4.getActivity()
            com.android.settings.widget.EntityHeaderController r6 = com.android.settings.widget.EntityHeaderController.newInstance(r3, r4, r6)
            com.android.settings.widget.EntityHeaderController r6 = r6.setUid(r1)
            com.android.settings.widget.EntityHeaderController r6 = r6.setHasAppInfoLink(r2)
            com.android.settings.widget.EntityHeaderController r6 = r6.setButtonActions(r0, r0)
            android.graphics.drawable.Drawable r0 = r4.mIcon
            com.android.settings.widget.EntityHeaderController r6 = r6.setIcon(r0)
            java.lang.CharSequence r0 = r4.mLabel
            com.android.settings.widget.EntityHeaderController r6 = r6.setLabel(r0)
            com.android.settings.widget.EntityHeaderController r5 = r6.setPackageName(r5)
            android.content.Context r6 = r4.getPrefContext()
            com.android.settingslib.widget.LayoutPreference r5 = r5.done(r3, r6)
            androidx.preference.PreferenceScreen r4 = r4.getPreferenceScreen()
            r4.addPreference(r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.datausage.AppDataUsage.onViewCreated(android.view.View, android.os.Bundle):void");
    }

    void updatePrefs() {
        updatePrefs(getAppRestrictBackground(), getUnrestrictData());
    }
}
