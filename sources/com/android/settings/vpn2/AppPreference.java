package com.android.settings.vpn2;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import androidx.preference.Preference;
import com.android.internal.net.VpnConfig;
import com.android.settingslib.RestrictedLockUtils;

/* loaded from: classes2.dex */
public class AppPreference extends ManageablePreference {
    public static final int STATE_DISCONNECTED = ManageablePreference.STATE_NONE;
    private final String mName;
    private final String mPackageName;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public AppPreference(Context context, int i, String str) {
        super(context, null);
        Drawable drawable = null;
        super.setUserId(i);
        this.mPackageName = str;
        disableIfConfiguredByAdmin();
        try {
            Context userContext = getUserContext();
            PackageManager packageManager = userContext.getPackageManager();
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(str, 0);
                if (packageInfo != null) {
                    drawable = packageInfo.applicationInfo.loadIcon(packageManager);
                    str = VpnConfig.getVpnLabel(userContext, str).toString();
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
            if (drawable == null) {
                drawable = packageManager.getDefaultActivityIcon();
            }
        } catch (PackageManager.NameNotFoundException unused2) {
        }
        this.mName = str;
        setTitle(str);
        setIcon(drawable);
    }

    private void disableIfConfiguredByAdmin() {
        if (isDisabledByAdmin()) {
            return;
        }
        if (this.mPackageName.equals(((DevicePolicyManager) getContext().createContextAsUser(UserHandle.of(getUserId()), 0).getSystemService(DevicePolicyManager.class)).getAlwaysOnVpnPackage())) {
            setDisabledByAdmin(RestrictedLockUtils.getProfileOrDeviceOwner(getContext(), UserHandle.of(this.mUserId)));
        }
    }

    private Context getUserContext() throws PackageManager.NameNotFoundException {
        return getContext().createPackageContextAsUser(getContext().getPackageName(), 0, UserHandle.of(this.mUserId));
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // androidx.preference.Preference, java.lang.Comparable
    public int compareTo(Preference preference) {
        if (!(preference instanceof AppPreference)) {
            return preference instanceof LegacyVpnPreference ? -((LegacyVpnPreference) preference).compareTo((Preference) this) : super.compareTo(preference);
        }
        AppPreference appPreference = (AppPreference) preference;
        int i = appPreference.mState - this.mState;
        if (i == 0) {
            int compareToIgnoreCase = this.mName.compareToIgnoreCase(appPreference.mName);
            if (compareToIgnoreCase == 0) {
                int compareTo = this.mPackageName.compareTo(appPreference.mPackageName);
                return compareTo == 0 ? this.mUserId - appPreference.mUserId : compareTo;
            }
            return compareToIgnoreCase;
        }
        return i;
    }

    public String getLabel() {
        return this.mName;
    }

    public PackageInfo getPackageInfo() {
        try {
            return getUserContext().getPackageManager().getPackageInfo(this.mPackageName, 0);
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    @Override // com.android.settingslib.widget.TwoTargetPreference, com.android.settingslib.miuisettings.preference.PreferenceFeature
    public boolean hasIcon() {
        return true;
    }
}
