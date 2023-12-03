package com.android.settings.wifi.operatorutils.operatorutilsimpl;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;
import androidx.preference.PreferenceScreen;
import com.android.settings.wifi.operatorutils.Operator;
import java.util.Iterator;
import java.util.List;
import miui.telephony.TelephonyManager;

/* loaded from: classes2.dex */
public class BaseOperator extends Operator {
    public final String TAG = "BaseOperator";
    public final Context mContext;
    public TelephonyManager mTeleMgr;
    public WifiManager mWifiManager;

    public BaseOperator(Context context) {
        this.mTeleMgr = null;
        this.mWifiManager = null;
        this.mContext = context;
        this.mTeleMgr = TelephonyManager.getDefault();
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
    }

    private boolean isCustomWifiExist(String str) {
        String str2;
        if (this.mWifiManager.getWifiState() == 1) {
            Log.w("BaseOperator", "Wifi state is disabled");
            return true;
        }
        List<ScanResult> scanResults = this.mWifiManager.getScanResults();
        if (scanResults == null) {
            Log.e("BaseOperator", "ScanResult is null");
            return false;
        }
        for (ScanResult scanResult : scanResults) {
            if (str.equals(scanResult.SSID) && (str2 = scanResult.capabilities) != null && str2.contains("EAP")) {
                return true;
            }
        }
        return false;
    }

    private boolean isCustomWifiSet(int i, String str, int i2) {
        List<WifiConfiguration> configuredNetworks = this.mWifiManager.getConfiguredNetworks();
        String convertToQuotedString = convertToQuotedString(str);
        String num = Integer.toString(i + 1);
        if (configuredNetworks != null) {
            for (WifiConfiguration wifiConfiguration : configuredNetworks) {
                if (convertToQuotedString.equals(wifiConfiguration.SSID) && i2 == wifiConfiguration.enterpriseConfig.getEapMethod()) {
                    String fieldValue = wifiConfiguration.enterpriseConfig.getFieldValue("sim_num");
                    return fieldValue instanceof Integer ? i == ((Integer) fieldValue).intValue() : num.equals(fieldValue);
                }
            }
            return false;
        }
        return false;
    }

    public String convertToQuotedString(String str) {
        return "\"" + str + "\"";
    }

    public int createPasspointConfiguration(String str, String str2, String str3, int i, String str4, String str5, int i2) {
        if (str == null || str2 == null) {
            Log.e("BaseOperator", "It does not meet the specifications of passpoint!");
            return -1;
        }
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.enterpriseConfig.setEapMethod(i2);
        wifiConfiguration.FQDN = str;
        wifiConfiguration.providerFriendlyName = str2;
        wifiConfiguration.enterpriseConfig.setRealm(str3);
        wifiConfiguration.enterpriseConfig.setPlmn(str5);
        String str6 = wifiConfiguration.FQDN;
        if (str6 != null) {
            wifiConfiguration.setPasspointUniqueId(wifiConfiguration.FQDN + "_" + (str6.hashCode() + wifiConfiguration.networkId + wifiConfiguration.creatorUid));
        }
        int addNetwork = this.mWifiManager.addNetwork(wifiConfiguration);
        if (addNetwork != -1) {
            this.mWifiManager.enableNetwork(addNetwork, true);
        }
        Log.d("BaseOperator", "setCustomWifiConfiguration" + addNetwork + " FQDN " + str + " FriendlyName " + str2 + " Realm " + str3 + " PLMN " + str5);
        return addNetwork;
    }

    public void deleteSaveWifiConfig(int i) {
        if (i != -1) {
            Log.d("BaseOperator", " deleteSaveWifiConfig mNetId: " + i);
            this.mWifiManager.forget(i, null);
        }
    }

