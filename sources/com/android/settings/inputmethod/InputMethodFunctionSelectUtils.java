package com.android.settings.inputmethod;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.os.BackgroundThread;
import com.android.settings.R;
import java.util.ArrayList;
import java.util.List;
import miui.cloud.sync.MiCloudStatusInfo;
import miui.os.Build;
import miui.util.CompatibilityHelper;

/* loaded from: classes.dex */
public class InputMethodFunctionSelectUtils {
    private static ArrayList<String> sFunctionKeyList = new ArrayList<>();
    private static ArrayList<Integer> sFunctionTitleList = new ArrayList<>();
    private static ArrayList<Integer> sFunctionDesList = new ArrayList<>();
    private static ArrayList<Integer> sFunctionImageResourceList = new ArrayList<>();
    private static ArrayList<Integer> sCloudQuickPasteSelectTitleList = new ArrayList<>();
    private static ArrayList<Integer> sCloudQuickPasteSelectDesList = new ArrayList<>();
    private static ArrayList<String> sCloudQuickPasteKeyList = new ArrayList<>();
    private static ArrayList<String> sMiddleFunctionKeyList = new ArrayList<>();
    public static List<String> sCustomIme = new ArrayList();

    static {
        sFunctionKeyList.add("voice_input");
        sFunctionKeyList.add("xiaoai_input");
        sFunctionKeyList.add("switch_input_method");
        sFunctionKeyList.add("clipboard_phrase");
        sFunctionKeyList.add("switch_keyboard_language");
        sFunctionKeyList.add("switch_keyboard_type");
        sFunctionKeyList.add("no_function");
        sFunctionTitleList.add(Integer.valueOf(R.string.input_method_function_voice));
        sFunctionTitleList.add(Integer.valueOf(R.string.input_method_function_xiaoai));
        sFunctionTitleList.add(Integer.valueOf(R.string.input_method_function_switch));
        sFunctionTitleList.add(Integer.valueOf(R.string.input_method_function_clipboard));
        sFunctionTitleList.add(Integer.valueOf(R.string.input_method_function_switch_keyboard_language));
        sFunctionTitleList.add(Integer.valueOf(R.string.input_method_function_switch_keyboard_type));
        sFunctionTitleList.add(Integer.valueOf(R.string.input_method_function_no_function));
        sFunctionDesList.add(Integer.valueOf(R.string.input_method_function_voice_des));
        sFunctionDesList.add(Integer.valueOf(R.string.input_method_function_xiaoai_des));
        sFunctionDesList.add(Integer.valueOf(R.string.input_method_function_switch_des));
        sFunctionDesList.add(Integer.valueOf(R.string.input_method_function_clipboard_des));
        sFunctionDesList.add(Integer.valueOf(R.string.input_method_function_switch_keyboard_language_des));
        sFunctionDesList.add(Integer.valueOf(R.string.input_method_function_switch_keyboard_type_des));
        sFunctionDesList.add(Integer.valueOf(R.string.input_method_function_no_function_des));
        sFunctionImageResourceList.add(Integer.valueOf(R.drawable.input_method_voice_function));
        sFunctionImageResourceList.add(Integer.valueOf(R.drawable.input_method_xiaoai_function));
        sFunctionImageResourceList.add(Integer.valueOf(R.drawable.input_method_switch_function));
        sFunctionImageResourceList.add(Integer.valueOf(R.drawable.input_method_clipboard_function));
        sFunctionImageResourceList.add(Integer.valueOf(R.drawable.input_method_no_function));
        sCloudQuickPasteSelectTitleList.add(Integer.valueOf(R.string.input_method_cloud_paste_mode_red_point_title));
        sCloudQuickPasteSelectTitleList.add(Integer.valueOf(R.string.input_method_cloud_paste_mode_bubble_title));
        sCloudQuickPasteSelectTitleList.add(Integer.valueOf(R.string.input_method_cloud_paste_mode_no_title));
        sCloudQuickPasteSelectDesList.add(Integer.valueOf(R.string.input_method_cloud_paste_mode_red_point_des));
        sCloudQuickPasteSelectDesList.add(Integer.valueOf(R.string.input_method_cloud_paste_mode_bubble_des));
        sCloudQuickPasteSelectDesList.add(Integer.valueOf(R.string.input_method_cloud_paste_mode_no_des));
        sCloudQuickPasteKeyList.add("red_point");
        sCloudQuickPasteKeyList.add("pop_vew");
        sCloudQuickPasteKeyList.add(MiCloudStatusInfo.QuotaInfo.WARN_NONE);
        sMiddleFunctionKeyList.add("quick_move_cursor");
        sMiddleFunctionKeyList.add("no_function");
        sCustomIme.add("com.sohu.inputmethod.sogou.xiaomi");
        sCustomIme.add("com.iflytek.inputmethod.miui");
        sCustomIme.add("com.baidu.input_mi");
    }

