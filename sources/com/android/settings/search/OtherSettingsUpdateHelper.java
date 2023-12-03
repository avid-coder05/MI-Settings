package com.android.settings.search;

import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.view.IWindowManager;
import android.view.InputDevice;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.textservice.SpellCheckerInfo;
import android.view.textservice.TextServicesManager;
import com.android.settings.FakeCellSettings;
import com.android.settings.MiuiSecuritySettings;
import com.android.settings.MiuiUtils;
import com.android.settings.Utils;
import com.android.settings.lab.MiuiAiPreloadController;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.OldmanHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.securityspace.ConfigUtils;
import miui.theme.ThemeManagerHelper;
import miui.util.CustomizeUtil;
import miui.util.FeatureParser;
import miui.util.HandyModeUtils;

/* loaded from: classes2.dex */
class OtherSettingsUpdateHelper extends BaseSearchUpdateHelper {
    private static final String AI_TOUCH_RESOURCE = "ai_button_title";
    private static final String APP_UPDATER_RESOURCE = "system_apps_updater";
    private static final String AVOID_UI_RESOURCE = "avoid_ui";
    private static final String BATTERY_INDICATOR_STYLE_RESOURCE = "battery_indicator_style";
    private static final String CALL_BREATHING_LIGHT_COLOR_RESOURCE = "call_breathing_light_color";
    private static final String CALL_BREATHING_LIGHT_FREQ_RESOURCE = "call_breathing_light_freq";
    private static final String EMERGENCY_SOS = "emergency_sos_title";
    private static final String FAKECELL_SETTINGS_RESOURCE = "manage_fakecell_settings";
    private static final String HANDY_MODE_RESOURCE = "handy_mode";
    private static final String HOME_XIAOAI_RESOURCE = "voice_assist";
    private static final String INFINITY_DISPLAY_RESOURCE = "infinity_display_title";
    private static final String LOCKSCREEN_MAGAZINE_RESOURCE = "lockscreen_magazine";
    private static final String MIUI_LAB_AI_PRELOAD = "miui_lab_ai_preload_title";
    private static final String MIUI_LAB_RESOURCE = "miui_lab_settings";
    private static final String MI_SERVICE = "mi_service";
    private static final String MI_SERVICE_URL = "http://ab.xiaomi.com/d?url=aHR0cDovL20ubWkuY29tL3Nkaz9waWQ9MTI1MDEmY2lkPTIwMDI3LjAwMDAxJnJvb3Q9Y29tLnhpYW9taS5zaG9wLnBsdWdpbi5zeXNzZXR0aW5nLlJvb3RGcmFnbWVudCZtYXNpZD0yMDAyNy4wMDAwMQ==";
    private static final String MMS_BREATHING_LIGHT_COLOR_RESOURCE = "mms_breathing_light_color";
    private static final String MMS_BREATHING_LIGHT_FREQ_RESOURCE = "mms_breathing_light_freq";
    private static final String NOTCH_FORCE_BLACK_RESOURCE = "notch_force_black_title";
    private static final String POWER_MODE_RESOURCE = "power_mode";
    private static final String PRINT_RESOURCE = "print_settings";
    private static final String SECOND_SPACE_RESOURCE = "second_space";
    private static final String SHOW_NOTIFICATION_ICON_RESOURCE = "status_bar_settings_show_notification_icon";
    private static final String SPELLCHECKERS_RESOURCE = "spellcheckers_settings_title";
    private static final String STATUS_BAR_RESOURCE = "status_bar_settings";
    private static final String TAPLUS_SETTINGS_RESOURCE = "taplus_title";
    private static final String THEME_RESOURCE = "theme_settings_title";
    private static final String USER_DICT_RESOURCE = "user_dict_settings_title";
    private static final String VIBRATE_INPUT_DEVICES_RESOURCE = "vibrate_input_devices";
    private static final String WALLPAPER_RESOURCE = "wallpaper_settings_title";
    private static final String XIAOMI_ACCOUNT = "xiaomi_account";
    private static final String XIAOMI_ACCOUNT_INFO = "unlogin_account_title";
    private static final String XSPACE_RESOURCE = "xspace";

