package com.android.settings.search.tree;

import android.accounts.Account;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ModuleInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.android.settings.MiuiMasterClear;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.RegionUtils;
import com.android.settings.backup.AutoRestorePreferenceController;
import com.android.settings.backup.BackupDataPreferenceController;
import com.android.settings.backup.ConfigureAccountPreferenceController;
import com.android.settings.backup.DataManagementPreferenceController;
import com.android.settings.backup.PrivacySettingsConfigData;
import com.android.settings.credentials.MiuiCredentialsUpdater;
import com.android.settings.device.MiuiAboutPhoneUtils;
import com.android.settings.device.controller.MiuiActivationInfoController;
import com.android.settings.device.controller.MiuiOneKeyMirgrateController;
import com.android.settings.device.controller.ParamsInterpretationController;
import com.android.settings.deviceinfo.StorageSizePreferenceController;
import com.android.settings.deviceinfo.legal.ModuleLicensesPreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.DeviceInfoUtils;
import com.android.settingslib.search.SearchUtils;
import com.android.settingslib.search.SettingsTree;
import java.util.List;
import java.util.Locale;
import miui.content.res.ThemeResources;
import miui.os.Build;
import miui.util.FeatureParser;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class AboutDeviceSettingsTree extends SettingsTree {
    private static final String ACTION_EDIT_EMERGENCY_INFO = "android.settings.EDIT_EMERGENCY_INFO";
    private static final String DEVICE_MALL_CARD_TITLE = "device_mall_card_title";
    private static final Intent INTENT_PROBE = new Intent("android.settings.SHOW_SAFETY_AND_REGULATORY_INFO");
    private static final String KEY_MI_TRANSFER = "mi_transfer";
    private static final long MALL_ENTRANCE_TIME = 2678400000L;
    private static final String MODULE_LICENSE_TITLE = "module_license_title";
    private static final String PACKAGE_NAME_EMERGENCY = "com.android.emergency";
    private static final String PACKAGE_NAME_MAINTENANCE = "com.miui.maintenancemode";
    private static final String PROPERTY_EQUIPMENT_ID = "ro.ril.fccid";
    private static final String RAM_TOTAL_SIZE = "ram_total_size";
    private final TelephonyManager mTelephonyManager;

    protected AboutDeviceSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
        this.mTelephonyManager = (TelephonyManager) ((SettingsTree) this).mContext.getSystemService(TelephonyManager.class);
    }

    private boolean isCustForJpKd() {
        try {
            return ((Boolean) miui.telephony.TelephonyManager.class.getMethod("isCustForJpKd", null).invoke(null, new Object[0])).booleanValue();
        } catch (Exception unused) {
            return false;
        }
    }

    private static ResolveInfo resolveSystemActivity(Context context, Intent intent) {
        List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 0);
        int size = queryIntentActivities.size();
        for (int i = 0; i < size; i++) {
            ResolveInfo resolveInfo = queryIntentActivities.get(i);
            if ((resolveInfo.activityInfo.applicationInfo.flags & 1) != 0) {
                return resolveInfo;
            }
        }
        return null;
    }

    public Intent getIntent() {
        Intent intent;
        String columnValue = getColumnValue("resource");
        Intent intent2 = new Intent(MiuiUtils.getInstance().getViewLicenseAction());
        if ("copyright".equals(columnValue)) {
            intent2.putExtra("android.intent.extra.LICENSE_TYPE", 0);
            return intent2;
        } else if ("user_agreement".equals(columnValue)) {
            intent2.putExtra("android.intent.extra.LICENSE_TYPE", 2);
            return intent2;
        } else if (columnValue.equals("privacy_policy")) {
            intent2.putExtra("android.intent.extra.LICENSE_TYPE", 1);
            return intent2;
        } else if ("sar".equals(columnValue)) {
            intent2.putExtra("android.intent.extra.LICENSE_TYPE", 7);
            return intent2;
        } else if (columnValue.equals("user_experience_program")) {
            intent2.putExtra("android.intent.extra.LICENSE_TYPE", 8);
            return intent2;
        } else if (!KEY_MI_TRANSFER.equals(columnValue) || SettingsFeatures.isNeedRemoveOneKeyMigrate(((SettingsTree) this).mContext)) {
            if (!"legal_written_offer".equals(columnValue)) {
                return "backup_configure_account_title".equals(columnValue) ? PrivacySettingsConfigData.getInstance().getConfigIntent() : (!"terms_title".equals(columnValue) || getParent() == null) ? super.getIntent() : getParent().getIntent();
            }
            Intent intent3 = new Intent("android.settings.LICENSE");
            intent3.putExtra("license_path", SystemProperties.get("ro.config.license_gpl_path", "/system/etc/NOTICE_GPL.html.gz"));
            intent3.putExtra("license_type", "written_offer");
            return intent3;
        } else {
            if (MiuiUtils.isApplicationInstalled(((SettingsTree) this).mContext, "com.miui.huanji")) {
                Intent intent4 = new Intent("com.intent.action.Huanji");
                intent4.setPackage("com.miui.huanji");
                intent4.putExtra("request_from", "com.android.settings");
                intent = intent4;
            } else {
                intent = new Intent("android.intent.action.VIEW", Uri.parse(MiuiOneKeyMirgrateController.APP_STORE_URL));
            }
            intent.setFlags(268435456);
            return intent;
        }
    }

    protected int getStatus() {
        String columnValue = getColumnValue("resource");
        String columnValue2 = getParent().getColumnValue("resource");
        if ("my_device_info_account_preference_title".equals(columnValue)) {
            Account[] accounts = FeatureFactory.getFactory(((SettingsTree) this).mContext).getAccountFeatureProvider().getAccounts(((SettingsTree) this).mContext);
            if (accounts == null || accounts.length == 0) {
                return 0;
            }
        } else if ("emergency_info_title".equals(columnValue)) {
            List<ResolveInfo> queryIntentActivities = ((SettingsTree) this).mContext.getPackageManager().queryIntentActivities(new Intent(ACTION_EDIT_EMERGENCY_INFO).setPackage("com.android.emergency"), 0);
            if (queryIntentActivities == null || queryIntentActivities.isEmpty()) {
                return 0;
            }
        } else if ("approve_title".equals(columnValue)) {
            if (!MiuiAboutPhoneUtils.enableShowCredentials()) {
                return 0;
            }
        } else if ("terms_title".equals(columnValue)) {
            if (resolveSystemActivity(((SettingsTree) this).mContext, new Intent("android.settings.TERMS")) == null) {
                return 0;
            }
        } else if ("license_title".equals(columnValue)) {
            if (resolveSystemActivity(((SettingsTree) this).mContext, new Intent("android.settings.LICENSE")) == null) {
                return 0;
            }
        } else if ("status_bt_address".equals(columnValue)) {
            if (BluetoothAdapter.getDefaultAdapter() == null) {
                return 0;
            }
        } else if ("pre_installed_application".equals(columnValue)) {
            if (!MiuiAboutPhoneUtils.supportDisplayPreInstalledApplication()) {
                return 0;
            }
        } else if ("device_miui_version_parameters".equals(columnValue)) {
            if (!Build.IS_INTERNATIONAL_BUILD && "my_device".equals(columnValue2)) {
                return 0;
            }
        } else if ("firmware_version".equals(columnValue)) {
            if (!Build.IS_INTERNATIONAL_BUILD && "my_device".equals(columnValue2)) {
                return 0;
            }
        } else if ("device_opcust_version".equals(columnValue)) {
            if (!SettingsFeatures.IS_NEED_OPCUST_VERSION) {
                return 0;
            }
        } else if ("security_patch".equals(columnValue)) {
            if (!Build.IS_INTERNATIONAL_BUILD && "my_device".equals(columnValue2)) {
                return 0;
            }
        } else if (KEY_MI_TRANSFER.equals(columnValue) && SettingsFeatures.isNeedRemoveOneKeyMigrate(((SettingsTree) this).mContext)) {
            return 0;
        } else {
            if (RAM_TOTAL_SIZE.equals(columnValue)) {
                if (!new StorageSizePreferenceController(((SettingsTree) this).mContext).isAvailable()) {
                    return 0;
                }
            } else if (MODULE_LICENSE_TITLE.equals(columnValue)) {
                List<ModuleInfo> installedModules = ((SettingsTree) this).mContext.getPackageManager().getInstalledModules(0);
                if (installedModules == null || !installedModules.stream().anyMatch(new ModuleLicensesPreferenceController.Predicate(((SettingsTree) this).mContext))) {
                    return 0;
                }
            } else if ("legal_written_offer".equals(columnValue) && SettingsFeatures.isNeedRemoveWrittenOffer()) {
                return 0;
            } else {
                if ("privacy_settings_new".equals(columnValue) && (UserHandle.myUserId() != 0 || MiuiUtils.isDeviceManaged(((SettingsTree) this).mContext))) {
                    return 0;
                }
                if ("backup_data_title".equals(columnValue)) {
                    if (new BackupDataPreferenceController(((SettingsTree) this).mContext, "backup_data").getAvailabilityStatus() != 0) {
                        return 0;
                    }
                } else if ("backup_configure_account_title".equals(columnValue)) {
                    if (new ConfigureAccountPreferenceController(((SettingsTree) this).mContext, "configure_account").getAvailabilityStatus() == 3) {
                        return 0;
                    }
                } else if ("auto_restore_title".equals(columnValue)) {
                    if (new AutoRestorePreferenceController(((SettingsTree) this).mContext, "auto_restore").getAvailabilityStatus() != 0) {
                        return 0;
                    }
                } else if ("backup_data_management_title".equals(columnValue)) {
                    if (new DataManagementPreferenceController(((SettingsTree) this).mContext, "data_management").getAvailabilityStatus() != 0) {
                        return 0;
                    }
                } else if ("master_clear_title_new".equals(columnValue) && UserHandle.myUserId() != 0) {
                    return 0;
                } else {
                    if ("huanji_history_title".equals(columnValue) && MiuiUtils.needRemoveMigrateHistory(((SettingsTree) this).mContext)) {
                        return 0;
                    }
                    if ("flash_drive_backup_restore".equals(columnValue) && (Build.IS_INTERNATIONAL_BUILD || MiuiUtils.isBackupDisabled(((SettingsTree) this).mContext) || !MiuiUtils.isInsertUsb(((SettingsTree) this).mContext))) {
                        return 0;
                    }
                    if ("model_name".equals(columnValue)) {
                        boolean z = Build.IS_INTERNATIONAL_BUILD;
                        if ((z && TextUtils.isEmpty(MiuiCredentialsUpdater.getGlobalCertNumber())) || (!z && TextUtils.isEmpty(MiuiAboutPhoneUtils.getCTANumble()))) {
                            return 0;
                        }
                    } else if ("micloud_service_title".equals(columnValue) && MiuiUtils.isDeviceManaged(((SettingsTree) this).mContext)) {
                        return 0;
                    } else {
                        if ("device_activation_info".equals(columnValue)) {
                            if (new MiuiActivationInfoController(((SettingsTree) this).mContext, "device_activation_info").getAvailabilityStatus() != 0) {
                                return 0;
                            }
                        } else if ("sd_data".equals(columnValue) && RegionUtils.IS_JP_KDDI && !MiuiUtils.hasSDCard(((SettingsTree) this).mContext)) {
                            return 0;
                        } else {
                            if ("maintenance_title".equals(columnValue)) {
                                if (Build.IS_INTERNATIONAL_BUILD || !MiuiUtils.isApplicationInstalled(((SettingsTree) this).mContext, PACKAGE_NAME_MAINTENANCE) || !Locale.getDefault().toLanguageTag().equals("zh-CN")) {
                                    return 0;
                                }
                            } else if ("phone_backup_title".equals(columnValue) && MiuiUtils.isBackupDisabled(((SettingsTree) this).mContext)) {
                                return 0;
                            } else {
                                if ("computer_backup_title".equals(columnValue) && MiuiUtils.isBackupDisabled(((SettingsTree) this).mContext)) {
                                    return 0;
                                }
                                if ("local_backup_usestatus_title".equals(columnValue) && !MiuiUtils.isBackupDisabled(((SettingsTree) this).mContext)) {
                                    return 0;
                                }
                                if ("params_interpretation".equals(columnValue)) {
                                    if (!(new ParamsInterpretationController(((SettingsTree) this).mContext).getAvailabilityStatus() == 0)) {
                                        return 0;
                                    }
                                } else if (DEVICE_MALL_CARD_TITLE.equals(columnValue) && SettingsFeatures.isNeedHideShopEntrance(((SettingsTree) this).mContext, MALL_ENTRANCE_TIME)) {
                                    return 0;
                                }
                            }
                        }
                    }
                }
            }
        }
        return super.getStatus();
    }

    protected String getTitle(boolean z) {
        if (z) {
            String columnValue = getColumnValue("resource");
            columnValue.hashCode();
            char c = 65535;
            switch (columnValue.hashCode()) {
                case -2045686368:
                    if (columnValue.equals("terms_title")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1939041207:
                    if (columnValue.equals("my_device")) {
                        c = 1;
                        break;
                    }
                    break;
                case -1752090986:
                    if (columnValue.equals("user_agreement")) {
                        c = 2;
                        break;
                    }
                    break;
                case -1549593382:
                    if (columnValue.equals("license_title")) {
                        c = 3;
                        break;
                    }
                    break;
                case -339464518:
                    if (columnValue.equals("user_manual")) {
                        c = 4;
                        break;
                    }
                    break;
                case 113636:
                    if (columnValue.equals("sar")) {
                        c = 5;
                        break;
                    }
                    break;
                case 926873033:
                    if (columnValue.equals("privacy_policy")) {
                        c = 6;
                        break;
                    }
                    break;
                case 1100083898:
                    if (columnValue.equals("status_number0")) {
                        c = 7;
                        break;
                    }
                    break;
                case 1100083899:
                    if (columnValue.equals("status_number1")) {
                        c = '\b';
                        break;
                    }
                    break;
                case 1522889671:
                    if (columnValue.equals("copyright")) {
                        c = '\t';
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    ResolveInfo resolveSystemActivity = resolveSystemActivity(((SettingsTree) this).mContext, new Intent("android.settings.TERMS"));
                    if (resolveSystemActivity != null) {
                        return resolveSystemActivity.loadLabel(((SettingsTree) this).mContext.getPackageManager()).toString();
                    }
                    break;
                case 1:
                    if (!SettingsFeatures.isShowMyDevice()) {
                        return ((SettingsTree) this).mContext.getResources().getString(R.string.about_settings);
                    }
                    break;
                case 2:
                case 4:
                case 5:
                case 6:
                case '\t':
                    Resources resources = SearchUtils.getPackageContext(((SettingsTree) this).mContext, "com.miui.core").getResources();
                    return resources.getString(resources.getIdentifier(columnValue, "string", ThemeResources.MIUI_PACKAGE));
                case 3:
                    ResolveInfo resolveSystemActivity2 = resolveSystemActivity(((SettingsTree) this).mContext, new Intent("android.settings.LICENSE"));
                    if (resolveSystemActivity2 != null) {
                        return resolveSystemActivity2.loadLabel(((SettingsTree) this).mContext.getPackageManager()).toString();
                    }
                    Resources resources2 = SearchUtils.getPackageContext(((SettingsTree) this).mContext, "com.miui.core").getResources();
                    return resources2.getString(resources2.getIdentifier(columnValue, "string", ThemeResources.MIUI_PACKAGE));
                case 7:
                    return ((SettingsTree) this).mContext.getString(R.string.status_number_sim_slot, 1);
                case '\b':
                    return ((SettingsTree) this).mContext.getString(R.string.status_number_sim_slot, 2);
            }
        }
        return super.getTitle(z);
    }

    public boolean initialize() {
        String columnValue = getColumnValue("resource");
        if ("my_device".equals(columnValue) && !Build.IS_INTERNATIONAL_BUILD) {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("resource", "device_miui_version");
                addSon(SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this));
            } catch (JSONException unused) {
            }
        } else if ("hardware_version".equals(columnValue)) {
            if (TextUtils.isEmpty(SystemProperties.get("ro.miui.cust_hardware"))) {
                return true;
            }
        } else if ("wifi_type_approval_title".equals(columnValue)) {
            if (TextUtils.isEmpty(((SettingsTree) this).mContext.getResources().getString(R.string.wifi_type_approval))) {
                return true;
            }
        } else if ("hardware_info".equals(columnValue)) {
            if (!((SettingsTree) this).mContext.getResources().getBoolean(R.bool.config_show_device_model)) {
                return true;
            }
        } else if ("wifi_ip_address".equals(columnValue)) {
            if (!((SettingsTree) this).mContext.getResources().getBoolean(R.bool.config_show_wifi_ip_address)) {
                return true;
            }
        } else if (!"status_wifi_mac_address".equals(columnValue)) {
            if ("safety_and_regulatory_info".equals(columnValue)) {
                if (((SettingsTree) this).mContext.getPackageManager().queryIntentActivities(INTENT_PROBE, 0).isEmpty()) {
                    return true;
                }
            } else if ("manual".equals(columnValue)) {
                if (!((SettingsTree) this).mContext.getResources().getBoolean(R.bool.config_show_manual)) {
                    return true;
                }
            } else if ("device_feedback".equals(columnValue)) {
                if (TextUtils.isEmpty(DeviceInfoUtils.getFeedbackReporterPackage(((SettingsTree) this).mContext))) {
                    return true;
                }
            } else if ("fcc_equipment_id".equals(columnValue)) {
                if (TextUtils.isEmpty(SystemProperties.get(PROPERTY_EQUIPMENT_ID))) {
                    return true;
                }
            } else if ("settings_safetylegal_title".equals(columnValue)) {
                if (!Build.IS_INTERNATIONAL_BUILD || TextUtils.isEmpty(SystemProperties.get("ro.url.safetylegal"))) {
                    return true;
                }
            } else if ("status_serial_number".equals(columnValue)) {
                if (TextUtils.isEmpty(android.os.Build.SERIAL)) {
                    return true;
                }
            } else if ("status_serialno".equals(columnValue)) {
                if (TextUtils.isEmpty(SystemProperties.get("permanent.hw.custom.serialno"))) {
                    return true;
                }
            } else if ("sar".equals(columnValue)) {
                if (!Build.IS_GLOBAL_BUILD) {
                    return true;
                }
            } else if ("device_camera".equals(columnValue)) {
                return true;
            } else {
                if (("poco_launcher_user_agreement".equals(columnValue) || "poco_launcher_privacy_policy".equals(columnValue)) && !SettingsFeatures.hasPocoLauncherDefault()) {
                    return true;
                }
                if ("device_status".equals(columnValue)) {
                    if (isNotSingleSimDevice()) {
                        for (int i = 0; i < this.mTelephonyManager.getPhoneCount(); i++) {
                            JSONObject jSONObject2 = new JSONObject();
                            try {
                                jSONObject2.put("resource", "status_number" + i);
                                addSon(SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject2, this));
                            } catch (JSONException unused2) {
                            }
                        }
                    }
                } else if ("status_number".equals(columnValue)) {
                    if (isNotSingleSimDevice()) {
                        return true;
                    }
                } else if ("erase_application".equals(columnValue)) {
                    if (!FeatureParser.getBoolean("support_erase_application", false)) {
                        return true;
                    }
                } else if ("erase_external_storage".equals(columnValue)) {
                    if (MiuiMasterClear.isRemoveEraseExternalStorage()) {
                        return true;
                    }
                } else if ("copyright".equals(columnValue) && Build.IS_INTERNATIONAL_BUILD) {
                    return true;
                } else {
                    if ("sim_card_lock".equals(columnValue) && !isCustForJpKd()) {
                        return true;
                    }
                }
            }
        } else if (!((SettingsTree) this).mContext.getResources().getBoolean(R.bool.config_show_wifi_mac_address)) {
            return true;
        }
        return super.initialize();
    }

    public boolean isNotSingleSimDevice() {
        return this.mTelephonyManager.isVoiceCapable() && this.mTelephonyManager.getPhoneCount() > 1 && !miui.telephony.TelephonyManager.isCustSingleSimDevice();
    }
}
