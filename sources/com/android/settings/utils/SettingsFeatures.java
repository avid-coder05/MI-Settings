package com.android.settings.utils;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.provider.SystemSettings$System;
import android.service.notification.ZenModeConfig;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.IWindowManager;
import androidx.preference.PreferenceManager;
import com.android.settings.FingerprintHelper;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.RegionUtils;
import com.android.settings.device.MiuiAboutPhoneUtils;
import com.android.settings.faceunlock.KeyguardSettingsFaceUnlockUtils;
import com.android.settings.lab.MiuiAiAsstCallScreenController;
import com.android.settings.lab.MiuiAiPreloadController;
import com.android.settings.lab.MiuiDriveModeController;
import com.android.settings.lab.MiuiFlashbackController;
import com.android.settings.lab.MiuiLabGestureController;
import com.android.settings.notification.SilentModeUtils;
import com.android.settings.search.SearchUpdater;
import com.android.settings.search.tree.SecuritySettingsTree;
import com.android.settings.security.SecuritySettingsController;
import com.android.settings.special.GameBoosterController;
import com.android.settings.special.PrivacyLabController;
import com.android.settingslib.OldmanHelper;
import com.miui.enterprise.settings.EnterpriseSettings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import miui.os.Build;
import miui.securityspace.ConfigUtils;
import miui.securityspace.CrossUserUtils;
import miui.telephony.TelephonyManager;
import miui.util.FeatureParser;
import miui.util.HapticFeedbackUtil;
import miui.util.HardwareInfo;
import miuix.animation.utils.DeviceUtils;

/* loaded from: classes2.dex */
public final class SettingsFeatures {
    public static final boolean FEATURE_HAPTIC_INFINITE_LEVEL;
    public static final boolean IS_KERNEL_VERSION_FILTER;
    public static final boolean IS_NEED_ADD_RINGTOYOU;
    public static final boolean IS_NEED_OPCUST_VERSION;
    public static final boolean IS_NEED_REMOVE_DISTURD;
    public static final boolean IS_NEED_REMOVE_EDGE_MODE;
    public static final boolean IS_NEED_REMOVE_HANDY_MODE;
    public static final boolean IS_NEED_REMOVE_KID_SPACE;
    public static final boolean IS_NEED_REMOVE_SOS;
    public static final boolean IS_NEED_REMOVE_THEME;
    public static final boolean IS_NEED_REMOVE_WAKE_UP_VOICE_ASSISTANT;
    private static final boolean IS_REMOVE_SPLIT_SCREEN;
    public static final boolean IS_SUPPORT_BEAUTY_CAMERA;
    private static final boolean IS_SUPPORT_COLOR_LAMP_DEVICE;
    public static final boolean IS_SUPPORT_FINGERPRINT_TAP;
    public static final boolean IS_SUPPORT_HEART_RATE;
    public static final boolean IS_SUPPORT_SHOULDER_KEY;
    public static final boolean IS_SUPPORT_SHOULDER_KEY_MORE;
    public static final boolean IS_SUPPORT_TRUE_COLOR;
    public static final boolean SUPPORT_FOLD;
    public static final List<String> sHeartRateSupportDevices;
    public static final List<String> sNotSupportToolBoxDevices;
    private static final Set<String> supportAccessibilityHapticDeviceSet;
    private static final Set<String> supportSettingHapticDeviceSet;

