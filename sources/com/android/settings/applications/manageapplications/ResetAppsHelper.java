package com.android.settings.applications.manageapplications;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.INotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.net.NetworkPolicyManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import com.android.settings.R;
import com.android.settings.search.FunctionColumns;
import java.util.Arrays;
import java.util.List;
import miui.app.constants.ThemeManagerConstants;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class ResetAppsHelper implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    private final AppOpsManager mAom;
    private final Context mContext;
    private final IPackageManager mIPm = IPackageManager.Stub.asInterface(ServiceManager.getService(FunctionColumns.PACKAGE));
    private final INotificationManager mNm = INotificationManager.Stub.asInterface(ServiceManager.getService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION));
    private final NetworkPolicyManager mNpm;
    private final PackageManager mPm;
    private AlertDialog mResetDialog;

    public ResetAppsHelper(Context context) {
        this.mContext = context;
        this.mPm = context.getPackageManager();
        this.mNpm = NetworkPolicyManager.from(context);
        this.mAom = (AppOpsManager) context.getSystemService("appops");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void buildResetDialog() {
        if (this.mResetDialog == null) {
            this.mResetDialog = new AlertDialog.Builder(this.mContext).setTitle(R.string.reset_app_preferences_title).setMessage(R.string.reset_app_preferences_desc).setPositiveButton(R.string.reset_app_preferences_button, this).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).setOnDismissListener(this).show();
        }
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (this.mResetDialog != dialogInterface) {
            return;
        }
        AsyncTask.execute(new Runnable() { // from class: com.android.settings.applications.manageapplications.ResetAppsHelper.1
            @Override // java.lang.Runnable
            public void run() {
                List<ApplicationInfo> installedApplications = ResetAppsHelper.this.mPm.getInstalledApplications(512);
                List asList = Arrays.asList(ResetAppsHelper.this.mContext.getResources().getStringArray(R.array.config_skip_reset_apps_package_name));
                for (int i2 = 0; i2 < installedApplications.size(); i2++) {
                    ApplicationInfo applicationInfo = installedApplications.get(i2);
                    if (!asList.contains(applicationInfo.packageName)) {
                        try {
                            ResetAppsHelper.this.mNm.clearData(applicationInfo.packageName, applicationInfo.uid, false);
                        } catch (RemoteException unused) {
                        }
                        if (!applicationInfo.enabled && ResetAppsHelper.this.mPm.getApplicationEnabledSetting(applicationInfo.packageName) == 3) {
                            ResetAppsHelper.this.mPm.setApplicationEnabledSetting(applicationInfo.packageName, 0, 1);
                        }
                    }
                }
                try {
                    ResetAppsHelper.this.mIPm.resetApplicationPreferences(UserHandle.myUserId());
                } catch (RemoteException unused2) {
                }
                ResetAppsHelper.this.mAom.resetAllModes();
                int[] uidsWithPolicy = ResetAppsHelper.this.mNpm.getUidsWithPolicy(1);
                int currentUser = ActivityManager.getCurrentUser();
                for (int i3 : uidsWithPolicy) {
                    if (UserHandle.getUserId(i3) == currentUser) {
                        ResetAppsHelper.this.mNpm.setUidPolicy(i3, 0);
                    }
                }
            }
        });
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        if (this.mResetDialog == dialogInterface) {
            this.mResetDialog = null;
        }
    }

    public void onRestoreInstanceState(Bundle bundle) {
        if (bundle == null || !bundle.getBoolean("resetDialog")) {
            return;
        }
        buildResetDialog();
    }

    public void onSaveInstanceState(Bundle bundle) {
        if (this.mResetDialog != null) {
            bundle.putBoolean("resetDialog", true);
        }
    }

    public void stop() {
        AlertDialog alertDialog = this.mResetDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mResetDialog = null;
        }
    }
}
