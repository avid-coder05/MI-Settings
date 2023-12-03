package com.android.settings.search.tree;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import com.android.settings.notification.NotificationSettingsHelper;
import com.android.settings.search.FunctionColumns;
import com.android.settings.utils.StatusBarUtils;
import com.android.settingslib.OldmanHelper;
import com.android.settingslib.search.SettingsTree;
import miui.os.Build;
import miui.util.CustomizeUtil;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class StatusBarSettingsTree extends SettingsTree {
    protected StatusBarSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    public Intent getIntent() {
        Intent preferManageEntranceIntent;
        return (!"manage_notification_title".equals(getColumnValue("resource")) || (preferManageEntranceIntent = NotificationSettingsHelper.getPreferManageEntranceIntent(((SettingsTree) this).mContext)) == null) ? super.getIntent() : preferManageEntranceIntent;
    }

    protected int getStatus() {
        String columnValue = getColumnValue("resource");
        if ("status_bar_settings".equals(columnValue)) {
            if (OldmanHelper.isStatusBarSettingsHidden(((SettingsTree) this).mContext)) {
                return 0;
            }
        } else if ("custom_carrier_title".equals(columnValue)) {
            if (StatusBarUtils.IS_MX_TELCEL || StatusBarUtils.IS_LM_CR) {
                return 0;
            }
            if (!((TelephonyManager) ((SettingsTree) this).mContext.getSystemService("phone")).isDataCapable()) {
                setColumnValue("resource", "custom_carrier_title");
            }
        } else if ("custom_wifi_name_title".equals(columnValue)) {
            if (((TelephonyManager) ((SettingsTree) this).mContext.getSystemService("phone")).isDataCapable()) {
                setColumnValue("resource", "custom_carrier_title");
            }
        } else if ("notification_shade_shortcut_title".equals(columnValue)) {
            return 0;
        } else {
            if (CustomizeUtil.HAS_NOTCH && "show_notification_icon_title".equals(columnValue)) {
                setColumnValue("resource", "show_notification_icon_title");
            }
        }
        if (Build.IS_INTERNATIONAL_BUILD && "notification_fold_title".equals(columnValue)) {
            return 0;
        }
        if (Build.VERSION.SDK_INT > 23 || !"notification_style_title".equals(columnValue)) {
            return super.getStatus();
        }
        return 0;
    }

    public boolean initialize() {
        if ("custom_carrier_title".equals(getColumnValue("resource"))) {
            setColumnValue(FunctionColumns.FRAGMENT, "com.android.settings.CarrierNameSettings");
        }
        return super.initialize();
    }
}
