package com.android.settings.wifi.operatorutils.operatorutilsimpl;

import android.content.Context;
import android.util.Log;

/* loaded from: classes2.dex */
public final class SingaporeOperator extends BaseOperator {
    private int mDefaultEapMethod;
    private int mSlotid;

    public SingaporeOperator(Context context) {
        super(context);
        this.mSlotid = 0;
        this.mDefaultEapMethod = 4;
    }

    private void deleteWifiConfig() {
        deleteSaveWifiConfig(getConfiguredNetworkNetId("\"Singtel WIFI\"-WPA_EAP"));
    }

    private boolean isM1Operator() {
        int slotidforOperatorName = getSlotidforOperatorName("52503");
        if (slotidforOperatorName != 2) {
            this.mSlotid = slotidforOperatorName;
            return true;
        }
        return false;
    }

    private boolean isSHOperator() {
        int slotidforOperatorName = getSlotidforOperatorName("52505");
        if (slotidforOperatorName != 2) {
            this.mSlotid = slotidforOperatorName;
            return true;
        }
        return false;
    }

    private boolean isSingaporeOperator() {
        int slotidforOperatorName = getSlotidforOperatorName("52501");
        int slotidforOperatorName2 = getSlotidforOperatorName("52502");
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
    public int getSlotId() {
        return this.mSlotid;
    }

    @Override // com.android.settings.wifi.operatorutils.operatorutilsimpl.BaseOperator, com.android.settings.wifi.operatorutils.Operator
    public boolean isOpCustomization(String str) {
        if (str == null) {
            return false;
        }
        if ((str.indexOf("Singtel") > -1 || str.indexOf("Wireless@SGx") > -1) && isSingaporeOperator()) {
            return true;
        }
        if (str.indexOf("Wireless@SGx") <= -1 || !isM1Operator()) {
            return str.indexOf("Wireless@SGx") > -1 && isSHOperator();
        }
        return true;
    }

    @Override // com.android.settings.wifi.operatorutils.operatorutilsimpl.BaseOperator, com.android.settings.wifi.operatorutils.Operator
    public void updateWifiConfig() {
        if (isSingaporeOperator()) {
            setCustomWifiConfiguration(this.mSlotid, "Singtel WIFI");
        } else {
            deleteWifiConfig();
        }
        Log.d("BaseOperator", "save sg wifi config successful mSlotid ");
    }
}
