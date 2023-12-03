package com.android.settings.backup;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.media.ExtraRingtoneManager;
import android.net.Uri;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.text.TextUtils;
import com.android.internal.widget.ILockSettings;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import miui.app.constants.ThemeManagerConstants;
import miuix.core.util.FileUtils;
import miuix.os.Environment;

/* loaded from: classes.dex */
public class SettingManager {
    protected Context mContext;
    private HashMap<String, String> mFileName2Path;
    private ILockSettings mLockSettingsService;
    protected ContentResolver mResolver;

    public SettingManager(Context context) {
        this.mContext = context;
        this.mResolver = context.getContentResolver();
    }

    private ILockSettings getLockSettings() {
        if (this.mLockSettingsService == null) {
            this.mLockSettingsService = ILockSettings.Stub.asInterface(ServiceManager.getService("lock_settings"));
        }
        return this.mLockSettingsService;
    }

    public boolean addLockSetting(SettingProtos$LockSetting settingProtos$LockSetting) {
        try {
            getLockSettings().setLong(settingProtos$LockSetting.getName(), settingProtos$LockSetting.getValue(), 0);
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Uri addSecureSetting(SettingProtos$SecureSetting settingProtos$SecureSetting) throws IOException {
        if (!settingProtos$SecureSetting.hasName() || settingProtos$SecureSetting.getName() == null || settingProtos$SecureSetting.getName().length() == 0) {
            throw new IOException("Cannot add secure setting which has empty name");
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", settingProtos$SecureSetting.getName());
        if (settingProtos$SecureSetting.hasValue()) {
            contentValues.put("value", settingProtos$SecureSetting.getValue());
        }
        return this.mResolver.insert(Settings.Secure.CONTENT_URI, contentValues);
    }

    public Uri addSystemSetting(SettingProtos$SystemSetting settingProtos$SystemSetting) throws IOException {
        if (!settingProtos$SystemSetting.hasName() || settingProtos$SystemSetting.getName() == null || settingProtos$SystemSetting.getName().length() == 0) {
            throw new IOException("Cannot add system setting which has empty name");
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", settingProtos$SystemSetting.getName());
        if (settingProtos$SystemSetting.hasValue()) {
            contentValues.put("value", settingProtos$SystemSetting.getValue());
        }
        return this.mResolver.insert(Settings.System.CONTENT_URI, contentValues);
    }

    public void applyRingtone(String str, int i) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        String str2 = this.mFileName2Path.get(str);
        if (TextUtils.isEmpty(str2)) {
            return;
        }
        File file = new File(str2);
        if (file.exists()) {
            File file2 = new File(Environment.getExternalStorageMiuiDirectory(), ThemeManagerConstants.COMPONENT_CODE_RINGTONE);
            if (!file2.exists()) {
                file2.mkdirs();
            }
            File file3 = new File(file2, file.getName());
            FileUtils.copyFile(file, file3);
            if (file3.exists()) {
                ExtraRingtoneManager.saveDefaultSound(this.mContext, i, Uri.fromFile(file3));
            }
            file.delete();
        }
    }

    public void restoreRingtone() {
        String str = this.mFileName2Path.get("settings_descript.xml");
        if (TextUtils.isEmpty(str)) {
            return;
        }
        File file = new File(str);
        if (file.exists()) {
            RingtoneDescript ringtoneDescript = new RingtoneDescript();
            ringtoneDescript.read(file);
            applyRingtone(ringtoneDescript.mRingtone, 1);
            applyRingtone(ringtoneDescript.mNotification, 2);
            applyRingtone(ringtoneDescript.mAlarm, 4);
            applyRingtone(ringtoneDescript.mSmsDelivered, 8);
            applyRingtone(ringtoneDescript.mSmsReceived, 16);
        }
    }

    public void restoreSysData() {
        for (SystemData systemData : Customization.DATA_SYSTEM_PARTITION) {
            String str = this.mFileName2Path.get(systemData.mFileName);
            if (!TextUtils.isEmpty(str)) {
                File file = new File(str);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }

    public void setFileName2Path(HashMap<String, String> hashMap) {
        this.mFileName2Path = hashMap;
    }
}
