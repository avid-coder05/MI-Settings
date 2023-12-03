package com.android.settings.search.tree;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;
import com.android.settings.MiuiUtils;
import com.android.settings.development.AdbInputPreferenceController;
import com.android.settings.development.AdbInstallPreferenceController;
import com.android.settings.development.AdbPreferenceController;
import com.android.settings.development.BluetoothAbsoluteVolumePreferenceController;
import com.android.settings.development.BluetoothAptxAdaptiveModePreferenceController;
import com.android.settings.development.BluetoothPageScanPreferenceController;
import com.android.settings.development.CameraLaserSensorPreferenceController;
import com.android.settings.development.ClearAdbKeysPreferenceController;
import com.android.settings.development.CoolColorTemperaturePreferenceController;
import com.android.settings.development.DarkUIPreferenceController;
import com.android.settings.development.DeviceLockStateController;
import com.android.settings.development.EmulateDisplayCutoutPreferenceController;
import com.android.settings.development.FreeformWindowsPreferenceController;
import com.android.settings.development.HdcpCheckingPreferenceController;
import com.android.settings.development.LocalTerminalPreferenceController;
import com.android.settings.development.LogPersistPreferenceController;
import com.android.settings.development.LowFlickerBacklightController;
import com.android.settings.development.MiuiOptimizationController;
import com.android.settings.development.OemUnlockPreferenceController;
import com.android.settings.development.SecondSpaceDeleteController;
import com.android.settings.development.ShowFirstCrashDialogPreferenceController;
import com.android.settings.development.SpeedModeToolsPreferenceController;
import com.android.settings.development.VerifyAppsOverUsbPreferenceController;
import com.android.settings.security.SecurityEncryptionPreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.search.SettingsTree;
import java.util.LinkedList;
import java.util.Map;
import miui.yellowpage.YellowPageStatistic;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class DevelopmentSettingsTree extends SettingsTree {
    private static final String A2DP_OFFLOAD_SUPPORTED_PROPERTY = "ro.bluetooth.a2dp_offload.supported";
    private static final String ACTUAL_LOGPERSIST_PROPERTY_ENABLE = "logd.logpersistd.enable";
    private static final String BLUETOOTH_DISABLE_A2DP_HW_OFFLOAD = "bluetooth_disable_a2dp_hw_offload";
    public static final String BLUETOOTH_DISABLE_ABSOLUTE_VOLUME = "bluetooth_disable_absolute_volume";
    public static final String BLUETOOTH_ENABLE_PAGE_SCAN = "bluetooth_enable_page_scan";
    public static final String BLUETOOTH_ENABLE_PTS_TEST_KEY = "bluetooth_enable_pts_test";
    private static final String BLUETOOTH_MAX_CONNECTED_AUDIO_DEVICES_STRING = "bluetooth_max_connected_audio_devices_string";
    private static final String BLUETOOTH_SELECT_A2DP_CODEC_APTXADAPTIVE_MODE = "bluetooth_select_a2dp_codec_aptxadaptive_mode";
    private static final String BOOTLOADER_STATUS = "bootloader_status";
    private static final String CAMERA_LASER_SENSOR_SWITCH = "camera_laser_sensor_switch";
    private static final String CLEAR_ADB_KEYS = "clear_adb_keys";
    private static final String COLOR_TEMPERATURE = "color_temperature";
    public static final String COM_ANDROID_TRACEUR = "com.android.traceur";
    private static final String DARK_UI_MODE = "dark_ui_mode";
    public static final String DEBUG_DEBUGGING_CATEGORY = "debug_debugging_category";
    private static final String DEVELOPMENT_SETTINGS_TITLE = "development_settings_title";
    private static final String DISPLAY_CUTOUT_EMULATION = "display_cutout_emulation";
    private static final String ENABLE_ADB = "enable_adb";
    private static final String ENABLE_FREEFORM_SUPPORT = "enable_freeform_support";
    private static final String ENABLE_TERMINAL_TITLE = "enable_terminal_title";
    private static final String FIVEG_NRCA_SETTINGS_TITLE = "fiveg_nrca_switch_title";
    private static final String FIVEG_SA_VICE_SWITCH_TITLE = "fiveg_sa_vice_switch_title";
    private static final String HDCP_CHECKING_TITLE = "hdcp_checking_title";
    private static final String LOW_FLICKER_BACKLIGHT_TITLE = "low_dc_light_title";
    private static final String MIUI_EXPERIENCE_OPTIMIZATION = "miui_experience_optimization";
    private static final String MOBILE_DATA_ALWAYS_ON = "mobile_data_always_on";
    private static final String OEM_UNLOCK_ENABLE = "oem_unlock_enable";
    private static final String SECONDSPACE_DELETE_SPACE_TITLE = "secondspace_delete_space_title";
    private static final String SECURITY_ENCRYPTION_TITLE = "security_encryption_title";
    private static final String SELECT_LOGD_OFF_SIZE_MARKER_VALUE = "32768";
    private static final String SELECT_LOGD_SIZE_PROPERTY = "persist.logd.size";
    private static final String SELECT_LOGD_TAG_PROPERTY = "persist.log.tag";
    private static final String SELECT_LOGD_TAG_SILENCE = "Settings";
    private static final String SELECT_LOGPERSIST_TITLE = "select_logpersist_title";
    private static final String SHOW_FIRST_CRASH_DIALOG = "show_first_crash_dialog";
    private static final String SPEED_MODE_TITLE = "speed_mode";
    public static final String SYSTEM_TRACING = "system_tracing";
    private static final String UNLOCK_SET_ENTER_SYSTEM = "unlock_set_enter_system";
    private static final String USB_ADB_INPUT = "usb_adb_input";
    private static final String USB_INSTALL_APP = "usb_install_app";
    private static final String VERIFY_APPS_OVER_USB_TITLE = "verify_apps_over_usb_title";
    private static final String WAIT_FOR_DEBUGGER = "wait_for_debugger";
    private static final String WIFI_CONNECTED_MAC_RANDOMIZATION = "wifi_connected_mac_randomization";
    private static final Map<String, AbstractPreferenceController> controllerMap = new ArrayMap();
    private String mTitle;

    protected DevelopmentSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
        this.mTitle = jSONObject.optString("title");
        buildPreferenceControllersMap(context);
    }

    private void buildPreferenceControllersMap(Context context) {
        Map<String, AbstractPreferenceController> map = controllerMap;
        if (map.isEmpty()) {
            map.put(OEM_UNLOCK_ENABLE, new OemUnlockPreferenceController(context, null, null));
            map.put(USB_INSTALL_APP, new AdbInstallPreferenceController(null));
            map.put(USB_ADB_INPUT, new AdbInputPreferenceController(null));
            map.put(VERIFY_APPS_OVER_USB_TITLE, new VerifyAppsOverUsbPreferenceController(context));
            map.put(ENABLE_TERMINAL_TITLE, new LocalTerminalPreferenceController(context));
            map.put(HDCP_CHECKING_TITLE, new HdcpCheckingPreferenceController(context));
            map.put(COLOR_TEMPERATURE, new CoolColorTemperaturePreferenceController(context));
            map.put(SECONDSPACE_DELETE_SPACE_TITLE, new SecondSpaceDeleteController(null, null));
            map.put(CLEAR_ADB_KEYS, new ClearAdbKeysPreferenceController(context, null));
            map.put(SELECT_LOGPERSIST_TITLE, new LogPersistPreferenceController(context, null, null));
            map.put(BLUETOOTH_SELECT_A2DP_CODEC_APTXADAPTIVE_MODE, new BluetoothAptxAdaptiveModePreferenceController(context, null, null));
            map.put(CAMERA_LASER_SENSOR_SWITCH, new CameraLaserSensorPreferenceController(context));
            map.put(DARK_UI_MODE, new DarkUIPreferenceController(context));
            map.put(MIUI_EXPERIENCE_OPTIMIZATION, new MiuiOptimizationController(context));
            map.put(BOOTLOADER_STATUS, new DeviceLockStateController(null));
            map.put(ENABLE_FREEFORM_SUPPORT, new FreeformWindowsPreferenceController(context));
            map.put(SHOW_FIRST_CRASH_DIALOG, new ShowFirstCrashDialogPreferenceController(context));
            map.put(DISPLAY_CUTOUT_EMULATION, new EmulateDisplayCutoutPreferenceController(context));
            map.put(ENABLE_ADB, new AdbPreferenceController(context, null));
            map.put(SECURITY_ENCRYPTION_TITLE, new SecurityEncryptionPreferenceController(context, SecurityEncryptionPreferenceController.PREF_KEY_SECURITY_ENCRYPTION));
            map.put(BLUETOOTH_DISABLE_ABSOLUTE_VOLUME, new BluetoothAbsoluteVolumePreferenceController(context));
            map.put(BLUETOOTH_ENABLE_PAGE_SCAN, new BluetoothPageScanPreferenceController(context));
            map.put(LOW_FLICKER_BACKLIGHT_TITLE, new LowFlickerBacklightController(context));
            map.put("speed_mode", new SpeedModeToolsPreferenceController(context, "speed_mode"));
        }
    }

    public LinkedList<SettingsTree> getSons() {
        if (DEVELOPMENT_SETTINGS_TITLE.equals(getColumnValue("resource"))) {
            LinkedList sons = super.getSons();
            if (sons != null) {
                for (int size = sons.size() - 1; size >= 0; size--) {
                    SettingsTree settingsTree = (SettingsTree) sons.get(size);
                    if (Boolean.parseBoolean(settingsTree.getColumnValue("temporary"))) {
                        settingsTree.removeSelf();
                    }
                }
            }
            String stringFromSpecificPackage = MiuiUtils.getStringFromSpecificPackage(((SettingsTree) this).mContext, COM_ANDROID_TRACEUR, SYSTEM_TRACING);
            if (!TextUtils.isEmpty(stringFromSpecificPackage)) {
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("title", stringFromSpecificPackage);
                    jSONObject.put(YellowPageStatistic.Display.CATEGORY, DEBUG_DEBUGGING_CATEGORY);
                    jSONObject.put("temporary", true);
                    addSon(SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this));
                } catch (JSONException unused) {
                }
            }
        }
        return super.getSons();
    }

    /* JADX WARN: Code restructure failed: missing block: B:113:0x019a, code lost:
    
        if (r0.isAvailable() == false) goto L114;
     */
    /* JADX WARN: Code restructure failed: missing block: B:94:0x014b, code lost:
    
        if (android.text.TextUtils.isEmpty(android.provider.Settings.Global.getString(((com.android.settingslib.search.SettingsTree) r8).mContext.getContentResolver(), "debug_app")) != false) goto L95;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected int getStatus() {
        /*
            Method dump skipped, instructions count: 620
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.search.tree.DevelopmentSettingsTree.getStatus():int");
    }

    protected String getTitle(boolean z) {
        return !TextUtils.isEmpty(this.mTitle) ? this.mTitle : super.getTitle(z);
    }
}
