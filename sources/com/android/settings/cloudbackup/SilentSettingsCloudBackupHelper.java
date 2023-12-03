package com.android.settings.cloudbackup;

import android.app.AutomaticZenRule;
import android.app.ExtraNotificationManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.SystemProperties;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.service.notification.ZenModeConfig;
import android.text.TextUtils;
import android.util.Log;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
class SilentSettingsCloudBackupHelper {
    private static int ANDROID_VERSION_O = 26;
    private static int mBackupAndroidVerion;
    private static int mCurrentAndroidVerion = Build.VERSION.SDK_INT;
    private static boolean mDebug = false;

    protected static AutomaticZenRule createAutomaticZenRule(ZenModeConfig.ZenRule zenRule) {
        return new AutomaticZenRule(zenRule.name, zenRule.component, zenRule.configurationActivity, zenRule.conditionId, zenRule.zenPolicy, NotificationManager.zenModeToInterruptionFilter(zenRule.zenMode), zenRule.enabled, zenRule.creationTime);
    }

    private static JSONArray getRules(ZenModeConfig.ZenRule zenRule) {
        JSONArray jSONArray = new JSONArray();
        jSONArray.put(zenRule.enabled ? 1 : 0);
        jSONArray.put(zenRule.snoozing ? 1 : 0);
        if (zenRule.name != null) {
            jSONArray.put(1);
            jSONArray.put(zenRule.name);
        } else {
            jSONArray.put(0);
        }
        jSONArray.put(zenRule.zenMode);
        Uri uri = zenRule.conditionId;
        ZenModeConfig.ScheduleInfo tryParseScheduleConditionId = uri != null ? ZenModeConfig.tryParseScheduleConditionId(uri) : null;
        if (tryParseScheduleConditionId != null) {
            jSONArray.put(1);
            jSONArray.put(toDayList(tryParseScheduleConditionId.days));
            jSONArray.put(tryParseScheduleConditionId.startHour);
            jSONArray.put(tryParseScheduleConditionId.startMinute);
            jSONArray.put(tryParseScheduleConditionId.endHour);
            jSONArray.put(tryParseScheduleConditionId.endMinute);
            jSONArray.put(tryParseScheduleConditionId.exitAtAlarm);
        } else {
            jSONArray.put(0);
        }
        if (zenRule.condition != null) {
            jSONArray.put(1);
        } else {
            jSONArray.put(0);
        }
        if (zenRule.component != null) {
            jSONArray.put(1);
            jSONArray.put(zenRule.component.getPackageName());
            jSONArray.put(zenRule.component.getClassName());
        } else {
            jSONArray.put(0);
        }
        if (zenRule.id != null) {
            jSONArray.put(1);
            jSONArray.put(zenRule.id);
        } else {
            jSONArray.put(0);
        }
        jSONArray.put(zenRule.creationTime);
        if (mCurrentAndroidVerion >= ANDROID_VERSION_O) {
            if (zenRule.enabler != null) {
                jSONArray.put(1);
                jSONArray.put(zenRule.enabler);
            } else {
                jSONArray.put(0);
            }
        }
        if (mDebug) {
            Log.d("SettingsBackup", "miui-audio: getRules volumes " + jSONArray);
        }
        return jSONArray;
    }

    private static JSONArray getTimeMute(Context context) {
        JSONArray jSONArray = new JSONArray();
        ZenModeConfig zenModeConfig = NotificationManager.from(context).getZenModeConfig();
        jSONArray.put(zenModeConfig.automaticRules.size());
        Iterator it = zenModeConfig.automaticRules.values().iterator();
        while (it.hasNext()) {
            jSONArray.put(getRules((ZenModeConfig.ZenRule) it.next()));
        }
        return jSONArray;
    }

    private static JSONArray getVipLists(Context context) {
        ZenModeConfig zenModeConfig = ExtraNotificationManager.getZenModeConfig(context);
        JSONArray jSONArray = new JSONArray();
        jSONArray.put(zenModeConfig.allowCalls);
        if (zenModeConfig.allowCalls) {
            jSONArray.put(zenModeConfig.allowCallsFrom);
            jSONArray.put(zenModeConfig.allowMessagesFrom);
        }
        return jSONArray;
    }

