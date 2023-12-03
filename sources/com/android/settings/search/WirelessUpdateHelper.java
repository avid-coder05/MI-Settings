package com.android.settings.search;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.TetheringManager;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.cardemulation.CardEmulation;
import android.os.UserManager;
import android.text.TextUtils;
import com.android.settings.R;
import com.android.settings.custs.CellBroadcastUtil;
import com.android.settingslib.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.os.Build;
import miui.util.FeatureParser;

/* loaded from: classes2.dex */
class WirelessUpdateHelper extends BaseSearchUpdateHelper {
    private static final String ACTION_MI_PLAY = "miui.intent.action.MIPLAY";
    private static final String BLUETOOTH_TETHER_RESOURCE = "bluetooth_tether_checkbox_text";
    private static final String CELL_BROADCAST_RESOURCE = "cell_broadcast_settings";
    private static final String DETECTION_RESOURCE = "wifi_poor_network_detection";
    private static final String DIALOG_REMIND_RESOURCE = "wifi_dialog_remind_type_title";
    private static final String ENABLE_WFD_RESOURCE = "enable_wifi_display";
    private static final String FREQUENCY_BAND_RESOURCE = "wifi_setting_frequency_band_title";
    private static final String GSM_TO_WIFI_CONNECT_TYPE_RESOURCE = "gsm_to_wifi_connect_type_title";
    private static final String NFC_PAYMENT_RESOURCE = "nfc_payment_settings_title";
    private static final String NFC_SE_ROUTE_RESOURCE = "nfc_se_route_title";
    private static final String NFC_SE_WALLET_RESOURCE = "nfc_se_wallet_title";
    private static final String PRIORITY_SETTINGS_RESOURCE = "wifi_priority_settings_title";
    private static final String PRIORITY_TYPE_RESOURCE = "wifi_priority_type_title";
    private static final String SELECT_SSID_RESOURCE = "select_ssid_type_title";
    private static final String USB_TETHER_RESOURCE = "usb_tethering_button_text";
    private static final String VOLTE_SWITCH_RESOURCE = "volte_switch_title";
    private static final String WAPI_CERT_INSTALL_RESOURCE = "wifi_wapi_cert_install";
    private static final String WAPI_CERT_UNINSTALL_RESOURCE = "wifi_wapi_cert_uninstall";
    private static final String WFD_SETTINGS_RESOURCE = "wfd_settings_title";
    private static final String WIFI_AUTOMATICALLY_CONNECT_RESOURCE = "wifi_automatically_connect_title";

    WirelessUpdateHelper() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void update(Context context, ArrayList<ContentProviderOperation> arrayList) {
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        if (new ArrayList().isEmpty()) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, WIFI_AUTOMATICALLY_CONNECT_RESOURCE);
        }
        if (FeatureParser.getBoolean("support_wapi", false) && !TextUtils.equals("mediatek", FeatureParser.getString("vendor"))) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, WAPI_CERT_INSTALL_RESOURCE);
            BaseSearchUpdateHelper.hideByResource(context, arrayList, WAPI_CERT_UNINSTALL_RESOURCE);
        }
        if (!Build.IS_CM_CUSTOMIZATION) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, PRIORITY_TYPE_RESOURCE);
            BaseSearchUpdateHelper.hideByResource(context, arrayList, PRIORITY_SETTINGS_RESOURCE);
            BaseSearchUpdateHelper.hideByResource(context, arrayList, GSM_TO_WIFI_CONNECT_TYPE_RESOURCE);
            BaseSearchUpdateHelper.hideByResource(context, arrayList, SELECT_SSID_RESOURCE);
            BaseSearchUpdateHelper.hideByResource(context, arrayList, DIALOG_REMIND_RESOURCE);
        }
        if (!Utils.isWifiOnly(context)) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, DETECTION_RESOURCE);
        }
        if (!Build.IS_GLOBAL_BUILD && !"pisces".equals(android.os.Build.DEVICE)) {
            Iterator<String> it = BaseSearchUpdateHelper.getIdWithResource(context, NFC_SE_ROUTE_RESOURCE).iterator();
            while (it.hasNext()) {
                BaseSearchUpdateHelper.updateItemData(context, arrayList, it.next(), "name", context.getResources().getString(R.string.nfc_se_wallet_title));
            }
            BaseSearchUpdateHelper.updatePath(context, arrayList, NFC_SE_ROUTE_RESOURCE, NFC_SE_WALLET_RESOURCE);
        }
        List list = null;
        try {
            list = CardEmulation.getInstance(NfcAdapter.getDefaultAdapter(context)).getServices("payment");
        } catch (NullPointerException unused) {
        }
        if (list == null || list.isEmpty()) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, NFC_PAYMENT_RESOURCE);
        }
        updateCellBroadcast(context, arrayList);
        if (context.getPackageManager().resolveActivity(new Intent(ACTION_MI_PLAY), SearchUpdater.GOOGLE) != null) {
            Iterator<String> it2 = BaseSearchUpdateHelper.getIdWithResource(context, "wfd_settings_title").iterator();
            while (it2.hasNext()) {
                BaseSearchUpdateHelper.updateSearchItem(arrayList, context.getPackageName() + it2.next(), 5, new String[]{"intent_action", FunctionColumns.INTENT_DATA, FunctionColumns.DEST_PACKAGE, FunctionColumns.DEST_CLASS, FunctionColumns.FRAGMENT}, new String[]{ACTION_MI_PLAY, "", "", "", ""});
            }
            BaseSearchUpdateHelper.hideByResource(context, arrayList, ENABLE_WFD_RESOURCE);
        }
        TetheringManager tetheringManager = (TetheringManager) context.getSystemService("tethering");
        if (tetheringManager.getTetherableUsbRegexs().length == 0) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, USB_TETHER_RESOURCE);
        }
        if (tetheringManager.getTetherableBluetoothRegexs().length == 0) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, BLUETOOTH_TETHER_RESOURCE);
        }
    }

    private static void updateCellBroadcast(Context context, ArrayList<ContentProviderOperation> arrayList) {
        PackageManager packageManager = context.getPackageManager();
        UserManager userManager = (UserManager) context.getSystemService("user");
        boolean z = context.getResources().getBoolean(285540354);
        boolean z2 = false;
        try {
            CellBroadcastUtil.setCellbroadcastEnabledSetting(context);
            if (z) {
                if (packageManager.getApplicationEnabledSetting("com.android.cellbroadcastreceiver") == 2) {
                    z = false;
                }
                if (CellBroadcastUtil.nccBroadcastEnabled(packageManager)) {
                    z = true;
                }
            }
            z2 = z;
        } catch (IllegalArgumentException unused) {
        }
        if ((!Build.IS_INTERNATIONAL_BUILD || !z2) || userManager.hasUserRestriction("no_config_cell_broadcasts")) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, "cell_broadcast_settings");
        }
    }
}