    public void deleteSaveWifiConfig(List<String> list) {
        for (WifiConfiguration wifiConfiguration : this.mWifiManager.getConfiguredNetworks()) {
            Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                if (it.next().equals(wifiConfiguration.getKey())) {
                    Log.d("BaseOperator", " deleteSaveWifiConfig list mNetId: ");
                    this.mWifiManager.forget(wifiConfiguration.networkId, null);
                }
            }
        }
    }

    public int getConfiguredNetworkNetId(String str) {
        for (WifiConfiguration wifiConfiguration : this.mWifiManager.getConfiguredNetworks()) {
            if (str.equals(wifiConfiguration.getKey())) {
                return wifiConfiguration.networkId;
            }
        }
        return -1;
    }

    public int getConfiguredNetworkNetId(List<String> list) {
        for (WifiConfiguration wifiConfiguration : this.mWifiManager.getConfiguredNetworks()) {
            Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                if (it.next().equals(wifiConfiguration.getKey())) {
                    return wifiConfiguration.networkId;
                }
            }
        }
        return -1;
    }

    @Override // com.android.settings.wifi.operatorutils.Operator
    public int getDefaultEapMethod() {
        return -1;
    }

    public String getSimOperator() {
        return this.mTeleMgr.getSimOperator();
    }

    @Override // com.android.settings.wifi.operatorutils.Operator
    public int getSlotId() {
        return 0;
    }

    public int getSlotidforOperatorName(String str) {
        String simOperatorForSlot;
        for (int i = 0; i < 2; i++) {
            if (this.mTeleMgr.getSimStateForSlot(i) == 5 && (simOperatorForSlot = this.mTeleMgr.getSimOperatorForSlot(i)) != null && simOperatorForSlot.equals(str)) {
                return i;
            }
        }
        return 2;
    }

    public int getSlotidforOperatorName(List<String> list) {
        for (int i = 0; i < 2; i++) {
            if (this.mTeleMgr.getSimStateForSlot(i) == 5) {
                String simOperatorForSlot = this.mTeleMgr.getSimOperatorForSlot(i);
                for (String str : list) {
                    if (simOperatorForSlot != null && simOperatorForSlot.equals(str)) {
                        return i;
                    }
                }
            }
        }
        return 2;
    }

    @Override // com.android.settings.wifi.operatorutils.Operator
    public boolean isForbidDelSsid(String str) {
        return false;
    }

    @Override // com.android.settings.wifi.operatorutils.Operator
    public boolean isOpCustomization(String str) {
        return false;
    }

    @Override // com.android.settings.wifi.operatorutils.Operator
    public void opCustomizationView(View view, PreferenceScreen preferenceScreen) {
    }

    @Override // com.android.settings.wifi.operatorutils.Operator
    public void registerReceiver() {
    }

    public int setCustomWifiConfiguration(int i, String str) {
        return setCustomWifiConfiguration(i, str, 4);
    }

    public int setCustomWifiConfiguration(int i, String str, int i2) {
        if (isCustomWifiSet(i, str, i2)) {
            Log.d("BaseOperator", "Custom wifi config has updated, return");
            return -1;
        }
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = convertToQuotedString(str);
        wifiConfiguration.allowedKeyManagement.set(2);
        wifiConfiguration.allowedKeyManagement.set(3);
        wifiConfiguration.enterpriseConfig.setEapMethod(i2);
        wifiConfiguration.enterpriseConfig.setFieldValue("sim_num", Integer.toString(i));
        wifiConfiguration.macRandomizationSetting = 0;
        int addNetwork = this.mWifiManager.addNetwork(wifiConfiguration);
        if (addNetwork != -1) {
            this.mWifiManager.enableNetwork(addNetwork, isCustomWifiExist(str));
        }
        Log.d("BaseOperator", "setCustomWifiConfiguration" + addNetwork);
        return addNetwork;
    }

    @Override // com.android.settings.wifi.operatorutils.Operator
    public void stopTethering() {
    }

    @Override // com.android.settings.wifi.operatorutils.Operator
    public void updateWifiConfig() {
    }
}
