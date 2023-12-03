package com.android.settings.search;

import android.app.backup.IBackupManager;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import java.util.ArrayList;
import java.util.Iterator;
import miui.accounts.ExtraAccountManager;
import miui.os.Build;
import miui.provider.ExtraContacts;

/* loaded from: classes2.dex */
class BackupSettingsUpdateHelper extends BaseSearchUpdateHelper {
    private static final String AUTO_RESTORE_RESOURCE = "auto_restore_title";
    private static final String BACKUP_DATA_RESOURCE = "backup_data_title";
    private static final String CLOUD_BACKUP_RESOURCE = "cloud_backup_settings_section_title";
    private static final String CLOUD_RESTORE_RESOURCE = "cloud_restore_section_title";
    private static final String CONFIGURE_ACCOUNT_RESOURCE = "backup_configure_account_title";
    private static final String LOCAL_AUTO_BACKUP_RESOURCE = "local_auto_backup_section_title";
    private static final String LOCAL_BACKUP_RESOURCE = "local_backup_section_title";

    BackupSettingsUpdateHelper() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void update(Context context, ArrayList<ContentProviderOperation> arrayList) {
        boolean z;
        if (ExtraAccountManager.getXiaomiAccount(context) == null) {
            BaseSearchUpdateHelper.disableByResource(context, arrayList, CLOUD_RESTORE_RESOURCE, true);
            BaseSearchUpdateHelper.disableByResource(context, arrayList, CLOUD_BACKUP_RESOURCE, true);
        }
        boolean z2 = false;
        if (context.getPackageManager().resolveContentProvider("com.google.settings", 0) == null) {
            BaseSearchUpdateHelper.hideTreeByRootResource(context, arrayList, BACKUP_DATA_RESOURCE);
            BaseSearchUpdateHelper.hideByResource(context, arrayList, CONFIGURE_ACCOUNT_RESOURCE);
            BaseSearchUpdateHelper.hideByResource(context, arrayList, AUTO_RESTORE_RESOURCE);
        } else {
            IBackupManager asInterface = IBackupManager.Stub.asInterface(ServiceManager.getService(ExtraContacts.Calls.BACKUP_PARAM));
            try {
                z = asInterface.isBackupEnabled();
                try {
                    if (asInterface.getConfigurationIntent(asInterface.getCurrentTransport()) != null && z) {
                        z2 = true;
                    }
                } catch (RemoteException unused) {
                }
            } catch (RemoteException unused2) {
                z = false;
            }
            if (!z) {
                BaseSearchUpdateHelper.disableByResource(context, arrayList, AUTO_RESTORE_RESOURCE, true);
            }
            if (!z2) {
                BaseSearchUpdateHelper.disableByResource(context, arrayList, CONFIGURE_ACCOUNT_RESOURCE, true);
            }
        }
        if (Build.IS_TABLET) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, CLOUD_RESTORE_RESOURCE);
            BaseSearchUpdateHelper.hideByResource(context, arrayList, CLOUD_BACKUP_RESOURCE);
            Iterator<String> it = BaseSearchUpdateHelper.getIdWithResource(context, LOCAL_BACKUP_RESOURCE).iterator();
            while (it.hasNext()) {
                BaseSearchUpdateHelper.updateSearchItem(arrayList, context.getPackageName() + it.next(), 4, new String[]{"intent_action", FunctionColumns.DEST_PACKAGE, FunctionColumns.DEST_CLASS, FunctionColumns.FRAGMENT}, new String[]{"", "com.android.settings", "com.android.settings.SubSettings", "com.miui.backup.ui.MainFragmentPad"});
            }
            Iterator<String> it2 = BaseSearchUpdateHelper.getIdWithResource(context, LOCAL_AUTO_BACKUP_RESOURCE).iterator();
            while (it2.hasNext()) {
                BaseSearchUpdateHelper.updateSearchItem(arrayList, context.getPackageName() + it2.next(), 4, new String[]{"intent_action", FunctionColumns.DEST_PACKAGE, FunctionColumns.DEST_CLASS, FunctionColumns.FRAGMENT}, new String[]{"", "com.android.settings", "com.android.settings.SubSettings", "com.miui.backup.auto.AutoBackupFragmentPad"});
            }
        }
    }
}