    private static void addInputMethodAnalytics(final Context context, String str, String str2) {
        final Bundle bundle = new Bundle();
        bundle.putString("clickKey", "click");
        bundle.putString("clickValue", str);
        bundle.putString("recordKey", str2);
        BackgroundThread.getHandler().post(new Runnable() { // from class: com.android.settings.inputmethod.InputMethodFunctionSelectUtils.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    context.getContentResolver().call(Uri.parse("content://com.miui.input.provider"), "input_method_analytics", (String) null, bundle);
                } catch (Exception e) {
                    Log.e("InputMethodSettings", "call input method provider error", e);
                }
            }
        });
    }

    public static void addMiuiBottomEnableRecord(Context context, String str) {
        addInputMethodAnalytics(context, str, "fullscreen_keyboard_enable");
    }

    public static void addSettingsRecord(Context context, String str) {
        addInputMethodAnalytics(context, str, "settings_keyboard_switch");
    }

    public static boolean getClipboardQuickPasteEnable(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "enable_miui_quick_paste", 0) == 1;
    }

    public static boolean getClipboardQuickPasteTaobaoEnable(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "enable_quick_paste_taobao", 1) == 1;
    }

    public static boolean getClipboardQuickPasteUrlEnable(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "enable_quick_paste_url", 1) == 1;
    }

    public static int getCloudClipboardQuickPasteMode(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "input_method_cloud_clipboard_quick_paste_mode", 0);
    }

    public static ArrayList<Integer> getCloudQuickPasteDesList() {
        return sCloudQuickPasteSelectDesList;
    }

    public static boolean getCloudQuickPasteEnable(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "enable_quick_paste_cloud", 1) == 1;
    }

    public static ArrayList<String> getCloudQuickPasteKeyList() {
        return sCloudQuickPasteKeyList;
    }

    public static ArrayList<Integer> getCloudQuickPasteTitleList() {
        return sCloudQuickPasteSelectTitleList;
    }

    public static String getCurrentInputMethod(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "default_input_method");
        return !TextUtils.isEmpty(string) ? string.substring(0, string.indexOf(47)) : "";
    }

    public static int getDefaultSelectedIndex(boolean z) {
        return sFunctionKeyList.indexOf(z ? "switch_input_method" : "clipboard_phrase");
    }

    public static int getMiddleFunctionSelectedIndex(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "full_screen_keyboard_middle_function");
        return !TextUtils.isEmpty(string) ? sMiddleFunctionKeyList.indexOf(string) : sMiddleFunctionKeyList.indexOf("quick_move_cursor");
    }

    public static String getMiddleFunctionSelectedKeyByIndex(int i) {
        return sMiddleFunctionKeyList.get(i);
    }

    public static int getSelectedFunctionIndex(Context context, boolean z) {
        String selectedKey = getSelectedKey(context, z);
        return !TextUtils.isEmpty(selectedKey) ? sFunctionKeyList.indexOf(selectedKey) : getDefaultSelectedIndex(z);
    }

    public static String getSelectedFunctionKeyByIndex(int i) {
        return sFunctionKeyList.get(i);
    }

    public static String getSelectedKey(Context context, boolean z) {
        return Settings.Secure.getString(context.getContentResolver(), z ? "full_screen_keyboard_left_function" : "full_screen_keyboard_right_function");
    }

    private static boolean isFullScreenDevice() {
        try {
            return CompatibilityHelper.hasNavigationBar(0);
        } catch (RemoteException e) {
            Log.e("InputMethodSettings", "get hasNavigationBar error", e);
            return false;
        }
    }

    public static boolean isKeyBoardSkinFollowSystemDefault(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "keyboard_skin_follow_system_enable", -1) == -1;
    }

    public static boolean isKeyBoardSkinFollowSystemEnable(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "keyboard_skin_follow_system_enable", 1) == 1;
    }

    public static boolean isMechKeyboardUsable(Context context) {
        String currentInputMethod = getCurrentInputMethod(context);
        if (sCustomIme.contains(currentInputMethod)) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(currentInputMethod, 0);
                if (!"com.baidu.input_mi".equals(currentInputMethod) || packageInfo.versionCode < 658) {
                    if (!"com.sohu.inputmethod.sogou.xiaomi".equals(currentInputMethod) || packageInfo.versionCode < 1151) {
                        return "com.iflytek.inputmethod.miui".equals(currentInputMethod) && packageInfo.versionCode >= 7945;
                    }
                    return true;
                }
                return true;
            } catch (PackageManager.NameNotFoundException e) {
                Log.i("InputMethodSettings", "getPackageVersionName", e);
                return false;
            }
        }
        return false;
    }

    public static boolean isMiuiImeBottomSupport() {
        return (SystemProperties.getInt("ro.miui.support_miui_ime_bottom", 0) == 1) && !Build.IS_INTERNATIONAL_BUILD && !Build.IS_TABLET && isFullScreenDevice();
    }

    public static boolean isSupportMechKeyboard(Context context) {
        return (Build.IS_INTERNATIONAL_BUILD || Settings.Global.getInt(context.getContentResolver(), "miui_mechanical_keyboard_support", 0) == 0) ? false : true;
    }

    public static void setPreferenceCheckedValue(Context context, String str, int i) {
        Settings.Secure.putInt(context.getContentResolver(), str, i);
    }
}
