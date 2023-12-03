package com.android.settings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.StatusBarManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimatedRotateDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.provider.SystemSettings$Secure;
import android.service.persistentdata.PersistentDataBlockManager;
import android.telephony.euicc.EuiccManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import com.android.settings.Settings;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import com.miui.enterprise.RestrictionsHelper;
import com.miui.tsmclient.service.ICallback;
import com.miui.tsmclient.service.IMiTsmCleanSeService;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import miui.accounts.ExtraAccountManager;
import miui.app.constants.ThemeManagerConstants;
import miui.cloud.common.XLogger;
import miui.cloud.finddevice.FindDeviceStatusManager;
import miui.content.res.ThemeResources;
import miui.os.Build;
import miui.os.MiuiInit;
import miui.payment.PaymentManager;
import miui.provider.Weather;
import miui.util.FeatureParser;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.ProgressDialog;
import miuix.net.ConnectivityHelper;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class MiuiMasterClear extends SettingsPreferenceFragment implements FragmentResultCallBack {
    private AccountManagerFuture<Bundle> mAccountManagerFuture;
    private CheckFindDeviceStatusTask mCheckFindDeviceStatusTask;
    private boolean mClosingFindDevicePasswordVerified;
    private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
    private CheckBoxPreference mEraseApplication;
    private CheckBoxPreference mEraseExternalStorage;
    private Dialog mFactoryResetDialog;
    private boolean mHasBaseRestriction;
    private boolean mIsClearAll;
    private PreferenceGroup mMasterClearPreferenceGroup;
    private String mPassWord;
    private boolean mPinConfirmed;
    private CheckBoxPreference mSdCardCheckPreference;
    private Preference mSdCardPreference;
    private ShutDownFindDeviceTask mShutDownFindDeviceTask;
    protected int mUserId;
    private IMiTsmCleanSeService miTsmCleanSeService;
    private boolean needCleanWallet = false;
    private boolean needShowBackupDialg = false;
    private boolean mEraseEsim = false;
    private int mESimInitialState = -1;
    private ServiceConnection remoteConnection = new ServiceConnection() { // from class: com.android.settings.MiuiMasterClear.8
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MiuiMasterClear.this.miTsmCleanSeService = IMiTsmCleanSeService.Stub.asInterface(iBinder);
            try {
                MiuiMasterClear.this.miTsmCleanSeService.querySeCard(MiuiMasterClear.this.walletSkipCallback);
            } catch (RemoteException e) {
                Log.d("MiuiMasterClear", "query se card error " + e.getMessage());
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            MiuiMasterClear.this.miTsmCleanSeService = null;
        }
    };
    private ICallback walletSkipCallback = new ICallback.Stub() { // from class: com.android.settings.MiuiMasterClear.9
        @Override // com.miui.tsmclient.service.ICallback
        public void onError(int i, String str) {
            Log.e("MiuiMasterClear", "error code : " + i + ", message : " + str);
        }

        @Override // com.miui.tsmclient.service.ICallback
        public void onSuccess(Bundle bundle) {
            if (bundle == null || !bundle.containsKey("key_data")) {
                return;
            }
            try {
                if (((Integer) new JSONObject(bundle.getString("key_data")).get("key_card_quantity")).intValue() > 0) {
                    MiuiMasterClear.this.needCleanWallet = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private ICallback walletCleanupCallback = new ICallback.Stub() { // from class: com.android.settings.MiuiMasterClear.10
        @Override // com.miui.tsmclient.service.ICallback
        public void onError(int i, String str) {
            MiuiMasterClear.this.needCleanWallet = true;
            Log.e("MiuiMasterClear", "error code : " + i + ", message : " + str);
        }

        @Override // com.miui.tsmclient.service.ICallback
        public void onSuccess(Bundle bundle) {
            MiuiMasterClear.this.needCleanWallet = false;
            MiuiMasterClear.this.needShowBackupDialg = true;
            Log.e("MiuiMasterClear", "clean wallet card data success");
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class AccountStartActivityCallback implements AccountManagerCallback<Bundle> {
        private int mRequestCode;

        public AccountStartActivityCallback(int i) {
            this.mRequestCode = i;
        }

        @Override // android.accounts.AccountManagerCallback
        public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
            if (MiuiMasterClear.this.mAccountManagerFuture != accountManagerFuture) {
                return;
            }
            Intent intent = null;
            MiuiMasterClear.this.mAccountManagerFuture = null;
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
                MiuiMasterClear.this.startActivityForResult(intent, this.mRequestCode);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class CheckFindDeviceStatusTask extends AsyncTask<Void, Void, Boolean> {
        private Context mAppContext;
        private ProgressDialog mProgressDialog;

        public CheckFindDeviceStatusTask(Context context) {
            this.mAppContext = context;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Void... voidArr) {
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
            MiuiMasterClear.this.mCheckFindDeviceStatusTask = null;
            this.mProgressDialog.dismiss();
            if (MiuiMasterClear.this.getActivity() == null || MiuiMasterClear.this.isDetached()) {
                return;
            }
            if (bool == null) {
                MiuiMasterClear.this.alertCheckFindDeviceStatusFailure();
            } else if (!bool.booleanValue()) {
                MiuiMasterClear.this.showFinalConfirmation();
            } else {
                FragmentActivity activity = MiuiMasterClear.this.getActivity();
                Account xiaomiAccount = ExtraAccountManager.getXiaomiAccount(activity);
                if (xiaomiAccount == null) {
                    throw new IllegalStateException("Find device is open, but there is no Xiaomi account. ");
                }
                if (MiuiMasterClear.this.mAccountManagerFuture != null) {
                    MiuiMasterClear.this.mAccountManagerFuture.cancel(true);
                }
                Bundle bundle = new Bundle();
                bundle.putBoolean("verify_only", false);
                bundle.putString(ExtraAccountManager.KEY_SERVICE_ID, "micloudfind");
                MiuiMasterClear.this.mAccountManagerFuture = AccountManager.get(activity).confirmCredentials(xiaomiAccount, bundle, null, new AccountStartActivityCallback(58), null);
            }
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            this.mProgressDialog = ProgressDialog.show(MiuiMasterClear.this.getActivity(), "", MiuiMasterClear.this.getString(R.string.checking_find_device_status));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ShutDownFindDeviceTask extends AsyncTask<Void, Void, Boolean> {
        private Context mAppContext;
        private ProgressDialog mProgressDialog;

        public ShutDownFindDeviceTask(Context context) {
            this.mAppContext = context;
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
            MiuiMasterClear.this.mShutDownFindDeviceTask = null;
            this.mProgressDialog.dismiss();
            if (MiuiMasterClear.this.getActivity() == null || MiuiMasterClear.this.isDetached()) {
                return;
            }
            if (bool.booleanValue()) {
                MiuiMasterClear.this.showFinalConfirmation();
            } else {
                MiuiMasterClear.this.alertShutDownFindDeviceFailure();
            }
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            this.mProgressDialog = ProgressDialog.show(MiuiMasterClear.this.getActivity(), "", MiuiMasterClear.this.getString(R.string.shuting_down_find_device));
        }
    }

    /* loaded from: classes.dex */
    public class UninstallTask extends AsyncTask<Void, Void, Void> {

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class DeleteObserver extends IPackageDeleteObserver.Stub {
            private Waitor mWaitor;

            public DeleteObserver(Waitor waitor) {
                this.mWaitor = waitor;
            }

            public void packageDeleted(String str, int i) {
                this.mWaitor.finish();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public class Waitor {
            private int mWaitInterval;

            Waitor(int i) {
                this.mWaitInterval = i;
            }

            public synchronized void finish() {
                notify();
            }

            public synchronized void waitInterval() {
                try {
                    wait(this.mWaitInterval);
                } catch (Exception unused) {
                }
            }
        }

        public UninstallTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            Waitor waitor = new Waitor(5000);
            IPackageDeleteObserver deleteObserver = new DeleteObserver(waitor);
            PackageManager packageManager = MiuiMasterClear.this.getPackageManager();
            List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(0);
            if (installedApplications != null) {
                MiuiSettings.System.putBoolean(MiuiMasterClear.this.getActivity().getContentResolver(), "package_delete_by_restore_phone", true);
                for (ApplicationInfo applicationInfo : installedApplications) {
                    if ((applicationInfo.flags & 1) == 0) {
                        packageManager.deletePackage(applicationInfo.packageName, deleteObserver, 0);
                        waitor.waitInterval();
                    }
                }
                return null;
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r1) {
            if (MiuiMasterClear.this.mFactoryResetDialog != null) {
                MiuiMasterClear.this.mFactoryResetDialog.dismiss();
            }
            MiuiMasterClear.this.doFactoryReset();
        }
    }

    /* loaded from: classes.dex */
    public interface WipeCallback {
        void onWipeFinished();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void alertCheckFindDeviceStatusFailure() {
        int i = R.string.failed_to_check_find_device_status_title;
        new AlertDialog.Builder(getActivity()).setTitle(i).setMessage(getString(R.string.failed_to_check_find_device_status_content)).setPositiveButton(R.string.check_find_device_status_failure_confirm, (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void alertShutDownFindDeviceFailure() {
        int i;
        String string;
        if (ConnectivityHelper.getInstance(getContext()).isNetworkConnected()) {
            i = R.string.failed_to_shut_down_find_device_title;
            string = getString(R.string.failed_to_shut_down_find_device_content);
        } else {
            i = R.string.shut_down_find_device_network_failure_title;
            string = getString(R.string.shut_down_find_device_network_failure_content);
        }
        new AlertDialog.Builder(getActivity()).setTitle(i).setMessage(string).setPositiveButton(R.string.shut_down_find_device_failure_confirm, (DialogInterface.OnClickListener) null).show();
    }

    private void cleanWalletData() {
        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.wallet_reset_title).setMessage(R.string.wallet_reset).setPositiveButton(R.string.wallet_reset_ok_btn, new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiMasterClear.7
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        MiuiMasterClear.this.miTsmCleanSeService.cleanSeCard(MiuiMasterClear.this.walletCleanupCallback);
                    } catch (Exception e) {
                        Log.e("MiuiMasterClear", e.getMessage());
                    }
                }
            }).setNegativeButton(R.string.wallet_reset_cancel_btn, new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiMasterClear.6
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        MiuiMasterClear.this.miTsmCleanSeService.keepSeCard(MiuiMasterClear.this.walletSkipCallback);
                    } catch (Exception e) {
                        Log.e("MiuiMasterClear", e.getMessage());
                    }
                    MiuiMasterClear.this.showConfirmDialog();
                }
            });
            builder.create().show();
        }
    }

    private void createFactoryResetDialog() {
        Dialog dialog = new Dialog(getActivity(), 16973933);
        this.mFactoryResetDialog = dialog;
        View inflate = LayoutInflater.from(dialog.getContext()).inflate(285999145, (ViewGroup) null);
        this.mFactoryResetDialog.setContentView(inflate);
        this.mFactoryResetDialog.setCancelable(false);
        WindowManager.LayoutParams attributes = this.mFactoryResetDialog.getWindow().getAttributes();
        attributes.screenOrientation = 1;
        this.mFactoryResetDialog.getWindow().setAttributes(attributes);
        this.mFactoryResetDialog.getWindow().setType(2021);
        this.mFactoryResetDialog.getWindow().addFlags(128);
        this.mFactoryResetDialog.show();
        ImageView imageView = (ImageView) inflate.findViewById(285868194);
        imageView.setVisibility(0);
        AnimatedRotateDrawable drawable = imageView.getDrawable();
        drawable.setFramesCount(getActivity().getResources().getInteger(285933649));
        drawable.setFramesDuration(getActivity().getResources().getInteger(285933650));
        drawable.start();
        new UninstallTask().execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doFactoryReset() {
        if (getActivity() == null) {
            return;
        }
        CheckBoxPreference checkBoxPreference = this.mEraseApplication;
        boolean z = false;
        if (checkBoxPreference != null && !checkBoxPreference.isChecked()) {
            z = true;
        }
        MiuiInit.doFactoryReset(z);
        Log.d("MasterClearRec", "doFactoryReset hex password:" + this.mPassWord);
        if (Build.IS_INTERNATIONAL_BUILD) {
            wipeFrpBlockDataAndDoMasterClear();
        } else {
            doMasterClear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doMasterClear() {
        CheckBoxPreference checkBoxPreference = this.mEraseExternalStorage;
        if (checkBoxPreference == null || !checkBoxPreference.isChecked()) {
            if (!isNeedPassWord()) {
                CheckBoxPreference checkBoxPreference2 = this.mSdCardCheckPreference;
                if (checkBoxPreference2 == null || !checkBoxPreference2.isChecked()) {
                    startFactoryReset();
                } else {
                    formatSdCardAndFactoryReset();
                }
            } else if (FeatureParser.getBoolean("support_erase_external_storage", false)) {
                Intent intent = new Intent("android.intent.action.FACTORY_RESET");
                intent.setPackage(ThemeResources.FRAMEWORK_PACKAGE);
                intent.putExtra("format_sdcard", true);
                intent.putExtra("com.android.internal.intent.extra.WIPE_ESIMS", this.mEraseEsim);
                intent.addFlags(268435456);
                if (!TextUtils.isEmpty(this.mPassWord)) {
                    intent.putExtra("password", this.mPassWord);
                    this.mPassWord = null;
                }
                getActivity().sendBroadcast(intent);
            }
        } else if (FeatureParser.getBoolean("support_erase_external_storage", false)) {
            Intent intent2 = new Intent("android.intent.action.FACTORY_RESET");
            intent2.setPackage(ThemeResources.FRAMEWORK_PACKAGE);
            intent2.putExtra("format_sdcard", true);
            intent2.putExtra("com.android.internal.intent.extra.WIPE_ESIMS", this.mEraseEsim);
            intent2.addFlags(268435456);
            getActivity().sendBroadcast(intent2);
        }
        Log.i("MiuiMasterClear", "doMasterClear:mEraseEsim " + this.mEraseEsim);
    }

    private void enableStatusBar(boolean z) {
        ((StatusBarManager) getSystemService(ThemeManagerConstants.COMPONENT_CODE_STATUSBAR)).disable(!z ? 23134208 : 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ProgressDialog getProgressDialog(Activity activity) {
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(activity.getString(R.string.master_clear_progress_title));
        progressDialog.setMessage(activity.getString(R.string.master_clear_progress_text));
        return progressDialog;
    }

    private boolean isBatteryLow() {
        return getActivity().registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED")).getIntExtra(Weather.AlertInfo.LEVEL, 0) <= 10;
    }

    private boolean isEuiccEnabled(Context context) {
        return ((EuiccManager) context.getSystemService("euicc")).isEnabled();
    }

    public static boolean isExtStorageEncrypted() {
        return "encrypted".equals(SystemProperties.get("ro.crypto.state"));
    }

    private boolean isNeedPassWord() {
        return "leo".equals(SystemProperties.get("ro.product.device", "UNKNOWN")) && isExtStorageEncrypted();
    }

    public static boolean isRemoveEraseExternalStorage() {
        return (Environment.isExternalStorageEmulated() && !FeatureParser.getBoolean("support_erase_external_storage", false)) || (!Environment.isExternalStorageRemovable() && isExtStorageEncrypted());
    }

    private void removePreferenceFromCategory(String str, String str2) {
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference(str);
        Preference findPreference = preferenceCategory.findPreference(str2);
        if (findPreference != null) {
            preferenceCategory.removePreference(findPreference);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runFindDeviceCheckAndDoMasterClean() {
        CheckFindDeviceStatusTask checkFindDeviceStatusTask = this.mCheckFindDeviceStatusTask;
        if (checkFindDeviceStatusTask != null) {
            checkFindDeviceStatusTask.cancel(true);
        }
        CheckFindDeviceStatusTask checkFindDeviceStatusTask2 = new CheckFindDeviceStatusTask(getActivity().getApplicationContext());
        this.mCheckFindDeviceStatusTask = checkFindDeviceStatusTask2;
        checkFindDeviceStatusTask2.execute(new Void[0]);
    }

    private boolean runKeyguardConfirmation(int i) {
        return new ChooseLockSettingsHelper.Builder(getActivity(), this).setRequestCode(i).setTitle(getActivity().getResources().getText(R.string.master_clear_title)).show();
    }

    private boolean runRestrictionsChallenge() {
        if (this.mHasBaseRestriction || this.mEnforcedAdmin == null) {
            return false;
        }
        RestrictedLockUtils.sendShowAdminSupportDetailsIntent(getActivity(), this.mEnforcedAdmin);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setESimStateIfNeed(int i) {
        Log.d("MiuiMasterClear", "setESimStateIfNeed pass state = " + i);
        if (SettingsFeatures.isNeedESIMFeature() && showWipeEuicc()) {
            try {
                Class<?> cls = Class.forName("miui.telephony.TelephonyManagerEx");
                int intValue = ((Integer) cls.getMethod("getEsimGPIOState", new Class[0]).invoke(cls.getMethod("getDefault", new Class[0]).invoke(null, new Object[0]), new Object[0])).intValue();
                Log.d("MiuiMasterClear", "setESimStateIfNeed --> current status " + intValue);
                if (this.mESimInitialState == -1) {
                    this.mESimInitialState = intValue;
                }
                if (intValue != i) {
                    try {
                        Class<?> cls2 = Class.forName("miui.telephony.TelephonyManagerEx");
                        Log.d("MiuiMasterClear", "setEsimState, ret = " + ((Integer) cls2.getMethod("setEsimState", Integer.TYPE).invoke(cls2.getMethod("getDefault", new Class[0]).invoke(null, new Object[0]), Integer.valueOf(i))).intValue());
                    } catch (Exception e) {
                        Log.e("MiuiMasterClear", "setEsimState: ", e);
                    }
                }
            } catch (Exception e2) {
                Log.e("MiuiMasterClear", "getEsimGPIOState: ", e2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_backup_title).setMessage(R.string.clear_data_alert_info).setPositiveButton(R.string.backup_at_once, new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiMasterClear.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                MiuiMasterClear.this.startActivity(new Intent(MiuiMasterClear.this.getActivity(), Settings.PrivacySettingsActivity.class));
            }
        }).setNegativeButton(R.string.continue_reset_factory, new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiMasterClear.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if ((SettingsFeatures.isNeedESIMCustmized() || SettingsFeatures.isNeedESIMFeature()) && MiuiMasterClear.this.showWipeEuicc()) {
                    MiuiMasterClear.this.showResetESimConfirmDialog();
                } else {
                    MiuiMasterClear.this.runFindDeviceCheckAndDoMasterClean();
                }
            }
        });
        builder.create().show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showFinalConfirmation() {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.MiuiMasterClearApplyActivity");
        CheckBoxPreference checkBoxPreference = this.mEraseExternalStorage;
        intent.putExtra("format_internal_storage", checkBoxPreference == null || checkBoxPreference.isChecked());
        startActivityForResult(intent, 57);
        getActivity().overridePendingTransition(0, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showResetESimConfirmDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_softbank_esim_wipe_title).setMessage(R.string.dialog_softbank_esim_wipe_info).setPositiveButton(R.string.dialog_softbank_esim_wipe_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiMasterClear.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                MiuiMasterClear.this.mEraseEsim = true;
                MiuiMasterClear.this.setESimStateIfNeed(0);
                MiuiMasterClear.this.runFindDeviceCheckAndDoMasterClean();
            }
        }).setNegativeButton(R.string.dialog_softbank_esim_wipe_cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiMasterClear.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                MiuiMasterClear.this.mEraseEsim = false;
                builder.create().dismiss();
                MiuiMasterClear.this.runFindDeviceCheckAndDoMasterClean();
            }
        });
        builder.create().show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean showWipeEuicc() {
        Context context = getContext();
        if (isEuiccEnabled(context)) {
            if (SettingsFeatures.isNeedESIMCustmized()) {
                return Settings.Global.getInt(context.getContentResolver(), "euicc_provisioned", 0) != 0 || DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(context);
            }
            return true;
        }
        return false;
    }

    private void shutFindDeviceDownAndShowFinalConfirm() {
        ShutDownFindDeviceTask shutDownFindDeviceTask = this.mShutDownFindDeviceTask;
        if (shutDownFindDeviceTask != null) {
            shutDownFindDeviceTask.cancel(true);
        }
        ShutDownFindDeviceTask shutDownFindDeviceTask2 = new ShutDownFindDeviceTask(getActivity().getApplicationContext());
        this.mShutDownFindDeviceTask = shutDownFindDeviceTask2;
        shutDownFindDeviceTask2.execute(new Void[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startFactoryReset() {
        Intent intent = new Intent("android.intent.action.FACTORY_RESET");
        intent.setPackage(ThemeResources.FRAMEWORK_PACKAGE);
        intent.addFlags(268435456);
        intent.putExtra("com.android.internal.intent.extra.WIPE_ESIMS", this.mEraseEsim);
        getActivity().sendBroadcast(intent);
    }

    private void toggleScreenButtonState(boolean z) {
        Settings.Secure.putInt(getContentResolver(), SystemSettings$Secure.SCREEN_BUTTONS_STATE, z ? 1 : 0);
    }

    private void wipeFrpBlockDataAndDoMasterClear() {
        wipeFrpBlockDataAndDoMasterClear(getActivity(), new WipeCallback() { // from class: com.android.settings.MiuiMasterClear.11
            @Override // com.android.settings.MiuiMasterClear.WipeCallback
            public void onWipeFinished() {
                MiuiMasterClear.this.doMasterClear();
            }
        });
    }

    public void formatSdCardAndFactoryReset() {
        VolumeInfo volumeInfo;
        final StorageManager storageManager = (StorageManager) getActivity().getSystemService(StorageManager.class);
        Iterator it = storageManager.getVolumes().iterator();
        while (true) {
            if (!it.hasNext()) {
                volumeInfo = null;
                break;
            }
            volumeInfo = (VolumeInfo) it.next();
            if (volumeInfo.getType() == 0 && (volumeInfo.getDisk().flags & 4) == 4) {
                break;
            }
        }
        if (volumeInfo == null) {
            startFactoryReset();
        } else if (volumeInfo.getState() != 2) {
            startFactoryReset();
        } else {
            final String id = volumeInfo.getId();
            new Thread(new Runnable() { // from class: com.android.settings.MiuiMasterClear.1
                @Override // java.lang.Runnable
                public void run() {
                    storageManager.format(id);
                    MiuiMasterClear.this.startFactoryReset();
                }
            }).start();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiMasterClear.class.getName();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 58) {
            if (i2 == -1) {
                this.mClosingFindDevicePasswordVerified = true;
            }
        }
        if (i == 56) {
            if (i2 == -1) {
                this.mPinConfirmed = true;
            }
        } else if (i != 57) {
            if (i == 55 && i2 == -1) {
                if (isNeedPassWord() && intent != null) {
                    String stringExtra = intent.getStringExtra("password");
                    if (!TextUtils.isEmpty(stringExtra)) {
                        try {
                            this.mPassWord = AESUtil.encrypt(stringExtra, AESUtil.getDefaultAESKeyPlaintext());
                        } catch (Exception e) {
                            Log.e("MiuiMasterClear", e.getMessage());
                        }
                    }
                }
                if (this.needCleanWallet) {
                    cleanWalletData();
                } else {
                    showConfirmDialog();
                }
            }
        } else if (i2 != -1) {
            if (this.mESimInitialState == 1) {
                setESimStateIfNeed(1);
            }
        } else if (Utils.isMonkeyRunning()) {
        } else {
            toggleScreenButtonState(true);
            enableStatusBar(false);
            CheckBoxPreference checkBoxPreference = this.mEraseApplication;
            if (checkBoxPreference == null || !checkBoxPreference.isChecked()) {
                doFactoryReset();
            } else {
                createFactoryResetDialog();
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!SettingsFeatures.isSplitTablet(getContext())) {
            getActivity().setRequestedOrientation(7);
        }
        setHasOptionsMenu(true);
        addPreferencesFromResource(R.xml.master_clear);
        this.mUserId = UserHandle.myUserId();
        this.mEraseApplication = (CheckBoxPreference) findPreference("erase_application");
        this.mEraseExternalStorage = (CheckBoxPreference) findPreference("erase_external_storage");
        this.mSdCardCheckPreference = (CheckBoxPreference) findPreference("remove_sd_data_check");
        this.mMasterClearPreferenceGroup = (PreferenceGroup) findPreference("erase_data");
        this.mSdCardPreference = findPreference("remove_sd_data");
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("erase_optional");
        if (!FeatureParser.getBoolean("support_erase_application", false)) {
            preferenceCategory.removePreference(this.mEraseApplication);
            this.mEraseApplication = null;
        }
        SystemProperties.get("ro.boot.sdcard.type");
        boolean isExternalStorageEmulated = Environment.isExternalStorageEmulated();
        if (isRemoveEraseExternalStorage()) {
            preferenceCategory.removePreference(this.mEraseExternalStorage);
            this.mEraseExternalStorage = null;
        } else if (isExternalStorageEmulated) {
            this.mEraseExternalStorage.setTitle(R.string.erase_internal_storage);
            this.mEraseExternalStorage.setSummary(R.string.erase_internal_storage_description);
            removePreferenceFromCategory("erase_data", "erase_external_no_emulate_sd");
        } else {
            removePreferenceFromCategory("erase_data", "erase_external_no_emulate_sd");
        }
        if (this.mEraseExternalStorage == null && this.mEraseApplication == null) {
            getPreferenceScreen().removePreference(preferenceCategory);
        }
        if (RegionUtils.IS_JP_KDDI && !MiuiUtils.hasSDCard(getActivity())) {
            removePreferenceFromCategory("erase_data", "remove_sd_data");
            removePreferenceFromCategory("erase_data", "remove_sd_data_check");
        } else if (RegionUtils.IS_JP_SB) {
            this.mMasterClearPreferenceGroup.removePreference(this.mSdCardPreference);
        } else {
            this.mMasterClearPreferenceGroup.removePreference(this.mSdCardCheckPreference);
        }
        boolean booleanExtra = getActivity().getIntent().getBooleanExtra("clear_all", false);
        this.mIsClearAll = booleanExtra;
        if (booleanExtra) {
            CheckBoxPreference checkBoxPreference = this.mEraseExternalStorage;
            if (checkBoxPreference != null) {
                checkBoxPreference.setChecked(true);
            }
            if (this.mEraseApplication != null) {
                createFactoryResetDialog();
            } else {
                doFactoryReset();
            }
        }
        Intent intent = new Intent("com.miui.tsmclient.action.CLEAN_SE_SERVICE");
        intent.setPackage("com.miui.tsmclient");
        if (getActivity() != null) {
            getActivity().bindService(intent, this.remoteConnection, 1);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuItem add = menu.add(0, 1, 0, R.string.clear_all_data);
        add.setIcon(R.drawable.action_button_clear);
        add.setShowAsAction(5);
        boolean z = MiuiSettings.System.getBoolean(getActivity().getContentResolver(), "enable_demo_mode", false);
        if (!RestrictionsHelper.hasRestriction(getActivity(), "disallow_factoryreset")) {
            add.setEnabled(!z);
            return;
        }
        Log.d("Enterprise", "MasterClear is restricted");
        add.setEnabled(false);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.remove_all_data_lyt, viewGroup, false);
        ((ViewGroup) inflate.findViewById(R.id.prefs_container)).addView(super.onCreateView(layoutInflater, viewGroup, bundle));
        return inflate;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        Dialog dialog = this.mFactoryResetDialog;
        if (dialog != null) {
            dialog.dismiss();
        }
        CheckFindDeviceStatusTask checkFindDeviceStatusTask = this.mCheckFindDeviceStatusTask;
        if (checkFindDeviceStatusTask != null) {
            checkFindDeviceStatusTask.cancel(true);
            this.mCheckFindDeviceStatusTask = null;
        }
        ShutDownFindDeviceTask shutDownFindDeviceTask = this.mShutDownFindDeviceTask;
        if (shutDownFindDeviceTask != null) {
            shutDownFindDeviceTask.cancel(true);
            this.mShutDownFindDeviceTask = null;
        }
        AccountManagerFuture<Bundle> accountManagerFuture = this.mAccountManagerFuture;
        if (accountManagerFuture != null) {
            accountManagerFuture.cancel(true);
            this.mAccountManagerFuture = null;
        }
        if (getActivity() != null) {
            getActivity().unbindService(this.remoteConnection);
        }
        super.onDestroy();
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public void onFragmentResult(int i, Bundle bundle) {
        int i2 = bundle.getInt("miui_security_fragment_result");
        if (i == 58) {
            if (i2 == 0) {
                this.mClosingFindDevicePasswordVerified = true;
            }
        }
        if (i == 56) {
            if (i2 == 0) {
                this.mPinConfirmed = true;
            }
        } else if (i != 57) {
            if (i == 55 && i2 == 0) {
                if (isNeedPassWord()) {
                    String string = bundle.getString("password");
                    if (!TextUtils.isEmpty(string)) {
                        try {
                            this.mPassWord = AESUtil.encrypt(string, AESUtil.getDefaultAESKeyPlaintext());
                        } catch (Exception e) {
                            Log.e("MiuiMasterClear", e.getMessage());
                        }
                    }
                }
                runFindDeviceCheckAndDoMasterClean();
            }
        } else if (i2 != 0) {
            if (this.mESimInitialState == 1) {
                setESimStateIfNeed(1);
            }
        } else if (Utils.isMonkeyRunning()) {
        } else {
            toggleScreenButtonState(true);
            enableStatusBar(false);
            CheckBoxPreference checkBoxPreference = this.mEraseApplication;
            if (checkBoxPreference == null || !checkBoxPreference.isChecked()) {
                doFactoryReset();
            } else {
                createFactoryResetDialog();
            }
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 1) {
            this.mPinConfirmed = false;
            if (runRestrictionsChallenge()) {
                return true;
            }
            if (RegionUtils.IS_JP_KDDI && isBatteryLow()) {
                new AlertDialog.Builder(getActivity()).setMessage(String.format(getActivity().getResources().getString(R.string.miui_master_clear_prompt_battery_low), NumberFormat.getPercentInstance().format(0.10000000149011612d))).setCancelable(false).setPositiveButton(R.string.button_text_ok, (DialogInterface.OnClickListener) null).show();
                return true;
            } else if (runKeyguardConfirmation(55) || ActivityManager.isUserAMonkey()) {
                return true;
            } else {
                MiStatInterfaceUtils.trackMasterClearClick(getName(), "factoryReset");
                OneTrackInterfaceUtils.trackMasterClearClick(getName(), "factoryReset");
                if (getActivity() == null) {
                    return super.onOptionsItemSelected(menuItem);
                }
                if (MiuiUtils.isDeviceManaged(getActivity().getApplicationContext())) {
                    runFindDeviceCheckAndDoMasterClean();
                } else if (this.needCleanWallet) {
                    cleanWalletData();
                } else {
                    showConfirmDialog();
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        toggleScreenButtonState(false);
        enableStatusBar(true);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mEnforcedAdmin = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(getActivity(), "no_factory_reset", this.mUserId);
        this.mHasBaseRestriction = RestrictedLockUtilsInternal.hasBaseUserRestriction(getActivity(), "no_factory_reset", this.mUserId);
        if (this.mPinConfirmed) {
            this.mPinConfirmed = false;
            if (!runKeyguardConfirmation(55)) {
                runFindDeviceCheckAndDoMasterClean();
            }
        }
        if (this.mClosingFindDevicePasswordVerified) {
            this.mClosingFindDevicePasswordVerified = false;
            shutFindDeviceDownAndShowFinalConfirm();
        }
        if (this.needShowBackupDialg) {
            showConfirmDialog();
            this.needShowBackupDialg = false;
        }
    }

    public void wipeFrpBlockDataAndDoMasterClear(final Activity activity, final WipeCallback wipeCallback) {
        if (Utils.isMonkeyRunning()) {
            return;
        }
        final PersistentDataBlockManager persistentDataBlockManager = (PersistentDataBlockManager) activity.getSystemService("persistent_data_block");
        if (persistentDataBlockManager != null && !persistentDataBlockManager.getOemUnlockEnabled() && Utils.isDeviceProvisioned(activity)) {
            new AsyncTask<Void, Void, Void>() { // from class: com.android.settings.MiuiMasterClear.12
                int mOldOrientation;
                ProgressDialog mProgressDialog;

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public Void doInBackground(Void... voidArr) {
                    persistentDataBlockManager.wipe();
                    return null;
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public void onPostExecute(Void r2) {
                    this.mProgressDialog.hide();
                    Activity activity2 = activity;
                    if (activity2 != null) {
                        activity2.setRequestedOrientation(this.mOldOrientation);
                        WipeCallback wipeCallback2 = wipeCallback;
                        if (wipeCallback2 != null) {
                            wipeCallback2.onWipeFinished();
                        } else {
                            MiuiMasterClear.this.doMasterClear();
                        }
                    }
                }

                @Override // android.os.AsyncTask
                protected void onPreExecute() {
                    ProgressDialog progressDialog = MiuiMasterClear.getProgressDialog(activity);
                    this.mProgressDialog = progressDialog;
                    progressDialog.show();
                    this.mOldOrientation = activity.getRequestedOrientation();
                    activity.setRequestedOrientation(14);
                }
            }.execute(new Void[0]);
        } else if (wipeCallback != null) {
            wipeCallback.onWipeFinished();
        } else {
            doMasterClear();
        }
    }
}