    private static boolean isValidAutomaticRule(ZenModeConfig.ZenRule zenRule) {
        return (zenRule == null || TextUtils.isEmpty(zenRule.name) || !Settings.Global.isValidZenMode(zenRule.zenMode) || zenRule.conditionId == null || zenRule.component == null) ? false : true;
    }

    private static int parseRule(Context context, JSONArray jSONArray) {
        ZenModeConfig.ZenRule zenRule = new ZenModeConfig.ZenRule();
        zenRule.enabled = jSONArray.optInt(0) == 1;
        zenRule.snoozing = false;
        jSONArray.optInt(1);
        zenRule.name = null;
        int i = 3;
        if (jSONArray.optInt(2) == 1) {
            zenRule.name = jSONArray.optString(3);
            i = 4;
        }
        int i2 = i + 1;
        zenRule.zenMode = jSONArray.optInt(i);
        zenRule.conditionId = null;
        int i3 = i2 + 1;
        if (jSONArray.optInt(i2) == 1) {
            ZenModeConfig.ScheduleInfo scheduleInfo = new ZenModeConfig.ScheduleInfo();
            int i4 = i3 + 1;
            scheduleInfo.days = tryParseDayList(jSONArray.optString(i3), "\\.");
            int i5 = i4 + 1;
            scheduleInfo.startHour = jSONArray.optInt(i4);
            int i6 = i5 + 1;
            scheduleInfo.startMinute = jSONArray.optInt(i5);
            int i7 = i6 + 1;
            scheduleInfo.endHour = jSONArray.optInt(i6);
            int i8 = i7 + 1;
            scheduleInfo.endMinute = jSONArray.optInt(i7);
            i3 = i8 + 1;
            scheduleInfo.exitAtAlarm = jSONArray.optBoolean(i8);
            zenRule.conditionId = ZenModeConfig.toScheduleConditionId(scheduleInfo);
        }
        zenRule.condition = null;
        int i9 = i3 + 1;
        jSONArray.optInt(i3);
        zenRule.component = null;
        int i10 = i9 + 1;
        if (jSONArray.optInt(i9) == 1) {
            int i11 = i10 + 1;
            zenRule.component = new ComponentName(jSONArray.optString(i10), jSONArray.optString(i11));
            i10 = i11 + 1;
        }
        zenRule.id = null;
        int i12 = i10 + 1;
        if (jSONArray.optInt(i10) == 1) {
            zenRule.id = jSONArray.optString(i12);
            i12++;
        }
        int i13 = i12 + 1;
        zenRule.creationTime = jSONArray.optLong(i12);
        if (mBackupAndroidVerion >= ANDROID_VERSION_O) {
            zenRule.enabler = null;
            int i14 = i13 + 1;
            if (jSONArray.optInt(i13) == 1) {
                i13 = i14 + 1;
                zenRule.enabler = jSONArray.optString(i14);
            } else {
                i13 = i14;
            }
        }
        if (mDebug) {
            Log.d("SettingsBackup", "miui-audio: parseRule " + zenRule.toString());
        }
        if (isValidAutomaticRule(zenRule)) {
            NotificationManager.from(context).addAutomaticZenRule(createAutomaticZenRule(zenRule));
        } else {
            Log.w("SettingsBackup", "Failed to restore auto zen rule " + jSONArray);
        }
        return i13;
    }

