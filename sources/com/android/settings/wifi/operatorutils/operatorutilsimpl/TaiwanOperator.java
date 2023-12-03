package com.android.settings.wifi.operatorutils.operatorutilsimpl;

import android.content.Context;
import android.util.Log;

/* loaded from: classes2.dex */
public final class TaiwanOperator extends BaseOperator {
    private int mDefaultEapMethod;
    private int mSlotid;

    public TaiwanOperator(Context context) {
        super(context);
        this.mSlotid = 0;
        this.mDefaultEapMethod = 4;
    }

    private void deleteWifiConfig() {
        deleteSaveWifiConfig(getConfiguredNetworkNetId("\"FET Wi-Fi Auto\"-WPA_EAP"));
    }

    private boolean isTWOperator() {
        int slotidforOperatorName = getSlotidforOperatorName("46601");
        int slotidforOperatorName2 = getSlotidforOperatorName("46602");
        if (slotidforOperatorName == 2 && slotidforOperatorName2 == 2) {
            return false;
        }
        this.mSlotid = Math.min(slotidforOperatorName, slotidforOperatorName2);
        return true;
    }

    @Override // com.android.settings.wifi.operatorutils.operatorutilsimpl.BaseOperator, com.android.settings.wifi.operatorutils.Operator
    public int getDefaultEapMethod() {
        return this.mDefaultEapMethod;
    }

    @Override // com.android.settings.wifi.operatorutils.operatorutilsimpl.BaseOperator, com.android.settings.wifi.operatorutils.Operator
    public boolean isOpCustomization(String str) {
        return str != null && str.indexOf("FET Wi-Fi Auto") > -1;
    }

    @Override // com.android.settings.wifi.operatorutils.operatorutilsimpl.BaseOperator, com.android.settings.wifi.operatorutils.Operator
    public void updateWifiConfig() {
        if (!isTWOperator()) {
            deleteWifiConfig();
            return;
        }
        setCustomWifiConfiguration(this.mSlotid, "FET Wi-Fi Auto");
        Log.d("BaseOperator", "save tw wifi config successful mSlotid " + this.mSlotid);
    }
}
