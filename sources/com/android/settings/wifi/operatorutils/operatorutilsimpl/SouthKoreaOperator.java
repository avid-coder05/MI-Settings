package com.android.settings.wifi.operatorutils.operatorutilsimpl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public final class SouthKoreaOperator extends BaseOperator {
    private int lastType;
    private Context mContext;
    private int mDefaultEapMethod;
    private List<String> mListConfigKey;
    private NetworkConnectivityChangedReceiver mNetworkConnectivityReceiver;
    private int mSlotid;
    private String mSsid;

    /* loaded from: classes2.dex */
    private class NetworkConnectivityChangedReceiver extends BroadcastReceiver {
        private NetworkConnectivityChangedReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
            int type = networkInfo.getType();
            if (networkInfo.getDetailedState() != NetworkInfo.DetailedState.CONNECTED || type >= 18) {
                return;
            }
            if (type == 0 && SouthKoreaOperator.this.lastType == 1) {
                Toast.makeText(SouthKoreaOperator.this.mContext, SouthKoreaOperator.this.mContext.getString(R.string.switchNetworkToast), 1).show();
            }
            SouthKoreaOperator.this.lastType = type;
        }
    }

    public SouthKoreaOperator(Context context) {
        super(context);
        this.mListConfigKey = new ArrayList();
        this.mSlotid = 0;
        this.lastType = 1;
        this.mDefaultEapMethod = 5;
        this.mContext = context;
    }

    private void deleteWifiConfig() {
        deleteSaveWifiConfig(this.mListConfigKey);
    }

    private boolean isLGUOperator() {
        int slotidforOperatorName = getSlotidforOperatorName("45006");
        if (slotidforOperatorName != 2) {
            this.mSlotid = slotidforOperatorName;
            return true;
        }
        return false;
    }

    private boolean isOperator() {
        int slotidforOperatorName = getSlotidforOperatorName("45008");
        if (slotidforOperatorName != 2) {
            this.mSlotid = slotidforOperatorName;
            return true;
        }
        return false;
    }

    private boolean isSKTOperator() {
        int slotidforOperatorName = getSlotidforOperatorName("45005");
        if (slotidforOperatorName != 2) {
            this.mSlotid = slotidforOperatorName;
            return true;
        }
        return false;
    }

    @Override // com.android.settings.wifi.operatorutils.operatorutilsimpl.BaseOperator, com.android.settings.wifi.operatorutils.Operator
    public int getDefaultEapMethod() {
        return this.mDefaultEapMethod;
    }

    @Override // com.android.settings.wifi.operatorutils.operatorutilsimpl.BaseOperator, com.android.settings.wifi.operatorutils.Operator
    public int getSlotId() {
        return this.mSlotid;
    }

    @Override // com.android.settings.wifi.operatorutils.operatorutilsimpl.BaseOperator, com.android.settings.wifi.operatorutils.Operator
    public boolean isForbidDelSsid(String str) {
        return "KT GiGA WiFi".equals(str) || "KT WiFi".equals(str) || "olleh GiGA WiFi".equals(str) || "ollehWiFi".equals(str);
    }

    @Override // com.android.settings.wifi.operatorutils.operatorutilsimpl.BaseOperator, com.android.settings.wifi.operatorutils.Operator
    public boolean isOpCustomization(String str) {
        if (isOperator() && ("KT GiGA WiFi".equals(str) || "KT WiFi".equals(str) || "olleh GiGA WiFi".equals(str) || "ollehWiFi".equals(str))) {
            return true;
        }
        if ("T wifi zone_secure".equals(str) && isSKTOperator()) {
            return true;
        }
        if (isLGUOperator()) {
            return "U+zone".equals(str) || "FREE_U+zone".equals(str) || "U+CAN".equals(str) || "5G_U+zone".equals(str) || "U+zone_5G".equals(str);
        }
        return false;
    }

    @Override // com.android.settings.wifi.operatorutils.operatorutilsimpl.BaseOperator, com.android.settings.wifi.operatorutils.Operator
    public void opCustomizationView(View view, PreferenceScreen preferenceScreen) {
        if (preferenceScreen == null) {
            return;
        }
        Preference findPreference = preferenceScreen.findPreference("wifi_detail_delete");
        if ("KT GiGA WiFi".equals(this.mSsid) || "KT WiFi".equals(this.mSsid) || "olleh GiGA WiFi".equals(this.mSsid) || "ollehWiFi".equals(this.mSsid)) {
            if (findPreference != null) {
                preferenceScreen.removePreference(findPreference);
            }
        } else if (findPreference != null) {
            preferenceScreen.addPreference(findPreference);
        }
    }

    @Override // com.android.settings.wifi.operatorutils.operatorutilsimpl.BaseOperator, com.android.settings.wifi.operatorutils.Operator
    public void registerReceiver() {
        if (this.mNetworkConnectivityReceiver == null) {
            this.mNetworkConnectivityReceiver = new NetworkConnectivityChangedReceiver();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.mContext.registerReceiver(this.mNetworkConnectivityReceiver, intentFilter);
    }

    @Override // com.android.settings.wifi.operatorutils.operatorutilsimpl.BaseOperator, com.android.settings.wifi.operatorutils.Operator
    public void updateWifiConfig() {
        if (isOperator()) {
            setCustomWifiConfiguration(this.mSlotid, "KT GiGA WiFi", 5);
            setCustomWifiConfiguration(this.mSlotid, "KT WiFi", 5);
            setCustomWifiConfiguration(this.mSlotid, "olleh GiGA WiFi", 5);
            setCustomWifiConfiguration(this.mSlotid, "ollehWiFi", 5);
            this.mListConfigKey.clear();
            this.mListConfigKey.add("\"KT GiGA WiFi\"-WPA_EAP");
            this.mListConfigKey.add("\"KT WiFi\"-WPA_EAP");
            this.mListConfigKey.add("\"olleh GiGA WiFi\"-WPA_EAP");
            this.mListConfigKey.add("\"ollehWiFi\"-WPA_EAP");
            Log.d("BaseOperator", "save  SouthKoreaOperator wifi config successful mSlotid " + this.mSlotid);
        } else if (isSKTOperator()) {
            setCustomWifiConfiguration(this.mSlotid, "T wifi zone_secure", 5);
            this.mListConfigKey.clear();
            this.mListConfigKey.add("\"T wifi zone_secure\"-WPA_EAP");
        } else if (!isLGUOperator()) {
            deleteWifiConfig();
        } else {
            setCustomWifiConfiguration(this.mSlotid, "U+zone", 5);
            setCustomWifiConfiguration(this.mSlotid, "FREE_U+zone", 5);
            setCustomWifiConfiguration(this.mSlotid, "U+CAN", 5);
            setCustomWifiConfiguration(this.mSlotid, "5G_U+zone", 5);
            setCustomWifiConfiguration(this.mSlotid, "U+zone_5G", 5);
            this.mListConfigKey.clear();
            this.mListConfigKey.add("\"U+zone\"-WPA_EAP");
            this.mListConfigKey.add("\"FREE_U+zone\"-WPA_EAP");
            this.mListConfigKey.add("\"U+CAN\"-WPA_EAP");
            this.mListConfigKey.add("\"5G_U+zone\"-WPA_EAP");
            this.mListConfigKey.add("\"U+zone_5G\"-WPA_EAP");
        }
    }
}
