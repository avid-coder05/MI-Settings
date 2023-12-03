package com.android.settings.cloudbackup;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;
import com.xiaomi.settingsdk.backup.ICloudBackup;
import com.xiaomi.settingsdk.backup.SettingsBackupHelper;
import com.xiaomi.settingsdk.backup.data.DataPackage;
import miui.os.Build;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class SettingsCloudBackupImpl implements ICloudBackup {
    private String getWeiboAccount(Context context) {
        for (Account account : ((AccountManager) context.getSystemService("account")).getAccounts()) {
            if ("com.sina.weibo.account".equals(account.type)) {
                return account.name;
            }
        }
        return "";
    }

    private static JSONObject logJSON(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        if (!Build.IS_OFFICIAL_VERSION) {
            Log.v("SettingsCloudBackup", jSONObject.toString());
        }
        return jSONObject;
    }

    @Override // com.xiaomi.settingsdk.backup.ICloudBackup
    public int getCurrentVersion(Context context) {
        return 1;
    }

    @Override // com.xiaomi.settingsdk.backup.ICloudBackup
    public void onBackupSettings(Context context, DataPackage dataPackage) {
        Log.i("SettingsCloudBackup", "start settings backup. ");
        dataPackage.addKeyJson("Connection", logJSON(ConnectionCloudBackupHelper.saveToCloud(context)));
        dataPackage.addKeyValue("weiboAccount", getWeiboAccount(context));
        dataPackage.addKeyJson("NotificationFilter", logJSON(NotificationCloudBackupHelper.saveToCloud(context)));
        dataPackage.addKeyJson("StatusBar", logJSON(StatusBarCloudBackupHelper.saveToCloud(context)));
        dataPackage.addKeyJson("AdvanceSettings", logJSON(AdvancedSettingsCloudBackupHelper.saveToCloud(context)));
        dataPackage.addKeyJson("ScreenKeySettings", logJSON(KeySettingsCloudBackupHelper.saveToCloud(context)));
        dataPackage.addKeyJson("SoundAndVibrateSettings", logJSON(SoundAndVibrateCloudBackupHelper.saveToCloud(context)));
        dataPackage.addKeyJson("DisplaySettings", logJSON(DisplaySettingsCloudBackupHelper.saveToCloud(context)));
        dataPackage.addKeyJson("Accessibility", logJSON(AccessibilityCloudBackupHelper.saveToCloud(context)));
        dataPackage.addKeyJson("LockScreen", logJSON(LockScreenSettingsCloudBackupHelper.saveToCloud(context)));
        dataPackage.addKeyJson("SilentSettings", logJSON(SilentSettingsCloudBackupHelper.saveToCloud(context)));
        dataPackage.addKeyJson("DefaultAppSettings", logJSON(DefaultAppSettingsCloudBackupHelper.saveToCloud(context)));
        RingtoneCloudBackupHelper.backupRingtones(context, dataPackage);
        Log.i("SettingsCloudBackup", "end settings backup. ");
    }

    @Override // com.xiaomi.settingsdk.backup.ICloudBackup
    public void onRestoreSettings(Context context, DataPackage dataPackage, int i) {
        Log.i("SettingsCloudBackup", "start settings restore. ");
        try {
            if (dataPackage.get("Connection") != null) {
                ConnectionCloudBackupHelper.restoreFromCloud(context, logJSON((JSONObject) dataPackage.get("Connection").getValue()));
            }
            if (dataPackage.get("NotificationFilter") != null) {
                NotificationCloudBackupHelper.restoreFromCloud(context, logJSON((JSONObject) dataPackage.get("NotificationFilter").getValue()));
            }
            if (dataPackage.get("StatusBar") != null) {
                StatusBarCloudBackupHelper.restoreFromCloud(context, logJSON((JSONObject) dataPackage.get("StatusBar").getValue()));
            }
            if (dataPackage.get("AdvanceSettings") != null) {
                AdvancedSettingsCloudBackupHelper.restoreFromCloud(context, logJSON((JSONObject) dataPackage.get("AdvanceSettings").getValue()));
            }
            if (dataPackage.get("ScreenKeySettings") != null) {
                KeySettingsCloudBackupHelper.restoreFromCloud(context, logJSON((JSONObject) dataPackage.get("ScreenKeySettings").getValue()));
            }
            if (dataPackage.get("SoundAndVibrateSettings") != null) {
                SoundAndVibrateCloudBackupHelper.restoreFromCloud(context, logJSON((JSONObject) dataPackage.get("SoundAndVibrateSettings").getValue()));
            }
            if (dataPackage.get("DisplaySettings") != null) {
                DisplaySettingsCloudBackupHelper.restoreFromCloud(context, logJSON((JSONObject) dataPackage.get("DisplaySettings").getValue()));
            }
            if (dataPackage.get("Accessibility") != null) {
                AccessibilityCloudBackupHelper.restoreFromCloud(context, logJSON((JSONObject) dataPackage.get("Accessibility").getValue()));
            }
            if (dataPackage.get("LockScreen") != null) {
                LockScreenSettingsCloudBackupHelper.restoreFromCloud(context, logJSON((JSONObject) dataPackage.get("LockScreen").getValue()));
            }
            if (dataPackage.get("SilentSettings") != null) {
                SilentSettingsCloudBackupHelper.restoreFromCloud(context, logJSON((JSONObject) dataPackage.get("SilentSettings").getValue()));
            }
            if (dataPackage.get("DefaultAppSettings") != null) {
                DefaultAppSettingsCloudBackupHelper.restoreFromCloud(context, logJSON((JSONObject) dataPackage.get("DefaultAppSettings").getValue()));
            }
            RingtoneCloudBackupHelper.restoreRingtones(context, dataPackage);
            SettingsBackupHelper.restoreFiles(dataPackage);
            Log.i("SettingsCloudBackup", "end settings restore. ");
        } catch (Exception unused) {
            Log.e("SettingsCloudBackup", "settings restore exception.");
            CloudBackupException.trackException("CloudBackupRestoreException");
        }
    }
}
