package com.android.settings;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.widget.Toast;
import com.android.settings.search.tree.GestureSettingsTree;
import com.google.android.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import miui.cloud.sync.MiCloudStatusInfo;
import miui.os.Build;
import miuix.util.Log;

/* loaded from: classes.dex */
public final class MiuiShortcut$Key {
    private static boolean isInitMap;
    public static final String[] GESTURE_SHORTCUT_ACTION = {"long_press_power_key", "double_click_power_key", "three_gesture_down", "three_gesture_long_press", "long_press_menu_key_when_lock", "double_knock", "key_combination_power_volume_down", "knock_long_press_horizontal_slid", "knock_slide_shape"};
    public static final String[] KEY_SHORTCUT_ACTION = {"long_press_home_key", "long_press_menu_key", "long_press_back_key", "key_combination_power_home", "key_combination_power_menu", "key_combination_power_back", "press_menu"};
    public static final List<String> FEATURE_KNOCK = Arrays.asList("knock_long_press_horizontal_slid", "knock_slide_shape", "double_knock");
    public static Map<String, List<String>> sGestureMap = new LinkedHashMap<String, List<String>>() { // from class: com.android.settings.MiuiShortcut$Key.1
        {
            put("launch_voice_assistant", Lists.newArrayList(new String[]{"long_press_power_key", "double_knock"}));
            put("launch_smarthome", Lists.newArrayList(new String[]{"long_press_power_key", "double_click_power_key"}));
            put("screen_shot", Lists.newArrayList(new String[]{"three_gesture_down", "key_combination_power_volume_down", "double_knock"}));
            put("partial_screen_shot", Lists.newArrayList(new String[]{"three_gesture_long_press", "knock_slide_shape"}));
            put("mi_pay", Lists.newArrayList(new String[]{"double_click_power_key"}));
            put("au_pay", Lists.newArrayList(new String[]{"double_click_power_key"}));
            put("google_pay", Lists.newArrayList(new String[]{"double_click_power_key"}));
            put("launch_camera", Lists.newArrayList(new String[]{"double_click_power_key", "double_click_volume_down_when_lock"}));
            put("turn_on_torch", Lists.newArrayList(new String[]{"double_click_power_key"}));
            put("change_brightness", Lists.newArrayList(new String[]{"knock_long_press_horizontal_slid"}));
            put("launch_google_search", Lists.newArrayList(new String[]{"long_press_power_key", "double_knock"}));
        }
    };
    public static List<String> mHidenPreferenceList = new ArrayList();

    private static void checkCloudDataStatus(Context context, String... strArr) {
        MiuiShortcut$Cloud.updateCacheValue(context);
        checkHealthCodeStatus(context, strArr);
    }

