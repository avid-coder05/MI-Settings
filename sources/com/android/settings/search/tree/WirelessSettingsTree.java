package com.android.settings.search.tree;

import android.content.Context;
import android.content.Intent;
import android.net.TetheringManager;
import android.nfc.NfcAdapter;
import android.nfc.cardemulation.CardEmulation;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.RegionUtils;
import com.android.settings.Utils;
import com.android.settings.connection.ScreenProjectionController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.SearchUpdater;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.vpn2.VpnManager;
import com.android.settingslib.drawer.DashboardCategory;
import com.android.settingslib.drawer.Tile;
import com.android.settingslib.search.SearchUtils;
import com.android.settingslib.search.SettingsTree;
import com.android.settingslib.search.TinyIntent;
import com.miui.enterprise.RestrictionsHelper;
import java.util.HashMap;
import java.util.LinkedList;
import miui.os.Build;
import miui.payment.PaymentManager;
import miui.util.FeatureParser;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class WirelessSettingsTree extends SettingsTree {
    private static final String ANDROID_BEAM_SETTINGS_TITLE = "android_beam_settings_title";
    private static final String BLOCK_LIST_CONNECTED_DEVICES = "block_list_connected_devices";
    private static final String BLOCK_LIST_TITLE = "block_list_title";
    private static final String CONNECTION_AND_SHARING = "connection_and_sharing";
    public static final String CONNECTION_AND_SHARING_TITLE = "connection_and_sharing";
    private static final String KEY_NETWORK_ASSISTANT = "app_name_na";
    private static final String NFC_CATEGORY_TITLE = "nfc_category_title";
    private static final String NFC_PAYMENT_SETTINGS_TITLE = "nfc_payment_settings_title";
    private static final String NFC_REPAIR_TITLE = "nfc_repair_title";
    private static final String NFC_SECURE_SETTINGS_TITLE = "nfc_secure_settings_title";
    private static final String NFC_SE_ROUTE_TITLE = "nfc_se_route_title";
    private static final String NFC_TOGGLE_TITLE = "nfc_quick_toggle_title";
    private static final String SCREEN_PROJECTION = "screen_projection";
    private static final String SCREEN_PROJECTION_EXAMPLE_GAME_TITLE = "screen_projection_example_game_title";
    private static final String SEARCH_ANDROID_AUTO_TITLE = "search_android_auto";
    private static final String SETTINGS_CATEGORY_IA_DEVICE = "com.android.settings.category.ia.device";
    private static final String SHOW_CONNECTED_DEVICES_TITLE = "show_connected_devices_title";
    public static final String TAG = "WirelessSettingsTree";
    private static final String TETHER_DEVICES_MAX_NUMBER = "tether_devices_max_number";
    private static final String TETHER_USE_WIFI_SIX_STANDARD = "tether_use_wifi6_title";
    private static final String UCAR_SCREEN_PROJECTION_TITLE = "ucar_screen_projection_title";
    private static final String UWB_SETTINGS_TITLE = "launch_smarthome";
    private static final String VENDOR_QCOM = "qcom";
    private static final String VPN_SETTINGS = "vpn_settings_title";
    public static final String WFD_SETTINGS_TITLE = "wfd_settings_title";
    private static final String WIFI_TETHER_SETTINGS = "wifi_tether_settings_title";
    private static final String XIAOMI_TRANSFER = "xiaomi_transfer";
    private static HashMap<String, String> sTileMapCache = new HashMap<>();

    /* loaded from: classes2.dex */
    private interface Android_Auto {
        public static final String CLASS_NAME_DEFAULTSETTINGS = "com.google.android.projection.gearhead.companion.settings.DefaultSettingsActivity";
        public static final String CLASS_NAME_STUBSETTINGS = "com.google.android.apps.auto.components.app.stubapp.StubSettingsActivity";
        public static final String DEFAULT_RESOURCE = "com.google.android.projection.gearhead/com.google.android.projection.gearhead.companion.settings.DefaultSettingsActivity";
        public static final String PACKAGE_NAME = "com.google.android.projection.gearhead";
        public static final String STUB_RESOURCE = "com.google.android.projection.gearhead/com.google.android.apps.auto.components.app.stubapp.StubSettingsActivity";
    }

    /* loaded from: classes2.dex */
    private interface Chrome_book {
        public static final String CLASS_NAME_STUBSETTINGS = "com.google.android.gms.auth.proximity.multidevice.SettingsActivity";
        public static final String PACKAGE_NAME = "com.google.android.gms";
        public static final String RESOURCE = "com.google.android.gms/com.google.android.gms.auth.proximity.multidevice.SettingsActivity";
    }

    /* loaded from: classes2.dex */
    private interface Nearby_Sharing {
        public static final String CLASS_NAME_STUBSETTINGS = "com.google.android.gms.nearby.sharing.SettingsActivityAlias";
        public static final String PACKAGE_NAME = "com.google.android.gms";
        public static final String RESOURCE = "com.google.android.gms/com.google.android.gms.nearby.sharing.SettingsActivityAlias";
    }

    protected WirelessSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    private void addSearchItem(String str, String str2) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("resource", str);
            jSONObject.put("title", str2);
            if (str.equals(Android_Auto.STUB_RESOURCE)) {
                jSONObject.put(PaymentManager.KEY_INTENT, new TinyIntent().setClassName(Android_Auto.PACKAGE_NAME, Android_Auto.CLASS_NAME_STUBSETTINGS).toJSONObject());
                jSONObject.put("keywords", SEARCH_ANDROID_AUTO_TITLE);
            } else if (str.equals(Android_Auto.DEFAULT_RESOURCE)) {
                jSONObject.put(PaymentManager.KEY_INTENT, new TinyIntent().setClassName(Android_Auto.PACKAGE_NAME, Android_Auto.CLASS_NAME_DEFAULTSETTINGS).toJSONObject());
                jSONObject.put("keywords", SEARCH_ANDROID_AUTO_TITLE);
            } else if (str.equals(Chrome_book.RESOURCE)) {
                jSONObject.put(PaymentManager.KEY_INTENT, new TinyIntent().setClassName("com.google.android.gms", Chrome_book.CLASS_NAME_STUBSETTINGS).toJSONObject());
            } else if (str.equals(Nearby_Sharing.RESOURCE)) {
                jSONObject.put(PaymentManager.KEY_INTENT, new TinyIntent().setClassName("com.google.android.gms", Nearby_Sharing.CLASS_NAME_STUBSETTINGS).toJSONObject());
            }
            addSon(SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addTilesToMapCache() {
        DashboardCategory tilesForCategory = FeatureFactory.getFactory(((SettingsTree) this).mContext).getDashboardFeatureProvider(((SettingsTree) this).mContext).getTilesForCategory(SETTINGS_CATEGORY_IA_DEVICE);
        if (tilesForCategory != null) {
            for (Tile tile : tilesForCategory.getTiles()) {
                String valueOf = String.valueOf(tile.getTitle(((SettingsTree) this).mContext));
                String str = tile.getPackageName() + "/" + tile.getComponentName();
                Log.i(TAG, "Tile title: " + valueOf);
                Log.i(TAG, "Tile resource: " + str);
                sTileMapCache.put(str, valueOf);
                addSearchItem(str, valueOf);
            }
        }
    }

    private boolean isVpnInvisibleOnSecondaryPage() {
        return new VpnManager(((SettingsTree) this).mContext).getVpnNumbers() > 0 || RestrictionsHelper.hasRestriction(((SettingsTree) this).mContext, "disallow_vpn");
    }

    private boolean isWifiTetherInvisibleOnSecondaryPage() {
        return SettingsFeatures.getWifiTetherPlacement(((SettingsTree) this).mContext) != 2 || ((TetheringManager) ((SettingsTree) this).mContext.getSystemService("tethering")).getTetherableWifiRegexs().length == 0 || Utils.isMonkeyRunning();
    }

    public Intent getIntent() {
        String columnValue = getColumnValue("resource");
        if (WFD_SETTINGS_TITLE.equals(columnValue)) {
            Intent intent = new Intent("miui.intent.action.MIPLAY");
            if (((SettingsTree) this).mContext.getPackageManager().resolveActivity(intent, SearchUpdater.GOOGLE) != null) {
                return intent;
            }
        } else if (UCAR_SCREEN_PROJECTION_TITLE.equals(columnValue) && MiuiUtils.isSupportUcarSettings(((SettingsTree) this).mContext) && ((SettingsTree) this).mContext.getResources().getBoolean(R.bool.config_show_ucar_projection_screen)) {
            return MiuiUtils.buildUcarSettingsIntent();
        }
        return super.getIntent();
    }

    public LinkedList<SettingsTree> getSons() {
        HashMap<String, String> hashMap;
        if ("connection_and_sharing".equals(getColumnValue("resource")) && ((hashMap = sTileMapCache) == null || hashMap.size() == 0)) {
            try {
                addTilesToMapCache();
            } catch (Exception e) {
                Log.e(TAG, "connection_and_sharinginitialize error: " + e.getMessage());
            }
        }
        return super.getSons();
    }

    protected int getStatus() {
        String columnValue = getColumnValue("category_origin");
        if (NFC_CATEGORY_TITLE.equals(columnValue) && (NfcAdapter.getDefaultAdapter(((SettingsTree) this).mContext) == null || SettingsFeatures.isNeedShowMiuiNFC())) {
            return 0;
        }
        String columnValue2 = getColumnValue("resource");
        if ("usb_tethering_button_text".equals(columnValue2)) {
            if (((TetheringManager) ((SettingsTree) this).mContext.getSystemService("tethering")).getTetherableUsbRegexs().length == 0) {
                return 0;
            }
        } else if ("bluetooth_tether_checkbox_text".equals(columnValue2)) {
            if (((TetheringManager) ((SettingsTree) this).mContext.getSystemService("tethering")).getTetherableBluetoothRegexs().length == 0) {
                return 0;
            }
        } else if (NFC_TOGGLE_TITLE.equals(columnValue2)) {
            if (NfcAdapter.getDefaultAdapter(((SettingsTree) this).mContext) == null) {
                return 0;
            }
            if (!TextUtils.equals(columnValue, NFC_CATEGORY_TITLE) && !SettingsFeatures.isNeedShowMiuiNFC()) {
                return 0;
            }
        } else if (ANDROID_BEAM_SETTINGS_TITLE.equals(columnValue2)) {
            if (NfcAdapter.getDefaultAdapter(((SettingsTree) this).mContext) == null || !((SettingsTree) this).mContext.getPackageManager().hasSystemFeature("android.sofware.nfc.beam")) {
                return 0;
            }
        } else if (NFC_REPAIR_TITLE.equals(columnValue2)) {
            if (!SettingsFeatures.hasNfcRepairFeature(((SettingsTree) this).mContext)) {
                return 0;
            }
        } else if ("nfc_dnd_mode_title".equals(columnValue2)) {
            if (!SettingsFeatures.hasNfcDispatchOptimFeature(((SettingsTree) this).mContext)) {
                return 0;
            }
        } else if (NFC_PAYMENT_SETTINGS_TITLE.equals(columnValue2)) {
            NfcAdapter defaultAdapter = NfcAdapter.getDefaultAdapter(((SettingsTree) this).mContext);
            if (defaultAdapter == null || RegionUtils.IS_MEXICO_TELCEL || SearchUtils.isEmpty(CardEmulation.getInstance(defaultAdapter).getServices("payment")) || !((SettingsTree) this).mContext.getPackageManager().hasSystemFeature("android.hardware.nfc")) {
                return 0;
            }
        } else if (WIFI_TETHER_SETTINGS.equals(columnValue2) && isWifiTetherInvisibleOnSecondaryPage()) {
            return 0;
        } else {
            if (VPN_SETTINGS.equals(columnValue2) && isVpnInvisibleOnSecondaryPage()) {
                return 0;
            }
            if (SCREEN_PROJECTION_EXAMPLE_GAME_TITLE.equals(columnValue2) && UserHandle.myUserId() != 0) {
                return 0;
            }
            if (XIAOMI_TRANSFER.equals(columnValue2) && !SettingsFeatures.isNeedShowMishare(((SettingsTree) this).mContext)) {
                return 0;
            }
            if (SCREEN_PROJECTION.equals(columnValue2) && (ScreenProjectionController.isNeedRemoveScreenProjection() || ScreenProjectionController.hasDecoupleMiLink(((SettingsTree) this).mContext))) {
                return 0;
            }
            if (WFD_SETTINGS_TITLE.equals(columnValue2) && !Build.IS_INTERNATIONAL_BUILD && !ScreenProjectionController.isNeedRemoveScreenProjection()) {
                return 0;
            }
            if (TETHER_USE_WIFI_SIX_STANDARD.equals(columnValue2) && !((SettingsTree) this).mContext.getResources().getBoolean(R.bool.config_show_softap_wifi6)) {
                return 0;
            }
            if (UWB_SETTINGS_TITLE.equals(columnValue2) && !MiuiUtils.isUWBSupport(((SettingsTree) this).mContext)) {
                return 0;
            }
        }
        if (!UCAR_SCREEN_PROJECTION_TITLE.equals(columnValue2) || (MiuiUtils.isSupportUcarSettings(((SettingsTree) this).mContext) && ((SettingsTree) this).mContext.getResources().getBoolean(R.bool.config_show_ucar_projection_screen))) {
            return super.getStatus();
        }
        return 0;
    }

    protected String getTitle(boolean z) {
        String str;
        String columnValue = getColumnValue("resource");
        HashMap<String, String> hashMap = sTileMapCache;
        return (hashMap == null || !hashMap.containsKey(columnValue) || (str = sTileMapCache.get(columnValue)) == null) ? super.getTitle(z) : str;
    }

    public boolean initialize() {
        String columnValue = getColumnValue("resource");
        if (NFC_SE_ROUTE_TITLE.equals(columnValue)) {
            if (!FeatureParser.getBoolean("support_se_route", false) || RegionUtils.IS_MEXICO_TELCEL) {
                return true;
            }
            if (!Build.IS_GLOBAL_BUILD && !"pisces".equals(android.os.Build.DEVICE)) {
                setColumnValue("resource", "nfc_se_wallet_title");
            }
        } else if (KEY_NETWORK_ASSISTANT.equals(columnValue)) {
            if (!Build.IS_TABLET) {
                return true;
            }
        } else if (BLOCK_LIST_TITLE.equals(columnValue)) {
            if (!VENDOR_QCOM.equals(FeatureParser.getString("vendor"))) {
                return true;
            }
        } else if ("tether_data_usage_limit".equals(columnValue) && Build.IS_TABLET) {
            return true;
        } else {
            if (NFC_SECURE_SETTINGS_TITLE.equals(columnValue) && (!NfcAdapter.getDefaultAdapter(((SettingsTree) this).mContext).isSecureNfcSupported() || RegionUtils.IS_MEXICO_TELCEL)) {
                return true;
            }
        }
        if (!UCAR_SCREEN_PROJECTION_TITLE.equals(columnValue) || MiuiUtils.isSupportUcarSettings(((SettingsTree) this).mContext)) {
            if (!BLOCK_LIST_CONNECTED_DEVICES.equals(getColumnValue("category_origin")) || VENDOR_QCOM.equals(FeatureParser.getString("vendor"))) {
                return super.initialize();
            }
            return true;
        }
        return true;
    }
}
