package com.android.settings.users;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import com.android.settings.Utils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;

/* loaded from: classes2.dex */
public class UserCapabilities {
    boolean mCanAddGuest;
    boolean mDisallowAddUser;
    boolean mDisallowAddUserSetByAdmin;
    boolean mDisallowSwitchUser;
    RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
    boolean mIsAdmin;
    boolean mIsGuest;
    boolean mUserSwitcherEnabled;
    boolean mEnabled = true;
    boolean mCanAddUser = true;
    boolean mCanAddRestrictedProfile = true;

    private UserCapabilities() {
    }

    public static UserCapabilities create(Context context) {
        UserManager userManager = (UserManager) context.getSystemService("user");
        UserCapabilities userCapabilities = new UserCapabilities();
        if (!UserManager.supportsMultipleUsers() || Utils.isMonkeyRunning()) {
            userCapabilities.mEnabled = false;
            return userCapabilities;
        }
        UserInfo userInfo = userManager.getUserInfo(UserHandle.myUserId());
        userCapabilities.mIsGuest = userInfo.isGuest();
        userCapabilities.mIsAdmin = userInfo.isAdmin();
        if (((DevicePolicyManager) context.getSystemService("device_policy")).isDeviceManaged() || Utils.isVoiceCapable(context)) {
            userCapabilities.mCanAddRestrictedProfile = false;
        }
        userCapabilities.updateAddUserCapabilities(context);
        return userCapabilities;
    }

    public boolean disallowAddUser() {
        return this.mDisallowAddUser;
    }

    public boolean disallowAddUserSetByAdmin() {
        return this.mDisallowAddUserSetByAdmin;
    }

    public RestrictedLockUtils.EnforcedAdmin getEnforcedAdmin() {
        return this.mEnforcedAdmin;
    }

    public boolean isAdmin() {
        return this.mIsAdmin;
    }

    public String toString() {
        return "UserCapabilities{mEnabled=" + this.mEnabled + ", mCanAddUser=" + this.mCanAddUser + ", mCanAddRestrictedProfile=" + this.mCanAddRestrictedProfile + ", mIsAdmin=" + this.mIsAdmin + ", mIsGuest=" + this.mIsGuest + ", mCanAddGuest=" + this.mCanAddGuest + ", mDisallowAddUser=" + this.mDisallowAddUser + ", mEnforcedAdmin=" + this.mEnforcedAdmin + ", mDisallowSwitchUser=" + this.mDisallowSwitchUser + ", mDisallowAddUserSetByAdmin=" + this.mDisallowAddUserSetByAdmin + ", mUserSwitcherEnabled=" + this.mUserSwitcherEnabled + '}';
    }

    public void updateAddUserCapabilities(Context context) {
        UserManager userManager = (UserManager) context.getSystemService("user");
        this.mEnforcedAdmin = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(context, "no_add_user", UserHandle.myUserId());
        boolean hasBaseUserRestriction = RestrictedLockUtilsInternal.hasBaseUserRestriction(context, "no_add_user", UserHandle.myUserId());
        RestrictedLockUtils.EnforcedAdmin enforcedAdmin = this.mEnforcedAdmin;
        this.mDisallowAddUserSetByAdmin = (enforcedAdmin == null || hasBaseUserRestriction) ? false : true;
        this.mDisallowAddUser = enforcedAdmin != null || hasBaseUserRestriction;
        this.mUserSwitcherEnabled = userManager.isUserSwitcherEnabled();
        this.mCanAddUser = true;
        if (!this.mIsAdmin || UserManager.getMaxSupportedUsers() < 2 || !UserManager.supportsMultipleUsers() || this.mDisallowAddUser) {
            this.mCanAddUser = false;
        }
        this.mCanAddGuest = (this.mIsGuest || this.mDisallowAddUser || !(this.mIsAdmin || Settings.Global.getInt(context.getContentResolver(), "add_users_when_locked", 0) == 1)) ? false : true;
        this.mDisallowSwitchUser = userManager.hasUserRestriction("no_user_switch");
    }
}
