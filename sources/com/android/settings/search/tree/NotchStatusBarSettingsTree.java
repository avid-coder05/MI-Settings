package com.android.settings.search.tree;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.MiuiSettings;
import android.telephony.TelephonyManager;
import com.android.settings.R;
import com.android.settings.search.FunctionColumns;
import com.android.settings.utils.StatusBarUtils;
import com.android.settings.utils.Utils;
import com.android.settingslib.OldmanHelper;
import com.android.settingslib.search.SettingsTree;
import miui.os.Build;
import miui.util.CustomizeUtil;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class NotchStatusBarSettingsTree extends SettingsTree {
    private static final String NOTCH_FORCE_BLACK = "force_black";
    private static final String NOTCH_FORCE_BLACK_V2 = "force_black_v2";

    protected NotchStatusBarSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    public Intent getIntent() {
        return super.getIntent();
    }

    protected int getStatus() {
        String columnValue = getColumnValue("resource");
        if ("notch_and_status_bar_settings".equals(columnValue)) {
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
            if (!"notch_style_mod_title".equals(columnValue) || (CustomizeUtil.HAS_NOTCH && !"odin".equals(android.os.Build.DEVICE))) {
                if (!"notch_force_black_title".equals(columnValue) || (CustomizeUtil.HAS_NOTCH && !"odin".equals(android.os.Build.DEVICE))) {
                    if ("cutout_mode_title".equals(columnValue)) {
                        if (Utils.supportCutoutMode()) {
                            if (MiuiSettings.Global.getBoolean(((SettingsTree) this).mContext.getContentResolver(), NOTCH_FORCE_BLACK) || MiuiSettings.Global.getBoolean(((SettingsTree) this).mContext.getContentResolver(), NOTCH_FORCE_BLACK_V2)) {
                                return 0;
                            }
                            return super.getStatus();
                        }
                        return 0;
                    } else if (!"cutout_type_title".equals(columnValue) || Utils.supportOverlayRoundedCorner()) {
                        if ("show_carrier_under_keyguard_title".equals(columnValue) && (StatusBarUtils.IS_MX_TELCEL || StatusBarUtils.IS_LM_CR)) {
                            return 0;
                        }
                        return super.getStatus();
                    } else {
                        return 0;
                    }
                }
                return 0;
            }
            return 0;
        }
        return 0;
    }

    protected String getTitle(boolean z) {
        return (!getColumnValue("resource").equals("notch_and_status_bar_settings") || CustomizeUtil.HAS_NOTCH) ? super.getTitle(z) : ((SettingsTree) this).mContext.getResources().getString(R.string.status_bar_title);
    }

    public boolean initialize() {
        if ("custom_carrier_title".equals(getColumnValue("resource"))) {
            setColumnValue(FunctionColumns.FRAGMENT, "com.android.settings.CarrierNameSettings");
        }
        return super.initialize();
    }
}
