package com.android.settings.wifi.operatorutils.operatorutilsimpl;

import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public final class MexicoOperator extends BaseOperator {
    private int mDefaultEapMethod;
    private List<String> mListATMncMCC;
    private String mMccmnc;
    private int mSlotid;

    public MexicoOperator(Context context) {
        super(context);
        this.mSlotid = 0;
        this.mMccmnc = "334050";
        this.mDefaultEapMethod = 5;
        this.mListATMncMCC = new ArrayList();
        initATMncMCC();
    }

    private void deleteWifiConfig() {
        deleteSaveWifiConfig(getConfiguredNetworkNetId("\"AT&T-MEX-WiFi\"-WPA_EAP"));
    }

    private void initATMncMCC() {
        this.mListATMncMCC.add("334050");
        this.mListATMncMCC.add("334090");
        this.mListATMncMCC.add("310410");
    }

    private boolean isATOperator() {
        int slotidforOperatorName = getSlotidforOperatorName(this.mListATMncMCC);
        if (slotidforOperatorName != 2) {
            this.mSlotid = slotidforOperatorName;
            this.mMccmnc = getSimOperator();
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
        return "AT&T-MEX-WiFi".equals(str) && isATOperator();
    }

    @Override // com.android.settings.wifi.operatorutils.operatorutilsimpl.BaseOperator, com.android.settings.wifi.operatorutils.Operator
    public void updateWifiConfig() {
        if (!isATOperator()) {
            deleteWifiConfig();
            return;
        }
        String str = this.mMccmnc.equals("310410") ? "attwifi.com" : "attmexwifi.com";
        createPasspointConfiguration(str, "AT&T MEX Passpoint", this.mMccmnc.equals("310410") ? "wlan.mnc410.mcc310.3gppnetwork.org" : "wlan.mnc050.mcc334.3gppnetwork.org", this.mSlotid, this.mMccmnc + "*", this.mMccmnc, 5);
        Log.d("BaseOperator", "save AT&T wifi config successful mSlotid " + this.mSlotid + " mccmnc " + this.mMccmnc);
    }
}
