package com.android.settings.wifi.operatorutils.operatorutilsimpl;

import android.content.Context;
import android.util.Log;

/* loaded from: classes2.dex */
public final class ThailandOperator extends BaseOperator {
    private int mDefaultEapMethod;
    private int mSlotid;

    public ThailandOperator(Context context) {
        super(context);
        this.mSlotid = 0;
        this.mDefaultEapMethod = 4;
    }

    private void deleteTHWifiConfig() {
        deleteSaveWifiConfig(getConfiguredNetworkNetId("\"  AIS SMART Login\"-WPA_EAP"));
        deleteSaveWifiConfig(getConfiguredNetworkNetId("\"   .@ TrueMove H\"-WPA_EAP"));
    }

    private boolean isTHOperator() {
        int slotidforOperatorName = getSlotidforOperatorName("52003");
        int slotidforOperatorName2 = getSlotidforOperatorName("52001");
        if (slotidforOperatorName == 2 && slotidforOperatorName2 == 2) {
            return false;
        }
        this.mSlotid = Math.min(slotidforOperatorName, slotidforOperatorName2);
        return true;
    }

    private boolean isTRUEOperator() {
        int slotidforOperatorName = getSlotidforOperatorName("52000");
        int slotidforOperatorName2 = getSlotidforOperatorName("52004");
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
        if ("  AIS SMART Login".equals(str) && isTHOperator()) {
            return true;
        }
        return "   .@ TrueMove H".equals(str) && isTRUEOperator();
    }

    @Override // com.android.settings.wifi.operatorutils.operatorutilsimpl.BaseOperator, com.android.settings.wifi.operatorutils.Operator
    public void updateWifiConfig() {
        if (!isTHOperator()) {
            if (isTRUEOperator()) {
                setCustomWifiConfiguration(this.mSlotid, "   .@ TrueMove H");
                return;
            } else {
                deleteTHWifiConfig();
                return;
            }
        }
        setCustomWifiConfiguration(this.mSlotid, "  AIS SMART Login");
        Log.d("BaseOperator", "save wifi config successful mSlotid " + this.mSlotid);
    }
}
