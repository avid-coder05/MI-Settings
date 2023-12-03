package com.android.settings;

import android.app.ActivityThread;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.input.InputManager;
import android.media.AudioManager;
import android.net.ConnectivitySettingsManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.security.ChooseLockSettingsHelper;
import android.security.MiuiLockPatternUtils;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import androidx.preference.PreferenceManager;
import com.android.settings.accessibility.accessibilitymenu.AccessibilityMenuService;
import com.android.settings.accessibility.utils.MiuiAccessibilityUtils;
import com.android.settings.applications.DefaultAppsHelper;
import com.android.settings.custs.CellBroadcastUtil;
import com.android.settings.dangerousoptions.DangerousOptionsUtil;
import com.android.settings.datetime.DualClockHealper;
import com.android.settings.development.SpeedModeToolsPreferenceController;
import com.android.settings.device.MiuiAboutPhoneUtils;
import com.android.settings.device.UpdateBroadcastManager;
import com.android.settings.display.DarkModeTimeModeUtil;
import com.android.settings.display.DisplayUtils;
import com.android.settings.display.LargeFontUtils;
import com.android.settings.display.PaperModeTimeModeUtil;
import com.android.settings.inputmethod.InputMethodFunctionSelectUtils;
import com.android.settings.network.telephony.ToggleSubscriptionDialogActivity;
import com.android.settings.notify.SettingsNotifyHelper;
import com.android.settings.privacypassword.PrivacyPasswordManager;
import com.android.settings.privacypassword.XiaomiAccountUtils;
import com.android.settings.report.InternationalCompat;
import com.android.settings.search.ReverseSearchService;
import com.android.settings.search.SearchUpdater;
import com.android.settings.shoulderkey.ShoulderKeyShortcutUtils;
import com.android.settings.special.ExternalRamController;
import com.android.settings.usagestats.controller.DeviceUsageController;
import com.android.settings.usagestats.utils.AppLimitStateUtils;
import com.android.settings.usagestats.utils.CacheUtils;
import com.android.settings.usagestats.utils.CommonUtils;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.vpn2.MiuiVpnUtils;
import com.android.settings.wifi.MiuiWifiService;
import com.android.settings.wifi.WifiConfigActivity;
import com.android.settings.wifi.operatorutils.Operator;
import com.android.settings.wifi.operatorutils.OperatorFactory;
import com.android.settingslib.util.ToastUtil;
import com.android.settingslib.utils.ThreadUtils;
import com.xiaomi.micloudsdk.utils.PermissionUtils;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import miui.accounts.ExtraAccountManager;
import miui.app.constants.ThemeManagerConstants;
import miui.os.Build;
import miui.provider.ExtraContacts;
import miui.provider.Wifi;
import miui.security.SecurityManager;
import miui.util.FeatureParser;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes.dex */
public class MiuiSettingsReceiver extends BroadcastReceiver {
    private static final String BACKUP_STORAGE_PATH;
    private int mLastSubId = -1;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory());
        String str = File.separator;
        sb.append(str);
        sb.append(YellowPageContract.Provider.PNAME_DEFAULT);
        sb.append(str);
        sb.append(ExtraContacts.Calls.BACKUP_PARAM);
        sb.append(str);
        sb.append("AllBackup");
        BACKUP_STORAGE_PATH = sb.toString();
    }

    private static boolean IsWifiAutoJoinRestored(Context context) {
        boolean z = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("wifi_auto_join_restored", false);
        Log.i("MiuiSettingsReceiver", "isWifiAutoJoinRestored = " + z);
        return z;
    }

    private void SaveSystemStatusWhenBootAction(Context context) {
        SharedPreferences.Editor edit = context.getSharedPreferences("ExternalRamStatus", 0).edit();
        boolean isExternalRamOpen = isExternalRamOpen();
        edit.putBoolean("isExternalRamOn", isExternalRamOpen);
        edit.apply();
        Settings.Global.putInt(context.getContentResolver(), "isExternalRamOn", isExternalRamOpen ? 1 : 0);
        Log.i("MiuiSettingsReceiver", "SaveSystemStatusWhenBootAction ExternalRamStatus: " + isExternalRamOpen);
    }

    private void adjustVolume(Context context) {
        if (Build.IS_CM_CUSTOMIZATION_TEST) {
            try {
                if (ActivityThread.getPackageManager().isFirstBoot()) {
                    AudioManager audioManager = (AudioManager) context.getSystemService("audio");
                    audioManager.setStreamVolume(2, 1, 0);
                    audioManager.setStreamVolume(3, 1, 0);
                    audioManager.setStreamVolume(4, 1, 0);
                    audioManager.setStreamVolume(5, 1, 0);
                    audioManager.setStreamVolume(6, 1, 0);
                }
            } catch (RemoteException unused) {
            }
        }
        String str = android.os.Build.DEVICE;
        if (("gemini".equals(str) || "capricorn".equals(str)) && !SystemProperties.getBoolean("persist.sys.call_vol_increased", false)) {
            AudioManager audioManager2 = (AudioManager) context.getSystemService("audio");
            int streamVolume = audioManager2.getStreamVolume(0);
            if (streamVolume <= 5) {
                audioManager2.setStreamVolume(0, (streamVolume * 11) / 5, 0);
            }
            SystemProperties.set("persist.sys.call_vol_increased", "true");
        }
    }

    private void checkPrivacyPasswordEnable(Context context) {
        if (Settings.Secure.getInt(context.getContentResolver(), "privacy_password_status_is_record", 0) == 1) {
            return;
        }
        PrivacyPasswordManager privacyPasswordManager = PrivacyPasswordManager.getInstance(context);
        privacyPasswordManager.setUsedPrivacyInBussiness(privacyPasswordManager.havePattern());
        Settings.Secure.putInt(context.getContentResolver(), "privacy_password_status_is_record", 1);
    }

    private void checkStateForGDPR(Context context) {
        if (!Build.IS_INTERNATIONAL_BUILD || PermissionUtils.isGdprPermissionGranted(context) || PreferenceManager.getDefaultSharedPreferences(context).getBoolean("DELETED_SYNCED_DATA", false)) {
            return;
        }
        Intent intent = new Intent(context, MiuiWifiService.class);
        intent.setAction("miui.intent.action.DELETE_WIFI_SYNCED_DATE");
        startMiuiWifiService(context, intent);
    }

    private void checkXiaomiAccount(Context context) {
        boolean haveAccessControlPassword = ((SecurityManager) context.getSystemService("security")).haveAccessControlPassword();
        boolean isLoginXiaomiAccount = XiaomiAccountUtils.isLoginXiaomiAccount(context);
        if (haveAccessControlPassword && isLoginXiaomiAccount) {
            Settings.Secure.putString(context.getContentResolver(), "app_lock_add_account_md5", XiaomiAccountUtils.getLoginedAccountMd5(context));
            Settings.Secure.putString(context.getContentResolver(), "privacy_add_account_md5", XiaomiAccountUtils.getLoginedAccountMd5(context));
        }
    }

    private void checkXiaomiAccountForPrivacyPassword(Context context) {
        checkPrivacyPasswordEnable(context);
        if (Settings.Secure.getInt(context.getContentResolver(), "password_has_promotioned", 1) == 1) {
            checkXiaomiAccount(context);
            if (Settings.Secure.getInt(context.getContentResolver(), "com_miui_applicatinlock_use_fingerprint_state", 1) == 2) {
                Settings.Secure.putInt(context.getContentResolver(), "fingerprint_apply_to_privacy_password", 2);
            }
            Settings.Secure.putInt(context.getContentResolver(), "password_has_promotioned", 0);
        }
    }

    private static void clearSecretAlbumLockPattern(Context context, Intent intent) {
        new MiuiLockPatternUtils(context, 2).saveMiuiLockPattern((List) null);
    }

    private static void clearSyncStates(Context context, Intent intent) {
        if (intent.getIntExtra(ExtraAccountManager.EXTRA_UPDATE_TYPE, -1) != 1) {
            return;
        }
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Wifi.SyncState.CONTENT_URI;
        if (contentResolver.acquireProvider(uri) != null) {
            context.getContentResolver().delete(uri, null, null);
        }
        ContentResolver contentResolver2 = context.getContentResolver();
        Uri uri2 = Wifi.CONTENT_URI;
        if (contentResolver2.acquireProvider(uri2) != null) {
            context.getContentResolver().delete(uri2, null, null);
        }
    }

    private static void configCustomWifi(final Context context, final int i) {
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.MiuiSettingsReceiver.2
            @Override // java.lang.Runnable
            public void run() {
                Settings.Global.putInt(context.getContentResolver(), "wifi_networks_available_notification_on", 0);
                if (!Build.IS_GLOBAL_BUILD) {
                    ConnectivitySettingsManager.setPrivateDnsDefaultMode(context, 1);
                }
                if (!RegionUtils.IS_JP_KDDI) {
                    Settings.Global.putInt(context.getContentResolver(), "wifi_wakeup_enabled", 0);
                }
                Operator operatorFactory = OperatorFactory.getInstance(context);
                if (operatorFactory != null) {
                    int i2 = i;
                    if (i2 == 1) {
                        operatorFactory.updateWifiConfig();
                    } else if (i2 == 2) {
                        operatorFactory.registerReceiver();
                    } else if (i2 == 3) {
                        operatorFactory.stopTethering();
                    } else {
                        Log.e("MiuiSettingsReceiver", "Ignoring unknown cmd: " + i);
                    }
                }
            }
        });
    }

    private void initImeSkinFollowSystem(Context context) {
        if (InputMethodFunctionSelectUtils.isKeyBoardSkinFollowSystemDefault(context)) {
            InputMethodFunctionSelectUtils.setPreferenceCheckedValue(context, "keyboard_skin_follow_system_enable", 1);
        }
    }

    private void initSystemSettings(Context context) {
        MiuiUtils miuiUtils = MiuiUtils.getInstance();
        if (miuiUtils.isTouchSensitive(context)) {
            miuiUtils.enableTouchSensitive(context, true);
        }
        try {
            CellBroadcastUtil.setCellbroadcastEnabledSetting(context);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        MiuiUtils.enableVolumKeyWakeUp(Settings.System.getInt(context.getContentResolver(), "volumekey_wake_screen", 0) == 1);
        showPowerModeNotification(context);
        if (PaperModeTimeModeUtil.isPaperModeTimeEnable(context)) {
            DisplayUtils.setScreenPaperModeGetLocation(context);
            PaperModeTimeModeUtil.startPaperModeAutoTime(context, PaperModeTimeModeUtil.getPaperModeSchedulerType(context));
        }
        if (DarkModeTimeModeUtil.isDarkModeTimeEnable(context)) {
            DarkModeTimeModeUtil.startDarkModeAutoTime(context, true);
        }
        if (FeatureParser.getBoolean("support_edge_handgrip", false)) {
            if (Settings.System.getInt(context.getContentResolver(), "edge_handgrip", -1) == -1) {
                Settings.System.putInt(context.getContentResolver(), "edge_handgrip", 1);
            }
            if (Settings.System.getInt(context.getContentResolver(), "edge_handgrip", 0) == 1) {
                MiuiSettingsCompatibilityHelper.switchInputManagerTouchEdgeMode((InputManager) context.getSystemService("input"), 2);
            }
            if (Settings.System.getInt(context.getContentResolver(), "edge_handgrip_photo", -1) == -1) {
                Settings.System.putInt(context.getContentResolver(), "edge_handgrip_photo", 1);
            }
            Settings.System.putInt(context.getContentResolver(), "edge_handgrip_screenshot", 0);
        }
    }

    private void initZenModeDefaultValues(Context context) {
        if (Build.IS_TABLET || Build.VERSION.SDK_INT < 30) {
            return;
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION);
        try {
            NotificationManager.Policy notificationPolicy = notificationManager.getNotificationPolicy();
            notificationManager.setNotificationPolicy(new NotificationManager.Policy(notificationPolicy.priorityCategories & (-17) & (-9), notificationPolicy.priorityCallSenders, notificationPolicy.priorityMessageSenders, notificationPolicy.suppressedVisualEffects, notificationPolicy.state));
        } catch (Exception e) {
            Log.w("MiuiSettingsReceiver", "initZenModeDefaultValues:fail");
            e.printStackTrace();
        }
    }

    public static boolean isExternalRamOpen() {
        int i;
        try {
            i = Integer.parseInt(SystemProperties.get(ExternalRamController.MIUI_EXTM_ENABLE));
        } catch (Exception e) {
            e.printStackTrace();
            i = -1;
        }
        return i == 1;
    }

    private void removeOldSettings(Context context) {
        if ("middle".equals(SystemProperties.get("persist.sys.aries.power_profile", "middle"))) {
            return;
        }
        SystemProperties.set("persist.sys.aries.power_profile", "middle");
        Settings.System.putString(context.getContentResolver(), "power_mode", "middle");
    }

    public static void restoreWifiAutoJoinDisabled(Context context) {
        if (IsWifiAutoJoinRestored(context)) {
            return;
        }
        HashSet disableWifiAutoConnectSsid = MiuiSettings.System.getDisableWifiAutoConnectSsid(context);
        int size = disableWifiAutoConnectSsid.size();
        if (size > 4) {
            Log.i("MiuiSettingsReceiver", "current disable wifi auto join num is : " + size);
            disableWifiAutoConnectSsid.clear();
            MiuiSettings.System.setDisableWifiAutoConnectSsid(context, disableWifiAutoConnectSsid);
        }
        setWifiAutoJoinRestored(context, true);
    }

    private void setDefaultInputMethod(Context context, String str, boolean z) {
        String str2;
        ContentResolver contentResolver = context.getContentResolver();
        String string = Settings.Secure.getString(contentResolver, "enabled_input_methods");
        if (TextUtils.isEmpty(string)) {
            return;
        }
        TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(':');
        simpleStringSplitter.setString(string);
        while (true) {
            if (!simpleStringSplitter.hasNext()) {
                str2 = "";
                break;
            }
            str2 = simpleStringSplitter.next();
            if (!TextUtils.isEmpty(str2) && str2.contains(str)) {
                break;
            }
        }
        if (TextUtils.isEmpty(str2)) {
            return;
        }
        String string2 = Settings.Secure.getString(contentResolver, "input_methods_subtype_history");
        if (str2.equals(Settings.Secure.getString(contentResolver, "default_input_method"))) {
            return;
        }
        if (z || TextUtils.isEmpty(string2) || !string2.contains(str2)) {
            Settings.Secure.putString(contentResolver, "default_input_method", str2);
        }
    }

    private static void setWifiAutoJoinRestored(Context context, boolean z) {
        Log.i("MiuiSettingsReceiver", "setWifiAutoJoinRestored = " + z);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("wifi_auto_join_restored", z).commit();
    }

    private void showEdgeModeNotification(Context context) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setDefaults(2);
        builder.setSmallIcon(R.drawable.ic_launcher_settings);
        int i = R.string.edge_mode_notification_title;
        builder.setContentTitle(context.getString(i));
        builder.setContentText(context.getString(R.string.edge_mode_notification_summary));
        builder.setWhen(System.currentTimeMillis());
        Intent intent = new Intent();
        intent.setClassName(context, "com.android.settings.Settings$EdgeModeSettingsActivity");
        builder.setContentIntent(PendingIntent.getActivity(context, 0, intent, 268435456));
        Notification build = builder.build();
        build.flags = 16;
        build.extraNotification.setEnableFloat(true);
        build.extraNotification.setMessageCount(0);
        ((NotificationManager) context.getSystemService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION)).notify(i, build);
    }

    private static void showPowerModeNotification(Context context) {
        if ("high".equals(SystemProperties.get("persist.sys.aries.power_profile", "middle"))) {
            context.sendBroadcast(new Intent("miui.intent.action.POWER_MODE_CHANGE"));
        }
    }

    private static void showWifiLoginDialog(Context context, WifiConfiguration wifiConfiguration, boolean z) {
        if (wifiConfiguration == null || wifiConfiguration.SSID == null) {
            return;
        }
        if (wifiConfiguration.allowedKeyManagement.get(0) && wifiConfiguration.wepKeys[0] == null) {
            return;
        }
        Intent intent = new Intent(context, WifiConfigActivity.class);
        intent.putExtra("wifi_config", wifiConfiguration);
        intent.putExtra("isSlave", z);
        intent.addFlags(268435456);
        context.startActivityAsUser(intent, UserHandle.CURRENT);
    }

    private void startMiuiWifiService(Context context, Intent intent) {
        try {
            context.startService(intent);
        } catch (IllegalStateException | SecurityException e) {
            Log.w("MiuiSettingsReceiver", e.toString());
        }
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        boolean hasIndependentTimer = CommonUtils.hasIndependentTimer(context);
        if ("android.intent.action.BOOT_COMPLETED".equals(action)) {
            Log.i("MiuiSettingsReceiver", "onReceive: ACTION_BOOT_COMPLETED");
            initSystemSettings(context);
            adjustVolume(context);
            removeOldSettings(context);
            configCustomWifi(context, 1);
            new ChooseLockSettingsHelper(context).setPrivacyModeEnabled(false);
            checkXiaomiAccountForPrivacyPassword(context);
            restoreWifiAutoJoinDisabled(context);
            checkStateForGDPR(context);
            Intent intent2 = new Intent(context, MiuiWifiService.class);
            intent2.setAction("android.intent.action.BOOT_COMPLETED");
            startMiuiWifiService(context, intent2);
            DangerousOptionsUtil.checkDangerousOptions(context, false);
            SaveSystemStatusWhenBootAction(context);
            boolean z = miui.os.Build.IS_INTERNATIONAL_BUILD;
            if (!z && !hasIndependentTimer) {
                DeviceUsageController.ensureServiceRunning(context);
                AppLimitStateUtils.initAllLimitApps(context, false);
            }
            if (!z && hasIndependentTimer && !CacheUtils.hasMoveComplete(context)) {
                CacheUtils.moveCache(context);
            }
            if (!SettingsFeatures.hasMarketName()) {
                try {
                    if (MiuiSettings.System.getDeviceName(context).equals(context.getString(MiuiAboutPhoneUtils.getDefaultNameRes())) && TextUtils.isEmpty(MiuiAboutPhoneUtils.getStringPreference(context, "auto_renamed"))) {
                        String modelNumber = MiuiAboutPhoneUtils.getModelNumber();
                        MiuiUtils.setDeviceName(context, modelNumber);
                        MiuiAboutPhoneUtils.setStringPreference(context, "auto_renamed", modelNumber);
                        context.sendBroadcast(new Intent("com.miui.action.edit_device_name"));
                    }
                } catch (Exception unused) {
                    Log.e("MiuiSettingsReceiver", "device rename failed!");
                }
            }
            ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.MiuiSettingsReceiver.1
                @Override // java.lang.Runnable
                public void run() {
                    File file = new File(MiuiSettingsReceiver.BACKUP_STORAGE_PATH);
                    if (file.exists()) {
                        return;
                    }
                    Log.d("MiuiSettingsReceiver", "create backup folder:" + file.getAbsolutePath());
                    if (file.mkdirs()) {
                        return;
                    }
                    Log.d("MiuiSettingsReceiver", "create backup folder fail");
                }
            });
            initImeSkinFollowSystem(context);
            try {
                if (Integer.parseInt(SystemProperties.get("miui.extm.low_data")) == 1) {
                    ToastUtil.show(context, R.string.external_ram_remind_toast, 1);
                }
            } catch (Exception e) {
                Log.d("MiuiSettingsReceiver", "onReceive: ", e);
            }
        } else if ("android.net.wifi.CONFIGURED_NETWORKS_CHANGE".equals(action) && intent.getIntExtra("changeReason", 3) == 1) {
            intent.setComponent(new ComponentName(context, MiuiWifiService.class));
            startMiuiWifiService(context, intent);
        } else if ("android.net.wifi.observed_accesspionts_changed".equals(action)) {
            Intent intent3 = new Intent(context, MiuiWifiService.class);
            intent3.setAction("android.net.wifi.observed_accesspionts_changed");
            startMiuiWifiService(context, intent3);
        } else if (ExtraAccountManager.LOGIN_ACCOUNTS_PRE_CHANGED_ACTION.equals(action)) {
            clearSyncStates(context, intent);
            clearSecretAlbumLockPattern(context, intent);
        } else if ("miui.intent.action.WIFI_CONNECTION_FAILURE".equals(action)) {
            showWifiLoginDialog(context, (WifiConfiguration) intent.getParcelableExtra("wifiConfiguration"), Boolean.valueOf(intent.getBooleanExtra("isSlave", false)).booleanValue());
        } else if ("android.provider.Telephony.SECRET_CODE".equals(action)) {
            boolean z2 = !MiuiSettings.System.getBoolean(context.getContentResolver(), "enable_demo_mode", false);
            MiuiSettings.System.putBoolean(context.getContentResolver(), "enable_demo_mode", z2);
            StringBuilder sb = new StringBuilder();
            sb.append(z2 ? ToggleSubscriptionDialogActivity.ARG_enable : "disable");
            sb.append(" demo mode.");
            Toast.makeText(context, sb.toString(), 100).show();
        } else if ("android.net.wifi.STATE_CHANGE".equals(action)) {
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
            if (networkInfo == null || networkInfo.getType() != 1) {
                return;
            }
            if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                Intent intent4 = new Intent(context, MiuiWifiService.class);
                intent4.setAction("miui.intent.action.UPDATE_CURRENT_WIFI_CONFIGURATION");
                startMiuiWifiService(context, intent4);
            } else if (networkInfo.getState() == NetworkInfo.State.DISCONNECTED) {
                configCustomWifi(context, 2);
            }
        } else if ("android.intent.action.PRE_BOOT_COMPLETED".equals(action)) {
            if (miui.os.Build.IS_CM_CUSTOMIZATION) {
                setDefaultInputMethod(context, "iflytek", false);
            }
            if (MiuiUtils.isDeviceProvisioned(context)) {
                Log.i("MiuiSettingsReceiver", "onReceive: PRE_BOOT_COMPLETED init dual clock");
                DualClockHealper.dualTimeZoneInit(context);
            }
            if (miui.os.Build.IS_GLOBAL_BUILD && MiuiSettings.System.getBoolean(context.getContentResolver(), "vibrate_in_normal", MiuiSettings.System.VIBRATE_IN_NORMAL_DEFAULT)) {
                Settings.System.putInt(context.getContentResolver(), "vibrate_when_ringing", 1);
            }
            MiuiUtils.closeSensorOff(context);
            MiuiUtils.resetTimeZoneIfNeed(context);
            MiuiUtils.resetDualClockIfNeed(context);
            DefaultAppsHelper.loadDefaultVideoPlayer(context);
            MiuiUtils.sendBroadcastToTheme(context);
            MiuiUtils.sendBroadcastToHuanji(context);
        } else if ("miui.intent.action.MIUI_REGION_CHANGED".equals(action)) {
            CellBroadcastUtil.setCellbroadcastEnabledSetting(context);
            configCustomWifi(context, 1);
        } else if ("android.net.vpn.SETTINGS".equals(action)) {
            MiuiVpnUtils.turnOnVpn(context, intent.getBooleanExtra("vpn_on", false));
        } else if ("android.intent.action.SIM_STATE_CHANGED".equals(action)) {
            SearchUpdater.getInstance().handleUpdate(SearchUpdater.SIM);
            SearchUpdater.getInstance().handleUpdate(64);
            configCustomWifi(context, 1);
        } else if ("xiaomi.appindex.action.FUNCTIONS_FINISHED".equals(action)) {
            SearchUpdater.getInstance().handleUpdate(-1);
            ReverseSearchService.createIndex();
        } else if ("android.intent.action.LOCALE_CHANGED".equals(action)) {
            if (FeatureParser.getBoolean("support_cm_language_bo", false) && "bo".equals(context.getResources().getConfiguration().locale.getLanguage())) {
                setDefaultInputMethod(context, "sogou.xiaomi", true);
            }
        } else if ("android.provision.action.PROVISION_COMPLETE".equals(action)) {
            boolean z3 = miui.os.Build.IS_GLOBAL_BUILD;
            if (!z3 || RegionUtils.IS_FR_ORANGE) {
                DualClockHealper.dualTimeZoneInit(context);
            } else {
                Settings.System.putInt(context.getContentResolver(), AodStylePreferenceController.AUTO_DUAL_CLOCK, 1);
                if (RegionUtils.IS_FR_SFR) {
                    MiuiUtils.resetSFRDualClock(context);
                }
            }
            if (FeatureParser.getBoolean("support_edge_handgrip", false)) {
                showEdgeModeNotification(context);
            }
            DefalutApplicationLoader.load(context);
            DefaultAppsHelper.loadDefaultBrowser(context);
            DefaultAppsHelper.loadDefaultVideoPlayer(context);
            Log.d("MiuiSettingsReceiver", "onReceive: PROVISION_COMPLETE_BROADCAST");
            if (!z3) {
                Settings.Global.putString(context.getContentResolver(), "ntp_server", "pool.ntp.org");
            }
            InternationalCompat.enableNetworkRequest(true);
            initZenModeDefaultValues(context);
            Settings.Global.putLong(context.getContentResolver(), "Activation_time", System.currentTimeMillis());
        } else if ("com.android.updater.action.TOGGLE_SUPERSCRIPT".equals(action)) {
            UpdateBroadcastManager.toggleSuperscript(context, intent.getExtras());
        } else if ("miui.intent.action.UPDATE_SSID_AUTO_CONNECT".equals(action) || "miui.intent.action.FORCE_SELECT_WIFI".equals(action)) {
            intent.setComponent(new ComponentName(context, MiuiWifiService.class));
            startMiuiWifiService(context, intent);
        } else if ("com.xiaomi.account.action.MODIFY_SAFE_PHONE".equals(action)) {
            int intExtra = intent.getIntExtra("bind_phone_type", 0);
            SettingsNotifyHelper.setPhoneRecycled(context, intExtra == 1);
            UpdateBroadcastManager.updateSuperscript(context, 2, intExtra == 1);
            Log.i("MiuiSettingsReceiver", "Phone recycled/bind, and pleae notify on settings, value=" + intExtra);
        } else if (action.equals("com.xiaomi.action.MICLOUD_PRIVACY_DENIED")) {
            checkStateForGDPR(context);
        } else if (action.equals("miui.intent.action.settings.SCHEDULE_DEVICE_USAGE_MONITOR")) {
            if (miui.os.Build.IS_INTERNATIONAL_BUILD || hasIndependentTimer) {
                return;
            }
            Log.i("MiuiSettingsReceiver", "Receive ACTION_DEVICE_USAGE_MONITOR!!!");
            DeviceUsageController.ensureServiceRunning(context);
        } else if ("miui.intent.action.settings.SCHEDULE_APP_LIMIT".equals(action)) {
            if (miui.os.Build.IS_INTERNATIONAL_BUILD || hasIndependentTimer) {
                return;
            }
            Log.i("MiuiSettingsReceiver", "Receive ACTION_APP_LIMIT_INIT!!!");
            AppLimitStateUtils.initAllLimitApps(context, true);
        } else if ("miui.intent.action.settings.SCHEDULE_PROLONG_LIMIT_TIME".equals(action) && intent.hasExtra("pkgName")) {
            AppLimitStateUtils.prolongLimitTime(context, intent.getStringExtra("pkgName"), intent.getIntExtra("remainTime", 0), intent.getLongExtra("showNotificationTime", 0L));
        } else if ("android.intent.action.TIME_SET".equals(action)) {
            if (miui.os.Build.IS_INTERNATIONAL_BUILD || hasIndependentTimer) {
                return;
            }
            AppLimitStateUtils.timeSetAction(context);
        } else if (action.equals("miui.intent.action.ACCESSIBILITY_MENU_INIT")) {
            AccessibilityMenuService.isReceiveA11yMenuInitBrodcast = true;
        } else if (action.equals("easy_mode_update_font")) {
            boolean booleanValue = ((Boolean) intent.getExtra("status", Boolean.FALSE)).booleanValue();
            int i = Settings.System.getInt(context.getContentResolver(), "previous_font", 1);
            if (booleanValue) {
                i = 15;
            }
            LargeFontUtils.sendUiModeChangeMessage(context, i);
        } else if (action.equals("miui.intent.action.MUSIC_UNMUTE_BY_USER_DONE")) {
            Log.w("MiuiSettingsReceiver", "Receive ACTION_MUSIC_UNMUTE_BY_USER_DONE!!!");
            Settings.Global.putInt(context.getContentResolver(), "miui_unmute_by_settings", 0);
        } else if (action.equals("miui.intent.action.START_TALKBACK")) {
            MiuiAccessibilityUtils.enableAccessibility(context);
        } else if (action.equals("android.telephony.action.CARRIER_SIGNAL_REQUEST_NETWORK_FAILED")) {
            int intExtra2 = intent.getIntExtra("android.telephony.extra.DATA_FAIL_CAUSE", 0);
            Log.i("MiuiSettingsReceiver", "Receive ACTION_CARRIER_SIGNAL_REQUEST_NETWORK_FAILED! cause: " + intExtra2);
            if (intExtra2 == 29) {
                configCustomWifi(context, 3);
            }
        } else if (action.equals("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED")) {
            int intExtra3 = intent.getIntExtra("subscription", -1);
            Log.i("MiuiSettingsReceiver", "Receive ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED! subId: " + intExtra3);
            if (intExtra3 != this.mLastSubId) {
                this.mLastSubId = intExtra3;
                configCustomWifi(context, 3);
            }
        } else if (action.equals("miui.intent.action.settings.SPEED_MODE_CLOSED")) {
            Settings.System.putInt(context.getContentResolver(), SpeedModeToolsPreferenceController.SPEED_MODE_KEY, 0);
            context.getContentResolver().notifyChange(Settings.System.getUriFor(SpeedModeToolsPreferenceController.SPEED_MODE_KEY), null);
            ((NotificationManager) context.getSystemService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION)).cancel(null, R.string.speed_mode_noti_title);
        } else if (action.equals("com.miui.shoulderkey.shortcut")) {
            ShoulderKeyShortcutUtils.getInstance(context).showPrompt();
        } else if (action.equals("android.intent.action.LOCKED_BOOT_COMPLETED")) {
            SaveSystemStatusWhenBootAction(context);
            Log.i("MiuiSettingsReceiver", "onReceive: ACTION_LOCKED_BOOT_COMPLETED setExternalRamStatus");
        } else if (action.equals("com.xiaomi.shop.action.NEW_MACHINE_ENTRANCE_CLOSE")) {
            Settings.Global.putInt(context.getContentResolver(), "Is_new_phone", 0);
        }
    }
}
