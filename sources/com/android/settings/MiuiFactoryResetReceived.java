package com.android.settings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.StatusBarManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RecoverySystem;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.storage.IStorageManager;
import android.provider.Settings;
import android.provider.SystemSettings$Secure;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.widget.LockPatternUtils;
import java.io.IOException;
import java.util.Locale;
import miui.accounts.ExtraAccountManager;
import miui.app.constants.ThemeManagerConstants;
import miui.cloud.common.XLogger;
import miui.cloud.finddevice.FindDeviceStatusManager;
import miui.payment.PaymentManager;
import miui.util.ReflectionUtils;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;
import miuix.appcompat.app.ProgressDialog;
import miuix.net.ConnectivityHelper;

/* loaded from: classes.dex */
public class MiuiFactoryResetReceived extends AppCompatActivity {
    private AccountManagerFuture<Bundle> mAccountManagerFuture;
    private CheckFindDeviceStatusTask mCheckFindDeviceStatusTask;
    private boolean mClosingFindDevicePasswordVerified;
    private LockPatternUtils mLockPatternUtils;
    private ShutDownFindDeviceTask mShutDownFindDeviceTask;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class AccountStartActivityCallback implements AccountManagerCallback<Bundle> {
        private int mRequestCode;

        public AccountStartActivityCallback(int i) {
            this.mRequestCode = i;
        }

        @Override // android.accounts.AccountManagerCallback
        public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
            if (MiuiFactoryResetReceived.this.mAccountManagerFuture != accountManagerFuture) {
                return;
            }
            Intent intent = null;
            MiuiFactoryResetReceived.this.mAccountManagerFuture = null;
            try {
                intent = (Intent) accountManagerFuture.getResult().get(PaymentManager.KEY_INTENT);
            } catch (AuthenticatorException e) {
                XLogger.log(e);
            } catch (OperationCanceledException e2) {
                XLogger.log(e2);
            } catch (IOException e3) {
                XLogger.log(e3);
            }
            if (intent != null) {
                MiuiFactoryResetReceived.this.startActivityForResult(intent, this.mRequestCode);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class CheckFindDeviceStatusTask extends AsyncTask<Void, Void, Boolean> {
        private Activity mActivity;
        private Context mAppContext;
        private ProgressDialog mProgressDialog;

        public CheckFindDeviceStatusTask(Context context, Activity activity) {
            this.mAppContext = context;
            this.mActivity = activity;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Void... voidArr) {
            Log.d("MiuiFactoryResetR", "CheckFindDeviceStatusTask");
            FindDeviceStatusManager obtain = FindDeviceStatusManager.obtain(this.mAppContext);
            try {
                return Boolean.valueOf(obtain.isOpen());
            } catch (RemoteException e) {
                XLogger.loge(e);
                return null;
            } catch (InterruptedException e2) {
                XLogger.loge(e2);
                return null;
            } finally {
                obtain.release();
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean bool) {
            MiuiFactoryResetReceived.this.mCheckFindDeviceStatusTask = null;
            this.mProgressDialog.dismiss();
            if (bool == null) {
                MiuiFactoryResetReceived.this.alertCheckFindDeviceStatusFailure();
            } else if (!bool.booleanValue()) {
                MiuiFactoryResetReceived.this.showFinalConfirmation();
            } else {
                Account xiaomiAccount = ExtraAccountManager.getXiaomiAccount(this.mActivity);
                if (xiaomiAccount == null) {
                    throw new IllegalStateException("Find device is open, but there is no Xiaomi account. ");
                }
                if (MiuiFactoryResetReceived.this.mAccountManagerFuture != null) {
                    MiuiFactoryResetReceived.this.mAccountManagerFuture.cancel(true);
                }
                Bundle bundle = new Bundle();
                bundle.putBoolean("verify_only", false);
                bundle.putString(ExtraAccountManager.KEY_SERVICE_ID, "micloudfind");
                MiuiFactoryResetReceived.this.mAccountManagerFuture = AccountManager.get(this.mActivity).confirmCredentials(xiaomiAccount, bundle, null, new AccountStartActivityCallback(58), null);
            }
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            this.mProgressDialog = ProgressDialog.show(this.mActivity, "", MiuiFactoryResetReceived.this.getString(R.string.checking_find_device_status));
        }
    }

    /* loaded from: classes.dex */
    public class ShutDownFindDeviceTask extends AsyncTask<Void, Void, Boolean> {
        private Activity mActivity;
        private Context mAppContext;
        private ProgressDialog mProgressDialog;

        public ShutDownFindDeviceTask(Context context, Activity activity) {
            this.mAppContext = context;
            this.mActivity = activity;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Void... voidArr) {
            FindDeviceStatusManager obtain = FindDeviceStatusManager.obtain(this.mAppContext);
            try {
                try {
                    try {
                        try {
                            obtain.close();
                            obtain.release();
                            return Boolean.TRUE;
                        } catch (InterruptedException e) {
                            XLogger.loge(e);
                            Boolean bool = Boolean.FALSE;
                            obtain.release();
                            return bool;
                        }
                    } catch (RemoteException e2) {
                        XLogger.loge(e2);
                        Boolean bool2 = Boolean.FALSE;
                        obtain.release();
                        return bool2;
                    }
                } catch (FindDeviceStatusManager.FindDeviceStatusManagerException e3) {
                    XLogger.loge(e3);
                    Boolean bool3 = Boolean.FALSE;
                    obtain.release();
                    return bool3;
                }
            } catch (Throwable th) {
                obtain.release();
                throw th;
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean bool) {
            MiuiFactoryResetReceived.this.mShutDownFindDeviceTask = null;
            this.mProgressDialog.dismiss();
            if (bool.booleanValue()) {
                MiuiFactoryResetReceived.this.showFinalConfirmation();
            } else {
                MiuiFactoryResetReceived.this.alertShutDownFindDeviceFailure();
            }
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            this.mProgressDialog = ProgressDialog.show(this.mActivity, "", MiuiFactoryResetReceived.this.getString(R.string.shuting_down_find_device));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void alertCheckFindDeviceStatusFailure() {
        int i = R.string.failed_to_check_find_device_status_title;
        new AlertDialog.Builder(this).setTitle(i).setMessage(getString(R.string.failed_to_check_find_device_status_content)).setPositiveButton(R.string.check_find_device_status_failure_confirm, (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void alertCloseLockPattern() {
        new AlertDialog.Builder(this, R.style.AlertDialog_Theme_DayNight).setTitle(R.string.failed_to_erase_title).setMessage(getString(R.string.failed_to_erase_text)).setCancelable(false).setPositiveButton(R.string.shut_down_find_device_failure_confirm, new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiFactoryResetReceived.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                MiuiFactoryResetReceived.this.finish();
            }
        }).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void alertShutDownFindDeviceFailure() {
        int i;
        String string;
        if (ConnectivityHelper.getInstance(this).isNetworkConnected()) {
            i = R.string.failed_to_shut_down_find_device_title;
            string = getString(R.string.failed_to_shut_down_find_device_content);
        } else {
            i = R.string.shut_down_find_device_network_failure_title;
            string = getString(R.string.shut_down_find_device_network_failure_content);
        }
        new AlertDialog.Builder(this).setTitle(i).setMessage(string).setPositiveButton(R.string.shut_down_find_device_failure_confirm, (DialogInterface.OnClickListener) null).show();
    }

    private void displayAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog_Theme_DayNight);
        builder.setMessage(getResources().getString(R.string.user_confirm_remove_message)).setCancelable(false).setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiFactoryResetReceived.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("MiuiFactoryResetR", "MiuiFactoryResetReceived called RecoverySystem");
                MiuiFactoryResetReceived.this.mLockPatternUtils = new LockPatternUtils(MiuiFactoryResetReceived.this.getApplicationContext());
                if (MiuiFactoryResetReceived.this.mLockPatternUtils.isSecure(0)) {
                    MiuiFactoryResetReceived.this.alertCloseLockPattern();
                } else {
                    MiuiFactoryResetReceived.this.runFindDeviceCheckAndDoMasterClean();
                }
                dialogInterface.cancel();
            }
        }).setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiFactoryResetReceived.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("MiuiFactoryResetR", "MiuiFactoryResetReceived canceled.");
                dialogInterface.cancel();
                MiuiFactoryResetReceived.this.finish();
            }
        });
        builder.create().show();
    }

