package com.android.settings.wifi.operatorutils.operatorutilsimpl;

import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public final class EuropeOperator extends BaseOperator {
    private int mDefaultEapMethod;
    private List<String> mListConfig;
    private int mSlotid;

    public EuropeOperator(Context context) {
        super(context);
        ArrayList arrayList = new ArrayList();
        this.mListConfig = arrayList;
        this.mSlotid = 0;
        this.mDefaultEapMethod = 5;
        arrayList.add("\"Telekom_SIM\"-WPA_EAP");
        this.mListConfig.add("\"COSMOTEWiFiAuto\"-WPA_EAP");
    }

    private void deleteWifiConfig() {
        deleteSaveWifiConfig(getConfiguredNetworkNetId(this.mListConfig));
    }

    private boolean isDEOperator() {
        int slotidforOperatorName = getSlotidforOperatorName("26201");
        if (slotidforOperatorName != 2) {
            this.mSlotid = slotidforOperatorName;
            return true;
        }
        return false;
    }

    private boolean isGROperator() {
        int slotidforOperatorName = getSlotidforOperatorName("20201");
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
        if ("Telekom_SIM".equals(str) || "COSMOTEWiFiAuto".equals(str)) {
            return isDEOperator() || isGROperator();
        }
        return false;
    }

    @Override // com.android.settings.wifi.operatorutils.operatorutilsimpl.BaseOperator, com.android.settings.wifi.operatorutils.Operator
    public void updateWifiConfig() {
        if (isDEOperator()) {
            setCustomWifiConfiguration(this.mSlotid, "Telekom_SIM", 5);
            Log.d("BaseOperator", "save DE wifi config successful mSlotid " + this.mSlotid);
        } else if (!isGROperator()) {
            deleteWifiConfig();
        } else {
            setCustomWifiConfiguration(this.mSlotid, "COSMOTEWiFiAuto", 5);
            Log.d("BaseOperator", "save GR wifi config successful mSlotid " + this.mSlotid);
        }
    }
}
