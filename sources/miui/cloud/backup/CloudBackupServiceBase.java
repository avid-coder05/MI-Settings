package miui.cloud.backup;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.util.Log;
import java.util.Map;
import miui.cloud.backup.data.DataPackage;
import miui.cloud.backup.data.SettingItem;

/* loaded from: classes3.dex */
public abstract class CloudBackupServiceBase extends IntentService {
    public static final String ACTION_CLOUD_BACKUP_SETTINGS = "miui.action.CLOUD_BACKUP_SETTINGS";
    public static final String ACTION_CLOUD_RESTORE_SETTINGS = "miui.action.CLOUD_RESTORE_SETTINGS";
    public static final String KEY_RESULT_RECEIVER = "result_receiver";
    private static final String TAG = "SettingsBackup";

    public CloudBackupServiceBase() {
        super("SettingsBackup");
    }

    private Bundle backupSettings() {
        Log.d("SettingsBackup", prependPackageName("SettingsBackupServiceBase:backupSettings"));
        ICloudBackup checkAndGetBackuper = checkAndGetBackuper();
        DataPackage dataPackage = new DataPackage();
        checkAndGetBackuper.onBackupSettings(this, dataPackage);
        Bundle bundle = new Bundle();
        dataPackage.appendToWrappedBundle(bundle);
        bundle.putInt("version", checkAndGetBackuper.getCurrentVersion(this));
        return bundle;
    }

    private ICloudBackup checkAndGetBackuper() {
        ICloudBackup backupImpl = getBackupImpl();
        if (backupImpl != null) {
            return backupImpl;
        }
        throw new IllegalArgumentException("backuper must not be null");
    }

    protected static void dumpDataPackage(DataPackage dataPackage) {
        for (Map.Entry<String, SettingItem<?>> entry : dataPackage.getDataItems().entrySet()) {
            Log.d("SettingsBackup", "key: " + entry.getKey() + ", value: " + entry.getValue().getValue());
        }
    }

    private String prependPackageName(String str) {
        return getPackageName() + ": " + str;
    }

    private boolean restoreSettings(DataPackage dataPackage, int i) {
        Log.d("SettingsBackup", prependPackageName("SettingsBackupServiceBase:restoreSettings"));
        ICloudBackup checkAndGetBackuper = checkAndGetBackuper();
        int currentVersion = checkAndGetBackuper.getCurrentVersion(this);
        if (i <= currentVersion) {
            checkAndGetBackuper.onRestoreSettings(this, dataPackage, i);
            return true;
        }
        Log.w("SettingsBackup", "drop restore data because dataVersion is higher than currentAppVersion, dataVersion: " + i + ", currentAppVersion: " + currentVersion);
        return false;
    }

    protected abstract ICloudBackup getBackupImpl();

    @Override // android.app.IntentService
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        Log.d("SettingsBackup", prependPackageName("myPid: " + Process.myPid()));
        Log.d("SettingsBackup", prependPackageName("intent: " + intent));
        Log.d("SettingsBackup", prependPackageName("extras: " + intent.getExtras()));
        String action = intent.getAction();
        ResultReceiver resultReceiver = (ResultReceiver) intent.getParcelableExtra("result_receiver");
        if ("miui.action.CLOUD_BACKUP_SETTINGS".equals(action)) {
            if (resultReceiver != null) {
                Bundle backupSettings = backupSettings();
                if (backupSettings == null) {
                    Log.e("SettingsBackup", prependPackageName("bundle result is null after backupSettings"));
                }
                resultReceiver.send(0, backupSettings);
            }
        } else if (!"miui.action.CLOUD_RESTORE_SETTINGS".equals(action) || resultReceiver == null) {
        } else {
            IBinder binder = intent.getExtras().getBinder("data_package");
            Parcel obtain = Parcel.obtain();
            Parcel obtain2 = Parcel.obtain();
            try {
                binder.transact(2, obtain, obtain2, 0);
                boolean restoreSettings = restoreSettings((DataPackage) obtain2.readParcelable(getClass().getClassLoader()), intent.getIntExtra("version", -1));
                Log.d("SettingsBackup", prependPackageName("r.send()" + Thread.currentThread()));
                if (restoreSettings) {
                    resultReceiver.send(0, new Bundle());
                } else {
                    resultReceiver.send(0, null);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } finally {
                obtain.recycle();
                obtain2.recycle();
            }
        }
    }
}