    OtherSettingsUpdateHelper() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void update(Context context, ArrayList<ContentProviderOperation> arrayList) {
        boolean z;
        boolean z2;
        char c;
        Intent buildXiaoAiSettingsIntent;
        PackageManager packageManager = context.getPackageManager();
        if (Build.VERSION.SDK_INT > 19 && (OldmanHelper.isOldmanMode() || !ConfigUtils.isSupportSecuritySpace())) {
            BaseSearchUpdateHelper.hideTreeByRootResource(context, arrayList, "second_space");
        }
        if (OldmanHelper.isOldmanMode()) {
            BaseSearchUpdateHelper.hideTreeByRootResource(context, arrayList, WALLPAPER_RESOURCE);
        }
        if (UserHandle.myUserId() != 0 || OldmanHelper.isOldmanMode() || !ConfigUtils.isSupportSecuritySpace()) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, XSPACE_RESOURCE);
        }
        if (ThemeManagerHelper.needDisableTheme(context) || OldmanHelper.isOldmanMode()) {
            BaseSearchUpdateHelper.hideTreeByRootResource(context, arrayList, THEME_RESOURCE);
        }
        if (!FeatureParser.getBoolean("support_power_mode", false)) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, POWER_MODE_RESOURCE);
        }
        if (OldmanHelper.isStatusBarSettingsHidden(context)) {
            BaseSearchUpdateHelper.hideTreeByRootResource(context, arrayList, STATUS_BAR_RESOURCE);
        }
        if (CustomizeUtil.HAS_NOTCH) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, BATTERY_INDICATOR_STYLE_RESOURCE);
            BaseSearchUpdateHelper.hideByResource(context, arrayList, SHOW_NOTIFICATION_ICON_RESOURCE);
        } else {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, NOTCH_FORCE_BLACK_RESOURCE);
        }
        Sensor defaultSensor = ((SensorManager) context.getSystemService("sensor")).getDefaultSensor(8);
        if ((defaultSensor != null && "Elliptic Proximity".equals(defaultSensor.getName()) && "Elliptic Labs".equals(defaultSensor.getVendor())) | (!packageManager.isPackageAvailable("com.miui.sensor.avoid"))) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, AVOID_UI_RESOURCE);
        }
        if (!HandyModeUtils.isFeatureVisible()) {
            BaseSearchUpdateHelper.hideTreeByRootResource(context, arrayList, HANDY_MODE_RESOURCE);
        }
        if (!Utils.isVoiceCapable(context)) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, CALL_BREATHING_LIGHT_COLOR_RESOURCE);
            BaseSearchUpdateHelper.hideByResource(context, arrayList, CALL_BREATHING_LIGHT_FREQ_RESOURCE);
            BaseSearchUpdateHelper.hideByResource(context, arrayList, MMS_BREATHING_LIGHT_COLOR_RESOURCE);
            BaseSearchUpdateHelper.hideByResource(context, arrayList, MMS_BREATHING_LIGHT_FREQ_RESOURCE);
        }
        if (MiuiUtils.needRemoveSystemAppsUpdater(context)) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, APP_UPDATER_RESOURCE);
        }
        TextServicesManager textServicesManager = (TextServicesManager) context.getSystemService("textservices");
        SpellCheckerInfo[] enabledSpellCheckers = textServicesManager.getEnabledSpellCheckers();
        if (!textServicesManager.isSpellCheckerEnabled() || enabledSpellCheckers == null || enabledSpellCheckers.length == 0) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, SPELLCHECKERS_RESOURCE);
        }
        List<InputMethodInfo> enabledInputMethodList = ((InputMethodManager) context.getSystemService("input_method")).getEnabledInputMethodList();
        int size = enabledInputMethodList == null ? 0 : enabledInputMethodList.size();
        int i = 0;
        while (true) {
            if (i >= size) {
                z = false;
                break;
            }
            CharSequence loadLabel = enabledInputMethodList.get(i).loadLabel(packageManager);
            if (loadLabel != null && loadLabel.toString().contains("AOSP")) {
                z = true;
                break;
            }
            i++;
        }
        if (!z) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, USER_DICT_RESOURCE);
        }
        int[] deviceIds = InputDevice.getDeviceIds();
        int i2 = 0;
        while (true) {
            if (i2 >= deviceIds.length) {
                z2 = false;
                break;
            }
            InputDevice device = InputDevice.getDevice(deviceIds[i2]);
            if (device != null && !device.isVirtual() && device.getVibrator().hasVibrator()) {
                z2 = true;
                break;
            }
            i2++;
        }
        if (!z2) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, VIBRATE_INPUT_DEVICES_RESOURCE);
        }
        if (!FakeCellSettings.supportDetectFakecell()) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, "manage_fakecell_settings");
        }
        List<ResolveInfo> queryIntentServices = packageManager.queryIntentServices(new Intent("android.printservice.PrintService"), 132);
        if (queryIntentServices == null || queryIntentServices.size() == 0) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, PRINT_RESOURCE);
        }
        if (SettingsFeatures.isMiuiLabNeedHide(context)) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, MIUI_LAB_RESOURCE);
        }
        if (SettingsFeatures.IS_NEED_REMOVE_SOS) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, "emergency_sos_title");
        }
        try {
            if (!IWindowManager.Stub.asInterface(ServiceManager.getService("window")).hasNavigationBar(0)) {
                BaseSearchUpdateHelper.hideTreeByRootResource(context, arrayList, "infinity_display_title");
            }
        } catch (RemoteException unused) {
        }
        if (miui.os.Build.IS_TABLET || miui.os.Build.IS_INTERNATIONAL_BUILD) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, MI_SERVICE);
        } else if (MiuiUtils.getInstance().canFindActivity(context, new Intent("android.intent.action.VIEW", Uri.parse(MI_SERVICE_URL)))) {
            Iterator<String> it = BaseSearchUpdateHelper.getIdWithResource(context, MI_SERVICE).iterator();
            while (it.hasNext()) {
                BaseSearchUpdateHelper.updateSearchItem(arrayList, context.getPackageName() + it.next(), 5, new String[]{"intent_action", FunctionColumns.INTENT_DATA, FunctionColumns.DEST_PACKAGE, FunctionColumns.DEST_CLASS, FunctionColumns.FRAGMENT}, new String[]{"android.intent.action.VIEW", MI_SERVICE_URL, "", "", ""});
            }
        }
        char c2 = 2;
        if (MiuiUtils.includeXiaoAi(context) && (buildXiaoAiSettingsIntent = MiuiUtils.buildXiaoAiSettingsIntent()) != null) {
            Iterator<String> it2 = BaseSearchUpdateHelper.getIdWithResource(context, HOME_XIAOAI_RESOURCE).iterator();
            while (it2.hasNext()) {
                BaseSearchUpdateHelper.updateSearchItem(arrayList, context.getPackageName() + it2.next(), 5, new String[]{"intent_action", FunctionColumns.INTENT_DATA, FunctionColumns.DEST_PACKAGE, FunctionColumns.DEST_CLASS, FunctionColumns.FRAGMENT}, new String[]{"", "", buildXiaoAiSettingsIntent.getComponent().getPackageName(), buildXiaoAiSettingsIntent.getComponent().getClassName(), ""});
            }
        }
        for (String str : BaseSearchUpdateHelper.getIdWithResource(context, LOCKSCREEN_MAGAZINE_RESOURCE)) {
            Intent wallpaperIntent = MiuiSecuritySettings.getWallpaperIntent(context);
            if (wallpaperIntent == null) {
                BaseSearchUpdateHelper.hideByResource(context, arrayList, LOCKSCREEN_MAGAZINE_RESOURCE);
                c = c2;
            } else {
                ComponentName resolveActivity = wallpaperIntent.resolveActivity(context.getPackageManager());
                c = 2;
                BaseSearchUpdateHelper.updateSearchItem(arrayList, context.getPackageName() + str, 5, new String[]{"intent_action", FunctionColumns.INTENT_DATA, FunctionColumns.DEST_PACKAGE, FunctionColumns.DEST_CLASS, FunctionColumns.FRAGMENT}, new String[]{"", "", resolveActivity.getPackageName(), resolveActivity.getClassName(), ""});
            }
            c2 = c;
        }
        if (!MiuiUtils.isAiKeyExist(context) || !FeatureParser.getBoolean("support_ai_task", false)) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, AI_TOUCH_RESOURCE);
        }
        if (MiuiAiPreloadController.isNotSupported()) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, MIUI_LAB_AI_PRELOAD);
        }
        boolean z3 = miui.os.Build.IS_INTERNATIONAL_BUILD;
        if (z3 || miui.os.Build.IS_TABLET) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, TAPLUS_SETTINGS_RESOURCE);
        }
        if (z3) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, XIAOMI_ACCOUNT_INFO);
        } else {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, XIAOMI_ACCOUNT);
        }
    }
}
