package com.android.settings.search.tree;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.TetheringManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.applications.XSpaceSettingsController;
import com.android.settings.search.FunctionColumns;
import com.android.settings.usagestats.utils.CommonUtils;
import com.android.settings.utils.HomeListUtils;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.vpn2.VpnManager;
import com.android.settingslib.search.SettingsTree;
import com.android.settingslib.search.TinyIntent;
import com.miui.enterprise.RestrictionsHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import miui.content.res.ThemeResources;
import miui.os.Build;
import miui.payment.PaymentManager;
import miui.provider.ExtraTelephony;
import miui.util.FeatureParser;
import miui.yellowpage.YellowPageStatistic;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class MiuiSettingsTree extends SettingsTree {
    private static final String AI_TOUCH = "ai_button_title";
    public static final String AOD_NOTIFICATION_STYLE = "aod_notification_style";
    private static final String APP_TIMER = "usage_state_app_timer";
    private static final String BLOCK_LIST_CONNECTED_DEVICES = "block_list_connected_devices";
    private static final String BLOCK_LIST_TITLE = "block_list_title";
    private static final Map<String, String> CATEGORY_MAP = new HashMap<String, String>() { // from class: com.android.settings.search.tree.MiuiSettingsTree.1
        {
            put("com.android.settings.category.wireless", "ic_google_settings");
            put("com.android.settings.category.device", "ic_google_settings");
            put("com.android.settings.category.personal", "ic_google_settings");
            put("com.android.settings.category.system", "ic_google_settings");
        }
    };
    private static final String COMMON_ACTIVITY_CONTROLS_SETTINGS_TITLE = "common_activity_controls_settings_title";
    private static final String COMMON_ADS_SETTINGS_TITLE = "common_ads_settings_title";
    private static final String COMMON_AUTOFILL_SETTINGS_TITLE = "common_autofill_settings_title";
    private static final String COMMON_USAGE_AND_DIAGNOSTICS = "common_usage_and_diagnostics";
    private static final String DEFAULT_LAUNCHER_TITLE = "default_launcher_title";
    private static final String EMERGENCY_SOS_TITLE = "emergency_sos_title";
    private static final String EXTRA_CATEGORY_KEY = "com.android.settings.category";
    private static final String EXTRA_SETTINGS_ACTION = "com.android.settings.action.EXTRA_SETTINGS";
    private static final String FEEDBACK_SETTINGS_TITLE = "feedback_settings";
    private static final String HOME_TITLE = "home_title";
    private static final String LAUNCHER_ICON_MANAGEMENT = "launcher_icon_management";
    private static final String LOCATION_SETTINGS_LOCATION_HISTORY_SETTINGS_APP_TITLE = "location_settings_location_history_settings_app_title";
    private static final String LOCATION_SETTINGS_TITLE = "location_settings_title";
    private static final String MAGIC_WINDOW = "magic_window_name";
    private static final String MI_ACCOUNT_LOGO = "xiaomi_account";
    private static final String MI_SERVICE = "mi_service";
    private static final String PRIVACY_DASHBOARD_TITLE = "privacy_dashboard_title";
    private static List<String> PRIVACY_RESOURCE_LIST = null;
    private static final String SAFE_INSTALL_MODE_SETTINGS = "safe_install_mode_settings";
    private static final String SHOW_CONNECTED_DEVICES_TITLE = "show_connected_devices_title";
    private static final String TAG = "MiuiSettingsTree";
    private static final String TETHER_DEVICES_MAX_NUMBER = "tether_devices_max_number";
    private static final String TETHER_USE_WIFI_SIX_STANDARD = "tether_use_wifi6_title";
    private static final String THEMEMANAGER_PACKAGENAME = "com.android.thememanager";
    private static final String THEMEMMANAGER_WALLPAPER_TARGET_CLASSNAME = "com.android.thememanager.settings.WallpaperSettingsActivity";
    private static final String VENDOR_QCOM = "qcom";
    private static final String VIP_SERVICE_SETTINGS_TITLE = "vip_service_settings";
    private static final String VPN_SETTINGS = "vpn_settings_title";
    private static final String WALLET_HEADER_TITLE = "wallet_header_title";
    private static final String WIFI_TETHER_SETTINGS = "wifi_tether_settings_title";
    private static final String XIAOMI_ACCOUNT = "xiaomi_account";
    private static final String XIAOMI_ACCOUNT_INFO = "unlogin_account_title";
    private static final String XSPACE_TITLE = "xspace";

    static {
        ArrayList arrayList = new ArrayList();
        PRIVACY_RESOURCE_LIST = arrayList;
        arrayList.add(COMMON_AUTOFILL_SETTINGS_TITLE);
        PRIVACY_RESOURCE_LIST.add(LOCATION_SETTINGS_LOCATION_HISTORY_SETTINGS_APP_TITLE);
        PRIVACY_RESOURCE_LIST.add(COMMON_ACTIVITY_CONTROLS_SETTINGS_TITLE);
        PRIVACY_RESOURCE_LIST.add(COMMON_ADS_SETTINGS_TITLE);
        PRIVACY_RESOURCE_LIST.add(COMMON_USAGE_AND_DIAGNOSTICS);
    }

    protected MiuiSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    private void addPrivacyDashboardSubItems() {
        for (String str : PRIVACY_RESOURCE_LIST) {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("resource", str);
                jSONObject.put(ExtraTelephony.UnderstandInfo.CLASS, MiuiSettingsTree.class.getName());
                addSon(SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this));
            } catch (JSONException e) {
                Log.e(TAG, "addPrivacyDashboardSearchSubItems json: ", e);
            }
        }
    }

    private void ensureAddAlexaSearch() {
        Intent amazonAlexIntent = HomeListUtils.getAmazonAlexIntent();
        if (HomeListUtils.shouldAddAmazonAlex(((SettingsTree) this).mContext, amazonAlexIntent)) {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("temporary", true);
                jSONObject.put("title", HomeListUtils.getAlexaAppName(((SettingsTree) this).mContext));
                jSONObject.put("icon", "ic_alexa_widget_icon");
                jSONObject.put(PaymentManager.KEY_INTENT, new TinyIntent(amazonAlexIntent).toJSONObject());
                jSONObject.put(ExtraTelephony.UnderstandInfo.CLASS, GeneratorTitleSettingsTree.class.getName());
                addSon(SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this));
            } catch (JSONException unused) {
            }
        }
    }

    private void ensureAddWellbeingSon(LinkedList<SettingsTree> linkedList) {
        if (Build.IS_INTERNATIONAL_BUILD) {
            Intent wellbeingIntent = HomeListUtils.getWellbeingIntent();
            if (MiuiUtils.getInstance().canFindActivity(((SettingsTree) this).mContext, wellbeingIntent)) {
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("temporary", true);
                    jSONObject.put("title", ((SettingsTree) this).mContext.getResources().getString(R.string.wellbing_title));
                    jSONObject.put("icon", "ic_google_wellbeing");
                    jSONObject.put(PaymentManager.KEY_INTENT, new TinyIntent(wellbeingIntent).toJSONObject());
                    jSONObject.put(ExtraTelephony.UnderstandInfo.CLASS, GeneratorTitleSettingsTree.class.getName());
                    jSONObject.put(FunctionColumns.SUMMARY, HomeListUtils.getAppName(((SettingsTree) this).mContext, "com.google.android.apps.wellbeing"));
                    replaceAppTimerItem(linkedList, SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this));
                } catch (JSONException unused) {
                }
            }
        }
    }

    private int getSpecialFeatureIndex(LinkedList<SettingsTree> linkedList) {
        for (int i = 0; i < linkedList.size(); i++) {
            if ("miui_special_feature".equals(linkedList.get(i).getColumnValue("resource"))) {
                return i;
            }
        }
        return -1;
    }

    private String getTitleByResourceName(Context context, String str) {
        int identifier;
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        try {
            Resources resources = context.createPackageContext("com.google.android.gms", 0).getResources();
            return (resources == null || (identifier = resources.getIdentifier(str, "LEMON_transformed_from_string", "com.google.android.gms")) == 0) ? "" : resources.getString(identifier);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean isSupportAod(Context context) {
        int identifier = context.getResources().getIdentifier("config_dozeAlwaysOnDisplayAvailable", "bool", ThemeResources.FRAMEWORK_PACKAGE);
        if (identifier > 0) {
            return context.getResources().getBoolean(identifier);
        }
        return false;
    }

    private boolean isVpnInvisibleOnHomePage() {
        return new VpnManager(((SettingsTree) this).mContext).getVpnNumbers() < 1 || RestrictionsHelper.hasRestriction(((SettingsTree) this).mContext, "disallow_vpn");
    }

    private boolean isWifiTetherInvisibleOnHomePage() {
        return SettingsFeatures.getWifiTetherPlacement(((SettingsTree) this).mContext) != 1 || ((TetheringManager) ((SettingsTree) this).mContext.getSystemService("tethering")).getTetherableWifiRegexs().length == 0 || Utils.isMonkeyRunning();
    }

    private void replaceAppTimerItem(LinkedList<SettingsTree> linkedList, SettingsTree settingsTree) {
        int specialFeatureIndex = getSpecialFeatureIndex(linkedList);
        if (specialFeatureIndex > 0) {
            addSon(specialFeatureIndex - 1, settingsTree);
        } else if (specialFeatureIndex == 0) {
            addSon(0, settingsTree);
        } else {
            addSon(settingsTree);
        }
    }

    public String getIcon() {
        return (Build.IS_INTERNATIONAL_BUILD || !XIAOMI_ACCOUNT_INFO.equals(getColumnValue("resource"))) ? super.getIcon() : "xiaomi_account";
    }

    public Intent getIntent() {
        String columnValue = getColumnValue("resource");
        if (HOME_TITLE.equals(columnValue)) {
            return MiuiUtils.buildLauncherSettingsIntent();
        }
        if (APP_TIMER.equals(columnValue)) {
            Intent intentTimerIntent = CommonUtils.getIntentTimerIntent();
            if (CommonUtils.hasIndependentTimer(((SettingsTree) this).mContext, intentTimerIntent)) {
                return intentTimerIntent;
            }
        } else if ("wallpaper_settings_title".equals(columnValue)) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(THEMEMANAGER_PACKAGENAME, THEMEMMANAGER_WALLPAPER_TARGET_CLASSNAME));
            if (MiuiUtils.getInstance().canFindActivity(((SettingsTree) this).mContext, intent)) {
                return intent;
            }
        } else if ("personalize_title".equals(columnValue)) {
            Intent intent2 = new Intent();
            intent2.setAction("android.intent.action.VIEW");
            intent2.addCategory("android.intent.category.DEFAULT");
            intent2.setData(Uri.parse("theme://zhuti.xiaomi.com/personalize?miback=true&miref=settings"));
            if (MiuiUtils.getInstance().canFindActivity(((SettingsTree) this).mContext, intent2)) {
                return intent2;
            }
        } else if (WALLET_HEADER_TITLE.equals(columnValue) && !MiuiUtils.needRemoveWalletEntrance(((SettingsTree) this).mContext)) {
            Intent intent3 = new Intent("android.intent.action.VIEW");
            intent3.setPackage("com.mipay.wallet");
            intent3.addCategory("com.mipay.wallet.MIPAYINFO");
            intent3.setData(Uri.parse("mipay://walletapp?id=mipay.info"));
            return intent3;
        } else if (SAFE_INSTALL_MODE_SETTINGS.equals(columnValue)) {
            Intent intent4 = new Intent();
            intent4.setClassName("com.miui.packageinstaller", "com.miui.packageInstaller.ui.secure.SecureModeActivity");
            intent4.putExtra("safe_mode_ref", "setting_entry");
            intent4.putExtra("safe_mode_type", "setting");
            return intent4;
        }
        return super.getIntent();
    }

    protected String getPath(boolean z, boolean z2) {
        return getParent() == null ? "" : super.getPath(z, z2);
    }

    public LinkedList<SettingsTree> getSons() {
        ActivityInfo activityInfo;
        Bundle bundle;
        if (getParent() == null) {
            LinkedList<SettingsTree> sons = super.getSons();
            for (int size = sons.size() - 1; size >= 0; size--) {
                SettingsTree settingsTree = sons.get(size);
                if (Boolean.parseBoolean(settingsTree.getColumnValue("temporary"))) {
                    settingsTree.removeSelf();
                }
            }
            if (Build.IS_GLOBAL_BUILD) {
                int i = 0;
                int size2 = sons.size() - 1;
                while (true) {
                    if (size2 < 0) {
                        break;
                    } else if ("other_advanced_settings".equals(sons.get(size2).getColumnValue("resource"))) {
                        i = size2;
                        break;
                    } else {
                        size2--;
                    }
                }
                UserManager userManager = UserManager.get(((SettingsTree) this).mContext);
                PackageManager packageManager = ((SettingsTree) this).mContext.getPackageManager();
                Intent intent = new Intent(EXTRA_SETTINGS_ACTION);
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("temporary", true);
                    jSONObject.put(ExtraTelephony.UnderstandInfo.CLASS, GoogleSettingsTree.class.getName());
                    for (UserHandle userHandle : userManager.getUserProfiles()) {
                        if (userHandle.getIdentifier() != 999) {
                            for (ResolveInfo resolveInfo : packageManager.queryIntentActivitiesAsUser(intent, 128, userHandle.getIdentifier())) {
                                if (resolveInfo.system && (bundle = (activityInfo = resolveInfo.activityInfo).metaData) != null && bundle.containsKey(EXTRA_CATEGORY_KEY)) {
                                    String string = bundle.getString(EXTRA_CATEGORY_KEY);
                                    Map<String, String> map = CATEGORY_MAP;
                                    if (map.get(string) != null) {
                                        jSONObject.put("title", resolveInfo.loadLabel(packageManager).toString());
                                        jSONObject.put("icon", map.get(string));
                                        jSONObject.put(YellowPageStatistic.Display.CATEGORY, "system_and_device_section_title");
                                        jSONObject.put(FunctionColumns.PACKAGE, activityInfo.packageName);
                                        jSONObject.put("activityName", activityInfo.name);
                                        int i2 = i + 1;
                                        addSon(i, SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this));
                                        i = i2;
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException unused) {
                }
            }
            ensureAddWellbeingSon(sons);
            ensureAddAlexaSearch();
        }
        return super.getSons();
    }

    protected int getStatus() {
        String columnValue = getColumnValue("resource");
        if (!AI_TOUCH.equals(columnValue) || (MiuiUtils.isAiKeyExist(((SettingsTree) this).mContext) && FeatureParser.getBoolean("support_ai_task", false))) {
            if ("emergency_sos_title".equals(columnValue) && SettingsFeatures.IS_NEED_REMOVE_SOS) {
                return 0;
            }
            if (WIFI_TETHER_SETTINGS.equals(columnValue) && isWifiTetherInvisibleOnHomePage()) {
                return 0;
            }
            if (VPN_SETTINGS.equals(columnValue) && isVpnInvisibleOnHomePage()) {
                return 0;
            }
            if (HOME_TITLE.equals(columnValue) && MiuiUtils.isEasyMode(((SettingsTree) this).mContext)) {
                return 0;
            }
            if (!DEFAULT_LAUNCHER_TITLE.equals(columnValue) || MiuiUtils.existsJeejen(((SettingsTree) this).mContext)) {
                if ("xiaomi_account".equals(columnValue) && MiuiUtils.isDeviceManaged(((SettingsTree) this).mContext)) {
                    return 0;
                }
                if (!TETHER_USE_WIFI_SIX_STANDARD.equals(columnValue) || ((SettingsTree) this).mContext.getResources().getBoolean(R.bool.config_show_softap_wifi6)) {
                    if (XSPACE_TITLE.equals(columnValue) && XSpaceSettingsController.needHideXspace()) {
                        return 0;
                    }
                    if (!VIP_SERVICE_SETTINGS_TITLE.equals(columnValue) || SettingsFeatures.isVipServiceNeeded(((SettingsTree) this).mContext)) {
                        return super.getStatus() | (getParent() == null ? 4 : 0);
                    }
                    return 0;
                }
                return 0;
            }
            return 0;
        }
        return 0;
    }

    protected String getTitle(boolean z) {
        String columnValue = getColumnValue("resource");
        if (Build.IS_INTERNATIONAL_BUILD || !XIAOMI_ACCOUNT_INFO.equals(columnValue)) {
            if (SettingsFeatures.isVipServiceNeeded(((SettingsTree) this).mContext) && FEEDBACK_SETTINGS_TITLE.equals(columnValue)) {
                return ((SettingsTree) this).mContext.getResources().getString(R.string.bug_report_settings);
            }
            columnValue.hashCode();
            char c = 65535;
            switch (columnValue.hashCode()) {
                case -1226036971:
                    if (columnValue.equals(COMMON_AUTOFILL_SETTINGS_TITLE)) {
                        c = 0;
                        break;
                    }
                    break;
                case -540728961:
                    if (columnValue.equals(COMMON_ADS_SETTINGS_TITLE)) {
                        c = 1;
                        break;
                    }
                    break;
                case 817086089:
                    if (columnValue.equals(COMMON_ACTIVITY_CONTROLS_SETTINGS_TITLE)) {
                        c = 2;
                        break;
                    }
                    break;
                case 1341664289:
                    if (columnValue.equals(LOCATION_SETTINGS_LOCATION_HISTORY_SETTINGS_APP_TITLE)) {
                        c = 3;
                        break;
                    }
                    break;
                case 1847472466:
                    if (columnValue.equals(COMMON_USAGE_AND_DIAGNOSTICS)) {
                        c = 4;
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
                    return getTitleByResourceName(((SettingsTree) this).mContext, columnValue);
                default:
                    return super.getTitle(z);
            }
        }
        return ((SettingsTree) this).mContext.getResources().getString(R.string.xiaomi_account);
    }

    public boolean initialize() {
        String columnValue = getColumnValue("resource");
        if (!HOME_TITLE.equals(columnValue)) {
            if (DEFAULT_LAUNCHER_TITLE.equals(columnValue)) {
                if (!Build.IS_INTERNATIONAL_BUILD) {
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("resource", "oldman_mode_entry_name");
                    addSon(SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this));
                }
            } else if (MI_SERVICE.equals(columnValue) && (Build.IS_TABLET || Build.IS_INTERNATIONAL_BUILD)) {
                return true;
            } else {
                if ("theme_settings_title".equals(columnValue) && SettingsFeatures.IS_NEED_REMOVE_THEME) {
                    return true;
                }
                if ("xiaomi_account".equals(columnValue) && !Build.IS_INTERNATIONAL_BUILD) {
                    return true;
                }
                if (XIAOMI_ACCOUNT_INFO.equals(columnValue) && Build.IS_INTERNATIONAL_BUILD) {
                    return true;
                }
                if (APP_TIMER.equals(columnValue) && MiuiUtils.shouldDisableAppTimer(((SettingsTree) this).mContext)) {
                    return true;
                }
                if (BLOCK_LIST_TITLE.equals(columnValue) || TETHER_DEVICES_MAX_NUMBER.equals(columnValue) || SHOW_CONNECTED_DEVICES_TITLE.equals(columnValue)) {
                    if (!VENDOR_QCOM.equals(FeatureParser.getString("vendor"))) {
                        return true;
                    }
                } else if ("power_usage_summary_title".equals(columnValue) && !Build.IS_TABLET) {
                    return true;
                } else {
                    if ("power_usage_summary_title_new".equals(columnValue) && Build.IS_TABLET) {
                        return true;
                    }
                    if ("location_settings_title".equals(columnValue)) {
                        if (!Build.IS_INTERNATIONAL_BUILD) {
                            return true;
                        }
                    } else if (PRIVACY_DASHBOARD_TITLE.equals(columnValue)) {
                        if (!Build.IS_INTERNATIONAL_BUILD) {
                            return true;
                        }
                        addPrivacyDashboardSubItems();
                    } else if ("theme_settings_title".equals(columnValue) && !Build.IS_INTERNATIONAL_BUILD) {
                        return true;
                    } else {
                        if ("wallpaper_settings_title".equals(columnValue) && !MiuiUtils.needRemovePersonalize(((SettingsTree) this).mContext)) {
                            return true;
                        }
                        if ("personalize_title".equals(columnValue) && MiuiUtils.needRemovePersonalize(((SettingsTree) this).mContext)) {
                            return true;
                        }
                        if (WALLET_HEADER_TITLE.equals(columnValue) && MiuiUtils.needRemoveWalletEntrance(((SettingsTree) this).mContext)) {
                            return true;
                        }
                        if ("already_delete_system_app".equals(columnValue) && !SettingsFeatures.isSupportUninstallSysApp(((SettingsTree) this).mContext)) {
                            return true;
                        }
                        if (MAGIC_WINDOW.equals(columnValue) && !SettingsFeatures.isSupportMagicWindow(((SettingsTree) this).mContext)) {
                            return true;
                        }
                        if ("control_center_style".equals(columnValue)) {
                            setColumnValue("keywords", "search_control_center_style");
                        } else if (SecuritySettingsTree.SECURITY_CENTER_RESOURCE.equals(columnValue) && !MiuiUtils.isSupportSecuritySettings(((SettingsTree) this).mContext)) {
                            return true;
                        } else {
                            if (TextUtils.equals(LAUNCHER_ICON_MANAGEMENT, columnValue) && TextUtils.equals(android.os.Build.DEVICE, "cetus")) {
                                return true;
                            }
                            if (TextUtils.equals(SAFE_INSTALL_MODE_SETTINGS, columnValue) && Build.IS_INTERNATIONAL_BUILD) {
                                return true;
                            }
                        }
                    }
                }
            }
            if (!BLOCK_LIST_CONNECTED_DEVICES.equals(getColumnValue("category_origin")) && !VENDOR_QCOM.equals(FeatureParser.getString("vendor"))) {
                return true;
            }
            if (!AOD_NOTIFICATION_STYLE.equals(columnValue) && !isSupportAod(((SettingsTree) this).mContext)) {
                return true;
            }
        }
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put("resource", DEFAULT_LAUNCHER_TITLE);
        addSon(SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject2, this));
        if (!BLOCK_LIST_CONNECTED_DEVICES.equals(getColumnValue("category_origin"))) {
        }
        return !AOD_NOTIFICATION_STYLE.equals(columnValue) ? super.initialize() : super.initialize();
    }
}
