package com.android.settings.users;

import android.content.Context;
import android.os.UserHandle;
import androidx.fragment.app.Fragment;
import com.android.settings.Utils;

/* loaded from: classes2.dex */
public class AutoSyncWorkDataPreferenceController extends AutoSyncPersonalDataPreferenceController {
    public AutoSyncWorkDataPreferenceController(Context context, Fragment fragment) {
        super(context, fragment);
        this.mUserHandle = Utils.getManagedProfileWithDisabled(this.mUserManager);
    }

    @Override // com.android.settings.users.AutoSyncPersonalDataPreferenceController, com.android.settings.users.AutoSyncDataPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "auto_sync_work_account_data";
    }

    @Override // com.android.settings.users.AutoSyncPersonalDataPreferenceController, com.android.settings.users.AutoSyncDataPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return (this.mUserHandle == null || this.mUserManager.isManagedProfile() || this.mUserManager.isLinkedUser() || this.mUserManager.getProfiles(UserHandle.myUserId()).size() <= 1) ? false : true;
    }
}