    private void doMasterClear() {
        if (TextUtils.equals(SystemProperties.get("ro.crypto.type", ""), "block")) {
            try {
                IStorageManager.Stub.asInterface(ServiceManager.getService("mount")).setField("SystemLocale", "");
            } catch (Exception e) {
                Log.e("MiuiFactoryResetR", "Error storing locale for 1217", e);
            }
        }
        ReflectionUtils.tryCallStaticMethod(RecoverySystem.class, "bootCommand", Void.class, new Object[]{getApplicationContext(), new String[]{"--factory_test_reset\n--locale=" + Locale.getDefault().toString()}});
    }

    private void enableStatusBar(boolean z) {
        ((StatusBarManager) getSystemService(ThemeManagerConstants.COMPONENT_CODE_STATUSBAR)).disable(!z ? 23134208 : 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runFindDeviceCheckAndDoMasterClean() {
        CheckFindDeviceStatusTask checkFindDeviceStatusTask = this.mCheckFindDeviceStatusTask;
        if (checkFindDeviceStatusTask != null) {
            checkFindDeviceStatusTask.cancel(true);
        }
        CheckFindDeviceStatusTask checkFindDeviceStatusTask2 = new CheckFindDeviceStatusTask(getApplicationContext(), this);
        this.mCheckFindDeviceStatusTask = checkFindDeviceStatusTask2;
        checkFindDeviceStatusTask2.execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showFinalConfirmation() {
        Log.d("MiuiFactoryResetR", "showFinalConfirmation");
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.MiuiMasterClearApplyActivity");
        intent.putExtra("format_internal_storage", true);
        intent.putExtra("1217", true);
        startActivityForResult(intent, 57);
        overridePendingTransition(0, 0);
    }

    private void shutFindDeviceDownAndShowFinalConfirm() {
        ShutDownFindDeviceTask shutDownFindDeviceTask = this.mShutDownFindDeviceTask;
        if (shutDownFindDeviceTask != null) {
            shutDownFindDeviceTask.cancel(true);
        }
        ShutDownFindDeviceTask shutDownFindDeviceTask2 = new ShutDownFindDeviceTask(getApplicationContext(), this);
        this.mShutDownFindDeviceTask = shutDownFindDeviceTask2;
        shutDownFindDeviceTask2.execute(new Void[0]);
    }

    private void toggleScreenButtonState(boolean z) {
        Settings.Secure.putInt(getContentResolver(), SystemSettings$Secure.SCREEN_BUTTONS_STATE, z ? 1 : 0);
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        Log.d("MiuiFactoryResetR", "onActivityResult:" + i);
        if (Utils.isMonkeyRunning()) {
            return;
        }
        if (i == 58 && i2 == -1) {
            this.mClosingFindDevicePasswordVerified = true;
            return;
        }
        if (i == 57 && i2 == -1) {
            toggleScreenButtonState(true);
            enableStatusBar(false);
            doMasterClear();
        }
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d("MiuiFactoryResetR", "onCreate");
        displayAlert();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        Log.d("MiuiFactoryResetR", "onResume");
        if (this.mClosingFindDevicePasswordVerified) {
            this.mClosingFindDevicePasswordVerified = false;
            shutFindDeviceDownAndShowFinalConfirm();
        }
    }
}
