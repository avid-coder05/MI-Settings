package com.android.settings.applications;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.util.Log;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/* loaded from: classes.dex */
public class PermissionInfoFragment extends SettingsPreferenceFragment {
    private ApplicationInfo mAppInfo;
    private PreferenceCategory mOtherCategory;
    private PermissionSet mPermSet;
    private PreferenceCategory mPrivacyCategory;
    private PreferenceCategory mSecurityCategory;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class PermissionSet {
        private Map<String, String> nPermPrivacy = new TreeMap();
        private Map<String, String> nPermSecurity = new TreeMap();
        private Map<String, String> nPermOther = new TreeMap();

        PermissionSet() {
        }

        public int getOtherCount() {
            return this.nPermOther.size();
        }

        public int getPrivacyCount() {
            return this.nPermPrivacy.size();
        }

        public int getSecurityCount() {
            return this.nPermSecurity.size();
        }
    }

    private void buildPreferences(Map<String, String> map, PreferenceCategory preferenceCategory) {
        if (map.size() <= 0) {
            getPreferenceScreen().removePreference(preferenceCategory);
            return;
        }
        for (String str : map.keySet()) {
            PreferenceScreen inflateFromResource = getPreferenceManager().inflateFromResource(getActivity(), R.layout.information_preference, null);
            inflateFromResource.setTitle(str);
            inflateFromResource.setSummary(map.get(str));
            preferenceCategory.addPreference(inflateFromResource);
        }
    }

    private static void extractPerms(String[] strArr, Set<PermissionInfo> set, PackageManager packageManager) {
        if (strArr == null || strArr.length == 0) {
            return;
        }
        for (String str : strArr) {
            try {
                PermissionInfo permissionInfo = packageManager.getPermissionInfo(str, 0);
                if (permissionInfo != null) {
                    set.add(permissionInfo);
                }
            } catch (PackageManager.NameNotFoundException unused) {
                Log.i("PermissionInfoActivity", "Ignoring unknown permission:" + str);
            }
        }
    }

    private static void getAllUsedPermissions(int i, Set<PermissionInfo> set, PackageManager packageManager) {
        String[] packagesForUid = packageManager.getPackagesForUid(i);
        if (packagesForUid == null || packagesForUid.length == 0) {
            return;
        }
        for (String str : packagesForUid) {
            getPermissionsForPackage(str, set, packageManager);
        }
    }

    private static void getPermissionsForPackage(String str, Set<PermissionInfo> set, PackageManager packageManager) {
        String[] strArr;
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(str, 4096);
            if (packageInfo == null || (strArr = packageInfo.requestedPermissions) == null) {
                return;
            }
            extractPerms(strArr, set, packageManager);
        } catch (PackageManager.NameNotFoundException unused) {
            Log.w("PermissionInfoActivity", "Could'nt retrieve permissions for package:" + str);
        }
    }

    public static PermissionSet parsePermission(int i, Context context) {
        PermissionSet permissionSet = new PermissionSet();
        if (i > -1) {
            List asList = Arrays.asList(context.getResources().getStringArray(R.array.permission_risk_privacy));
            List asList2 = Arrays.asList(context.getResources().getStringArray(R.array.permission_risk_security));
            HashSet<PermissionInfo> hashSet = new HashSet();
            PackageManager packageManager = context.getPackageManager();
            getAllUsedPermissions(i, hashSet, packageManager);
            for (PermissionInfo permissionInfo : hashSet) {
                CharSequence loadLabel = permissionInfo.loadLabel(packageManager);
                if (loadLabel != null) {
                    String charSequence = loadLabel.toString();
                    CharSequence loadDescription = permissionInfo.loadDescription(packageManager);
                    if (loadDescription != null) {
                        String charSequence2 = loadDescription.toString();
                        if (asList.contains(permissionInfo.name)) {
                            permissionSet.nPermPrivacy.put(charSequence, charSequence2);
                        } else if (asList2.contains(permissionInfo.name)) {
                            permissionSet.nPermSecurity.put(charSequence, charSequence2);
                        } else {
                            permissionSet.nPermOther.put(charSequence, charSequence2);
                        }
                    }
                }
            }
        }
        return permissionSet;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return PermissionInfoFragment.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.permission_info);
        ApplicationInfo applicationInfo = (ApplicationInfo) getArguments().getParcelable("extra_package_application");
        this.mAppInfo = applicationInfo;
        if (applicationInfo == null) {
            Log.e("PermissionInfoActivity", "onCreate: mAppInfo is null");
            finish();
            return;
        }
        this.mPermSet = parsePermission(applicationInfo.uid, getActivity());
        this.mPrivacyCategory = (PreferenceCategory) findPreference("privacy_relative");
        this.mSecurityCategory = (PreferenceCategory) findPreference("security_relative");
        this.mOtherCategory = (PreferenceCategory) findPreference("other_relative");
        buildPreferences(this.mPermSet.nPermPrivacy, this.mPrivacyCategory);
        buildPreferences(this.mPermSet.nPermSecurity, this.mSecurityCategory);
        buildPreferences(this.mPermSet.nPermOther, this.mOtherCategory);
    }
}
