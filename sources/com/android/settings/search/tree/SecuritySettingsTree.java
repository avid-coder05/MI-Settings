package com.android.settings.search.tree;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.miui.AppOpsUtils;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.telephony.CarrierConfigManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.AgpsSettings;
import com.android.settings.FakeCellSettings;
import com.android.settings.MiuiUtils;
import com.android.settings.applications.specialaccess.MoreSpecialAccessPreferenceController;
import com.android.settings.applications.specialaccess.interactacrossprofiles.InteractAcrossProfilesController;
import com.android.settings.enterprise.FinancedPrivacyPreferenceController;
import com.android.settings.location.LocationForWorkPreferenceController;
import com.android.settings.location.MiuiModemLocationSwitchController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.FunctionColumns;
import com.android.settings.security.ChangeProfileScreenLockPreferenceController;
import com.android.settings.security.LockUnificationPreferenceController;
import com.android.settings.security.SecuritySettingsController;
import com.android.settings.security.SimLockPreferenceController;
import com.android.settings.security.VirtualSimUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.drawer.DashboardCategory;
import com.android.settingslib.drawer.Tile;
import com.android.settingslib.location.SettingsInjector;
import com.android.settingslib.search.SearchUtils;
import com.android.settingslib.search.SettingsTree;
import com.android.settingslib.search.TinyIntent;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import miui.os.Build;
import miui.payment.PaymentManager;
import miui.provider.ExtraTelephony;
import miui.telephony.SubscriptionInfo;
import miui.telephony.SubscriptionManager;
import miui.telephony.TelephonyManagerEx;
import miui.util.FeatureParser;
import miui.yellowpage.YellowPageStatistic;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class SecuritySettingsTree extends SettingsTree {
    private static final String AGPS_ROAMING = "agps_roaming";
    private static final String ASSISTED_GPS = "assisted_gps";
    public static final String COM_GOOGLE_ANDROID_GMS = "com.google.android.gms";
    public static final String COM_XIAOMI_JOYOSE = "com.xiaomi.joyose";
    private static final String CREDENTIALS_INSTALL = "credentials_install";
    private static final String DIRECTORY_ACCESS = "directory_access";
    public static final String EXTRA_LICENSE_TYPE = "android.intent.extra.LICENSE_TYPE";
    public static final String FORBIDDEN_FAKECELL = "forbidden_fakecell";
    private static final String HIGH_POWER_APPS = "high_power_apps";
    private static final String HP_LOCATION = "xiaomi_hp_location";
    public static final String IC_LOCATION_INFO_SETTINGS = "ic_location_info_settings";
    private static final String INSTALL_OTHER_APPS = "install_other_apps";
    private static final String LOCATION_AGPS_PARAMS_SETTINGS_TITLE = "location_agps_params_settings_title";
    private static final String LOCATION_APP_LEVEL_PERMISSIONS = "location_app_level_permissions";
    private static final String LOCATION_MODE_SCREEN_TITLE = "location_mode_screen_title";
    public static final String LOCATION_RECENT_LOCATION_REQUESTS_SEE_ALL = "location_recent_location_requests_see_all";
    public static final String LOCATION_SETTINGS_TITLE = "location_settings_title";
    public static final String MANAGE_FAKECELL_SETTINGS = "manage_fakecell_settings";
    public static final String PRIVACY_PROTECTION_TITLE = "privacy_protection_title";
    public static final String SECURITY_CENTER_PACKAGE_NAME = "com.miui.securitycenter";
    public static final String SECURITY_CENTER_RESOURCE = "security_center_title";
    public static final String SECURITY_PRIVACY_SETTINGS_RESOURCE = "security_privacy_settings_title";
    private static final String SIM_LOCK_SETTINGS_CATEGORY = "sim_lock_settings_category";
    public static final String SIM_LOCK_SETTINGS_TITLE = "sim_lock_settings_title";
    public static final String SIM_PIN_CHANGE = "sim_pin_change";
    public static final String SIM_PIN_TOGGLE = "sim_pin_toggle";
    public static final String SIM_RADIO_OFF = "sim_radio_off";
    public static final String SLOT_ID = "slotId";
    public static final String SPECIAL_ACCESS_RESOURCE = "special_access";
    private static final String SYSTEM_ALERT_WINDOW_SETTINGS = "system_alert_window_settings";
    private static final String TAG = "SecuritySettingsTree";
    private static final String USAGE_ACCESS = "usage_access";
    public static final String WRITE_SETTINGS = "write_settings";
    private String mTitle;

    protected SecuritySettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
        this.mTitle = jSONObject.optString("title");
    }

    private void addSecurityPrivacySubItems() {
        List<Tile> tiles;
        DashboardCategory tilesForCategory = FeatureFactory.getFactory(((SettingsTree) this).mContext).getDashboardFeatureProvider(((SettingsTree) this).mContext).getTilesForCategory("com.android.settings.category.ia.security");
        if (tilesForCategory == null || (tiles = tilesForCategory.getTiles()) == null || tiles.isEmpty()) {
            return;
        }
        for (Tile tile : tiles) {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("title", tile.getTitle(((SettingsTree) this).mContext));
                jSONObject.put("temporary", true);
                jSONObject.put(ExtraTelephony.UnderstandInfo.CLASS, SecuritySettingsTree.class.getName());
                addSon(SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this));
            } catch (JSONException e) {
                Log.e(TAG, "addSecurityPrivacySubItems json: ", e);
            }
        }
    }

    private void addSon(String str) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("resource", str);
            addSon(SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this));
        } catch (JSONException unused) {
        }
    }

    private boolean hasXiaomiHpFeature() {
        return SystemProperties.getBoolean("persist.vendor.gnss.hpLocSetUI", false);
    }

    private static boolean isApplicationAvilible(Context context, String str) {
        List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(0);
        if (installedPackages != null) {
            for (int i = 0; i < installedPackages.size(); i++) {
                if (str.equals(installedPackages.get(i).packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isSimIccReady() {
        TelephonyManager telephonyManager = TelephonyManager.getDefault();
        List<SubscriptionInfo> activeSubscriptionInfoList = SubscriptionManager.getDefault().getActiveSubscriptionInfoList();
        if (activeSubscriptionInfoList != null) {
            Iterator<SubscriptionInfo> it = activeSubscriptionInfoList.iterator();
            while (it.hasNext()) {
                if (telephonyManager.hasIccCard(it.next().getSlotId())) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private static boolean isSimReady() {
        TelephonyManager telephonyManager = TelephonyManager.getDefault();
        List<SubscriptionInfo> activeSubscriptionInfoList = SubscriptionManager.getDefault().getActiveSubscriptionInfoList();
        if (activeSubscriptionInfoList != null) {
            Iterator<SubscriptionInfo> it = activeSubscriptionInfoList.iterator();
            while (it.hasNext()) {
                int simState = telephonyManager.getSimState(it.next().getSlotId());
                if (simState != 1 && simState != 0) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private boolean isZh() {
        return Locale.getDefault().toString().endsWith("zh_CN");
    }

    public String getIcon() {
        String columnValue = getColumnValue("resource");
        String icon = super.getIcon();
        return (SPECIAL_ACCESS_RESOURCE.equals(columnValue) || SPECIAL_ACCESS_RESOURCE.equals(getParent() != null ? getParent().getColumnValue("resource") : "")) ? "ic_privacy_protection" : (AppOpsUtils.isXOptMode() || !IC_LOCATION_INFO_SETTINGS.equals(icon)) ? (SECURITY_PRIVACY_SETTINGS_RESOURCE.equals(columnValue) && SecuritySettingsController.hasSecurityCenterSecureEntry()) ? "ic_security_center_settings" : (!LOCATION_RECENT_LOCATION_REQUESTS_SEE_ALL.equals(columnValue) || getParent() == null) ? icon : getParent().getIcon() : "ic_privacy_protection";
    }

    public Intent getIntent() {
        String stringExtra;
        String columnValue = getColumnValue("resource");
        if (LOCATION_APP_LEVEL_PERMISSIONS.equals(columnValue)) {
            if (AppOpsUtils.isXOptMode()) {
                Intent intent = new Intent("android.intent.action.MANAGE_PERMISSION_APPS");
                intent.putExtra("android.intent.extra.PERMISSION_NAME", "android.permission-group.LOCATION");
                return intent;
            }
            Intent intent2 = new Intent("com.miui.permission.single_item");
            intent2.setPackage(SECURITY_CENTER_PACKAGE_NAME);
            intent2.putExtra("permissionID", "32");
            return intent2;
        }
        if (SIM_PIN_TOGGLE.equals(columnValue) || SIM_PIN_CHANGE.equals(columnValue)) {
            Intent intent3 = getParent().getIntent();
            if (intent3 != null) {
                Intent intent4 = new Intent();
                intent4.setClassName("com.android.settings", "com.android.settings.Settings$IccLockSettingsActivity");
                if (intent3.hasExtra(SLOT_ID)) {
                    SubscriptionManager.putSlotIdExtra(intent4, Integer.valueOf(intent3.getStringExtra(SLOT_ID)).intValue());
                }
                return intent4;
            }
        } else if (SIM_RADIO_OFF.equals(columnValue)) {
            Intent intent5 = super.getIntent();
            if (intent5 != null && intent5.hasExtra(SLOT_ID)) {
                SubscriptionManager.putSlotIdExtra(intent5, Integer.valueOf(intent5.getStringExtra(SLOT_ID)).intValue());
                return intent5;
            }
        } else if ("user_certificate".equals(columnValue)) {
            Intent intent6 = new Intent("android.credentials.INSTALL");
            intent6.setClassName("com.android.certinstaller", "com.android.certinstaller.CertInstallerMain");
            intent6.putExtra("certificate_install_usage", "user");
            return intent6;
        } else if ("wifi_certificate".equals(columnValue)) {
            Intent intent7 = new Intent("android.credentials.INSTALL");
            intent7.setClassName("com.android.certinstaller", "com.android.certinstaller.CertInstallerMain");
            intent7.putExtra("certificate_install_usage", "wifi");
            return intent7;
        } else if ("change_wifi_state_title".equals(columnValue)) {
            Intent intent8 = new Intent();
            intent8.setClassName("com.android.settings", "com.android.settings.Settings$ChangeWifiStateActivity");
            intent8.putExtra("classname", "com.android.settings.Settings$ChangeWifiStateActivity");
            return intent8;
        } else if ("user_experience_open_url".equals(columnValue)) {
            Intent intent9 = super.getIntent();
            if (intent9 != null && (stringExtra = intent9.getStringExtra("android.intent.extra.LICENSE_TYPE")) != null && TextUtils.isDigitsOnly(stringExtra)) {
                intent9.putExtra("android.intent.extra.LICENSE_TYPE", Integer.parseInt(stringExtra));
            }
            return intent9;
        }
        return super.getIntent();
    }

    public Intent getIntentForStart() {
        String columnValue = getColumnValue("resource");
        if (!CREDENTIALS_INSTALL.equals(columnValue) && !"user_certificate".equals(columnValue) && !"wifi_certificate".equals(columnValue)) {
            return super.getIntentForStart();
        }
        return getIntent();
    }

    public String getMoreSecuritySettingsTitle() {
        int identifier;
        try {
            Resources resources = ((SettingsTree) this).mContext.createPackageContext(SECURITY_CENTER_PACKAGE_NAME, 0).getResources();
            return (resources == null || (identifier = resources.getIdentifier("sp_more_security_settings", "string", SECURITY_CENTER_PACKAGE_NAME)) == 0) ? "" : resources.getString(identifier);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    protected String getPath(boolean z, boolean z2) {
        String str;
        String str2;
        String columnValue = getColumnValue("resource");
        String string = SearchUtils.getString(getPackageContext(((SettingsTree) this).mContext), SECURITY_PRIVACY_SETTINGS_RESOURCE);
        String string2 = SearchUtils.getString(getPackageContext(((SettingsTree) this).mContext), SECURITY_CENTER_RESOURCE);
        if (SECURITY_PRIVACY_SETTINGS_RESOURCE.equals(columnValue) && SecuritySettingsController.hasSecurityCenterSecureEntry()) {
            return string2 + "/" + getMoreSecuritySettingsTitle();
        }
        String path = super.getPath(z, z2);
        String string3 = SearchUtils.getString(getPackageContext(((SettingsTree) this).mContext), SPECIAL_ACCESS_RESOURCE);
        String string4 = SearchUtils.getString(getPackageContext(((SettingsTree) this).mContext), PRIVACY_PROTECTION_TITLE);
        String string5 = SearchUtils.getString(getPackageContext(((SettingsTree) this).mContext), LOCATION_SETTINGS_TITLE);
        if (TextUtils.isEmpty(path)) {
            return path;
        }
        if (!path.contains(string3) && (AppOpsUtils.isXOptMode() || !path.contains(string5))) {
            if (path.contains(string) && SecuritySettingsController.hasSecurityCenterSecureEntry()) {
                return string2 + "/" + getMoreSecuritySettingsTitle() + "/" + getTitle(z);
            }
            return path;
        }
        String privacyManageTitle = getPrivacyManageTitle();
        if (TextUtils.isEmpty(privacyManageTitle)) {
            str = "";
        } else {
            str = privacyManageTitle + "/";
        }
        if (path.contains(string3)) {
            String title = getTitle(z);
            if (!title.equals(string3)) {
                title = string3 + "/" + title;
            }
            str2 = string4 + "/" + str + title;
        } else if (!path.contains(string5) || path.contains(string4)) {
            return path;
        } else {
            str2 = string4 + "/" + str + path;
        }
        return str2;
    }

    public String getPrivacyManageTitle() {
        try {
            Resources resources = ((SettingsTree) this).mContext.createPackageContext(SECURITY_CENTER_PACKAGE_NAME, 0).getResources();
            if (resources != null) {
                int identifier = Build.IS_INTERNATIONAL_BUILD ? resources.getIdentifier("privacy_manage_title", "string", SECURITY_CENTER_PACKAGE_NAME) : resources.getIdentifier("pp_privacy_protection", "string", SECURITY_CENTER_PACKAGE_NAME);
                return identifier != 0 ? resources.getString(identifier) : "";
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public LinkedList<SettingsTree> getSons() {
        int i;
        String columnValue = getColumnValue("resource");
        if (SECURITY_PRIVACY_SETTINGS_RESOURCE.equals(getColumnValue("resource")) && !SearchUtils.isSecondSpace() && MiuiUtils.getInstance().isMultiSimSupported()) {
            LinkedList sons = super.getSons();
            if (sons != null) {
                i = sons.size();
                for (int size = sons.size() - 1; size >= 0; size--) {
                    SettingsTree settingsTree = (SettingsTree) sons.get(size);
                    if (Boolean.parseBoolean(settingsTree.getColumnValue("temporary"))) {
                        settingsTree.removeSelf();
                        i--;
                    }
                    if (SIM_LOCK_SETTINGS_CATEGORY.equals(settingsTree.getColumnValue("resource"))) {
                        i = size;
                    }
                }
            } else {
                i = 0;
            }
            List<SubscriptionInfo> subscriptionInfoList = SubscriptionManager.getDefault().getSubscriptionInfoList();
            Iterator<SubscriptionInfo> it = subscriptionInfoList.iterator();
            while (it.hasNext()) {
                if (VirtualSimUtils.isVirtualSim(((SettingsTree) this).mContext, it.next().getSlotId())) {
                    it.remove();
                }
            }
            if (!subscriptionInfoList.isEmpty()) {
                Collections.sort(subscriptionInfoList, MiuiUtils.getInstance().getSubscriptionInfoComparable());
                for (int i2 = 0; i2 < subscriptionInfoList.size(); i2++) {
                    SubscriptionInfo subscriptionInfo = subscriptionInfoList.get(i2);
                    int slotId = subscriptionInfo.getSlotId();
                    JSONObject jSONObject = new JSONObject();
                    Intent intent = new Intent();
                    intent.setClassName("com.android.settings", "com.android.settings.Settings$IccLockSettingsActivity");
                    intent.putExtra(SLOT_ID, String.valueOf(slotId));
                    SubscriptionManager.putSlotIdExtra(intent, slotId);
                    TelephonyManagerEx telephonyManagerEx = TelephonyManagerEx.getDefault();
                    boolean z = (!telephonyManagerEx.isRadioOnForSlot(slotId) || telephonyManagerEx.getSimStateForSlot(slotId) == 1 || telephonyManagerEx.getSimStateForSlot(slotId) == 0) ? false : true;
                    try {
                        jSONObject.put(ExtraTelephony.UnderstandInfo.CLASS, SecuritySettingsTree.class.getName());
                        jSONObject.put("resource", SIM_RADIO_OFF);
                        if (z) {
                            jSONObject.put("title", subscriptionInfo.getDisplayName());
                        }
                        jSONObject.put(YellowPageStatistic.Display.CATEGORY, SIM_LOCK_SETTINGS_TITLE);
                        jSONObject.put(PaymentManager.KEY_INTENT, new TinyIntent(intent).toJSONObject());
                        jSONObject.put("temporary", true);
                        jSONObject.put("status", z ? 3 : 1);
                        addSon(i + i2, SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this));
                    } catch (JSONException unused) {
                    }
                }
            }
            if (isApplicationAvilible(((SettingsTree) this).mContext, "com.google.android.gms")) {
                addSecurityPrivacySubItems();
            }
        } else if (MANAGE_FAKECELL_SETTINGS.equals(columnValue)) {
            LinkedList sons2 = super.getSons();
            if (sons2 != null) {
                for (int size2 = sons2.size() - 1; size2 >= 0; size2--) {
                    SettingsTree settingsTree2 = (SettingsTree) sons2.get(size2);
                    if (Boolean.parseBoolean(settingsTree2.getColumnValue("temporary"))) {
                        settingsTree2.removeSelf();
                    }
                }
            }
            String stringFromSpecificPackage = MiuiUtils.getStringFromSpecificPackage(((SettingsTree) this).mContext, COM_XIAOMI_JOYOSE, FORBIDDEN_FAKECELL);
            if (!TextUtils.isEmpty(stringFromSpecificPackage)) {
                JSONObject jSONObject2 = new JSONObject();
                try {
                    jSONObject2.put("title", stringFromSpecificPackage);
                    jSONObject2.put("temporary", true);
                    addSon(SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject2, this));
                } catch (JSONException unused2) {
                }
            }
        } else if (Build.IS_INTERNATIONAL_BUILD && LOCATION_SETTINGS_TITLE.equals(columnValue)) {
            LinkedList sons3 = super.getSons();
            if (sons3 != null) {
                for (int size3 = sons3.size() - 1; size3 >= 0; size3--) {
                    SettingsTree settingsTree3 = (SettingsTree) sons3.get(size3);
                    if (Boolean.parseBoolean(settingsTree3.getColumnValue("temporary"))) {
                        settingsTree3.removeSelf();
                    }
                }
            }
            List<Preference> list = new SettingsInjector(((SettingsTree) this).mContext).getInjectedSettings(((SettingsTree) this).mContext, UserHandle.myUserId()).get(Integer.valueOf(UserHandle.myUserId()));
            if (list != null) {
                for (Preference preference : list) {
                    JSONObject jSONObject3 = new JSONObject();
                    try {
                        jSONObject3.put("title", preference.getTitle());
                        jSONObject3.put("temporary", true);
                        addSon(SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject3, this));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return super.getSons();
    }

    protected int getStatus() {
        String columnValue = getColumnValue("resource");
        if ("crypt_keeper_encrypt_title".equals(columnValue)) {
            if (LockPatternUtils.isDeviceEncryptionEnabled()) {
                return LockPatternUtils.isFileEncryptionEnabled() ? 1 : 0;
            }
        } else if ("credentials_reset".equals(columnValue) || "trusted_credentials".equals(columnValue) || CREDENTIALS_INSTALL.equals(columnValue) || "credential_storage_type".equals(columnValue) || "user_credentials".equals(columnValue)) {
            if (RestrictedLockUtilsInternal.hasBaseUserRestriction(((SettingsTree) this).mContext, "no_config_credentials", UserHandle.myUserId())) {
                return 0;
            }
        } else if (SIM_LOCK_SETTINGS_CATEGORY.equals(columnValue)) {
            if (SearchUtils.isSecondSpace() || MiuiUtils.getInstance().isMultiSimSupported() || !isSimIccReady() || VirtualSimUtils.isDcOnlyVirtualSim(((SettingsTree) this).mContext) || ((CarrierConfigManager) ((SettingsTree) this).mContext.getSystemService("carrier_config")).getConfig().getBoolean("hide_sim_lock_settings_bool")) {
                return 0;
            }
            if (!isSimReady()) {
                return 1;
            }
        } else if (MANAGE_FAKECELL_SETTINGS.equals(columnValue)) {
            if (!FakeCellSettings.supportDetectFakecell()) {
                return 0;
            }
        } else if ("manage_trust_agents".equals(columnValue)) {
            if (!new LockPatternUtils(((SettingsTree) this).mContext).isSecure(UserHandle.myUserId())) {
                return 1;
            }
        } else if ("enterprise_privacy_settings".equals(columnValue)) {
            if (!FeatureFactory.getFactory(((SettingsTree) this).mContext).getEnterprisePrivacyFeatureProvider(((SettingsTree) this).mContext).hasDeviceOwner()) {
                return 0;
            }
        } else if (MiuiSecurityAndPrivacySettingsTree.LOCK_SETTINGS_PROFILE_UNIFICATION_TITLE.equals(columnValue)) {
            if (!new LockUnificationPreferenceController(((SettingsTree) this).mContext, null).isAvailable()) {
                return 0;
            }
        } else if (MiuiSecurityAndPrivacySettingsTree.UNLOCK_SET_UNLOCK_LAUNCH_PICKER_TITLE_PROFILE.equals(columnValue)) {
            if (!new ChangeProfileScreenLockPreferenceController(((SettingsTree) this).mContext, null).isAvailable()) {
                return 0;
            }
        } else if ("managed_profile_location_switch_title".equals(columnValue)) {
            if (!new LocationForWorkPreferenceController(((SettingsTree) this).mContext, null).isAvailable()) {
                return 0;
            }
        } else if ("interact_across_profiles_title".equals(columnValue)) {
            if (!new InteractAcrossProfilesController(((SettingsTree) this).mContext, null).isAvailable()) {
                return 0;
            }
        } else if ("device_oaid".equals(columnValue) && AppOpsUtils.isXOptMode()) {
            return 0;
        } else {
            if ("special_access_more".equals(columnValue)) {
                MoreSpecialAccessPreferenceController moreSpecialAccessPreferenceController = new MoreSpecialAccessPreferenceController(((SettingsTree) this).mContext, "special_access_more");
                if (moreSpecialAccessPreferenceController.getAvailabilityStatus() != 0 && moreSpecialAccessPreferenceController.getAvailabilityStatus() != 1) {
                    return 0;
                }
            } else if ("location_modem_title".equals(columnValue)) {
                if (!MiuiModemLocationSwitchController.MODEM_LOCATION_ENABLE) {
                    return 0;
                }
            } else if ("financed_privacy_settings".equals(columnValue) && !new FinancedPrivacyPreferenceController(((SettingsTree) this).mContext).isAvailable()) {
                return 0;
            }
        }
        return super.getStatus();
    }

    protected String getTitle(boolean z) {
        return (SECURITY_PRIVACY_SETTINGS_RESOURCE.equals(getColumnValue("resource")) && SecuritySettingsController.hasSecurityCenterSecureEntry()) ? getMoreSecuritySettingsTitle() : !TextUtils.isEmpty(this.mTitle) ? this.mTitle : super.getTitle(z);
    }

    public boolean initialize() {
        String columnValue = getColumnValue("resource");
        boolean isAgpsEnabled = AgpsSettings.isAgpsEnabled();
        columnValue.hashCode();
        char c = 65535;
        switch (columnValue.hashCode()) {
            case -1882128253:
                if (columnValue.equals(WRITE_SETTINGS)) {
                    c = 0;
                    break;
                }
                break;
            case -1759783895:
                if (columnValue.equals(HIGH_POWER_APPS)) {
                    c = 1;
                    break;
                }
                break;
            case -1368284178:
                if (columnValue.equals(SIM_LOCK_SETTINGS_CATEGORY)) {
                    c = 2;
                    break;
                }
                break;
            case -1365461837:
                if (columnValue.equals(ASSISTED_GPS)) {
                    c = 3;
                    break;
                }
                break;
            case -1149797217:
                if (columnValue.equals(SYSTEM_ALERT_WINDOW_SETTINGS)) {
                    c = 4;
                    break;
                }
                break;
            case -424161854:
                if (columnValue.equals(USAGE_ACCESS)) {
                    c = 5;
                    break;
                }
                break;
            case -159557943:
                if (columnValue.equals(LOCATION_AGPS_PARAMS_SETTINGS_TITLE)) {
                    c = 6;
                    break;
                }
                break;
            case -33480859:
                if (columnValue.equals(INSTALL_OTHER_APPS)) {
                    c = 7;
                    break;
                }
                break;
            case 725414531:
                if (columnValue.equals(AGPS_ROAMING)) {
                    c = '\b';
                    break;
                }
                break;
            case 1447952264:
                if (columnValue.equals(HP_LOCATION)) {
                    c = '\t';
                    break;
                }
                break;
            case 1880747894:
                if (columnValue.equals(DIRECTORY_ACCESS)) {
                    c = '\n';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 1:
            case 4:
            case 5:
            case 7:
            case '\n':
                setColumnValue(FunctionColumns.FRAGMENT, "");
                break;
            case 2:
                LinkedList<SettingsTree> sons = getSons();
                if (sons != null) {
                    for (int size = sons.size() - 1; size >= 0; size--) {
                        sons.get(size).removeSelf();
                    }
                    break;
                }
                break;
            case 3:
                if (!isAgpsEnabled) {
                    return true;
                }
                break;
            case 6:
                if (!isAgpsEnabled || FeatureParser.getBoolean("support_agps_roaming", false)) {
                    return true;
                }
                break;
            case '\b':
                if (!isAgpsEnabled || FeatureParser.getBoolean("support_agps_paras", false)) {
                    return true;
                }
                break;
            case '\t':
                if (Build.IS_INTERNATIONAL_BUILD || !hasXiaomiHpFeature()) {
                    return true;
                }
                break;
        }
        String columnValue2 = getColumnValue("category_origin");
        if (LOCATION_MODE_SCREEN_TITLE.equals(columnValue2)) {
            if (!((SettingsTree) this).mContext.getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
                return true;
            }
        } else if (SIM_LOCK_SETTINGS_TITLE.equals(columnValue2) && new SimLockPreferenceController(((SettingsTree) this).mContext).getAvailabilityStatus() == 0) {
            addSon(SIM_PIN_TOGGLE);
            addSon(SIM_PIN_CHANGE);
        }
        return super.initialize();
    }
}
