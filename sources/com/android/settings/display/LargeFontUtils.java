package com.android.settings.display;

import android.app.ActivityManagerNative;
import android.app.ActivityThread;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.MiuiConfiguration;
import android.content.res.Resources;
import android.miui.AppOpsUtils;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.utils.MiuiFileUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import java.util.HashMap;
import miui.util.ExquisiteModeUtils;

/* loaded from: classes.dex */
public class LargeFontUtils {
    public static int getCurrentUIModeType() {
        return Resources.getSystem().getConfiguration().uiMode & 15;
    }

    static float getFontScale(int i) {
        return MiuiConfiguration.getFontScale(i);
    }

    public static int getFontWeight(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "key_miui_font_weight_scale", 50);
    }

    public static boolean getVariableFontChange(Context context) {
        if (Settings.Global.getInt(context.getContentResolver(), "key_miui_font_weight_scale", -1) == 50) {
            Settings.Global.putInt(context.getContentResolver(), "key_miui_font_weight_scale", -1);
            Settings.System.putInt(context.getContentResolver(), "key_miui_font_weight_scale", 50);
            return true;
        }
        return false;
    }

    public static boolean isSupportVarintFont() {
        String str = SystemProperties.get("ro.miui.ui.fonttype", "");
        boolean isDirEmpty = MiuiFileUtils.isDirEmpty("data/system/theme/fonts");
        Log.d("LargeFontUtils", "isSupportVarintFont()....fontType=" + str + ", isEmptyThemeFont=" + isDirEmpty);
        return TextUtils.equals(str, "mipro") && !AppOpsUtils.isXOptMode() && SystemProperties.getBoolean("persist.sys.miui_var_font", true) && isDirEmpty;
    }

    private static void recordCountEvent(int i) {
        HashMap hashMap = new HashMap();
        switch (i) {
            case 10:
                hashMap.put("font_size", "extral_small_font_size");
                break;
            case 11:
                hashMap.put("font_size", "godzilla_font_size");
                break;
            case 12:
                hashMap.put("font_size", "small_font_size");
                break;
            case 13:
                hashMap.put("font_size", "medium_font_size");
                break;
            case 14:
                hashMap.put("font_size", "large_font_size");
                break;
            case 15:
                hashMap.put("font_size", "huge_font_size");
                break;
            default:
                hashMap.put("font_size", "standard_font_size");
                break;
        }
        OneTrackInterfaceUtils.track("fontsize", hashMap);
    }

    public static boolean sendUiModeChangeMessage(Context context, int i) {
        try {
            Configuration configuration = ActivityManagerNative.getDefault().getConfiguration();
            if ((configuration.uiMode & 15) != i) {
                Settings.System.putInt(context.getContentResolver(), "ui_mode_scale", i);
                configuration.fontScale = getFontScale(i);
                configuration.uiMode = (configuration.uiMode & (-16)) | i;
                if (ExquisiteModeUtils.SUPPORT_EXQUISITE_MODE) {
                    MiuiConfiguration miuiConfiguration = configuration.extraConfig;
                    if (miuiConfiguration instanceof MiuiConfiguration) {
                        miuiConfiguration.updateTheme(268435456L);
                    }
                }
                ActivityManagerNative.getDefault().updatePersistentConfigurationWithAttribution(configuration, ActivityThread.currentOpPackageName(), (String) null);
                recordCountEvent(i);
                return true;
            }
            return false;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void setFontWeight(Context context, int i) {
        if (i >= 0 && i <= 100) {
            Settings.System.putInt(context.getContentResolver(), "key_miui_font_weight_scale", i);
            Settings.Global.putInt(context.getContentResolver(), "key_miui_font_weight_scale", i);
            return;
        }
        Log.d("LargeFontUtils", "setFontWeight()....invalid value = " + i);
    }
}