    static {
        boolean z = Build.IS_TABLET;
        boolean z2 = true;
        IS_NEED_REMOVE_SOS = z || SecuritySettingsController.hasSecurityCenterSecureEntry() || isNeedHideSosForCarrier();
        FEATURE_HAPTIC_INFINITE_LEVEL = SystemProperties.getBoolean("sys.haptic.infinitelevel", false);
        String str = android.os.Build.DEVICE;
        IS_KERNEL_VERSION_FILTER = "begonia".equals(str) || "mediatek".equals(FeatureParser.getString("vendor"));
        SUPPORT_FOLD = SystemProperties.getInt("persist.sys.muiltdisplay_type", 0) == 2;
        IS_NEED_REMOVE_THEME = z || OldmanHelper.isOldmanMode() || RegionUtils.IS_MEXICO_TELCEL || isFoldDevice() || RegionUtils.IS_LM_CLARO || RegionUtils.IS_MX_AT;
        IS_NEED_ADD_RINGTOYOU = RegionUtils.IS_KOREA_KT && "monet".equals(str);
        IS_SUPPORT_FINGERPRINT_TAP = FeatureParser.getBoolean("is_support_fingerprint_tap", false);
        IS_SUPPORT_SHOULDER_KEY = FeatureParser.getBoolean("support_shoulder_key", false);
        IS_SUPPORT_SHOULDER_KEY_MORE = FeatureParser.getBoolean("support_shoulder_key_more", false);
        IS_SUPPORT_BEAUTY_CAMERA = (!Boolean.parseBoolean(SystemProperties.get("persist.vendor.vcb.ability", "false")) || RegionUtils.IS_INDIA || RegionUtils.IS_JP_KDDI) ? false : true;
        IS_SUPPORT_COLOR_LAMP_DEVICE = "lmi".equals(str) || "lmiin".equals(str) || "lmipro".equals(str) || "lmiinpro".equals(str) || "cezanne".equals(str);
        IS_REMOVE_SPLIT_SCREEN = "camellian".equals(str) || "camellia".equals(str);
        IS_NEED_OPCUST_VERSION = !TextUtils.isEmpty(SystemProperties.get("ro.miui.opcust.version"));
        ArrayList arrayList = new ArrayList();
        sNotSupportToolBoxDevices = arrayList;
        ArrayList arrayList2 = new ArrayList();
        sHeartRateSupportDevices = arrayList2;
        IS_SUPPORT_HEART_RATE = FeatureParser.getBoolean("support_heartbeat_rate", false);
        supportAccessibilityHapticDeviceSet = new HashSet();
        supportSettingHapticDeviceSet = new HashSet();
        arrayList.add("dandelion");
        arrayList.add("angelica");
        arrayList.add("angelicain");
        arrayList.add("cattail");
        arrayList.add("angelican");
        arrayList.add("frost");
        arrayList2.add("venus");
        arrayList2.add("star");
        IS_NEED_REMOVE_WAKE_UP_VOICE_ASSISTANT = (Build.IS_GLOBAL_BUILD && hasNavigationBar()) ? false : true;
        IS_NEED_REMOVE_DISTURD = (z && Build.VERSION.SDK_INT < 21) || MiuiSettings.SilenceMode.isSupported;
        IS_NEED_REMOVE_EDGE_MODE = !FeatureParser.getBoolean("support_edge_handgrip", false);
        IS_NEED_REMOVE_HANDY_MODE = !SystemProperties.getBoolean("ro.support_one_handed_mode", false);
        if (UserHandle.myUserId() == 0 && !z && !miui.os.Build.IS_INTERNATIONAL_BUILD && !isFoldDevice() && ConfigUtils.isSupportSecuritySpace()) {
            z2 = false;
        }
        IS_NEED_REMOVE_KID_SPACE = z2;
        IS_SUPPORT_TRUE_COLOR = FeatureParser.getBoolean("support_true_color", false);
    }

