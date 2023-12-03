package com.android.settingslib.wifi;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.Network;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.util.Log;
import com.android.wifitrackerlib.ISlaveWifiUtils;
import java.lang.reflect.Method;

/* loaded from: classes2.dex */
public class SlaveWifiUtils implements ISlaveWifiUtils {
    private static volatile SlaveWifiUtils mInstance;
    private boolean mIsSupportDualWifi;
    private Object mSlaveWifiManager;
    private Method method_connectToSlaveAp;
    private Method method_connectToSlaveAp_config;
    private Method method_disconnectSlaveWifi;
    private Method method_getAutoDisableDefault;
    private Method method_getInstance = null;
    private Method method_getSlaveDhcpInfo;
    private Method method_getSlaveWifiCurrentNetwork;
    private Method method_getSlaveWifiState;
    private Method method_getWifiSlaveConnectionInfo;
    private Method method_isSlaveWifiEnabled;
    private Method method_setWifiSlaveEnabled;
    private Method method_supportDualWifi;

    public SlaveWifiUtils(Context context) {
        this.mSlaveWifiManager = null;
        this.method_setWifiSlaveEnabled = null;
        this.method_getWifiSlaveConnectionInfo = null;
        this.method_getSlaveWifiCurrentNetwork = null;
        this.method_getSlaveDhcpInfo = null;
        this.method_disconnectSlaveWifi = null;
        this.method_connectToSlaveAp = null;
        this.method_connectToSlaveAp_config = null;
        this.method_getSlaveWifiState = null;
        this.method_isSlaveWifiEnabled = null;
        this.method_getAutoDisableDefault = null;
        this.method_supportDualWifi = null;
        try {
            Class<?> cls = Class.forName("android.net.wifi.SlaveWifiManager");
            this.mSlaveWifiManager = context.getSystemService(getSlaveServiceName(cls));
            this.method_supportDualWifi = cls.getDeclaredMethod("supportDualWifi", null);
            boolean supportDualWifi = supportDualWifi();
            this.mIsSupportDualWifi = supportDualWifi;
            if (supportDualWifi) {
                this.method_setWifiSlaveEnabled = cls.getDeclaredMethod("setWifiSlaveEnabled", Boolean.TYPE);
                this.method_getWifiSlaveConnectionInfo = cls.getDeclaredMethod("getWifiSlaveConnectionInfo", null);
                this.method_getSlaveWifiCurrentNetwork = cls.getDeclaredMethod("getSlaveWifiCurrentNetwork", null);
                this.method_getSlaveDhcpInfo = cls.getDeclaredMethod("getSlaveDhcpInfo", null);
                this.method_disconnectSlaveWifi = cls.getDeclaredMethod("disconnectSlaveWifi", null);
                this.method_connectToSlaveAp = cls.getDeclaredMethod("connectToSlaveAp", Integer.TYPE);
                this.method_connectToSlaveAp_config = cls.getDeclaredMethod("connectToSlaveAp", WifiConfiguration.class);
                this.method_getSlaveWifiState = cls.getDeclaredMethod("getSlaveWifiState", null);
                this.method_isSlaveWifiEnabled = cls.getDeclaredMethod("isSlaveWifiEnabled", null);
                this.method_getAutoDisableDefault = cls.getDeclaredMethod("getAutoDisableDefault", Context.class);
            }
        } catch (Exception e) {
            Log.e("SlaveWifiUtils", "the device don't support dual wifi, return " + e);
        }
    }

    private boolean checkIsVaild() {
        return this.mIsSupportDualWifi && this.mSlaveWifiManager != null;
    }

