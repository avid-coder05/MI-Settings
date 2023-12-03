package com.android.settings.search.tree;

import android.app.backup.IBackupManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import com.android.settingslib.search.SettingsTree;
import miui.os.Build;
import miui.provider.ExtraContacts;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class BackupSettingsTree extends SettingsTree {
    private static final String GSETTINGS_PROVIDER = "com.google.settings";
    private boolean mIsSupportBRWithUsb;

    protected BackupSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
        this.mIsSupportBRWithUsb = supportBackupRestoreWithUsb();
    }

    private boolean supportBackupRestoreWithUsb() {
        if (Build.IS_TABLET) {
            return false;
        }
        try {
            Bundle bundle = ((SettingsTree) this).mContext.getPackageManager().getPackageInfo("com.miui.backup", 128).applicationInfo.metaData;
            if (bundle != null) {
                return bundle.getBoolean("com.miui.backup.hasBackupToUsbFeature");
            }
            return false;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public Intent getIntent() {
        String columnValue = getColumnValue("resource");
        if ("cloud_restore_section_title".equals(columnValue) || "cloud_backup_settings_section_title".equals(columnValue)) {
            if (getParent() != null) {
                return getParent().getIntent();
            }
        } else if ("backup_configure_account_title".equals(columnValue)) {
            IBackupManager asInterface = IBackupManager.Stub.asInterface(ServiceManager.getService(ExtraContacts.Calls.BACKUP_PARAM));
            try {
                return asInterface.getConfigurationIntent(asInterface.getCurrentTransport());
            } catch (RemoteException unused) {
                return null;
            }
        }
        return super.getIntent();
    }

    /* JADX WARN: Code restructure failed: missing block: B:46:0x00a0, code lost:
    
        if (r0.isEmpty() != false) goto L47;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected int getStatus() {
        /*
            r8 = this;
            android.content.Context r0 = r8.mContext
            android.content.pm.PackageManager r0 = r0.getPackageManager()
            java.lang.String r1 = "com.google.settings"
            r2 = 0
            android.content.pm.ProviderInfo r0 = r0.resolveContentProvider(r1, r2)
            r1 = 1
            if (r0 != 0) goto L12
            r0 = r1
            goto L13
        L12:
            r0 = r2
        L13:
            java.lang.String r3 = "backup"
            android.os.IBinder r3 = android.os.ServiceManager.getService(r3)
            android.app.backup.IBackupManager r3 = android.app.backup.IBackupManager.Stub.asInterface(r3)
            int r4 = android.os.UserHandle.myUserId()     // Catch: android.os.RemoteException -> L26
            boolean r4 = r3.isBackupServiceActive(r4)     // Catch: android.os.RemoteException -> L26
            goto L27
        L26:
            r4 = r2
        L27:
            java.lang.String r5 = "category_origin"
            java.lang.String r5 = r8.getColumnValue(r5)
            java.lang.String r6 = "resource"
            java.lang.String r6 = r8.getColumnValue(r6)
            java.lang.String r7 = "google_backup_section_title"
            boolean r5 = r7.equals(r5)
            if (r5 == 0) goto L3f
            if (r0 == 0) goto L4c
            return r2
        L3f:
            java.lang.String r5 = "flash_drive_backup_restore"
            boolean r5 = r5.equals(r6)
            if (r5 == 0) goto L4c
            boolean r5 = r8.mIsSupportBRWithUsb
            if (r5 != 0) goto L4c
            return r2
        L4c:
            java.lang.String r5 = "backup_inactive_title"
            boolean r5 = r5.equals(r6)
            if (r5 == 0) goto L5b
            if (r0 != 0) goto L5a
            if (r4 == 0) goto L59
            goto L5a
        L59:
            return r1
        L5a:
            return r2
        L5b:
            java.lang.String r5 = "backup_data_title"
            boolean r5 = r5.equals(r6)
            java.lang.String r7 = "backup_configure_account_title"
            if (r5 != 0) goto L73
            boolean r5 = r7.equals(r6)
            if (r5 != 0) goto L73
            java.lang.String r5 = "auto_restore_title"
            boolean r5 = r5.equals(r6)
            if (r5 == 0) goto La3
        L73:
            if (r0 != 0) goto La9
            if (r4 != 0) goto L78
            goto La9
        L78:
            boolean r0 = r3.isBackupEnabled()     // Catch: android.os.RemoteException -> La8
            if (r0 != 0) goto L7f
            return r1
        L7f:
            boolean r0 = r7.equals(r6)     // Catch: android.os.RemoteException -> La8
            if (r0 == 0) goto La3
            java.lang.String r0 = r3.getCurrentTransport()     // Catch: android.os.RemoteException -> La8
            android.content.Intent r0 = r3.getConfigurationIntent(r0)     // Catch: android.os.RemoteException -> La8
            if (r0 != 0) goto L90
            return r1
        L90:
            android.content.Context r3 = r8.mContext     // Catch: android.os.RemoteException -> La8
            android.content.pm.PackageManager r3 = r3.getPackageManager()     // Catch: android.os.RemoteException -> La8
            java.util.List r0 = r3.queryIntentActivities(r0, r2)     // Catch: android.os.RemoteException -> La8
            if (r0 == 0) goto La2
            boolean r0 = r0.isEmpty()     // Catch: android.os.RemoteException -> La8
            if (r0 == 0) goto La3
        La2:
            return r1
        La3:
            int r8 = super.getStatus()
            return r8
        La8:
            return r1
        La9:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.search.tree.BackupSettingsTree.getStatus():int");
    }
}
