package com.android.settings.wifi.operatorutils.operatorutilsimpl;

import android.content.Context;
import android.util.Log;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public final class VodafoneOperator extends BaseOperator {
    private int mDefaultEapMethod;
    private List<String> mListConfig;
    private int mSlotid;

    public VodafoneOperator(Context context) {
        super(context);
        ArrayList arrayList = new ArrayList();
        this.mListConfig = arrayList;
        this.mSlotid = 0;
        this.mDefaultEapMethod = 5;
        arrayList.add("\"Vodafone NL Wifi\"-WPA_EAP");
        this.mListConfig.add("\"VodafoneWiFi\"-WPA_EAP");
    }

    private void deleteWifiConfig() {
        deleteSaveWifiConfig(getConfiguredNetworkNetId(this.mListConfig));
    }

    private String getVodafoneOperator() {
        int slotidforOperatorName = getSlotidforOperatorName("20404");
        int slotidforOperatorName2 = getSlotidforOperatorName("23415");
        if (slotidforOperatorName == 2 && slotidforOperatorName2 == 2) {
            return null;
        }
        int min = Math.min(slotidforOperatorName, slotidforOperatorName2);
        this.mSlotid = min;
        return slotidforOperatorName == min ? "20404" : "23415";
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
        if ("Vodafone NL Wifi".equals(str) || "VodafoneWiFi".equals(str)) {
            String vodafoneOperator = getVodafoneOperator();
            if ("20404".equals(vodafoneOperator) || "23415".equals(vodafoneOperator)) {
                return true;
            }
        }
        return false;
    }

    @Override // com.android.settings.wifi.operatorutils.operatorutilsimpl.BaseOperator, com.android.settings.wifi.operatorutils.Operator
    public void opCustomizationView(View view, PreferenceScreen preferenceScreen) {
        if (view == null || preferenceScreen == null) {
            return;
        }
        Preference findPreference = preferenceScreen.findPreference("wifi_detail_delete");
        Preference findPreference2 = preferenceScreen.findPreference("wifi_detail_modify");
        if (findPreference != null && findPreference2 != null) {
            preferenceScreen.removePreference(findPreference);
            preferenceScreen.removePreference(findPreference2);
        }
        view.findViewById(R.id.ip_fields).setVisibility(8);
        view.findViewById(R.id.proxy_settings_fields).setVisibility(8);
    }

    @Override // com.android.settings.wifi.operatorutils.operatorutilsimpl.BaseOperator, com.android.settings.wifi.operatorutils.Operator
    public void updateWifiConfig() {
        String vodafoneOperator = getVodafoneOperator();
        if ("20404".equals(vodafoneOperator)) {
            setCustomWifiConfiguration(this.mSlotid, "Vodafone NL Wifi", 5);
            Log.d("BaseOperator", "save nl wifi config successful mSlotid " + this.mSlotid);
        } else if (!"23415".equals(vodafoneOperator)) {
            deleteWifiConfig();
        } else {
            setCustomWifiConfiguration(this.mSlotid, "VodafoneWiFi", 5);
            Log.d("BaseOperator", "save uk wifi config successful mSlotid " + this.mSlotid);
        }
    }
}