    private static void checkHealthCodeStatus(Context context, String... strArr) {
        boolean z;
        boolean z2;
        boolean z3 = false;
        boolean z4 = false;
        for (String str : strArr) {
            String keyAndGestureShortcutSetFunction = getKeyAndGestureShortcutSetFunction(context, str);
            if ("launch_alipay_health_code".equals(keyAndGestureShortcutSetFunction)) {
                z2 = MiuiShortcut$Cloud.showAlipayHealthCode;
                if (!z2) {
                    if (!z3) {
                        Toast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.launch_alipay_health_code_auto_close), 1).show();
                        z3 = true;
                    }
                    Settings.System.putStringForUser(context.getContentResolver(), str, MiCloudStatusInfo.QuotaInfo.WARN_NONE, -2);
                }
            }
            if ("launch_wechat_health_code".equals(keyAndGestureShortcutSetFunction)) {
                z = MiuiShortcut$Cloud.showWeChatHealthCode;
                if (!z) {
                    if (!z4) {
                        Toast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.launch_wechat_health_code_auto_close), 1).show();
                        z4 = true;
                    }
                    Settings.System.putStringForUser(context.getContentResolver(), str, MiCloudStatusInfo.QuotaInfo.WARN_NONE, -2);
                }
            }
        }
    }

    private static List<String> getActions(Context context, String[] strArr, String str) {
        ArrayList arrayList = new ArrayList();
        for (String str2 : strArr) {
            if (str.equals(getKeyAndGestureShortcutSetFunction(context, str2))) {
                arrayList.add(str2);
            }
        }
        return arrayList;
    }

    public static List<String> getGestureShortcutAction(Context context, String str) {
        if (str != null) {
            return getActions(context, GESTURE_SHORTCUT_ACTION, str);
        }
        return null;
    }

    public static String getKeyAndGestureShortcutSetFunction(Context context, String str) {
        return MiuiSettings.Key.getKeyAndGestureShortcutFunction(context, str);
    }

    public static String getKeyForResourceName(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1071007508:
                if (str.equals("mi_pay_summary")) {
                    c = 0;
                    break;
                }
                break;
            case -319208938:
                if (str.equals("voice_assist")) {
                    c = 1;
                    break;
                }
                break;
            case -304503603:
                if (str.equals("regional_screen_shot")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return "mi_pay";
            case 1:
                return "launch_voice_assistant";
            case 2:
                return "partial_screen_shot";
            default:
                return str;
        }
    }

    public static List<String> getKeyShortcutAction(Context context, String str) {
        if (str != null) {
            return getActions(context, KEY_SHORTCUT_ACTION, str);
        }
        return null;
    }

    public static String getResourceForKey(String str, Context context) {
        int identifier = context.getResources().getIdentifier(str, "string", context.getPackageName());
        if ("three_gesture_long_press".equals(str)) {
            return String.format(context.getResources().getString(R.string.three_gesture_long_press), 3);
        }
        if ("long_press_power_key".equals(str)) {
            return String.format(context.getResources().getString(R.string.long_press_power_key_half_second), Double.valueOf(0.5d));
        }
        if ("partial_screen_shot".equals(str)) {
            return context.getResources().getString(R.string.regional_screen_shot);
        }
        if ("knock_gesture_v".equals(str)) {
            identifier = context.getResources().getIdentifier(GestureSettingsTree.KNOCK_GESTURE_V_TITLE, "string", context.getPackageName());
        }
        if (identifier != 0) {
            return context.getResources().getString(identifier);
        }
        Log.e("MiuiShortcut", "find key=" + str + "'s value is null!");
        return "";
    }

    public static String getResoureceNameForKey(String str, Context context) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1074479227:
                if (str.equals("mi_pay")) {
                    c = 0;
                    break;
                }
                break;
            case 596487045:
                if (str.equals("launch_voice_assistant")) {
                    c = 1;
                    break;
                }
                break;
            case 2134385967:
                if (str.equals("partial_screen_shot")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return "mi_pay_summary";
            case 1:
                return "voice_assist";
            case 2:
                return "regional_screen_shot";
            default:
                return str;
        }
    }

    private static boolean hasArrayIncludeItem(String str, String... strArr) {
        if (str == null) {
            return false;
        }
        for (String str2 : strArr) {
            if (str.equals(str2)) {
                return true;
            }
        }
        return false;
    }

    private static void initFunctionValues(ArrayList<String> arrayList, String... strArr) {
        boolean z;
        boolean z2;
        arrayList.add("screen_shot");
        if (!hasArrayIncludeItem("back_double_tap", strArr)) {
            arrayList.add("turn_on_torch");
        }
        arrayList.add("launch_control_center");
        arrayList.add("launch_notification_center");
        boolean z3 = Build.IS_GLOBAL_BUILD;
        if (z3) {
            arrayList.add("launch_google_search");
        } else {
            arrayList.add("launch_voice_assistant");
            arrayList.add("launch_ai_shortcut");
        }
        arrayList.add("launch_camera");
        arrayList.add("launch_calculator");
        arrayList.add("mute");
        if (!z3) {
            arrayList.add("launch_alipay_payment_code");
            arrayList.add("launch_wechat_payment_code");
            z = MiuiShortcut$Cloud.showAlipayHealthCode;
            if (z) {
                arrayList.add("launch_alipay_health_code");
            }
            z2 = MiuiShortcut$Cloud.showWeChatHealthCode;
            if (z2) {
                arrayList.add("launch_wechat_health_code");
            }
            arrayList.add("launch_alipay_scanner");
            arrayList.add("launch_wechat_scanner");
        }
        arrayList.add("go_to_sleep");
        arrayList.add("dump_log");
        arrayList.add(MiCloudStatusInfo.QuotaInfo.WARN_NONE);
    }

    private static void initFunctions(Context context, ArrayList<String> arrayList, String... strArr) {
        boolean z;
        boolean z2;
        Resources resources = context.getResources();
        arrayList.add(resources.getString(R.string.screen_shot));
        if (!hasArrayIncludeItem("back_double_tap", strArr)) {
            arrayList.add(resources.getString(R.string.turn_on_torch));
        }
        arrayList.add(resources.getString(R.string.launch_control_center));
        arrayList.add(resources.getString(R.string.launch_notification_center));
        boolean z3 = Build.IS_GLOBAL_BUILD;
        if (z3) {
            arrayList.add(resources.getString(R.string.launch_google_search));
        } else {
            arrayList.add(resources.getString(R.string.launch_voice_assistant));
            arrayList.add(resources.getString(R.string.launch_ai_shortcut));
        }
        arrayList.add(resources.getString(R.string.launch_camera));
        arrayList.add(resources.getString(R.string.launch_calculator));
        arrayList.add(resources.getString(R.string.mute));
        if (!z3) {
            arrayList.add(resources.getString(R.string.launch_alipay_payment_code));
            arrayList.add(resources.getString(R.string.launch_wechat_payment_code));
            z = MiuiShortcut$Cloud.showAlipayHealthCode;
            if (z) {
                arrayList.add(resources.getString(R.string.launch_alipay_health_code));
            }
            z2 = MiuiShortcut$Cloud.showWeChatHealthCode;
            if (z2) {
                arrayList.add(resources.getString(R.string.launch_wechat_health_code));
            }
            arrayList.add(resources.getString(R.string.launch_alipay_scanner));
            arrayList.add(resources.getString(R.string.launch_wechat_scanner));
        }
        arrayList.add(resources.getString(R.string.go_to_sleep));
        arrayList.add(resources.getString(R.string.dump_log));
        arrayList.add(resources.getString(R.string.key_none));
    }

    public static void initFunctionsAndValues(Context context, ArrayList<String> arrayList, ArrayList<String> arrayList2, String... strArr) {
        if (arrayList == null || arrayList2 == null || strArr == null) {
            return;
        }
        checkCloudDataStatus(context, strArr);
        initFunctions(context, arrayList, strArr);
        initFunctionValues(arrayList2, strArr);
    }

    private static void initGestureMap(Context context) {
        for (Map.Entry<String, List<String>> entry : sGestureMap.entrySet()) {
            ArrayList arrayList = new ArrayList();
            for (String str : entry.getValue()) {
                if (!MiuiShortcut$System.hasKnockFeature(context) && FEATURE_KNOCK.contains(str)) {
                    arrayList.add(str);
                }
            }
            entry.getValue().removeAll(arrayList);
            if (entry.getValue().size() == 0) {
                mHidenPreferenceList.add(entry.getKey());
            }
        }
        if (Build.IS_GLOBAL_BUILD) {
            mHidenPreferenceList.add("launch_voice_assistant");
        } else {
            mHidenPreferenceList.add("launch_google_search");
            if (!MiuiShortcut$System.hasVoiceAssist(context)) {
                mHidenPreferenceList.add("launch_voice_assistant");
            }
        }
        if (!isTSMClientInstalled(context)) {
            mHidenPreferenceList.add("mi_pay");
        }
        if (!MiuiShortcut$System.supportPartialScreenShot()) {
            mHidenPreferenceList.add("partial_screen_shot");
        }
        if (!"XIG02".equals(android.os.Build.DEVICE)) {
            mHidenPreferenceList.add("au_pay");
        }
        if (!Build.hasCameraFlash(context)) {
            mHidenPreferenceList.add("turn_on_torch");
        }
        mHidenPreferenceList.add("google_pay");
        if (!MiuiShortcut$System.hasSmartHome(context)) {
            mHidenPreferenceList.add("launch_smarthome");
        }
        Iterator<String> it = mHidenPreferenceList.iterator();
        while (it.hasNext()) {
            sGestureMap.remove(it.next());
        }
        isInitMap = true;
    }

    public static boolean isTSMClientInstalled(Context context) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager != null) {
            try {
                if (packageManager.getPackageInfo("com.miui.tsmclient", 0) != null) {
                    return true;
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        return false;
    }

    public static void setGestureMap(Context context) {
        if (isInitMap) {
            return;
        }
        initGestureMap(context);
    }
}
