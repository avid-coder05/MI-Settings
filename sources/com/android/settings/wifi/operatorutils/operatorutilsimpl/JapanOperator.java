package com.android.settings.wifi.operatorutils.operatorutilsimpl;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public final class JapanOperator extends BaseOperator {
    private ConnectivityManager cm;
    private int mDefaultEapMethod;
    private List<String> mListKDDIMncMCC;
    private int mSlotid;

    public JapanOperator(Context context) {
        super(context);
        this.mSlotid = 0;
        this.mListKDDIMncMCC = new ArrayList();
        this.mDefaultEapMethod = 5;
        this.cm = (ConnectivityManager) context.getSystemService("connectivity");
        initKddiMncMCC();
    }

    private void deleteWifiConfig() {
        deleteSaveWifiConfig(getConfiguredNetworkNetId("\"0000Rakuten\"-WPA_EAP"));
        deleteSaveWifiConfig(getConfiguredNetworkNetId("\"au_Wi-Fi2\"-WPA_EAP"));
    }

    private void initKddiMncMCC() {
        this.mListKDDIMncMCC.add("44007");
        this.mListKDDIMncMCC.add("44008");
        this.mListKDDIMncMCC.add("44050");
        this.mListKDDIMncMCC.add("44051");
        this.mListKDDIMncMCC.add("44052");
        this.mListKDDIMncMCC.add("44053");
        this.mListKDDIMncMCC.add("44054");
        this.mListKDDIMncMCC.add("44055");
        this.mListKDDIMncMCC.add("44056");
        this.mListKDDIMncMCC.add("44070");
        this.mListKDDIMncMCC.add("44071");
        this.mListKDDIMncMCC.add("44072");
        this.mListKDDIMncMCC.add("44073");
        this.mListKDDIMncMCC.add("44074");
        this.mListKDDIMncMCC.add("44075");
        this.mListKDDIMncMCC.add("44076");
        this.mListKDDIMncMCC.add("44077");
        this.mListKDDIMncMCC.add("44078");
        this.mListKDDIMncMCC.add("44079");
        this.mListKDDIMncMCC.add("44088");
        this.mListKDDIMncMCC.add("44089");
        this.mListKDDIMncMCC.add("44170");
    }

    private boolean isKDDIOperator() {
        int slotidforOperatorName = getSlotidforOperatorName(this.mListKDDIMncMCC);
        if (slotidforOperatorName != 2) {
            this.mSlotid = slotidforOperatorName;
            return true;
        }
        return false;
    }

    private boolean isRMNOperator() {
        int slotidforOperatorName = getSlotidforOperatorName("44011");
        if (slotidforOperatorName != 2) {
            this.mSlotid = slotidforOperatorName;
            return true;
        }
        return false;
    }

    private boolean isSoftBankOperator() {
        int slotidforOperatorName = getSlotidforOperatorName("44020");
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
    public boolean isOpCustomization(String str) {
        if ("0000Rakuten".equals(str) && isRMNOperator()) {
            return true;
        }
        if ("au_Wi-Fi2".equals(str) && isKDDIOperator()) {
            return true;
        }
        if ("0002softbank".equals(str) && isSoftBankOperator()) {
            this.mDefaultEapMethod = 4;
            return true;
        }
        return false;
    }

    @Override // com.android.settings.wifi.operatorutils.operatorutilsimpl.BaseOperator, com.android.settings.wifi.operatorutils.Operator
    public void stopTethering() {
        ConnectivityManager connectivityManager;
        Log.i("BaseOperator", "ready to stop Tethering!");
        if (!isKDDIOperator() || (connectivityManager = this.cm) == null) {
            return;
        }
        connectivityManager.stopTethering(0);
        this.cm.stopTethering(2);
        this.cm.stopTethering(1);
    }

    @Override // com.android.settings.wifi.operatorutils.operatorutilsimpl.BaseOperator, com.android.settings.wifi.operatorutils.Operator
    public void updateWifiConfig() {
        if (!isRMNOperator()) {
            if (isKDDIOperator()) {
                setCustomWifiConfiguration(this.mSlotid, "au_Wi-Fi2", 5);
                return;
            } else {
                deleteWifiConfig();
                return;
            }
        }
        setCustomWifiConfiguration(this.mSlotid, "0000Rakuten", 5);
        Log.d("BaseOperator", "save RMN wifi config successful mSlotid " + this.mSlotid);
    }
}
