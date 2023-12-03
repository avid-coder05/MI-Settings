package com.android.settings.ai;

import android.content.Context;
import android.text.TextUtils;
import androidx.preference.PreferenceManager;
import miui.mipub.MipubStat;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class PreferenceHelper {

    /* loaded from: classes.dex */
    public static class AiSettingsPreferenceHelper extends PreferenceHelper {
        private static AiSettingsItem deSerialize(String str) {
            AiSettingsItem aiSettingsItem = new AiSettingsItem();
            try {
                JSONObject jSONObject = new JSONObject(str);
                aiSettingsItem.selected = jSONObject.getBoolean("selected");
                aiSettingsItem.name = jSONObject.optString("name");
                aiSettingsItem.type = jSONObject.getInt("type");
                aiSettingsItem.mIndex = jSONObject.optInt("index");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return aiSettingsItem;
        }

        public static AiSettingsItem getPressAiButtonSettings(Context context, String str) {
            String string = PreferenceHelper.getString(context, ("key_long_press_down_ai_button_settings".equals(str) || "key_long_press_up_ai_button_settings".equals(str)) ? "key_long_press_ai_button_settings" : str, "");
            AiSettingsItem deSerialize = !TextUtils.isEmpty(string) ? deSerialize(string) : DataFactory.generateDefaultActionItem(context, str);
            if (deSerialize.type == 1) {
                deSerialize.voiceAssistantMode = DataFactory.getMode(str);
            }
            return deSerialize;
        }

        public static void setPressAiButtonSettings(Context context, String str, AiSettingsItem aiSettingsItem) {
            if ("key_long_press_down_ai_button_settings".equals(str) || "key_long_press_up_ai_button_settings".equals(str)) {
                str = "key_long_press_ai_button_settings";
            }
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("selected", aiSettingsItem.selected);
                jSONObject.put("type", aiSettingsItem.type);
                jSONObject.put("index", aiSettingsItem.mIndex);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            PreferenceHelper.putString(context, str, jSONObject.toString());
        }
    }

    private static long getLong(Context context, String str, long j) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(str, j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getString(Context context, String str, String str2) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(str, str2);
    }

    public static boolean isNewUser(Context context, long j) {
        long j2 = getLong(context, "key_first_time", -1L);
        if (j2 != -1) {
            return j - j2 < MipubStat.STAT_EXPIRY_DATA;
        }
        putLong(context, "key_first_time", j);
        return true;
    }

    private static boolean putLong(Context context, String str, long j) {
        return PreferenceManager.getDefaultSharedPreferences(context).edit().putLong(str, j).commit();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean putString(Context context, String str, String str2) {
        return PreferenceManager.getDefaultSharedPreferences(context).edit().putString(str, str2).commit();
    }

    public static boolean shouldRecordItemType(Context context, long j) {
        long j2 = getLong(context, "key_7_days", -1L);
        if (j2 == -1) {
            putLong(context, "key_7_days", j);
            return true;
        } else if (j - j2 > MipubStat.STAT_EXPIRY_DATA) {
            putLong(context, "key_7_days", j);
            return true;
        } else {
            return false;
        }
    }
}
