package micloud.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

/* loaded from: classes2.dex */
public class ConnectivityHelper {
    private static ConnectivityHelper sInstance;
    private final ConnectivityManager mConnectivityManager;
    private volatile String mMacAddress;
    private final WifiManager mWifiManager;

    private ConnectivityHelper(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.mConnectivityManager = (ConnectivityManager) applicationContext.getSystemService("connectivity");
        this.mWifiManager = (WifiManager) applicationContext.getSystemService("wifi");
    }

    public static synchronized ConnectivityHelper getInstance(Context context) {
        ConnectivityHelper connectivityHelper;
        synchronized (ConnectivityHelper.class) {
            if (sInstance == null) {
                sInstance = new ConnectivityHelper(context);
            }
            connectivityHelper = sInstance;
        }
        return connectivityHelper;
    }

    public String getMacAddress() {
        String str = this.mMacAddress;
        if (TextUtils.isEmpty(str)) {
            WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
            if (connectionInfo != null) {
                str = connectionInfo.getMacAddress();
            }
            if (!TextUtils.isEmpty(str)) {
                this.mMacAddress = str;
            }
        }
        return str;
    }
}
