package com.android.settings.backup;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.AbstractPreferenceController;
import com.miui.enterprise.RestrictionsHelper;
import miui.accounts.ExtraAccountManager;
import miui.cloud.CloudPushConstants;
import miui.os.Build;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiCloudBackupController extends BasePreferenceController implements Preference.OnPreferenceClickListener {
    private static final String CLOUD_BACKUP_CATEGORY = "cloud_backup_category";
    private static final String CLOUD_BACKUP_SETTINGS = "cloud_backup_settings";
    private static final String CLOUD_RESTORE = "cloud_restore";
    private static final String TAG = "MiuiCloudBackupController";

    public MiuiCloudBackupController(Context context, String str) {
        super(context, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loginAccount(final Intent intent) {
        AccountManager.get(this.mContext).addAccount("com.xiaomi", CloudPushConstants.AUTH_TOKEN_TYPE, null, null, (Activity) this.mContext, new AccountManagerCallback<Bundle>() { // from class: com.android.settings.backup.MiuiCloudBackupController.2
            @Override // android.accounts.AccountManagerCallback
            public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                try {
                    Bundle result = accountManagerFuture.getResult();
                    if (result == null || !result.getBoolean("booleanResult", false)) {
                        Log.d(MiuiCloudBackupController.TAG, "login account failed, finish");
                    } else {
                        ((AbstractPreferenceController) MiuiCloudBackupController.this).mContext.startActivity(intent);
                    }
                } catch (Exception e) {
                    Log.e("PrivacySettings", "Exception when add account", e);
                }
            }
        }, null);
    }

    private void showLoginAccountWarn(final Intent intent) {
        new AlertDialog.Builder(this.mContext).setTitle(this.mContext.getString(R.string.login_account_dialog_title)).setMessage(this.mContext.getString(R.string.login_account_dialog_message)).setIconAttribute(16843605).setCancelable(true).setPositiveButton(R.string.login_action, new DialogInterface.OnClickListener() { // from class: com.android.settings.backup.MiuiCloudBackupController.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                MiuiCloudBackupController.this.loginAccount(intent);
            }
        }).setNegativeButton(R.string.cancel_action, (DialogInterface.OnClickListener) null).create().show();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Log.i(TAG, "displayPreference");
        Preference findPreference = preferenceScreen.findPreference(CLOUD_RESTORE);
        Preference findPreference2 = preferenceScreen.findPreference(CLOUD_BACKUP_SETTINGS);
        findPreference.setOnPreferenceClickListener(this);
        findPreference2.setOnPreferenceClickListener(this);
        if (Build.IS_TABLET) {
            setVisible(preferenceScreen, CLOUD_BACKUP_CATEGORY, false);
            preferenceScreen.removePreference(findPreference);
            preferenceScreen.removePreference(findPreference2);
        }
        if (RestrictionsHelper.hasRestriction(this.mContext, "disallow_backup")) {
            Log.d("Enterprise", "Backup is restricted");
            setVisible(preferenceScreen, CLOUD_BACKUP_CATEGORY, false);
            setVisible(preferenceScreen, CLOUD_RESTORE, false);
            setVisible(preferenceScreen, CLOUD_BACKUP_SETTINGS, false);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (ExtraAccountManager.getXiaomiAccount(this.mContext) == null) {
            showLoginAccountWarn(preference.getIntent());
            return true;
        }
        return false;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
