package com.android.settings.aikey;

import android.content.Context;
import android.util.Log;

/* loaded from: classes.dex */
public class PreferenceHelper {

    /* loaded from: classes.dex */
    public static class AiSettingsPreferenceHelper extends PreferenceHelper {
        public static int getPressAiButtonSettings(Context context, String str) {
            int i = PreferenceHelper.getInt(context, ("key_long_press_down_ai_button_settings".equals(str) || "key_long_press_up_ai_button_settings".equals(str)) ? "key_long_press_ai_button_settings" : str, 1);
            Log.i("AiSettingsPreferenceHelper", "key:" + str + "settings:" + i);
            return i;
        }

        public static void setPressAiButtonSettings(Context context, String str, int i) {
            if ("key_long_press_down_ai_button_settings".equals(str) || "key_long_press_up_ai_button_settings".equals(str)) {
                str = "key_long_press_ai_button_settings";
            }
            Log.i("AiSettingsPreferenceHelper", "key:" + str + "value:" + i + "   b:" + PreferenceHelper.putInt(context, str, i));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int getInt(Context context, String str, int i) {
        return context.getSharedPreferences("ai_settings", 0).getInt(str, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean putInt(Context context, String str, int i) {
        return context.getSharedPreferences("ai_settings", 0).edit().putInt(str, i).commit();
    }
}
