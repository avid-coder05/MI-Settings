package com.android.settings.search.tree;

import android.content.Context;
import com.android.settingslib.search.SettingsTree;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class BluetoothSettingsTree extends SettingsTree {
    public static final String ADD_THIRD_APP_NAME = "add_text_app_name";
    public static final String BT_AUTO_DISABLE_LE_BA = "auto_disable_broadcast_audio";
    public static final String BT_BROADCAST_AUDIO = "bluetooth_broadcast_audio";
    public static final String BT_CONNECT_HELP = "connect_help";
    public static final String BT_LE_BA_SECURITY = "ba_security_settings";
    public static final String BT_NOTIFICATION_ID = "bt_show_notification_title";
    public static final String BT_SHARE_BROADCAST = "bluetooth_share_broadcast";
    public static final String FAST_CONNECT_DEVICE_ID = "bluetooth_fastConnect_key_device_id";

    protected BluetoothSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    public boolean initialize() {
        String columnValue = getColumnValue("resource");
        columnValue.hashCode();
        char c = 65535;
        switch (columnValue.hashCode()) {
            case -2013885982:
                if (columnValue.equals(FAST_CONNECT_DEVICE_ID)) {
                    c = 0;
                    break;
                }
                break;
            case -1248451376:
                if (columnValue.equals(BT_SHARE_BROADCAST)) {
                    c = 1;
                    break;
                }
                break;
            case 769494855:
                if (columnValue.equals(BT_BROADCAST_AUDIO)) {
                    c = 2;
                    break;
                }
                break;
            case 914961817:
                if (columnValue.equals(BT_NOTIFICATION_ID)) {
                    c = 3;
                    break;
                }
                break;
            case 1293730033:
                if (columnValue.equals(BT_AUTO_DISABLE_LE_BA)) {
                    c = 4;
                    break;
                }
                break;
            case 1747821122:
                if (columnValue.equals(BT_LE_BA_SECURITY)) {
                    c = 5;
                    break;
                }
                break;
            case 1913655670:
                if (columnValue.equals(BT_CONNECT_HELP)) {
                    c = 6;
                    break;
                }
                break;
            case 2085187997:
                if (columnValue.equals(ADD_THIRD_APP_NAME)) {
                    c = 7;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                return true;
            default:
                return super.initialize();
        }
    }
}
