package com.android.settings.applications.specialaccess.deviceadmin;

import android.app.admin.DeviceAdminInfo;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.util.Log;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class DeviceAdminListItem implements Comparable<DeviceAdminListItem> {
    private final DevicePolicyManager mDPM;
    private CharSequence mDescription;
    private final Drawable mIcon;
    private final DeviceAdminInfo mInfo;
    private final String mKey;
    private final CharSequence mName;
    private final UserHandle mUserHandle;

    public DeviceAdminListItem(Context context, DeviceAdminInfo deviceAdminInfo) {
        this.mInfo = deviceAdminInfo;
        UserHandle userHandle = new UserHandle(getUserIdFromDeviceAdminInfo(deviceAdminInfo));
        this.mUserHandle = userHandle;
        this.mKey = userHandle.getIdentifier() + "@" + deviceAdminInfo.getComponent().flattenToString();
        this.mDPM = (DevicePolicyManager) context.getSystemService("device_policy");
        PackageManager packageManager = context.getPackageManager();
        this.mName = deviceAdminInfo.loadLabel(packageManager);
        try {
            this.mDescription = deviceAdminInfo.loadDescription(packageManager);
        } catch (Resources.NotFoundException unused) {
            Log.w("DeviceAdminListItem", "Setting description to null because can't find resource: " + this.mKey);
        }
        this.mIcon = packageManager.getUserBadgedIcon(this.mInfo.loadIcon(packageManager), this.mUserHandle);
    }

    private static int getUserIdFromDeviceAdminInfo(DeviceAdminInfo deviceAdminInfo) {
        return UserHandle.getUserId(deviceAdminInfo.getActivityInfo().applicationInfo.uid);
    }

    @Override // java.lang.Comparable
    public int compareTo(DeviceAdminListItem deviceAdminListItem) {
        return this.mName.toString().compareTo(deviceAdminListItem.mName.toString());
    }

    public CharSequence getDescription() {
        return this.mDescription;
    }

    public Drawable getIcon() {
        return this.mIcon;
    }

    public String getKey() {
        return this.mKey;
    }

    public Intent getLaunchIntent(Context context) {
        return new Intent(context, DeviceAdminAdd.class).putExtra("android.app.extra.DEVICE_ADMIN", this.mInfo.getComponent());
    }

    public CharSequence getName() {
        return this.mName;
    }

    public UserHandle getUser() {
        return new UserHandle(getUserIdFromDeviceAdminInfo(this.mInfo));
    }

    public boolean isActive() {
        return this.mDPM.isAdminActiveAsUser(this.mInfo.getComponent(), getUserIdFromDeviceAdminInfo(this.mInfo));
    }

    public boolean isEnabled() {
        return !this.mDPM.isRemovingAdmin(this.mInfo.getComponent(), getUserIdFromDeviceAdminInfo(this.mInfo));
    }
}
