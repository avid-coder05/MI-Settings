package com.android.settingslib.wifi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.SystemProperties;
import android.util.Log;
import com.miui.cloudservice.IPasspointKeyInterface;
import java.util.Random;
import miui.cloud.Constants;
import miui.os.Build;

/* loaded from: classes2.dex */
public class WifiPasspointProvision {
    private static final Random RANDOM = new Random();
    private static WifiPasspointProvision sInstance;
    private ServiceConnection mConnection = new ServiceConnection() { // from class: com.android.settingslib.wifi.WifiPasspointProvision.1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            WifiPasspointProvision.this.mIPasspointKeyInterface = IPasspointKeyInterface.Stub.asInterface(iBinder);
            Log.i("WifiPasspointProvision", "onServiceConnected");
            if (WifiPasspointProvision.this.mIPasspointKeyInterface == null) {
                Log.i("WifiPasspointProvision", "mIPasspointKeyInterface == null");
            } else {
                Log.i("WifiPasspointProvision", "mIPasspointKeyInterface != null");
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("WifiPasspointProvision", "onServiceDisconnected");
        }
    };
    private Context mContext;
    private IPasspointKeyInterface mIPasspointKeyInterface;
    private WifiManager mWifiManager;

    /* loaded from: classes2.dex */
    public static abstract class PasspointR1ProvisioningCallback {
    }

    public WifiPasspointProvision(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.mWifiManager = (WifiManager) applicationContext.getSystemService("wifi");
    }

    public static WifiPasspointProvision getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new WifiPasspointProvision(context);
        }
        return sInstance;
    }

    public static boolean isPasspointR1Supported() {
        return !Build.IS_INTERNATIONAL_BUILD && SystemProperties.getInt("ro.vendor.net.enable_passpoint_r1", 0) == 1;
    }

    public void bindPasspointKeyService() {
        if (this.mIPasspointKeyInterface != null) {
            return;
        }
        Intent intent = new Intent("com.miui.cloudservice.PasspointService");
        intent.setClassName(Constants.CLOUDSERVICE_PACKAGE_NAME, "com.miui.cloudservice.alipay.provision.PasspointService");
        this.mContext.bindService(intent, this.mConnection, 1);
    }
}
