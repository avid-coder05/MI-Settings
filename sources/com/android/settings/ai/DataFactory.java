package com.android.settings.ai;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import com.android.settings.ai.PreferenceHelper;
import com.android.settings.search.provider.SettingsProvider;
import com.android.settingslib.util.MiStatInterfaceUtils;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class DataFactory {
    public static AiSettingsItem generateDefaultActionItem(Context context, String str) {
        AiSettingsItem aiSettingsItem = new AiSettingsItem(true, 1, 0);
        aiSettingsItem.voiceAssistantMode = getMode(str);
        return aiSettingsItem;
    }

    public static List<AiSettingsItem> generateItems(Context context, String str) {
        AiSettingsItem pressAiButtonSettings = PreferenceHelper.AiSettingsPreferenceHelper.getPressAiButtonSettings(context, str);
        ArrayList arrayList = new ArrayList();
        int i = 0;
        if ("key_single_click_ai_button_settings".equals(str)) {
            AiSettingsItem aiSettingsItem = new AiSettingsItem(1, 0);
            if (aiSettingsItem.equals(pressAiButtonSettings)) {
                aiSettingsItem.selected = true;
            }
            arrayList.add(aiSettingsItem);
            AiSettingsItem aiSettingsItem2 = new AiSettingsItem(2, 1);
            if (aiSettingsItem2.equals(pressAiButtonSettings)) {
                aiSettingsItem2.selected = true;
            }
            arrayList.add(aiSettingsItem2);
            AiSettingsItem aiSettingsItem3 = new AiSettingsItem(8, 7);
            if (aiSettingsItem3.equals(pressAiButtonSettings)) {
                aiSettingsItem3.selected = true;
            }
            arrayList.add(aiSettingsItem3);
        } else {
            while (i < 8) {
                int i2 = i + 1;
                AiSettingsItem aiSettingsItem4 = new AiSettingsItem(i2, i);
                if (aiSettingsItem4.equals(pressAiButtonSettings)) {
                    aiSettingsItem4.selected = true;
                }
                arrayList.add(aiSettingsItem4);
                i = i2;
            }
        }
        return arrayList;
    }

    public static int getMode(String str) {
        if ("key_long_press_down_ai_button_settings".equals(str)) {
            return 3;
        }
        if ("key_long_press_up_ai_button_settings".equals(str)) {
            return 4;
        }
        return ("key_double_click_ai_button_settings".equals(str) || "key_single_click_ai_button_settings".equals(str)) ? 5 : 1;
    }

    public static boolean isDeviceProvisioned(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "device_provisioned", 0) != 0;
    }

    public static void record(Context context, String str, AiSettingsItem aiSettingsItem) {
        StringBuilder sb = new StringBuilder();
        if ("key_single_click_ai_button_settings".equals(str)) {
            sb.append(1);
        } else if ("key_long_press_up_ai_button_settings".equals(str) || "key_long_press_down_ai_button_settings".equals(str)) {
            sb.append(2);
        } else if ("key_double_click_ai_button_settings".equals(str)) {
            sb.append(3);
        }
        sb.append("_");
        sb.append(aiSettingsItem.type);
        MiStatInterfaceUtils.recordCountEventAnonymous("AIkey_active_act0", sb.toString());
        Log.d("firebase_report", SettingsProvider.ARGS_KEY + "AIkey_active_act0 v1: " + sb.toString());
        String str2 = PreferenceHelper.isNewUser(context, System.currentTimeMillis()) ? "1" : "0";
        MiStatInterfaceUtils.recordCountEventAnonymous("AIkey_active_isFirst", str2);
        Log.d("firebase_report", SettingsProvider.ARGS_KEY + "AIkey_active_isFirst v2: " + str2);
        if ("key_single_click_ai_button_settings".equals(str) || !PreferenceHelper.shouldRecordItemType(context, System.currentTimeMillis())) {
            return;
        }
        if (!"key_long_press_up_ai_button_settings".equals(str)) {
            "key_long_press_down_ai_button_settings".equals(str);
        }
        int i = aiSettingsItem.type;
        MiStatInterfaceUtils.recordCountEventAnonymous("AIkey_status_Status", str2);
        Log.d("firebase_report", SettingsProvider.ARGS_KEY + "AIkey_status_Status  v3: " + str2);
    }
}