    public static void removeCurrentRules(Context context) {
        Iterator it = NotificationManager.from(context).getZenModeConfig().automaticRules.keySet().iterator();
        while (it.hasNext()) {
            NotificationManager.from(context).removeAutomaticZenRule((String) it.next());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void restoreFromCloud(Context context, JSONObject jSONObject) {
        if (jSONObject != null) {
            mDebug = SystemProperties.getBoolean("debug.backup.silent_settings", false);
            if (mCurrentAndroidVerion >= ANDROID_VERSION_O) {
                mBackupAndroidVerion = jSONObject.optInt("key_android_version");
            }
            if (mDebug) {
                Log.d("SettingsBackup", "miui-audio: restoreFromCloud from version " + mBackupAndroidVerion);
            }
            MiuiSettings.SoundMode.setSilenceModeOn(context, jSONObject.optBoolean("key_silent_mode"));
            MiuiSettings.SoundMode.setZenModeOn(context, jSONObject.optBoolean("key_zen_mode"), "SettingsBackup");
            setVipLists(context, jSONObject.optJSONArray("key_vip_list"));
            MiuiSettings.AntiSpam.setRepeatedCallActionEnable(context, jSONObject.optBoolean("key_repeat"));
            Settings.System.putIntForUser(context.getContentResolver(), "mute_music_at_silent", jSONObject.optInt("key_mute_music") > 0 ? 1 : 0, -3);
            Settings.System.putIntForUser(context.getContentResolver(), "show_notification", jSONObject.optInt("key_popup_window") > 0 ? 1 : 0, -3);
            restoreTimeMute(context, jSONObject.optJSONArray("key_timing_mute"));
        }
    }

    private static void restoreTimeMute(Context context, JSONArray jSONArray) {
        removeCurrentRules(context);
        int i = 0;
        int optInt = jSONArray.optInt(0);
        if (mDebug) {
            Log.d("SettingsBackup", "miui-audio: restoreTimeMute ruleNum " + optInt + " volumes " + jSONArray);
        }
        int i2 = 1;
        while (i < optInt) {
            int i3 = i2 + 1;
            try {
                parseRule(context, jSONArray.getJSONArray(i2));
            } catch (JSONException e) {
                Log.w("SettingsBackup", "miui-audio: parseRule " + i + " " + e.toString());
                CloudBackupException.trackException();
            }
            i++;
            i2 = i3;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static JSONObject saveToCloud(Context context) {
        JSONObject jSONObject = new JSONObject();
        boolean z = SystemProperties.getBoolean("debug.backup.silent_settings", false);
        mDebug = z;
        if (z) {
            Log.d("SettingsBackup", "miui-audio: saveToCloud start");
        }
        try {
            int i = mCurrentAndroidVerion;
            if (i >= ANDROID_VERSION_O) {
                jSONObject.put("key_android_version", i);
            }
            jSONObject.put("key_silent_mode", MiuiSettings.SoundMode.isSilenceModeOn(context));
            jSONObject.put("key_zen_mode", MiuiSettings.SoundMode.isZenModeOn(context));
            jSONObject.put("key_vip_list", getVipLists(context));
            jSONObject.put("key_repeat", MiuiSettings.AntiSpam.isRepeatedCallActionEnable(context));
            jSONObject.put("key_mute_music", Settings.System.getIntForUser(context.getContentResolver(), "mute_music_at_silent", 1, -3));
            jSONObject.put("key_popup_window", Settings.System.getIntForUser(context.getContentResolver(), "show_notification", 1, -3));
            jSONObject.put("key_timing_mute", getTimeMute(context));
        } catch (JSONException e) {
            Log.e("SettingsBackup", "miui-audio: SilentSettings build json error:", e);
            CloudBackupException.trackException();
        }
        return jSONObject;
    }

    private static void setVipLists(Context context, JSONArray jSONArray) {
        boolean optBoolean = jSONArray.optBoolean(0);
        MiuiSettings.SilenceMode.enableVIPMode(context, optBoolean);
        if (optBoolean) {
            ZenModeConfig zenModeConfig = ExtraNotificationManager.getZenModeConfig(context);
            zenModeConfig.allowCallsFrom = jSONArray.optInt(1);
            zenModeConfig.allowMessagesFrom = jSONArray.optInt(2);
            ExtraNotificationManager.setZenModeConfig(context, zenModeConfig);
        }
    }

    private static String toDayList(int[] iArr) {
        if (iArr == null || iArr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < iArr.length; i++) {
            if (i > 0) {
                sb.append('.');
            }
            sb.append(iArr[i]);
        }
        return sb.toString();
    }

    private static int[] tryParseDayList(String str, String str2) {
        if (str == null) {
            return null;
        }
        String[] split = str.split(str2);
        if (split.length == 0) {
            return null;
        }
        int[] iArr = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            int tryParseInt = tryParseInt(split[i], -1);
            if (tryParseInt == -1) {
                return null;
            }
            iArr[i] = tryParseInt;
        }
        return iArr;
    }

    private static int tryParseInt(String str, int i) {
        if (TextUtils.isEmpty(str)) {
            return i;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            Log.w("SettingsBackup", "miui-audio: tryParseInt fail " + e.toString());
            CloudBackupException.trackException("NumberFormatException");
            return i;
        }
    }
}
