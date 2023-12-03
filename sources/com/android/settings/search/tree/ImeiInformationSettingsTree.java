package com.android.settings.search.tree;

import android.content.Context;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.android.settings.R;
import com.android.settingslib.search.SearchUtils;
import com.android.settingslib.search.SettingsTree;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class ImeiInformationSettingsTree extends SettingsTree {
    protected ImeiInformationSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    protected int getStatus() {
        String columnValue = getColumnValue("resource");
        if (!"status_meid_number".equals(columnValue)) {
            int charAt = columnValue.charAt(columnValue.length() - 1) - '0';
            if (charAt < 0 || charAt > 1) {
                charAt = 0;
            } else {
                columnValue = columnValue.substring(0, columnValue.length() - 1);
            }
            if (TelephonyManager.from(((SettingsTree) this).mContext).getCurrentPhoneTypeForSlot(charAt) == 2) {
                if ("status_imei_sv".equals(columnValue)) {
                    return 0;
                }
            } else if ("status_prl_version".equals(columnValue) || "status_min_number".equals(columnValue) || "status_icc_id".equals(columnValue)) {
                return 0;
            }
        } else if (TextUtils.isEmpty(miui.telephony.TelephonyManager.getDefault().getMeid())) {
            return 0;
        }
        return super.getStatus();
    }

    protected String getTitle(boolean z) {
        String columnValue = getColumnValue("resource");
        if (z) {
            int charAt = columnValue.charAt(columnValue.length() - 1) - '0';
            if (charAt < 0 || charAt > 1) {
                return super.getTitle(true);
            }
            return SearchUtils.getString(((SettingsTree) this).mContext, columnValue.substring(0, columnValue.length() - 1)) + " " + ((SettingsTree) this).mContext.getResources().getString(R.string.slot_number, Integer.valueOf(charAt + 1));
        }
        return columnValue;
    }

    public boolean initialize() {
        if ("imei_information_title".equals(getColumnValue("resource")) && TelephonyManager.from(((SettingsTree) this).mContext).getSimCount() > 1) {
            if (SystemProperties.getInt("ro.miui.singlesim", 0) == 0) {
                int size = getSons().size();
                for (int i = 0; i < size; i++) {
                    SettingsTree settingsTree = (SettingsTree) getSons().get(i);
                    JSONObject jSONObject = new JSONObject();
                    String columnValue = settingsTree.getColumnValue("resource");
                    if (!"status_meid_number".equals(columnValue)) {
                        settingsTree.setColumnValue("resource", columnValue + "0");
                        try {
                            jSONObject.put("resource", columnValue + "1");
                            addSon(SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this));
                        } catch (JSONException unused) {
                        }
                    }
                }
            }
        }
        return super.initialize();
    }
}
