package com.android.settings.cloudbackup;

import android.content.Context;
import android.media.ExtraRingtoneManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import com.xiaomi.settingsdk.backup.data.DataPackage;
import com.xiaomi.settingsdk.backup.data.KeyStringSettingItem;
import com.xiaomi.settingsdk.backup.data.SettingItem;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import miui.app.constants.ThemeManagerConstants;

/* loaded from: classes.dex */
public class RingtoneCloudBackupHelper {
    private static final Uri DEFAULT_AUDIO_BASE_URI = Uri.parse("file:///system/media/audio/");
    private static final Map<String, Integer> BACKUP_SOUND_TYPES_MAP = new HashMap<String, Integer>() { // from class: com.android.settings.cloudbackup.RingtoneCloudBackupHelper.1
        {
            put(ThemeManagerConstants.COMPONENT_CODE_RINGTONE, 1);
            put("notification_sound", 2);
            put("alarm_alert", 4);
            put("sms_delivered_sound", 8);
            put("sms_received_sound", 16);
            put("calendar_alert", 4096);
            put("notes_alert", 8192);
            put("ringtone_sound_slot_1", 64);
            put("ringtone_sound_slot_2", 128);
            put("sms_received_sound_slot_1", Integer.valueOf((int) MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE));
            put("sms_received_sound_slot_2", Integer.valueOf((int) MiuiWindowManager$LayoutParams.EXTRA_FLAG_FINDDEVICE_KEYGUARD));
            put("sms_delivered_sound_slot_1", 256);
            put("sms_delivered_sound_slot_2", 512);
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void backupRingtones(Context context, DataPackage dataPackage) {
        Uri defaultSoundSettingUri;
        File ringtoneFile;
        for (Map.Entry<String, Integer> entry : BACKUP_SOUND_TYPES_MAP.entrySet()) {
            String key = entry.getKey();
            int intValue = entry.getValue().intValue();
            if (!TextUtils.isEmpty(key) && (defaultSoundSettingUri = ExtraRingtoneManager.getDefaultSoundSettingUri(context, intValue)) != null && (ringtoneFile = getRingtoneFile(defaultSoundSettingUri)) != null && ringtoneFile.exists()) {
                try {
                    Log.d("SettingsCloudBackup", "key: " + key + " ringtone path: " + ringtoneFile.getPath());
                    dataPackage.addKeyValue(key, defaultSoundSettingUri.toString());
                    dataPackage.addKeyFile(ringtoneFile.getPath(), ringtoneFile);
                } catch (FileNotFoundException e) {
                    Log.w("SettingsCloudBackup", "can not find ringtone file: " + key, e);
                    CloudBackupException.trackException("FileNotFoundException");
                }
            }
        }
    }

    private static String getFilePath(Uri uri) {
        try {
            String decode = URLDecoder.decode(uri.toString(), "utf-8");
            Log.d("SettingsCloudBackup", "decoded ringtone url: " + decode);
            URL url = new URL(decode);
            if (url.getProtocol().equals("file")) {
                return url.getPath();
            }
            return null;
        } catch (UnsupportedEncodingException | MalformedURLException e) {
            Log.w("SettingsCloudBackup", "getRingtoneFile() ", e);
            CloudBackupException.trackException("URLEncodingException");
            return null;
        }
    }

    private static File getRingtoneFile(Uri uri) {
        String filePath = getFilePath(uri);
        if (filePath != null) {
            return new File(filePath);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void restoreRingtones(Context context, DataPackage dataPackage) {
        SettingItem<?> settingItem;
        for (Map.Entry<String, Integer> entry : BACKUP_SOUND_TYPES_MAP.entrySet()) {
            String key = entry.getKey();
            int intValue = entry.getValue().intValue();
            if (!TextUtils.isEmpty(key) && (settingItem = dataPackage.get(key)) != null && (settingItem instanceof KeyStringSettingItem)) {
                String value = ((KeyStringSettingItem) settingItem).getValue();
                if (!TextUtils.isEmpty(value)) {
                    Log.i("RingtoneCloudBackupHelper", "restoreRingtones type: " + intValue + " value: " + value);
                    Uri parse = Uri.parse(value);
                    if (value.contains("system/media/")) {
                        File file = new File(parse.getPath());
                        if (!file.exists()) {
                            Log.w("RingtoneCloudBackupHelper", "file not exist: " + file.getPath());
                        }
                    }
                    ExtraRingtoneManager.saveDefaultSound(context, intValue, parse);
                }
            }
        }
    }
}
