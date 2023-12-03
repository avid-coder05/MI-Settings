package com.android.settings.datausage;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.util.IconDrawableFactory;
import android.view.View;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settings.applications.appinfo.AppInfoDashboardFragment;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.datausage.AppStateDataUsageBridge;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreferenceHelper;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.widget.AppSwitchPreference;

/* loaded from: classes.dex */
public class UnrestrictedDataAccessPreference extends AppSwitchPreference implements DataSaverBackend.Listener {
    private final ApplicationsState mApplicationsState;
    private final DataSaverBackend mDataSaverBackend;
    private final AppStateDataUsageBridge.DataUsageState mDataUsageState;
    private final ApplicationsState.AppEntry mEntry;
    private final RestrictedPreferenceHelper mHelper;
    private final IconDrawableFactory mIconDrawableFactory;
    private final DashboardFragment mParentFragment;

    public UnrestrictedDataAccessPreference(Context context, ApplicationsState.AppEntry appEntry, ApplicationsState applicationsState, DataSaverBackend dataSaverBackend, DashboardFragment dashboardFragment) {
        super(context);
        ApplicationInfo applicationInfo;
        setWidgetLayoutResource(R.layout.restricted_switch_widget);
        this.mHelper = new RestrictedPreferenceHelper(context, this, null);
        this.mEntry = appEntry;
        Object obj = appEntry.extraInfo;
        if (obj == null || !(obj instanceof AppStateDataUsageBridge.DataUsageState)) {
            this.mDataUsageState = null;
        } else {
            this.mDataUsageState = (AppStateDataUsageBridge.DataUsageState) obj;
        }
        appEntry.ensureLabel(context);
        this.mApplicationsState = applicationsState;
        applicationsState.ensureIcon(appEntry);
        this.mDataSaverBackend = dataSaverBackend;
        this.mParentFragment = dashboardFragment;
        ApplicationInfo applicationInfo2 = appEntry.info;
        setDisabledByAdmin(RestrictedLockUtilsInternal.checkIfMeteredDataRestricted(context, applicationInfo2.packageName, UserHandle.getUserId(applicationInfo2.uid)));
        updateState();
        setKey(generateKey(appEntry));
        IconDrawableFactory newInstance = IconDrawableFactory.newInstance(context);
        this.mIconDrawableFactory = newInstance;
        Drawable drawable = appEntry.icon;
        if (drawable != null) {
            if (newInstance != null && (applicationInfo = appEntry.info) != null) {
                drawable = newInstance.getBadgedIcon(applicationInfo);
            }
            setIcon(drawable);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String generateKey(ApplicationsState.AppEntry appEntry) {
        return appEntry.info.packageName + "|" + appEntry.info.uid;
    }

    public AppStateDataUsageBridge.DataUsageState getDataUsageState() {
        return this.mDataUsageState;
    }

    public ApplicationsState.AppEntry getEntry() {
        return this.mEntry;
    }

    public boolean isDisabledByAdmin() {
        return this.mHelper.isDisabledByAdmin();
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onAllowlistStatusChanged(int i, boolean z) {
        AppStateDataUsageBridge.DataUsageState dataUsageState = this.mDataUsageState;
        if (dataUsageState == null || this.mEntry.info.uid != i) {
            return;
        }
        dataUsageState.isDataSaverAllowlisted = z;
        updateState();
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        this.mDataSaverBackend.addListener(this);
    }

    @Override // com.android.settingslib.widget.AppSwitchPreference, androidx.preference.SwitchPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        if (this.mEntry.icon == null) {
            preferenceViewHolder.itemView.post(new Runnable() { // from class: com.android.settings.datausage.UnrestrictedDataAccessPreference.1
                @Override // java.lang.Runnable
                public void run() {
                    UnrestrictedDataAccessPreference.this.mApplicationsState.ensureIcon(UnrestrictedDataAccessPreference.this.mEntry);
                    UnrestrictedDataAccessPreference unrestrictedDataAccessPreference = UnrestrictedDataAccessPreference.this;
                    unrestrictedDataAccessPreference.setIcon((unrestrictedDataAccessPreference.mIconDrawableFactory == null || UnrestrictedDataAccessPreference.this.mEntry.info == null) ? UnrestrictedDataAccessPreference.this.mEntry.icon : UnrestrictedDataAccessPreference.this.mIconDrawableFactory.getBadgedIcon(UnrestrictedDataAccessPreference.this.mEntry.info));
                }
            });
        }
        boolean isDisabledByAdmin = isDisabledByAdmin();
        View findViewById = preferenceViewHolder.findViewById(16908312);
        if (isDisabledByAdmin) {
            findViewById.setVisibility(0);
        } else {
            AppStateDataUsageBridge.DataUsageState dataUsageState = this.mDataUsageState;
            findViewById.setVisibility((dataUsageState == null || !dataUsageState.isDataSaverDenylisted) ? 0 : 4);
        }
        super.onBindViewHolder(preferenceViewHolder);
        this.mHelper.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.findViewById(R.id.restricted_icon).setVisibility(isDisabledByAdmin ? 0 : 8);
        preferenceViewHolder.findViewById(16908352).setVisibility(isDisabledByAdmin ? 8 : 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.TwoStatePreference, androidx.preference.Preference
    public void onClick() {
        AppStateDataUsageBridge.DataUsageState dataUsageState = this.mDataUsageState;
        if (dataUsageState == null || !dataUsageState.isDataSaverDenylisted) {
            super.onClick();
        } else {
            AppInfoDashboardFragment.startAppInfoFragment(AppDataUsage.class, R.string.data_usage_app_summary_title, null, this.mParentFragment, this.mEntry);
        }
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDataSaverChanged(boolean z) {
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDenylistStatusChanged(int i, boolean z) {
        AppStateDataUsageBridge.DataUsageState dataUsageState = this.mDataUsageState;
        if (dataUsageState == null || this.mEntry.info.uid != i) {
            return;
        }
        dataUsageState.isDataSaverDenylisted = z;
        updateState();
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
        this.mDataSaverBackend.remListener(this);
        super.onDetached();
    }

    @Override // androidx.preference.Preference
    public void performClick() {
        if (this.mHelper.performClick()) {
            return;
        }
        super.performClick();
    }

    public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin enforcedAdmin) {
        this.mHelper.setDisabledByAdmin(enforcedAdmin);
    }

    public void updateState() {
        setTitle(this.mEntry.label);
        AppStateDataUsageBridge.DataUsageState dataUsageState = this.mDataUsageState;
        if (dataUsageState != null) {
            setChecked(dataUsageState.isDataSaverAllowlisted);
            if (isDisabledByAdmin()) {
                setSummary(R.string.disabled_by_admin);
            } else if (this.mDataUsageState.isDataSaverDenylisted) {
                setSummary(R.string.restrict_background_blocklisted);
            } else {
                setSummary("");
            }
        }
        notifyChanged();
    }
}