    public static SlaveWifiUtils getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SlaveWifiUtils.class) {
                if (mInstance == null) {
                    mInstance = new SlaveWifiUtils(context);
                }
            }
        }
        return mInstance;
    }

    private String getSlaveServiceName(Class cls) {
        try {
            return (String) cls.getField("SERVICE_NAME").get(null);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override // com.android.wifitrackerlib.ISlaveWifiUtils
    public void connectToSlaveAp(int i) {
        if (checkIsVaild()) {
            try {
                this.method_connectToSlaveAp.invoke(this.mSlaveWifiManager, Integer.valueOf(i));
            } catch (Exception e) {
                Log.e("SlaveWifiUtils", "connectToSlaveAp Exception:" + e);
            }
        }
    }

    @Override // com.android.wifitrackerlib.ISlaveWifiUtils
    public void connectToSlaveAp(WifiConfiguration wifiConfiguration) {
        if (checkIsVaild()) {
            try {
                this.method_connectToSlaveAp_config.invoke(this.mSlaveWifiManager, wifiConfiguration);
            } catch (Exception e) {
                Log.e("SlaveWifiUtils", "method_connectToSlaveAp_config Exception:" + e);
            }
        }
    }

    public boolean disconnectSlaveWifi() {
        if (checkIsVaild()) {
            Boolean bool = Boolean.FALSE;
            try {
                bool = (Boolean) this.method_disconnectSlaveWifi.invoke(this.mSlaveWifiManager, null);
            } catch (Exception e) {
                Log.e("SlaveWifiUtils", "disconnectSlaveWifi Exception:" + e);
            }
            return bool.booleanValue();
        }
        return false;
    }

    public int getAutoDisableDefault(Context context) {
        int i = 0;
        try {
            i = ((Integer) this.method_getAutoDisableDefault.invoke(null, context)).intValue();
        } catch (Exception e) {
            Log.e("SlaveWifiUtils", "getAutoDisableDefault Exception:" + e);
        }
        Log.d("SlaveWifiUtils", "getAutoDisableDefault:" + i);
        return i;
    }

    public DhcpInfo getSlaveDhcpInfo() {
        DhcpInfo dhcpInfo = null;
        if (checkIsVaild()) {
            try {
                dhcpInfo = (DhcpInfo) this.method_getSlaveDhcpInfo.invoke(this.mSlaveWifiManager, null);
            } catch (Exception e) {
                Log.e("SlaveWifiUtils", "getSlaveDhcpInfo Exception:" + e);
            }
            Log.d("SlaveWifiUtils", "getSlaveDhcpInfo:" + dhcpInfo);
            return dhcpInfo;
        }
        return null;
    }

    @Override // com.android.wifitrackerlib.ISlaveWifiUtils
    public Network getSlaveWifiCurrentNetwork() {
        Network network = null;
        if (checkIsVaild()) {
            try {
                network = (Network) this.method_getSlaveWifiCurrentNetwork.invoke(this.mSlaveWifiManager, null);
            } catch (Exception e) {
                Log.e("SlaveWifiUtils", "getSlaveWifiCurrentNetwork Exception:" + e);
            }
            Log.d("SlaveWifiUtils", "getSlaveWifiCurrentNetwork:" + network);
            return network;
        }
        return null;
    }

    public int getSlaveWifiState() {
        int i = -1;
        if (checkIsVaild()) {
            try {
                i = ((Integer) this.method_getSlaveWifiState.invoke(this.mSlaveWifiManager, null)).intValue();
            } catch (Exception e) {
                Log.e("SlaveWifiUtils", "getSlaveWifiState Exception:" + e);
            }
            Log.d("SlaveWifiUtils", "getSlaveWifiState:" + i);
            return i;
        }
        return -1;
    }

    @Override // com.android.wifitrackerlib.ISlaveWifiUtils
    public WifiInfo getWifiSlaveConnectionInfo() {
        WifiInfo wifiInfo = null;
        if (checkIsVaild()) {
            try {
                wifiInfo = (WifiInfo) this.method_getWifiSlaveConnectionInfo.invoke(this.mSlaveWifiManager, null);
            } catch (Exception e) {
                Log.e("SlaveWifiUtils", "getWifiSlaveConnectionInfo Exception:" + e);
            }
            Log.d("SlaveWifiUtils", "getWifiSlaveConnectionInfo:" + wifiInfo);
            return wifiInfo;
        }
        return null;
    }

    @Override // com.android.wifitrackerlib.ISlaveWifiUtils
    public boolean is24GHz(ScanResult scanResult) {
        return WifiUtils.is24GHz(scanResult);
    }

    @Override // com.android.wifitrackerlib.ISlaveWifiUtils
    public boolean is5GHz(ScanResult scanResult) {
        return WifiUtils.is5GHz(scanResult);
    }

    @Override // com.android.wifitrackerlib.ISlaveWifiUtils
    public boolean isSlaveWifiEnabled() {
        if (checkIsVaild()) {
            Boolean bool = Boolean.FALSE;
            try {
                bool = (Boolean) this.method_isSlaveWifiEnabled.invoke(this.mSlaveWifiManager, null);
            } catch (Exception e) {
                Log.e("SlaveWifiUtils", "isSlaveWifiEnabled Exception:" + e);
            }
            Log.d("SlaveWifiUtils", "isSlaveWifiEnabled:" + bool);
            return bool.booleanValue();
        }
        return false;
    }

    public boolean isUiVisible(Context context) {
        return supportDualWifi() && !Build.MODEL.contains("Redmi Note 8 Pro");
    }

    public boolean setWifiSlaveEnabled(boolean z) {
        if (checkIsVaild()) {
            Boolean bool = Boolean.FALSE;
            try {
                bool = (Boolean) this.method_setWifiSlaveEnabled.invoke(this.mSlaveWifiManager, Boolean.valueOf(z));
            } catch (Exception e) {
                Log.e("SlaveWifiUtils", "setWifiSlaveEnabled Exception:" + e);
            }
            return bool.booleanValue();
        }
        return false;
    }

    public boolean supportDualWifi() {
        Boolean bool = Boolean.FALSE;
        try {
            bool = (Boolean) this.method_supportDualWifi.invoke(this.mSlaveWifiManager, null);
        } catch (Exception e) {
            Log.e("SlaveWifiUtils", "supportDualWifi Exception:" + e);
        }
        return bool.booleanValue();
    }
}
