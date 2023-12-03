package com.android.settings.development;

import android.app.backup.IBackupManager;
import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserManager;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import miui.provider.ExtraContacts;

/* loaded from: classes.dex */
public class LocalBackupPasswordPreferenceController extends DeveloperOptionsPreferenceController implements PreferenceControllerMixin {
    private final IBackupManager mBackupManager;
    private final UserManager mUserManager;

    public LocalBackupPasswordPreferenceController(Context context) {
        super(context);
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mBackupManager = IBackupManager.Stub.asInterface(ServiceManager.getService(ExtraContacts.Calls.BACKUP_PARAM));
    }

    private void updatePasswordSummary(Preference preference) {
        preference.setEnabled(isAdminUser() && this.mBackupManager != null);
        IBackupManager iBackupManager = this.mBackupManager;
        if (iBackupManager == null) {
            return;
        }
        try {
            if (iBackupManager.hasBackupPassword()) {
                preference.setSummary(R.string.local_backup_password_summary_change);
            } else {
                preference.setSummary(R.string.local_backup_password_summary_none);
            }
        } catch (RemoteException unused) {
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "local_backup_password";
    }

    boolean isAdminUser() {
        return this.mUserManager.isAdminUser();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updatePasswordSummary(preference);
    }
}