    public static boolean checkGlobalFontSettingEnable(Context context) {
        if (miui.os.Build.IS_GLOBAL_BUILD) {
            try {
                Bundle call = context.getContentResolver().call(Uri.parse("content://com.android.thememanager.theme_provider"), "getFontService", (String) null, (Bundle) null);
                if (call != null) {
                    return call.getBoolean("theme_font_access");
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public static boolean disableVideoWallpaper(Context context) {
        boolean z;
        Resources resourcesForApplication;
        int identifier;
        try {
            resourcesForApplication = context.getPackageManager().getResourcesForApplication("com.android.thememanager");
            identifier = resourcesForApplication.getIdentifier("disableVideoWallpaper", "boolean", "com.android.thememanager");
        } catch (Exception e) {
            Log.e("SettingsFeatures", "fail get resource : " + e);
        }
        if (identifier != 0) {
            if (resourcesForApplication.getBoolean(identifier)) {
                z = true;
                return z || "veux".equals(android.os.Build.DEVICE);
            }
        }
        z = false;
        if (z) {
            return true;
        }
    }

    public static int getPasswordTypes(Context context) {
        FingerprintHelper fingerprintHelper = new FingerprintHelper(context);
        return !SecuritySettingsController.hasSecurityCenterSecureEntry() ? R.string.password_and_security : (fingerprintHelper.isHardwareDetected() && KeyguardSettingsFaceUnlockUtils.isSupportFaceUnlock(context)) ? R.string.fingerprint_face_password_unlock : fingerprintHelper.isHardwareDetected() ? R.string.fingerprint_password_unlock : KeyguardSettingsFaceUnlockUtils.isSupportFaceUnlock(context) ? R.string.face_password_unlock : R.string.password_unlock;
    }

    public static int getWifiTetherPlacement(Context context) {
        if (!miui.os.Build.IS_TABLET || UserManager.get(context).hasUserRestriction("no_config_tethering") || ((ConnectivityManager) context.getSystemService("connectivity")).isTetheringSupported()) {
            return (!isSplitTablet(context) && PreferenceManager.getDefaultSharedPreferences(context).getBoolean("wifi_tether_opened", false)) ? 1 : 2;
        }
        return 0;
    }

    public static boolean hasBackTapSensorFeature(Context context) {
        return ((SensorManager) context.getSystemService("sensor")).getDefaultSensor(33171045) != null;
    }

    public static boolean hasMarketName() {
        return !TextUtils.isEmpty(SystemProperties.get(SystemSettings$System.RO_MARKET_NAME));
    }

    private static boolean hasNavigationBar() {
        try {
            return IWindowManager.Stub.asInterface(ServiceManager.getService("window")).hasNavigationBar(0);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean hasNfcDispatchOptimFeature(Context context) {
        if (context.getPackageManager().hasSystemFeature("android.hardware.nfc")) {
            return "1".equals(SystemProperties.get("ro.vendor.nfc.dispatch_optim", "0"));
        }
        return false;
    }

    public static boolean hasNfcRepairFeature(Context context) {
        if (context.getPackageManager().hasSystemFeature("android.hardware.nfc")) {
            return "1".equals(SystemProperties.get("ro.vendor.nfc.repair", ""));
        }
        return false;
    }

    public static boolean hasPocoLauncherDefault() {
        return "com.mi.android.globallauncher".equals(SystemProperties.get("ro.miui.product.home"));
    }

    private static boolean hasSmallFreeformFeature() {
        try {
            Class<?> cls = Class.forName("android.view.Display");
            return ((Boolean) cls.getDeclaredMethod("hasSmallFreeformFeature", null).invoke(cls, new Object[0])).booleanValue();
        } catch (Exception e) {
            Log.e("SettingsFeatures", "reflect error when get hasSmallFreeformFeature", e);
            return false;
        }
    }

    public static boolean hasSplitScreen() {
        if (isMiuiLiteVersion() || !isLargeMemoryDevice()) {
            return false;
        }
        return !sNotSupportToolBoxDevices.contains(android.os.Build.DEVICE);
    }

    public static boolean isAlienTablet() {
        return !miui.os.Build.IS_TABLET && "cetus".equals(android.os.Build.DEVICE);
    }

    public static boolean isCMTCallingAppAdmin(Context context) {
        List<ComponentName> activeAdmins;
        if (context == null || (activeAdmins = ((DevicePolicyManager) context.getSystemService("device_policy")).getActiveAdmins()) == null) {
            return false;
        }
        Iterator<ComponentName> it = activeAdmins.iterator();
        while (it.hasNext()) {
            if (it.next().getPackageName().equals("com.controlmovil.telcel")) {
                return true;
            }
        }
        return false;
    }

    public static final boolean isDeviceLockNeed(Context context) {
        String[] stringArray;
        if (context == null || (stringArray = context.getResources().getStringArray(R.array.config_device_lock_need)) == null || stringArray.length <= 0) {
            return false;
        }
        return Arrays.asList(stringArray).contains(android.os.Build.DEVICE);
    }

    public static boolean isFeedbackNeeded(Context context) {
        return context.getPackageManager().queryIntentActivities(new Intent("miui.intent.action.miservice"), SearchUpdater.GOOGLE).size() > 0;
    }

    public static boolean isFoldDevice() {
        return SUPPORT_FOLD;
    }

    public static boolean isFrontAssistantSupport(Context context) {
        return MiuiDockUtils.isFrontAssistantSupport(context);
    }

    public static boolean isHealthGlobalItemNeedHide(Context context) {
        if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
            return ((sHeartRateSupportDevices.contains(android.os.Build.DEVICE) || IS_SUPPORT_HEART_RATE) && MiuiUtils.isAppInstalledAndEnabled(context, "com.mi.healthglobal")) ? false : true;
        }
        return true;
    }

    public static boolean isHideRingtoneCall(Context context) {
        return context == null || com.android.settingslib.Utils.isWifiOnly(context) || UserHandle.myUserId() != 0 || !com.android.settings.Utils.isVoiceCapable(context);
    }

    public static boolean isIncallShowNeeded(Context context) {
        return context.getPackageManager().queryIntentActivities(new Intent("miui.intent.action.INCALL_SHOW_PICKER"), SearchUpdater.GOOGLE).size() > 0 && supportIncallShow(context) && !isFoldDevice() && !isMiuiLiteAndStokeDevice().booleanValue() && TelephonyManager.getDefault().isVoiceCapable() && !disableVideoWallpaper(context);
    }

    public static boolean isLargeMemoryDevice() {
        return HardwareInfo.getTotalPhysicalMemory() / 1000000000 > 4;
    }

    public static boolean isManagePasswordNeeded(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.miui.contentcatcher", "com.miui.contentcatcher.autofill.activitys.AutofillSettingActivity");
        return context.getPackageManager().queryIntentActivities(intent, 0).size() > 0;
    }

    public static boolean isMisoundShowNeeded(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.miui.misound", "com.miui.misound.HeadsetSettingsActivity");
        return context.getPackageManager().queryIntentActivities(intent, 0).size() > 0;
    }

    public static boolean isMiuiLabNeedHide(Context context) {
        return MiuiAiPreloadController.isNotSupported() && MiuiDriveModeController.isNeedHideDriveMode(context) && MiuiLabGestureController.isNotSupported() && MiuiAiAsstCallScreenController.isNeedHideCallScreen(context) && MiuiFlashbackController.isNotSupported();
    }

    public static Boolean isMiuiLiteAndStokeDevice() {
        return Boolean.valueOf(isMiuiLiteVersion() && !DeviceUtils.isStockDevice());
    }

    public static boolean isMiuiLiteVersion() {
        try {
            return ((Boolean) Class.forName("miui.util.DeviceLevel").getField("IS_MIUI_LITE_VERSION").get(null)).booleanValue();
        } catch (Exception e) {
            Log.e("SettingsFeatures", "reflect error when get DeviceLevel IS_MIUI_LITE_VERSION", e);
            return false;
        }
    }

    public static final boolean isNeedESIMCustmized() {
        return (RegionUtils.IS_JP_SB && TextUtils.equals(android.os.Build.DEVICE, "lilac")) || (RegionUtils.IS_JP && TextUtils.equals(android.os.Build.DEVICE, "veux"));
    }

    public static final boolean isNeedESIMFeature() {
        boolean z;
        String region;
        try {
            Class<?> cls = Class.forName("miui.telephony.TelephonyManagerEx");
            Object invoke = cls.getMethod("getDefault", new Class[0]).invoke(null, new Object[0]);
            region = RegionUtils.getRegion();
            z = ((Boolean) cls.getMethod("isSupportEsimForCountry", String.class).invoke(invoke, region)).booleanValue();
        } catch (Exception e) {
            e = e;
            z = false;
        }
        try {
            Log.d("SettingsFeatures", "isSupportEsimForCountry " + z + " region " + region);
        } catch (Exception e2) {
            e = e2;
            Log.e("SettingsFeatures", "isNeedESIMFeature: ", e);
            if (SystemProperties.getBoolean("ro.vendor.miui.support_esim", false)) {
            }
        }
        return !SystemProperties.getBoolean("ro.vendor.miui.support_esim", false) && z;
    }

    public static boolean isNeedHideShopEntrance(Context context, long j) {
        if (context == null) {
            return true;
        }
        return miui.os.Build.IS_TABLET || isFoldDevice() || miui.os.Build.IS_INTERNATIONAL_BUILD || !(Settings.Global.getInt(context.getContentResolver(), "Is_new_phone", 1) == 1) || UserHandle.myUserId() != 0 || System.currentTimeMillis() - Settings.Global.getLong(context.getContentResolver(), "Activation_time", 0L) > j;
    }

    public static boolean isNeedHideSosForCarrier() {
        return RegionUtils.IS_JP_SB && TextUtils.equals(android.os.Build.DEVICE, "diting");
    }

    public static boolean isNeedRemoveContentExtension(Context context) {
        if (miui.os.Build.IS_TABLET) {
            return true;
        }
        Intent intent = new Intent();
        intent.setAction("com.miui.contentextension.action.TAPLUS_SETTINGS");
        intent.setPackage("com.miui.contentextension");
        return !MiuiUtils.getInstance().canFindActivity(context, intent);
    }

    public static boolean isNeedRemoveEasyMode(Context context) {
        if (RegionUtils.IS_JP_SB || hasPocoLauncherDefault() || isSplitTablet(context)) {
            return true;
        }
        if (MiuiUtils.isLowMemoryMachine() && miui.os.Build.IS_INTERNATIONAL_BUILD) {
            return true;
        }
        return MiuiUtils.isSecondSpace(context) || context.getPackageManager().queryIntentActivities(new Intent("com.xiaomi.action.ENTER_ELDERLY_MODE"), 131072).size() <= 0;
    }

    public static boolean isNeedRemoveGmsCoreSettigns(Context context) {
        if (miui.os.Build.IS_INTERNATIONAL_BUILD || !"1".equals(SystemProperties.get("ro.miui.has_gmscore"))) {
            return true;
        }
        Intent intent = new Intent("miui.intent.action.APP_SETTINGS");
        intent.setClassName(SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME, "com.miui.googlebase.ui.GmsCoreSettings");
        return intent.resolveActivityInfo(context.getPackageManager(), SearchUpdater.GOOGLE) == null;
    }

    public static boolean isNeedRemoveLedSettings() {
        return !FeatureParser.getBoolean("support_led_light", false) || FeatureParser.getBoolean("support_led_colorful", false) || FeatureParser.getBoolean("support_color_lamp", false);
    }

    public static boolean isNeedRemoveOldmanMode(Context context) {
        if (!miui.os.Build.IS_INTERNATIONAL_BUILD && FeatureParser.getBoolean("is_hongmi", false) && FeatureParser.getBoolean("support_simple_launcher", true)) {
            String[] queryStringArray = MiuiUtils.queryStringArray(context, "remove_oldman_mode_device_list");
            String str = android.os.Build.DEVICE;
            if (queryStringArray != null && !TextUtils.isEmpty(str)) {
                for (String str2 : queryStringArray) {
                    if (str.equalsIgnoreCase(str2)) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }

    public static boolean isNeedRemoveOneKeyMigrate(Context context) {
        return miui.os.Build.IS_TABLET || miui.os.Build.IS_INTERNATIONAL_BUILD || UserHandle.myUserId() != 0 || MiuiUtils.isDeviceManaged(context);
    }

    public static boolean isNeedRemoveSmsReceivedSound(Context context) {
        return com.android.settingslib.Utils.isWifiOnly(context) || (miui.os.Build.IS_INTERNATIONAL_BUILD && SystemProperties.getBoolean("ro.miui.google.csp", false));
    }

    public static boolean isNeedRemoveTouchAssistant(Context context) {
        Intent intent = new Intent();
        intent.setAction("miui.intent.action.TOUCH_ASSISTANT_SETTINGS");
        intent.setPackage("com.miui.touchassistant");
        return !MiuiUtils.getInstance().canFindActivity(context, intent);
    }

    public static boolean isNeedRemoveWrittenOffer() {
        if (TextUtils.equals("eea", SystemProperties.get("ro.miui.build.region")) && "DE".equalsIgnoreCase(SystemProperties.get("ro.miui.region"))) {
            return !MiuiUtils.isFilePathValid(SystemProperties.get("ro.config.license_gpl_path", "/system/etc/NOTICE_GPL.html.gz"));
        }
        return true;
    }

    public static boolean isNeedShowColorGameLed() {
        return FeatureParser.getBoolean("support_led_colorful", false) && FeatureParser.getBoolean("support_led_colorful_game", false);
    }

    public static boolean isNeedShowColorLamp() {
        return IS_SUPPORT_COLOR_LAMP_DEVICE;
    }

    public static boolean isNeedShowColorLed() {
        return FeatureParser.getBoolean("support_led_colorful", false) && !FeatureParser.getBoolean("support_led_colorful_game", false);
    }

    public static boolean isNeedShowMishare(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.miui.mishare.action.MiShareSettings");
        return context.getPackageManager().queryIntentActivities(intent, SearchUpdater.GOOGLE).size() > 0;
    }

    public static boolean isNeedShowMiuiNFC() {
        return SystemProperties.getInt("ro.vendor.nfc.wallet_fusion", 0) == 1;
    }

    public static boolean isOtherSpecialFeatureItemNeedHide() {
        return MiuiUtils.isLowMemoryMachine() && miui.os.Build.IS_INTERNATIONAL_BUILD;
    }

    public static boolean isPrivacyProtectionNeeded(Context context) {
        return context.getPackageManager().queryIntentActivities(new Intent("miui.intent.action.PRIVACY_SETTINGS"), SearchUpdater.GOOGLE).size() > 0;
    }

    public static boolean isScreenLayoutLarge(Context context) {
        return context != null && (context.getResources().getConfiguration().screenLayout & 15) == 3;
    }

    public static boolean isSecondSpaceItemNeedHide(Context context) {
        if (RegionUtils.IS_JP_SB) {
            return true;
        }
        int secondSpaceEntranceStatus = MiuiSettings.Secure.getSecondSpaceEntranceStatus(context.getContentResolver(), 0);
        if (isOtherSpecialFeatureItemNeedHide() || !ConfigUtils.isSupportSecuritySpace() || OldmanHelper.isOldmanMode() || CrossUserUtils.isAirSpace(context, UserHandle.myUserId())) {
            return true;
        }
        return (UserHandle.myUserId() == 0 && secondSpaceEntranceStatus == 0) || EnterpriseSettings.ENTERPRISE_ACTIVATED;
    }

    public static boolean isShowFreeformGuideSetting() {
        return supportQuickReply() && hasSmallFreeformFeature();
    }

    public static boolean isShowGameTurbo(Context context) {
        if (isSupportDock(context)) {
            return false;
        }
        try {
            Intent parseUri = sNotSupportToolBoxDevices.contains(android.os.Build.DEVICE) ? null : Intent.parseUri(GameBoosterController.JUMP_GAME_ACTION, 0);
            if (parseUri != null) {
                return MiuiUtils.isIntentActivityExistAsUser(context, parseUri, UserHandle.myUserId());
            }
            return false;
        } catch (Exception unused) {
            Log.e("SettingsFeatures", "URI invalid");
            return false;
        }
    }

    public static boolean isShowMyDevice() {
        return true;
    }

    public static boolean isShowPrivacyLab() {
        return (miui.os.Build.IS_INTERNATIONAL_BUILD || PrivacyLabController.ISLOWER_V12_5_BUILD) ? false : true;
    }

    public static boolean isShowQuickReplySetting() {
        return (miui.os.Build.IS_TABLET || hasSmallFreeformFeature() || !supportQuickReply()) ? false : true;
    }

    public static boolean isShowSlotDevice() {
        return TextUtils.equals(android.os.Build.DEVICE, "lilac");
    }

    public static boolean isShowVideoToolBoxSetting(Context context) {
        if (isSupportDock(context)) {
            return false;
        }
        try {
            if (sNotSupportToolBoxDevices.contains(android.os.Build.DEVICE)) {
                return false;
            }
            if (FeatureParser.getBoolean("is_support_video_tool_box", false)) {
                Intent intent = new Intent();
                intent.setAction("com.miui.gamebooster.action.VIDEOBOX_SETTINGS");
                return context.getPackageManager().queryIntentActivities(intent, SearchUpdater.GOOGLE).size() > 0;
            } else if (supportQuickReply()) {
                Intent intent2 = new Intent();
                intent2.setAction("com.miui.gamebooster.action.VIDEOBOX_SETTINGS_ALL");
                return context.getPackageManager().queryIntentActivities(intent2, SearchUpdater.GOOGLE).size() > 0;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.e("SettingsFeatures", "reflect error when get video tool box support state", e);
            return false;
        }
    }

    public static boolean isSplitTablet(Context context) {
        return isSplitTabletDevice() || (TextUtils.equals(android.os.Build.DEVICE, "cetus") && isScreenLayoutLarge(context));
    }

    public static boolean isSplitTabletDevice() {
        String str = android.os.Build.DEVICE;
        return TextUtils.equals(str, "enuma") || TextUtils.equals(str, "elish") || TextUtils.equals(str, "nabu");
    }

    public static boolean isSupportAccessibilityHaptic(Context context) {
        Set<String> set = supportAccessibilityHapticDeviceSet;
        if (set.isEmpty()) {
            set.addAll(Arrays.asList(context.getResources().getStringArray(R.array.support_accessibility_haptic_device_list)));
        }
        return set.contains(android.os.Build.DEVICE) && !miui.os.Build.IS_INTERNATIONAL_BUILD;
    }

    public static boolean isSupportCustomZenPriorityPkg() {
        return FeatureParser.getBoolean("support_custom_zen_priority_exception_pkg", false);
    }

    public static boolean isSupportDock(Context context) {
        return MiuiDockUtils.isDockSupport(context);
    }

    public static boolean isSupportEdgeSuppression() {
        int i = SystemProperties.getInt("ro.vendor.touchfeature.type", 0);
        return (i & 4) != 0 && (i & 64) == 0;
    }

    public static boolean isSupportMagicWindow(Context context) {
        return Arrays.asList(context.getResources().getStringArray(R.array.support_magic_window_device_list)).contains(android.os.Build.DEVICE);
    }

    public static boolean isSupportOtgReverseCharge() {
        return SystemProperties.getBoolean("persist.vendor.otg_control", false);
    }

    public static boolean isSupportPin() {
        try {
            Class<?> cls = Class.forName("miui.app.MiuiFreeFormManager");
            return ((Boolean) cls.getDeclaredMethod("isSupportPin", null).invoke(cls, new Object[0])).booleanValue();
        } catch (Exception e) {
            Log.e("SettingsFeatures", "reflect error when get isSupportPin state", e);
            return false;
        }
    }

    public static boolean isSupportSettingsHaptic(Context context) {
        Set<String> set = supportSettingHapticDeviceSet;
        if (set.isEmpty()) {
            set.addAll(Arrays.asList(context.getResources().getStringArray(R.array.support_settings_haptic_device_list)));
        }
        return isSupportAccessibilityHaptic(context) || set.contains(android.os.Build.DEVICE);
    }

    public static boolean isSupportSpeakerAutoClean(Context context) {
        return Arrays.asList(context.getResources().getStringArray(R.array.support_speaker_auto_clean_device_list)).contains(android.os.Build.DEVICE) || SystemProperties.getBoolean("ro.vendor.audio.spk.clean", false);
    }

    public static boolean isSupportUninstallSysApp(Context context) {
        return false;
    }

    public static boolean isSystemHapticNeeded() {
        return HapticFeedbackUtil.isSupportLinearMotorVibrate();
    }

    public static boolean isVipServiceNeeded(Context context) {
        if (UserHandle.myUserId() == 0 && MiuiAboutPhoneUtils.isLocalCnAndChinese()) {
            return context.getPackageManager().queryIntentActivities(new Intent("miui.intent.action.vipservice.new"), SearchUpdater.GOOGLE).size() > 0;
        }
        return false;
    }

    public static boolean isZenModeRuleOn(Context context) {
        ArrayMap arrayMap;
        ZenModeConfig zenModeConfig = SilentModeUtils.getZenModeConfig(context);
        if (zenModeConfig == null || (arrayMap = zenModeConfig.automaticRules) == null) {
            return false;
        }
        Iterator it = arrayMap.values().iterator();
        while (it.hasNext()) {
            if (((ZenModeConfig.ZenRule) it.next()).enabled) {
                return true;
            }
        }
        return false;
    }

    private static boolean supportIncallShow(Context context) {
        try {
            Bundle bundle = context.getApplicationContext().getPackageManager().getApplicationInfo("com.android.incallui", 128).metaData;
            if (bundle != null) {
                return bundle.getInt("incall_incoming_show", 0) >= 1;
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean supportQuickReply() {
        try {
            Class<?> cls = Class.forName("android.util.MiuiMultiWindowUtils");
            return ((Boolean) cls.getDeclaredMethod("supportQuickReply", null).invoke(cls, new Object[0])).booleanValue();
        } catch (Exception e) {
            Log.e("SettingsFeatures", "reflect error when get supportQuickReply state", e);
            return false;
        }
    }
}
