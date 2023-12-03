package com.android.settings.accounts;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;
import com.android.settings.AccessiblePreferenceCategory;
import com.android.settings.R;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;
import java.util.ArrayList;
import java.util.Arrays;

/* loaded from: classes.dex */
public class AccountRestrictionHelper {
    private final Context mContext;

    public AccountRestrictionHelper(Context context) {
        this.mContext = context;
    }

    private RestrictedLockUtils.EnforcedAdmin getEnforcedAdmin(String str, int i) {
        int managedUserId;
        ComponentName profileOwnerAsUser;
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) this.mContext.getSystemService("device_policy");
        if (devicePolicyManager == null || (profileOwnerAsUser = devicePolicyManager.getProfileOwnerAsUser((managedUserId = getManagedUserId(i)))) == null) {
            return null;
        }
        return new RestrictedLockUtils.EnforcedAdmin(profileOwnerAsUser, str, UserHandle.of(managedUserId));
    }

    private int getManagedUserId(int i) {
        for (UserInfo userInfo : UserManager.get(this.mContext).getProfiles(i)) {
            if (userInfo.id != i && userInfo.isManagedProfile()) {
                return userInfo.id;
            }
        }
        return -1;
    }

    public static boolean hideAccount(Context context, String str) {
        String[] stringArray = context.getResources().getStringArray(R.array.hide_account_list);
        if (stringArray == null || stringArray.length == 0) {
            return false;
        }
        return Arrays.asList(stringArray).contains(str);
    }

    private boolean isOrganizationOwnedDevice() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) this.mContext.getSystemService("device_policy");
        if (devicePolicyManager == null) {
            return false;
        }
        return devicePolicyManager.isOrganizationOwnedDeviceWithManagedProfile();
    }

    public static boolean showAccount(String[] strArr, ArrayList<String> arrayList) {
        if (strArr == null || arrayList == null) {
            return true;
        }
        for (String str : strArr) {
            if (arrayList.contains(str)) {
                return true;
            }
        }
        return false;
    }

    public AccessiblePreferenceCategory createAccessiblePreferenceCategory(Context context) {
        return new AccessiblePreferenceCategory(context);
    }

    public void enforceRestrictionOnPreference(RestrictedPreference restrictedPreference, String str, int i) {
        if (restrictedPreference == null) {
            return;
        }
        if (!hasBaseUserRestriction(str, i)) {
            restrictedPreference.checkRestrictionAndSetDisabled(str, i);
        } else if (str.equals("no_remove_managed_profile") && isOrganizationOwnedDevice()) {
            restrictedPreference.setDisabledByAdmin(getEnforcedAdmin(str, i));
        } else {
            restrictedPreference.setEnabled(false);
        }
    }

    public boolean hasBaseUserRestriction(String str, int i) {
        return RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mContext, str, i);
    }
}
