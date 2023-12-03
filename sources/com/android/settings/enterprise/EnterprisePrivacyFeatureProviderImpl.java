package com.android.settings.enterprise;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.VpnManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import com.android.settings.R;
import com.android.settings.vpn2.VpnUtils;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class EnterprisePrivacyFeatureProviderImpl implements EnterprisePrivacyFeatureProvider {
    private static final int MY_USER_ID = UserHandle.myUserId();
    private final ConnectivityManager mCm;
    private final Context mContext;
    private final DevicePolicyManager mDpm;
    private final PackageManager mPm;
    private final Resources mResources;
    private final UserManager mUm;
    private final VpnManager mVm;

    public EnterprisePrivacyFeatureProviderImpl(Context context, DevicePolicyManager devicePolicyManager, PackageManager packageManager, UserManager userManager, ConnectivityManager connectivityManager, VpnManager vpnManager, Resources resources) {
        this.mContext = context.getApplicationContext();
        this.mDpm = devicePolicyManager;
        this.mPm = packageManager;
        this.mUm = userManager;
        this.mCm = connectivityManager;
        this.mVm = vpnManager;
        this.mResources = resources;
    }

    private ComponentName getDeviceOwnerComponent() {
        if (this.mPm.hasSystemFeature("android.software.device_admin")) {
            return this.mDpm.getDeviceOwnerComponentOnAnyUser();
        }
        return null;
    }

    private int getManagedProfileUserId() {
        UserInfo managedProfileUserInfo = getManagedProfileUserInfo();
        if (managedProfileUserInfo != null) {
            return managedProfileUserInfo.id;
        }
        return -10000;
    }

    private UserInfo getManagedProfileUserInfo() {
        for (UserInfo userInfo : this.mUm.getProfiles(MY_USER_ID)) {
            if (userInfo.isManagedProfile()) {
                return userInfo;
            }
        }
        return null;
    }

    private Intent getParentalControlsIntent() {
        DevicePolicyManager devicePolicyManager = this.mDpm;
        int i = MY_USER_ID;
        ComponentName profileOwnerOrDeviceOwnerSupervisionComponent = devicePolicyManager.getProfileOwnerOrDeviceOwnerSupervisionComponent(new UserHandle(i));
        if (profileOwnerOrDeviceOwnerSupervisionComponent == null) {
            return null;
        }
        Intent addFlags = new Intent("android.settings.SHOW_PARENTAL_CONTROLS").setPackage(profileOwnerOrDeviceOwnerSupervisionComponent.getPackageName()).addFlags(268435456);
        if (this.mPm.queryIntentActivitiesAsUser(addFlags, 0, i).size() != 0) {
            return addFlags;
        }
        return null;
    }

    private Intent getWorkPolicyInfoIntentDO() {
        ComponentName deviceOwnerComponent = getDeviceOwnerComponent();
        if (deviceOwnerComponent == null) {
            return null;
        }
        Intent addFlags = new Intent("android.settings.SHOW_WORK_POLICY_INFO").setPackage(deviceOwnerComponent.getPackageName()).addFlags(268435456);
        if (this.mPm.queryIntentActivities(addFlags, 0).size() != 0) {
            return addFlags;
        }
        return null;
    }

    private Intent getWorkPolicyInfoIntentPO() {
        ComponentName profileOwnerAsUser;
        int managedProfileUserId = getManagedProfileUserId();
        if (managedProfileUserId == -10000 || (profileOwnerAsUser = this.mDpm.getProfileOwnerAsUser(managedProfileUserId)) == null) {
            return null;
        }
        Intent addFlags = new Intent("android.settings.SHOW_WORK_POLICY_INFO").setPackage(profileOwnerAsUser.getPackageName()).addFlags(268435456);
        if (this.mPm.queryIntentActivitiesAsUser(addFlags, 0, managedProfileUserId).size() != 0) {
            return addFlags;
        }
        return null;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public CharSequence getDeviceOwnerDisclosure() {
        if (hasDeviceOwner()) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            CharSequence deviceOwnerOrganizationName = this.mDpm.getDeviceOwnerOrganizationName();
            if (deviceOwnerOrganizationName != null) {
                spannableStringBuilder.append((CharSequence) this.mResources.getString(R.string.do_disclosure_with_name, deviceOwnerOrganizationName));
            } else {
                spannableStringBuilder.append((CharSequence) this.mResources.getString(R.string.do_disclosure_generic));
            }
            return spannableStringBuilder;
        }
        return null;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public String getDeviceOwnerOrganizationName() {
        CharSequence deviceOwnerOrganizationName = this.mDpm.getDeviceOwnerOrganizationName();
        if (deviceOwnerOrganizationName == null) {
            return null;
        }
        return deviceOwnerOrganizationName.toString();
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public String getImeLabelIfOwnerSet() {
        if (this.mDpm.isCurrentInputMethodSetByOwner()) {
            ContentResolver contentResolver = this.mContext.getContentResolver();
            int i = MY_USER_ID;
            String stringForUser = Settings.Secure.getStringForUser(contentResolver, "default_input_method", i);
            if (stringForUser == null) {
                return null;
            }
            try {
                return this.mPm.getApplicationInfoAsUser(stringForUser, 0, i).loadLabel(this.mPm).toString();
            } catch (PackageManager.NameNotFoundException unused) {
                return null;
            }
        }
        return null;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public Date getLastBugReportRequestTime() {
        long lastBugReportRequestTime = this.mDpm.getLastBugReportRequestTime();
        if (lastBugReportRequestTime < 0) {
            return null;
        }
        return new Date(lastBugReportRequestTime);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public Date getLastNetworkLogRetrievalTime() {
        long lastNetworkLogRetrievalTime = this.mDpm.getLastNetworkLogRetrievalTime();
        if (lastNetworkLogRetrievalTime < 0) {
            return null;
        }
        return new Date(lastNetworkLogRetrievalTime);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public Date getLastSecurityLogRetrievalTime() {
        long lastSecurityLogRetrievalTime = this.mDpm.getLastSecurityLogRetrievalTime();
        if (lastSecurityLogRetrievalTime < 0) {
            return null;
        }
        return new Date(lastSecurityLogRetrievalTime);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public int getMaximumFailedPasswordsBeforeWipeInCurrentUser() {
        ComponentName deviceOwnerComponentOnCallingUser = this.mDpm.getDeviceOwnerComponentOnCallingUser();
        if (deviceOwnerComponentOnCallingUser == null) {
            deviceOwnerComponentOnCallingUser = this.mDpm.getProfileOwnerAsUser(MY_USER_ID);
        }
        if (deviceOwnerComponentOnCallingUser == null) {
            return 0;
        }
        return this.mDpm.getMaximumFailedPasswordsForWipe(deviceOwnerComponentOnCallingUser, MY_USER_ID);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public int getMaximumFailedPasswordsBeforeWipeInManagedProfile() {
        ComponentName profileOwnerAsUser;
        int managedProfileUserId = getManagedProfileUserId();
        if (managedProfileUserId == -10000 || (profileOwnerAsUser = this.mDpm.getProfileOwnerAsUser(managedProfileUserId)) == null) {
            return 0;
        }
        return this.mDpm.getMaximumFailedPasswordsForWipe(profileOwnerAsUser, managedProfileUserId);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public int getNumberOfActiveDeviceAdminsForCurrentUserAndManagedProfile() {
        Iterator it = this.mUm.getProfiles(MY_USER_ID).iterator();
        int i = 0;
        while (it.hasNext()) {
            List activeAdminsAsUser = this.mDpm.getActiveAdminsAsUser(((UserInfo) it.next()).id);
            if (activeAdminsAsUser != null) {
                i += activeAdminsAsUser.size();
            }
        }
        return i;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public int getNumberOfOwnerInstalledCaCertsForCurrentUser() {
        List ownerInstalledCaCerts = this.mDpm.getOwnerInstalledCaCerts(new UserHandle(MY_USER_ID));
        if (ownerInstalledCaCerts == null) {
            return 0;
        }
        return ownerInstalledCaCerts.size();
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public int getNumberOfOwnerInstalledCaCertsForManagedProfile() {
        List ownerInstalledCaCerts;
        int managedProfileUserId = getManagedProfileUserId();
        if (managedProfileUserId == -10000 || (ownerInstalledCaCerts = this.mDpm.getOwnerInstalledCaCerts(new UserHandle(managedProfileUserId))) == null) {
            return 0;
        }
        return ownerInstalledCaCerts.size();
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean hasDeviceOwner() {
        return getDeviceOwnerComponent() != null;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean hasWorkPolicyInfo() {
        return (getWorkPolicyInfoIntentDO() == null && getWorkPolicyInfoIntentPO() == null) ? false : true;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean isAlwaysOnVpnSetInCurrentUser() {
        return VpnUtils.isAlwaysOnVpnSet(this.mVm, MY_USER_ID);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean isAlwaysOnVpnSetInManagedProfile() {
        int managedProfileUserId = getManagedProfileUserId();
        return managedProfileUserId != -10000 && VpnUtils.isAlwaysOnVpnSet(this.mVm, managedProfileUserId);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean isInCompMode() {
        return hasDeviceOwner() && getManagedProfileUserId() != -10000;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean isNetworkLoggingEnabled() {
        return this.mDpm.isNetworkLoggingEnabled(null);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean isSecurityLoggingEnabled() {
        return this.mDpm.isSecurityLoggingEnabled(null);
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean showParentalControls() {
        Intent parentalControlsIntent = getParentalControlsIntent();
        if (parentalControlsIntent != null) {
            this.mContext.startActivity(parentalControlsIntent);
            return true;
        }
        return false;
    }

    @Override // com.android.settings.enterprise.EnterprisePrivacyFeatureProvider
    public boolean showWorkPolicyInfo() {
        Intent workPolicyInfoIntentDO = getWorkPolicyInfoIntentDO();
        if (workPolicyInfoIntentDO != null) {
            this.mContext.startActivity(workPolicyInfoIntentDO);
            return true;
        }
        Intent workPolicyInfoIntentPO = getWorkPolicyInfoIntentPO();
        UserInfo managedProfileUserInfo = getManagedProfileUserInfo();
        if (workPolicyInfoIntentPO == null || managedProfileUserInfo == null) {
            return false;
        }
        this.mContext.startActivityAsUser(workPolicyInfoIntentPO, managedProfileUserInfo.getUserHandle());
        return true;
    }
}
