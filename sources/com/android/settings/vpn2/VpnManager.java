package com.android.settings.vpn2;

import android.app.Activity;
import android.content.Context;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.security.LegacyVpnProfileStore;
import android.util.Log;
import android.widget.Toast;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnConfig;
import com.android.internal.net.VpnProfile;
import com.android.settings.R;
import java.util.Iterator;

/* loaded from: classes2.dex */
public class VpnManager {
    private String TAG = "VpnManager";
    private Context mContext;
    private UserManager mUserManager;
    final android.net.VpnManager mVpmManager;

    public VpnManager(Context context) {
        this.mContext = context;
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mVpmManager = (android.net.VpnManager) this.mContext.getSystemService("vpn_management");
    }

    private boolean isExistInThirdPartyAppList(String str) {
        Iterator<AppVpnInfo> it = VpnSettings.getVpnApps(this.mContext, true).iterator();
        boolean z = false;
        while (it.hasNext()) {
            if (str.equalsIgnoreCase(it.next().packageName)) {
                z = true;
            }
        }
        return z;
    }

    public void connect(VpnProfile vpnProfile, Activity activity) throws RemoteException {
        if (vpnProfile == null) {
            return;
        }
        LegacyVpnProfileStore.put("VPN_" + vpnProfile.key, vpnProfile.encode());
        disconnect(vpnProfile.key);
        try {
            this.mVpmManager.startLegacyVpn(vpnProfile);
        } catch (IllegalStateException unused) {
            if (activity != null) {
                Toast.makeText(activity, R.string.vpn_no_network, 1).show();
            }
        }
    }

    public void disconnect(String str) {
        try {
            LegacyVpnInfo legacyVpnInfo = this.mVpmManager.getLegacyVpnInfo(UserHandle.myUserId());
            if (legacyVpnInfo == null || !str.equals(legacyVpnInfo.key)) {
                return;
            }
            this.mVpmManager.prepareVpn("[Legacy VPN]", "[Legacy VPN]", UserHandle.myUserId());
        } catch (Exception e) {
            Log.e(this.TAG, "Failed to disconnect", e);
        }
    }

    public LegacyVpnInfo getLegacyVpnInfo(int i) {
        android.net.VpnManager vpnManager = this.mVpmManager;
        if (vpnManager == null) {
            return null;
        }
        return vpnManager.getLegacyVpnInfo(i);
    }

    public int getVpnConnectionStatus() {
        try {
            LegacyVpnInfo legacyVpnInfo = this.mVpmManager.getLegacyVpnInfo(UserHandle.myUserId());
            r0 = legacyVpnInfo != null ? legacyVpnInfo.state : -1;
            Iterator<UserHandle> it = this.mUserManager.getUserProfiles().iterator();
            while (it.hasNext()) {
                VpnConfig vpnConfig = this.mVpmManager.getVpnConfig(it.next().getIdentifier());
                if (vpnConfig != null && isExistInThirdPartyAppList(vpnConfig.user)) {
                    return 3;
                }
            }
            return r0;
        } catch (Exception unused) {
            return r0;
        }
    }

    public int getVpnNumbers() {
        return LegacyVpnProfileStore.list("VPN_").length + VpnSettings.getVpnApps(this.mContext, true).size();
    }
}
