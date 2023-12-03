package com.android.settings.wifi;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.provider.MiuiSettings;
import android.provider.Settings;
import androidx.preference.PreferenceManager;
import java.util.HashSet;
import java.util.Set;

/* loaded from: classes2.dex */
public class AutoConnectUtils {
    private static AutoConnectUtils sAutoConnectUtils;
    private HashSet<String> mSsidSet;
    private Object mLock = new Object();
    private String OPEN_WIFI_AUTO_CONNECT_SSID = "open_wifi_auto_connect_ssid_list";

    private AutoConnectUtils(Context context) {
        registerDisableWifiAutoConnectChangedObserver(context);
    }

    public static AutoConnectUtils getInstance(Context context) {
        if (sAutoConnectUtils == null) {
            sAutoConnectUtils = new AutoConnectUtils(context.getApplicationContext());
        }
        return sAutoConnectUtils;
    }

    private Set<String> getNoSecretWifiSet(Context context) {
        return new HashSet(PreferenceManager.getDefaultSharedPreferences(context).getStringSet(this.OPEN_WIFI_AUTO_CONNECT_SSID, new HashSet()));
    }

    private void registerDisableWifiAutoConnectChangedObserver(final Context context) {
        context.getContentResolver().registerContentObserver(Settings.System.getUriFor("disable_wifi_auto_connect_ssid"), false, new ContentObserver(null) { // from class: com.android.settings.wifi.AutoConnectUtils.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                synchronized (AutoConnectUtils.this.mLock) {
                    AutoConnectUtils.this.mSsidSet = MiuiSettings.System.getDisableWifiAutoConnectSsid(context);
                }
            }
        });
        this.mSsidSet = MiuiSettings.System.getDisableWifiAutoConnectSsid(context);
    }

    public void enableAutoConnect(Context context, String str, boolean z) {
        synchronized (this.mLock) {
            if (z) {
                this.mSsidSet.remove(str);
            } else {
                this.mSsidSet.add(str);
            }
            MiuiSettings.System.setDisableWifiAutoConnectSsid(context, this.mSsidSet);
        }
    }

    public boolean isAutoConnect(String str) {
        boolean z;
        synchronized (this.mLock) {
            z = !this.mSsidSet.contains(str);
        }
        return z;
    }

    public void removeNoSecretWifi(Context context, String str) {
        Set<String> noSecretWifiSet = getNoSecretWifiSet(context);
        if (noSecretWifiSet.contains(str)) {
            noSecretWifiSet.remove(str);
            SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
            edit.putStringSet(this.OPEN_WIFI_AUTO_CONNECT_SSID, noSecretWifiSet);
            edit.commit();
        }
    }
}
