package com.android.settings.cloudbackup;

import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.util.Log;
import com.android.settings.MiuiUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import miui.settings.commonlib.SoundDefaultValueUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
class SoundAndVibrateCloudBackupHelper {
    private static final String[] VOLUME_TYPE = {"volume_ring", "volume_alarm", "volume_music", "volume_assistant"};
    private static List<Integer> deviceLists = new ArrayList(AudioSystem.DEVICE_OUT_ALL_SET);

    private static JSONArray getVolumes(Context context, int i) {
        ContentResolver contentResolver = context.getContentResolver();
        JSONArray jSONArray = new JSONArray();
        Collections.sort(deviceLists);
        try {
            Iterator<Integer> it = deviceLists.iterator();
            while (it.hasNext()) {
                int intValue = it.next().intValue();
                String str = VOLUME_TYPE[i];
                String outputDeviceName = AudioSystem.getOutputDeviceName(intValue);
                if (!outputDeviceName.isEmpty()) {
                    str = str + "_" + outputDeviceName;
                }
                jSONArray.put(Settings.System.getInt(contentResolver, str, -1));
            }
        } catch (Exception e) {
            Log.e("SettingsBackup", "getVolumes error" + e.getMessage());
        }
        return jSONArray;
    }

    private static void putVolumes(Context context, int i, JSONArray jSONArray) {
        if (jSONArray == null) {
            return;
        }
        ContentResolver contentResolver = context.getContentResolver();
        Collections.sort(deviceLists);
        int i2 = 0;
        Iterator<Integer> it = deviceLists.iterator();
        while (it.hasNext()) {
            int intValue = it.next().intValue();
            String str = VOLUME_TYPE[i];
            String outputDeviceName = AudioSystem.getOutputDeviceName(intValue);
            if (!outputDeviceName.isEmpty()) {
                str = str + "_" + outputDeviceName;
            }
            Settings.System.putInt(contentResolver, str, jSONArray.optInt(i2));
            i2++;
        }
        String[] strArr = VOLUME_TYPE;
        Settings.System.putInt(contentResolver, strArr[i], Settings.System.getInt(contentResolver, strArr[i], 10));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void restoreFromCloud(Context context, JSONObject jSONObject) {
        if (jSONObject != null) {
            ContentResolver contentResolver = context.getContentResolver();
            putVolumes(context, 0, jSONObject.optJSONArray("ring_volume"));
            putVolumes(context, 1, jSONObject.optJSONArray("alarm_volume"));
            putVolumes(context, 2, jSONObject.optJSONArray("music_volume"));
            if (MiuiUtils.includeXiaoAi(context)) {
                putVolumes(context, 3, jSONObject.optJSONArray("voice_assist_volume"));
            }
            ((AudioManager) context.getSystemService("audio")).reloadAudioSettings();
            int optInt = jSONObject.optInt("locked_only_enabled", -1);
            if (optInt != -1) {
                Settings.System.putInt(contentResolver, "zen_mode_intercepted_when_unlocked", optInt);
            }
            updateVibrateInSilent(context, jSONObject.optInt("vibrate_in_silent", 1));
            updateVibrateInNormal(context, jSONObject.optInt("vibrate_when_ringing", MiuiSettings.System.VIBRATE_IN_NORMAL_DEFAULT ? 1 : 0));
            Settings.System.putInt(contentResolver, "dtmf_tone", jSONObject.optBoolean("dtmf_tone", true) ? 1 : 0);
            Settings.System.putInt(contentResolver, "sound_effects_enabled", jSONObject.optBoolean("sound_effects_enabled", true) ? 1 : 0);
            Settings.System.putInt(contentResolver, "lockscreen_sounds_enabled", jSONObject.optBoolean("lockscreen_sounds_enabled", true) ? 1 : 0);
            Settings.System.putInt(contentResolver, "haptic_feedback_enabled", jSONObject.optBoolean("haptic_feedback_enabled", true) ? 1 : 0);
            Settings.System.putInt(contentResolver, "haptic_feedback_level", jSONObject.optInt("haptic_feedback_level", MiuiSettings.System.HAPTIC_FEEDBACK_LEVEL_DEFAULT));
            MiuiSettings.System.putBoolean(contentResolver, "delete_sound_effect", jSONObject.optBoolean("delete_sound_effect_enabled", SoundDefaultValueUtil.getDeleteSoundEffectDefaultValue()));
            MiuiSettings.System.putBoolean(contentResolver, "launcher_app_delete_sound_effect", jSONObject.optBoolean("launcher_app_delete_sound_effect", SoundDefaultValueUtil.getAppDeleteSoundEffectDefaultValue()));
            Settings.Global.putInt(contentResolver, "power_sounds_enabled", jSONObject.optInt("power_sounds_enabled", 1));
            Settings.Global.putInt(contentResolver, "boot_audio", jSONObject.optInt("boot_audio", 1));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static JSONObject saveToCloud(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("ring_volume", getVolumes(context, 0));
            jSONObject.put("alarm_volume", getVolumes(context, 1));
            jSONObject.put("music_volume", getVolumes(context, 2));
            if (MiuiUtils.includeXiaoAi(context)) {
                jSONObject.put("voice_assist_volume", getVolumes(context, 3));
            }
            jSONObject.put("locked_only_enabled", Settings.System.getInt(context.getContentResolver(), "zen_mode_intercepted_when_unlocked", -1));
            jSONObject.put("vibrate_in_silent", Settings.System.getIntForUser(context.getContentResolver(), "vibrate_in_silent", 1, -3));
            jSONObject.put("vibrate_when_ringing", Settings.System.getIntForUser(context.getContentResolver(), "vibrate_in_normal", MiuiSettings.System.VIBRATE_IN_NORMAL_DEFAULT ? 1 : 0, -3));
            jSONObject.put("dtmf_tone", Settings.System.getInt(contentResolver, "dtmf_tone", 1) != 0);
            jSONObject.put("sound_effects_enabled", Settings.System.getInt(contentResolver, "sound_effects_enabled", 1) != 0);
            jSONObject.put("lockscreen_sounds_enabled", Settings.System.getInt(contentResolver, "lockscreen_sounds_enabled", 1) != 0);
            jSONObject.put("haptic_feedback_enabled", Settings.System.getInt(contentResolver, "haptic_feedback_enabled", 1) != 0);
            jSONObject.put("haptic_feedback_level", Settings.System.getInt(contentResolver, "haptic_feedback_level", MiuiSettings.System.HAPTIC_FEEDBACK_LEVEL_DEFAULT));
            jSONObject.put("delete_sound_effect_enabled", MiuiSettings.System.getBoolean(contentResolver, "delete_sound_effect", SoundDefaultValueUtil.getDeleteSoundEffectDefaultValue()));
            jSONObject.put("launcher_app_delete_sound_effect", MiuiSettings.System.getBoolean(contentResolver, "launcher_app_delete_sound_effect", SoundDefaultValueUtil.getAppDeleteSoundEffectDefaultValue()));
            jSONObject.put("power_sounds_enabled", Settings.Global.getInt(contentResolver, "power_sounds_enabled", 1));
            jSONObject.put("boot_audio", Settings.Global.getInt(contentResolver, "boot_audio", 1));
        } catch (JSONException e) {
            Log.e("SettingsBackup", "build json error:", e);
            CloudBackupException.trackException();
        }
        return jSONObject;
    }

    private static void updateVibrateInNormal(Context context, int i) {
        Settings.System.putIntForUser(context.getContentResolver(), "vibrate_in_normal", i, -3);
        Settings.System.putIntForUser(context.getContentResolver(), "vibrate_when_ringing", i, -3);
    }

    private static void updateVibrateInSilent(Context context, int i) {
        Settings.System.putIntForUser(context.getContentResolver(), "vibrate_in_silent", i, -3);
        AudioManager audioManager = (AudioManager) context.getSystemService("audio");
        if (audioManager.getRingerModeInternal() != 2) {
            audioManager.setRingerModeInternal(i > 0 ? 1 : 0);
        }
    }
}
