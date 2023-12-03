package com.android.settings.search.tree;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.MiuiSettings;
import androidx.fragment.app.Fragment;
import com.android.settings.FaceUnlockStateController;
import com.android.settings.FingerprintUnlockStateController;
import com.android.settings.PasswordUnlockStateController;
import com.android.settings.custs.CellBroadcastUtil;
import com.android.settings.security.PrivacyRevocationController;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.search.SearchUtils;
import com.android.settingslib.search.SettingsTree;
import miui.content.res.ThemeResources;
import miui.os.Build;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class MiuiSecurityAndPrivacySettingsTree extends SettingsTree {
    public static final String BLUETOOTH_UNLOCK_SETTINGS_UNLOCK_DEVICE = "bluetooth_unlock_settings_unlock_device";
    public static final String BLUETOOTH_UNLOCK_TITLE = "bluetooth_unlock_title";
    public static final String CELL_BROADCAST_SETTINGS = "cell_broadcast_settings";
    public static final String EMERGENCY_SOS_TITLE = "emergency_sos_title";
    private static final String GREEN_GUARD_TITLE = "miui_sos_green_guard_title";
    public static final String KEYGUARD_SECURITY_SETTING_PRIVACY_POLICY = "keyguard_security_setting_privacy_policy";
    public static final String LOCK_SETTINGS_PROFILE_UNIFICATION_TITLE = "lock_settings_profile_unification_title";
    public static final String MANAGE_PASSWORD = "manage_password";
    public static final String PASSWORD_UNLOCK_TITLE = "password_unlock_title";
    public static final String PRIVACY_AUTHORIZE_REVOKE_TITLE = "privacy_authorize_revoke_title";
    public static final String PRIVACY_PASSWORD_TITLE = "privacy_password_title";
    public static final String PRIVACY_PASSWORD_USE_FINGER_DIALOG_TITLE = "privacy_password_use_finger_dialog_title";
    public static final String UNLOCK_SET_UNLOCK_BIOMETRIC_WEAK_TITLE = "unlock_set_unlock_biometric_weak_title";
    public static final String UNLOCK_SET_UNLOCK_LAUNCH_PICKER_TITLE_PROFILE = "unlock_set_unlock_launch_picker_title_profile";

    protected MiuiSecurityAndPrivacySettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    protected int getStatus() {
        String columnValue = getColumnValue("resource");
        columnValue.hashCode();
        boolean z = true;
        char c = 65535;
        switch (columnValue.hashCode()) {
            case -1771987232:
                if (columnValue.equals(LOCK_SETTINGS_PROFILE_UNIFICATION_TITLE)) {
                    c = 0;
                    break;
                }
                break;
            case -828775231:
                if (columnValue.equals(PASSWORD_UNLOCK_TITLE)) {
                    c = 1;
                    break;
                }
                break;
            case -729713797:
                if (columnValue.equals(UNLOCK_SET_UNLOCK_BIOMETRIC_WEAK_TITLE)) {
                    c = 2;
                    break;
                }
                break;
            case -721514443:
                if (columnValue.equals(MANAGE_PASSWORD)) {
                    c = 3;
                    break;
                }
                break;
            case -286378878:
                if (columnValue.equals(EMERGENCY_SOS_TITLE)) {
                    c = 4;
                    break;
                }
                break;
            case 221242810:
                if (columnValue.equals(UNLOCK_SET_UNLOCK_LAUNCH_PICKER_TITLE_PROFILE)) {
                    c = 5;
                    break;
                }
                break;
            case 491110194:
                if (columnValue.equals(PRIVACY_PASSWORD_USE_FINGER_DIALOG_TITLE)) {
                    c = 6;
                    break;
                }
                break;
            case 850886175:
                if (columnValue.equals(BLUETOOTH_UNLOCK_SETTINGS_UNLOCK_DEVICE)) {
                    c = 7;
                    break;
                }
                break;
            case 1865143182:
                if (columnValue.equals(BLUETOOTH_UNLOCK_TITLE)) {
                    c = '\b';
                    break;
                }
                break;
            case 1969388510:
                if (columnValue.equals(CELL_BROADCAST_SETTINGS)) {
                    c = '\t';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 5:
                return 0;
            case 1:
                if (!new PasswordUnlockStateController(((SettingsTree) this).mContext, null, new Fragment()).isAvailable()) {
                    return 0;
                }
                break;
            case 2:
                if (!new FaceUnlockStateController(((SettingsTree) this).mContext, null, new Fragment()).isAvailable()) {
                    return 0;
                }
                break;
            case 3:
                if (!SettingsFeatures.isManagePasswordNeeded(((SettingsTree) this).mContext)) {
                    return 0;
                }
                break;
            case 4:
                if (SettingsFeatures.IS_NEED_REMOVE_SOS) {
                    return 0;
                }
                break;
            case 6:
                if (!new FingerprintUnlockStateController(((SettingsTree) this).mContext, null, new Fragment()).isAvailable()) {
                    return 0;
                }
                break;
            case 7:
                if (UserHandle.myUserId() != 0 || !MiuiSettings.Secure.hasCommonPassword(((SettingsTree) this).mContext)) {
                    return 0;
                }
                break;
            case '\b':
                if (UserHandle.myUserId() != 0) {
                    return 0;
                }
                if (!MiuiSettings.Secure.hasCommonPassword(((SettingsTree) this).mContext)) {
                    return 1;
                }
                break;
            case '\t':
                PackageManager packageManager = ((SettingsTree) this).mContext.getPackageManager();
                UserManager userManager = (UserManager) ((SettingsTree) this).mContext.getSystemService("user");
                try {
                    CellBroadcastUtil.setCellbroadcastEnabledSetting(((SettingsTree) this).mContext);
                    boolean z2 = packageManager.getApplicationEnabledSetting("com.android.cellbroadcastreceiver") != 2;
                    if (!CellBroadcastUtil.nccBroadcastEnabled(packageManager)) {
                        z = z2;
                    }
                } catch (IllegalArgumentException unused) {
                    z = false;
                }
                if (!z || userManager.hasUserRestriction("no_config_cell_broadcasts")) {
                    return 0;
                }
                break;
        }
        return super.getStatus();
    }

    protected String getTitle(boolean z) {
        String columnValue = getColumnValue("resource");
        columnValue.hashCode();
        if (columnValue.equals("password_and_security")) {
            return ((SettingsTree) this).mContext.getString(SettingsFeatures.getPasswordTypes(((SettingsTree) this).mContext));
        } else if (columnValue.equals("privacy_policy") && z) {
            Resources resources = SearchUtils.getPackageContext(((SettingsTree) this).mContext, "com.miui.core").getResources();
            return resources.getString(resources.getIdentifier(columnValue, "string", ThemeResources.MIUI_PACKAGE));
        } else {
            return super.getTitle(z);
        }
    }

    public boolean initialize() {
        String columnValue = getColumnValue("resource");
        columnValue.hashCode();
        char c = 65535;
        switch (columnValue.hashCode()) {
            case -1251041652:
                if (columnValue.equals(PRIVACY_AUTHORIZE_REVOKE_TITLE)) {
                    c = 0;
                    break;
                }
                break;
            case 345343627:
                if (columnValue.equals(GREEN_GUARD_TITLE)) {
                    c = 1;
                    break;
                }
                break;
            case 1969388510:
                if (columnValue.equals(CELL_BROADCAST_SETTINGS)) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                if (PrivacyRevocationController.hidePrivacyRevoke()) {
                    return true;
                }
                break;
            case 1:
                if (Build.IS_INTERNATIONAL_BUILD) {
                    return true;
                }
                break;
            case 2:
                if (!((SettingsTree) this).mContext.getResources().getBoolean(285540354) || !Build.IS_INTERNATIONAL_BUILD) {
                    return true;
                }
                break;
        }
        return super.initialize();
    }
}
