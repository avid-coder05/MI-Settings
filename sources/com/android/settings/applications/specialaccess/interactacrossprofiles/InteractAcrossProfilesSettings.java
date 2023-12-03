package com.android.settings.applications.specialaccess.interactacrossprofiles;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.CrossProfileApps;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.IconDrawableFactory;
import android.util.Pair;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.applications.AppInfoBase;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.EmptyTextSettings;
import com.android.settingslib.widget.AppPreference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/* loaded from: classes.dex */
public class InteractAcrossProfilesSettings extends EmptyTextSettings {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.interact_across_profiles);
    private Context mContext;
    private CrossProfileApps mCrossProfileApps;
    private IconDrawableFactory mIconDrawableFactory;
    private PackageManager mPackageManager;
    private UserManager mUserManager;

    static ArrayList<Pair<ApplicationInfo, UserHandle>> collectConfigurableApps(PackageManager packageManager, UserManager userManager, CrossProfileApps crossProfileApps) {
        UserHandle profileParent;
        UserHandle workProfile = getWorkProfile(userManager);
        if (workProfile != null && (profileParent = userManager.getProfileParent(workProfile)) != null) {
            ArrayList<Pair<ApplicationInfo, UserHandle>> arrayList = new ArrayList<>();
            for (PackageInfo packageInfo : getAllInstalledPackages(packageManager, profileParent, workProfile)) {
                if (crossProfileApps.canUserAttemptToConfigureInteractAcrossProfiles(packageInfo.packageName)) {
                    arrayList.add(new Pair<>(packageInfo.applicationInfo, profileParent));
                }
            }
            return arrayList;
        }
        return new ArrayList<>();
    }

    private static List<PackageInfo> getAllInstalledPackages(PackageManager packageManager, UserHandle userHandle, UserHandle userHandle2) {
        List installedPackagesAsUser = packageManager.getInstalledPackagesAsUser(1, userHandle.getIdentifier());
        List<PackageInfo> installedPackagesAsUser2 = packageManager.getInstalledPackagesAsUser(1, userHandle2.getIdentifier());
        ArrayList arrayList = new ArrayList(installedPackagesAsUser);
        for (final PackageInfo packageInfo : installedPackagesAsUser2) {
            if (arrayList.stream().noneMatch(new Predicate() { // from class: com.android.settings.applications.specialaccess.interactacrossprofiles.InteractAcrossProfilesSettings$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$getAllInstalledPackages$0;
                    lambda$getAllInstalledPackages$0 = InteractAcrossProfilesSettings.lambda$getAllInstalledPackages$0(packageInfo, (PackageInfo) obj);
                    return lambda$getAllInstalledPackages$0;
                }
            })) {
                arrayList.add(packageInfo);
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getNumberOfEnabledApps(final Context context, PackageManager packageManager, UserManager userManager, final CrossProfileApps crossProfileApps) {
        UserHandle workProfile = getWorkProfile(userManager);
        if (workProfile == null || userManager.getProfileParent(workProfile) == null) {
            return 0;
        }
        ArrayList<Pair<ApplicationInfo, UserHandle>> collectConfigurableApps = collectConfigurableApps(packageManager, userManager, crossProfileApps);
        collectConfigurableApps.removeIf(new Predicate() { // from class: com.android.settings.applications.specialaccess.interactacrossprofiles.InteractAcrossProfilesSettings$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getNumberOfEnabledApps$1;
                lambda$getNumberOfEnabledApps$1 = InteractAcrossProfilesSettings.lambda$getNumberOfEnabledApps$1(context, crossProfileApps, (Pair) obj);
                return lambda$getNumberOfEnabledApps$1;
            }
        });
        return collectConfigurableApps.size();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static UserHandle getWorkProfile(UserManager userManager) {
        for (UserInfo userInfo : userManager.getProfiles(UserHandle.myUserId())) {
            if (userManager.isManagedProfile(userInfo.id)) {
                return userInfo.getUserHandle();
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getAllInstalledPackages$0(PackageInfo packageInfo, PackageInfo packageInfo2) {
        return packageInfo.packageName.equals(packageInfo2.packageName);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getNumberOfEnabledApps$1(Context context, CrossProfileApps crossProfileApps, Pair pair) {
        return (InteractAcrossProfilesDetails.isInteractAcrossProfilesEnabled(context, ((ApplicationInfo) pair.first).packageName) && crossProfileApps.canConfigureInteractAcrossProfiles(((ApplicationInfo) pair.first).packageName)) ? false : true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1829;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.interact_across_profiles;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Context context = getContext();
        this.mContext = context;
        this.mPackageManager = context.getPackageManager();
        this.mUserManager = (UserManager) this.mContext.getSystemService(UserManager.class);
        this.mIconDrawableFactory = IconDrawableFactory.newInstance(this.mContext);
        this.mCrossProfileApps = (CrossProfileApps) this.mContext.getSystemService(CrossProfileApps.class);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        ArrayList<Pair<ApplicationInfo, UserHandle>> collectConfigurableApps = collectConfigurableApps(this.mPackageManager, this.mUserManager, this.mCrossProfileApps);
        Context prefContext = getPrefContext();
        Iterator<Pair<ApplicationInfo, UserHandle>> it = collectConfigurableApps.iterator();
        while (it.hasNext()) {
            Pair<ApplicationInfo, UserHandle> next = it.next();
            final ApplicationInfo applicationInfo = (ApplicationInfo) next.first;
            UserHandle userHandle = (UserHandle) next.second;
            final String str = applicationInfo.packageName;
            CharSequence loadLabel = applicationInfo.loadLabel(this.mPackageManager);
            AppPreference appPreference = new AppPreference(prefContext);
            appPreference.setIcon(this.mIconDrawableFactory.getBadgedIcon(applicationInfo, userHandle.getIdentifier()));
            appPreference.setTitle(this.mPackageManager.getUserBadgedLabel(loadLabel, userHandle));
            appPreference.setSummary(InteractAcrossProfilesDetails.getPreferenceSummary(prefContext, str));
            appPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.applications.specialaccess.interactacrossprofiles.InteractAcrossProfilesSettings.1
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference) {
                    int i = R.string.interact_across_profiles_title;
                    String str2 = str;
                    int i2 = applicationInfo.uid;
                    InteractAcrossProfilesSettings interactAcrossProfilesSettings = InteractAcrossProfilesSettings.this;
                    AppInfoBase.startAppInfoFragment(InteractAcrossProfilesDetails.class, i, str2, i2, interactAcrossProfilesSettings, -1, interactAcrossProfilesSettings.getMetricsCategory());
                    return true;
                }
            });
            preferenceScreen.addPreference(appPreference);
        }
    }

    @Override // com.android.settings.widget.EmptyTextSettings, com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        setEmptyText(R.string.interact_across_profiles_empty_text);
    }
}
