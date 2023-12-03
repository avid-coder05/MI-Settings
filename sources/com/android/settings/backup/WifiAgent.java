package com.android.settings.backup;

import android.app.backup.FullBackupDataOutput;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.android.settings.utils.LogUtil;
import com.android.settings.wifi.MiuiWifiService;
import com.android.settings.wifi.WifiConfigForSupplicant;
import com.android.settings.wifi.WifiConfigurationManager;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.app.backup.BackupMeta;
import miui.app.backup.FullBackupAgent;

/* loaded from: classes.dex */
public class WifiAgent extends AgentBase {
    private Context mContext;

    public WifiAgent(FullBackupAgent fullBackupAgent) {
        super(fullBackupAgent);
        this.mContext = fullBackupAgent.getApplicationContext();
    }

    @Override // com.android.settings.backup.AgentBase
    public int endRestore(BackupMeta backupMeta) {
        return 0;
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:27:0x0057 -> B:38:0x005a). Please submit an issue!!! */
    @Override // com.android.settings.backup.AgentBase
    public int fullBackup(ParcelFileDescriptor parcelFileDescriptor) throws IOException {
        FileOutputStream fileOutputStream;
        LogUtil.logCloudSync("Backup:WifiAgent", "-------------fullBackup begin-------------");
        FileOutputStream fileOutputStream2 = null;
        try {
            try {
                try {
                    fileOutputStream = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
                } catch (IOException e) {
                    Log.e("Backup:WifiAgent", "IOException", e);
                }
            } catch (IOException e2) {
                e = e2;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            List<String> configuredNetworks = WifiConfigForSupplicant.getInstance().getConfiguredNetworks(this.mContext);
            if (configuredNetworks != null && configuredNetworks.size() != 0) {
                Iterator<String> it = configuredNetworks.iterator();
                while (it.hasNext()) {
                    fileOutputStream.write(it.next().getBytes());
                }
                fileOutputStream.flush();
            }
            fileOutputStream.close();
        } catch (IOException e3) {
            e = e3;
            fileOutputStream2 = fileOutputStream;
            Log.e("Backup:WifiAgent", "IOException", e);
            if (fileOutputStream2 != null) {
                fileOutputStream2.close();
            }
            LogUtil.logCloudSync("Backup:WifiAgent", "-------------fullBackup   end-------------");
            return 0;
        } catch (Throwable th2) {
            th = th2;
            fileOutputStream2 = fileOutputStream;
            if (fileOutputStream2 != null) {
                try {
                    fileOutputStream2.close();
                } catch (IOException e4) {
                    Log.e("Backup:WifiAgent", "IOException", e4);
                }
            }
            throw th;
        }
        LogUtil.logCloudSync("Backup:WifiAgent", "-------------fullBackup   end-------------");
        return 0;
    }

    @Override // com.android.settings.backup.AgentBase
    public int getBackupVersion() {
        return 1;
    }

    @Override // com.android.settings.backup.AgentBase
    public int restoreAttaches(BackupMeta backupMeta, ParcelFileDescriptor parcelFileDescriptor, String str) {
        return 0;
    }

    @Override // com.android.settings.backup.AgentBase
    public int restoreData(BackupMeta backupMeta, ParcelFileDescriptor parcelFileDescriptor) throws IOException {
        LogUtil.logCloudSync("Backup:WifiAgent", "-------------restoreData begin-------------");
        WifiConfigForSupplicant.getInstance().storeWifiConfigs(this.mAgent, parcelFileDescriptor.getFileDescriptor());
        ArrayList<WifiConfiguration> filterUnsavedWifiConfigurations = WifiConfigurationManager.getInstance(this.mAgent).filterUnsavedWifiConfigurations(WifiConfigForSupplicant.getInstance().getRestoreWifiConfigs(this.mAgent));
        LogUtil.logCloudSync("Backup:WifiAgent", "-------------restoreData   end-------------");
        if (filterUnsavedWifiConfigurations != null) {
            Intent intent = new Intent((Context) this.mAgent, (Class<?>) MiuiWifiService.class);
            intent.setAction("miui.intent.action.RESTORE_WIFI_CONFIGURATIONS");
            intent.putParcelableArrayListExtra("wifiConfiguration", filterUnsavedWifiConfigurations);
            this.mAgent.startService(intent);
            return 0;
        }
        return 0;
    }

    @Override // com.android.settings.backup.AgentBase
    public int tarAttaches(String str, FullBackupDataOutput fullBackupDataOutput) {
        return 0;
    }
}
